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
package de.tilman_neumann.jml.gcd;

import java.util.Collection;
import java.util.Iterator;

/**
 * GCD implementations for 32-bit integers.
 * 
 * The binary gcd is much faster than the Euclidean, and also faster than the built-in BigInteger gcd().
 * 
 * @author Tilman Neumann
 */
public class Gcd31 {

	/**
	 * Greatest common divisor of the given two arguments.
	 * Euclid's algorithm implementation with division.
	 * @param m
	 * @param n
	 * @return
	 */
	// much slower than binary gcd !
	public int gcd_euclid_withDivision(int m, int n) {
		m = Math.abs(m);
		n = Math.abs(n);
		int mCmp1 = m-1;
		int nCmp1 = n-1;
		if (mCmp1>0 && nCmp1>0) {
			// initialize
			int g; // greater argument
			int s; // smaller argument
			if (m>n) {
				g = m;
				s = n;
			} else {
				g = n;
				s = m;
			}
			// iterate
			while (s>0) {
				int dv = g/s;
				int rem = g-dv*s;
				g = s;
				s = rem; // remainder
			}
			return g;
		}
		if (mCmp1<0) return n;
		if (nCmp1<0) return m;
		// else one argument is 1
		return 1;
	}
	
	/**
	 * Binary gcd implementation.
	 * @param m
	 * @param n
	 * @return gcd(m, n)
	 */
	public int gcd_binary1(int m, int n) {
		m = Math.abs(m);
		n = Math.abs(n);
		int mCmp1 = m-1;
		int nCmp1 = n-1;
		if (mCmp1>0 && nCmp1>0) {
			int m_lsb = Long.numberOfTrailingZeros(m);
			int n_lsb = Long.numberOfTrailingZeros(n);
			int shifts = Math.min(m_lsb, n_lsb);
			m = m>>m_lsb;
			n = n>>n_lsb;
			// now m and n are odd
			//LOG.debug("m=" + m + ", n=" + n + ", g=" + g);
			while (m > 0) {
				int t = (m-n)>>1;
		        if (t<0) {
		        	n = -t;
		        	n = n>>Long.numberOfTrailingZeros(n);
		        } else {
		        	m = t>>Long.numberOfTrailingZeros(t);
		        }
				//LOG.debug("m=" + m + ", n=" + n);
			}
			int gcd = n<<shifts;
			//LOG.debug("gcd=" + gcd);
			return gcd;
		}
		if (mCmp1<0) return n;
		if (nCmp1<0) return m;
		// else one argument is 1
		return 1;
	}

	/**
	 * Faster binary gcd adapted from OpenJdk's MutableBigInteger.binaryGcd(int, int).
	 * @param a
	 * @param b
	 * @return
	 */
	public int gcd/*_binary2*/(int a, int b) {
		a = Math.abs(a);
		if (b == 0) return a;
		b = Math.abs(b);
		if (a == 0) return b;

		// Right shift a & b till their last bits equal to 1.
		final int aZeros = Integer.numberOfTrailingZeros(a);
		final int bZeros = Integer.numberOfTrailingZeros(b);
		a >>>= aZeros;
		b >>>= bZeros;

		final int t = (aZeros < bZeros ? aZeros : bZeros);

		while (a != b) {
			if ((a+0x80000000) > (b+0x80000000)) { // a > b as unsigned
				a -= b;
				a >>>= Integer.numberOfTrailingZeros(a);
			} else {
				b -= a;
				b >>>= Integer.numberOfTrailingZeros(b);
			}
		}
		return a<<t;
	}

	/**
	 * GCD of all arguments.
	 * @param arguments
	 * @return
	 */
	public Integer gcd(Collection<Integer> arguments) {
		if (arguments==null || arguments.size()==0) { 
			return null;
		}

		Iterator<Integer> itr = arguments.iterator();
		int ret = itr.next();
		while(itr.hasNext()) {
			ret = gcd(ret, itr.next()); // fastest gcd
		}
		return ret;
	}
}
