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

import java.util.Iterator;
import java.util.Map;

/**
 * A multiset with a sort relation between elements.<br>
 * 
 * The sorting of elements has the following consequences:
 * - elements must be Comparable
 * - SortedMaps are a bit slower than HashMaps
 * - output of sorted multisets looks nicer than that of unsorted multisets
 * 
 * Since elements are sorted, we can easily define a sorting on SortedMultisets, too:
 * A sorted multiset is bigger than another one if it's biggest element is bigger
 * than the largest element of the other multiset; or the 2.nd-biggest if
 * the biggest elements are equal; or the 3.rd biggest, and so on.
 * 
 * @author Tilman Neumann
 * 
 * @param <T> value class
 */
public interface SortedMultiset<T extends Comparable<T>> extends Multiset<T>, Comparable<SortedMultiset<T>> {

	/**
	 * @return The smallest element.
	 */
	T getSmallestElement();

	/**
	 * @return The biggest element.
	 */
	T getBiggestElement();
	
	/**
	 * @return an iterator that returns biggest elements first.
	 */
	Iterator<Map.Entry<T, Integer>> getTopDownIterator();
	
	/**
	 * Returns the multiset of elements contained in both this and in the other multiset.
	 * @param other
	 * @return
	 */
	@Override // declare exact result data type
	SortedMultiset<T> intersect(Multiset<T> other);

	/**
	 * Conversion to String, with
	 * @param entrySep e.g. "*" for multiplicative elements
	 * @param expSep e.g. "^" for multiplicative elements
	 * @return
	 */
	String toString(String entrySep, String expSep);
}