package org.matheclipse.core.builtin;

import static org.matheclipse.core.expression.F.Ceiling;
import static org.matheclipse.core.expression.F.Floor;
import static org.matheclipse.core.expression.F.FractionalPart;
import static org.matheclipse.core.expression.F.IntegerPart;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Round;

import java.math.BigInteger;
import java.util.function.Function;

import javax.annotation.Nonnull;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractArg2;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.IntervalSym;
import org.matheclipse.core.expression.StringX;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

public class IntegerFunctions {
	/**
	 * 
	 * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation in static
	 * initializer</a>
	 */
	private static class Initializer {

		private static void init() {
			F.BitLength.setEvaluator(new BitLength());
			F.Ceiling.setEvaluator(new Ceiling());
			F.Floor.setEvaluator(new Floor());
			F.FractionalPart.setEvaluator(new FractionalPart());
			F.FromDigits.setEvaluator(new FromDigits());
			F.IntegerDigits.setEvaluator(new IntegerDigits());
			F.IntegerExponent.setEvaluator(new IntegerExponent());
			F.IntegerLength.setEvaluator(new IntegerLength());
			F.IntegerPart.setEvaluator(new IntegerPart());
			F.Mod.setEvaluator(new Mod());
			F.PowerMod.setEvaluator(new PowerMod());
			F.Quotient.setEvaluator(new Quotient());
			F.QuotientRemainder.setEvaluator(new QuotientRemainder());
			F.Round.setEvaluator(new Round());
			F.UnitStep.setEvaluator(new UnitStep());
		}
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
			if (ast.arg1().isInteger()) {
				IInteger iArg1 = (IInteger) ast.arg1();
				BigInteger big = iArg1.toBigNumerator();
				return F.ZZ(big.bitLength());
			}
			return F.NIL;
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
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

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_2;
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
			if (arg1.isInfinity() || arg1.isNegativeInfinity()) {
				return arg1;
			}

			if (arg1.isPlus()) {
				IASTAppendable[] splittedPlus = ((IAST) arg1).filterNIL(new CeilingPlusFunction());
				if (splittedPlus[0].size() > 1) {
					if (splittedPlus[1].size() > 1) {
						splittedPlus[0].append(F.Ceiling(splittedPlus[1].oneIdentity0()));
					}
					return splittedPlus[0];
				}
			}
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Negate(Floor(negExpr));
			}
			if (arg1.isInterval()) {
				return IntervalSym.mapSymbol(F.Ceiling, (IAST) arg1);
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
	 * IntegerDigits(n, base)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns a list of integer digits for <code>n</code> under <code>base</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * IntegerDigits(n, base, padLeft)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * pads the result list on the left with maximum <code>padLeft</code> zeros.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; IntegerDigits(123)
	 * {1,2,3}
	 * 
	 * &gt;&gt; IntegerDigits(-123)
	 * {1,2,3}
	 * 
	 * &gt;&gt; IntegerDigits(123, 2)
	 * {1,1,1,1,0,1,1}
	 * 
	 * &gt;&gt; IntegerDigits(123, 2, 10)
	 * {0,0,0,1,1,1,1,0,1,1}
	 * 
	 * &gt;&gt; IntegerDigits({123,456,789}, 2, 10)
	 * {{0,0,0,1,1,1,1,0,1,1},{0,1,1,1,0,0,1,0,0,0},{1,1,0,0,0,1,0,1,0,1}}
	 * </pre>
	 */
	private static class IntegerDigits extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(IAST ast, EvalEngine engine) {
			IInteger base = F.C10;
			int padLeftZeros = 0;
			if (ast.size() >= 3) {
				IExpr arg2 = ast.arg2();
				if (arg2.isInteger() && ((IInteger) arg2).compareInt(1) > 0) {
					base = (IInteger) arg2;
				} else {
					return F.NIL;
				}
			}
			if (ast.size() >= 4) {
				padLeftZeros = ast.arg3().toIntDefault(Integer.MIN_VALUE);
				if (padLeftZeros < 0) {
					return F.NIL;
				}
			}
			IExpr arg1 = ast.arg1();
			if (arg1.isInteger()) {
				IASTAppendable list = F.ListAlloc();
				IInteger n = ((IInteger) arg1).abs();
				if (n.isZero()) {
					list.append(F.C0);
				} else {
					while (n.isPositive()) {
						IInteger mod = n.mod(base);
						list.append(mod);
						n = n.subtract(mod).div(base);
					}
				}
				int padSizeZeros = padLeftZeros - list.argSize();
				if (padSizeZeros < 0) {
					padSizeZeros = 0;
				}
				IASTAppendable result = F.ListAlloc(list.argSize() + padSizeZeros);
				for (int i = 0; i < padSizeZeros; i++) {
					result.append(F.C0);
				}
				return list.reverse(result);
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_3;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
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
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_2;
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

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_2;
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
			if (arg1.isInfinity() || arg1.isNegativeInfinity()) {
				return arg1;
			}
			if (arg1.isPlus()) {
				IASTAppendable[] splittedPlus = ((IAST) arg1).filterNIL(new FloorPlusFunction());
				if (splittedPlus[0].size() > 1) {
					if (splittedPlus[1].size() > 1) {
						splittedPlus[0].append(F.Floor(splittedPlus[1].oneIdentity0()));
					}
					return splittedPlus[0];
				}
			}
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Negate(Ceiling(negExpr));
			}
			if (arg1.isInterval()) {
				return IntervalSym.mapSymbol(F.Floor, (IAST) arg1);
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
			IExpr arg1 = ast.arg1();
			if (arg1.isNumber()) {
				// Note: fractional part for a complex number returns a new complex number with the fractional parts of
				// the real and imaginary part.
				return ((INumber) arg1).fractionalPart();
			}
			if (arg1.isInfinity() || arg1.isComplexInfinity()) {
				return F.Interval(F.List(F.C0, F.C1));
			}
			if (arg1.isNegativeInfinity()) {
				return F.Interval(F.List(F.CN1, F.C0));
			}
			if (arg1.isDirectedInfinity(F.CI)) {
				return F.Times(F.CI, F.Interval(F.List(F.C0, F.C1)));
			}
			if (arg1.isDirectedInfinity(F.CNI)) {
				return F.Times(F.CNI, F.Interval(F.List(F.C0, F.C1)));
			}
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Negate(FractionalPart(negExpr));
			}
			// if (arg1.isPlus() && arg1.first().isInteger()) {
			// }
			try {
				ISignedNumber signedNumber = arg1.evalReal();
				if (signedNumber != null) {
					if (signedNumber.isRangeExclExcl(F.CN1, F.C1)) {
						// arg1 is in the interval ]-1, 1[
						return arg1;
					}
					IInteger intValue = signedNumber.integerPart();
					return F.Subtract(arg1, intValue);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return F.NIL;
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.NHOLDALL | ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		}
	}

	private static class FromDigits extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(IAST ast, EvalEngine engine) {
			IExpr base = F.C10;
			if (ast.size() >= 3) {
				base = ast.arg2();
			}
			IExpr arg1 = ast.arg1();
			if (arg1.isList()) {
				IAST list = (IAST) arg1;
				return fromDigits(list, base);
			}
			if (arg1.isString()) {
				StringX str = (StringX) arg1;
				int radix = base.toIntDefault(-1);
				if (radix > 0) {
					try {
						return F.ZZ(new BigInteger(str.toString(), radix));
					} catch (RuntimeException rex) {
						//
					}
				}
				IASTAppendable digitsList = F.ListAlloc(str.length());
				for (int i = 0; i < str.length(); i++) {
					int digit = Integer.MIN_VALUE;
					char ch = str.charAt(i);
					if (ch >= '0' && ch <= '9') {
						digit = Character.digit(ch, radix);
					} else if (ch >= 'A' && ch <= 'Z') {
						digit = Character.digit(ch, 36);
					} else if (ch >= 'a' && ch <= 'z') {
						digit = Character.digit(ch, 36);
					} else {
						return F.NIL;
					}
					if (digit == Integer.MIN_VALUE) {
						return F.NIL;
					}
					digitsList.append(F.ZZ(digit));
				}
				return fromDigits(digitsList, base);

			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_2;
		}

		private IExpr fromDigits(IAST list, IExpr radix) {
			IASTAppendable result = F.PlusAlloc(list.size());
			int exp = 0;
			for (int i = list.size() - 1; i >= 1; i--) {
				result.append(list.get(i).abs().times(radix.power(exp++)));
			}
			return result;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
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
			if (ast.arg1().isInteger()) {
				IInteger radix = F.C10;
				if (ast.isAST2()) {
					if (ast.arg2().isInteger()) {
						radix = ((IInteger) ast.arg2());
					} else {
						return F.NIL;
					}
				}
				if (radix.isLT(F.C1)) {
					return engine.printMessage("IntegerLength: The base must be greater than 1");
				}
				IInteger iArg1 = (IInteger) ast.arg1();
				if (iArg1.isZero()) {
					return F.C1;
				}
				long l = iArg1.integerLength(radix);

				return F.ZZ(l);
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_2;
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
			try {
				IExpr arg1 = ast.arg1();
				ISignedNumber signedNumber = arg1.evalReal();
				if (signedNumber != null) {
					return signedNumber.integerPart();
				}
				if (arg1.isIntegerResult()) {
					return arg1;
				}
				if (arg1.isInfinity() || arg1.isNegativeInfinity() || arg1.isDirectedInfinity(F.CI)
						|| arg1.isDirectedInfinity(F.CNI) || arg1.isAST(F.IntegerPart, 2)) {
					return arg1;
				}
				IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
				if (negExpr.isPresent()) {
					return Negate(IntegerPart(negExpr));
				}
				if (arg1.isInterval()) {
					return IntervalSym.mapSymbol(F.IntegerPart, (IAST) arg1);
				}
			} catch (ArithmeticException ae) {
				// ISignedNumber#floor() or #ceil() may throw ArithmeticException
			}
			return F.NIL;
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
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
	private static class Mod extends AbstractFunctionEvaluator {

		/**
		 * 
		 * See: <a href="http://en.wikipedia.org/wiki/Modular_arithmetic">Wikipedia - Modular arithmetic</a>
		 */
		public IExpr evaluate(final IAST ast, @Nonnull EvalEngine engine) {
			IExpr m = ast.arg1();
			IExpr n = ast.arg2();
			if (n.isZero()) {
				EvalEngine.get().printMessage("Mod: Modulus 0 encountered");
				return F.Indeterminate;
			}
			if (ast.isAST3()) {
				IExpr d = ast.arg3();
				if (m.isNumber() && n.isNumber() && d.isNumber()) {
					if (m.isInteger() && n.isInteger() && d.isInteger()) {
						IExpr subExpr = ((ISignedNumber) m.subtract(d).divide(n)).floorFraction();
						return m.plus(F.CN1.times(n).times(subExpr));
					}
					if (m.isComplex() || n.isComplex() || d.isComplex() || //
							m.isComplexNumeric() || n.isComplexNumeric() || d.isComplexNumeric()) {
						// https://mathematica.stackexchange.com/a/114373/21734
						IExpr subExpr = engine.evaluate(F.Divide(F.Subtract(m, d), n));
						IExpr re = ((ISignedNumber) subExpr.re()).round();
						IExpr im = ((ISignedNumber) subExpr.im()).round();
						return F.Plus(m, F.Times(F.CN1, n, re), F.Times(F.CI, im));
					}
				}
				return F.NIL;
			}
			if (m.isInteger() && n.isInteger()) {
				final IInteger i0 = (IInteger) m;
				final IInteger i1 = (IInteger) n;
				if (i1.isNegative()) {
					return i0.negate().mod(i1.negate()).negate();
				}
				return i0.mod(i1);
			}
			if (ast.isAST2()) {
				if (m.isReal() && n.isReal()) {
					return F.Subtract(m, F.Times(n, F.Floor(((ISignedNumber) m).divideBy((ISignedNumber) n))));
				}
				if (m.isRealResult() && n.isRealResult()) {
					return F.Subtract(m, F.Times(n, F.Floor(F.num(m.evalDouble() / n.evalDouble()))));
				}
				if (m.isNumericFunction() && n.isNumericFunction()) {
					return F.Subtract(m, F.Times(n, F.Floor(F.complexNum(m.evalComplex().divide(n.evalComplex())))));
				}
			}
			return F.NIL;
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_2_3;
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
			if (ast.exists(x -> !x.isInteger())) {
				return F.NIL;
			}
			IInteger arg1 = (IInteger) ast.arg1();
			IInteger arg2 = (IInteger) ast.arg2();
			IInteger arg3 = (IInteger) ast.arg3();
			try {
				if (arg2.isMinusOne()) {
					return arg1.modInverse(arg3);
				}
				return arg1.modPow(arg2, arg3);
			} catch (ArithmeticException ae) {
				return engine.printMessage("PowerMod: " + ae.getMessage());
			}
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_3_3;
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
	private static class Quotient extends AbstractFunctionEvaluator {

		public IExpr evaluate(final IAST ast, @Nonnull EvalEngine engine) {
			IExpr z = ast.arg1();
			IExpr n = ast.arg2();
			if (n.isZero()) {
				EvalEngine.get().printMessage("Quotient: division by zero");
				return F.CComplexInfinity;
			}
			if (ast.isAST2()) {
				if (z.isInteger() && n.isInteger()) {
					return ((IInteger) z).quotient((IInteger) n);
				}
				if (z.isReal() && n.isReal()) {
					return F.Floor(((ISignedNumber) z).divideBy((ISignedNumber) n));
				}
				if (z.isRealResult() && n.isRealResult()) {
					return F.Floor(F.num(z.evalDouble() / n.evalDouble()));
				}
				if (z.isNumericFunction() && n.isNumericFunction()) {
					return F.Floor(F.complexNum(z.evalComplex().divide(n.evalComplex())));
				}
				return F.NIL;
			}
			if (ast.isAST3()) {
				IExpr d = ast.arg3();
				if (z.isInteger() && n.isInteger() && d.isInteger()) {
					// TODO implement for 3 args
				}
				return F.NIL;
			}
			return F.NIL;
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_2_3;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		}
	}

	/**
	 * <pre>
	 * QuotientRemainder(m, n)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * computes a list of the quotient and remainder from division of <code>m</code> and <code>n</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; QuotientRemainder(23, 7)
	 * {3,2}
	 * </pre>
	 * <p>
	 * Infinite expression QuotientRemainder(13, 0) encountered.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; QuotientRemainder(13, 0)
	 * QuotientRemainder(13, 0)
	 * 
	 * &gt;&gt; QuotientRemainder(-17, 7)
	 * {-3,4}
	 * 
	 * &gt;&gt; QuotientRemainder(-17, -4)
	 * {4,-1}
	 * 
	 * &gt;&gt; QuotientRemainder(19, -4)
	 * {-5,-1}
	 * </pre>
	 */
	private static class QuotientRemainder extends AbstractArg2 {

		@Override
		public IExpr e2IntArg(final IInteger i0, final IInteger i1) {
			try {
				if (i1.isZero()) {
					return EvalEngine.get().printMessage("QuotientRemainder: division by zero");
				}
				IASTMutable list = F.ListAlloc(F.Null, F.Null);
				list.set(1, i0.quotient(i1));
				if (i1.isNegative()) {
					list.set(2, i0.negate().mod(i1.negate()).negate());
					return list;
				}
				list.set(2, i0.mod(i1));
				return list;
			} catch (ArithmeticException ae) {
				return EvalEngine.get().printMessage("QuotientRemainder: " + ae.getMessage());
			}

		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		}
	}

	public static void initialize() {
		Initializer.init();
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
	private static class Round extends AbstractCoreFunctionEvaluator implements INumeric {

		private final static class RoundPlusFunction implements Function<IExpr, IExpr> {
			@Override
			public IExpr apply(IExpr expr) {
				if (expr.isIntegerResult()) {
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
			try {
				IExpr arg1 = engine.evaluate(ast.arg1());
				if (arg1.isList()) {
					return ((IAST) arg1).mapThread(ast.setAtCopy(1, F.Null), 1);
				}
				if (ast.isAST2()) {
					// Round(z, a)
					ISignedNumber multiple = ast.arg2().evalReal();
					if (multiple != null) {
						if (multiple.isZero()) {
							return F.Indeterminate;
						}
						ISignedNumber signedNumber = arg1.evalReal();
						if (signedNumber != null) {
							return signedNumber.roundClosest(multiple);
						}
						if (arg1.isComplexNumeric()) {
							IComplexNum cmp = (IComplexNum) arg1;
							ISignedNumber re = cmp.re().roundClosest(multiple);
							ISignedNumber im = cmp.im().roundClosest(multiple);
							return F.Complex(re, im);
						}
						if (arg1.isComplex()) {
							IComplex cmp = (IComplex) arg1;
							ISignedNumber re = cmp.re().roundClosest(multiple);
							ISignedNumber im = cmp.im().roundClosest(multiple);
							return F.Complex(re, im);
						}
						if (arg1.isInfinity() || arg1.isNegativeInfinity()) {
							return arg1;
						}
					}

					return F.NIL;
				}

				if (arg1.isIntegerResult()) {
					return arg1;
				}
				ISignedNumber signedNumber = arg1.evalReal();
				if (signedNumber != null) {
					return signedNumber.round();
				}
				if (arg1.isComplexNumeric()) {
					IComplexNum cmp = (IComplexNum) arg1;
					IInteger re = cmp.re().round();
					IInteger im = cmp.im().round();
					return F.complex(re, im);
				}
				if (arg1.isComplex()) {
					IComplex cmp = (IComplex) arg1;
					IInteger re = cmp.re().round();
					IInteger im = cmp.im().round();
					return F.complex(re, im);
				}

				if (arg1.isInfinity() || arg1.isNegativeInfinity()) {
					return arg1;
				}
				if (arg1.isPlus()) {
					IASTAppendable[] result = ((IAST) arg1).filterNIL(new RoundPlusFunction());
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
				if (arg1.isInterval()) {
					return IntervalSym.mapSymbol(F.Round, (IAST) arg1);
				}
			} catch (ArithmeticException ae) {
				// ISignedNumber#round() may throw ArithmeticException
			}
			return F.NIL;
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_2;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}
	}

	/**
	 * <pre>
	 * UnitStep(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>0</code>, if <code>expr</code> is less than <code>0</code> and returns <code>1</code>, if
	 * <code>expr</code> is greater equal than <code>0</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; UnitStep(-42)
	 * 0
	 * </pre>
	 */
	private static class UnitStep extends AbstractEvaluator implements INumeric {

		@Override
		public double evalReal(double[] stack, int top, int size) {
			for (int i = top - size + 1; i < top + 1; i++) {
				if (stack[i] < 0.0) {
					return 0.0;
				}
			}
			return 1.0;
		}

		/**
		 * Unit step <code>1</code> for all x greater equal <code>0</code>. <code>0</code> in all other cases,
		 */
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			int size = ast.size();
			if (size > 1) {
				for (int i = 1; i < size; i++) {
					IExpr expr = ast.get(i);
					ISignedNumber temp = expr.evalReal();
					if (temp != null) {
						if (temp.sign() < 0) {
							return F.C0;
						} else {
							continue;
						}
					} else {
						expr = engine.evaluate(expr);
						if (expr.isNegativeInfinity()) {
							return F.C0;
						}
						if (expr.isInfinity()) {
							continue;
						}
						if (expr.isNegativeResult()) {
							return F.C0;
						}
						if (expr.isNonNegativeResult()) {
							continue;
						}
						if (expr.isInterval1()) {
							IExpr l = expr.lower();
							IExpr u = expr.upper();
							if (l.isReal() && u.isReal()) {
								ISignedNumber min = (ISignedNumber) l;
								ISignedNumber max = (ISignedNumber) u;
								if (min.sign() < 0) {
									if (max.sign() < 0) {
										return F.Interval(F.List(F.C0, F.C0));
									} else {
										if (size == 2) {
											return F.Interval(F.List(F.C0, F.C1));
										}
									}
								} else {
									if (max.sign() < 0) {
										if (size == 2) {
											return F.Interval(F.List(F.C1, F.C0));
										}
									} else {
										if (size == 2) {
											return F.Interval(F.List(F.C1, F.C1));
										}
										continue;
									}
								}
							}
						}
					}
					return F.NIL;
				}
			}
			return F.C1;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.ORDERLESS | ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		}
	}
}
