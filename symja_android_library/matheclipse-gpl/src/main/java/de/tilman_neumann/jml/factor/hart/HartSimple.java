/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2018-2024 Tilman Neumann - tilman.neumann@web.de
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
package de.tilman_neumann.jml.factor.hart;

import java.math.BigInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.tilman_neumann.jml.factor.FactorAlgorithm;
import de.tilman_neumann.jml.gcd.Gcd63;

/**
 * Simple implementation of Hart's one line factor algorithm.
 * @see <a href="http://wrap.warwick.ac.uk/54707/">http://wrap.warwick.ac.uk/54707/</a>
 * @author Tilman Neumann
 */
public class HartSimple extends FactorAlgorithm {
	private static final Logger LOG = LogManager.getLogger(HartSimple.class);
	
	private static final boolean DEBUG = false;

	/** This is a constant that is below 1 for rounding up double values to long. */
	private static final double ROUND_UP_DOUBLE = 0.9999999665;
	
	/** Size of arrays */
	private static final int I_MAX = 1<<21;

	private double[] sqrt;

	private final Gcd63 gcdEngine = new Gcd63();

	public HartSimple() {
		// Precompute sqrts for all possible k. 2^21 entries are enough for N~2^63.
		sqrt = new double[I_MAX + 1];
		for (int i = 1; i <= I_MAX; i++) {
			final double sqrtI = Math.sqrt(i);
			sqrt[i] = sqrtI;
		}
	}
	
	@Override
	public String getName() {
		return "HartSimple";
	}

	@Override
	public BigInteger findSingleFactor(BigInteger N) {
		return BigInteger.valueOf(findSingleFactor(N.longValue()));
	}

	public long findSingleFactor(long N) {
		try {
			double sqrtN = Math.sqrt(N);
			for (int k = 1; ; k++) {
				final long a = (long) (sqrtN * sqrt[k] + ROUND_UP_DOUBLE);
				final long test = a*a - k * N;
				final long b = (long) Math.sqrt(test);
				if (b*b == test) {
					long gcd = gcdEngine.gcd(a+b, N);
					if (gcd>1 && gcd<N) return gcd;
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			if (DEBUG) LOG.error(getName() + ": Failed to factor N=" + N + ". Either it has factors < cbrt(N) needing trial division, or the arrays are too small.");
			return 1;
		}
	}
}
