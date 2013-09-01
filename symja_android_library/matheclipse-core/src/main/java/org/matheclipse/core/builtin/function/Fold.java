package org.matheclipse.core.builtin.function;

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
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 4);
		return evaluateNestList(ast);
	}

	public static IExpr evaluateNestList(final IAST ast) {

		try {
			IExpr temp = F.eval(ast.get(3));
			if (temp.isAST()) {
				final IAST list = (IAST) temp;
				return list.args().foldLeft(new BinaryMap(F.ast(ast.get(1))), ast.get(2));
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
