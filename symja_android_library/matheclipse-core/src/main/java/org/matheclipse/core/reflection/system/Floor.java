package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.WrongNumberOfArguments;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.generic.interfaces.INumericFunction;

import com.google.common.base.Function;

/**
 * Returns the largest (closest to positive infinity) <code>ISignedNumber</code>
 * value that is not greater than <code>this</code> and is equal to a
 * mathematical integer.
 * 
 * See <a
 * href="http://en.wikipedia.org/wiki/Floor_and_ceiling_functions">Wikipedia -
 * Floor and ceiling functions</a>
 */
public class Floor extends AbstractFunctionEvaluator implements INumeric {

	private final class FloorPlusFunction implements Function<IExpr, IExpr> {
		public IExpr apply(IExpr expr) {
			if (expr.isInteger()) {
				return expr;
			}
			return null;
		}
	}

//	private final class FloorNumericFunction implements INumericFunction<IExpr> {
//		public IExpr apply(double value) {
//			if (value < Integer.MAX_VALUE && value > Integer.MIN_VALUE) {
//				long result = Math.round(Math.floor(value));
//				return F.integer(result);
//			}
//			return null;
//		}
//	}

	public Floor() {
	}

	public double evalReal(final double[] stack, final int top, final int size) {
		if (size != 1) {
			throw new UnsupportedOperationException();
		}
		return Math.floor(stack[top]);
	}

	public IExpr evaluate(final IAST ast) {
		if (ast.size() != 2) {
			throw new WrongNumberOfArguments(ast, 1, ast.size() - 1);
		}
		IExpr arg1 = ast.get(1);
		if (arg1.isSignedNumber()) {
			return ((ISignedNumber) arg1).floor();
		}
		if (NumericQ.CONST.apply(arg1)) {
			IExpr result = F.evaln(arg1);
			if (result.isSignedNumber()) {
				result = ((ISignedNumber) result).floor();
				if (result.isNumIntValue()) {
					return F.integer(((INum) result).intValue());
				}
			}
		}

		if (arg1.isPlus()) {
			IAST[] result = ((IAST) arg1).split(new FloorPlusFunction());
			if (result[0].size() > 1) {
				if (result[1].size() > 1) {
					result[0].add(F.Floor(result[1]));
				}
				return result[0];
			}
		}
		return null;
	}

	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(symbol);
	}
}
