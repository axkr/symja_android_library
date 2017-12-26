package org.matheclipse.core.builtin;

import static org.matheclipse.core.expression.F.BernoulliB;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.CComplexInfinity;
import static org.matheclipse.core.expression.F.CInfinity;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.CNInfinity;
import static org.matheclipse.core.expression.F.Erf;
import static org.matheclipse.core.expression.F.Factorial;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.NIL;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Pi;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.QQ;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.Zeta;

import java.math.BigDecimal;
import java.util.function.DoubleUnaryOperator;

import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.hipparchus.exception.MathIllegalStateException;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractArg12;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.rules.BetaRules;
import org.matheclipse.core.reflection.system.rules.PolyGammaRules;
import org.matheclipse.core.reflection.system.rules.PolyLogRules;
import org.matheclipse.core.reflection.system.rules.ProductLogRules;
import org.matheclipse.core.reflection.system.rules.StieltjesGammaRules;
import org.matheclipse.core.reflection.system.rules.StruveHRules;
import org.matheclipse.core.reflection.system.rules.StruveLRules;

public class SpecialFunctions {
	static {
		F.Beta.setEvaluator(new Beta());
		F.BetaRegularized.setEvaluator(new BetaRegularized());
		F.Erf.setEvaluator(new Erf());
		F.Erfc.setEvaluator(new Erfc());
		F.GammaRegularized.setEvaluator(new GammaRegularized());
		F.HypergeometricPFQRegularized.setEvaluator(new HypergeometricPFQRegularized());
		F.InverseErf.setEvaluator(new InverseErf());
		F.InverseErfc.setEvaluator(new InverseErfc());
		F.PolyGamma.setEvaluator(new PolyGamma());
		F.PolyLog.setEvaluator(new PolyLog());
		F.ProductLog.setEvaluator(new ProductLog());
		F.StieltjesGamma.setEvaluator(new StieltjesGamma());
		F.StruveH.setEvaluator(new StruveH());
		F.StruveL.setEvaluator(new StruveL());
		F.Zeta.setEvaluator(new Zeta());
	}

	private static class Beta extends AbstractFunctionEvaluator implements BetaRules {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			IExpr a = ast.arg1();
			IExpr b = ast.arg2();

			if (a.isNumber() && b.isNumber()) {
				if (a.isInteger() && a.isPositive() && b.isInteger() && b.isPositive()) {
					return Times(Factorial(Plus(CN1, a)), Factorial(Plus(CN1, b)),
							Power(Factorial(Plus(CN1, a, b)), -1));
				}
				return F.Times(F.Gamma(a), F.Gamma(b), F.Power(F.Gamma(F.Plus(a, b)), -1));
			}
			IExpr s = a.plus(F.C1).subtract(b);
			if (s.isZero()) {
				return F.Power(F.Times(a, b, F.CatalanNumber(a)), -1);
			}
			IExpr sum = a.plus(b);
			if (sum.isInteger() && sum.isNegative()) {
				return F.C0;
			}
			return F.NIL;
		}

		@Override
		public IAST getRuleAST() {
			return RULES;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.ORDERLESS | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}

	}

	private static class BetaRegularized extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 4);

			IExpr z = ast.arg1();
			IExpr a = ast.arg2();
			IExpr n = ast.arg3();
			if (n.isInteger()) {
				if (n.isNegative()) {
					// for n>=0; BetaRegularized(z, a, -n)=0
					return F.C0;
				}
				int ni = n.toIntDefault(-1);
				if (ni >= 0) {

					IASTAppendable sum = F.PlusAlloc(ni);
					// {k, 0, n - 1}
					for (int k = 0; k < ni; k++) {
						// (Pochhammer(a, k)*(1 - z)^k)/k!
						IInteger kk = F.integer(k);
						sum.append(F.Times(F.Power(F.Plus(F.C1, F.Negate(z)), kk), F.Power(F.Factorial(kk), -1),
								F.Pochhammer(a, kk)));
					}
					// z^a * sum
					return F.Times(F.Power(z, a), sum);
				}
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
	 * Returns the error function.
	 * 
	 * @see org.matheclipse.core.reflection.system.InverseErf
	 */
	private final static class Erf extends AbstractTrigArg1 implements INumeric, DoubleUnaryOperator {

		@Override
		public double applyAsDouble(double operand) {
			return org.hipparchus.special.Erf.erf(operand);
		}

		@Override
		public IExpr e1DblArg(final double arg1) {
			try {
				return Num.valueOf(org.hipparchus.special.Erf.erf(arg1));
			} catch (final MathIllegalStateException e) {
			}
			return F.NIL;
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1) {
				throw new UnsupportedOperationException();
			}
			try {
				return org.hipparchus.special.Erf.erf(stack[top]);
			} catch (final MathIllegalStateException e) {
			}
			throw new UnsupportedOperationException();
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1) {
			if (arg1.isZero()) {
				return F.C0;
			}
			if (arg1.equals(CInfinity)) {
				return F.C1;
			}
			if (arg1.equals(CNInfinity)) {
				return F.CN1;
			}
			if (arg1.isComplexInfinity()) {
				return F.Indeterminate;
			}
			if (arg1.isDirectedInfinity(F.CI) || arg1.isDirectedInfinity(F.CNI)) {
				return arg1;
			}
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Negate(Erf(negExpr));
			}
			return F.NIL;
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() == 3) {
				return F.Subtract(F.Erf(ast.arg2()), F.Erf(ast.arg1()));
			}
			return super.evaluate(ast, engine);
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}
	}

	private final static class Erfc extends AbstractTrigArg1 implements INumeric {

		@Override
		public IExpr e1DblArg(final double arg1) {
			if (arg1 >= 0. && arg1 <= 2.0) {
				try {
					return Num.valueOf(org.hipparchus.special.Erf.erfc(arg1));
				} catch (final MathIllegalStateException e) {
				}
			}
			return F.NIL;
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1) {
				throw new UnsupportedOperationException();
			}
			try {
				double arg1 = stack[top];
				if (arg1 >= 0. && arg1 <= 2.0) {
					return org.hipparchus.special.Erf.erfc(arg1);
				}
			} catch (final MathIllegalStateException e) {
			}
			throw new UnsupportedOperationException();
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1) {
			if (arg1.isSignedNumber()) {
				if (arg1.isZero()) {
					return F.C1;
				}
				if (arg1.equals(CInfinity)) {
					return F.C0;
				}
				if (arg1.equals(CNInfinity)) {
					return F.C2;
				}
				if (arg1.isComplexInfinity()) {
					return F.Indeterminate;
				}
				if (arg1.isDirectedInfinity(F.CI) || arg1.isDirectedInfinity(F.CNI)) {
					return arg1.negate();
				}
			}
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return F.Subtract(F.C2, F.Erfc(negExpr));
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}
	}

	private static class GammaRegularized extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);
			
			IExpr a = ast.arg1();
			IExpr z = ast.arg2();
			if (a.isZero()) {
				return F.C0;
			} else if (a.equals(F.C1D2)) {
				// Erfc(Sqrt(z))
				return F.Erfc(F.Sqrt(z));
			} else if (a.isOne()) {
				// E^(-z)
				return F.Power(F.E, F.Negate(F.z));
			} else if (a.isInteger() && a.isNegative()) {
				return F.C0;
			}

			if (z.isZero()) {
				IExpr temp = a.re();
				if (temp.isPositive()) {
					return F.C1;
				}
				if (temp.isNegative()) {
					return F.CComplexInfinity;
				}
			} else if (z.isMinusOne()) {
				// (E/Gamma[a])*Subfactorial(a - 1)
				return F.Times(F.E, F.Power(F.Gamma(a), -1), F.Subfactorial(F.Plus(F.CN1, a)));
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}

	}

	private static class HypergeometricPFQRegularized extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}

	}

	/**
	 * Returns the inverse erf.
	 * 
	 * @see org.matheclipse.core.reflection.system.Erf
	 */
	private final static class InverseErf extends AbstractTrigArg1 implements INumeric {

		@Override
		public IExpr e1DblArg(final double arg1) {
			try {
				if (arg1 >= -1.0 && arg1 <= 1.0) {
					return Num.valueOf(org.hipparchus.special.Erf.erfInv(arg1));
				}
			} catch (final MathIllegalStateException e) {
			}
			return F.NIL;
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1) {
				throw new UnsupportedOperationException();
			}
			try {
				double arg1 = stack[top];
				if (arg1 >= -1.0 && arg1 <= 1.0) {
					return org.hipparchus.special.Erf.erfInv(arg1);
				}
			} catch (final MathIllegalStateException e) {
			}
			throw new UnsupportedOperationException();
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1) {
			if (arg1.isSignedNumber()) {
				if (arg1.isZero()) {
					return F.C0;
				}
				if (arg1.isOne()) {
					return F.CInfinity;
				}
				if (arg1.isMinusOne()) {
					return F.CNInfinity;
				}
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
	 * Returns the inverse erf.
	 * 
	 * @see org.matheclipse.core.reflection.system.Erf
	 */
	private final static class InverseErfc extends AbstractTrigArg1 implements INumeric {

		@Override
		public IExpr e1DblArg(final double arg1) {
			if (arg1 >= 0. && arg1 <= 2.0) {
				try {
					return Num.valueOf(org.hipparchus.special.Erf.erfcInv(arg1));
				} catch (final MathIllegalStateException e) {
				}
			}
			return F.NIL;
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1) {
				throw new UnsupportedOperationException();
			}
			try {
				double arg1 = stack[top];
				if (arg1 >= 0. && arg1 <= 2.0) {
					return org.hipparchus.special.Erf.erfcInv(arg1);
				}
			} catch (final MathIllegalStateException e) {
			}
			throw new UnsupportedOperationException();
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1) {
			if (arg1.isSignedNumber()) {
				if (arg1.isZero()) {
					return F.CInfinity;
				}
				if (arg1.isOne()) {
					return F.C0;
				}
				if (arg1.equals(F.C2)) {
					return F.CNInfinity;
				}
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}
	}

	private static class PolyGamma extends AbstractFunctionEvaluator implements PolyGammaRules {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			IExpr arg1 = ast.arg1();
			if (ast.isAST1()) {
				if (arg1.isPositive()) {

				} else {
					if (arg1.isInteger()) {
						return F.CComplexInfinity;
					}
				}
			} else {
				IExpr arg2 = ast.arg2();
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

	private static class PolyLog extends AbstractFunctionEvaluator implements PolyLogRules {

		/**
		 * See <a href= "https://github.com/sympy/sympy/blob/master/sympy/functions/special/zeta_functions.py">Sympy -
		 * zeta_functions.py</a>
		 */
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			IExpr arg1 = ast.arg1();
			IExpr arg2 = ast.arg2();

			if (arg2.isZero()) {
				return F.C0;
			}
			if (arg2.isOne()) {
				IExpr temp = arg1.re();
				if (temp.isSignedNumber()) {
					ISignedNumber num = (ISignedNumber) temp;
					if (num.isOne()) {
						return F.Indeterminate;
					} else if (num.isGreaterThan(F.C1)) {
						return F.Zeta(arg1);
					} else {
						return F.CComplexInfinity;
					}
				}
			} else if (arg2.isMinusOne()) {
				// (2^(1-arg1)-1)*Zeta(arg1)
				return Times(Plus(CN1, Power(C2, Plus(C1, Negate(arg1)))), Zeta(arg1));
			}

			if (arg1.isSignedNumber()) {
				if (arg1.isZero()) {
					// arg2/(1 - arg2)
					return Times(arg2, Power(Plus(C1, Negate(arg2)), -1));
				} else if (arg1.isOne()) {
					// -Log(1 - arg2))
					return Negate(Log(Plus(C1, Negate(arg2))));
				} else if (arg1.isMinusOne()) {
					// arg2/(arg2 - 1)^2
					return Times(arg2, Power(Plus(C1, Negate(arg2)), -2));
				} else if (arg1.equals(F.CN2)) {
					// -((arg2*(1 + arg2))/(arg2 - 1)^3)
					return Times(CN1, arg2, Plus(C1, arg2), Power(Plus(CN1, arg2), -3));
				} else if (arg1.equals(F.CN3)) {
					// (arg2*(1 + 4*arg2 + arg2^2))/(arg2 - 1)^4
					return Times(arg2, Plus(C1, Times(C4, arg2), Sqr(arg2)), Power(Plus(C1, Negate(arg2)), -4));
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

	/**
	 * <p>
	 * Lambert W function
	 * </p>
	 * 
	 * See: <a href="http://en.wikipedia.org/wiki/Lambert_W_function">Wikipedia - Lambert W function</a>
	 */
	private final static class ProductLog extends AbstractArg12 implements ProductLogRules {
		@Override
		public IAST getRuleAST() {
			return RULES;
		}

		@Override
		public IExpr e1DblArg(final INum d) {
			try {
				return F.num(ApfloatMath.w(new Apfloat(new BigDecimal(d.doubleValue()), Config.MACHINE_PRECISION)));
			} catch (Exception ce) {

			}
			return F.complexNum(
					ApcomplexMath.w(new Apfloat(new BigDecimal(d.doubleValue()), Config.MACHINE_PRECISION)));
		}

		@Override
		public IExpr e1DblComArg(IComplexNum arg1) {
			Apcomplex c = new Apcomplex(new Apfloat(new BigDecimal(arg1.getRealPart()), Config.MACHINE_PRECISION),
					new Apfloat(new BigDecimal(arg1.getImaginaryPart()), Config.MACHINE_PRECISION));
			return F.complexNum(ApcomplexMath.w(c));
		}

		@Override
		public IExpr e1ApfloatArg(Apfloat arg1) {
			try {
				return F.num(ApfloatMath.w(arg1));
			} catch (Exception ce) {

			}
			return F.complexNum(ApcomplexMath.w(arg1));
		}

		@Override
		public IExpr e1ApcomplexArg(Apcomplex arg1) {
			return F.complexNum(ApcomplexMath.w(arg1));
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}

	}

	private static class StieltjesGamma extends AbstractFunctionEvaluator implements StieltjesGammaRules {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			IExpr arg1 = ast.arg1();
			if (ast.isAST1()) {

			} else {
				IExpr arg2 = ast.arg2();
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

	private final static class StruveH extends AbstractFunctionEvaluator implements StruveHRules {

		public IExpr e2DblArg(final INum d0, final INum d1) {
			double v = d0.getReal();
			double z = d1.getReal();
			try {
				final double iterationSum = 100;
				double fraction = 0;
				double fractionFactor = Math.pow((0.5 * z), v + 1);
				for (int i = 0; i < iterationSum; ++i) {
					double fractionTopPart = Math.pow(-1, i) * Math.pow(0.5 * z, 2 * i);
					double fractionBottomPart = gammaEuler(i + 1.5) * gammaEuler(i + v + 1.5);
					fraction = fraction + (fractionTopPart / fractionBottomPart);
				}
				return F.num(fractionFactor * fraction);
			} catch (Exception e) {
				throw e;
			}
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			IExpr arg1 = ast.arg1();
			IExpr arg2 = ast.arg2();
			if (arg2.isZero()) {
				IExpr re = arg1.re();
				if (re.isMinusOne()) {
					// StruveH(n_,0):=Indeterminate/;Re(n)==(-1)
					return F.Indeterminate;
				}
				IExpr temp = re.greaterThan(F.CN1);
				if (temp.isTrue()) {
					// StruveH(n_,0):=0/;Re(n)>(-1)
					return F.C0;
				}
				if (temp.isFalse()) {
					// StruveH(n_,0):=ComplexInfinity/;Re(n)<(-1)
					return F.CComplexInfinity;
				}
			} else if (arg1 instanceof INum && arg2 instanceof INum) {
				return e2DblArg((INum) arg1, (INum) arg2);
			} else {
				IExpr negArg2 = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg2);
				if (negArg2.isPresent()) {
					// StruveH(n_, arg2_)) := ((-(arg2)^n) StruveH(n,
					// negArg2))/negArg2^n
					return F.Times(F.CN1, F.Power(arg2, arg1), F.Power(negArg2, F.Negate(arg1)),
							F.StruveH(arg1, negArg2));
				}
			}
			return F.NIL;
		}

		protected static double gammaEuler(double z) {
			try {
				double gamma = 1;
				if (z > 0) {
					final int iterationSum = 10000;
					for (int i = 1; i < iterationSum; ++i) {
						gamma = gamma * (Math.pow((1 + ((double) 1 / i)), z) * Math.pow((1 + (z / i)), -1));
					}
					gamma = (1 / z) * gamma;
				} else
					return 0;
				return gamma;
			} catch (Exception e) {
				throw e;
			}
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

	private final static class StruveL extends AbstractFunctionEvaluator implements StruveLRules {

		public IExpr e2DblArg(final INum d0, final INum d1) {
			double v = d0.getReal();
			double z = d1.getReal();
			try {
				final int iterationSum = 100;
				double fraction = 0;
				double fractionFactor = Math.pow((0.5 * z), v + 1);
				for (int i = 0; i < iterationSum; ++i) {
					double fractionTopPart = 1 * Math.pow((0.5 * z), (2 * i));
					double fractionBottomPart = StruveH.gammaEuler(i + (1.5)) * StruveH.gammaEuler(i + v + (1.5));
					fraction = fraction + (fractionTopPart / fractionBottomPart);
				}
				return F.num(fractionFactor * fraction);
			} catch (Exception e) {
				throw e;
			}
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			IExpr arg1 = ast.arg1();
			IExpr arg2 = ast.arg2();
			if (arg2.isZero()) {
				IExpr re = arg1.re();
				if (re.isMinusOne()) {
					// StruveL(n_,0):=Indeterminate/;Re(n)==(-1)
					return F.Indeterminate;
				}
				IExpr temp = re.greaterThan(F.CN1);
				if (temp.isTrue()) {
					// StruveL(n_,0):=0/;Re(n)>(-1)
					return F.C0;
				}
				if (temp.isFalse()) {
					// StruveL(n_,0):=ComplexInfinity/;Re(n)<(-1)
					return F.CComplexInfinity;
				}
			} else if (arg1 instanceof INum && arg2 instanceof INum) {
				return e2DblArg((INum) arg1, (INum) arg2);
			} else {
				IExpr negArg2 = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg2);
				if (negArg2.isPresent()) {
					// StruveL(n_, arg2_)) := ((-(arg2)^n) StruveL(n,
					// negArg2))/negArg2^n
					return F.Times(F.CN1, F.Power(arg2, arg1), F.Power(negArg2, F.Negate(arg1)),
							F.StruveL(arg1, negArg2));
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

	private final static class Zeta extends AbstractArg12 {

		@Override
		public IExpr e1DblArg(INum d) {
			// TODO add implementation
			return F.NIL;
		}

		@Override
		public IExpr e1DblComArg(IComplexNum c) {
			// TODO add implementation
			return F.NIL;
		}

		@Override
		public IExpr e1ObjArg(final IExpr arg1) {
			if (arg1.isZero()) {
				return CN1D2;
			}
			if (arg1.isOne()) {
				return CComplexInfinity;
			}
			if (arg1.isMinusOne()) {
				// -1/12
				return QQ(-1, 12);
			}
			if (arg1.isInteger()) {
				IInteger n = (IInteger) arg1;

				if (!n.isPositive()) {
					if (n.isEven()) {
						return F.C0;
					}
					// Zeta(-n) :=
					// ((-1)^n/(n + 1))*BernoulliB(n + 1)
					n = n.negate();
					IExpr n1 = n.add(C1);
					return Times(Power(CN1, n), Power(n1, -1), BernoulliB(n1));
				}
				if (n.isEven()) {
					// Zeta(2*n) :=
					// ((((-1)^(n-1)*2^(-1+2*n)*Pi^(2*n))/(2*n)!)*BernoulliB(2*n)
					n = n.shiftRight(1);
					return Times(Power(CN1, Plus(CN1, n)), Power(C2, Plus(CN1, Times(C2, n))), Power(Pi, Times(C2, n)),
							Power(Factorial(Times(C2, n)), -1), BernoulliB(Times(C2, n)));
				}

			} else if (arg1.isInfinity()) {
				return C1;
			}
			return NIL;
		}

		@Override
		public IExpr e2ObjArg(IExpr s, IExpr a) {
			if (a.isZero()) {
				return Zeta(s);
			}
			if (a.isMinusOne()) {
				return Plus(C1, Zeta(s));
			}
			if (s.isInteger() && a.isInteger()) {
				if (!s.isPositive() || ((IInteger) s).isEven()) {
					int nInt = ((IInteger) a).toIntDefault(0);
					if (nInt < 0) {
						nInt *= -1;
						// Zeta(s, -n) := Zeta(s) + Sum(1/k^s, {k, 1, n})
						return Plus(F.sum(k -> Power(Power(k, s), -1), 1, nInt), Zeta(s));
					}
				}
			}
			if (a.equals(C2)) {
				return Plus(CN1, Zeta(s));
			}
			if (a.equals(C1D2)) {
				return Times(Plus(CN1, Sqr(s)), Zeta(s));
			}
			return NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}
	}

	private final static SpecialFunctions CONST = new SpecialFunctions();

	public static SpecialFunctions initialize() {
		return CONST;
	}

	private SpecialFunctions() {

	}

}
