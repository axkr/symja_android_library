package org.matheclipse.core.reflection.system;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;
import org.apache.commons.math3.linear.RealMatrix;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISymbol;

public class InterpolatingFunction implements IFunctionEvaluator {

	public InterpolatingFunction() {
	}

	public IExpr evaluate(final IAST ast) {
		if (ast.head().isAST()) {
			final IAST function = (IAST) ast.head();
			if (ast.size() == 2 && ast.get(1) instanceof INum) {
				if (function.size() == 2) {
					int[] dims = function.get(1).isMatrix();
					if (dims != null && dims[1] == 2) {
						RealMatrix matrix = Convert.list2RealMatrix((IAST) function.get(1));
						double interpolatedY = interpolate(matrix, ((INum) ast.get(1)).doubleValue());
						return F.num(interpolatedY);
					}
				}
				return null;
			}
		}
		return null;
	}

	private double interpolate(RealMatrix matrix, double interpolationX) {
		int rowDim = matrix.getRowDimension();
		double x[] = new double[rowDim];
		double y[] = new double[rowDim];
		double[][] data = matrix.getData();
		for (int i = 0; i < rowDim; i++) {
			x[i] = data[i][0];
			y[i] = data[i][1];
		}
		UnivariateInterpolator interpolator = new SplineInterpolator();
		UnivariateFunction function = interpolator.interpolate(x, y);
		double interpolatedY = function.value(interpolationX);
		return interpolatedY;
	}

	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}
}
