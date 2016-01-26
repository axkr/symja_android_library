package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Trace of a matrix.<br>
 * 
 * See <a href="http://en.wikipedia.org/wiki/Trace_(linear_algebra)">Trace (linear algebra)</a>
 */
public class Tr extends AbstractEvaluator {

	public Tr() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 3);

		// TODO generalize for tensors
		final int[] dim = ast.arg1().isMatrix();
		if (dim != null) {
			final IAST mat = (IAST) ast.arg1();
			IAST tr;
			int len = dim[0] < dim[1] ? dim[0] : dim[1];
			if (ast.size() > 2) {
				tr = F.ast(ast.arg2(), len, true);
			} else {
				tr = F.ast(F.Plus, len, true);
			}
			IAST row;
			for (int i = 1; i <= len; i++) {
				row = (IAST) mat.get(i);
				tr.set(i, row.get(i));
			}
			return tr;
		}
		return F.UNEVALED;
	}

	@Override
	public void setUp(final ISymbol symbol) {
	}

}