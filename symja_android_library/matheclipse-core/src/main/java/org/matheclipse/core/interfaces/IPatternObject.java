package org.matheclipse.core.interfaces;

import org.matheclipse.core.patternmatching.PatternMap;

/**
 * Interface for pattern objects (i.e. x_, x__)
 * 
 */
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
	 * @param pm
	 *            the PatternMap from which we determine the index.
	 * @return
	 */
	public int getIndex(PatternMap pm);

}
