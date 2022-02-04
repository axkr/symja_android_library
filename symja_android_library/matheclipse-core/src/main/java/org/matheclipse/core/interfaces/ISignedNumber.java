package org.matheclipse.core.interfaces;

import org.apfloat.Apfloat;
import org.hipparchus.complex.Complex;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;

/** Interface for "rational" numbers (i.e. IInteger, IFraction or INum) */
public interface ISignedNumber extends INumber {

  /** {@inheritDoc} */
  @Override
  public ISignedNumber abs();

  /**
   * Get a {@link Apfloat} number wrapped into an <code>ApfloatNum</code> object.
   *
   * @return this signed number represented as an ApfloatNum
   */
  public ApfloatNum apfloatNumValue();

  public Apfloat apfloatValue();

  /** {@inheritDoc} */
  @Override
  default IExpr complexArg() {
    if (complexSign() < 0) {
      return S.Pi;
    }
    return F.C0;
  }

  /** {@inheritDoc} */
  @Override
  default ISignedNumber conjugate() {
    return this;
  }

  /**
   * Divide <code>this</code> signed number by <code>that</code> signed number.
   *
   * @param that a signed number
   * @return
   */
  public ISignedNumber divideBy(ISignedNumber that);

  /**
   * Returns the value of the specified number as a {@code double}, which may involve rounding.
   *
   * @return the numeric value represented by this object after conversion to type {@code double}.
   */
  public double doubleValue();

  @Override
  default double evalDouble() throws ArgumentTypeException {
    return doubleValue();
  }

  @Override
  default Complex evalComplex() throws ArgumentTypeException {
    return new Complex(doubleValue());
  }

  @Override
  public IInteger ceilFraction() throws ArithmeticException;

  @Override
  public IInteger floorFraction() throws ArithmeticException;

  /**
   * Return the fractional part of this fraction
   *
   * @return
   */
  @Override
  public ISignedNumber fractionalPart();

  /**
   * Return the integer part of this number
   *
   * <p>
   * This method raises ArithmeticException if a numeric value cannot be represented by an longtype.
   *
   * @return
   */
  @Override
  public IInteger integerPart();

  /** {@inheritDoc} */
  @Override
  public ISignedNumber inverse();

  /**
   * Test if <code>this</code> signed number is greater than <code>that</code> signed number..
   *
   * @return <code>this > that</code>
   */
  public boolean isGT(ISignedNumber that);

  /**
   * Test if <code>this</code> signed number is greater equal than <code>that</code> signed number.
   *
   * @return <code>this > that</code>
   */
  default boolean isGE(ISignedNumber that) {
    return !isLT(that);
  }

  /**
   * @param that
   * @return
   * @deprecated use #isGT()
   */
  @Deprecated
  default boolean isGreaterThan(ISignedNumber that) {
    return isGT(that);
  }

  /**
   * Test if <code>this</code> signed number is less than <code>that</code> signed number.
   *
   * @return <code>this < that</code>
   */
  public boolean isLT(ISignedNumber that);

  /**
   * Test if <code>this</code> signed number is less equal than <code>that</code> signed number.
   *
   * @return <code>this > that</code>
   */
  default boolean isLE(ISignedNumber that) {
    return !isGT(that);
  }

  /**
   * @param that
   * @return
   * @deprecated use #isLT()
   */
  @Deprecated
  default boolean isLessThan(ISignedNumber that) {
    return isLT(that);
  }

  /**
   * Test if this number is negative.
   *
   * @return <code>true</code>, if <code>this < 0</code>
   */
  @Override
  public boolean isNegative();

  @Override
  default boolean isNegativeResult() {
    return isNegative();
  }

  @Override
  default boolean isNonNegativeResult() {
    return !isNegative();
  }

  /**
   * Test if this number is positive.
   *
   * @return <code>true</code>, if <code>this > 0</code>
   */
  @Override
  public boolean isPositive();

  @Override
  default boolean isPositiveResult() {
    return isPositive();
  }

  /** {@inheritDoc} */
  @Override
  public boolean isZero();

  /**
   * If this is a <code>Interval[{lower, upper}]</code> expression return the <code>lower</code>
   * value. If this is a <code>ISignedNUmber</code> expression return <code>this</code>.
   *
   * @return <code>F.NIL</code> if this expression is no interval and no signed number.
   */
  @Override
  public default IExpr lower() {
    return this;
  }

  /**
   * Returns (-1) * this
   *
   * @return
   */
  @Override
  public ISignedNumber negate();

  /**
   * Get a Java double number wrapped into a <code>Num</code> object.
   *
   * @return
   */
  public INum numValue();

  @Override
  public ISignedNumber opposite();

  /**
   * Returns the closest <code>IInteger</code> to the argument. The result is rounded to an integer
   * by adding 1/2 and taking the floor of the result. <br>
   * This method raises {@link ArithmeticException} if a numeric value cannot be represented by an
   * <code>long</code> type.
   *
   * @return the closest integer to the argument.
   */
  @Override
  public IInteger roundExpr();

  /**
   * Round <code>this</code> number to the closest <code>ISignedNumber</code> multiple of the <code>
   * factor</code>.
   *
   * @param factor
   * @return the closest integer to the argument.
   * @throws ArithmeticException
   */
  public ISignedNumber roundClosest(ISignedNumber factor);

  /**
   * Returns the signum function of this number (i.e., -1, 0 or 1 as the value of this number is
   * negative, zero or positive).
   *
   * @return 1 if this is a positive number;<br>
   *         0 if this is a zero;<br>
   *         -1 if this is a negative number;
   */
  @Override
  public int complexSign();

  /**
   * Subtract <code>that</code> signed number from <code>this</code> signed number
   *
   * @param that a signed number
   * @return
   */
  public ISignedNumber subtractFrom(ISignedNumber that);

  /**
   * Converts this number to <code>int</code>; unlike {@link #intValue} this method raises
   * {@link ArithmeticException} if this number cannot be represented by an <code>int</code> type.
   *
   * @return the numeric value represented by this integer after conversion to type <code>int</code>
   *         .
   * @throws ArithmeticException if conversion to <code>int</code> is not possible.
   */
  public int toInt() throws ArithmeticException;

  /**
   * This real number is in the interval <code>[from, to]</code> inclusive
   *
   * @param from
   * @param to
   * @return
   */
  default boolean isRange(ISignedNumber from, ISignedNumber to) {
    return isGE(from) && isLE(to);
  }

  /**
   * This real number is in the interval <code>]from, to[</code> exclusive
   *
   * @param from
   * @param to
   * @return
   */
  default boolean isRangeExclExcl(ISignedNumber from, ISignedNumber to) {
    return isGT(from) && isLT(to);
  }

  /**
   * This real number is in the interval <code>]from, to]</code>
   *
   * @param from
   * @param to
   * @return
   */
  default boolean isRangeExclIncl(ISignedNumber from, ISignedNumber to) {
    return isGT(from) && isLE(to);
  }

  /**
   * This real number is in the interval <code>]from, to]</code>
   *
   * @param from
   * @param to
   * @return
   */
  default boolean isRangeInclExcl(ISignedNumber from, ISignedNumber to) {
    return isGE(from) && isLT(to);
  }

  /**
   * Converts this number to <code>long</code>; unlike {@link #longValue} this method raises
   * {@link ArithmeticException} if this number cannot be represented by a <code>long</code> type.
   *
   * @return the numeric value represented by this integer after conversion to type <code>long
   *     </code>.
   * @throws ArithmeticException if conversion to <code>long</code> is not possible.
   */
  public long toLong() throws ArithmeticException;

  @Override
  public default IExpr unitStep() {
    return isNegative() ? F.C0 : F.C1;
  }

  /**
   * If this is a <code>Interval[{lower, upper}]</code> expression return the <code>upper</code>
   * value. If this is a <code>ISignedNUmber</code> expression return <code>this</code>.
   *
   * @return <code>F.NIL</code> if this expression is no interval and no signed number.
   */
  @Override
  public default IExpr upper() {
    return this;
  }

  @Override
  default IAST toPolarCoordinates() {
    if (isNegative()) {
      return F.list(this.negate(), S.Pi);
    }
    return F.list(this, F.C0);
  }
}
