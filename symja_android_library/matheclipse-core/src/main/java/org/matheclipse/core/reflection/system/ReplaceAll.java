package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class ReplaceAll implements IFunctionEvaluator {

	public ReplaceAll() {
	}

	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);
		try {
			if (ast.get(2).isListOfLists()) {
				IAST result = F.List();
				for (IExpr subList : (IAST) ast.arg2()) {
					result.add(F.subst(ast.arg1(), (IAST) subList));
				}
				return result;
			}
			if (ast.arg2().isAST()) {
				return F.subst(ast.arg1(), (IAST) ast.arg2());
			} else {
				WrongArgumentType wat = new WrongArgumentType(ast, ast, -1, "Rule expression (x->y) expected: ");
				EvalEngine.get().printMessage(wat.getMessage());
			}
		} catch (WrongArgumentType wat) {
			EvalEngine.get().printMessage(wat.getMessage());
		}
		return null;
	}

	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDREST);
	}
}
