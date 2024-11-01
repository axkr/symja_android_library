package org.matheclipse.core.interfaces;

import java.math.BigInteger;
import org.hipparchus.fraction.BigFraction;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.exception.BigIntegerLimitExceeded;
import org.matheclipse.core.expression.F;
import edu.jas.arith.BigComplex;

/** Interface for "rational" numbers (i.e. numbers implementing IInteger or IFraction) */
public interface IRational extends IReal, IBigNumber {

  /** {@inheritDoc} */
  @Override
  public IRational abs();

  public IRational add(IRational parm1);

  default IRational add(int parm1) {
    return add(F.ZZ(parm1));
  }

  @Override
  public IInteger ceil();

  /**
   * Check if the bit length of the numerator or denominator is greater than
   * {@link Config#MAX_BIT_LENGTH}.
   * <p>
   * If <code>true</code> throw BigIntegerLimitExceeded
   */
  public void checkBitLength() throws BigIntegerLimitExceeded;

  public int compareInt(final int value);

  @Override
  public IRational dec();

  /**
   * Returns the denominator of this fraction.
   *
   * @return denominator
   */
  public IInteger denominator();

  public IRational divideBy(IRational parm1);

  default IRational divideBy(int parm1) {
    return divideBy(F.ZZ(parm1));
  }

  /**
   * Check if this number equals the given fraction <code>numerator/denominator</code> number.
   * <code>GCD(numerator, /denominator)</code> should be 1;
   *
   * @param numerator the numerator
   * @param denominator the denominator
   * @return
   */
  public boolean equalsFraction(final int numerator, final int denominator);

  /**
   * Return the prime factors paired with their exponents for integer and fractional numbers. For
   * factors of the denominator part of fractional numbers the exponents are negative.
   *
   * <pre>
   * factorInteger(-4) ==> {{-1,1},{2,2}}
   * </pre>
   *
   * @return the list of prime factors paired with their exponents
   */
  public IASTAppendable factorInteger();

  /**
   * Factor into small factors below 1021 if possible and determine the root.
   *
   * @param numerator
   * @param root the <code>root > 1</code> which should be determined
   * @return the rest of the factorization
   */
  public IAST factorSmallPrimes(int numerator, int root);

  @Override
  public IInteger floor();

  /**
   * Return the fractional part of this fraction
   *
   * @return
   */
  @Override
  public IRational fractionalPart();

  /**
   * Compute the gcd of two rationals. The gcd is the rational number, such that dividing this and
   * other with the gcd will yield two co-prime integers.
   *
   * @param that the second rational argument.
   * @return the gcd of this and other.
   */
  public IRational gcd(IRational that);

  /**
   * Returns the denominator of this fraction.
   *
   * @return denominator
   * @deprecated use {@link #denominator()}
   */
  @Deprecated
  default IInteger getDenominator() {
    return denominator();
  }

  /**
   * Returns this number as <code>BigFraction</code> number.
   *
   * @return <code>this</code> number s big fraction.
   * @deprecated use {@link #toBigFraction()}
   */
  @Deprecated
  default BigFraction getFraction() {
    return toBigFraction();
  }

  /**
   * Returns the numerator of this fraction.
   *
   * @return
   * @deprecated use {@link #numerator()}
   */
  @Deprecated
  default IInteger getNumerator() {
    return numerator();
  }

  @Override
  default IRational imRational() {
    return F.C0;
  }

  @Override
  public IRational inc();

  /** {@inheritDoc} */
  @Override
  public IRational inverse();

  /**
   * Returns <code>this mod m</code>, a non-negative value less than m. This differs from <code>
   * this % m</code>, which might be negative. For example:
   *
   * <pre>
   * mod(7, 4) == 3
   * mod(-7, 4) == 1
   * mod(-1, 4) == 3
   * mod(-8, 4) == 0
   * mod(8, 4) == 0
   * </pre>
   *
   * @param m
   * @return
   * @throws ArithmeticException - if m <= 0
   */
  default IRational mod(final IRational m) {
    return subtract(m.multiply(this.divideBy(m).floorFraction()));
  }

  @Override
  public IRational multiply(int n);

  public IRational multiply(IRational parm1);

  @Override
  public IRational negate();

  /**
   * Return the normalized form of this number (i.e. if the denominator part equals one, return the
   * numerator part as an integer number).
   *
   * @return
   */
  public IRational normalize();

  /**
   * Returns the numerator of this fraction.
   *
   * @return numerator
   */
  public IInteger numerator();

  /**
   * Returns this number raised at the specified exponent. See
   * <a href="https://en.wikipedia.org/wiki/Exponentiation_by_squaring">Wikipedia - Exponentiation
   * by squaring</a>
   *
   * @param exp the exponent.
   * @return <code>this<sup>exp</sup></code>
   * @throws ArithmeticException if {@code 0^0} is given.
   */
  public IRational powerRational(final long exp) throws ArithmeticException;

  @Override
  default IRational reRational() {
    return this;
  }

  @Override
  public IRational roundClosest(IReal factor);

  public IRational subtract(IRational parm1);

  default IRational subtract(int parm1) {
    return subtract(F.ZZ(parm1));
  }

  /**
   * Returns the denominator of this fraction.
   *
   * @return denominator
   */
  public BigInteger toBigDenominator();

  /**
   * Returns this number as {@link edu.jas.arith.BigRational} number.
   *
   * @return <code>this</code> number s big fraction.
   */
  public BigFraction toBigFraction();

  /**
   * Returns this number as {@link edu.jas.arith.BigComplex} number.
   *
   * @return <code>this</code> number s big complex representation.
   */
  @Override
  default BigComplex toBigComplex() {
    return new BigComplex(this.toBigRational());
  }

  /**
   * Returns this number as {@link edu.jas.arith.BigRational} number.
   *
   * @return <code>this</code> number s big fraction.
   */
  public edu.jas.arith.BigRational toBigRational();

  /**
   * Returns the numerator of this fraction.
   *
   * @return denominator
   */
  public BigInteger toBigNumerator();

  /**
   * Truncates the integer part in the "direction to 0" as <code>isNegative() ? ceil() : floor()
   * </code>..
   *
   * @return
   */
  default IInteger trunc() {
    return isNegative() ? ceil() : floor();
  }
}
