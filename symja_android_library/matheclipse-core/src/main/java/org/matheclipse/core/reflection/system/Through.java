package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class Through extends AbstractFunctionEvaluator {

	public Through() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 3);

		if (ast.arg1().isAST()) {
			IAST arg1AST = (IAST) ast.arg1();
			IExpr arg1Head = arg1AST.head();
			if (arg1Head.isAST()) {

				IAST clonedList;
				IAST arg1HeadAST = (IAST) arg1Head;
				if (ast.isAST2() && !arg1HeadAST.head().equals(ast.arg2())) {
					return arg1AST;
				}
				IAST result = F.ast(arg1HeadAST.head());
				for (int i = 1; i < arg1HeadAST.size(); i++) {
					clonedList = arg1AST.apply(arg1HeadAST.get(i));
					result.append(clonedList);
				}
				return result;
			}
			return arg1AST;
		}
		return ast.arg1();
	}
}
