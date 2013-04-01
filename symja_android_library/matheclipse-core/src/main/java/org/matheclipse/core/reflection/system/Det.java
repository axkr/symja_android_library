package org.matheclipse.core.reflection.system;

import org.apache.commons.math3.linear.FieldLUDecomposition;
import org.apache.commons.math3.linear.FieldMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.matheclipse.core.eval.interfaces.AbstractMatrix1Expr;
import org.matheclipse.core.expression.ExprFieldElement;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Compute the determinant of a matrix
 * 
 * See <a href="http://en.wikipedia.org/wiki/Determinant">Determinant</a>
 * 
 */
public class Det extends AbstractMatrix1Expr {

	public Det() {
		super();
	}

	@Override
	public ExprFieldElement matrixEval(final FieldMatrix<ExprFieldElement> matrix) {
		if (matrix.getRowDimension() == 2 && matrix.getColumnDimension() == 2) {
			// 2x2 matrix
			ExprFieldElement[] row1 = matrix.getRow(0);
			ExprFieldElement[] row2 = matrix.getRow(1);
			return row1[0].multiply(row2[1]).subtract((row1[1].multiply(row2[0])));
		}
		if (matrix.getRowDimension() == 3 && matrix.getColumnDimension() == 3) {
			// 3x3 matrix
			ExprFieldElement[] row1 = matrix.getRow(0);
			ExprFieldElement[] row2 = matrix.getRow(1);
			ExprFieldElement[] row3 = matrix.getRow(2);
			return row1[0].multiply(row2[1].multiply(row3[2])).subtract(
					  (row1[0].multiply(row2[2].multiply(row3[1])))).subtract(
					  (row1[1].multiply(row2[0].multiply(row3[2])))).add(
						(row1[1].multiply(row2[2].multiply(row3[0])))).add(
						(row1[2].multiply(row2[0].multiply(row3[1])))).subtract(
						(row1[2].multiply(row2[1].multiply(row3[0]))));
		}
		final FieldLUDecomposition<ExprFieldElement> lu = new FieldLUDecomposition<ExprFieldElement>(matrix);
		return lu.getDeterminant();
	}

	@Override
	public IExpr realMatrixEval(RealMatrix matrix) {
		final LUDecomposition lu = new LUDecomposition(matrix);
		return F.num(lu.getDeterminant());
	}
}