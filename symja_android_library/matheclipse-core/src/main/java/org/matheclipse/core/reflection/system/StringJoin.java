package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;

public class StringJoin extends AbstractFunctionEvaluator {

	public StringJoin() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 3);

		StringBuffer buf = new StringBuffer();
		for (int i = 1; i < ast.size(); i++) {
			if (ast.get(i) instanceof IStringX) {
				buf.append(ast.get(i).toString());
			} else {
				return null;
			}
		}
		return F.stringx(buf.toString());
 
	}
}