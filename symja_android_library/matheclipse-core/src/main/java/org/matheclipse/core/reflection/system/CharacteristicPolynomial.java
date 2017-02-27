package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;

import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

import edu.jas.arith.BigRational;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.PolyUtil;

/**
 * Compute the characteristic polynomial of a square matrix.
 * 
 * See
 * <a href="http://en.wikipedia.org/wiki/Characteristic_polynomial">Wikipedia -
 * Characteristic polynomial</a>
 */
public class CharacteristicPolynomial extends AbstractFunctionEvaluator {

	public CharacteristicPolynomial() {
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
			return generateCharacteristicPolynomial(dimensions[0], matrix, variable);
		}

		return F.NIL;
	}

	/**
	 * Generate the characteristic polynomial of a square matrix.
	 * 
	 * @param dim
	 *            dimension of the square matrix
	 * @param matrix
	 *            the square matrix
	 * @param variable
	 *            the variable which should be used in the resulting
	 *            characteristic polynomial
	 * @return
	 */
	public static IAST generateCharacteristicPolynomial(int dim, IAST matrix, IExpr variable) {
		final IExpr[] valuesForIdentityMatrix = { F.C0, variable };
		return F.Det(F.Subtract(matrix, IdentityMatrix.diagonalMatrix(valuesForIdentityMatrix, dim)));
	}

}