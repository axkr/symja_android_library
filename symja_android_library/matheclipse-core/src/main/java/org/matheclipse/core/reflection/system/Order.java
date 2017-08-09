package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <pre>
 * Order(a, b)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * is <code>0</code> if <code>a</code> equals <code>b</code>. Is <code>-1</code> or <code>1</code> according to
 * canonical order of <code>a</code> and <code>b</code>.
 * </p>
 * </blockquote>
 * <h3>Examples</h3>
 * 
 * <pre>
 * &gt;&gt; Order(3,4)
 * 1
 * 
 * &gt;&gt; Order(4,3)
 * -1
 * </pre>
 */
public class Order extends AbstractFunctionEvaluator {

	public Order() {
	}

	/**
	 * Compares the first expression with the second expression for order. Returns 1, 0, -1 as this expression is
	 * canonical less than, equal to, or greater than the specified expression. <br>
	 * <br>
	 * (<b>Implementation note</b>: see the different results in the <code>IExpr#compareTo(IExpr)</code> method)
	 * 
	 * @see org.matheclipse.core.interfaces.IExpr#compareTo(IExpr)
	 */
	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);

		final int cp = ast.arg1().compareTo(ast.arg2());
		if (cp < 0) {
			return F.C1;
		} else if (cp > 0) {
			return F.CN1;
		}
		return F.C0;
	}

}
