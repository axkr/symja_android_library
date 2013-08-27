package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Power;

import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.AST;
import org.matheclipse.core.expression.IntegerSym;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.generic.interfaces.IIndexFunction;
import org.matheclipse.generic.nested.IndexTableGenerator;

/**
 * Vandermonde matrix, defined by A<sub>i,j</sub> = vector[i]^(j-1). See <a
 * href="http://en.wikipedia.org/wiki/Vandermonde_matrix">Vandermonde matrix</a>
 * 
 */
public class VandermondeMatrix extends AbstractFunctionEvaluator {
	public VandermondeMatrix() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		if ((ast.size() == 2) && ast.get(1).isList()) {
			final AST lst = (AST) ast.get(1);
			final int len0 = lst.size() - 1;

			final IAST resultList = List();
			final int[] indexArray = new int[2];
			indexArray[0] = len0;
			indexArray[1] = len0;

			final IIndexFunction<IExpr> function = new IIndexFunction<IExpr>() {
				public IExpr evaluate(int[] index) {
					return Power(lst.get(index[0] + 1), IntegerSym.valueOf(index[1]));
				}
			};
			final IndexTableGenerator generator = new IndexTableGenerator(indexArray, resultList, function);
			final IAST matrix = (IAST) generator.table();
			if (matrix != null) {
				matrix.addEvalFlags(IAST.IS_MATRIX);
			}
			return matrix;
		}

		return null;
	}
}
