package org.matheclipse.core.builtin;

import org.hipparchus.complex.Complex;
import org.matheclipse.core.builtin.functions.EllipticFunctionsJS;
import org.matheclipse.core.builtin.functions.EllipticIntegralsJS;
import org.matheclipse.core.convert.Object2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

public class EllipticIntegrals {
	/**
	 * 
	 * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation in static
	 * initializer</a>
	 */
	private static class Initializer {

		private static void init() {
			F.EllipticE.setEvaluator(new EllipticE());
			F.EllipticF.setEvaluator(new EllipticF());
			F.EllipticK.setEvaluator(new EllipticK());
			F.EllipticPi.setEvaluator(new EllipticPi());
			F.EllipticTheta.setEvaluator(new EllipticTheta());

			// F.InverseWeierstrassP.setEvaluator(new InverseWeierstrassP());
			F.JacobiZeta.setEvaluator(new JacobiZeta());

			F.WeierstrassHalfPeriods.setEvaluator(new WeierstrassHalfPeriods());
			F.WeierstrassInvariants.setEvaluator(new WeierstrassInvariants());
			F.WeierstrassP.setEvaluator(new WeierstrassP());
			F.WeierstrassPPrime.setEvaluator(new WeierstrassPPrime());
		}
	}

	/**
	 * <pre>
	 * EllipticE(z)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the complete elliptic integral of the second kind.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:
	 * </p>
	 * <ul>
	 * <li><a href=
	 * "https://en.wikipedia.org/wiki/Elliptic_integral#Complete_elliptic_integral_of_the_second_kind">Wikipedia -
	 * Elliptic integral - Complete elliptic integral of the second kind)</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; EllipticE(5/4,1)
	 * Sin(5/4)
	 * </pre>
	 */
	private static class EllipticE extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(IAST ast, EvalEngine engine) {
			IExpr z = ast.arg1();
			if (ast.isAST2()) {
				IExpr m = ast.arg2();
				if (m.isZero()) {
					return z;
				}
				if (z.isZero()) {
					return F.C0;
				}
				if (m.isOne()) {
					// Abs(Re(z)) <= Pi/2
					if (engine.evalTrue(F.LessEqual(F.Abs(F.Re(z)), F.CPiHalf))) {
						return F.Sin(z);
					}
				}
				if (m.isInfinity() || m.isNegativeInfinity()) {
					return F.CComplexInfinity;
				}
				if (z.equals(F.CPiHalf)) {
					// EllipticE(Pi/2, m) = EllipticE(m)
					return F.EllipticE(m);
				}

				// if (z instanceof INum && m instanceof INum) {
				// double a = ((ISignedNumber) z).doubleValue();
				// double b = ((ISignedNumber) m).doubleValue();
				// try {
				// // see github #109
				// b = -Math.asin(Math.sqrt(b));
				// return F.num(de.lab4inf.math.functions.IncompleteSecondEllipticIntegral.icseint(a, b));
				// } catch (RuntimeException rex) {
				// return engine.printMessage("EllipticE: " + rex.getMessage());
				// }
				// }
				if (z.isReal() && m.isReal()) {
					try {
						return F.complexNum(EllipticIntegralsJS.ellipticE(z.evalDouble(), m.evalDouble()));
					} catch (RuntimeException rte) {
						return engine.printMessage("EllipticE: " + rte.getMessage());
					}
				} else if (z.isNumeric() && m.isNumeric()) {
					try {
						return F.complexNum(EllipticIntegralsJS.ellipticE(z.evalComplex(), m.evalComplex()));
					} catch (RuntimeException rte) {
						return engine.printMessage("EllipticE: " + rte.getMessage());
					}
				}
				IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(z);
				if (negExpr.isPresent()) {
					// EllipticE(-z,m) = -EllipticE(z,m)
					return F.Negate(F.EllipticE(negExpr, m));
				}
				return F.NIL;
			}

			if (z.isZero()) {
				// Pi/2
				return F.CPiHalf;
			}
			if (z.isOne()) {
				return F.C1;
			}
			if (z.isNumEqualRational(F.C1D2)) {
				// (Pi^2 + 2 Gamma(3/4)^4)/(4*Sqrt(Pi)*Gamma(3/4)^2)
				return F.Times(F.C1D4, F.Power(F.Pi, F.CN1D2), F.Power(F.Gamma(F.QQ(3L, 4L)), -2),
						F.Plus(F.Sqr(F.Pi), F.Times(F.C2, F.Power(F.Gamma(F.QQ(3L, 4L)), 4))));
			}
			if (z.isMinusOne()) {
				// (Pi^2+2*Gamma(3/4)^4)/(2*Sqrt(2)*Sqrt(Pi)*Gamma(3/4)^2)
				return F.Times(F.C1D2, F.C1DSqrt2, F.Power(F.Pi, F.CN1D2), F.Power(F.Gamma(F.QQ(3L, 4L)), -2),
						F.Plus(F.Sqr(F.Pi), F.Times(F.C2, F.Power(F.Gamma(F.QQ(3L, 4L)), 4))));
			}

			if (z instanceof INum) {
				double a = ((ISignedNumber) z).doubleValue();
				try {
					return F.num(de.lab4inf.math.functions.CompleteSecondEllipticIntegral.cseint(a));
				} catch (RuntimeException rex) {
					engine.printMessage("EllipticE: " + rex.getMessage());
				}
			}
			if (z.isInfinity() || z.isNegativeInfinity() || z.isComplexInfinity()) {
				return F.CComplexInfinity;
			}
			return F.NIL;
		}

		@Override
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
	 * EllipticF(z)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the incomplete elliptic integral of the first kind.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:
	 * </p>
	 * <ul>
	 * <li><a href=
	 * "https://en.wikipedia.org/wiki/Elliptic_integral#Incomplete_elliptic_integral_of_the_first_kind">Wikipedia -
	 * Elliptic integral - Incomplete elliptic integral of the first kind)</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; EllipticF(17/2*Pi, m)
	 * 17*EllipticK(m)
	 * </pre>
	 */
	private static class EllipticF extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(IAST ast, EvalEngine engine) {
			IExpr z = ast.arg1();
			IExpr m = ast.arg2();
			if (z.isZero() || m.isInfinity() || m.isNegativeInfinity()) {
				return F.C0;
			}
			if (m.isZero()) {
				return z;
			}
			if (z.equals(F.CPiHalf)) {
				// EllipticF(Pi/2, m) = EllipticK(m)
				return F.EllipticK(m);
			}
			if (z.isTimes() && z.second().equals(F.Pi) && z.first().isRational()) {
				IRational k = ((IRational) z.first()).multiply(F.C2).normalize();
				if (k.isInteger()) {
					// EllipticF(k*Pi/2, m) = k*EllipticK(m) /; IntegerQ(k)
					return F.Times(k, F.EllipticK(m));
				}
			}
			if (m.isOne()) {
				// Abs(Re(z)) <= Pi/2
				IExpr temp = engine.evaluate(F.Abs(F.Re(z)));
				if (F.LessEqual.ofQ(engine, temp, F.CPiHalf)) {
					// Log(Sec(z) + Tan(z))
					return F.Log(F.Plus(F.Sec(z), F.Tan(z)));
				}
				if (F.Greater.ofQ(engine, temp, F.CPiHalf)) {
					return F.CComplexInfinity;
				}
			}
			// if (z instanceof INum && m instanceof INum) {
			// double a = ((ISignedNumber) z).doubleValue();
			// double b = ((ISignedNumber) m).doubleValue();
			// try {
			// // see github #109
			// b = -Math.asin(Math.sqrt(b));
			// return F.num(de.lab4inf.math.functions.IncompleteFirstEllipticIntegral.icfeint(a, b));
			// } catch (RuntimeException rex) {
			// return engine.printMessage("EllipticF: " + rex.getMessage());
			// }
			// }
			if (z.isReal() && m.isReal()) {
				try {
					return F.complexNum(EllipticIntegralsJS.ellipticF(z.evalDouble(), m.evalDouble()));
				} catch (RuntimeException rte) {
					return engine.printMessage("EllipticF: " + rte.getMessage());
				}
			} else if (z.isNumeric() && m.isNumeric()) {
				try {
					return F.complexNum(EllipticIntegralsJS.ellipticF(z.evalComplex(), m.evalComplex()));
				} catch (RuntimeException rte) {
					return engine.printMessage("EllipticF: " + rte.getMessage());
				}
			}

			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(z);
			if (negExpr.isPresent()) {
				// EllipticF(-z,m) = -EllipticF(z,m)
				return F.Negate(F.EllipticF(negExpr, m));
			}

			// test EllipticF(zz+k*Pi,m)
			IAST parts = AbstractFunctionEvaluator.getPeriodicParts(z, F.Pi);
			if (parts.isPresent()) {
				IExpr k = parts.arg2();
				if (k.isInteger()) {
					// EllipticF(zz,m)+2*k*EllipticK(m)
					IExpr zz = parts.arg1();
					return F.Plus(F.EllipticF(zz, m), F.Times(F.C2, k, F.EllipticK(m)));
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

	/**
	 * <pre>
	 * EllipticK(z)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the complete elliptic integral of the first kind.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:
	 * </p>
	 * <ul>
	 * <li><a href=
	 * "https://en.wikipedia.org/wiki/Elliptic_integral#Complete_elliptic_integral_of_the_first_kind">Wikipedia -
	 * Elliptic integral - Complete elliptic integral of the first kind)</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Table(EllipticK(x+I), {x,-1.0, 1.0, 1/4})
	 * {1.26549+I*0.16224,1.30064+I*0.18478,1.33866+I*0.21305,1.37925+I*0.24904,1.42127+I*0.29538,1.46203+I*0.35524,1.49611+I*0.43136,1.51493+I*0.52354,1.50924+I*0.62515}
	 * </pre>
	 */
	private static class EllipticK extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(IAST ast, EvalEngine engine) {
			IExpr m = ast.arg1();
			if (m.isInfinity() || m.isNegativeInfinity() || m.isDirectedInfinity(F.CI) || m.isDirectedInfinity(F.CNI)) {
				return F.C0;
			}
			if (m.isZero()) {
				return F.CPiHalf;
			}
			if (m.isOne()) {
				return F.CComplexInfinity;
			}
			if (m.isMinusOne()) {
				// Gamma(1/4)^2/(4*Sqrt(2*Pi))
				return F.Times(F.C1D4, F.C1DSqrt2, F.Power(F.Pi, F.CN1D2), F.Sqr(F.Gamma(F.C1D4)));
			}
			if (m.isNumEqualRational(F.C1D2)) {
				// (8 Pi^(3/2))/Gamma(-(1/4))^2
				return F.Times(F.C8, F.Power(F.Pi, F.QQ(3L, 2L)), F.Power(F.Gamma(F.CN1D4), -2));
			}
			if (m.isReal()) {
				try {
					return F.complexNum(EllipticIntegralsJS.ellipticK(m.evalDouble()));
				} catch (RuntimeException rte) {
					return engine.printMessage("EllipticK: " + rte.getMessage());
				}
			} else if (m.isNumeric()) {
				try {
					return F.complexNum(EllipticIntegralsJS.ellipticK(m.evalComplex()));
				} catch (RuntimeException rte) {
					return engine.printMessage("EllipticK: " + rte.getMessage());
				}
			}
			if (m.isNumber()) {
				// EllipticK(m_) := Pi/(2*ArithmeticGeometricMean(1,Sqrt(1-m)))
				return F.Times(F.C1D2, F.Pi,
						F.Power(F.ArithmeticGeometricMean(F.C1, F.Sqrt(F.Plus(F.C1, F.Negate(m)))), -1));
			}
			return F.NIL;
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}
	}

	/**
	 * <pre>
	 * EllipticPi(n, m)
	 * </pre>
	 * <p>
	 * or
	 * </p>
	 * 
	 * <pre>
	 * EllipticPi(n, m, z)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the complete elliptic integral of the third kind.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:
	 * </p>
	 * <ul>
	 * <li><a href=
	 * "https://en.wikipedia.org/wiki/Elliptic_integral#Complete_elliptic_integral_of_the_third_kind">Wikipedia -
	 * Elliptic integral - Complete elliptic integral of the third kind</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; EllipticPi(n,Pi/2,x)
	 * EllipticPi(n,x)
	 * </pre>
	 */
	private static class EllipticPi extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(IAST ast, EvalEngine engine) {
			IExpr n = ast.arg1();

			if (ast.isAST3()) {
				IExpr z = ast.arg2();
				IExpr m = ast.arg3();
				// if (n instanceof INum && z instanceof INum && m instanceof INum) {
				// double a = ((ISignedNumber) n).doubleValue();
				// double b = ((ISignedNumber) z).doubleValue();
				// double c = ((ISignedNumber) m).doubleValue();
				// try {
				// // see github #109
				// c = -Math.asin(Math.sqrt(c));
				// return F.num(de.lab4inf.math.functions.IncompleteThirdEllipticIntegral.icteint(a, b, c));
				// } catch (RuntimeException rex) {
				// return engine.printMessage("EllipticPi: " + rex.getMessage());
				// }
				// }
				if (n.isReal() && z.isReal() && m.isReal()) {
					try {
						return F.complexNum(
								EllipticIntegralsJS.ellipticPi(n.evalDouble(), z.evalDouble(), m.evalDouble()));
					} catch (RuntimeException rte) {
						return engine.printMessage("EllipticPi: " + rte.getMessage());
					}
				} else if (n.isNumeric() && z.isNumeric() && m.isNumeric()) {
					try {
						return F.complexNum(
								EllipticIntegralsJS.ellipticPi(n.evalComplex(), z.evalComplex(), m.evalComplex()));
					} catch (RuntimeException rte) {
						return engine.printMessage("EllipticPi: " + rte.getMessage());
					}
				}
				if (z.equals(F.CPiHalf)) {
					if (n.isZero()) {
						// EllipticPi(0,Pi/2,z) = EllipticK(z)
						return F.EllipticK(ast.arg3());
					}
					if (n.equals(ast.arg3())) {
						// EllipticPi(n,Pi/2,n) = EllipticE(n)/(1-n)
						return F.Times(F.Power(F.Plus(F.C1, F.Negate(n)), -1), F.EllipticE(n));
					}
					return F.EllipticPi(n, ast.arg3());
				}
				if (n.isZero()) {
					return F.EllipticF(z, ast.arg3());
				}
				return F.NIL;
			}
			IExpr m = ast.arg2();
			if (n.isZero()) {
				return F.EllipticK(m);
			}
			if (n.isOne()) {
				return F.CComplexInfinity;
			}
			if (m.isZero()) {
				// Pi/(2*Sqrt(1-n))
				return F.Times(F.C1D2, F.Power(F.Plus(F.C1, F.Negate(n)), F.CN1D2), F.Pi);
			}
			if (m.isOne()) {
				// -(Infinity/Sign(n-1))
				return F.Times(F.oo, F.Power(F.Sign(F.Plus(F.C1, F.Negate(n))), -1));
			}
			if (n.equals(m)) {
				// EllipticE(n)/(1 - n)
				return F.Times(F.Power(F.Plus(F.C1, F.Negate(n)), -1), F.EllipticE(n));
			}
			if (n.isReal() && m.isReal()) {
				try {
					return F.complexNum(EllipticIntegralsJS.ellipticPi(n.evalDouble(), Math.PI / 2, m.evalDouble()));
				} catch (RuntimeException rte) {
					return engine.printMessage("EllipticPi: " + rte.getMessage());
				}
			} else if (n.isNumeric() && m.isNumeric()) {
				try {
					return F.complexNum(EllipticIntegralsJS.ellipticPi(n.evalComplex(),
							new org.hipparchus.complex.Complex(Math.PI / 2), m.evalComplex()));
				} catch (RuntimeException rte) {
					return engine.printMessage("EllipticPi: " + rte.getMessage());
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

	private static class EllipticTheta extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(IAST ast, EvalEngine engine) {
			IExpr n = ast.arg1();
			int a = n.toIntDefault();
			if (ast.isAST3()) {
				IExpr x = ast.arg2();
				IExpr m = ast.arg3();

				if (a >= 1 && a <= 4) {
					if (m.isZero()) {
						switch (a) {
						case 1:
						case 2:
							return F.C0;
						case 3:
						case 4:
							return F.C1;
						}
					} else if (a == 1) {
						if (x.isZero()) {
							return F.C0;
						} else if (x.isPi() && m.isNumEqualRational(F.C1D2)) {
							return F.C0;
						}
					}
					if (x.isReal() && m.isReal()) {
						try {
							return F.complexNum(EllipticFunctionsJS.jacobiTheta(a, x.evalDouble(), m.evalDouble()));
						} catch (RuntimeException rte) {
							return engine.printMessage("EllipticTheta: " + rte.getMessage());
						}
					} else if (x.isNumeric() && m.isNumeric()) {
						try {
							return F.complexNum(EllipticFunctionsJS.jacobiTheta(a, x.evalComplex(), m.evalComplex()));
						} catch (RuntimeException rte) {
							return engine.printMessage("EllipticTheta: " + rte.getMessage());
						}
					}
				}
				return F.NIL;
			}

			IExpr m = ast.arg2();
			if (a >= 1 && a <= 4) {
				if (m.isZero()) {
					switch (a) {
					case 1:
					case 2:
						return F.C0;
					case 3:
					case 4:
						return F.C1;
					}
				}
				if (m.isReal()) {
					try {
						return F.complexNum(EllipticFunctionsJS.jacobiTheta(a, 0.0, m.evalDouble()));
					} catch (RuntimeException rte) {
						return engine.printMessage("EllipticTheta: " + rte.getMessage());
					}
				} else if (m.isNumeric()) {
					try {
						return F.complexNum(EllipticFunctionsJS.jacobiTheta(a, org.hipparchus.complex.Complex.ZERO,
								m.evalComplex()));
					} catch (RuntimeException rte) {
						return engine.printMessage("EllipticTheta: " + rte.getMessage());
					}
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
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NHOLDFIRST | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}
	}

	// private static class InverseWeierstrassP extends AbstractFunctionEvaluator {
	//
	// @Override
	// public IExpr evaluate(IAST ast, EvalEngine engine) {
	// IExpr u = ast.arg1();
	// if (ast.arg2().isVector() == 2) {
	// IAST list = (IAST) ast.arg2();
	// IExpr g2 = list.arg1();
	// IExpr g3 = list.arg2();
	// if (u.isNumeric() && g2.isNumeric() && g3.isNumeric()) {
	// try {
	// return F.complexNum(
	// EllipticFunctionsJS.inverseWeierstrassP(u.evalComplex(), g2.evalComplex(), g3.evalComplex()));
	// } catch (RuntimeException rte) {
	// return engine.printMessage("InverseWeierstrassP: " + rte.getMessage());
	// }
	// }
	// }
	//
	// return F.NIL;
	// }
	//
	// @Override
	// public int[] expectedArgSize() {
	// return IOFunctions.ARGS_2_2;
	// }
	//
	// @Override
	// public void setUp(final ISymbol newSymbol) {
	// newSymbol.setAttributes(ISymbol.NUMERICFUNCTION);
	// super.setUp(newSymbol);
	// }
	// }

	private static class JacobiZeta extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(IAST ast, EvalEngine engine) {
			IExpr z = ast.arg1();
			IExpr m = ast.arg2();
			if (m.isZero()) {
				return F.C0;
			}
			if (z.isZero()) {
				return F.C0;
			}
			if (z.equals(F.CPiHalf)) {
				return F.C0;
			}
			if (m.isOne()) {
				// Abs(Re(z)) <= Pi/2
				if (engine.evalTrue(F.LessEqual(F.Abs(F.Re(z)), F.CPiHalf))) {
					return F.Sin(z);
				}
			}
			if (m.isInfinity() || m.isNegativeInfinity()) {
				return F.CComplexInfinity;
			}

			// if (z.isNumeric() && m.isNumeric()) {
			// try {
			// return F.complexNum(EllipticIntegralsJS.jacobiZeta(z.evalComplex(), m.evalComplex()));
			// } catch (RuntimeException rte) {
			// return engine.printMessage("EllipticPi: " + rte.getMessage());
			// }
			// }

			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}
	}

	private static class WeierstrassHalfPeriods extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(IAST ast, EvalEngine engine) {
			if (ast.arg1().isVector() == 2) {
				IAST list = (IAST) ast.arg1();
				IExpr g2 = list.arg1();
				IExpr g3 = list.arg2();
				if (g2.isNumeric() && g3.isNumeric()) {
					try {
						org.hipparchus.complex.Complex[] invariants = EllipticFunctionsJS
								.weierstrassHalfPeriods(g2.evalComplex(), g3.evalComplex());
						return Object2Expr.convertComplex(false, invariants);
					} catch (RuntimeException rte) {
						return engine.printMessage("WeierstrassHalfPeriods: " + rte.getMessage());
					}
				}
			}

			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}
	}

	private static class WeierstrassInvariants extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(IAST ast, EvalEngine engine) {
			if (ast.arg1().isVector() == 2) {
				IAST list = (IAST) ast.arg1();
				IExpr g2 = list.arg1();
				IExpr g3 = list.arg2();
				if (g2.isNumeric() && g3.isNumeric()) {
					try {
						org.hipparchus.complex.Complex[] invariants = EllipticFunctionsJS
								.weierstrassInvariants(g2.evalComplex(), g3.evalComplex());
						return Object2Expr.convertComplex(false, invariants);
					} catch (RuntimeException rte) {
						return engine.printMessage("WeierstrassInvariants: " + rte.getMessage());
					}
				}
			}

			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}
	}

	private static class WeierstrassP extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(IAST ast, EvalEngine engine) {
			IExpr u = ast.arg1();
			if (u.isZero()) {
				return F.CComplexInfinity;
			}
			if (ast.arg2().isVector() == 2) {
				IAST list = (IAST) ast.arg2();
				IExpr g2 = list.arg1();
				IExpr g3 = list.arg2();
				if (u.isNumeric() && g2.isNumeric() && g3.isNumeric()) {
					try {
						return F.complexNum(
								EllipticFunctionsJS.weierstrassP(u.evalComplex(), g2.evalComplex(), g3.evalComplex()));
					} catch (RuntimeException rte) {
						return engine.printMessage("WeierstrassP: " + rte.getMessage());
					}
				}
			}

			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_2_2;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}
	}

	private static class WeierstrassPPrime extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(IAST ast, EvalEngine engine) {
			IExpr u = ast.arg1();
			if (u.isZero()) {
				return F.CComplexInfinity;
			}
			if (ast.arg2().isVector() == 2) {
				IAST list = (IAST) ast.arg2();
				IExpr g2 = list.arg1();
				IExpr g3 = list.arg2();
				if (u.isNumeric() && g2.isNumeric() && g3.isNumeric()) {
					try {
						return F.complexNum(EllipticFunctionsJS.weierstrassPPrime(u.evalComplex(), g2.evalComplex(),
								g3.evalComplex()));
					} catch (RuntimeException rte) {
						return engine.printMessage("WeierstrassPPrime: " + rte.getMessage());
					}
				}
			}

			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_2_2;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}
	}

	public static void initialize() {
		Initializer.init();
	}

	private EllipticIntegrals() {

	}

}
