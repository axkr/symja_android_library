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

//import org.apache.log4j.Logger;

import de.tilman_neumann.jml.gcd.Gcd63;
import de.tilman_neumann.jml.factor.FactorAlgorithmBase;

/**
 * Lehman analyzer that finds the correct k- and a-values of inputs other algorithms can not cope with.
 * Based on the simple implementation.
 * Detects 4kN long overflows.
 * 
 * @author Tilman Neumann
 */
public class Lehman_Analyzer2 extends FactorAlgorithmBase {
//	private static final Logger LOG = Logger.getLogger(Lehman_Analyzer2.class);
	
	private final Gcd63 gcdEngine = new Gcd63();

	@Override
	public String getName() {
		return "Lehman_Analyzer2";
	}

	@Override
	public BigInteger findSingleFactor(BigInteger N) {
		return BigInteger.valueOf(findSingleFactor(N.longValue()));
	}
	
	public long findSingleFactor(long N) {
		// 1. Check via trial division whether N has a nontrivial divisor d <= cbrt(N), and if so, return d.
		int cbrt = (int) Math.ceil(Math.cbrt(N));
//		LOG.debug("N=" + N + ", N%24 = " + (N%24) + ", #bits=" + (64-Long.numberOfLeadingZeros(N)) + ", cbrt=" + cbrt);
		int i=0, p;
		while ((p = SMALL_PRIMES.getPrime(i++)) <= cbrt) {
			if (N%p==0) {
//				LOG.debug("    tdiv found factor " + p);
				return p;
			}
		}
		
		// 2. Main loop
		final int kLimit = ((int) cbrt  + 6) / 6 * 6;
		int kTwoA = Math.max(((kLimit >> 6) - 1), 0) | 1;
		double sixthRoot = Math.pow(N, 1/6.0); // double precision is required for stability
		boolean loggedOverflow = false;
		for (int k=1; k <= cbrt; k++) {
			long fourKN = k*N<<2;
			if (fourKN<0 && !loggedOverflow) {
//				LOG.error("    ******** 4kN = " + fourKN + " overflows positive longs!");
				loggedOverflow = true;
			}
			double fourSqrtK = Math.sqrt(k<<4);
			long sqrt4kN = (long) Math.ceil(Math.sqrt(fourKN));
			long limit = (long) (sqrt4kN + sixthRoot / fourSqrtK);
			for (long a = sqrt4kN; a <= limit; a++) {
				long test = a*a - fourKN;
				long b = (long) Math.sqrt(test);
				if (b*b == test) {
					long gcd = gcdEngine.gcd(a+b, N);
					if (gcd>1 && gcd<N) {
//						LOG.debug("    Lehman found factor " + gcd + ":");
//						LOG.debug("    k=" + k + ", k%24=" + (k%24) + ", cbrt=" + cbrt + ", kLimit=" + kLimit + ", kTwoA=" + kTwoA);
//						LOG.debug("    a=" + a + ", a%24=" + (a%24) + ", aStart=" + sqrt4kN + ", aLimit=" + limit);
						return gcd;
					}
				}
			}
	    }
		
//		LOG.debug("    ******** Factoring failed.");
		return 0;
	}
	
//	public static void main(String[] args) {
//		ConfigUtil.initProject();
//		
//		// These test number were too hard for older Lehman implementations
//		long[] testNumbers = new long[] {
//				5640012124823L,
//				7336014366011L,
//				19699548984827L,
//				52199161732031L,
//				73891306919159L,
//				112454098638991L,
//				
//				32427229648727L,
//				87008511088033L,
//				92295512906873L,
//				338719143795073L,
//				346425669865991L,
//				1058244082458461L,
//				1773019201473077L,
//				6150742154616377L,
//
//				44843649362329L,
//				67954151927287L,
//				134170056884573L,
//				198589283218993L,
//				737091621253457L,
//				1112268234497993L,
//				2986396307326613L,
//				
//				26275638086419L,
//				62246008190941L,
//				209195243701823L,
//				290236682491211L,
//				485069046631849L,
//				1239671094365611L,
//				2815471543494793L,
//				5682546780292609L,
//			};
//		
//		Lehman_Analyzer2 lehman = new Lehman_Analyzer2();
//		for (long testNumber : testNumbers) {
//			lehman.findSingleFactor(testNumber);
//		}
//	}
}
