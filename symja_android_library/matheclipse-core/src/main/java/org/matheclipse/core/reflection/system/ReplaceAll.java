package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class ReplaceAll implements IFunctionEvaluator {

	public ReplaceAll() {
	}

	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);
		if (ast.get(2).isListOfLists()) {
			IAST result = F.List();
			for (IExpr subList : (IAST) ast.get(2)) {
				result.add(F.subst(ast.get(1), (IAST) subList));
			}
			return result;
		}
		return F.subst(ast.get(1), (IAST) ast.get(2));

	}

	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDREST);
	}
}
