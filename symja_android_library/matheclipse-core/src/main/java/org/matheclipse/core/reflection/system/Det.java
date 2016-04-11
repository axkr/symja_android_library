package org.matheclipse.core.reflection.system;

import org.apache.commons.math4.linear.FieldLUDecomposition;
import org.apache.commons.math4.linear.FieldMatrix;
import org.apache.commons.math4.linear.LUDecomposition;
import org.apache.commons.math4.linear.RealMatrix;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractMatrix1Expr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Compute the determinant of a matrix
 * 
 * See <a href="http://en.wikipedia.org/wiki/Determinant">Determinant</a>
 * 
 */
public class Det extends AbstractMatrix1Expr {

	/**
	 * <p>
	 * Use cramer's rule to solve linear equations represented by a
	 * <code>2 x 3</code> augmented matrix which represents the system
	 * <code>M.x == b</code>, where the columns of the <code>2 x 2</code> matrix
	 * <code>M</code> are augmented by the vector <code>b</code>. This method
	 * assumes that the dimensions of the matrix are already checked by the
	 * caller.
	 * </p>
	 * See: <a href="https://en.wikipedia.org/wiki/Cramer's_rule">Wikipedia
	 * Cramer's rule</a>
	 * 
	 * @param matrix
	 *            the <code>2 x 3</code> augmented matrix
	 * @param quiet
	 *            show no message if there is no solution
	 * @param engine
	 *            the evaluation engine
	 * @return a list of values which solve the equations or <code>F#NIL</code>,
	 *         if the equations have no solution.
	 */
	public static IAST cramersRule2x3(FieldMatrix<IExpr> matrix, boolean quiet, EvalEngine engine) {
		IAST list = F.List();
		// a1 * b2 - b1 * a2
		IExpr denominator = determinant2x2(matrix);
		if (denominator.isZero()) {
			if (!quiet) {
				engine.printMessage("Row reduced linear equations have no solution.");
			}
			return F.NIL;
		}
		// c1 * b2 - b1 * c2
		IExpr xNumerator = F.Subtract(F.Times(matrix.getEntry(0, 2), matrix.getEntry(1, 1)),
				F.Times(matrix.getEntry(0, 1), matrix.getEntry(1, 2)));
		list.add(F.Divide(xNumerator, denominator));
		// a1 * c2 - c1*a2
		IExpr yNumerator = F.Subtract(F.Times(matrix.getEntry(0, 0), matrix.getEntry(1, 2)),
				F.Times(matrix.getEntry(0, 2), matrix.getEntry(1, 0)));
		list.add(F.Divide(yNumerator, denominator));
		return list;
	}

	/**
	 * <p>
	 * Use cramer's rule to solve linear equations represented by a
	 * <code>3 x 4</code> augmented matrix which represents the system
	 * <code>M.x == b</code>, where the columns of the <code>3 x 3</code> matrix
	 * <code>M</code> are augmented by the vector <code>b</code>. This method
	 * assumes that the dimensions of the matrix are already checked by the
	 * caller.
	 * </p>
	 * See: <a href="https://en.wikipedia.org/wiki/Cramer's_rule">Wikipedia
	 * Cramer's rule</a>
	 * 
	 * @param matrix
	 *            the <code>3 x 4</code> augmented matrix
	 * @param quiet
	 *            show no message if there is no solution
	 * @param engine
	 *            the evaluation engine
	 * @return a list of values which solve the equations or <code>F#NIL</code>,
	 *         if the equations have no solution.
	 */
	public static IAST cramersRule3x4(FieldMatrix<IExpr> matrix, boolean quiet, EvalEngine engine) {
		IAST list = F.List();
		FieldMatrix<IExpr> denominatorMatrix = matrix.getSubMatrix(0, 2, 0, 2);
		IExpr denominator = determinant3x3(denominatorMatrix);
		if (denominator.isZero()) {
			if (!quiet) {
				engine.printMessage("Row reduced linear equations have no solution.");
			}
			return F.NIL;
		}

		FieldMatrix<IExpr> xMatrix = denominatorMatrix.copy();
		xMatrix.setColumn(0, new IExpr[] { matrix.getEntry(0, 3), matrix.getEntry(1, 3), matrix.getEntry(2, 3) });
		IExpr xNumerator = determinant3x3(xMatrix);
		
		list.add(F.Divide(xNumerator, denominator));

		FieldMatrix<IExpr> yMatrix = denominatorMatrix.copy();
		yMatrix.setColumn(1, new IExpr[] { matrix.getEntry(0, 3), matrix.getEntry(1, 3), matrix.getEntry(2, 3) });
		IExpr yNumerator = determinant3x3(yMatrix);
		
		list.add(F.Divide(yNumerator, denominator));

		FieldMatrix<IExpr> zMatrix = denominatorMatrix.copy();
		zMatrix.setColumn(2, new IExpr[] { matrix.getEntry(0, 3), matrix.getEntry(1, 3), matrix.getEntry(2, 3) });
		IExpr zNumerator = determinant3x3(zMatrix);
		
		list.add(F.Divide(zNumerator, denominator));

		return list;
	}

	/**
	 * Get the determinant of a <code>2 x 2</code> matrix. This method assumes
	 * that the dimensions of the matrix are already checked by the caller.
	 * 
	 * @param matrix
	 *            a 2x2 matrix
	 * @return
	 */
	public static IExpr determinant2x2(final FieldMatrix<IExpr> matrix) {
		// 2x2 matrix
		IExpr[] row1 = matrix.getRow(0);
		IExpr[] row2 = matrix.getRow(1);
		return F.evalExpand(row1[0].multiply(row2[1]).subtract((row1[1].multiply(row2[0]))));
	}

	/**
	 * Get the determinant of a <code>3 x 3</code> matrix. This method assumes
	 * that the dimensions of the matrix are already checked by the caller.
	 * 
	 * @param matrix
	 *            a 3x3 matrix
	 * @return
	 */
	public static IExpr determinant3x3(final FieldMatrix<IExpr> matrix) {
		// 3x3 matrix
		IExpr[] row1 = matrix.getRow(0);
		IExpr[] row2 = matrix.getRow(1);
		IExpr[] row3 = matrix.getRow(2);
		return F.evalExpand(row1[0].multiply(row2[1].multiply(row3[2]))
				.subtract((row1[0].multiply(row2[2].multiply(row3[1]))))
				.subtract((row1[1].multiply(row2[0].multiply(row3[2]))))
				.plus((row1[1].multiply(row2[2].multiply(row3[0])))).plus((row1[2].multiply(row2[0].multiply(row3[1]))))
				.subtract((row1[2].multiply(row2[1].multiply(row3[0])))));
	}

	public Det() {
		super();
	}

	@Override
	public IExpr matrixEval(final FieldMatrix<IExpr> matrix) {
		if (matrix.getRowDimension() == 2 && matrix.getColumnDimension() == 2) {
			return determinant2x2(matrix);
		}
		if (matrix.getRowDimension() == 3 && matrix.getColumnDimension() == 3) {
			return determinant3x3(matrix);
		}
		final FieldLUDecomposition<IExpr> lu = new FieldLUDecomposition<IExpr>(matrix);
		return F.evalExpand(lu.getDeterminant());
	}

	@Override
	public IExpr realMatrixEval(RealMatrix matrix) {
		final LUDecomposition lu = new LUDecomposition(matrix);
		return F.num(lu.getDeterminant());
	}
}