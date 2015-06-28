package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.WrongNumberOfArguments;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * ChessboardDistance of two vectors
 */
public class ChessboardDistance implements IFunctionEvaluator {

	public ChessboardDistance() {
	}

	@Override
	public IExpr evaluate(final IAST functionList) {
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
				IAST a1 = ((IAST) arg1);
				IAST a2 = ((IAST) arg2);
				IAST maxAST = F.Max();
				for (int i = 1; i < a1.size(); i++) {
					maxAST.add(F.Abs(F.Subtract(a1.get(i), a2.get(i))));
				}
				return maxAST;
			}
		}
		return null;
	}

	@Override
	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	@Override
	public void setUp(ISymbol symbol) {

	}

}
