package org.matheclipse.core.eval.interfaces;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Common interface for built-in MathEclipse functions.
 * 
 */
public interface IFunctionEvaluator extends IEvaluator {
	/**
	 * Symbolic evaluation of a function. <code>ast.get(0)</code> contains the
	 * <i>head</i> (i.e. the function symbol) of this abstract syntax tree (AST).
	 * 
	 * @param ast
	 *          the abstract syntax tree (AST) which should be evaluated
	 * @return the evaluated object or <code>null</code>, if evaluation isn't
	 *         possible
	 */
	public IExpr evaluate(IAST ast);

	/**
	 * Numeric evaluation of a function. <code>ast.get(0)</code> contains the
	 * <i>head</i> (i.e. the function symbol) of this abstract syntax tree (AST).
	 * 
	 * @param ast
	 *          the abstract syntax tree (AST) which should be evaluated
	 * @return the evaluated object or <code>null</code>, if evaluation isn't
	 *         possible
	 */
	public IExpr numericEval(IAST ast);

}
