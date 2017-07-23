package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <pre>
 * Divergence({f1, f2, f3,...},{x1, x2, x3,...})
 * </pre>
 * 
 * <blockquote>
 * <p>
 * compute the divergence.
 * </p>
 * </blockquote>
 * <p>
 * See:<br />
 * </p>
 * <ul>
 * <li><a href="http://en.wikipedia.org/wiki/Divergence">Wikipedia - Divergence</a></li>
 * </ul>
 * <h3>Examples</h3>
 * 
 * <pre>
 * &gt;&gt; Divergence({x^2, y^3},{x, y})
 * 2*x+3*y^2
 * </pre>
 */
public class Divergence extends AbstractFunctionEvaluator {
	public Divergence() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);
		if ((ast.arg1().isVector() == ast.arg2().isVector()) && (ast.arg1().isVector() >= 0)) {
			IAST vector = (IAST) ast.arg1();
			IAST variables = (IAST) ast.arg2();
			IAST divergenceValue = F.Plus();
			for (int i = 1; i < vector.size(); i++) {
				divergenceValue.append(F.D(vector.get(i), variables.get(i)));
			}
			return divergenceValue;
		}

		return F.NIL;
	}

}
