package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.IConstantHeaders;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class ExpandAll extends AbstractFunctionEvaluator implements
		IConstantHeaders {
	public ExpandAll() {
		super();
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);

		IExpr temp = expandAll(ast.get(1));
		if (temp != null) {
			return temp;
		}

		return ast.get(1);
	}

	public static IExpr expandAll(final IExpr expr) {
		if (!expr.isAST()) {
			return null;
		}
		IAST ast = (IAST) expr;
		int j = ast.size();
		IExpr temp = null;
		for (int i = 1; i < ast.size(); i++) {
			if (ast.get(i).isAST()) {
				j = i;
				break;
			}
		}
		if (j >= ast.size()) {
			return null;
		}
		IAST result = ast.clone();
		for (int i = j; i < ast.size(); i++) {
			if (ast.get(i).isAST()) {
				temp = expandAll(ast.get(i));
				if (temp != null) {
					result.set(i, temp);
				} else {
					result.set(i, ast.get(i));
				}
			}
		}
		temp = Expand.expand(result);
		if (temp != null) {
			return temp;
		}
		return result;
	}

}