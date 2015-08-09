package org.matheclipse.core.reflection.system;

import org.apache.commons.math4.linear.RealMatrix;
import org.apache.commons.math4.linear.LUDecomposition;
import org.matheclipse.commons.math.linear.FieldLUDecomposition;
import org.matheclipse.commons.math.linear.FieldMatrix;
import org.matheclipse.core.eval.interfaces.AbstractMatrix1Expr;
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
	public IExpr matrixEval(final FieldMatrix matrix) {
		if (matrix.getRowDimension() == 2 && matrix.getColumnDimension() == 2) {
			// 2x2 matrix
			IExpr[] row1 = matrix.getRow(0);
			IExpr[] row2 = matrix.getRow(1);
			return F.evalExpand(row1[0].multiply(row2[1]).subtract((row1[1].multiply(row2[0]))));
		}
		if (matrix.getRowDimension() == 3 && matrix.getColumnDimension() == 3) {
			// 3x3 matrix
			IExpr[] row1 = matrix.getRow(0);
			IExpr[] row2 = matrix.getRow(1);
			IExpr[] row3 = matrix.getRow(2);
			return F.evalExpand(row1[0].multiply(row2[1].multiply(row3[2])).subtract(
					  (row1[0].multiply(row2[2].multiply(row3[1])))).subtract(
					  (row1[1].multiply(row2[0].multiply(row3[2])))).plus(
						(row1[1].multiply(row2[2].multiply(row3[0])))).plus(
						(row1[2].multiply(row2[0].multiply(row3[1])))).subtract(
						(row1[2].multiply(row2[1].multiply(row3[0])))));
		}
		final FieldLUDecomposition lu = new FieldLUDecomposition(matrix);
		return F.evalExpand(lu.getDeterminant());
	}

	@Override
	public IExpr realMatrixEval(RealMatrix matrix) {
		final LUDecomposition lu = new LUDecomposition(matrix);
		return F.num(lu.getDeterminant());
	}
}