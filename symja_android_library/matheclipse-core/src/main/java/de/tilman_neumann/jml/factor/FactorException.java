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
package de.tilman_neumann.jml.factor;

import java.math.BigInteger;

/**
 * An exception indicating that a factor was found.
 * 
 * @author Tilman Neumann
 */
public class FactorException extends Exception {
	private static final long serialVersionUID = -6623862851940555652L;
	
	private BigInteger factor = null;
	
	/**
	 * Complete constructor.
	 * @param factor
	 */
	public FactorException(BigInteger factor) {
		this.factor = factor;
	}
	
	/**
	 * @return factor
	 */
	public BigInteger getFactor() {
		return this.factor;
	}
}
