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
import org.matheclipse.core.eval.interfaces.AbstractArg2;
import org.matheclipse.core.eval.interfaces.AbstractArgMultiple;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.eval.util.AbstractAssumptions;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.expression.NumberUtil;
import org.matheclipse.core.interfaces.IAST;
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
import org.matheclipse.core.patternmatching.HashedOrderlessMatcher;
import org.matheclipse.core.patternmatching.HashedOrderlessMatcherPlus;
import org.matheclipse.core.patternmatching.HashedOrderlessMatcherTimes;
import org.matheclipse.core.reflection.system.rules.AbsRules;
import org.matheclipse.core.reflection.system.rules.ConjugateRules;
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
		F.Minus.setEvaluator(new Minus());

		F.Abs.setEvaluator(new Abs());
		F.AddTo.setEvaluator(new AddTo());
		F.Arg.setEvaluator(new Arg());
		F.Complex.setEvaluator(CONST_COMPLEX);
		F.Conjugate.setEvaluator(new Conjugate());
		F.Decrement.setEvaluator(new Decrement());
		F.DirectedInfinity.setEvaluator(new DirectedInfinity());
		F.DivideBy.setEvaluator(new DivideBy());
		F.Gamma.setEvaluator(new Gamma());
		F.HarmonicNumber.setEvaluator(new HarmonicNumber());
		F.Im.setEvaluator(new Im());
		F.Increment.setEvaluator(new Increment());
		F.Piecewise.setEvaluator(new Piecewise());
		F.Pochhammer.setEvaluator(new Pochhammer());
		F.Precision.setEvaluator(new Precision());
		F.PreDecrement.setEvaluator(new PreDecrement());
		F.PreIncrement.setEvaluator(new PreIncrement());
		F.Rational.setEvaluator(CONST_RATIONAL);
		F.Re.setEvaluator(new Re());
		F.SubtractFrom.setEvaluator(new SubtractFrom());
		F.TimesBy.setEvaluator(new TimesBy());

	}

	/**
	 * Absolute value of a number. See
	 * <a href="http://en.wikipedia.org/wiki/Absolute_value">Wikipedia:Absolute
	 * value</a>
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
				if (temp.isSignedNumber()) {
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
				IAST[] result = ((IAST) arg1).filter(new AbsTimesFunction());
				if (result[0].size() > 1) {
					if (result[1].size() > 1) {
						result[0].append(F.Abs(result[1]));
					}
					return result[0];
				}
			}
			if (arg1.isPower() && arg1.getAt(2).isSignedNumber()) {
				return F.Power(F.Abs(arg1.getAt(1)), arg1.getAt(2));
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
	 * Operator +=
	 * 
	 */
	private static class AddTo extends AbstractFunctionEvaluator {

		protected IAST getAST(final IExpr value) {
			return F.Plus(null, value);
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
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Argument_%28complex_analysis%29">
	 * Wikipedia - Argument (complex_analysis)</a>
	 * 
	 */
	private static class Arg extends AbstractFunctionEvaluator implements INumeric, DoubleUnaryOperator {

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

			final IExpr arg1 = ast.arg1();
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
				if (temp.isSignedNumber()) {
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
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION | ISymbol.NHOLDFIRST);
			super.setUp(newSymbol);
		}
	}

	private final static class Complex extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			try {
				IExpr realExpr = engine.evaluate(ast.arg1());
				IExpr imaginaryExpr = engine.evaluate(ast.arg2());
				if (realExpr.isComplex() || realExpr.isComplexNumeric() || imaginaryExpr.isComplex()
						|| imaginaryExpr.isComplexNumeric()) {
					return F.Plus(realExpr, F.Times(F.CI, imaginaryExpr));
					// if (((IComplex) imaginaryExpr).getRealPart().isZero()) {
					// imaginaryExpr = ((IComplex) imaginaryExpr).getImaginaryPart();
					// }
				}
				// else if (imaginaryExpr.isComplexNumeric()) {
				// if (F.isZero(((IComplexNum) imaginaryExpr).getRealPart())) {
				// imaginaryExpr = F.num(((IComplexNum) imaginaryExpr).getImaginaryPart());
				// }
				// }
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

	/**
	 * Conjugate the given argument.
	 * 
	 * See
	 * <a href="http://en.wikipedia.org/wiki/Complex_conjugation">Wikipedia:Complex
	 * conjugation</a>
	 */
	private final static class Conjugate extends AbstractTrigArg1 implements INumeric, ConjugateRules {

		/**
		 * Conjugate numbers and special objects like <code>Infinity</code> and
		 * <code>Indeterminate</code>.
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
				IExpr base = ((IAST) arg1).arg1();
				if (base.isPositiveResult()) {
					return F.Power(base, F.Conjugate(((IAST) arg1).arg2()));
				}
			}
			if (arg1.isPlus()) {
				return ((IAST) arg1).mapThread(F.Conjugate(F.Null), 1);
			}
			if (arg1.isTimes()) {
				IAST result = F.NIL;
				IAST clone = ((IAST) arg1).clone();
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
				return arg1.getAt(1);
			} else if (arg1.isAST(F.Zeta, 2)) {
				return F.Zeta(F.Conjugate(arg1.getAt(1)));
			} else if (arg1.isAST(F.Zeta, 3) && arg1.getAt(1).isSignedNumber() && arg1.getAt(2).isSignedNumber()) {
				return F.Zeta(F.Conjugate(arg1.getAt(1)), F.Conjugate(arg1.getAt(2)));
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

	private static class Decrement extends AbstractFunctionEvaluator {

		protected IAST getAST() {
			return F.Plus(null, F.CN1);
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
	 * Operator /=
	 * 
	 */
	private static class DivideBy extends AddTo {

		@Override
		protected IAST getAST(final IExpr value) {
			return F.Times(null, F.Power(value, F.CN1));
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
					if (arg1.isSignedNumber()) {
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
						if (a1.isSignedNumber()) {
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
						if (result.isSignedNumber()) {
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
					if (a2.isSignedNumber()) {
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
	 * <p>
	 * Returns the Gamma function value.
	 * </p>
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Gamma_function">Gamma function</a>
	 * and <a href=
	 * "https://en.wikipedia.org/wiki/Particular_values_of_the_Gamma_function">
	 * Particular values of the Gamma function</a>
	 * 
	 */
	private final static class Gamma extends AbstractTrigArg1 implements DoubleUnaryOperator {

		/**
		 * Implement: <code>Gamma(x_Integer) := (x-1)!</code>
		 * 
		 * @param x
		 * @return
		 */
		public static IInteger gamma(final IInteger x) {
			return NumberTheory.factorial(x.subtract(C1));
		}

		@Override
		public double applyAsDouble(double operand) {
			return org.hipparchus.special.Gamma.gamma(operand);
		}

		@Override
		public IExpr e1DblArg(final double arg1) {
			double gamma = org.hipparchus.special.Gamma.gamma(arg1);
			return num(gamma);
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 4);

			if (ast.isAST1()) {
				return evaluateArg1(ast.arg1());
			}
			return NIL;
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1) {
			if (arg1.isInteger()) {
				return gamma((IInteger) arg1);
			}
			if (arg1.isFraction()) {
				IFraction frac = (IFraction) arg1;
				if (frac.getDenominator().equals(C2)) {
					IInteger n = frac.getNumerator();
					if (arg1.isNegative()) {
						n = n.negate();
						return Times(Power(CN1, Times(C1D2, Plus(C1, n))), Power(C2, n), Sqrt(Pi),
								Power(Factorial(n), -1), Factorial(Times(C1D2, Plus(CN1, n))));
					} else {
						// Sqrt(Pi) * (n-2)!! / 2^((n-1)/2)
						return Times(Sqrt(Pi), Factorial2(n.subtract(C2)), Power(C2, Times(C1D2, Subtract(C1, n))));
					}
				}
			}
			if (arg1.isAST()) {
				IAST z = (IAST) arg1;
				if (z.isAST(Conjugate, 2)) {
					// mirror symmetry for Conjugate()
					return Conjugate(Gamma(z.arg1()));
				}

			}
			return NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}
	}

	/**
	 * Harmonic number of a given integer value
	 * 
	 * See: <a href="http://en.wikipedia.org/wiki/Harmonic_number">Harmonic
	 * number</a>
	 */
	private final static class HarmonicNumber extends AbstractEvaluator {

		public HarmonicNumber() {
		}

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
						IAST result = F.PlusAlloc(n);
						for (int i = 1; i <= n; i++) {
							result.append(Power(integer(i), Negate(arg2)));
						}
						return result;
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
				if (n == 1) {
					return C1;
				}

				return fraction(harmonicNumber(n));
			}

			return F.NIL;
		}

		/**
		 * The Harmonic number at the index specified
		 * 
		 * @param n
		 *            the index, non-negative.
		 * @return the H_1=1 for n=1, H_2=3/2 for n=2 etc. For values of n less than 1,
		 *         zero is returned.
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
	 * Get the imaginary part of an expression
	 * 
	 * See: <a href="http://en.wikipedia.org/wiki/Imaginary_part">Imaginary part</a>
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
					IExpr im = engine.evaluate(F.Im(directedInfininty.arg1()));
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
				if (arg1.getAt(1).isSignedNumber()) {
					IAST temp = ((IAST) arg1).removeAtClone(1);
					return F.Times(arg1.getAt(1), F.Im(temp));
				}
				if (arg1.getAt(1).isImaginaryUnit()) {
					// Im(I*temp) -> Re(temp)
					return ((IAST) arg1).removeAtClone(1).re();
				}
			}
			if (arg1.isPlus()) {
				return ((IAST) arg1).mapThread((IAST) F.Im(null), 1);
			}
			if (arg1.isPower()) {
				IAST astPower = (IAST) arg1;
				IExpr base = astPower.arg1();
				if (base.isRealResult()) {
					// test for x^(a+I*b)
					IExpr x = base;
					IExpr exponent = astPower.arg2();
					if (exponent.isNumber()) {
						// (x^2)^(a/2)*E^(-b*Arg[x])*Sin[a*Arg[x]+1/2*b*Log[x^2]]
						IExpr a = ((INumber) exponent).re();
						IExpr b = ((INumber) exponent).im();
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

	private static class Increment extends Decrement {

		@Override
		protected IAST getAST() {
			return F.Plus(null, F.C1);
		}

		@Override
		protected ISymbol getFunctionSymbol() {
			return F.Increment;
		}
	}

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
	 * A piecewise-defined function (also called a piecewise function or a hybrid
	 * function) is a function which is defined by multiple subfunctions, each
	 * subfunction applying to a certain interval of the main function's domain.
	 * 
	 * See: <a href="http://en.wikipedia.org/wiki/Piecewise">Wikipedia:Piecewise</a>
	 * 
	 */
	private final static class Piecewise extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			int[] dim = ast.arg1().isMatrix(false);
			if (dim == null || dim[0] <= 0 || dim[1] != 2) {
				engine.printMessage("Piecewise: Matrix with row-dimension > 0 and column-dimension == 2 expected!");
				return F.NIL;
			}
			IAST matrix = (IAST) ast.arg1();
			IExpr defaultValue = F.C0;
			if (ast.isAST2()) {
				defaultValue = ast.arg2();
			}
			IExpr cond;
			IAST row;
			int matrixSize = matrix.size();
			IAST result = F.ListAlloc(matrixSize);
			IAST pw = F.ast(F.Piecewise);
			pw.append(result);
			boolean evaluated = false;
			boolean noBoolean = false;
			for (int i = 1; i < matrixSize; i++) {
				row = matrix.getAST(i);
				cond = row.arg2();
				if (cond.isTrue()) {
					if (!evaluated && i == matrixSize - 1) {
						if (!row.arg1().isSymbol()) {
							return row.arg1();
						}
						return F.NIL;
					}
					if (noBoolean) {
						result.append(F.List(row.arg1(), F.True));
						return pw;
					}
					return row.arg1();
				} else if (cond.isFalse()) {
					evaluated = true;
					continue;
				}
				cond = engine.evaluateNull(cond);
				if (!cond.isPresent()) {
					noBoolean = true;
					result.append(F.List(row.arg1(), row.arg2()));
					continue;
				} else if (cond.isTrue()) {
					evaluated = true;
					if (noBoolean) {
						result.append(F.List(row.arg1(), F.True));
						return pw;
					}
					return row.arg1();
				} else if (cond.isFalse()) {
					evaluated = true;
					continue;
				} else {
					result.append(F.List(row.arg1(), cond));
					noBoolean = true;
					continue;
				}
			}
			if (!noBoolean) {
				return defaultValue;
			} else {
				if (evaluated) {
					pw.append(defaultValue);
					return pw;
				}
			}

			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.NUMERICFUNCTION);
		}
	}

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
				if (ast.get(i) instanceof INum) {
					if (ast.get(i) instanceof ApfloatNum) {
						number = number.add((INum) ast.get(i));
					} else {
						if (number instanceof ApfloatNum) {
							number = number.add(((INum) ast.get(i)).apfloatNumValue(number.precision()));
						} else {
							number = number.add((INum) ast.get(i));
						}
					}
				} else if (ast.get(i) instanceof IComplexNum) {
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
				if (ast.get(i) instanceof INum) {
					number = (INum) ast.get(i);
					if (number instanceof Num) {
						complexNumber = complexNumber.add(F.complexNum(((Num) number).doubleValue()));
					} else {
						complexNumber = complexNumber.add(F.complexNum(((ApfloatNum) number).apfloatValue()));
					}
				} else if (ast.get(i) instanceof IComplexNum) {
					if (complexNumber instanceof ApcomplexNum) {
						complexNumber = complexNumber
								.add(((IComplexNum) ast.get(i)).apcomplexNumValue(complexNumber.precision()));
					} else {
						complexNumber = complexNumber.add((IComplexNum) ast.get(i));
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
		 * See: <a href="http://www.cs.berkeley.edu/~fateman/papers/newsimp.pdf">
		 * Experiments in Hash-coded Algebraic Simplification</a>
		 * 
		 * @param ast
		 *            the abstract syntax tree (AST) of the form <code>Plus(...)</code>
		 *            which should be evaluated
		 * @return the evaluated object or <code>null</code>, if evaluation isn't
		 *         possible
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
					return ((IAST) temp).arg1();
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
	 * Compute Pochhammer's symbol (this)_n.
	 * 
	 * @param n
	 *            The number of product terms in the evaluation.
	 * @return Gamma(this+n)/Gamma(this) = this*(this+1)*...*(this+n-1).
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
			if (a.isRational() && n.isInteger()) {
				BigFraction bf = ((IRational) a).getFraction();
				BigFraction ph = pochhammer(bf, ((IInteger) n).toBigNumerator());
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
				int ni = n.toIntDefault(Integer.MIN_VALUE);
				if (ni > Integer.MIN_VALUE) {
					if (ni > 0) {
						// Product(a + k, {k, 0, n - 1})
						return F.product(x -> F.Plus(a, x), 0, ni - 1);
					}
					if (ni < 0) {
						// Product(1/(a - k), {k, 1, -n})
						return Power(F.product(x -> F.Plus(a, x.negate()), 1, -ni), -1);
					}
				}
			}
			if (a.isNumber() && n.isNumber()) {
				return F.Divide(F.Gamma(F.Plus(a, n)), F.Gamma(a));
			}
			return F.NIL;
		}

		/**
		 * Compute Pochhammer's symbol (this)_n.
		 * 
		 * @param n
		 *            The number of product terms in the evaluation.
		 * @return Gamma(this+n)/Gamma(this) = this*(this+1)*...*(this+n-1).
		 */
		public static BigFraction pochhammer(BigFraction th, final BigInteger n) {
			if (n.compareTo(BigInteger.ZERO) < 0) {
				BigFraction res = BigFraction.ONE;
				BigInteger i = BigInteger.valueOf(-1);
				for (; i.compareTo(n) >= 0; i = i.subtract(BigInteger.ONE)) {
					res = res.multiply(th.add(i));
				}
				return res.reciprocal();
			} else if (n.equals(BigInteger.ZERO)) {
				return BigFraction.ONE;
			} else {
				BigFraction res = new BigFraction(th.getNumerator(), th.getDenominator());
				BigInteger i = BigInteger.ONE;
				for (; i.compareTo(n) < 0; i = i.add(BigInteger.ONE)) {
					res = res.multiply(th.add(i));
				}
				return res;
			}
		}

		/**
		 * Compute pochhammer's symbol (this)_n.
		 * 
		 * @param n
		 *            The number of product terms in the evaluation.
		 * @return Gamma(this+n)/GAMMA(this).
		 */
		// public static BigFraction pochhammer(BigFraction th, int n) {
		// return pochhammer(th, BigInteger.valueOf(n));
		// }

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		}
	}

	public static class Power extends AbstractArg2 implements INumeric, PowerRules {

		/**
		 * <p>
		 * Calculate <code>interval({lower, upper}) ^ exponent</code>.
		 * </p>
		 * <p>
		 * See: <a href=
		 * "https://de.wikipedia.org/wiki/Intervallarithmetik#Elementare_Funktionen">
		 * Intervallarithmetik - Elementare Funktionen</a>
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
		 * Split this integer into the nth-root (with prime factors less equal 1021) and
		 * the &quot;rest factor&quot;
		 * 
		 * @return <code>{nth-root, rest factor}</code> or <code>null</code> if the root
		 *         is not available
		 */
		private IInteger[] calculateRoot(IInteger a, IInteger root) {
			try {
				int n = root.toInt();
				if (n > 0) {
					if (a.isOne()) {
						return null;
					}
					if (a.isMinusOne()) {
						return null;
					}
					IInteger[] result = a.nthRootSplit(n);
					if (result[1].equals(a)) {
						// no roots found
						return null;
					}
					return result;
				}
			} catch (ArithmeticException e) {

			}
			return null;
		}

		@Override
		public IExpr e2ApcomplexArg(final ApcomplexNum ac0, final ApcomplexNum ac1) {
			return ac0.pow(ac1);
		}

		@Override
		public IExpr e2ApfloatArg(final ApfloatNum af0, final ApfloatNum af1) {
			return af0.pow(af1);
		}

		@Override
		public IExpr e2ComArg(final IComplex c0, final IComplex c1) {
			return F.NIL;
		}

		@Override
		public IExpr e2DblArg(final INum d0, final INum d1) {
			if (d1.isMinusOne()) {
				return d0.inverse();
			}
			if (d1.isNumIntValue()) {
				return d0.pow(d1);
			}
			if (d0.isNegative()) {
				return F.complexNum(d0.doubleValue()).pow(F.complexNum(d1.doubleValue()));
			}
			return d0.pow(d1);
		}

		@Override
		public IExpr e2DblComArg(final IComplexNum c0, final IComplexNum c1) {
			return c0.pow(c1);
		}

		@Override
		public IExpr e2FraArg(IFraction f0, IFraction f1) {
			if (f0.getNumerator().isZero()) {
				return F.C0;
			}

			if (f1.getNumerator().isZero()) {
				return F.C1;
			}

			if (f1.equals(F.C1D2)) {
				if (f0.isNegative()) {
					return F.Times(F.CI, F.Power(f0.negate(), f1));
				}
			}

			if (f1.equals(F.CN1D2)) {
				if (f0.isNegative()) {
					return F.Times(F.CNI, F.Power(f0.negate().inverse(), f1.negate()));
				}
			}

			if (!f1.getDenominator().isOne()) {
				IInteger a;
				IInteger b;
				IFraction f0Temp = f0;
				if (f0.sign() < 0) {
					f0Temp = f0Temp.negate();
				}
				if (f1.isNegative()) {
					a = f0Temp.getDenominator();
					b = f0Temp.getNumerator();
				} else {
					a = f0Temp.getNumerator();
					b = f0Temp.getDenominator();
				}

				// example: (-27)^(2/3) or 8^(1/3)
				if (!f1.getNumerator().isOne()) {
					try {
						int exp = f1.getNumerator().toInt();
						if (exp < 0) {
							exp *= (-1);
						}
						a = a.pow(exp);
						b = b.pow(exp);
					} catch (ArithmeticException e) {
						return F.NIL;
					}
				}

				final IInteger root = f1.getDenominator();

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
						if (f0.sign() < 0) {
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
						if (f0.sign() < 0) {
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
						if (f0.sign() < 0) {
							return Times(fraction(C1, new_denom[0]), Power(p0.negate(), new_root));
						}
						return Times(fraction(C1, new_denom[0]), Power(p0, new_root));
					}
				}

				return F.NIL;
			}
			return f0.power(f1);
		}

		@Override
		public IExpr e2IntArg(final IInteger i0, final IInteger i1) {
			if (i0.isZero()) {
				// all other cases see e2ObjArg
				return F.NIL;
			}

			try {
				long n = i1.toLong();
				return i0.power(n);
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
					if (arg1.isSignedNumber() && arg1.isPositive()) {
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
					if (arg1.isSignedNumber() && arg1.isPositive()) {
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
		public IExpr e2ObjArg(final IExpr arg1, final IExpr arg2) {
			if (arg2.isDirectedInfinity()) {
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
			}
			if (arg1.isDirectedInfinity()) {
				if (arg2.isZero()) {
					return F.Indeterminate;
				}
				if (arg1.isComplexInfinity()) {
					if (arg2.isSignedNumber()) {
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
			}
			if (arg1.isZero()) {
				return powerZero(arg2);
			}
			if (arg1.isInterval1()) {
				if (arg2.isInteger()) {
					IInteger ii = (IInteger) arg2;
					return powerInterval(arg1, ii);
				}
			}

			if (arg2.isZero()) {
				return (arg1.isInfinity() || arg1.isNegativeInfinity()) ? F.Indeterminate : F.C1;
			}

			if (arg2.isOne()) {
				return arg1;
			}

			if (arg1.isOne()) {
				return F.C1;
			}

			if (arg1.isMinusOne() && arg2.isInteger()) {
				return (((IInteger) arg2).isEven()) ? F.C1 : F.CN1;
			}

			if (arg2.isSignedNumber()) {
				if (arg1.isComplex() && arg2.isFraction() && arg2.isPositive()) {
					IExpr temp = powerComplexFraction((IComplex) arg1, (IFraction) arg2);
					if (temp.isPresent()) {
						return temp;
					}
				}
				ISignedNumber is1 = (ISignedNumber) arg2;
				if (arg1.isInfinity()) {
					if (is1.isNegative()) {
						return F.C0;
					} else {
						return F.CInfinity;
					}
				} else if (arg1.isPower() && is1.isNumIntValue() && is1.isPositive()) {
					IAST a0 = (IAST) arg1;
					if (a0.arg2().isNumIntValue() && a0.arg2().isPositive()) {
						// (x*n)^m => x ^(n*m)
						return Power(a0.arg1(), is1.times(a0.arg2()));
					}
				} else if (arg1.isNegativeInfinity() && arg2.isInteger()) {
					IInteger ii = (IInteger) arg2;
					if (ii.isNegative()) {
						return F.C0;
					} else {
						if (ii.isOdd()) {
							return F.CNInfinity;
						} else {
							return F.CInfinity;
						}
					}
				}
				if (arg2.isMinusOne() || arg2.isInteger()) {
					if (arg1.isNumber()) {
						if (arg2.isMinusOne()) {
							return ((INumber) arg1).inverse();
						}
						try {
							long n = ((IInteger) arg2).toLong();
							return ((INumber) arg1).power(n);
						} catch (ArithmeticException ae) {

						}
					} else {
						IExpr o1negExpr = F.NIL;
						if (arg2.isInteger() && ((IInteger) arg2).isEven()) {
							o1negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1, true);
						} else {
							o1negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1, false);
						}
						if (o1negExpr.isPresent()) {
							if (arg2.isMinusOne()) {
								return Times(CN1, Power(o1negExpr, CN1));
							} else {
								IInteger ii = (IInteger) arg2;
								if (ii.isEven()) {
									return Power(o1negExpr, arg2);
								}
							}
						}
						if (arg2.isMinusOne() && arg1.isTimes()) {
							IAST timesAST = (IAST) arg1;
							IExpr temp = powerTimesInverse(timesAST, arg2);
							if (temp.isPresent()) {
								return temp;
							}
						}
					}
				}
			}

			if (arg1.isSignedNumber() && ((ISignedNumber) arg1).isNegative() && arg2.equals(F.C1D2)) {
				// extract I for sqrt
				return F.Times(F.CI, F.Power(F.Negate(arg1), arg2));
			}

			if (arg1.isE() && (arg2.isPlusTimesPower())) {
				IExpr expandedFunction = F.evalExpand(arg2);
				if (expandedFunction.isPlus()) {
					return powerEPlus((IAST) expandedFunction);
				}
			}

			if (arg1.isAST()) {
				IAST astArg1 = (IAST) arg1;
				if (astArg1.isTimes()) {
					if (arg2.isInteger() || arg2.isMinusOne()) {
						// (a * b * c)^n => a^n * b^n * c^n
						return astArg1.mapThread(Power(null, arg2), 1);
					}
					if (arg2.isNumber()) {
						final IAST f0 = astArg1;

						if ((f0.size() > 1) && (f0.arg1().isNumber())) {
							return Times(Power(f0.arg1(), arg2), Power(F.ast(f0, F.Times, true, 2, f0.size()), arg2));
						}
					}
				} else if (astArg1.isPower()) {
					if (astArg1.arg2().isSignedNumber() && arg2.isSignedNumber()) {
						IExpr temp = astArg1.arg2().times(arg2);
						if (temp.isOne()) {
							if (astArg1.arg1().isNonNegativeResult()) {
								return astArg1.arg1();
							}
							if (astArg1.arg1().isRealResult()) {
								return F.Abs(astArg1.arg1());
							}
						}
					}
					if (arg2.isInteger()) {
						// (a ^ b )^n => a ^ (b * n)
						if (astArg1.arg2().isNumber()) {
							return F.Power(astArg1.arg1(), arg2.times(astArg1.arg2()));
						}
						return F.Power(astArg1.arg1(), F.Times(arg2, astArg1.arg2()));
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
		 * @param arg2
		 * @return <code>F.NIL</code> if the transformation isn't possible.
		 */
		private static IExpr powerTimesInverse(IAST timesAST, final IExpr arg2) {
			IAST resultAST = F.NIL;
			for (int i = 1; i < timesAST.size(); i++) {
				IExpr temp = timesAST.get(i);
				if (temp.isPower() && temp.getAt(2).isMinusOne()) {
					if (!resultAST.isPresent()) {
						resultAST = timesAST.clone();
						for (int j = 1; j < i; j++) {
							resultAST.set(j, F.Power(timesAST.get(j), arg2));
						}
					}
					resultAST.set(i, temp.getAt(1));
				} else {
					if (resultAST.isPresent()) {
						resultAST.set(i, F.Power(temp, arg2));
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
		private IExpr powerZero(final IExpr exponent) {
			EvalEngine engine = EvalEngine.get();
			if (exponent.isZero()) {
				// 0^0
				// TODO add a real log message
				// throw new DivisionByZero("0^0");
				engine.printMessage("Infinite expression 0^0");
				return F.Indeterminate;
			}

			IExpr a = exponent.re();
			if (a.isSignedNumber()) {
				if (((ISignedNumber) a).isNegative()) {
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
				if (temp.isSignedNumber()) {
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
			IAST multiplicationFactors = F.NIL;
			IAST plusClone = F.NIL;
			for (int i = plus.size() - 1; i > 0; i--) {
				if (plus.get(i).isLog()) {
					if (!multiplicationFactors.isPresent()) {
						multiplicationFactors = F.Times();
						plusClone = plus.clone();
					}
					multiplicationFactors.append(plus.get(i).getAt(1));
					plusClone.remove(i);
				} else if (plus.get(i).isTimes()) {
					IAST times = (IAST) plus.get(i);
					for (int j = times.size() - 1; j > 0; j--) {
						if (times.get(j).isLog()) {
							IExpr innerFunc = times.get(j).getAt(1);
							if (!multiplicationFactors.isPresent()) {
								multiplicationFactors = F.Times();
								plusClone = plus.clone();
							}
							multiplicationFactors.append(F.Power(innerFunc, F.ast(times, F.Times, false, j, j + 1)));
							plusClone.remove(i);
							break;
						}
					}
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
				IInteger numerator = fractionNumber.getNumerator();
				IInteger denominator = fractionNumber.getDenominator();
				IInteger div = numerator.div(denominator);
				if (div.isOdd()) {
					div = div.subtract(F.C1);
				}
				IRational rat = fractionNumber.subtract(div);
				numerator = rat.getNumerator();
				denominator = rat.getDenominator().multiply(F.C2);
				return F.Times(F.CN1, F.Power(F.CNI, div),
						F.Power(F.CN1, F.fraction(denominator.subtract(numerator), denominator)));
			}
			return F.NIL;
		}

		@Override
		public IExpr eComFraArg(final IComplex c0, final IFraction i1) {
			if (i1.equals(F.C1D2) && c0.getRealPart().isZero()) {
				// square root of pure imaginary number
				IRational im = c0.getImaginaryPart();
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
		public IExpr eComIntArg(final IComplex c0, final IInteger i1) {
			if (c0.isZero()) {
				return F.C0;
			}

			if (i1.isZero()) {
				return F.C1;
			}

			return c0.pow(i1.toBigNumerator().intValue());
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

	private static class PreDecrement extends Decrement {

		public PreDecrement() {
			super();
		}

		@Override
		protected IAST getAST() {
			return F.Plus(null, F.CN1);
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

	private static class PreIncrement extends PreDecrement {

		@Override
		protected IAST getAST() {
			return F.Plus(null, F.C1);
		}

		@Override
		protected ISymbol getFunctionSymbol() {
			return F.PreIncrement;
		}

	}

	/**
	 * Representation for a rational number
	 * 
	 */
	private final static class Rational extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			try {
				// try to convert into a fractional number
				IExpr arg0 = ast.arg1();
				IExpr arg1 = ast.arg2();
				if (arg0.isInteger() && arg1.isInteger()) {
					// already evaluated
				} else if (arg0 instanceof INum && arg1 instanceof INum) {
					// already evaluated
				} else {
					arg0 = engine.evaluate(arg0);
					arg1 = engine.evaluate(arg1);
				}
				if (arg0.isInteger() && arg1.isInteger()) {
					// symbolic mode
					IInteger numerator = (IInteger) arg0;
					IInteger denominator = (IInteger) arg1;
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
				} else if (arg0 instanceof INum && arg1 instanceof INum) {
					// numeric mode
					INum numerator = (INum) arg0;
					INum denominator = (INum) arg1;
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
					return F.num(numerator.doubleValue() / denominator.doubleValue());
				}
			} catch (Exception e) {
				if (Config.SHOW_STACKTRACE) {
					e.printStackTrace();
				}
			}

			return F.NIL;
		}

	}

	/**
	 * Get the real part of an expression
	 * 
	 * See: <a href="http://en.wikipedia.org/wiki/Real_part">Real part</a>
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
				if (expr.getAt(1).isSignedNumber()) {
					IAST temp = ((IAST) expr).removeAtClone(1);
					return F.Times(expr.getAt(1), F.Re(temp));
				}
				if (expr.getAt(1).isImaginaryUnit()) {
					// Re(I*temp) -> -Im(temp)
					IAST temp = ((IAST) expr).removeAtClone(1);
					return F.Times(F.CN1, F.Im(temp));
				}
			}
			if (expr.isPlus()) {
				return ((IAST) expr).mapThread((IAST) F.Re(null), 1);
			}
			if (expr.isPower()) {
				IAST astPower = (IAST) expr;
				IExpr base = astPower.arg1();
				if (base.isRealResult()) {
					// test for x^(a+I*b)
					IExpr exponent = astPower.arg2();
					if (exponent.isNumber()) {
						// (x^2)^(a/2)*E^(-b*Arg[x])*Cos[a*Arg[x]+1/2*b*Log[x^2]]
						return rePowerComplex(x, ((INumber) exponent).re(), ((INumber) exponent).im());
					}
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

	private static class Sqrt extends AbstractArg1 implements INumeric {

		public Sqrt() {
		}

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
	 * Operator -=
	 *
	 */
	private static class SubtractFrom extends AddTo {

		@Override
		protected IAST getAST(final IExpr value) {
			return F.Plus(null, F.Negate(value));
		}

		@Override
		protected ISymbol getFunctionSymbol() {
			return F.SubtractFrom;
		}
	}

	public static class Times extends AbstractArgMultiple implements INumeric {
		/**
		 * Constructor for the singleton
		 */
		public final static Times CONST = new Times();

		private static HashedOrderlessMatcher ORDERLESS_MATCHER = new HashedOrderlessMatcher();

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
					return F.eval(F.DirectedInfinity(F.Times(inf.arg1(), ((IAST) o1).arg1())));
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
		 *            the original expression which is used, if
		 *            <code>!ast.isPresent()</code>
		 * @return the evaluated object or <code>ast</code>, if the distribution of an
		 *         integer factor isn't possible
		 */
		private IExpr distributeLeadingFactor(IExpr ast, IAST originalExpr) {
			IExpr expr = ast;
			if (!expr.isPresent()) {
				expr = originalExpr;
			}
			if (expr.isTimes() && expr.getAt(1).isInteger()) {
				IAST times = (IAST) expr;
				IInteger leadingFactor = (IInteger) times.arg1();

				if (!leadingFactor.isMinusOne()) {
					IAST result = F.NIL;
					for (int i = 2; i < times.size(); i++) {
						IExpr temp = times.get(i);
						if (temp.isPower() && temp.getAt(1).isInteger() && !temp.getAt(2).isNumber()) {
							IAST power = (IAST) temp;
							IInteger powArg1 = (IInteger) power.arg1();
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
										result = times.clone();
									}
									power = power.clone();
									power.set(2, F.Plus(F.integer(count), power.arg2()));
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
			IExpr temp = F.NIL;

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
				final IAST power0 = (IAST) o0;
				IExpr power0Arg1 = power0.arg1();
				IExpr power0Arg2 = power0.arg2();
				if (power0.equalsAt(1, o1)) {
					// (x^a) * x
					if (power0Arg2.isNumber() && !o1.isRational()) {
						// avoid reevaluation of a root of a rational number (example: 2*Sqrt(2) )
						return o1.power(power0Arg2.inc());
					} else if (!power0Arg2.isNumber()) {
						return o1.power(power0Arg2.inc());
					}
				}

				if (o1.isPower()) {
					final IAST power1 = (IAST) o1;
					IExpr power1Arg1 = power1.arg1();
					IExpr power1Arg2 = power1.arg2();
					temp = timesPowerPower(power0Arg1, power0Arg2, power1Arg1, power1Arg2);
					if (temp.isPresent()) {
						return temp;
					}
				}
			}

			if (o1.isPower()) {
				final IAST power1 = (IAST) o1;
				IExpr power1Arg1 = power1.arg1();
				IExpr power1Arg2 = power1.arg2();
				temp = timesArgPower(o0, power1Arg1, power1Arg2);
				if (temp.isPresent()) {
					return temp;
				}
			}

			if (o1.isPlus()) {
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
			}
			if (o0.isInterval1()) {
				if (o1.isInterval1() || o1.isSignedNumber()) {
					return timesInterval(o0, o1);
				}
			}
			if (o1.isInterval1()) {
				if (o0.isInterval1() || o0.isSignedNumber()) {
					return timesInterval(o0, o1);
				}
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
				if (ast.get(i) instanceof INum) {
					if (ast.get(i) instanceof ApfloatNum) {
						number = number.multiply((INum) ast.get(i));
					} else {
						if (number instanceof ApfloatNum) {
							number = number.multiply(((INum) ast.get(i)).apfloatNumValue(number.precision()));
						} else {
							number = number.multiply((INum) ast.get(i));
						}
					}
				} else if (ast.get(i) instanceof IComplexNum) {
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
				if (ast.get(i) instanceof INum) {
					number = (INum) ast.get(i);
					if (number instanceof Num) {
						complexNumber = complexNumber.multiply(F.complexNum(((Num) number).doubleValue()));
					} else {
						complexNumber = complexNumber.multiply(F.complexNum(((ApfloatNum) number).apfloatValue()));
					}
				} else if (ast.get(i) instanceof IComplexNum) {
					if (complexNumber instanceof ApcomplexNum) {
						complexNumber = complexNumber
								.multiply(((IComplexNum) ast.get(i)).apcomplexNumValue(complexNumber.precision()));
					} else {
						complexNumber = complexNumber.multiply((IComplexNum) ast.get(i));
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
				IAST result = null;
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
							if (i == astTimes.size() - 1) {
								result.append(astTimes.get(i));
							} else {
								temp = astTimes.get(i);
							}
							i++;
						}

					} else {
						evaled = true;
						temp = tres;

						if (i == (astTimes.size() - 1)) {
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
			// ORDERLESS_MATCHER.setUpHashRule("Log[x_]", "Log[y_]^(-1)",
			// Log.getFunction());
			ORDERLESS_MATCHER.defineHashRule(Log(x_), Power(Log(y_), CN1), ExpTrigsFunctions.integerLogFunction());

			// Sin(x)*Cot(x) -> Cos(x)
			ORDERLESS_MATCHER.defineHashRule(F.Sin(x_), F.Cot(x_), F.Cos(x));
			ORDERLESS_MATCHER.defineHashRule(F.Sin(x_), F.Sec(x_), F.Tan(x));
			ORDERLESS_MATCHER.defineHashRule(F.Cos(x_), F.Tan(x_), F.Sin(x));
			ORDERLESS_MATCHER.defineHashRule(F.Csc(x_), F.Tan(x_), F.Sec(x));
			ORDERLESS_MATCHER.defineHashRule(F.Cos(x_), F.Csc(x_), F.Cot(x));
			ORDERLESS_MATCHER.defineHashRule(F.ProductLog(x_), F.Power(F.E, F.ProductLog(x_)), x);

			// Cos(x)^m * Sec(x)^n -> Cos(x)^(m-n)
			ORDERLESS_MATCHER.definePatternHashRule(Power(Cos(x_), F.m_Integer), Power(F.Sec(x_), F.n_Integer),
					Power(Cos(x), Subtract(F.m, F.n)), F.And(Positive(F.m), F.Greater(F.m, F.n)));
			// Cos(x)^m * Sec(x) -> Cos(x)^(m-1)
			ORDERLESS_MATCHER.definePatternHashRule(Power(Cos(x_), F.m_Integer), F.Sec(x_),
					Power(Cos(x), Subtract(F.m, F.C1)));

			// Sin(x)^m * Csc(x)^n -> Sin(x)^(m-n)
			ORDERLESS_MATCHER.definePatternHashRule(Power(Sin(x_), F.m_Integer), Power(F.Csc(x_), F.n_Integer),
					Power(Sin(x), Subtract(F.m, F.n)), F.And(Positive(F.m), F.Greater(F.m, F.n)));
			// Sin(x)^m * Csc(x) -> Sin(x)^(m-1)
			ORDERLESS_MATCHER.definePatternHashRule(Power(Sin(x_), F.m_Integer), F.Csc(x_),
					Power(Sin(x), Subtract(F.m, F.C1)));

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
			} else if (power1Arg1.isInteger() && power1Arg2.isFraction()) {
				if (power1Arg1.isMinusOne()) {
					if (arg0.isImaginaryUnit()) {
						// I * power1Arg1 ^ power1Arg2 -> (-1) ^ (power1Arg2 +
						// (1/2))
						return F.Power(F.CN1, power1Arg2.plus(F.C1D2));
					}
					if (arg0.isNegativeImaginaryUnit()) {
						// (-I) * power1Arg1 ^ power1Arg2 -> (-1) * (-1) ^
						// (power1Arg2 + (1/2))
						return F.Times(F.CN1, F.Power(F.CN1, power1Arg2.plus(F.C1D2)));
					}
				}
				if (arg0.isFraction()) {
					// example: 1/9 * 3^(1/2) -> 1/3 * 3^(-1/2)

					// TODO implementation for complex numbers instead of
					// fractions
					IFraction f0 = (IFraction) arg0;
					IInteger pArg1 = (IInteger) power1Arg1;
					IFraction pArg2 = (IFraction) power1Arg2;
					if (pArg1.isPositive()) {
						if (pArg2.isPositive()) {
							IInteger denominatorF0 = f0.getDenominator();
							IInteger[] res = denominatorF0.divideAndRemainder(pArg1);
							if (res[1].isZero()) {
								return F.Times(F.fraction(f0.getNumerator(), res[0]), F.Power(pArg1, pArg2.negate()));
							}
						} else {
							IInteger numeratorF0 = f0.getNumerator();
							IInteger[] res = numeratorF0.divideAndRemainder(pArg1);
							if (res[1].isZero()) {
								return F.Times(F.fraction(res[0], f0.getDenominator()), F.Power(pArg1, pArg2.negate()));
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

		/**
		 * Try simpplifying
		 * <code>(power0Arg1 ^ power0Arg2) * (power1Arg1 ^ power1Arg2)</code>
		 * 
		 * @param power0Arg1
		 * @param power0Arg2
		 * @param power1Arg1
		 * @param power1Arg2
		 * @return
		 */
		private IExpr timesPowerPower(IExpr power0Arg1, IExpr power0Arg2, IExpr power1Arg1, IExpr power1Arg2) {
			if (power0Arg2.isNumber()) {
				if (power1Arg2.isNumber()) {
					if (power0Arg1.equals(power1Arg1)) {
						// x^(a)*x^(b) => x ^(a+b)
						return power0Arg1.power(power0Arg2.plus(power1Arg2));
					}
					if (power0Arg2.equals(power1Arg2) && power0Arg1.isPositive() && power1Arg1.isPositive()
							&& power0Arg1.isSignedNumber() && power1Arg1.isSignedNumber()) {
						// a^(c)*b^(c) => (a*b) ^c
						return power0Arg1.times(power1Arg1).power(power0Arg2);
					}
					// if (power0Arg1.isPlus() && power1Arg1.isPlus() &&
					// power0Arg1.equals(power1Arg1.negate())) {// Issue#128
					// return
					// power0Arg1.power(power0Arg2.plus(power1Arg2)).times(CN1.power(power1Arg2));
					// }
				}
			}
			if (power0Arg1.equals(power1Arg1)) {
				// x^(a)*x^(b) => x ^(a+b)
				return power0Arg1.power(power0Arg2.plus(power1Arg2));
			}
			return F.NIL;
		}
	}

	/**
	 * Operator *=
	 * 
	 */
	private static class TimesBy extends AddTo {

		@Override
		protected IAST getAST(final IExpr value) {
			return F.Times(null, value);
		}

		@Override
		protected ISymbol getFunctionSymbol() {
			return F.TimesBy;
		}
	}

	private final static Arithmetic CONST = new Arithmetic();

	public static Arithmetic initialize() {
		return CONST;
	}

	private Arithmetic() {

	}
}
