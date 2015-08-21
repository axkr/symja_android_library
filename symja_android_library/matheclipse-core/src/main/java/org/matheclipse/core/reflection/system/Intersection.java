package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.ExprComparator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Intersection of 2 sets
 * 
 * See: <a href="http://en.wikipedia.org/wiki/Intersection_(set_theory)">Intersection (set theory)</a>
 */
public class Intersection extends AbstractFunctionEvaluator {

	public Intersection() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);
		
		if (!ast.arg1().isAtom() && !ast.arg2().isAtom()) {
			final IAST result = F.List();
			((IAST) ast.arg1()).args().intersection(result,
					((IAST) ast.arg2()).args());
			return result.args().sort(ExprComparator.CONS);
		}
		return null;
	}

}
