package org.matheclipse.core.reflection.system;

import java.math.BigInteger;

import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.expression.AbstractIntegerSym;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;

import com.google.common.math.BigIntegerMath;
import com.google.common.math.LongMath;

/**
 * Returns the factorial of an integer n
 * 
 * See <a href="http://en.wikipedia.org/wiki/Factorial">Factorial</a>
 * 
 */
public class Factorial extends AbstractTrigArg1 {

	public Factorial() {
	}

	@Override
	public IExpr e1DblArg(final double arg1) {
		double d = org.apache.commons.math4.special.Gamma.gamma(arg1 + 1.0);
		return F.num(d);
	}

	public static IInteger factorial(final IInteger x) {
		try {
			int ni = x.toInt();

			BigInteger result;
			if (ni < 0) {
				result = BigIntegerMath.factorial(-1 * ni);
				if ((ni & 0x0001) == 0x0001) {
					// odd integer number
					result = result.multiply(BigInteger.valueOf(-1L));
				}
			} else {
				if (ni <= 20) {
					return AbstractIntegerSym.valueOf(LongMath.factorial(ni));
				}
				result = BigIntegerMath.factorial(ni);
			}
			return AbstractIntegerSym.valueOf(result);

		} catch (ArithmeticException ae) {
			//
		}

		IInteger result = F.C1;
		if (x.compareTo(F.C0) == -1) {
			result = F.CN1;

			for (IInteger i = F.CN2; i.compareTo(x) >= 0; i = i.add(F.CN1)) {
				result = result.multiply(i);
			}
		} else {
			for (IInteger i = F.C2; i.compareTo(x) <= 0; i = i.add(F.C1)) {
				result = result.multiply(i);
			}
		}
		return result;
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		if (arg1.isInteger()) {
			return factorial((IInteger) arg1);
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(symbol);
	}
}
