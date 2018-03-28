package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.ArcCos;
import static org.matheclipse.core.expression.F.ArcSin;
import static org.matheclipse.core.expression.F.ArcTan;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.Cosh;
import static org.matheclipse.core.expression.F.Coth;
import static org.matheclipse.core.expression.F.Csch;
import static org.matheclipse.core.expression.F.Sech;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Sinh;
import static org.matheclipse.core.expression.F.Tan;
import static org.matheclipse.core.expression.F.Tanh;
import static org.matheclipse.core.expression.F.x_;

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

	final static Matcher MATCHER = new Matcher(EvalEngine.get());
	static {
		MATCHER.caseOf(Sin(x_), //
				x -> // [$ I/(2*E^(I*x))-1/2*I*E^(I*x) $]
				F.Plus(F.Times(F.CN1D2, F.CI, F.Exp(F.Times(F.CI, x))),
						F.Times(F.CI, F.Power(F.Times(F.C2, F.Exp(F.Times(F.CI, x))), -1)))); // $$);
		MATCHER.caseOf(Cos(x_), //
				x -> // [$ 1/(2*E^(I*x))+E^(I*x)/2 $]
				F.Plus(F.Power(F.Times(F.C2, F.Exp(F.Times(F.CI, x))), -1), F.Times(F.C1D2, F.Exp(F.Times(F.CI, x))))); // $$);
		MATCHER.caseOf(Tan(x_), //
				x -> // [$ (I*(E^(-I*x)-E^(I*x)))/(E^(-I*x)+E^(I*x)) $]
				F.Times(F.CI, F.Plus(F.Exp(F.Times(F.CNI, x)), F.Negate(F.Exp(F.Times(F.CI, x)))),
						F.Power(F.Plus(F.Exp(F.Times(F.CNI, x)), F.Exp(F.Times(F.CI, x))), -1))); // $$);
		MATCHER.caseOf(ArcSin(x_), //
				x -> // [$ -I*Log(I*x+Sqrt(1-x^2)) $]
				F.Times(F.CNI, F.Log(F.Plus(F.Times(F.CI, x), F.Sqrt(F.Plus(F.C1, F.Negate(F.Sqr(x)))))))); // $$);
		MATCHER.caseOf(ArcCos(x_), //
				x -> // [$ Pi/2+I*Log(I*x+Sqrt(1-x^2)) $]
				F.Plus(F.Times(F.C1D2, F.Pi),
						F.Times(F.CI, F.Log(F.Plus(F.Times(F.CI, x), F.Sqrt(F.Plus(F.C1, F.Negate(F.Sqr(x))))))))); // $$);
		MATCHER.caseOf(ArcTan(x_), //
				x -> // [$ 1/2*I*Log(1-I*x)-1/2*I*Log(1+I*x) $]
				F.Plus(F.Times(F.C1D2, F.CI, F.Log(F.Plus(F.C1, F.Times(F.CNI, x)))),
						F.Times(F.CN1D2, F.CI, F.Log(F.Plus(F.C1, F.Times(F.CI, x)))))); // $$);
		MATCHER.caseOf(Cosh(x_), //
				x -> // [$ (E^x+E^(-x))/2 $]
				F.Times(F.C1D2, F.Plus(F.Exp(x), F.Exp(F.Negate(x))))); // $$);
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
				x -> // [$ (E^x-E^(-x))/2 $]
				F.Times(F.C1D2, F.Plus(F.Negate(F.Exp(F.Negate(x))), F.Exp(x)))); // $$);
		MATCHER.caseOf(Tanh(x_), //
				x -> // [$ ((-E^(-x))+E^x)/((E^(-x))+E^x) $]
				F.Times(F.Plus(F.Negate(F.Exp(F.Negate(x))), F.Exp(x)),
						F.Power(F.Plus(F.Exp(F.Negate(x)), F.Exp(x)), -1))); // $$);
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
		Validate.checkSize(ast, 2);
		IExpr arg1 = ast.arg1();
		return MATCHER.replaceAll(arg1).orElse(arg1);
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE);
	}

}