package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class ReplacePart implements IFunctionEvaluator {

	public ReplacePart() {
	}

	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 3, 4);
		if (ast.size() == 4) {
			if (ast.get(3).isList()) {
				IExpr result = ast.get(1);
				for (IExpr subList : (IAST) ast.get(3)) {
					IExpr expr = result.replacePart(F.Rule(subList, ast.get(2)));
					if (expr != null) {
						result = expr;
					}
				}
				return result;
			}
			final IExpr result = ast.get(1).replacePart(F.Rule(ast.get(3), ast.get(2)));
			return (result == null) ? ast.get(1) : result;
		}
		if (ast.get(2).isList()) {
			IExpr result = ast.get(1);
			for (IExpr subList : (IAST) ast.get(2)) {
				if (subList.isRuleAST()) {
					IExpr expr = result.replacePart((IAST) subList);
					if (expr != null) {
						result = expr;
					}
				}
			}
			return result;
		}
		IExpr result = ast.get(1);
		if (ast.get(2).isRuleAST()) {
			result = ast.get(1).replacePart((IAST) ast.get(2));
			return (result == null) ? ast.get(1) : result;
		}
		return result;

	}

	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	public void setUp(final ISymbol symbol) {
	}
}
