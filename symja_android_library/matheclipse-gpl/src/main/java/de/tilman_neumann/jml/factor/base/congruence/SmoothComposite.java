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
package de.tilman_neumann.jml.factor.base.congruence;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.tilman_neumann.util.Ensure;
import de.tilman_neumann.util.SortedMultiset;
import de.tilman_neumann.util.SortedMultiset_BottomUp;

/**
 * A smooth congruence composed from several partials.
 * @author Tilman Neumann
 */
public class SmoothComposite implements Smooth {
	private static final boolean DEBUG = false;
	
	private Integer[] oddExpElements;
	private AQPair[] aqPairs;
	/** congruences never change; therefore we must compute the hashCode only once. */
	private int hashCode;

	/**
	 * Constructor from several AQ-pairs.
	 * @param aqPairs
	 */
	public SmoothComposite(Set<? extends AQPair> aqPairs) {
		this.aqPairs = new AQPair[aqPairs.size()];
		if (DEBUG) Ensure.ensureGreater(aqPairs.size(), 1);
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
	public Set<AQPair> getAQPairs() {
		Set<AQPair> set = new HashSet<>();
		for (AQPair aqPair : aqPairs) {
			set.add(aqPair);
		}
		return set;
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
	
	/**
	 * Checks if this composite smooth relation is equal to another object.</br>
	 * 
	 * Simple smooths (having exactly one AQPair) and composite smooths (having strictly more than one AQPair) can never be equal;
	 * hence it is correct to have two separate equals() implementations that reject all objects of the other type.
	 */
	@Override
	public boolean equals(Object o) {
		if (o==null || !(o instanceof SmoothComposite)) return false;
		SmoothComposite other = (SmoothComposite) o;
		// equal objects must have the same hashCode
		if (hashCode != other.hashCode) return false;
		return Arrays.equals(this.aqPairs, other.aqPairs);
	}

	@Override
	public String toString() {
		SortedMultiset<BigInteger> allA = new SortedMultiset_BottomUp<BigInteger>();
		SortedMultiset<Long> allQ = new SortedMultiset_BottomUp<Long>();
		for (AQPair aqPair : aqPairs) {
			allA.add(aqPair.getA());
			allQ.addAll(aqPair.getAllQFactors());
		}
		return "A = {" + allA.toString("*", "^") + "}, Q = {" + allQ.toString("*", "^") + "}";
	}
}
