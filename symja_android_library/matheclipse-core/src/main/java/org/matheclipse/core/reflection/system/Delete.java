package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

public class Delete extends AbstractFunctionEvaluator {

	public Delete() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);
		if (ast.get(1).isAST()) {
			final IAST arg1 = (IAST) ast.get(1);
			final IExpr arg2 = ast.get(2);

			if (arg2.isInteger()) {
				try {
					int indx = Validate.checkIntType(ast, 2, Integer.MIN_VALUE);
					if (indx < 0) {
						indx = arg1.size() + indx;
					}
					IAST result = arg1.clone();
					result.remove(indx);
					return result;
				} catch (final IndexOutOfBoundsException e) {
					if (Config.DEBUG) {
						e.printStackTrace();
					}
					return null;
				}
			}
		}
		return null;
	}

}