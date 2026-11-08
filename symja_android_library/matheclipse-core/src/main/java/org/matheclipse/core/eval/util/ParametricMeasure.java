package org.matheclipse.core.eval.util;

import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The measure of a parametrized region: the integral of the volume element of a map
 * <code>f(u1,...,uk)</code> over a box of the parameters. The coordinates of <code>f</code> can be
 * those of a chart <code>"Polar"</code>, <code>"Cylindrical"</code> or <code>"Spherical"</code>.
 */
public final class ParametricMeasure {

  /** The positions of the sample points in the range of a parameter. */
  private static final double[] FRACTIONS = {0.13, 0.41, 0.67, 0.89};

  private ParametricMeasure() {}

  /**
   * The Cartesian coordinates of a point given in a chart.
   *
   * @return {@link F#NIL} for an unknown chart or a wrong number of coordinates
   */
  private static IExpr cartesian(IAST point, IExpr chart) {
    if (chart.isNIL() || chart.isString("Cartesian")) {
      return point;
    }
    if (chart.isString("Polar") && point.argSize() == 2) {
      return F.list(F.Times(point.arg1(), F.Cos(point.arg2())),
          F.Times(point.arg1(), F.Sin(point.arg2())));
    }
    if (chart.isString("Cylindrical") && point.argSize() == 3) {
      return F.list(F.Times(point.arg1(), F.Cos(point.arg2())),
          F.Times(point.arg1(), F.Sin(point.arg2())), point.arg3());
    }
    if (chart.isString("Spherical") && point.argSize() == 3) {
      IExpr radial = F.Times(point.arg1(), F.Sin(point.arg2()));
      return F.list(F.Times(radial, F.Cos(point.arg3())), F.Times(radial, F.Sin(point.arg3())),
          F.Times(point.arg1(), F.Cos(point.arg2())));
    }
    return F.NIL;
  }

  /** The parameters are real and within their ranges. */
  private static IExpr assumptions(IAST iterators) {
    IASTAppendable and = F.ast(S.And, 3 * iterators.argSize());
    for (IExpr iterator : iterators) {
      IExpr u = iterator.first();
      and.append(F.Element(u, S.Reals));
      if (!iterator.second().isInfinity() && !iterator.second().isNegativeInfinity()) {
        and.append(F.GreaterEqual(u, iterator.second()));
      }
      if (!iterator.getAt(3).isInfinity() && !iterator.getAt(3).isNegativeInfinity()) {
        and.append(F.LessEqual(u, iterator.getAt(3)));
      }
    }
    return and;
  }

  /** A point of the range <code>[lo, hi]</code>, which can be infinite. */
  private static double samplePoint(double lo, double hi, double fraction) {
    if (Double.isInfinite(lo) && Double.isInfinite(hi)) {
      return Math.tan(Math.PI * (fraction - 0.5));
    }
    if (Double.isInfinite(hi)) {
      return lo + fraction / (1.0 - fraction);
    }
    if (Double.isInfinite(lo)) {
      return hi - fraction / (1.0 - fraction);
    }
    return lo + (hi - lo) * fraction;
  }

  private static double bound(IExpr bound, IAST rules, EvalEngine engine) {
    IExpr value = engine.evaluate(F.subst(bound, rules));
    if (value.isInfinity()) {
      return Double.POSITIVE_INFINITY;
    }
    if (value.isNegativeInfinity()) {
      return Double.NEGATIVE_INFINITY;
    }
    return engine.evalDouble(value);
  }

  /**
   * The sign of an expression on the sample points of the parameters with the index
   * <code>from</code> and later ones.
   *
   * @return <code>1</code> or <code>-1</code> for one sign, <code>0</code> for both or none
   */
  private static int sign(IExpr expr, IAST iterators, int from, IASTAppendable rules,
      EvalEngine engine) {
    if (from > iterators.argSize()) {
      double value = engine.evalDouble(F.subst(expr, rules));
      return value > 0.0 ? 1 : (value < 0.0 ? -1 : 0);
    }
    IExpr iterator = iterators.get(from);
    double lo = bound(iterator.second(), rules, engine);
    double hi = bound(iterator.getAt(3), rules, engine);
    int result = 2;
    int size = rules.size();
    for (double fraction : FRACTIONS) {
      engine.checkTimeLimit();
      rules.append(F.Rule(iterator.first(), F.num(samplePoint(lo, hi, fraction))));
      int sign = sign(expr, iterators, from + 1, rules, engine);
      rules.remove(size);
      if (sign == 0 || (result != 2 && result != sign)) {
        return 0;
      }
      result = sign;
    }
    return result;
  }

  /**
   * The Jacobian determinant without its absolute value, if its sign is the same on the samples of
   * the box. Symbols other than the parameters are taken as positive numbers.
   */
  private static IExpr volumeElement(IExpr determinant, IAST iterators, EvalEngine engine) {
    IASTAppendable rules = F.ListAlloc(iterators.argSize() + 4);
    IAST all = F.List(determinant, iterators);
    int index = 1;
    for (IExpr symbol : new VariablesSet(all).getVarList()) {
      if (!iterators.exists(x -> x.first().equals(symbol))) {
        rules.append(F.Rule(symbol, F.num(1.0 + 0.37 * index++)));
      }
    }
    int sign;
    try {
      sign = sign(determinant, iterators, 1, rules, engine);
    } catch (ArgumentTypeException ate) {
      sign = 0;
    }
    if (sign == 0) {
      return F.Abs(determinant);
    }
    return sign > 0 ? determinant : F.Negate(determinant);
  }

  /**
   * The measure of the image of a box of parameters.
   *
   * @param map the list of the coordinates
   * @param iterators the list of the ranges <code>{u, umin, umax}</code>
   * @param chart the name of a chart, or {@link F#NIL} for Cartesian coordinates
   * @param engine the evaluation engine
   * @return {@link F#NIL} if the arguments have no such form
   */
  public static IExpr measure(IExpr map, IAST iterators, IExpr chart, EvalEngine engine) {
    int k = iterators.argSize();
    if (!map.isList() || k == 0 || !iterators.forAll(x -> x.isList3() && x.first().isVariable())) {
      return F.NIL;
    }
    IExpr point = cartesian((IAST) map, chart);
    if (point.isNIL() || point.argSize() < k) {
      return F.NIL;
    }
    IExpr parameters = iterators.map(IExpr::first);
    IExpr jacobian = engine.evaluate(F.Grad(point, parameters));
    if (!jacobian.isList()) {
      return F.NIL;
    }
    IExpr assumptions = assumptions(iterators);
    IExpr integrand;
    if (point.argSize() == k) {
      IExpr determinant = engine.evaluate(F.Simplify(F.Det(jacobian), assumptions));
      integrand = volumeElement(determinant, iterators, engine);
    } else {
      IExpr gram =
          engine.evaluate(F.Simplify(F.Det(F.Dot(F.Transpose(jacobian), jacobian)), assumptions));
      integrand = engine.evaluate(F.Simplify(F.Sqrt(gram), assumptions));
    }
    IASTAppendable integral = F.ast(S.Integrate, k + 1);
    integral.append(integrand);
    integral.appendArgs(iterators);
    return engine.evaluate(integral);
  }
}
