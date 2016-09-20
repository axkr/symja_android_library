package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.ArcCos;
import static org.matheclipse.core.expression.F.ArcSin;
import static org.matheclipse.core.expression.F.ArcTan;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CI;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CNI;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.Cosh;
import static org.matheclipse.core.expression.F.Coth;
import static org.matheclipse.core.expression.F.Csch;
import static org.matheclipse.core.expression.F.E;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Pi;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sech;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Sinh;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Tan;
import static org.matheclipse.core.expression.F.Tanh;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.x;
import static org.matheclipse.core.expression.F.x_;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.Matcher;

/**
 * Exponential definitions for trigonometric functions
 * 
 * See <a href=
 * "http://en.wikipedia.org/wiki/List_of_trigonometric_identities#Exponential_definitions">
 * List of trigonometric identities - Exponential definitions</a>,<br/>
 * <a href="http://en.wikipedia.org/wiki/Hyperbolic_function">Hyperbolic
 * function</a>
 */
public class TrigToExp extends AbstractEvaluator {

	final static Matcher matcher = new Matcher();
	static {
		matcher.caseOf(Sin(x_),
				Subtract(Times(C1D2, CI, Power(E, Times(CNI, x))), Times(C1D2, CI, Power(E, Times(CI, x)))));
		matcher.caseOf(Cos(x_), Plus(Times(C1D2, Power(E, Times(CNI, x))), Times(C1D2, Power(E, Times(CI, x)))));
		matcher.caseOf(Tan(x_), Times(CI, Subtract(Power(E, Times(CNI, x)), Power(E, Times(CI, x))),
				Power(Plus(Power(E, Times(CNI, x)), Power(E, Times(CI, x))), CN1)));
		matcher.caseOf(ArcSin(x_), Times(CNI, Log(Plus(Sqrt(Subtract(C1, Sqr(x))), Times(CI, x)))));
		matcher.caseOf(ArcCos(x_),
				Plus(Times(C1D2, Pi), Times(CI, Log(Plus(Sqrt(Subtract(C1, Sqr(x))), Times(CI, x))))));
		matcher.caseOf(ArcTan(x_),
				Subtract(Times(C1D2, CI, Log(Plus(C1, Times(CNI, x)))), Times(C1D2, CI, Log(Plus(C1, Times(CI, x))))));
		// JavaForm[(E^x+E^(-x))/2]
		matcher.caseOf(Cosh(x_), Times(C1D2, Plus(Power(E, x), Power(E, Times(CN1, x)))));
		// JavaForm[2/(E^x-E^(-x))]
		matcher.caseOf(Csch(x_), Times(C2, Power(Plus(Power(E, x), Times(CN1, Power(E, Times(CN1, x)))), CN1)));
		// JavaForm[((E^(-x))+E^x)/((-E^(-x))+E^x)]
		matcher.caseOf(Coth(x_), Times(Plus(Power(E, x), Power(E, Times(CN1, x))),
				Power(Plus(Power(E, x), Times(CN1, Power(E, Times(CN1, x)))), CN1)));
		// JavaForm[2/(E^x+E^(-x))]
		matcher.caseOf(Sech(x_), Times(C2, Power(Plus(Power(E, x), Power(E, Times(CN1, x))), CN1)));
		// JavaForm[(E^x-E^(-x))/2]
		matcher.caseOf(Sinh(x_), Times(C1D2, Plus(Power(E, x), Times(CN1, Power(E, Times(CN1, x))))));
		// JavaForm[((-E^(-x))+E^x)/((E^(-x))+E^x)]
		matcher.caseOf(Tanh(x_), Times(Plus(Times(CN1, Power(E, Times(CN1, x))), Power(E, x)),
				Power(Plus(Power(E, Times(CN1, x)), Power(E, x)), CN1)));
	}

	public TrigToExp() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);
		IExpr arg1 = ast.arg1();
		return matcher.replaceAll(arg1).orElse(arg1);
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE);
	}

}
