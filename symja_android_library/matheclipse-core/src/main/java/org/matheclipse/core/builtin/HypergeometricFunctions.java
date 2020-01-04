package org.matheclipse.core.builtin;

import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Factorial;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Times;

import java.util.function.DoubleUnaryOperator;

import org.hipparchus.complex.Complex;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.functions.GammaJS;
import org.matheclipse.core.builtin.functions.HypergeometricJS;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ThrowException;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.rules.Hypergeometric0F1Rules;
import org.matheclipse.core.reflection.system.rules.Hypergeometric2F1Rules;

public class HypergeometricFunctions {
	/**
	 * 
	 * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation in static
	 * initializer</a>
	 */
	private static class Initializer {

		private static void init() {
			F.CosIntegral.setEvaluator(new CosIntegral());
			F.CoshIntegral.setEvaluator(new CoshIntegral());
			F.ExpIntegralE.setEvaluator(new ExpIntegralE());
			F.ExpIntegralEi.setEvaluator(new ExpIntegralEi());
			F.FresnelC.setEvaluator(new FresnelC());
			F.FresnelS.setEvaluator(new FresnelS());
			F.GegenbauerC.setEvaluator(new GegenbauerC());
			F.Hypergeometric0F1.setEvaluator(new Hypergeometric0F1());
			F.Hypergeometric1F1.setEvaluator(new Hypergeometric1F1());
			F.Hypergeometric2F1.setEvaluator(new Hypergeometric2F1());
			F.HypergeometricPFQ.setEvaluator(new HypergeometricPFQ());
			// F.HypergeometricU.setEvaluator(new HypergeometricU());
			F.LogIntegral.setEvaluator(new LogIntegral());
			F.SinIntegral.setEvaluator(new SinIntegral());
			F.SinhIntegral.setEvaluator(new SinhIntegral());
		}
	}

	private static class CosIntegral extends AbstractTrigArg1 implements INumeric, DoubleUnaryOperator {
		@Override
		public double applyAsDouble(double operand) {
			return de.lab4inf.math.functions.CosineIntegral.ci(operand);
		}

		@Override
		public IExpr e1DblArg(final double arg1) {
			return F.num(de.lab4inf.math.functions.CosineIntegral.ci(arg1));
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1) {
				throw new UnsupportedOperationException();
			}
			return de.lab4inf.math.functions.CosineIntegral.ci(stack[top]);
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
			if (arg1.isZero()) {
				return F.CNInfinity;
			}
			if (arg1.isInfinity()) {
				return F.C0;
			}
			if (arg1.isNegativeInfinity()) {
				return F.Times(F.CI, F.Pi);
			}
			if (arg1.isDirectedInfinity(F.CI) || arg1.isDirectedInfinity(F.CNI)) {
				return F.CInfinity;
			}
			if (arg1.isComplexInfinity()) {
				return F.Indeterminate;
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}
	}

	private static class CoshIntegral extends AbstractTrigArg1 { // implements INumeric, DoubleUnaryOperator {

		// @Override
		// public double applyAsDouble(double z) {
		// if (F.isZero(z)) {
		// return Double.NEGATIVE_INFINITY;
		// }
		// // 1/4*(2*(ExpIntegralEi(-z)+ExpIntegralEi(z))+Log(-1/z)+Log(1/z)-Log(-z)+3*Log(z))
		// return 0.25 * (2.0 * (ExpIntegralEi.CONST.applyAsDouble(-z) + ExpIntegralEi.CONST.applyAsDouble(z))
		// + Math.log(-1 / z) + Math.log(1 / z) - Math.log(-z) + 3 * Math.log(z));
		// }
		//
		// @Override
		// public IExpr e1DblArg(final double arg1) {
		// if (F.isZero(arg1)) {
		// return F.CNInfinity;
		// }
		// return F.num(applyAsDouble(arg1));
		// }
		//
		// @Override
		// public double evalReal(final double[] stack, final int top, final int size) {
		// if (size != 1) {
		// throw new UnsupportedOperationException();
		// }
		// return applyAsDouble(stack[top]);
		// }

		@Override
		public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
			if (arg1.isZero()) {
				return F.CNInfinity;
			}
			if (arg1.isInfinity()) {
				return F.CInfinity;
			}
			if (arg1.isNegativeInfinity()) {
				return F.CInfinity;
			}
			if (arg1.isDirectedInfinity(F.CI)) {
				return F.Times(F.CPiHalf, F.CI);
			}
			if (arg1.isDirectedInfinity(F.CNI)) {
				return F.Times(F.CNPiHalf, F.CI);
			}
			if (arg1.isComplexInfinity()) {
				return F.Indeterminate;
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}
	}

	private static class ExpIntegralE extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(IAST ast, EvalEngine engine) {
			IExpr n = ast.arg1();
			IExpr z = ast.arg2();
			if (n.isZero()) {
				// 1/(E^z*z)
				return F.Power(F.Times(z, F.Power(F.E, z)), -1);
			}
			if (z.isZero()) {
				if (n.re().greaterThan(F.C1).isTrue()) {
					// 1/(n-1)
					return F.Power(F.Plus(n, F.CN1), -1);
				}
				if (n.re().lessThan(F.C1).isTrue()) {
					return F.CComplexInfinity;
				}
			}
			return F.NIL;
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_2_2;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}
	}

	private static class ExpIntegralEi extends AbstractTrigArg1 implements INumeric, DoubleUnaryOperator {
		@Override
		public double applyAsDouble(double operand) {
			if (F.isZero(operand)) {
				return Double.NEGATIVE_INFINITY;
			}
			return de.lab4inf.math.functions.ExponentialIntegalFunction.ei(operand);
		}

		@Override
		public IExpr e1DblArg(final double arg1) {
			if (F.isZero(arg1)) {
				return F.CNInfinity;
			}
			return F.num(de.lab4inf.math.functions.ExponentialIntegalFunction.ei(arg1));
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1) {
				throw new UnsupportedOperationException();
			}
			if (F.isZero(stack[top])) {
				return Double.NEGATIVE_INFINITY;
			}
			return de.lab4inf.math.functions.ExponentialIntegalFunction.ei(stack[top]);
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
			if (arg1.isZero()) {
				return F.CNInfinity;
			}
			if (arg1.isInfinity()) {
				return F.CInfinity;
			}
			if (arg1.isNegativeInfinity()) {
				return F.C0;
			}
			if (arg1.isDirectedInfinity(F.CI)) {
				return F.Times(F.CI, F.Pi);
			}
			if (arg1.isDirectedInfinity(F.CNI)) {
				return F.Times(F.CNI, F.Pi);
			}
			if (arg1.isComplexInfinity()) {
				return F.Indeterminate;
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}
	}

	private static class FresnelC extends AbstractTrigArg1 implements INumeric, DoubleUnaryOperator {

		@Override
		public double applyAsDouble(double operand) {
			return de.lab4inf.math.functions.FresnelC.fresnelC(operand);
		}

		@Override
		public IExpr e1ComplexArg(final Complex c) {
			return F.complexNum(GammaJS.fresnelC(c));
		}

		@Override
		public IExpr e1DblArg(final double arg1) {
			return F.num(de.lab4inf.math.functions.FresnelC.fresnelC(arg1));
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1) {
				throw new UnsupportedOperationException();
			}
			return de.lab4inf.math.functions.FresnelC.fresnelC(stack[top]);
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
			if (arg1.isNumber()) {
				if (arg1.isZero()) {
					return F.C0;
				}
			}
			if (arg1.isInfinity()) {
				return F.C1D2;
			}
			if (arg1.isNegativeInfinity()) {
				return F.CN1D2;
			}
			if (arg1.equals(F.CIInfinity)) {
				return F.Divide(F.CI, F.C2);
			}
			if (arg1.equals(F.CNIInfinity)) {
				return F.Divide(F.CNI, F.C2);
			}
			if (arg1.equals(F.CComplexInfinity)) {
				return F.Indeterminate;
			}
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return F.Negate(F.FresnelC(negExpr));
			}
			IExpr restExpr = AbstractFunctionEvaluator.extractFactorFromExpression(arg1, F.CI);
			if (restExpr.isPresent()) {
				return F.Times(F.CI, F.FresnelC(restExpr));
			}

			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}
	}

	private static class FresnelS extends AbstractTrigArg1 implements INumeric, DoubleUnaryOperator {

		@Override
		public double applyAsDouble(double operand) {
			return de.lab4inf.math.functions.FresnelS.fresnelS(operand);
		}

		@Override
		public IExpr e1ComplexArg(final Complex c) {
			return F.complexNum(GammaJS.fresnelS(c));
		}

		@Override
		public IExpr e1DblArg(final double arg1) {
			return F.num(de.lab4inf.math.functions.FresnelS.fresnelS(arg1));
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1) {
				throw new UnsupportedOperationException();
			}
			return de.lab4inf.math.functions.FresnelS.fresnelS(stack[top]);
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
			if (arg1.isNumber()) {
				if (arg1.isZero()) {
					return F.C0;
				}
			}
			if (arg1.isInfinity()) {
				return F.C1D2;
			}
			if (arg1.isNegativeInfinity()) {
				return F.CN1D2;
			}
			if (arg1.equals(F.CIInfinity)) {
				return F.Divide(F.CNI, F.C2);
			}
			if (arg1.equals(F.CNIInfinity)) {
				return F.Divide(F.CI, F.C2);
			}
			if (arg1.equals(F.CComplexInfinity)) {
				return F.Indeterminate;
			}
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return F.Negate(F.FresnelS(negExpr));
			}
			IExpr restExpr = AbstractFunctionEvaluator.extractFactorFromExpression(arg1, F.CI);
			if (restExpr.isPresent()) {
				return F.Times(F.CNI, F.FresnelS(restExpr));
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
			IExpr n = ast.arg1();

			if (ast.size() == 4) {
				// GegenbauerC(n, l, z)
				IExpr l = ast.arg2();
				IExpr z = ast.arg3();
				if (l.isNumEqualRational(F.C1D2)) {
					return F.LegendreP(n, z);
				}

				int lInt = l.toIntDefault(Integer.MIN_VALUE);
				if (lInt >= 0) {
					switch (lInt) {
					case 0:
						return F.C0;
					case 1:
						return F.ChebyshevU(n, z);
					case 2:
						// ((-2 - n)*ChebyshevU(n, z) + z*(1 + n)*ChebyshevU(1 + n, z))/(2*(-1 + z^2))
						return F.Times(F.C1D2, F.Power(F.Plus(F.CN1, F.Sqr(z)), -1),
								F.Plus(F.Times(F.Plus(F.CN2, F.Negate(F.n)), F.ChebyshevU(n, z)),
										F.Times(F.Plus(F.C1, n), z, F.ChebyshevU(F.Plus(F.C1, n), z))));
					}

				}
				if (n.isZero()) {
					return F.C1;
				}
				int nInt = n.toIntDefault(Integer.MIN_VALUE);
				if (nInt > 0) {
					// Sum(((-1)^k*Pochhammer(l, n - k)*(2*z)^(n - 2*k))/(k!*(n - 2*k)!), {k, 0, Floor(n/2)})
					return F.sum(
							k -> F.Times(F.Power(F.CN1, k), F.Power(F.Times(F.C2, z), F.Plus(F.Times(F.CN2, k), n)),
									F.Power(F.Factorial(k), -1), F.Power(F.Factorial(F.Plus(F.Times(F.CN2, k), n)), -1),
									F.Pochhammer(l, F.Plus(F.Negate(k), n))),
							0, nInt / 2);
				}
				return F.NIL;
			}
			// GegenbauerC(n, z)
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
					// (2^n/n)*z^n + Sum(((-1)^k*(n - k - 1)!*(2*z)^(n - 2*k))/(k! * (n -
					// 2*k)!), {k, 1, Floor(n/2)})
					int floorND2 = nInt / 2;
					return Plus(Times(Power(C2, n), Power(n, -1), Power(z, n)),
							F.sum(k -> Times(Power(CN1, k), Power(Times(C2, z), Plus(Times(F.CN2, k), n)),
									Power(Times(Factorial(k), Factorial(Plus(Times(F.CN2, k), n))), -1),
									Factorial(Plus(CN1, Negate(k), n))), 1, floorND2));
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
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_2_3;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}
	}

	private static class Hypergeometric0F1 extends AbstractFunctionEvaluator implements Hypergeometric0F1Rules {
		@Override
		public IAST getRuleAST() {
			return RULES;
		}

		@Override
		public IExpr evaluate(IAST ast, EvalEngine engine) {
			IExpr b = ast.arg1();
			IExpr z = ast.arg2();
			if (z.isZero()) {
				return F.C1;
			}
			if (z.isInfinity()) {
				return F.CComplexInfinity;
			}
			if (b.isReal() && z.isReal()) {
				double bDouble = ((ISignedNumber) b).doubleValue();
				double zDouble = ((ISignedNumber) z).doubleValue();
				try {
					return F.num(de.lab4inf.math.functions.HypergeometricLimitFunction.limitSeries(bDouble, zDouble));
				} catch (RuntimeException rex) {
					return engine.printMessage(ast.topHead() + ": " + rex.getMessage());
				}
			}
			return F.NIL;
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_2_2;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}
	}

	private static class Hypergeometric1F1 extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(IAST ast, EvalEngine engine) {
			IExpr a = ast.arg1();
			if (a.isZero()) {
				return F.C1;
			}
			IExpr b = ast.arg2();
			if (b.isZero()) {
				return F.CComplexInfinity;
			}
			IExpr z = ast.arg3();
			if (z.isZero()) {
				return F.C1;
			}

			if (b.isOne()) {
				// LaguerreL(-a, z)
				return F.LaguerreL(a.negate(), z);
			}
			if (a.isInteger() && b.isInteger() && a.isNegative() && b.isNegative()
					&& ((IInteger) b).isGT((IInteger) a)) {
				return F.CComplexInfinity;
			}
			if (engine.isDoubleMode()) {
				try {
					double aDouble = Double.NaN;
					double bDouble = Double.NaN;
					double zDouble = Double.NaN;
					try {
						aDouble = a.evalDouble();
						bDouble = b.evalDouble();
						zDouble = z.evalDouble();
					} catch (ValidateException ve) {
					}
					if (Double.isNaN(aDouble) || Double.isNaN(bDouble) || Double.isNaN(zDouble)) {
						Complex ac = a.evalComplex();
						Complex bc = b.evalComplex();
						Complex zc = z.evalComplex();

						return F.complexNum(HypergeometricJS.hypergeometric1F1(ac, bc, zc));

					} else {
						return F.num(HypergeometricJS.hypergeometric1F1(aDouble, bDouble, zDouble));
					}

				} catch (ValidateException ve) {
					if (Config.SHOW_STACKTRACE) {
						ve.printStackTrace();
					}
				} catch (RuntimeException rex) {
					// rex.printStackTrace();
					return engine.printMessage(ast.topHead() + ": " + rex.getMessage());
				}
			} 
			if (a.equals(b)) {
				// E^z
				return F.Power(F.E, z);
			}
			if (a.isOne()) {
				// (-1 + b)*E^z*z^(1 - b)*(Gamma(-1 + b) - Gamma(-1 + b, z))
				return F.Times(F.Plus(F.CN1, b), F.Power(F.E, z), F.Power(z, F.Plus(F.C1, F.Negate(b))),
						F.Plus(F.Gamma(F.Plus(F.CN1, b)), F.Negate(F.Gamma(F.Plus(F.CN1, b), z))));
			}
			if (a.isMinusOne()) {
				// 1 - z/b
				return F.Subtract(F.C1, F.Divide(z, b));
			}
			if (a.isNumEqualInteger(F.C2)) {
				// (-1 + b)*(1 + (2 - b)*E^z*z^(1 - b)* (Gamma(-1 + b) - Gamma(-1 + b, z)) + E^z*z^(2 - b)*(Gamma(-1 +
				// b] - Gamma(-1 + b, z)))
				return F.Times(F.Plus(F.CN1, b), F.Plus(F.C1,
						F.Times(F.Plus(F.C2, F.Negate(b)), F.Power(F.E, z), F.Power(z, F.Plus(F.C1, F.Negate(b))),
								F.Plus(F.Gamma(F.Plus(F.CN1, b)), F.Negate(F.Gamma(F.Plus(F.CN1, b), z)))),
						F.Times(F.Power(F.E, z), F.Power(z, F.Plus(F.C2, F.Negate(b))),
								F.Plus(F.Gamma(F.Plus(F.CN1, b)), F.Negate(F.Gamma(F.Plus(F.CN1, b), z))))));
			}
			if (a.isNumEqualInteger(F.CN2)) {
				// 1 - (2*z)/b + z^2/(b*(1 + b))
				return F.Plus(F.C1, F.Times(F.CN2, F.Power(b, -1), z),
						F.Times(F.Power(b, -1), F.Power(F.Plus(F.C1, b), -1), F.Sqr(z)));
			}
			return F.NIL;
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_3_3;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		}
	}

	private static class Hypergeometric2F1 extends AbstractFunctionEvaluator implements Hypergeometric2F1Rules {
		@Override
		public IAST getRuleAST() {
			return RULES;
		}

		@Override
		public IExpr evaluate(IAST ast, EvalEngine engine) {
			IExpr a = ast.arg1();
			IExpr b = ast.arg2();
			IExpr c = ast.arg3();
			IExpr z = ast.arg4();
			if (z.isZero()) {
				return F.C1;
			}
			if (a.compareTo(b) > 0) {
				IASTMutable newAST = ast.copy();
				newAST.set(1, b);
				newAST.set(2, a);
				return newAST;
			}
			if (a.isInteger() && a.isNegative() && z.isOne()) {
				IInteger n = (IInteger) a.negate();
				// Pochhammer(c-b, n) / Pochhammer(c, n)
				return F.Divide(F.Expand(F.Pochhammer(F.Subtract(c, b), n)), F.Pochhammer(c, n));
			}
			 
			if (engine.isDoubleMode()) {
				try {
					double aDouble = Double.NaN;
					double bDouble = Double.NaN;
					double cDouble = Double.NaN;
					double zDouble = Double.NaN;
					try {
						aDouble = a.evalDouble();
						bDouble = b.evalDouble();
						cDouble = c.evalDouble();
						zDouble = z.evalDouble();
					} catch (ValidateException ve) {
					}
					if (Double.isNaN(aDouble) || Double.isNaN(bDouble) || Double.isNaN(zDouble)) {
						Complex ac = a.evalComplex();
						Complex bc = b.evalComplex();
						Complex cc = c.evalComplex();
						Complex zc = z.evalComplex();

						return F.complexNum(HypergeometricJS.hypergeometric2F1(ac, bc, cc, zc));

					} else {
						return F.num(HypergeometricJS.hypergeometric2F1(aDouble, bDouble, cDouble, zDouble));
					}

				} catch (ThrowException te) {
					if (Config.SHOW_STACKTRACE) {
						te.printStackTrace();
					}
					return te.getValue();
				} catch (ValidateException ve) {
					if (Config.SHOW_STACKTRACE) {
						ve.printStackTrace();
					}
				} catch (RuntimeException rex) {
					// rex.printStackTrace();
					return engine.printMessage(ast.topHead() + ": " + rex.getMessage());
				}
			} 
//			
//			if (a.isReal() && b.isReal() && c.isReal() && z.isReal()) {
//				double aDouble = ((ISignedNumber) a).doubleValue();
//				double bDouble = ((ISignedNumber) b).doubleValue();
//				double cDouble = ((ISignedNumber) c).doubleValue();
//				double zDouble = ((ISignedNumber) z).doubleValue();
//				try {
//					return F.num(de.lab4inf.math.functions.HypergeometricGaussSeries.gaussSeries(aDouble, bDouble,
//							cDouble, zDouble));
//				} catch (RuntimeException rex) {
//					return engine.printMessage(ast.topHead() + ": " + rex.getMessage());
//				}
//			}
			return F.NIL;
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_4_4;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}
	}

	private static class HypergeometricPFQ extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(IAST ast, EvalEngine engine) {
			IExpr a = ast.arg1();
			IExpr b = ast.arg2();
			IExpr c = ast.arg3();
			if (c.isList()) {
				// thread elementwise over list in arg3
				return ((IAST) c).mapThread(ast.setAtCopy(3, F.Null), 3);
			}

			if (engine.isDoubleMode() && a.isVector() > 0 && b.isVector() > 0) {
				try {
					double A[] = a.toDoubleVector();
					double B[] = b.toDoubleVector();
					double cDouble = Double.NaN;
					try {
						cDouble = c.evalDouble();
					} catch (ValidateException ve) {
					}
					if (A == null || B == null || Double.isNaN(cDouble)) {
						Complex AC[] = a.toComplexVector();
						Complex BC[] = b.toComplexVector();
						if (AC != null && BC != null) {
							return F.complexNum(HypergeometricJS.hypergeometricPFQ(AC, BC, c.evalComplex(),
									Config.DOUBLE_TOLERANCE));
						}
					} else {
						INum result = F.num(HypergeometricJS.hypergeometricPFQ(A, B, cDouble, Config.DOUBLE_TOLERANCE));

						return result;
					}

				} catch (ValidateException ve) {
					if (Config.SHOW_STACKTRACE) {
						ve.printStackTrace();
					}
				} catch (RuntimeException rex) {
					// rex.printStackTrace();
					return engine.printMessage(ast.topHead() + ": " + rex.getMessage());
				}
			}
			return F.NIL;
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_3_3;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}
	}

	private static class LogIntegral extends AbstractTrigArg1 implements INumeric, DoubleUnaryOperator {

		@Override
		public double applyAsDouble(double operand) {
			if (F.isZero(operand)) {
				return 0.0;
			}
			if (F.isEqual(operand, 1.0)) {
				return Double.NEGATIVE_INFINITY;
			}
			return de.lab4inf.math.functions.LogarithmicIntegalFunction.li(operand);
		}

		@Override
		public IExpr e1DblArg(final double arg1) {
			if (F.isZero(arg1)) {
				return F.C0;
			}
			if (F.isEqual(arg1, 1.0)) {
				return F.CNInfinity;
			}
			return F.num(de.lab4inf.math.functions.LogarithmicIntegalFunction.li(arg1));
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1) {
				throw new UnsupportedOperationException();
			}
			return applyAsDouble(stack[top]);
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
			if (arg1.isZero()) {
				return F.C0;
			}
			if (arg1.isOne()) {
				return F.CNInfinity;
			}
			if (arg1.isInfinity()) {
				return F.CInfinity;
			}
			if (arg1.isComplexInfinity()) {
				return F.CComplexInfinity;
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}
	}

	private static class SinIntegral extends AbstractTrigArg1 implements INumeric, DoubleUnaryOperator {

		@Override
		public double applyAsDouble(double operand) {
			return de.lab4inf.math.functions.SineIntegral.si(operand);
		}

		@Override
		public IExpr e1DblArg(final double arg1) {
			return F.num(de.lab4inf.math.functions.SineIntegral.si(arg1));
		}

		@Override
		public double evalReal(final double[] stack, final int top, final int size) {
			if (size != 1) {
				throw new UnsupportedOperationException();
			}
			return de.lab4inf.math.functions.SineIntegral.si(stack[top]);
		}

		@Override
		public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
			if (arg1.isZero()) {
				return F.C0;
			}
			if (arg1.isInfinity()) {
				return F.CPiHalf;
			}
			if (arg1.isNegativeInfinity()) {
				return F.CNPiHalf;
			}
			if (arg1.isDirectedInfinity(F.CI)) {
				return arg1;
			}
			if (arg1.isComplexInfinity()) {
				return F.Indeterminate;
			}
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Negate(F.SinIntegral(negExpr));
			}
			if (arg1.isTimes() && arg1.first().isComplex() && arg1.first().re().isZero()) {
				// I * SinhIntegral(-I*arg1)
				return F.Times(F.I, F.SinhIntegral(F.Times(F.CNI, arg1)));
			}
			IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
			if (imPart.isPresent()) {
				return F.Times(F.CI, F.SinhIntegral(imPart));
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}
	}

	private static class SinhIntegral extends AbstractTrigArg1 {

		@Override
		public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
			if (arg1.isZero()) {
				return F.C0;
			}
			if (arg1.isInfinity()) {
				return F.CInfinity;
			}
			if (arg1.isNegativeInfinity()) {
				return F.CNInfinity;
			}
			if (arg1.isDirectedInfinity(F.CI)) {
				return F.Times(F.CI, F.CPiHalf);
			}
			if (arg1.isComplexInfinity()) {
				return F.Indeterminate;
			}
			if (arg1.isTimes() && arg1.first().isComplex() && arg1.first().re().isZero()) {
				// I * SinIntegral(-I*arg1)
				return F.Times(F.I, F.SinIntegral(F.Times(F.CNI, arg1)));
			}
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
			if (negExpr.isPresent()) {
				return Negate(F.SinhIntegral(negExpr));
			}
			IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
			if (imPart.isPresent()) {
				return F.Times(F.CI, F.SinIntegral(imPart));
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}
	}

	public static void initialize() {
		Initializer.init();
	}

	private HypergeometricFunctions() {

	}

}
