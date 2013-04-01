package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.generic.IsBinaryFalse;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.list.algorithms.EvaluationSupport;

public class Sort extends AbstractFunctionEvaluator {

	public Sort() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 2, 3);
		
		if (ast.get(1).isAST()) {
			final IAST shallowCopy = ((IAST) ast.get(1)).clone();
			if (shallowCopy.size() <= 2) {
				return shallowCopy;
			}
			if (ast.size() == 2) {
				EvaluationSupport.sort(shallowCopy);
			} else if (ast.get(2).isSymbol()) {
				EvaluationSupport.sort(shallowCopy, new IsBinaryFalse<IExpr>(ast.get(2)));
			}
			return shallowCopy;
		}

		return null;
	}
}
