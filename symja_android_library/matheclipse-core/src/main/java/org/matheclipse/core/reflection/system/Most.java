package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 */
public class Most extends AbstractFunctionEvaluator {

	public Most() {
	}
  
	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);
		final IAST sublist = Validate.checkASTType(ast, 1);

		if (sublist.size() > 1) {
			return sublist.removeAtClone(sublist.size() - 1);
		}

		return null;
	}

}
