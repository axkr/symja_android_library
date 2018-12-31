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

//import java.math.BigInteger;
//import org.apache.log4j.Logger;

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
	//private static final Logger LOG = Logger.getLogger(EEA63.class);

	public static class Result {
		/** if g==1 and y>0 then a = (1/x) mod y */
		public long a;
		/** if g==1 and y>0 then a = (1/y) mod x */
		public long b;
		/** gcd */
		public long g;
		
		public Result(long g, long a, long b) {
			this.a = a;
			this.b = b;
			this.g = g;
		}
	}
	
	/**
	 * Computes gcd, a = (1/x) mod y and b = (1/y) mod x.
	 * @param x
	 * @param y
	 * @return
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
		
//		LOG.debug("correctResult = " + BigInteger.valueOf(x).modInverse(BigInteger.valueOf(y)));
//		LOG.debug("a = " + a);
//		LOG.debug("y-a = " + (y-a));
//		LOG.debug("y+a = " + (y+a));
//		LOG.debug("parity = " + parity);
//		LOG.debug("sign of x = " + Long.signum(x));

		if (Long.signum(x)==parity) {
			a = (parity==1) ? y+a : y-a; // TODO: What about b?
		}
		return new Result(g, a, b);
	}
	
	/**
	 * Computes only gcd and a = (1/x) mod y.
	 * @param x
	 * @param y
	 * @return
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
	public long modularInverse1(long x, long y) {
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
	 * @return
	 */
	public long modularInverse/*2*/(long a, long modulus) {
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
}
