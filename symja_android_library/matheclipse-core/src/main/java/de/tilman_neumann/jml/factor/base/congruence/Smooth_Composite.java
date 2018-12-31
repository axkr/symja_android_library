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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.tilman_neumann.util.SortedMultiset;
import de.tilman_neumann.util.SortedMultiset_BottomUp;

/**
 * A smooth congruence composed from several partials.
 * @author Tilman Neumann
 */
public class Smooth_Composite implements Smooth {
	private Integer[] oddExpElements;
	private AQPair[] aqPairs;
	/** congruences never change; therefore we must compute the hashCode only once. */
	private int hashCode;

	/**
	 * Constructor from several AQ-pairs.
	 * @param aqPairs
	 */
	public Smooth_Composite(Set<AQPair> aqPairs) {
		this.aqPairs = new AQPair[aqPairs.size()];
		HashSet<Integer> smallFactorsWithOddExp = new HashSet<Integer>();
		int aqPairCount = 0;
		for (AQPair aqPair : aqPairs) {
			this.aqPairs[aqPairCount++] = aqPair;
			for (int i=0; i<aqPair.smallFactors.length; i++) {
				if ((aqPair.smallFactorExponents[i]&1)==1) {
					// add via xor
					Integer oddExpSmallFactor = aqPair.smallFactors[i];
					if (!smallFactorsWithOddExp.remove(oddExpSmallFactor)) smallFactorsWithOddExp.add(oddExpSmallFactor);
				}
			}
		}
		this.oddExpElements = smallFactorsWithOddExp.toArray(new Integer[smallFactorsWithOddExp.size()]);
		
		this.hashCode = aqPairs.hashCode();
	}

	@Override
	public void addMyAQPairsViaXor(Set<AQPair> targetSet) {
		for (AQPair aqPair : aqPairs) {
			if (!targetSet.remove(aqPair)) targetSet.add(aqPair);
		}
	}

	@Override
	public Integer[] getMatrixElements() {
		return oddExpElements;
	}

	@Override
	public boolean isExactSquare() {
		return oddExpElements.length==0;
	}

	@Override
	public int hashCode() {
		// only used in equals()
		//LOG.debug("hashCode()", new Throwable());
		return hashCode;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o==null || !(o instanceof Smooth_Composite)) return false;
		Smooth_Composite other = (Smooth_Composite) o;
		// equal objects must have the same hashCode
		if (hashCode != other.hashCode) return false;
		return Arrays.equals(this.aqPairs, other.aqPairs);
	}

	@Override
	public String toString() {
		SortedMultiset<BigInteger> allA = new SortedMultiset_BottomUp<BigInteger>();
		SortedMultiset<Integer> allQ = new SortedMultiset_BottomUp<Integer>();
		for (AQPair aqPair : aqPairs) {
			allA.add(aqPair.getA());
			allQ.addAll(aqPair.getAllQFactors());
		}
		return "A = {" + allA.toString("*", "^") + "}, Q = {" + allQ.toString("*", "^") + "}";
	}
}
