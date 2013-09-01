package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

public class NestWhile extends NestWhileList {

	public NestWhile() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 4);

		return nestWhile(ast.get(2), F.eval(ast.get(3)), Functors.append(F.ast(ast.get(1))));
	}

	public static IExpr nestWhile(final IExpr expr, final IExpr test, final Function<IExpr, IExpr> fn) {
		IExpr temp = expr;
		Predicate<IExpr> predicate = Predicates.isTrue(test);

		while (predicate.apply(temp)) {
			temp = F.eval(fn.apply(temp));
		}
		return temp;

	}

}
