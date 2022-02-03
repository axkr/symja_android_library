package org.matheclipse.core.interfaces;

import org.apfloat.Apcomplex;
import org.hipparchus.complex.Complex;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.F;

/** Implemented by all number interfaces */
public interface INumber extends IExpr {

  /**
   * Get the absolute value for a given number
   *
   * @return
   */
  @Override
  public IExpr abs();

  /**
   * Get a {@link Apcomplex} number wrapped into an <code>ApcomplexNum</code> object.
   *
   * @return this number represented as an ApcomplexNum
   */
  public ApcomplexNum apcomplexNumValue();

  /**
   * Get a {@link Apcomplex} object.
   *
   * @return this number represented as an Apcomplex
   */
  public Apcomplex apcomplexValue();

  /**
   * Returns the smallest (closest to negative infinity) <code>IInteger</code> value that is not
   * less than <code>this</code> and is equal to a mathematical integer. This method raises
   * {@link ArithmeticException} if a numeric value cannot be represented by an <code>long</code>
   * type.
   *
   * @return the smallest (closest to negative infinity) <code>IInteger</code> value that is not
   *         less than <code>this</code> and is equal to a mathematical integer.
   */
  public INumber ceilFraction() throws ArithmeticException;

  /**
   * Compare the absolute value of this number with <code>1</code> and return
   *
   * <ul>
   * <li><code>1</code>, if the absolute value is greater than 1
   * <li><code>0</code>, if the absolute value equals 1
   * <li><code>-1</code>, if the absolute value is less than 1
   * </ul>
   *
   * @return
   */
  public int compareAbsValueToOne();

  /**
   * Get the argument of the complex number
   *
   * @return
   */
  @Override
  public IExpr complexArg();

  /**
   * Get a <code>ComplexNum</code> number bject.
   *
   * @return
   */
  public ComplexNum complexNumValue();

  /**
   * Gets the signum value of a complex number
   *
   * @return 0 for <code>this == 0</code>; +1 for <code>real(this) &gt; 0</code> or
   *         <code>( real(this)==0 &amp;&amp; imaginary(this) &gt; 0 )</code>; -1 for
   *         <code>real(this) &lt; 0 || ( real(this) == 0 &amp;&amp; imaginary(this) &lt; 0 )
   */
  public int complexSign();

  @Override
  public INumber conjugate();

  /**
   * Get the absolute value for a given number
   *
   * @return
   * @deprecated use abs()
   */
  @Deprecated
  default IExpr eabs() {
    return abs();
  }

  /**
   * Check if this number equals the given <code>int</code> number?
   *
   * @param i the integer number
   * @return
   */
  public boolean equalsInt(int i);

  default INumber evaluatePrecision(EvalEngine engine) {
    return this;
  }

  /**
   * Returns the largest (closest to positive infinity) <code>IInteger</code> value that is not
   * greater than <code>this</code> and is equal to a mathematical integer. <br>
   * This method raises {@link ArithmeticException} if a numeric value cannot be represented by an
   * <code>long</code> type.
   *
   * @return the largest (closest to positive infinity) <code>IInteger</code> value that is not
   *         greater than <code>this</code> and is equal to a mathematical integer.
   */
  public INumber floorFraction() throws ArithmeticException;

  /**
   * Return the fractional part of this number
   *
   * @return
   */
  public INumber fractionalPart();

  /**
   * Return the integer (real and imaginary) part of this number
   *
   * @return
   */
  public INumber integerPart();

  /**
   * Returns the imaginary part of a complex number
   *
   * @return real part
   * @deprecated use {@link #imDoubleValue()}
   */
  @Deprecated
  default double getImaginary() {
    return imDoubleValue();
  }

  /**
   * Returns the real part of a complex number
   *
   * @return real part
   * @deprecated use {@link #reDoubleValue()}
   */
  @Override
  @Deprecated
  default double getReal() {
    return reDoubleValue();
  }

  @Override
  default boolean isNumber() {
    return true;
  }

  @Override
  default boolean isNumericFunction(boolean allowList) {
    return true;
  }

  /**
   * Returns the imaginary part of a complex number
   *
   * @return real part
   */
  @Override
  public ISignedNumber im();

  /**
   * Returns the imaginary part of a complex number
   *
   * @return real part
   */
  public double imDoubleValue();

  @Override
  public default IExpr[] linear(IExpr variable) {
    return new IExpr[] {this, F.C0};
  }

  @Override
  public default IExpr[] linearPower(IExpr variable) {
    return new IExpr[] {this, F.C0, F.C1};
  }

  @Override
  public INumber opposite();

  /**
   * Return the rational Factor of this number. For IComplex numbers check if real and imaginary
   * parts are equal or real part or imaginary part is zero.
   *
   * @return <code>null</code> if no factor could be extracted
   */
  default IRational rationalFactor() {
    if (this instanceof IRational) {
      return (IRational) this;
    }
    return null;
  }

  /**
   * Returns the real part of a complex number
   *
   * @return real part
   */
  @Override
  public ISignedNumber re();

  /**
   * Returns the real part of a complex number
   *
   * @return real part
   */
  public double reDoubleValue();

  /**
   * Returns the closest <code>IInteger</code> real and imaginary part to the argument. The result
   * is rounded to an integer by adding 1/2 and taking the floor of the result by applying round
   * separately to the real and imaginary part . <br>
   * This method raises {@link ArithmeticException} if a numeric value cannot be represented by an
   * <code>long</code> type.
   *
   * @return the closest integer to the argument.
   */
  public IExpr roundExpr();

  /**
   * Return the list <code>{r, theta}</code> of the polar coordinates of this number
   *
   * @return
   */
  public IAST toPolarCoordinates();

  @Override
  default Complex evalComplex() throws ArgumentTypeException {
    return new Complex(reDoubleValue(), imDoubleValue());
  }
}
