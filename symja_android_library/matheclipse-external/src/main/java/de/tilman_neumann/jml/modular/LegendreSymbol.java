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
package de.tilman_neumann.jml.modular;

import static de.tilman_neumann.jml.base.BigIntConstants.I_1;

import java.math.BigInteger;

import de.tilman_neumann.jml.modular.ModularPower;

/**
 * Computation of the Legendre symbol using Eulers formula.
 * @author Tilman Neumann
 */
public class LegendreSymbol {
	
	private ModularPower mpe = new ModularPower();
	
	/**
	 * Computes the Legendre symbol L(a|p) via Eulers formula.
	 * p is BigInteger and must be an odd prime.
	 * 
	 * Very slow; if possible use the Jacobi symbol!
	 * 
	 * @param a
	 * @param p
	 * @return
	 */
	public int EulerFormula(BigInteger a, BigInteger p) {
		BigInteger modPow = a.modPow(p.subtract(I_1).shiftRight(1), p);
		return (modPow.compareTo(I_1)>0) ? modPow.subtract(p).intValue() : modPow.intValue();
	}

	/**
	 * Computes the Legendre symbol L(a|p) via Eulers formula.
	 * p is int and must be an odd prime.
	 * 
	 * Eulers formula with int p is very fast!
	 * 
	 * @param a
	 * @param p
	 * @return
	 */
	public int EulerFormula(BigInteger a, int p) {
		int modPow = mpe.modPow(a, (p-1)>>1, p);
		return (modPow>1) ? modPow-p : modPow;
	}

	/**
	 * Computes the Legendre symbol L(a|p) via Eulers formula for a, p int.
	 * p must be an odd prime.
	 * 
	 * Eulers formula with int p is very fast!
	 *
	 * @param a
	 * @param p
	 * @return
	 */
	public int EulerFormula(int a, int p) {
		int modPow = mpe.modPow(a, (p-1)>>1, p);
		return (modPow>1) ? modPow-p : modPow;
	}
}
