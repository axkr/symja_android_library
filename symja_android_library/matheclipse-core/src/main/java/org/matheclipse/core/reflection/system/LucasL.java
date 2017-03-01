package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <p>
 * Lucas number.
 * </p>
 * See: <a href= "https://en.wikipedia.org/wiki/Lucas_number">Wikipedia: Lucas
 * number</a>
 */
public class LucasL extends AbstractTrigArg1 {

	public LucasL() {
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		try {
			if (arg1.isInteger()) {
				IInteger n = (IInteger) arg1;
				// LucasL(n) = Fibonacci(n-1) + Fibonacci(n+1)
				return Fibonacci.generateFibonacci(n.subtract(F.CN1)).add(Fibonacci.generateFibonacci(n.add(F.CN1)));
			}
		} catch (ArithmeticException ae) {
			// because of toInt() method
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
	}
}
