package org.matheclipse.core.reflection.system;

import org.hipparchus.stat.StatUtils;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.ASTRealVector;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Compute the variance for a list of elements
 */
public class Variance extends AbstractFunctionEvaluator {

	public Variance() {
		// empty default constructor
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);

		if (ast.arg1().isAST()) {
			IAST arg1 = (IAST) ast.arg1();
			int dim = arg1.isVector();
			if (dim >= 0) {
				if (arg1.isRealVector()) {
					return F.num(StatUtils.variance(arg1.toDoubleVector()));
				}
				return Covariance.vectorCovarianceSymbolic(arg1, arg1, dim);
			}
			int[] matrixDimensions = arg1.isMatrix();
			if (matrixDimensions != null) {
				if (arg1.isRealMatrix()) {
					double[][] matrix = arg1.toDoubleMatrix();
					matrix = Convert.toDoubleTransposed(matrix);
					double[] result = new double[matrixDimensions[1]];
					for (int i = 0; i < matrix.length; i++) {
						result[i] = StatUtils.variance(matrix[i]);
					}
					return new ASTRealVector(result, false);
				}
				return F.NIL;
			}

			if (arg1.isAST()) {
				IAST dist = (IAST) arg1;
				if (dist.head().isSymbol()) {
					ISymbol head = (ISymbol) dist.head();
					if (arg1.isAST1()) {
						if (head.equals(F.BernoulliDistribution)) {
						} else if (head.equals(F.PoissonDistribution)) {
						}
					} else if (arg1.isAST2()) {
						if (head.equals(F.BinomialDistribution)) {
						} else if (head.equals(F.NormalDistribution)) {
						}
					} else if (arg1.isAST3()) {
						IExpr n = dist.arg1();
						IExpr nSucc = dist.arg2();
						IExpr nTot = dist.arg3();
						if (head.equals(F.HypergeometricDistribution)) {
						}
					}
				}
			}
		}
		return F.NIL;
	}

}
