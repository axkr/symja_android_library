package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.visit.VisitorLevelSpecification;

import com.google.common.base.Function;

/**
 */
public class MapThread extends AbstractFunctionEvaluator {

	private static class UnaryMapThread implements Function<IExpr, IExpr> {
		final IExpr fConstant;

		public UnaryMapThread(final IExpr constant) {
			fConstant = constant;
		}

		public IExpr apply(final IExpr firstArg) {
			if (firstArg.isAST()) {
				IExpr result = Thread.threadList((IAST) firstArg, F.List, fConstant, 1);
				if (result == null) {
					return firstArg;
				}
				return result;
			}
			return firstArg;
		}

	}

	public MapThread() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 3, 4);

		VisitorLevelSpecification level = null;
		Function<IExpr, IExpr> umt = new UnaryMapThread(ast.get(1));
		if (ast.size() == 4) {
			level = new VisitorLevelSpecification(umt, ast.get(3), false);
		} else {
			level = new VisitorLevelSpecification(umt, 0);
		}
		final IExpr result = ast.get(2).accept(level);

		return result == null ? ast.get(2) : result;
	}

}
