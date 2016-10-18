package org.matheclipse.core.builtin.function;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * 
 */
public class Insert extends AbstractCoreFunctionEvaluator {

	public Insert() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 4);
		
		IExpr arg1 = engine.evaluate(ast.arg1());
		IAST arg1AST = Validate.checkASTType(arg1);
		IExpr arg2 = engine.evaluate(ast.arg2());
		IExpr arg3 = engine.evaluate(ast.arg3());
		if (arg3.isInteger()) {
			try {
				int i = Validate.checkIntType(arg3,  Integer.MIN_VALUE);
				if (i < 0) {
					i = 1 + arg1AST.size() + i;
				}
				if (i > 0 && i < arg1AST.size()) {
					return arg1AST.appendAtClone(i, arg2);
				}
			} catch (final IndexOutOfBoundsException e) {
				if (Config.DEBUG) {
					e.printStackTrace();
				}
			}
		}
		return F.NIL;
	}

}