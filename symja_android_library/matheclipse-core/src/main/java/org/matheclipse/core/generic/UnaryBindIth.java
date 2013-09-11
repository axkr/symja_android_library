package org.matheclipse.core.generic;

import org.matheclipse.core.generic.interfaces.IUnaryIndexFunction;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Clone a given AST and set the i-th argument of the new AST to {@code arg} in
 * the {@code apply} method.
 * 
 */
public class UnaryBindIth implements IUnaryIndexFunction<IExpr, IExpr> {
	protected final IAST fConstant;

	/**
	 * The {@code constant} AST will be cloned in the {@code apply} method.
	 * 
	 * @param constant
	 *          a &quot;template AST&quot; with all arguments set with
	 *          a predefined value.
	 */
	public UnaryBindIth(final IAST constant) {
		super();
		fConstant = constant;
	}

	/**
	 * Clone the given AST and set the i-th argument of the new AST to {@code arg}
	 * .
	 * 
	 * @param index
	 *          the i-th index should be used
	 * @param arg
	 *          the i-th argument in the new AST
	 */
	public IExpr apply(int index, final IExpr arg) {
		final IAST ast = fConstant.clone();
		ast.set(index, arg);
		return ast;
	}

}
