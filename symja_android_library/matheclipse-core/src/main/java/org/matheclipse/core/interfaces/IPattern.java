package org.matheclipse.core.interfaces;


/**
 * Interface for pattern objects (i.e. x_)
 * 
 */
public interface IPattern extends IPatternObject, IExpr {

	/**
	 * Get the additional patterns condition expression
	 * 
	 * @return may return null;
	 */
	public IExpr getCondition();

	/**
	 * Return <code>true</code>, if the expression fullfills the patterns
	 * additional condition
	 * 
	 * @param expr
	 * @return
	 */
	public boolean isConditionMatched(IExpr expr);

	/**
	 * Return <code>true</code>, if the expression is a blank pattern
	 * 
	 * @return
	 */
	public boolean isBlank();

	/**
	 * Return <code>true</code>, if the expression is a pattern with an associated
	 * default value,
	 * 
	 * @return
	 */
	public boolean isDefault();
}
