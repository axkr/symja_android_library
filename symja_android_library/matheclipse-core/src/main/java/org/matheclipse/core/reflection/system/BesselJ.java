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
 * See <a href="http://en.wikipedia.org/wiki/Bessel_function">Wikipedia: Bessel
 * function</a>
 *
 */
public class BesselJ extends AbstractFunctionEvaluator {

	public BesselJ() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);

		IExpr arg1 = ast.arg1();
		IExpr arg2 = ast.arg2();
		if (arg1 instanceof INum && arg2 instanceof INum) {
			try {
				// numeric mode evaluation
				org.apache.commons.math4.special.BesselJ besselJ = new org.apache.commons.math4.special.BesselJ(
						((INum) arg2).doubleValue());
				return F.num(besselJ.value(((INum) arg1).doubleValue()));
			} catch (NegativeArraySizeException nae) {
				engine.printMessage(ast.toString() + " caused NegativeArraySizeException");
			} catch (RuntimeException rte) {
				engine.printMessage(rte.getMessage());
				return F.NIL;
			}
		}
		if (arg2.isZero()) {
			if (arg1.isZero()) {
				// (0, 0)
				return F.C1;
			}
			if (arg1.isInteger()) {
				// (<integer>, 0)
				return F.C0;
			}

			IExpr a = F.eval(F.Re(arg1));
			if (a.isPositive()) {
				// Re(arg1) > 0
				return F.C0;
			}

		}

		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
	}
}
