package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Return the <i>rest</i> of a given list, i.e. a sublist with all elements from
 * list[[2]]...list[[n]]
 */
public class Rest extends AbstractCoreFunctionEvaluator {

	public Rest() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);
		
		IExpr arg1 = engine.evaluate(ast.arg1());
		final IAST sublist = Validate.checkASTType(arg1);

		if (sublist.size() > 1) {
			return sublist.removeAtClone(1);
		}
 
		return F.NIL;
	}

}
