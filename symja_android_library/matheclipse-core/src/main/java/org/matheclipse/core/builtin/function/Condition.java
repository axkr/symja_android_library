package org.matheclipse.core.builtin.function;

import org.matheclipse.core.builtin.function.Module;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ConditionException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.ICoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Condition implements ICoreFunctionEvaluator {

	public Condition() {
	}

	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);

		if (F.evalTrue(ast.get(2))) {
			return F.eval(ast.get(1));
		}
		if (EvalEngine.get().isEvalLHSMode()) {
			return null;
		}
		throw new ConditionException(ast);
	}

	/**
	 * Check the (possible nested) condition in pattern matcher without
	 * evaluating a result.
	 * 
	 * @param arg1
	 * @param arg2
	 * @param engine
	 * @return
	 */
	public static boolean checkCondition(IExpr arg1, IExpr arg2, final EvalEngine engine) {
		if (F.evalTrue(arg2)) {
			if (arg1.isCondition()) {
				return checkCondition(arg1.getAt(1), arg1.getAt(2), engine);
			} else if (arg2.isModule()) {
				return Module.checkModuleCondition(arg2.getAt(1), arg2.getAt(2), engine);
			}
			return true;
		}
		return false;
	}

	public IExpr numericEval(final IAST ast) {
		return evaluate(ast);
	}

	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}

}
