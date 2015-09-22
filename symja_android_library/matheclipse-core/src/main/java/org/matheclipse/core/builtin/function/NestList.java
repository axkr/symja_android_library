package org.matheclipse.core.builtin.function;

import static org.matheclipse.core.expression.F.List;

import java.util.Collection;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

import com.google.common.base.Function;

public class NestList extends AbstractCoreFunctionEvaluator {

	public NestList() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 4);

		return evaluateNestList(ast, List());
	}

	public static IExpr evaluateNestList(final IAST ast, final IAST resultList) {
		IExpr arg3 = F.eval(ast.arg3());
		if (arg3.isInteger()) {
			final int n = Validate.checkIntType(arg3);
			nestList(ast.arg2(), n, Functors.append(F.ast(ast.arg1())), resultList);
			return resultList;
		}
		return null;
	}

	public static void nestList(final IExpr expr, final int n, final Function<IExpr, IExpr> fn, final Collection<IExpr> resultList) {
		IExpr temp = expr;
		resultList.add(temp);
		for (int i = 0; i < n; i++) {
			temp = F.eval(fn.apply(temp));
			resultList.add(temp);
		}
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}
}
