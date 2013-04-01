package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * See <a href="http://en.wikipedia.org/wiki/Divergence">Wikipedia - Divergence</a>
 * 
 * Example: <code>Divergence[{f[u,v,w],f[v,w,u],f[w,u,v]}, {u,v,w}]</code>.
 */
public class Divergence extends AbstractFunctionEvaluator {
	public Divergence() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);
		if ((ast.get(1).isVector() == ast.get(2).isVector())
				&& (ast.get(1).isVector() >= 0)) {
			IAST vector = (IAST) ast.get(1);
			IAST variables = (IAST) ast.get(2);
			IAST divergenceValue = F.Plus();
			for (int i = 1; i < vector.size(); i++) {
				divergenceValue.add(F.D(vector.get(i), variables.get(i)));
			}
			return divergenceValue;
		}

		return null;
	}

}
