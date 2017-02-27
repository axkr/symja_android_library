package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Symbol;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Compute the minimal polynomial of a matrix.
 * 
 * See
 * <a href="https://en.wikipedia.org/wiki/Minimal_polynomial_(linear_algebra)">
 * Wikipedia - Minimal polynomial (linear algebra)</a>
 */
public class MatrixMinimalPolynomial extends AbstractFunctionEvaluator {

	public MatrixMinimalPolynomial() {
		super();
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);
		int[] dimensions = ast.arg1().isMatrix();
		if (dimensions != null && dimensions[0] == dimensions[1]) {
			// a matrix with square dimensions
			IAST matrix = (IAST) ast.arg1();
			IExpr variable = ast.arg2();
			ISymbol i = new Symbol("§i");
			int n = 1;
			IAST qu = F.List();
			IAST mnm = (IAST) engine.evaluate(
					F.List(F.Flatten(IdentityMatrix.diagonalMatrix(new IExpr[] { F.C0, F.C1 }, dimensions[0]))));
			while (qu.size() == 1) {
				mnm.append(engine.evaluate(F.Flatten(F.MatrixPower(matrix, F.integer(n)))));
				qu = (IAST) engine.evaluate(F.NullSpace(F.Transpose(mnm)));
				n++;
			}
			return engine.evaluate(F.Dot(F.First(qu), F.Table(F.Power(variable, i), F.List(i, F.C0, F.integer(--n)))));
		}

		return F.NIL;
	}

}