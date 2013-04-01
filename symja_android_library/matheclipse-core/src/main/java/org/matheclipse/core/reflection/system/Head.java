package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Head extends AbstractFunctionEvaluator {

	public Head() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		if (ast.size() == 2) {
			// Validate.checkSize(ast, 2);
			return ast.get(1).head();
		}
		return F.SymbolHead;
	}

	@Override
	public void setUp(final ISymbol symbol) {
	}

}
