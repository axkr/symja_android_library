package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <pre>
 * FullForm(expression)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * shows the internal representation of the given <code>expression</code>.
 * </p>
 * </blockquote>
 * <h3>Examples</h3>
 * <p>
 * FullForm shows the difference in the internal expression representation:
 * </p>
 * 
 * <pre>
 * &gt;&gt;&gt; FullForm(x(x+1))
 * "x(Plus(1, x))"
 * 
 * &gt;&gt;&gt; FullForm(x*(x+1))
 * "Times(x, Plus(1, x))"
 * </pre>
 */
public class FullForm extends AbstractCoreFunctionEvaluator {

	public FullForm() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);

		return F.stringx(engine.evaluate(ast.arg1()).fullFormString());
	}

	@Override
	public void setUp(ISymbol newSymbol) {
	}
}
