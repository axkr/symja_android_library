package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * 
 * See: <a href="http://en.wikipedia.org/wiki/Curl_(mathematics)">Wikipedia:Curl
 * (mathematics)</a>
 * 
 * Example: <code>Curl[{f[u,v,w],f[v,w,u],f[w,u,v],f[x]}, {u,v,w}]</code>.
 */
public class Curl extends AbstractFunctionEvaluator {
	public Curl() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);
		if (ast.get(1).isVector() >= 3) {
			if (ast.get(2).isVector() == 3) {
				IAST variables = (IAST) ast.get(2);
				IAST vector = (IAST) ast.get(1);
				IAST curlVector = F.List();
				curlVector.add(F.Subtract(F.D(vector.get(3), variables.get(2)), F.D(vector.get(2), variables.get(3))));
				curlVector.add(F.Subtract(F.D(vector.get(1), variables.get(3)), F.D(vector.get(3), variables.get(1))));
				curlVector.add(F.Subtract(F.D(vector.get(2), variables.get(1)), F.D(vector.get(1), variables.get(2))));
				for (int i = 4; i < vector.size(); i++) {
					curlVector.add(vector.get(i));
				}
				return curlVector;
			}
		}

		return null;
	}

}
