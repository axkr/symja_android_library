package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.ICoreFunctionEvaluator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Sow implements ICoreFunctionEvaluator {
	public final double DEFAULT_CHOP_DELTA = 10E-10;

	public Sow() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);
		
		EvalEngine engine = EvalEngine.get();
		IAST reapList = engine.getReapList();
		IExpr expr = engine.evaluate(ast.get(1));
		if (reapList != null) {
			reapList.add(expr);
		}
		return expr;
	}
	
	@Override
	public IExpr numericEval(IAST ast) {
		return evaluate(ast);
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}

}
