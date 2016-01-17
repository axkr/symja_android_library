package org.matheclipse.core.builtin.function;

import java.util.HashMap;
import java.util.Map;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.WrongNumberOfArguments;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.util.OpenFixedSizeMap;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Function extends AbstractCoreFunctionEvaluator {

	public Function() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (ast.head().isAST()) {

			final IAST function = (IAST) ast.head();
			if (function.size() == 2) {
				return replaceSlots(function.arg1(), ast);
			} else if (function.size() == 3) {
				IAST symbolSlots = null;
				if (function.arg1().isList()) {
					symbolSlots = (IAST) function.arg1();
				} else {
					symbolSlots = F.List(function.arg1());
				}
				if (symbolSlots.size() > ast.size()) {
					throw new WrongNumberOfArguments(ast, symbolSlots.size() - 1, ast.size() - 1);
				}
				final IExpr result = function.arg2().replaceAll(Functors.rules(getRulesMap(symbolSlots, ast)));
				return (result == null) ? function.arg2() : result;
			}

		}
		return F.UNEVALED;
	}

	private Map<IExpr, IExpr> getRulesMap(final IAST symbolSlots, final IAST ast) {
		int size = symbolSlots.size() - 1;
		final Map<IExpr, IExpr> rulesMap;
		if (size <= 5) {
			rulesMap = new OpenFixedSizeMap<IExpr, IExpr>(size * 3 - 1);
		} else {
			rulesMap = new HashMap<IExpr, IExpr>();
		}
		for (int i = 1; i < size; i++) {
			rulesMap.put(symbolSlots.get(i), ast.get(i));
		}
		return rulesMap;
	}

	public static IExpr replaceSlots(final IExpr expr, final IAST list) {
		final IExpr result = expr.replaceSlots(list);
		return (result == null) ? expr : result;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}
}
