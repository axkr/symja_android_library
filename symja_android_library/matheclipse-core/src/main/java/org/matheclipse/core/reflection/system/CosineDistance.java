package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.WrongNumberOfArguments;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * CosineDistance of two vectors
 */
public class CosineDistance extends AbstractEvaluator {

	public CosineDistance() {
	}

	@Override
	public IExpr evaluate(final IAST functionList, EvalEngine engine) {
		if (functionList.size() != 3) {
			throw new WrongNumberOfArguments(functionList, 2, functionList.size() - 1);
		}
		IExpr arg1 = functionList.arg1();
		IExpr arg2 = functionList.arg2();

		int dim1 = arg1.isVector();
		if (dim1 > (-1)) {
			int dim2 = arg2.isVector();
			if (dim1 == dim2) {
				if (dim1 == 0) {
					return F.C0;
				}

				return F.Subtract(F.C1, F.Divide(F.Dot(arg1, arg2), F.Times(F.Norm(arg1), F.Norm(arg2))));

			}
		}
		return F.NIL;
	}

}
