package org.matheclipse.core.reflection.system;

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
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);
		final IAST sublist = Validate.checkASTType(ast, 1).clone();

		if (sublist.size() > 1) {
			sublist.remove(sublist.size() - 1);
			return sublist;
		}

		return null;
	}

}
