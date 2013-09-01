package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Round;
import static org.matheclipse.core.expression.F.Times;

import org.matheclipse.core.builtin.function.NumericQ;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

import com.google.common.base.Function;

/**
 * Round a given value to nearest integer.
 * 
 * See <a href="http://en.wikipedia.org/wiki/Floor_and_ceiling_functions">Wikipedia - Floor and ceiling functions</a>
 */
public class Round extends AbstractFunctionEvaluator implements INumeric {

	private final class RoundPlusFunction implements Function<IExpr, IExpr> {
		public IExpr apply(IExpr expr) {
			if (expr.isInteger()) {
				return expr;
			}
			return null;
		}
	}

	public Round() {
	}

	public double evalReal(final double[] stack, final int top, final int size) {
		if (size != 1) {
			throw new UnsupportedOperationException();
		}
		return Math.round(stack[top]);
	}

	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);

		try {
			IExpr arg1 = ast.get(1);
			if (arg1.isSignedNumber()) {
				return ((ISignedNumber) arg1).round();
			}
			if (NumericQ.CONST.apply(arg1)) {
				IExpr result = F.evaln(arg1);
				if (result.isSignedNumber()) {
					return ((ISignedNumber) result).round();
				}
			}
			if (arg1.isPlus()) {
				IAST[] result = ((IAST) arg1).split(new RoundPlusFunction());
				if (result[0].size() > 1) {
					if (result[1].size() > 1) {
						result[0].add(F.Round(result[1]));
					}
					return result[0];
				}
			}
			if (AbstractFunctionEvaluator.isNegativeExpression(arg1)) {
				return Times(CN1, Round(Times(CN1, arg1)));
			}
		} catch (ArithmeticException ae) {
			// ISignedNumber#round() may throw ArithmeticException
		}
		return null;
	}

	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.NHOLDALL | ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(symbol);
	}
}
