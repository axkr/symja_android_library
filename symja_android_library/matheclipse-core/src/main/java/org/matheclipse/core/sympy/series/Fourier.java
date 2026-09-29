package org.matheclipse.core.sympy.series;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
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
    IExpr integral = engine.evaluate(F.Integrate(function, F.List(x, lower, upper)));
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
