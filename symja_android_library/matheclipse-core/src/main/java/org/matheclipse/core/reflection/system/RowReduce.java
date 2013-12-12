package org.matheclipse.core.reflection.system;

import org.apache.commons.math3.linear.FieldMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.matheclipse.core.convert.FieldReducedRowEchelonForm;
import org.matheclipse.core.eval.interfaces.AbstractMatrix1Matrix;
import org.matheclipse.core.expression.ExprFieldElement;

/**
 * Reduce thea matrix to row form.
 * 
 * See: <a href="http://en.wikipedia.org/wiki/Row_echelon_form">Wikipedia - Row echelon form</a>.
 */
public class RowReduce extends AbstractMatrix1Matrix {

	public RowReduce() {
		super();
	}

	@Override
	public FieldMatrix<ExprFieldElement> matrixEval(FieldMatrix<ExprFieldElement> matrix) {
		FieldReducedRowEchelonForm<ExprFieldElement> fmw = new FieldReducedRowEchelonForm<ExprFieldElement>(matrix);
		return fmw.rowReduce();
	}

	@Override
	public RealMatrix realMatrixEval(RealMatrix matrix) {
		// TODO implement rowReduce() for RealMatrix
		return null;
	}
}