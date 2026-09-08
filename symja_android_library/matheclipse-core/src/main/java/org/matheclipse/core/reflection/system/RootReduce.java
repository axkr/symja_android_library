package org.matheclipse.core.reflection.system;

import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.CompareUtil;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;

public class RootReduce extends AbstractFunctionEvaluator {

  /** {@inheritDoc} */
  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    final IExpr arg1 = ast.arg1();
    IAST list = CompareUtil.threadListLogicEquationOperators(arg1, ast, 1);
    if (list.isPresent()) {
      return list;
    }

    // Fast path: already a Root object. The 3-argument form is canonical and returned as-is; the
    // 2-argument form is normalized to Root[f, k, 0] so that RootReduce always emits the same form
    // as the general path below. Symja implements only the real-first root ordering, so Root[f, k]
    // and Root[f, k, 0] denote the same root (see Root#evaluate).
    if (arg1.isAST(S.Root, 4)) {
      return arg1;
    }
    if (arg1.isAST(S.Root, 3)) {
      IAST root = (IAST) arg1;
      return F.ternaryAST3(S.Root, root.arg1(), root.arg2(), F.C0);
    }

    // Fast path: rational numbers are trivially algebraic
    if (arg1.isRational()) {
      return arg1;
    }

    // Existing special-case: 1/(p1 + p2) where terms are sqrt-like
    // e.g. 1/(1 + Sqrt[2]) → (1 - Sqrt[2]) / (1 - 2) = -(1 - Sqrt[2])
    if (arg1.isPowerReciprocal()) {
      IExpr base = arg1.base();
      if (base.isPlus() && base.size() == 3) {
        IExpr p1 = base.first();
        IExpr p2 = base.second();
        if ((p1.isRational() || p1.isFactorSqrtExpr()) && p2.isFactorSqrtExpr()) {
          IRational denominator = (IRational) S.Subtract.of(engine, F.Sqr(p1), F.Sqr(p2));
          IAST numerator = F.Subtract(p1, p2);
          return F.Divide(numerator, denominator);
        }
      }
    }

    // General path: use MinimalPolynomial to produce Root[poly, k, 0]
    try {
      // Only attempt if the expression is numeric (no free symbolic variables)
      // i.e. it's a closed-form algebraic number expression
      VariablesSet vars = new VariablesSet(arg1);
      if (!vars.isEmpty()) {
        // has symbolic variables - there's nothing to reduce, so RootReduce is the identity here
        return arg1;
      }

      // Step 1: compute the minimal polynomial as a pure Function of Slot(1)
      IExpr minPoly = S.MinimalPolynomial.funEval(engine, arg1);
      if (minPoly.isNIL() || minPoly.equals(arg1)) {
        return F.NIL;
      }

      // Step 2: build the polynomial p[x] from the pure function using a fresh variable
      ISymbol x = F.$s("$rrVar");
      IExpr appliedX = F.unaryAST1(minPoly, x);
      IExpr polyInX = engine.evaluate(appliedX);
      if (polyInX.isNIL()) {
        // minPoly may already be in x form if MinimalPolynomial returned poly, not Function
        polyInX = minPoly;
      }

      // Step 3: determine degree – if degree 1, the algebraic number is rational
      IExpr degree = engine.evaluate(F.Exponent(polyInX, x));
      if (F.C1.equals(degree)) {
        // Degree 1 → rational, solve directly: return -c0/c1
        IExpr c1 = S.Coefficient.funEval(engine, polyInX, x, F.C1);
        IExpr c0 = S.Coefficient.funEval(engine, polyInX, x, F.C0);
        return engine.evaluate(F.Divide(F.Negate(c0), c1));
      }

      // Step 5: numerically evaluate the input expression
      IExpr numericArg1 = S.N.funEval(engine, arg1);
      if (!numericArg1.isNumber()) {
        return F.NIL;
      }

      // Steps 4, 6 and 7: pick the root of the minimal polynomial which is closest to the
      // numerical value of the input and return it as a Root[] object
      return nearestRootObject(polyInX, x, minPoly, numericArg1, 1e-6, engine);

    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return F.NIL;
    }
  }

  /**
   * Determine the root of the univariate polynomial <code>polyInX</code> which lies closest to
   * <code>numericValue</code> and return it as a <code>Root[f, k, 0]</code> object.
   *
   * <p>
   * The roots are ordered the way Wolfram Language indexes them: real roots first (ascending), then
   * the complex roots (real part ascending, imaginary part ascending). This has to agree with the
   * sort in {@link ToRadicals#rootToRadicals(IAST, EvalEngine)} so that the resulting
   * <code>Root[..., k, 0]</code> expands back to the same algebraic number.
   *
   * @param polyInX a univariate polynomial in <code>x</code>
   * @param x the variable of <code>polyInX</code>
   * @param pureFunction the same polynomial as a pure function of <code>Slot1</code>, or
   *        {@link F#NIL} to let this method build it from <code>polyInX</code>
   * @param numericValue the numerical value which selects the root
   * @param tolerance the maximum accepted distance between <code>numericValue</code> and the
   *        selected root
   * @param engine the evaluation engine
   * @return the <code>Root[f, k, 0]</code> object or {@link F#NIL} if no root is close enough
   */
  public static IExpr nearestRootObject(IExpr polyInX, ISymbol x, IExpr pureFunction,
      IExpr numericValue, double tolerance, EvalEngine engine) {
    try {
      // Step 4: compute numerical roots of the polynomial
      IExpr nrootsResult = S.NRoots.funEval(engine, polyInX, x);
      if (!nrootsResult.isList()) {
        return F.NIL;
      }
      IAST rootsList = (IAST) nrootsResult;

      // Step 4b: reorder roots to match WMA's k-indexing used by Root[f, k, 0]:
      // real roots first (ascending), then complex roots (Re ascending, Im ascending).
      // This must agree with the sort in ToRadicals.rootToRadicals so that the resulting
      // Root[..., k, 0] expands back to the same algebraic number.
      final int nRoots = rootsList.argSize();
      double[] reVals = new double[nRoots];
      double[] imVals = new double[nRoots];
      Integer[] order = new Integer[nRoots];
      boolean canSort = true;
      for (int i = 0; i < nRoots; i++) {
        IExpr rootI = rootsList.get(i + 1);
        try {
          reVals[i] = rootI.re().evalfNaN();
          imVals[i] = rootI.im().evalfNaN();
          if (Double.isNaN(reVals[i]) || Double.isNaN(imVals[i])) {
            canSort = false;
            break;
          }
        } catch (RuntimeException ex) {
          canSort = false;
          break;
        }
        order[i] = i;
      }
      if (canSort) {
        final double imTol = 1e-10;
        final double reTol = 1e-10;
        java.util.Arrays.sort(order, (xi, yi) -> {
          boolean xReal = Math.abs(imVals[xi]) < imTol;
          boolean yReal = Math.abs(imVals[yi]) < imTol;
          if (xReal && !yReal) {
            return -1;
          }
          if (!xReal && yReal) {
            return 1;
          }
          if (Math.abs(reVals[xi] - reVals[yi]) > reTol) {
            return Double.compare(reVals[xi], reVals[yi]);
          }
          return Double.compare(imVals[xi], imVals[yi]);
        });
      }

      // Step 6: find the root index k (1-based) whose numeric value is closest to the given value,
      // using the sorted ordering so k matches WMA's Root[f, k, 0] convention.
      double re1 = numericValue.re().evalfNaN();
      double im1 = numericValue.im().evalfNaN();
      if (Double.isNaN(re1) || Double.isNaN(im1)) {
        return F.NIL;
      }
      double minDist = Double.MAX_VALUE;
      int bestK = -1;
      for (int idx = 0; idx < nRoots; idx++) {
        int origIndex = canSort ? order[idx] : idx;
        IExpr rootK = rootsList.get(origIndex + 1);
        double re2 = rootK.re().evalfNaN();
        double im2 = rootK.im().evalfNaN();
        if (Double.isNaN(re2) || Double.isNaN(im2)) {
          return F.NIL;
        }
        double dist = Math.hypot(re1 - re2, im1 - im2);
        if (dist < minDist) {
          minDist = dist;
          bestK = idx + 1; // 1-based index into sorted order
        }
      }

      if (bestK < 1 || minDist > tolerance) {
        return F.NIL;
      }

      // Step 7: build and return Root[pure_function, k, 0]. The trailing 0 marks
      // WMA's "real-first" ordering convention; quadratic Root expressions will
      // auto-evaluate via Root.evaluate to their radical form.
      IExpr function = pureFunction;
      if (function.isNIL()) {
        function = F.Function(F.subst(polyInX, x, F.Slot1));
      }
      return F.ternaryAST3(S.Root, function, F.ZZ(bestK), F.C0);

    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return F.NIL;
    }
  }


  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {}
}
