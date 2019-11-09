package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.Beta;
import static org.matheclipse.core.expression.F.BetaRegularized;
import static org.matheclipse.core.expression.F.Binomial;
import static org.matheclipse.core.expression.F.ChebyshevT;
import static org.matheclipse.core.expression.F.ChebyshevU;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.Factorial;
import static org.matheclipse.core.expression.F.a;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.b;
import static org.matheclipse.core.expression.F.b_;
import static org.matheclipse.core.expression.F.c;
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

import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.builtin.WindowFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.Matcher;
import org.matheclipse.core.polynomials.QuarticSolver;

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

	private final static Matcher MATCHER = new Matcher();

	/**
	 * 
	 * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation in static
	 * initializer</a>
	 */
	private static class Initializer {

		private static void init() {
			// Beta
			MATCHER.caseOf(Beta(z_, a_, b_), //
					// [$ Beta(a, b)*(1 - (1 - z)^b*Sum((Pochhammer(b, k)*z^k)/k!, {k, 0, a - 1})) /; IntegerQ(a)&&a>0
					// $]
					F.Condition(
							F.Times(F.Beta(a, b), F.Plus(F.C1, F.Times(F.CN1, F.Power(F.Subtract(F.C1, z), b),
									F.Sum(F.Times(F.Power(z, k), F.Power(F.Factorial(k), F.CN1), F.Pochhammer(b, k)),
											F.List(k, F.C0, F.Plus(F.CN1, a)))))),
							F.And(F.IntegerQ(a), F.Greater(a, F.C0)))); // $$);

			MATCHER.caseOf(Beta(a_, b_), //
					// [$ Factorial(a-1)*Product((k+b)^(-1),{k,0,a-1}) /; IntegerQ(a)&&a>0 $]
					F.Condition(
							F.Times(F.Factorial(F.Plus(F.CN1, a)),
									F.Product(F.Power(F.Plus(k, b), F.CN1), F.List(k, F.C0, F.Plus(F.CN1, a)))),
							F.And(F.IntegerQ(a), F.Greater(a, F.C0)))); // $$);

			MATCHER.caseOf(BetaRegularized(z_, a_, b_), //
					// [$ (Beta(z, a, b)*Gamma(a + b))/(Gamma(a)*Gamma(b)) $]
					F.Times(F.Beta(z, a, b), F.Power(F.Times(F.Gamma(a), F.Gamma(b)), F.CN1), F.Gamma(F.Plus(a, b)))); // $$);

			MATCHER.caseOf(Binomial(a_, b_), //
					// [$ If(IntegerQ(b)&&Positive(b),Product(a-c,{c,0,b-1})/b!, If(IntegerQ(a)&&Positive(a),
					// (a!*Sin(b*Pi))/(Product(b-c,{c,0,a})*Pi), Gamma(1 + a)/(Gamma(1 + b)*Gamma(1 - b + a))) ) $]
					F.If(F.And(F.IntegerQ(b), F
							.Positive(b)), F
									.Times(F.Power(F.Factorial(b), F.CN1), F.Product(F.Subtract(a, c),
											F.List(c, F.C0, F.Plus(F.CN1, b)))),
							F.If(F.And(F.IntegerQ(a), F
									.Positive(a)), F
											.Times(F.Power(
													F.Times(F.Product(F.Subtract(b, c), F.List(c, F.C0, a)),
															F.Pi),
													F.CN1), F.Factorial(a), F.Sin(F.Times(b, F.Pi))),
									F.Times(F.Gamma(F.Plus(F.C1, a)), F.Power(
											F.Times(F.Gamma(F.Plus(F.C1, b)), F.Gamma(F.Plus(F.C1, F.Negate(b), a))),
											F.CN1))))); // $$);

			// CatalanNumber
			MATCHER.caseOf(F.CatalanNumber(n_), //
					// [$ (2^(2*n)*Gamma(1/2 + n))/(Sqrt(Pi)*Gamma(2 + n)) $]
					F.Times(F.Power(F.C2, F.Times(F.C2, n)), F.Gamma(F.Plus(F.C1D2, n)),
							F.Power(F.Times(F.Sqrt(F.Pi), F.Gamma(F.Plus(F.C2, n))), F.CN1))); // $$);
			// ChebyshevT
			MATCHER.caseOf(ChebyshevT(n_, x_), //
					// [$ Cos(n*ArcCos(x)) $]
					F.Cos(F.Times(n, F.ArcCos(x)))); // $$);
			// ChebyshevU
			MATCHER.caseOf(ChebyshevU(n_, x_), //
					// [$ Sin((1 + n)*ArcCos(x))/(Sqrt(1 - x)*Sqrt(1 + x)) $]
					F.Times(F.Power(F.Times(F.Sqrt(F.Subtract(F.C1, x)), F.Sqrt(F.Plus(F.C1, x))), F.CN1),
							F.Sin(F.Times(F.Plus(F.C1, n), F.ArcCos(x))))); // $$);

			// Cos
			MATCHER.caseOf(F.Cos(F.Sqrt(F.Sqr(x_))), //
					F.Cos(x));
			// Sin
			MATCHER.caseOf(F.Sin(F.Sqrt(F.Sqr(x_))), //
					// [$ (Sqrt(x^2)*Sin(x))/x $]
					F.Times(F.Power(x, F.CN1), F.Sqrt(F.Sqr(x)), F.Sin(x))); // $$);

			// CosIntegral
			MATCHER.caseOf(F.CosIntegral(F.Times(F.CN1, x_)), //
					// [$ CosIntegral(x) + Log(x) - Log(x)
					// $]
					F.Plus(F.CosIntegral(x), F.Negate(F.Log(x)), F.Log(x))); // $$);
			MATCHER.caseOf(F.CosIntegral(F.Times(F.CI, x_)), //
					// [$ CoshIntegral(x) - Log(x) + Log(I*x)
					// $]
					F.Plus(F.CoshIntegral(x), F.Negate(F.Log(x)), F.Log(F.Times(F.CI, x)))); // $$);
			MATCHER.caseOf(F.CosIntegral(F.Times(F.CNI, x_)), //
					// [$ CoshIntegral(x) - Log(x) + Log(-I*x)
					// $]
					F.Plus(F.CoshIntegral(x), F.Negate(F.Log(x)), F.Log(F.Times(F.CNI, x)))); // $$);
			MATCHER.caseOf(F.CosIntegral(F.Power(F.Power(x_, F.C2), F.C1D2)), //
					// [$ CosIntegral(x) + Log(Sqrt(x^2)) - Log(x)
					// $]
					F.Plus(F.CosIntegral(x), F.Negate(F.Log(x)), F.Log(F.Sqrt(F.Sqr(x))))); // $$);

			// SinIntegral
			MATCHER.caseOf(F.SinIntegral(F.Power(F.Power(x_, F.C2), F.C1D2)), //
					// [$ (Sqrt(x^2)/x) * SinIntegral(x)
					// $]
					F.Times(F.Power(x, F.CN1), F.Sqrt(F.Sqr(x)), F.SinIntegral(x))); // $$);

			// Factorial
			MATCHER.caseOf(Factorial(x_), //
					// [$ Gamma(1+x) $]
					F.Gamma(F.Plus(F.C1, x))); // $$);

			// Fibonacci
			MATCHER.caseOf(F.Fibonacci(F.Plus(m_, n_)), //
					// [$ ((1/2)*Fibonacci(m)*LucasL(n) + (1/2)*Fibonacci(n)*LucasL(m)) /; IntegerQ(m)&&Element(n,
					// Integers)
					// $]
					F.Condition(
							F.Plus(F.Times(F.C1D2, F.Fibonacci(m), F.LucasL(n)),
									F.Times(F.C1D2, F.Fibonacci(n), F.LucasL(m))),
							F.And(F.IntegerQ(m), F.Element(n, F.Integers)))); // $$);

			// Haversine
			MATCHER.caseOf(F.Haversine(x_), //
					// [$ (1/2) * (1 - Cos(x)) $]
					F.Times(F.C1D2, F.Subtract(F.C1, F.Cos(x)))); // $$);
			// InverseHaversine
			MATCHER.caseOf(F.InverseHaversine(x_), //
					// [$ 2*ArcSin( Sqrt(x) ) $]
					F.Times(F.C2, F.ArcSin(F.Sqrt(x)))); // $$);

			// LegendreQ
			MATCHER.caseOf(F.LegendreQ(x_, y_, z_), //
					// [$ -((Pi*Csc(Pi*y)*Gamma(1 + x + y)*LegendreP(x, -y, z))/(2*Gamma(1 + x - y))) +
					// (1/2)*Pi*Cot(Pi*y)*LegendreP(x, y, z) $]
					F.Plus(F.Times(F.CN1, F.Pi, F.Csc(F.Times(F.Pi, y)),
							F.Power(F.Times(F.C2, F.Gamma(F.Plus(F.C1, x, F.Negate(y)))), F.CN1),
							F.Gamma(F.Plus(F.C1, x, y)), F.LegendreP(x, F.Negate(y), z)),
							F.Times(F.C1D2, F.Pi, F.Cot(F.Times(F.Pi, y)), F.LegendreP(x, y, z)))); // $$);
			// Pochhammer
			MATCHER.caseOf(F.Pochhammer(x_, y_), //
					// [$ Gamma(x+y)/Gamma(x) $]
					F.Times(F.Power(F.Gamma(x), F.CN1), F.Gamma(F.Plus(x, y)))); // $$);
			// PolyGamma
			MATCHER.caseOf(F.PolyGamma(F.CN2, F.C1), //
					// [$ (1/2)*(Log(2)+Log(Pi)) $]
					F.Times(F.C1D2, F.Plus(F.Log(F.C2), F.Log(F.Pi)))); // $$);
			MATCHER.caseOf(F.PolyGamma(F.CN3, F.C1), //
					// [$ Log(Glaisher) + (1/4)*(Log(2) + Log(Pi)) $]
					F.Plus(F.Log(F.Glaisher), F.Times(F.C1D4, F.Plus(F.Log(F.C2), F.Log(F.Pi))))); // $$);
			// MATCHER.caseOf(F.PolyGamma(m_, F.C1), //
			// (1/(-m - 1)!)*(((-m - 1)*EulerGamma)/(-m) + PolyGamma(-m) + Sum(Binomial(-m - 1,
			// k)*(Sum((-1)^j*Binomial(k, j)*PolyGamma(k - j + 1)*Zeta(j - k, 2), {j, 1, k}) - PolyGamma(1 + k)), {k, 0,
			// -m
			// - 1}) - Sum(Binomial(-m - 1, k)*Derivative(1)[Zeta][-k], {k, 0, -m - 2})) /; IntegerQ(m) && m < 0

			// F.Condition(F.Times(F.Power(F.Factorial(F.Plus(F.CN1,F.Negate(m))),-1),F.Plus(F.Times(F.EulerGamma,F.Power(F.Negate(m),-1),F.Plus(F.CN1,F.Negate(m))),F.PolyGamma(F.Negate(m)),F.Sum(F.Times(F.Binomial(F.Plus(F.CN1,F.Negate(m)),k),F.Plus(F.Negate(F.PolyGamma(F.Plus(F.C1,k))),F.Sum(F.Times(F.Power(F.CN1,j),F.Binomial(k,j),F.PolyGamma(F.Plus(F.Negate(j),k,F.C1)),F.Zeta(F.Plus(j,F.Negate(k)),F.C2)),F.List(j,F.C1,k)))),F.List(k,F.C0,F.Plus(F.CN1,F.Negate(m)))),F.Negate(F.Sum(F.Times(F.Binomial(F.Plus(F.CN1,F.Negate(m)),k),F.$(F.$(F.Derivative(F.C1),F.Zeta),F.Negate(k))),F.List(k,F.C0,F.Plus(F.CN2,F.Negate(m))))))),F.And(F.IntegerQ(m),F.Less(m,F.C0))));
			// // $$);

			MATCHER.caseOf(F.Degree, //
					// [$ Pi/180 $]
					F.Times(F.QQ(1L, 180L), F.Pi)); // $$);
			MATCHER.caseOf(F.GoldenAngle, //
					// [$ (3-Sqrt(5))*Pi $]
					F.Times(F.Subtract(F.C3, F.CSqrt5), F.Pi)); // $$);
			MATCHER.caseOf(F.GoldenRatio, //
					// [$ 1/2*(1+Sqrt(5)) $]
					F.Times(F.C1D2, F.Plus(F.C1, F.CSqrt5))); // $$);

			// Power
			MATCHER.caseOf(F.Power(F.E, F.ArcSinh(x_)), //
					// [$ (x+Sqrt(1+x^2)) $]
					F.Plus(x, F.Sqrt(F.Plus(F.C1, F.Sqr(x))))); // $$);
			MATCHER.caseOf(F.Power(F.E, F.ArcCosh(x_)), //
					// [$ (x+Sqrt(x-1)*Sqrt(x+1)) $]
					F.Plus(x, F.Times(F.Sqrt(F.Plus(F.CN1, x)), F.Sqrt(F.Plus(x, F.C1))))); // $$);
			MATCHER.caseOf(F.Power(F.E, F.ArcTanh(x_)), //
					// [$ ((x+1)/Sqrt(1-x^2)) $]
					F.Times(F.Plus(x, F.C1), F.Power(F.Subtract(F.C1, F.Sqr(x)), F.CN1D2))); // $$);
			MATCHER.caseOf(F.Power(F.E, F.ArcCsch(x_)), //
					// [$ (1/x+Sqrt(1+1/x^2)) $]
					F.Plus(F.Power(x, F.CN1), F.Sqrt(F.Plus(F.C1, F.Power(x, F.CN2))))); // $$);
			MATCHER.caseOf(F.Power(F.E, F.ArcSech(x_)), //
					// [$ (1/x+Sqrt(1/x-1)*Sqrt(1/x+1)) $]
					F.Plus(F.Power(x, F.CN1), F.Times(F.Sqrt(F.Plus(F.CN1, F.Power(x, F.CN1))),
							F.Sqrt(F.Plus(F.Power(x, F.CN1), F.C1))))); // $$);
			MATCHER.caseOf(F.Power(F.E, F.ArcCoth(x_)), //
					// [$ (1/Sqrt((x-1)/(x+1))) $]
					F.Power(F.Times(F.Power(F.Plus(x, F.C1), F.CN1), F.Plus(F.CN1, x)), F.CN1D2)); // $$);

			// Log
			MATCHER.caseOf(F.Log(F.Times(m_, n_)), //
					// [$ (Log(m)+Log(n)) /; Positive(m)
					// $]
					F.Condition(F.Plus(F.Log(m), F.Log(n)), F.Positive(m))); // $$);

			// Log(x^(y_?( RealNumberQ(#) && (x>-1) && (#<1) )& ))
			MATCHER.caseOf(
					F.Log(F.Power(x_,
							F.PatternTest(y_,
									(F.Function(F.And(F.RealNumberQ(F.Slot1), F.Greater(F.Slot1, F.CN1),
											F.Less(F.Slot1, F.C1))))))), //
					// [$ (y * Log(x))
					// $]
					F.Times(y, F.Log(x))); // $$);

			MATCHER.caseOf(F.BartlettWindow.of(x_), WindowFunctions.bartlettWindow(x));
			MATCHER.caseOf(F.BlackmanHarrisWindow.of(x_), WindowFunctions.blackmanHarrisWindow(x));
			MATCHER.caseOf(F.BlackmanNuttallWindow.of(x_), WindowFunctions.blackmanNuttallWindow(x));
			MATCHER.caseOf(F.BlackmanWindow.of(x_), WindowFunctions.blackmanWindow(x));
			MATCHER.caseOf(F.DirichletWindow.of(x_), WindowFunctions.dirichletWindow(x));
			MATCHER.caseOf(F.HannWindow.of(x_), WindowFunctions.hannWindow(x));
			MATCHER.caseOf(F.FlatTopWindow.of(x_), WindowFunctions.flatTopWindow(x));
			MATCHER.caseOf(F.GaussianWindow.of(x_), WindowFunctions.gaussianWindow(x));
			MATCHER.caseOf(F.HammingWindow.of(x_), WindowFunctions.hammingWindow(x));
			MATCHER.caseOf(F.NuttallWindow.of(x_), WindowFunctions.nuttallWindow(x));
			MATCHER.caseOf(F.ParzenWindow.of(x_), WindowFunctions.parzenWindow(x));
			MATCHER.caseOf(F.TukeyWindow.of(x_), WindowFunctions.tukeyWindow(x));

		}
	}

	public FunctionExpand() {
	}

	private static IExpr beforeRules(IAST ast) {
		if (ast.isSqrt() && ast.base().isAST(F.Plus, 3)) {
			IAST plus = (IAST) ast.base();
			final IExpr arg1 = plus.arg1();
			final IExpr arg2 = plus.arg2();
			if (arg1.isRational()) {
				return nestedSquareRoots((IRational) arg1, arg2);
			}
		}
		return F.NIL;
	}

	/**
	 * 
	 * See: <a href="// https://en.wikipedia.org/wiki/Nested_radical#Two_nested_square_roots">Wikipedia - Nested radical
	 * - Two nested square roots</a>
	 * 
	 * @param arg1
	 * @param arg2
	 * @return
	 */
	private static IExpr nestedSquareRoots(IRational arg1, IExpr arg2) {
		if (arg1.isNegative()) {
			return nestedSquareRoots(arg1.negate(), arg2.negate()).//
					map(x -> F.Times(F.CI, x));
		} else {
			final EvalEngine engine = EvalEngine.get();
			boolean arg2IsNegative = false;
			IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg2);
			if (negExpr.isPresent()) {
				arg2 = negExpr;
				arg2IsNegative = true;
			}
			// (arg2/2) ^ 2
			IExpr squared = engine.evaluate(F.Sqr(F.Divide(arg2, F.C2)));
			if (squared.isRealResult()) {
				IAST list = QuarticSolver.quadraticSolve(F.C1, arg1.negate(), squared);
				if (list.isAST2()) {
					IExpr a = engine.evaluate(list.arg1());
					if (a.isRational()) {
						IExpr b = engine.evaluate(list.arg2());
						if (b.isRational()) {
							if (arg2IsNegative) {
								return F.Plus(F.Sqrt(a), F.Negate(F.Sqrt(b)));
							}
							return F.Plus(F.Sqrt(a), F.Sqrt(b));
						}
					}
				}
			}
		}
		return F.NIL;
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		IExpr result = F.REMEMBER_AST_CACHE.getIfPresent(ast);
		if (result != null) {
			return result;
		}
		IExpr arg1 = ast.arg1();
		IExpr assumptionExpr = F.NIL;
		if (ast.size() > 2) {
			IExpr arg2 = ast.arg2();
			if (!arg2.isRule()) {
				assumptionExpr = arg2;
			}
			final OptionArgs options = new OptionArgs(ast.topHead(), ast, 2, engine);
			assumptionExpr = options.getOption(F.Assumptions).orElse(assumptionExpr);
		}
		if (assumptionExpr.isPresent()) {
			if (assumptionExpr.isAST()) {
				IAssumptions oldAssumptions = engine.getAssumptions();
				IAssumptions assumptions = oldAssumptions;
				if (oldAssumptions == null) {
					assumptions = org.matheclipse.core.eval.util.Assumptions.getInstance(assumptionExpr);
				} else {
					assumptions = oldAssumptions.addAssumption((IAST) assumptionExpr);
				}
				if (assumptions != null) {
					try {
						engine.setAssumptions(assumptions);
						IExpr temp = MATCHER.replaceAll(arg1, FunctionExpand::beforeRules).orElse(arg1);
						F.REMEMBER_AST_CACHE.put(ast, temp);
						return temp;
					} finally {
						engine.setAssumptions(oldAssumptions);
					}
				}
			}

		}
		IExpr temp = MATCHER.replaceAll(arg1, FunctionExpand::beforeRules).orElse(arg1);
		F.REMEMBER_AST_CACHE.put(ast, temp);
		return temp;
	}

	public int[] expectedArgSize() {
		return IOFunctions.ARGS_1_2;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		Initializer.init();
		newSymbol.setAttributes(ISymbol.LISTABLE);
	}

}
