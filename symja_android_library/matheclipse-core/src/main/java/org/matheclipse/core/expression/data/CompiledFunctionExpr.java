package org.matheclipse.core.expression.data;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class CompiledFunctionExpr extends DataExpr<AbstractFunctionEvaluator> {

	private static final long serialVersionUID = 3098987741558862963L;

	/**
	 * 
	 * @param value
	 * @return
	 */
	public static CompiledFunctionExpr newInstance(final AbstractFunctionEvaluator value) {
		return new CompiledFunctionExpr(value);
	}

	protected CompiledFunctionExpr(final AbstractFunctionEvaluator function) {
		super(F.CompiledFunction, function);
	}

	public IExpr evaluate(IAST ast, EvalEngine engine) {
		return fData.evaluate(ast, engine);
	}

	@Override
	public IExpr copy() {
		return new CompiledFunctionExpr(fData);
	}
}
