package org.matheclipse.core.interfaces;

/**
 * An expression representing a symbol (i.e. variable- constant- or function-name)
 * 
 */
public interface IBuiltInSymbol extends ISymbol {
	/**
	 * Get the ordinal number of this built-in symbol in the enumeration of built-in symbols.
	 * 
	 * @return
	 */
	public int ordinal();

	/**
	 * Get the current evaluator for this symbol
	 * 
	 * @return the evaluator which is associated to this symbol or <code>null</code> if no evaluator is associated
	 */
	public IEvaluator getEvaluator();

	/**
	 * Set the current evaluator which is associated to this symbol
	 */
	public void setEvaluator(IEvaluator module);

}