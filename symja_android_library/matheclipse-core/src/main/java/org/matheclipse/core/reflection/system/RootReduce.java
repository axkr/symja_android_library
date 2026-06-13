package org.matheclipse.core.reflection.system;

import org.matheclipse.core.convert.VariablesSet;
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
    IExpr arg1 = ast.arg1();

    // Fast path: already a Root object – return as-is (accept 2- and 3-arg forms)
    if (arg1.isAST(S.Root, 3) || arg1.isAST(S.Root, 4)) {
      return arg1;
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
          IRational denominator = (IRational) F.Subtract.of(F.Sqr(p1), F.Sqr(p2));
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
        return F.NIL; // has symbolic variables – cannot reduce
      }

      // Step 1: compute the minimal polynomial as a pure Function of Slot(1)
      IExpr minPoly = engine.evaluate(F.MinimalPolynomial(arg1));
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
        IExpr c1 = engine.evaluate(F.Coefficient(polyInX, x, F.C1));
        IExpr c0 = engine.evaluate(F.Coefficient(polyInX, x, F.C0));
        return engine.evaluate(F.Divide(F.Negate(c0), c1));
      }

      // Step 4: compute numerical roots of the minimal polynomial
      IExpr nrootsResult = engine.evaluate(F.NRoots(polyInX, x));
      if (!nrootsResult.isList()) {
        return F.NIL;
      }
      IAST rootsList = (IAST) nrootsResult;

      // Step 4b: reorder roots to match WMA's k-indexing used by Root[f, k, 0]:
      //   real roots first (ascending), then complex roots (Re ascending, Im ascending).
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
          reVals[i] = rootI.re().evalf();
          imVals[i] = rootI.im().evalf();
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

      // Step 5: numerically evaluate the input expression
      IExpr numericArg1 = engine.evaluate(F.N(arg1));
      if (!numericArg1.isNumber()) {
        return F.NIL;
      }

      // Step 6: find the root index k (1-based) whose numeric value is closest to arg1,
      // using the sorted ordering so k matches WMA's Root[f, k, 0] convention.
      double re1 = numericArg1.re().evalf();
      double im1 = numericArg1.im().evalf();
      double minDist = Double.MAX_VALUE;
      int bestK = -1;
      for (int idx = 0; idx < nRoots; idx++) {
        int origIndex = canSort ? order[idx] : idx;
        IExpr rootK = rootsList.get(origIndex + 1);
        double re2 = rootK.re().evalf();
        double im2 = rootK.im().evalf();
        double dist = Math.hypot(re1 - re2, im1 - im2);
        if (dist < minDist) {
          minDist = dist;
          bestK = idx + 1; // 1-based index into sorted order
        }
      }

      if (bestK < 1 || minDist > 1e-6) {
        return F.NIL;
      }

      // Step 7: build and return Root[minPoly_pure_function, k, 0]. The trailing 0 marks
      // WMA's "real-first" ordering convention; quadratic Root expressions will
      // auto-evaluate via Root.evaluate to their radical form.
      return F.ternaryAST3(S.Root, minPoly, F.ZZ(bestK), F.C0);

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
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.LISTABLE);
  }
}