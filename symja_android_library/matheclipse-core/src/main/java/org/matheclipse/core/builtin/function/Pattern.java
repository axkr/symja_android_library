package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr; 
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.ISymbol;

public class Pattern implements IFunctionEvaluator {
	public final static Pattern CONST = new Pattern();

	public Pattern() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);

		if (ast.arg1().isSymbol()) {
			if (ast.arg2().isBlank()) {
				IPatternObject blank = (IPatternObject) ast.arg2();
				return F.$p((ISymbol) ast.arg1(), blank.getCondition());
			}
			// if (ast.arg2().isPattern()) {
			// IPattern blank = (IPattern) ast.arg2();
			// // if (blank.isBlank()) {
			// return F.$p((ISymbol) ast.arg1(), blank.getCondition());
			// // }
			// }
		}
		return null;
	}

	@Override
	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	@Override
	public void setUp(ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}
}
