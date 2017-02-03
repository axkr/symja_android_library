package org.matheclipse.core.reflection.system;

import java.math.BigInteger;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

import com.google.common.math.BigIntegerMath;

/**
 */
public class BellB extends AbstractFunctionEvaluator {

	public BellB() {
	}

	/**
	 * Generates the Bell Number of the given index, where B(1) is 1. This is
	 * recursive.
	 * 
	 * @param index
	 * @return
	 */
	public static BigInteger generateBellNumber(int index) {
		if (index > 1) {
			BigInteger sum = BigInteger.ZERO;
			for (int i = 0; i < index; i++) {
				BigInteger prevBellNum = generateBellNumber(i);
				BigInteger binomialCoeff = BigIntegerMath.binomial(index - 1, i);
				sum = sum.add(binomialCoeff.multiply(prevBellNum));
			}
			return sum;
		}
		return BigInteger.ONE;
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);

		IExpr arg1 = ast.arg1();
		if (arg1.isOne()) {
			return F.C1;
		}
		if (arg1.isInteger() && arg1.isPositive()) {
			try {
				int index = ((IInteger) arg1).toInt();
				BigInteger bellB = generateBellNumber(index);
				return F.integer(bellB);
			} catch (ArithmeticException ae) {
				//
			}
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE);
	}
}
