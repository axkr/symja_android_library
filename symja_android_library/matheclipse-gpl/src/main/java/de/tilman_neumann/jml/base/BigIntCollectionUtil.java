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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

import static de.tilman_neumann.jml.base.BigIntConstants.*;

/**
 * Utility methods for collections of BigIntegers.
 * 
 * @author Tilman Neumann
 */
public class BigIntCollectionUtil {

	/**
	 * Factory method creating a list of big integers from the given comma-separated string.
	 * @param str
	 * @return list of big integers
	 */
	public static ArrayList<BigInteger> stringToList(String str) {
		if (str==null) { return null; }

		ArrayList<BigInteger> list = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(str.trim(), ",");

        while (tokenizer.hasMoreTokens()) {
        	String token = tokenizer.nextToken().trim();
        	try {
        		list.add(new BigInteger(token));
        	} catch (NumberFormatException nfe) {
        		throw new IllegalArgumentException("str contains illegal value '" + token + "'");
        	}
        }
        return list;
	}

	/**
	 * @param c a collection of BigIntegers
	 * @return The sum of all elements of collection c.
	 */
	public static BigInteger sum(Collection<BigInteger> c) {
		BigInteger sum = I_0;
		for (BigInteger elem : c) {
			sum = sum.add(elem);
		}
		return sum;
	}

	/**
	 * @param c a collection of BigIntegers
	 * @return The sum of the absolute values of the elements of collection c.
	 */
	public static BigInteger absSum(Collection<BigInteger> c) {
		BigInteger sum = I_0;
		for (BigInteger elem : c) {
			sum = sum.add(elem.abs());
		}
		return sum;
	}

	/**
	 * @param c a collection of BigIntegers
	 * @return The product of all elements of collection c, 0 if the collection is empty.
	 */
	public static BigInteger product(Collection<BigInteger> c) {
		if (c==null || c.isEmpty()) {
			return I_0;
		}
		
		BigInteger prod = I_1;
		for (BigInteger elem : c) {
			prod = prod.multiply(elem);
		}
		return prod;
	}
}
