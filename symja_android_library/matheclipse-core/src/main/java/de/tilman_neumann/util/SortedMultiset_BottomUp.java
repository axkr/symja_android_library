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
package de.tilman_neumann.util;

//import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;

/**
 * A sorted set of elements with multiple occurrences, sorted smallest elements first.
 * Sorting is due to extending TreeMap; with HashMap the sorting behavior would be undefined.
 * Elements must implement the Comparable interface.
 * 
 * @author Tilman Neumann
 * 
 * @param <T> element class
 */
public class SortedMultiset_BottomUp<T extends Comparable<T>> extends TreeMap<T, Integer> implements SortedMultiset<T> {
	
	private static final long serialVersionUID = -6604624351619809213L;
	
//	@SuppressWarnings("unused")
//	private static final Logger LOG = Logger.getLogger(SortedMultiset_BottomUp.class);

	private int totalCount = 0;
	
	/**
	 * Constructor for an empty multiset, sorted smallest elements first.
	 * This sort order is particularly adequate for the parts of multiplicative partitions (prime factorizations).
	 */
	public SortedMultiset_BottomUp() {
		super();
	}
	
	/**
	 * Constructor for a multiset, sorted smallest elements first, from a collection.
	 * @param values
	 */
	public SortedMultiset_BottomUp(Collection<T> values) {
		this();
		this.addAll(values);
	}
	
	/**
	 * Constructor for a multiset, sorted smallest elements first, from a value array.
	 * @param values
	 */
	public SortedMultiset_BottomUp(T[] values) {
		this();
		for (T value : values) {
			this.add(value);
		}
	}

	/**
	 * Copy constructor for a multiset, sorted smallest elements first.
	 * @param original
	 */
	public SortedMultiset_BottomUp(Multiset<T> original) {
		this();
		this.addAll(original);
	}
	
	public int add(T entry) {
		Integer myMult = super.get(entry);
		int oldMult = (myMult!=null) ? myMult.intValue() : 0;
		int newMult = oldMult+1;
		super.put(entry, Integer.valueOf(newMult));
		totalCount++;
		return oldMult;
	}

	public int add(T entry, int mult) {
		//LOG.debug("entry " + entry + " has " + entry.getClass());
		Integer myMult = super.get(entry);
		int oldMult = (myMult!=null) ? myMult.intValue() : 0;
		if (mult > 0) {
			int newMult = oldMult + mult;
			super.put(entry, Integer.valueOf(newMult));
			totalCount += mult;
		}
		return oldMult;
	}

	public void addAll(Multiset<T> other) {
		if (other!=null) {
			// we need to recreate the entries of the internal set to avoid
			// that changes in the copy affect the old original
			for (Map.Entry<T, Integer> entry : other.entrySet()) {
				this.add(entry.getKey(), entry.getValue().intValue());
			}
		}
	}
	
	public void addAll(Collection<T> values) {
		if (values != null) {
			for (T value : values) {
				this.add(value);
			}
		}
	}
	
	public Integer remove(Object key) {
		Integer mult = this.get(key);
		if (mult!=null) {
			int imult = mult.intValue();
			if (imult>0) {
				if (imult>1) {
					// the cast works if we only put T-type keys into the map
					// which should be guaranteed in the add-methods
					this.put((T)key, Integer.valueOf(imult-1));
				} else {
					// delete entry from internal map
					super.remove(key);
				}
				this.totalCount--;
			}
		}
		return mult;
	}

	public int remove(T key, int mult) {
		Integer myMult = super.get(key);
		int oldMult = (myMult!=null) ? myMult.intValue() : 0;
		if (oldMult>0) {
			int newMult = Math.max(0, oldMult - mult);
			totalCount += (newMult-oldMult);
			if (newMult>0) {
				super.put(key, Integer.valueOf(newMult));
				return oldMult;
			}
			return super.remove(key).intValue();
		}
		// nothing to remove
		return 0;
	}
	
	public int removeAll(T key) {
		Integer mult = this.get(key);
		if (mult!=null) {
			int imult = mult.intValue();
			if (imult>0) {
				// delete entry from internal map
				super.remove(key);
				this.totalCount -= imult;
			}
			return imult;
		}
		return 0;
	}

	public SortedMultiset<T> intersect(Multiset<T> other) {
		SortedMultiset<T> resultset = new SortedMultiset_BottomUp<T>();
		if (other != null) {
			for (Map.Entry<T, Integer> myEntry: this.entrySet()) {
				int myMult = myEntry.getValue().intValue();
				if (myMult > 0) {
					T myKey = myEntry.getKey();
					Integer otherMult = other.get(myKey);
					if (otherMult != null) {
						int jointMult = Math.min(myMult, otherMult.intValue());
						if (jointMult > 0) {
							resultset.add(myKey, jointMult);
						}
					} // other has myKey
				} // myKey is contained
			}
		}
		return resultset;
	}
	
	public int keyCount() {
		return this.size();
	}
	
	public int totalCount() {
		return this.totalCount;
	}

	public T getSmallestElement() {
		return (this.size() > 0) ? this.keySet().iterator().next() : null;
	}
	
	public T getBiggestElement() {
		return ((SortedSet<T>)(this.keySet())).last();
	}

	public List<T> toList() {
		List<T> flatList = new ArrayList<T>(totalCount);
		for (Map.Entry<T, Integer> entry : this.entrySet()) {
			T value = entry.getKey();
			int multiplicity = entry.getValue().intValue();
			for (int i=0; i<multiplicity; i++) {
				flatList.add(value);
			}
		}
		return flatList;
	}

	@Override
	public Iterator<java.util.Map.Entry<T, Integer>> getTopDownIterator() {
		// currently elements are already sorted bottom-up, so we have to revert then
		return this.descendingMap().entrySet().iterator();
	}

	/**
	 * Compares this to another SortedMultiset.
	 * A sorted multiset is bigger than another one if it's biggest element is bigger
	 * than the largest element of the other multiset, or the 2.nd-biggest if
	 * the biggest elements are equal, or the 3.rd biggest, and so on.
	 * 
	 * @return <0/0/>0 if this is smaller than/equal to /bigger than <code>other</code>.
	 */
	public int compareTo(SortedMultiset<T> other) {
		if (other==null) return Integer.MAX_VALUE;
		// get elements sorted biggest elements first
		Iterator<Map.Entry<T, Integer>> myEntryIter = this.getTopDownIterator();
		Iterator<Map.Entry<T, Integer>> otherEntryIter = other.getTopDownIterator();
		while (myEntryIter.hasNext() && otherEntryIter.hasNext()) {
			Map.Entry<T, Integer> myEntry = myEntryIter.next();
			T myKey = (myEntry!=null) ? myEntry.getKey() : null;
			Map.Entry<T, Integer> otherEntry = otherEntryIter.next();
			T otherKey = (otherEntry!=null) ? otherEntry.getKey() : null;
			if (myKey!=null && otherKey!=null) {
				int keyCmp = myKey.compareTo(otherKey);
				if (keyCmp!=0) return keyCmp;
				// else keys are equal...
				Integer myMultiplicity = (myEntry!=null) ? myEntry.getValue() : null;
				Integer otherMultiplicity = (otherEntry!=null) ? otherEntry.getValue() : null;
				int myMult = (myMultiplicity!=null) ? myMultiplicity.intValue() : 0;
				int otherMult = (otherMultiplicity!=null) ? otherMultiplicity.intValue() : 0;
				int multCmp = myMult-otherMult;
				if (multCmp!=0) return multCmp;
			} else if (myKey!=null && otherKey==null) {
				return Integer.MAX_VALUE;
			} else if (myKey==null && otherKey!=null) {
				return Integer.MIN_VALUE;
			} // else both null -> continue
		}
		// one or both iterators have no more values
		if (myEntryIter.hasNext()) return Integer.MAX_VALUE;
		if (otherEntryIter.hasNext()) return Integer.MIN_VALUE;
		return 0; // completely equal
	}

	/**
	 * Sorted multisets are equal if they have exactly the same elements and
	 * these elements the same multiplicity.
	 */
	public boolean equals(Object o) {
		if (o!=null && o instanceof SortedMultiset) {
			@SuppressWarnings("unchecked")
			SortedMultiset<T> other = (SortedMultiset<T>) o;
			if (this.totalCount != other.totalCount()) return false;
			Iterator<Map.Entry<T, Integer>> myEntryIter = this.getTopDownIterator();
			Iterator<Map.Entry<T, Integer>> otherEntryIter = other.getTopDownIterator();
			while (myEntryIter.hasNext()) {
				if (!otherEntryIter.hasNext()) return false;
				Map.Entry<T, Integer> myEntry = myEntryIter.next();
				Map.Entry<T, Integer> otherEntry = otherEntryIter.next();
				if (!myEntry.equals(otherEntry)) return false;
			}
			// all elements so far were equal, and this has no more entries.
			return !otherEntryIter.hasNext();
		}
		return false;
	}
	
	public String toString(String entrySep, String expSep) {
		String ret = "";
		for (Map.Entry<T, Integer> entry : this.entrySet()) {
			ret += entry.getKey();
			int exp = entry.getValue();
			if (exp > 1) {
				ret += expSep + exp;
			}
			ret += " " + entrySep + " ";
		}
		if (ret.length()>0) {
			// remove last entry separator
			ret = ret.substring(0, ret.length()-3);
		}
		return ret;
	}
}
