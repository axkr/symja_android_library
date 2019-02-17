package org.matheclipse.core.interfaces;

import org.hipparchus.fraction.BigFraction;
import java.math.BigInteger;

/**
 * Interface for "rational" numbers (i.e. numbers implementing IInteger or IFraction)
 * 
 */
public interface IRational extends ISignedNumber, IBigNumber {

	/** {@inheritDoc} */
	@Override
	public IRational abs();

	public IRational add(IRational parm1);

	public IInteger ceil();

	public int compareInt(final int value);

	/**
	 * Returns the denominator of this fraction.
	 * 
	 * @return denominator
	 */
	public IInteger denominator();

	public IRational divideBy(IRational parm1);

	/**
	 * Check if this number equals the given fraction <code>numerator/denominator</code> number.
	 * <code>GCD(numerator, /denominator)</code> should be 1;
	 * 
	 * @param numerator
	 *            the numerator
	 * @param denominator
	 *            the denominator
	 * @return
	 */
	public boolean equalsFraction(final int numerator, final int denominator);

	/**
	 * Return the prime factors paired with their exponents for integer and fractional numbers. For factors of the
	 * denominator part of fractional numbers the exponents are negative.
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
	 * @param root
	 *            the <code>root > 1</code> which should be determined
	 * @return the rest of the factorization
	 */
	public IAST factorSmallPrimes(int numerator, int root);
	
	public IInteger floor();

	/**
	 * Return the fractional part of this fraction
	 * 
	 * @return
	 */
	public IRational fractionalPart();

	/**
	 * Returns the denominator of this fraction.
	 * 
	 * @return denominator
	 * @deprecated use {@link #denominator()}
	 */
	default IInteger getDenominator() {
		return denominator();
	}

	/**
	 * Returns this number as <code>BigFraction</code> number.
	 * 
	 * @return <code>this</code> number s big fraction.
	 * @deprecated use {@link #toBigFraction()}
	 */
	default BigFraction getFraction() {
		return toBigFraction();
	}

	/**
	 * Returns the numerator of this fraction.
	 * 
	 * @return
	 * @deprecated use {@link #numerator()}
	 */ 
	default IInteger getNumerator() {
		return numerator();
	}

	public IRational multiply(IRational parm1);

	@Override
	public IRational negate();

	/**
	 * Return the normalized form of this number (i.e. if the denominator part equals one, return the numerator part as
	 * an integer number).
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
	 * <a href="https://en.wikipedia.org/wiki/Exponentiation_by_squaring">Wikipedia - Exponentiation by squaring</a>
	 * 
	 * @param exp
	 *            the exponent.
	 * @return <code>this<sup>exp</sup></code>
	 * @throws ArithmeticException
	 *             if {@code 0^0} is given.
	 */
	public IRational pow(final long exp) throws ArithmeticException;

	public IRational subtract(IRational parm1);

	/**
	 * Returns the denominator of this fraction.
	 * 
	 * @return denominator
	 */
	public BigInteger toBigDenominator();

	/**
	 * Returns this number as <code>BigFraction</code> number.
	 * 
	 * @return <code>this</code> number s big fraction.
	 */
	public BigFraction toBigFraction();

	/**
	 * Returns the numerator of this fraction.
	 * 
	 * @return denominator
	 */
	public BigInteger toBigNumerator();

}
