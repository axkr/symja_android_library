package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

/**
 * 
 * @see Scan
 */
public class MapAt extends AbstractFunctionEvaluator {

	public MapAt() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 4);

		final IExpr arg2 = ast.arg2();
		final IExpr arg3 = ast.arg3();
		if (arg2.isAST()) {
			try {
				final IAST arg1Function = F.ast(ast.arg1());
				if (arg3.isInteger()) {
					IInteger i3 = (IInteger) arg3;
					int n = i3.toInt();
					return ((IAST) arg2).setAtCopy(n, Functors.append(arg1Function).apply(((IAST) arg2).get(n)));
				}
			} catch (RuntimeException ae) {
				return null;
			}
		}
		return F.NIL;
	}

}
