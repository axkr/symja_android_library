package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.List;

import org.apache.commons.math3.linear.RealMatrix;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.ASTRealMatrix;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * 
 * See <a href="http://en.wikipedia.org/wiki/Singular_value_decomposition">
 * Wikipedia: Singular value decomposition</a>
 */
public class SingularValueDecomposition extends AbstractFunctionEvaluator {

	public SingularValueDecomposition() {
		super();
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);

		RealMatrix matrix;
		try {

			matrix = ast.arg1().toRealMatrix();
			if (matrix != null) {
				final org.apache.commons.math3.linear.SingularValueDecomposition svd = new org.apache.commons.math3.linear.SingularValueDecomposition(
						matrix);
				final RealMatrix uMatrix = svd.getU();
				final RealMatrix sMatrix = svd.getS();
				final RealMatrix vMatrix = svd.getV();

				final IAST result = List();
				final IAST uMatrixAST = new ASTRealMatrix(uMatrix, false);
				final IAST sMatrixAST = new ASTRealMatrix(sMatrix, false);
				final IAST vMatrixAST = new ASTRealMatrix(vMatrix, false);
				result.append(uMatrixAST);
				result.append(sMatrixAST);
				result.append(vMatrixAST);
				return result;
			}

		} catch (final WrongArgumentType e) {
			// WrongArgumentType occurs in list2RealMatrix(),
			// if the matrix elements aren't pure numerical values
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		} catch (final IndexOutOfBoundsException e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		}

		return F.NIL;
	}

}