package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.List;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.visit.VisitorLevelSpecification;

public class Level extends AbstractFunctionEvaluator {

	public Level() {
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

		if (!ast.arg1().isAtom()) {
			final IAST arg1 = (IAST) ast.arg1();
			IAST resultList;
			if (lastIndex != 3) {
				resultList = List();
			} else {
				resultList = F.ast(ast.get(lastIndex));
			}

			final VisitorLevelSpecification level = new VisitorLevelSpecification(Functors.collect(resultList), ast.get(2), heads);
			arg1.accept(level);

			return resultList;
		}
		return null;
	}

}
