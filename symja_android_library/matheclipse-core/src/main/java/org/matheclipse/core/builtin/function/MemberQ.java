package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 */
public class MemberQ extends AbstractCoreFunctionEvaluator {

	public MemberQ() {
	}

	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);

		final IExpr arg1 = F.eval(ast.get(1));
		final IExpr arg2 = F.eval(ast.get(2));
		return F.bool(arg1.isMember(arg2, false));
	}

}
