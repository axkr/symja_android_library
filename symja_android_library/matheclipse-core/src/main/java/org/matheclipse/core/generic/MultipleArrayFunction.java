package org.matheclipse.core.generic;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.generic.interfaces.IArrayFunction;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class MultipleArrayFunction implements IArrayFunction {
	final EvalEngine fEngine;

	final IAST fHeadAST;

	public MultipleArrayFunction(final EvalEngine engine, final IAST headAST) {
		fEngine = engine;
		fHeadAST = headAST;
	}

	public IExpr evaluate(final IExpr[] index) {
		final IAST ast = fHeadAST.clone();
		for (int i = 0; i < index.length; i++) {
			ast.append(index[i]);
		}
		return fEngine.evaluate(ast);
	}
}
