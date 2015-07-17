package org.matheclipse.core.reflection.system;

import java.math.BigInteger;

import org.matheclipse.core.eval.interfaces.AbstractArg2;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.NumberUtil;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

import com.google.common.math.BigIntegerMath;

/**
 * Returns the binomial coefficient of 2 integers.
 * 
 * See <a href="http://en.wikipedia.org/wiki/Binomial_coefficient">Binomial coefficient</a>
 */
public class Binomial extends AbstractArg2 {

	public static BigInteger binomial(final BigInteger n, final BigInteger k) {
		// k>n : by definition --> 0
		if (k.compareTo(n) > 0) {
			return BigInteger.ZERO;
		}
		if (k.equals(BigInteger.ZERO) || k.equals(n)) {
			return BigInteger.ONE;
		}

		try {
			int ni = NumberUtil.toInt(n);
			int ki = NumberUtil.toInt(k);
			if (ki > ni) {
				return BigInteger.ZERO;
			}
			return BigIntegerMath.binomial(ni, ki);
		} catch (ArithmeticException ae) {
			//
		}

		BigInteger bin = BigInteger.ONE;
		BigInteger i = BigInteger.ONE;

		while (!(i.compareTo(k) > 0)) {
			bin = bin.multiply(n.subtract(i).add(BigInteger.ONE)).divide(i);
			i = i.add(BigInteger.ONE);
		}
		return bin;
	}

	public static BigInteger binomial(final int n, final int k) {
		return BigIntegerMath.binomial(n, k);
	}

	public Binomial() {
	}

	@Override
	public IExpr e2IntArg(final IInteger n0, final IInteger k0) {
		final BigInteger n = n0.getBigNumerator();
		final BigInteger k = k0.getBigNumerator();
		return F.integer(binomial(n, k));
	}

	@Override
	public IExpr e2ObjArg(final IExpr n, final IExpr k) {
		if (k.isInteger()) {
			if (n.isInteger()) {
				// use e2IntArg() method
				return null;
			}
			IInteger ki = (IInteger) k;
			if (ki.isOne()) {
				return n;
			}
			if (ki.isZero()) {
				return F.C1;
			}
			if (ki.isLessThan(F.C10) && ki.isGreaterThan(F.C1)) {
				int kInt = ki.intValue();
				IAST ast = F.Times();
				IAST temp;
				IExpr nTemp = n;
				for (int i = 1; i <= kInt; i++) {
					temp = F.Divide(nTemp, F.integer(i));
					ast.add(temp);
					nTemp = F.eval(F.Subtract(nTemp, F.C1));
				}
				return ast;
			}
		}
		if (n.equals(k)) {
			return F.C1;
		}
		IExpr nMinus1 = F.eval(F.Subtract(n, F.C1));
		if (nMinus1.equals(k)) {
			return n;
		}
		IExpr boole = F.eval(F.Greater(F.Times(F.C2, k), n));
		if (boole.isTrue()) {
			// case k*2 > n : Binomial[n, k] -> Binomial[n, n-k]
			return F.Binomial(n, F.Subtract(n, k));
		}
		return null;
	}
}
