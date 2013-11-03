package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.IntegerPart;
import static org.matheclipse.core.expression.F.Times;

import org.matheclipse.core.builtin.function.NumericQ;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

public class IntegerPart extends AbstractFunctionEvaluator {

	public IntegerPart() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);

		try {
			IExpr arg1 = ast.arg1();
			if (arg1.isSignedNumber()) {
				final ISignedNumber signedNumber = (ISignedNumber) arg1;
				if ((signedNumber).isNegative()) {
					return (signedNumber).ceil();
				} else {
					return (signedNumber).floor();
				}
			}
			if (NumericQ.CONST.apply(arg1)) {
				IExpr result = F.evaln(arg1);
				if (result.isSignedNumber()) {
					final ISignedNumber signedNumber = (ISignedNumber) result;
					if ((signedNumber).isNegative()) {
						return (signedNumber).ceil();
					} else {
						return (signedNumber).floor();
					}
				}
			}
			if (AbstractFunctionEvaluator.isNegativeExpression(arg1)) {
				return Times(CN1, IntegerPart(Times(CN1, arg1)));
			}
		} catch (ArithmeticException ae) {
			// ISignedNumber#floor() or #ceil() may throw ArithmeticException
		}
		return null;
	}

	@Override
	public IExpr numericEval(final IAST ast) {
		return evaluate(ast);
	} 

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.NHOLDALL | ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(symbol);
	}

}
