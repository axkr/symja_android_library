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
package de.tilman_neumann.jml.factor.siqs;

import java.math.BigInteger;

//import org.apache.log4j.Logger;

import de.tilman_neumann.jml.base.UnsignedBigInt;
import de.tilman_neumann.jml.modular.ModularSqrt;
import de.tilman_neumann.jml.modular.ModularSqrt31;

import static de.tilman_neumann.jml.base.BigIntConstants.*;

/**
 * Engine to compute the smallest modular sqrts for all elements of the prime base.
 * @author Tilman Neumann
 */
public class ModularSqrtsEngine {
//	private static final Logger LOG = Logger.getLogger(ModularSqrtsEngine.class);
//	private static final boolean DEBUG = false;
	
	private ModularSqrt31 modularSqrtEngine = new ModularSqrt31();

	/**
	 * For all primes p in the prime base, find the modular sqrt's of kN (mod p), i.e. the t such that t^2 == kN (mod p).
	 *
	 * @param primesArray
	 * @param primeBaseSize
	 * @param kN
	 * @return
	 */
	public int[] computeTArray(int[] primesArray, int primeBaseSize, BigInteger kN) {
		UnsignedBigInt kN_UBI = new UnsignedBigInt(kN);
		int[] tArray = new int[primeBaseSize]; // zero-initialized
		// special treatment for p[0]=2 (always contained in prime base)
		tArray[0] = kN.intValue() & 1; // kN % 2
		// odd primes
		for (int i = primeBaseSize-1; i>0; i--) {
			// Tonelli_Shanks requires Legendre(kN|p)==1, 0 is not ok. But this is easy to "heal":
			// Since p is prime, Legendre(kN|p)==0 means that kN is a multiple of p.
			// Thus t^2 == kN == 0 (mod p) and the modular sqrt t is 0, too.
			int p = primesArray[i];
			int kN_mod_p = kN_UBI.mod(p);
			if (kN_mod_p > 0) {
				tArray[i] = modularSqrtEngine.modularSqrt(kN_mod_p, p);
			}
			// else: 
			// * We do not need to set tArray[i] = 0, because the array has already been initialized with zeros.
			// * Testing for p|N does not help TDiv's internal QS, because the Q's have been stripped of prime base elements before it is called.
		}
//		if (DEBUG) {
//			assertEquals(kN.mod(I_2), BigInteger.valueOf(tArray[0]).pow(2).mod(I_2));
//			ModularSqrt modularSqrtEngine_big = new ModularSqrt();
//			for (int i = primeBaseSize-1; i>0; i--) {
//				int p = primesArray[i];
//				BigInteger pBig = BigInteger.valueOf(p);
//				BigInteger kN_mod_p = kN.mod(pBig);
//				if (kN_mod_p.compareTo(I_0) > 0) {
//					assertEquals(modularSqrtEngine_big.modularSqrt(kN, p), tArray[i]);
//					assertEquals(modularSqrtEngine_big.modularSqrt(kN_mod_p, p), tArray[i]);
//				} else {
//					assertEquals(0, tArray[i]);
//				}
//				LOG.debug("kN=" + kN + ", p=" + p + ", kN%p = " + kN_mod_p + " -> t=" + tArray[i]);
//			}
//		}
		return tArray;
	}
}
