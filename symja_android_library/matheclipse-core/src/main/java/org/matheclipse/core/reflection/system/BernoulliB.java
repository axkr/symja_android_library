package org.matheclipse.core.reflection.system;

import java.math.BigInteger;

import org.apache.commons.math4.fraction.BigFraction;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.AbstractFractionSym;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;

import com.google.common.math.BigIntegerMath;
import com.google.common.math.LongMath;

/**
 * Compute the Bernoulli number of the first kind.
 * 
 * See <a href="http://en.wikipedia.org/wiki/Bernoulli_number">Wikipedia -
 * Bernoulli number</a>. <br/>
 * For better performing implementations see <a href=
 * "http://oeis.org/wiki/User:Peter_Luschny/ComputationAndAsymptoticsOfBernoulliNumbers"
 * >ComputationAndAsymptoticsOfBernoulliNumbers</a>
 * 
 */
public class BernoulliB extends AbstractFunctionEvaluator {

	public BernoulliB() {
	}

	@Override
	public IExpr evaluate(IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 3);

		if (ast.size() == 2) {
			// bernoulli number
			if (ast.arg1().isInteger()) {
				IFraction bernoulli = bernoulliNumber((IInteger) ast.arg1());
				if (bernoulli != null) {
					return bernoulli;
				}
			}
		}
		return F.NIL;
	}

	/**
	 * Compute the Bernoulli number of the first kind.
	 * 
	 * @param biggi
	 * @return
	 */
	// public static IFraction bernoulliNumber(final IInteger biggi) {
	// return bernoulliNumber(biggi.getBigNumerator());
	// }

	/**
	 * Compute the Bernoulli number of the first kind.
	 * 
	 * @param biggi
	 * @return
	 */
	public static IFraction bernoulliNumber(final IInteger biggi) {
		int N = 0;
		try {
			N = biggi.toInt(); // NumberUtil.toInt(biggi);
			return bernoulliNumber(N);
		} catch (ArithmeticException ae) {
			//
		}
		return null;
	}

	/**
	 * Compute the Bernoulli number of the first kind.
	 * 
	 * @param n
	 * @return
	 */
	public static IFraction bernoulliNumber(int n) {
		if (n == 0) {
			return AbstractFractionSym.ONE;
		} else if (n == 1) {
			return AbstractFractionSym.valueOf(-1, 2);
		} else if (n % 2 != 0) {
			return AbstractFractionSym.ZERO;
		}
		IFraction[] bernoulli = new IFraction[n + 1];
		bernoulli[0] = AbstractFractionSym.ONE;
		bernoulli[1] = AbstractFractionSym.valueOf(-1, 2);// new BigFraction(-1,
															// 2);
		for (int k = 2; k <= n; k++) {
			bernoulli[k] = AbstractFractionSym.ZERO;
			for (int i = 0; i < k; i++) {
				if (!bernoulli[i].isZero()) {
					IFraction bin = AbstractFractionSym.valueOf(BigIntegerMath.binomial(k + 1, k + 1 - i));
					bernoulli[k] = bernoulli[k].sub(bin.mul(bernoulli[i]));
				}
			}
			bernoulli[k] = bernoulli[k].div(AbstractFractionSym.valueOf(k + 1));
		}
		return bernoulli[n];
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
	}

}
