package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Floor;
import static org.matheclipse.core.expression.F.Times;

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
 * Returns the smallest (closest to negative infinity) <code>ISignedNumber</code> value that is not less than <code>this</code> and
 * is equal to a mathematical integer.
 * 
 * See <a href="http://en.wikipedia.org/wiki/Floor_and_ceiling_functions">Wikipedia - Floor and ceiling functions</a>
 * 
 */
public class Ceiling extends AbstractFunctionEvaluator implements INumeric {

	private final class CeilingPlusFunction implements Function<IExpr, IExpr> {
		public IExpr apply(IExpr expr) {
			if (expr.isInteger()) {
				return expr;
			}
			return null;
		}
	}

	// private final class CeilingNumericFunction implements
	// INumericFunction<IExpr> {
	// public IExpr apply(double value) {
	// if (value < Integer.MAX_VALUE && value > Integer.MIN_VALUE) {
	// long result = Math.round(Math.ceil(value));
	// return F.integer(result);
	// }
	// return null;
	// }
	// }

	public Ceiling() {
	}

	public double evalReal(final double[] stack, final int top, final int size) {
		if (size != 1) {
			throw new UnsupportedOperationException();
		}
		return Math.ceil(stack[top]);
	}

	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);

		try {
			IExpr arg1 = ast.get(1);
			if (arg1.isSignedNumber()) {
				return ((ISignedNumber) arg1).ceil();
			}
			if (NumericQ.CONST.apply(arg1)) {
				IExpr result = F.evaln(arg1);
				if (result.isSignedNumber()) {
					return ((ISignedNumber) result).ceil();
				}
			}
			if (arg1.isPlus()) {
				IAST[] result = ((IAST) arg1).split(new CeilingPlusFunction());
				if (result[0].size() > 1) {
					if (result[1].size() > 1) {
						result[0].add(F.Ceiling(result[1]));
					}
					return result[0];
				}
			}
			if (AbstractFunctionEvaluator.isNegativeExpression(arg1)) {
				return Times(CN1, Floor(Times(CN1, arg1)));
			}
		} catch (ArithmeticException ae) {
			// ISignedNumber#floor() or #ceil() may throw ArithmeticException
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
