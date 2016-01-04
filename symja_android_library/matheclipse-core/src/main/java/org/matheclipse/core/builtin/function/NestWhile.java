package org.matheclipse.core.builtin.function;

import java.util.function.Function;
import java.util.function.Predicate;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class NestWhile extends NestWhileList {

	public NestWhile() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 4);

		return nestWhile(ast.arg2(), engine.evaluate(ast.arg3()), Functors.append(F.ast(ast.arg1())), engine);
	}

	public static IExpr nestWhile(final IExpr expr, final IExpr test, final Function<IExpr, IExpr> fn,
			EvalEngine engine) {
		IExpr temp = expr;
		Predicate<IExpr> predicate = Predicates.isTrue(test);

		while (predicate.test(temp)) {
			temp = engine.evaluate(fn.apply(temp));
		}
		return temp;

	}

}
