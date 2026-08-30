package org.matheclipse.core.expression;

import org.apfloat.Apfloat;
import org.matheclipse.core.eval.exception.SymjaMathException;
import org.apfloat.LossOfPrecisionException;
import org.matheclipse.core.eval.EvalEngine;
import org.hipparchus.complex.Complex;
import org.matheclipse.core.numerics.functions.BesselIK;
import org.matheclipse.core.numerics.functions.ComplexGamma;
import org.matheclipse.core.numerics.functions.BesselJY;

public class DMath {

  /**
   * A double as an {@link Apfloat}, declining the ones it has no representation for.
   *
   * <p>
   * {@code new Apfloat(Double.POSITIVE_INFINITY)} answers a {@code NumberFormatException} saying
   * "Infinity is not a valid number", which escaped from whichever special function was being
   * evaluated - {@code HankelH2(2, Infinity)} and {@code BesselJ(2, Infinity)} among them. Raised
   * as a Symja exception instead, {@code EvalEngine.evalASTBuiltinFunction} catches it and the
   * expression comes back unevaluated with a message, which is what every function here wants when
   * its argument has no arbitrary precision value. Functions with a known limit at infinity handle
   * it before reaching this point, as {@link #airyAi(double)} does.
   *
   * @param value the argument to convert
   * @return the value as an {@link Apfloat}
   */
  private static Apfloat apfloatOf(double value) {
    if (!Double.isFinite(value)) {
      throw new SymjaMathException(
          "cannot convert " + value + " into an arbitrary precision number");
    }
    return new Apfloat(value);
  }

  // --- Missing Arc- and Hyperbolic Trig Functions ---

  public static double acosh(double x) {
    return Math.log(x + Math.sqrt(x * x - 1.0));
  }

  public static double asinh(double x) {
    return Math.log(x + Math.sqrt(x * x + 1.0));
  }

  public static double atanh(double x) {
    return 0.5 * Math.log((1.0 + x) / (1.0 - x));
  }

  public static double csc(double x) {
    return 1.0 / Math.sin(x);
  }

  public static double sec(double x) {
    return 1.0 / Math.cos(x);
  }

  public static double cot(double x) {
    return 1.0 / Math.tan(x);
  }

  public static double csch(double x) {
    return 1.0 / Math.sinh(x);
  }

  public static double sech(double x) {
    return 1.0 / Math.cosh(x);
  }

  public static double coth(double x) {
    return 1.0 / Math.tanh(x);
  }

  public static double acsc(double x) {
    return Math.asin(1.0 / x);
  }

  public static double asec(double x) {
    return Math.acos(1.0 / x);
  }

  public static double acot(double x) {
    return Math.PI / 2.0 - Math.atan(x);
  }

  public static double acsch(double x) {
    return asinh(1.0 / x);
  }

  public static double asech(double x) {
    return acosh(1.0 / x);
  }

  public static double acoth(double x) {
    return atanh(1.0 / x);
  }

  // --- Existing Special Functions ---

  public static double agm(double a, double b) {
    return EvalEngine.getApfloatDouble().agm(apfloatOf(a), apfloatOf(b)).doubleValue();
  }

  /**
   * <code>AiryAi(x)</code> as a machine double, answering <code>0.0</code> where an accurate value
   * is out of reach.
   *
   * <p>
   * Apfloat raises a {@link LossOfPrecisionException} from about <code>|x| = 10^10</code> upwards.
   * For positive <code>x</code> the function has decayed like
   * <code>exp(-2/3*x^(3/2))</code> long before that and underflowed to zero anyway - it is already
   * <code>2.6*10^-291</code> at <code>x = 100</code>. For negative <code>x</code> it oscillates
   * with amplitude <code>|x|^(-1/4)/sqrt(Pi)</code> and a phase of <code>2/3*|x|^(3/2)</code>,
   * which is exactly the quantity that has outrun the resolution of a double by the point apfloat
   * gives up, so the centre of the oscillation is the best available answer. Mathematica answers
   * <code>0.</code> at either end, with an underflow message for the positive one.
   */
  public static double airyAi(double value) {
    try {
      return EvalEngine.getApfloatDouble().airyAi(apfloatOf(value)).doubleValue();
    } catch (LossOfPrecisionException lpe) {
      return 0.0;
    }
  }

  /**
   * <code>AiryAi'(x)</code> as a machine double, answering <code>0.0</code> where an accurate value
   * is out of reach, for the reasons given on {@link #airyAi(double)}. Mathematica answers
   * <code>0</code> with an underflow message at <code>1.79*10^308</code>.
   */
  public static double airyAiPrime(double value) {
    try {
      return EvalEngine.getApfloatDouble().airyAiPrime(apfloatOf(value)).doubleValue();
    } catch (LossOfPrecisionException lpe) {
      return 0.0;
    }
  }

  /**
   * <code>AiryBi(x)</code> as a machine double, answering the limit where an accurate value is out
   * of reach.
   *
   * <p>
   * Apfloat raises a {@link LossOfPrecisionException} from about <code>|x| = 10^10</code> upwards,
   * the same point at which {@link #airyAi(double)} gives up, but the two ends behave differently
   * here. For positive <code>x</code> the function grows like <code>exp(2/3*x^(3/2))</code> and has
   * long outrun what a double can hold, so an infinity is the honest answer and the caller turns it
   * into <code>Overflow()</code>. For negative <code>x</code> it oscillates about zero with
   * amplitude <code>|x|^(-1/4)/sqrt(Pi)</code>, exactly as AiryAi does, so the centre of the
   * oscillation is the best available value.
   */
  public static double airyBi(double value) {
    try {
      return EvalEngine.getApfloatDouble().airyBi(apfloatOf(value)).doubleValue();
    } catch (LossOfPrecisionException lpe) {
      return value > 0.0 ? Double.POSITIVE_INFINITY : 0.0;
    }
  }

  /**
   * <code>AiryBi'(x)</code> as a machine double, answering the limit where an accurate value is out
   * of reach, for the reasons given on {@link #airyBi(double)}.
   */
  public static double airyBiPrime(double value) {
    try {
      return EvalEngine.getApfloatDouble().airyBiPrime(apfloatOf(value)).doubleValue();
    } catch (LossOfPrecisionException lpe) {
      return value > 0.0 ? Double.POSITIVE_INFINITY : 0.0;
    }
  }

  public static double angerJ(double v, double z) {
    return EvalEngine.getApfloatDouble().angerJ(apfloatOf(v), apfloatOf(z)).doubleValue();
  }

  /**
   * <code>BesselI(v, x)</code> as a machine double. Same split as {@link #besselJ(double, double)}:
   * {@link BesselIK} where it is validated, Apfloat elsewhere and whenever BesselIK reports NaN.
   */
  public static double besselI(double v, double x) {
    if (BesselIK.isSupported(v, x)) {
      double result = BesselIK.besselI(v, x);
      if (!Double.isNaN(result)) {
        return result;
      }
    }
    return EvalEngine.getApfloatDouble().besselI(apfloatOf(v), apfloatOf(x)).doubleValue();
  }

  /**
   * <code>BesselJ(v, x)</code> as a machine double.
   *
   * <p>
   * Uses {@link BesselJY} where that is validated and falls back to Apfloat everywhere else. The
   * fall-back is not a formality: Apfloat needs milliseconds for a single evaluation - enough to
   * make a Bessel-zero root find take a second - while the double implementation needs well under a
   * microsecond, so the split is what makes numeric Bessel work usable at all. {@link BesselJY}
   * reports NaN when its own conditioning check fails, which routes those points here too.
   */
  public static double besselJ(double v, double x) {
    if (BesselJY.isSupported(v, x)) {
      double result = BesselJY.besselJ(v, x);
      if (!Double.isNaN(result)) {
        return result;
      }
    }
    return EvalEngine.getApfloatDouble().besselJ(apfloatOf(v), apfloatOf(x)).doubleValue();
  }

  /**
   * <code>BesselK(v, x)</code> as a machine double. See {@link #besselI(double, double)}. Note that
   * {@code BesselJS.besselK} is NOT used: it is wrong by 8e-7 against Apfloat at 40 digits.
   */
  public static double besselK(double v, double x) {
    if (BesselIK.isSupported(v, x)) {
      double result = BesselIK.besselK(v, x);
      if (!Double.isNaN(result)) {
        return result;
      }
    }
    return EvalEngine.getApfloatDouble().besselK(apfloatOf(v), apfloatOf(x)).doubleValue();
  }

  /**
   * <code>BesselY(v, x)</code> as a machine double. See {@link #besselJ(double, double)} for how
   * the double implementation and the Apfloat fall-back divide the domain.
   */
  public static double besselY(double v, double x) {
    if (BesselJY.isSupported(v, x)) {
      double result = BesselJY.besselY(v, x);
      if (!Double.isNaN(result)) {
        return result;
      }
    }
    return EvalEngine.getApfloatDouble().besselY(apfloatOf(v), apfloatOf(x)).doubleValue();
  }

  /**
   * <code>Beta(a, b)</code> as a machine double.
   *
   * <p>
   * Routed through the complex log-gamma form rather than the real quotient
   * <code>Gamma(a)Gamma(b)/Gamma(a+b)</code>: the quotient loses the answer next to the poles at
   * the non-positive integers, where Beta is legitimately enormous rather than undefined. Beta of
   * real arguments is real, so the imaginary part is only rounding residue - a result carrying more
   * than that is not trusted and falls back.
   */
  public static double beta(double a, double b) {
    Complex beta = ComplexGamma.beta(new Complex(a, 0.0), new Complex(b, 0.0));
    if (beta != null) {
      double re = beta.getReal();
      double im = beta.getImaginary();
      if (Math.abs(im) <= 1.0e-12 * Math.abs(re)) {
        return re;
      }
    }
    return EvalEngine.getApfloatDouble().beta(apfloatOf(a), apfloatOf(b)).doubleValue();
  }

  public static double beta(double x, double a, double b) {
    return EvalEngine.getApfloatDouble().beta(apfloatOf(x), apfloatOf(a), apfloatOf(b))
        .doubleValue();
  }

  public static double beta(double x1, double x2, double a, double b) {
    return EvalEngine.getApfloatDouble()
        .beta(apfloatOf(x1), apfloatOf(x2), apfloatOf(a), apfloatOf(b)).doubleValue();
  }

  public static double chebyshevT(double v, double x) {
    return EvalEngine.getApfloatDouble().chebyshevT(apfloatOf(v), apfloatOf(x)).doubleValue();
  }

  public static double chebyshevU(double v, double x) {
    return EvalEngine.getApfloatDouble().chebyshevU(apfloatOf(v), apfloatOf(x)).doubleValue();
  }

  public static double ellipticE(double value) {
    return EvalEngine.getApfloatDouble().ellipticE(apfloatOf(value)).doubleValue();
  }

  public static double ellipticK(double value) {
    return EvalEngine.getApfloatDouble().ellipticK(apfloatOf(value)).doubleValue();
  }

  public static double erf(double value) {
    return org.hipparchus.special.Erf.erf(value);
  }

  public static double erfc(double value) {
    return org.hipparchus.special.Erf.erfc(value);
  }

  public static double erfi(double value) {
    return EvalEngine.getApfloatDouble().erfi(apfloatOf(value)).doubleValue();
  }

  public static double fibonacci(double n, double x) {
    return EvalEngine.getApfloatDouble().fibonacci(apfloatOf(n), apfloatOf(x)).doubleValue();
  }

  public static double fresnelC(double value) {
    return EvalEngine.getApfloatDouble().fresnelC(apfloatOf(value)).doubleValue();
  }

  public static double fresnelS(double value) {
    return EvalEngine.getApfloatDouble().fresnelS(apfloatOf(value)).doubleValue();
  }

  public static double gamma(double value) {
    return org.hipparchus.special.Gamma.gamma(value);
  }

  public static double gamma(double a, double x) {
    return EvalEngine.getApfloatDouble().gamma(apfloatOf(a), apfloatOf(x)).doubleValue();
  }

  public static double gamma(double a, double x0, double x1) {
    return EvalEngine.getApfloatDouble().gamma(apfloatOf(a), apfloatOf(x0), apfloatOf(x1))
        .doubleValue();
  }

  public static double gegenbauerC(double n, double x) {
    return EvalEngine.getApfloatDouble().gegenbauerC(apfloatOf(n), apfloatOf(x)).doubleValue();
  }

  public static double gegenbauerC(double n, double m, double x) {
    return EvalEngine.getApfloatDouble().gegenbauerC(apfloatOf(n), apfloatOf(m), apfloatOf(x))
        .doubleValue();
  }

  public static double harmonicNumber(double value) {
    return EvalEngine.getApfloatDouble().harmonicNumber(apfloatOf(value)).doubleValue();
  }

  public static double harmonicNumber(double value, double r) {
    return EvalEngine.getApfloatDouble().harmonicNumber(apfloatOf(value), apfloatOf(r))
        .doubleValue();
  }

  public static double hermiteH(double v, double x) {
    return EvalEngine.getApfloatDouble().hermiteH(apfloatOf(v), apfloatOf(x)).doubleValue();
  }

  public static double hypergeometric0F1Regularized(double a, double b) {
    return EvalEngine.getApfloatDouble()
        .hypergeometric0F1Regularized(apfloatOf(a), apfloatOf(b)).doubleValue();
  }

  public static double hypergeometric1F1Regularized(double a, double b, double c) {
    return EvalEngine.getApfloatDouble()
        .hypergeometric1F1Regularized(apfloatOf(a), apfloatOf(b), apfloatOf(c)).doubleValue();
  }

  public static double hypergeometric2F1(double a, double b, double c, double d) {
    return EvalEngine.getApfloatDouble()
        .hypergeometric2F1(apfloatOf(a), apfloatOf(b), apfloatOf(c), apfloatOf(d))
        .doubleValue();
  }

  public static double hypergeometric2F1Regularized(double a, double b, double c, double d) {
    return EvalEngine.getApfloatDouble().hypergeometric2F1Regularized(apfloatOf(a),
        apfloatOf(b), apfloatOf(c), apfloatOf(d)).doubleValue();
  }

  public static double inverseErf(double value) {
    return org.hipparchus.special.Erf.erfInv(value);
  }

  public static double inverseErfc(double value) {
    return org.hipparchus.special.Erf.erfcInv(value);
  }

  public static double jacobiP(double n, double a, double b, double x) {
    return EvalEngine.getApfloatDouble()
        .jacobiP(apfloatOf(n), apfloatOf(a), apfloatOf(b), apfloatOf(x)).doubleValue();
  }

  public static double laguerreL(double n, double x) {
    return EvalEngine.getApfloatDouble().laguerreL(apfloatOf(n), apfloatOf(x)).doubleValue();
  }

  public static double laguerreL(double n, double a, double x) {
    return EvalEngine.getApfloatDouble().laguerreL(apfloatOf(n), apfloatOf(a), apfloatOf(x))
        .doubleValue();
  }

  public static double legendreP(double v, double x) {
    return EvalEngine.getApfloatDouble().legendreP(apfloatOf(v), apfloatOf(x)).doubleValue();
  }

  public static double legendreP(double v, double m, double x) {
    return EvalEngine.getApfloatDouble().legendreP(apfloatOf(v), apfloatOf(m), apfloatOf(x))
        .doubleValue();
  }

  public static double legendreQ(double v, double x) {
    return EvalEngine.getApfloatDouble().legendreQ(apfloatOf(v), apfloatOf(x)).doubleValue();
  }

  public static double legendreQ(double v, double m, double x) {
    return EvalEngine.getApfloatDouble().legendreQ(apfloatOf(v), apfloatOf(m), apfloatOf(x))
        .doubleValue();
  }

  public static double logGamma(double value) {
    return org.hipparchus.special.Gamma.logGamma(value);
  }

  public static double logisticSigmoid(double value) {
    return EvalEngine.getApfloatDouble().logisticSigmoid(apfloatOf(value)).doubleValue();
  }

  public static double pochhammer(double x, double n) {
    return EvalEngine.getApfloatDouble().pochhammer(apfloatOf(x), apfloatOf(n)).doubleValue();
  }

  public static double polyGamma(double value) {
    return org.hipparchus.special.Gamma.digamma(value);
  }

  public static double polyGamma(long n, double x) {
    return EvalEngine.getApfloatDouble().polygamma(n, apfloatOf(x)).doubleValue();
  }

  public static double polyLog(double n, double x) {
    return EvalEngine.getApfloatDouble().polylog(apfloatOf(n), apfloatOf(x)).doubleValue();
  }

  private DMath() {}
}
