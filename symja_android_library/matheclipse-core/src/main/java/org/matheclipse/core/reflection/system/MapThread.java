package org.matheclipse.core.reflection.system;

import java.util.function.Function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.visit.VisitorLevelSpecification;

/**
 */
public class MapThread extends AbstractFunctionEvaluator {

	private static class UnaryMapThread implements Function<IExpr, IExpr> {
		final IExpr fConstant;

		public UnaryMapThread(final IExpr constant) {
			fConstant = constant;
		}

		@Override
		public IExpr apply(final IExpr firstArg) {
			if (firstArg.isAST()) {
				return Thread.threadList((IAST) firstArg, F.List, fConstant).orElse(firstArg);
			}
			return firstArg;
		}

	}

	public MapThread() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 3, 4);

		VisitorLevelSpecification level = null;
		Function<IExpr, IExpr> umt = new UnaryMapThread(ast.arg1());
		if (ast.isAST3()) {
			level = new VisitorLevelSpecification(umt, ast.arg3(), false);
		} else {
			level = new VisitorLevelSpecification(umt, 0);
		}
		final IExpr result = ast.arg2().accept(level);
		return result.isPresent() ? result : ast.arg2();
	}

}
