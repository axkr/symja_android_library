package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

public class Factorial2 extends AbstractTrigArg1 {

	public Factorial2() {
	}

	public static IInteger factorial2(final IInteger iArg) {
		IInteger result = F.C1;
		final IInteger biggi = iArg;
		IInteger start;
		if (biggi.compareTo(F.C0) == -1) {
			result = F.CN1;
			if (biggi.isOdd()) {
				start = F.CN3;
			} else {
				start = F.CN2;
			}
			for (IInteger i = start; i.compareTo(biggi) >= 0; i = i.add(F.CN2)) {
				result = result.multiply(i);
			}
		} else {
			if (biggi.isOdd()) {
				start = F.C3;
			} else {
				start = F.C2;
			}
			for (IInteger i = start; i.compareTo(biggi) <= 0; i = i.add(F.C2)) {
				result = result.multiply(i);
			}
		}

		return result;
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		if (arg1.isInteger()) {
			if (!arg1.isNegative()) {
				return factorial2((IInteger) arg1);
			}
			try {
				int n = ((IInteger) arg1).toInt();
				switch (n) {
				case -1:
					return F.C1;
				case -2:
					return F.CComplexInfinity;
				case -3:
					return F.CN1;
				case -4:
					return F.CComplexInfinity;
				case -5:
					return F.C1D3;
				case -6:
					return F.CComplexInfinity;
				case -7:
					return F.fraction(-1L, 15L);
				}
			} catch (ArithmeticException ae) {

			}
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(newSymbol);
	}
}
