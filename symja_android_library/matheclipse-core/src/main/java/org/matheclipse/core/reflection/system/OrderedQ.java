package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.IsLEOrdered;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

import com.google.common.base.Predicate;

public class OrderedQ extends AbstractFunctionEvaluator implements Predicate<IAST> {
	/**
	 * Constructor for the unary predicate
	 */
	public final static OrderedQ CONST = new OrderedQ();

	public OrderedQ() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);
		return F.bool(apply(((IAST) ast.get(1))));
	}

	public boolean apply(IAST ast) {
		return ast.args().compareAdjacent(new IsLEOrdered<IExpr>());
	}

}
