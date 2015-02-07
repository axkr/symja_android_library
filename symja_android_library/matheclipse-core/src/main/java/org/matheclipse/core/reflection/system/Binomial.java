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
	public IExpr e2ObjArg(final IExpr nObj, final IExpr kObj) {
		if (kObj.isInteger()) {
			if (nObj.isInteger()) {
				return null;
			}
			IInteger ii = (IInteger) kObj;
			if (ii.isOne()) {
				return nObj;
			}
			if (ii.isZero()) {
				return F.C1;
			}
			if (ii.isLessThan(F.C10) && ii.isGreaterThan(F.C1)) {
				int k = ii.intValue();
				IAST ast = F.Times();
				IAST temp;
				IExpr nTemp = nObj;
				for (int i = 1; i <= k; i++) {
					temp = F.Divide(nTemp, F.integer(i));
					ast.add(temp);
					nTemp = F.eval(F.Subtract(nTemp, F.C1));
				}
				return ast;
			}
		}
		if (nObj.equals(kObj)) {
			return F.C1;
		}
		IExpr nMinus1 = F.eval(F.Subtract(nObj, F.C1));
		if (nMinus1.equals(kObj)) {
			return nObj;
		}
		return null;
	}
}
