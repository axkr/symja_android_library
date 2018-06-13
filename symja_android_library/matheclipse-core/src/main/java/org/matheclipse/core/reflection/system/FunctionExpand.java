package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.Beta;
import static org.matheclipse.core.expression.F.ChebyshevT;
import static org.matheclipse.core.expression.F.ChebyshevU;
import static org.matheclipse.core.expression.F.Factorial;
import static org.matheclipse.core.expression.F.a;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.b;
import static org.matheclipse.core.expression.F.b_;
import static org.matheclipse.core.expression.F.k;
import static org.matheclipse.core.expression.F.m;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.n;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.x;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.y;
import static org.matheclipse.core.expression.F.y_;
import static org.matheclipse.core.expression.F.z;
import static org.matheclipse.core.expression.F.z_;

import org.matheclipse.core.builtin.AssumptionFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.Matcher;

/**
 * <pre>
 * FunctionExpand(f)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * expands the special function <code>f</code>.
 * </p>
 * </blockquote>
 * <h3>Examples</h3>
 * 
 * <pre>
 * &gt;&gt; FunctionExpand(Beta(10, b))
 * 362880/(b*(1+b)*(2+b)*(3+b)*(4+b)*(5+b)*(6+b)*(7+b)*(8+b)*(9+b))
 * </pre>
 */
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

		// CatalanNumber
		MATCHER.caseOf(F.CatalanNumber(n_), //
				// [$ (2^(2*n)*Gamma(1/2 + n))/(Sqrt(Pi)*Gamma(2 + n)) $]
				F.Times(F.Power(F.C2, F.Times(F.C2, n)), F.Gamma(F.Plus(F.C1D2, n)),
						F.Power(F.Times(F.Sqrt(F.Pi), F.Gamma(F.Plus(F.C2, n))), -1))); // $$);
		// ChebyshevT
		MATCHER.caseOf(ChebyshevT(n_, x_), //
				// [$ Cos(n*ArcCos(x)) $]
				F.Cos(F.Times(n, F.ArcCos(x)))); // $$);
		// ChebyshevU
		MATCHER.caseOf(ChebyshevU(n_, x_), //
				// [$ Sin((1 + n)*ArcCos(x))/(Sqrt(1 - x)*Sqrt(1 + x)) $]
				F.Times(F.Power(F.Times(F.Sqrt(F.Plus(F.C1, F.Negate(x))), F.Sqrt(F.Plus(F.C1, x))), -1),
						F.Sin(F.Times(F.Plus(F.C1, n), F.ArcCos(x))))); // $$);

		// Factorial
		MATCHER.caseOf(Factorial(x_), //
				// [$ Gamma(1+x) $]
				F.Gamma(F.Plus(F.C1, x))); // $$);

		// Fibonacci
		MATCHER.caseOf(F.Fibonacci(F.Plus(m_, n_)), //
				// [$ ((1/2)*Fibonacci(m)*LucasL(n) + (1/2)*Fibonacci(n)*LucasL(m)) /; IntegerQ(m)&&Element(n, Integers)
				// $]
				F.Condition(
						F.Plus(F.Times(F.C1D2, F.Fibonacci(m), F.LucasL(n)),
								F.Times(F.C1D2, F.Fibonacci(n), F.LucasL(m))),
						F.And(F.IntegerQ(m), F.Element(n, F.Integers)))); // $$);

		// LegendreQ
		MATCHER.caseOf(F.LegendreQ(x_, y_, z_), //
				// [$ -((Pi*Csc(Pi*y)*Gamma(1 + x + y)*LegendreP(x, -y, z))/(2*Gamma(1 + x - y))) +
				// (1/2)*Pi*Cot(Pi*y)*LegendreP(x, y, z) $]
				F.Plus(F.Times(F.CN1, F.Pi, F.Csc(F.Times(F.Pi, y)),
						F.Power(F.Times(F.C2, F.Gamma(F.Plus(F.C1, x, F.Negate(y)))), -1), F.Gamma(F.Plus(F.C1, x, y)),
						F.LegendreP(x, F.Negate(y), z)),
						F.Times(F.C1D2, F.Pi, F.Cot(F.Times(F.Pi, y)), F.LegendreP(x, y, z)))); // $$);

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
		Validate.checkRange(ast, 2, 3);
		IExpr arg1 = ast.arg1();
		IExpr assumptionExpr = F.NIL;
		if (ast.size() > 2) {
			IExpr arg2 = ast.arg2();

			if (!arg2.isRule()) {
				assumptionExpr = arg2;
			}
			final Options options = new Options(ast.topHead(), ast, 2, engine);
			IExpr option = options.getOption("Assumptions");
			if (option.isPresent()) {
				assumptionExpr = option;
			}
		}
		if (assumptionExpr.isPresent()) {
			if (assumptionExpr.isAST()) {
				IAssumptions oldAssumptions = engine.getAssumptions();
				IAssumptions assumptions =oldAssumptions;
				if (oldAssumptions == null) {
					assumptions = org.matheclipse.core.eval.util.Assumptions.getInstance(assumptionExpr);
				} else {
					assumptions = oldAssumptions.addAssumption((IAST) assumptionExpr);
				}
				if (assumptions != null) {
					try {
						engine.setAssumptions(assumptions);
						return MATCHER.replaceAll(arg1).orElse(arg1);
					} finally {
						engine.setAssumptions(oldAssumptions);
					}
				}
			}
			
		}
		return MATCHER.replaceAll(arg1).orElse(arg1);
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE);
	}

}
