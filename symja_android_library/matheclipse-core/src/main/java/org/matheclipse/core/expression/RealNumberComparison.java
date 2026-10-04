package org.matheclipse.core.expression;

import java.math.BigDecimal;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.Apint;
import org.apfloat.ApintMath;
import org.apfloat.Aprational;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.parser.client.ParserConfig;

/**
 * Orders real numbers independent of their storage type. There are two levels:
 * <ul>
 * <li>{@link #compare(IReal, IReal)} and the predicates built on it are what
 * {@link IReal#isLT(IReal)}, {@link IReal#isGT(IReal)} and <code>compareTo()</code> answer. Next to
 * a machine number the other operand is taken at machine precision, as long as it has a machine
 * value; every other pair is compared by its exact stored value. No tolerance is applied.
 * <li>{@link #compareWithTolerance(IReal, IReal)} is what <code>Less</code>, <code>Greater</code>
 * and <code>Equal</code> answer: inexact numbers which differ only in their last 7 bits are equal.
 * </ul>
 */
public final class RealNumberComparison {
  /** <code>Equal</code>, <code>Less</code> and <code>Greater</code> ignore the last 7 bits. */
  private static final int EQUAL_BITS = 7;

  /** <code>SameQ</code> ignores the last bit. */
  private static final int SAME_BITS = 1;

  private RealNumberComparison() {}

  public static boolean isGreater(IReal left, IReal right) {
    if (left instanceof Num && right instanceof Num) {
      // the common case needs no exact representation
      return left.doubleValue() > right.doubleValue();
    }
    return !left.isNaN() && !right.isNaN() && compare(left, right) > 0;
  }

  public static boolean isLess(IReal left, IReal right) {
    if (left instanceof Num && right instanceof Num) {
      return left.doubleValue() < right.doubleValue();
    }
    return !left.isNaN() && !right.isNaN() && compare(left, right) < 0;
  }

  /**
   * Compare two real numbers without a tolerance. Total sorting order:
   * <code>-Infinity &lt; finite values &lt; +Infinity &lt; NaN</code>; the predicates above keep
   * <code>NaN</code> unordered instead of using that sorting convention.
   */
  public static int compare(IReal left, IReal right) {
    if (left.isNaN() || right.isNaN()) {
      return Boolean.compare(left.isNaN(), right.isNaN());
    }
    if (left.isInfinite() || right.isInfinite()) {
      return Integer.compare(left.isInfinite() ? left.complexSign() : 0,
          right.isInfinite() ? right.complexSign() : 0);
    }
    if (left instanceof Num || right instanceof Num) {
      // Takes the other operand at machine precision: 2^53+1 is not greater than 2.^53
      double l = machineValue(left);
      double r = machineValue(right);
      if (!Double.isNaN(l) && !Double.isNaN(r)) {
        // signed zeros are equal
        return l == r ? 0 : Double.compare(l, r);
      }
    }
    return exactCompare(left, right);
  }

  /**
   * Compare two real numbers as <code>Less</code>, <code>Greater</code> and <code>Equal</code> do:
   * two numbers of which at least one is inexact are equal, if they differ by at most
   * <code>2^7</code> units in the last place of the less precise one, relative to the smaller
   * magnitude. Zero is equal to zero only, and two exact numbers are compared exactly.
   *
   * @return <code>-1, 0, 1</code>
   */
  public static int compareWithTolerance(IReal left, IReal right) {
    return compareWithTolerance(left, right, EQUAL_BITS);
  }

  /**
   * Whether two inexact real numbers are the same for <code>SameQ</code>: they differ in at most
   * their last bit, at the precision of the less precise one.
   */
  public static boolean isSameWithTolerance(IReal left, IReal right) {
    return compareWithTolerance(left, right, SAME_BITS) == 0;
  }

  private static int compareWithTolerance(IReal left, IReal right, int bits) {
    if (left.isNaN() || right.isNaN() || left.isInfinite() || right.isInfinite()) {
      return Integer.signum(compare(left, right));
    }
    if (left instanceof IRational && right instanceof IRational) {
      return Integer.signum(left.compareTo(right));
    }
    if (left instanceof Num || right instanceof Num) {
      double l = machineValue(left);
      double r = machineValue(right);
      if (!Double.isNaN(l) && !Double.isNaN(r)) {
        if (l == r) {
          return 0;
        }
        // exact in double arithmetic: the difference of two nearby doubles and a power of two
        // times a double
        return Math.abs(l - r) <= Math.scalb(Math.min(Math.abs(l), Math.abs(r)), bits - 53) ? 0
            : (l < r ? -1 : 1);
      }
    }
    final int c = Integer.signum(exactCompare(left, right));
    if (c == 0) {
      return 0;
    }
    if ((left instanceof Num && Double.isInfinite(right.doubleValue()))
        || (right instanceof Num && Double.isInfinite(left.doubleValue()))) {
      // every machine number is smaller than a number beyond the double range:
      // $MaxMachineNumber < 2^1024
      return c;
    }
    Apfloat tolerance = tolerance(left, right, bits);
    if (tolerance == null) {
      // no operand of a finite precision
      return c;
    }
    final long digits = workingDigits(left, right);
    Apfloat x = decimalValue(left, digits);
    Apfloat y = decimalValue(right, digits);
    if (x.signum() == 0 || y.signum() != x.signum()) {
      // zero is equal to zero only
      return c;
    }
    if (Math.abs(x.scale() - y.scale()) > 1) {
      return c;
    }
    Apfloat absX = ApfloatMath.abs(x);
    Apfloat absY = ApfloatMath.abs(y);
    Apfloat smaller = absX.compareTo(absY) <= 0 ? absX : absY;
    Apfloat difference = ApfloatMath.abs(x.subtract(y));
    return difference.compareTo(smaller.multiply(tolerance)) <= 0 ? 0 : c;
  }

  /**
   * <code>|x - y|</code> of two finite real numbers; a fraction is rounded to <code>digits</code>
   * digits, every other number is taken exactly.
   */
  public static Apfloat absoluteDifference(IReal x, IReal y, long digits) {
    Apfloat a = decimalValue(x, digits);
    Apfloat b = decimalValue(y, digits);
    if (a.signum() != 0 && b.signum() != 0 && Math.abs(a.scale() - b.scale()) > digits) {
      // Numbers of very different size: the exact difference of E^(10^6) and 3 would have a
      // million digits. The larger one is the difference to the digits asked for.
      return ApfloatMath.abs(a.scale() > b.scale() ? a : b);
    }
    return ApfloatMath.abs(a.subtract(b));
  }

  private static int exactCompare(IReal left, IReal right) {
    // Exact values make both signed zeros equal without changing either stored representation.
    // Apfloat.compareTo already dispatches to Aprational when an exact fraction is involved.
    return exactValue(left).compareTo(exactValue(right));
  }

  /**
   * The exact stored value of a finite real number as a radix 10 number, independent of the numeric
   * precision of the engine: the binary value of a machine number, all digits of an arbitrary
   * precision number, numerator and denominator of a rational number. The result may be an
   * {@link Aprational}, which <code>Apfloat#compareTo</code> handles without rounding.
   */
  public static Apfloat exactValue(IReal x) {
    if (x instanceof Num) {
      // the stored binary double, including subnormal values
      return new Aprational(x.doubleValue(), 10);
    }
    if (x instanceof IRational) {
      IRational rational = (IRational) x;
      return new Aprational(new Apint(rational.toBigNumerator(), 10),
          new Apint(rational.toBigDenominator(), 10));
    }
    Apfloat value = x.apfloatValue();
    if (value.radix() == 10) {
      // keeps the compact exponent
      return value.precision(Apfloat.INFINITE);
    }
    if (value instanceof Aprational) {
      return ((Aprational) value).toRadix(10);
    }
    if (value.signum() == 0) {
      return new Apint(0, 10);
    }
    // A finite mantissa in any radix is coefficient * radix^exponent. Convert that exact ratio;
    // direct decimal conversion would round repeating values such as 0.1 in radix 3 (= 1/3).
    long exponent = value.scale() - value.size();
    Apint coefficient =
        ApfloatMath.scale(value.precision(Apfloat.INFINITE), -exponent).truncate().toRadix(10);
    Apint factor = ApintMath.pow(new Apint(value.radix(), 10), Math.abs(exponent));
    return exponent < 0 ? new Aprational(coefficient, factor) : coefficient.multiply(factor);
  }

  /**
   * The canonical order of two real numbers, which <code>compareTo()</code> answers:
   * {@link #compare(IReal, IReal)}, and for two numbers of the same value order of the types -
   * integer, machine number, arbitrary precision number, fraction. So <code>1</code> sorts before
   * <code>1.</code> and <code>0.3</code> before <code>3/10</code>, and <code>Min</code> and
   * <code>Max</code> return the first and the last one of them.
   *
   * @return <code>-1, 0, 1</code>
   */
  public static int order(IReal left, IReal right) {
    int c = Integer.signum(compare(left, right));
    if (c == 0) {
      c = Integer.compare(typeRank(left), typeRank(right));
    }
    if (c == 0 && left instanceof ApfloatNum && right instanceof ApfloatNum) {
      // the less precise one first
      c = Long.compare(((ApfloatNum) left).precision(), ((ApfloatNum) right).precision());
    }
    return c;
  }

  private static int typeRank(IReal x) {
    if (x instanceof IRational) {
      return ((IRational) x).isInteger() ? 0 : 3;
    }
    return x instanceof Num ? 1 : 2;
  }

  /**
   * The double value a number is compared with next to a machine number.
   *
   * @return <code>NaN</code> if the number has no machine value: it lies beyond the double range,
   *         underflows to zero, or is less precise than a machine number
   */
  private static double machineValue(IReal x) {
    if (x instanceof Num) {
      return x.doubleValue();
    }
    final boolean zero;
    if (x instanceof ApfloatNum) {
      Apfloat value = ((ApfloatNum) x).apfloatValue();
      if (value.precision() < ParserConfig.MACHINE_PRECISION) {
        return Double.NaN;
      }
      zero = value.signum() == 0;
    } else {
      zero = x.isZero();
    }
    double d = x.doubleValue();
    if (Double.isFinite(d) && (zero || d != 0.0)) {
      // a subnormal value too: 10^-320 is equal to 1.*^-320
      return d;
    }
    return Double.NaN;
  }

  /**
   * The relative tolerance for a pair of numbers: <code>bits</code> bits of the less precise
   * operand.
   *
   * @return <code>null</code> if both numbers are exact
   */
  private static Apfloat tolerance(IReal left, IReal right, int bits) {
    // a number of machine precision beyond the double range, like N(10^400), counts as a machine
    // number: 10^400*(1+2^-46) is equal to it
    boolean machine = left instanceof Num || right instanceof Num || isMachinePrecision(left)
        || isMachinePrecision(right);
    long precision = Apfloat.INFINITE;
    if (left instanceof ApfloatNum) {
      precision = Math.min(precision, ((ApfloatNum) left).precision());
    }
    if (right instanceof ApfloatNum) {
      precision = Math.min(precision, ((ApfloatNum) right).precision());
    }
    if (machine && precision >= ParserConfig.MACHINE_PRECISION) {
      return new Apfloat(new BigDecimal(Math.scalb(1.0, bits - 53))).precision(Apfloat.INFINITE);
    }
    if (precision == Apfloat.INFINITE) {
      return null;
    }
    // 2^bits * 10^(-precision)
    return ApfloatMath.scale(new Apfloat(1L << bits, Apfloat.INFINITE), -precision);
  }

  private static boolean isMachinePrecision(IReal x) {
    return x instanceof ApfloatNum
        && ((ApfloatNum) x).precision() == ParserConfig.MACHINE_PRECISION;
  }

  private static long workingDigits(IReal left, IReal right) {
    long digits = ParserConfig.MACHINE_PRECISION + 1;
    if (left instanceof ApfloatNum && ((ApfloatNum) left).precision() != Apfloat.INFINITE) {
      digits = Math.max(digits, ((ApfloatNum) left).precision());
    }
    if (right instanceof ApfloatNum && ((ApfloatNum) right).precision() != Apfloat.INFINITE) {
      digits = Math.max(digits, ((ApfloatNum) right).precision());
    }
    return digits + 20;
  }

  /**
   * A number as a decimal number of infinite precision: exact for an integer and for an inexact
   * number, rounded to <code>digits</code> digits for a fraction.
   */
  private static Apfloat decimalValue(IReal x, long digits) {
    if (x instanceof Num) {
      return new Apfloat(new BigDecimal(x.doubleValue())).precision(Apfloat.INFINITE);
    }
    Apfloat exact = exactValue(x);
    if (exact instanceof Aprational && !(exact instanceof Apint)) {
      Aprational fraction = (Aprational) exact;
      return fraction.numerator().precision(digits).divide(fraction.denominator().precision(digits))
          .precision(Apfloat.INFINITE);
    }
    return exact;
  }
}
