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
import java.util.Set;

import de.tilman_neumann.jml.factor.base.SortedIntegerArray;

/**
 * Base class for partial congruences.
 * The matrix elements of a partial congruence are the large factors appearing with odd exponent.
 * 
 * @author Tilman Neumann
 */
abstract public class Partial extends AQPair implements Congruence {

	/**
	 * Full constructor.
	 * @param A
	 * @param smallFactors small factors of Q
	 * @param bigFactor the single large factor of Q
	 */
	public Partial(BigInteger A, SortedIntegerArray smallFactors) {
		super(A, smallFactors);
	}

	@Override
	public void addMyAQPairsViaXor(Set<AQPair> targetSet) {
		if (!targetSet.remove(this)) targetSet.add(this);
	}
}
