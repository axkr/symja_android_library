package org.matheclipse.core.reflection.system;

import org.apache.commons.math4.linear.FieldMatrix;
import org.apache.commons.math4.linear.RealMatrix;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.AbstractMatrix1Expr;
import org.matheclipse.core.expression.ASTRealMatrix;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Compute the covariance.
 * 
 * See <a href="http://en.wikipedia.org/wiki/Covariance">Covariance</a>
 * 
 */
public class Covariance extends AbstractMatrix1Expr {

	public Covariance() {
		super();
	}

	@Override
	public IExpr realMatrixEval(RealMatrix matrix) {
		org.apache.commons.math4.stat.correlation.Covariance cov = new org.apache.commons.math4.stat.correlation.Covariance(
				matrix);
		return new ASTRealMatrix(cov.getCovarianceMatrix(), false);
	}

	@Override
	public IExpr matrixEval(FieldMatrix<IExpr> matrix) {
		return F.NIL;
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 3);
		if (ast.size() == 2) {
			return super.evaluate(ast, engine);
		}
		return F.NIL;
	}

	@Override
	public IExpr numericEval(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 3);
		if (ast.size() == 2) {
			return super.numericEval(ast, engine);
		}
		if (ast.size() == 3) {
			final IAST arg1 = (IAST) ast.arg1();
			final IAST arg2 = (IAST) ast.arg2();
			try {
				// if (engine.isApfloat()) {
				// FieldMatrix<IExpr> arg1FieldMatrix =
				// Convert.list2Matrix(arg1);
				// if (arg1FieldMatrix != null) {
				// FieldMatrix<IExpr> arg2FieldMatrix =
				// Convert.list2Matrix(arg2);
				// if (arg1FieldMatrix != null) {
				// return matrixEval2(arg1FieldMatrix, arg2FieldMatrix);
				// }
				// }
				// return F.NIL;
				// }
				int arg1Length = arg1.isVector();
				if (arg1Length >= 0) {
					int arg2Length = arg2.isVector();
					if (arg2Length >= 0) {
						double[] arg1DoubleArray = Convert.list2RealVector(arg1).toArray();
						double[] arg2DoubleArray = Convert.list2RealVector(arg2).toArray();
						org.apache.commons.math4.stat.correlation.Covariance cov = new org.apache.commons.math4.stat.correlation.Covariance();
						return F.num(cov.covariance(arg1DoubleArray, arg2DoubleArray, true));
					}
				}
			} catch (final WrongArgumentType e) {
				// WrongArgumentType occurs in list2RealMatrix(),
				// if the matrix elements aren't pure numerical values
			} catch (final IndexOutOfBoundsException e) {
				if (Config.SHOW_STACKTRACE) {
					e.printStackTrace();
				}
			}
		}
		return F.NIL;
	}
}