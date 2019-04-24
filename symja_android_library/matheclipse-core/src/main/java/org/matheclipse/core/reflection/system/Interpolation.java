package org.matheclipse.core.reflection.system;

import org.matheclipse.core.builtin.IOFunctions;
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
		int[] dims = ast.arg1().isMatrix();
		return (dims != null) ? F.InterpolatingFunction(ast.arg1()) : F.NIL;
	}

	@Override
	public int[] expectedArgSize() {
		return IOFunctions.ARGS_1_1;
	}
}
