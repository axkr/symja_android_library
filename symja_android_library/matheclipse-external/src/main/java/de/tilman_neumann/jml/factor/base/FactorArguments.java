package de.tilman_neumann.jml.factor.base;

import java.math.BigInteger;

public class FactorArguments {
	/** The number to factor */
	public BigInteger N;
	
	/** The number of bits of N */
	public int NBits;

	/** The exponent of N */
	public int exp;
	
	/**
	 * Full constructor.
	 * @param N the number to factor
	 * @param exp the exponent of N
	 */
	public FactorArguments(BigInteger N, int exp) {
		this.N = N;
		this.NBits = N.bitLength();
		this.exp = exp;
	}
}
