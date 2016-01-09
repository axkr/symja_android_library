package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Create a unit vector
 * 
 * See <a href="http://en.wikipedia.org/wiki/Unit_vector">Wikipedia - Unit vector</a>
 */
public class UnitVector extends AbstractFunctionEvaluator {

	public UnitVector() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 3);

		if (ast.size() == 3) {
			int n = Validate.checkIntType(ast, 1);
			int k = Validate.checkIntType(ast, 2);
			if (k <= n) {
				IAST vector = F.ListC(n);
				for (int i = 0; i < n; i++) {
					vector.add(F.C0);
				}
				vector.set(k, F.C1);
				return vector;
			}
			return null;
		} 
		
		if (ast.arg1().isInteger()) {
			int k = Validate.checkIntType(ast, 1);
			if (k == 1) {
				return F.List(F.C1, F.C0);
			}
			if (k == 2) {
				return F.List(F.C0, F.C1);
			}
		}
		return null;
	}

}
