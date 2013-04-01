package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Compute the characteristic polynomial of a matrix.
 * 
 * See <a
 * href="http://en.wikipedia.org/wiki/Characteristic_polynomial">Wikipedia -
 * Characteristic polynomial</a>
 */
public class CharacteristicPolynomial extends AbstractFunctionEvaluator {

	public CharacteristicPolynomial() {
		super();
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);
		int[] dim = ast.get(1).isMatrix();
		if (dim != null && dim[0] == dim[1]) {
			// a matrix with square dimensions
			final IExpr[] valuesForIdentityMatrix = { F.C0, ast.get(2) };
			return F.eval(F.Det(F.Subtract(ast.get(1), IdentityMatrix
					.diagonalMatrix(valuesForIdentityMatrix, dim[0]))));
		}

		return null;
	}
}