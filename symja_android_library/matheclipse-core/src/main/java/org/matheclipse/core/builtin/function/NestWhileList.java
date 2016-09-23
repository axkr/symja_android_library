package org.matheclipse.core.builtin.function;

import static org.matheclipse.core.expression.F.List;

import java.util.function.Function;
import java.util.function.Predicate;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class NestWhileList extends AbstractCoreFunctionEvaluator {

	public NestWhileList() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 4);

		return nestList(ast.arg2(), engine.evaluate(ast.arg3()), Functors.append(F.ast(ast.arg1())), List(), engine);
	}

	public static IAST nestList(final IExpr expr, final IExpr test, final Function<IExpr, IExpr> fn,
			final IAST resultList, EvalEngine engine) {
		IExpr temp = expr;
		Predicate<IExpr> predicate = Predicates.isTrue(test);

		while (predicate.test(temp)) {
			resultList.append(temp);
			temp = engine.evaluate(fn.apply(temp));
		}
		resultList.append(temp);
		return resultList;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.HOLDALL);
	}
}
