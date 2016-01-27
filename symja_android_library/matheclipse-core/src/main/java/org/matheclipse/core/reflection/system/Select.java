package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Select extends AbstractEvaluator {

	public Select() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 3, 4);

		int size = ast.size();
		if (ast.arg1().isAST()) {
			IAST arg1 = (IAST) ast.arg1();
			IExpr arg2 = ast.arg2();
			if (size == 3) {
				return arg1.filter(arg1.copyHead(), Predicates.isTrue(arg2));
			} else if ((size == 4) && ast.arg3().isInteger()) {
				final int resultLimit = Validate.checkIntType(ast, 3);
				return arg1.filter(arg1.copyHead(), Predicates.isTrue(arg2), resultLimit);
			}
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol symbol) {
	}

}
