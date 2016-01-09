package org.matheclipse.core.reflection.system;

import java.util.function.Function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ReturnException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.visit.VisitorLevelSpecification;

/**
 * @see Map
 */
public class Scan extends Map {

	public Scan() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 3, 5);

		int lastIndex = ast.size() - 1;
		boolean heads = false;
		final Options options = new Options(ast.topHead(), ast, lastIndex, engine);
		IExpr option = options.getOption("Heads");
		if (option != null) {
			lastIndex--;
			if (option.isTrue()) {
				heads = true;
			}
		} else {
			Validate.checkRange(ast, 3, 4);
		}

		try {
			final IAST arg1 = F.ast(ast.arg1());
			if (lastIndex == 3) {
				IAST result = F.List();
				Function<IExpr, IExpr> sf = Functors.scan(arg1, result);
				VisitorLevelSpecification level = new VisitorLevelSpecification(sf, ast.get(lastIndex), heads);

				ast.arg2().accept(level);
				for (int i = 1; i < result.size(); i++) {
					engine.evaluate(result.get(i));
				}

			} else {
				if (ast.arg2().isAST()) {
					engine.evaluate(((IAST) ast.arg2()).map(Functors.append(arg1)));
				} else {
					engine.evaluate(ast.arg2());
				}
			}
			return F.Null;
		} catch (final ReturnException e) {
			return e.getValue();
			// don't catch Throw[] here !
		}
	}

}
