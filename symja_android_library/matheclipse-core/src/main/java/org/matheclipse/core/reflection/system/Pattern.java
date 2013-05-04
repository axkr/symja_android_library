package org.matheclipse.core.reflection.system;

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
		
		if (ast.get(1).isSymbol()) {
			if (ast.get(2).isAST(F.Blank)) {
				IAST blank = (IAST) ast.get(2);
				if (blank.size() == 1) {
					return F.$p((ISymbol)ast.get(1));
				}
				if (blank.size() == 2) {
					return F.$p((ISymbol)ast.get(1), blank.get(1));
				}
			}
			if (ast.get(2).isPattern()) {
				IPattern blank = (IPattern) ast.get(2);
				if (blank.isBlank()) {
					return F.$p((ISymbol)ast.get(1), blank.getCondition());
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
