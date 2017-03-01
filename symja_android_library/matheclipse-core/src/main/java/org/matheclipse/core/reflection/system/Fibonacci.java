package org.matheclipse.core.reflection.system;

import java.math.BigInteger;

import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.NumberUtil;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <p>
 * Fibonacci sequence. Algorithm in <code>O(log(n))</code> time.F
 * </p>
 * See:
 * <a href= "https://www.rosettacode.org/wiki/Fibonacci_sequence#Iterative_28">
 * Roseatta code: Fibonacci sequence.</a>
 */
public class Fibonacci extends AbstractTrigArg1 {

	public Fibonacci() {
	}

	/**
	 * <p>
	 * Fibonacci sequence. Algorithm in <code>O(log(n))</code> time.F
	 * </p>
	 * See: <a href=
	 * "https://www.rosettacode.org/wiki/Fibonacci_sequence#Iterative_28">
	 * Roseatta code: Fibonacci sequence.</a>
	 * 
	 * @param iArg
	 * @return
	 */
	public static IInteger generateFibonacci(final IInteger iArg) {
		IInteger a = F.C1;
		IInteger b = F.C0;
		IInteger c = F.C1;
		IInteger d = F.C0;
		IInteger result = F.C0;
		IInteger temp = iArg;
		if (iArg.isNegative()) {
			temp = temp.negate();
		}

		while (!temp.isZero()) {
			if (temp.isOdd()) {
				d = result.multiply(c);
				result = a.multiply(c).add(result.multiply(b).add(d));
				a = a.multiply(b).add(d);
			}

			d = c.multiply(c);
			c = b.multiply(c).shiftLeft(1).add(d);
			b = b.multiply(b).add(d);
			temp = temp.shiftRight(1);
		}
		if (iArg.isNegative() && iArg.isEven()) {
			return result.negate();
		}
		return result;
	}

	public static BigInteger fibonacci(long k) {
		return fibonacci(BigInteger.valueOf(k));
	}

	public static BigInteger fibonacci(BigInteger temp) {
		BigInteger a = BigInteger.ONE;
		BigInteger b = BigInteger.ZERO;
		BigInteger c = BigInteger.ONE;
		BigInteger d = BigInteger.ZERO;
		BigInteger result = BigInteger.ZERO;
		while (!temp.equals(BigInteger.ZERO)) {
			if (NumberUtil.isOdd(temp)) {
				d = result.multiply(c);
				result = a.multiply(c).add(result.multiply(b).add(d));
				a = a.multiply(b).add(d);
			}

			d = c.multiply(c);
			c = b.multiply(c).shiftLeft(1).add(d);
			b = b.multiply(b).add(d);
			temp = temp.shiftRight(1);
		}
		return result;
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		if (arg1.isInteger()) {
			return generateFibonacci((IInteger) arg1);
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
	}
}
