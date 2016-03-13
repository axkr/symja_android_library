package org.matheclipse.core.generic;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import java.util.function.BiPredicate;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Check if the evaluation of a binary AST object gives <code>False</code>
 * 
 */
public class IsBinaryFalse<E extends IExpr> extends ExprComparator implements BiPredicate<E, E> {
	protected final EvalEngine fEngine;

	protected final IAST fAST;

	/**
	 * Define a binary AST with the header <code>head</code>.
	 * 
	 * @param head
	 *            the AST's head expresion
	 */
	public IsBinaryFalse(final IExpr head) {
		fEngine = EvalEngine.get();
		fAST = F.ast(head, 2, true);
	}

	/**
	 * Check if the evaluation of a binary AST object gives <code>False</code> by settings it's first argument to
	 * <code>firstArg</code> and settings it's second argument to <code>secondArg</code>
	 * 
	 */
	public boolean test(final IExpr firstArg, final IExpr secondArg) {
		fAST.set(1, firstArg);
		fAST.set(2, secondArg);
		if (fEngine.evaluate(fAST).equals(F.False)) {
			return true;
		}
		return false;
	}

	public int compare(final IExpr firstArg, final IExpr secondArg) {
		fAST.set(1, firstArg);
		fAST.set(2, secondArg);
		IExpr temp = fEngine.evaluate(fAST);
		if (temp.equals(F.False)) {
			return 1;
		}
		if (temp.equals(F.True)) {
			return -1;
		}
		return 0;
	}
}
