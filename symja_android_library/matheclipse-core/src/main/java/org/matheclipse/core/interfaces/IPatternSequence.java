package org.matheclipse.core.interfaces;

import org.matheclipse.core.patternmatching.PatternMap;

/**
 * Interface for pattern sequence objects (i.e. x__)
 */
public interface IPatternSequence extends IPatternObject, IExpr {

	/**
	 * Get the additional pattern sequences condition expression
	 * 
	 * @return may return null;
	 */
	public IExpr getCondition();

	public boolean matchPatternSequence(final IAST sequence, PatternMap patternMap);
	
	/**
	 * Return <code>true</code>, if all of the elements in the <code>sequence</code> fulfill the pattern sequences additional
	 * condition
	 * 
	 * @param sequence
	 * @return
	 */
	public boolean isConditionMatchedSequence(IAST sequence);

	/**
	 * Return <code>true</code>, if the expression is a blank sequence pattern
	 * 
	 * @return
	 */
	public boolean isBlank();

	/**
	 * Return <code>true</code>, if the expression is a pattern sequence with an associated default value,
	 * 
	 * @return
	 */
	public boolean isDefault();
}
