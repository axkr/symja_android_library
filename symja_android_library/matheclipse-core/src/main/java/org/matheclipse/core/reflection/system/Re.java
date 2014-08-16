package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.Arg;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.E;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Re;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.Times;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Get the real part of an expression
 * 
 * See: <a href="http://en.wikipedia.org/wiki/Real_part">Real part</a>
 */
public class Re implements IFunctionEvaluator {

	public Re() {
	}

	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);

		IExpr arg1 = ast.arg1();
		if (arg1.isNumber()) {
			return ((INumber) arg1).getRe();
		}
//		if (arg1.isSignedNumber()) {
//			return arg1;
//		}
//		if (arg1.isComplex()) {
//			return ((IComplex) arg1).getRe();
//		}
//		if (arg1 instanceof IComplexNum) {
//			return F.num(((IComplexNum) arg1).getRealPart());
//		}
		IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
		if (negExpr != null) {
			return Times(CN1, Re(negExpr));
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
			return ((IAST) arg1).mapAt(F.Re(null), 1);
		}
		if (arg1.isPower()) {
			IAST astPower = (IAST) arg1;
			if (astPower.arg1().isRealFunction()) {
				// test for x^(a+I*b)
				IExpr x = astPower.arg1();
				if (astPower.get(2).isNumber()) {
					// (x^2)^(a/2)*E^(-b*Arg[x])*Cos[a*Arg[x]+1/2*b*Log[x^2]]
					IExpr a = ((INumber) astPower.get(2)).getRe();
					IExpr b = ((INumber) astPower.get(2)).getIm();
					return rePowerComplex(x, a, b);
				}
				// (x^2)^(a/2)*E^(-b*Arg[x])*Cos[a*Arg[x]+1/2*b*Log[x^2]]
				IExpr a = F.eval(F.Re(astPower.get(2)));
				IExpr b = F.eval(F.Im(astPower.get(2)));
				return rePowerComplex(x, a, b);
			}
		}
		return null;
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
		return Times(Times(Power(Power(x, C2), Times(C1D2, a)), Power(E, Times(Times(CN1, b), Arg(x)))),
				Cos(Plus(Times(a, Arg(x)), Times(Times(C1D2, b), Log(Power(x, C2))))));
	}

	public IExpr numericEval(final IAST ast) {
		return evaluate(ast);
	}

	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.LISTABLE);
	}

}
