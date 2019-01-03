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
package de.tilman_neumann.jml.modular;

import java.math.BigInteger;

//import org.apache.log4j.Logger;
//
/**
 * Compute modular sqrts t with t^2 == n (mod p) and u with u^2 == n (mod p^e) using Tonelli-Shanks' algorithm.
 * @author Tilman Neumann
 */
public class ModularSqrt {
//	private static final Logger LOG = Logger.getLogger(ModularSqrt.class);
//	private static final boolean DEBUG = false;

	private ModularPower mpe = new ModularPower();
	private JacobiSymbol jacobiEngine = new JacobiSymbol();

	/**
	 * Compute the modular sqrt t with t^2 == n (mod p).
	 * Uses Tonelli-Shanks algorithm for p==1 (mod 8), Lagrange's formula for p==3, 7 (mod 8) and another special formula for p==5 (mod 8).
	 * 
	 * @param n a positive integer having Jacobi(n|p) = 1
	 * @param p odd prime
	 * @return the modular sqrt t
	 */
	public int modularSqrt(BigInteger n, int p) {
//		if (DEBUG) {
//			BigInteger p_big = BigInteger.valueOf(p);
//			assertTrue(p%2==1 && p_big.isProbablePrime(20)); // p odd prime
//			// Tonelli_Shanks requires Legendre(n|p)==1, 0 is not ok. But this is easy to "heal":
//			// Since p is prime, Legendre(n|p)==0 means that n is a multiple of p.
//			// Thus n mod p == 0 and the square of this is 0, too.
//			// So if the following assert fails, just test n mod p == 0 before calling this method.
//			assertTrue(jacobiEngine.jacobiSymbol(n, p)==1);
//		}
		int pMod8 = p&7;
		switch (pMod8) {
		case 1: 		
			return Tonelli_Shanks(n, p);
		case 3: case 7: 
			return Lagrange(n, p);
		case 5:			
			return case5Mod8(n, p);
		default:
//			LOG.warn("Tonelli_Shanks() has been called with even p=" + p + " -> brute force search required.");
			return bruteForce(n, p);
		}
	}
	
	/**
	 * Tonelli-Shanks algorithm for modular sqrt R^2 == n (mod p),
	 * following <link>en.wikipedia.org/wiki/Tonelli-Shanks_algorithm</link>.
	 * 
	 * This algorithm works for odd primes <code>p</code> and <code>n</code> with <code>Legendre(n|p)=1</code>,
	 * but usually it is applied only to p with p==1 (mod 8), because for p == 3, 5, 7 (mod 8) there are simpler and faster formulas.
	 * 
	 * @param n a positive integer having Jacobi(n|p) = 1
	 * @param p odd prime
	 * @return the modular sqrt t
	 */
	private int Tonelli_Shanks(BigInteger n, int p) {
		// factor out powers of 2 from p-1, defining Q and S as p-1 = Q*2^S with Q odd.
		int pm1 = p-1;
		int S = Integer.numberOfTrailingZeros(pm1); // lowest set bit (0 if pm1 were odd which is impossible because p is odd)
//		if (DEBUG) {
//			LOG.debug("n=" + n + ", p=" + p);
//			assertEquals(1, jacobiEngine.jacobiSymbol(n, p));
//			assertTrue(S > 1); // S=1 is the Lagrange case p == 3 (mod 4), but we check it nonetheless.
//		}
		int Q = pm1>>S;
		// find some z with Legendre(z|p)==-1, i.e. z being a quadratic non-residue (mod p)
		int z;
		for (z=2; ; z++) {
			if (jacobiEngine.jacobiSymbol(z, p) == -1) break;
		}
		// now z is found -> set c == z^Q
		int c = mpe.modPow(z, Q, p);
		int R = mpe.modPow(n, (Q+1)>>1, p);
		int t = mpe.modPow(n, Q, p);
		int M = S;
		while (t!=1) { // if t=1 then R is the result
			// find the smallest i, 0<i<M, such that t^(2^i) == 1 (mod p)
//			if (DEBUG) {
//				LOG.debug("Find i < M=" + M + " with t=" + t);
//				// test invariants from <link>https://en.wikipedia.org/wiki/Tonelli%E2%80%93Shanks_algorithm#Proof</link>:
//				assertEquals(p-1, mpe.modPow(c, 1<<(M-1), p)); //  -1 == c^(2^(M-1)) (mod p)
//				assertEquals(1, mpe.modPow(t, 1<<(M-1), p));   //   1 == t^(2^(M-1)) (mod p)
//				long nModP = n.mod(BigInteger.valueOf(p)).longValue();
//				assertEquals((R*(long)R) % p, (t*nModP) % p);  // R^2 == t*n (mod p)
//			}
			boolean foundI = false;
			int i;
			for (i=1; i<M; i++) {
				if (mpe.modPow(t, 1<<i, p)==1) { // t^(2^i) == 1 (mod p) ?
					foundI = true;
					break;
				}
			}
			if (foundI==false) throw new IllegalStateException("Tonelli-Shanks did not find an 'i' < M=" + M);
			
			int b = mpe.modPow(c, 1<<(M-i-1), p); // c^(2^(M-i-1))
			R = (int) ((R*(long)b) % p);
			c = (int) ((b*(long)b) % p);
			t = (int) ((t*(long)c) % p);
			M = i;
		}
//		if (DEBUG) assertEquals(BigInteger.valueOf(R).pow(2).mod(BigInteger.valueOf(p)), n.mod(BigInteger.valueOf(p)));
		// return the smaller sqrt
		return R <= (p>>1) ? R : p-R;
	}

	/**
	 * Lagrange's formula for p == 3 (mod 4): t = n^k % p, k = (p+1)/4.
	 * @param n a positive integer having Jacobi(n|p) = 1
	 * @param p
	 * @return sqrt of a modulo p
	 */
	private int Lagrange(BigInteger n, int p) {
		int t = mpe.modPow(n, (p+1)>>2, p);
//		if (DEBUG) assertEquals(BigInteger.valueOf(t).pow(2).mod(BigInteger.valueOf(p)), n.mod(BigInteger.valueOf(p)));
		// return the smaller sqrt
		return t <= (p>>1) ? t : p-t;
	}
	
	/**
	 * The simpler formula for case p == 5 (mod 8), following <link>www.point-at-infinity.org/ecc/Algorithm_of_Shanks_&_Tonelli.html</link>.
	 * @param n a positive integer having Jacobi(n|p) = 1
	 * @param p
	 * @return
	 */
	private int case5Mod8(BigInteger n, int p) {
		int k = p>>3; // for p == 5 (mod 8) we need k = (p-5)/8 = p>>3
		BigInteger n2 = n.shiftLeft(1);
		int g = mpe.modPow(n2, k, p);
		BigInteger gSquare = BigInteger.valueOf(g*(long)g);
		BigInteger p_big = BigInteger.valueOf(p);
		int i = n2.multiply(gSquare).mod(p_big).intValue();
		int t = n.multiply(BigInteger.valueOf(g*(long)(i-1))).mod(p_big).intValue();
//		if (DEBUG) assertEquals(BigInteger.valueOf(t).pow(2).mod(p_big), n.mod(p_big));
		// return the smaller sqrt
		return t <= (p>>1) ? t : p-t;
	}

	/**
	 * Brute force implementation: Test all t from 0 to (p-1)/2.
	 * @param n a positive integer having Jacobi(n|p) = 1
	 * @param p
	 * @return 
	 */
	private int bruteForce(BigInteger n, int p) {
		//boolean foundT = false;
		BigInteger p_big = BigInteger.valueOf(p);
		int nModP = n.mod(p_big).intValue();
		int t;
		int tMax = (p-1)>>1; // (p-1)/2
		for (t=0; t<=tMax; t++) {
			long tt = t * (long)t;
			int ttModP = (int) (tt % p);
			if (ttModP == nModP) {
				// found t with t^2 == n (mod p)
				//LOG.debug("n=" + n + " == t^2 = " + t + "^2 (mod " + p + ")");
				//foundT = true;
				break;
			}
		}
		//if (!foundT) LOG.error("ERROR: Failed to find t with t^2==" + n + " (mod " + p + ") !");
//		if (DEBUG) assertEquals(BigInteger.valueOf(t).pow(2).mod(p_big), n.mod(p_big));
		// return the smaller sqrt
		return t <= (p>>1) ? t : p-t;
	}
	
	/**
	 * General solution of u^2 == n (mod p^exponent), well explained in
	 * [http://mathoverflow.net/questions/52081/is-there-an-efficient-algorithm-for-finding-a-square-root-modulo-a-prime-power, Gottfried Barthel].
	 * Works for any odd p with t^2 == n (mod p) having solution t>0.
	 * Implementation for arguments having int solutions.
	 * 
	 * @param n
	 * @param power p^exponent
	 * @param last_power p^(exponent-1)
	 * @param t solution of t^2 == n (mod p)
	 * 
	 * @return sqrt of n (mod p^exponent)
	 */
	public int modularSqrtModPower(BigInteger n, int power, int last_power, int t) {
		// Barthel's e = (power - 2*last_power + 1)/2
		int f = (power - (last_power<<1) + 1)>>1;
		// square root == n (mod p^exponent)
		int u = (int) (mpe.modPow(t, last_power, power) * (long)mpe.modPow(n, f, power) % power);
		// return the smaller sqrt
		return u <= (power>>1) ? u : power-u;
	}
}
