package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.ASTRange;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class RotateLeft extends AbstractCoreFunctionEvaluator {

	public RotateLeft() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 3);

		IExpr arg1 = engine.evaluate(ast.arg1());
		if (arg1.isAST()) {
			final IAST result = F.ast(arg1.head());
			if (ast.size() == 2) {
				ASTRange range = ((IAST) arg1).args();
				range.rotateLeft(result, 1);
				// Rotating.rotateLeft((IAST) list.arg1(), result, 2, 1);
				return result;
			} else {
				IExpr arg2 = engine.evaluate(ast.arg2());
				if (arg2.isInteger()) {
					int n = Validate.checkIntType(arg2);

					ASTRange range = ((IAST) arg1).args();
					range.rotateLeft(result, n);
					return result;
				}
			}

		}
		return F.UNEVALED;
	}

}
