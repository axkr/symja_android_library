package org.matheclipse.core.reflection.system;

import java.util.Iterator;

import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.IntervalSym;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <code>Interval[{min1, max1},......]</code>
 */
public class Interval extends AbstractEvaluator {

	public Interval() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (ast.isEvalFlagOff(IAST.BUILT_IN_EVALED)) {
			IAST result = IntervalSym.normalize(ast, engine);
			if (result.isPresent()) {
				return result;
			}
		}
		return F.NIL;
	}

	public int[] expectedArgSize(IAST ast) {
		return IOFunctions.ARGS_1_INFINITY;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
	}

}
