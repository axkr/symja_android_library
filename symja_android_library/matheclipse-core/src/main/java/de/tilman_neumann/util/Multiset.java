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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Multiset<T> {

	/**
	 * Add an entry with multiplicity 1.
	 * @param entry
	 */
	int add(T entry);

	/**
	 * Add one entry with given multiplicity.
	 * @param entry
	 * @param mult
	 */
	int add(T entry, int mult);

	/**
	 * Add another multiset to this.
	 * @param other
	 */
	void addAll(Multiset<T> other);

	/**
	 * Add all values of the given collection.
	 * @param values
	 */
	void addAll(Collection<T> values);

	/**
	 * Returns the multiplicity of the given value.
	 * 
	 * The key is declared as Object and not as T to match exactly
	 * the signature of the same method in the Map-interface. Nevertheless,
	 * of course the arguments should be of type T.
	 * 
	 * @param value
	 * @return multiplicity
	 */
	Integer get(Object value);

	/**
	 * Removes one instance of the given value from this multiset, if at least one element
	 * is contained. 
	 * 
	 * The key is declared as Object and not as T to match exactly
	 * the signature of the same method in the Map-interface. Nevertheless,
	 * of course the arguments should be of type T.
	 * 
	 * @param key
	 * @return previous multiplicity of the argument
	 */
	Integer remove(Object key);
	
	/**
	 * Remove the given key multiple times.
	 * @param key
	 * @param mult
	 * @return old multiplicity
	 */
	int remove(T key, int mult);

	/**
	 * Removes the key-value pair of the given key no matter what its multiplicity was
	 * @param key
	 * @return previous multiplicity of the argument
	 */
	int removeAll(T key);

	/**
	 * Returns the multiset of elements contained in both this and in the other multiset.
	 * @param other
	 * @return
	 */
	Multiset<T> intersect(Multiset<T> other);

	/**
	 * @return The number of different elements
	 */
	// same as size()
	int keyCount();

	/**
	 * @return The number of different elements
	 */
	// same as keyCount()
	int size();
	
	/**
	 * @return Total number of elements, i.e. the sum of multiplicities of
	 * all different elements
	 */
	int totalCount();

	/**
	 * @return set of distinct values
	 */
	Set<T> keySet();

	/**
	 * @return set of pairs (value, multiplicity)
	 */
	Set<Map.Entry<T, Integer>> entrySet();

	/**
	 * @return this as a flat list in which each value occurs 'multiplicity' times.
	 */
	List<T> toList();

	String toString();

	boolean equals(Object o);
}