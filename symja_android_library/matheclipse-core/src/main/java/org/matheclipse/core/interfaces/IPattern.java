package org.matheclipse.core.interfaces;

import org.matheclipse.core.patternmatching.PatternMap;

/**
 * Interface for pattern objects (i.e. x_)
 * 
 */
public interface IPattern extends IPatternObject, IExpr {

	/**
	 * Return <code>true</code>, if the expression fullfills the patterns
	 * additional condition
	 * 
	 * @param expr
	 * @return
	 */
	public boolean isConditionMatched(IExpr expr, PatternMap patternMap);
	
	/**
	 * Get the default value for this pattern if available.
	 * 
	 * @return <code>null</code> if no default value is available
	 */
	public IExpr getDefaultValue();
}
