package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.PatternMatcher;

/**
 * Trace the evaluation steps for a given expression. The resulting trace
 * expression list is wrapped by Hold (i.e. <code>Hold[{...}]</code>.
 * 
 */
public class Trace implements IFunctionEvaluator {

	public Trace() {
	}

	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 2, 3);

		final IExpr temp = ast.arg1();
		PatternMatcher matcher = null;
		if (ast.size() == 3) {
			matcher = new PatternMatcher(ast.arg2());
		}
		final EvalEngine engine = EvalEngine.get();
		IAST traceList = engine.evalTrace(temp, matcher, F.List());
		final IAST holdList = F.ast(F.Hold);
		holdList.add(traceList);
		return holdList;
	}

	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}

}
