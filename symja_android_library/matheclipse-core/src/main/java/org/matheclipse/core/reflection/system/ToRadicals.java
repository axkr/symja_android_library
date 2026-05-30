package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.C0;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.Algebra;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.longexponent.ExprMonomial;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomial;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomialRing;
import org.matheclipse.core.polynomials.longexponent.ExprRingFactory;

// Add new class after the Root class (after line 3261):
public class ToRadicals extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    // If the argument itself is Root[f, k] or Root[f, k, n], expand it directly
    // (any degree 1..4)
    if (arg1.isAST(S.Root, 3) || arg1.isAST(S.Root, 4)) {
      IExpr result = ToRadicals.rootToRadicals((IAST) arg1, engine, 4);
      return result.orElse(arg1);
    }
    // Walk the expression tree and replace any Root[f, k]/Root[f, k, n] subexpressions.
    // If no Root is present (or none can be expanded), return arg1 unchanged so that
    // ToRadicals acts as identity for non-Root inputs.
    IExpr result = arg1.replaceAll(x -> {
      if (x.isAST(S.Root, 3) || x.isAST(S.Root, 4)) {
        IExpr radical = ToRadicals.rootToRadicals((IAST) x, engine, 4);
        if (radical.isPresent()) {
          return radical;
        }
      }
      return F.NIL;
    });
    return result.orElse(arg1);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.LISTABLE);
  }

  private static IExpr rootNearFloatNumber(EvalEngine engine, IExpr f, IExpr c) {
    double targetC = 0.0;
    try {
      targetC = c.evalf();
    } catch (ArgumentTypeException e) {
      // Root approximation `1` is not a number.
      return Errors.printMessage(S.Root, "rapp", F.List(c));
    }

    // Represents the root of the general equation f(x) == 0 near x = c
    ISymbol x = F.Dummy("x");
    IAST eq = F.Equal(F.unaryAST1(f, x), F.C0);

    try {
      // Attempt to find exact symbolic solutions using Solve(eq, x)
      double cmin = targetC - Config.DEFAULT_CHOP_DELTA;
      double cmax = targetC + Config.DEFAULT_CHOP_DELTA;
      IAST solve = F.Solve(F.List(eq, F.LessEqual(x, F.Rationalize(F.num(cmax), F.C0)),
          F.GreaterEqual(x, F.Rationalize(F.num(cmin), F.C0))), x);
      IExpr solveResult = engine.evaluate(solve);

      if (solveResult.isList()) {
        IAST list = (IAST) solveResult;
        IExpr bestExactRoot = F.NIL;
        double minDiff = Double.MAX_VALUE;

        // Iterate through the solutions to find the one closest to 'c'
        for (int i = 1; i <= list.argSize(); i++) {
          IExpr ruleList = list.get(i);
          if (ruleList.isList1() && ruleList.first().isRuleAST()) {
            IExpr exactVal = ruleList.first().second();
            try {
              double val = exactVal.evalf();
              double diff = Math.abs(val - targetC);

              // Define a reasonable threshold for "near" x = c, e.g., 1e-6
              if (diff < minDiff && diff < 1e-6) {
                minDiff = diff;
                bestExactRoot = exactVal;
              }
            } catch (ArgumentTypeException e) {
              // Skip if the exact value cannot be evaluated to a double
              continue;
            }
          }
        }

        if (bestExactRoot.isPresent()) {
          return bestExactRoot;
        }
      }

    } catch (ArgumentTypeException e) {
    }
    // If no exact root is found close enough to 'c', leave the Root object unevaluated
    return F.NIL;
  }

  public static IExpr rootToRadicals(final IAST ast, EvalEngine engine) {
    return rootToRadicals(ast, engine, 4);
  }

  /**
   * Convert a {@code Root[f, k]} expression to its radical form.
   *
   * @param ast the {@code Root[...]} expression
   * @param engine the evaluation engine
   * @param maxDegree maximum polynomial degree to expand. Pass {@code 2} for auto-evaluation of
   *        {@code Root} (quadratics only), or {@code 4} to also expand cubics and quartics (used by
   *        {@code ToRadicals}).
   * @return the radical form, or {@link F#NIL} if the polynomial cannot be expanded (e.g. degree
   *         exceeds {@code maxDegree}).
   */
  public static IExpr rootToRadicals(final IAST ast, EvalEngine engine, int maxDegree) {
    if (ast.isAST1() && ast.arg1().isList2()) {
      IExpr f = ast.arg1().first();
      IExpr c = ast.arg1().second();
      return rootNearFloatNumber(engine, f, c);
    }

    // Accept both Root[f, k] and Root[f, k, n] (n in {0,1}). Symja implements only the
    // real-first ordering, so the trailing argument is ignored here.
    if ((ast.size() == 3 || ast.size() == 4) && ast.arg2().isInteger()) {
      IExpr expr = ast.arg1();
      if (expr.isFunction()) {
        expr = expr.first();
        try {
          int k = ast.arg2().toIntDefault();
          if (k < 1) {
            return F.NIL;
          }
          final IAST variables = F.list(F.Slot1);
          ExprPolynomialRing ring = new ExprPolynomialRing(ExprRingFactory.CONST, variables);
          ExprPolynomial polynomial = ring.create(expr, false, true, false);

          final long varDegree = polynomial.degree(0);
          if (polynomial.isConstant()) {
            return F.CEmptyList;
          }
          IExpr a, b, c2, d, e;
          if (varDegree >= 1 && varDegree <= maxDegree) {
            a = C0;
            b = C0;
            c2 = C0;
            d = C0;
            e = C0;
            for (ExprMonomial monomial : polynomial) {
              final IExpr coeff = monomial.coefficient();
              long lExp = monomial.exponent().getVal(0);
              if (lExp == 4) {
                e = coeff;
              } else if (lExp == 3) {
                d = coeff;
              } else if (lExp == 2) {
                c2 = coeff;
              } else if (lExp == 1) {
                b = coeff;
              } else if (lExp == 0) {
                a = coeff;
              } else {
                throw new ArithmeticException("Root::Unexpected exponent value: " + lExp);
              }
            }

            // Compute all radical roots (degree many), then sort numerically to
            // align with NRoots / k-indexing (Re ascending, Im ascending).
            int degree = (int) varDegree;
            IExpr[] radicalRoots = new IExpr[degree];
            boolean allPresent = true;
            for (int i = 1; i <= degree; i++) {
              IAST ri = F.NIL;
              if (degree == 1) {
                ri = Algebra.root1(a, b, i);
              } else if (degree == 2) {
                ri = Algebra.root2(a, b, c2, i);
              } else if (degree == 3) {
                ri = Algebra.root3(a, b, c2, d, i);
              } else {
                ri = Algebra.root4(a, b, c2, d, e, i);
              }
              if (!ri.isPresent()) {
                allPresent = false;
                break;
              }
              radicalRoots[i - 1] = engine.evaluate(ri);
            }

            if (!allPresent) {
              return F.NIL;
            }

            // Sort by (real-first, then Re asc, then Im asc) to match k-indexing.
            radicalRoots = sortRootsByMmaOrder(radicalRoots);

            if (k < 1 || k > degree) {
              return F.NIL;
            }
            return radicalRoots[k - 1];
          } else if (varDegree > maxDegree && maxDegree >= 4) {
            // Degree exceeds the Cardano/Ferrari formulas (degree 1..4). For solvable
            // polynomials (e.g. binomials like #^5 - 2) fall back to Solve, which can return
            // exact radical solutions. This branch is gated to ToRadicals callers (maxDegree>=4);
            // plain Root auto-evaluation (maxDegree==2) never reaches here.
            IExpr[] radicalRoots = solveToRadicals(expr, (int) varDegree, engine);
            if (radicalRoots == null) {
              return F.NIL;
            }
            // Sort by (real-first, then Re asc, then Im asc) to match k-indexing.
            radicalRoots = sortRootsByMmaOrder(radicalRoots);
            if (k < 1 || k > radicalRoots.length) {
              return F.NIL;
            }
            return radicalRoots[k - 1];
          }
        } catch (JASConversionException e2) {
          //
        }
      }
    }
    return F.NIL;
  }

  /**
   * Sort the given radical roots by {@code Root} k-indexing convention:
   * <ol>
   * <li>real roots first, ordered by ascending value</li>
   * <li>complex roots ordered by ascending real part, then ascending imaginary part</li>
   * </ol>
   * If any root cannot be evaluated to a numeric value (e.g. symbolic coefficients), the original
   * order is returned unchanged so that the natural Cardano/Ferrari k-indexing is preserved.
   *
   * @param roots the radical roots to sort
   * @return a new sorted array, or the original array if numeric sorting is not possible
   */
  private static IExpr[] sortRootsByMmaOrder(IExpr[] roots) {
    final int n = roots.length;
    final double[] reVals = new double[n];
    final double[] imVals = new double[n];
    for (int i = 0; i < n; i++) {
      try {
        reVals[i] = roots[i].re().evalf();
        imVals[i] = roots[i].im().evalf();
        if (Double.isNaN(reVals[i]) || Double.isNaN(imVals[i])) {
          return roots;
        }
      } catch (RuntimeException ex) {
        return roots;
      }
    }
    Integer[] order = new Integer[n];
    for (int i = 0; i < n; i++) {
      order[i] = i;
    }
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
    IExpr[] sorted = new IExpr[n];
    for (int i = 0; i < n; i++) {
      sorted[i] = roots[order[i]];
    }
    return sorted;
  }

  /**
   * Fall back to {@code Solve} to obtain the radical solutions of {@code body == 0} for polynomials
   * whose degree exceeds the closed-form Cardano/Ferrari formulas (degree 1..4). This handles
   * solvable polynomials such as binomials (e.g. {@code #^5 - 2}).
   *
   * @param body the polynomial expression in {@link F#Slot1} (the body of the pure function)
   * @param degree the polynomial degree (expected number of solutions)
   * @param engine the evaluation engine
   * @return an array of {@code degree} exact radical solutions, or {@code null} if {@code Solve}
   *         could not return exactly {@code degree} explicit radical solutions
   */
  private static IExpr[] solveToRadicals(IExpr body, int degree, EvalEngine engine) {
    ISymbol x = F.Dummy("x");
    IExpr eqBody = body.replaceAll(F.Rule(F.Slot1, x)).orElse(body);
    IExpr solveResult = engine.evaluate(F.Solve(F.Equal(eqBody, C0), x));
    if (!solveResult.isListOfRules(false) && !solveResult.isList()) {
      return null;
    }
    IAST list = (IAST) solveResult;
    if (list.argSize() != degree) {
      return null;
    }
    IExpr[] solutions = new IExpr[degree];
    for (int i = 1; i <= degree; i++) {
      IExpr ruleList = list.get(i);
      if (!ruleList.isList1() || !ruleList.first().isRuleAST()) {
        return null;
      }
      IExpr value = ruleList.first().second();
      // Reject solutions that are not exact radical forms: still contain Root, reference the
      // solve variable (unsolved), or are inexact (numeric-only) results.
      if (!value.isFree(S.Root) || !value.isFree(x) || value.isInexactNumber()) {
        return null;
      }
      solutions[i - 1] = engine.evaluate(value);
    }
    return solutions;
  }
}
