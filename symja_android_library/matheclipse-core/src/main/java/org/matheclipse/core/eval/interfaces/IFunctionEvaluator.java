package org.matheclipse.core.eval.interfaces;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Common interface for built-in Symja functions.
 * 
 */
public interface IFunctionEvaluator extends IEvaluator {
	/**
	 * Symbolic evaluation of a function. <code>ast.get(0)</code> contains the <i>head</i> (i.e. the function symbol) of this
	 * abstract syntax tree (AST). <code>ast.get(1)</code> to <code>ast.get(n)</code> contains the first to n-th argument of the
	 * function.<br />
	 * If necessary use the methods from the <code>Validate</code> class to check the number or types of arguments in the evaluate
	 * method.<br/>
	 * <b>Note:</b> if the symbolic evaluation isn't possible or no result is found the evaluate method returns with a
	 * <code>null</code> value without throwing an exception!<br/>
	 * 
	 * @param ast
	 *            the abstract syntax tree (AST) which should be evaluated
	 * @return the evaluated object or <code>null</code>, if evaluation isn't possible
	 * @see org.matheclipse.core.eval.exception.Validate
	 */
	public IExpr evaluate(IAST ast);

	/**
	 * Numeric evaluation of a function. <code>ast.get(0)</code> contains the <i>head</i> (i.e. the function symbol) of this
	 * abstract syntax tree (AST). <code>ast.get(1)</code> to <code>ast.get(n)</code> contains the first to n-th argument of the
	 * function.<br />
	 * If necessary use the methods from the <code>Validate</code> class to check the number or types of arguments in the evaluate
	 * method.
	 * 
	 * @param ast
	 *            the abstract syntax tree (AST) which should be evaluated
	 * @return the evaluated object or <code>null</code>, if evaluation isn't possible
	 * @see org.matheclipse.core.eval.exception.Validate
	 */
	public IExpr numericEval(IAST ast);

}
