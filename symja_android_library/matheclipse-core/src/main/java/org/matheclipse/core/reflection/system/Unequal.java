package org.matheclipse.core.reflection.system;

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
	public IExpr evaluate(final IAST ast) {
		if (ast.size() > 1) {
			if (ast.size() == 3) {
				IExpr result = simplifyCompare(ast.arg1(), ast.arg2(), F.Unequal);
				if (result != null) {
					return result;
				}
			}
			COMPARE_RESULT b = COMPARE_RESULT.UNDEFINED;
			IAST result = ast.clone();
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
						return null;
					}
				}
				i++;
			}
			return F.True;

		}
		return null;
	}

}