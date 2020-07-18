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

import java.util.Set;

/**
 * A congruence from 1 or more AQ-pairs.
 * 
 * @author Tilman Neumann
 */
public interface Congruence {
	
	/**
	 * @return set matrix elements.
	 * For a smooth congruence these are the small factors appearing with odd exponent,
	 * and for a partial congruence the large factors with odd exponent,
	 */
	Integer[] getMatrixElements();
	
	/**
	 * Add <code>this</code>'s AQPairs to the target set via xor.
	 * This operation permits to get around without creating new array objects
	 * for all those congruence sub-classes that represent just a single AQPair.
	 *
	 * @param targetSet
	 */
	void addMyAQPairsViaXor(Set<AQPair> targetSet);
}
