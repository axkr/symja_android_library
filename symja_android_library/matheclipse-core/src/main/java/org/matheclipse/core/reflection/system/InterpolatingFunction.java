package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.InterpolatingFunctionExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class InterpolatingFunction extends AbstractEvaluator {

	public InterpolatingFunction() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		IExpr head = ast.head();
		if (head instanceof InterpolatingFunctionExpr) {
			try {
				return ((InterpolatingFunctionExpr<IExpr>) head).evaluate(ast, engine);
			} catch (RuntimeException rex) {
				engine.printMessage(ast.topHead(), rex);
			}
			return F.NIL;
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
	}
}
