package org.matheclipse.core.reflection.system;

import java.math.BigInteger;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Returns the gcd of two positive numbers plus the bezout relations
 * 
 * See
 * <a href="http://en.wikipedia.org/wiki/Extended_Euclidean_algorithm">Extended
 * Euclidean algorithm</a> and See
 * <a href="http://en.wikipedia.org/wiki/B%C3%A9zout%27s_identity">Bézout's
 * identity</a>
 * 
 * @author jeremy watts
 * @version 05/03/07
 */
public class ExtendedGCD extends AbstractFunctionEvaluator {

	public ExtendedGCD() {
	}

	/**
	 * Returns the gcd of two positive numbers plus the bezout relations
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Extended_Euclidean_algorithm">
	 * Extended Euclidean algorithm</a> and See
	 * <a href="http://en.wikipedia.org/wiki/B%C3%A9zout%27s_identity">Bézout's
	 * identity</a>
	 * 
	 */
	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 3);
		IExpr arg;
		for (int i = 1; i < ast.size(); i++) {
			arg = ast.get(i);
			if (!arg.isInteger()) {
				return F.NIL;
			}
			if (!((IInteger) arg).isPositive()) {
				return F.NIL;
			}
		}
		// all arguments are positive integers now

		try {

			BigInteger factor = BigInteger.ONE;
			BigInteger[] subBezouts = new BigInteger[ast.size() - 1];
			BigInteger gcd = extendedGCD(ast, subBezouts);
			// convert the Bezout numbers to sublists
			IAST subList = F.List();
			for (int i = 0; i < subBezouts.length; i++) {
				subList.append(F.integer(subBezouts[i]));
			}
			// create the output list
			IAST list = F.List();
			list.append(F.integer(gcd));
			list.append(subList);
			return list;
		} catch (ArithmeticException ae) {
			if (Config.SHOW_STACKTRACE) {
				ae.printStackTrace();
			}
		}
		return F.NIL;
	}

	public static BigInteger extendedGCD(final IAST ast, BigInteger[] subBezouts) {
		BigInteger factor;
		BigInteger gcd = ((IInteger) ast.arg1()).toBigNumerator();
		Object[] stepResult = extendedGCD(((IInteger) ast.arg2()).toBigNumerator(), gcd);

		gcd = (BigInteger) stepResult[0];
		subBezouts[0] = ((BigInteger[]) stepResult[1])[0];
		subBezouts[1] = ((BigInteger[]) stepResult[1])[1];

		for (int i = 3; i < ast.size(); i++) {
			stepResult = extendedGCD(((IInteger) ast.get(i)).toBigNumerator(), gcd);
			gcd = (BigInteger) stepResult[0];
			factor = ((BigInteger[]) stepResult[1])[0];
			for (int j = 0; j < i - 1; j++) {
				subBezouts[j] = subBezouts[j].multiply(factor);
			}
			subBezouts[i - 1] = ((BigInteger[]) stepResult[1])[1];
		}
		return gcd;
	}

	/**
	 * Returns the gcd of two positive numbers plus the bezout relation
	 */
	public static Object[] extendedGCD(BigInteger numberOne, BigInteger numberTwo) throws ArithmeticException {
		Object[] results = new Object[2];
		BigInteger dividend;
		BigInteger divisor;
		BigInteger quotient;
		BigInteger remainder;
		BigInteger xValue;
		BigInteger yValue;
		BigInteger tempValue;
		BigInteger lastxValue;
		BigInteger lastyValue;
		BigInteger gcd = BigInteger.ONE;
		BigInteger mValue = BigInteger.ONE;
		BigInteger nValue = BigInteger.ONE;
		boolean exchange;

		remainder = BigInteger.ONE;
		xValue = BigInteger.ZERO;
		lastxValue = BigInteger.ONE;
		yValue = BigInteger.ONE;
		lastyValue = BigInteger.ZERO;
		if ((!((numberOne.compareTo(BigInteger.ZERO) == 0) || (numberTwo.compareTo(BigInteger.ZERO) == 0)))
				&& (((numberOne.compareTo(BigInteger.ZERO) == 1) && (numberTwo.compareTo(BigInteger.ZERO) == 1)))) {
			if (numberOne.compareTo(numberTwo) == 1) {
				exchange = false;
				dividend = numberOne;
				divisor = numberTwo;
			} else {
				exchange = true;
				dividend = numberTwo;
				divisor = numberOne;
			}

			BigInteger[] divisionResult = null;
			while (remainder.compareTo(BigInteger.ZERO) != 0) {
				divisionResult = dividend.divideAndRemainder(divisor);
				quotient = divisionResult[0];
				remainder = divisionResult[1];

				dividend = divisor;
				divisor = remainder;

				tempValue = xValue;
				xValue = lastxValue.subtract(quotient.multiply(xValue));
				lastxValue = tempValue;

				tempValue = yValue;
				yValue = lastyValue.subtract(quotient.multiply(yValue));
				lastyValue = tempValue;
			}

			gcd = dividend;
			if (exchange) {
				mValue = lastyValue;
				nValue = lastxValue;
			} else {
				mValue = lastxValue;
				nValue = lastyValue;
			}
		} else {
			throw new ArithmeticException("ExtendedGCD contains wrong arguments");
		}
		results[0] = gcd;
		BigInteger[] values = new BigInteger[2];
		values[0] = nValue;
		values[1] = mValue;
		results[1] = values;
		return results;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE);
		super.setUp(newSymbol);
	}
}
