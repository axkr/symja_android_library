package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <p>
 * Bessel function of the first kind.
 * </p>
 * 
 * See <a href="http://en.wikipedia.org/wiki/Bessel_function">Wikipedia: Bessel function</a>
 *
 */
public class BesselJ extends AbstractFunctionEvaluator {

	public BesselJ() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);

		IExpr n = ast.arg1();
		int order = n.toIntDefault(Integer.MAX_VALUE);
		// if (order == Integer.MAX_VALUE) {
		// return F.NIL;
		// }

		IExpr z = ast.arg2();
		if (z.isZero()) {
			if (n.isZero()) {
				// (0, 0)
				return F.C1;
			}
			if (n.isIntegerResult() || order != Integer.MAX_VALUE) {
				return F.C0;
			}

			IExpr a = n.re();
			if (a.isPositive()) {
				// Re(arg1) > 0
				return F.C0;
			} else if (a.isNegative()) {
				// Re(arg1) < 0 && !a.isInteger()
				return F.CComplexInfinity;
			} else if (a.isZero() && !n.isZero()) {
				return F.Indeterminate;
			}

		}
		if (n.equals(F.CN1D2) || n.equals(F.num(-0.5))) {
			// (Sqrt(2/Pi)* Cos(z))/Sqrt(z)
			return F.Times(F.Sqrt(F.Divide(F.C2, F.Pi)), F.Cos(z), F.Power(z, F.CN1D2));
		}
		if (n.equals(F.C1D2) || n.equals(F.num(0.5))) {
			// (Sqrt(2/Pi)* Sin(z))/Sqrt(z)
			return F.Times(F.Sqrt(F.Divide(F.C2, F.Pi)), F.Sin(z), F.Power(z, F.CN1D2));
		}
		if (z.isInfinity() || z.isNegativeInfinity()) {
			return F.C0;
		}
		if (n.isInteger() || order != Integer.MAX_VALUE) {
			if (n.isNegative()) {
				// (-n,z) => (-1)^n*BesselJ(n,z)
				return F.Times(F.Power(F.CN1, n), F.BesselJ(n.negate(), z));
			}
		}
		if (n instanceof INum && z instanceof INum) {
			try {
				// numeric mode evaluation
				org.hipparchus.special.BesselJ besselJ = new org.hipparchus.special.BesselJ(((INum) n).doubleValue());
				return F.num(besselJ.value(((INum) z).doubleValue()));
			} catch (NegativeArraySizeException nae) {
				engine.printMessage(ast.toString() + " caused NegativeArraySizeException");
			} catch (RuntimeException rte) {
				engine.printMessage(rte.getMessage());
				return F.NIL;
			}
		}

		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
	}
}
