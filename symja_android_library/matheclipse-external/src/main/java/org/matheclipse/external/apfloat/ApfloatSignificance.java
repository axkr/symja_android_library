package org.matheclipse.external.apfloat;

import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;

/**
 * Significance arithmetic: how the error of an arbitrary-precision number carries through a
 * calculation.
 *
 * <p>
 * A number <code>x</code> of decimal precision <code>p</code> has the relative error
 * <code>10^-p</code> and the absolute error <code>|x|*10^-p</code>; its accuracy is
 * <code>p - Log10(|x|)</code>, the number of correct digits behind the decimal point. An exact
 * number has precision and accuracy <code>Infinity</code>.
 *
 * <ul>
 * <li>In a sum the absolute errors add: the accuracy of <code>x1+x2+...</code> is
 * <code>-Log10(10^-a1 + 10^-a2 + ...)</code>.
 * <li>In a product the relative errors add: the precision of <code>x1*x2*...</code> is
 * <code>-Log10(10^-p1 + 10^-p2 + ...)</code>.
 * <li>A function <code>f(x1, x2, ...)</code> multiplies the relative error of each argument by its
 * condition number <code>|xi*D(f,xi)/f|</code>.
 * </ul>
 *
 * The sums are formed with the exponents relative to the smallest one, so that very large
 * precisions or accuracies do not underflow.
 */
public final class ApfloatSignificance {

  private ApfloatSignificance() {}

  /**
   * <code>Log10(Abs(x))</code> of a nonzero number, also beyond the double range.
   *
   * @param x a number which is not zero
   * @return the decimal logarithm of the absolute value
   */
  public static double log10Abs(Apfloat x) {
    final long scale = x.scale();
    // the mantissa in [0.1, 1)
    final double mantissa = Math.abs(ApfloatMath.scale(x, -scale).doubleValue());
    return scale + Math.log10(mantissa);
  }

  /**
   * <code>-Log10(Sum(c[i]*10^-e[i]))</code>: the precision (or accuracy) of a result whose relative
   * (or absolute) errors are <code>c[i]*10^-e[i]</code>. An exponent <code>Infinity</code> or a
   * factor <code>0</code> is an error-free contribution.
   *
   * @param exponents the precisions or accuracies of the contributions
   * @param factors the factors of the contributions, or <code>null</code> for factors
   *        <code>1</code>
   * @return <code>Infinity</code> if no contribution has an error
   */
  public static double combine(double[] exponents, double[] factors) {
    double smallest = Double.POSITIVE_INFINITY;
    for (int i = 0; i < exponents.length; i++) {
      double e = exponents[i] - log10Factor(factors, i);
      if (e < smallest) {
        smallest = e;
      }
    }
    if (Double.isInfinite(smallest) || Double.isNaN(smallest)) {
      return smallest;
    }
    double sum = 0.0;
    for (int i = 0; i < exponents.length; i++) {
      double e = exponents[i] - log10Factor(factors, i);
      if (!Double.isInfinite(e) && !Double.isNaN(e)) {
        sum += Math.pow(10.0, smallest - e);
      }
    }
    return smallest - Math.log10(sum);
  }

  private static double log10Factor(double[] factors, int i) {
    if (factors == null) {
      return 0.0;
    }
    final double factor = Math.abs(factors[i]);
    return factor == 0.0 ? Double.NEGATIVE_INFINITY : Math.log10(factor);
  }

  /**
   * The accuracy of a sum from the accuracies of its terms.
   *
   * @param accuracies the accuracies of the terms, <code>Infinity</code> for an exact term
   */
  public static double sumAccuracy(double... accuracies) {
    return combine(accuracies, null);
  }

  /**
   * The precision of a product or quotient from the precisions of its factors.
   *
   * @param precisions the precisions of the factors, <code>Infinity</code> for an exact factor
   */
  public static double productPrecision(double... precisions) {
    return combine(precisions, null);
  }

  /**
   * The precision of a function value from the precisions of its arguments and the condition
   * numbers of the function in them.
   *
   * @param precisions the precisions of the arguments, <code>Infinity</code> for an exact argument
   * @param conditionNumbers <code>|xi*D(f,xi)/f|</code> for each argument
   */
  public static double functionPrecision(double[] precisions, double[] conditionNumbers) {
    return combine(precisions, conditionNumbers);
  }

  /** The functions whose condition numbers are known here. */
  public enum Function {
    /** <code>b^e</code> with the arguments <code>{b, e}</code> */
    POWER,
    /** <code>Exp(x)</code> */
    EXP,
    /** <code>Log(x)</code> */
    LOG,
    /** <code>Log(b, x)</code> with the arguments <code>{b, x}</code> */
    LOG_BASE,
    SIN, COS, TAN, COT, SEC, CSC, ARCSIN, ARCCOS, ARCTAN, ARCCOT, ARCSEC, ARCCSC, //
    SINH, COSH, TANH, COTH, SECH, CSCH, //
    ARCSINH, ARCCOSH, ARCTANH, ARCCOTH, ARCSECH, ARCCSCH
  }

  /**
   * Test if the function has poles: where not one digit of its value is known, the value is
   * <code>ComplexInfinity</code>, as <code>Tan(1.5707963`5)</code>.
   */
  public static boolean hasPoles(Function function) {
    switch (function) {
      case TAN:
      case COT:
      case SEC:
      case CSC:
      case COTH:
      case CSCH:
        return true;
      default:
        return false;
    }
  }

  /**
   * The bound of the absolute value of the function, which is also a bound of its error: the
   * accuracy of <code>Sin(1.`20*^30)</code> is <code>0</code>, not negative.
   *
   * @return <code>NaN</code> if the function is not bounded
   */
  public static double bound(Function function) {
    switch (function) {
      case SIN:
      case COS:
      case TANH:
      case SECH:
        return 1.0;
      default:
        return Double.NaN;
    }
  }

  /** The largest condition number passed on; beyond it no digit of the result is right anyway. */
  private static final double MAX_CONDITION = 1.0e300;

  /**
   * The condition numbers <code>|xi*D(f,xi)/f|</code> of a function at real arguments, from the
   * arguments and the value. The derivatives are written with the value where that is possible, so
   * that the digits a value near zero has are those of the value already calculated.
   *
   * @param function the function
   * @param args the arguments
   * @param value the value of the function, not zero
   * @return <code>null</code> if a condition number is not a finite number
   */
  public static double[] conditionNumbers(Function function, Apfloat[] args, Apfloat value) {
    if (value.signum() == 0) {
      return null;
    }
    final double x = args[0].doubleValue();
    switch (function) {
      // the functions whose condition numbers don't need the value: they are known beyond the
      // range of a double value too, as for Sinh(1000.`2)
      case POWER:
      case EXP:
      case SINH:
      case COSH:
      case SECH:
      case CSCH:
        return conditionNumbers(function, args, x, Double.NaN);
      case TANH:
      case COTH:
        if (Math.abs(x) >= 1.0) {
          return conditionNumbers(function, args, x, Double.NaN);
        }
        break;
      default:
        break;
    }
    final double f = value.doubleValue();
    if (!Double.isFinite(f) || f == 0.0) {
      return null;
    }
    return conditionNumbers(function, args, x, f);
  }

  private static double[] conditionNumbers(Function function, Apfloat[] args, double x,
      double f) {
    double[] c;
    switch (function) {
      case POWER: {
        final double e = args[1].doubleValue();
        if (!Double.isFinite(e) || args[0].signum() == 0) {
          return null;
        }
        c = new double[] {Math.abs(e), Math.abs(e * lnAbs(args[0]))};
        break;
      }
      case EXP:
        c = new double[] {Math.abs(x)};
        break;
      case LOG:
        c = new double[] {1.0 / Math.abs(f)};
        break;
      case LOG_BASE: {
        // Log(b, x) = Log(x)/Log(b): the relative errors are divided by the logarithms
        if (args[0].signum() == 0 || args[1].signum() == 0) {
          return null;
        }
        c = new double[] {1.0 / Math.abs(lnAbs(args[0])), 1.0 / Math.abs(lnAbs(args[1]))};
        break;
      }
      case SIN:
      case COS:
        // |x*Cos(x)/Sin(x)| and |x*Sin(x)/Cos(x)|: the other function from the value
        c = new double[] {Math.abs(x) * Math.sqrt(Math.max(0.0, 1.0 - f * f)) / Math.abs(f)};
        break;
      case TAN:
      case COT:
        // |x/(Sin(x)*Cos(x))| = |x*(1+Tan(x)^2)/Tan(x)|
        c = new double[] {Math.abs(x) * (1.0 + f * f) / Math.abs(f)};
        break;
      case SEC:
      case CSC:
        // |x*Tan(x)| = |x|*Sqrt(Sec(x)^2-1), and |x*Cot(x)| from Csc(x) alike
        c = new double[] {Math.abs(x) * Math.sqrt(Math.max(0.0, f * f - 1.0))};
        break;
      case ARCSIN:
      case ARCCOS:
        c = new double[] {Math.abs(x / (Math.sqrt(Math.max(0.0, 1.0 - x * x)) * f))};
        break;
      case ARCTAN:
      case ARCCOT:
        c = new double[] {Math.abs(x / ((1.0 + x * x) * f))};
        break;
      case ARCSEC:
      case ARCCSC:
        c = new double[] {Math.abs(1.0 / (Math.sqrt(Math.max(0.0, x * x - 1.0)) * f))};
        break;
      case SINH:
      case CSCH:
        // |x*Cosh(x)/Sinh(x)| = |x*Coth(x)|, the same for 1/Sinh(x)
        c = new double[] {Math.abs(x / Math.tanh(x))};
        break;
      case COSH:
      case SECH:
        c = new double[] {Math.abs(x * Math.tanh(x))};
        break;
      case TANH:
      case COTH:
        if (Math.abs(x) < 1.0) {
          // as the quotient Sinh(x)/Cosh(x), the errors of both added
          c = new double[] {Math.abs(x * (f + 1.0 / f))};
        } else {
          // x*(1-Tanh(x)^2)/Tanh(x) without the cancellation
          c = new double[] {Math.abs(2.0 * x / Math.sinh(2.0 * x))};
        }
        break;
      case ARCSINH:
        c = new double[] {Math.abs(x / (Math.sqrt(1.0 + x * x) * f))};
        break;
      case ARCCOSH:
        c = new double[] {Math.abs(x / (Math.sqrt(Math.max(0.0, x * x - 1.0)) * f))};
        break;
      case ARCTANH:
      case ARCCOTH:
        c = new double[] {Math.abs(x / ((1.0 - x * x) * f))};
        break;
      case ARCSECH:
        c = new double[] {Math.abs(1.0 / (Math.sqrt(Math.max(0.0, 1.0 - x * x)) * f))};
        break;
      case ARCCSCH:
        c = new double[] {Math.abs(1.0 / (Math.sqrt(1.0 + x * x) * f))};
        break;
      default:
        return null;
    }
    for (int i = 0; i < c.length; i++) {
      if (Double.isNaN(c[i])) {
        return null;
      }
      c[i] = Math.min(c[i], MAX_CONDITION);
    }
    return c;
  }

  /** <code>Log(Abs(x))</code> of a nonzero number, also beyond the double range. */
  private static double lnAbs(Apfloat x) {
    return log10Abs(x) * Math.log(10.0);
  }

  /**
   * The value of a function at real arguments, calculated with <code>precision</code> digits: for
   * a result which has more digits than its arguments, like the square root.
   *
   * @return <code>null</code> if it cannot be calculated here
   */
  public static Apfloat evaluate(Function function, Apfloat[] args, long precision) {
    try {
      final Apfloat x = withPrecision(args[0], precision);
      switch (function) {
        case POWER: {
          final Apfloat e = withPrecision(args[1], precision);
          if (e.isInteger() && e.scale() <= 18) {
            return ApfloatMath.pow(x, e.longValue());
          }
          return ApfloatMath.pow(x, e);
        }
        case EXP:
          return ApfloatMath.exp(x);
        case LOG:
          return ApfloatMath.log(x);
        case LOG_BASE:
          return ApfloatMath.log(withPrecision(args[1], precision), x);
        case SIN:
          return ApfloatMath.sin(x);
        case COS:
          return ApfloatMath.cos(x);
        case TAN:
          return ApfloatMath.tan(x);
        case COT:
          return ApfloatMath.inverseRoot(ApfloatMath.tan(x), 1);
        case SEC:
          return ApfloatMath.inverseRoot(ApfloatMath.cos(x), 1);
        case CSC:
          return ApfloatMath.inverseRoot(ApfloatMath.sin(x), 1);
        case ARCSIN:
          return ApfloatMath.asin(x);
        case ARCCOS:
          return ApfloatMath.acos(x);
        case ARCTAN:
          return ApfloatMath.atan(x);
        case ARCCOT:
          return ApfloatMath.atan(ApfloatMath.inverseRoot(x, 1));
        case ARCSEC:
          return ApfloatMath.acos(ApfloatMath.inverseRoot(x, 1));
        case ARCCSC:
          return ApfloatMath.asin(ApfloatMath.inverseRoot(x, 1));
        case SINH:
          return ApfloatMath.sinh(x);
        case COSH:
          return ApfloatMath.cosh(x);
        case TANH:
          return ApfloatMath.tanh(x);
        case COTH:
          return ApfloatMath.inverseRoot(ApfloatMath.tanh(x), 1);
        case SECH:
          return ApfloatMath.inverseRoot(ApfloatMath.cosh(x), 1);
        case CSCH:
          return ApfloatMath.inverseRoot(ApfloatMath.sinh(x), 1);
        case ARCSINH:
          return ApfloatMath.asinh(x);
        case ARCCOSH:
          return ApfloatMath.acosh(x);
        case ARCTANH:
          return ApfloatMath.atanh(x);
        case ARCCOTH:
          return ApfloatMath.atanh(ApfloatMath.inverseRoot(x, 1));
        case ARCSECH:
          return ApfloatMath.acosh(ApfloatMath.inverseRoot(x, 1));
        case ARCCSCH:
          return ApfloatMath.asinh(ApfloatMath.inverseRoot(x, 1));
        default:
          return null;
      }
    } catch (ArithmeticException | IllegalArgumentException | ApfloatRuntimeException e) {
      // also a complete loss of the digits of a large argument
      return null;
    }
  }

  /**
   * The value of a function at real arguments, which may be complex, as <code>ArcSin(2)</code> or
   * <code>Log(-1)</code>, calculated with <code>precision</code> digits.
   *
   * @return <code>null</code> if it cannot be calculated here
   */
  public static Apcomplex evaluateComplex(Function function, Apfloat[] args, long precision) {
    try {
      final Apfloat x = withPrecision(args[0], precision);
      switch (function) {
        case POWER:
          return ApcomplexMath.pow(x, withPrecision(args[1], precision));
        case LOG:
          return ApcomplexMath.log(x);
        case LOG_BASE:
          return ApcomplexMath.log(withPrecision(args[1], precision))
              .divide(ApcomplexMath.log(x));
        case ARCSIN:
          return ApcomplexMath.asin(x);
        case ARCCOS:
          return ApcomplexMath.acos(x);
        case ARCSEC:
          return ApcomplexMath.acos(ApfloatMath.inverseRoot(x, 1));
        case ARCCSC:
          return ApcomplexMath.asin(ApfloatMath.inverseRoot(x, 1));
        case ARCCOSH:
          return ApcomplexMath.acosh(x);
        case ARCTANH:
          return ApcomplexMath.atanh(x);
        case ARCCOTH:
          return ApcomplexMath.atanh(ApfloatMath.inverseRoot(x, 1));
        case ARCSECH:
          return ApcomplexMath.acosh(ApfloatMath.inverseRoot(x, 1));
        default:
          // real for every real argument
          return evaluate(function, args, precision);
      }
    } catch (ArithmeticException | IllegalArgumentException | ApfloatRuntimeException e) {
      return null;
    }
  }

  /**
   * A function of one real argument, calculated with a given number of digits, whose value may be
   * complex.
   */
  @FunctionalInterface
  public interface ArgumentFunction {
    /**
     * @param x the argument
     * @param precision the number of digits of the calculation
     * @return <code>null</code> if the function has no value at <code>x</code>
     */
    Apcomplex value(Apfloat x, long precision);
  }

  /** The steps of {@link #changeWithin(ArgumentFunction, Apfloat, Apfloat, double)}: 2^-40. */
  private static final long SHRINK_BITS = 40L;

  /**
   * The value of a function of one real argument <code>x</code>, and how much the function changes
   * within <code>delta</code> of <code>x</code>: the error of the value where the condition number
   * doesn't exist. For a zero argument, which has no relative error, <code>Sin(0``20)</code> is
   * <code>0``20</code> and <code>Cos(0``20)</code> is <code>1</code> with the precision
   * <code>40.301</code>, since the cosine changes by <code>(10^-20)^2/2</code> only. At an end of
   * the domain, <code>ArcCos(1.`20)</code> is <code>0``9.849</code>, since the arc cosine changes by
   * <code>Sqrt(2*10^-20)</code>.
   *
   * <p>
   * The change is the leading term <code>c*delta^p</code> of the function at <code>x</code>, also
   * where <code>delta</code> is not small: <code>Log(1.`3)</code> is <code>0``3</code>, not the
   * <code>0``3.0002</code> of <code>Log(1.001)</code>. The power <code>p</code> is read off the
   * changes within two much smaller steps.
   *
   * <p>
   * On each side the smaller change is taken: where the function is continuous they agree, and a
   * branch cut jumps on one side only. A side where the function has no real value, as above
   * <code>1</code> for the arc cosine, is left out, unless the value at <code>x</code> is complex
   * too.
   *
   * @param function a function of one argument
   * @param x the argument
   * @param delta the absolute error of the argument, not zero
   * @param digits the accuracy of the argument, which decides the working precision
   * @return <code>{value, change}</code>, or <code>null</code> if the function has no real value at
   *         <code>x</code> or on either side of it
   */
  public static Apfloat[] changeWithin(Function function, Apfloat x, Apfloat delta,
      double digits) {
    final Apcomplex[] change =
        changeWithin((t, precision) -> realValue(function, t, precision), x, delta, digits);
    if (change == null || change[0].imag().signum() != 0) {
      return null;
    }
    return new Apfloat[] {change[0].real(), change[1].real()};
  }

  /**
   * The value of a function of one real argument, which may be complex, and how much it changes
   * within <code>delta</code> of <code>x</code>, see
   * {@link #changeWithin(Function, Apfloat, Apfloat, double)}. The change of a complex value is
   * the absolute value of the difference.
   *
   * @return <code>{value, change}</code>, or <code>null</code> if the function has no value at
   *         <code>x</code> or on either side of it
   */
  public static Apcomplex[] changeWithin(ArgumentFunction function, Apfloat x, Apfloat delta,
      double digits) {
    // enough digits for a change of second order within the small steps
    final long precision =
        (long) Math.ceil(2.0 * (Math.max(digits, 1.0) + SHRINK_BITS * Math.log10(2.0))) + 20L;
    final Apfloat argument = withPrecision(x, precision);
    Apcomplex value = function.value(argument, precision);
    if (value == null && argument.signum() == 0) {
      value = valueNextToZero(function, precision);
    }
    if (value == null) {
      return null;
    }
    final boolean complex = value.imag().signum() != 0;
    final Apfloat d = delta.precision(precision);
    final Apfloat whole = change(function, argument, value, d, precision, complex);
    if (whole == null || whole.signum() == 0) {
      return null;
    }
    final Apfloat two = new Apfloat(2, precision);
    final Apfloat step = d.multiply(ApfloatMath.pow(two, -SHRINK_BITS));
    final Apfloat small = change(function, argument, value, step, precision, complex);
    final Apfloat smaller =
        change(function, argument, value, step.divide(two), precision, complex);
    if (small == null || smaller == null || small.signum() == 0 || smaller.signum() == 0) {
      return new Apcomplex[] {value, whole};
    }
    double power = Math.log(small.divide(smaller).doubleValue()) / Math.log(2.0);
    final double twice = 2.0 * power;
    if (Math.abs(twice - Math.rint(twice)) < 0.01) {
      // a power like 1, 2 or 1/2 is taken as exact
      power = Math.rint(twice) / 2.0;
    }
    // the leading term at the size of delta
    final Apfloat factor = ApfloatMath.pow(two, new Apfloat(SHRINK_BITS * power, precision));
    return new Apcomplex[] {value, small.multiply(factor)};
  }

  /**
   * The value at <code>0</code>, which has infinite precision, to which a transcendental value like
   * <code>ArcCosh(0)</code> cannot be calculated: the value next to it, far within the digits of
   * the calculation. A function which is infinite at <code>0</code>, like <code>ArcSech</code>,
   * changes there by far more than the digits, and has no value.
   */
  private static Apcomplex valueNextToZero(ArgumentFunction function, long precision) {
    final Apfloat next = ApfloatMath.scale(new Apfloat(1L, precision), -2L * precision);
    final Apcomplex value = function.value(next, precision);
    final Apcomplex further = function.value(next.multiply(new Apfloat(10L, precision)), precision);
    if (value == null || further == null) {
      return null;
    }
    final Apfloat change = ApcomplexMath.abs(further.subtract(value));
    final Apfloat size = ApcomplexMath.abs(value);
    final double limit =
        -precision / 2.0 + (size.signum() == 0 ? 0.0 : Math.max(0.0, log10Abs(size)));
    return change.signum() == 0 || log10Abs(change) <= limit ? value : null;
  }

  /**
   * The smaller change of the two sides <code>x+step</code> and <code>x-step</code>, or
   * <code>null</code> if the function has no value on either side.
   *
   * @param complex <code>true</code> if a side with a complex value counts
   */
  private static Apfloat change(ArgumentFunction function, Apfloat x, Apcomplex value,
      Apfloat step, long precision, boolean complex) {
    Apfloat change = null;
    for (Apfloat side : new Apfloat[] {x.add(step), x.subtract(step)}) {
      final Apcomplex other = function.value(side, precision);
      if (other == null || (!complex && other.imag().signum() != 0)) {
        continue;
      }
      final Apfloat difference = ApcomplexMath.abs(other.subtract(value));
      if (change == null || difference.compareTo(change) < 0) {
        change = difference;
      }
    }
    return change;
  }

  /**
   * The value of a function of one argument, or <code>null</code> if it isn't a real number: the
   * functions of {@link #evaluate(Function, Apfloat[], long)} throw for a complex value.
   */
  private static Apfloat realValue(Function function, Apfloat x, long precision) {
    return evaluate(function, new Apfloat[] {x}, precision);
  }

  /** <code>x</code> with <code>precision</code> digits; a zero keeps its infinite precision. */
  private static Apfloat withPrecision(Apfloat x, long precision) {
    return x.signum() == 0 ? x : x.precision(precision);
  }

  /**
   * <code>10^-exponent</code> for a real exponent, also beyond the range of a double value.
   *
   * @param exponent a finite number
   * @param precision the precision of the result
   */
  public static Apfloat tenToTheMinus(double exponent, long precision) {
    final double whole = Math.floor(exponent);
    final Apfloat fraction = new Apfloat(Math.pow(10.0, whole - exponent), precision);
    return ApfloatMath.scale(fraction, -(long) whole);
  }
}
