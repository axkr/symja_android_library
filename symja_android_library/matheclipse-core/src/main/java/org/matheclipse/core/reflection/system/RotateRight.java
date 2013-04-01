package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.ASTRange;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class RotateRight extends AbstractFunctionEvaluator {

	public RotateRight() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 2, 3);

		try {
			if (!ast.get(1).isAtom()) {
				final IAST result = F.ast(ast.get(1).head());
				if (ast.size() == 2) {
					ASTRange range = ((IAST) ast.get(1)).args();
					range.rotateRight(result, 1);
					// Rotating.rotateRight((IAST) list.get(1), result, 1, 1);
					return result;
				} else {
					if (ast.get(2).isInteger()) {
						int n = Validate.checkIntType(ast, 2, 1);
						ASTRange range = ((IAST) ast.get(1)).args();
						range.rotateRight(result, n);
						// Rotating.rotateRight((IAST) list.get(1), result, n, 1);
						return result;
					}
				}

			}
		} catch (final ArithmeticException e) {

		}
		return null;
	}

}
