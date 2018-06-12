package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.*;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.Matcher;

public class FunctionExpand extends AbstractEvaluator {

	final static Matcher MATCHER = new Matcher(EvalEngine.get());
	static {
		// Beta
		MATCHER.caseOf(Beta(z_, a_, b_), //
				// [$ Beta(a, b)*(1 - (1 - z)^b*Sum((Pochhammer(b, k)*z^k)/k!, {k, 0, a - 1})) /; IntegerQ(a)&&a>0 $]
				F.Condition(
						F.Times(F.Beta(a, b),
								F.Plus(F.C1, F.Times(F.CN1, F.Power(F.Plus(F.C1, F.Negate(z)), b),
										F.Sum(F.Times(F.Power(z, k), F.Power(F.Factorial(k), -1), F.Pochhammer(b, k)),
												F.List(k, F.C0, F.Plus(F.CN1, a)))))),
						F.And(F.IntegerQ(a), F.Greater(a, F.C0)))); // $$);

		MATCHER.caseOf(Beta(a_, b_), //
				// [$ Factorial(a-1)*Product((k+b)^(-1),{k,0,a-1}) /; IntegerQ(a)&&a>0 $]
				F.Condition(
						F.Times(F.Factorial(F.Plus(F.CN1, a)),
								F.Product(F.Power(F.Plus(k, b), -1), F.List(k, F.C0, F.Plus(F.CN1, a)))),
						F.And(F.IntegerQ(a), F.Greater(a, F.C0)))); // $$);

		// ChebyshevT
		MATCHER.caseOf(ChebyshevT(n_, x_), //
				// [$ Cos(n*ArcCos(x)) $]
				F.Cos(F.Times(n, F.ArcCos(x)))); // $$);
		// ChebyshevU
		MATCHER.caseOf(ChebyshevU(n_, x_), //
				// [$ Sin((1 + n)*ArcCos(x))/(Sqrt(1 - x)*Sqrt(1 + x)) $]
				F.Times(F.Power(F.Times(F.Sqrt(F.Plus(F.C1, F.Negate(x))), F.Sqrt(F.Plus(F.C1, x))), -1),
						F.Sin(F.Times(F.Plus(F.C1, n), F.ArcCos(x))))); // $$);

		MATCHER.caseOf(F.Degree, //
				// [$ Pi/180 $]
				F.Times(F.QQ(1L, 180L), F.Pi)); // $$);
		MATCHER.caseOf(F.GoldenRatio, //
				// [$ 1/2*(1+Sqrt(5)) $]
				F.Times(F.C1D2, F.Plus(F.C1, F.CSqrt5))); // $$);
	}

	public FunctionExpand() {
	}

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
