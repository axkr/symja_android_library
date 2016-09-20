package org.matheclipse.core.reflection.system;

import java.util.function.Predicate;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class OrderedQ extends AbstractFunctionEvaluator implements Predicate<IAST> {
	/**
	 * Constructor for the unary predicate
	 */
	public final static OrderedQ CONST = new OrderedQ();

	public OrderedQ() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);
		return F.bool(test(((IAST) ast.arg1())));
	}

	@Override
	public boolean test(IAST ast) {
		return ast.args().compareAdjacent((x, y) -> x.isLEOrdered(y));
	}

}
