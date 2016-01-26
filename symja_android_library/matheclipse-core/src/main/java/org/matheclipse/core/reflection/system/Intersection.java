package org.matheclipse.core.reflection.system;

import java.util.Set;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.ExprComparator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Intersection of 2 sets
 * 
 * See:
 * <a href="http://en.wikipedia.org/wiki/Intersection_(set_theory)">Intersection
 * (set theory)</a>
 */
public class Intersection extends AbstractFunctionEvaluator {

	public Intersection() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 3);

		if (ast.size() == 2 && ast.arg1().isAST()) {
			final IAST result = F.List();
			IAST arg1 = (IAST) ast.arg1();
			Set<IExpr> set = arg1.asSet();
			for (IExpr IExpr : set) {
				result.add(IExpr);
			}
			return result.args().sort(ExprComparator.CONS);
		}

		if (ast.arg1().isAST() && ast.arg2().isAST()) {
			final IAST result = F.List();
			((IAST) ast.arg1()).args().intersection(result, ((IAST) ast.arg2()).args());
			return result.args().sort(ExprComparator.CONS);
		}
		return F.UNEVALED;
	}

}
