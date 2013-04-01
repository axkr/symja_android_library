package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class Append extends AbstractFunctionEvaluator {

	public Append() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		if ((ast.size() == 3) && (ast.get(1).isAST())) {
			final IAST f0 = ((IAST) ast.get(1)).clone();
			f0.add(ast.get(2));
			return f0; 
		}

		return null;
	}

}