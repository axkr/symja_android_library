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
package de.tilman_neumann.jml.factor;

/**
 * Definition of the "nature" of test numbers.
 * @author Tilman Neumann
 */
public enum TestNumberNature {
	/** 
	 * Arbitrary random composite numbers N chosen from a certain bit length.
	 * Note that there are no such numbers with less than 3 bits.
	 */
	RANDOM_COMPOSITES,
	
	/** 
	 * Random odd composite numbers N chosen from a certain bit length.
	 * Note that there are no such numbers with less than 4 bits.
	 */
	RANDOM_ODD_COMPOSITES,
	
	/**
	 * Odd semiprimes N=a*b with min(a,b) &gt;= cbrt(N).
	 * Note that there are no such numbers with less than 4 bits.
	 */
	MODERATE_SEMIPRIMES,
	
	/**
	 * Odd semiprimes N=a*b with bitLength(min(a,b)) == bitLength(N)/2 - 1 bits.
	 */
	QUITE_HARD_SEMIPRIMES
}
