package org.matheclipse.core.expression;

import org.matheclipse.core.interfaces.ISymbol;

/**
 * Interface for observing the creation of symbols.
 *
 */
public interface ISymbolObserver {
	/**
	 * If a symbol with a starting upper case character in its name is created this
	 * method will be called.
	 * 
	 * @param symbol
	 * @return
	 */
	boolean createPredefinedSymbol(String symbol);

	/**
	 * If a user symbol with a starting '$' character in its name is created this
	 * method will be called.
	 * 
	 * @param symbol
	 */
	void createUserSymbol(ISymbol symbol);
}
