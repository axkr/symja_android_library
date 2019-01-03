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
package de.tilman_neumann.jml.primes.bounds;

/**
 * Bounds for the prime counting function pi(x) = number of primes in (0, x].
 * @author Tilman Neumann
 */
public class PrimeCountUpperBounds {
	
	private PrimeCountUpperBounds() {
		// static class
	}
	
	/**
	 * Computes an upper bound for the prime counting function pi(x) := number of primes in (0, x]
	 * @param x
	 * @return
	 */
	public static long combinedUpperBound(long x) {
		if (x<2) return 0;
		if (x<200) return Rosser_Schoenfeld(x); // works for x > 1
		if (x<24103) return Dusart2010_eq6_6(x); // works for x <= 24103, x >= 60184
		if (x<60184) return Axler_3_5d(x); // works for x = 5.43 ... 230387 and x > 2634562561
		if (x<120000) return Dusart2010_eq6_6(x); // works for x <= 24103, x >= 60184
		if (x<230387) return Axler_3_5d(x); // works for x = 5.43 ... 230387 and x > 2634562561
		if (x<420000) return Dusart2010_eq6_6(x); // works for x <= 24103, x >= 60184
		
		// Clearly best for 420000 < x <= all x tested so far (x <= 411200000000).
		// From that data I estimated that Theorem 1.3 would be better for approximately x > 2470000000000.
		if (x<2470000000000L) return Axler_3_5c(x); // works for x >= 9.25
		
		// Axler Theorem 1.3 is the best in the long run
		return Axler_1_3(x);
	}
	
	/**
	 * Axler, https://arxiv.org/pdf/1409.1780.pdf, Theorem 1.1:
	 * pi(x) < x/ln(x) + x/ln^2(x) + 2*x/ln^3(x) + 6.35*x/ln^4(x) + 24.35*x/ln^5(x) + 121.75*x/ln^6(x) + 730.5*x(ln^7(x) + 6801.4*x/ln^8(x) for x>1.
	 * 
	 * "Improves Dusart's estimate for every x > e^23.11" = 1.08779...e10.
	 * Statement verified, correct.
	 */
	public static long Axler_1_1(long x) {
		double lnx = Math.log(x);
		double lnxPow2 = lnx * lnx;
		double lnxPow3 = lnxPow2 * lnx;
		double lnxPow4 = lnxPow3 * lnx;
		double lnxPow5 = lnxPow4 * lnx;
		double lnxPow6 = lnxPow5 * lnx;
		double lnxPow7 = lnxPow6 * lnx;
		double lnxPow8 = lnxPow7 * lnx;
		return (long) Math.ceil(x/lnx + x/lnxPow2 + 2*x/lnxPow3 + 6.35*x/lnxPow4 + 24.35*x/lnxPow5 + 121.75*x/lnxPow6 + 730.5*x/lnxPow7 + 6801.4*x/lnxPow8);
	}

	/**
	 * Axler, https://arxiv.org/pdf/1409.1780.pdf, Theorem 1.3:
	 * pi(x) < x / [ln(x) - 1 - 1/ln(x) - 3.35/ln^2(x) - 12.65/ln^3(x) - 71.7/ln^4(x) - 466.1275/ln^5(x) - 3489.8225/ln^6(x)], for x > e^3.804.
	 * 
	 * Axler comments: "improvement of Theorem 1.1 for every sufficiently large x". This is true, but the improvement is quite small.
	 * 
	 * From data up to x ~ 4.112 * 10^11 I estimated that Theorem 1.3 is better than Corollar 3.5c for approximately x > 2.470.000.000.000.
	 */
	public static long Axler_1_3(long x) {
		double lnx = Math.log(x);
		double lnxPow2 = lnx * lnx;
		double lnxPow3 = lnxPow2 * lnx;
		double lnxPow4 = lnxPow3 * lnx;
		double lnxPow5 = lnxPow4 * lnx;
		double lnxPow6 = lnxPow5 * lnx;
		double den = lnx - 1 - 1/lnx - 3.35/lnxPow2 - 12.65/lnxPow3 - 71.7/lnxPow4 - 466.1275/lnxPow5 - 3489.8225/lnxPow6;
		return (long) Math.ceil(x/den);
	}

	/**
	 * Axler, https://arxiv.org/pdf/1409.1780.pdf, Corollary 3.5a: 
	 * pi(x) < x / [ln(x) - 1 - 1/ln(x) - 3.35/ln^2(x) - 12.65/ln^3(x) - 89.6/ln^4(x)] for x >= 21.95
	 */
	public static long Axler_3_5a(long x) {
		double lnx = Math.log(x);
		double lnxPow2 = lnx * lnx;
		double lnxPow3 = lnxPow2 * lnx;
		double lnxPow4 = lnxPow3 * lnx;
		double den = lnx - 1 - 1/lnx - 3.35/lnxPow2 - 12.65/lnxPow3 - 89.6/lnxPow4;
		return (long) Math.ceil(x/den);
	}

	/**
	 * Axler, https://arxiv.org/pdf/1409.1780.pdf, Corollary 3.5b: 
	 * pi(x) < x / [ln(x) - 1 - 1/ln(x) - 3.35/ln^2(x) - 15.43/ln^3(x)] for x >= 14.36
	 */
	public static long Axler_3_5b(long x) {
		double lnx = Math.log(x);
		double lnxPow2 = lnx * lnx;
		double lnxPow3 = lnxPow2 * lnx;
		double den = lnx - 1 - 1/lnx - 3.35/lnxPow2 - 15.43/lnxPow3;
		return (long) Math.ceil(x/den);
	}

	/**
	 * Axler, https://arxiv.org/pdf/1409.1780.pdf, Corollary 3.5c:
	 * pi(x) < x / [ln(x) - 1 - 1/ln(x) - 3.83/ln^2(x)] for x >= 9.25
	 * 
	 * Best stable algorithm for all x tested so far! (x <= 50673847669)
	 */
	public static long Axler_3_5c(long x) {
		double lnx = Math.log(x);
		double lnxPow2 = lnx * lnx;
		double den = lnx - 1 - 1/lnx - 3.83/lnxPow2;
		return (long) Math.ceil(x/den);
	}

	/**
	 * Axler, https://arxiv.org/pdf/1409.1780.pdf, Corollary 3.5d:
	 * pi(x) < x / [ln(x) - 1 - 1.17/ln(x)]<br/>
	 * 
	 * Works for x >= 2.634.800.823 and then it is the best bound for x < 6.200.000.000 approximately.
	 */
	public static long Axler_3_5d(long x) {
		double lnx = Math.log(x);
		double den = lnx - 1 - 1.17/lnx;
		return (long) Math.ceil(x/den);
	}

	/**
	 * Dusart 2010 theorem 6.9, eq. (6.5), holds for any x >= 1.
	 */
	public static long Dusart2010_eq6_5(long x) {
		double lnx = Math.log(x);
		return (long) Math.ceil(x/lnx * (1 + 1.2762/lnx));
	}

	/**
	 * Dusart 2010 theorem 6.9, eq. (6.6), holds for any x >= 60184.
	 * @see https://arxiv.org/PS_cache/arxiv/pdf/1002/1002.0442v1.pdf
	 */
	public static long Dusart2010_eq6_6(long x) {
		return (long) Math.ceil(x / (Math.log(x) - 1.1));
	}

	/**
	 * Dusart 2010 theorem 6.9, eq. (6.7), holds for any x >= 2953652287.
	 * @see https://arxiv.org/PS_cache/arxiv/pdf/1002/1002.0442v1.pdf
	 */
	public static long Dusart2010_eq6_7(long x) {
		double lnx = Math.log(x);
		double squareOf_lnx = lnx * lnx;
		double cubeOf_lnx = squareOf_lnx * lnx;
		return (long) Math.ceil(x/lnx + x/squareOf_lnx + 2.334*x/cubeOf_lnx);
	}

	/**
	 * Rosser, Schoenfeld: pi(x) < 1.25506*x / ln(x) for x > 1.
	 */
	public static long Rosser_Schoenfeld(long x) {
		return (long) Math.ceil(1.25506*x / Math.log(x));
	}
}
