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
package de.tilman_neumann.jml.factor.base;

//import org.apache.log4j.Logger;

/**
 * A reused buffer to store small factors temporarily during trial division.
 * @author Tilman Neumann
 */
// XXX: add() methods might profit from binary search and arraycopy()
public class SortedIntegerArray {
	@SuppressWarnings("unused")
//	private static final Logger LOG = Logger.getLogger(SortedIntegerArray.class);
	
	private int[] factors;
	private short[] exponents;
	private int size;
	
	public SortedIntegerArray() {
		this.factors = new int[50];
		this.exponents = new short[50];
	}
	
	/**
	 * reset() must be called before using for a new Q.
	 */
	public void reset() {
		this.size = 0;
	}
	
	/**
	 * Add a factor.
	 * @param factor
	 * @param power
	 */
	public void add(int factor) {
		//LOG.debug("add small factor " + factor);
		if (size>0) {
			// find position: for short lists a linear search is ok.
			int insertPosition = 0;
			for (; insertPosition<size; insertPosition++) {
				if (factor <= factors[insertPosition]) break;
			}
			if (insertPosition<size) {
				if (factor == factors[insertPosition]) {
					// the factor exists already -> just increment exponent
					exponents[insertPosition]++;
					return;
				}
				// the new factor that must be inserted before some other content.
				// -> shift the elements bigger than factor:
				for (int i=size-1; i>=insertPosition; i--) {
					factors[i+1] = factors[i];
					exponents[i+1] = exponents[i];
				}
				// and add the new factor
				factors[insertPosition] = factor;
				exponents[insertPosition] = 1;
				size++;
				return;
			}
			// insertPosition==size -> append new last element
			factors[size] = factor;
			exponents[size] = 1;
			size++;
			return;
		}
		// size is still 0
		factors[0] = factor;
		exponents[0] = 1;
		size = 1;
	}
	
	/**
	 * Add a factor to the given power.
	 * @param factor
	 * @param power
	 */
	public void add(int factor, short power) {
		//LOG.debug("add small factor " + factor);
		if (size>0) {
			// find position: for short lists a linear search is ok.
			int insertPosition = 0;
			for (; insertPosition<size; insertPosition++) {
				if (factor <= factors[insertPosition]) break;
			}
			if (insertPosition<size) {
				if (factor == factors[insertPosition]) {
					// the factor exists already -> just increment exponent
					exponents[insertPosition] += power;
					return;
				}
				// the new factor that must be inserted before some other content.
				// -> shift the elements bigger than factor:
				for (int i=size-1; i>=insertPosition; i--) {
					factors[i+1] = factors[i];
					exponents[i+1] = exponents[i];
				}
				// and add the new factor
				factors[insertPosition] = factor;
				exponents[insertPosition] = power;
				size++;
				return;
			}
			// insertPosition==size -> append new last element
			factors[size] = factor;
			exponents[size] = power;
			size++;
			return;
		}
		// size is still 0
		factors[0] = factor;
		exponents[0] = power;
		size = 1;
	}
	
	/**
	 * @param i
	 * @return the i.th entry
	 */
	public int get(int i) {
		return factors[i];
	}
	
	/**
	 * @param i
	 * @return the i.th exponent
	 */
	public int getExponent(int i) {
		return exponents[i];
	}

	public int size() {
		return size;
	}
	
	public int[] copyFactors() {
		int[] copy = new int[size];
		System.arraycopy(factors, 0, copy, 0, size);
		return copy;
	}
	
	public short[] copyExponents() {
		short[] copy = new short[size];
		System.arraycopy(exponents, 0, copy, 0, size);
		return copy;
	}
	
	public String toString() {
		if (size==0) return "(empty)";
		String str = "";
		for (int i=0; i<size; i++) {
			str += factors[i] + "^" + exponents[i] + " * ";
		}
		return str.substring(0, str.length()-3);
	}
}
