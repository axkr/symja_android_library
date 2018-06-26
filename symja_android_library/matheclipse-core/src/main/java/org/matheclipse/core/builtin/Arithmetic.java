package org.matheclipse.core.builtin;

import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.ArcCos;
import static org.matheclipse.core.expression.F.ArcCot;
import static org.matheclipse.core.expression.F.ArcSin;
import static org.matheclipse.core.expression.F.ArcTan;
import static org.matheclipse.core.expression.F.Arg;
import static org.matheclipse.core.expression.F.BernoulliB;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Conjugate;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.E;
import static org.matheclipse.core.expression.F.Equal;
import static org.matheclipse.core.expression.F.Factorial;
import static org.matheclipse.core.expression.F.Factorial2;
import static org.matheclipse.core.expression.F.Gamma;
import static org.matheclipse.core.expression.F.Im;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.NIL;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Pi;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Positive;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Re;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.fraction;
import static org.matheclipse.core.expression.F.integer;
import static org.matheclipse.core.expression.F.num;
import static org.matheclipse.core.expression.F.x;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.y;
import static org.matheclipse.core.expression.F.y_;

import java.math.BigInteger;
import java.util.function.DoubleFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;

import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.hipparchus.fraction.BigFraction;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.PlusOp;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractArg1;
import org.matheclipse.core.eval.interfaces.AbstractArg12;
import org.matheclipse.core.eval.interfaces.AbstractArg2;
import org.matheclipse.core.eval.interfaces.AbstractArgMultiple;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.eval.util.AbstractAssumptions;
import org.matheclipse.core.eval.util.OpenIntToIExprHashMap;
import org.matheclipse.core.expression.ASTSeriesData;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.expression.NumberUtil;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.numbertheory.Primality;
import org.matheclipse.core.patternmatching.hash.HashedOrderlessMatcher;
import org.matheclipse.core.patternmatching.hash.HashedOrderlessMatcherPlus;
import org.matheclipse.core.patternmatching.hash.HashedOrderlessMatcherTimes;
import org.matheclipse.core.patternmatching.hash.HashedPatternRulesLog;
import org.matheclipse.core.patternmatching.hash.HashedPatternRulesTimes;
import org.matheclipse.core.reflection.system.rules.AbsRules;
import org.matheclipse.core.reflection.system.rules.ConjugateRules;
import org.matheclipse.core.reflection.system.rules.GammaRules;
import org.matheclipse.core.reflection.system.rules.PowerRules;

public final class Arithmetic {
	public final static Plus CONST_PLUS = new Plus();
	public final static Times CONST_TIMES = new Times();
	public final static Power CONST_POWER = new Power();
	public final static IFunctionEvaluator CONST_COMPLEX = new Complex();
	public final static IFunctionEvaluator CONST_RATIONAL = new Rational();

	static {
		F.Plus.setDefaultValue(F.C0);
		F.Plus.setEvaluator(CONST_PLUS);
		F.Times.setDefaultValue(F.C1);
		F.Times.setEvaluator(CONST_TIMES);
		F.Power.setDefaultValue(2, F.C1);
		F.Power.setEvaluator(CONST_POWER);
		F.Sqrt.setEvaluator(new Sqrt());
		F.Surd.setEvaluator(new Surd());
		F.Minus.setEvaluator(new Minus());

		F.Abs.setEvaluator(new Abs());
		F.AddTo.setEvaluator(new AddTo());
		F.Arg.setEvaluator(new Arg());
		F.Chop.setEvaluator(new Chop());
		F.Clip.setEvaluator(new Clip());
		F.Complex.setEvaluator(CONST_COMPLEX);
		F.ConditionalExpression.setEvaluator(new ConditionalExpression());
		F.Conjugate.setEvaluator(new Conjugate());
		F.Decrement.setEvaluator(new Decrement());
		F.DirectedInfinity.setEvaluator(new DirectedInfinity());
		F.Divide.setEvaluator(new Divide());
		F.DivideBy.setEvaluator(new DivideBy());
		F.Gamma.setEvaluator(new Gamma());
		F.GCD.setEvaluator(new GCD());
		F.HarmonicNumber.setEvaluator(new HarmonicNumber());
		F.Im.setEvaluator(new Im());
		F.Increment.setEvaluator(new Increment());
		F.LCM.setEvaluator(new LCM());
		F.N.setEvaluator(new N());
		F.Piecewise.setEvaluator(new Piecewise());
		F.Pochhammer.setEvaluator(new Pochhammer());
		F.Precision.setEvaluator(new Precision());
		F.PreDecrement.setEvaluator(new PreDecrement());
		F.PreIncrement.setEvaluator(new PreIncrement());
		F.Rational.setEvaluator(CONST_RATIONAL);
		F.Re.setEvaluator(new Re());
		F.Subtract.setEvaluator(new Subtract());
		F.SubtractFrom.setEvaluator(new SubtractFrom());
		F.TimesBy.setEvaluator(new TimesBy());

	}

	/**
	 * <pre>
	 * Abs(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the absolute value of the real or complex number <code>expr</code>.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:<br />
	 * </p>
	 * <ul>
	 * <li><a href="http://en.wikipedia.org/wiki/Absolute_value">Wikipedia - Absolute value</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Abs(-3)
	 * 3
	 * </pre>
	 */
	private final static class Abs extends AbstractTrigArg1 implements INumeric, AbsRules, DoubleUnaryOperator {

		private static final class AbsNumericFunction implements DoubleFunction<IExpr> {
			final ISymbol symbol;

			public AbsNumericFunction(ISymbol symbol) {
				this.symbol = symbol;
			}

			@Override
			public IExpr apply(double value) {
				if (value < Integer.MAX_VALUE && value > Integer.MIN_VALUE) {
					double result = Math.abs(value);
					if (result > 0.0) {
						return symbol;
					}
				}
				return F.NIL;
			}
		}

		private static final class AbsTimesFunction implements Function<IExpr, IExpr> {
			@Override
			public IExpr apply(IExpr expr) {
				if (expr.isNumber()) {
					return ((INumber) expr).abs();
				}
				IExpr temp = F.eval(F.Abs(expr));
				if (!temp.topHead().equals(F.Abs)) {
					return temp;
				}
				return F.NIL;
			}
		}

		@Override
		public double applyAsDouble(double operand) {
			return Math.abs(operand);
		}

		@Override
		public IExpr e1ApcomplexArg(Apcomplex arg1) {
			return F.num(ApcomplexMath.abs(arg1));
		}

		@Override
		public IExpr e1ApfloatArg(Apfloat arg1) {
			return F.num(ApfloatMath.abs(arg1));
		}

		@Override
		public IExpr e1ComplexArg(final org.hipparchus.complex.Complex arg1) {
			return F.num(ComplexNum.dabs(arg1));
		}

		@Override
		public IExpr e1DblArg(final double arg1) {
			return F.num(Math.abs(arg1));
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1) {
				throw new UnsupportedOperationException();
			}
			return Math.abs(stack[top]);
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1) {
			if (arg1.isDirectedInfinity()) {
				return F.CInfinity;
			}
			if (arg1.isNumber()) {
				return ((INumber) arg1).abs();
			}
			if (arg1.isNumericFunction()) {
				IExpr temp = F.evaln(arg1);
				if (temp.isReal()) {
					return arg1.copySign((ISignedNumber) temp);
				}
			}
			if (arg1.isNegativeResult()) {
				return F.Negate(arg1);
			}
			if (arg1.isNonNegativeResult()) {
				return arg1;
			}
			if (arg1.isSymbol()) {
				ISymbol sym = (ISymbol) arg1;
				return sym.mapConstantDouble(new AbsNumericFunction(sym));
			}

			if (arg1.isTimes()) {
				IASTAppendable[] result = ((IAST) arg1).filter(new AbsTimesFunction());
				if (result[0].size() > 1) {
					if (result[1].size() > 1) {
						result[0].append(F.Abs(result[1]));
					}
					return result[0];
				}
			}
			if (arg1.isPower() && arg1.exponent().isReal()) {
				return F.Power(F.Abs(arg1.base()), arg1.exponent());
			}
			return F.NIL;
		}

		@Override
		public IAST getRuleAST() {
			return RULES;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}

	}

	/**
	 * <pre>
	 * AddTo(x, dx)
	 * 
	 * x += dx
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * is equivalent to <code>x = x + dx</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; a = 10
	 * &gt;&gt; a += 2   
	 * 12    
	 * 
	 * &gt;&gt; a    
	 * 12
	 * </pre>
	 */
	private static class AddTo extends AbstractFunctionEvaluator {

		protected IASTMutable getAST(final IExpr value) {
			return (IASTMutable) F.Plus(null, value);
		}

		protected ISymbol getFunctionSymbol() {
			return F.AddTo;
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);
			ISymbol sym = Validate.checkSymbolType(ast, 1);
			IExpr arg2 = engine.evaluate(ast.arg2());
			IExpr[] results = sym.reassignSymbolValue(getAST(arg2), getFunctionSymbol(), engine);
			if (results != null) {
				return results[1];
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	/**
	 * <pre>
	 * Arg(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the argument of the complex number <code>expr</code>.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:
	 * </p>
	 * <ul>
	 * <li><a href="http://en.wikipedia.org/wiki/Argument_%28complex_analysis%29">Wikipedia - Argument
	 * (complex_analysis)</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Arg(1+I)   
	 * Pi/4
	 * </pre>
	 */
	private static class Arg extends AbstractCoreFunctionEvaluator implements INumeric, DoubleUnaryOperator {

		@Override
		public double applyAsDouble(double operand) {
			if (operand < 0) {
				return Math.PI;
			}
			return 0.0;
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1) {
				throw new UnsupportedOperationException();
			}
			if (stack[top] < 0) {
				return Math.PI;
			} else if (stack[top] >= 0) {
				return 0;
			}
			throw new UnsupportedOperationException();
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			final IExpr arg1 = engine.evaluate(ast.arg1());
			if (arg1.isList()) {
				return ((IAST) arg1).mapThread(F.Arg(F.Null), 1);
			}
			if (arg1.isIndeterminate()) {
				return F.Indeterminate;
			}
			if (arg1.isDirectedInfinity()) {
				IAST directedInfininty = (IAST) arg1;
				if (directedInfininty.isAST1()) {
					if (directedInfininty.isInfinity()) {
						return F.C0;
					}
					return F.Arg(directedInfininty.arg1());
				} else if (arg1.isComplexInfinity()) {
					return F.Interval(F.List(F.Pi.negate(), F.Pi));
				}
			} else if (arg1.isNumber()) {
				return ((INumber) arg1).complexArg();
			}
			if (arg1.isNumericFunction()) {
				IExpr temp = engine.evalN(arg1);
				if (temp.isReal()) {
					if (temp.isNegative()) {
						return F.Pi;
					}
					return F.C0;
				} else if (temp.isNumber() && engine.isNumericMode()) {
					return ((INumber) temp).complexArg();
				}
			}

			if (AbstractAssumptions.assumeNegative(arg1)) {
				return F.Pi;
			}
			if (AbstractAssumptions.assumePositive(arg1)) {
				return F.C0;
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}
	}

	/**
	 * <pre>
	 * Chop(numerical - expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * replaces numerical values in the <code>numerical-expr</code> which are close to zero with symbolic value
	 * <code>0</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Chop(0.00000000001)
	 * 0
	 * </pre>
	 */
	private static class Chop extends AbstractCoreFunctionEvaluator {
		public final static double DEFAULT_CHOP_DELTA = 1.0e-10;

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			IExpr arg1 = ast.arg1();
			double delta = DEFAULT_CHOP_DELTA;
			if (ast.isAST2() && ast.arg2() instanceof INum) {
				delta = ((INum) ast.arg2()).getRealPart();
			}
			try {
				arg1 = engine.evaluate(arg1);
				if (arg1.isAST()) {
					IAST list = (IAST) arg1;
					// Chop[{a,b,c}] -> {Chop[a],Chop[b],Chop[c]}
					return list.mapThread(F.Chop(F.Null), 1);
				}
				if (arg1.isNumber()) {
					return F.chopNumber((INumber) arg1, delta);
				}
			} catch (Exception e) {
				if (Config.SHOW_STACKTRACE) {
					e.printStackTrace();
				}
			}

			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.LISTABLE);
		}
	}

	/**
	 * <pre>
	 * Clip(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>expr</code> in the range <code>-1</code> to <code>1</code>. Returns <code>-1</code> if
	 * <code>expr</code> is less than <code>-1</code>. Returns <code>1</code> if <code>expr</code> is greater than
	 * <code>1</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * Clip(expr, {min, max})
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>expr</code> in the range <code>min</code> to <code>max</code>. Returns <code>min</code> if
	 * <code>expr</code> is less than <code>min</code>. Returns <code>max</code> if <code>expr</code> is greater than
	 * <code>max</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * Clip(expr, {min, max}, {vMin, vMax})
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>expr</code> in the range <code>min</code> to <code>max</code>. Returns <code>vMin</code> if
	 * <code>expr</code> is less than <code>min</code>. Returns <code>vMax</code> if <code>expr</code> is greater than
	 * <code>max</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Clip(Sin(Pi/7))
	 * Sin(Pi/7)
	 * 
	 * &gt;&gt; Clip(Tan(E))
	 * Tan(E)
	 * 
	 * &gt;&gt; Clip(Tan(2*E)
	 * -1
	 * 
	 * &gt;&gt; Clip(Tan(-2*E))
	 * 1
	 * 
	 * &gt;&gt; Clip(x)
	 * Clip(x)
	 * 
	 * &gt;&gt; Clip(Tan(2*E), {-1/2,1/2})
	 * -1/2
	 * 
	 * &gt;&gt; Clip(Tan(-2*E), {-1/2,1/2})
	 * 1/2
	 * 
	 * &gt;&gt; Clip(Tan(E), {-1/2,1/2}, {a,b})
	 * Tan(E)
	 * 
	 * &gt;&gt; Clip(Tan(2*E), {-1/2,1/2}, {a,b})
	 * a
	 * 
	 * &gt;&gt; Clip(Tan(-2*E), {-1/2,1/2}, {a,b})
	 * b
	 * </pre>
	 */
	private final static class Clip extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 4);

			IExpr x = ast.first();
			if (ast.size() == 2) {
				return clip(x);
			}

			IExpr vMin = null;
			IExpr vMax = null;
			if (ast.size() == 4) {
				IExpr arg3 = ast.arg3();
				if (arg3.isAST(F.List, 3)) {
					// { vMin, vMax } as 3rd argument expected
					vMin = arg3.first();
					vMax = arg3.second();
				} else {
					return F.NIL;
				}
			}
			if (ast.size() >= 3) {
				IExpr arg2 = ast.arg2();
				if (arg2.isAST(F.List, 3)) {
					// { min, max } as 2nd argument expected
					IExpr min = arg2.first();
					IExpr max = arg2.second();
					if (ast.size() == 3) {
						vMin = min;
						vMax = max;
					}
					if (min.isReal() && max.isReal()) {
						return clip(x, (ISignedNumber) min, (ISignedNumber) max, vMin, vMax);
					}
					ISignedNumber minEvaled = min.evalSignedNumber();
					if (minEvaled != null) {
						ISignedNumber maxEvaled = max.evalSignedNumber();
						if (maxEvaled != null) {
							return clip(x, (ISignedNumber) minEvaled, (ISignedNumber) maxEvaled, vMin, vMax);
						}
					}
				}
			}
			return F.NIL;
		}

		private IExpr clip(IExpr x) {
			if (x.isReal()) {
				ISignedNumber real = (ISignedNumber) x;
				if (real.isGreaterThan(F.C1)) {
					return F.C1;
				}
				if (real.isLessThan(F.CN1)) {
					return F.CN1;
				}
				return x;
			}
			ISignedNumber real = x.evalSignedNumber();
			if (real != null) {
				if (real.isGreaterThan(F.C1)) {
					return F.C1;
				}
				if (real.isLessThan(F.CN1)) {
					return F.CN1;
				}
				return x;
			}
			return F.NIL;
		}

		/**
		 * gives <code>vMin</code> for <code>x<min</code> and <code>vMax</code> for <code>x>max</code>.
		 * 
		 * @param x
		 *            the expreesion value
		 * @param min
		 *            minimum value
		 * @param max
		 *            maximum value
		 * @param vMin
		 *            value for x less than minimum
		 * @param vMax
		 *            value for x greater than minimum
		 * @return x if x is in the range min to max. Return vMin if x is less than min.Return vMax if x is greater than
		 *         max.
		 */
		private IExpr clip(IExpr x, ISignedNumber min, ISignedNumber max, IExpr vMin, IExpr vMax) {
			if (x.isReal()) {
				ISignedNumber real = (ISignedNumber) x;
				if (real.isGreaterThan(max)) {
					return vMax;
				}
				if (real.isLessThan(min)) {
					return vMin;
				}
				return x;
			}
			ISignedNumber real = x.evalSignedNumber();
			if (real != null) {
				if (real.isGreaterThan(max)) {
					return vMax;
				}
				if (real.isLessThan(min)) {
					return vMin;
				}
				return x;
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.NUMERICFUNCTION);
		}
	}

	/**
	 * <pre>
	 * Complex
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * is the head of complex numbers.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * Complex(a, b)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * constructs the complex number <code>a + I * b</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Head(2 + 3*I)
	 * Complex
	 * 
	 * &gt;&gt; Complex(1, 2/3)
	 * 1+I*2/3
	 * 
	 * &gt;&gt; Abs(Complex(3, 4))
	 * 5
	 * 
	 * &gt;&gt; -2 / 3 - I
	 * -2/3-I
	 * 
	 * &gt;&gt; Complex(10, 0)
	 * 10
	 * 
	 * &gt;&gt; 0. + I
	 * I*1.0
	 * 
	 * &gt;&gt; 1 + 0*I
	 * 1
	 * 
	 * &gt;&gt; Head(1 + 0*I)
	 * Integer
	 * 
	 * &gt;&gt; Complex(0.0, 0.0)
	 * 0.0
	 * 
	 * &gt;&gt; 0.*I
	 * 0.0
	 * 
	 * &gt;&gt; 0. + 0.*I
	 * 0.0
	 * 
	 * &gt;&gt; 1. + 0.*I
	 * 1.0
	 * 
	 * &gt;&gt; 0. + 1.*I
	 * I*1.0
	 * </pre>
	 * <p>
	 * Check nesting Complex
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Complex(1, Complex(0, 1))
	 * 0
	 * 
	 * &gt;&gt; Complex(1, Complex(1, 0))
	 * 1+I 
	 * 
	 * &gt;&gt; Complex(1, Complex(1, 1))
	 * I
	 * </pre>
	 */
	private final static class Complex extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			try {
				IExpr realExpr = ast.arg1();
				IExpr imaginaryExpr = ast.arg2();
				if (realExpr.isRational() && imaginaryExpr.isRational()) {
					// already evaluated
				} else if (realExpr instanceof INum && imaginaryExpr instanceof INum) {
					// already evaluated
				} else {
					realExpr = engine.evaluate(realExpr);
					imaginaryExpr = engine.evaluate(imaginaryExpr);
					if (!realExpr.isNumber() || !imaginaryExpr.isNumber()) {
						return F.NIL;
					}
				}

				if (realExpr.isRational() && imaginaryExpr.isRational()) {
					IRational re;
					if (realExpr.isInteger()) {
						re = (IInteger) realExpr; // F.fraction((IInteger) arg1, F.C1);
					} else {
						re = (IFraction) realExpr;
					}
					IRational im;
					if (imaginaryExpr.isInteger()) {
						im = (IInteger) imaginaryExpr; // F.fraction((IInteger) arg2, F.C1);
					} else {
						im = (IFraction) imaginaryExpr;
					}
					return F.complex(re, im);
				}
				if (realExpr instanceof INum && imaginaryExpr instanceof INum) {
					return F.complexNum(((INum) realExpr).doubleValue(), ((INum) imaginaryExpr).doubleValue());
				}
				if (realExpr.isNumber() && imaginaryExpr.isNumber()) {
					return F.Plus(realExpr, F.Times(F.CI, imaginaryExpr));
				}

				// don't optimize this way:
				// if (imaginaryExpr.isZero()) {
				// return realExpr;
				// }
				// if (realExpr.isZero()) {
				// return F.Times(F.CI, imaginaryExpr);
				// }

			} catch (Exception e) {
				if (Config.SHOW_STACKTRACE) {
					e.printStackTrace();
				}
			}

			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	private final static class ConditionalExpression extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			IExpr arg2 = engine.evaluate(ast.arg2());
			if (arg2.isTrue()) {
				return engine.evaluate(ast.arg1());
			}
			if (arg2.isFalse()) {
				return F.Undefined;
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.NUMERICFUNCTION);
		}
	}

	/**
	 * <pre>
	 * Conjugate(z)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the complex conjugate of the complex number <code>z</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Conjugate(3 + 4*I)
	 * 3 - 4 I
	 * 
	 * &gt;&gt; Conjugate(3)
	 * 3
	 * 
	 * &gt;&gt; Conjugate(a + b * I)
	 * -I*Conjugate(b)+Conjugate(a)
	 * 
	 * &gt;&gt; Conjugate({{1, 2 + I*4, a + I*b}, {I}})
	 * {{1,2-I*4,-I*Conjugate(b)+Conjugate(a)},{-I}}
	 * 
	 * &gt;&gt; {Conjugate(Pi), Conjugate(E)}
	 * {Pi,E}
	 * 
	 * &gt;&gt; Conjugate(1.5 + 2.5*I)
	 * 1.5+I*(-2.5)
	 * </pre>
	 */
	private final static class Conjugate extends AbstractTrigArg1 implements INumeric, ConjugateRules {

		/**
		 * Conjugate numbers and special objects like <code>Infinity</code> and <code>Indeterminate</code>.
		 * 
		 * @param arg1
		 * @return <code>F.NIL</code> if the evaluation wasn't possible
		 */
		private IExpr conjugate(IExpr arg1) {
			if (arg1.isNumber()) {
				return ((INumber) arg1).conjugate();
			}
			if (arg1.isRealResult()) {
				return arg1;
			}
			if (arg1.isDirectedInfinity()) {
				IAST directedInfininty = (IAST) arg1;
				if (directedInfininty.isComplexInfinity()) {
					return F.CComplexInfinity;
				}
				if (directedInfininty.isAST1()) {
					if (directedInfininty.isInfinity()) {
						return F.CInfinity;
					}
					IExpr conjug = F.eval(F.Conjugate(directedInfininty.arg1()));
					return F.Times(conjug, F.CInfinity);
				}
			}
			return F.NIL;
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1) {
				throw new UnsupportedOperationException();
			}
			return stack[top];
		}

		@Override
		public IExpr numericEval(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);
			IExpr arg1 = ast.arg1();
			return evaluateArg1(arg1);
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1) {
			IExpr temp = conjugate(arg1);
			if (temp.isPresent()) {
				return temp;
			}
			if (arg1.isPower()) {
				IExpr base = arg1.base();
				if (base.isPositiveResult()) {
					return F.Power(base, F.Conjugate(arg1.exponent()));
				}
			}
			if (arg1.isPlus()) {
				return ((IAST) arg1).mapThread((IASTMutable) F.Conjugate(F.Null), 1);
			}
			if (arg1.isTimes()) {
				IASTAppendable result = F.NIL;
				IASTAppendable clone = ((IAST) arg1).copyAppendable();
				int i = 1;
				while (i < clone.size()) {
					temp = conjugate(clone.get(i));
					if (temp.isPresent()) {
						clone.remove(i);
						if (!result.isPresent()) {
							result = ((IAST) arg1).copyHead();
						}
						result.append(temp);
						continue;
					}
					i++;
				}
				if (result.isPresent()) {
					if (clone.isAST0()) {
						return result;
					}
					if (clone.isAST0()) {
						result.append(F.Conjugate(clone.arg1()));
						return result;
					}
					result.append(F.Conjugate(clone));
					return result;
				}
			} else if (arg1.isConjugate()) {
				return arg1.first();
			} else if (arg1.isAST(F.Zeta, 2)) {
				return F.Zeta(F.Conjugate(arg1.first()));
			} else if (arg1.isAST(F.Zeta, 3) && arg1.first().isReal() && arg1.second().isReal()) {
				return F.Zeta(F.Conjugate(arg1.first()), F.Conjugate(arg1.second()));
			}
			return F.NIL;
		}

		@Override
		public IAST getRuleAST() {
			return RULES;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}

	}

	/**
	 * <pre>
	 * Decrement(x)
	 * 
	 * x--
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * decrements <code>x</code> by <code>1</code>, returning the original value of <code>x</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; a = 5   
	 * &gt;&gt; a--   
	 * 5    
	 * 
	 * &gt;&gt; a    
	 * 4
	 * </pre>
	 */
	private static class Decrement extends AbstractFunctionEvaluator {

		protected IASTMutable getAST() {
			return (IASTMutable) F.Plus(null, F.CN1);
		}

		public Decrement() {
			super();
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);
			IExpr symbol = ast.arg1();
			if (symbol.isSymbol() && ((ISymbol) symbol).hasAssignedSymbolValue()) {
				IExpr[] results = ((ISymbol) symbol).reassignSymbolValue(getAST(), getFunctionSymbol(), engine);
				if (results != null) {
					return getResult(results[0], results[1]);
				}
			}
			return F.NIL;
		}

		protected ISymbol getFunctionSymbol() {
			return F.Decrement;
		}

		protected IExpr getResult(IExpr symbolValue, IExpr calculatedResult) {
			return symbolValue;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	/**
	 * <pre>
	 * Divide(a, b)
	 * 
	 * a / b
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * represents the division of <code>a</code> by <code>b</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; 30 / 5
	 * 6
	 * 
	 * &gt;&gt; 1 / 8
	 * 1/8
	 * 
	 * &gt;&gt; Pi / 4
	 * Pi / 4
	 * </pre>
	 * <p>
	 * Use <code>N</code> or a decimal point to force numeric evaluation:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Pi / 4.0
	 * 0.7853981633974483
	 * 
	 * &gt;&gt; N(1 / 8)
	 * 0.125
	 * </pre>
	 * <p>
	 * Nested divisions:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; a / b / c
	 * a/(b*c)
	 * 
	 * &gt;&gt; a / (b / c)
	 * (a*c)/b
	 * 
	 * &gt;&gt; a / b / (c / (d / e))
	 * (a*d)/(b*c*e)
	 * 
	 * &gt;&gt; a / (b ^ 2 * c ^ 3 / e)
	 * (a*e)/(b^2*c^3) 
	 * 
	 * &gt;&gt; 1 / 4.0
	 * 0.25
	 * 
	 * &gt;&gt; 10 / 3 // FullForm
	 * "Rational(10,3)"
	 * 
	 * &gt;&gt; a / b // FullForm
	 * "Times(a, Power(b, -1))"
	 * </pre>
	 */
	private static class Divide extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);
			// arg1 * arg2^(-1)
			return F.Divide(ast.arg1(), ast.arg2());
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	/**
	 * <pre>
	 * DivideBy(x, dx)
	 * 
	 * x /= dx
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * is equivalent to <code>x = x / dx</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; a = 10
	 * &gt;&gt; a /= 2   
	 * 5
	 * 
	 * &gt;&gt; a    
	 * 5
	 * </pre>
	 */
	private static class DivideBy extends AddTo {

		@Override
		protected IASTMutable getAST(final IExpr value) {
			return (IASTMutable) F.Times(null, F.Power(value, F.CN1));
		}

		@Override
		protected ISymbol getFunctionSymbol() {
			return F.DivideBy;
		}

	}

	private final static class DirectedInfinity extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 1, 2);

			if (ast.isAST1()) {
				boolean numericMode = engine.isNumericMode();
				boolean evaled = false;
				try {
					engine.setNumericMode(false);
					IExpr arg1 = ast.arg1();
					IExpr temp = engine.evalLoop(arg1);
					if (temp.isPresent()) {
						arg1 = temp;
						evaled = true;
					}
					if (arg1.isIndeterminate() || arg1.isZero()) {
						return F.CComplexInfinity;
					}
					if (arg1.isReal()) {
						if (arg1.isOne() || arg1.isMinusOne()) {
							if (evaled) {
								return F.DirectedInfinity(arg1);
							}
							return F.NIL;
						}
						if (arg1.isNegative()) {
							return F.DirectedInfinity(F.CN1);
						} else {
							return F.DirectedInfinity(F.C1);
						}
					}
					if (arg1.isNumber()) {
						IExpr arg1Abs = engine.evaluate(F.Divide(arg1, F.Abs(arg1)));
						if (arg1.equals(arg1Abs)) {
							if (evaled) {
								return F.DirectedInfinity(arg1);
							}
							return F.NIL;
						}
						return F.DirectedInfinity(arg1Abs);
					}
					if (arg1.isNumericFunction()) {
						IExpr a1 = engine.evalN(arg1);
						if (a1.isReal()) {
							if (a1.isZero()) {
								return F.CComplexInfinity;
							}
							if (a1.isNegative()) {
								return F.DirectedInfinity(F.CN1);
							} else {
								return F.DirectedInfinity(F.C1);
							}
						}
					}
					if (evaled) {
						return F.DirectedInfinity(arg1);
					}
				} finally {
					engine.setNumericMode(numericMode);
				}
			}
			return F.NIL;

		}

		public static IExpr timesInf(IAST inf, IExpr a2) {
			if (inf.isAST1()) {
				IExpr result;
				IExpr a1 = inf.arg1();
				if (a1.isNumber()) {
					if (a2.isNumber()) {
						result = a1.times(a2);
						if (result.isReal()) {
							if (result.isNegative()) {
								return F.CNInfinity;
							} else {
								return F.CInfinity;
							}
						} else if (result.isImaginaryUnit()) {
							return F.DirectedInfinity(F.CI);
						} else if (result.isNegativeImaginaryUnit()) {
							return F.DirectedInfinity(F.CNI);
						}
					} else if (a2.isSymbol()) {
						if (a1.isOne()) {
							return F.DirectedInfinity(a2);
						} else if (a1.isMinusOne() || a1.equals(F.CI) || a1.equals(F.CNI)) {
							return F.DirectedInfinity(F.Times(a1, a2));
						}
					}
				} else if (a1.isSymbol()) {
					if (a2.isReal()) {
						if (a2.isNegative()) {
							return F.DirectedInfinity(F.Times(F.CN1, F.Sign(a1)));
						} else {
							return F.DirectedInfinity(a1);
						}
					} else if (a2.equals(F.CI)) {
						return F.DirectedInfinity(F.Times(F.CI, a1));
					} else if (a2.equals(F.CNI)) {
						return F.DirectedInfinity(F.Times(F.CNI, F.Sign(a1)));
					}
				}
				result = F.Divide(F.Times(a1, a2), F.Abs(F.Times(a1, a2)));
				return F.DirectedInfinity(result);
			}
			// ComplexInfinity
			return inf;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			// don't set ISymbol.NUMERICFUNCTION);
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	/**
	 * <pre>
	 * Gamma(z)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * is the gamma function on the complex number <code>z</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Gamma(8)
	 * 5040
	 * 
	 * &gt;&gt; Gamma(1/2)
	 * Sqrt(Pi)
	 * 
	 * &gt;&gt; Gamma(2.2)
	 * 1.1018024908797128
	 * </pre>
	 */
	private final static class Gamma extends AbstractArg12 implements GammaRules {

		// @Override
		// public double applyAsDouble(double operand) {
		// return org.hipparchus.special.Gamma.gamma(operand);
		// }

		@Override
		public IExpr e1DblArg(final INum arg1) {
			double gamma = org.hipparchus.special.Gamma.gamma(arg1.doubleValue());
			return num(gamma);
		}

		public IExpr e2DblArg(final INum d0, final INum d1) {
			return F.num(de.lab4inf.math.functions.IncompleteGamma.incGammaP(d0.doubleValue(), d1.doubleValue()));
		}

		public IExpr e2ObjArg(final IExpr o0, final IExpr z) {
			int n = o0.toIntDefault(Integer.MIN_VALUE);
			if (n > 0) {
				//
				// Gamma(n,z) = ((n - 1)! * Sum(z^k/k!, {k, 0, n - 1}))/E^z
				//
				IASTAppendable sum = F.PlusAlloc(n);
				for (int k = 0; k < n; k++) {
					sum.append(F.Divide(F.Power(z, k), F.Factorial(F.ZZ(k))));
				}
				return F.Times(F.Factorial(F.ZZ(n - 1)), sum, F.Power(E, z.negate()));
			}
			return F.NIL;
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);
			if (ast.size() != 3) {
				return unaryOperator(ast.arg1());
			}
			return binaryOperator(ast.arg1(), ast.arg2());
		}

		@Override
		public IExpr e1ObjArg(final IExpr arg1) {
			if (arg1.isIntegerResult()) {
				if (arg1.isNegative()) {
					return F.CComplexInfinity;
				}
				if (arg1.isNonNegativeResult()) {
					if (arg1.isInteger()) {
						return NumberTheory.factorial(((IInteger) arg1).subtract(F.C1));
					}
					return F.Factorial(arg1.subtract(F.C1));
				}
				return F.NIL;
			}
			if (arg1.isFraction()) {
				IFraction frac = (IFraction) arg1;
				if (frac.denominator().equals(C2)) {
					IInteger n = frac.numerator();
					if (arg1.isNegative()) {
						n = n.negate();
						return Times(Power(CN1, Times(C1D2, Plus(C1, n))), Power(C2, n), Sqrt(Pi),
								Power(Factorial(n), -1), Factorial(Times(C1D2, Plus(CN1, n))));
					} else {
						// Sqrt(Pi) * (n-2)!! / 2^((n-1)/2)
						return Times(Sqrt(Pi), Factorial2(n.subtract(C2)), Power(C2, Times(C1D2, Subtract(C1, n))));
					}
				}
			} else if (arg1.isAST()) {
				IAST z = (IAST) arg1;
				if (z.isAST(Conjugate, 2)) {
					// mirror symmetry for Conjugate()
					return Conjugate(Gamma(z.arg1()));
				}

			}
			return NIL;
		}

		@Override
		public IAST getRuleAST() {
			return RULES;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}
	}

	/**
	 * <pre>
	 * GCD(n1, n2, ...)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * computes the greatest common divisor of the given integers.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; GCD(20, 30)
	 * 10
	 * &gt;&gt; GCD(10, y)
	 * GCD(10, y)
	 * </pre>
	 * <p>
	 * 'GCD' is 'Listable':
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; GCD(4, {10, 11, 12, 13, 14})
	 * {2, 1, 4, 1, 2}
	 * </pre>
	 */
	private final static class GCD extends AbstractArgMultiple {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST0()) {
				return F.C0;
			} else if (ast.isAST1()) {
				if (ast.arg1().isExactNumber()) {
					return ast.arg1().abs();
				}
			}
			return super.evaluate(ast, engine);
		}

		@Override
		public IExpr e2FraArg(IFraction f0, IFraction f1) {
			return f0.gcd(f1);
		}

		/**
		 * Compute gcd of 2 integer numbers
		 * 
		 */
		@Override
		public IExpr e2IntArg(final IInteger i0, final IInteger i1) {
			return i0.gcd(i1);
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.ONEIDENTITY | ISymbol.ORDERLESS | ISymbol.FLAT | ISymbol.LISTABLE);
		}

	}

	/**
	 * <pre>
	 * HarmonicNumber(n)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the <code>n</code>th harmonic number.<br />
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Table(HarmonicNumber(n), {n, 8})
	 * {1,3/2,11/6,25/12,137/60,49/20,363/140,761/280}
	 * </pre>
	 */
	private final static class HarmonicNumber extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			IExpr arg1 = ast.arg1();
			if (ast.isAST2()) {
				IExpr arg2 = ast.arg2();
				if (arg2.isOne()) {
					return F.HarmonicNumber(arg1);
				} else {
					// generalized harmonic number
					if (arg2.isInteger()) {
						if (arg1.isInfinity()) {
							if (arg2.isPositive() && ((IInteger) arg2).isEven()) {
								// Module({v=s/2},((2*Pi)^(2*v)*(-1)^(v+1)*BernoulliB(2*v))/(2*(2*v)!))
								IExpr v = Times(C1D2, arg2);
								return Times(Power(Times(C2, Pi), Times(C2, v)), Power(CN1, Plus(v, C1)),
										BernoulliB(Times(C2, v)), Power(Times(C2, Factorial(Times(C2, v))), CN1));
							}
							return F.NIL;
						}
					}
					if (arg1.isInteger()) {
						int n = Validate.checkIntType(ast, 1, Integer.MIN_VALUE);
						if (n < 0) {
							return F.NIL;
						}
						if (n == 0) {
							return C0;
						}
						IASTAppendable result = F.PlusAlloc(n);
						return result.appendArgs(n + 1, i -> Power(integer(i), Negate(arg2)));
						// for (int i = 1; i <= n; i++) {
						// result.append(Power(integer(i), Negate(arg2)));
						// }
						// return result;
					}
					return F.NIL;
				}
			}
			if (arg1.isInteger()) {
				if (arg1.isNegative()) {
					return F.CComplexInfinity;
				}
				int n = Validate.checkIntType(ast, 1, Integer.MIN_VALUE);
				if (n < 0) {
					return F.NIL;
				}
				if (n == 0) {
					return C0;
				}
				if (n == 1) {
					return C1;
				}

				return fraction(harmonicNumber(n));
			}
			if (arg1.isInfinity()) {
				return arg1;
			}
			if (arg1.isNegativeInfinity()) {
				return F.CComplexInfinity;
			}

			return F.NIL;
		}

		/**
		 * The Harmonic number at the index specified
		 * 
		 * @param n
		 *            the index, non-negative.
		 * @return the H_1=1 for n=1, H_2=3/2 for n=2 etc. For values of n less than 1, zero is returned.
		 */
		public BigFraction harmonicNumber(int n) {
			if (n < 1)
				return BigFraction.ZERO;
			else {
				/*
				 * start with 1 as the result
				 */
				BigFraction a = new BigFraction(1, 1);

				/*
				 * add 1/i for i=2..n
				 */
				for (int i = 2; i <= n; i++) {
					a = a.add(new BigFraction(1, i));
				}
				return a;
			}
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}

	}

	/**
	 * <pre>
	 * Im(z)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the imaginary component of the complex number <code>z</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Im(3+4I)
	 * 4
	 * 
	 * &gt;&gt; Im(0.5 + 2.3*I)
	 * 2.3
	 * </pre>
	 */
	private final static class Im extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			IExpr arg1 = ast.arg1();
			if (arg1.isDirectedInfinity()) {
				IAST directedInfininty = (IAST) arg1;
				if (directedInfininty.isComplexInfinity()) {
					return F.Indeterminate;
				}
				if (directedInfininty.isAST1()) {
					if (directedInfininty.isInfinity()) {
						return F.C0;
					}
					IExpr im = F.Im.of(engine, directedInfininty.arg1());
					if (im.isNumber()) {
						if (im.isZero()) {
							return F.C0;
						}
						return F.Times(F.Sign(im), F.CInfinity);
					}
				}
			}
			if (arg1.isNumber()) {
				return ((INumber) arg1).im();
			}
			if (arg1.isRealResult()) {
				return F.C0;
			}
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Negate(Im(negExpr));
			}
			if (arg1.isTimes()) {
				IExpr first = arg1.first();
				if (first.isReal()) {
					return F.Times(first, F.Im(arg1.rest()));
				}
				if (first.isImaginaryUnit()) {
					// Im(I*temp) -> Re(temp)
					return arg1.rest().re();
				}
			}
			if (arg1.isPlus()) {
				return ((IAST) arg1).mapThread((IAST) F.Im(null), 1);
			}
			if (arg1.isPower()) {
				IExpr x = arg1.base();
				if (x.isRealResult()) {
					// test for x^(a+I*b)
					IExpr exponent = arg1.exponent();
					if (exponent.isNumber()) {
						// (x^2)^(a/2)*E^(-b*Arg[x])*Sin[a*Arg[x]+1/2*b*Log[x^2]]
						IExpr a = exponent.re();
						IExpr b = exponent.im();
						return imPowerComplex(x, a, b);
					}
					if (exponent.isNumericFunction()) {
						// (x^2)^(a/2)*E^(-b*Arg[x])*Sin[a*Arg[x]+1/2*b*Log[x^2]]
						IExpr a = engine.evaluate(F.Re(exponent));
						IExpr b = engine.evaluate(F.Im(exponent));
						return imPowerComplex(x, a, b);
					}
				}
			}
			return F.NIL;
		}

		/**
		 * Evaluate <code>Im(x^(a+I*b))</code>
		 * 
		 * @param x
		 * @param a
		 *            the real part of the exponent
		 * @param b
		 *            the imaginary part of the exponent
		 * @return
		 */
		private IExpr imPowerComplex(IExpr x, IExpr a, IExpr b) {
			if (x.isE()) {
				// Im(E^(a+I*b)) -> E^a*Sin[b]
				return Times(Power(F.E, a), Sin(b));
			}
			return Times(Times(Power(Power(x, C2), Times(C1D2, a)), Power(E, Times(Negate(b), Arg(x)))),
					Sin(Plus(Times(a, Arg(x)), Times(Times(C1D2, b), Log(Power(x, C2))))));
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		}

	}

	/**
	 * <pre>
	 * Increment(x)
	 * 
	 * x++
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * increments <code>x</code> by <code>1</code>, returning the original value of <code>x</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; a = 2;   
	 * &gt;&gt; a++    
	 * 2    
	 * 
	 * &gt;&gt; a    
	 * 3
	 * </pre>
	 * <p>
	 * Grouping of 'Increment', 'PreIncrement' and 'Plus':<br />
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; ++++a+++++2//Hold//FullForm    
	 * Hold(Plus(PreIncrement(PreIncrement(Increment(Increment(a)))), 2))
	 * </pre>
	 */
	private static class Increment extends Decrement {

		@Override
		protected IASTMutable getAST() {
			return (IASTMutable) F.Plus(null, F.C1);
		}

		@Override
		protected ISymbol getFunctionSymbol() {
			return F.Increment;
		}
	}

	/**
	 * <pre>
	 * LCM(n1, n2, ...)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * computes the least common multiple of the given integers.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; LCM(15, 20)
	 * 60
	 * &gt;&gt; LCM(20, 30, 40, 50)
	 * 600
	 * </pre>
	 */
	private final static class LCM extends AbstractArgMultiple {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2);

			if (ast.isAST1()) {
				if (ast.arg1().isExactNumber()) {
					return ast.arg1().abs();
				}
			}
			return super.evaluate(ast, engine);
		}

		/**
		 * Compute lcm of 2 integer numbers
		 * 
		 */
		@Override
		public IExpr e2IntArg(final IInteger i0, final IInteger i1) {
			return i0.lcm(i1);
		}

		@Override
		public IExpr e2ObjArg(final IExpr o0, final IExpr o1) {
			if (o0.isZero()) {
				return o0;
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.ORDERLESS | ISymbol.FLAT | ISymbol.LISTABLE);
		}

	}

	/**
	 * <pre>
	 * Minus(expr)
	 * 
	 * 		- expr
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * is the negation of <code>expr</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; -a //FullForm
	 * "Times(-1, a)"
	 * </pre>
	 * <p>
	 * <code>Minus</code> automatically distributes:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; -(x - 2/3)
	 * 2/3-x
	 * </pre>
	 * <p>
	 * <code>Minus</code> threads over lists:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; -Range(10)
	 * {-1,-2,-3,-4,-5,-6,-7,-8,-9,-10}
	 * </pre>
	 */
	private final static class Minus extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() == 2) {
				IExpr arg1 = engine.evaluate(ast.arg1());
				return F.Times(F.CN1, arg1);
			}
			engine.printMessage("Minus: exactly 1 argument expected");
			return F.NIL;
		}

	}

	/**
	 * <pre>
	 * N(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * gives the numerical value of <code>expr</code>.<br />
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * N(expr, precision)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * evaluates <code>expr</code> numerically with a precision of <code>prec</code> digits.<br />
	 * </p>
	 * </blockquote>
	 * <p>
	 * <strong>Note</strong>: the upper case identifier <code>N</code> is different from the lower case identifier
	 * <code>n</code>.
	 * </p>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; N(Pi)
	 * 3.141592653589793
	 * 
	 * &gt;&gt; N(Pi, 50)
	 * 3.1415926535897932384626433832795028841971693993751
	 * 
	 * &gt;&gt; N(1/7)
	 * 0.14285714285714285
	 * 
	 * &gt;&gt; N(1/7, 5)
	 * 1.4285714285714285714e-1
	 * </pre>
	 */
	private static class N extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			return numericEval(ast, engine);
		}

		@Override
		public IExpr numericEval(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			final boolean numericMode = engine.isNumericMode();
			final int oldPrecision = engine.getNumericPrecision();
			try {
				int numericPrecision = Config.MACHINE_PRECISION;
				if (ast.isAST2()) {
					numericPrecision = Validate.checkIntType(ast.arg2());
				}
				engine.setNumericMode(true, numericPrecision);
				return engine.evalWithoutNumericReset(ast.arg1());
			} finally {
				engine.setNumericMode(numericMode);
				engine.setNumericPrecision(oldPrecision);
			}
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	/**
	 * <pre>
	 * Piecewise({{expr1, cond1}, ...})
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * represents a piecewise function.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * Piecewise({{expr1, cond1}, ...}, expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * represents a piecewise function with default <code>expr</code>.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:
	 * </p>
	 * <ul>
	 * <li><a href="http://en.wikipedia.org/wiki/Piecewise">Wikipedia - Piecewise</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Piecewise({{-x, x&lt;0}, {x, x&gt;=0}})/.{{x-&gt;-3}, {x-&gt;-1/3}, {x-&gt;0}, {x-&gt;1/2}, {x-&gt;5}}
	 * {3,1/3,0,1/2,5}
	 * </pre>
	 * <p>
	 * Heaviside function
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Piecewise({{0, x &lt;= 0}}, 1)    
	 * Piecewise({{0, x &lt;= 0}}, 1)
	 * </pre>
	 * <p>
	 * Piecewise defaults to <code>0</code>, if no other case is matching.<br />
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Piecewise({{1, False}})    
	 * 0    
	 * 
	 * &gt;&gt; Piecewise({{0 ^ 0, False}}, -1)    
	 * -1
	 * </pre>
	 */
	private final static class Piecewise extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			IExpr arg1 = ast.arg1();
			int[] dim = arg1.isMatrix(false);
			if (dim == null || dim[0] <= 0 || dim[1] != 2) {
				if (arg1.isAST(F.List, 1)) {
					if (ast.isAST2()) {
						return ast.arg2();
					}
					return F.C0;
				}
				engine.printMessage("Piecewise: Matrix with row-dimension > 0 and column-dimension == 2 expected!");
				return F.NIL;
			}
			IAST matrix = (IAST) arg1;
			IExpr defaultValue = F.C0;
			if (ast.isAST2()) {
				defaultValue = ast.arg2();
			}
			IExpr condition;
			IAST row;
			int matrixSize = matrix.size();
			IASTAppendable result = F.NIL;
			IASTAppendable piecewiseAST = F.NIL;
			boolean evaluated = false;
			boolean noBoolean = false;
			for (int i = 1; i < matrixSize; i++) {
				row = matrix.getAST(i);
				condition = row.arg2();
				if (condition.isTrue()) {
					if (!evaluated && i == matrixSize - 1) {
						if (!row.arg1().isSymbol()) {
							return row.arg1();
						}
						return F.NIL;
					}
					if (noBoolean) {
						result = appendPiecewise(result, row.arg1(), F.True, matrixSize);
						return createPiecewise(piecewiseAST, result);
					}
					return row.arg1();
				} else if (condition.isFalse()) {
					evaluated = true;
					continue;
				}
				condition = engine.evaluateNull(condition);
				if (condition.isTrue()) {
					evaluated = true;
					if (noBoolean) {
						result = appendPiecewise(result, row.arg1(), F.True, matrixSize);
						return createPiecewise(piecewiseAST, result);
					}
					return row.arg1();
				} else if (condition.isFalse()) {
					evaluated = true;
					continue;
				} else {
					result = appendPiecewise(result, row.arg1(), condition.orElse(row.arg2()), matrixSize);
					piecewiseAST = createPiecewise(piecewiseAST, result);
					noBoolean = true;
					continue;
				}
			}
			if (!noBoolean) {
				return defaultValue;
			} else {
				if (evaluated) {
					piecewiseAST = createPiecewise(piecewiseAST, F.List());
					piecewiseAST.append(defaultValue);
					return piecewiseAST;
				}
			}

			return F.NIL;
		}

		private static IASTAppendable createPiecewise(IASTAppendable piecewiseAST, IAST resultList) {
			if (!piecewiseAST.isPresent()) {
				piecewiseAST = F.ast(F.Piecewise);
				piecewiseAST.append(resultList);
			}
			return piecewiseAST;
		}

		private static IASTAppendable appendPiecewise(IASTAppendable list, IExpr function, IExpr predicate,
				int matrixSize) {
			if (!list.isPresent()) {
				list = F.ListAlloc(matrixSize);
			}
			list.append(F.List(function, predicate));
			return list;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.NUMERICFUNCTION);
		}
	}

	/**
	 * <pre>
	 * Plus(a, b, ...)
	 * 
	 * a + b + ...
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * represents the sum of the terms <code>a, b, ...</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; 1 + 2
	 * 3
	 * </pre>
	 * <p>
	 * <code>Plus</code> performs basic simplification of terms:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; a + b + a
	 * 2*a+b
	 * 
	 * &gt;&gt; a + a + 3 * a
	 * 5*a
	 * 
	 * &gt;&gt; a + b + 4.5 + a + b + a + 2 + 1.5 * b
	 * 6.5+3.0*a+3.5*b
	 * </pre>
	 * <p>
	 * Apply <code>Plus</code> on a list to sum up its elements:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Plus @@ {2, 4, 6}
	 * 12
	 * </pre>
	 * <p>
	 * The sum of the first <code>1000</code> integers:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Plus @@ Range(1000)
	 * 500500
	 * </pre>
	 * <p>
	 * <code>Plus</code> has default value <code>0</code>:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; a /. n_. + x_ :&gt; {n, x}
	 * {0,a}
	 * 
	 * &gt;&gt; -2*a - 2*b
	 * -2*a-2*b
	 * 
	 * &gt;&gt; -4+2*x+2*Sqrt(3)
	 * -4+2*x+2*Sqrt(3)
	 * 
	 * &gt;&gt; 1 - I * Sqrt(3)
	 * 1-I*Sqrt(3)
	 * 
	 * &gt;&gt; Head(3 + 2 I)
	 * Complex
	 * 
	 * &gt;&gt; N(Pi, 30) + N(E, 30)
	 * 5.85987448204883847382293085463
	 * 
	 * &gt;&gt; N(Pi, 30) + N(E, 30) // Precision
	 * 30
	 * </pre>
	 */
	public static class Plus extends AbstractArgMultiple implements INumeric {

		private static HashedOrderlessMatcherPlus ORDERLESS_MATCHER = new HashedOrderlessMatcherPlus();

		public Plus() {

		}

		@Override
		public IExpr e2ComArg(final IComplex c0, final IComplex c1) {
			return c0.add(c1);
		}

		@Override
		public IExpr e2DblArg(final INum d0, final INum d1) {
			return d0.add(d1);
		}

		@Override
		public IExpr e2DblComArg(final IComplexNum d0, final IComplexNum d1) {
			return d0.add(d1);
		}

		@Override
		public IExpr e2FraArg(final IFraction f0, final IFraction f1) {
			return f0.add(f1);
		}

		@Override
		public IExpr e2IntArg(final IInteger i0, final IInteger i1) {
			return i0.add(i1);
		}

		@Override
		public IExpr eComIntArg(final IComplex c0, final IInteger i1) {
			return c0.add(F.complex(i1, F.C0));
		}

		private IExpr evalNumericMode(final IAST ast) {
			INum number = F.CD0;
			int start = -1;
			for (int i = 1; i < ast.size(); i++) {
				IExpr temp = ast.get(i);
				if (temp instanceof INum) {
					if (temp instanceof ApfloatNum) {
						number = number.add((INum) temp);
					} else {
						if (number instanceof ApfloatNum) {
							number = number.add(((INum) temp).apfloatNumValue(number.precision()));
						} else {
							number = number.add((INum) temp);
						}
					}
				} else if (temp instanceof IComplexNum) {
					start = i;
					break;
				} else {
					return F.NIL;
				}
			}
			if (start < 0) {
				return number;
			}
			IComplexNum complexNumber;
			if (number instanceof Num) {
				complexNumber = F.complexNum(((Num) number).doubleValue());
			} else {
				complexNumber = F.complexNum(((ApfloatNum) number).apfloatValue());
			}
			for (int i = start; i < ast.size(); i++) {
				IExpr temp = ast.get(i);
				if (temp instanceof INum) {
					number = (INum) temp;
					if (number instanceof Num) {
						complexNumber = complexNumber.add(F.complexNum(((Num) number).doubleValue()));
					} else {
						complexNumber = complexNumber.add(F.complexNum(((ApfloatNum) number).apfloatValue()));
					}
				} else if (temp instanceof IComplexNum) {
					if (complexNumber instanceof ApcomplexNum) {
						complexNumber = complexNumber
								.add(((IComplexNum) temp).apcomplexNumValue(complexNumber.precision()));
					} else {
						complexNumber = complexNumber.add((IComplexNum) temp);
					}
				} else {
					return F.NIL;
				}
			}
			return complexNumber;
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			double result = 0;
			for (int i = top - size + 1; i < top + 1; i++) {
				result += stack[i];
			}
			return result;
		}

		/**
		 * 
		 * See: <a href="http://www.cs.berkeley.edu/~fateman/papers/newsimp.pdf"> Experiments in Hash-coded Algebraic
		 * Simplification</a>
		 * 
		 * @param ast
		 *            the abstract syntax tree (AST) of the form <code>Plus(...)</code> which should be evaluated
		 * @return the evaluated object or <code>null</code>, if evaluation isn't possible
		 */
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			int size = ast.size();
			if (size == 1) {
				return F.C0;
			}
			if (size == 2 && ast.head() == F.Plus) {
				return ast.arg1();
			}
			if (size > 2) {
				PlusOp plusOp = new PlusOp(size);
				for (int i = 1; i < size; i++) {
					final IExpr temp = plusOp.plus(ast.get(i));
					if (temp.isPresent()) {
						return temp;
					}
				}
				if (plusOp.isEvaled()) {
					return plusOp.getSum();
				}
			}

			if (size > 2) {
				IExpr temp = evaluateHashsRepeated(ast, engine);
				if (temp.isAST(F.Plus, 2)) {
					return temp.first();
				}
				return temp;
			}
			return F.NIL;
		}

		@Override
		public HashedOrderlessMatcher getHashRuleMap() {
			return ORDERLESS_MATCHER;
		}

		/** {@inheritDoc} */
		@Override
		public IExpr numericEval(final IAST ast, EvalEngine engine) {
			IExpr temp = evalNumericMode(ast);
			if (temp.isPresent()) {
				return temp;
			}
			return evaluate(ast, engine);
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.ONEIDENTITY | ISymbol.ORDERLESS | ISymbol.FLAT | ISymbol.LISTABLE
					| ISymbol.NUMERICFUNCTION);

			// ORDERLESS_MATCHER.setUpHashRule("Sin[x_]^2", "Cos[x_]^2", "a");
			ORDERLESS_MATCHER.definePatternHashRule(Power(Sin(x_), C2), Power(Cos(x_), C2), C1);

			// ORDERLESS_MATCHER.setUpHashRule("a_*Sin[x_]^2", "a_*Cos[x_]^2", "a");
			// ORDERLESS_MATCHER.defineHashRule(Times(a_, Power(Sin(x_), C2)), Times(a_,
			// Power(Cos(x_), C2)), a);

			// ORDERLESS_MATCHER.setUpHashRule("ArcSin[x_]", "ArcCos[x_]", "Pi/2");
			ORDERLESS_MATCHER.defineHashRule(ArcSin(x_), ArcCos(x_), Times(C1D2, Pi));
			// ORDERLESS_MATCHER.setUpHashRule("ArcTan[x_]", "ArcCot[x_]", "Pi/2");
			ORDERLESS_MATCHER.defineHashRule(ArcTan(x_), ArcCot(x_), Times(C1D2, Pi));
			// ORDERLESS_MATCHER.setUpHashRule("ArcTan[x_]", "ArcTan[y_]", "Pi/2",
			// "Positive[x]&&(y==1/x)");
			ORDERLESS_MATCHER.defineHashRule(ArcTan(x_), ArcTan(y_), Times(C1D2, Pi),
					And(Positive(x), Equal(y, Power(x, CN1))));

			// ORDERLESS_MATCHER.setUpHashRule("-ArcTan[x_]", "-ArcTan[y_]",
			// "-Pi/2", "Positive[x]&&(y==1/x)");
			// ORDERLESS_MATCHER.definePatternHashRule(Times(CN1, ArcTan(x_)), Times(CN1,
			// ArcTan(y_)), Times(CN1D2, Pi),
			// And(Positive(x), Equal(y, Power(x, CN1))));
			// ORDERLESS_MATCHER.setUpHashRule("Cosh[x_]^2", "-Sinh[x_]^2",
			// "1");
			// ORDERLESS_MATCHER.definePatternHashRule(Power(Cosh(x_), C2), Times(CN1,
			// Power(Sinh(x_), C2)), C1);
			super.setUp(newSymbol);
		}

	}

	/**
	 * <pre>
	 * Pochhammer(a, n)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the pochhammer symbol for a rational number <code>a</code> and an integer number <code>n</code>.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:<br />
	 * </p>
	 * <ul>
	 * <li><a href="http://en.wikipedia.org/wiki/Pochhammer_symbol">Wikipedia - Pochhammer symbol</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Pochhammer(4, 8)
	 * 6652800
	 * </pre>
	 */
	private final static class Pochhammer extends AbstractArg2 {// implements PochhammerRules {

		@Override
		public IExpr e2ObjArg(final IExpr a, final IExpr n) {
			if (n.isZero()) {
				return F.C1;
			}
			if (n.isOne()) {
				return a;
			}
			int ni = n.toIntDefault(Integer.MIN_VALUE);
			if (a.isRational() && ni > Integer.MIN_VALUE) {
				BigFraction bf = ((IRational) a).toBigFraction();
				BigFraction ph = pochhammer(bf, ni);
				if (ph != null) {
					return F.fraction(ph);
				}
			}
			if (a.isInteger() && a.isPositive()) {
				IExpr temp = EvalEngine.get().evaluate(F.Plus(((IInteger) a).subtract(F.C1), n));
				if (temp.isSymbol()) {
					return F.Divide(F.Factorial(temp), F.Gamma(a));
				}
			}

			if (n.isInteger()) {

				if (ni > Integer.MIN_VALUE) {
					if (ni > 0) {
						// Product(a + k, {k, 0, n - 1})
						return F.product(k -> F.Plus(a, k), 0, ni - 1);
					}
					if (ni < 0) {
						// Product(1/(a - k), {k, 1, -n})
						return Power(F.product(k -> F.Plus(a, k.negate()), 1, -ni), -1);
					}
				}
			}
			if (a.isNumber() && n.isNumber()) {
				return F.Divide(F.Gamma(F.Plus(a, n)), F.Gamma(a));
			}
			return F.NIL;
		}

		/**
		 * Compute Pochhammer's symbol (that)_n.
		 * 
		 * @param that
		 * @param n
		 *            The number of product terms in the evaluation.
		 * @return Gamma(that+n)/Gamma(that) = that*(that+1)*...*(that+n-1).
		 */
		public static BigFraction pochhammer(BigFraction that, final int n) {
			if (n < 0) {
				BigFraction res = BigFraction.ONE;
				for (int i = (-1); i >= n; i--) {
					res = res.multiply(that.add(i));
				}
				return res.reciprocal();
			} else if (n == 0) {
				return BigFraction.ONE;
			} else {
				if (that.equals(BigFraction.ZERO)) {
					return BigFraction.ZERO;
				}
				BigFraction res = that;
				for (int i = 1; i < n; i++) {
					res = res.multiply(that.add(i));
				}
				return res;
			}
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		}
	}

	/**
	 * <pre>
	 * Power(a, b)  
	 * 
	 * a ^ b
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * represents <code>a</code> raised to the power of <code>b</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; 4 ^ (1/2)
	 * 2
	 * 
	 * &gt;&gt; 4 ^ (1/3)
	 * 4^(1/3)
	 * 
	 * &gt;&gt; 3^123
	 * 48519278097689642681155855396759336072749841943521979872827
	 * 
	 * &gt;&gt; (y ^ 2) ^ (1/2)
	 * Sqrt(y^2)
	 * 
	 * &gt;&gt; (y ^ 2) ^ 3
	 * y^6
	 * </pre>
	 * <p>
	 * Use a decimal point to force numeric evaluation:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; 4.0 ^ (1/3)
	 * 1.5874010519681994
	 * </pre>
	 * <p>
	 * <code>Power</code> has default value <code>1</code> for its second argument:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; a /. x_ ^ n_. :&gt; {x, n}
	 * {a,1}
	 * </pre>
	 * <p>
	 * <code>Power</code> can be used with complex numbers:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; (1.5 + 1.0*I) ^ 3.5
	 * -3.682940057821917+I*6.951392664028508
	 * 
	 * &gt;&gt; (1.5 + 1.0*I) ^ (3.5 + 1.5*I)
	 * -3.1918162904562815+I*0.6456585094161581
	 * </pre>
	 * <p>
	 * Infinite expression 0^(negative number)
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; 1/0 
	 * ComplexInfinity
	 * 
	 * &gt;&gt; 0 ^ -2
	 * ComplexInfinity
	 * 
	 * &gt;&gt; 0 ^ (-1/2)
	 * ComplexInfinity
	 * 
	 * &gt;&gt; 0 ^ -Pi
	 * ComplexInfinity
	 * </pre>
	 * <p>
	 * Indeterminate expression 0 ^ (complex number) encountered.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; 0 ^ (2*I*E)
	 * Indeterminate
	 * 
	 * &gt;&gt; 0 ^ - (Pi + 2*E*I)
	 * ComplexInfinity
	 * </pre>
	 * <p>
	 * Indeterminate expression 0 ^ 0 encountered.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; 0 ^ 0
	 * 
	 * &gt;&gt; Sqrt(-3+2.*I)
	 * 0.5502505227003375+I*1.8173540210239707
	 * 
	 * &gt;&gt; Sqrt(-3+2*I)
	 * Sqrt(-3+I*2) 
	 * 
	 * &gt;&gt; (3/2+1/2I)^2
	 * 2+I*3/2
	 * 
	 * &gt;&gt; I ^ I
	 * I^I
	 * 
	 * &gt;&gt; 2 ^ 2.0
	 * 4.0
	 * 
	 * &gt;&gt; Pi ^ 4.
	 * 97.40909103400242
	 * 
	 * &gt;&gt; a ^ b
	 * a^b
	 * </pre>
	 */
	public static class Power extends AbstractArg2 implements INumeric, PowerRules {

		/**
		 * <p>
		 * Calculate <code>interval({lower, upper}) ^ exponent</code>.
		 * </p>
		 * <p>
		 * See: <a href= "https://de.wikipedia.org/wiki/Intervallarithmetik#Elementare_Funktionen"> Intervallarithmetik
		 * - Elementare Funktionen</a>
		 * </p>
		 * 
		 * @param interval
		 * @param exponent
		 * @return
		 */
		private static IExpr powerInterval(final IExpr interval, IInteger exponent) {
			if (exponent.isNegative()) {
				if (exponent.isMinusOne()) {
					// TODO implement division
					return F.NIL;
				}
				return F.Power(powerIntervalPositiveExponent(interval, exponent.negate()), F.CN1);
			}
			return powerIntervalPositiveExponent(interval, exponent);
		}

		private static IExpr powerIntervalPositiveExponent(final IExpr interval, IInteger exponent) {
			if (exponent.isEven()) {
				if (!interval.lower().isNegativeResult()) {
					return F.Interval(F.List(interval.lower().power(exponent), interval.upper().power(exponent)));
				} else {
					if (interval.upper().isNegativeResult()) {
						return F.Interval(F.List(interval.upper().power(exponent), interval.lower().power(exponent)));
					}
					return F.Interval(
							F.List(F.C0, F.Max(interval.lower().power(exponent), interval.upper().power(exponent))));
				}
			}
			return F.Interval(F.List(interval.lower().power(exponent), interval.upper().power(exponent)));
		}

		/**
		 * Split this integer into the nth-root (with prime factors less equal 1021) and the &quot;rest factor&quot;
		 * 
		 * @return <code>{nth-root, rest factor}</code> or <code>null</code> if the root is not available
		 */
		private IInteger[] calculateRoot(IInteger a, IInteger root) {
			if (a.isOne() || a.isMinusOne()) {
				return null;
			}
			int n = root.toIntDefault(Integer.MIN_VALUE);
			if (n > 0) {
				IInteger[] result = a.nthRootSplit(n);
				if (result[1].equals(a)) {
					// no roots found
					return null;
				}
				return result;
			}
			return null;
		}

		@Override
		public IExpr e2ApcomplexArg(final ApcomplexNum base, final ApcomplexNum exponent) {
			return base.pow(exponent);
		}

		@Override
		public IExpr e2ApfloatArg(final ApfloatNum base, final ApfloatNum exponent) {
			return base.pow(exponent);
		}

		@Override
		public IExpr e2ComArg(final IComplex base, final IComplex exponent) {
			return F.NIL;
		}

		@Override
		public IExpr e2DblArg(final INum base, final INum exponent) {
			if (base.isZero()) {
				if (exponent.isNegative()) {
					EvalEngine.get().printMessage("Infinite expression 0^(negative number)");
					return F.CComplexInfinity;
				}
				if (exponent.isZero()) {
					// 0^0
					EvalEngine.get().printMessage("Infinite expression 0^0");
					return F.Indeterminate;
				}
			}
			if (exponent.isMinusOne()) {
				return base.inverse();
			}
			if (exponent.isNumIntValue()) {
				return base.pow(exponent);
			}
			if (base.isNegative()) {
				return F.complexNum(base.doubleValue()).pow(F.complexNum(exponent.doubleValue()));
			}
			return base.pow(exponent);
		}

		@Override
		public IExpr e2DblComArg(final IComplexNum base, final IComplexNum exponent) {
			return base.pow(exponent);
		}

		@Override
		public IExpr e2FraArg(IFraction base, IFraction exponent) {
			if (base.numerator().isZero()) {
				return F.C0;
			}

			if (exponent.numerator().isZero()) {
				return F.C1;
			}

			if (base.numerator().isOne() && !base.denominator().isOne()) {
				return F.Power(base.denominator(), exponent.negate());
			}

			// if (exponent.equals(F.C1D2)) {
			// if (base.isNegative()) {
			// done in e2ObjArg
			// return F.Times(F.CI, F.Power(base.negate(), exponent));
			// }
			// }

			if (exponent.equals(F.CN1D2)) {
				if (base.isNegative()) {
					return F.Times(F.CNI, F.Power(base.negate().inverse(), exponent.negate()));
				}
			}

			if (exponent.isNegative() && !base.denominator().isOne()) {
				return F.Power(base.inverse(), exponent.negate());
			}

			if (!exponent.denominator().isOne()) {
				IExpr temp = rationalPower(base.numerator(), base.denominator(), exponent);
				if (temp.isPresent()) {
					return temp;
				}

				IInteger a;
				IInteger b;
				IFraction f0Temp = base;
				if (base.sign() < 0) {
					f0Temp = f0Temp.negate();
				}
				if (exponent.isNegative()) {
					a = f0Temp.denominator();
					b = f0Temp.numerator();
				} else {
					a = f0Temp.numerator();
					b = f0Temp.denominator();
				}

				// example: (-27)^(2/3) or 8^(1/3)
				if (!exponent.numerator().isOne()) {
					try {
						int exp = exponent.numerator().toInt();
						if (exp < 0) {
							exp *= (-1);
						}
						a = a.pow(exp);
						b = b.pow(exp);
					} catch (ArithmeticException e) {
						return F.NIL;
					}
				}

				final IInteger root = exponent.denominator();
				IInteger[] new_numer = calculateRoot(a, root);
				IInteger[] new_denom = calculateRoot(b, root);
				final IFraction new_root = F.fraction(C1, root);

				if (new_numer != null) {
					if (new_denom != null) {
						IRational p0 = null;
						if (new_denom[1].isOne()) {
							p0 = new_numer[1];
						} else {
							p0 = fraction(new_numer[1], new_denom[1]);
						}
						if (base.sign() < 0) {
							return Times(fraction(new_numer[0], new_denom[0]), Power(p0.negate(), new_root));
						}
						return Times(fraction(new_numer[0], new_denom[0]), Power(p0, new_root));
					} else {
						if (a.isOne()) {
							return F.NIL;
						}
						IRational p0 = null;
						if (b.isOne()) {
							p0 = new_numer[1];
						} else {
							p0 = fraction(new_numer[1], b);
						}
						if (base.sign() < 0) {
							return Times(new_numer[0], Power(p0.negate(), new_root));
						}
						return Times(new_numer[0], Power(p0, new_root));
					}
				} else {
					if (new_denom != null) {
						if (b.isOne()) {
							return F.NIL;
						}
						IRational p0 = null;
						if (new_denom[1].isOne()) {
							p0 = a;
						} else {
							p0 = F.fraction(a, new_denom[1]);
						}
						if (base.sign() < 0) {
							return Times(fraction(C1, new_denom[0]), Power(p0.negate(), new_root));
						}
						return Times(fraction(C1, new_denom[0]), Power(p0, new_root));
					}
				}

				return F.NIL;
			}
			return base.power(exponent);
		}

		@Override
		public IExpr e2IntArg(final IInteger base, final IInteger exponent) {
			if (base.isZero()) {
				// all other cases see e2ObjArg
				return F.NIL;
			}

			try {
				long n = exponent.toLong();
				return base.power(n);
			} catch (ArithmeticException ae) {

			}
			return F.NIL;
		}

		/**
		 * 
		 * @param arg1
		 *            a number
		 * @param arg2
		 *            must be a <code>DirectedInfinity[...]</code> expression
		 * @return
		 */
		private IExpr e2NumberDirectedInfinity(final INumber arg1, final IAST arg2) {
			int comp = arg1.compareAbsValueToOne();
			switch (comp) {
			case 1:
				// Abs(arg1) > 1
				if (arg2.isInfinity()) {
					// arg1 ^ Inf
					if (arg1.isReal() && arg1.isPositive()) {
						return F.CInfinity;
					}
					// complex or negative numbers
					return F.CComplexInfinity;
				}
				if (arg2.isNegativeInfinity()) {
					// arg1 ^ (-Inf)
					return F.C0;
				}
				break;
			case -1:
				// Abs(arg1) < 1
				if (arg2.isInfinity()) {
					// arg1 ^ Inf
					return F.C0;
				}
				if (arg2.isNegativeInfinity()) {
					// arg1 ^ (-Inf)
					if (arg1.isReal() && arg1.isPositive()) {
						return F.CInfinity;
					}
					// complex or negative numbers
					return F.CComplexInfinity;
				}
				break;
			}
			return F.NIL;
		}

		@Override
		public IExpr e2ObjArg(final IExpr base, final IExpr exponent) {
			if (exponent.isDirectedInfinity()) {
				IExpr temp = evalDirectedInfinityArg2(base, (IAST) exponent);
				if (temp.isPresent()) {
					return temp;
				}
			}
			if (base.isDirectedInfinity()) {
				IExpr temp = evalDirectedInfinityArg1((IAST) base, exponent);
				if (temp.isPresent()) {
					return temp;
				}
			}
			if (base.isZero()) {
				return powerZeroArg1(exponent);
			}
			if (base.isInterval1()) {
				if (exponent.isInteger()) {
					IInteger ii = (IInteger) exponent;
					return powerInterval(base, ii);
				}
			} else if (base instanceof ASTSeriesData) {
				int exp = exponent.toIntDefault(Integer.MIN_VALUE);
				if (exp != Integer.MIN_VALUE) {
					return ((ASTSeriesData) base).pow(exp);
				}
			}

			if (exponent.isZero()) {
				return (base.isInfinity() || base.isNegativeInfinity()) ? F.Indeterminate : F.C1;
			}

			if (exponent.isOne()) {
				return base;
			}

			if (base.isOne()) {
				return F.C1;
			}

			if (base.isMinusOne() && exponent.isInteger()) {
				return (((IInteger) exponent).isEven()) ? F.C1 : F.CN1;
			}

			if (exponent.isReal()) {
				if (base.isPower()) {
					final IExpr baseArg1 = base.base();
					final IExpr exponentArg1 = base.exponent();
					if (exponentArg1.isReal() && baseArg1.isNonNegativeResult()) {
						// (base ^ exponent) ^ arg2 ==> base ^ (exponent * arg2)
						return F.Power(baseArg1, exponentArg1.times(exponent));
					}
				}
				if (base.isComplex() && exponent.isFraction() && exponent.isPositive()) {
					IExpr temp = powerComplexFraction((IComplex) base, (IFraction) exponent);
					if (temp.isPresent()) {
						return temp;
					}
				}
				ISignedNumber is1 = (ISignedNumber) exponent;
				if (base.isInfinity()) {
					if (is1.isNegative()) {
						return F.C0;
					} else {
						return F.CInfinity;
					}
				} else if (base.isPower() && is1.isNumIntValue() && is1.isPositive()) {
					final IExpr baseExponent = base.exponent();
					if (baseExponent.isNumIntValue() && baseExponent.isPositive()) {
						// (x*n)^m => x ^(n*m)
						return Power(base.base(), is1.times(baseExponent));
					}
				} else if (base.isNegativeInfinity()) {
					if (exponent.isInteger()) {
						IInteger ii = (IInteger) exponent;
						if (ii.isNegative()) {
							return F.C0;
						} else {
							if (ii.isOdd()) {
								return F.CNInfinity;
							} else {
								return F.CInfinity;
							}
						}
					} else {
						int exp = exponent.toIntDefault(Integer.MIN_VALUE);
						if (exp != Integer.MIN_VALUE) {
							if (exp < 0) {
								return F.C0;
							} else {
								if ((exp & 0x1) == 0x1) {
									return F.CNInfinity;
								} else {
									return F.CInfinity;
								}
							}
						}
					}
				}
				if (exponent.isMinusOne() || exponent.isInteger()) {
					if (base.isNumber()) {
						if (exponent.isMinusOne()) {
							return ((INumber) base).inverse();
						}
						try {
							long n = ((IInteger) exponent).toLong();
							return ((INumber) base).power(n);
						} catch (ArithmeticException ae) {

						}
					} else {
						IExpr o1negExpr = F.NIL;
						if (exponent.isInteger() && ((IInteger) exponent).isEven()) {
							o1negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(base, true);
						} else {
							o1negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(base, false);
						}
						if (o1negExpr.isPresent()) {
							if (exponent.isMinusOne()) {
								return Times(CN1, Power(o1negExpr, CN1));
							} else {
								IInteger ii = (IInteger) exponent;
								if (ii.isEven()) {
									return Power(o1negExpr, exponent);
								}
							}
						}
						if (exponent.isMinusOne() && base.isTimes()) {
							IExpr temp = powerTimesInverse((IAST) base, (ISignedNumber) exponent);
							if (temp.isPresent()) {
								return temp;
							}
						}
					}
				}
			}

			if (base.isReal() && base.isNegative() && exponent.isNumEqualRational(F.C1D2)) {
				// extract I for sqrt
				return F.Times(F.CI, F.Power(F.Negate(base), exponent));
			}

			if (base.isE() && (exponent.isPlusTimesPower())) {
				IExpr expandedFunction = F.evalExpand(exponent);
				if (expandedFunction.isPlus()) {
					return powerEPlus((IAST) expandedFunction);
				}
			}

			if (base.isAST()) {
				IAST astArg1 = (IAST) base;
				if (astArg1.isTimes()) {
					if (exponent.isInteger() || exponent.isMinusOne()) {
						// (a * b * c)^n => a^n * b^n * c^n
						return astArg1.mapThread(Power(null, exponent), 1);
					}
					if (exponent.isNumber()) {
						final IAST f0 = astArg1;

						if ((f0.size() > 1) && (f0.arg1().isNumber())) {
							return Times(Power(f0.arg1(), exponent),
									Power(F.ast(f0, F.Times, true, 2, f0.size()), exponent));
						}
					}
				} else if (astArg1.isPower()) {
					if (astArg1.arg2().isReal() && exponent.isReal()) {
						IExpr temp = astArg1.arg2().times(exponent);
						if (temp.isOne()) {
							if (astArg1.arg1().isNonNegativeResult()) {
								return astArg1.arg1();
							}
							if (astArg1.arg1().isRealResult()) {
								return F.Abs(astArg1.arg1());
							}
						}
					}
					if (exponent.isInteger()) {
						// (a ^ b )^n => a ^ (b * n)
						if (astArg1.arg2().isNumber()) {
							return F.Power(astArg1.arg1(), exponent.times(astArg1.arg2()));
						}
						return F.Power(astArg1.arg1(), F.Times(exponent, astArg1.arg2()));
					}
				}
			}
			return F.NIL;
		}

		public IExpr evalDirectedInfinityArg1(final IAST arg1, final IExpr arg2) {
			if (arg2.isZero()) {
				return F.Indeterminate;
			}
			if (arg1.isComplexInfinity()) {
				if (arg2.isReal()) {
					if (arg2.isNegative()) {
						return F.C0;
					}
					return F.CComplexInfinity;
				}
				return F.Indeterminate;
			}
			if (arg2.isOne()) {
				return arg1;
			}
			if (arg2.isMinusOne()) {
				return F.C0;
			}
			return F.NIL;
		}

		public IExpr evalDirectedInfinityArg2(final IExpr arg1, final IAST arg2) {
			if (arg2.isComplexInfinity()) {
				return F.Indeterminate;
			}

			if (arg1.isOne() || arg1.isMinusOne() || arg1.isImaginaryUnit() || arg1.isNegativeImaginaryUnit()) {
				return F.Indeterminate;
			}
			IAST directedInfinity = (IAST) arg2;
			if (arg1.isZero()) {
				if (directedInfinity.isInfinity()) {
					// 0 ^ Inf
					return F.C0;
				}
				if (directedInfinity.isNegativeInfinity()) {
					// 0 ^ (-Inf)
					return F.CComplexInfinity;
				}
				return F.Indeterminate;
			}
			if (arg1.isInfinity()) {
				if (directedInfinity.isInfinity()) {
					// Inf ^ Inf
					return F.CComplexInfinity;
				}
				if (directedInfinity.isNegativeInfinity()) {
					// Inf ^ (-Inf)
					return F.C0;
				}
				return F.Indeterminate;
			}
			if (arg1.isNegativeInfinity()) {
				if (directedInfinity.isInfinity()) {
					// (-Inf) ^ Inf
					return F.CComplexInfinity;
				}
				if (directedInfinity.isNegativeInfinity()) {
					// (-Inf) ^ (-Inf)
					return F.C0;
				}
				return F.Indeterminate;
			}
			if (arg1.isComplexInfinity()) {
				if (directedInfinity.isInfinity()) {
					// ComplexInfinity ^ Inf
					return F.CComplexInfinity;
				}
				if (directedInfinity.isNegativeInfinity()) {
					// ComplexInfinity ^ (-Inf)
					return F.C0;
				}
				return F.Indeterminate;
			}
			if (arg1.isDirectedInfinity()) {
				if (directedInfinity.isInfinity()) {
					return F.CComplexInfinity;
				}
				if (directedInfinity.isNegativeInfinity()) {
					return F.C0;
				}
				return F.Indeterminate;
			}

			if (arg1.isNumber()) {
				IExpr temp = e2NumberDirectedInfinity((INumber) arg1, directedInfinity);
				if (temp.isPresent()) {
					return temp;
				}
			} else {
				IExpr a1 = F.evaln(arg1);
				if (a1.isNumber()) {
					IExpr temp = e2NumberDirectedInfinity((INumber) a1, directedInfinity);
					if (temp.isPresent()) {
						return temp;
					}
				}
			}
			return F.NIL;
		}

		/**
		 * Transform <code>Power(Times(a,b,c,Power(d,-1.0)....), -1.0)</code> to
		 * <code>Times(a^(-1.0),b^(-1.0),c^(-1.0),d,....)</code>
		 * 
		 * @param timesAST
		 *            a <code>Times(...)</code> expression
		 * @param arg2
		 *            equals <code>-1</code> or <code>-1.0</code>
		 * @return <code>F.NIL</code> if the transformation isn't possible.
		 */
		private static IExpr powerTimesInverse(final IAST timesAST, final ISignedNumber arg2) {
			IASTAppendable resultAST = F.NIL;
			for (int i = 1; i < timesAST.size(); i++) {
				IExpr temp = timesAST.get(i);
				if (temp.isPower() && temp.exponent().isReal()) {
					if (!resultAST.isPresent()) {
						resultAST = timesAST.copyAppendable();
						resultAST.map(resultAST, x -> F.Power(x, arg2));
					}
					if (temp.exponent().isMinusOne()) {
						resultAST.set(i, temp.base());
					} else {
						resultAST.set(i, F.Power(temp.base(), temp.exponent().multiply(arg2)));
					}
				}
			}
			return resultAST;
		}

		/**
		 * Determine <code>0 ^ exponent</code>.
		 * 
		 * @param exponent
		 *            the exponent of the 0-Power expression
		 * @return
		 */
		private IExpr powerZeroArg1(final IExpr exponent) {
			EvalEngine engine = EvalEngine.get();
			if (exponent.isZero()) {
				// 0^0
				// TODO add a real log message
				// throw new DivisionByZero("0^0");
				engine.printMessage("Infinite expression 0^0");
				return F.Indeterminate;
			}

			IExpr a = exponent.re();
			if (a.isReal()) {
				if (a.isNegative()) {
					engine.printMessage("Infinite expression 0^(negative number)");
					return F.CComplexInfinity;
				}
				if (a.isZero()) {
					engine.printMessage("Infinite expression 0^0.");
					return F.Indeterminate;
				}
				return F.C0;
			}
			if (a.isNumericFunction()) {
				IExpr temp = engine.evalN(a);
				if (temp.isReal()) {
					if (((ISignedNumber) temp).isNegative()) {
						engine.printMessage("Infinite expression 0^(negative number)");
						return F.CComplexInfinity;
					}
					if (temp.isZero()) {
						engine.printMessage("Infinite expression 0^0.");
						return F.Indeterminate;
					}
					return F.C0;
				}
				if (temp.isComplex() || temp.isComplexNumeric()) {
					engine.printMessage("Indeterminate expression 0 ^ (complex number) encountered.");
					return F.Indeterminate;
				}
			}

			return F.NIL;
		}

		/**
		 * Simplify <code>E^(y+Log(x))</code> to <code>x*E^(y)</code>
		 * 
		 * @param plus
		 * @return
		 */
		private IAST powerEPlus(IAST plus) {
			IASTAppendable multiplicationFactors = F.NIL;
			IASTAppendable plusClone = F.NIL;
			for (int i = plus.argSize(); i > 0; i--) {
				IExpr temp = plus.get(i);
				if (temp.isLog()) {
					if (!multiplicationFactors.isPresent()) {
						multiplicationFactors = F.TimesAlloc(8);
						plusClone = plus.copyAppendable();
					}
					multiplicationFactors.append(temp.first());
					plusClone.remove(i);
				} else if (temp.isTimes() && temp.size() == 3 && temp.second().isLog() && temp.first().isReal()) {
					IAST times = (IAST) temp;
					IExpr logArgument = times.arg2().first();
					if (!multiplicationFactors.isPresent()) {
						multiplicationFactors = F.TimesAlloc(8);
						plusClone = plus.copyAppendable();
					}
					// logArgument ^ times.arg1()
					multiplicationFactors.append(F.Power(logArgument, times.arg1()));
					plusClone.remove(i);
				}
			}
			if (multiplicationFactors.isPresent()) {
				multiplicationFactors.append(F.Exp(plusClone));
				return multiplicationFactors;
			}
			return F.NIL;
		}

		/**
		 * <code> complexNumber ^ fractionNumber</code>
		 * 
		 * @param complexNumber
		 * @param fractionNumber
		 * @return
		 */
		private IExpr powerComplexFraction(final IComplex complexNumber, final IFraction fractionNumber) {
			if (complexNumber.isImaginaryUnit()) {
				return F.Power(F.CN1, F.C1D2.times(fractionNumber));
			} else if (complexNumber.isNegativeImaginaryUnit()) {
				IInteger numerator = fractionNumber.numerator();
				IInteger denominator = fractionNumber.denominator();
				IInteger div = numerator.div(denominator);
				if (div.isOdd()) {
					div = div.subtract(F.C1);
				}
				IRational rat = fractionNumber.subtract(div);
				numerator = rat.numerator();
				denominator = rat.denominator().multiply(F.C2);
				return F.Times(F.CN1, F.Power(F.CNI, div),
						F.Power(F.CN1, F.fraction(denominator.subtract(numerator), denominator)));
			}
			return F.NIL;
		}

		@Override
		public IExpr eComFraArg(final IComplex base, final IFraction exponent) {
			if (exponent.equals(F.C1D2) && base.getRealPart().isZero()) {
				// square root of pure imaginary number
				IRational im = base.getImaginaryPart();
				boolean negative = false;
				im = im.divideBy(F.C2);
				if (im.isNegative()) {
					im = im.negate();
					negative = true;
				}
				if (NumberUtil.isPerfectSquare(im)) {
					IExpr temp = F.Sqrt(im);
					if (negative) {
						// Sqrt(im.negate()) - I * Sqrt(im);
						return F.Plus(temp, F.Times(F.CNI, temp));
					}
					// Sqrt(im.negate()) + I * Sqrt(im);
					return F.Plus(temp, F.Times(F.CI, temp));
				}
			}
			return F.NIL;
		}

		@Override
		public IExpr eComIntArg(final IComplex base, final IInteger exponent) {
			if (base.isZero()) {
				return F.C0;
			}

			if (exponent.isZero()) {
				return F.C1;
			}

			return base.pow(exponent.toBigNumerator().intValue());
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 2) {
				throw new UnsupportedOperationException();
			}
			return Math.pow(stack[top - 1], stack[top]);
		}

		@Override
		public IAST getRuleAST() {
			return RULES;
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			int size = ast.size();
			if (size == 1) {
				return F.C1;
			}
			if (size == 2 && ast.head() == F.Power) {
				return ast.arg1();
			}
			return super.evaluate(ast, engine);
		}

		/** {@inheritDoc} */
		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.ONEIDENTITY | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}
	}

	private final static class Precision extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() == 2) {
				IExpr arg1 = engine.evaluate(ast.arg1());
				if (arg1 instanceof INum) {
					return F.integer(((INum) arg1).precision());
				}
				if (arg1 instanceof IComplexNum) {
					return F.integer(((IComplexNum) arg1).precision());
				}
				engine.printMessage("Precision: Numeric expression expected");
				return F.NIL;
			}
			return Validate.checkSize(ast, 2);
		}
	}

	/**
	 * <pre>
	 * PreDecrement(x)
	 * 
	 * --x
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * decrements <code>x</code> by <code>1</code>, returning the new value of <code>x</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * <p>
	 * <code>--a</code> is equivalent to <code>a = a - 1</code>:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; a = 2   
	 * &gt;&gt; --a    
	 * 1
	 * 
	 * &gt;&gt; a    
	 * 1
	 * </pre>
	 */
	private static class PreDecrement extends Decrement {

		@Override
		protected IASTMutable getAST() {
			return (IASTMutable) F.Plus(null, F.CN1);
		}

		@Override
		protected IExpr getResult(IExpr symbolValue, IExpr calculatedResult) {
			return calculatedResult;
		}

		@Override
		protected ISymbol getFunctionSymbol() {
			return F.PreDecrement;
		}
	}

	/**
	 * <pre>
	 * PreIncrement(x)
	 * 
	 * ++x
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * increments <code>x</code> by <code>1</code>, returning the new value of <code>x</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * <p>
	 * <code>++a</code> is equivalent to <code>a = a + 1</code>:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; a = 2   
	 * &gt;&gt; ++a    
	 * 3    
	 * 
	 * &gt;&gt; a    
	 * 3
	 * </pre>
	 */
	private static class PreIncrement extends PreDecrement {

		@Override
		protected IASTMutable getAST() {
			return (IASTMutable) F.Plus(null, F.C1);
		}

		@Override
		protected ISymbol getFunctionSymbol() {
			return F.PreIncrement;
		}

	}

	/**
	 * <pre>
	 * Rational
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * is the head of rational numbers.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * Rational(a, b)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * constructs the rational number <code>a / b</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Head(1/2)
	 * Rational
	 * 
	 * &gt;&gt; Rational(1, 2)
	 * 1/2
	 * 
	 * &gt;&gt; -2/3
	 * -2/3
	 * </pre>
	 */

	private final static class Rational extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			try {
				// try to convert into a fractional number
				IExpr numeratorExpr = ast.arg1();
				IExpr denominatorExpr = ast.arg2();
				if (numeratorExpr.isInteger() && denominatorExpr.isInteger()) {
					// already evaluated
				} else {
					numeratorExpr = engine.evaluate(numeratorExpr);
					denominatorExpr = engine.evaluate(denominatorExpr);
					if (!numeratorExpr.isInteger() || !denominatorExpr.isInteger()) {
						return F.NIL;
					}
				}

				// symbolic mode
				IInteger numerator = (IInteger) numeratorExpr;
				IInteger denominator = (IInteger) denominatorExpr;
				if (denominator.isZero()) {
					engine.printMessage(
							"Division by zero expression: " + numerator.toString() + "/" + denominator.toString());
					if (numerator.isZero()) {
						// 0^0
						return F.Indeterminate;
					}
					return F.CComplexInfinity;
				}
				if (numerator.isZero()) {
					return F.C0;
				}
				return F.fraction(numerator, denominator);

				// don't evaluate in numeric mode
				// } else if (numeratorExpr instanceof INum && denominatorExpr instanceof INum) {
				// INum numerator = (INum) numeratorExpr;
				// INum denominator = (INum) denominatorExpr;
				// if (denominator.isZero()) {
				// engine.printMessage(
				// "Division by zero expression: " + numerator.toString() + "/" + denominator.toString());
				// if (numerator.isZero()) {
				// // 0^0
				// return F.Indeterminate;
				// }
				// return F.CComplexInfinity;
				// }
				// if (numerator.isZero()) {
				// return F.C0;
				// }
				// return F.num(numerator.doubleValue() / denominator.doubleValue());

			} catch (Exception e) {
				if (Config.SHOW_STACKTRACE) {
					e.printStackTrace();
				}
			}

			return F.NIL;
		}

	}

	/**
	 * <pre>
	 * Re(z)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the real component of the complex number <code>z</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Re(3+4I)
	 * 3
	 * 
	 * &gt;&gt; Im(0.5 + 2.3*I)
	 * 2.3
	 * </pre>
	 */
	private final static class Re extends AbstractEvaluator {

		public static IExpr evalRe(IExpr expr, EvalEngine engine) {
			if (expr.isDirectedInfinity()) {
				IAST directedInfininty = (IAST) expr;
				if (directedInfininty.isComplexInfinity()) {
					return F.Indeterminate;
				}
				if (directedInfininty.isAST1()) {
					if (directedInfininty.isInfinity()) {
						return F.CInfinity;
					}
					IExpr re = directedInfininty.arg1().re();
					if (re.isNumber()) {
						if (re.isZero()) {
							return F.C0;
						}
						return F.Times(F.Sign(re), F.CInfinity);
					}
				}
			}
			if (expr.isNumber()) {
				return ((INumber) expr).re();
			}
			if (expr.isRealResult()) {
				return expr;
			}

			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(expr);
			if (negExpr.isPresent()) {
				return Negate(Re(negExpr));
			}
			if (expr.isTimes()) {
				IExpr first = expr.first();
				if (first.isReal()) {
					return F.Times(first, F.Re(expr.rest()));
				}
				if (first.isImaginaryUnit()) {
					// Re(I*temp) -> -Im(temp)
					return F.Times(F.CN1, F.Im(expr.rest()));
				}
			}
			if (expr.isPlus()) {
				return ((IAST) expr).mapThread((IAST) F.Re(null), 1);
			}
			if (expr.isPower()) {
				IExpr base = expr.base();
				if (base.isRealResult()) {
					// test for x^(a+I*b)
					IExpr exponent = expr.exponent();
					// if (exponent.isNumber()) {
					// // (x^2)^(a/2)*E^(-b*Arg[x])*Cos[a*Arg[x]+1/2*b*Log[x^2]]
					// return rePowerComplex(x, ((INumber) exponent).re(), ((INumber) exponent).im());
					// }
					// (x^2)^(a/2)*E^(-b*Arg[x])*Cos[a*Arg[x]+1/2*b*Log[x^2]]
					return rePowerComplex(x, exponent.re(), exponent.im());
				}
			}
			return F.NIL;
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			IExpr arg1 = ast.arg1();
			return evalRe(arg1, engine);
		}

		/**
		 * Evaluate <code>Re(x^(a+I*b))</code>
		 * 
		 * @param x
		 * @param a
		 *            the real part of the exponent
		 * @param b
		 *            the imaginary part of the exponent
		 * @return
		 */
		private static IExpr rePowerComplex(IExpr x, IExpr a, IExpr b) {
			if (x.isE()) {
				// Re(E^(a+I*b)) -> E^a*Cos[b]
				return Times(Power(F.E, a), Cos(b));
			}
			return Times(Times(Power(Power(x, C2), Times(C1D2, a)), Power(E, Times(Negate(b), Arg(x)))),
					Cos(Plus(Times(a, Arg(x)), Times(Times(C1D2, b), Log(Power(x, C2))))));
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		}

	}

	/**
	 * <pre>
	 * Sqrt(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the square root of <code>expr</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Sqrt(4)
	 * 2
	 * 
	 * &gt;&gt; Sqrt(5)
	 * Sqrt(5)
	 * 
	 * &gt;&gt; Sqrt(5) // N
	 * 2.23606797749979
	 * 
	 * &gt;&gt; Sqrt(a)^2
	 * a
	 * </pre>
	 * <p>
	 * Complex numbers:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Sqrt(-4)
	 * I*2
	 * 
	 * &gt;&gt; I == Sqrt(-1)
	 * True
	 * 
	 * &gt;&gt; N(Sqrt(2), 50)
	 * 1.41421356237309504880168872420969807856967187537694
	 * </pre>
	 */
	private static class Sqrt extends AbstractArg1 implements INumeric {

		@Override
		public IExpr e1ObjArg(final IExpr o) {
			return Power(o, F.C1D2);
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1) {
				throw new UnsupportedOperationException();
			}
			return Math.sqrt(stack[top]);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		}
	}

	/**
	 * <pre>
	 * Surd(expr, n)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the <code>n</code>-th root of <code>expr</code>. If the result is defined, it's a real value.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Surd(16.0,3)
	 * 2.51984
	 * </pre>
	 */
	private static class Surd extends AbstractArg2 implements INumeric {
		@Override
		public IExpr e2ApfloatArg(final ApfloatNum af0, final ApfloatNum af1) {
			if (af1.isZero()) {
				EvalEngine ee = EvalEngine.get();
				ee.printMessage("Surd(a,b) division by zero");
				return F.Indeterminate;
			}
			if (af0.isNegative()) {
				return af0.abs().pow(af1.inverse()).negate();
			}
			return af0.pow(af1.inverse());
		}

		@Override
		public IExpr e2DblArg(INum d0, INum d1) {
			double val = d0.doubleValue();
			double r = d1.doubleValue();
			double result = doubleSurd(val, r);
			if (Double.isNaN(result)) {
				return F.Indeterminate;
			}
			return F.num(result);
		}

		@Override
		public IExpr e2ObjArg(final IExpr base, final IExpr root) {
			if (base.isNumber() && root.isInteger()) {
				EvalEngine ee = EvalEngine.get();
				if (base.isComplex() || base.isComplexNumeric()) {
					ee.printMessage("Surd(a,b) - \"a\" should be a real value.");
					return F.NIL;
				}

				if (root.isZero()) {
					ee.printMessage("Surd(a,b) division by zero");
					return F.Indeterminate;
				}
				if (base.isNegative()) {
					if (((IInteger) root).isEven()) {
						ee.printMessage("Surd(a,b) is undefined for negative \"a\" and even \"b\"");
						return F.Indeterminate;
					}
					return F.Times(F.CN1, Power(base.negate(), ((IInteger) root).inverse()));
				}

				if (base.isMinusOne()) {
					return F.CN1;
				}
				return Power(base, ((IInteger) root).inverse());

			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NHOLDREST | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}

		@Override
		public double evalReal(double[] stack, int top, int size) {
			if (size != 2) {
				throw new UnsupportedOperationException();
			}
			return doubleSurd(stack[top - 1], stack[top]);
		}

		private static double doubleSurd(double val, double r) {
			if (r == 0.0d) {
				EvalEngine ee = EvalEngine.get();
				ee.printMessage("Surd(a,b) division by zero");
				return Double.NaN;
			}
			if (val < 0.0d) {
				double root = Math.floor(r);
				if (Double.isFinite(r) && Double.compare(r, root) == 0) {
					// integer type
					int iRoot = (int) root;
					if ((iRoot & 0x0001) == 0x0000) {
						EvalEngine ee = EvalEngine.get();
						ee.printMessage("Surd(a,b) - undefined for negative \"a\" and even \"b\" values");
						return Double.NaN;
					}
					return -Math.pow(Math.abs(val), 1.0d / r);
				}
				return Double.NaN;
			}
			return Math.pow(val, 1.0d / r);
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);
			IExpr base = ast.arg1();
			IExpr arg2 = engine.evaluateNonNumeric(ast.arg2());
			if (arg2.isNumber()) {
				if (arg2.isInteger()) {
					IInteger root = (IInteger) arg2;
					if (base.isNegative()) {
						if (root.isEven()) {
							// necessary for two double args etc
							engine.printMessage("Surd(a,b) - undefined for negative \"a\" and even \"b\" values");
							return F.Indeterminate;
						}
					}
				} else {
					engine.printMessage("Surd(a,b) - b should be an integer");
					return F.NIL;
				}
			}

			return binaryOperator(ast.arg1(), ast.arg2());
		}
	}

	/**
	 * <pre>
	 * SubtractFrom(x, dx)
	 * 
	 * x -= dx
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * is equivalent to <code>x = x - dx</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; a = 10
	 * &gt;&gt; a -= 2   
	 * 8    
	 * 
	 * &gt;&gt; a    
	 * 8
	 * </pre>
	 */
	private static class SubtractFrom extends AddTo {

		@Override
		protected IASTMutable getAST(final IExpr value) {
			return (IASTMutable) F.Plus(null, F.Negate(value));
		}

		@Override
		protected ISymbol getFunctionSymbol() {
			return F.SubtractFrom;
		}
	}

	/**
	 * <pre>
	 * Subtract(a, b)
	 * 
	 * a - b
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * represents the subtraction of <code>b</code> from <code>a</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; 5 - 3
	 * 2
	 * 
	 * &gt;&gt; a - b // FullForm
	 * "Plus(a, Times(-1, b))"
	 * 
	 * &gt;&gt; a - b - c
	 * a-b-c
	 * 
	 * &gt;&gt; a - (b - c)
	 * a-b+c
	 * </pre>
	 */
	private static class Subtract extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);
			// arg1 + (-1)*arg2
			return F.Subtract(ast.arg1(), ast.arg2());
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	/**
	 * <pre>
	 * Times(a, b, ...)
	 * 
	 * a * b * ...
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * represents the product of the terms <code>a, b, ...</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; 10*2   
	 * 20     
	 * 
	 * &gt;&gt; a * a   
	 * a^2 
	 * 
	 * &gt;&gt; x ^ 10 * x ^ -2   
	 * x^8
	 * 
	 * &gt;&gt; {1, 2, 3} * 4   
	 * {4,8,12}  
	 * 
	 * &gt;&gt; Times @@ {1, 2, 3, 4}   
	 * 24   
	 * 
	 * &gt;&gt; IntegerLength(Times@@Range(100))  
	 * 158
	 * </pre>
	 * <p>
	 * <code>Times</code> has default value <code>1</code>:<br />
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; a /. n_. * x_ :&gt; {n, x}   
	 * {1,a}   
	 * 
	 * &gt;&gt; -a*b // FullForm   
	 * "Times(-1, a, b)" 
	 * 
	 * &gt;&gt; -(x - 2/3)   
	 * 2/3-x   
	 * 
	 * &gt;&gt; -x*2   
	 * -2 x  
	 * 
	 * &gt;&gt; -(h/2) // FullForm   
	 * "Times(Rational(-1,2), h)"  
	 * 
	 * &gt;&gt; x / x   
	 * 1   
	 * 
	 * &gt;&gt; 2*x^2 / x^2   
	 * 2   
	 * 
	 * &gt;&gt; 3.*Pi   
	 * 9.42477796076938
	 * 
	 * &gt;&gt; Head(3 * I)   
	 * Complex   
	 * 
	 * &gt;&gt; Head(Times(I, 1/2))   
	 * Complex   
	 * 
	 * &gt;&gt; Head(Pi * I)   
	 * Times   
	 * 
	 * &gt;&gt; -2.123456789 * x   
	 * -2.123456789*x
	 * 
	 * &gt;&gt; -2.123456789 * I   
	 * I*(-2.123456789)
	 * 
	 * &gt;&gt; N(Pi, 30) * I   
	 * I*3.14159265358979323846264338327  
	 * 
	 * &gt;&gt; N(I*Pi, 30)   
	 * I*3.14159265358979323846264338327 
	 * 
	 * &gt;&gt; N(Pi * E, 30)   
	 * 8.53973422267356706546355086954   
	 * 
	 * &gt;&gt; N(Pi, 30) * N(E, 30)   
	 * 8.53973422267356706546355086954   
	 * 
	 * &gt;&gt; N(Pi, 30) * E   
	 * 8.53973422267356649108017774746   
	 * 
	 * &gt;&gt; N(Pi, 30) * E // Precision   
	 * 30
	 * </pre>
	 */
	public static class Times extends AbstractArgMultiple implements INumeric {
		/**
		 * Constructor for the singleton
		 */
		public final static Times CONST = new Times();

		private static HashedOrderlessMatcherTimes TIMES_ORDERLESS_MATCHER = new HashedOrderlessMatcherTimes();

		private static IExpr eInfinity(IAST inf, IExpr o1) {
			if (inf.isComplexInfinity()) {
				if (o1.isZero()) {
					return F.Indeterminate;
				}
				return F.CComplexInfinity;
			}
			if (inf.isInfinity()) {
				if (o1.isInfinity()) {
					return F.CInfinity;
				}
				if (o1.isNegativeInfinity()) {
					return F.CNInfinity;
				}
				if (o1.isComplexInfinity()) {
					return F.CComplexInfinity;
				}
			}
			if (inf.isNegativeInfinity()) {
				if (o1.isInfinity()) {
					return F.CNInfinity;
				}
				if (o1.isNegativeInfinity()) {
					return F.CInfinity;
				}
				if (o1.isComplexInfinity()) {
					return F.CComplexInfinity;
				}
			}
			if (inf.isAST1()) {
				if (o1.isNumber() || o1.isSymbol()) {
					if (inf.isAST1()) {
						return DirectedInfinity.timesInf(inf, o1);
					}

				}
				if (o1.isDirectedInfinity() && o1.isAST1()) {
					return F.eval(F.DirectedInfinity(F.Times(inf.first(), o1.first())));
				}
			}
			return F.NIL;
		}

		public Times() {
		}

		// private void addTrigRules(ISymbol head1, ISymbol head2, ISymbol
		// resultHead) {
		// IAST sinX_ = F.unaryAST1(head1, x_);
		// IAST cotX_ = F.unaryAST1(head2, x_);
		// IAST sinX = F.unaryAST1(head1, x);
		// IAST cotX = F.unaryAST1(head2, x);
		// IAST resultX = F.unaryAST1(resultHead, x);
		// ORDERLESS_MATCHER.defineHashRule(sinX_, cotX_, resultX);
		// ORDERLESS_MATCHER.defineHashRule(sinX_, F.Power(cotX_, $p(n,
		// IntegerQ)),
		// F.Times(F.Power(cotX, F.Subtract(n, F.C1)), resultX), F.Positive(n));
		// ORDERLESS_MATCHER.defineHashRule(F.Power(sinX_, $p(m, IntegerQ)),
		// cotX_,
		// F.Times(F.Power(sinX, F.Subtract(m, F.C1)), resultX), F.Positive(m));
		// ORDERLESS_MATCHER.defineHashRule(F.Power(sinX_, $p(m, IntegerQ)),
		// F.Power(cotX_, $p(n, IntegerQ)),
		// F.If(F.Greater(m, n), F.Times(F.Power(sinX, F.Subtract(m, n)),
		// F.Power(resultX, n)),
		// F.Times(F.Power(cotX, F.Subtract(n, m)), F.Power(resultX, m))),
		// F.And(F.Positive(m), F.Positive(n)));
		// }

		/**
		 * Distribute a leading integer factor over the integer powers if available.
		 * <code>12*2^x*3^y   ==>   2^(2+x)*3^(1+y)</code>.
		 * 
		 * @param ast
		 *            the already evaluated expression
		 * @param originalExpr
		 *            the original expression which is used, if <code>!ast.isPresent()</code>
		 * @return the evaluated object or <code>ast</code>, if the distribution of an integer factor isn't possible
		 */
		private IExpr distributeLeadingFactor(IExpr ast, IAST originalExpr) {
			IExpr expr = ast;
			if (!expr.isPresent()) {
				expr = originalExpr;
			}
			if (expr.isTimes() && expr.first().isInteger()) {
				IAST times = (IAST) expr;
				IInteger leadingFactor = (IInteger) times.arg1();

				if (!leadingFactor.isMinusOne()) {
					IASTAppendable result = F.NIL;
					for (int i = 2; i < times.size(); i++) {
						IExpr temp = times.get(i);
						if (temp.isPower() && temp.base().isInteger() && !temp.exponent().isNumber()) {
							// IASTMutable power = (IASTMutable) temp;
							IInteger powArg1 = (IInteger) temp.base();
							if (powArg1.isPositive()) {
								IInteger mod = F.C0;
								int count = 0;
								while (!leadingFactor.isZero()) {
									mod = leadingFactor.mod(powArg1);
									if (mod.isZero()) {
										count++;
										leadingFactor = leadingFactor.div(powArg1);
									} else {
										break;
									}
								}
								if (count > 0) {
									if (!result.isPresent()) {
										result = times.copyAppendable();
									}
									IASTMutable power = ((IASTMutable) temp).copyAppendable();
									power.set(2, F.Plus(F.integer(count), power.exponent()));
									result.set(i, power);
								}
							}
						}
					}
					if (result.isPresent()) {
						result.set(1, leadingFactor);
						return result;
					}
				}

			}
			return ast;
		}

		@Override
		public IExpr e2ComArg(final IComplex c0, final IComplex c1) {
			return c0.multiply(c1);
		}

		@Override
		public IExpr e2DblArg(final INum d0, final INum d1) {
			return d0.multiply(d1);
		}

		@Override
		public IExpr e2DblComArg(final IComplexNum d0, final IComplexNum d1) {
			return d0.multiply(d1);
		}

		@Override
		public IExpr e2FraArg(final IFraction f0, final IFraction f1) {
			return f0.mul(f1);
		}

		@Override
		public IExpr e2IntArg(final IInteger i0, final IInteger i1) {
			return i0.multiply(i1);
		}

		@Override
		public IExpr e2ObjArg(final IExpr o0, final IExpr o1) {

			if (o0.isZero()) {
				if (o1.isDirectedInfinity()) {
					return F.Indeterminate;
				}
				return F.C0;
			}

			if (o1.isZero()) {
				if (o0.isDirectedInfinity()) {
					return F.Indeterminate;
				}
				return F.C0;
			}

			if (o0.isOne()) {
				return o1;
			}

			if (o1.isOne()) {
				return o0;
			}

			if (o0.equals(o1)) {
				if (o0.isNumber()) {
					return o0.times(o0);
				}
				return o0.power(F.C2);
			}

			IExpr temp = F.NIL;
			if (o0.isDirectedInfinity()) {
				temp = eInfinity((IAST) o0, o1);
			} else if (o1.isDirectedInfinity()) {
				temp = eInfinity((IAST) o1, o0);
			}
			if (temp.isPresent()) {
				return temp;
			}

			if (o0.isPower()) {
				// (x^a) * b
				IExpr power0Base = o0.base();
				IExpr power0Exponent = o0.exponent();
				if (o0.equalsAt(1, o1)) {
					// (x^a) * x
					if (power0Exponent.isNumber() && !o1.isRational()) {
						// avoid reevaluation of a root of a rational number (example: 2*Sqrt(2) )
						return o1.power(power0Exponent.inc());
					} else if (!power0Exponent.isNumber()) {
						return o1.power(power0Exponent.inc());
					}
				}

				if (o1.isPower()) {
					IExpr power1Base = o1.base();
					IExpr power1Exponent = o1.exponent();
					temp = timesPowerPower(power0Base, power0Exponent, power1Base, power1Exponent);
					if (temp.isPresent()) {
						return temp;
					}
				}
			} else if (o0.isInterval1()) {
				if (o1.isInterval1() || o1.isReal()) {
					return timesInterval(o0, o1);
				}
			} else if (o0.isNegative() && o1.isLog() && o1.first().isFraction() && o0.isReal()) {
				// -<number> * Log(<fraction>) -> <number> * Log(<fraction>.inverse())
				return o0.negate().times(F.Log(o1.first().inverse()));
			}

			// if (o1.isPlus()) {
			// final IAST f1 = (IAST) o1;
			// issue#128
			// if (o0.isMinusOne()) {
			// return f1.mapAt(F.Times(o0, null), 2);
			// }
			// if (o0.isInteger() && o1.isPlus() && o1.isAST2() && (((IAST)
			// o1).arg1().isNumericFunction())) {
			// // Note: this doesn't work for Together() function, if we
			// allow
			// // o0 to be a fractional number
			// return f1.mapAt(F.Times(o0, null), 2);
			// }
			// }

			if (o1.isPower()) {
				IExpr power1Base = o1.base();
				IExpr power1Exponent = o1.exponent();
				temp = timesArgPower(o0, power1Base, power1Exponent);
				if (temp.isPresent()) {
					return temp;
				}
			} else if (o1.isInterval1()) {
				if (o0.isInterval1() || o0.isReal()) {
					return timesInterval(o0, o1);
				}
			} else if (o1 instanceof ASTSeriesData) {
				return ((ASTSeriesData) o1).times(o0);
			}
			return F.NIL;
		}

		@Override
		public IExpr eComIntArg(final IComplex c0, final IInteger i1) {
			return c0.multiply(F.complex(i1, F.C0));
		}

		private IExpr evalNumericMode(final IAST ast) {
			INum number = F.CD1;
			int start = -1;
			for (int i = 1; i < ast.size(); i++) {
				IExpr temp = ast.get(i);
				if (temp instanceof INum) {
					if (temp instanceof ApfloatNum) {
						number = number.multiply((INum) temp);
					} else {
						if (number instanceof ApfloatNum) {
							number = number.multiply(((INum) temp).apfloatNumValue(number.precision()));
						} else {
							number = number.multiply((INum) temp);
						}
					}
				} else if (temp instanceof IComplexNum) {
					start = i;
					break;
				} else {
					return F.NIL;
				}
			}
			if (start < 0) {
				return number;
			}
			IComplexNum complexNumber;
			if (number instanceof Num) {
				complexNumber = F.complexNum(((Num) number).doubleValue());
			} else {
				complexNumber = F.complexNum(((ApfloatNum) number).apfloatValue());
			}
			for (int i = start; i < ast.size(); i++) {
				IExpr temp = ast.get(i);
				if (temp instanceof INum) {
					number = (INum) temp;
					if (number instanceof Num) {
						complexNumber = complexNumber.multiply(F.complexNum(((Num) number).doubleValue()));
					} else {
						complexNumber = complexNumber.multiply(F.complexNum(((ApfloatNum) number).apfloatValue()));
					}
				} else if (temp instanceof IComplexNum) {
					if (complexNumber instanceof ApcomplexNum) {
						complexNumber = complexNumber
								.multiply(((IComplexNum) temp).apcomplexNumValue(complexNumber.precision()));
					} else {
						complexNumber = complexNumber.multiply((IComplexNum) temp);
					}
				} else {
					return F.NIL;
				}
			}
			return complexNumber;
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			double result = 1;
			for (int i = top - size + 1; i < top + 1; i++) {
				result *= stack[i];
			}
			return result;
		}

		@Override
		public IExpr evaluate(final IAST ast1, EvalEngine engine) {
			IAST astTimes = ast1;
			int size = astTimes.size();
			if (size == 1) {
				return F.C1;
			}
			if (size == 2 && astTimes.head() == F.Times) {
				return astTimes.arg1();
			}
			if (size > 2) {
				IAST temp = evaluateHashsRepeated(astTimes, engine);
				if (temp.isPresent()) {
					return temp.getOneIdentity(F.C1);
				}
			}
			if (size == 3) {
				if ((astTimes.arg1().isNumeric() || astTimes.arg1().isOne() || astTimes.arg1().isMinusOne())
						&& astTimes.arg2().isPlus()) {
					if (astTimes.arg1().isOne()) {
						return astTimes.arg2();
					}
					// distribute the number over the sum:
					final IAST arg2 = (IAST) astTimes.arg2();
					return arg2.mapThread(F.Times(astTimes.arg1(), null), 2);
				}
				return distributeLeadingFactor(binaryOperator(astTimes.arg1(), astTimes.arg2()), astTimes);
			}

			if (size > 3) {
				final ISymbol sym = astTimes.topHead();
				IASTAppendable result = null;
				IExpr tres;
				IExpr temp = astTimes.arg1();
				boolean evaled = false;
				int i = 2;

				while (i < astTimes.size()) {

					tres = binaryOperator(temp, astTimes.get(i));

					if (!tres.isPresent()) {

						for (int j = i + 1; j < astTimes.size(); j++) {
							tres = binaryOperator(temp, astTimes.get(j));

							if (tres.isPresent()) {
								evaled = true;
								temp = tres;

								astTimes = astTimes.removeAtClone(j);

								break;
							}
						}

						if (!tres.isPresent()) {
							if (result == null) {
								result = F.ast(sym, astTimes.size() - i + 1, false);
							}
							result.append(temp);
							if (i == astTimes.argSize()) {
								result.append(astTimes.get(i));
							} else {
								temp = astTimes.get(i);
							}
							i++;
						}

					} else {
						evaled = true;
						temp = tres;

						if (i == astTimes.argSize()) {
							if (result == null) {
								result = F.ast(sym, astTimes.size() - i + 1, false);
							}
							result.append(temp);
						}

						i++;
					}
				}

				if (evaled) {
					if (sym.hasOneIdentityAttribute() && result.size() > 1) {
						return result.getOneIdentity(F.C0);
					}

					return distributeLeadingFactor(result, F.NIL);
				}
				return distributeLeadingFactor(F.NIL, astTimes);
			}

			return F.NIL;
		}

		@Override
		public HashedOrderlessMatcher getHashRuleMap() {
			return TIMES_ORDERLESS_MATCHER;
		}

		/** {@inheritDoc} */
		@Override
		public IExpr numericEval(final IAST ast, EvalEngine engine) {
			IExpr temp = evalNumericMode(ast);
			if (temp.isPresent()) {
				return temp;
			}
			return evaluate(ast, engine);
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.ONEIDENTITY | ISymbol.ORDERLESS | ISymbol.FLAT | ISymbol.LISTABLE
					| ISymbol.NUMERICFUNCTION);

			TIMES_ORDERLESS_MATCHER.defineHashRule(new HashedPatternRulesLog(//
					Log(x_), //
					Log(y_)));

			// Sin(x)*Cot(x) -> Cos(x)
			TIMES_ORDERLESS_MATCHER.defineHashRule(new HashedPatternRulesTimes(//
					F.Sin(x_), //
					F.Cot(x_), //
					F.Cos(x)));
			// Sin(x)*Csc(x) -> 1
			TIMES_ORDERLESS_MATCHER.defineHashRule(new HashedPatternRulesTimes(//
					F.Sin(x_), //
					F.Csc(x_), //
					F.C1));

			TIMES_ORDERLESS_MATCHER.defineHashRule(new HashedPatternRulesTimes(//
					F.Tan(x_), //
					F.Cot(x_), //
					F.C1));
			TIMES_ORDERLESS_MATCHER.defineHashRule(new HashedPatternRulesTimes(//
					F.Cos(x_), //
					F.Sec(x_), //
					F.C1));
			TIMES_ORDERLESS_MATCHER.defineHashRule(new HashedPatternRulesTimes(//
					F.Cos(x_), //
					F.Tan(x_), //
					F.Sin(x)));
			TIMES_ORDERLESS_MATCHER.defineHashRule(new HashedPatternRulesTimes(//
					F.Csc(x_), //
					F.Tan(x_), //
					F.Sec(x)));
			TIMES_ORDERLESS_MATCHER.defineHashRule(new HashedPatternRulesTimes(//
					F.ProductLog(x_), //
					F.Power(F.E, F.ProductLog(x_)), //
					x));
			TIMES_ORDERLESS_MATCHER.defineHashRule(new HashedPatternRulesTimes(//
					F.Gamma(x_), //
					F.Gamma(F.Plus(F.C1, F.Times(F.CN1, x_))), //
					// Pi*Csc(x*Pi)
					F.Times(F.Pi, F.Csc(F.Times(x, F.Pi)))));

			// TODO: HACK useOnlyEqualFactors = true in the following rules,
			// to avoid stack overflow in integration rules.
			// If true use only rules where both factors are equal,
			TIMES_ORDERLESS_MATCHER.defineHashRule(new HashedPatternRulesTimes(//
					F.Sin(x_), //
					F.Sec(x_), //
					F.Tan(x), true));
			TIMES_ORDERLESS_MATCHER.defineHashRule(new HashedPatternRulesTimes(//
					F.Cos(x_), //
					F.Csc(x_), //
					F.Cot(x), true));

			super.setUp(newSymbol);
		}

		/**
		 * Try simplifying <code>arg0 * ( power1Arg1 ^ power1Arg2 )</code>
		 * 
		 * @param arg0
		 * @param power1Arg1
		 * @param power1Arg2
		 * @return
		 */
		private IExpr timesArgPower(final IExpr arg0, IExpr power1Arg1, IExpr power1Arg2) {
			if (power1Arg1.equals(arg0)) {
				if (power1Arg2.isNumber() && !arg0.isRational()) {
					// avoid reevaluation of a root of a rational number (example: 2*Sqrt(2) )
					return arg0.power(power1Arg2.inc());
				} else if (!power1Arg2.isNumber()) {
					return arg0.power(power1Arg2.inc());
				}
				// } else if (arg0.isPlus() && power1Arg1.equals(arg0.negate()))
				// {
				// // Issue#128
				// if (power1Arg2.isInteger()) {
				// return arg0.power(power1Arg2.inc()).negate();
				// } else if (!power1Arg2.isNumber()) {
				// return arg0.power(power1Arg2.inc()).negate();
				// }
			} else if (power1Arg2.isFraction()) {
				if (power1Arg1.isMinusOne()) {
					if (arg0.isImaginaryUnit()) {
						// I * power1Arg1 ^ power1Arg2 -> (-1) ^ (power1Arg2 + (1/2))
						return F.Power(F.CN1, power1Arg2.plus(F.C1D2));
					}
					if (arg0.isNegativeImaginaryUnit()) {
						// (-I) * power1Arg1 ^ power1Arg2 -> (-1) * (-1) ^ (power1Arg2 + (1/2))
						return F.Times(F.CN1, F.Power(F.CN1, power1Arg2.plus(F.C1D2)));
					}
				}
				if (arg0.isRational()) {
					// if (power1Arg1.isInteger()) {
					// // example: 1/9 * 3^(1/2) -> 1/3 * 3^(-1/2)
					//
					// // TODO implementation for complex numbers instead of
					// // fractions
					// IFraction f0 = (IFraction) arg0;
					// IInteger pArg1 = (IInteger) power1Arg1;
					// IFraction pArg2 = (IFraction) power1Arg2;
					// if (pArg1.isPositive()) {
					// if (pArg2.isPositive()) {
					// IInteger denominatorF0 = f0.getDenominator();
					// IInteger[] res = denominatorF0.divideAndRemainder(pArg1);
					// if (res[1].isZero()) {
					// return F.Times(F.fraction(f0.getNumerator(), res[0]),
					// F.Power(pArg1, F.Subtract(F.C1, pArg2).negate()));
					// }
					// } else {
					// IInteger numeratorF0 = f0.getNumerator();
					// IInteger[] res = numeratorF0.divideAndRemainder(pArg1);
					// if (res[1].isZero()) {
					// return F.Times(F.fraction(res[0], f0.getDenominator()),
					// F.Power(pArg1, pArg2.negate()));
					// }
					// }
					// }
					// } else
					if ((power1Arg1.isRational())) {

						if (power1Arg2.isNegative()) {
							IExpr temp = timesPowerPower(((IRational) arg0).numerator(),
									((IRational) arg0).denominator(), F.C1, //
									((IRational) power1Arg1).denominator(), ((IRational) power1Arg1).numerator(),
									(IFraction) power1Arg2.negate());
							if (temp.isPresent()) {
								return temp;
							}
						} else {
							IExpr temp = timesPowerPower(((IRational) arg0).numerator(),
									((IRational) arg0).denominator(), F.C1, //
									((IRational) power1Arg1).numerator(), ((IRational) power1Arg1).denominator(),
									(IFraction) power1Arg2);
							if (temp.isPresent()) {
								return temp;
							}
						}
					}
				}
			}

			return F.NIL;
		}

		private IExpr timesInterval(final IExpr o0, final IExpr o1) {
			return F.Interval(F.List(
					F.Min(o0.lower().times(o1.lower()), o0.lower().times(o1.upper()), o0.upper().times(o1.lower()),
							o0.upper().times(o1.upper())),
					F.Max(o0.lower().times(o1.lower()), o0.lower().times(o1.upper()), o0.upper().times(o1.lower()),
							o0.upper().times(o1.upper()))));
		}

	}

	/**
	 * <pre>
	 * TimesBy(x, dx)
	 * 
	 * x *= dx
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * is equivalent to <code>x = x * dx</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; a = 10
	 * &gt;&gt; a *= 2   
	 * 20    
	 * 
	 * &gt;&gt; a    
	 * 20
	 * </pre>
	 */
	private static class TimesBy extends AddTo {

		@Override
		protected IASTMutable getAST(final IExpr value) {
			return (IASTMutable) F.Times(null, value);
		}

		@Override
		protected ISymbol getFunctionSymbol() {
			return F.TimesBy;
		}
	}

	/**
	 * Try simpplifying <code>(power0Arg1 ^ power0Arg2) * (power1Arg1 ^ power1Arg2)</code>
	 * 
	 * @param power0Arg1
	 * @param power0Arg2
	 * @param power1Arg1
	 * @param power1Arg2
	 * @return
	 */
	private static IExpr timesPowerPower(IExpr power0Arg1, IExpr power0Arg2, IExpr power1Arg1, IExpr power1Arg2) {
		if (power0Arg2.isNumber()) {
			if (power1Arg2.isNumber()) {
				if (power0Arg1.equals(power1Arg1)) {
					// x^(a)*x^(b) => x ^(a+b)
					return power0Arg1.power(power0Arg2.plus(power1Arg2));
				}
				if (power0Arg2.equals(power1Arg2) && power0Arg1.isPositive() && power1Arg1.isPositive()
						&& power0Arg1.isReal() && power1Arg1.isReal()) {
					// a^(c)*b^(c) => (a*b) ^c
					return power0Arg1.times(power1Arg1).power(power0Arg2);
				}
				if (power0Arg1.isFraction() && power0Arg2.isFraction() && power1Arg1.isFraction()
						&& power1Arg2.isFraction()) {
					IExpr temp = timesPowerPower(((IFraction) power0Arg1).numerator(),
							((IFraction) power0Arg1).denominator(), (IFraction) power0Arg2, //
							((IFraction) power1Arg1).numerator(), ((IFraction) power1Arg1).denominator(),
							(IFraction) power1Arg2);
					if (temp.isPresent()) {
						return temp;
					}
				}
				// if (power0Arg1.isPlus() && power1Arg1.isPlus() &&
				// power0Arg1.equals(power1Arg1.negate())) {// Issue#128
				// return
				// power0Arg1.power(power0Arg2.plus(power1Arg2)).times(CN1.power(power1Arg2));
				// }
			}
		}
		if (power0Arg1.equals(power1Arg1))

		{
			// x^(a)*x^(b) => x ^(a+b)
			return power0Arg1.power(power0Arg2.plus(power1Arg2));
		}
		return F.NIL;
	}

	/**
	 * (p1Numer/p1Denom)^(p1Exp) * (p2Numer1/p2Denom1)^(p2Exp)
	 * 
	 * @return
	 */
	public static IExpr timesPowerPower(IInteger p1Numer, IInteger p1Denom, IRational p1Exp, IInteger p2Numer,
			IInteger p2Denom, IRational p2Exp) {
		boolean[] evaled = new boolean[] { false };

		OpenIntToIExprHashMap<IRational> fn1Map = new OpenIntToIExprHashMap<IRational>();
		IInteger fn1Rest = Primality.countPrimes1021(p1Numer, p1Exp, fn1Map, false, evaled);
		IInteger fd2Rest = Primality.countPrimes1021(p2Denom, p2Exp.negate(), fn1Map, false, evaled);

		OpenIntToIExprHashMap<IRational> fn2Map = new OpenIntToIExprHashMap<IRational>();
		IInteger fn2Rest = Primality.countPrimes1021(p2Numer, p2Exp, fn2Map, false, evaled);
		IInteger fd1Rest = Primality.countPrimes1021(p1Denom, p1Exp.negate(), fn2Map, false, evaled);

		if (evaled[0]) {
			OpenIntToIExprHashMap<IRational>.Iterator iter = fn2Map.iterator();
			while (iter.hasNext()) {
				iter.advance();
				int base = iter.key();
				IRational exponent = iter.value();
				IRational exp = fn1Map.get(base);
				if (exp == null) {
					fn1Map.put(base, exponent);
				} else {
					fn1Map.put(base, exp.add(exponent));
				}
			}
			IASTAppendable times1 = F.TimesAlloc(fn1Map.size() + 4);
			if (!fn1Rest.isOne()) {
				times1.append(F.Power(fn1Rest, p1Exp));
			}
			if (!fd2Rest.isOne()) {
				times1.append(F.Power(fd2Rest, p2Exp.negate()));
			}
			if (!fn2Rest.isOne()) {
				times1.append(F.Power(fn2Rest, p2Exp));
			}
			if (!fd1Rest.isOne()) {
				times1.append(F.Power(fd1Rest, p1Exp.negate()));
			}
			iter = fn1Map.iterator();
			while (iter.hasNext()) {
				iter.advance();
				int base = iter.key();
				IRational exponent = iter.value();
				if (base != 1) {
					times1.append(F.Power(F.ZZ(base), exponent));
				}
			}
			return times1;
		}

		return F.NIL;
	}

	/**
	 * (p1Numer/p1Denom)^(p1Exp)
	 * 
	 * @return
	 */
	public static IExpr rationalPower(IInteger p1Numer, IInteger p1Denom, IRational p1Exp) {
		boolean[] evaled = new boolean[] { false };

		OpenIntToIExprHashMap<IRational> fn1Map = new OpenIntToIExprHashMap<IRational>();
		IInteger fn1Rest = Primality.countPrimes1021(p1Numer, p1Exp, fn1Map, true, evaled);
		IInteger fd1Rest = Primality.countPrimes1021(p1Denom, p1Exp.negate(), fn1Map, true, evaled);

		if (evaled[0]) {
			IASTAppendable times1 = F.TimesAlloc(fn1Map.size() + 4);
			if (!fn1Rest.isOne()) {
				times1.append(F.Power(fn1Rest, p1Exp));
			}
			if (!fd1Rest.isOne()) {
				times1.append(F.Power(fd1Rest, p1Exp.negate()));
			}
			OpenIntToIExprHashMap<IRational>.Iterator iter = fn1Map.iterator();
			while (iter.hasNext()) {
				iter.advance();
				int base = iter.key();
				IRational exponent = iter.value();
				if (base != 1) {
					times1.append(F.Power(F.ZZ(base), exponent));
				}
			}
			return times1;
		}

		return F.NIL;
	}

	private final static Arithmetic CONST = new Arithmetic();

	public static Arithmetic initialize() {
		return CONST;
	}

	private Arithmetic() {

	}
}
