package org.matheclipse.core.eval;

import static org.matheclipse.core.expression.S.x;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;

public class PiecewiseUtil {

  /**
   * Rewrite the function as <code>Piecewise()</code> function if possible. Rewrite functions:
   * <code>Abs, Clip, If, Ramp, UnitStep</code>
   *
   * @param function
   * @param domain if set to <code>F.Reals</code> a function like <code>Abs(x)</code> can be
   *        rewritten.
   * @return {@link F#NIL} if evaluation wasn't possible
   */
  public static IAST piecewiseExpand(final IAST function, IBuiltInSymbol domain) {
    EvalEngine engine = EvalEngine.get();
    if (function.isPlus()) {
      int piecewisePosition = function.indexOf(x -> x.isPiecewise() != null);
      if (piecewisePosition > 0) {
        IAST piecewise = (IAST) function.get(piecewisePosition);
        int[] piecewiseDimension = piecewise.isPiecewise();
        if (piecewiseDimension[0] > 0 && piecewiseDimension[1] == 2) {
          IAST piecewiseList = (IAST) piecewise.arg1();
          IExpr rest = function.removeAtCopy(piecewisePosition).oneIdentity0();
          IASTAppendable result = F.mapList(piecewiseList, subList -> {
            return F.list(F.Plus(rest, subList.first()), subList.second());
          });
          if (piecewise.argSize() == 2) {
            return F.Piecewise(result, S.Plus.of(engine, rest, piecewise.arg2()));
          }
          return F.Piecewise(result);
        }
      }
      return F.NIL;
    }
    if (function.isTimes()) {
      int piecewisePosition = function.indexOf(x -> x.isPiecewise() != null);
      if (piecewisePosition > 0) {
        IAST piecewise = (IAST) function.get(piecewisePosition);
        int[] piecewiseDimension = piecewise.isPiecewise();
        if (piecewiseDimension[0] > 0 && piecewiseDimension[1] == 2) {
          IAST piecewiseList = (IAST) piecewise.arg1();
          IExpr rest = function.removeAtCopy(piecewisePosition).oneIdentity1();
          IASTAppendable result = F.mapList(piecewiseList, subList -> {
            return F.list(F.Times(rest, subList.first()), subList.second());
          });
          if (piecewise.argSize() == 2) {
            return F.Piecewise(result, S.Times.of(engine, rest, piecewise.arg2()));
          }
          return F.Piecewise(result);
        }
      }
      return F.NIL;
    }
    if (function.argSize() == 1) {
      IExpr x = function.arg1();
      if ((domain.equals(S.Reals) || function.arg1().isRealResult())) {
        if (function.isAST(S.Abs) || function.isAST(S.RealAbs)) {
          return F.Piecewise(F.list(F.list(F.Negate(x), F.Less(x, F.C0))), x);
        }
        if (function.isAST(S.Arg)) {
          return F.Piecewise(F.list(F.list(S.Pi, F.Less(x, F.C0))), F.C0);
        }
        if (function.isAST(S.Sign) || function.isAST(S.RealSign)) {
          return F.Piecewise(F.list(F.list(F.CN1, F.Less(x, F.C0)), //
              F.list(F.C1, F.Greater(x, F.C0))), F.C0);
        }
      }
      if (function.isAST(S.Boole, 2)) {
        return F.Piecewise(F.list(F.list(F.C1, x)), F.C0);
      }
      if (function.isAST(S.Unitize)) {
        return F.Piecewise(F.list(F.list(F.C1, F.Unequal(x, F.C0))), F.C0);
      }
    }
    if (function.isAST(S.BernsteinBasis, 4)) {
      IExpr d = function.arg1();
      IExpr n = function.arg2();
      IExpr x = function.arg3();
      return F
          .Piecewise(F.list(F.list(F.C1,
                  F.Or(F.And(F.Equal(d, F.C0), F.Equal(n, F.C0)),
                      F.And(F.GreaterEqual(d, F.C0), F.Equal(n, F.C0), F.Equal(x, F.C0)),
                      F.And(F.Greater(d, F.C0), F.Equal(x, F.C1),
                          F.Equal(F.Subtract(d, n), F.C0)))),
              F.list(
                  F.Times(F.Power(F.Subtract(F.C1, x), F.Subtract(d, n)), F.Power(x, n),
                      F.Binomial(d, n)),
                  F.And(F.Greater(d, F.C0), F.GreaterEqual(n, F.C0),
                      F.GreaterEqual(F.Subtract(d, n), F.C0), F.Less(F.C0, x, F.C1)))),
              F.C0);
    }
    if (function.isAST(S.RealAbs, 2)) {
      return F.Piecewise(F.list(F.list(F.Negate(x), F.Less(x, F.C0))), x);
    } else if (function.isAST(S.RealSign, 2)) {
      return F.Piecewise(F.list(F.list(F.CN1, F.Less(x, F.C0)), //
          F.list(F.C1, F.Greater(x, F.C0))), F.C0);
    }
    if (function.isAST(S.Clip, 2)) {
      IExpr x = function.arg1();
      return F.Piecewise(F.list(F.list(F.CN1, F.Less(x, F.CN1)), F.list(F.C1, F.Greater(x, F.C1))),
          x);
    }
    if (function.isAST(S.Clip, 3) && function.second().isList2()) {
      IExpr x = function.arg1();
      IExpr low = function.second().first();
      IExpr high = function.second().second();
      return F.Piecewise(F.list(F.list(low, F.Less(x, low)), F.list(high, F.Greater(x, high))), x);
    }
    if (function.isAST(S.If, 3)) {
      IExpr a1 = function.arg1();
      IExpr a2 = function.arg2();
      return F.Piecewise(F.list(F.list(a2, a1), F.C0));
    }
    if (function.isAST(S.If, 4)) {
      IExpr a1 = function.arg1();
      IExpr a2 = function.arg2();
      IExpr a3 = function.arg3();
      return F.Piecewise(F.list(F.list(a2, a1)), a3);
    }
    if (function.isAST(S.Ramp, 2)) {
      IExpr x = function.arg1();
      return F.Piecewise(F.list(F.list(x, F.GreaterEqual(x, F.C0))), F.C0);
    }
    if (function.isAST(S.Unitize, 3)) {
      IExpr x = function.arg1();
      IExpr dx = function.arg2();
      return F.Piecewise(F.list(F.list(F.C1, F.LessEqual(F.Subtract(dx, F.Abs(x)), F.C0))), F.C0);
    }
    if (function.isAST(S.UnitStep) && function.size() > 1) {
      // Piecewise({{1, x >= 0 && y >= 0 && z >= 0}}, 0)
      final int size = function.size();
      IASTAppendable andAST = F.mapFunction(S.And, function, t -> F.GreaterEqual(t, F.C0));
      return F.Piecewise(F.list(F.list(F.C1, andAST)), F.C0);
    }
  
    if (function.size() > 1) {
      if (function.isAST(S.DiscreteDelta)) {
        if (function.size() == 2) {
          return F.Piecewise(F.list(F.list(F.C1, F.Equal(function.arg1(), F.C0))), F.C0);
        }
        IASTAppendable andAST = F.mapFunction(S.And, function, t -> F.Equal(t, F.C0));
        return F.Piecewise(F.list(F.list(F.C1, andAST)), F.C0);
      }
      if (function.isAST(S.KroneckerDelta)) {
        if (function.size() == 2) {
          return F.Piecewise(F.list(F.list(F.C1, F.Equal(function.arg1(), F.C0))), F.C0);
        }
        IExpr[] last = new IExpr[] {function.arg1()};
        IASTAppendable andAST = F.mapFunction(S.And, function, 2, function.size(), t -> {
          final IExpr subtrahend = last[0];
          last[0] = t;
          return F.Equal(F.Subtract(subtrahend, t), F.C0);
        });
        return F.Piecewise(F.list(F.list(F.C1, andAST)), F.C0);
      }
    }
    return F.NIL;
  }

  private PiecewiseUtil() {
    // private constructor to avoid instantiation
  }

}
