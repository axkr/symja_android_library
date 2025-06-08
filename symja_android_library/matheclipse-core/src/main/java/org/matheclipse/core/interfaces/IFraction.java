package org.matheclipse.core.interfaces;

import java.math.BigInteger;
import org.hipparchus.fraction.BigFraction;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Pair;

/** interface for "fractional" numbers */
public interface IFraction extends IRational {

  @Override
  public void checkBitLength();

  /** {@inheritDoc} */
  @Override
  public IFraction abs();

  @Override
  public abstract IInteger floorFraction();

  /**
   * Return the fractional part of this fraction
   *
   * @return
   */
  @Override
  public IFraction fractionalPart();

  public IFraction add(IFraction parm1);

  public IFraction div(IFraction other);

  /**
   * Returns an array of two BigIntegers containing (numerator / denominator) followed by (numerator
   * % denominator).
   *
   * @return
   */
  public IInteger[] divideAndRemainder();

  /**
   * Returns a new rational representing the inverse of <code>this</code>.
   *
   * @return Inverse of <code>this</code>.
   */
  @Override
  public IFraction inverse();

  public IFraction gcd(IFraction other);

  public IFraction mul(IFraction other);

  /**
   * Returns a new rational equal to <code>-this</code>.
   *
   * @return <code>-this</code>.
   */
  @Override
  public IFraction negate();

  @Override
  public IRational normalize();

  /**
   * Returns this number raised at the specified exponent. See
   * <a href="https://en.wikipedia.org/wiki/Exponentiation_by_squaring">Wikipedia - Exponentiation
   * by squaring</a>
   *
   * @param exp the exponent.
   * @return <code>this<sup>exp</sup></code>
   * @throws ArithmeticException if {@code 0^0} is given.
   */
  @Override
  public IFraction powerRational(final long exp) throws ArithmeticException;

  public IFraction sub(IFraction parm1);

  /**
   * Returns the denominator of this fraction.
   *
   * @return denominator
   */
  @Override
  public BigInteger toBigDenominator();

  /** {@inheritDoc} */
  @Override
  public BigFraction toBigFraction();

  /**
   * Returns the numerator of this fraction.
   *
   * @return numerator
   */
  @Override
  public BigInteger toBigNumerator();

  /**
   * Converts this number into the `n-th` root integer value if possible. The fraction should be of
   * the form <code>1 / denominator</code> for a &quot;positive root&quot; or
   * <code>-1 / denominator</code> for a &quot;negative root&quot;
   * 
   * <p>
   * This method returns the {@link Config#INVALID_INT} if this fractions numerator unequals
   * <code>-1</code> or <code>1</code>, or the denominator equals <code>Integer.MAX_VALUE</code>.
   * 
   * @return
   */
  public int toIntRoot();

  /**
   * Converts this number into the `n-th` root integer value if possible. The fraction should be of
   * the form <code>1 / denominator</code> for a &quot;positive root&quot; or
   * <code>-1 / denominator</code> for a &quot;negative root&quot;
   * 
   * <p>
   * This method returns the <code>defaultValue</code> if this fractions numerator unequals
   * <code>-1</code> or <code>1</code>, or the denominator equals <code>Integer.MAX_VALUE</code>.
   * 
   * @param defaultValue
   * @return
   */
  public int toIntRoot(int defaultValue);

  @Override
  default Pair asCoeffMul(boolean rational) {
    // https://github.com/sympy/sympy/blob/8f90e7f894b09a3edc54c44af601b838b15aa41b/sympy/core/numbers.py#L2034
    return F.pair(this, F.C1);
  }
}
