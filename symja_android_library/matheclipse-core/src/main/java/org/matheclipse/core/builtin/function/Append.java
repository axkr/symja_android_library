package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class Append extends AbstractCoreFunctionEvaluator {

	public Append() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);
		IExpr arg1 = F.eval(ast.arg1());
		IAST arg1AST = Validate.checkASTType(arg1);
		IExpr arg2 = F.eval(ast.arg2());
		return arg1AST.clone().append(arg2);
	}

}