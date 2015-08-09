package org.matheclipse.core.reflection.system;

import org.apache.commons.math4.linear.EigenDecomposition;
import org.apache.commons.math4.linear.RealMatrix;
import org.matheclipse.commons.math.linear.FieldMatrix;
import org.matheclipse.core.eval.exception.WrappedException;
import org.matheclipse.core.eval.interfaces.AbstractMatrix1Expr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Compute the numerical Eigenvalues of a real symmetric matrix
 * 
 * See: <a href="http://en.wikipedia.org/wiki/Eigenvalue,_eigenvector_and_eigenspace" >Wikipedia - Eigenvalue, eigenvector and
 * eigenspace</a>
 */
public class Eigenvalues extends AbstractMatrix1Expr {

	public Eigenvalues() {
		super();
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		// switch to numeric calculation
		return numericEval(ast);
	}

	@Override
	public IAST realMatrixEval(RealMatrix matrix) {
		try {
			IAST list = F.List();
			EigenDecomposition ed = new EigenDecomposition(matrix);
			double[] realValues = ed.getRealEigenvalues();
			double[] imagValues = ed.getImagEigenvalues();
			for (int i = 0; i < realValues.length; i++) {
				if (F.isZero(imagValues[i])) {
					list.add(F.num(realValues[i]));
				} else {
					list.add(F.complexNum(realValues[i], imagValues[i]));
				}
			}
			return list;
		} catch (Exception ime) {
			throw new WrappedException(ime);
		}
	}

	@Override
	public IExpr matrixEval(FieldMatrix matrix) {
		return null;
	}
}