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

import de.tilman_neumann.jml.modular.JacobiSymbol;
import de.tilman_neumann.jml.modular.ModularPower;

import static de.tilman_neumann.jml.base.BigIntConstants.*;

/**
 * Compute modular sqrts t with t^2 == n (mod p) and u with u^2 == n (mod p^e) using Tonelli-Shanks' algorithm.
 * Implementation for all-BigInteger arguments.
 * 
 * @author Tilman Neumann
 */
public class ModularSqrt_BB {
//	private static final Logger LOG = Logger.getLogger(ModularSqrt_BB.class);
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
	public BigInteger modularSqrt(BigInteger n, BigInteger p) {
//		if (DEBUG) {
//			assertTrue((p.intValue() & 1) == 1 && p.isProbablePrime(20)); // p odd prime
//			// Tonelli_Shanks requires Legendre(n|p)==1, 0 is not ok. But this is easy to "heal":
//			// Since p is prime, Legendre(n|p)==0 means that n is a multiple of p.
//			// Thus n mod p == 0 and the square of this is 0, too.
//			// So if the following assert fails, just test n mod p == 0 before calling this method.
//			assertTrue(jacobiEngine.jacobiSymbol(n, p)==1);
//		}
		int pMod8 = p.intValue() & 7;
		switch (pMod8) {
		case 1: 		
			return Tonelli_Shanks(n, p);
		case 3: case 7: 
			return Lagrange(n, p);
		case 5:			
			return case5Mod8(n, p);
		default:
			throw new IllegalStateException("Tonelli_Shanks() has been called with even p=" + p + " -> brute force search would be hardy a choice for BigInteger arguments.");
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
	private BigInteger Tonelli_Shanks(BigInteger n, BigInteger p) {
		// factor out powers of 2 from p-1, defining Q and S as p-1 = Q*2^S with Q odd.
		BigInteger pm1 = p.subtract(I_1);
		int S = pm1.getLowestSetBit(); // lowest set bit (0 if pm1 were odd which is impossible because p is odd)
//		if (DEBUG) {
//			LOG.debug("n=" + n + ", p=" + p);
//			assertEquals(1, jacobiEngine.jacobiSymbol(n, p));
//			assertTrue(S > 1); // S=1 is the Lagrange case p == 3 (mod 4), but we check it nonetheless.
//		}
		BigInteger Q = pm1.shiftRight(S);
		// find some z with Legendre(z|p)==-1, i.e. z being a quadratic non-residue (mod p)
		int z;
		for (z=2; ; z++) {
			if (jacobiEngine.jacobiSymbol(z, p) == -1) break;
		}
		// now z is found -> set c == z^Q
		BigInteger c = mpe.modPow(BigInteger.valueOf(z), Q, p); // TODO: implement modPow(int, BigInteger, BigInteger)
		BigInteger R = mpe.modPow(n, Q.add(I_1).shiftRight(1), p);
		BigInteger t = mpe.modPow(n, Q, p);
		int M = S;
		while (t.compareTo(I_1) != 0) { // if t=1 then R is the result
			// find the smallest i, 0<i<M, such that t^(2^i) == 1 (mod p)
//			if (DEBUG) {
//				LOG.debug("Find i < M=" + M + " with t=" + t);
//				// test invariants from <link>https://en.wikipedia.org/wiki/Tonelli%E2%80%93Shanks_algorithm#Proof</link>:
//				assertEquals(pm1, mpe.modPow(c, I_1.shiftLeft(M-1), p)); //  -1 == c^(2^(M-1)) (mod p)
//				assertEquals(1, mpe.modPow(t, I_1.shiftLeft(M-1), p));   //   1 == t^(2^(M-1)) (mod p)
//				BigInteger nModP = n.mod(p);
//				assertEquals(R.multiply(R).mod(p), t.multiply(nModP).mod(p));  // R^2 == t*n (mod p)
//			}
			boolean foundI = false;
			int i;
			for (i=1; i<M; i++) {
				if (mpe.modPow(t, I_1.shiftLeft(i), p).equals(I_1)) { // t^(2^i) == 1 (mod p) ?
					foundI = true;
					break;
				}
			}
			if (foundI==false) throw new IllegalStateException("Tonelli-Shanks did not find an 'i' < M=" + M);
			
			BigInteger b = mpe.modPow(c, I_1.shiftLeft(M-i-1), p); // c^(2^(M-i-1))
			R = R.multiply(b).mod(p);
			c = b.multiply(b).mod(p);
			t = t.multiply(c).mod(p);
			M = i;
		}
//		if (DEBUG) assertEquals(R.pow(2).mod(p), n.mod(p));
		// return the smaller sqrt
		return (R.compareTo(p.shiftRight(1)) <= 0) ? R : p.subtract(R);
	}

	/**
	 * Lagrange's formula for p == 3 (mod 4): t = n^k % p, k = (p+1)/4.
	 * @param n a positive integer having Jacobi(n|p) = 1
	 * @param p
	 * @return sqrt of a modulo p
	 */
	private BigInteger Lagrange(BigInteger n, BigInteger p) {
		BigInteger k = p.add(I_1).shiftRight(2);
		BigInteger t = mpe.modPow(n, k, p);
//		if (DEBUG) assertEquals(t.pow(2).mod(p), n.mod(p));
		// return the smaller sqrt
		return (t.compareTo(p.shiftRight(1)) <= 0) ? t : p.subtract(t);
	}
	
	/**
	 * The simpler formula for case p == 5 (mod 8), following <link>www.point-at-infinity.org/ecc/Algorithm_of_Shanks_&_Tonelli.html</link>.
	 * @param n a positive integer having Jacobi(n|p) = 1
	 * @param p
	 * @return
	 */
	private BigInteger case5Mod8(BigInteger n, BigInteger p) {
		BigInteger k = p.shiftRight(3); // for p == 5 (mod 8) we need k = (p-5)/8 = p>>3
		BigInteger n2 = n.shiftLeft(1);
		BigInteger g = mpe.modPow(n2, k, p);
		BigInteger gSquare = g.multiply(g);
		BigInteger i = n2.multiply(gSquare).mod(p);
		BigInteger im1 = i.subtract(I_1);
		BigInteger t = n.multiply(g.multiply(im1)).mod(p);
//		if (DEBUG) assertEquals(t.pow(2).mod(p), n.mod(p));
		// return the smaller sqrt
		return (t.compareTo(p.shiftRight(1)) <= 0) ? t : p.subtract(t);
	}
	
	/**
	 * Simplest algorithm to compute solutions u of u^2 == n (mod p^2).
	 * This is the first proposition in [Pomerance 1985: The Quadratic Sieve Factoring Algorithm, p. 178].
	 * Works only for p==3 (mod 4).
	 * 
	 * @param n a positive integer having Jacobi(n|p) = 1
	 * @param p an odd prime with p==3 (mod 4)
	 * @param pSquare p^2
	 * @return
	 */
	public BigInteger modularSqrtModSquare_v01(BigInteger n, BigInteger p, BigInteger pSquare) {
		// Compute one of the two solutions
		BigInteger u = n.modPow(pSquare.subtract(p).add(I_2).shiftRight(2), pSquare);
		// Return the smaller sqrt
		return u.compareTo(pSquare.shiftRight(1)) <= 0 ? u : pSquare.subtract(u);
	}
	
	/**
	 * A faster version to compute solutions u of u^2 == n (mod p^2), 
	 * doing modular arithmetics (mod p) only, which is an application of lifting via Hensels lemma.
	 * 
	 * This is the second proposition in [Pomerance 1985], but not fully out-formulated there.
	 * This implementation was accomplished following [Silverman 1987: The Multiple Polynomial Quadratic Sieve], p. 332].
	 * 
	 * Works only for p==3 (mod 4).
	 * 
	 * @param n a positive integer having Jacobi(n|p) = 1
	 * @param p an odd prime with p==3 (mod 4)
	 * @param pSquare p^2
	 * @return
	 */
	public BigInteger modularSqrtModSquare_v02(BigInteger n, BigInteger p, BigInteger pSquare) {
		BigInteger b1 = n.modPow(p.add(I_1).shiftRight(2), p);
		// now b = b1 + x*p, x unknown
		BigInteger b1invp = b1.shiftLeft(1).modInverse(p); // 1/(2*b1) mod q
		BigInteger b1Square = b1.multiply(b1);
		BigInteger b2 = b1invp.multiply(n.subtract(b1Square).divide(p)).mod(p);
		// square root == n (mod p^2)
		BigInteger u = b1.add(b2.multiply(p)).mod(pSquare);
		// return the smaller sqrt
		return u.compareTo(pSquare.shiftRight(1)) <= 0 ? u : pSquare.subtract(u);
	}

	/**
	 * General solution of u^2 == n (mod p^exponent), well explained in
	 * [http://mathoverflow.net/questions/52081/is-there-an-efficient-algorithm-for-finding-a-square-root-modulo-a-prime-power, Gottfried Barthel].
	 * Works for any odd p with t^2 == n (mod p) having solution t>0.
	 * 
	 * @param n
	 * @param power p^exponent
	 * @param last_power p^(exponent-1)
	 * @param t solution of t^2 == n (mod p)
	 * 
	 * @return sqrt of n (mod p^exponent)
	 */
	public BigInteger modularSqrtModPower(BigInteger n, BigInteger power, BigInteger last_power, BigInteger t) {
		// Barthel's e = (power - 2*last_power + 1)/2
		BigInteger f = power.subtract(last_power.shiftLeft(1)).add(I_1).shiftRight(1);
		// square root == n (mod p^exponent)
		BigInteger u = t.modPow(last_power, power).multiply(n.modPow(f, power)).mod(power);
		// return the smaller sqrt
		return u.compareTo(power.shiftRight(1)) <= 0 ? u : power.subtract(u);
	}
}
