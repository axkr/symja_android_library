package org.matheclipse.core.eval.interfaces;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Common interface for built-in Symja functions.
 * 
 */
public interface IFunctionEvaluator extends IEvaluator {

	/**
	 * <p>
	 * <b>Symbolic evaluation</b> of a function. The method
	 * <code>ast.get(0)</code> (or alternatively <code>ast.head()</code>)
	 * contains the <i>head</i> (i.e. the function symbol) of this abstract
	 * syntax tree (AST).
	 * </p>
	 * <p>
	 * From <code>ast.get(1)</code> to <code>ast.get(n)</code> the
	 * <code>ast</code> contains the first to n-th argument of the function
	 * (alternatively you get the first to fifth argument with the methods
	 * <code>arg1()</code>, <code>arg2()</code>,... <code>arg5()</code>).
	 * </p>
	 * <p>
	 * <b>Example:</b> the expression <code>Binomial(n,m)</code> is represented
	 * as AST with <code>ast.head() <=> F.Binomial</code>,
	 * <code>ast.arg1() <=> n</code> and <code>ast.arg2() <=> m</code>
	 * </p>
	 * <p>
	 * If necessary use the methods from the <code>Validate</code> class to
	 * check the number or types of arguments in the evaluate method.
	 * </p>
	 * <p>
	 * <b>Note:</b> if the symbolic evaluation isn't possible or no result is
	 * found the evaluate method returns with a <code>F#NIL</code> value without
	 * throwing an exception!
	 * </p>
	 * 
	 * @param ast
	 *            the abstract syntax tree (AST) which should be evaluated
	 * @param engine
	 *            the users current evaluation engine
	 * @return the evaluated object or <code>F#NIL</code>, if evaluation isn't
	 *         possible
	 * @see org.matheclipse.core.eval.exception.Validate
	 * @see IExpr#head()
	 * @see IAST#arg1()
	 * @see IAST#arg2()
	 * @see IAST#arg3()
	 */
	public IExpr evaluate(IAST ast, EvalEngine engine);

	/**
	 * <p>
	 * <b>Numeric evaluation</b> of a function. The method
	 * <code>ast.get(0)</code> (or alternatively <code>ast.head()</code>)
	 * contains the <i>head</i> (i.e. the function symbol) of this abstract
	 * syntax tree (AST).
	 * </p>
	 * <p>
	 * From <code>ast.get(1)</code> to <code>ast.get(n)</code> the
	 * <code>ast</code> contains the first to n-th argument of the function
	 * (alternatively you get the first to fifth argument with the methods
	 * <code>arg1()</code>, <code>arg2()</code>,... <code>arg5()</code>).
	 * </p>
	 * <p>
	 * <b>Example:</b> the expression <code>Binomial(n,m)</code> is represented
	 * as AST with <code>ast.head() <=> F.Binomial</code>,
	 * <code>ast.arg1() <=> n</code> and <code>ast.arg2() <=> m</code>
	 * </p>
	 * <p>
	 * If necessary use the methods from the <code>Validate</code> class to
	 * check the number or types of arguments in the evaluate method.
	 * </p>
	 * <p>
	 * <b>Note:</b> if the symbolic evaluation isn't possible or no result is
	 * found the evaluate method returns with a <code>F#NIL</code> value without
	 * throwing an exception!
	 * </p>
	 * 
	 * @param ast
	 *            the abstract syntax tree (AST) which should be evaluated
	 * @param engine
	 *            the users current evaluation engine
	 * @return the evaluated object or <code>null</code>, if evaluation isn't
	 *         possible
	 * @see org.matheclipse.core.eval.exception.Validate
	 * @see IExpr#head()
	 * @see IAST#arg1()
	 * @see IAST#arg2()
	 * @see IAST#arg3()
	 */
	public IExpr numericEval(IAST ast, EvalEngine engine);

}
