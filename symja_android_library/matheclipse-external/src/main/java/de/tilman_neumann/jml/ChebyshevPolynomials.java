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
package de.tilman_neumann.jml;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.log4j.Logger;

import de.tilman_neumann.util.ConfigUtil;

import static de.tilman_neumann.jml.base.BigIntConstants.*;
import static de.tilman_neumann.jml.base.BigDecimalConstants.*;

/**
 * Computation of values of the Chebyshev polynomials.
 * @author Tilman Neumann
 */
public class ChebyshevPolynomials {
	private static final Logger LOG = Logger.getLogger(ChebyshevPolynomials.class);
	
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
	 * Test.
	 * @param args ignored
	 */
	public static void main(String[] args) {
    	ConfigUtil.initProject();
		for (int n=0; n<=10; n++) {
			String nStr = "n=" + n + ": T_" + n + "(x) = ";
			for (BigInteger x=I_0; x.compareTo(I_10)<0; x=x.add(I_1)) {
				BigDecimal T_n_of_x = ChebyshevT(n, new BigDecimal(x));
				nStr += T_n_of_x + ", ";
			}
			nStr = nStr.substring(0, nStr.length()-2);
			LOG.info(nStr);
		}
		for (int n=0; n<=10; n++) {
			String nStr = "n=" + n + ": U_" + n + "(x) = ";
			for (BigInteger x=I_0; x.compareTo(I_10)<0; x=x.add(I_1)) {
				BigDecimal U_n_of_x = ChebyshevU(n, new BigDecimal(x));
				nStr += U_n_of_x + ", ";
			}
			nStr = nStr.substring(0, nStr.length()-2);
			LOG.info(nStr);
		}
	}
}
