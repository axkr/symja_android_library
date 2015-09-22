package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.IntegerPart;
import static org.matheclipse.core.expression.F.Negate;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

public class IntegerPart extends AbstractFunctionEvaluator {

	public IntegerPart() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);

		try {
			IExpr arg1 = ast.arg1();
			ISignedNumber signedNumber = arg1.evalSignedNumber();
			if (signedNumber != null) {
				return signedNumberIntegerPart(signedNumber);
			}
			if (arg1.isIntegerResult()) {
				return arg1;
			}
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr != null) {
				return Negate(IntegerPart(negExpr));
			}
		} catch (ArithmeticException ae) {
			// ISignedNumber#floor() or #ceil() may throw ArithmeticException
		}
		return null;
	}

	private IExpr signedNumberIntegerPart(ISignedNumber arg1) {
		final ISignedNumber signedNumber = arg1;
		if ((signedNumber).isNegative()) {
			return (signedNumber).ceil();
		} else {
			return (signedNumber).floor();
		}
	} 

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.NHOLDALL | ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(symbol);
	}

}
