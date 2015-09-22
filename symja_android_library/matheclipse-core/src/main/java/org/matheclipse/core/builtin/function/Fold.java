package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.BinaryMap;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Fold extends AbstractCoreFunctionEvaluator {

	public Fold() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 4);
		return evaluateNestList(ast);
	}

	public static IExpr evaluateNestList(final IAST ast) {

		try {
			IExpr temp = F.eval(ast.arg3());
			if (temp.isAST()) {
				final IAST list = (IAST) temp;
				IExpr arg1 = F.eval(ast.arg1());
				IExpr arg2 = F.eval(ast.arg2());
				return list.args().foldLeft(new BinaryMap(arg1), arg2);
			}
		} catch (final ArithmeticException e) {

		}
		return null;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}
}
