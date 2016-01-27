package org.matheclipse.core.builtin.function;

import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class Compile extends AbstractCoreFunctionEvaluator {

	public Compile() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (!ToggleFeature.COMPILE) {
			return F.NIL;
		}
		engine.printMessage("Compile: Compile() function not implemented! ");
		return F.Null;
	}

}
