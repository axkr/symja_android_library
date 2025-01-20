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

/**
 * A two-dimensional grid of big integers.
 * 
 * @author Tilman Neumann
 */
public class BigIntGrid extends NumberGrid<BigInteger> {

	private static final long serialVersionUID = 9189914650661627444L;

	/**
	 * Simplified constructor with offsets 1.
	 * 
	 * @param xLabel The letter to use for the x-axis
	 * @param xStart The start value for the x-axis-values
	 * @param yLabel The letter to use for the y-axis
	 * @param yStart The start value for the y-axis-values
	 */
	public BigIntGrid(String xLabel, int xStart, String yLabel, int yStart) {
		super(xLabel, xStart, yLabel, yStart);
	}

	/**
	 * Full constructor with all options.
	 * 
	 * @param xLabel The letter to use for the x-axis
	 * @param xStart The start value for the x-axis-values
	 * @param xIncrement The increment for the x-axis-values
	 * @param yLabel The letter to use for the y-axis
	 * @param yStart The start value for the y-axis-values
	 * @param yIncrement The increment for the y-axis-values
	 */
	public BigIntGrid(String xLabel, int xStart, int xIncrement, String yLabel, int yStart, int yIncrement) {
		super(xLabel, xStart, xIncrement, yLabel, yStart, yIncrement);
	}
}
