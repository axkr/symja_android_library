package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

import de.lab4inf.math.functions.Bessel;

/**
 * <pre>
 * BesselJ(n, z)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * Bessel function of the first kind.
 * </p>
 * </blockquote>
 * <p>
 * See
 * </p>
 * <ul>
 * <li><a href="https://en.wikipedia.org/wiki/Bessel_function">Wikipedia - Bessel function</a></li>
 * </ul>
 * <h3>Examples</h3>
 * 
 * <pre>
 * &gt;&gt; BesselJ(1, 3.6)
 * 0.09547
 * </pre>
 */
public class BesselJ extends AbstractFunctionEvaluator {

	/**
	 * Precondition <code> n - 1/2 </code> is an integer number.
	 * 
	 * @param n
	 * @param z
	 * @return
	 */
	private IExpr besselJHalf(IExpr n, IExpr z) {
		// (1/Sqrt(z))*Sqrt(2/Pi)*(Cos((1/2)*Pi*(n - 1/2) - z)*Sum(((-1)^j*(2*j + Abs(n) + 1/2)! * (2*z)^(-2*j - 1))/
		// ((2*j + 1)! * (-2*j + Abs(n) - 3/2)!), {j, 0, Floor((1/4)*(2*Abs(n) - 3))}) - Sin((1/2)*Pi*(n - 1/2) -
		// z)*Sum(((-1)^j*(2*j + Abs(n) - 1/2)!)/ ((2*j)!*(-2*j + Abs(n) - 1/2)!*(2*z)^(2*j)), {j, 0,
		// Floor((1/4)*(2*Abs(n) - 1))}))
		ISymbol j = F.Dummy("j");
		return F.Times(
				F.CSqrt2, F.Power(F.Pi, F.CN1D2), F.Power(z, F.CN1D2), F
						.Plus(F.Times(
								F.Cos(F.Plus(F.Times(F.C1D2, F.Plus(F.CN1D2, n), F.Pi), F.Negate(z))), F.Sum(
										F.Times(F.Power(F.CN1, j),
												F.Power(F.Times(F.C2, z), F.Plus(F.CN1, F.Times(F.CN2, j))),
												F.Factorial(F.Plus(F.Times(F.C2, j), F.Abs(n), F.C1D2)),
												F.Power(F
														.Times(F.Factorial(F.Plus(F.Times(F.C2, j), F.C1)),
																F.Factorial(F.Plus(F.QQ(-3L, 2L), F.Times(F.CN2, j),
																		F.Abs(n)))),
														-1)),
										F.List(j, F.C0,
												F.Floor(F.Times(F.C1D4, F.Plus(F.CN3, F.Times(F.C2, F.Abs(n)))))))),
								F.Times(F.CN1, F.Sin(F.Plus(F.Times(F.C1D2, F.Plus(F.CN1D2,
										n), F.Pi), F
												.Negate(z))),
										F.Sum(F.Times(
												F.Power(F.CN1, j),
												F.Power(F.Times(F.Factorial(F.Times(F.C2, j)),
														F.Factorial(F.Plus(F.CN1D2, F.Times(F.CN2, j), F.Abs(n))),
														F.Power(F.Times(F.C2, z), F.Times(F.C2, j))), -1),
												F.Factorial(F.Plus(F.CN1D2, F.Times(F.C2, j), F.Abs(n)))),
												F.List(j, F.C0, F.Floor(
														F.Times(F.C1D4, F.Plus(F.CN1, F.Times(F.C2, F.Abs(n))))))))));
	}

	public BesselJ() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);

		IExpr n = ast.arg1();
		int order = n.toIntDefault(Integer.MIN_VALUE);
		IExpr z = ast.arg2();
		if (z.isZero()) {
			if (n.isZero()) {
				// (0, 0)
				return F.C1;
			}
			if (n.isIntegerResult() || order != Integer.MIN_VALUE) {
				return F.C0;
			}

			IExpr a = n.re();
			if (a.isPositive()) {
				// Re(arg1) > 0
				return F.C0;
			} else if (a.isNegative()) {
				// Re(arg1) < 0 && !a.isInteger()
				return F.CComplexInfinity;
			} else if (a.isZero() && !n.isZero()) {
				return F.Indeterminate;
			}

		}
		if (n.isReal()) {
			IExpr in = engine.evaluate(((ISignedNumber) n).add(F.CN1D2));
			if (in.isNumIntValue()) {
				return besselJHalf(n, z);
				// if (n.equals(F.CN1D2) || n.equals(F.num(-0.5))) {
				// // (Sqrt(2/Pi)* Cos(z))/Sqrt(z)
				// return F.Times(F.Sqrt(F.Divide(F.C2, F.Pi)), F.Cos(z), F.Power(z, F.CN1D2));
				// }
				// if (n.equals(F.C1D2) || n.equals(F.num(0.5))) {
				// // (Sqrt(2/Pi)* Sin(z))/Sqrt(z)
				// return F.Times(F.Sqrt(F.Divide(F.C2, F.Pi)), F.Sin(z), F.Power(z, F.CN1D2));
				// }
			}
		}
		if (z.isInfinity() || z.isNegativeInfinity()) {
			return F.C0;
		}
		if (n.isInteger() || order != Integer.MIN_VALUE) {
			if (n.isNegative()) {
				// (-n,z) => (-1)^n*BesselJ(n,z)
				return F.Times(F.Power(F.CN1, n), F.BesselJ(n.negate(), z));
			}
		}
		if (n.isReal() && z instanceof INum) {
			try {
				// numeric mode evaluation
				if (order != Integer.MIN_VALUE) {
					double bessel = Bessel.jn(order, ((INum) z).doubleValue());
					return F.num(bessel);
				} else {
					if (n.isReal()) {
						org.hipparchus.special.BesselJ besselJ = new org.hipparchus.special.BesselJ(
								((ISignedNumber) n).doubleValue());
						return F.num(besselJ.value(((INum) z).doubleValue()));
					}
				}
			} catch (NegativeArraySizeException nae) {
				engine.printMessage("BesselJ: "+ast.toString() + " caused NegativeArraySizeException");
			} catch (RuntimeException rte) {
				engine.printMessage("BesselJ: "+rte.getMessage());
			}
		}

		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
	}
}
