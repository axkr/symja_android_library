package org.matheclipse.core.reflection.system;

import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

public class Ramp extends AbstractEvaluator {

	public Ramp() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		IExpr expr = ast.arg1();

		if (expr.isPositiveResult() || //
				expr.isInfinity()) {
			return expr;
		}
		if (expr.isNegativeResult() || //
				expr.isNegativeInfinity() || //
				expr.isZero()) {
			return F.C0;
		}
		return F.NIL;

	}

	@Override
	public int[] expectedArgSize() {
		return IOFunctions.ARGS_1_1;
	}

	@Override
	public void setUp(ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
	}
}
