package org.matheclipse.core.builtin;

import static org.matheclipse.core.expression.F.Ceiling;
import static org.matheclipse.core.expression.F.Floor;
import static org.matheclipse.core.expression.F.Negate;

import java.math.BigInteger;
import java.util.function.Function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;

public class IntegerFunctions {
	static {
		F.BitLength.setEvaluator(new BitLength());
		F.Ceiling.setEvaluator(new Ceiling());
		F.Floor.setEvaluator(new Floor());
		F.IntegerLength.setEvaluator(new IntegerLength());
	}

	private static class BitLength extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			if (ast.arg1().isInteger()) {
				IInteger iArg1 = (IInteger) ast.arg1();
				BigInteger big = iArg1.toBigNumerator();
				return F.integer(big.bitLength());
			}
			return F.NIL;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}
	}

	/**
	 * Returns the smallest (closest to negative infinity) <code>ISignedNumber</code> value that is not less than
	 * <code>this</code> and is equal to a mathematical integer.
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Floor_and_ceiling_functions">Wikipedia - Floor and ceiling
	 * functions</a>
	 * 
	 */
	private final static class Ceiling extends AbstractFunctionEvaluator implements INumeric {

		private final static class CeilingPlusFunction implements Function<IExpr, IExpr> {
			@Override
			public IExpr apply(IExpr expr) {
				if (expr.isInteger()) {
					return expr;
				}
				return F.NIL;
			}
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1) {
				throw new UnsupportedOperationException();
			}
			return Math.ceil(stack[top]);
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			try {
				if (ast.isAST2()) {
					return F.Times(F.Ceiling(F.Divide(ast.arg1(), ast.arg2())), ast.arg2());
				}
				IExpr arg1 = engine.evaluateNull(ast.arg1());
				if (arg1.isPresent()) {
					return evalCeiling(arg1).orElse(F.Ceiling(arg1));
				}
				return evalCeiling(ast.arg1());
			} catch (ArithmeticException ae) {
				// ISignedNumber#floor() or #ceil() may throw ArithmeticException
			}
			return F.NIL;
		}

		public IExpr evalCeiling(IExpr arg1) {
			INumber number = arg1.evalNumber();
			if (number != null) {
				return number.ceilFraction();
			}

			if (arg1.isIntegerResult()) {
				return arg1;
			}

			if (arg1.isPlus()) {
				IAST[] splittedPlus = ((IAST) arg1).filter(new CeilingPlusFunction());
				if (splittedPlus[0].size() > 1) {
					if (splittedPlus[1].size() > 1) {
						splittedPlus[0].append(F.Ceiling(splittedPlus[1].getOneIdentity(F.C0)));
					}
					return splittedPlus[0];
				}
			}
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Negate(Floor(negExpr));
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}
	}

	/**
	 * Returns the largest (closest to positive infinity) <code>ISignedNumber</code> value that is not greater than
	 * <code>this</code> and is equal to a mathematical integer.
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Floor_and_ceiling_functions">Wikipedia - Floor and ceiling
	 * functions</a>
	 */
	private final static class Floor extends AbstractFunctionEvaluator implements INumeric {

		private final static class FloorPlusFunction implements Function<IExpr, IExpr> {
			@Override
			public IExpr apply(IExpr expr) {
				if (expr.isInteger()) {
					return expr;
				}
				return F.NIL;
			}
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
			Validate.checkRange(ast, 2, 3);

			try {
				if (ast.isAST2()) {
					return F.Times(F.Floor(F.Divide(ast.arg1(), ast.arg2())), ast.arg2());
				}
				IExpr arg1 = engine.evaluateNull(ast.arg1());
				if (arg1.isPresent()) {
					return evalFloor(arg1).orElse(F.Floor(arg1));
				}
				return evalFloor(ast.arg1());
			} catch (ArithmeticException ae) {
				// ISignedNumber#floor() may throw ArithmeticException
			}
			return F.NIL;
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
						splittedPlus[0].append(F.Floor(splittedPlus[1].getOneIdentity(F.C0)));
					}
					return splittedPlus[0];
				}
			}
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Negate(Ceiling(negExpr));
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}
	}

	private static class IntegerLength extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			if (ast.arg1().isInteger()) {
				IInteger radix = F.C10;
				if (ast.isAST2()) {
					if (ast.arg2().isInteger()) {
						radix = ((IInteger) ast.arg2());
					} else {
						return F.NIL;
					}
				}
				if (radix.isLessThan(F.C1)) {
					engine.printMessage("IntegerLength: The base must be greater than 1");
					return F.NIL;
				}
				IInteger iArg1 = (IInteger) ast.arg1();
				if (iArg1.isZero()) {
					return F.C1;
				}
				long l = 0L;
				iArg1 = iArg1.abs();
				while (iArg1.isGreaterThan(F.C0)) {
					iArg1 = iArg1.div(radix);
					l++;
				}

				return F.integer(l);
			}
			return F.NIL;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}
	}

	final static IntegerFunctions CONST = new IntegerFunctions();

	public static IntegerFunctions initialize() {
		return CONST;
	}

	private IntegerFunctions() {

	}

}
