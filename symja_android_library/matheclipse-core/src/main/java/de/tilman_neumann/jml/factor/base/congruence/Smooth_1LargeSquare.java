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
 * A smooth congruence with 1 large factor contained as a square.
 * 
 * @author Tilman Neumann
 */
public class Smooth_1LargeSquare extends Smooth_Simple {

	/** the large factor contained as a square */
	private int bigFactor;
	
	/**
	 * Full constructor.
	 * @param A
	 * @param smallFactors small factors of Q
	 * @param bigFactor the single large factor of Q contained as square
	 */
	public Smooth_1LargeSquare(BigInteger A, SortedIntegerArray smallFactors, int bigFactor) {
		super(A, smallFactors);
		// the large factor contained as a square
		this.bigFactor = bigFactor;
	}

	@Override
	public SortedMultiset<Integer> getAllQFactors() {
		// get small factors of Q
		SortedMultiset<Integer> allFactors = super.getSmallQFactors();
		// add single large factor square
		allFactors.add(bigFactor, 2);
		return allFactors;
	}

	@Override
	public int getNumberOfLargeQFactors() {
		return 2;
	}
}
