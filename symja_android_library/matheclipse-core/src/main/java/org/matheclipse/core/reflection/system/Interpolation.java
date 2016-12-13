package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class Interpolation extends AbstractEvaluator {

	public Interpolation() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);
		int[] dims = ast.arg1().isMatrix();
		if (dims != null) {
			return F.InterpolatingFunction(ast.arg1());
		}
		return F.NIL;
	}
}
