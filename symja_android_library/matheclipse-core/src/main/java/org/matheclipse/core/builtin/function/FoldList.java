package org.matheclipse.core.builtin.function;

import static org.matheclipse.core.expression.F.List;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.ICoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.BinaryMap;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.generic.Algorithms;

public class FoldList implements ICoreFunctionEvaluator {

	public FoldList() {
	}

	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 4);

		return evaluateNestList(ast, List());
	}

	public static IExpr evaluateNestList(final IAST ast, final IAST resultList) {

		try {
			IExpr temp = F.eval(ast.get(3));
			if (temp.isAST()) {
				final IAST list = (IAST) temp;
				Algorithms.foldLeft(ast.get(2), list, 1, list.size(), new BinaryMap(F.ast(ast.get(1))), resultList);
				return resultList;
			}
		} catch (final ArithmeticException e) {

		}
		return null;
	}

	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}
}
