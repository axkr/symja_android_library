package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.List;

import org.matheclipse.core.builtin.function.FoldList;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.BinaryApply;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class ComposeList extends AbstractEvaluator {

	public ComposeList() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		return evaluateComposeList(ast, List());
	}

	public static IExpr evaluateComposeList(final IAST ast, final IAST resultList) {
		try {
			if ((ast.size() == 3) && (ast.arg1().isAST())) {
				// final EvalEngine engine = EvalEngine.get();
				final IAST list = (IAST) ast.arg1();
				FoldList.foldLeft(ast.arg2(), list, 1, list.size(), new BinaryApply(F.ast(ast.arg1())), resultList);
				return resultList;
			}
		} catch (final ArithmeticException e) {

		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}
}
