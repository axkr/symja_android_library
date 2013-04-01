package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Through extends AbstractFunctionEvaluator {

	public Through() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 2, 3);

		if (ast.get(1).isAST()) {
			IAST l1 = (IAST) ast.get(1);
			IExpr h = l1.head();
			if (h.isAST()) {

				IAST clonedList;
				IAST l2 = (IAST) h;
				if (ast.size() == 3 && !l2.head().equals(ast.get(2))) {
					return l1;
				}
				IAST result = F.ast(l2.head());
				for (int i = 1; i < l2.size(); i++) {
					if (l1.get(i).isSymbol() || l2.get(i).isAST()) {
						clonedList = l1.clone();
						clonedList.setHeader(l2.get(i));
						result.add(clonedList);
					} else {
						result.add(l2.get(i));
					}
				}
				return result;
			}
			return l1;
		}
		return ast.get(1);
	}
}
