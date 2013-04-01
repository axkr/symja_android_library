package org.matheclipse.core.generic;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

import com.google.common.base.Function;

/**
 * Collect the arguments in a new constructed internal AST.
 * 
 */
public class UnaryCollect implements Function<IExpr, IExpr> {

	protected IAST fAST;

	/**
	 * Define an unary AST with the header <code>head</code>. This new AST
	 * collects the arguments of the <code>apply()</code> method.
	 * 
	 * @param head
	 *          the AST's head expresion
	 */
	public UnaryCollect(final IExpr head) {
		fAST = F.ast(head, 1, false);
	}

	/**
	 * Add <code>firstArg</code> to this collection
	 * 
	 */
	public IExpr apply(final IExpr firstArg) {
		fAST.add(firstArg);
		return fAST;
	}

	public IAST getCollectedAST() {
		return fAST;
	}

}
