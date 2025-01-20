/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2020 Tilman Neumann - tilman.neumann@web.de
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
package de.tilman_neumann.jml.factor.base;

import java.math.BigInteger;

import de.tilman_neumann.util.SortedMultiset;

public class FactorResult {

	/** factors that are at least probable prime */
	public SortedMultiset<BigInteger> primeFactors;
	
	/** factors whose primeness has not been checked yet */
	public SortedMultiset<BigInteger> untestedFactors;
	
	/** factors that are certainly composite */
	public SortedMultiset<BigInteger> compositeFactors;
	
	/** the smallest factor that could occur in the unfactored rest, e.g. because smaller factors have been excluded by trial division */
	public long smallestPossibleFactor;
	
	/**
	 * Full constructor.
	 * @param primeFactors prime factors found
	 * @param untestedFactors factors found but not investigated for primeness
	 * @param compositeFactors factors found that are certainly composite
	 * @param smallestPossibleFactor the smallest factor that could occur in untestedFactors or compositeFactors
	 */
	public FactorResult(SortedMultiset<BigInteger> primeFactors, SortedMultiset<BigInteger> untestedFactors, SortedMultiset<BigInteger> compositeFactors, long smallestPossibleFactor) {
		this.primeFactors = primeFactors;
		this.untestedFactors = untestedFactors;
		this.compositeFactors = compositeFactors;
		this.smallestPossibleFactor = smallestPossibleFactor;
	}
	
	@Override
	public String toString() {
		return "primeFactors = " + primeFactors + ", untestedFactors = " + untestedFactors + ", compositeFactors = " + compositeFactors + ", smallestPossibleFactor = " + smallestPossibleFactor;
	}
}
