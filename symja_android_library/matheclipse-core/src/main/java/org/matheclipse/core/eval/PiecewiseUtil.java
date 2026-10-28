package org.matheclipse.core.eval;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;

public class PiecewiseUtil {

  /**
   * Distribute the other summands or factors over the values and the default value of the first
   * <code>Piecewise</code> argument of a <code>Plus</code> or <code>Times</code> function.
   *
   * @param function a <code>Plus</code> or <code>Times</code> function
   * @param plus <code>true</code> for <code>Plus</code>
   * @param engine the evaluation engine
   * @return {@link F#NIL} if there is no <code>Piecewise</code> argument
   */
  private static IExpr distributeOverPiecewise(final IAST function, boolean plus,
      EvalEngine engine) {
    int piecewisePosition = function.indexOf(x -> x.isPiecewise() != null);
    if (piecewisePosition > 0) {
      IAST piecewise = (IAST) function.get(piecewisePosition);
      int[] piecewiseDimension = piecewise.isPiecewise();
      if (piecewiseDimension[0] > 0 && piecewiseDimension[1] == 2) {
        IAST piecewiseList = (IAST) piecewise.arg1();
        IAST restAST = function.removeAtCopy(piecewisePosition);
        IExpr rest = plus ? restAST.oneIdentity0() : restAST.oneIdentity1();

        IASTAppendable result = F.ListAlloc(piecewiseList.argSize());
        for (int i = 1; i <= piecewiseList.argSize(); i++) {
          IAST subList = (IAST) piecewiseList.get(i);
          IExpr value = plus ? F.Plus(rest, subList.arg1()) : F.Times(rest, subList.arg1());
          result.append(F.list(value, subList.arg2()));
        }

        if (piecewise.argSize() == 2) {
          IBuiltInSymbol op = plus ? S.Plus : S.Times;
          return F.Piecewise(result, op.of(engine, rest, piecewise.arg2()));
        }
        return F.Piecewise(result);
      }
    }
    return F.NIL;
  }

  /**
   * Rewrite <code>Max(...)</code> or <code>Min(...)</code> with at least two arguments as a
   * <code>Piecewise</code> function. An argument is chosen if it is strictly greater (smaller) than
   * all previous arguments and greater (smaller) than or equal to all subsequent arguments.
   *
   * @param function a <code>Max</code> or <code>Min</code> function
   * @param max <code>true</code> for <code>Max</code>
   * @return {@link F#NIL} if the function has less than two arguments
   */
  private static IExpr minMaxPiecewise(final IAST function, boolean max) {
    final int argSize = function.argSize();
    if (argSize > 1) {
      IASTAppendable conditions = F.ListAlloc(argSize);
      for (int i = 1; i < argSize; i++) {
        IExpr curr = function.get(i);
        IASTAppendable andAST = F.ast(S.And, argSize);

        for (int j = 1; j < i; j++) {
          IExpr difference = F.Subtract(function.get(j), curr);
          andAST.append(max ? F.Less(difference, F.C0) : F.Greater(difference, F.C0));
        }
        for (int k = i + 1; k <= argSize; k++) {
          IExpr difference = F.Subtract(curr, function.get(k));
          andAST.append(max ? F.GreaterEqual(difference, F.C0) : F.LessEqual(difference, F.C0));
        }

        IExpr cond = andAST.isAST1() ? andAST.arg1() : andAST;
        conditions.append(F.list(curr, cond));
      }
      return F.Piecewise(conditions, function.last());
    }
    return F.NIL;
  }

  /**
   * Rewrite the function as <code>Piecewise()</code> function if possible. Rewrites functions:
   * <code>Abs, Arg, BernsteinBasis, Boole, Clip, DiscreteDelta, If, KroneckerDelta, Max, Min, Ramp, Sign, Unitize, UnitBox, UnitStep, UnitTriangle, Which</code>
   *
   * @param function the function to expand
   * @param domain if set to <code>F.Reals</code> a function like <code>Abs(x)</code> can be
   *        rewritten.
   * @return {@link F#NIL} if evaluation wasn't possible
   */
  public static IExpr piecewiseExpand(final IAST function, IBuiltInSymbol domain) {
    EvalEngine engine = EvalEngine.get();
    IExpr head = function.head();

    if (head.isBuiltInSymbol()) {
      final int argSize = function.argSize();

      switch (((IBuiltInSymbol) head).ordinal()) {
        case ID.Plus:
          return distributeOverPiecewise(function, true, engine);
        case ID.Times:
          return distributeOverPiecewise(function, false, engine);
        case ID.Abs:
        case ID.RealAbs: {
          if (argSize == 1) {
            IExpr x = function.arg1();
            int ordinal = ((IBuiltInSymbol) head).ordinal();
            if (ordinal == ID.RealAbs || domain == S.Reals || x.isRealResult()) {
              return F.Piecewise(F.list(F.list(F.Negate(x), F.Less(x, F.C0))), x);
            }
          }
          return F.NIL;
        }
        case ID.Arg: {
          if (argSize == 1) {
            IExpr x = function.arg1();
            if ((domain == S.Reals || x.isRealResult())) {
              return F.Piecewise(F.list(F.list(S.Pi, F.Less(x, F.C0))), F.C0);
            }
          }
          return F.NIL;
        }
        case ID.Sign:
        case ID.RealSign: {
          if (argSize == 1) {
            IExpr x = function.arg1();
            int ordinal = ((IBuiltInSymbol) head).ordinal();
            if (ordinal == ID.RealSign || domain == S.Reals || x.isRealResult()) {
              return F.Piecewise(
                  F.list(F.list(F.CN1, F.Less(x, F.C0)), F.list(F.C1, F.Greater(x, F.C0))), F.C0);
            }
          }
          return F.NIL;
        }
        case ID.Boole: {
          if (argSize == 1) {
            return F.Piecewise(F.list(F.list(F.C1, function.arg1())), F.C0);
          }
          return F.NIL;
        }
        case ID.Unitize: {
          if (argSize == 1) {
            IExpr x = function.arg1();
            return F.Piecewise(F.list(F.list(F.C1, F.Unequal(x, F.C0))), F.C0);
          } else if (argSize == 2) {
            IExpr x = function.arg1();
            IExpr dx = function.arg2();
            return F.Piecewise(F.list(F.list(F.C1, F.LessEqual(F.Subtract(dx, F.Abs(x)), F.C0))),
                F.C0);
          }
          return F.NIL;
        }
        case ID.BernsteinBasis: {
          if (argSize == 3) {
            IExpr d = function.arg1();
            IExpr n = function.arg2();
            IExpr x = function.arg3();
            return F.Piecewise(F.list(
                F.list(F.C1,
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
          return F.NIL;
        }
        case ID.Clip: {
          if (argSize == 1) {
            IExpr x = function.arg1();
            return F.Piecewise(
                F.list(F.list(F.CN1, F.Less(x, F.CN1)), F.list(F.C1, F.Greater(x, F.C1))), x);
          } else if (argSize == 2 && function.arg2().isList2()) {
            IExpr x = function.arg1();
            IExpr low = function.arg2().first();
            IExpr high = function.arg2().second();
            return F.Piecewise(
                F.list(F.list(low, F.Less(x, low)), F.list(high, F.Greater(x, high))), x);
          } else if (argSize == 3 && function.arg2().isList2() && function.arg3().isList2()) {
            IExpr x = function.arg1();
            IExpr low = function.arg2().first();
            IExpr high = function.arg2().second();
            IExpr vLow = function.arg3().first();
            IExpr vHigh = function.arg3().second();
            return F.Piecewise(
                F.list(F.list(vLow, F.Less(x, low)), F.list(vHigh, F.Greater(x, high))), x);
          }
          return F.NIL;
        }
        case ID.If: {
          if (argSize == 2) {
            return F.Piecewise(F.list(F.list(function.arg2(), function.arg1())), F.C0);
          } else if (argSize == 3) {
            return F.Piecewise(F.list(F.list(function.arg2(), function.arg1())), function.arg3());
          }
          return F.NIL;
        }
        case ID.Which: {
          if (argSize >= 2) {
            IASTAppendable conditions = F.ListAlloc(argSize / 2);
            IExpr defaultVal = F.NIL;
            for (int i = 1; i < argSize; i += 2) {
              IExpr cond = function.get(i);
              IExpr val = function.get(i + 1);
              if (cond.isTrue()) {
                defaultVal = val;
                break;
              }
              conditions.append(F.list(val, cond));
            }
            if (defaultVal.isPresent()) {
              return F.Piecewise(conditions, defaultVal);
            }
            return F.Piecewise(conditions);
          }
          return F.NIL;
        }
        case ID.Ramp: {
          if (argSize == 1) {
            IExpr x = function.arg1();
            return F.Piecewise(F.list(F.list(x, F.GreaterEqual(x, F.C0))), F.C0);
          }
          return F.NIL;
        }
        case ID.UnitStep: {
          if (argSize >= 1) {
            IASTAppendable andAST = F.ast(S.And, argSize);
            for (int i = 1; i <= argSize; i++) {
              andAST.append(F.GreaterEqual(function.get(i), F.C0));
            }
            return F.Piecewise(F.list(F.list(F.C1, andAST)), F.C0);
          }
          return F.NIL;
        }
        case ID.UnitBox: {
          // 1 for Abs(xi) <= 1/2 for all i; 0 otherwise. Multiple arguments are a product, so the
          // conditions are combined with And.
          if (argSize >= 1) {
            IASTAppendable andAST = F.ast(S.And, argSize);
            for (int i = 1; i <= argSize; i++) {
              andAST.append(F.LessEqual(F.CN1D2, function.get(i), F.C1D2));
            }
            IExpr cond = andAST.isAST1() ? andAST.arg1() : andAST;
            return F.Piecewise(F.list(F.list(F.C1, cond)), F.C0);
          }
          return F.NIL;
        }
        case ID.UnitTriangle: {
          // Product of (1 - Abs(xi)) where every Abs(xi) <= 1; 0 otherwise.
          if (argSize >= 1) {
            IASTAppendable andAST = F.ast(S.And, argSize);
            IASTAppendable valueAST = F.ast(S.Times, argSize);
            for (int i = 1; i <= argSize; i++) {
              IExpr x = function.get(i);
              andAST.append(F.LessEqual(F.CN1, x, F.C1));
              valueAST.append(F.Subtract(F.C1, F.Abs(x)));
            }
            IExpr cond = andAST.isAST1() ? andAST.arg1() : andAST;
            IExpr value = valueAST.isAST1() ? valueAST.arg1() : valueAST;
            return F.Piecewise(F.list(F.list(value, cond)), F.C0);
          }
          return F.NIL;
        }
        case ID.DiscreteDelta: {
          if (argSize == 1) {
            return F.Piecewise(F.list(F.list(F.C1, F.Equal(function.arg1(), F.C0))), F.C0);
          } else if (argSize > 1) {
            IASTAppendable andAST = F.ast(S.And, argSize);
            for (int i = 1; i <= argSize; i++) {
              andAST.append(F.Equal(function.get(i), F.C0));
            }
            return F.Piecewise(F.list(F.list(F.C1, andAST)), F.C0);
          }
          return F.NIL;
        }
        case ID.KroneckerDelta: {
          if (argSize == 1) {
            return F.Piecewise(F.list(F.list(F.C1, F.Equal(function.arg1(), F.C0))), F.C0);
          } else if (argSize > 1) {
            IASTAppendable andAST = F.ast(S.And, argSize);
            IExpr last = function.arg1();
            for (int i = 2; i <= argSize; i++) {
              IExpr curr = function.get(i);
              andAST.append(F.Equal(F.Subtract(last, curr), F.C0));
              last = curr;
            }
            return F.Piecewise(F.list(F.list(F.C1, andAST)), F.C0);
          }
          return F.NIL;
        }
        case ID.Max:
          return minMaxPiecewise(function, true);
        case ID.Min:
          return minMaxPiecewise(function, false);
      }
    }
    return F.NIL;
  }

  private PiecewiseUtil() {
    // private constructor to avoid instantiation
  }
}
