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
package de.tilman_neumann.jml.factor.base.matrixSolver;

import java.util.ArrayList;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.tilman_neumann.util.Ensure;

/**
 * BitArray implementation of an IndexSet, realized in long[], used by the Gaussian solver.
 * 
 * The xor() operation has complexity O(n), where n is the sum of the size of the two argument arrays.
 * Since here 64 xor's are done at once, this implementation is very fast compared to other algorithms with the same complexity.
 * 
 * @author Tilman Neumann
 */
public class IndexSet  {
	private static final Logger LOG = LogManager.getLogger(IndexSet.class);
	private static final boolean DEBUG = false;

	/**
	 * the bit array:
	 * bitArray[0] has bits 0..63, bitArray[1] bits 64..127 and so on.
	 */
	private long[] bitArray;
	private int numberOfBits;
	private int numberOfLongs;
	private int biggestEntry; // biggestEntry = -1 indicates that no bit is set / the "set" is empty
	private boolean dirty;
	private int setCount;

	/**
	 * Standard constructor, creates an empty bit array capable to hold the given numberOfBits.
	 * 
	 * @param numberOfBits
	 */
	public IndexSet(int numberOfBits) {
		this.numberOfBits = numberOfBits;
		this.numberOfLongs = (numberOfBits+63)>>6; // ceil(numberOfBits/64)
		this.bitArray = new long[numberOfLongs];
		this.biggestEntry = -1; // no bit set
		this.dirty = true;
		this.setCount = 0;
	}
	
	public void setBits(int newBits) {
		if(newBits != this.numberOfBits) { 
			numberOfLongs = (newBits+63)>>6; // ceil(numberOfBits/64)
			if(numberOfLongs > bitArray.length) {
				this.bitArray = new long[numberOfLongs];
			}
			this.numberOfBits = newBits;
		}
		for(int i=0; i<numberOfLongs; i++) {
			bitArray[i] = 0L;
		}
		this.biggestEntry = -1; // no bit set
		this.dirty = true;
		this.setCount = 0;
	}
	
	/**
	 * Add a single element x to this index set.
	 * @param x
	 */
	public void add(int x) {
		int longIndex = x>>6; // floor(x/64)
		int restIndex = x-(longIndex<<6); // x-64*longIndex
		bitArray[longIndex] |= (1L<<restIndex); // set bit
		if (x>biggestEntry) biggestEntry = x; // update biggest entry
		dirty = true;  // need to recount set bits on next use
	}

	public int getNumberOfSetBits() {
		if(dirty) {
			setCount = 0;
			for (int longIndex=biggestEntry>>6; longIndex>=0; longIndex--) {
				setCount += Long.bitCount(bitArray[longIndex]);
			}
			dirty = false;
		}
		return setCount;
	}
	
	public boolean contains(Object o) {
		LOG.debug("contains()", new Throwable()); // never used, method untested
		if (o==null || !(o instanceof Integer)) return false;
		int x = ((Integer)o).intValue();
		if (x >= numberOfBits) return false; // there is no entry as big as x
		int longIndex = x>>6; // floor(x/64)
		long theLong = bitArray[longIndex];
		int restIndex = x - (longIndex<<6);
		return (theLong & (1L<<restIndex)) != 0; // bit theLong[restIndex] is set
	}

	public Integer last() {
		return biggestEntry;
	}

	/**
	 * @return true if this set is empty
	 */
	public boolean isEmpty() {
		return biggestEntry<0;
	}

	public void addXor(IndexSet other) {
		if (numberOfBits!=other.numberOfBits) {
			throw new IllegalArgumentException("IndexSet.addXor(): the argument has a different size!");
		}
	
		dirty = true;  // need to recount set bits on next use
		
		int xMax = Math.max(biggestEntry, other.biggestEntry);
		int maxLongIndex = xMax>>6; // xMax/64
		for (int longIndex=maxLongIndex; longIndex>=0; longIndex--) {
			bitArray[longIndex] ^= other.bitArray[longIndex]; // xor of 64 bits at once
		}
		// recompute biggest entry
		for (int longIndex=maxLongIndex; longIndex>=0; longIndex--) {
			long theLong = bitArray[longIndex];
			if (theLong != 0) {
				// some bit is set
				for (int i=63; i>=0; i--) {
					if ((theLong & (1L<<i)) != 0) {
						biggestEntry = 64*longIndex + i;
						return;
					}
				}
				throw new IllegalStateException("theLong != 0 -> some bit should have been set!?");
			}
		}
		// nothing found
		this.biggestEntry = -1;
	}
	
	/**
	 * @return this index set as a list
	 */
	public ArrayList<Integer> toList() {
		ArrayList<Integer> result = new ArrayList<Integer>();
		for (int i=0; i<numberOfLongs; i++) {
			long theLong = bitArray[i];
			int x = i<<6; // x = 64*i
			if (DEBUG) Ensure.ensureEquals(64*i, x);
			if (x>biggestEntry) break;
			for (int j=0; j<64; j++) {
				boolean isSet = (theLong & (1L<<j)) != 0;
				if (isSet) result.add(x+j);
			}
		}
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof IndexSet)) return false;
		IndexSet other = (IndexSet) o;
		if (this.numberOfBits != other.numberOfBits) return false;
		if (this.biggestEntry != other.biggestEntry) return false;
		for (int i=0; i<numberOfLongs; i++) {
			if (bitArray[i] != other.bitArray[i]) return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		throw new IllegalStateException("Class IndexSet is not ready to be used in hash structures");
	}

	@Override
	public String toString() {
		String str = "[";
		for (int i=0; i<numberOfLongs; i++) {
			str += Long.toBinaryString(bitArray[i]) + " ";
		}
		return str.substring(0, str.length()-1) + "]";
	}
}