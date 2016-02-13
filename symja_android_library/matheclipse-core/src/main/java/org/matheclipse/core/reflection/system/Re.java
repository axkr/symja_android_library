package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.Arg;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.E;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Re;
import static org.matheclipse.core.expression.F.Times;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Get the real part of an expression
 * 
 * See: <a href="http://en.wikipedia.org/wiki/Real_part">Real part</a>
 */
public class Re extends AbstractEvaluator {

	public Re() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);

		IExpr arg1 = ast.arg1();
		if (arg1.isIndeterminate()) {
			return F.Indeterminate;
		}
		if (arg1.isDirectedInfinity()) {
			IAST directedInfininty = (IAST) arg1;
			if (directedInfininty.isComplexInfinity()) {
				return F.Indeterminate;
			}
			if (directedInfininty.size() == 2) {
				if (directedInfininty.isInfinity()) {
					return F.CInfinity;
				}
				IExpr re = engine.evaluate(F.Re(directedInfininty.arg1()));
				if (re.isNumber()) {
					if (re.isZero()) {
						return F.C0;
					}
					return F.Times(F.Sign(re), F.CInfinity);
				}
			}
		}
		if (arg1.isNumber()) {
			return ((INumber) arg1).getRe();
		}

		IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
		if (negExpr.isPresent()) {
			return Negate(Re(negExpr));
		}
		if (arg1.isTimes()) {
			if (arg1.getAt(1).isSignedNumber()) {
				IAST temp = ((IAST) arg1).removeAtClone(1);
				return F.Times(arg1.getAt(1), F.Re(temp));
			}
			if (arg1.getAt(1).equals(F.CI)) {
				// Re(I*temp) -> -Im(temp)
				IAST temp = ((IAST) arg1).removeAtClone(1);
				return F.Times(F.CN1, F.Im(temp));
			}
		}
		if (arg1.isPlus()) {
			return ((IAST) arg1).mapAt((IAST) F.Re(null), 1);
		}
		if (arg1.isPower()) {
			IAST astPower = (IAST) arg1;
			if (astPower.arg1().isRealResult()) {
				// test for x^(a+I*b)
				IExpr x = astPower.arg1();
				if (astPower.arg2().isNumber()) {
					// (x^2)^(a/2)*E^(-b*Arg[x])*Cos[a*Arg[x]+1/2*b*Log[x^2]]
					IExpr a = ((INumber) astPower.arg2()).getRe();
					IExpr b = ((INumber) astPower.arg2()).getIm();
					return rePowerComplex(x, a, b);
				}
				// (x^2)^(a/2)*E^(-b*Arg[x])*Cos[a*Arg[x]+1/2*b*Log[x^2]]
				IExpr a = engine.evaluate(F.Re(astPower.arg2()));
				IExpr b = engine.evaluate(F.Im(astPower.arg2()));
				return rePowerComplex(x, a, b);
			}
		}
		return F.NIL;
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
	private IExpr rePowerComplex(IExpr x, IExpr a, IExpr b) {
		if (x.isE()) {
			// Re(E^(a+I*b)) -> E^a*Cos[b]
			return Times(Power(F.E, a), Cos(b));
		}
		return Times(Times(Power(Power(x, C2), Times(C1D2, a)), Power(E, Times(Negate(b), Arg(x)))),
				Cos(Plus(Times(a, Arg(x)), Times(Times(C1D2, b), Log(Power(x, C2))))));
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.LISTABLE);
	}

}
