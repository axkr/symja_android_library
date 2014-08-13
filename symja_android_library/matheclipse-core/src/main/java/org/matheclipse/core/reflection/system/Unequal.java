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
				IExpr arg1 = ast.arg1();
				IExpr arg2 = ast.arg2();
				IExpr temp1 = F.evalExpandAll(arg1);
				IExpr temp2 = F.evalExpandAll(arg2);
				IExpr difference = F.eval(F.Subtract(temp1, temp2));
				if (difference.isNumber()) {
					if (difference.isZero()) {
						return F.False;
					}
					return F.True;
				}
				if (difference.isConstant()) {
					return F.True;
				}
				IExpr result = simplifyCompare(arg1, arg2, F.Unequal);
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