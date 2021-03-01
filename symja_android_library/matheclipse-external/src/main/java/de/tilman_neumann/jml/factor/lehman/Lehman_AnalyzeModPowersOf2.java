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
package de.tilman_neumann.jml.factor.lehman;

import java.math.BigInteger;

import org.apache.log4j.Logger;

import de.tilman_neumann.jml.gcd.Gcd63;
import de.tilman_neumann.jml.quadraticResidues.QuadraticResiduesMod2PowN;
import de.tilman_neumann.util.ConfigUtil;
import de.tilman_neumann.jml.factor.TestsetGenerator;
import de.tilman_neumann.jml.factor.TestNumberNature;

/**
 * Analyze quadratic residues of a^2 - 4kN (mod m) for m=2, 4, 8, 16, 32, 64,...
 * @author Tilman Neumann
 */
public class Lehman_AnalyzeModPowersOf2 {
	private static final Logger LOG = Logger.getLogger(Lehman_AnalyzeModPowersOf2.class);
	
	private final Gcd63 gcdEngine = new Gcd63();

	public long findSingleFactor(long N) {
		LOG.info("factor N = " + N);
		int cbrt = (int) Math.ceil(Math.cbrt(N));
		double sixthRoot = Math.pow(N, 1/6.0); // double precision is required for stability
		for (int k=1; k <= cbrt; k++) {
			long fourKN = k*N<<2;
			double fourSqrtK = Math.sqrt(k<<4);
			long sqrt4kN = (long) Math.ceil(Math.sqrt(fourKN));
			long limit = (long) (sqrt4kN + sixthRoot / fourSqrtK);
			for (long a = sqrt4kN; a <= limit; a++) {
				final long test = a*a - fourKN;
				final long b = (long) Math.sqrt(test);
				if (b*b == test) {
					long gcd = gcdEngine.gcd(a+b, N);
					if (gcd>1 && gcd<N) {
						LOG.info("  Found factor " + gcd + " from k=" + k + ", a=" + a);
						int n = 0;
						long mod = 1;
						// anything mod 1 is 0
						long aMod = 0, kMod = 0;
						while (true) {
							long lastMod = mod;
							
							n++;
							mod <<= 1;
							if (mod > N) {
								break;
							}
							
							// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
							long lastkMod = kMod;
							long lastaMod = aMod;
							kMod = k % mod;
							if (kMod < 0) kMod += mod;
							aMod = a % mod;
							if (aMod < 0) aMod += mod;
							// The kMod, aMod of k, a that give a factor are 
							// kMod=lastkMod or kMod=lastkMod+lastMod, and
							// aMod=lastaMod or aMod=lastaMod+lastMod !
//							assertTrue(kMod == lastkMod || kMod == lastkMod+lastMod);
//							assertTrue(aMod == lastaMod || aMod == lastaMod+lastMod);
							// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
							
							LOG.info("    MOD = 2^" + n + " = " + mod + ": k%" + mod + " = " + kMod + ", a%" + mod + " = " + aMod);

							long testMod = (a*a - fourKN) % mod;
							if (testMod < 0) testMod += mod;
							LOG.info("      (a*a - 4kN) % " + mod + " = " + testMod); // always a quadratic residue
							
							long fourKModNMod = 4*kMod*N % mod;
							long testMod2 = (aMod*aMod % mod) - (fourKModNMod % mod);
							if (testMod2 < 0) testMod2 += mod;
							//LOG.info("      ((a % " + mod + ")^2 % " + mod + ") - (4*(k % " + mod + ")*N % " + mod + ") = " + testMod2); // always a quadratic residue
							// For k, a that give a factor we have a^2 - 4kN (mod mod) == (a mod mod)^2 (mod mod) - 4*(k mod mod)*N (mod mod)
							// -> we only have to search up to a%mod, k%mod !
//							assertEquals(testMod, testMod2);
							
							boolean isQuadraticResidue = QuadraticResiduesMod2PowN.isQuadraticResidueMod2PowN(testMod2, n);
							//LOG.info("      isQuadraticResidue % " + mod + " = " + isQuadraticResidue);
//							assertTrue(isQuadraticResidue);
							//LOG.info("      quadratic residues % " + mod + " = " + QuadraticResidues.getQuadraticResidues(mod));
							
							if (mod <= 1<<30) { // for larger mods it gets notably slower
								// count/plot quadratic residues of (a^2 - 4kN) (mod 2^n) for all (k, a) with k, a < mod
								long qrCount = 0;
								boolean plotArray = mod <= 256;
								for (int k2 = 0; k2<mod; k2++) {
									String qrForK = "";
									for (int a2 = 0; a2<mod; a2++) {
										long test2 = (a2*a2 - 4*k2*N) % mod;
										if (test2 < 0) test2 += mod;
										boolean isQuadraticResidue2 = QuadraticResiduesMod2PowN.isQuadraticResidueMod2PowN(test2, n);
										if (isQuadraticResidue2) qrCount++;
										if (plotArray) {
											if (isQuadraticResidue2) {
												if (k2==kMod && a2==aMod) {
													qrForK += "#";
												} else {
													qrForK += "+";
												}
											} else {
												qrForK += ".";
											}
										}
									}
									if (plotArray) LOG.debug(qrForK);
									// Result: The pattern of quadratic residues repeats 4 times each in k- and a-dimension!
								}
								LOG.debug("      #{quadratic residues of (a^2 - 4kN) (mod " + mod + ")} = " + qrCount);
								
								// compare with general quadratic residue count
								long qrCount2 = 0;
								for (int i = 0; i<mod; i++) {
									boolean isQuadraticResidue2 = QuadraticResiduesMod2PowN.isQuadraticResidueMod2PowN(i, n);
									if (isQuadraticResidue2) qrCount2++;
								}
								LOG.debug("      #{quadratic residues of i (mod " + mod + ")} = " + qrCount2 + ", square = " + qrCount2*qrCount2);
								// Count results (for N odd semiprimes)
								// n:                                       1, 2,  3,  4,   5,   6,    7,    8,     9,     10,     11,      12,      13,       14,       15,        16,         17,         18,          ...
								// A1(n) = (a2 - 4kN) (mod 2^n) QR count:   4, 16, 48, 128, 448, 1536, 5888, 22528, 89088, 352256, 1404928, 5603328, 22396928, 89522176, 358023168, 1431830528, 5727059968, 22907191296, ... = 2^(n+1) * A023105(n), for n>1
								// A2(n) = i (mod 2^n) QR count:            2, 2,  3,  4,   7,   12,   23,   44,    87,    172,    343,     684,     1367,     2732,     5463,      10924,      21847,      43692,       ... = A023105(n)
								// A3(n) = (i (mod 2^n) QR count)^2:        4, 4,  9,  16,  49,  144,  529,  1936,  7569,  29584,  117649,  467856,  1868689,  7463824,  29844369,  119333776,  477291409,  1908990864,  ...
								// 
								// First relation: A1(n) = 2^(n+1)*A2(n) for n>1
//								if (n>1) assertEquals(2*mod*qrCount2, qrCount);
								
								// Second relation:
								// lim_{n->infinity} A1(n)/A3(n) = 12
								// So (a^2 - 4kN) (mod 2^n) yields about 12 times more quadratic residues than general i.
								// The exact relation for n>1 is
								// A1(n) = 12*A3(n) - c*A2(n) = 12*A2(n)^2 - c*A2(n), with c=16 if n even, c=20 if n odd
								if (n>1) {
									int c = (n&1)==0 ? 16 : 20;
//									assertEquals(12*qrCount2*qrCount2 - c*qrCount2, qrCount);
									
									// Setting the two relations for A1(n) equal gives A2(n) = A023105(n) = (2^n + c/2) / 6,
									// which is the formula from David S. Dodson in http://oeis.org/search?q=A023105
//									assertEquals(QuadraticResiduesMod2PowN.getNumberOfQuadraticResiduesMod2PowN(n), qrCount2);
								}
							}
						}
						
						return gcd; // removes the blur at even k ?
					}
				}
			}
	    }
		
		return 0; // factoring failed
	}

	public static void main(String[] args) {
    	ConfigUtil.initProject();
    	
    	Lehman_AnalyzeModPowersOf2 analyzer = new Lehman_AnalyzeModPowersOf2();
		int bits = 30;
		BigInteger[] testNumbers = TestsetGenerator.generate(2000, bits, TestNumberNature.MODERATE_SEMIPRIMES);
		
		for (BigInteger N : testNumbers) {
			analyzer.findSingleFactor(N.longValue());
		}
	}
}
