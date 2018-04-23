package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class InterpolatingFunction extends AbstractEvaluator {

	public InterpolatingFunction() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (ast.head().isAST()) {
			if (ast.isAST1() && ast.arg1().isSignedNumber()) {
				final IAST function = (IAST) ast.head();
				if (function.isAST1()) {

					int[] dims = function.arg1().isMatrix();
					if (dims != null) {
						if (dims[1] == 2) {
							int rowsSize = dims[0];
							if (rowsSize >= 4) {
								IAST matrix = (IAST) function.arg1();
								IExpr interpolator = (IExpr) Config.EXPR_CACHE.getIfPresent(function);
								if (interpolator != null) {
									return F.unaryAST1(interpolator, ast.arg1());
								}
								// do a Piecewise polynomial interpolation with InterpolatingPolynomial
								IASTAppendable list1 = F.ListAlloc(rowsSize);
								int i = 1;
								for (int j = i + 3; j <= rowsSize; j++) {
									IAST compare;
									compare = createComparator(i, j, rowsSize);
									IASTAppendable data = F.ListAlloc(4);
									for (int k = i; k <= j; k++) {
										data.append(matrix.get(k));
									}
									IAST list = F.List(F.InterpolatingPolynomial(data, F.Slot1), compare);
									list1.append(list);
									i++;
								}
								interpolator = F.Function(F.Piecewise(list1));
								Config.EXPR_CACHE.put(function, interpolator);
								return F.unaryAST1(interpolator, ast.arg1());
							}
						}
					}

				}
				return F.NIL;
			}
		}
		return F.NIL;
	}

	private IAST createComparator(int i, int j, int size) {
		if (i == 1) {
			// # < i+1
			return F.Less(F.Slot1, F.ZZ(i + 1));
		} else {
			if (j < size) {
				// i <= # < i+1
				return F.And(F.LessEqual(F.ZZ(i), F.Slot1), F.Less(F.Slot1, F.ZZ(i + 1)));
			} else {
				// # >= i
				return F.GreaterEqual(F.Slot1, F.ZZ(i));
			}
		}
	}

	// private UnivariateDifferentiableVectorFunction interpolate(IAST matrixAST) {
	// HermiteInterpolator interpolator = (HermiteInterpolator) Config.EXPR_CACHE.getIfPresent(matrixAST);
	// if (interpolator != null) {
	// return interpolator;
	// }
	// RealMatrix matrix = matrixAST.toRealMatrix();
	// if (matrix != null) {
	// int rowDim = matrix.getRowDimension();
	// int colDim = matrix.getColumnDimension();
	// double x[] = new double[colDim - 1];
	// double[][] data = matrix.getData();
	// interpolator = new HermiteInterpolator();
	// for (int i = 0; i < rowDim; i++) {
	// System.arraycopy(data[i], 1, x, 0, colDim - 1);
	// interpolator.addSamplePoint(data[i][0], x);
	// }
	// Config.EXPR_CACHE.put(matrixAST, interpolator);
	// return interpolator;
	// }
	// return null;
	// }

	// private UnivariateFunction interpolateSpline(IAST matrixAST) {
	//
	// RealMatrix matrix = matrixAST.toRealMatrix();
	// int rowDim = matrix.getRowDimension();
	// double x[] = new double[rowDim];
	// double y[] = new double[rowDim];
	// double[][] data = matrix.getData();
	// for (int i = 0; i < rowDim; i++) {
	// x[i] = data[i][0];
	// y[i] = data[i][1];
	// }
	// UnivariateInterpolator interpolator = new SplineInterpolator();
	// UnivariateFunction function = interpolator.interpolate(x, y);
	// return function;
	// }

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.HOLDALL);
	}
}
