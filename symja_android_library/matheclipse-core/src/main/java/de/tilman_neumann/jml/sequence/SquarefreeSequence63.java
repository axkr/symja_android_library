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
package de.tilman_neumann.jml.sequence;

//import org.apache.log4j.Logger;

import de.tilman_neumann.jml.primes.exact.AutoExpandingPrimesArray;
//import de.tilman_neumann.util.ConfigUtil;

/**
 * Sequence of multiplier * {squarefree numbers 1,2,3,5,6,7,10,11,13,...}, long implementation.
 * @author Tilman Neumann
 */
public class SquarefreeSequence63 implements NumberSequence<Long> {
//	private static final Logger LOG = Logger.getLogger(SquarefreeSequence63.class);

	private AutoExpandingPrimesArray primesArray = AutoExpandingPrimesArray.get();
	
	private long multiplier;
	private long next;
	
	public SquarefreeSequence63(long multiplier) {
		this.multiplier = multiplier;
	}
	
	@Override
	public String getName() {
		return multiplier + "*squarefree63";
	}

	@Override
	public void reset() {
		this.next = 1;
	}

	@Override
	public Long next() {
		long ret = next;
		// compute next square free number
		while (true) {
			next = next + 1;
			boolean isSquareFree = true;
			// next must not be divisible by any prime square p^2 <= next
			long test = next;
			int primeIndex = 0;
			for (long p = 2; p*p<=test; p = primesArray.getPrime(++primeIndex)) {
				// test p
				if (test%p == 0) {
					test /= p;
					if (test%p == 0) {
						// next is not square free !
						isSquareFree = false;
						break;
					}
				}
			}
			if (isSquareFree) break; // found next square-free number
		}
		return ret * multiplier;
	}
	
	// standalone test
//	public static void main(String[] args) {
//	   	ConfigUtil.initProject();
//	   	SquarefreeSequence63 seqGen = new SquarefreeSequence63(1);
//		seqGen.reset();
//		for (int i=1; i<=1000; i++) {
//			LOG.info("squarefree(" + i + ") = " + seqGen.next());
//		}
//	}
}
