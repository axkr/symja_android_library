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
package de.tilman_neumann.jml.base;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.StringTokenizer;

import static de.tilman_neumann.jml.base.BigIntConstants.*;

/**
 * A list of big integers.
 * 
 * @author Tilman Neumann
 */
public class BigIntList extends ArrayList<BigInteger> {

	private static final long serialVersionUID = -5780972400272364758L;

	/**
	 * Constructor for an empty list with standard initial capacity.
	 */
	public BigIntList() {
		super();
	}
	
	/**
	 * Constructor for an empty list with specified initial capacity.
	 * @param size initial capacity
	 */
	public BigIntList(int size) {
		super(size);
	}

	/**
	 * Factory method creating a list of big integers from the given comma-separated string.
	 * @param str
	 * @return list of big integers
	 */
	public static BigIntList valueOf(String str) {
		if (str==null) { return null; }

		BigIntList list = new BigIntList();
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
	 * @return The sum of all elements.
	 */
	public BigInteger sum() {
		BigInteger sum = I_0;
		for (BigInteger elem : this) {
			sum = sum.add(elem);
		}
		return sum;
	}

	/**
	 * @return The sum of the absolute values of the elements.
	 */
	public BigInteger absSum() {
		BigInteger sum = I_0;
		for (BigInteger elem : this) {
			sum = sum.add(elem.abs());
		}
		return sum;
	}

	/**
	 * @return The product of all elements, 0 if the list is empty.
	 */
	public BigInteger product() {
		if (this.isEmpty()) {
			return I_0;
		}
		
		BigInteger prod = I_1;
		for (BigInteger elem : this) {
			prod = prod.multiply(elem);
		}
		return prod;
	}
}
