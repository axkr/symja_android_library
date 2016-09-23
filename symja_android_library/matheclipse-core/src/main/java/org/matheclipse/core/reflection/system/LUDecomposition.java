package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.List;

import org.apache.commons.math3.linear.FieldLUDecomposition;
import org.apache.commons.math3.linear.FieldMatrix;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class LUDecomposition extends AbstractFunctionEvaluator {

	public LUDecomposition() {
		super();
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);

		FieldMatrix<IExpr> matrix;
		try {
			final IAST list = (IAST) ast.arg1();
			matrix = Convert.list2Matrix(list);
			final FieldLUDecomposition<IExpr> lu = new FieldLUDecomposition<IExpr>(matrix);
			final FieldMatrix<IExpr> lMatrix = lu.getL();
			final FieldMatrix<IExpr> uMatrix = lu.getU();
			final int[] iArr = lu.getPivot();
			// final int permutationCount = lu.getPermutationCount();
			final IAST iList = List();
			for (int i = 0; i < iArr.length; i++) {
				// +1 because in MathEclipse the offset is +1 compared to java arrays
				iList.append(F.integer(iArr[i] + 1));
			}
			final IAST result = List();
			final IAST lMatrixAST = Convert.matrix2List(lMatrix);
			final IAST uMatrixAST = Convert.matrix2List(uMatrix);
			result.append(lMatrixAST);
			result.append(uMatrixAST);
			result.append(iList);
			return result;

		} catch (final ClassCastException e) {
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