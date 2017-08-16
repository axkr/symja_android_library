package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

public class Hypergeometric2F1 extends AbstractFunctionEvaluator {

	public Hypergeometric2F1() {
	}

	@Override
	public IExpr evaluate(IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 5);

		IExpr a = ast.arg1();
		IExpr b = ast.arg2();
		IExpr c = ast.arg3();
		IExpr z = ast.arg4();
		if (z.isZero()) {
			return F.C1;
		}
		if (a.isInteger() && a.isNegative()&&z.isOne()) {
			IInteger n = (IInteger) a.negate();
			// Pochhammer(c-b, n) / Pochhammer(c, n)
			return F.Divide(F.Expand(F.Pochhammer(F.Subtract(c, b),n)), F.Pochhammer(c, n));
		}
		if (a.isSignedNumber() && b.isSignedNumber() && c.isSignedNumber() && z.isSignedNumber()) {
			double aDouble = ((ISignedNumber) a).doubleValue();
			double bDouble = ((ISignedNumber) b).doubleValue();
			double cDuble = ((ISignedNumber) c).doubleValue();
			double zDouble = ((ISignedNumber) z).doubleValue();
			try {
				return F.num(de.lab4inf.math.functions.HypergeometricGaussSeries.gaussSeries(aDouble, bDouble, cDuble,
						zDouble));
			} catch (RuntimeException rex) {
				engine.printMessage("Hypergeometric2F1: " + rex.getMessage());
			}
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(newSymbol);
	}
}
