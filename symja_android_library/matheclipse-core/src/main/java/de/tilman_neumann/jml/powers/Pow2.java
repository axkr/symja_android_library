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
package de.tilman_neumann.jml.powers;

import static de.tilman_neumann.jml.base.BigIntConstants.*;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Pow2 {
	
	/**
	 * Power of 2 with integer exponent.
	 * @param n
	 * @return
	 */
	public static BigDecimal pow2(int n) {
		if (n >= 0) {
			return new BigDecimal(I_1.shiftLeft(n));
		}
		return new BigDecimal(I_5.pow(-n), -n);
	}
	
	/**
	 * Multiplication with the n.th power of 2.
	 * 
	 * @param n Exponent
	 * @return x*2^n
	 */
	public static BigDecimal mulPow2(BigDecimal x, int n) {
		if (n > 0) {
			BigDecimal result = new BigDecimal(x.unscaledValue().shiftLeft(n), x.scale());
			//LOG.debug("mulPow2(" + x + ", " + n + ") = " + result);
			return result;
		}
		if (n < 0) {
			//LOG.debug("mulPow2(" + x + ", " + n + ")");
			return divPow2(x, -n);
		}
		// Exponent ist genau 0:
		return x;
	}
	
	/**
	 * Division by the n.th power of 2.
	 * 
	 * @param n Exponent
	 * @return x/2^n
	 */
	public static BigDecimal divPow2(BigDecimal x, int n) {
		if (n > 0) {
			BigDecimal result = null;
			int xScale = x.scale();
			BigInteger xUnscaled = x.unscaledValue();
			int containedTwos = xUnscaled.getLowestSetBit();
			int missingTwos = n-containedTwos;
			//LOG.debug("x=" + x + "(unscaled=" + xUnscaled + ",scale=" + xScale + "), containedTwos=" + containedTwos + ", missingTwos=" + missingTwos);
			if (missingTwos > 0) {
				// Integer-Teil um fehlende Stellen erweitern:
				xScale += missingTwos;
				x = x.setScale(xScale);
				xUnscaled = x.unscaledValue();
			}
			//LOG.debug("x=" + x + "(unscaled=" + xUnscaled + ",scale=" + xScale + "), containedTwos=" + containedTwos + ", missingTwos=" + missingTwos);
			result = new BigDecimal(xUnscaled.shiftRight(n), xScale);
			//LOG.debug("divPow2(" + x + ", " + n + ") = " + result);
			return result;
		}
		
		if (n==-2147483648) { // NaN
			throw new RuntimeException("-" + n + " = " + -n);
		}
		
		if (n < 0) {
			//LOG.debug("divPow2(" + x + ", " + n + ")");
			return mulPow2(x, -n);
		}
		// Exponent is 0:
		return x;
	}
}
