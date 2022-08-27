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
package de.tilman_neumann.jml.primes;

import java.util.Stack;

import org.apache.log4j.Logger;

import de.tilman_neumann.jml.primes.exact.SegmentedSieve;
import de.tilman_neumann.jml.primes.exact.SieveCallback;
import de.tilman_neumann.util.ConfigUtil;

/**
 * Find primes with relatively large prime gaps, say ratios p(i)/p(i-1) > p(k)/p(k-1) for all k > i.
 * 
 * Bertrand’s postulate states that there is a prime in any interval [n, 2n].
 * Legendre's conjecture says that there is at least one prime in any interval (n^2, (n+1)^2).
 * But for not too small i, the true maximum ratio p(i)/p(i-1) is much smaller than that.
 * 
 * The main purpose of this class is to find the following rational sequence:
 * Numerators are A277718 = "Bounding prime for the first k-Ramanujan prime."
 * Denominators are A209407 = "Primes p(i) such that p(i+1)/p(i) > p(k+1)/p(k) for all k>i"
 * 
 * Bounds are taken from Christian Axler, Thomas Leßmann: "An explicit upper bound for the first k-Ramanujan prime" 2018,
 * https://arxiv.org/pdf/1504.05485.pdf, and the proof procedure has been derived from comments by Charles Greathouse IV. in A209407.
 * 
 * A few results:
 * 1. Testing all primes below 1e11 allows us to prove the first 87 sequence elements.
 * 2. Trudgian's bound is not satisfied at a(75) ?
 * 3. Trudgian's bound seems to be the one of most practical relevance; the transition point between Trudgian's bound
 *    and Axler/Leßmann's bound is at approximately e^131.1687 = 9.24363...*10^56.
 * 
 * @author Tilman Neumann
 */
public class PrimeGapTest implements SieveCallback {
	private static final Logger LOG = Logger.getLogger(PrimeGapTest.class);

	public static class StackElement {
		public long lastPrime;
		public long prime;
		public double ratio;
		
		public StackElement(long lastPrime, long prime, double ratio) {
			this.lastPrime = lastPrime;
			this.prime = prime;
			this.ratio = ratio;
		}
	}
	
	private Stack<StackElement> stack;
	private SegmentedSieve sieve;
	private long limit;
	private long count = 0;
	private long lastPrime = 2; // avoid "simulated" large relative prime gap
	private int highestRankUpdate = Integer.MAX_VALUE;

	public PrimeGapTest(long limit) {
		stack = new Stack<StackElement>();
		sieve = new SegmentedSieve(this);
		this.limit = limit;
	}

	public void run() {
		long startTime = System.currentTimeMillis();
		stack.push(new StackElement(2, 3, 1.5)); // initial stack must be non-empty; dummy entry will be surpassed by 5/3
		sieve.sieve(limit);
		LOG.info("Tested " + count + " primes <= " + limit + " in " + (System.currentTimeMillis()-startTime) + "ms");
		LOG.info("Record p_i/p_(i-1) ratios:");
		int i=1;
		for (Object elem : stack) {
			checkResult((StackElement) elem, i);
			i++;
		}
	}
	
	@Override
	public void processPrime(long prime) {
		//LOG.debug("p_" + count + " = " + prime);
		double ratio = prime / (double) lastPrime;
		StackElement elem;
		while (!stack.isEmpty()) {
			elem = (StackElement) stack.pop();
			if (elem.ratio > ratio) {
				// the new ratio is worse, push the old one back
				stack.push(elem);
				break;
			} // else: new ratio is better, forget old stack element
		}
		// add the new element
		highestRankUpdate = Math.min(highestRankUpdate, stack.size());
		stack.push(new StackElement(lastPrime, prime, ratio));
		count++;
		if (count % 10000000 == 0) {
			LOG.info("Tested primes: " + count + ", current stack size = " + stack.size() + ", highest rank update = " + highestRankUpdate);
			highestRankUpdate = Integer.MAX_VALUE;
		}
		lastPrime = prime;
	}

    @SuppressWarnings("UnicodeInCode")
	private void checkResult(StackElement elem, int i) {
		long p_i = elem.prime;
		long p_im1 = elem.lastPrime;
		double ratio = elem.ratio;
		LOG.info("    " + i + ". " + p_i + "/" + p_im1 + " = " + ratio + ", difference = " + (p_i - p_im1));

		// Test correctness of bounds
		// 1. Dusart bound: For every x ≥ 396738, there is a prime p with x < p <= x * (1 + 1/(25*ln^2(x))).
		double ln_p_im1 = Math.log(p_im1);
		double squareOf_ln_p_im1 = ln_p_im1 * ln_p_im1;
		if (p_im1 >= 396738) {
			double DusartBound = p_im1 * (1 + 1/(25*squareOf_ln_p_im1));
			if (p_i>DusartBound) LOG.error("        ERROR! p_i = " + p_i + " exceeded Dusart bound " + DusartBound + " !");
		}
		// 2. Trudgian bound: For every x ≥ 2898239, there is a prime p with x < p <= x * (1 + 1/(111*ln^2(x))).
		if (p_im1 >= 2898239) {
			double TrudgianBound =  p_im1 * (1 + 1/(111*squareOf_ln_p_im1));
			if (p_i>TrudgianBound) LOG.error("        ERROR! p_i = " + p_i + " exceeded Trudgian bound " + TrudgianBound + " !");
		}
		// 3. Axler/Leßmann bound: For every x ≥ 58837, there is a prime p with x < p <= x * (1 + 1.188/ln^3(x)).
		if (p_im1 >= 58837) {
			double cubeOf_ln_p_im1 = squareOf_ln_p_im1 * ln_p_im1;
			double AxlerLeßmannBound = p_im1 * (1 + 1.188/cubeOf_ln_p_im1);
			if (p_i>AxlerLeßmannBound) LOG.error("        ERROR! p_i = " + p_i + " exceeded Axler/Leßmann bound " + AxlerLeßmannBound + " !");
		}
	
		// Test correctness of records
		// We have to prove p_i/p_im1 > p_k/p_km1 for all k > i.
		
		// 1. Dusart bound:
		// We know from above that p_k <= p_km1 * (1 + 1/(25*ln^2(p_km1))) <=> p_k/p_km1 <= 1 + 1/(25*ln^2(p_km1))
		// So if p_i/p_im1 > 1 + 1/(25*ln^2(p_k)), then p_i/p_im1 > p_k/p_km1 for all k > i.
		// Invert the first expression:
		// <=> 25 * (p_i/p_im1 - 1) > 1/ln^2(p_k)
		// <=> ln^2(p_k) > 1 / (25 * (p_i/p_im1 - 1))
		// <=> ln(p_k) > sqrt(1 / (25 * (p_i/p_im1 - 1)))
		// <=> p_k = exp(sqrt(1 / (25 * (p_i/p_im1 - 1))))
		// To prove that some p_i/p_im1 is a valid record, we need to check all primes up to max(p_k, 396738)
		
		double p_k = Math.exp(Math.sqrt(1 / (25 * (ratio - 1))));
		double bound = Math.max(396738, p_k);
		if (bound <= limit) LOG.info("        Confirmed by checking all primes up to Dusart bound " + bound);
		else                LOG.info("        Dusart bound " + bound + " has not been reached.");

		// 2. Trudgian bound: Very similar to the derivation of the Dusart bound. 
		
		p_k = Math.exp(Math.sqrt(1 / (111 * (ratio - 1))));
		bound = Math.max(2898239, p_k);
		if (bound <= limit) LOG.info("        Confirmed by checking all primes up to Trudgian bound " + bound);
		else                LOG.info("        Trudgian bound " + bound + " has not been reached.");

		// 3. Axler/Leßmann bound:
		// We know from above that p_k <= p_km1 * (1 + 1.188/ln^3(x)) <=> p_k/p_km1 <= 1 + 1.188/ln^3(x)
		// So if p_i/p_im1 > 1 + 1.188/ln^3(p_k), then p_i/p_im1 > p_k/p_km1 for all k > i.
		// Invert the first expression:
		// <=> p_i/p_im1 - 1 > 1.188/ln^3(p_k)
		// <=> ln^3(p_k) > 1.188 / (p_i/p_im1 - 1)
		// <=> ln(p_k) > (1.188 / (p_i/p_im1 - 1))^(1/3)
		// <=> p_k = exp((1.188 / (p_i/p_im1 - 1))^(1/3))
		// To prove that some p_i/p_im1 is a valid record, we need to check all primes up to max(p_k, 58837)

		p_k = Math.exp(Math.cbrt(1.188 / (ratio - 1)));
		bound = Math.max(58837, p_k);
		if (bound <= limit) LOG.info("        Confirmed by checking all primes up to Axler/Leßmann bound " + bound);
		else                LOG.info("        Axler/Leßmann bound " + bound + " has not been reached.");
	}
	
	public static void main(String[] args) {
		ConfigUtil.initProject();
		new PrimeGapTest(100000000000L).run();
	}
}
