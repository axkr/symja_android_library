package org.matheclipse.core.builtin.function;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <pre>Chop(numerical-expr)
 * </pre>
 * <blockquote><p>replaces numerical values in the <code>numerical-expr</code> which are close to zero with symbolic value <code>0</code>.</p>
 * </blockquote>
 * <h3>Examples</h3>
 * <pre>&gt;&gt; Chop(0.00000000001)
 * 0
 * </pre>
 */
public class Chop extends AbstractCoreFunctionEvaluator {
	public final static double DEFAULT_CHOP_DELTA = 1.0e-10;
	public Chop() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 3);

		IExpr arg1 = ast.arg1();
		double delta = DEFAULT_CHOP_DELTA;
		if (ast.isAST2() && ast.arg2() instanceof INum) {
			delta = ((INum) ast.arg2()).getRealPart();
		}
		try {
			arg1 = engine.evaluate(arg1);
			if (arg1.isAST()) {
				IAST list = (IAST) arg1;
				// Chop[{a,b,c}] -> {Chop[a],Chop[b],Chop[c]}
				return list.mapThread(F.Chop(F.Null),1);
			}
			if (arg1.isNumber()) {
				return F.chopNumber((INumber)arg1, delta);
			}
		} catch (Exception e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		}

		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.LISTABLE);
	}
}
