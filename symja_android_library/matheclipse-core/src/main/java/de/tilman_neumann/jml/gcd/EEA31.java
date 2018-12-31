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
 * This int implementation is quite fast.
 * 
 * @author Tilman Neumann
 */
public class EEA31 {
	//private static final Logger LOG = Logger.getLogger(EEA31.class);

	public static class Result {
		/** if g==1 and y>0 then a = (1/x) mod y */
		public int a;
		/** if g==1 and y>0 then a = (1/y) mod x */
		public int b;
		/** gcd */
		public int g;
		
		public Result(int g, int a, int b) {
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
	public Result computeAll(int x, int y) {
		// initialize
		int a = 1;
		int b = 0;
		int g = x;
		int u = 0;
		int v = 1;
		int w = y;
		int parity = -1;

		// loop
		int q, tmp;
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
//		LOG.debug("sign of x = " + Integer.signum(x));

		if (Integer.signum(x)==parity) {
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
	public Result computeHalf(int x, int y) {
		// initialize
		int a = 1;
		int g = x;
		int u = 0;
		int w = y;
		int parity = -1;
		
		// loop
		int tmp, rem;
		while (w != 0) {
			rem = g % w;
			tmp = a - (g/w)*u; // floor(g/w)
			a = u; 
			u = tmp;
			g = w; 
			w = rem;
			parity = -parity;
		}
		
		if (Integer.signum(x)==parity) {
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
	public int modularInverse1(int x, int y) {
		// initialize
		int a = 1;
		int g = x;
		int u = 0;
		int w = y;
		int parity = -1;
		
		// loop
		int tmp, rem;
		while (w != 0) {
			rem = g % w;
			tmp = a - (g/w)*u; // floor(g/w)
			a = u; 
			u = tmp;
			g = w; 
			w = rem;
			parity = -parity;
		}
		
		if (Integer.signum(x)==parity) {
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
	public int modularInverse/*2*/(int a, int modulus) {
		int ps, ps1, ps2, parity, dividend, divisor, rem, q, sign, aPos;

		if (a == 1) return 1;
		
		// make argument positive, remember sign
		sign = Integer.signum(a);
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
