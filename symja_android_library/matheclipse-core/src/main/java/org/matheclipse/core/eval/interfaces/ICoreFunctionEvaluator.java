package org.matheclipse.core.eval.interfaces;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <p>
 * Interface for &quot;core functions&quot; which don't have associated rules and are only defined by a derived classes from this
 * interface.
 * </p>
 * 
 */
public interface ICoreFunctionEvaluator extends IFunctionEvaluator {
	public final static ICoreFunctionEvaluator ARGS_EVALUATOR = new ICoreFunctionEvaluator() {

		@Override
		public IExpr evaluate(IAST ast, EvalEngine engine) {
			return engine.evalAttributes(ast.topHead(), ast);
		}

		@Override
		public IExpr numericEval(IAST ast, EvalEngine engine) {
			return engine.evalAttributes(ast.topHead(), ast);
		}

		@Override
		public void setUp(ISymbol newSymbol) {

		}

	};
}
