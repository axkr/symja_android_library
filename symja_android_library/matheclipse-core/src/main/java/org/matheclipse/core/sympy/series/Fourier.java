package org.matheclipse.core.sympy.series;

import java.util.Map;
import java.util.TreeMap;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.sympy.exception.ValueError;

/**
 * Fourier series. Ported from
 * <a href="https://github.com/sympy/sympy/blob/master/sympy/series/fourier.py">sympy/series/fourier.py</a>
 *
 * <p>
 * The lazy <code>FourierSeries</code> sequence object of sympy isn't ported. The coefficients and
 * the truncated series are returned as expressions.
 */
public class Fourier {

  private Fourier() {}

  /**
   * Integrate <code>function</code> from <code>lower</code> to <code>upper</code>.
   *
   * @return {@link F#NIL} if the integral can't be evaluated in closed form
   */
  private static IExpr integrate(IExpr function, IExpr x, IExpr lower, IExpr upper, IExpr n,
      EvalEngine engine) {
    IExpr integral = F.NIL;
    if (!function.isFree(S.Piecewise, true)) {
      integral = integratePiecewise(function, x, lower, upper, engine);
    }
    if (integral.isNIL()) {
      integral = engine.evaluate(F.Integrate(function, F.List(x, lower, upper)));
    }
    if (!integral.isFree(S.Integrate, true) || integral.isIndeterminate()
        || integral.isDirectedInfinity()) {
      return F.NIL;
    }
    if (!n.isNumber()) {
      // n is assumed to be an integer
      integral = engine.evaluate(F.Simplify(integral, F.Element(n, S.Integers)));
    }
    return integral;
  }

  /**
   * Integrate a piecewise defined function by splitting the interval <code>(lower, upper)</code>
   * at the break points of the conditions.
   *
   * @return {@link F#NIL} if the function can't be splitted into pieces
   */
  private static IExpr integratePiecewise(IExpr function, IExpr x, IExpr lower, IExpr upper,
      EvalEngine engine) {
    IExpr expanded = engine.evaluate(F.PiecewiseExpand(function));
    if (!expanded.isAST(S.Piecewise) || ((IAST) expanded).argSize() < 1
        || !expanded.first().isListOfLists()) {
      return F.NIL;
    }
    IAST piecewise = (IAST) expanded;
    IAST pieces = (IAST) piecewise.arg1();
    IExpr defaultValue = piecewise.argSize() >= 2 ? piecewise.arg2() : F.C0;
    try {
      final double low = lower.evalf();
      final double up = upper.evalf();
      if (!(low < up)) {
        return F.NIL;
      }
      TreeMap<Double, IExpr> points = new TreeMap<Double, IExpr>();
      points.put(low, lower);
      points.put(up, upper);
      for (int i = 1; i < pieces.size(); i++) {
        IAST piece = (IAST) pieces.get(i);
        if (!piece.isAST2() || !piece.arg1().isFree(S.Piecewise, true)
            || !collectBreakPoints(piece.arg2(), x, low, up, points)) {
          return F.NIL;
        }
      }
      if (!defaultValue.isFree(S.Piecewise, true)) {
        return F.NIL;
      }
      IASTAppendable sum = F.PlusAlloc(points.size());
      Map.Entry<Double, IExpr> previous = null;
      for (Map.Entry<Double, IExpr> entry : points.entrySet()) {
        if (previous != null) {
          final IExpr middle = F.num(0.5 * (previous.getKey() + entry.getKey()));
          IExpr active = defaultValue;
          for (int i = 1; i < pieces.size(); i++) {
            IAST piece = (IAST) pieces.get(i);
            IExpr condition = engine.evaluate(F.subst(piece.arg2(), x, middle));
            if (condition.isTrue()) {
              active = piece.arg1();
              break;
            }
            if (!condition.isFalse()) {
              return F.NIL;
            }
          }
          IExpr integral = engine
              .evaluate(F.Integrate(active, F.List(x, previous.getValue(), entry.getValue())));
          if (!integral.isFree(S.Integrate, true)) {
            return F.NIL;
          }
          sum.append(integral);
        }
        previous = entry;
      }
      return engine.evaluate(sum);
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return F.NIL;
  }

  /**
   * Collect the break points <code>c</code> of the inequalities <code>x &lt; c</code> in the
   * <code>condition</code>, which are inside the open interval <code>(low, up)</code>.
   *
   * @return <code>false</code> if the condition contains unsupported expressions
   */
  private static boolean collectBreakPoints(IExpr condition, IExpr x, double low, double up,
      Map<Double, IExpr> points) {
    if (condition.isTrue() || condition.isFalse()) {
      return true;
    }
    if (!condition.isAST()) {
      return false;
    }
    IAST ast = (IAST) condition;
    if (ast.isAnd() || ast.isOr() || ast.isNot()) {
      for (int i = 1; i < ast.size(); i++) {
        if (!collectBreakPoints(ast.get(i), x, low, up, points)) {
          return false;
        }
      }
      return true;
    }
    if (ast.isRelational() || ast.isEqual() || ast.isAST(S.Unequal)) {
      // also chained inequalities like a < x < b
      for (int i = 1; i < ast.size(); i++) {
        IExpr arg = ast.get(i);
        if (arg.equals(x)) {
          continue;
        }
        if (!arg.isFree(x)) {
          return false;
        }
        double point = arg.evalf();
        if (Double.isNaN(point)) {
          return false;
        }
        if (low < point && point < up) {
          points.put(point, arg);
        }
      }
      return true;
    }
    return false;
  }

  private static void checkLimits(IExpr x, IExpr lower, IExpr upper) {
    if (!x.isVariable()) {
      throw new ValueError("Invalid limits given: " + x);
    }
    if (lower.isDirectedInfinity() || upper.isDirectedInfinity()) {
      throw new ValueError("Both the start and end value should be bounded");
    }
  }

  /**
   * Returns the coefficient <code>a(n)</code> of the term <code>Cos(2*n*Pi*x/L)</code> in the
   * Fourier series of <code>function</code> over the interval <code>(lower, upper)</code> with
   * <code>L = upper - lower</code>:
   *
   * <pre>
   * a(n) = 2/L * Integrate(function * Cos(2*n*Pi*x/L), {x, lower, upper})
   * </pre>
   *
   * <p>
   * <b>Note:</b> the constant term of the Fourier series is <code>a(0)/2</code>.
   *
   * @param n an integer or a symbol, which is assumed to be an integer
   * @return {@link F#NIL} if the integral can't be evaluated in closed form
   */
  public static IExpr fourierCosCoefficient(IExpr function, IExpr x, IExpr lower, IExpr upper,
      IExpr n, EvalEngine engine) {
    // x, L = limits[0], limits[2] - limits[1]
    // cos_term = cos(2*n*pi*x / L)
    // formula = 2 * cos_term * integrate(func * cos_term, limits) / L
    checkLimits(x, lower, upper);
    IExpr L = engine.evaluate(F.Subtract(upper, lower));
    IExpr cosTerm = F.Cos(F.Times(F.C2, n, S.Pi, x, F.Power(L, F.CN1)));
    IExpr integral = integrate(F.Times(function, cosTerm), x, lower, upper, n, engine);
    if (integral.isNIL()) {
      return F.NIL;
    }
    return engine.evaluate(F.Times(F.C2, integral, F.Power(L, F.CN1)));
  }

  /**
   * Returns the coefficient <code>b(n)</code> of the term <code>Sin(2*n*Pi*x/L)</code> in the
   * Fourier series of <code>function</code> over the interval <code>(lower, upper)</code> with
   * <code>L = upper - lower</code>:
   *
   * <pre>
   * b(n) = 2/L * Integrate(function * Sin(2*n*Pi*x/L), {x, lower, upper})
   * </pre>
   *
   * @param n an integer or a symbol, which is assumed to be an integer
   * @return {@link F#NIL} if the integral can't be evaluated in closed form
   */
  public static IExpr fourierSinCoefficient(IExpr function, IExpr x, IExpr lower, IExpr upper,
      IExpr n, EvalEngine engine) {
    // x, L = limits[0], limits[2] - limits[1]
    // sin_term = sin(2*n*pi*x / L)
    // 2 * sin_term * integrate(func * sin_term, limits) / L
    checkLimits(x, lower, upper);
    IExpr L = engine.evaluate(F.Subtract(upper, lower));
    IExpr sinTerm = F.Sin(F.Times(F.C2, n, S.Pi, x, F.Power(L, F.CN1)));
    IExpr integral = integrate(F.Times(function, sinTerm), x, lower, upper, n, engine);
    if (integral.isNIL()) {
      return F.NIL;
    }
    return engine.evaluate(F.Times(F.C2, integral, F.Power(L, F.CN1)));
  }

  /**
   * Returns the coefficient <code>c(n)</code> of the term <code>E^(2*I*n*Pi*x/L)</code> in the
   * exponential Fourier series of <code>function</code> over the interval
   * <code>(lower, upper)</code> with <code>L = upper - lower</code>:
   *
   * <pre>
   * c(n) = 1/L * Integrate(function * E^(-2*I*n*Pi*x/L), {x, lower, upper})
   * </pre>
   *
   * @param n an integer or a symbol, which is assumed to be an integer
   * @return {@link F#NIL} if the integral can't be evaluated in closed form
   */
  public static IExpr fourierExpCoefficient(IExpr function, IExpr x, IExpr lower, IExpr upper,
      IExpr n, EvalEngine engine) {
    checkLimits(x, lower, upper);
    IExpr L = engine.evaluate(F.Subtract(upper, lower));
    IExpr expTerm = F.Exp(F.Times(F.CN2, F.CI, n, S.Pi, x, F.Power(L, F.CN1)));
    IExpr integral = integrate(F.Times(function, expTerm), x, lower, upper, n, engine);
    if (integral.isNIL()) {
      return F.NIL;
    }
    return engine.evaluate(F.Times(integral, F.Power(L, F.CN1)));
  }

  /**
   * Computes the Fourier trigonometric series expansion of <code>function</code> over the interval
   * <code>(lower, upper)</code> up to the harmonic <code>order</code>:
   *
   * <pre>
   * a(0)/2 + Sum(a(n)*Cos(2*n*Pi*x/L) + b(n)*Sin(2*n*Pi*x/L), {n, 1, order})
   * </pre>
   *
   * @return {@link F#NIL} if the integrals can't be evaluated in closed form
   */
  public static IExpr fourierSeries(IExpr function, IExpr x, IExpr lower, IExpr upper, int order,
      EvalEngine engine) {
    // >>> s = fourier_series(x**2, (x, -pi, pi))
    // >>> s.truncate(n=3)
    // -4*cos(x) + cos(2*x) + pi**2/3
    if (order < 0) {
      throw new ValueError("Non-negative order expected.");
    }
    checkLimits(x, lower, upper);
    IExpr L = engine.evaluate(F.Subtract(upper, lower));
    if (function.isFree(x)) {
      // if x not in f.free_symbols: return f
      return function;
    }
    IExpr a0 = fourierCosCoefficient(function, x, lower, upper, F.C0, engine);
    if (a0.isNIL()) {
      return F.NIL;
    }
    IASTAppendable series = F.PlusAlloc(2 * order + 1);
    series.append(F.Times(F.C1D2, a0));
    for (int k = 1; k <= order; k++) {
      IExpr n = F.ZZ(k);
      IExpr arg = F.Times(F.C2, n, S.Pi, x, F.Power(L, F.CN1));
      IExpr an = fourierCosCoefficient(function, x, lower, upper, n, engine);
      if (an.isNIL()) {
        return F.NIL;
      }
      IExpr bn = fourierSinCoefficient(function, x, lower, upper, n, engine);
      if (bn.isNIL()) {
        return F.NIL;
      }
      series.append(F.Times(an, F.Cos(arg)));
      series.append(F.Times(bn, F.Sin(arg)));
    }
    return engine.evaluate(series);
  }

  /**
   * Computes the exponential Fourier series expansion of <code>function</code> over the interval
   * <code>(lower, upper)</code>:
   *
   * <pre>
   * Sum(c(n)*E^(2*I*n*Pi*x/L), {n, -order, order})
   * </pre>
   *
   * @return {@link F#NIL} if the integrals can't be evaluated in closed form
   */
  public static IExpr fourierExpSeries(IExpr function, IExpr x, IExpr lower, IExpr upper,
      int order, EvalEngine engine) {
    if (order < 0) {
      throw new ValueError("Non-negative order expected.");
    }
    checkLimits(x, lower, upper);
    IExpr L = engine.evaluate(F.Subtract(upper, lower));
    IASTAppendable series = F.PlusAlloc(2 * order + 1);
    for (int k = -order; k <= order; k++) {
      IExpr n = F.ZZ(k);
      IExpr cn = fourierExpCoefficient(function, x, lower, upper, n, engine);
      if (cn.isNIL()) {
        return F.NIL;
      }
      series.append(F.Times(cn, F.Exp(F.Times(F.C2, F.CI, n, S.Pi, x, F.Power(L, F.CN1)))));
    }
    return engine.evaluate(series);
  }

  /**
   * Computes the Fourier cosine series (i.e. the Fourier series of the even extension) of
   * <code>function</code> given on the interval <code>(0, upper)</code>.
   *
   * @return {@link F#NIL} if the integrals can't be evaluated in closed form
   */
  public static IExpr fourierCosSeries(IExpr function, IExpr x, IExpr upper, int order,
      EvalEngine engine) {
    return halfRangeSeries(function, x, upper, order, true, engine);
  }

  /**
   * Computes the Fourier sine series (i.e. the Fourier series of the odd extension) of
   * <code>function</code> given on the interval <code>(0, upper)</code>.
   *
   * @return {@link F#NIL} if the integrals can't be evaluated in closed form
   */
  public static IExpr fourierSinSeries(IExpr function, IExpr x, IExpr upper, int order,
      EvalEngine engine) {
    return halfRangeSeries(function, x, upper, order, false, engine);
  }

  /**
   * The coefficient of the term <code>Cos(n*Pi*x/upper)</code> (or <code>Sin(n*Pi*x/upper)</code>)
   * in the half range expansion of <code>function</code> given on the interval
   * <code>(0, upper)</code>:
   *
   * <pre>
   * 2/upper * Integrate(function * Cos(n*Pi*x/upper), {x, 0, upper})
   * </pre>
   *
   * @return {@link F#NIL} if the integral can't be evaluated in closed form
   */
  public static IExpr halfRangeCoefficient(IExpr function, IExpr x, IExpr upper, IExpr n,
      boolean cosine, EvalEngine engine) {
    checkLimits(x, F.C0, upper);
    IExpr arg = F.Times(n, S.Pi, x, F.Power(upper, F.CN1));
    IExpr term = cosine ? F.Cos(arg) : F.Sin(arg);
    IExpr integral = integrate(F.Times(function, term), x, F.C0, upper, n, engine);
    if (integral.isNIL()) {
      return F.NIL;
    }
    return engine.evaluate(F.Times(F.C2, integral, F.Power(upper, F.CN1)));
  }

  private static IExpr halfRangeSeries(IExpr function, IExpr x, IExpr upper, int order,
      boolean cosine, EvalEngine engine) {
    if (order < 0) {
      throw new ValueError("Non-negative order expected.");
    }
    IASTAppendable series = F.PlusAlloc(order + 1);
    if (cosine) {
      IExpr a0 = halfRangeCoefficient(function, x, upper, F.C0, true, engine);
      if (a0.isNIL()) {
        return F.NIL;
      }
      series.append(F.Times(F.C1D2, a0));
    }
    for (int k = 1; k <= order; k++) {
      IExpr n = F.ZZ(k);
      IExpr coefficient = halfRangeCoefficient(function, x, upper, n, cosine, engine);
      if (coefficient.isNIL()) {
        return F.NIL;
      }
      IExpr arg = F.Times(n, S.Pi, x, F.Power(upper, F.CN1));
      series.append(F.Times(coefficient, cosine ? F.Cos(arg) : F.Sin(arg)));
    }
    return engine.evaluate(series);
  }
}
