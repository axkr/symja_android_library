package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class AnyTrue extends AbstractFunctionEvaluator {

	public AnyTrue() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);

		if (ast.arg1().isAST()) {
			IAST arg1 = (IAST) ast.arg1();
			IExpr head = ast.arg2();
			IAST logicalOr = F.Or();
			int size = arg1.size();
			for (int i = 1; i < size; i++) {
				IExpr temp = engine.evaluate(F.unary(head, arg1.get(i)));
				if (temp.isTrue()) {
					return F.True;
				} else if (temp.isFalse()) {
					continue;
				}
				logicalOr.append(temp);
			}
			if (logicalOr.size() > 1) {
				return logicalOr;
			}
			return F.False;
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
	}

}
