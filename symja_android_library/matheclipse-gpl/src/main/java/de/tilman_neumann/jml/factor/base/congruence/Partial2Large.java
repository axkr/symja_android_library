/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2018 Tilman Neumann - tilman.neumann@web.de
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
package de.tilman_neumann.jml.factor.base.congruence;

import java.math.BigInteger;

import de.tilman_neumann.jml.factor.base.SortedIntegerArray;
import de.tilman_neumann.util.SortedMultiset;

/**
 * A partial congruence having 2 distinct large factors.
 * 
 * @author Tilman Neumann
 */
public class Partial2Large extends Partial {

	private long bigFactor1, bigFactor2; // needs 16 byte instead of 64 byte for a Long[2]
	
	/**
	 * Full constructor.
	 * @param A
	 * @param smallFactors small factors of Q
	 * @param bigFactor1 the first large factor of Q
	 * @param bigFactor2 the second large factor of Q
	 */
	public Partial2Large(BigInteger A, SortedIntegerArray smallFactors, long bigFactor1, long bigFactor2) {
		super(A, smallFactors);
		// we have 2 large factor; guarantee that bigFactor1 is the smaller one
		if (bigFactor1 <= bigFactor2) {
			this.bigFactor1 = bigFactor1;
			this.bigFactor2 = bigFactor2;
		} else {
			this.bigFactor1 = bigFactor2;
			this.bigFactor2 = bigFactor1;
		}
	}

	@Override
	public SortedMultiset<Long> getAllQFactors() {
		// get small factors of Q
		SortedMultiset<Long> allFactors = super.getSmallQFactors();
		// add single large factor
		allFactors.add(bigFactor1);
		allFactors.add(bigFactor2);
		return allFactors;
	}

	@Override
	public Long[] getLargeFactorsWithOddExponent() {
		return new Long[] {bigFactor1, bigFactor2};
	}

	@Override
	public int getNumberOfLargeQFactors() {
		return 2;
	}
}
