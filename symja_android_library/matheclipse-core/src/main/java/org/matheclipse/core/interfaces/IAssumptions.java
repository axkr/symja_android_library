package org.matheclipse.core.interfaces;

public interface IAssumptions {
	/**
	 * Gives <code>true</code>, if the symbol is assumed to be a negative value, <code>false</code> in all other cases.
	 * 
	 * @param symbol
	 * @return
	 */
	public boolean isNegative(ISymbol symbol);

	/**
	 * Gives <code>true</code>, if the symbol is assumed to be a positive value, <code>false</code> in all other cases.
	 * 
	 * @param symbol
	 * @return
	 */
	public boolean isPositive(ISymbol symbol);
	
	/**
	 * Gives <code>true</code>, if the symbol is assumed to be a non-negative value, <code>false</code> in all other cases.
	 * 
	 * @param symbol
	 * @return
	 */
	public boolean isNonNegative(ISymbol symbol);

	/**
	 * Gives <code>true</code>, if the symbol is assumed to be an integer value, <code>false</code> in all other cases.
	 * 
	 * @param symbol
	 * @return
	 */
	public boolean isInteger(ISymbol symbol);
}
