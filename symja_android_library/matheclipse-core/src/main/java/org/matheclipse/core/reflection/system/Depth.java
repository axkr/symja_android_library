package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Calculates the depth of an expression (i.e. <code>{x,{y}} --> 3</code>
 */
public class Depth extends AbstractFunctionEvaluator {

	public Depth() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);

		if (!(ast.get(1).isAST())) {
			return F.C1;
		}
		return F.integer(depth((IAST) ast.get(1), 1));
	}

	/**
	 * Calculates the depth of an expression. Atomic expressions (no sublists) have depth <code>1</code> Example: the nested list
	 * <code>[x,[y]]</code> has depth <code>3</code>
	 * 
	 * @param headOffset
	 * 
	 */
	public static int depth(final IAST list, int headOffset) {
		int maxDepth = 1;
		int d;
		for (int i = headOffset; i < list.size(); i++) {
			if (list.get(i).isAST()) {
				d = depth((IAST) list.get(i), headOffset);
				if (d > maxDepth) {
					maxDepth = d;
				}
			}
		}
		return ++maxDepth;
	}

}
