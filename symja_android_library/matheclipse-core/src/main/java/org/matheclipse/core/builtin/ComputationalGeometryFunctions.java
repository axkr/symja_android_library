package org.matheclipse.core.builtin;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class ComputationalGeometryFunctions {
	static {
		F.ConvexHullMesh.setEvaluator(new ConvexHullMesh());
	}

	private static class ConvexHullMesh extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2);
			if (ast.arg1().isList()) {
			}
			return F.NIL;
		}
	}

	private final static ComputationalGeometryFunctions CONST = new ComputationalGeometryFunctions();

	public static ComputationalGeometryFunctions initialize() {
		return CONST;
	}

	private ComputationalGeometryFunctions() {

	}

}
