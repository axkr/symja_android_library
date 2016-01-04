package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class Prepend extends AbstractCoreFunctionEvaluator {

	public Prepend() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);
		IExpr arg1 = engine.evaluate(ast.arg1());
		IAST arg1AST = Validate.checkASTType(arg1);
		IExpr arg2 = engine.evaluate(ast.arg2());
		return arg1AST.addAtClone(1, arg2);
	}

}