package org.matheclipse.core.interfaces;

import org.apfloat.Apcomplex;
import org.hipparchus.complex.Complex;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Pair;

/** Implemented by all number interfaces */
public interface INumber extends IExpr, IAtomicConstant, IAtomicEvaluate {

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

  @Override
  default Pair asCoeffAdd() {
    // https://github.com/sympy/sympy/blob/b64cfcdb640975706c71f305d99a8453ea5e46d8/sympy/core/numbers.py#L816
    if (isInteger() || isFraction()) {
      return F.pair(this, F.Plus());
    }
    return F.pair(F.C0, F.Plus(this));
  }

  @Override
  default Pair asCoeffmul(ISymbol deps, boolean rational) {
    // https://github.com/sympy/sympy/blob/8f90e7f894b09a3edc54c44af601b838b15aa41b/sympy/core/numbers.py#L828
    if (!rational || isRational()) {
      return F.pair(this, F.CEmptyList);
    } else if (isNegative()) {
      return F.pair(F.CN1, F.List(negate()));
    }
    return F.pair(F.C1, F.List(this));
  }

  @Override
  default Pair asCoeffMul(boolean rational) {
    // https://github.com/sympy/sympy/blob/8f90e7f894b09a3edc54c44af601b838b15aa41b/sympy/core/numbers.py#L828
    if (rational && !isRational()) {
      return F.pair(F.C1, this);
    }
    if (isZero()) {
      return F.pair(F.C1, this);
    }
    return F.pair(this, F.C1);
  }

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
   * Get a <code>ComplexNum</code> number object.
   *
   * @return
   */
  public ComplexNum complexNumValue();

  default Complex complexValue() {
    return complexNumValue().complexValue();
  }

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

  default INumber divide(INumber that) {
    if (that.isZero()) {
      throw new ArithmeticException("Division by zero");
    }
    if (this.isZero()) {
      return F.C0;
    }
    return times(that.inverse());
  }

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

  @Override
  default IExpr eval(EvalEngine engine) {
    return evaluate(engine).orElse(this);
  }

  @Override
  default Complex evalfc() throws ArgumentTypeException {
    return new Complex(reDoubleValue(), imDoubleValue());
  }

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

  /**
   * Returns the imaginary part of a complex number
   *
   * @return real part
   */
  @Override
  public IReal im();

  /**
   * Returns the imaginary part of a complex number
   *
   * @return real part
   */
  public double imDoubleValue();

  /**
   * Return the integer (real and imaginary) part of this number
   *
   * @return
   */
  public INumber integerPart();

  @Override
  default COMPARE_TERNARY isIrrational() {
    return COMPARE_TERNARY.FALSE;
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
   * Test if this number equals <code>0</code> in symbolic or numeric mode. In numeric mode check if
   * the number is close to <code>0</code> according to the given <code>tolerance</code>.
   * 
   * @param tolerance
   * @return
   */
  default boolean isZero(double tolerance) {
    return isZero();
  }

  @Override
  public default IExpr[] linear(IExpr variable) {
    return new IExpr[] {this, F.C0};
  }

  @Override
  public default IExpr[] linearPower(IExpr variable) {
    return new IExpr[] {this, F.C0, F.C1};
  }

  @Override
  public INumber negate();

  /**
   * Multiplicative neutral element of this number
   * 
   * <p>
   * For any number s, the scalar s.one() shall satisfy the equation
   * 
   * <pre>
   * s.multiply(one()) equals s
   * </pre>
   * 
   * <p>
   * one() is provided for the implementation of generic functions and algorithms.
   * 
   * @return multiplicative neutral element of this scalar
   */
  @Override
  default INumber one() {
    return F.C1;
  }

  @Override
  public INumber inverse();

  @Override
  public INumber opposite();

  /**
   * Returns an <code>INumber</code> whose value is <code>(this + that)</code>.
   * 
   * @param that
   * @return
   */
  public INumber plus(INumber that);

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
  public IReal re();

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
  @Override
  public IExpr roundExpr();

  /**
   * Returns an <code>INumber</code> whose value is <code>(this - that)</code>.
   * 
   * @param that
   * @return
   */
  default INumber subtract(INumber that) {
    if (this.isZero()) {
      return that.negate();
    }
    if (that.isZero()) {
      return this;
    }
    return plus(that.negate());
  }

  /**
   * Returns an <code>INumber</code> whose value is <code>(this * that)</code>.
   * 
   * @param that the number to multiply with
   */
  public INumber times(INumber that);

  /**
   * Returns an <code>IExpr</code> whose value is <code>(this * that)</code>.
   * 
   * @param that
   */
  default IExpr timesExpr(INumber that) {
    if (this.isInfinite()) {
      IExpr arg1 = this.isPositive() ? F.CInfinity : F.CNInfinity;
      if (that.isInfinite()) {
        IExpr arg2 = that.isPositive() ? F.CInfinity : F.CNInfinity;
        return F.Times(arg1, arg2);
      }
      return F.Times(arg1, that);
    }
    if (that.isInfinite()) {
      IExpr arg2 = that.isPositive() ? F.CInfinity : F.CNInfinity;
      return F.Times(this, arg2);
    }
    return times(that);
  }

  /**
   * Return the list <code>{r, theta}</code> of the polar coordinates of this number
   *
   */
  public IAST toPolarCoordinates();

  /**
   * Additive neutral element of this number.
   * 
   * <p>
   * For any number s, the scalar s.zero() shall satisfy the equation
   * 
   * <pre>
   * s.add(zero()) equals s
   * </pre>
   * 
   * <p>
   * zero() is provided for the implementation of generic functions and algorithms.
   * 
   * @return additive neutral element of field of this scalar
   */
  @Override
  default INumber zero() {
    return F.C0;
  }

}
