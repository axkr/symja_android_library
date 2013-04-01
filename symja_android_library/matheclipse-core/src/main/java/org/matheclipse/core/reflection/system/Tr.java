package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.AST;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Trace of a matrix. See <a
 * href="http://en.wikipedia.org/wiki/Trace_(linear_algebra)">Trace (linear
 * algebra)</a>
 */
public class Tr implements IFunctionEvaluator {

	public Tr() {
	}

	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 2, 3);
		final int[] dim = ast.get(1).isMatrix();
		if (dim != null) {
			final IAST mat = (IAST) ast.get(1);
			IAST tr;
			int len = dim[0] < dim[1] ? dim[0] : dim[1];
			if (ast.size() > 2) {
				tr = F.ast(ast.get(2), len, true);
			} else {
				tr = F.ast(F.Plus, len, true);
			}
			IAST row;
			for (int i = 1; i <= len; i++) {
				row = (AST) mat.get(i);
				tr.set(i, row.get(i));
			}
			return tr;
		}
		return null;
	}

	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	public void setUp(final ISymbol symbol) {
	}

}