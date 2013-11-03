package org.matheclipse.core.builtin.function;

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
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);
		
		IExpr arg1 = F.eval(ast.arg1());
		final IAST sublist = Validate.checkASTType(arg1).clone();

		if (sublist.size() > 1) {
			sublist.remove(1);
			return sublist;
		}
 
		return null;
	}

}
