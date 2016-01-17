package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.Ceiling;
import static org.matheclipse.core.expression.F.Negate;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;

import java.util.function.Function;

/**
 * Returns the largest (closest to positive infinity) <code>ISignedNumber</code> value that is not greater than <code>this</code>
 * and is equal to a mathematical integer.
 * 
 * See <a href="http://en.wikipedia.org/wiki/Floor_and_ceiling_functions">Wikipedia - Floor and ceiling functions</a>
 */
public class Floor extends AbstractFunctionEvaluator implements INumeric {

	private final static class FloorPlusFunction implements Function<IExpr, IExpr> {
		@Override
		public IExpr apply(IExpr expr) {
			if (expr.isInteger()) {
				return expr;
			}
			return null;
		}
	}

	public Floor() {
	}

	@Override
	public double evalReal(final double[] stack, final int top, final int size) {
		if (size != 1) {
			throw new UnsupportedOperationException();
		}
		return Math.floor(stack[top]);
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);

		try {
			IExpr arg1 = engine.evaluateNull(ast.arg1());
			if (arg1.isPresent()) {
				IExpr temp = evalFloor(arg1);
				if (temp == null) {
					return F.Floor(arg1);
				}
				return temp;
			}
			return evalFloor(ast.arg1());
		} catch (ArithmeticException ae) {
			// ISignedNumber#floor() may throw ArithmeticException
		}
		return null;
	}

	public IExpr evalFloor(IExpr arg1) {
		INumber number = arg1.evalNumber();
		if (number != null) {
			return number.floorFraction();
		}
		if (arg1.isIntegerResult()) {
			return arg1;
		}
		if (arg1.isPlus()) {
			IAST[] splittedPlus = ((IAST) arg1).filter(new FloorPlusFunction());
			if (splittedPlus[0].size() > 1) {
				if (splittedPlus[1].size() > 1) {
					splittedPlus[0].add(F.Floor(splittedPlus[1].getOneIdentity(F.C0)));
				}
				return splittedPlus[0];
			}
		}
		IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
		if (negExpr != null) {
			return Negate(Ceiling(negExpr));
		}
		return null;
	} 

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL | ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(symbol);
	}
}
