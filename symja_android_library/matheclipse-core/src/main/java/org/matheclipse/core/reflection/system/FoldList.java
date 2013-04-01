package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.List;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.BinaryMap;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.generic.Algorithms;

public class FoldList implements IFunctionEvaluator {

	public FoldList() {
	}

	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 4);

		return evaluateNestList(ast, List());
	}

	public static IExpr evaluateNestList(final IAST ast, final IAST resultList) {

		try {
			if (ast.get(3).isAST()) {
				final IAST list = (IAST) ast.get(3);
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
	}
}
