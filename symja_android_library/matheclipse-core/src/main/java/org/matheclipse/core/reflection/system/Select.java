package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Select implements IFunctionEvaluator {

	public Select() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 3, 4);
		
		int size = ast.size();
		if (ast.arg1().isAST()) {
			IAST arg1 = (IAST) ast.arg1();
			IExpr arg2 = ast.arg2();
			if (size == 3) {
				return arg1.filter(arg1.copyHead(), Predicates.isTrue(arg2));
			} else if ((size == 4) && ast.arg3().isInteger()) {
				final int resultLimit = Validate.checkIntType(ast, 3);
				return arg1.filter(arg1.copyHead(), Predicates.isTrue(arg2), resultLimit);
			}
		}
		return null;
	}

	@Override
	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	@Override
	public void setUp(final ISymbol symbol) {
	}

}
