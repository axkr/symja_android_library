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
package de.tilman_neumann.jml.factor.base.congruence;

import java.math.BigInteger;

import de.tilman_neumann.jml.factor.base.SortedIntegerArray;
import de.tilman_neumann.util.SortedMultiset;

/**
 * A partial congruence having 1 large factor.
 * 
 * @author Tilman Neumann
 */
public class Partial_1Large extends Partial {

	private int bigFactor; // needs 8 byte instead of 56 byte for a Long[1]
	
	/**
	 * Full constructor.
	 * @param A
	 * @param smallFactors small factors of Q
	 * @param bigFactor the single large factor of Q
	 */
	public Partial_1Large(BigInteger A, SortedIntegerArray smallFactors, int bigFactor) {
		super(A, smallFactors);
		// only 1 large factor
		this.bigFactor = bigFactor;
	}

	@Override
	public SortedMultiset<Integer> getAllQFactors() {
		// get small factors of Q
		SortedMultiset<Integer> allFactors = super.getSmallQFactors();
		// add single large factor
		allFactors.add(bigFactor);
		return allFactors;
	}

	@Override
	public Integer[] getMatrixElements() {
		return new Integer[] {bigFactor};
	}

	@Override
	public int getNumberOfLargeQFactors() {
		return 1;
	}
}
