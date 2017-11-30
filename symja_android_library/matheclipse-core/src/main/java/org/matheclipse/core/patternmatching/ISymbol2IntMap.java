package org.matheclipse.core.patternmatching;

import org.matheclipse.core.interfaces.IExpr;

/**
 * Interface for mapping ISymbol objects to int values.
 * 
 */
public interface ISymbol2IntMap {

	/**
	 * Gives the number of symbols used in this map.
	 * 
	 * @return the number of symbols used in this map.
	 */
	public int size();

	/**
	 * Gives <code>true</code> if there's no symbol used in this map.
	 * 
	 * @return <code>true</code> if there's no symbol used in this map.
	 */
	public boolean isEmpty();

	/**
	 * Get the <code>int</code> value mapped to the given pattern or symbol.
	 * 
	 * @param patternOrSymbol
	 *            the given pattern or symbol
	 * @return <code>-1</code> if the symbol isn't available in this map.
	 */
	public int get(IExpr patternOrSymbol);

}
