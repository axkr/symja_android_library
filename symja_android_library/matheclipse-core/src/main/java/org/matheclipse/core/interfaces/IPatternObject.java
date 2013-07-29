package org.matheclipse.core.interfaces;

public interface IPatternObject {
	/**
	 * Get the associated symbol for this pattern-object
	 * 
	 * @return
	 */
	public ISymbol getSymbol();

	/**
	 * Get the pattern-matchers index in the <code>PatternMap</code>
	 * 
	 * @return
	 */
	public int getIndex();

	/**
	 * Set the pattern-matchers index in the <code>PatternMap</code>
	 * 
	 * @return
	 */
	public void setIndex(final int i);
}
