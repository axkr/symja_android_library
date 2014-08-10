package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.Arg;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.E;
import static org.matheclipse.core.expression.F.Im;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sin;
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
 * Get the imaginary part of an expression
 * 
 * See: <a href="http://en.wikipedia.org/wiki/Imaginary_part">Imaginary part</a>
 */
public class Im implements IFunctionEvaluator {

	public Im() {
	}

	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);

		IExpr arg1 = ast.arg1();
		if (arg1.isSignedNumber()) {
			return F.C0;
		}
		if (arg1.isComplex()) {
			return ((IComplex) arg1).getIm();
		}
		if (arg1 instanceof IComplexNum) {
			return F.num(((IComplexNum) arg1).getImaginaryPart());
		}
		IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
		if (negExpr != null) {
			return Times(CN1, Im(negExpr));
		}
		if (arg1.isTimes()) {
			if (arg1.getAt(1).isSignedNumber()) {
				IAST temp = ((IAST) arg1).clone();
				temp.remove(1);
				return F.Times(arg1.getAt(1), F.Im(temp));
			}
			if (arg1.getAt(1).equals(F.CI)) {
				// Im(I*temp) -> Re(temp)
				IAST temp = ((IAST) arg1).clone();
				temp.remove(1);
				return F.Re(temp);
			}
		}
		if (arg1.isPlus()) {
			return ((IAST) arg1).mapAt(F.Im(null), 1);
		}
		if (arg1.isPower()) {
			IAST astPower = (IAST) arg1;
			if (astPower.arg1().isRealFunction()) {
				// test for x^(a+I*b)
				IExpr x = astPower.arg1();
				if (astPower.get(2).isNumber()) {
					// (x^2)^(a/2)*E^(-b*Arg[x])*Sin[a*Arg[x]+1/2*b*Log[x^2]]
					IExpr a = ((INumber) astPower.get(2)).getRe();
					IExpr b = ((INumber) astPower.get(2)).getIm();
					return imPowerComplex(x, a, b);
				}
				if (astPower.get(2).isNumericFunction()) {
					// (x^2)^(a/2)*E^(-b*Arg[x])*Sin[a*Arg[x]+1/2*b*Log[x^2]]
					IExpr a = F.eval(F.Re(astPower.get(2)));
					IExpr b = F.eval(F.Im(astPower.get(2)));
					return imPowerComplex(x, a, b);
				}
			}
		}
		return null;
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
		if (x.equals(F.E)) {
			// Im(E^(a+I*b)) -> E^a*Sin[b]
			return Times(Power(F.E, a), Sin(b));
		}
		return Times(Times(Power(Power(x, C2), Times(C1D2, a)), Power(E, Times(Times(CN1, b), Arg(x)))),
				Sin(Plus(Times(a, Arg(x)), Times(Times(C1D2, b), Log(Power(x, C2))))));
	}

	public IExpr numericEval(final IAST ast) {
		return evaluate(ast);
	}

	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.LISTABLE);
	}

}
