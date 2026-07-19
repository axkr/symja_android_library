package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 *
 *
 * <pre>
 * <code>DifferenceDelta(f(x), x)
 * </code>
 * </pre>
 *
 * <p>
 * generates a forward difference <code>f(x+1) - f(x)</code>
 *
 * <pre>
 * <code>DifferenceDelta(f(x), {x,n,h})
 * </code>
 * </pre>
 *
 * <p>
 * generates a higher-order forward difference for integers <code>n</code>.
 *
 * <p>
 * See:
 *
 * <ul>
 * <li><a href="https://en.wikipedia.org/wiki/Finite_difference#Higher-order_differences">Wikipedia
 * - Finite difference - Higher-order differences</a>
 * </ul>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * <code>&gt;&gt; DifferenceDelta(b(a),a)
 * -b(a)+b(1+a)
 *
 * &gt;&gt; DifferenceDelta(b(a),{a,5,c})
 * -b(a)+5*b(a+c)-10*b(a+2*c)+10*b(a+3*c)-5*b(a+4*c)+b(a+5*c)
 *
 * </code>
 * </pre>
 */
public class DifferenceDelta extends AbstractCoreFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    // Evaluate the first argument first, so that e.g. an indefinite Sum collapses to its closed
    // form before the forward difference shifts the variable. Otherwise the variable shift would
    // also rewrite the bound iterator of a held Sum(f, i).
    IExpr arg1 = engine.evaluate(ast.arg1());
    IExpr arg2 = ast.arg2();
    if (arg1.isList()) {
      // thread elementwise over list in arg1
      return arg1.mapThread(ast.setAtCopy(1, F.Slot1), 1);
    }

    if (ast.argSize() > 2) {
      // DifferenceDelta[f, i, j, ...] is evaluated as
      // DifferenceDelta[DifferenceDelta[f, i], j, ...]
      IAST newAST = ast.removeAtCopy(ast.argSize());
      return engine.evaluate(F.DifferenceDelta(engine.evaluate(newAST), ast.last()));
    }

    if (arg2.isList()) {
      // DifferenceDelta[f, {x, n, h}]
      if (arg2.isList2() || arg2.isList3()) {
        IExpr symbolX = arg2.first();
        if (!arg1.isFree(symbolX, true)) {
          if (arg2.second().isInteger() && arg2.second().isNonNegativeResult()) {
            int n = arg2.second().toIntDefault();
            if (n == 0) {
              return arg1;
            }
            if (n > 0) {
              IExpr stepH = F.C1;
              if (arg2.isList3()) {
                stepH = arg2.getAt(3);
              }

              IExpr closedForm = closedFormDifference(arg1, symbolX, n, stepH);
              if (closedForm.isPresent()) {
                return closedForm;
              }

              IASTAppendable result = F.PlusAlloc(n + 1);
              for (int i = 0; i <= n; i++) {
                IExpr diffTerm =
                    F.xreplace(arg1, symbolX, F.Plus(F.Times(F.ZZ(i), stepH), symbolX));
                result.append(F.Times(F.Power(F.CN1, n - i), F.Binomial(n, i), diffTerm));
              }
              return normalizeDifference(result, symbolX, stepH, engine);
            }
          }
        } else {
          // All quantities that do not explicitly depend on the variables given are taken to have
          // zero partial difference.
          return F.C0;
        }
      }
      return F.NIL;
    }

    // DifferenceDelta[f, x]
    if (!arg1.isFree(arg2, true)) {
      IExpr closedForm = closedFormDifference(arg1, arg2, 1, F.C1);
      if (closedForm.isPresent()) {
        return closedForm;
      }

      IExpr f2 = F.xreplace(arg1, arg2, F.Plus(F.C1, arg2));
      return normalizeDifference(F.Subtract(f2, arg1), arg2, F.C1, engine);
    }
    // All quantities that do not explicitly depend on the variables given are taken to have zero
    // partial difference.
    return F.C0;
  }

  /**
   * Bring a raw forward-difference result into the canonical output form. A genuine polynomial or
   * rational function of <code>x</code> is canonicalized: with a numeric step it is factored
   * (matching wolframscript, e.g. <code>x^2 + x -&gt; 2*(1 + x)</code>, and rational summands combine
   * over a common denominator), with a symbolic step it is only expanded. A difference of unknown
   * functions such as <code>f(x + 1) - f(x)</code> is already in simplest form and is returned
   * unchanged (Factor must not reorder it or fold it into an opaque product).
   */
  private static IExpr normalizeDifference(IExpr result, IExpr x, IExpr stepH, EvalEngine engine) {
    IExpr together = engine.evaluate(F.Together(result));
    boolean rational = engine.evaluate(F.PolynomialQ(F.Numerator(together), x)).isTrue()
        && engine.evaluate(F.PolynomialQ(F.Denominator(together), x)).isTrue();
    if (!rational) {
      return result;
    }
    return stepH.isNumber() //
        ? engine.evaluate(F.Factor(result))
        : engine.evaluate(F.ExpandAll(result));
  }

  /**
   * Attempts to generate a closed-form solution for the n-th order forward difference of known
   * functions.
   */
  private static IExpr closedFormDifference(IExpr f, IExpr x, int n, IExpr h) {
    if (n == 0) {
      return f;
    }
    if (f.isAST1()) {
      IExpr head = f.head();
      if (head.isBuiltInSymbol()) {
        IExpr arg = f.first();
        IExpr[] linear = arg.linear(x);

        if (linear != null) {
          IExpr a = linear[1];
          IExpr nExpr = F.ZZ(n);
          switch (f.validHeadID()) {
            case ID.Sin: {
              // 2^n * Sin[a*h/2]^n * Sin[c + a*x + n*(a*h + Pi)/2]
              IExpr coeff =
                  F.Times(F.Power(F.C2, nExpr), F.Power(F.Sin(F.Times(a, h, F.C1D2)), nExpr));
              IExpr inner = F.Plus(arg, F.Times(nExpr, F.Plus(F.Times(a, h), S.Pi), F.C1D2));
              return F.Times(coeff, F.Sin(inner));
            }
            case ID.Cos: {
              // 2^n * Sin[a*h/2]^n * Cos[c + a*x + n*(a*h + Pi)/2]
              IExpr coeff =
                  F.Times(F.Power(F.C2, nExpr), F.Power(F.Sin(F.Times(a, h, F.C1D2)), nExpr));
              IExpr inner = F.Plus(arg, F.Times(nExpr, F.Plus(F.Times(a, h), S.Pi), F.C1D2));
              return F.Times(coeff, F.Cos(inner));
            }
            case ID.Sinh: {
              // 2^n * Sinh[a*h/2]^n * (Sinh or Cosh)
              IExpr coeff =
                  F.Times(F.Power(F.C2, nExpr), F.Power(F.Sinh(F.Times(a, h, F.C1D2)), nExpr));
              IExpr inner = F.Plus(arg, F.Times(nExpr, a, h, F.C1D2));
              return F.Times(coeff, (n % 2 == 0) ? F.Sinh(inner) : F.Cosh(inner));
            }
            case ID.Cosh: {
              // 2^n * Sinh[a*h/2]^n * (Cosh or Sinh)
              IExpr coeff =
                  F.Times(F.Power(F.C2, nExpr), F.Power(F.Sinh(F.Times(a, h, F.C1D2)), nExpr));
              IExpr inner = F.Plus(arg, F.Times(nExpr, a, h, F.C1D2));
              return F.Times(coeff, (n % 2 == 0) ? F.Cosh(inner) : F.Sinh(inner));
            }
            case ID.Factorial: {
              // first forward difference: Factorial(arg + a*h) - Factorial(arg)
              //   = Factorial(arg) * (Pochhammer(arg + 1, a*h) - 1)
              // (e.g. DifferenceDelta(k!, k) -> k*k!)
              if (n == 1) {
                return F.Times(f,
                    F.Subtract(F.Pochhammer(F.Plus(arg, F.C1), F.Times(a, h)), F.C1));
              }
              break;
            }
          }
        }
      }
    } else if (f.isPower()) {
      IExpr base = f.base();
      IExpr arg = f.exponent();
      if (base.isFree(x, true)) {
        IExpr[] linear = arg.linear(x);
        if (linear != null) {
          IExpr a = linear[1];
          IExpr nExpr = F.ZZ(n);

          // base^(c + a*x) * (base^(a*h) - 1)^n -> f * (base^(a*h) - 1)^n
          return F.Times(f, F.Power(F.Subtract(F.Power(base, F.Times(a, h)), F.C1), nExpr));
        }
      }
    } else if (f.isTimes()) {
      // const * base^(c + a*x) with all remaining factors free of x:
      // delta = f * (base^(a*h) - 1)^n
      // (e.g. the antidifference a^i/(-1+a) of a geometric sum differences back to a^i)
      IExpr base = F.NIL;
      IExpr a = F.NIL;
      boolean valid = true;
      for (IExpr factor : (IAST) f) {
        if (factor.isFree(x, true)) {
          continue;
        }
        if (base.isNIL() && factor.isPower() && factor.base().isFree(x, true)) {
          IExpr[] linear = factor.exponent().linear(x);
          if (linear != null && !linear[1].isZero()) {
            base = factor.base();
            a = linear[1];
            continue;
          }
        }
        valid = false;
        break;
      }
      if (valid && base.isPresent()) {
        IExpr nExpr = F.ZZ(n);
        return F.Times(f, F.Power(F.Subtract(F.Power(base, F.Times(a, h)), F.C1), nExpr));
      }
    }
    return F.NIL;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_INFINITY;
  }
}
