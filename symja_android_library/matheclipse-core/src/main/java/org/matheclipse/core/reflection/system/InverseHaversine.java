package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class InverseHaversine extends AbstractFunctionEvaluator {

	public InverseHaversine() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);
		IExpr arg1 = ast.arg1();
		// 2 * ArcSin(Sqrt(z))
		return F.Times(F.C2, F.ArcSin(F.Sqrt(arg1)));
	}

	@Override
	public void setUp(ISymbol symbol) {
		symbol.setAttributes(ISymbol.LISTABLE);
	}

}
