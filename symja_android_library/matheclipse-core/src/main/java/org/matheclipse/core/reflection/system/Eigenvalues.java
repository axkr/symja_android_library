package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Times;

import org.hipparchus.linear.EigenDecomposition;
import org.hipparchus.linear.FieldMatrix;
import org.hipparchus.linear.RealMatrix;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.WrappedException;
import org.matheclipse.core.eval.interfaces.AbstractMatrix1Expr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Compute the numerical Eigenvalues of a real symmetric matrix
 * 
 * See:
 * <a href="http://en.wikipedia.org/wiki/Eigenvalue,_eigenvector_and_eigenspace"
 * >Wikipedia - Eigenvalue, eigenvector and eigenspace</a>
 */
public class Eigenvalues extends AbstractMatrix1Expr {

	public Eigenvalues() {
		super();
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		FieldMatrix<IExpr> matrix;
		try {

			int[] dim = ast.arg1().isMatrix();
			if (dim != null) {
				if (dim[0] == 1 && dim[1] == 1) {
					// Eigenvalues({{a}}) 
					return List(ast.arg1().getAt(1).getAt(1));
				}
				if (dim[0] == 2 && dim[1] == 2) {
					matrix = Convert.list2Matrix((IAST) ast.arg1());
					if (matrix != null) {
						// Eigenvalues({{a, b}, {c, d}}) =>
						// {
						// 1/2 (a + d - Sqrt[a^2 + 4 b c - 2 a d + d^2]),
						// 1/2 (a + d + Sqrt[a^2 + 4 b c - 2 a d + d^2])
						// }
						IExpr sqrtExpr = Sqrt(Plus(Sqr(matrix.getEntry(0, 0)),
								Times(C4, matrix.getEntry(0, 1), matrix.getEntry(1, 0)),
								Times(CN2, matrix.getEntry(0, 0), matrix.getEntry(1, 1)), Sqr(matrix.getEntry(1, 1))));
						return List(Times(C1D2, Plus(Negate(sqrtExpr), matrix.getEntry(0, 0), matrix.getEntry(1, 1))),
								Times(C1D2, Plus(sqrtExpr, matrix.getEntry(0, 0), matrix.getEntry(1, 1))));
					}
				}

			}

		} catch (final ClassCastException e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		} catch (final IndexOutOfBoundsException e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		}

		// switch to numeric calculation
		return numericEval(ast, engine);
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
					list.append(F.num(realValues[i]));
				} else {
					list.append(F.complexNum(realValues[i], imagValues[i]));
				}
			}
			return list;
		} catch (Exception ime) {
			throw new WrappedException(ime);
		}
	}

	@Override
	public IExpr matrixEval(FieldMatrix<IExpr> matrix) {
		return F.NIL;
	}
}