package org.matheclipse.core.builtin;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

public class EllipticIntegrals {
	static {
		F.EllipticE.setEvaluator(new EllipticE());
		F.EllipticF.setEvaluator(new EllipticF());
		F.EllipticK.setEvaluator(new EllipticK());
		F.EllipticPi.setEvaluator(new EllipticPi());
		F.JacobiZeta.setEvaluator(new JacobiZeta());
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
			Validate.checkRange(ast, 2, 3);

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

				if (z instanceof INum && m instanceof INum) {
					double a = ((ISignedNumber) z).doubleValue();
					double b = ((ISignedNumber) m).doubleValue();
					try {
						return F.num(de.lab4inf.math.functions.IncompleteSecondEllipticIntegral.icseint(a, b));
					} catch (RuntimeException rex) {
						engine.printMessage("EllipticE: " + rex.getMessage());
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
			if (z.equals(F.C1D2)) {
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
			Validate.checkSize(ast, 3);

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
				if (engine.evalTrue(F.LessEqual(F.Abs(F.Re(z)), F.CPiHalf))) {
					// Log(Sec(z) + Tan(z))
					return F.Log(F.Plus(F.Sec(z), F.Tan(z)));
				}
			}
			if (z instanceof INum && m instanceof INum) {
				double a = ((ISignedNumber) z).doubleValue();
				double b = ((ISignedNumber) m).doubleValue();
				try {
					return F.num(de.lab4inf.math.functions.IncompleteFirstEllipticIntegral.icfeint(a, b));
				} catch (RuntimeException rex) {
					engine.printMessage("EllipticF: " + rex.getMessage());
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
			Validate.checkSize(ast, 2);

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
			if (m.equals(F.C1D2)) {
				// (8 Pi^(3/2))/Gamma(-(1/4))^2
				return F.Times(F.C8, F.Power(F.Pi, F.QQ(3L, 2L)), F.Power(F.Gamma(F.CN1D4), -2));
			}
			if (m.isNumber()) {
				// EllipticK(m_) := Pi/(2*ArithmeticGeometricMean(1,Sqrt(1-m)))
				return F.Times(F.C1D2, F.Pi,
						F.Power(F.ArithmeticGeometricMean(F.C1, F.Sqrt(F.Plus(F.C1, F.Negate(m)))), -1));
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
			Validate.checkRange(ast, 3, 4);

			IExpr n = ast.arg1();
			IExpr m = ast.arg2();
			if (ast.isAST3()) {
				if (n instanceof INum && m instanceof INum && ast.arg3() instanceof INum) {
					double a = ((ISignedNumber) n).doubleValue();
					double b = ((ISignedNumber) m).doubleValue();
					double c = ((ISignedNumber) ast.arg3()).doubleValue();
					try {
						return F.num(de.lab4inf.math.functions.IncompleteThirdEllipticIntegral.icteint(a, b, c));
					} catch (RuntimeException rex) {
						engine.printMessage("EllipticPi: " + rex.getMessage());
					}
				}
				if (m.equals(F.CPiHalf)) {
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
					return F.EllipticF(m, ast.arg3());
				}
				return F.NIL;
			}
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
				double a = ((ISignedNumber) n).doubleValue();
				double b = ((ISignedNumber) m).doubleValue();
				try {
					return F.num(de.lab4inf.math.functions.CompleteThirdEllipticIntegral.cteint(a, b));
				} catch (RuntimeException rex) {
					engine.printMessage("EllipticPi: " + rex.getMessage());
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

			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
			super.setUp(newSymbol);
		}
	}

	private final static EllipticIntegrals CONST = new EllipticIntegrals();

	public static EllipticIntegrals initialize() {
		return CONST;
	}

	private EllipticIntegrals() {

	}

}
