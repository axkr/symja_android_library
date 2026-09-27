package org.matheclipse.core.sympy.assumptions;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.sympy.core.Traversal;

/**
 * Handlers for the refinement of expressions with assumptions. Ported from
 * <a href="https://github.com/sympy/sympy/blob/master/sympy/assumptions/refine.py">sympy/assumptions/refine.py</a>
 *
 * <p>
 * Only the handlers which aren't covered by the evaluation with assumptions are ported. The
 * assumptions have to be set in the evaluation engine.
 */
public class Refine {

  private Refine() {}

  /**
   * Apply the handlers to all subexpressions.
   *
   * @param expr the expression which was already evaluated with the assumptions
   * @param engine the evaluation engine with the assumptions
   * @return the refined expression or <code>expr</code> if no handler could be applied
   */
  public static IExpr refine(IExpr expr, EvalEngine engine) {
    if (expr.isFree(x -> x == S.ArcTan || x == S.Sign || x.isMinusOne(), true)) {
      return expr;
    }
    IExpr result = Traversal.bottomUp(expr, x -> {
      if (x.isAST(S.ArcTan, 3)) {
        return refineAtan2((IAST) x, engine).orElse(x);
      }
      if (x.isPower() && x.base().isMinusOne()) {
        return refinePow((IAST) x, engine).orElse(x);
      }
      if (x.isAST(S.Sign, 2)) {
        return refineSign((IAST) x, engine).orElse(x);
      }
      return x;
    });
    return result.equals(expr) ? expr : engine.evaluate(result);
  }

  private static boolean isEven(IExpr expr, EvalEngine engine) {
    if (expr.isInteger()) {
      return ((IInteger) expr).isEven();
    }
    if (expr.isNumber()) {
      return false;
    }
    return engine.evaluate(F.Times(F.C1D2, expr)).isIntegerResult();
  }

  private static boolean isOdd(IExpr expr, EvalEngine engine) {
    if (expr.isInteger()) {
      return ((IInteger) expr).isOdd();
    }
    if (expr.isNumber()) {
      return false;
    }
    return engine.evaluate(F.Times(F.C1D2, F.Plus(F.CN1, expr))).isIntegerResult();
  }

  /**
   * Handler for powers of <code>-1</code>.
   */
  public static IExpr refinePow(IAST power, EvalEngine engine) {
    // >>> refine_Pow((-1)**x, Q.even(x))
    // 1
    // >>> refine_Pow((-1)**x, Q.odd(x))
    // -1
    // For powers of -1, individual terms can be removed:
    // >>> refine_Pow((-1)**(x+y), Q.even(x))
    // (-1)**y
    // >>> refine_Pow((-1)**(x+y+z), Q.odd(x) & Q.odd(z))
    // (-1)**y
    // >>> refine_Pow((-1)**(x+y+2), Q.odd(x))
    // (-1)**(y + 1)
    // >>> refine_Pow((-1)**(x+3), True)
    // (-1)**(x + 1)
    IExpr exponent = power.exponent();
    if (isEven(exponent, engine)) {
      return F.C1;
    }
    if (isOdd(exponent, engine)) {
      return F.CN1;
    }
    if (exponent.isPlus()) {
      // For powers of (-1) we can remove
      // - even terms
      // - pairs of odd terms
      // - a single odd term + 1
      // - A numerical constant N can be replaced with mod(N,2)
      IAST plus = (IAST) exponent;
      IExpr coeff = F.C0;
      IASTAppendable terms = F.PlusAlloc(plus.argSize());
      int oddTerms = 0;
      boolean changed = false;
      for (int i = 1; i < plus.size(); i++) {
        IExpr t = plus.get(i);
        if (t.isInteger()) {
          coeff = coeff.plus(t);
        } else if (isEven(t, engine)) {
          changed = true;
        } else if (isOdd(t, engine)) {
          oddTerms++;
          changed = true;
        } else {
          terms.append(t);
        }
      }
      if (!coeff.isInteger()) {
        return F.NIL;
      }
      IInteger newCoeff = (IInteger) coeff;
      if ((oddTerms & 1) == 1) {
        newCoeff = newCoeff.add(F.C1);
      }
      newCoeff = newCoeff.mod(F.C2);
      if (!newCoeff.equals(coeff)) {
        changed = true;
      }
      if (changed) {
        terms.append(newCoeff);
        return engine.evaluate(F.Power(F.CN1, terms));
      }
    }
    return F.NIL;
  }

  /**
   * Handler for the two argument form <code>ArcTan(x, y)</code>.
   */
  public static IExpr refineAtan2(IAST arcTan, EvalEngine engine) {
    // >>> refine_atan2(atan2(y,x), Q.real(y) & Q.positive(x))
    // atan(y/x)
    // >>> refine_atan2(atan2(y,x), Q.negative(y) & Q.negative(x))
    // atan(y/x) - pi
    // >>> refine_atan2(atan2(y,x), Q.positive(y) & Q.negative(x))
    // atan(y/x) + pi
    // >>> refine_atan2(atan2(y,x), Q.zero(y) & Q.negative(x))
    // pi
    // >>> refine_atan2(atan2(y,x), Q.positive(y) & Q.zero(x))
    // pi/2
    // >>> refine_atan2(atan2(y,x), Q.negative(y) & Q.zero(x))
    // -pi/2
    // >>> refine_atan2(atan2(y,x), Q.zero(y) & Q.zero(x))
    // nan
    IExpr x = arcTan.arg1();
    IExpr y = arcTan.arg2();
    if (x.isPositiveResult()) {
      if (y.isRealResult()) {
        return F.ArcTan(F.Times(y, F.Power(x, F.CN1)));
      }
    } else if (x.isNegativeResult()) {
      if (y.isNegativeResult()) {
        return F.Plus(F.ArcTan(F.Times(y, F.Power(x, F.CN1))), F.Negate(S.Pi));
      } else if (y.isPositiveResult()) {
        return F.Plus(F.ArcTan(F.Times(y, F.Power(x, F.CN1))), S.Pi);
      } else if (isZero(y, engine)) {
        return S.Pi;
      }
    } else if (isZero(x, engine)) {
      if (y.isPositiveResult()) {
        return F.CPiHalf;
      } else if (y.isNegativeResult()) {
        return F.CNPiHalf;
      }
    }
    return F.NIL;
  }

  private static boolean isZero(IExpr expr, EvalEngine engine) {
    if (expr.isZero()) {
      return true;
    }
    if (expr.isNumber()) {
      return false;
    }
    return engine.evaluate(F.Equal(expr, F.C0)).isTrue();
  }

  private static boolean isNonZero(IExpr expr, EvalEngine engine) {
    if (expr.isNumber()) {
      return !expr.isZero();
    }
    if (expr.isAbs()) {
      return isNonZero(expr.first(), engine);
    }
    return engine.evaluate(F.Unequal(expr, F.C0)).isTrue();
  }

  /**
   * Handler for sign.
   */
  public static IExpr refineSign(IAST sign, EvalEngine engine) {
    // >>> refine_sign(sign(x), Q.positive(x) & Q.nonzero(x))
    // 1
    // >>> refine_sign(sign(x), Q.negative(x) & Q.nonzero(x))
    // -1
    // >>> refine_sign(sign(x), Q.zero(x))
    // 0
    // >>> refine_sign(sign(y), Q.positive(im(y)))
    // I
    // >>> refine_sign(sign(y), Q.negative(im(y)))
    // -I
    IExpr arg = sign.arg1();
    if (isZero(arg, engine)) {
      return F.C0;
    }
    if (arg.isRealResult()) {
      if (arg.isPositiveResult()) {
        return F.C1;
      }
      if (arg.isNegativeResult()) {
        return F.CN1;
      }
      if (isNonZero(arg, engine)) {
        if (arg.isNonNegativeResult()) {
          return F.C1;
        }
        if (arg.isNonPositiveResult()) {
          return F.CN1;
        }
      }
      return F.NIL;
    }
    if (isZero(F.Re(arg), engine)) {
      // imaginary argument
      IExpr im = engine.evaluate(F.Im(arg));
      if (im.isPositiveResult() || engine.evaluate(F.Greater(F.Im(arg), F.C0)).isTrue()) {
        return F.CI;
      }
      if (im.isNegativeResult() || engine.evaluate(F.Less(F.Im(arg), F.C0)).isTrue()) {
        return F.CNI;
      }
    }
    return F.NIL;
  }
}
