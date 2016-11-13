package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

public class Hypergeometric2F1 extends AbstractFunctionEvaluator {

	public Hypergeometric2F1() {
	}

	@Override
	public IExpr evaluate(IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 5);

		if (ast.arg1().isSignedNumber() && ast.arg2().isSignedNumber() && ast.arg3().isSignedNumber()
				&& ast.arg4().isSignedNumber()) {
			double a = ((ISignedNumber) ast.arg1()).doubleValue();
			double b = ((ISignedNumber) ast.arg2()).doubleValue();
			double c = ((ISignedNumber) ast.arg3()).doubleValue();
			double z = ((ISignedNumber) ast.arg4()).doubleValue();
			try {
				return F.num(de.lab4inf.math.functions.HypergeometricGaussSeries.gaussSeries(a, b, c, z));
			} catch (RuntimeException rex) {
				engine.printMessage("Hypergeometric2F1: "+rex.getMessage());
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
