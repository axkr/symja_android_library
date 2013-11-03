package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * 
 * See: <a href="http://en.wikipedia.org/wiki/Curl_(mathematics)">Wikipedia:Curl (mathematics)</a>
 * 
 * Example: <code>Curl[{f[u,v,w],f[v,w,u],f[w,u,v],f[x]}, {u,v,w}]</code>.
 */
public class Curl extends AbstractFunctionEvaluator {
	public Curl() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);
		if (ast.arg1().isVector() >= 3) {
			if (ast.arg2().isVector() == 3) {
				IAST variables = (IAST) ast.arg2();
				IAST vector = (IAST) ast.arg1();
				IAST curlVector = F.List();
				curlVector.add(F.Subtract(F.D(vector.arg3(), variables.arg2()), F.D(vector.arg2(), variables.arg3())));
				curlVector.add(F.Subtract(F.D(vector.arg1(), variables.arg3()), F.D(vector.arg3(), variables.arg1())));
				curlVector.add(F.Subtract(F.D(vector.arg2(), variables.arg1()), F.D(vector.arg1(), variables.arg2())));
				for (int i = 4; i < vector.size(); i++) {
					curlVector.add(vector.get(i));
				}
				return curlVector;
			}
		}

		return null;
	}

}
