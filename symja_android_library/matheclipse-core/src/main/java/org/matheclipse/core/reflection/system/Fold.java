package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.BinaryMap;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Fold implements IFunctionEvaluator {

	public Fold() {
	}

	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 4);
		return evaluateNestList(ast);
	}

	public static IExpr evaluateNestList(final IAST ast) {
		 
		try {
			if (ast.get(3).isAST()) {
				final IAST list = (IAST) ast.get(3);
				return list.args().foldLeft(new BinaryMap(F.ast(ast.get(1))), ast.get(2));
			}
		} catch (final ArithmeticException e) {

		}
		return null;
	}

	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	public void setUp(final ISymbol symbol) {
	}
}
