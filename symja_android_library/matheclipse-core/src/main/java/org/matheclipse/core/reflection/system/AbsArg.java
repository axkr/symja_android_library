package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Return a list with the 2 values <code>Abs(x), Arg(x)</code> for a complex
 * number <code>x</code>.
 *
 */
public class AbsArg extends AbstractEvaluator {

	public AbsArg() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		int size = ast.size();
		if (size == 2) {
			IExpr z = ast.arg1();
			return F.List(F.Abs(z), F.Arg(z));
		}
		return F.NIL;
	}

	@Override
	public void setUp(ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE);
	}
}
