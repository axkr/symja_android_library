package org.matheclipse.core.polynomials;

import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.interfaces.IExpr;

import edu.jas.arith.BigRational;
import edu.jas.poly.GenPolynomial;

public interface IPartialFractionGenerator {

	/**
	 * Allocate memory for the <code>Plus()</code> operator
	 */
	public abstract void allocPlus(int size);

	/**
	 * Set the used JAS instance.
	 * 
	 * @param jas
	 */
	public abstract void setJAS(JASConvert<BigRational> jas);

	/**
	 * Get the final result.
	 * 
	 * @return
	 */
	public abstract IExpr getResult();

	/**
	 * Add the non-fractional part of the partial fraction decomposition.
	 * 
	 * @param genPolynomial
	 */
	public abstract void addNonFractionalPart(GenPolynomial<BigRational> genPolynomial);

	/**
	 * Add a single partial fraction.
	 * 
	 * @param genPolynomial
	 * @param Di_1
	 * @param j
	 */
	public abstract void addSinglePartialFraction(GenPolynomial<BigRational> genPolynomial,
			GenPolynomial<BigRational> Di_1, int j);

}