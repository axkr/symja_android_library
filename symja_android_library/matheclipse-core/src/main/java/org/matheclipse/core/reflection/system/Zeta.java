package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.*;

import org.matheclipse.core.eval.interfaces.AbstractArg12;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

public class Zeta extends AbstractArg12 {

	public Zeta() {
	}

	@Override
	public IExpr e1ObjArg(final IExpr arg1) {
		if (arg1.isZero()) {
			return CN1D2;
		}
		if (arg1.isOne()) {
			return CComplexInfinity;
		}
		if (arg1.isMinusOne()) {
			// -1/12
			return QQ(-1, 12);
		}
		if (arg1.isInteger()) {
			IInteger n = (IInteger) arg1;

			if (!n.isPositive()) {
				if (n.isEven()) {
					return F.C0;
				}
				// Zeta(-n) :=
				// ((-1)^n/(n + 1))*BernoulliB(n + 1)
				n = n.negate();
				IExpr n1 = n.add(C1);
				return Times(Power(CN1, n), Power(n1, -1), BernoulliB(n1));
			}
			if (n.isEven()) {
				// Zeta(2*n) :=
				// ((((-1)^(n-1)*2^(-1+2*n)*Pi^(2*n))/(2*n)!)*BernoulliB(2*n)
				n = n.shiftRight(1);
				return Times(Power(CN1, Plus(CN1, n)), Power(C2, Plus(CN1, Times(C2, n))), Power(Pi, Times(C2, n)),
						Power(Factorial(Times(C2, n)), -1), BernoulliB(Times(C2, n)));
			}

		}
		if (arg1.isInfinity()) {
			return C1;
		}
		return NIL;
	}

	@Override
	public IExpr e2ObjArg(IExpr s, IExpr a) {
		if (a.isZero()) {
			return Zeta(s);
		}
		if (a.isMinusOne()) {
			return Plus(C1, Zeta(s));
		}
		if (s.isInteger() && a.isInteger()) {
			if (!s.isPositive() || ((IInteger) s).isEven()) {
				IInteger n = (IInteger) a;
				if (n.isNegative()) {
					n = n.negate();
					// Zeta(s, -n) :=
					// Zeta(s) + Sum(1/k^s, {k, 1, n})
					return Plus(Sum(Power(Power(k, s), -1), List(k, C1, n)), Zeta(s));
				}
			}
		}
		if (a.equals(C2)) {
			return Plus(CN1, Zeta(s));
		}
		if (a.equals(C1D2)) {
			return Times(Plus(CN1, Sqr(s)), Zeta(s));
		}
		return NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(newSymbol);
	}
}
