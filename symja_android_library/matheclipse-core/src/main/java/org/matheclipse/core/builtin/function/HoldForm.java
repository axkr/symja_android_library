package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <pre>HoldForm(expr)
 * </pre>
 * <blockquote><p><code>HoldForm</code> doesn't evaluate <code>expr</code> and didn't appear in the output</p>
 * </blockquote>
 * <h3>Examples</h3>
 * <pre>&gt;&gt; HoldForm(3*2)
 * 3*2
 * </pre>
 */
public class HoldForm extends AbstractCoreFunctionEvaluator {

	public HoldForm() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		return F.NIL;
	}
 
	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.HOLDALL);
	}

}
