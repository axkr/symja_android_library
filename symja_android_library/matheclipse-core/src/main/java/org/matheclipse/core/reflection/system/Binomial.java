package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.AbstractArg2;
import org.matheclipse.core.expression.AbstractIntegerSym;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

import com.google.common.math.BigIntegerMath;

/**
 * Returns the binomial coefficient of 2 integers.
 * 
 * See <a href="http://en.wikipedia.org/wiki/Binomial_coefficient">Binomial
 * coefficient</a>
 */
public class Binomial extends AbstractArg2 {

	public static IInteger binomial(final IInteger n, final IInteger k) {
		// k>n : by definition --> 0
		if (k.compareTo(n) > 0) {
			return F.C0;
		}
		if (k.isZero() || k.equals(n)) {
			return F.C1;
		}

		try {
			int ni = n.toInt();
			int ki = k.toInt();
			if (ki > ni) {
				return F.C0;
			}
			return AbstractIntegerSym.valueOf(BigIntegerMath.binomial(ni, ki));
		} catch (ArithmeticException ae) {
			//
		}

		IInteger bin = F.C1;
		IInteger i = F.C1;

		while (!(i.compareTo(k) > 0)) {
			bin = bin.multiply(n.subtract(i).add(F.C1)).div(i);
			i = i.add(F.C1);
		}
		return bin;
	}

	public Binomial() {
	}

	@Override
	public IExpr e2IntArg(final IInteger n, final IInteger k) {
		return binomial(n, k);
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
			if (ki.compareInt(10) < 0 && ki.compareInt(1) > 0) {
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
