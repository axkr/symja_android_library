package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class FullForm extends AbstractCoreFunctionEvaluator {

	public FullForm() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);

		return F.stringx(new StringBuffer(F.eval(ast.arg1()).fullFormString()));
	}

	@Override
	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	@Override
	public void setUp(ISymbol symbol) {
	}
}
