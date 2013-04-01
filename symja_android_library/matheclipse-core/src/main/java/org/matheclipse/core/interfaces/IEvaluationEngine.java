package org.matheclipse.core.interfaces;

/**
 * Interface for an evaluation engine
 *
 */
public interface IEvaluationEngine {
	/**
	 * Evaluate an object, if evaluation is not possible return the input object
	 *
	 * @param expr
	 *          the object which should be evaluated
	 * @return the evaluated object
	 *
	 */
  public IExpr evalWithoutNumericReset(IExpr expr);

  /**
   * Resets internal flags and states:
   *
   */
  public void init();

}
