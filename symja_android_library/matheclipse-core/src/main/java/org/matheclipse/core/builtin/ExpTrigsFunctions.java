package org.matheclipse.core.builtin;

import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CI;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.Cot;
import static org.matheclipse.core.expression.F.Divide;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Pi;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Tan;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.num;

import java.util.function.DoubleUnaryOperator;

import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.hipparchus.complex.Complex;
import org.matheclipse.core.eval.interfaces.AbstractArg1;
import org.matheclipse.core.eval.interfaces.AbstractArg12;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.eval.util.AbstractAssumptions;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.ComplexUtils;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.expression.NumberUtil;
import org.matheclipse.core.generic.BinaryFunctorImpl;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.rules.CosRules;
import org.matheclipse.core.reflection.system.rules.LogRules;
import org.matheclipse.core.reflection.system.rules.SinRules;
import org.matheclipse.core.reflection.system.rules.TanRules;

public class ExpTrigsFunctions {
	static {
		F.Cos.setEvaluator(new Cos());
		F.Exp.setEvaluator(new Exp());
		F.Log.setEvaluator(new Log());
		F.Log10.setEvaluator(new Log10());
		F.Log2.setEvaluator(new Log2());
		F.Sin.setEvaluator(new Sin());
		F.Tan.setEvaluator(new Tan());
	}

	/**
	 * Cosine function
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Trigonometric_functions">Trigonometric functions</a> and
	 * <a href="http://en.wikipedia.org/wiki/Exact_trigonometric_constants"> Wikipedia - Exact trigonometric
	 * constants</a>
	 */
	private final static class Cos extends AbstractTrigArg1 implements INumeric, CosRules, DoubleUnaryOperator {

		@Override
		public double applyAsDouble(double operand) {
			return Math.cos(operand);
		}

		@Override
		public IExpr e1ApcomplexArg(Apcomplex arg1) {
			return F.complexNum(ApcomplexMath.cos(arg1));
		}

		@Override
		public IExpr e1ApfloatArg(Apfloat arg1) {
			return F.num(ApfloatMath.cos(arg1));
		}

		@Override
		public IExpr e1ComplexArg(final Complex arg1) {
			return F.complexNum(arg1.cos());
		}

		@Override
		public IExpr e1DblArg(final double arg1) {
			return F.num(Math.cos(arg1));
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1) {
				throw new UnsupportedOperationException();
			}
			return Math.cos(stack[top]);
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1) {
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Cos(negExpr);
			}

			IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
			if (imPart.isPresent()) {
				return F.Cosh(imPart);
			}

			IAST parts = AbstractFunctionEvaluator.getPeriodicParts(arg1, Pi);
			if (parts.isPresent()) {
				if (parts.arg2().isInteger()) {
					// period n*Pi
					IInteger i = (IInteger) parts.arg2();
					if (i.isEven()) {
						return Cos(parts.arg1());
					} else {
						return Negate(F.Cos(parts.arg1()));
					}
				}
				if (parts.arg2().isFraction()) {
					// period (n/m)*Pi
					IFraction f = (IFraction) parts.arg2();
					IInteger[] divRem = f.divideAndRemainder();
					IFraction rest = F.fraction(divRem[1], f.getDenominator());
					if (!NumberUtil.isZero(divRem[0])) {

						if (divRem[0].isEven()) {
							return Cos(Plus(parts.arg1(), Times(rest, Pi)));
						} else {
							return Negate(Cos(Plus(parts.arg1(), Times(rest, Pi))));
						}
					}

					if (rest.equals(C1D2)) {
						// Cos(z) == Sin(Pi/2 - z)
						return Sin(Subtract(Divide(Pi, C2), arg1));
					}
				}
				if (F.True.equals(AbstractAssumptions.assumeInteger(parts.arg2()))) {
					// period n*Pi
					return Times(Power(CN1, parts.arg2()), Cos(parts.arg1()));
				}
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

	private final static class Exp extends AbstractArg1 implements INumeric {
		public Exp() {
		}

		@Override
		public IExpr e1ObjArg(final IExpr o) {
			return Power(F.E, o);
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1) {
				throw new UnsupportedOperationException();
			}
			return Math.exp(stack[top]);
		}
	}

	/**
	 * See <a href="http://en.wikipedia.org/wiki/Logarithm">Wikipedia - Logarithm</a>
	 * 
	 */
	private final static class Log extends AbstractArg12 implements INumeric, LogRules {

		/**
		 * <pre>
		 * { Log[1]=0, Log[E]=1, Log[E^(x_Integer)]:=x, Log[E^(x_Rational)]:=x, Log[E^(I)]=I, Log[Exp[-I]]=(-I),
		 * Log[0]=(-Infinity) }
		 * </pre>
		 */

		@Override
		public IAST getRuleAST() {
			// if (RULES == null) {
			// RULES = List(Set(Log(Power(E, Times(CN1, CI))), Times(CN1, CI)),
			// Set(Log(Power(E, CI)), CI),
			// Set(Log(C0), Times(CN1, CInfinity)), Set(Log(C1), C0), Set(Log(E),
			// C1),
			// SetDelayed(Log(Power(E, $p("x", $s("Integer")))), $s("x")),
			// SetDelayed(Log(Power(E, $p("x", $s("Rational")))), $s("x")));
			// }
			return RULES;
		}

		@Override
		public IExpr e1DblArg(final INum arg1) {
			return Num.valueOf(Math.log(arg1.getRealPart()));
		}

		@Override
		public IExpr e1DblComArg(final IComplexNum arg1) {
			return ComplexUtils.log((ComplexNum) arg1);
		}

		@Override
		public IExpr e2DblArg(final INum arg1, final INum arg2) {
			return Num.valueOf(Math.log(arg2.getRealPart()) / Math.log(arg1.getRealPart()));
		}

		@Override
		public IExpr e2IntArg(final IInteger arg1, final IInteger arg2) {
			return baseBLog(arg1, arg2);
		}

		/**
		 * Integer logarithm of <code>arg</code> for base <code>b</code>. Gives Log <sub>b</sub>(arg) or
		 * <code>Log(arg)/Log(b)</code>.
		 * 
		 * @param b
		 *            the base of the logarithm
		 * @param arg
		 * @return
		 */
		public static IExpr baseBLog(final IInteger b, final IInteger arg) {
			try {
				long l1 = b.toLong();
				long l2 = arg.toLong();
				double res = Math.log(l2) / Math.log(l1);
				if (F.isNumIntValue(res)) {
					int r = Double.valueOf(Math.round(res)).intValue();
					if (arg.equals(b.pow(r))) {
						return F.integer(r);
					}
				}
			} catch (ArithmeticException ae) {
				// toLong() method failed
			}
			return F.NIL;
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1) {
				throw new UnsupportedOperationException();
			}
			return Math.log(stack[top]);
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}

		@Override
		public IExpr e1ObjArg(IExpr expr) {
			if (expr.isPower()) {
				IExpr arg1 = expr.getAt(1);
				IExpr arg2 = expr.getAt(2);
				// arg2*Log(arg1)
				IExpr temp = F.eval(Times(arg2, F.Log(arg1)));
				IExpr imTemp = F.eval(F.Im(temp));
				if (imTemp.isSignedNumber()) {
					if (((ISignedNumber) imTemp).isGreaterThan(F.num(-1 * Math.PI))
							&& ((ISignedNumber) imTemp).isLessThan(F.num(Math.PI))) {
						// Log(arg1 ^ arg2) == arg2*Log(arg1) ||| -Pi <
						// Im(arg2*Log(arg1)) < Pi
						return temp;
					}
				}
				if (AbstractAssumptions.assumePositive(arg1) && F.True.equals(AbstractAssumptions.assumeReal(arg2))) {
					// Log(arg1 ^ arg2) == arg2*Log(arg1) ||| arg1 > 0 && arg2 is
					// Real
					return temp;
				}

			}
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(expr);
			if (negExpr.isPresent()) {
				if (negExpr.isPositiveResult()) {
					return F.Plus(F.Log(negExpr), F.Times(CI, F.Pi));
				}
			}
			return F.NIL;
		}
	}

	private final static class Log10 extends AbstractArg1 implements INumeric {

		@Override
		public IExpr e1ObjArg(final IExpr o) {
			return F.Log(F.C10, o);
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1) {
				throw new UnsupportedOperationException();
			}
			return Math.log10(stack[top]);
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}
	}

	private final static class Log2 extends AbstractArg1 implements INumeric {

		@Override
		public IExpr e1ObjArg(final IExpr o) {
			return F.Log(F.C2, o);
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1) {
				throw new UnsupportedOperationException();
			}
			return Math.log(stack[top] / 2.0d);
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}
	}

	/**
	 * Sine function.
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Trigonometric_functions">Trigonometric functions</a>
	 */
	private final static class Sin extends AbstractTrigArg1 implements INumeric, SinRules, DoubleUnaryOperator {

		@Override
		public double applyAsDouble(double operand) {
			return Math.sin(operand);
		}

		@Override
		public IExpr e1ApcomplexArg(Apcomplex arg1) {
			return F.complexNum(ApcomplexMath.sin(arg1));
		}

		@Override
		public IExpr e1ApfloatArg(Apfloat arg1) {
			return F.num(ApfloatMath.sin(arg1));
		}

		@Override
		public IExpr e1ComplexArg(final Complex arg1) {
			return F.complexNum(arg1.sin());
		}

		@Override
		public IExpr e1DblArg(final double arg1) {
			return num(Math.sin(arg1));
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1) {
				throw new UnsupportedOperationException();
			}
			return Math.sin(stack[top]);
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1) {
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Negate(Sin(negExpr));
			}
			IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
			if (imPart.isPresent()) {
				return F.Times(F.CI, F.Sinh(imPart));
			}
			IAST parts = AbstractFunctionEvaluator.getPeriodicParts(arg1, Pi);
			if (parts.isPresent()) {
				if (parts.arg2().isInteger()) {
					// period n*Pi
					IInteger i = (IInteger) parts.arg2();
					if (i.isEven()) {
						return Sin(parts.arg1());
					} else {
						return Times(CN1, Sin(parts.arg1()));
					}
				}

				if (parts.arg2().isFraction()) {
					// period (n/m)*Pi
					IFraction f = (IFraction) parts.arg2();
					IInteger[] divRem = f.divideAndRemainder();
					IFraction rest = F.fraction(divRem[1], f.getDenominator());
					if (!divRem[0].isZero()) {

						if (divRem[0].isEven()) {
							return Sin(Plus(parts.arg1(), Times(rest, Pi)));
						} else {
							return Times(CN1, Sin(Plus(parts.arg1(), Times(rest, Pi))));
						}
					}

					if (rest.equals(C1D2)) {
						// Sin(z) == Cos(Pi/2 - z)
						return Cos(Subtract(Divide(Pi, C2), arg1));
					}
				}

				if (F.True.equals(AbstractAssumptions.assumeInteger(parts.arg2()))) {
					// period n*Pi
					return Times(Power(CN1, parts.arg2()), Sin(parts.arg1()));
				}

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

	public static BinaryFunctorImpl<IExpr> integerLogFunction() {
		return new BinaryFunctorImpl<IExpr>() {
			/**
			 * Evaluate if Log(arg2)/Log(arg1) has an integer number result
			 */
			@Override
			public IExpr apply(IExpr arg1, IExpr arg2) {
				if (arg1.isInteger() && arg2.isInteger()) {
					return Log.baseBLog((IInteger) arg2, (IInteger) arg1);
				}
				return F.NIL;
			}
		};
	}

	/**
	 * Tan
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Trigonometric_functions">Trigonometric functions</a>
	 */
	private final static class Tan extends AbstractTrigArg1 implements INumeric, TanRules, DoubleUnaryOperator {

		@Override
		public double applyAsDouble(double operand) {
			return Math.tan(operand);
		}

		@Override
		public IExpr e1ApcomplexArg(Apcomplex arg1) {
			return F.complexNum(ApcomplexMath.tan(arg1));
		}

		@Override
		public IExpr e1ApfloatArg(Apfloat arg1) {
			return F.num(ApfloatMath.tan(arg1));
		}

		@Override
		public IExpr e1ComplexArg(final Complex arg1) {
			return F.complexNum(arg1.tan());
		}

		@Override
		public IExpr e1DblArg(final double arg1) {
			return F.num(Math.tan(arg1));
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1) {
				throw new UnsupportedOperationException();
			}
			return Math.tan(stack[top]);
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1) {
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Negate(Tan(negExpr));
			}
			IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
			if (imPart.isPresent()) {
				return F.Times(F.CI, F.Tanh(imPart));
			}
			IAST parts = AbstractFunctionEvaluator.getPeriodicParts(arg1, F.Pi);
			if (parts.isPresent()) {
				if (parts.arg2().isInteger()) {
					// period Pi
					return F.Tan(parts.arg1());
				}

				if (parts.arg2().isFraction()) {
					// period (n/m)*Pi
					IFraction f = (IFraction) parts.arg2();
					IInteger[] divRem = f.divideAndRemainder();
					IFraction rest = F.fraction(divRem[1], f.getDenominator());
					if (!divRem[0].isZero()) {
						return Tan(Plus(parts.arg1(), Times(rest, Pi)));
					}

					if (rest.equals(C1D2)) {
						// Tan(z) == Cot(Pi/2 - z)
						return Cot(Subtract(Divide(Pi, C2), arg1));
					}
				}

				if (F.True.equals(AbstractAssumptions.assumeInteger(parts.arg2()))) {
					// period Pi
					return F.Tan(parts.arg1());
				}
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

	final static ExpTrigsFunctions CONST = new ExpTrigsFunctions();

	public static ExpTrigsFunctions initialize() {
		return CONST;
	}

	private ExpTrigsFunctions() {

	}

}
