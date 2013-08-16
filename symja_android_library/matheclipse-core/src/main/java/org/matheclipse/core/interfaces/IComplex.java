package org.matheclipse.core.interfaces;

import org.apache.commons.math3.fraction.BigFraction;

/**
 * An expression representing a complex number
 * 
 */
public interface IComplex extends IBigNumber {
	public IComplex conjugate();

	public IComplex add(IComplex val);

	/**
	 * Returns the imaginary part of a complex number
	 * 
	 * @return imaginary part
	 */
	public BigFraction getImaginaryPart();
	
	/**
	 * Returns the real part of a complex number
	 * 
	 * @return real part
	 */
	public BigFraction getRealPart();
	
	public IComplex multiply(IComplex val);

	public IComplex pow(int parm1);

	public INumber normalize();
}
