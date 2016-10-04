package org.matheclipse.core.reflection.system;

import org.hipparchus.linear.DecompositionSolver;
import org.hipparchus.linear.FieldDecompositionSolver;
import org.hipparchus.linear.FieldLUDecomposition;
import org.hipparchus.linear.FieldMatrix;
import org.hipparchus.linear.LUDecomposition;
import org.hipparchus.linear.RealMatrix;
import org.matheclipse.core.eval.interfaces.AbstractMatrix1Matrix;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Invert a matrix
 * 
 * See <a href="http://en.wikipedia.org/wiki/Invertible_matrix">Invertible
 * matrix</a>
 */
public class Inverse extends AbstractMatrix1Matrix {

	public Inverse() {
		super();
	}

	@Override
	public FieldMatrix<IExpr> matrixEval(FieldMatrix<IExpr> matrix) {
		final FieldLUDecomposition<IExpr> lu = new FieldLUDecomposition<IExpr>(matrix);
		FieldDecompositionSolver<IExpr> solver = lu.getSolver();
		if (!solver.isNonSingular()) {
			return null;
		}
		return solver.getInverse();
	}

	@Override
	public RealMatrix realMatrixEval(RealMatrix matrix) {
		final LUDecomposition lu = new LUDecomposition(matrix);
		DecompositionSolver solver = lu.getSolver();
		if (!solver.isNonSingular()) {
			return null;
		}
		return solver.getInverse();
	}
}