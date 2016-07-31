package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;

public class ChebyshevU extends AbstractFunctionEvaluator {

	public ChebyshevU() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);
		IExpr arg1 = ast.arg1();
		IExpr arg2 = ast.arg2();
		if (arg1.isInteger()) {
			int degree = -1;
			try {
				degree = ((ISignedNumber) arg1).toInt();
			} catch (Exception ex) {
				return F.NIL;
			}

			if (degree < 0) {
				return F.NIL;
			}
			if (degree == 0) {
				return F.C1;
			}
			if (degree == 1) {
				return F.Times(F.C2, arg2);
			}
			return F.Expand(F.Subtract(F.Times(F.C2, arg2, F.ChebyshevU(F.integer(degree - 1), arg2)),
					F.ChebyshevU(F.integer(degree - 2), arg2)));
		}
		if (arg2.isOne()) {
			return F.Plus(F.C1, arg1);
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE|ISymbol.NUMERICFUNCTION);
		super.setUp(symbol);
	}
}
