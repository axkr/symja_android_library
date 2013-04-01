package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.Slot;

import java.util.HashMap;
import java.util.Map;

import org.matheclipse.core.eval.exception.WrongNumberOfArguments;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.AST;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.ISymbol;

import com.google.common.base.Predicate;

public class Function implements IFunctionEvaluator {

	public Function() {
	}

	public IExpr evaluate(final IAST ast) {
		if (ast.head().isAST()) {

			final IAST function = (IAST) ast.head();
			if (function.size() == 2) {
				return replaceSlots(function.get(1), ast);
			} else if (function.size() == 3) {
				IAST symbolSlots = null;
				if (function.get(1).isList()) {
					symbolSlots = (IAST) function.get(1);
				} else {
					symbolSlots = F.List(function.get(1));
				}
				if (symbolSlots.size() > ast.size()) {
					throw new WrongNumberOfArguments(ast, symbolSlots.size() - 1, ast.size() - 1);
				}
				final IExpr result = function.get(2).replaceAll(Functors.rules(getRulesMap(symbolSlots, ast)));
				return (result == null) ? function.get(2) : result;
			}

		}
		return null;
	}

	private Map<IExpr, IExpr> getRulesMap(final IAST symbolSlots, final IAST ast) {
		final Map<IExpr, IExpr> rulesMap = new HashMap<IExpr, IExpr>();
		int size = symbolSlots.size();
		for (int i = 1; i < size; i++) {
			rulesMap.put(symbolSlots.get(i), ast.get(i));
		}
		return rulesMap;
	}

	public static IExpr replaceSlots(final IExpr expr, final IAST list) {
		final IExpr result = expr.replaceSlots(list);
		return (result == null) ? expr : result;
	}

	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}
}
