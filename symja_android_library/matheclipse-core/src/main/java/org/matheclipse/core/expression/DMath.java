package org.matheclipse.core.expression;

import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.matheclipse.external.apfloat.ApcomplexBesselMath;
import org.matheclipse.external.apfloat.ApcomplexBetaMath;
import java.util.function.BinaryOperator;
import org.matheclipse.parser.client.ParserConfig;
import org.matheclipse.core.eval.exception.SymjaMathException;
import org.apfloat.LossOfPrecisionException;
import org.matheclipse.core.eval.EvalEngine;
import org.hipparchus.complex.Complex;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.numerics.functions.CarlsonArbitraryPrecision;
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
   * <code>AiryAi(x)</code> as a machine double.
   *
   * <p>
   * For a positive <code>x</code> the function decays like <code>exp(-2/3*x^(3/2))</code> and is
   * <code>0.0</code> beyond the range of a double - it is already <code>2.6*10^-291</code> at
   * <code>x = 100</code>. For a negative <code>x</code> it oscillates with the amplitude
   * <code>|x|^(-1/4)/sqrt(Pi)</code> and the phase <code>2/3*|x|^(3/2)</code>, which
   * {@link #airyOscillating(double, boolean, boolean)} reduces with as many digits as the size of
   * the argument takes.
   */
  /**
   * From this argument on the asymptotic series of the Airy functions reach the precision of a
   * double: their smallest term is about <code>exp(-4/3*x^(3/2))</code>.
   */
  private static final double AIRY_ASYMPTOTIC = 12.0;

  /**
   * The asymptotic series of an Airy function of a large positive argument,
   * <code>Sum(s^k*u(k)/zeta^k)</code> or the one with the coefficients <code>v(k)</code> of the
   * derivatives, where <code>zeta = 2/3*x^(3/2)</code>.
   *
   * @param sign -1 for AiryAi and its derivative, 1 for AiryBi and its derivative
   * @see <a href="https://dlmf.nist.gov/9.7">DLMF 9.7</a>
   */
  private static double airySeries(double zeta, int sign, boolean derivative) {
    double sum = 1.0;
    double u = 1.0;
    double power = 1.0;
    double last = Double.MAX_VALUE;
    for (int k = 1; k < 200; k++) {
      // u(k) = u(k-1)*(6k-5)*(6k-3)*(6k-1)/(216*k*(2k-1))
      u *= (6.0 * k - 5.0) * (6.0 * k - 3.0) * (6.0 * k - 1.0) / (216.0 * k * (2.0 * k - 1.0));
      power *= sign / zeta;
      // v(k) = (6k+1)/(1-6k)*u(k)
      double term = power * (derivative ? u * (6.0 * k + 1.0) / (1.0 - 6.0 * k) : u);
      double size = Math.abs(term);
      if (size >= last) {
        break;
      }
      sum += term;
      last = size;
      if (size < 1.0e-17 * Math.abs(sum)) {
        break;
      }
    }
    return sum;
  }

  /**
   * The phase <code>2/3*x^(3/2)</code> of the Airy functions of <code>-x</code>, modulo
   * <code>2*Pi</code>. The digits in front of its decimal point are no part of the result, so the
   * power is calculated with as many more: a double has no digit of the phase left from
   * <code>x = 10^10</code> on.
   */
  private static double airyPhase(double x) {
    final long digits = 25 + (long) (0.5 * 0.30103 * Math.max(0, Math.getExponent(x)));
    final Apfloat exact = ApfloatNum.apfloatOf(x, digits);
    final long precision = exact.precision();
    final Apfloat zeta = exact.multiply(ApfloatMath.sqrt(exact)).multiply(new Apfloat(2, precision))
        .divide(new Apfloat(3, precision));
    final Apfloat twoPi = ApfloatMath.pi(precision).multiply(new Apfloat(2, precision));
    return ApfloatMath.fmod(zeta, twoPi).doubleValue();
  }

  /**
   * <code>Hypergeometric0F1(b, x)</code> for a large negative <code>x</code> as a machine double:
   * <code>Gamma(b)*(-x)^((1-b)/2)*BesselJ(b-1, 2*Sqrt(-x))</code> with the expansion of the Bessel
   * function for a large argument. The phase is the one of <code>2*Sqrt(-x)</code> with all its
   * digits, of which the square root as a double has none left from <code>x = -10^30</code> on.
   *
   * @return {@link Double#NaN} where the expansion doesn't apply or the value leaves the range of
   *         a double
   */
  public static double hypergeometric0F1OfLargeNegative(double b, double x) {
    if (!(x < -2500.0) || Double.isInfinite(x) || !Double.isFinite(b)
        || (b <= 0.0 && b == Math.rint(b))) {
      return Double.NaN;
    }
    final double z = -x;
    final Apfloat exact = ApfloatNum.apfloatOf(z, 25);
    final long precision = exact.precision();
    final Apfloat argument = ApfloatMath.sqrt(exact).multiply(new Apfloat(2, precision));
    final double phase = ApfloatMath
        .fmod(argument, ApfloatMath.pi(precision).multiply(new Apfloat(2, precision)))
        .doubleValue();
    final double root = Math.sqrt(z);
    final double[] bessel = BesselJY.hankel(b - 1.0, 2.0 * root, Math.sin(phase), Math.cos(phase));
    if (bessel == null) {
      return Double.NaN;
    }
    final double value = gamma(b) * Math.pow(root, 1.0 - b) * bessel[0];
    return Double.isFinite(value) ? value : Double.NaN;
  }

  /**
   * An Airy function or its derivative of the negative argument <code>-x</code> with
   * <code>x &gt;= 12</code>, from the asymptotic series: with <code>zeta = 2/3*x^(3/2)</code> and
   * <code>t = zeta - Pi/4</code>
   * <code>AiryAi(-x) = (Cos(t)*P + Sin(t)*Q)/(Sqrt(Pi)*x^(1/4))</code> and
   * <code>AiryBi(-x) = (-Sin(t)*P + Cos(t)*Q)/(Sqrt(Pi)*x^(1/4))</code>, where <code>P</code> has
   * the terms <code>u(k)/zeta^k</code> with an even <code>k</code>, <code>Q</code> those with an odd
   * one, and the sign changes every two terms. The error is below <code>10^-15</code> of the
   * amplitude.
   *
   * @param second <code>AiryBi</code> instead of <code>AiryAi</code>
   * @see <a href="https://dlmf.nist.gov/9.7#E9">DLMF 9.7.9 - 9.7.12</a>
   */
  private static double airyOscillating(double x, boolean second, boolean derivative) {
    final double zeta = 2.0 / 3.0 * x * Math.sqrt(x);
    double even = 1.0;
    double odd = 0.0;
    double u = 1.0;
    double power = 1.0;
    double last = Double.MAX_VALUE;
    for (int k = 1; k < 200; k++) {
      u *= (6.0 * k - 5.0) * (6.0 * k - 3.0) * (6.0 * k - 1.0) / (216.0 * k * (2.0 * k - 1.0));
      power /= zeta;
      final double term = power * (derivative ? u * (6.0 * k + 1.0) / (1.0 - 6.0 * k) : u);
      final double size = Math.abs(term);
      if (size >= last) {
        break;
      }
      final double signed = ((k >> 1) & 1) == 0 ? term : -term;
      if ((k & 1) == 0) {
        even += signed;
      } else {
        odd += signed;
      }
      last = size;
      if (size < 1.0e-17 * Math.max(Math.abs(even), Math.abs(odd))) {
        break;
      }
    }
    final double t = airyPhase(x) - Math.PI / 4.0;
    final double sin = Math.sin(t);
    final double cos = Math.cos(t);
    final double root = Math.sqrt(Math.sqrt(x));
    if (derivative) {
      return root / Math.sqrt(Math.PI) * (second ? cos * even + sin * odd : sin * even - cos * odd);
    }
    return (second ? cos * odd - sin * even : cos * even + sin * odd) / (Math.sqrt(Math.PI) * root);
  }

  public static double airyAi(double value) {
    if (value <= -AIRY_ASYMPTOTIC && value != Double.NEGATIVE_INFINITY) {
      return airyOscillating(-value, false, false);
    }
    if (value >= AIRY_ASYMPTOTIC) {
      // E^(-zeta)/(2*Sqrt(Pi)*x^(1/4)) times the series; apfloat needs minutes at x = 300
      double zeta = 2.0 / 3.0 * value * Math.sqrt(value);
      return Math.exp(-zeta) / (2.0 * Math.sqrt(Math.PI) * Math.sqrt(Math.sqrt(value)))
          * airySeries(zeta, -1, false);
    }
    try {
      return EvalEngine.getApfloatDouble().airyAi(apfloatOf(value)).doubleValue();
    } catch (LossOfPrecisionException lpe) {
      return 0.0;
    }
  }

  /**
   * <code>AiryAi'(x)</code> as a machine double, with the two ends of the range as
   * {@link #airyAi(double)}.
   */
  public static double airyAiPrime(double value) {
    if (value <= -AIRY_ASYMPTOTIC && value != Double.NEGATIVE_INFINITY) {
      return airyOscillating(-value, false, true);
    }
    if (value >= AIRY_ASYMPTOTIC) {
      double zeta = 2.0 / 3.0 * value * Math.sqrt(value);
      return -Math.sqrt(Math.sqrt(value)) * Math.exp(-zeta) / (2.0 * Math.sqrt(Math.PI))
          * airySeries(zeta, -1, true);
    }
    try {
      return EvalEngine.getApfloatDouble().airyAiPrime(apfloatOf(value)).doubleValue();
    } catch (LossOfPrecisionException lpe) {
      return 0.0;
    }
  }

  /**
   * <code>AiryBi(x)</code> as a machine double.
   *
   * <p>
   * For a positive <code>x</code> the function grows like <code>exp(2/3*x^(3/2))</code> and leaves
   * the range of a double at <code>x = 104</code>: an infinity is the answer, which the caller
   * turns into <code>Overflow()</code>. For a negative <code>x</code> it oscillates as
   * {@link #airyAi(double)} does.
   */
  public static double airyBi(double value) {
    if (value <= -AIRY_ASYMPTOTIC && value != Double.NEGATIVE_INFINITY) {
      return airyOscillating(-value, true, false);
    }
    if (value >= AIRY_ASYMPTOTIC && value < 104.0) {
      // below the argument where the result leaves the range of a double
      double zeta = 2.0 / 3.0 * value * Math.sqrt(value);
      return Math.exp(zeta) / (Math.sqrt(Math.PI) * Math.sqrt(Math.sqrt(value)))
          * airySeries(zeta, 1, false);
    }
    try {
      return EvalEngine.getApfloatDouble().airyBi(apfloatOf(value)).doubleValue();
    } catch (LossOfPrecisionException lpe) {
      return value > 0.0 ? Double.POSITIVE_INFINITY : 0.0;
    }
  }

  /**
   * <code>AiryBi'(x)</code> as a machine double, with the two ends of the range as
   * {@link #airyBi(double)}.
   */
  public static double airyBiPrime(double value) {
    if (value <= -AIRY_ASYMPTOTIC && value != Double.NEGATIVE_INFINITY) {
      return airyOscillating(-value, true, true);
    }
    if (value >= AIRY_ASYMPTOTIC && value < 104.0) {
      double zeta = 2.0 / 3.0 * value * Math.sqrt(value);
      return Math.sqrt(Math.sqrt(value)) * Math.exp(zeta) / Math.sqrt(Math.PI)
          * airySeries(zeta, 1, true);
    }
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
    if (x > 0.0) {
      return besselGuarded(ApcomplexBesselMath::besselI, v, x);
    }
    return EvalEngine.getApfloatDouble().besselI(apfloatOf(v), apfloatOf(x)).doubleValue();
  }

  /**
   * A Bessel function of a positive machine number by the arbitrary precision routine. The two
   * doubles are taken as the binary numbers they are, and the routine adds the digits which the
   * function cancels, or which the size of the argument takes from the phase:
   * {@link EvalEngine#getApfloatDouble()} alone gave 5 digits of <code>BesselY(0, 1.0*^12)</code>
   * and no number for <code>BesselJ(0, 1.0*^10)</code>.
   *
   * @throws ArithmeticException if the value is not real
   */
  private static double besselGuarded(BinaryOperator<Apcomplex> function, double v, double x) {
    final int digits = (int) ParserConfig.MACHINE_PRECISION + 1;
    Apcomplex value =
        function.apply(ApfloatNum.apfloatOf(v, digits), ApfloatNum.apfloatOf(x, digits));
    if (value.imag().signum() != 0) {
      throw new ArithmeticException("Bessel function with a complex value");
    }
    return value.real().doubleValue();
  }

  /**
   * A Bessel function with a complex value by the arbitrary precision routine, as
   * {@link #besselGuarded(BinaryOperator, double, double)}: the series of the library at the
   * precision of a double gave <code>-97+23*I</code> for <code>BesselI(100.5, 60.0+20.0*I)</code>,
   * which is <code>10^-5</code>.
   *
   * @param function the routine which adds the digits the function cancels
   * @param library the function of the library, for the argument zero
   */
  public static Apcomplex besselComplex(BinaryOperator<Apcomplex> function,
      BinaryOperator<Apcomplex> library, INumber nu, INumber z) {
    final int digits = (int) ParserConfig.MACHINE_PRECISION + 1;
    final Apcomplex order = CarlsonArbitraryPrecision.apcomplex(nu, digits);
    final Apcomplex argument = CarlsonArbitraryPrecision.apcomplex(z, digits);
    if (argument.real().signum() == 0 && argument.imag().signum() == 0) {
      return library.apply(nu.apcomplexValue(), z.apcomplexValue());
    }
    return function.apply(order, argument);
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
    if (x > 0.0) {
      // beyond the validated domain: the expansion for a large argument, which keeps the phase
      double[] hankel = BesselJY.hankel(v, x);
      return hankel != null ? hankel[0] : besselGuarded(ApcomplexBesselMath::besselJ, v, x);
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
    if (x > 0.0) {
      return besselGuarded(ApcomplexBesselMath::besselK, v, x);
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
    if (x > 0.0) {
      double[] hankel = BesselJY.hankel(v, x);
      return hankel != null ? hankel[1] : besselGuarded(ApcomplexBesselMath::besselY, v, x);
    }
    return EvalEngine.getApfloatDouble().besselY(apfloatOf(v), apfloatOf(x)).doubleValue();
  }

  /**
   * <code>StruveH(v, x)</code> as a machine double for an argument beyond 100, where
   * {@link #besselY(double, double)} keeps the phase of a large argument:
   * <code>BesselY(v, x)</code> plus the expansion of the difference of the two functions,
   * <code>1/Pi*Sum(Gamma(k+1/2)/Gamma(v+1/2-k)*(x/2)^(v-2*k-1))</code>.
   *
   * @return {@link Double#NaN} where the expansion doesn't apply or doesn't reach the precision of
   *         a double, an infinity where the value leaves the range of one
   * @see <a href="https://dlmf.nist.gov/11.6#E1">DLMF 11.6.1</a>
   */
  public static double struveHLargeArgument(double v, double x) {
    if (!(x > 100.0) || Double.isInfinite(x) || !(4.0 * x > 5.0 * Math.abs(v))) {
      return Double.NaN;
    }
    final double half = x / 2.0;
    final double order = v + 0.5;
    double sum = 0.0;
    double smallest = 0.0;
    // every term vanishes at a pole of Gamma(v+1/2): StruveH is BesselY there
    if (!(order <= 0.0 && order == Math.rint(order))) {
      final double gamma = gamma(order);
      // k = 0: (x/2)^(v-1)/(Sqrt(Pi)*Gamma(v+1/2))
      double term = Math.pow(half, v - 1.0) / (Math.sqrt(Math.PI) * gamma);
      if (Double.isNaN(term) || Double.isInfinite(gamma) || gamma == 0.0) {
        return Double.NaN;
      }
      if (Double.isInfinite(term)) {
        return term;
      }
      for (int k = 0; k < 200; k++) {
        sum += term;
        smallest = Math.abs(term);
        // term(k+1)/term(k) = (k+1/2)*(v-1/2-k)/(x/2)^2
        final double ratio = (k + 0.5) * (v - 0.5 - k) / half / half;
        if (!(Math.abs(ratio) < 1.0)) {
          // an asymptotic series says nothing behind its smallest term
          break;
        }
        term *= ratio;
        if (Math.abs(term) <= 1.0e-17 * Math.abs(sum)) {
          smallest = Math.abs(term);
          break;
        }
      }
    }
    final double bessel = besselY(v, x);
    final double value = bessel + sum;
    if (!(smallest <= Math.ulp(1.0) * Math.max(Math.abs(value), Math.abs(bessel)))) {
      return Double.NaN;
    }
    return value;
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
    if (a > 0.0 && b > 0.0 && Math.max(a, b) >= 20.0 && Double.isFinite(a) && Double.isFinite(b)) {
      return a <= b ? betaOfLarge(a, b) : betaOfLarge(b, a);
    }
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

  /** The coefficients <code>B(2k)/(2k*(2k-1))</code> of the Stirling series of the logarithm. */
  private static final double[] STIRLING = {1.0 / 12.0, -1.0 / 360.0, 1.0 / 1260.0, -1.0 / 1680.0,
      1.0 / 1188.0, -691.0 / 360360.0, 1.0 / 156.0, -3617.0 / 122400.0};

  /**
   * The Stirling series <code>LogGamma(z) - (z-1/2)*Log(z) + z - Log(2*Pi)/2</code> for an argument
   * of at least 20, where its 8 terms reach the precision of a double.
   */
  private static double stirling(double z) {
    final double w = 1.0 / z / z;
    double sum = 0.0;
    for (int i = STIRLING.length - 1; i >= 0; i--) {
      sum = sum * w + STIRLING[i];
    }
    return sum / z;
  }

  /**
   * <code>Beta(a, b)</code> for <code>0 &lt; a &lt;= b</code> and <code>b &gt;= 20</code>:
   * <code>Gamma(a)*Gamma(b)/Gamma(a+b)</code> with the Stirling series of the two gamma functions
   * of a large argument, whose logarithms are subtracted term by term.
   *
   * <p>
   * The difference of the two logarithms themselves has the error of one of them, which is the
   * precision times <code>b*Log(b)</code>: it left 9 digits of <code>Beta(0.5, 1.0*^6)</code>, and
   * none beyond <code>b = 10^16</code>.
   */
  private static double betaOfLarge(double a, double b) {
    final double c = a + b;
    // Log(Gamma(b)/Gamma(a+b)) + a*Log(a+b)
    final double rest = -(b - 0.5) * Math.log1p(a / b) + a + stirling(b) - stirling(c);
    if (a <= 170.0) {
      final double power = Math.pow(c, -a);
      final double gamma = gamma(a) * Math.exp(rest);
      if (power > 1.0e-290 && gamma < 1.0e300) {
        return gamma * power;
      }
    }
    // next to the ends of the range of a double: the logarithm, with the error of its size
    return Math.exp(org.hipparchus.special.Gamma.logGamma(a) - a * Math.log(c) + rest);
  }

  /**
   * <code>{Sin(Pi*x), Cos(Pi*x)}</code> from the argument modulo 2, which is exact for a double of
   * any size, with the exact values at the multiples of 1/2.
   */
  private static double[] sinCosPi(double x) {
    final double r = x % 2.0;
    final double n = Math.rint(2.0 * r);
    final double t = Math.PI * (r - n / 2.0);
    final double sin = Math.sin(t);
    final double cos = Math.cos(t);
    switch (((int) n) & 3) {
      case 0:
        return new double[] {sin, cos};
      case 1:
        return new double[] {cos, -sin};
      case 2:
        return new double[] {-sin, -cos};
      default:
        return new double[] {-cos, sin};
    }
  }

  /**
   * <code>Binomial(n, k)</code> of machine numbers where the quotient
   * <code>Gamma(n+1)/(Gamma(k+1)*Gamma(n-k+1))</code> has a gamma function beyond the range of a
   * double, or one of a negative argument of a large size: <code>Binomial(1.0*^20, 3.0)</code> and
   * <code>Binomial(-0.5, 1.0*^20)</code> were <code>0.0</code>.
   *
   * @return {@link Double#NaN} if the quotient is left to the gamma functions: small arguments, a
   *         value beyond the range of a double, or a case this method has no formula for
   */
  public static double binomial(double n, double k) {
    if (!Double.isFinite(n) || !Double.isFinite(k) || Math.max(Math.abs(n), Math.abs(k)) < 20.0) {
      return Double.NaN;
    }
    final double j = n - k;
    if (k <= -1.0 && k != Math.rint(k) && j > -1.0) {
      // Binomial(n, n-k): the formulas below have none for a lower index below -1, and the
      // gamma functions of the quotient leave the range - Binomial(0.5, -700.5)
      return binomial(n, j);
    }
    // whether n-k is an integer, which the difference of a small and a large double doesn't show
    final boolean integerDifference = n - Math.floor(n) == k - Math.floor(k);
    double value = Double.NaN;
    if (k == Math.rint(k) && k >= 0.0 && k <= 64.0) {
      // (n-k+1)*(n-k+2)*...*n/k!
      value = 1.0;
      for (int i = 1; i <= k; i++) {
        value = value * (j + i) / i;
      }
    } else if (integerDifference && j >= 0.0 && j <= 64.0) {
      // Binomial(n, n-k)
      value = 1.0;
      for (int i = 1; i <= j; i++) {
        value = value * (k + i) / i;
      }
    } else if (n > -1.0 && k > -1.0) {
      if (j > -1.0) {
        // the three gamma functions have positive arguments
        value = 1.0 / ((n + 1.0) * beta(k + 1.0, j + 1.0));
      } else if (!integerDifference) {
        // 1/Gamma(n-k+1) is Gamma(k-n)*Sin(Pi*(k-n))/Pi
        final double[] ofK = sinCosPi(k);
        final double[] ofN = sinCosPi(n);
        value = (ofK[0] * ofN[1] - ofK[1] * ofN[0]) / Math.PI * beta(n + 1.0, k - n);
      } else {
        // a pole of Gamma(n-k+1)
        value = 0.0;
      }
    } else if (n < 0.0 && n != Math.rint(n) && k > -1.0) {
      // Gamma(n+1) and 1/Gamma(n-k+1) by the reflection formula:
      // Sin(Pi*(k-n))/Sin(Pi*(n+1))/((k-n)*Beta(-n, k+1))
      final double[] ofK = sinCosPi(k);
      final double[] ofN = sinCosPi(n);
      value = (ofK[0] * ofN[1] - ofK[1] * ofN[0]) / -ofN[0] / ((k - n) * beta(-n, k + 1.0));
    }
    return Double.isFinite(value) ? value : Double.NaN;
  }

  /**
   * Whether <code>Gamma(a+n)</code> has a pole and <code>Gamma(a)</code> has none, where
   * <code>Pochhammer(a, n)</code> is infinite. The sum of the two fractional parts tells whether
   * <code>a+n</code> is an integer, which the sum of a small and a large double doesn't.
   */
  public static boolean isPochhammerPole(double a, double n) {
    if (!Double.isFinite(a) || !Double.isFinite(n) || !(a + n <= 0.0)) {
      return false;
    }
    final double fractions = (a - Math.floor(a)) + (n - Math.floor(n));
    return (fractions == 0.0 || fractions == 1.0) && !(a <= 0.0 && a == Math.rint(a));
  }

  /**
   * <code>Pochhammer(a, n)</code> of machine numbers where the quotient
   * <code>Gamma(a+n)/Gamma(a)</code> has a gamma function beyond the range of a double, or the
   * two cancel: <code>Pochhammer(1.0*^20, 3.0)</code> was <code>0.0</code>, and
   * <code>Pochhammer(1.0*^6, 0.001)</code> had 9 digits.
   *
   * @return {@link Double#NaN} if the quotient is left to the gamma functions: small arguments, a
   *         pole, a value beyond the range of a double, or a case this method has no formula for
   */
  public static double pochhammerOfLarge(double a, double n) {
    if (!Double.isFinite(a) || !Double.isFinite(n) || n == 0.0
        || Math.max(Math.abs(a), Math.abs(n)) < 20.0) {
      return Double.NaN;
    }
    // whether a+n is an integer, which the sum of a small and a large double doesn't show
    final double fractions = (a - Math.floor(a)) + (n - Math.floor(n));
    final boolean integerSum = fractions == 0.0 || fractions == 1.0;
    final boolean integerA = a == Math.rint(a);
    final double sum = a + n;
    double value = Double.NaN;
    if (n == Math.rint(n) && Math.abs(n) <= 64.0) {
      // a*(a+1)*...*(a+n-1), or 1/((a-1)*(a-2)*...*(a+n))
      value = 1.0;
      if (n > 0.0) {
        for (int i = 0; i < n; i++) {
          value *= a + i;
        }
      } else {
        for (int i = 1; i <= -n; i++) {
          value /= a - i;
        }
      }
    } else if (a > 0.0 && sum > 0.0 && Math.abs(n) <= 170.0) {
      // Gamma(n)/Beta(a, n), or Beta(a+n, -n)/Gamma(-n)
      value = n > 0.0 ? gamma(n) / beta(a, n) : beta(sum, -n) / gamma(-n);
    } else if (integerSum && sum <= 0.0) {
      // a pole of Gamma(a+n)
      return Double.NaN;
    } else if (a > 0.0 && sum < 0.0) {
      // Gamma(a+n) = Pi/(Sin(Pi*(a+n))*Gamma(1-a-n)): the quotient is below the range of a double
      // with a large 1-a-n
      if (-n > 170.0) {
        value = 0.0;
      } else {
        final double[] ofA = sinCosPi(a);
        final double[] ofN = sinCosPi(n);
        final double sin = ofA[0] * ofN[1] + ofA[1] * ofN[0];
        value = Math.PI / (sin * beta(a, 1.0 - sum) * gamma(1.0 - n));
      }
    } else if (a < 0.0 && !integerA && sum < 0.0 && Math.abs(n) <= 170.0) {
      // Sin(Pi*a)/Sin(Pi*(a+n))*Gamma(1-a)/Gamma(1-a-n)
      final double[] ofA = sinCosPi(a);
      final double[] ofN = sinCosPi(n);
      final double sin = ofA[0] * ofN[1] + ofA[1] * ofN[0];
      final double upper = 1.0 - sum;
      final double quotient = n > 0.0 ? gamma(n) / beta(upper, n) : beta(1.0 - a, -n) / gamma(-n);
      value = ofA[0] / sin * quotient;
    } else if (a < 0.0 && !integerA && sum > 0.0 && n <= 170.0) {
      // Gamma(a) = Pi/(Sin(Pi*a)*Gamma(1-a))
      value = sinCosPi(a)[0] / Math.PI * beta(sum, 1.0 - a) * gamma(n + 1.0);
    }
    return Double.isFinite(value) ? value : Double.NaN;
  }

  public static double beta(double x, double a, double b) {
    // with guard digits: at machine precision the series cancels for large parameters
    long precision = EvalEngine.getApfloatDouble().precision();
    Apcomplex beta = ApcomplexBetaMath.beta(apfloatOf(x).precision(precision),
        apfloatOf(a).precision(precision), apfloatOf(b).precision(precision));
    if (beta.imag().signum() != 0) {
      throw new ArithmeticException("Result would be complex");
    }
    return beta.real().doubleValue();
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
    if (Math.abs(value) < 1.0e-10) {
      // 2/Sqrt(Pi)*x: the square of the argument, which the incomplete gamma function takes,
      // is below the range of a double from 10^-162 on, and the result was 0
      return 1.1283791670955126 * value;
    }
    return org.hipparchus.special.Erf.erf(value);
  }

  public static double erfc(double value) {
    return org.hipparchus.special.Erf.erfc(value);
  }

  public static double erfi(double value) {
    if (Math.abs(value) < 1.0e-10) {
      // 2/Sqrt(Pi)*x
      return 1.1283791670955126 * value;
    }
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

  /** <code>Zeta(2), ..., Zeta(11)</code>: the coefficients of the series of the harmonic number at 0. */
  private static final double[] ZETA = {1.6449340668482264, 1.2020569031595943, 1.0823232337111382,
      1.0369277551433699, 1.0173430619844491, 1.0083492773819228, 1.0040773561979443,
      1.0020083928260822, 1.0009945751278181, 1.0004941886041195};

  /**
   * <code>HarmonicNumber(x)</code> as a machine double. Next to 0 it is the series
   * <code>Zeta(2)*x - Zeta(3)*x^2 + Zeta(4)*x^3 - ...</code>: the digamma function of
   * <code>1+x</code> has the digits of <code>x</code> which the sum with 1 leaves, and none below
   * <code>10^-16</code>.
   */
  public static double harmonicNumber(double value) {
    if (Math.abs(value) <= 0.01) {
      double sum = 0.0;
      for (int i = ZETA.length - 1; i >= 0; i--) {
        sum = ZETA[i] - value * sum;
      }
      return value * sum;
    }
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
