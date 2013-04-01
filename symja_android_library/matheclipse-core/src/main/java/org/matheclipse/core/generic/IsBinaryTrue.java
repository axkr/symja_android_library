package org.matheclipse.core.generic;

import java.util.Comparator;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.generic.interfaces.BiPredicate;

/**
 * Check if the evaluation of a binary AST object gives <code>True</code>
 * 
 */
public class IsBinaryTrue<E extends IExpr> implements  BiPredicate<E>, Comparator<E> {
	protected final EvalEngine fEngine;

	protected final IAST fAST;

	/**
	 * Define a binary AST with the header <code>head</code>.
	 * 
	 * @param head
	 *          the AST's head expresion
	 */
	public IsBinaryTrue(final IExpr head) {
		fEngine = EvalEngine.get();
		fAST = F.ast(head, 1, false);
	}

	/**
	 * Check if the evaluation of a binary AST object gives <code>True</code> by
	 * settings it's first argument to <code>firstArg</code> and settings it's
	 * second argument to <code>secondArg</code>
	 * 
	 */
	public boolean apply(final IExpr firstArg, final IExpr secondArg) {
		final IAST ast = fAST.clone();
		ast.add(firstArg);
		ast.add(secondArg);
		if (fEngine.evaluate(ast).equals(F.True)) {
			return true;
		}
		return false;
	}

	public int compare(final IExpr firstArg, final IExpr secondArg) {
		final IAST ast = fAST.clone();
		ast.add(firstArg);
		ast.add(secondArg);
		IExpr temp = fEngine.evaluate(ast);
		if (temp.equals(F.True)) {
			return 1;
		}
		if (temp.equals(F.False)) {
			return -1;
		}
		return 0;
	}
	
}
