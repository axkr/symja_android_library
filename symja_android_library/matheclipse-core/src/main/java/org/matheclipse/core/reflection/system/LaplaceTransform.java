package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.UnaryNumerical;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISymbol;

/**
 *
 *
 * <pre>
 * LaplaceTransform(f, s, t)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * returns the laplace transform.
 *
 * </blockquote>
 *
 * <p>
 * See:
 *
 * <ul>
 * <li><a href="https://en.wikipedia.org/wiki/Laplace_transform">Wikipedia - Laplace transform</a>
 * </ul>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * &gt;&gt; LaplaceTransform(t^2*Exp(2+3*t), t, s)
 * (-2*E^2)/(3-s)^3
 * </pre>
 */
public class LaplaceTransform extends AbstractFunctionEvaluator {
  public LaplaceTransform() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr a1 = ast.arg1();
    IExpr t = ast.arg2();
    IExpr s = ast.arg3();

    if (!t.isList() && !s.isList() && !t.equals(s)) {
      if (s instanceof INum && t.isSymbol()) {
        double sDouble = s.evalf();
        final IAST cacheKey = F.List(S.LaplaceTransform, a1, t);
        Object value = engine.getObjectCache(cacheKey);
        final UnaryNumerical unaryNumerical;
        if (value instanceof UnaryNumerical) {
          unaryNumerical = (UnaryNumerical) value;
        } else {
          unaryNumerical = new UnaryNumerical(a1, (ISymbol) t, engine);
          engine.putObjectCache(cacheKey, unaryNumerical);
        }
        return F.num(laplaceTransform(unaryNumerical, sDouble));
      }
      if (a1.isFree(t)) { // && a1.isAtom()) {
        return F.Divide(a1, s);
      }
      if (a1.equals(t) && a1.isFree(s)) {
        return F.Power(s, F.CN2);
      }
      if (ast.arg1().isAST()) {
        IAST arg1 = (IAST) ast.arg1();
        if (arg1.isTimes()) {
          IAST timesAST = arg1;
          IASTAppendable result = F.TimesAlloc(timesAST.size());
          IASTAppendable rest = F.TimesAlloc(timesAST.size());
          arg1.filter(result, rest, x -> x.isFree(t));
          if (result.size() > 1) {
            return F.Times(result.oneIdentity1(), F.LaplaceTransform(rest, t, s));
          }
          int indexOfPower = timesAST.indexOf(//
              x -> x.equals(t)//
                  || (x.isPower() //
                      && x.base().equals(t)//
                      && x.exponent().isInteger() //
                      && x.exponent().isPositive()));
          if (indexOfPower > 0) {
            IExpr temp = timesAST.get(indexOfPower);
            IInteger n;
            if (temp.isPower()) {
              n = (IInteger) timesAST.get(indexOfPower).exponent();
            } else {
              n = F.C1;
            }
            IASTMutable r = timesAST.removeAtCopy(indexOfPower);
            // LaplaceTransform(r_ * t_ ^n_, t_, s_Symbol) := (-1)^n * D(LaplaceTransform(r, t, s),
            // {s,n}) /; FreeQ({n,s}, t) && n>0
            temp = engine.evaluate(F.D(F.LaplaceTransform(r, t, s), F.List(s, n)));
            if (temp.isAST()) {
              IAST derivedLaplaceTransform = (IAST) temp;
              if (derivedLaplaceTransform
                  .isFree(x -> x.isFunctionID(ID.D, ID.Derivative, ID.LaplaceTransform), true)) {
                return F.Times(F.Power(F.CN1, n), derivedLaplaceTransform);
              }

            }
          }
        } else if (arg1.isPower() && arg1.base().equals(t)) {
          IExpr n = arg1.exponent();
          if (n.isAtom() && !n.isMinusOne()) {
            return F.Divide(F.Gamma(F.Plus(F.C1, n)), F.Power(s, F.Plus(F.C1, n)));
          }
        } else if (arg1.isPlus()) {
          // LaplaceTransform[a_+b_+c_,t_,s_] ->
          // LaplaceTransform[a,t,s]+LaplaceTransform[b,t,s]+LaplaceTransform[c,t,s]
          return arg1.mapThread(F.LaplaceTransform(F.Slot1, t, s), 1);
        }
      }
    }
    return F.NIL;
  }

  /**
   * Laplace Transform.
   * 
   * @param function function to perform the Laplace transform to.
   * @param s Frequency at which to evaluate the transform.
   * @return {@code L{y(t} = Y(s)} evaluated at s.
   */
  private static double laplaceTransform(UnaryNumerical function, double s) {
    final int DefaultIntegralN = 5000;
    double du = 0.5 / DefaultIntegralN;
    double y = -function.valueLimit(0.0) / 2.0;
    double u = 0.0;
    double limit = 1.0 - Config.SPECIAL_FUNCTIONS_TOLERANCE;
    while (u < limit) {
      u += du;
      double powU = Math.pow(u, s - 1);
      y += (powU + powU) * function.valueLimit(-Math.log(u));
      u += du;
      powU = Math.pow(u, s - 1);
      y += powU * function.valueLimit(-Math.log(u));
    }
    return 2.0 * y * du / 3.0;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_3_3;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }
}
