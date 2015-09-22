package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.visit.VisitorLevelSpecification;

/**
 */
public class MapAll extends AbstractFunctionEvaluator {

	public MapAll() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 3);

		final IAST arg1AST = F.ast(ast.arg1());
		final VisitorLevelSpecification level = new VisitorLevelSpecification(Functors.append(arg1AST), 0, Integer.MAX_VALUE, false);

		final IExpr result = ast.arg2().accept(level);
		return result == null ? ast.arg2() : result;
	}

}
