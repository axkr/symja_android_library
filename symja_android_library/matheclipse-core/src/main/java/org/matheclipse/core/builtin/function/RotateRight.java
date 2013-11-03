package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.ASTRange;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class RotateRight extends AbstractCoreFunctionEvaluator {

	public RotateRight() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 2, 3);

		IExpr arg1 = F.eval(ast.arg1());
		try {
			if (arg1.isAST()) {
				final IAST result = F.ast(arg1.head());
				if (ast.size() == 2) {
					ASTRange range = ((IAST) arg1).args();
					range.rotateRight(result, 1);
					// Rotating.rotateRight((IAST) list.arg1(), result, 1, 1);
					return result;
				} else {
					IExpr arg2 = F.eval(ast.arg2());
					if (arg2.isInteger()) {
						int n = Validate.checkIntType(ast, 2, 1);
						ASTRange range = ((IAST) arg1).args();
						range.rotateRight(result, n);
						// Rotating.rotateRight((IAST) list.arg1(), result, n, 1);
						return result;
					}
				}

			}
		} catch (final ArithmeticException e) {

		}
		return null;
	}

}
