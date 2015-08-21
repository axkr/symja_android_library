package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.visit.VisitorLevelSpecification;

import com.google.common.base.Function;

/**
 * Sums up all elements of a list.
 * 
 */
public class Total extends AbstractFunctionEvaluator {

	public Total() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 2, 3);

		VisitorLevelSpecification level = null;
		Function<IExpr, IExpr> tf = Functors.apply(F.Plus);
		if (ast.size() == 3) {
			level = new VisitorLevelSpecification(tf, ast.arg2(), false);
			// increment level because we select only subexpressions
		} else {
			level = new VisitorLevelSpecification(tf, 1, false);
		}

		if (ast.arg1().isAST()) {
			// increment level because we select only subexpressions
			level.incCurrentLevel();
			IExpr result = ast.arg1().accept(level);
			return result;
		}

		return null;
	}

}
