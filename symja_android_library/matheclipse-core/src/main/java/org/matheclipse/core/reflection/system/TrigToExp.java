package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.$;
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

import org.matheclipse.core.eval.exception.WrongNumberOfArguments;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.VisitorExpr;

/**
 * Exponential definitions for trigonometric functions
 * 
 * See <a href="http://en.wikipedia.org/wiki/List_of_trigonometric_identities#Exponential_definitions">List of trigonometric
 * identities - Exponential definitions</a>,<br/>
 * <a href="http://en.wikipedia.org/wiki/Hyperbolic_function">Hyperbolic function</a>
 */
public class TrigToExp implements IFunctionEvaluator {

	public TrigToExp() {
	}

	class TrigToExpVisitor extends VisitorExpr {
		public TrigToExpVisitor() {
			super();
		}

		@Override
		public IExpr visit2(IExpr head, IExpr arg1) {
			IExpr x = arg1;
			IExpr result = arg1.accept(this);
			if (result != null) {
				x = result;
			}
			if (head.equals(Sin)) {
				return Subtract(Times(C1D2, CI, Power(E, Times(CNI, x))), Times(C1D2, CI, Power(E, Times(CI, x))));
			}
			if (head.equals(Cos)) {
				return Plus(Times(C1D2, Power(E, Times(CNI, x))), Times(C1D2, Power(E, Times(CI, x))));
			}
			if (head.equals(Tan)) {
				return Times(CI, Subtract(Power(E, Times(CNI, x)), Power(E, Times(CI, x))),
						Power(Plus(Power(E, Times(CNI, x)), Power(E, Times(CI, x))), CN1));
			}
			if (head.equals(ArcSin)) {
				return Times(CNI, Log(Plus(Sqrt(Subtract(C1, Sqr(x))), Times(CI, x))));
			}
			if (head.equals(ArcCos)) {
				return Plus(Times(C1D2, Pi), Times(CI, Log(Plus(Sqrt(Subtract(C1, Sqr(x))), Times(CI, x)))));
			}
			if (head.equals(ArcTan)) {
				return Subtract(Times(C1D2, CI, Log(Plus(C1, Times(CNI, x)))), Times(C1D2, CI, Log(Plus(C1, Times(CI, x)))));
			}
			if (head.equals(Cosh)) {
				// JavaForm[(E^x+E^(-x))/2]
				return Times(C1D2,Plus(Power(E,x),Power(E,Times(CN1,x))));
			}
			if (head.equals(Csch)) {
				// JavaForm[2/(E^x-E^(-x))]
				return Times(C2,Power(Plus(Power(E,x),Times(CN1,Power(E,Times(CN1,x)))),CN1));
			}
			if (head.equals(Coth)) {
				// JavaForm[((E^(-x))+E^x)/((-E^(-x))+E^x)]
				return Times(Plus(Power(E,x),Power(E,Times(CN1,x))),Power(Plus(Power(E,x),Times(CN1,Power(E,Times(CN1,x)))),CN1));
			}
			if (head.equals(Sech)) {
				// JavaForm[2/(E^x+E^(-x))]
				return Times(C2,Power(Plus(Power(E,x),Power(E,Times(CN1,x))),CN1));
			}
			if (head.equals(Sinh)) {
				// JavaForm[(E^x-E^(-x))/2]
				return Times(C1D2, Plus(Power(E, x), Times(CN1, Power(E, Times(CN1, x)))));
			}
			if (head.equals(Tanh)) {
				// JavaForm[((-E^(-x))+E^x)/((E^(-x))+E^x)]
				return Times(Plus(Times(CN1, Power(E, Times(CN1, x))), Power(E, x)),
						Power(Plus(Power(E, Times(CN1, x)), Power(E, x)), CN1));
			}
			if (result != null) {
				return $(head, result);
			}
			return null;
		}
	}

	public IExpr evaluate(final IAST functionList) {
		if (functionList.size() != 2) {
			throw new WrongNumberOfArguments(functionList, 1, functionList.size() - 1);
		}
		TrigToExpVisitor tteVisitor = new TrigToExpVisitor();
		IExpr result = functionList.get(1).accept(tteVisitor);
		if (result != null) {
			return result;
		}
		return functionList.get(1);
	}

	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.LISTABLE);
	}

}
