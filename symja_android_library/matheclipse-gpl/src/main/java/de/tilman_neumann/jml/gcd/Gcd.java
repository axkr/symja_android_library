/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2018 Tilman Neumann - tilman.neumann@web.de
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
package de.tilman_neumann.jml.gcd;

import static de.tilman_neumann.jml.base.BigIntConstants.*;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;

/**
 * GCD implementations for BigIntegers.
 * 
 * By far the fastest implementation is BigInteger's gcd(), which uses a hybrid gcd with a very fast binary gcd implementation.
 * 
 * @author Tilman Neumann
 */
public class Gcd {

	/**
	 * Greatest common divisor of the given two arguments.
	 * Euclid's algorithm implementation with division.
	 * @param m
	 * @param n
	 * @return gcd(m, n)
	 */
	public BigInteger gcd_euclid_withDivision(BigInteger m, BigInteger n) {
		m = m.abs();
		n = n.abs();
		int mCmp1 = m.compareTo(I_1);
		int nCmp1 = n.compareTo(I_1);
		if (mCmp1>0 && nCmp1>0) {
			// initialize
			BigInteger g; // greater argument
			BigInteger s; // smaller argument
			if (m.compareTo(n) >= 0) {
				g = m;
				s = n;
			} else {
				g = n;
				s = m;
			}
			// iterate
			while (s.compareTo(I_0)>0) {
				BigInteger[] dv = g.divideAndRemainder(s);
				g = s;
				s = dv[1]; // remainder
			}
			return g;
		}
		if (mCmp1<0) return n;
		if (nCmp1<0) return m;
		// else one argument is 1
		return I_1;
	}
	
	// very slow for arguments that are not very small
	public BigInteger gcd_euclid_withoutDivision(BigInteger m, BigInteger n) {
		m = m.abs();
		n = n.abs();
		int mCmp1 = m.compareTo(I_1);
		int nCmp1 = n.compareTo(I_1);
		if (mCmp1>0 && nCmp1>0) {
			int cmp = m.compareTo(n);
			while (cmp != 0) {
				//LOG.debug("m = " + m + ", n = " + n + ", cmp = " + cmp);
				if (cmp > 0) {
					m = m.subtract(n);
				} else {
					n = n.subtract(m);
				}
				cmp = m.compareTo(n);
			}
			return m;
		}
		if (mCmp1<0) return n;
		if (nCmp1<0) return m;
		// else one argument is 1
		return I_1;
	}
	
	/**
	 * Binary gcd implementation.
	 * @param m
	 * @param n
	 * @return gcd(m, n)
	 */
	public BigInteger gcd_binary1(BigInteger m, BigInteger n) {
		m = m.abs();
		n = n.abs();
		int mCmp1 = m.compareTo(I_1);
		int nCmp1 = n.compareTo(I_1);
		if (mCmp1>0 && nCmp1>0) {
			int m_lsb = m.getLowestSetBit();
			int n_lsb = n.getLowestSetBit();
			int shifts = Math.min(m_lsb, n_lsb);
			m = m.shiftRight(m_lsb);
			n = n.shiftRight(n_lsb);
			// now m and n are odd
			//LOG.debug("m=" + m + ", n=" + n + ", g=" + g);
			while (m.signum() > 0) {
		    	BigInteger t = m.subtract(n).shiftRight(1);
		        if (t.signum()<0) {
		        	t = t.negate();
		        	n = t.shiftRight(t.getLowestSetBit());
		        } else {
		        	m = t.shiftRight(t.getLowestSetBit());
		        }
				//LOG.debug("m=" + m + ", n=" + n);
			}
			BigInteger gcd = n.shiftLeft(shifts);
			//LOG.debug("gcd=" + gcd);
			return gcd;
		}
		if (mCmp1<0) return n;
		if (nCmp1<0) return m;
		// else one argument is 1
		return I_1;
	}

	/**
	 * Slightly faster binary gcd adapted from OpenJdk's MutableBigInteger.binaryGcd(int, int).
	 * The BigInteger.gcd() implementation is still much faster.
	 * 
	 * @param a
	 * @param b
	 * @return gcd(a, b)
	 */
	public BigInteger gcd/*_binary2*/(BigInteger a, BigInteger b) {
		a = a.abs();
		if (b.equals(I_0)) return a;
		b = b.abs();
		if (a.equals(I_0)) return b;

		// Right shift a & b till their last bits equal to 1.
		final int aZeros = a.getLowestSetBit();
		final int bZeros = b.getLowestSetBit();
		a = a.shiftRight(aZeros);
		b = b.shiftRight(bZeros);

		final int t = (aZeros < bZeros ? aZeros : bZeros);

		int cmp;
		while ((cmp = a.compareTo(b)) != 0) {
			if (cmp > 0) {
				a = a.subtract(b);
				a = a.shiftRight(a.getLowestSetBit());
			} else {
				b = b.subtract(a);
				b = b.shiftRight(b.getLowestSetBit());
			}
		}
		return a.shiftLeft(t);
	}

	/**
	 * GCD of k arguments n1, n2, ..., nk.
	 * @param arguments Collection of arguments
	 * @return GCD(n1, n2, ..., nk)
	 */
	public static BigInteger gcd(Collection<BigInteger> arguments) {
		if (arguments==null || arguments.size()==0) { 
			return null;
		}

		Iterator<BigInteger> itr = arguments.iterator();
		BigInteger ret = itr.next();
		while(itr.hasNext()) {
			if (ret.equals(I_1)) return I_1; // gcd(1, n) == 1, no further change possible (thx to Axel Kramer)
			ret = ret.gcd(itr.next()); // fastest BigInteger gcd
		}
		return ret;
	}

	/**
	 * GCD of k arguments n1, n2, ..., nk.
	 * @param arguments array of arguments
	 * @return GCD(n1, n2, ..., nk)
	 */
	public static BigInteger gcd(BigInteger[] arguments) {
		if (arguments==null || arguments.length==0) { 
			return null;
		}

		BigInteger ret = arguments[0];
		for(int i=1; i<arguments.length; i++) {
			if (ret.equals(I_1)) return I_1; // gcd(1, n) == 1, no further change possible (thx to Axel Kramer)
			ret = ret.gcd(arguments[i]);
		}
		return ret;
	}

	/**
	 * Least common multiple of two arguments.
	 * @param a
	 * @param b
	 * @return LCM(a, b)
	 */
	public static BigInteger lcm(BigInteger a, BigInteger b) {
		if (a.equals(I_0) || b.equals(I_0)) return I_0;
		BigInteger ab = a.multiply(b);
		BigInteger gcd = a.gcd(b);
		return gcd.equals(I_1) ? ab : ab.divide(gcd);
	}
	
	/**
	 * Least common multiple of k arguments n1, n2, ..., nk.
	 * @param arguments Collection of arguments
	 * @return LCM(n1, n2, ..., nk)
	 */
	public static BigInteger lcm(Collection<BigInteger> arguments) {
		if (arguments==null || arguments.size()==0) { 
			return null;
		}

		Iterator<BigInteger> itr = arguments.iterator();
		BigInteger ret = itr.next();
		while(itr.hasNext()) {
			ret = lcm(ret, itr.next());
		}
		return ret;
	}
	
	/**
	 * Least common multiple of k arguments n1, n2, ..., nk.
	 * @param arguments array of arguments
	 * @return LCM(n1, n2, ..., nk)
	 */
	public static BigInteger lcm(BigInteger[] arguments) {
		if (arguments==null || arguments.length==0) { 
			return null;
		}

		BigInteger ret = arguments[0];
		for(int i=1; i<arguments.length; i++) {
			ret = lcm(ret, arguments[i]);
		}
		return ret;
	}
}
