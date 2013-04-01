package org.matheclipse.core.eval.interfaces;

import org.matheclipse.core.interfaces.IExpr;

/**
 * 
 */
public interface ICreatePatternMatcher {
	/**
	 * Returns an array of 2 objects with type IPatternMatcher and IExpr
	 * 
	 * @param leftHandSide
	 * @param rightHandSide
	 * @return
	 */
	public Object[] createPatternMatcher(IExpr leftHandSide, IExpr rightHandSide);
}
