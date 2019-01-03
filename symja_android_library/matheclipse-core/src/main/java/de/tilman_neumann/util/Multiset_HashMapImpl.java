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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A set of unsorted elements with multiple occurences.
 * 
 * This is an exact implementation of multisets: 
 * - Two multisets like {a, a, b} and {a, b, a} are equal
 * - and there is no ordering relation between elements.
 * 
 * As such, the implementation can be based on HashMaps which also have no defined sort order.
 * This has the following consequences:
 * - HashMaps are usually faster than SortedMaps
 * - without defined sort order, it makes no sense to demand entries to be Comparable,
 *   neither for the multisets themselves because an ordering of the multisets would depend
 *   on one for their entries.
 * - output of these multisets will usually look a bit ugly
 * 
 * @author Tilman Neumann
 * 
 * @param T element class, must have consistent equals() and hashCode() methods
 */
public class Multiset_HashMapImpl<T> extends HashMap<T, Integer> implements Multiset<T> {
	
	private static final long serialVersionUID = -6604624351619809213L;
	
	@SuppressWarnings("unused")
//	private static final Logger LOG = Logger.getLogger(Multiset_HashMapImpl.class);

	private int totalCount = 0;
	
	/**
	 * Constructor for an empty multiset.
	 */
	public Multiset_HashMapImpl() {
		super();
	}
	
	/**
	 * Constructor from an ordinary collection.
	 * @param values
	 */
	public Multiset_HashMapImpl(Collection<T> values) {
		this();
		this.addAll(values);
	}
	
	/**
	 * Constructor from a value array.
	 * @param values
	 */
	public Multiset_HashMapImpl(T[] values) {
		this();
		for (T value : values) {
			this.add(value);
		}
	}

	/**
	 * Copy constructor.
	 * @param original
	 */
	public Multiset_HashMapImpl(Multiset<T> original) {
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
		Integer oldMult = super.get(key);
		if (oldMult!=null) {
			int imult = oldMult.intValue();
			if (imult>1) {
				// the cast works if we only put T-type keys into the map
				// which should be guaranteed in the add-methods
				super.put((T)key, Integer.valueOf(imult-1));
				this.totalCount--;
			} else if (imult==1) {
				// delete entry from internal map
				super.remove(key);
				this.totalCount--;
			}
		}
		return oldMult;
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

	public Multiset<T> intersect(Multiset<T> other) {
		Multiset<T> resultset = new Multiset_HashMapImpl<T>();
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

	/**
	 * Returns a string representation of the unsorted multiset similar to collections,
	 * with distinct keys separated by commas and the multiplicity indicated by "^".
	 */
	public String toString() {
		if (this.size()>0) {
			// Implementation note: Is faster with String than with StringBuffer!
			String factorStr = "{";
			for (Map.Entry<T, Integer> entry : this.entrySet()) {
				factorStr += entry.getKey();
				Integer multiplicity = entry.getValue();
				if (multiplicity.intValue() > 1) {
					factorStr += "^" + multiplicity;
				}
				factorStr += ", ";
			}
			// remove the last ", " and append closing bracket
			return factorStr.substring(0, factorStr.length()-2) + "}";
		}
		
		// no elements
		return "{}";
	}
	
	/**
	 * Unordered multisets are equal if they have exactly the same elements and
	 * these elements the same multiplicity, no matter in which iteration
	 * order the elements appear.
	 */
	public boolean equals(Object o) {
		if (o!=null && o instanceof Multiset) {
			Multiset<T> other = (Multiset<T>) o;
			if (this.totalCount != other.totalCount()) return false;
			if (this.keyCount() != other.keyCount()) return false;
			for (Map.Entry<T, Integer> myEntry : this.entrySet()) {
				// get multiplicities of this and other for the same key
				Integer myMult = myEntry.getValue();
				Integer otherMult = other.get(myEntry.getKey());
				// check equality of multiplicities
				if (myMult==null && otherMult==null) continue;
				if (myMult==null || otherMult==null || !otherMult.equals(myMult)) return false;
			}
			return true;
		}
		return false;
	}
}
