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
package de.tilman_neumann.jml.primes.probable;

import static de.tilman_neumann.jml.base.BigIntConstants.I_1;
import static de.tilman_neumann.jml.base.BigIntConstants.I_0;

import java.math.BigInteger;

import de.tilman_neumann.jml.modular.JacobiSymbol;
import de.tilman_neumann.jml.roots.SqrtExact;

/**
 * Lucas probable prime tests. Both non-strong and strong variants are available.
 * This implementation is inspired by [http://en.wikipedia.org/wiki/Baillie-PSW_primality_test] and references therein,
 * as well as Java's built-in BigInteger.isProbablePrime() implementation.
 * 
 * This implementation passes on preliminary trial division, because it is thought to be used in the "trial division" phase of the quadratic
 * sieve on numbers that have already undergone quite a lot of trial divisions.
 * 
 * @author Tilman Neumann
 */
public class LucasTest {
	
	private JacobiSymbol jacobiEngine = new JacobiSymbol();

    /**
     * (Non-strong) Lucas probable prime test with parameters P=1, D some value in 5, -7, 9, -11, 13, -15, ...  and Q=(1-D)/4 in -1, 2, -2, 3, -3, ...
     * 
     * @param N
     * @return true if N is a Lucas probable prime, false if N is composite
     */
	public boolean isProbablePrime(BigInteger N) {
		// We will never find a D with Jacobi(D|N) == -1 if N is a perfect square -> do square-test before.
		// Examples where BPSW would fail otherwise are N = 1194649 = 1093^2 and N = 12327121 = 3511^2.
		if (SqrtExact.exactSqrt(N) != null) return false;
		
		// Now get first D with Jacobi(D|N) == -1
        BigInteger D = findDParameter(N);
        
        // delta(N) = N - Jacobi(D|N); since Jacobi(D|N)==-1 it evaluates to delta = N+1
        BigInteger delta = N.add(I_1);
        
        // run Lucas sequence until we find U_delta
        BigInteger U = I_1;
        BigInteger V = I_1;
        for (int i = delta.bitLength()-2; i >= 0; i--) { // i = index of delta bits from left to right
        	// double indices:
        	// U_2k = U_k * V_k (mod N)
            BigInteger U2 = U.multiply(V).mod(N);
            // V_2k = V_k^2 - 2*Q^k (mod N).
            // Here we use the trick also applied in Java's BigInteger.isProbablePrime() implementation:
            // With V_k^2 - D*U_k^2 = 4*Q^k   <=> 2*Q^k = (V_k^2 - D*U^2) / 2 we get
            // V_2k = V_k^2 - ((V_k^2 - D*U^2) / 2) (mod N)   <=> V_2k = (V_k^2 + D*U^2) / 2 (mod N)
            // The last expression saves us a modular power and keeping track of the index.
            BigInteger V2 = V.multiply(V).add(D.multiply(U.multiply(U))).mod(N);
            if (V2.testBit(0)) V2 = V2.subtract(N);
            V2 = V2.shiftRight(1);
            
            if (delta.testBit(i)) {
	            // increment indices:
	            // U_(2k+1) = (P*U_2k + V_2k) / 2 (mod N), and we have P=1
	            U = U2.add(V2).mod(N);
	            if (U.testBit(0)) U = U.subtract(N);
	            U = U.shiftRight(1);
	            // V_(2k+1) = (D*U_2k + P*V_2k) / 2 (mod N), and we have P=1
	            V = V2.add(D.multiply(U2)).mod(N);
	            if (V.testBit(0)) V = V.subtract(N);
	            V = V.shiftRight(1);
            } else {
            	// bit i of delta is 0, no need to increment indices
                U = U2;
                V = V2;
            }
        }
        // If U_delta == 0 (mod N) then N is a Lucas probable prime.
        // Another mod() is not required, because U is already in the range [1-N, N-1]
        return U.equals(I_0);
    }

    /**
     * Strong Lucas probable prime test with parameters P=1, D some value in 5, -7, 9, -11, 13, -15, ...  and Q=(1-D)/4 in -1, 2, -2, 3, -3, ...
     * 
     * @param N
     * @return true if N is a strong Lucas probable prime, false if N is composite
     */
	public boolean isStrongProbablePrime(BigInteger N) {
		// We will never find a D with Jacobi(D|N) == -1 if N is a perfect square -> do square-test before.
		// Examples where BPSW would fail otherwise are N = 1194649 = 1093^2 and N = 12327121 = 3511^2.
		if (SqrtExact.exactSqrt(N) != null) return false;
		
		// Now get first D with Jacobi(D|N) == -1
        BigInteger D = findDParameter(N);
        
        // delta(N) = N - Jacobi(D|N); since Jacobi(D|N)==-1 it evaluates to delta = N+1
        BigInteger delta = N.add(I_1);
    	// decompose delta = d*2^s, d odd
    	int s = delta.getLowestSetBit();
    	BigInteger d = delta.shiftRight(s);
    	
    	// run Lucas sequence until we find U_d
        BigInteger U = I_1;
        BigInteger V = I_1;
        for (int i = d.bitLength()-2; i >= 0; i--) {
        	// double indices:
            BigInteger U2 = U.multiply(V).mod(N);
            BigInteger V2 = V.multiply(V).add(D.multiply(U.multiply(U))).mod(N);
            if (V2.testBit(0)) V2 = V2.subtract(N);
            V2 = V2.shiftRight(1);
            
            if (d.testBit(i)) {
	            // increment indices:
	            U = U2.add(V2).mod(N);
	            if (U.testBit(0)) U = U.subtract(N);
	            U = U.shiftRight(1);
	            V = V2.add(D.multiply(U2)).mod(N);
	            if (V.testBit(0)) V = V.subtract(N);
	            V = V.shiftRight(1);
            } else {
            	// bit i of delta is 0, no need to increment indices
                U = U2;
                V = V2;
            }
        }
        // If U_d == 0 (mod N) or V_(d*2^0)=V_d == 0 (mod N) then N is a Lucas probable prime
        if (U.equals(I_0) || V.equals(I_0)) return true;
         
        // test V_(d*2^r) == 0 (mod N) for 0<r<s
        for (int r=1; r<s; r++) {
        	// double indices:
            BigInteger U2 = U.multiply(V).mod(N);
            BigInteger V2 = V.multiply(V).add(D.multiply(U.multiply(U))).mod(N);
            if (V2.testBit(0)) {
            	// V2 is odd. If we subtract N to make it even before the division by 2, then it is in the range [1-N, -2]
            	V2 = V2.subtract(N);
                V2 = V2.shiftRight(1);
                U = U2;
                V = V2;
                // now V is in the range [(1-N)/2, -1] and thus surely V_(d*2^r) != 0 (mod N) -> no 0-test required
            } else {
                V2 = V2.shiftRight(1);
                U = U2;
                V = V2;
                // here we need to test if V_(d*2^r) == 0 (mod N); then N is a Lucas probable prime
                if (V.equals(I_0)) return true;
            }
        }
        return false;
    }
	
	/**
	 * Find the first D in the sequence 5, -7, 9, -11, 13, -15, ... such that Jacobi(D|N) is -1.
	 * Note that this wouldn't work for N that are perfect squares
	 * @param N
	 * @return D
	 */
	private BigInteger findDParameter(BigInteger N) {
        int D = 5;
        while (jacobiEngine.jacobiSymbol(D, N) != -1) {
        	D = D>0 ? -D-2 : -D+2;
        }
        return BigInteger.valueOf(D);
	}
}
