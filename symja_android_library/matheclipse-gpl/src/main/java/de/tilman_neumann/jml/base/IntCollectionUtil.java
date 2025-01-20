/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2018 Tilman Neumann - tilman.neumann@web.de
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
package de.tilman_neumann.jml.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

/**
 * Utility methods for collections of Integers.
 * 
 * @author Tilman Neumann
 */
public class IntCollectionUtil {

	/**
	 * Converts the given comma-separated string into a list of Integers.
	 * @param str
	 * @return list of sequence elements
	 */
	public static ArrayList<Integer> stringToList(String str) {
		ArrayList<Integer> list = new ArrayList<>();
		if (str!=null) {
	        StringTokenizer tokenizer = new StringTokenizer(str.trim(), ",");
	        while (tokenizer.hasMoreTokens()) {
	        	String token = tokenizer.nextToken().trim();
	        	try {
	        		list.add(Integer.decode(token));
	        	} catch (NumberFormatException nfe) {
	        		throw new IllegalArgumentException("str contains illegal value '" + token + "'");
	        	}
	        }
		}
	    return list;
	}

	/**
	 * Returns the maximum value of the given non-negative integer collection.
	 * @param c the collection (not null)
	 * @return the biggest element, or 0 if the collection is empty
	 */
	public static int max(Collection<Integer> c) {
		int result = 0;
		for (int elem : c) {
			if (elem>result) result = elem;
		}
		return result;
	}
}
