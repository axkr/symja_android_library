package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

public class Hypergeometric1F1 extends AbstractFunctionEvaluator {

	public Hypergeometric1F1() {
	}

	@Override
	public IExpr evaluate(IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 4);

		IExpr a = ast.arg1();
		if (a.isZero()) {
			return F.C1;
		}
		IExpr b = ast.arg2();
		if (b.isZero()) {
			return F.CComplexInfinity;
		}
		IExpr z = ast.arg3();
		if (z.isZero()) {
			return F.C1;
		}

		if (b.isOne()) {
			return F.LaguerreL(a.negate(), z);
		}

		if (a.isSignedNumber() && b.isSignedNumber()) {
			ISignedNumber n = (ISignedNumber) a;
			ISignedNumber m = (ISignedNumber) b;
			if (n.isInteger() && m.isInteger() && n.isNegative() && m.isNegative() && m.isGreaterThan(n)) {
				return F.CComplexInfinity;
			}
			if (z.isSignedNumber()) {
				double aDouble = n.doubleValue();
				double bDoube = m.doubleValue();
				double zDouble = ((ISignedNumber) z).doubleValue();
				try {
					return F.num(de.lab4inf.math.functions.KummerFunction.kummer(aDouble, bDoube, zDouble));
				} catch (RuntimeException rex) {
					engine.printMessage("Hypergeometric1F1: " + rex.getMessage());
				}
			}
		}
		if (a.equals(b)) {
			return F.Power(F.E, z);
		}
		if (a.isMinusOne()) {
			// 1 - z/b
			return F.Subtract(F.C1, F.Divide(z, b));
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
	}
}
