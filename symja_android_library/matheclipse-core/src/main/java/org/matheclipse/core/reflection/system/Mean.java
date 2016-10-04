package org.matheclipse.core.reflection.system;

import org.hipparchus.stat.StatUtils;
import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * 
 * See <a href="http://en.wikipedia.org/wiki/Arithmetic_mean">Arithmetic
 * mean</a>
 */
public class Mean extends AbstractTrigArg1 {
	public Mean() {
		// default ctor
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		if (arg1.isRealVector()) {
			return F.num(StatUtils.mean(arg1.toDoubleVector()));
		}
		if (arg1.isList()) {
			final IAST list = (IAST) arg1;
			return F.Times(list.apply(F.Plus), F.Power(F.integer(list.size() - 1), F.CN1));
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.NOATTRIBUTE);
	}

}
