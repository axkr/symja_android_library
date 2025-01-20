/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2018-2024 Tilman Neumann - tilman.neumann@web.de
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
package de.tilman_neumann.jml;

import java.math.BigDecimal;

import static de.tilman_neumann.jml.base.BigDecimalConstants.*;

/**
 * Computation of values of the Chebyshev polynomials.
 * 
 * First kind:
 * T_0(x) = 1
 * T_1(x) = x
 * T_2(x) = 2x^2 - 1
 * T_3(x) = 4x^3 - 3x
 * T_4(x) = 8x^4 - 8x^2 + 1
 * T_5(x) = 16x^5 - 20x^3 + 5x
 * T_6(x) = 32x^6 - 48x^4 + 18x^2 - 1
 * T_7(x) = 64x^7 - 112x^5 + 56x^3 - 7x
 * T_8(x) = 128x^8 - 256x^6 + 160x^4 - 32x^2 + 1
 * ...
 * 
 * Second kind:
 * U_0(x) = 1
 * U_1(x) = 2x
 * U_2(x) = 4x^2 - 1
 * U_3(x) = 8x^3 - 4x
 * U_4(x) = 16x^4 - 12x^2 + 1
 * U_5(x) = 32x^5 - 32x^3 + 6x
 * U_6(x) = 64x^6 - 80x^4 + 24x^2 - 1
 * U_7(x) = 128x^7 - 192x^5 + 80x^3 - 8x
 * ...
 * 
 * @author Tilman Neumann
 */
public class ChebyshevPolynomials {
	
	/**
	 * Recurrent computation of Chebyshev polynomials of the first kind.
	 * @param n degree
	 * @param x argument
	 * @return T_n(x)
	 */
	public static BigDecimal ChebyshevT(int n, BigDecimal x) {
		if (n==0) return F_1;
		if (n==1) return x;
		return F_2.multiply(x).multiply(ChebyshevT(n-1, x)).subtract(ChebyshevT(n-2, x));
	}

	/**
	 * Closed computation formulas of Chebyshev polynomials of the first kind for n<=4.
	 * @param n must be <= 4
	 * @param x
	 * @return T_n(x)
	 * @throws IllegalArgumentException for n>4
	 */
	static BigDecimal ChebyshevTClosed(int n, BigDecimal x) throws IllegalArgumentException {
		switch (n) {
		case 0: return F_1;
		case 1: return x;
		case 2: return F_2.multiply(x.pow(2)).subtract(F_1);
		case 3: return F_4.multiply(x.pow(3)).subtract(F_3.multiply(x));
		case 4: return F_8.multiply(x.pow(4)).subtract(F_8.multiply(x.pow(2))).add(F_1);
		default: throw new IllegalArgumentException("n = " + n);
		}
	}
	
	/**
	 * Recurrent computation of Chebyshev polynomials of the second kind.
	 * @param n degree
	 * @param x argument
	 * @return U_n(x)
	 */
	public static BigDecimal ChebyshevU(int n, BigDecimal x) {
		if (n==0) return F_1;
		if (n==1) return F_2.multiply(x);
		return F_2.multiply(x).multiply(ChebyshevU(n-1, x)).subtract(ChebyshevU(n-2, x));
	}

	/**
	 * Closed computation formulas of Chebyshev polynomials of the second kind for n<=4.
	 * @param n must be <= 4
	 * @param x
	 * @return U_n(x)
	 * @throws IllegalArgumentException for n>4
	 */
	static BigDecimal ChebyshevUClosed(int n, BigDecimal x) throws IllegalArgumentException {
		switch (n) {
		case 0: return F_1;
		case 1: return F_2.multiply(x);
		case 2: return F_4.multiply(x.pow(2)).subtract(F_1);
		case 3: return F_8.multiply(x.pow(3)).subtract(F_4.multiply(x));
		case 4: return F_16.multiply(x.pow(4)).subtract(F_12.multiply(x.pow(2))).add(F_1);
		default: throw new IllegalArgumentException("n = " + n);
		}
	}
}
