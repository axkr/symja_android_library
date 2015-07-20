package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Haversine extends AbstractFunctionEvaluator {

	public Haversine() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);
		IExpr arg1 = ast.arg1();
		return F.Power(F.Sin(F.C1D2.times(arg1)), F.C2);
	}

	@Override
	public void setUp(ISymbol symbol) {
		symbol.setAttributes(ISymbol.LISTABLE);
	}

}
