package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class Outer extends AbstractFunctionEvaluator {

	private static class OuterAlgorithm {
		final IAST ast;
		final IExpr f;
		final IExpr head;

		public OuterAlgorithm(final IAST ast, final IExpr head) {
			this.ast = ast;
			this.f = ast.arg1();
			this.head = head;
		}

		private IAST outer(int astPosition, IExpr expr, IAST current) {
			if (expr.isAST() && head.equals(expr.head())) {
				IAST list = (IAST) expr;
				IAST result = F.ast(head);
				for (int i = 1; i < list.size(); i++) {
					IExpr temp = list.get(i);
					result.add(outer(astPosition, temp, current));
				}
				return result;
			}

			if (ast.size() > astPosition) {
				try {
					current.add(expr);
					return outer(astPosition + 1, ast.get(astPosition), current);
				} finally {
					current.remove(current.size() - 1);
				}
			} else {
				IAST result = F.ast(f);
				result.addAll(current);
				result.add(expr);
				return result;
			}
		}
	}

	public Outer() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 4);

		IExpr head = null;
		for (int i = 2; i < ast.size(); i++) {
			IExpr list = ast.get(i);
			if (!list.isAST()) {
				return F.UNEVALED;
			}
			if (head == null) {
				head = list.head();
			} else if (!head.equals(list.head())) {
				return F.UNEVALED;
			}
		}
		OuterAlgorithm algorithm = new OuterAlgorithm(ast, head);
		return algorithm.outer(3, ast.arg2(), F.ast(F.List, ast.size() - 1, false));
	}
}
