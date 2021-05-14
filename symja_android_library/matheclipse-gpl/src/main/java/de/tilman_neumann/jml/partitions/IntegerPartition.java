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
package de.tilman_neumann.jml.partitions;

import java.math.BigInteger;
import java.util.Map;

import org.matheclipse.core.numbertheory.SortedMultiset_BottomUp;

import de.tilman_neumann.jml.combinatorics.Factorial;

/**
 * Integer partition, with nice String output.
 * @author Tilman Neumann
 */
public class IntegerPartition extends SortedMultiset_BottomUp<Integer> {

	private static final long serialVersionUID = -5763523706198835658L;
	
	private Integer n = null;
	private BigInteger realizations = null;

	/**
	 * Constructor from flat element array.
	 * @param elements
	 */
	public IntegerPartition(int[] elements) {
		super();
		for (int element : elements) this.add(element);
	}
	
	/**
	 * @return the sum over all entries, taking into account their frequencies.
	 */
	public int sum() {
		int ret = 0;
		for (Map.Entry<Integer, Integer> entry : this.entrySet()) {
			ret += entry.getKey() * entry.getValue();
		}
		return ret;
	}
	
	// totalCount() delivers number of parts
	
	/**
	 * @return The number of ways to realize this partition.
	 */
	public BigInteger getNumberOfRealizations() {
		// lazy init
		if (realizations == null) {
			if (n==null) n = this.sum();
			realizations = Factorial.factorial(n);
			for (Map.Entry<Integer, Integer> partAndPower : this.entrySet()) {
				int part = partAndPower.getKey().intValue();
				int power = partAndPower.getValue().intValue();
				realizations = realizations.divide(Factorial.factorial(part).pow(power).multiply(Factorial.factorial(power)));
			}
		}
		return realizations;
	}

	/**
	 * Returns a sum-like representation of the additive multiset,
	 * with distinct keys separated by "+" and the multiplicity indicated by "*".
	 */
	public String toString() {
		if (this.size()>0) {
			// Implementation note: Is faster with String than with StringBuffer!
			String sumStr = "";
			for (Map.Entry<Integer, Integer> entry : this.entrySet()) {
				Integer multiplicity = entry.getValue();
				if (multiplicity.intValue() > 1) {
					sumStr += multiplicity + "*";
				}
				sumStr += entry.getKey();
				sumStr += " + ";
			}
			// remove the last " + "
			return sumStr.substring(0, sumStr.length()-3);
		}
		
		// no elements
		return "0";
	}
}
