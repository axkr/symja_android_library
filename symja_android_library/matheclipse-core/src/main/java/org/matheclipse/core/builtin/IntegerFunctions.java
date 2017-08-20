package org.matheclipse.core.builtin;

import static org.matheclipse.core.expression.F.Ceiling;
import static org.matheclipse.core.expression.F.Floor;
import static org.matheclipse.core.expression.F.FractionalPart;
import static org.matheclipse.core.expression.F.IntegerPart;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Round;

import java.math.BigInteger;
import java.util.function.Function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractArg2;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.eval.util.Lambda;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

public class IntegerFunctions {
	static {
		F.BitLength.setEvaluator(new BitLength());
		F.Ceiling.setEvaluator(new Ceiling());
		F.Floor.setEvaluator(new Floor());
		F.FractionalPart.setEvaluator(new FractionalPart());
		F.IntegerExponent.setEvaluator(new IntegerExponent());
		F.IntegerLength.setEvaluator(new IntegerLength());
		F.IntegerPart.setEvaluator(new IntegerPart());
		F.Mod.setEvaluator(new Mod());
		F.PowerMod.setEvaluator(new PowerMod());
		F.Quotient.setEvaluator(new Quotient());
		F.Round.setEvaluator(new Round());
	}

	/**
	 * <pre>
	 * BitLengthi(x)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * gives the number of bits needed to represent the integer <code>x</code>. The sign of <code>x</code> is ignored.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; BitLength(1023)    
	 * 10  
	 * 
	 * &gt;&gt; BitLength(100)    
	 * 7    
	 * 
	 * &gt;&gt; BitLength(-5)    
	 * 3    
	 * 
	 * &gt;&gt; BitLength(0)    
	 * 0
	 * </pre>
	 */
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
	 * <pre>
	 * Ceiling(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * gives the first integer greater than or equal <code>expr</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * Ceiling(expr, a)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * gives the first multiple of <code>a</code> greater than or equal to <code>expr</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Ceiling(1/3)
	 * 1
	 * 
	 * &gt;&gt; Ceiling(-1/3)
	 * 0
	 * 
	 * &gt;&gt; Ceiling(1.2)    
	 * 2    
	 * 
	 * &gt;&gt; Ceiling(3/2)    
	 * 2
	 * </pre>
	 * <p>
	 * For complex <code>expr</code>, take the ceiling of real and imaginary parts.<br />
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Ceiling(1.3 + 0.7*I)    
	 * 2+I    
	 * 
	 * &gt;&gt; Ceiling(10.4, -1)    
	 * 10    
	 * 
	 * &gt;&gt; Ceiling(-10.4, -1)    
	 * -11
	 * </pre>
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

		/**
		 * Returns the smallest (closest to negative infinity) <code>ISignedNumber</code> value that is not less than
		 * <code>this</code> and is equal to a mathematical integer.
		 * 
		 * See <a href="http://en.wikipedia.org/wiki/Floor_and_ceiling_functions">Wikipedia - Floor and ceiling
		 * functions</a>
		 * 
		 */
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
			if (arg1.isNumber()) {
				return ((INumber) arg1).ceilFraction();
			}
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
	 * <pre>
	 * IntegerExponent(n, b)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * gives the highest exponent of <code>b</code> that divides <code>n</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; IntegerExponent(16, 2)
	 * 4
	 * &gt;&gt; IntegerExponent(-510000)
	 * 4
	 * &gt;&gt; IntegerExponent(10, b)
	 * IntegerExponent(10, b)
	 * </pre>
	 */
	private static class IntegerExponent extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);
			IInteger base = F.C10;
			if (ast.isAST2()) {
				IExpr arg2 = ast.arg2();
				if (arg2.isInteger() && ((IInteger) arg2).compareInt(1) > 0) {
					base = (IInteger) arg2;
				} else {
					return F.NIL;
				}
			}
			IExpr arg1 = ast.arg1();
			if (arg1.isInteger()) {
				return ((IInteger) arg1).exponent(base);
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}
	}

	/**
	 * <pre>
	 * Floor(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * gives the smallest integer less than or equal <code>expr</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * Floor(expr, a)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * gives the smallest multiple of <code>a</code> less than or equal to <code>expr</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Floor(1/3)
	 * 0
	 * 
	 * &gt;&gt; Floor(-1/3)
	 * -1
	 * 
	 * &gt;&gt; Floor(10.4)    
	 * 10    
	 * 
	 * &gt;&gt; Floor(10/3)    
	 * 3    
	 * 
	 * &gt;&gt; Floor(10)    
	 * 10    
	 * 
	 * &gt;&gt; Floor(21, 2)    
	 * 20    
	 * 
	 * &gt;&gt; Floor(2.6, 0.5)    
	 * 2.5    
	 * 
	 * &gt;&gt; Floor(-10.4)    
	 * -11
	 * </pre>
	 * <p>
	 * For complex <code>expr</code>, take the floor of real an imaginary parts.<br />
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Floor(1.5 + 2.7*I)    
	 * 1+I*2
	 * </pre>
	 * <p>
	 * For negative <code>a</code>, the smallest multiple of <code>a</code> greater than or equal to <code>expr</code>
	 * is returned.<br />
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Floor(10.4, -1)    
	 * 11    
	 * 
	 * &gt;&gt; Floor(-10.4, -1)    
	 * -10
	 * </pre>
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

		/**
		 * Returns the largest (closest to positive infinity) <code>ISignedNumber</code> value that is not greater than
		 * <code>this</code> and is equal to a mathematical integer.
		 * 
		 * See <a href="http://en.wikipedia.org/wiki/Floor_and_ceiling_functions">Wikipedia - Floor and ceiling
		 * functions</a>
		 */
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
			if (arg1.isNumber()) {
				return ((INumber) arg1).floorFraction();
			}
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

	/**
	 * <pre>
	 * FractionalPart(number)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * get the fractional part of a <code>number</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; FractionalPart(1.5)
	 * 0.5
	 * </pre>
	 */
	private static class FractionalPart extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			IExpr arg1 = ast.arg1();
			if (arg1.isInteger()) {
				return F.C0;
			} else if (arg1.isFraction()) {
				IFraction fr = (IFraction) arg1;
				return fr.fractionalPart();
			}
			ISignedNumber signedNumber = arg1.evalSignedNumber();
			if (signedNumber != null) {
				return signedNumberFractionalPart(signedNumber);
			}
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Negate(FractionalPart(negExpr));
			}
			return F.NIL;
		}

		private IExpr signedNumberFractionalPart(INumber arg1) {
			if (arg1.isInteger()) {
				return F.C0;
			} else if (arg1.isFraction()) {
				IFraction fr = (IFraction) arg1;
				return fr.fractionalPart();
			} else if (arg1 instanceof INum) {
				INum num = (INum) arg1;
				return F.num(num.getRealPart() % 1);
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.NHOLDALL | ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		}
	}

	/**
	 * <pre>
	 * IntegerLength(x)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * gives the number of digits in the base-10 representation of <code>x</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * IntegerLength(x, b)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * gives the number of base-<code>b</code> digits in <code>x</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; IntegerLength(123456)
	 * 6
	 * 
	 * &gt;&gt; IntegerLength(10^10000)
	 * 10001
	 * 
	 * &gt;&gt; IntegerLength(-10^1000)
	 * 1001
	 * </pre>
	 * <p>
	 * <code>IntegerLength</code> with base <code>2</code>:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; IntegerLength(8, 2)
	 * 4
	 * </pre>
	 * <p>
	 * Check that <code>IntegerLength</code> is correct for the first 100 powers of 10:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; IntegerLength /@ (10 ^ Range(100)) == Range(2, 101)
	 * True
	 * </pre>
	 * <p>
	 * The base must be greater than <code>1</code>:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; IntegerLength(3, -2)
	 * IntegerLength(3, -2)
	 * </pre>
	 * <p>
	 * '0' is a special case:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; IntegerLength(0)
	 * 0
	 * 
	 * &gt;&gt; IntegerLength /@ (10 ^ Range(100) - 1) == Range(1, 100)
	 * True
	 * </pre>
	 */
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
				long l = iArg1.integerLength(radix);

				return F.integer(l);
			}
			return F.NIL;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}
	}

	/**
	 * <pre>
	 * IntegerPart(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * for real <code>expr</code> return the integer part of <code>expr</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; IntegerPart(2.4)
	 * 2
	 * 
	 * &gt;&gt; IntegerPart(-9/4)
	 * -2
	 * </pre>
	 */
	private static class IntegerPart extends AbstractFunctionEvaluator {

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
				if (negExpr.isPresent()) {
					return Negate(IntegerPart(negExpr));
				}
			} catch (ArithmeticException ae) {
				// ISignedNumber#floor() or #ceil() may throw ArithmeticException
			}
			return F.NIL;
		}

		private IExpr signedNumberIntegerPart(ISignedNumber arg1) {
			final ISignedNumber signedNumber = arg1;
			if ((signedNumber).isNegative()) {
				return (signedNumber).ceilFraction();
			} else {
				return (signedNumber).floorFraction();
			}
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.NHOLDALL | ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		}

	}

	/**
	 * <pre>
	 * Mod(x, m)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>x</code> modulo <code>m</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Mod(14, 6)
	 * 2
	 * &gt;&gt; Mod(-3, 4)
	 * 1
	 * &gt;&gt; Mod(-3, -4)
	 * -3
	 * </pre>
	 * <p>
	 * The argument 0 should be nonzero
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Mod(5, 0) 
	 * Mod(5, 0)
	 * </pre>
	 */
	private static class Mod extends AbstractArg2 {

		/**
		 * 
		 * See: <a href="http://en.wikipedia.org/wiki/Modular_arithmetic">Wikipedia - Modular arithmetic</a>
		 */
		@Override
		public IExpr e2IntArg(final IInteger i0, final IInteger i1) {
			try {
				if (i1.isNegative()) {
					return i0.negate().mod(i1.negate()).negate();
				}
				return i0.mod(i1);
			} catch (ArithmeticException ae) {
				EvalEngine.get().printMessage("Mod: " + ae.getMessage());
				return F.NIL;
			}
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}

	}

	/**
	 * <pre>
	 * PowerMod(x, y, m)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * computes <code>x^y</code> modulo <code>m</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; PowerMod(2, 10000000, 3)
	 * 1
	 * &gt;&gt; PowerMod(3, -2, 10)
	 * 9
	 * </pre>
	 * <p>
	 * 0 is not invertible modulo 2.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; PowerMod(0, -1, 2)
	 * PowerMod(0, -1, 2)
	 * </pre>
	 * <p>
	 * The argument 0 should be nonzero.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; PowerMod(5, 2, 0)
	 *  PowerMod(5, 2, 0)
	 * </pre>
	 */
	private static class PowerMod extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 4);

			if (Lambda.compareStop(ast, x -> !x.isInteger())) {
				return F.NIL;
			}
			IInteger arg1 = (IInteger) ast.get(1);
			IInteger arg2 = (IInteger) ast.get(2);
			IInteger arg3 = (IInteger) ast.get(3);
			try {
				if (arg2.isMinusOne()) {
					return arg1.modInverse(arg3);
				}
				return arg1.modPow(arg2, arg3);
			} catch (ArithmeticException ae) {
				engine.printMessage("PowerMod: " + ae.getMessage());
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
			super.setUp(newSymbol);
		}
	}

	/**
	 * <pre>
	 * Quotient(m, n)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * computes the integer quotient of <code>m</code> and <code>n</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Quotient(23, 7)
	 * 3
	 * </pre>
	 * <p>
	 * Infinite expression Quotient(13, 0) encountered.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Quotient(13, 0)
	 * ComplexInfinity
	 * 
	 * &gt;&gt; Quotient(-17, 7)
	 * -3
	 * 
	 * &gt;&gt; Quotient(-17, -4)
	 * 4
	 * 
	 * &gt;&gt; Quotient(19, -4)
	 * -5
	 * </pre>
	 */
	private static class Quotient extends AbstractArg2 {

		@Override
		public IExpr e2IntArg(final IInteger i0, final IInteger i1) {
			if (i1.isZero()) {
				EvalEngine.get().printMessage("Quotient: division by zero");
				return F.NIL;
			}
			return i0.quotient(i1);
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		}
	}

	final static IntegerFunctions CONST = new IntegerFunctions();

	public static IntegerFunctions initialize() {
		return CONST;
	}

	private IntegerFunctions() {

	}

	/**
	 * <pre>
	 * Round(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * round a given <code>expr</code> to nearest integer.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Round(-3.6)
	 * -4
	 * </pre>
	 */
	private static class Round extends AbstractFunctionEvaluator implements INumeric {

		private final static class RoundPlusFunction implements Function<IExpr, IExpr> {
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
			return Math.round(stack[top]);
		}

		/**
		 * Round a given value to nearest integer.
		 * 
		 * See <a href="http://en.wikipedia.org/wiki/Floor_and_ceiling_functions">Wikipedia - Floor and ceiling
		 * functions</a>
		 */
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			try {
				IExpr arg1 = F.eval(ast.arg1());
				ISignedNumber signedNumber = arg1.evalSignedNumber();
				if (signedNumber != null) {
					return signedNumber.round();
				}

				if (arg1.isIntegerResult()) {
					return arg1;
				}

				if (arg1.isPlus()) {
					IAST[] result = ((IAST) arg1).filter(new RoundPlusFunction());
					if (result[0].size() > 1) {
						if (result[1].size() > 1) {
							result[0].append(F.Round(result[1]));
						}
						return result[0];
					}
				}
				IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
				if (negExpr.isPresent()) {
					return Negate(Round(negExpr));
				}
			} catch (ArithmeticException ae) {
				// ISignedNumber#round() may throw ArithmeticException
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}
	}

}
