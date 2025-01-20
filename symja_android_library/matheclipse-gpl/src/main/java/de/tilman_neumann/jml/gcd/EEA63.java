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
package de.tilman_neumann.jml.gcd;

import java.math.BigInteger;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Extended Euclidean algorithm, mostly used to compute the modular inverse of x (mod y).
 * Extends [Crandall/Pomerance 2005: "Prime numbers", algorithm 2.1.4]
 * to let modInverse(x, y) work on negative x, too.
 * 
 * Long version.
 * 
 * @author Tilman Neumann
 */
public class EEA63 {
	private static final Logger LOG = LogManager.getLogger(EEA63.class);
	private static final boolean DEBUG = false;
	
	public static class Result {
		/** gcd */
		public long g;
		/** if g==1 then a = (1/x) mod y */
		public long a;
		/** if g==1 then b = (1/y) mod x */
		public long b;
		
		public Result(long g, long a, long b) {
			this.g = g;
			this.a = a;
			this.b = b;
		}
		
		public String toString() {
			return "{" + g + ", " + a + ", " + b + "}";
		}
	}
	
	/**
	 * Computes gcd, a = (1/x) mod y and b = (1/y) mod x.
	 * @param x
	 * @param y
	 * @return {gcd, a, b}
	 */
	public Result computeAll(long x, long y) {
		// initialize
		long a = 1;
		long b = 0;
		long g = x;
		long u = 0;
		long v = 1;
		long w = y;
		int parity = -1;

		// loop
		long q, tmp;
		while(w != 0) {
			q = g/w; // floor
			// update
			tmp = a - q*u; a=u; u=tmp;
			tmp = b - q*v; b=v; v=tmp;
			tmp = g - q*w; g=w; w=tmp;
			parity = -parity;
		}
		
		if (DEBUG) {
			LOG.debug("correctResult = " + BigInteger.valueOf(x).modInverse(BigInteger.valueOf(y)));
			LOG.debug("a = " + a);
			LOG.debug("y-a = " + (y-a));
			LOG.debug("y+a = " + (y+a));
			LOG.debug("parity = " + parity);
			LOG.debug("sign of x = " + Long.signum(x));
		}
		
		if (Long.signum(x)==parity) {
			a = (parity==1) ? y+a : y-a; // XXX It seems that b does not depend on parity?
		}
		return new Result(g, a, b);
	}
	
	/**
	 * Computes only gcd and a = (1/x) mod y.
	 * @param x
	 * @param y
	 * @return {gcd, a, b=-1 (undefined)}
	 */
	public Result computeHalf(long x, long y) {
		// initialize
		long a = 1;
		long g = x;
		long u = 0;
		long w = y;
		int parity = -1;
		
		// loop
		long tmp, rem;
		while (w != 0) {
			rem = g % w;
			tmp = a - (g/w)*u; // floor(g/w)
			a = u; 
			u = tmp;
			g = w; 
			w = rem;
			parity = -parity;
		}
		
		if (Long.signum(x)==parity) {
			a = (parity==1) ? y+a : y-a;
		}
		return new Result(g, a, -1);
	}
	
	/**
	 * Computes only the modular inverse a = (1/x) mod y.
	 * @param x
	 * @param y
	 * @return (1/x) mod y
	 */
	public long modularInverse_v1(long x, long y) {
		// initialize
		long a = 1;
		long g = x;
		long u = 0;
		long w = y;
		int parity = -1;
		
		// loop
		long tmp, rem;
		while (w != 0) {
			rem = g % w;
			tmp = a - (g/w)*u; // floor(g/w)
			a = u; 
			u = tmp;
			g = w; 
			w = rem;
			parity = -parity;
		}
		
		if (Long.signum(x)==parity) {
			a = (parity==1) ? y+a : y-a;
		}
		return a;
	}
	
	/**
	 * Slightly faster modular inverse initializing variables with first iteration
	 * and needing one step less at the end.
	 * 
	 * Adapted from R.D.Silverman's single_modinv2(),
	 * see http://www.mersenneforum.org/showthread.php?t=12963&page=2, post #16.
	 *
	 * @param a
	 * @param modulus
	 * @return (1/a) mod modulus
	 */
	public long modularInverse_v2(long a, long modulus) {
		long ps, ps1, ps2, dividend, divisor, rem, q, aPos;
		int parity, sign;
		
		if (a == 1) return 1;
		
		// make argument positive, remember sign
		sign = Long.signum(a);
		aPos = (a<0) ? -a : a;
		
		// initialize variables with first iteration
		q = modulus / aPos;
		rem = modulus % aPos;
		dividend = aPos;
		divisor = rem;
		ps1 = q;
		ps2 = 1;
		parity = 1;

		while (divisor > 1) {
			q = dividend / divisor;
			rem = dividend % divisor; // faster than rem = dividend - q * divisor
			ps = q*ps1 + ps2;
			ps2 = ps1;
			ps1 = ps;
			dividend = divisor;
			divisor = rem;
			parity = -parity;
		}
		
		return (sign==parity) ? modulus-ps1 : ps1;
	}

	/**
	 * Compute the modular inverse x of a mod p, i.e. x = (1/a) mod p.
	 * 
	 * Taken from Ben Buhrow's tinyecm.c and modified to work for a<0 and a>=p, too.
	 * Optimal performance is achieved by 6 "if's" in Java, compared to 9 "if's" in C.
	 * 
	 * Significantly faster than the versions above.
	 * 
	 * @param a
	 * @param p modulus
	 * @return (1/a) mod p
	 */
	public long modularInverse/*_v3*/(long a, long p) {

		/* thanks to the folks at www.mersenneforum.org */

		long ps1, ps2, parity, dividend, divisor, rem, q, t, sign, aPos;
		
		// make argument positive, remember sign
		if (a<0) {
			aPos = -a;
			sign = -1;
		} else {
			aPos = a;
			sign = 1;
		}
		
		if (p < aPos) {
			q = 0;
			ps1 = 0;
			ps2 = 1;
			rem = p;
			dividend = aPos;
			divisor = p;
			parity = 1;
		} else {
			q = 1;
			ps1 = 1;
			ps2 = 0;
			rem = aPos;
			dividend = p;
			divisor = aPos;
			parity = -1;
		}
		
		while (divisor > 1) {
			rem = dividend - divisor;
			t = rem - divisor;
			if (rem >= divisor) {
				q += ps1; rem = t; t -= divisor;
				if (rem >= divisor) {
					q += ps1; rem = t; t -= divisor;
					if (rem >= divisor) {
						q += ps1; rem = t; t -= divisor;
						if (rem >= divisor) {
							q += ps1; rem = t; t -= divisor;
							if (rem >= divisor) {
								q += ps1; rem = t;
								if (rem >= divisor) {
									q = dividend / divisor;
									rem = dividend % divisor;
									q *= ps1;
								}
							}
						}
					}
				}
			}

			q += ps2;
			parity = -parity;
			dividend = divisor;
			divisor = rem;
			ps2 = ps1;
			ps1 = q;
		}

		return (sign==parity) ? p-ps1 : ps1;
	}
}
