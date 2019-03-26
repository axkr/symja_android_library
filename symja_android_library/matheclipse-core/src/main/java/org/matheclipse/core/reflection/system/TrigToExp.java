package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.ArcCos;
import static org.matheclipse.core.expression.F.ArcCosh;
import static org.matheclipse.core.expression.F.ArcCot;
import static org.matheclipse.core.expression.F.ArcCoth;
import static org.matheclipse.core.expression.F.ArcCsc;
import static org.matheclipse.core.expression.F.ArcCsch;
import static org.matheclipse.core.expression.F.ArcSec;
import static org.matheclipse.core.expression.F.ArcSech;
import static org.matheclipse.core.expression.F.ArcSin;
import static org.matheclipse.core.expression.F.ArcSinh;
import static org.matheclipse.core.expression.F.ArcTan;
import static org.matheclipse.core.expression.F.ArcTanh;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.Cosh;
import static org.matheclipse.core.expression.F.Cot;
import static org.matheclipse.core.expression.F.Coth;
import static org.matheclipse.core.expression.F.Csc;
import static org.matheclipse.core.expression.F.Csch;
import static org.matheclipse.core.expression.F.Sec;
import static org.matheclipse.core.expression.F.Sech;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Sinh;
import static org.matheclipse.core.expression.F.Tan;
import static org.matheclipse.core.expression.F.Tanh;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.y_;

import org.matheclipse.core.builtin.Structure;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.Matcher;

/**
 * <pre>
 * TrigToExp(expr)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * converts trigonometric functions in <code>expr</code> to exponentials.
 * </p>
 * </blockquote>
 * <h3>Examples</h3>
 * 
 * <pre>
 * &gt;&gt; TrigToExp(Cos(x))
 * 1/2*E^(I*x)+1/2*E^(-I*x)
 * </pre>
 */
public class TrigToExp extends AbstractEvaluator {

	private final static Matcher MATCHER = new Matcher();

	/**
	 * 
	 * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation in static
	 * initializer</a>
	 */
	private static class Initializer {

		private static void init() {
			MATCHER.caseOf(Sec(x_), //
					x -> // [$ 2/(E^((-I)*x) + E^(I*x)) $]
					F.Times(F.C2, F.Power(F.Plus(F.Exp(F.Times(F.CNI, x)), F.Exp(F.Times(F.CI, x))), -1))); // $$);
			MATCHER.caseOf(Sin(x_), //
					x -> // [$ I/(2*E^(I*x))-1/2*I*E^(I*x) $]
					F.Plus(F.Times(F.CN1D2, F.CI, F.Exp(F.Times(F.CI, x))),
							F.Times(F.CI, F.Power(F.Times(F.C2, F.Exp(F.Times(F.CI, x))), -1)))); // $$);
			MATCHER.caseOf(Cos(x_), //
					x -> // [$ 1/(2*E^(I*x))+E^(I*x)/2 $]
					F.Plus(F.Power(F.Times(F.C2, F.Exp(F.Times(F.CI, x))), -1),
							F.Times(F.C1D2, F.Exp(F.Times(F.CI, x))))); // $$);
			MATCHER.caseOf(Cot(x_), //
					x -> // [$ -((I*(E^((-I)*x) + E^(I*x)))/(E^((-I)*x) - E^(I*x))) $]
					F.Times(F.CN1, F.CI, F.Plus(F.Exp(F.Times(F.CNI, x)), F.Exp(F.Times(F.CI, x))),
							F.Power(F.Plus(F.Exp(F.Times(F.CNI, x)), F.Negate(F.Exp(F.Times(F.CI, x)))), -1))); // $$);
			MATCHER.caseOf(Csc(x_), //
					x -> // [$ -((2*I)/(E^((-I)*x) - E^(I*x))) $]
					F.Times(F.CN1, F.C2, F.CI,
							F.Power(F.Plus(F.Exp(F.Times(F.CNI, x)), F.Negate(F.Exp(F.Times(F.CI, x)))), -1))); // $$);
			MATCHER.caseOf(Tan(x_), //
					x -> // [$ (I*(E^(-I*x)-E^(I*x)))/(E^(-I*x)+E^(I*x)) $]
					F.Times(F.CI, F.Plus(F.Exp(F.Times(F.CNI, x)), F.Negate(F.Exp(F.Times(F.CI, x)))),
							F.Power(F.Plus(F.Exp(F.Times(F.CNI, x)), F.Exp(F.Times(F.CI, x))), -1))); // $$);

			MATCHER.caseOf(ArcSec(x_), //
					x -> // [$ Pi/2 + I*Log(Sqrt(1 - 1/x^2) + I/x) $]
					F.Plus(F.Times(F.C1D2, F.Pi), F.Times(F.CI, F.Log(
							F.Plus(F.Sqrt(F.Plus(F.C1, F.Negate(F.Power(x, -2)))), F.Times(F.CI, F.Power(x, -1))))))); // $$);
			MATCHER.caseOf(ArcSin(x_), //
					x -> // [$ -I*Log(I*x+Sqrt(1-x^2)) $]
					F.Times(F.CNI, F.Log(F.Plus(F.Times(F.CI, x), F.Sqrt(F.Plus(F.C1, F.Negate(F.Sqr(x)))))))); // $$);
			MATCHER.caseOf(ArcCos(x_), //
					x -> // [$ Pi/2+I*Log(I*x+Sqrt(1-x^2)) $]
					F.Plus(F.Times(F.C1D2, F.Pi),
							F.Times(F.CI, F.Log(F.Plus(F.Times(F.CI, x), F.Sqrt(F.Plus(F.C1, F.Negate(F.Sqr(x))))))))); // $$);
			MATCHER.caseOf(ArcCsc(x_), //
					x -> // [$ (-I)*Log(Sqrt(1 - 1/x^2) + I/x) $]
					F.Times(F.CNI, F.Log(
							F.Plus(F.Sqrt(F.Plus(F.C1, F.Negate(F.Power(x, -2)))), F.Times(F.CI, F.Power(x, -1)))))); // $$);
			MATCHER.caseOf(ArcCot(x_), //
					x -> // [$ (1/2)*I*Log(1 - I/x) - (1/2)*I*Log(1 + I/x) $]
					F.Plus(F.Times(F.C1D2, F.CI, F.Log(F.Plus(F.C1, F.Times(F.CNI, F.Power(x, -1))))),
							F.Times(F.CN1D2, F.CI, F.Log(F.Plus(F.C1, F.Times(F.CI, F.Power(x, -1))))))); // $$);
			MATCHER.caseOf(ArcTan(x_), //
					x -> // [$ 1/2*I*Log(1-I*x)-1/2*I*Log(1+I*x) $]
					F.Plus(F.Times(F.C1D2, F.CI, F.Log(F.Plus(F.C1, F.Times(F.CNI, x)))),
							F.Times(F.CN1D2, F.CI, F.Log(F.Plus(F.C1, F.Times(F.CI, x)))))); // $$);
			MATCHER.caseOf(ArcTan(x_, y_), //
					(x, y) -> // [$ (-I)*Log((x + I*y)/Sqrt(x^2 + y^2)) $]
					F.Times(F.CNI,
							F.Log(F.Times(F.Plus(x, F.Times(F.CI, y)), F.Power(F.Plus(F.Sqr(x), F.Sqr(y)), F.CN1D2))))); // $$);

			MATCHER.caseOf(ArcCosh(x_), //
					x -> // [$ Log(x + Sqrt(-1 + x)*Sqrt(1 + x)) $]
					F.Log(F.Plus(x, F.Times(F.Sqrt(F.Plus(F.CN1, x)), F.Sqrt(F.Plus(F.C1, x)))))); // $$);
			MATCHER.caseOf(ArcCsch(x_), //
					x -> // [$ Log(Sqrt(1 + 1/x^2) + 1/x) $]
					F.Log(F.Plus(F.Sqrt(F.Plus(F.C1, F.Power(x, -2))), F.Power(x, -1)))); // $$);
			MATCHER.caseOf(ArcCoth(x_), //
					x -> // [$ (-(1/2))*Log(1 - 1/x) + (1/2)*Log(1 + 1/x) $]
					F.Plus(F.Times(F.CN1D2, F.Log(F.Plus(F.C1, F.Negate(F.Power(x, -1))))),
							F.Times(F.C1D2, F.Log(F.Plus(F.C1, F.Power(x, -1)))))); // $$);
			MATCHER.caseOf(ArcSech(x_), //
					x -> // [$ Log(Sqrt(-1 + 1/x)*Sqrt(1 + 1/x) + 1/x) $]
					F.Log(F.Plus(F.Times(F.Sqrt(F.Plus(F.CN1, F.Power(x, -1))), F.Sqrt(F.Plus(F.C1, F.Power(x, -1)))),
							F.Power(x, -1)))); // $$);
			MATCHER.caseOf(ArcSinh(x_), //
					x -> // [$ Log(x + Sqrt(1 + x^2)) $]
					F.Log(F.Plus(x, F.Sqrt(F.Plus(F.C1, F.Sqr(x)))))); // $$);
			MATCHER.caseOf(ArcTanh(x_), //
					x -> // [$ (-(1/2))*Log(1 - x) + (1/2)*Log(1 + x) $]
					F.Plus(F.Times(F.CN1D2, F.Log(F.Plus(F.C1, F.Negate(x)))),
							F.Times(F.C1D2, F.Log(F.Plus(F.C1, x))))); // $$);

			MATCHER.caseOf(Cosh(x_), //
					x -> // [$ 1/(E^x*2) + E^x/2 $]
					F.Plus(F.Power(F.Times(F.Exp(x), F.C2), -1), F.Times(F.C1D2, F.Exp(x)))); // $$);
			MATCHER.caseOf(Csch(x_), //
					x -> // [$ 2/(E^x-E^(-x)) $]
					F.Times(F.C2, F.Power(F.Plus(F.Negate(F.Exp(F.Negate(x))), F.Exp(x)), -1))); // $$);
			MATCHER.caseOf(Coth(x_), //
					x -> // [$ ((E^(-x))+E^x)/((-E^(-x))+E^x) $]
					F.Times(F.Plus(F.Exp(F.Negate(x)), F.Exp(x)),
							F.Power(F.Plus(F.Negate(F.Exp(F.Negate(x))), F.Exp(x)), -1))); // $$);
			MATCHER.caseOf(Sech(x_), //
					x -> // [$ 2/(E^x+E^(-x)) $]
					F.Times(F.C2, F.Power(F.Plus(F.Exp(x), F.Exp(F.Negate(x))), -1))); // $$);
			MATCHER.caseOf(Sinh(x_), //
					x -> // [$ -(1/(E^x*2)) + E^x/2 $]
					F.Plus(F.Negate(F.Power(F.Times(F.Exp(x), F.C2), -1)), F.Times(F.C1D2, F.Exp(x)))); // $$);
			MATCHER.caseOf(Tanh(x_), //
					x -> // [$ -(1/(E^x*(E^(-x) + E^x))) + E^x/(E^(-x) + E^x) $]
					F.Plus(F.Negate(F.Power(F.Times(F.Exp(x), F.Plus(F.Exp(F.Negate(x)), F.Exp(x))), -1)),
							F.Times(F.Exp(x), F.Power(F.Plus(F.Exp(F.Negate(x)), F.Exp(x)), -1)))); // $$);
		}
	}

	public TrigToExp() {
	}

	/**
	 * Exponential definitions for trigonometric functions
	 * 
	 * See <a href= "http://en.wikipedia.org/wiki/List_of_trigonometric_identities#Exponential_definitions"> List of
	 * trigonometric identities - Exponential definitions</a>,<br/>
	 * <a href="http://en.wikipedia.org/wiki/Hyperbolic_function">Hyperbolic function</a>
	 */
	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (ast.size() == 2) {
			IExpr temp = Structure.threadLogicEquationOperators(ast.arg1(), ast, 1);
			if (temp.isPresent()) {
				return temp;
			}

			IExpr arg1 = ast.arg1();
			return MATCHER.replaceAll(arg1).orElse(arg1);
		}
		Validate.checkSize(ast, 2);
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		Initializer.init();
		newSymbol.setAttributes(ISymbol.LISTABLE);
	}

}
