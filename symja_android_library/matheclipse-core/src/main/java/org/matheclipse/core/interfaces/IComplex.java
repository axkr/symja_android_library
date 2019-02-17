package org.matheclipse.core.interfaces;

/**
 * An expression representing a complex number
 * 
 */
public interface IComplex extends IBigNumber {

	public IComplex add(IComplex val);

	/**
	 * Returns the imaginary part of a complex number
	 * 
	 * @return imaginary part
	 */
	public IRational getImaginaryPart();

	/**
	 * Returns the real part of a complex number
	 * 
	 * @return real part
	 */
	public IRational getRealPart();

	public IComplex inverse();
	
	public IComplex multiply(IComplex val);

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
	public IComplex pow(long n);

	/**
	 * Return the normalized form of this number (i.e. if the imaginary part equals zero, return the real part as a fractional or
	 * integer number).
	 * 
	 * @return
	 */
	public INumber normalize();
}
