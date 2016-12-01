package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.Divide;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;

import org.hipparchus.linear.EigenDecomposition;
import org.hipparchus.linear.FieldMatrix;
import org.hipparchus.linear.RealMatrix;
import org.hipparchus.linear.RealVector;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.WrappedException;
import org.matheclipse.core.eval.interfaces.AbstractMatrix1Expr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Compute the numerical Eigenvectors of a real symmetric matrix
 * 
 * See:
 * <a href="http://en.wikipedia.org/wiki/Eigenvalue,_eigenvector_and_eigenspace"
 * >Eigenvalue, eigenvector and eigenspace</a>
 */
public class Eigenvectors extends AbstractMatrix1Expr {

	public Eigenvectors() {
		super();
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		FieldMatrix<IExpr> matrix;
		try {

			int[] dim = ast.arg1().isMatrix();
			if (dim != null) {
				if (dim[0] == 1 && dim[1] == 1) {
					// Eigenvectors({{a}}) 
					return C1;
				}
				if (dim[0] == 2 && dim[1] == 2) {
					matrix = Convert.list2Matrix((IAST) ast.arg1());
					if (matrix != null) {
						if (matrix.getEntry(1, 0).isZero()) {
							if (matrix.getEntry(0, 0).equals(matrix.getEntry(1, 1))) {
								// Eigenvectors({{a, b}, {0, a}})
								return List(List(C1, C0), List(C0, C0));
							} else {
								// Eigenvectors({{a, b}, {0, d}})
								return List(List(C1, C0), List(Divide(Negate(matrix.getEntry(0, 1)),
										Subtract(matrix.getEntry(0, 0), matrix.getEntry(1, 1))), C1));
							}
						} else {
							// Eigenvectors({{a, b}, {c, d}}) =>
							// {
							// { - (1/(2*c)) * (-a + d + Sqrt[a^2 + 4 b c - 2 a
							// d +
							// d^2]), 1},
							// { - (1/(2*c)) * (-a + d - Sqrt[a^2 + 4 b c - 2 a
							// d +
							// d^2]), 1}
							// }
							IExpr sqrtExpr = Sqrt(Plus(Sqr(matrix.getEntry(0, 0)),
									Times(C4, matrix.getEntry(0, 1), matrix.getEntry(1, 0)),
									Times(CN2, matrix.getEntry(0, 0), matrix.getEntry(1, 1)),
									Sqr(matrix.getEntry(1, 1))));
							return List(List(
									Times(CN1D2, Power(matrix.getEntry(1, 0), CN1),
											Plus(sqrtExpr, Negate(matrix.getEntry(0, 0)), matrix.getEntry(1, 1))),
									C1), List(
											Times(CN1D2,
													Power(matrix.getEntry(1, 0), CN1), Plus(Negate(sqrtExpr),
															Negate(matrix.getEntry(0, 0)), matrix.getEntry(1, 1))),
											C1));
						}
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
			for (int i = 0; i < matrix.getColumnDimension(); i++) {
				RealVector rv = ed.getEigenvector(i);
				list.append(Convert.realVector2List(rv));
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