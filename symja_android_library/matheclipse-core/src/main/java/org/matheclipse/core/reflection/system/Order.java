package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.WrongNumberOfArguments;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Compares the first expression with the second expression for order.  Returns 1, 0, -1
 * as this expression is canonical less than, equal to, or greater than the specified expression.
 * <br><br>
 * (<b>Implementation note</b>: see the different results in the <code>IExpr#compareTo(IExpr)</code> method)
 * @see org.matheclipse.core.interfaces.IExpr#compareTo(IExpr)
 */
public class Order extends AbstractFunctionEvaluator {

	public Order() {
	}

	@Override
	public IExpr evaluate(final IAST functionList) {
		if (functionList.size() != 3) {
			throw new WrongNumberOfArguments(functionList, 2, functionList.size()-1);
		}
		final int cp = functionList.get(1).compareTo(functionList.get(2));
		if (cp < 0) {
			return F.C1;
		} else if (cp > 0) {
			return F.CN1;
		}
		return F.C0;
	}

}
