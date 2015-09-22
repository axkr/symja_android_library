package org.matheclipse.core.integrate.rubi45;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.AbortException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class AbortRubi extends AbstractFunctionEvaluator {

	public AbortRubi() {

	}

	@Override
	public IExpr evaluate(IAST ast, EvalEngine engine) {
		throw new AbortException();
	}

}
