/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2018 Tilman Neumann (www.tilman-neumann.de)
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
package de.tilman_neumann.jml.factor.base.matrixSolver;

import java.math.BigInteger;
import java.util.Set;

//import org.apache.log4j.Logger;

import de.tilman_neumann.jml.factor.FactorException;
import de.tilman_neumann.jml.factor.base.congruence.AQPair;
import de.tilman_neumann.util.SortedMultiset;
import de.tilman_neumann.util.SortedMultiset_BottomUp;

import static de.tilman_neumann.jml.base.BigIntConstants.*;
/**
 * Factor test using modular reduction (mod N).
 * 
 * @author Tilman Neumann
 */
public class FactorTest01 implements FactorTest {
//	private static final Logger LOG = Logger.getLogger(FactorTest01.class);
//	private static final boolean DEBUG = false;

	private BigInteger N;
	
	public FactorTest01(BigInteger N) {
		this.N = N;
	}
	
	/* (non-Javadoc)
	 * @see de.tilman_neumann.math.factor._basics.FactorTest#getName()
	 */
	@Override
	public String getName() {
		return "FactorTest01(" + N + ")";
	}

	/**
	 * Test if a square congruence A^2 == Q (mod kN) gives a factor of N.
	 * 
	 * Reducing both A and sqrt(Q) (mod N) means a great speed gain. Otherwise these products can become huge, 
	 * like a 1.000.000 bit number for N having 250 bit.
	 *
	 * @param aqPairs
	 * @throws FactorException
	 */
	@Override
	public void testForFactor(Set<AQPair> aqPairs) throws FactorException {
		// Collect Q-factors from all AQPairs
		SortedMultiset<Integer> totalQ_factors = new SortedMultiset_BottomUp<Integer>();
		for (AQPair aqPair : aqPairs) {
			totalQ_factors.addAll(aqPair.getAllQFactors());
		}
		// Compose totalQSqrt from the prime factorizations of the involved Q's;
		// BlockLanczos returns non-square "solutions", too, so we test on the fly if Q is really a square.
		long t0 = System.currentTimeMillis();
		BigInteger totalQSqrt = I_1;
		for (int factor : totalQ_factors.keySet()) {
			if (factor == -1) continue; // sqrt(Q-product) can be positive or negative, but we want the positive solution -> skip sign
			int exp = totalQ_factors.get(factor);
			if ((exp&1) == 1) {
				// non-square "solution" -> early exit
//				if (DEBUG) LOG.debug("factor = " + factor + ", exp = " + exp);
				return;
			}
			int halfExp = exp>>1;
			totalQSqrt = totalQSqrt.multiply(BigInteger.valueOf(factor).pow(halfExp)).mod(N); // reduction (mod N) is much faster
		}
		// Compute product of all A (mod N)
		BigInteger AProd = I_1;
		for (AQPair aqPair : aqPairs) {
			AProd = AProd.multiply(aqPair.getA()).mod(N); // reduction (mod N) is much faster
		}
		
//		if (DEBUG) {
//			long compositionDuration = System.currentTimeMillis() - t0;
//			LOG.debug("N = " + N + ": testForFactor(): A=" + getAString(aqPairs) + "=" + AProd + ", Q=" + getQString(aqPairs) + ", sqrt(Q)=" + totalQSqrt);
//			// verify congruence A^2 == Q (mod N)
//			BigInteger totalQ = totalQSqrt.multiply(totalQSqrt);
//			BigInteger div[] = AProd.pow(2).subtract(totalQ).divideAndRemainder(N);
//			assertEquals(I_0, div[1]);
//			LOG.debug("A^2-Q = " + div[0] + " * N");
//			LOG.debug("A^2 % N = " + AProd.pow(2).mod(N) + ", Q = " + totalQ);
//		}

		// test A-sqrt(Q)
		BigInteger minusGcd = AProd.subtract(totalQSqrt).gcd(N);
//		if (DEBUG) LOG.debug("minusGcd = " + minusGcd);
		if (minusGcd.compareTo(I_1)>0 && minusGcd.compareTo(N)<0) throw new FactorException(minusGcd); // factor!
		// test A+sqrt(Q)
		BigInteger plusGcd = AProd.add(totalQSqrt).gcd(N);
//		if (DEBUG) LOG.debug("plusGcd = " + plusGcd);
		if (plusGcd.compareTo(I_1)>0 && plusGcd.compareTo(N)<0) throw new FactorException(plusGcd); // factor!
		// no factor exception -> no success
	}
	
	private String getAString(Set<AQPair> aqPairs) {
		String str = "";
		for (AQPair aqPair : aqPairs) {
			str += aqPair.getA() + "*";
		}
		return str.substring(0, str.length()-1); // remove last "*"
	}
	
	private String getQString(Set<AQPair> aqPairs) {
		String str = "";
		for (AQPair aqPair : aqPairs) {
			str += aqPair.getAllQFactors() + "*";
		}
		return str.substring(0, str.length()-1); // remove last "*"
	}
}
