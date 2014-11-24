package org.matheclipse.core.builtin.function;


import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Block extends AbstractCoreFunctionEvaluator {
	public Block() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);

		if (ast.arg1().isList()) {
			final IAST blockVariablesList = (IAST) ast.arg1();
			return EvalEngine.get().evalBlock(ast.arg2(), blockVariablesList);
		}

		return null;
	}

	@Override
	public void setUp(ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}

}
