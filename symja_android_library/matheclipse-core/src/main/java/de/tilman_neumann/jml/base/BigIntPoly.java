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

//import org.apache.log4j.Logger;

import static de.tilman_neumann.jml.base.BigIntConstants.*;

/**
 * A simple integer polynomial implementation, once inspired by http://www.strw.leidenuniv.nl/~mathar/progs/FI/oeis_8java.html
 * (now dead link, sorry)
 * 
 * @author Tilman Neumann 
 */
public class BigIntPoly extends ArrayList<BigInteger> {
//	@SuppressWarnings("unused")
//	private static final Logger LOG = Logger.getLogger(BigIntPoly.class);

	private static final long serialVersionUID = 5031959748489950586L;

	/**
	 * Constructor for an empty polynomial with initial capacity n.
	 * @param n
	 */
	public BigIntPoly(int n) {
		super(n);
		this.add(I_0);
		this.add(I_1);
	}
	
	/**
	 * Set a polynomial coefficient.
	 * @param n the zero-based index of the coefficient. n=0 for the constant term. 
	 * If the polynomial has not yet the degree to need this coefficient,
	 * the intermediate coefficients are implicitly set to zero.
	 * @param value the new value of the coefficient.
	 */
	public BigInteger set(final int n, final BigInteger value) {
		if (n<0) throw new IllegalArgumentException("index must be non-negative, but is " + n);
		
		if (n>=0 && n<this.size()) {
			return super.set(n,value);
		}
		// fill intermediate powers with coefficients of zero
		while (this.size() < n ) {
			this.add(BigInteger.ZERO);
		}
		this.add(value);
		return BigInteger.ZERO;
	}
	
	/**
	 * Multiply by another polynomial.
	 * @param val the other polynomial
	 * @return the product of this with the other polynomial
	 */
	public BigIntPoly multiply(BigIntPoly val)
	{
		// the degree of the result is the sum of the two degrees.
		final int nmax = this.degree()+val.degree();
		BigIntPoly resul = new BigIntPoly(nmax+1);
		
		for(int n=0; n<=nmax; n++) {
			BigInteger coef = BigInteger.ZERO;
			for (int nleft=0; nleft <= n ; nleft++) {
				// LOG.debug(""+nleft+" "+at(nleft).toString() ) ;
				// LOG.debug(" * "+(n-nleft)+" "+at(n-nleft).toString() ) ;
				// LOG.debug(" = "+ at(nleft).multiply(val.at(n-nleft)).toString() ) ;
				coef = coef.add(this.at(nleft).multiply(val.at(n-nleft))) ;
			}
			resul.set(n,coef) ;
		}
		return resul ;
	}
	
	/** 
	 * Retrieve a polynomial coefficient.
	 * @param n the zero-based index of the coefficient. n=0 for the constant term. 
	 * @return the polynomial coefficient in front of x^n.
	 */
	public BigInteger at(final int n) {
		if ( n>=0 && n<this.size()) {
			return super.get(n);
		}
		return BigInteger.ZERO;
	}

	/**
	 * @return the polynomial degree.
	 */
	public int degree() {
		return this.size()-1;
	}
}
