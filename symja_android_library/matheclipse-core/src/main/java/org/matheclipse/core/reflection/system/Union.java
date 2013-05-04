package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.ExprComparator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Union of two sets. 
 * See <a href="http://en.wikipedia.org/wiki/Union_(set_theory)">Union (set theory)</a>
 */
public class Union extends AbstractFunctionEvaluator {

	public Union() {
	}

	@Override
	public IExpr evaluate(final IAST functionList) {
		if (functionList.size() != 3) {
			return null;
		}
		if (!functionList.get(1).isAtom() && !functionList.get(2).isAtom()) {
			final IAST result = F.List();
			((IAST) functionList.get(1)).args().union(result,
					((IAST) functionList.get(2)).args());
			return result.args().sort(ExprComparator.CONS);
		}
		return null;
	}

}
