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
		return evaluateNestList(ast, engine);
	}

	public static IExpr evaluateNestList(final IAST ast, EvalEngine engine) {

		try {
			IExpr temp = engine.evaluate(ast.arg3());
			if (temp.isAST()) {
				final IAST list = (IAST) temp;
				IExpr arg1 = engine.evaluate(ast.arg1());
				IExpr arg2 = engine.evaluate(ast.arg2());
				return list.args().foldLeft(new BinaryMap(arg1), arg2);
			}
		} catch (final ArithmeticException e) {

		}
		return F.UNEVALED;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}
}
