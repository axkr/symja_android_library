package org.matheclipse.core.generic;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IUnaryIndexFunction;

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
	@Override
	public IExpr apply(int index, final IExpr arg) {
		return fConstant.setAtCopy(index, arg);
	}

}
