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

		IExpr arg1 = ast.arg1();
		 int order = arg1.toIntDefault(Integer.MAX_VALUE);
		// if (order == Integer.MAX_VALUE) {
		// return F.NIL;
		// }

		IExpr arg2 = ast.arg2();
		if (arg2.isZero()) {
			if (arg1.isZero()) {
				// (0, 0)
				return F.C1;
			}
			if (arg1.isIntegerResult() || order != Integer.MAX_VALUE) {
				return F.C0;
			}

			IExpr a = arg1.re();
			if (a.isPositive()) {
				// Re(arg1) > 0
				return F.C0;
			} else if (a.isNegative()) {
				// Re(arg1) < 0 && !a.isInteger()
				return F.CComplexInfinity;
			} else if (a.isZero() && !arg1.isZero()) {
				return F.Indeterminate;
			}

		}
		if (arg1 instanceof INum && arg2 instanceof INum) {
			try {
				// numeric mode evaluation
				org.hipparchus.special.BesselJ besselJ = new org.hipparchus.special.BesselJ(
						((INum) arg1).doubleValue());
				return F.num(besselJ.value(((INum) arg2).doubleValue()));
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
