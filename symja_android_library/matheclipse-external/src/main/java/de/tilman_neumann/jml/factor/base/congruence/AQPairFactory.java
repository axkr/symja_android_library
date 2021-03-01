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
import de.tilman_neumann.jml.factor.base.SortedLongArray;

/**
 * Creates an elementary congruence of the subclass appropriate for the given large factors.
 * @author Tilman Neumann
 */
public class AQPairFactory {
	/**
	 * @param A
	 * @param smallFactors small factors of Q
	 * @param bigFactors large factors of Q
	 * @return a new AQPair created from the given arguments
	 */
	public AQPair create(BigInteger A, SortedIntegerArray smallFactors, SortedLongArray bigFactors) {
		int distinctBigFactorsCount = bigFactors.size();
		if (distinctBigFactorsCount == 0) {
			return new Smooth_Perfect(A, smallFactors);
		}
		if (distinctBigFactorsCount == 1) {
			int exp = bigFactors.getExponent(0);
			if (exp==1) return new Partial_1Large(A, smallFactors, bigFactors.get(0));
			if (exp==2) return new Smooth_1LargeSquare(A, smallFactors, bigFactors.get(0));
			// higher exponents are treated below
		} else if (distinctBigFactorsCount == 2) {
			if (bigFactors.getExponent(0)==1 && bigFactors.getExponent(1)==1) {
				return new Partial_2Large(A, smallFactors, bigFactors.get(0), bigFactors.get(1));
			}
			// total factor counts > 2 are treated below
		}
		
		// now in total we have at least 3 large factors
		for (int i=0; i<distinctBigFactorsCount; i++) {
			if ((bigFactors.getExponent(i)&1)==1) {
				// big factors are not square -> it is a partial congruence
				return new Partial_nLarge(A, smallFactors, bigFactors);
			}
		}
		// big factors are square -> it is a smooth congruence
		return new Smooth_nLargeSquares(A, smallFactors, bigFactors);
	}
}
