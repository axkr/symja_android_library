package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 */
public class MemberQ extends AbstractCoreFunctionEvaluator {

	public MemberQ() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 3, 5);

		boolean heads = false;
		if (ast.size() > 3) {
			final Options options = new Options(ast.topHead(), ast, ast.size() - 1);
			// IExpr option = options.getOption("Heads");
			if (options.isOption("Heads")) {
				heads = true;
			}
		}
		final IExpr arg1 = F.eval(ast.arg1());
		final IExpr arg2 = F.eval(ast.arg2());
		if (arg1.isAST()) {
			return F.bool(arg1.isMember(arg2, heads));
		}
		return F.False;
	}

}
