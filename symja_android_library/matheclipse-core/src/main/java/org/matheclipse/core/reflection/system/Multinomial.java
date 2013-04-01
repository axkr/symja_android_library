package org.matheclipse.core.reflection.system;

import java.math.BigInteger;
import java.util.List;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Returns the multinomial coefficient.
 * 
 * See <a
 * href="http://en.wikipedia.org/wiki/Multinomial_coefficient">Multinomial
 * coefficient</a>
 */
public class Multinomial extends AbstractFunctionEvaluator {
	public Multinomial() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 3);

		for (int i = 1; i < ast.size(); i++) {
			if (!(ast.get(i).isInteger())) {
				return null;
			}
			if (((IInteger) ast.get(i)).isNegative()) {
				return null;
			}
		}

		return F.integer(multinomial(ast));

	}

	public static BigInteger multinomial(final List<IExpr> ast) {
		BigInteger[] k = new BigInteger[ast.size() - 1];
		BigInteger n = BigInteger.ZERO;
		for (int i = 1; i < ast.size(); i++) {
			k[i - 1] = ((IInteger) ast.get(i)).getBigNumerator();
			n = n.add(k[i - 1]);
		}

		BigInteger result = Factorial.factorial(n);
		for (int i = 0; i < k.length; i++) {
			result = result.divide(Factorial.factorial(k[i]));
		}
		return result;
	}

	/**
	 * 
	 * @param indices
	 *          the non-negative coefficients
	 * @param n
	 *          the sum of the non-negative coefficients
	 * @return
	 */
	public static BigInteger multinomial(final int[] indices, final int n) {
		BigInteger bn = BigInteger.valueOf(n);
		BigInteger result = Factorial.factorial(bn);
		for (int i = 0; i < indices.length; i++) {
			if (indices[i] != 0) {
				result = result.divide(Factorial.factorial(BigInteger.valueOf(indices[i])));
			}
		}
		return result;
	}

	@Override
	public void setUp(ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}
}
