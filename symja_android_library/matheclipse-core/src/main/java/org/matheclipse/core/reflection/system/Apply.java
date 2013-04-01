package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.visit.VisitorLevelSpecification;

import com.google.common.base.Function;

public class Apply extends AbstractFunctionEvaluator {

	public Apply() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 3, 5);
		
		int lastIndex = ast.size() - 1;
		boolean heads = false;
		final Options options = new Options(ast.topHead(), ast, lastIndex);
		IExpr option = options.getOption("Heads");
		if (option != null) {
			lastIndex--;
			if (option.isTrue()) {
				heads = true;
			}
		} else {
			Validate.checkRange(ast, 3, 4);
		}

		VisitorLevelSpecification level = null;
		Function<IExpr, IExpr> af = Functors.apply(ast.get(1));
		if (lastIndex == 3) {
			level = new VisitorLevelSpecification(af, ast.get(lastIndex), heads);
		} else {
			level = new VisitorLevelSpecification(af, 0);
		}

		try {

			if (!ast.get(2).isAtom()) {
				final IExpr result = ast.get(2).accept(level);

				return result == null ? ast.get(2) : result;
			} else if (ast.size() == 3) {
				if (ast.get(1).isFunction()) {
					IAST fun = F.ast(ast.get(1));
					fun.add(ast.get(2));
					return fun;
				}
				return ast.get(2);
			}
		} catch (final ArithmeticException e) {

		}
		return null;
	}

}
