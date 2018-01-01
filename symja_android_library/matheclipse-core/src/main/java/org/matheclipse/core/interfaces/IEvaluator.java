package org.matheclipse.core.interfaces;

/**
 * An IEvaluator can be linked to an ISymbol to define
 * the evaluation behaviour of the symbol at creation time.
 *
 */
public interface IEvaluator {
	default void join() {
		// do nothing
	}
	
	/**
	 * This method will be called every time a new ISymbol will be created.
	 * In this method you can set ISymbol attributes or constants for the symbol
	 *
	 * @param newSymbol the symbol which should be set up
	 */
	public void setUp(ISymbol newSymbol);
}
