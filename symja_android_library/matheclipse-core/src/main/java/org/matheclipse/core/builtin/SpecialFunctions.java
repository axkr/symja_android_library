package org.matheclipse.core.builtin;

import static org.matheclipse.core.expression.F.BernoulliB;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CComplexInfinity;
import static org.matheclipse.core.expression.F.CInfinity;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.CNInfinity;
import static org.matheclipse.core.expression.F.Erf;
import static org.matheclipse.core.expression.F.Factorial;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.NIL;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Pi;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.QQ;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sum;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.Zeta;
import static org.matheclipse.core.expression.F.k;

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
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.PolynomialsUtils;
import org.matheclipse.core.reflection.system.rules.LegendrePRules;
import org.matheclipse.core.reflection.system.rules.ProductLogRules;
import org.matheclipse.core.reflection.system.rules.StruveHRules;
import org.matheclipse.core.reflection.system.rules.StruveLRules;

public class SpecialFunctions {
	static {
		F.ChebyshevT.setEvaluator(new ChebyshevT());
		F.ChebyshevU.setEvaluator(new ChebyshevU());
		F.Erf.setEvaluator(new Erf());
		F.Erfc.setEvaluator(new Erfc());
		F.GegenbauerC.setEvaluator(new GegenbauerC());
		F.HermiteH.setEvaluator(new HermiteH());
		F.InverseErf.setEvaluator(new InverseErf());
		F.InverseErfc.setEvaluator(new InverseErfc());
		F.LaguerreL.setEvaluator(new LaguerreL());
		F.LegendreP.setEvaluator(new LegendreP());
		F.ProductLog.setEvaluator(new ProductLog());
		F.StruveH.setEvaluator(new StruveH());
		F.StruveL.setEvaluator(new StruveL());
		F.Zeta.setEvaluator(new Zeta());
	}

	private final static class ChebyshevT extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);
			IExpr arg1 = ast.arg1();
			IExpr arg2 = ast.arg2();
			if (arg1.isInteger()) {
				int degree = ((IInteger) arg1).toIntDefault(Integer.MIN_VALUE);
				if (degree == Integer.MIN_VALUE) {
					return F.NIL;
				}
				if (degree < 0) {
					degree *= -1;
				}
				return PolynomialsUtils.createChebyshevPolynomial(degree, ast.arg2());
			}
			if (arg2.isZero()) {
				return F.Cos(F.Times(F.C1D2, F.Pi, arg1));
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}
	}

	private final static class ChebyshevU extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);
			IExpr arg1 = ast.arg1();
			IExpr arg2 = ast.arg2();
			if (arg1.isInteger()) {
				int degree = ((IInteger) arg1).toIntDefault(Integer.MIN_VALUE);
				if (degree == Integer.MIN_VALUE) {
					return F.NIL;
				}

				if (degree < 0) {
					return F.NIL;
				}
				if (degree == 0) {
					return F.C1;
				}
				if (degree == 1) {
					return F.Times(F.C2, arg2);
				}
				return F.Expand(F.Subtract(F.Times(F.C2, arg2, F.ChebyshevU(F.integer(degree - 1), arg2)),
						F.ChebyshevU(F.integer(degree - 2), arg2)));
			}
			if (arg2.isOne()) {
				return F.Plus(F.C1, arg1);
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

	private final static class GegenbauerC extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);
			IExpr n = ast.arg1();
			IExpr z = ast.arg2();

			int nInt = n.toIntDefault(Integer.MIN_VALUE);
			if (nInt > Integer.MIN_VALUE) {
				if (nInt == 0) {
					return F.CComplexInfinity;
				}
				if (nInt == 1) {
					// 2*z
					return F.Times(F.C2, z);
				}
				if (nInt == 2) {
					// 2*z^2 - 1
					return F.Plus(F.CN1, F.Times(F.C2, F.Sqr(z)));
				}
				if (nInt > 2) {
					// KroneckerDelta(n,0)/n + (2^n/n)*z^n + Sum(((-1)^k*(n - k - 1)!*(2 z)^(n - 2 k))/(k! * (n -
					// 2*k)!), {k, 1, Floor(n/2)})
					return Plus(
							Times(Power(C2,
									n),
									Power(n,
											-1),
									Power(z, n)),
							Times(Power(n, -1), F.KroneckerDelta(F.C0, n)),
							Sum(Times(Power(CN1, k), Power(Times(C2, z), Plus(Times(F.CN2, k), n)),
									Power(Times(Factorial(k), Factorial(Plus(Times(F.CN2, k), n))), -1),
									Factorial(Plus(CN1, Negate(k), n))), List(k, C1, F.Floor(Times(C1D2, n)))));
				}
			}

			int zInt = z.toIntDefault(Integer.MIN_VALUE);
			if (zInt > Integer.MIN_VALUE) {
				if (zInt == 0) {
					// 2 * (1/v) * Cos(1/2*Pi*v)
					return F.Times(F.C2, F.pow(n, F.CN1), F.Cos(F.Times(C1D2, F.Pi, n)));
				}
				if (zInt == 1) {
					// 2 / v
					return F.Divide(F.C2, n);
				}
				if (zInt == -1) {
					// (2/v)*Cos(Pi*v)
					return F.Times(F.C2, F.pow(n, F.CN1), F.Cos(F.Times(F.Pi, n)));
				}
			}

			if (n.equals(F.C1D2)) {
				// 4*Sqrt((1+z)/2)
				return F.Times(F.C4, F.Sqrt(F.Times(F.C1D2, F.Plus(F.C1, z))));
			}
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(n);
			if (negExpr.isPresent()) {
				return F.GegenbauerC(negExpr, z).negate();
			}
			if (n.isInteger() && n.isPositive()) {
				negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(z);
				if (negExpr.isPresent()) {
					return F.Times(F.Power(F.CN1, n), F.GegenbauerC(n, negExpr));
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

	private final static class HermiteH extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			int degree = ast.arg1().toIntDefault(Integer.MIN_VALUE);
			if (degree > Integer.MIN_VALUE) {
				return PolynomialsUtils.createHermitePolynomial(degree, ast.arg2());
			}
			return F.NIL;
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

	private final static class LaguerreL extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);
			int degree = ast.arg1().toIntDefault(Integer.MIN_VALUE);
			if (degree > Integer.MIN_VALUE) {
				return PolynomialsUtils.createLaguerrePolynomial(degree, ast.arg2());
			}
			return F.NIL;
		}

	}

	private final static class LegendreP extends AbstractFunctionEvaluator implements LegendrePRules {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			int degree = ast.arg1().toIntDefault(Integer.MIN_VALUE);
			if (degree > Integer.MIN_VALUE) {
				return PolynomialsUtils.createLegendrePolynomial(degree, ast.arg2());
			}
			return F.NIL;
		}

		@Override
		public IAST getRuleAST() {
			return RULES;
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
						gamma = gamma * (Math.pow((1 + ((double) 1 / i)), z) * Math.pow((1 + ((double) z / i)), -1));
					}
					gamma = ((double) 1 / z) * gamma;
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

			}
			if (arg1.isInfinity()) {
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
					IInteger n = (IInteger) a;
					if (n.isNegative()) {
						n = n.negate();
						// Zeta(s, -n) :=
						// Zeta(s) + Sum(1/k^s, {k, 1, n})
						return Plus(Sum(Power(Power(k, s), -1), List(k, C1, n)), Zeta(s));
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

	final static SpecialFunctions CONST = new SpecialFunctions();

	public static SpecialFunctions initialize() {
		return CONST;
	}

	private SpecialFunctions() {

	}

}
