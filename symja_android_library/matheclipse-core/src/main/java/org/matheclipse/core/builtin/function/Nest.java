package org.matheclipse.core.builtin.function;

import java.util.function.Function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Nest extends AbstractCoreFunctionEvaluator {

	public Nest() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 4);

		return evaluateNest(ast, engine);
	}

	public static IExpr evaluateNest(final IAST ast, EvalEngine engine) {
		IExpr arg3 = engine.evaluate(ast.arg3());
		if (arg3.isInteger()) {
			final int n = Validate.checkIntType(arg3);
			return nest(ast.arg2(), n, Functors.append(F.ast(ast.arg1())), engine);
		}
		return F.UNEVALED;
	}

	public static IExpr nest(final IExpr expr, final int n, final Function<IExpr, IExpr> fn, EvalEngine engine) {
		IExpr temp = expr;
		for (int i = 0; i < n; i++) {
			temp = engine.evaluate(fn.apply(temp));
		}
		return temp;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}
}
