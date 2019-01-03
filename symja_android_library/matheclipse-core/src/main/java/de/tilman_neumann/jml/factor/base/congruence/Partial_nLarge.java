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
import java.util.ArrayList;

import de.tilman_neumann.jml.factor.base.SortedIntegerArray;
import de.tilman_neumann.jml.factor.base.SortedLongArray;
import de.tilman_neumann.util.SortedMultiset;

/**
 * A partial congruence having an arbitrary number of large factors.
 * This class will hardly be needed in SIQS, but may be needed in CFrac.
 * 
 * @author Tilman Neumann
 */
public class Partial_nLarge extends Partial {

	private int[] bigFactors; // needs about 50 byte for 3 large factors
	private byte[] bigFactorExponents; // needs about 36 byte for 3 large factors
	
	/**
	 * Full constructor.
	 * @param A
	 * @param smallFactors small factors of Q
	 * @param bigFactors large factors of Q
	 */
	public Partial_nLarge(BigInteger A, SortedIntegerArray smallFactors, SortedLongArray bigFactors) {
		super(A, smallFactors);
		// copy big factors of Q
		this.bigFactors = bigFactors.copyFactors();
		this.bigFactorExponents = bigFactors.copyExponents();
	}

	@Override
	public SortedMultiset<Integer> getAllQFactors() {
		// get small factors of Q
		SortedMultiset<Integer> allFactors = super.getSmallQFactors();
		// add large factors
		for (int i=0; i<bigFactors.length; i++) {
			allFactors.add(bigFactors[i], bigFactorExponents[i]);
		}
		return allFactors;
	}

	@Override
	public Integer[] getMatrixElements() {
		ArrayList<Integer> result = new ArrayList<Integer>();
		for (int i=0; i<bigFactors.length; i++) {
			if ((bigFactorExponents[i]&1)==1) result.add(bigFactors[i]);
		}
		return result.toArray(new Integer[result.size()]);
	}

	@Override
	public int getNumberOfLargeQFactors() {
		int count = 0;
		for (int i=0; i<bigFactorExponents.length; i++) {
			count += bigFactorExponents[i];
		}
		return count;
	}
}
