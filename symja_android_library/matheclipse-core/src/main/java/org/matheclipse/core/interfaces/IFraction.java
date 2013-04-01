package org.matheclipse.core.interfaces;


import java.math.BigInteger;

import org.apache.commons.math3.fraction.BigFraction;
/**
 * interface for "fractional" numbers
 * 
 */
public interface IFraction extends IRational {

	 public IFraction add(IFraction parm1);
	//
	// public IFraction divide(IFraction parm1);

	public BigFraction getRational();

	/**
	 * Returns the denominator of this fraction.
	 * 
	 * @return denominator
	 */
	public BigInteger getBigDenominator();

	/**
	 * Returns the numerator of this fraction.
	 * 
	 * @return denominator
	 */
	public BigInteger getBigNumerator();

	 public IFraction multiply(IFraction parm1);
	//
	// public IFraction subtract(IFraction parm1);
	 
	 public IFraction pow(final int exp);
}
