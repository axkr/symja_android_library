/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2020 Tilman Neumann - tilman.neumann@web.de
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

import java.math.BigInteger;

public class FactorArguments {
	/** The number to factor */
	public BigInteger N;
	
	/** The number of bits of N */
	public int NBits;

	/** The exponent of N */
	public int exp;
	
	/**
	 * Full constructor.
	 * @param N the number to factor
	 * @param exp the exponent of N
	 */
	public FactorArguments(BigInteger N, int exp) {
		this.N = N;
		this.NBits = N.bitLength();
		this.exp = exp;
	}
}
