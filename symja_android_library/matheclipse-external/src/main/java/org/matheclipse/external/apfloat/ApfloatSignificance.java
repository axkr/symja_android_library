package org.matheclipse.external.apfloat;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;

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
    SIN, COS, TAN, COT, SEC, CSC, ARCSIN, ARCCOS, ARCTAN, ARCCOT, ARCSEC, ARCCSC
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
    final double f = value.doubleValue();
    final double x = args[0].doubleValue();
    if (!Double.isFinite(f) || f == 0.0) {
      return null;
    }
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
      final Apfloat x = args[0].precision(precision);
      switch (function) {
        case POWER: {
          final Apfloat e = args[1].precision(precision);
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
          return ApfloatMath.log(args[1].precision(precision), x);
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
        default:
          return null;
      }
    } catch (ArithmeticException | IllegalArgumentException e) {
      return null;
    }
  }
}
