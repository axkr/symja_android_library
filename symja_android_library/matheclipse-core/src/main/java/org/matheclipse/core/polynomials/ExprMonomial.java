package org.matheclipse.core.polynomials;

import java.util.Map;

import org.matheclipse.core.interfaces.IExpr;
  

/**
 * Monomial class. Represents pairs of exponent vectors and coefficients. Adaptor for Map.Entry.
 * 
 */
public final class ExprMonomial {

	/**
	 * Exponent of monomial.
	 */
	public final ExpVectorLong e;

	/**
	 * Coefficient of monomial.
	 */
	public final IExpr c;

	/**
	 * Constructor of monomial.
	 * 
	 * @param me
	 *            a MapEntry.
	 */
	public ExprMonomial(Map.Entry<ExpVectorLong, IExpr> me) {
		this(me.getKey(), me.getValue());
	}

	/**
	 * Constructor of monomial.
	 * 
	 * @param e
	 *            exponent.
	 * @param c
	 *            coefficient.
	 */
	public ExprMonomial(ExpVectorLong e, IExpr c) {
		this.e = e;
		this.c = c;
	}

	/**
	 * Getter for exponent.
	 * 
	 * @return exponent.
	 */
	public ExpVectorLong exponent() {
		return e;
	}

	/**
	 * Getter for coefficient.
	 * 
	 * @return coefficient.
	 */
	public IExpr coefficient() {
		return c;
	}

	/**
	 * String representation of Monomial.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return c.toString() + " " + e.toString();
	}

}
