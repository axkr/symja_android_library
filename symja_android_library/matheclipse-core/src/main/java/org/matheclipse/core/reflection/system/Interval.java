package org.matheclipse.core.reflection.system;

import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
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
		if (ast.isInterval1()) {
			IAST list = (IAST) ast.arg1();
			try {
				ISignedNumber min = (ISignedNumber) list.arg1();
				ISignedNumber max = (ISignedNumber) list.arg2();
				if (min.greaterThan(max).isTrue()) {
					return F.Interval(F.List(max,min));
//					throw new WrongArgumentType(ast, ast.arg1(), 1, "Min > Mac in interval");
				}
			} catch (ClassCastException cca) {
				// do nothing
			}
		}
		return F.NIL;
	}

	public int[] expectedArgSize() {
		return IOFunctions.ARGS_1_1;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
	}

}
