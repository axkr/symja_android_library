package org.matheclipse.core.eval.util;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;

public interface IAssumptions {
	
	public IAssumptions addAssumption(IAST ast);
	
	/**
	 * Gives <code>true</code>, if the expression is assumed to be an algebraic value (i.e. an element of the
	 * <code>Algebraics</code> domain), <code>false</code> in all other cases.
	 * 
	 * @param expr
	 * @return
	 */
	public boolean isAlgebraic(IExpr expr);

	/**
	 * Gives <code>true</code>, if the expression is assumed to be a boolean value (i.e. an element of the
	 * <code>Booleans</code> domain), <code>false</code> in all other cases.
	 * 
	 * @param expr
	 * @return
	 */
	public boolean isBoolean(IExpr expr);

	/**
	 * Gives <code>true</code>, if the expression is assumed to be a complex value (i.e. an element of the
	 * <code>Complexes</code> domain), <code>false</code> in all other cases.
	 * 
	 * @param expr
	 * @return
	 */
	public boolean isComplex(IExpr expr);

	/**
	 * Gives <code>true</code>, if the expression is assumed to be greater equal number, <code>false</code> in all other
	 * cases.
	 * 
	 * @param expr
	 * @param number
	 * @return
	 */
	public boolean isGreaterEqual(IExpr expr, ISignedNumber number);

	/**
	 * Gives <code>true</code>, if the expression is assumed to be greater than number, <code>false</code> in all other
	 * cases.
	 * 
	 * @param expr
	 * @param number
	 * @return
	 */
	public boolean isGreaterThan(IExpr expr, ISignedNumber number);

	/**
	 * Gives <code>true</code>, if the expression is assumed to be an integer value (i.e. an element of the
	 * <code>Integers</code> domain), <code>false</code> in all other cases.
	 * 
	 * @param expr
	 * @return
	 */
	public boolean isInteger(IExpr expr);

	/**
	 * Gives <code>true</code>, if the expression is assumed to be less than number, <code>false</code> in all other
	 * cases.
	 * 
	 * @param expr
	 * @param number
	 * @return
	 */
	public boolean isLessThan(IExpr expr, ISignedNumber number);

	/**
	 * Gives <code>true</code>, if the expression is assumed to be a negative value, <code>false</code> in all other
	 * cases.
	 * 
	 * @param expr
	 * @return
	 */
	public boolean isNegative(IExpr expr);

	/**
	 * Gives <code>true</code>, if the expression is assumed to be a non-negative value, <code>false</code> in all other
	 * cases.
	 * 
	 * @param expr
	 * @return
	 */
	public boolean isNonNegative(IExpr expr);

	/**
	 * Gives <code>true</code>, if the expression is assumed to be a positive value, <code>false</code> in all other
	 * cases.
	 * 
	 * @param expr
	 * @return
	 */
	public boolean isPositive(IExpr expr);

	/**
	 * Gives <code>true</code>, if the expression is assumed to be a prime number (i.e. an element of the
	 * <code>Primes</code> domain), <code>false</code> in all other cases.
	 * 
	 * @param expr
	 * @return
	 */
	public boolean isPrime(IExpr expr);

	/**
	 * Gives <code>true</code>, if the expression is assumed to be a rational value (i.e. an element of the
	 * <code>Rationals</code> domain), <code>false</code> in all other cases.
	 * 
	 * @param expr
	 * @return
	 */
	public boolean isRational(IExpr expr);

	/**
	 * Gives <code>true</code>, if the expression is assumed to be an integer value (i.e. an element of the
	 * <code>Reals</code> domain), <code>false</code> in all other cases.
	 * 
	 * @param expr
	 * @return
	 */
	public boolean isReal(IExpr expr);
}
