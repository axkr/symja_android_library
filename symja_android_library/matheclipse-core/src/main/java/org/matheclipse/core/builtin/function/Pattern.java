package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.ISymbol;

public class Pattern implements IFunctionEvaluator {
	public final static Pattern CONST = new Pattern();

	public Pattern() {
	}

	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);
		
		if (ast.arg1().isSymbol()) {
			if (ast.get(2).isAST(F.Blank)) {
				IAST blank = (IAST) ast.get(2);
				if (blank.size() == 1) {
					return F.$p((ISymbol)ast.arg1());
				}
				if (blank.size() == 2) {
					return F.$p((ISymbol)ast.arg1(), blank.arg1());
				}
			}
			if (ast.arg2().isPattern()) {
				IPattern blank = (IPattern) ast.arg2();
				if (blank.isBlank()) {
					return F.$p((ISymbol)ast.arg1(), blank.getCondition());
				}
			}
		}
		return null;
	}

	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	public void setUp(ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}
}
