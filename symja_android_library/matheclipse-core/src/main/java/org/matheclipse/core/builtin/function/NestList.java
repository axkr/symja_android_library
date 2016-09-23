package org.matheclipse.core.builtin.function;

import static org.matheclipse.core.expression.F.List;

import java.util.function.Function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.harmony.util.HMCollection;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class NestList extends AbstractCoreFunctionEvaluator {

	public NestList() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 4);

		return evaluateNestList(ast, List(), engine);
	}

	public static IExpr evaluateNestList(final IAST ast, final IAST resultList, EvalEngine engine) {
		IExpr arg3 = engine.evaluate(ast.arg3());
		if (arg3.isInteger()) {
			final int n = Validate.checkIntType(arg3);
			nestList(ast.arg2(), n, Functors.append(F.ast(ast.arg1())), resultList, engine);
			return resultList;
		}
		return F.NIL;
	}

	public static void nestList(final IExpr expr, final int n, final Function<IExpr, IExpr> fn,
			final HMCollection<IExpr> resultList, EvalEngine engine) {
		IExpr temp = expr;
		resultList.append(temp);
		for (int i = 0; i < n; i++) {
			temp = engine.evaluate(fn.apply(temp));
			resultList.append(temp);
		}
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.HOLDALL);
	}
}
