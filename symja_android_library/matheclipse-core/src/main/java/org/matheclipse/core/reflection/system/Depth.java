package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.generic.nested.NestedAlgorithms;

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
		return F.integer(NestedAlgorithms.depth((IAST) ast.get(1), 1));
	}

}
