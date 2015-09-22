package org.matheclipse.core.generic;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Return the evaluation of a binary AST object
 */
public class BinaryEval extends BinaryFunctorImpl<IExpr> {
	protected final EvalEngine fEngine;

	protected final IAST fAST;

	/**
	 * Define an binary AST with the header <code>head</code>.
	 *
	 * @param head the AST's head expresion
	 * @param engine TODO
	 */
	public BinaryEval(final IExpr head, EvalEngine engine) {
		fEngine = engine;
		fAST = F.ast(head, 1, false);
	}

	/**
	 * Return the evaluation of an binary AST object by settings it's first
	 * argument to <code>firstArg</code> and it's second argument to
	 * <code>secondArg</code>
	 *
	 */
	public IExpr apply(final IExpr firstArg, final IExpr secondArg) {
		final IAST ast = fAST.clone();
		ast.add(firstArg);
		ast.add(secondArg); 
		return fEngine.evaluate(ast);
	}

}
