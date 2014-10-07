package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

import com.google.common.base.Function;

public class Nest extends AbstractCoreFunctionEvaluator {

	public Nest() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 4);

		return evaluateNest(ast);
	}

	public static IExpr evaluateNest(final IAST ast) {
		IExpr arg3 = F.eval(ast.arg3());
		if (arg3.isInteger()) {
			final int n = Validate.checkIntType(arg3);
			return nest(ast.arg2(), n, Functors.append(F.ast(ast.arg1())));
		}
		return null;
	}

	public static IExpr nest(final IExpr expr, final int n, final Function<IExpr, IExpr> fn) {
		IExpr temp = expr;
		for (int i = 0; i < n; i++) {
			temp = F.eval(fn.apply(temp));
		}
		return temp;
	} 

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}
}
