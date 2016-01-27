package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>!=</code> operator implementation.
 * 
 */
public class Unequal extends Equal {

	public Unequal() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (ast.size() > 1) {
			COMPARE_RESULT b = COMPARE_RESULT.UNDEFINED;
			if (ast.size() == 3) {
				IExpr arg1 = F.expandAll(ast.arg1(), true, true);
				IExpr arg2 = F.expandAll(ast.arg2(), true, true);

				b = compare(arg1, arg2);
				if (b == COMPARE_RESULT.FALSE) {
					return F.True;
				}
				if (b == COMPARE_RESULT.TRUE) {
					return F.False;
				}

				IExpr result = simplifyCompare(arg1, arg2, F.Unequal);
				if (result != null) {
					return result;
				}
			}
			
			IAST result = ast.clone();
			for (int i = 1; i < result.size(); i++) {
				result.set(i, F.expandAll(result.get(i), true, true));
			}
			int i = 2;
			int j;
			while (i < result.size()) {
				j = i;
				while (j < result.size()) {
					b = compare(result.get(i - 1), result.get(j++));
					if (b == COMPARE_RESULT.TRUE) {
						return F.False;
					}
					if (b == COMPARE_RESULT.UNDEFINED) {
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