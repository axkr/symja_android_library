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
    if (t.equals(s)) {
      return F.NIL;
    }
    if (!t.isList() && !s.isList() && !t.equals(s)) {
      if (s instanceof INum && t.isSymbol()) {
        double sDouble = s.evalfNaN();
        final IAST cacheKey = F.List(S.LaplaceTransform, a1, t);
        Object value = engine.getObjectCache(cacheKey);
        final UnaryNumerical unaryNumerical;
        if (value instanceof UnaryNumerical) {
          unaryNumerical = (UnaryNumerical) value;
        } else {
          unaryNumerical = new UnaryNumerical(a1, (ISymbol) t, Double.NaN, engine);
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
      if (t.isSymbol()) {
        IExpr stepped = laplaceTransformOfSteps(engine, a1, t, s);
        if (stepped.isPresent()) {
          return stepped;
        }
      }
      if (ast.arg1().isAST()) {
        IAST arg1 = (IAST) ast.arg1();

        if (arg1.isTimes()) {
          IExpr temp = laplaceTransformTimes(engine, t, s, arg1);
          if (temp.isPresent()) {
            return temp;
          }
        } else if (arg1.isPower() && arg1.base().equals(t)) {
          IExpr n = arg1.exponent();
          if (n.isFree(t) && !n.isMinusOne()) {
            return F.Divide(F.Gamma(F.Plus(F.C1, n)), F.Power(s, F.Plus(F.C1, n)));
          }
        } else if (arg1.isPower() && arg1.base().isTimes()) {
          // Handle (c*t)^n => c^n * t^n
          IAST baseTimes = (IAST) arg1.base();
          IASTAppendable constants = F.TimesAlloc(baseTimes.size());
          boolean foundT = false;
          for (int i = 1; i <= baseTimes.argSize(); i++) {
            IExpr factor = baseTimes.get(i);
            if (factor.equals(t)) {
              foundT = true;
            } else if (factor.isFree(t)) {
              constants.append(factor);
            } else {
              foundT = false;
              break;
            }
          }
          if (foundT && constants.argSize() > 0) {
            IExpr n = arg1.exponent();
            if (n.isFree(t) && !n.isMinusOne()) {
              IExpr c = constants.oneIdentity1();
              // (c*t)^n = c^n * Gamma(1+n) / s^(1+n)
              return F.Times(F.Power(c, n),
                  F.Divide(F.Gamma(F.Plus(F.C1, n)), F.Power(s, F.Plus(F.C1, n))));
            }
          }
        } else if (!arg1.isPlus() && !arg1.isExpanded()) {
          // Try expanding expressions like (Sin(t) + Cos(t))^2 into a Plus
          IExpr expanded = engine.evaluate(F.Expand(arg1));
          if (expanded.isPlus()) {
            return expanded.mapThread(F.LaplaceTransform(F.Slot1, t, s), 1);
          }
        } else if (arg1.isPlus()) {
          // LaplaceTransform(a_+b_+c_,t_,s_) ->
          // LaplaceTransform(a,t,s)+LaplaceTransform(b,t,s)+LaplaceTransform(c,t,s)
          return arg1.mapThread(F.LaplaceTransform(F.Slot1, t, s), 1);
        }
      }

      // Try TrigReduce to simplify trigonometric products
      IExpr trigReduced = engine.evaluate(F.TrigReduce(a1));
      if (!trigReduced.equals(a1)) {
        IExpr temp = engine.evaluate(F.LaplaceTransform(trigReduced, t, s));
        if (!temp.has(S.LaplaceTransform)) {
          return engine.evaluate(F.Together(temp));
        }
      }

      // Fallback: try symbolic integration definition L{f(t)} = Integrate(f(t)*E^(-s*t), {t, 0,
      // Infinity})
      if (a1.isAST() && t.isSymbol()) {
        IExpr integral = engine.evaluate(
            F.Integrate(F.Times(a1, F.Exp(F.Times(F.CN1, s, t))), F.list(t, F.C0, F.CInfinity)));
        if (integral.isPresent() && !integral.has(S.Integrate)
            && !integral.has(S.LaplaceTransform)) {
          return integral;
        }
      }
    }
    return F.NIL;
  }

  /**
   * The transform of a function which switches on or off at a point: a step, a product with a
   * step, or a <code>Piecewise</code> in <code>t</code>.
   *
   * <p>
   * These are the forcing terms of the Laplace chapter of every textbook on differential equations,
   * and the transform of each is elementary by the second shift theorem,
   * <code>L{f(t)*UnitStep(t-a)} == E^(-a*s)*L{f(t+a)}</code> for <code>a &gt;= 0</code>. A
   * <code>Piecewise</code> whose pieces hold on intervals of <code>t</code> is the same thing
   * written differently: a piece <code>v</code> on <code>lo &lt;= t &lt; hi</code> is
   * <code>v*(UnitStep(t-lo) - UnitStep(t-hi))</code>.
   *
   * @return {@link F#NIL} if <code>a1</code> is not of one of those shapes
   */
  private static IExpr laplaceTransformOfSteps(EvalEngine engine, IExpr a1, IExpr t, IExpr s) {
    if (a1.isFree(x -> isStep(x, t) || x.isAST(S.Piecewise), true)) {
      return F.NIL;
    }
    if (a1.isAST(S.Piecewise)) {
      IExpr steps = piecewiseAsSteps((IAST) a1, t, engine);
      if (steps.isNIL()) {
        return F.NIL;
      }
      IExpr transformed = engine.evaluate(F.LaplaceTransform(F.Expand(steps), t, s));
      return transformed.has(S.LaplaceTransform) ? F.NIL : transformed;
    }
    IExpr step;
    IExpr rest;
    if (isStep(a1, t)) {
      step = a1;
      rest = F.C1;
    } else if (a1.isTimes()) {
      IAST times = (IAST) a1;
      int index = times.indexOf(x -> isStep(x, t));
      if (index <= 0) {
        return F.NIL;
      }
      step = times.get(index);
      rest = times.removeAtCopy(index).oneIdentity1();
      if (!rest.isFree(x -> isStep(x, t), true)) {
        // A product of steps is a step too, but not one this reads off.
        return F.NIL;
      }
    } else {
      return F.NIL;
    }
    // The argument of the step is c*t - b; it switches on at a == b/c when c is positive, and off
    // there when c is negative.
    IExpr argument = step.first();
    IExpr c = engine.evaluate(F.Coefficient(argument, t, F.C1));
    IExpr b = engine.evaluate(F.Negate(F.Coefficient(argument, t, F.C0)));
    if (c.isZero() || !c.isFree(t) || !b.isFree(t)
        || !engine.evaluate(F.Subtract(argument, F.Subtract(F.Times(c, t), b))).isZero()) {
      return F.NIL;
    }
    IExpr a = engine.evaluate(F.Divide(b, c));
    if (c.isNegative()) {
      // UnitStep(a - t) is 1 - UnitStep(t - a) everywhere but at a, which the integral does not see.
      IExpr on = engine.evaluate(F.LaplaceTransform(rest, t, s));
      IExpr off = engine.evaluate(
          F.LaplaceTransform(F.Times(rest, F.UnitStep(F.Subtract(t, a))), t, s));
      if (on.has(S.LaplaceTransform) || off.has(S.LaplaceTransform)) {
        return F.NIL;
      }
      return engine.evaluate(F.Subtract(on, off));
    }
    if (!c.isPositive()) {
      return F.NIL;
    }
    if (engine.evaluate(F.LessEqual(a, F.C0)).isTrue()) {
      // A step which is already on at t == 0 is 1 on the whole of the transform's range.
      IExpr transformed = engine.evaluate(F.LaplaceTransform(rest, t, s));
      return transformed.has(S.LaplaceTransform) ? F.NIL : transformed;
    }
    if (!engine.evaluate(F.Greater(a, F.C0)).isTrue()) {
      // The sign of where it switches on is not known, and the two cases have different answers.
      return F.NIL;
    }
    IExpr shifted = engine.evaluate(F.subst(rest, t, F.Plus(t, a)));
    IExpr transformed = engine.evaluate(F.LaplaceTransform(shifted, t, s));
    if (transformed.has(S.LaplaceTransform)) {
      return F.NIL;
    }
    return engine.evaluate(F.Times(F.Exp(F.Times(F.CN1, a, s)), transformed));
  }

  /** Whether <code>expr</code> is a unit step in <code>t</code>. */
  private static boolean isStep(IExpr expr, IExpr t) {
    return (expr.isAST(S.UnitStep, 2) || expr.isAST(S.HeavisideTheta, 2)) && !expr.isFree(t);
  }

  /**
   * <code>Piecewise</code> rewritten as a sum of steps, for pieces which hold on intervals of
   * <code>t</code> and a default of zero.
   *
   * @return {@link F#NIL} for any other <code>Piecewise</code>
   */
  private static IExpr piecewiseAsSteps(IAST piecewise, IExpr t, EvalEngine engine) {
    if (piecewise.argSize() < 1 || !piecewise.arg1().isList()) {
      return F.NIL;
    }
    IExpr otherwise = piecewise.argSize() >= 2 ? piecewise.arg2() : F.C0;
    if (!otherwise.isZero()) {
      return F.NIL;
    }
    IAST pieces = (IAST) piecewise.arg1();
    IASTAppendable sum = F.PlusAlloc(pieces.argSize());
    IExpr previousHi = F.CNInfinity;
    for (int i = 1; i <= pieces.argSize(); i++) {
      IExpr piece = pieces.get(i);
      if (!piece.isList() || ((IAST) piece).argSize() != 2) {
        return F.NIL;
      }
      IExpr value = ((IAST) piece).arg1();
      IExpr[] bounds = intervalOf(((IAST) piece).arg2(), t);
      if (bounds == null) {
        return F.NIL;
      }
      // The first piece whose condition holds is the one which applies, so pieces which overlap
      // would count twice here. Those are declined: the pieces have to follow one another.
      if (!engine.evaluate(F.GreaterEqual(bounds[0], previousHi)).isTrue()) {
        return F.NIL;
      }
      previousHi = bounds[1];
      IExpr on = F.UnitStep(F.Subtract(t, bounds[0]));
      IExpr window = bounds[1].isInfinity() ? on
          : F.Subtract(on, F.UnitStep(F.Subtract(t, bounds[1])));
      sum.append(F.Times(value, window));
    }
    return engine.evaluate(sum.oneIdentity0());
  }

  /**
   * The interval <code>{lo, hi}</code> a condition on <code>t</code> describes, with
   * <code>hi</code> possibly <code>Infinity</code>, or <code>null</code>. Whether an end is open
   * does not matter to an integral.
   */
  private static IExpr[] intervalOf(IExpr condition, IExpr t) {
    IExpr lo = F.CNInfinity;
    IExpr hi = F.CInfinity;
    IAST parts;
    if (condition.isAST(S.And)) {
      parts = (IAST) condition;
    } else {
      parts = F.List(condition);
    }
    for (int i = 1; i <= parts.argSize(); i++) {
      IExpr part = parts.get(i);
      if (part.isAST(S.Inequality) && ((IAST) part).argSize() == 5
          && ((IAST) part).arg3().equals(t)) {
        IAST inequality = (IAST) part;
        if (!isLess(inequality.arg2()) || !isLess(inequality.get(4))) {
          return null;
        }
        lo = inequality.arg1();
        hi = inequality.get(5);
      } else if (part.isAST2() && (isLess(part.head()) || isGreater(part.head()))) {
        IExpr left = part.first();
        IExpr right = part.second();
        boolean less = isLess(part.head());
        if (left.equals(t) && right.isFree(t)) {
          if (less) {
            hi = right;
          } else {
            lo = right;
          }
        } else if (right.equals(t) && left.isFree(t)) {
          if (less) {
            lo = left;
          } else {
            hi = left;
          }
        } else {
          return null;
        }
      } else {
        return null;
      }
    }
    if (!lo.isFree(t) || !hi.isFree(t) || lo.isNegativeInfinity()) {
      return null;
    }
    return new IExpr[] {lo, hi};
  }

  private static boolean isLess(IExpr head) {
    return head == S.Less || head == S.LessEqual;
  }

  private static boolean isGreater(IExpr head) {
    return head == S.Greater || head == S.GreaterEqual;
  }

  private IExpr laplaceTransformTimes(EvalEngine engine, IExpr t, IExpr s, IAST arg1) {
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
        n = (IInteger) temp.exponent();
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
          return F.Times(F.Power(-1, n), derivedLaplaceTransform);
        }

      }
    }
    // Division-by-t rule: L{f(t)/t} = Integrate(F(u), {u, s, Infinity})
    // Match t^(-1) or t^(-n) factor for negative integer exponent
    int indexOfNegPower = timesAST.indexOf(x -> (x.isPower() && x.base().equals(t)
        && x.exponent().isInteger() && x.exponent().isNegative()));
    if (indexOfNegPower > 0) {
      IExpr negPowFactor = timesAST.get(indexOfNegPower);
      IInteger negN = (IInteger) negPowFactor.exponent(); // e.g. -1, -2
      IInteger posN = negN.negate();
      IASTMutable remainder = timesAST.removeAtCopy(indexOfNegPower);
      // L{f(t)/t^n} = repeated integration: ∫_s^∞ ... ∫_s^∞ F(u) du (n times)
      // For n=1: Integrate(LaplaceTransform(remainder, t, u$), {u$, s, Infinity})
      if (posN.isOne()) {
        ISymbol u = F.Dummy("u");
        IExpr innerLT = engine.evaluate(F.LaplaceTransform(remainder, t, u));
        if (!innerLT.has(S.LaplaceTransform)) {
          IExpr temp = engine.evaluate(F.Integrate(innerLT, F.list(u, s, F.CInfinity)));
          if (!temp.has(S.Integrate)) {
            return temp;
          }
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
