package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * {@code !=} operator implementation.
 * 
 */
public class Unequal extends Equal {

	public Unequal() {
		// default ctor
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (ast.size() > 1) {
			IExpr.COMPARE_TERNARY b = IExpr.COMPARE_TERNARY.UNDEFINED;
			if (ast.isAST2()) {
				IExpr arg1 = F.expandAll(ast.arg1(), true, true);
				IExpr arg2 = F.expandAll(ast.arg2(), true, true);

				b = prepareCompare(arg1, arg2);
				if (b == IExpr.COMPARE_TERNARY.FALSE) {
					return F.True;
				}
				if (b == IExpr.COMPARE_TERNARY.TRUE) {
					return F.False;
				}

				IExpr result = simplifyCompare(arg1, arg2, F.Unequal);
				if (result.isPresent()) {
					return result;
				}
			}
			
			IAST result = ast.copy();
			for (int i = 1; i < result.size(); i++) {
				result.set(i, F.expandAll(result.get(i), true, true));
			}
			int i = 2;
			int j;
			while (i < result.size()) {
				j = i;
				while (j < result.size()) {
					b = compareTernary(result.get(i - 1), result.get(j++));
					if (b == IExpr.COMPARE_TERNARY.TRUE) {
						return F.False;
					}
					if (b == IExpr.COMPARE_TERNARY.UNDEFINED) {
						return F.NIL;
					}
				}
				i++;
			}
			return F.True;

		}
		return F.NIL;
	}

}