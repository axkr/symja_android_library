package org.matheclipse.core.reflection.system;

import java.math.BigInteger;

import org.apache.commons.math4.fraction.BigFraction;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.NumberUtil;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;

/**
 * Compute the Bernoulli number of the first kind.
 * 
 * See <a href="http://en.wikipedia.org/wiki/Bernoulli_number">Wikipedia - Bernoulli number</a>. <br/>
 * For better performing implementations see <a href=
 * "http://oeis.org/wiki/User:Peter_Luschny/ComputationAndAsymptoticsOfBernoulliNumbers"
 * >ComputationAndAsymptoticsOfBernoulliNumbers</a>
 * 
 */
public class BernoulliB extends AbstractFunctionEvaluator {

	public BernoulliB() {
	}

	@Override
	public IExpr evaluate(IAST ast) {
		Validate.checkRange(ast, 2, 3);

		if (ast.size() == 2) {
			// bernoulli number
			if (ast.arg1().isInteger()) {
				BigFraction bernoulli = bernoulliNumber(((IInteger) ast.arg1()).getBigNumerator());
				if (bernoulli != null) {
					return F.fraction(bernoulli);
				}
			}
		}
		return null;
	}

	/**
	 * Compute the Bernoulli number of the first kind.
	 * 
	 * @param biggi
	 * @return
	 */
	public static IFraction bernoulliNumber(final IInteger biggi) {
		BigFraction bf = bernoulliNumber(biggi.getBigNumerator());
		if (bf != null) {
			return F.fraction(bf);
		}
		return null;
	}

	/**
	 * Compute the Bernoulli number of the first kind.
	 * 
	 * @param biggi
	 * @return
	 */
	public static BigFraction bernoulliNumber(final BigInteger biggi) {
		int N = 0;
		try {
			N = NumberUtil.toInt(biggi);
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
	public static BigFraction bernoulliNumber(int n) {
		if (n == 0) {
			return BigFraction.ONE;
		} else if (n == 1) {
			return new BigFraction(-1, 2);
		} else if (n % 2 != 0) {
			return BigFraction.ZERO;
		}
		BigFraction[] bernoulli = new BigFraction[n + 1];
		bernoulli[0] = BigFraction.ONE;
		bernoulli[1] = new BigFraction(-1, 2);
		for (int k = 2; k <= n; k++) {
			bernoulli[k] = BigFraction.ZERO;
			for (int i = 0; i < k; i++) {
				if (!bernoulli[i].equals(BigFraction.ZERO)) {
					BigFraction bin = new BigFraction(Binomial.binomial(k + 1, k + 1 - i));
					bernoulli[k] = bernoulli[k].subtract(bin.multiply(bernoulli[i]));
				}
			}
			bernoulli[k] = bernoulli[k].divide(new BigFraction(k + 1));
		}
		return bernoulli[n];
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
	}

}
