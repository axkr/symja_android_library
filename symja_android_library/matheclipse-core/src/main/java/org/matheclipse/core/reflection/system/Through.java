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
			IAST l1 = (IAST) ast.arg1();
			IExpr h = l1.head();
			if (h.isAST()) {

				IAST clonedList;
				IAST l2 = (IAST) h;
				if (ast.isAST2() && !l2.head().equals(ast.arg2())) {
					return l1;
				}
				IAST result = F.ast(l2.head());
				for (int i = 1; i < l2.size(); i++) {
					if (l1.get(i).isSymbol() || l2.get(i).isAST()) {
						clonedList = l1.apply(l2.get(i));
						result.append(clonedList);
					} else {
						result.append(l2.get(i));
					}
				}
				return result;
			}
			return l1;
		}
		return ast.arg1();
	}
}
