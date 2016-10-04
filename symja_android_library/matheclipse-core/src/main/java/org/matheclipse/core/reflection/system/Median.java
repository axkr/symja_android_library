package org.matheclipse.core.reflection.system;

import org.hipparchus.stat.StatUtils;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * 
 * See <a href="http://en.wikipedia.org/wiki/Median">Median</a>
 */
public class Median extends AbstractTrigArg1 {
	public Median() {
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		if (arg1.isRealVector()) {
			return F.num(StatUtils.percentile(arg1.toDoubleVector(), 50));
		}
		if (arg1.isList()) {
			final IAST list = (IAST) arg1;
			if (list.size() > 1) {
				final IAST sortedList = list.copy();
				EvalAttributes.sort(sortedList);
				int size = sortedList.size();
				if ((size & 0x00000001) == 0x00000001) {
					// odd number of elements
					size = size / 2;
					return F.Times(F.Plus(sortedList.get(size), sortedList.get(size + 1)), F.C1D2);
				} else {
					return sortedList.get(size / 2);
				}
			}
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.NOATTRIBUTE);
	}
}
