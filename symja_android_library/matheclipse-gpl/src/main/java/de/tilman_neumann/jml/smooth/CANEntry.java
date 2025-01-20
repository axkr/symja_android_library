/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2018-2024 Tilman Neumann - tilman.neumann@web.de
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program;
 * if not, see <http://www.gnu.org/licenses/>.
 */
package de.tilman_neumann.jml.smooth;

import static de.tilman_neumann.jml.base.BigIntConstants.*;

import java.math.BigInteger;
import java.util.ArrayList;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.tilman_neumann.jml.primes.probable.BPSWTest;

/**
 * A colossally abundant number (CAN), together with some information that was necessary to compute it.
 * @author Tilman Neumann
 */
public class CANEntry {
	private static final Logger LOG = LogManager.getLogger(CANEntry.class);
	private static final boolean DEBUG = false;

	private double epsilon;
	private BigInteger can;
	private ArrayList<BigInteger> primes;
	private ArrayList<Integer> exponents;
	/** the sum of prime factor exponents is the sequence element number of CANs */
	private int exponentSum;

	private static final BPSWTest bpsw = new BPSWTest();

	// private, use factory method computeCAN(epsilon)
	private CANEntry(double epsilon) {
		this.epsilon = epsilon;
		this.can = I_1;
		this.primes = new ArrayList<BigInteger>();
		this.exponents = new ArrayList<Integer>();
		this.exponentSum = 0;
	}

	private void add(BigInteger prime, int exponent) {
		primes.add(prime);
		exponents.add(exponent);
		exponentSum += exponent;
		can = can.multiply(prime.pow(exponent));
	}

	/**
	 * Compute exponent of prime p in CAN(epsilon).
	 * @param epsilon
	 * @param p
	 * @return exponent
	 */
	private static int computeExponent(double epsilon, BigInteger p) {
		double powTerm1 = Math.pow(p.doubleValue(), 1+epsilon);
		double logTerm1 = Math.log(powTerm1 - 1);
		double powTerm2 = Math.pow(p.doubleValue(), epsilon);
		double logTerm2 = Math.log(powTerm2 - 1);
		double logTerm3 = Math.log(p.doubleValue());
		double totalLogTerm = (logTerm1 - logTerm2) / logTerm3;
		int e = (int) Math.floor(totalLogTerm) - 1;
		return e;
	}

	/**
	 * Compute CAN(epsilon), where epsilon is a positive real number.
	 * @param epsilon
	 * @return CAN(epsilon)
	 */
	public static CANEntry computeCAN(double epsilon) {
		CANEntry result = new CANEntry(epsilon);
		BigInteger p = I_2;
		while (true) {
			int exponent = computeExponent(epsilon, p);
			if (exponent==0) break;
			if (DEBUG) LOG.debug("    epsilon=" + epsilon + ", p=" + p + ", exponent=" + exponent);
			result.add(p, exponent);
			p = bpsw.nextProbablePrime(p);
		}
		return result;
	}
	
	public double getEpsilon() {
		return epsilon;
	}
	
	public BigInteger getCAN() {
		return can;
	}
	
	public int getExponentSum() {
		return exponentSum;
	}
	
	public ArrayList<BigInteger> getPrimes() {
		return primes;
	}
	
	public ArrayList<Integer> getExponents() {
		return exponents;
	}
}
