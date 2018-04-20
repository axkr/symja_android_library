package org.matheclipse.core.builtin;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.Supplier;

import org.hipparchus.linear.FieldMatrix;
import org.hipparchus.linear.RealMatrix;
import org.hipparchus.stat.StatUtils;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractMatrix1Expr;
import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.expression.ASTRealMatrix;
import org.matheclipse.core.expression.ASTRealVector;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IDistribution;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.rules.CDFRules;
import org.matheclipse.core.reflection.system.rules.PDFRules;
import org.matheclipse.core.reflection.system.rules.QuantileRules;
import org.matheclipse.core.reflection.system.rules.StandardDeviationRules;
import org.uncommons.maths.random.BinomialGenerator;
import org.uncommons.maths.random.DiscreteUniformGenerator;
import org.uncommons.maths.random.ExponentialGenerator;
import org.uncommons.maths.random.GaussianGenerator;
import org.uncommons.maths.random.PoissonGenerator;

public class StatisticsFunctions {

	static {
		F.CDF.setEvaluator(new CDF());
		F.PDF.setEvaluator(new PDF());
		F.BernoulliDistribution.setEvaluator(new BernoulliDistribution());
		F.BinCounts.setEvaluator(new BinCounts());
		F.BinomialDistribution.setEvaluator(new BinomialDistribution());
		F.CentralMoment.setEvaluator(new CentralMoment());
		F.Correlation.setEvaluator(new Correlation());
		F.Covariance.setEvaluator(new Covariance());
		F.DiscreteUniformDistribution.setEvaluator(new DiscreteUniformDistribution());
		F.ErlangDistribution.setEvaluator(new ErlangDistribution());
		F.Expectation.setEvaluator(new Expectation());
		F.ExponentialDistribution.setEvaluator(new ExponentialDistribution());
		F.FrechetDistribution.setEvaluator(new FrechetDistribution());
		F.GammaDistribution.setEvaluator(new GammaDistribution());
		F.GeometricDistribution.setEvaluator(new GeometricDistribution());
		F.GumbelDistribution.setEvaluator(new GumbelDistribution());
		F.HypergeometricDistribution.setEvaluator(new HypergeometricDistribution());
		F.Kurtosis.setEvaluator(new Kurtosis());
		F.LogNormalDistribution.setEvaluator(new LogNormalDistribution());
		F.Mean.setEvaluator(new Mean());
		F.MeanDeviation.setEvaluator(new MeanDeviation());
		F.Median.setEvaluator(new Median());
		F.NakagamiDistribution.setEvaluator(new NakagamiDistribution());
		F.NormalDistribution.setEvaluator(new NormalDistribution());
		F.PoissonDistribution.setEvaluator(new PoissonDistribution());
		F.Quantile.setEvaluator(new Quantile());
		F.RandomVariate.setEvaluator(new RandomVariate());
		F.Skewness.setEvaluator(new Skewness());
		F.StandardDeviation.setEvaluator(new StandardDeviation());
		F.Standardize.setEvaluator(new Standardize());
		F.StudentTDistribution.setEvaluator(new StudentTDistribution());
		F.Variance.setEvaluator(new Variance());
		F.WeibullDistribution.setEvaluator(new WeibullDistribution());
	}

	/**
	 * Capability to produce random variate.
	 */
	interface IRandomVariate {
		/**
		 * @param distribution
		 *            the distribution
		 * @return sample generated using the given random generator
		 */
		IExpr randomVariate(Random random, IAST distribution);
	}

	/**
	 * Functionality for a discrete probability distribution
	 */
	interface IDiscreteDistribution extends IDistribution {
		/** @return lowest value a random variable from this distribution may attain */
		IExpr lowerBound(IAST dist);

		IExpr randomVariate(Random random, IAST dist);

		/**
		 * @param n
		 * @return P(X == n), i.e. probability of random variable X == n
		 */
		IExpr p_equals(IAST dist, IExpr n);
	}

	/**
	 * Any distribution for which an analytic expression of the variance exists should implement {@link IVariance}.
	 * 
	 * <p>
	 * The function is used in {@link Expectation} to provide the variance of a given {@link IDistribution}.
	 */
	public interface IVariance {
		/** @return variance of distribution */
		IExpr variance(IAST distribution);
	}

	/**
	 * Cumulative distribution function
	 * 
	 * ICDF extends the capabilities of {@link IPDF}
	 * 
	 * 
	 */
	public interface ICDF {
		static final IExpr CDF_NUMERIC_THRESHOLD = F.num(1e-14);

		public IExpr cdf(IAST dist, IExpr x);

	}

	/**
	 * probability density function
	 * 
	 */
	interface IPDF {
		/**
		 * 
		 * <p>
		 * For {@link IDiscreteDistribution}, the function returns the P(X == x), i.e. probability of random variable X
		 * == x
		 * 
		 * <p>
		 * For continuous distributions, the function
		 * <ul>
		 * <li>returns the value of the probability density function, which is <em>not</em> identical to P(X == x)]
		 * </ul>
		 * 
		 * @param x
		 * @return
		 */
		IExpr pdf(IAST dist, IExpr x);
	}

	/** functionality and suggested base class for a discrete probability distribution */
	private static abstract class AbstractDiscreteDistribution extends AbstractEvaluator
			implements IDiscreteDistribution, IDistribution, IPDF, IRandomVariate {
		// @Override
		// public final IExpr randomVariate(Random random, IAST dist) {
		// return protected_quantile(dist, F.num(random.nextDouble()));
		// }

		/**
		 * @param p
		 *            in the semi-open interval [0, 1)
		 * @return
		 */
		protected abstract IExpr protected_quantile(IAST dist, IExpr p);

		@Override
		public IExpr pdf(IAST dist, IExpr x) throws ArithmeticException {
			if (!x.isInteger()) {
				return F.C0;
			}
			return p_equals(dist, x);
		}

		@Override
		public final IExpr p_equals(IAST dist, IExpr n) {
			IExpr result = F.eval(F.Less(n, lowerBound(dist)));
			if (result.isTrue()) {
				return F.C0;
			}
			return protected_p_equals(dist, n);
		}

		/**
		 * @param n
		 *            with n >= lowerBound()
		 * @return P(X == n), i.e. probability of random variable X == n
		 */
		protected abstract IExpr protected_p_equals(IAST dist, IExpr n);
	}

	/**
	 * Compute the cumulative distribution function
	 */
	private static class CDF extends AbstractFunctionEvaluator implements CDFRules {

		@Override
		public IAST getRuleAST() {
			return RULES;
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			// 1 or 2 arguments
			if (ast.isAST2()) {
				if (ast.arg1().isAST() && !ast.arg2().isList()) {
					try {
						IAST arg1 = (IAST) ast.arg1();
						IExpr xArg = ast.arg2();

						if (arg1.isDistribution()) {
							IExpr function = engine.evaluate(F.CDF(arg1));
							if (function.isFunction()) {
								if (ast.arg2().isList()) {
									return ((IAST) ast.arg2()).map(x -> F.unaryAST1(function, x), 1);
								}
								return F.unaryAST1(function, ast.arg2());
							}
						}

						if (arg1.isAST()) {
							IAST dist = arg1;

							if (dist.head().isSymbol()) {
								ISymbol head = (ISymbol) dist.head();

								if (dist.head().isSymbol()) {
									if (head instanceof IBuiltInSymbol) {
										IEvaluator evaluator = ((IBuiltInSymbol) head).getEvaluator();
										if (evaluator instanceof ICDF) {
											ICDF cdf = (ICDF) evaluator;
											return cdf.cdf(dist, xArg);
										}
									}
								}

							}
						}
					} catch (Exception ex) {
						if (Config.SHOW_STACKTRACE) {
							ex.printStackTrace();
						}
					}
				}
			}

			return F.NIL;
		}

	}

	private final static class BernoulliDistribution extends AbstractEvaluator
			implements ICDF, IDistribution, IPDF, IVariance {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);
			return F.NIL;
		}

		@Override
		public IExpr mean(IAST dist) {
			if (dist.isAST1()) {
				return dist.arg1();
			}
			return F.NIL;
		}

		@Override
		public IExpr cdf(IAST dist, IExpr k) {
			if (dist.isAST1()) {
				IExpr p = dist.arg1();
				// Piecewise({{0, k < 0}, {1 - p, 0<=k<1 }}, 1)
				return F.Piecewise(F.List(F.List(F.C0, F.Less(k, F.C0)),
						F.List(F.Plus(F.C1, F.Negate(p)), F.And(F.LessEqual(F.C0, k), F.Less(k, F.C1)))), F.C1);
			}
			return F.NIL;
		}

		@Override
		public IExpr pdf(IAST dist, IExpr k) {
			if (dist.isAST1()) {
				IExpr p = dist.arg1();
				// Piecewise({{1 - p, k == 0}, {p, k == 1}}, 0)
				return F.Piecewise(
						F.List(F.List(F.Plus(F.C1, F.Negate(p)), F.Equal(k, F.C0)), F.List(p, F.Equal(k, F.C1))), F.C0);
			}
			return F.NIL;
		}

		@Override
		public IExpr variance(IAST dist) {
			if (dist.isAST1()) {
				IExpr N = dist.arg1();
				return F.Times(N, F.Subtract(F.C1, N));
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	private final static class BinCounts extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			IExpr dx = F.C1;
			if (ast.size() == 3) {
				dx = ast.arg2();
			}
			if (ast.arg1().isList()) {
				IAST vector = (IAST) ast.arg1();
				if (vector.size() == 1) {
					return F.List();
				}
			}
			return F.NIL;
		}

	}

	private final static class BinomialDistribution extends AbstractEvaluator
			implements ICDF, IDistribution, IPDF, IVariance, IRandomVariate {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			// Validate.checkSize(ast, 3);
			// 2 args
			return F.NIL;
		}

		@Override
		public IExpr mean(IAST dist) {
			if (dist.isAST2()) {
				return F.Times(dist.arg1(), dist.arg2());
			}
			return F.NIL;
		}

		@Override
		public IExpr cdf(IAST dist, IExpr k) {
			if (dist.isAST2()) {
				IExpr n = dist.arg1();
				IExpr m = dist.arg2();
				// Piecewise({{BetaRegularized(1 - m, n - Floor(k), 1 + Floor(k)), 0<=k<n}, {1, k >= n}}, 0)
				return F.Piecewise(F.List(
						F.List(F.BetaRegularized(F.Plus(F.C1, F.Negate(m)), F.Plus(n, F.Negate(F.Floor(k))),
								F.Plus(F.C1, F.Floor(k))), F.And(F.LessEqual(F.C0, k), F.Less(k, n))),
						F.List(F.C1, F.GreaterEqual(k, n))), F.C0);
			}
			return F.NIL;
		}

		@Override
		public IExpr pdf(IAST dist, IExpr k) {
			if (dist.isAST2()) {
				IExpr n = dist.arg1();
				IExpr m = dist.arg2();
				// Piecewise({{(1 - m)^(-k + n)*m^k*Binomial(n, k), 0 <= k <= n}}, 0)
				return F.Piecewise(F.List(F.List(F.Times(F.Power(F.Plus(F.C1, F.Negate(m)), F.Plus(F.Negate(k), n)),
						F.Power(m, k), F.Binomial(n, k)), F.LessEqual(F.C0, k, n))), F.C0);
			}
			return F.NIL;
		}

		@Override
		public IExpr variance(IAST dist) {
			if (dist.isAST2()) {
				// (1 - m) m n
				return F.Times(dist.arg1(), dist.arg2(), F.Subtract(F.C1, dist.arg2()));
			}
			return F.NIL;
		}

		@Override
		public IExpr randomVariate(Random random, IAST dist) {
			if (dist.isAST2()) {
				int n = dist.arg1().toIntDefault(-1);
				if (n > 0) {
					double p = dist.arg2().evalDouble();
					return F.ZZ(new BinomialGenerator(n, p, random).nextValue());
				}
			}
			return F.NIL;
		}
	}

	/**
	 * <pre>
	 * CentralMoment(list, r)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * gives the the <code>r</code>th central moment (i.e. the <code>r</code>th moment about the mean) of
	 * <code>list</code>.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:<br />
	 * </p>
	 * <ul>
	 * <li><a href="https://en.wikipedia.org/wiki/Central_moment">Wikipedia - Central moment</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt;&gt; CentralMoment({1.1, 1.2, 1.4, 2.1, 2.4}, 4)
	 * 0.10085
	 * </pre>
	 */
	private final static class CentralMoment extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);
			if (ast.arg1().isList()) {
				IAST list = (IAST) ast.arg1();
				IExpr r = ast.arg2();
				return F.Divide(F.Total(F.Power(F.Subtract(list, F.Mean(list)), r)), F.Length(list));
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	private final static class Correlation extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);
			IExpr a = ast.arg1();
			IExpr b = ast.arg2();
			int dim1 = a.isVector();
			int dim2 = b.isVector();
			if (dim1 >= 0 && dim1 == dim2) {
				return F.Divide(F.Covariance(a, b), F.Times(F.StandardDeviation(a), F.StandardDeviation(b)));
			}
			return F.NIL;
		}

	}

	private final static class FrechetDistribution extends AbstractEvaluator
			implements ICDF, IDistribution, IPDF, IVariance {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);
			return F.NIL;
		}

		@Override
		public IExpr mean(IAST dist) {
			if (dist.isAST2()) {
				IExpr n = dist.arg1();
				IExpr m = dist.arg2();
				// Piecewise({{m*Gamma(1 - 1/n), 1 < n}}, Infinity)
				return F.Piecewise(
						F.List(F.List(F.Times(m, F.Gamma(F.Subtract(F.C1, F.Power(n, F.CN1)))), F.Less(F.C1, n))),
						F.CInfinity);
			}
			return F.NIL;
		}

		@Override
		public IExpr cdf(IAST dist, IExpr k) {
			if (dist.isAST2()) {
				IExpr n = dist.arg1();
				IExpr m = dist.arg2();
				// Piecewise({{E^(-(k/m)^(-n)), k > 0}}, 0)
				return F.Piecewise(
						F.List(F.List(F.Power(F.E, F.Negate(F.Power(F.Times(k, F.Power(m, -1)), F.Negate(n)))),
								F.Greater(k, F.C0))),
						F.C0);
			}
			return F.NIL;
		}

		@Override
		public IExpr pdf(IAST dist, IExpr k) {
			if (dist.isAST2()) {
				IExpr n = dist.arg1();
				IExpr m = dist.arg2();
				// Piecewise({{((k/m)^(-1 - n)*n)/(E^(k/m)^(-n)*m), k > 0}}, 0)
				return F.Piecewise(
						F.List(F.List(F.Times(F.Power(F.Times(k, F.Power(m, -1)), F.Plus(F.CN1, F.Negate(n))),
								F.Power(F.Times(F.Power(F.E, F.Power(F.Times(k, F.Power(m, -1)), F.Negate(n))), m), -1),
								n), F.Greater(k, F.C0))),
						F.C0);
			}
			return F.NIL;
		}

		@Override
		public IExpr variance(IAST dist) {
			if (dist.isAST2()) {
				IExpr n = dist.arg1();
				IExpr m = dist.arg2();
				// Piecewise({{m^2*(Gamma(1 - 2/n) - Gamma(1 - 1/n)^2), n > 2}}, Infinity)
				return F.Piecewise(F.List(F.List(
						F.Times(F.Sqr(m),
								F.Plus(F.Gamma(F.Plus(F.C1, F.Times(F.CN2, F.Power(n, -1)))),
										F.Negate(F.Sqr(F.Gamma(F.Plus(F.C1, F.Negate(F.Power(n, -1)))))))),
						F.Greater(n, F.C2))), F.CInfinity);
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	private final static class GammaDistribution extends AbstractEvaluator implements IDistribution, IVariance {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			// 2 or 4 arguments
			return F.NIL;
		}

		@Override
		public IExpr mean(IAST dist) {
			if (dist.isAST2()) {
				IExpr n = dist.arg1();
				IExpr m = dist.arg2();
				// m*n
				return F.Times(m, n);
			}
			if (dist.size() == 5) {
				IExpr a = dist.arg1();
				IExpr b = dist.arg2();
				IExpr g = dist.arg3();
				IExpr d = dist.arg4();
				return // [$ d + (b*Gamma(a + 1/g))/Gamma(a) $]
				F.Plus(d, F.Times(b, F.Power(F.Gamma(a), -1), F.Gamma(F.Plus(a, F.Power(g, -1))))); // $$;
			}
			return F.NIL;
		}

		@Override
		public IExpr variance(IAST dist) {
			if (dist.isAST2()) {
				IExpr n = dist.arg1();
				IExpr m = dist.arg2();
				// m^2*n
				return F.Times(F.Sqr(m), n);
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	private final static class GeometricDistribution extends AbstractDiscreteDistribution
			implements ICDF, IDiscreteDistribution, IDistribution, IPDF, IVariance, IRandomVariate {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);
			return F.NIL;
		}

		@Override
		public IExpr mean(IAST dist) {
			if (dist.isAST1()) {
				// -1 + 1/n
				IExpr n = dist.arg1();
				return F.Plus(F.CN1, F.Power(n, F.CN1));
			}
			return F.NIL;
		}

		@Override
		public IExpr cdf(IAST dist, IExpr k) {
			if (dist.isAST1()) {
				IExpr n = dist.arg1();
				// Piecewise({{1 - (1 - n)^(1 + Floor(k)), k >= 0}}, 0)
				return F.Piecewise(F.List(
						F.List(F.Plus(F.C1, F.Negate(F.Power(F.Plus(F.C1, F.Negate(n)), F.Plus(F.C1, F.Floor(k))))),
								F.GreaterEqual(k, F.C0))),
						F.C0);
			}
			return F.NIL;
		}

		@Override
		public IExpr pdf(IAST dist, IExpr k) {
			if (dist.isAST1()) {
				IExpr n = dist.arg1();
				// Piecewise({{(1 - n)^k*n, k >= 0}}, 0)
				return F.Piecewise(
						F.List(F.List(F.Times(F.Power(F.Plus(F.C1, F.Negate(n)), k), n), F.GreaterEqual(k, F.C0))),
						F.C0);
			}
			return F.NIL;
		}

		@Override
		public IExpr variance(IAST dist) {
			if (dist.isAST1()) {
				// (1-n) / n^2
				IExpr n = dist.arg1();
				return F.Times(F.Subtract(F.C1, n), F.Power(n, F.CN2));
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

		@Override
		public IExpr lowerBound(IAST dist) {
			return F.C0;
		}

		@Override
		protected IExpr protected_quantile(IAST dist, IExpr arg) {
			if (dist.isAST1()) {
				IExpr n = dist.arg1();
				IExpr num = F.Log(F.C1.subtract(arg));
				IExpr den = F.Log(F.C1.subtract(n));
				return F.Floor(num.divide(den));
			}
			return F.NIL;
		}

		@Override
		protected IExpr protected_p_equals(IAST dist, IExpr arg) {
			if (dist.isAST1()) {
				// n * Power(1-n, arg)
				IExpr n = dist.arg1();
				return n.multiply(F.Power(F.C1.subtract(n), arg));
			}
			return F.NIL;
		}

		@Override
		public final IExpr randomVariate(Random random, IAST dist) {
			return protected_quantile(dist, F.num(random.nextDouble()));
		}
	}

	private final static class GumbelDistribution extends AbstractEvaluator
			implements ICDF, IDistribution, IPDF, IVariance {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			// 0 or 2 args
			return F.NIL;
		}

		@Override
		public IExpr mean(IAST dist) {
			if (dist.isAST0()) {
				// -EulerGamma
				return F.EulerGamma.negate();
			}
			if (dist.isAST2()) {
				IExpr n = dist.arg1();
				IExpr m = dist.arg2();
				// -EulerGamma*m + n
				return F.Plus(F.Times(F.CN1, F.EulerGamma, m), n);
			}
			return F.NIL;
		}

		@Override
		public IExpr cdf(IAST dist, IExpr k) {
			if (dist.isAST2()) {
				IExpr n = dist.arg1();
				IExpr m = dist.arg2();
				// 1 - E^(-E^((k - n)/m))
				return F.Plus(F.C1, F
						.Negate(F.Power(F.E, F.Negate(F.Power(F.E, F.Times(F.Power(m, -1), F.Plus(k, F.Negate(n))))))));
			}
			return F.NIL;
		}

		@Override
		public IExpr pdf(IAST dist, IExpr k) {
			if (dist.isAST2()) {
				IExpr n = dist.arg1();
				IExpr m = dist.arg2();
				// E^(-E^((k - n)/m) + (k - n)/m)/m
				return F.Times(
						F.Power(F.E, F.Plus(F.Negate(F.Power(F.E, F.Times(F.Power(m, -1), F.Plus(k, F.Negate(n))))),
								F.Times(F.Power(m, -1), F.Plus(k, F.Negate(n))))),
						F.Power(m, -1));
			}
			return F.NIL;
		}

		@Override
		public IExpr variance(IAST dist) {
			if (dist.isAST2()) {
				IExpr m = dist.arg2();
				// (m^2*Pi^2)/6
				return F.Times(F.QQ(1, 6), F.Sqr(m), F.Sqr(F.Pi));
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	private final static class HypergeometricDistribution extends AbstractEvaluator
			implements ICDF, IDistribution, IPDF, IVariance {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 4);
			return F.NIL;
		}

		private int[] parameters(IAST hypergeometricDistribution) {
			if (hypergeometricDistribution.size() == 4) {
				int N = hypergeometricDistribution.arg1().toIntDefault(-1);
				int n = hypergeometricDistribution.arg2().toIntDefault(-1);
				int m_n = hypergeometricDistribution.arg3().toIntDefault(-1);
				if (N >= 0 && n >= 0 && m_n >= 0) {
					int param[] = new int[3];
					param[0] = N;
					param[1] = n;
					param[2] = m_n;
					return param;
				}
			}
			return null;
		}

		@Override
		public IExpr mean(IAST dist) {
			if (dist.isAST3()) {
				int param[] = parameters(dist);
				if (param != null) {
					// N * (n / m_n)
					return F.ZZ(param[0]).multiply(F.QQ(param[1], param[2]));
				}
				IExpr N = dist.arg1();
				IExpr n = dist.arg2();
				IExpr m_n = dist.arg3();
				return F.Divide(F.Times(N, n), m_n);
			}
			return F.NIL;
		}

		@Override
		public IExpr cdf(IAST dist, IExpr k) {
			if (dist.isAST3()) {
				IExpr n = dist.arg1();
				IExpr ns = dist.arg2();
				IExpr nt = dist.arg3();
				// Piecewise({{1 - (ns!*(-ns + nt)!*HypergeometricPFQRegularized({1, 1 - n + Floor(k), 1 - ns +
				// Floor(k)}, {2 + Floor(k), 2 - n - ns + nt + Floor(k)}, 1))/(Binomial(nt, n)*(-1 + n - Floor(k))!*(-1
				// + ns - Floor(k))!), 0 <= k && n + ns - nt <= k && k < n && k < ns}, {1, k >= n || k >= ns}}, 0)
				return F.Piecewise(F
						.List(F.List(
								F.Plus(F.C1,
										F.Times(F.CN1, F.Factorial(ns), F.Factorial(F.Plus(F.Negate(ns), nt)),
												F.Power(F.Times(F.Binomial(nt, n),
														F.Factorial(F.Plus(F.CN1, n, F.Negate(F.Floor(k)))),
														F.Factorial(F.Plus(F.CN1, ns, F.Negate(F.Floor(k))))), -1),
												F.HypergeometricPFQRegularized(
														F.List(F.C1, F.Plus(F.C1, F.Negate(n), F.Floor(k)),
																F.Plus(F.C1, F.Negate(ns), F.Floor(k))),
														F.List(F.Plus(F.C2, F.Floor(k)),
																F.Plus(F.C2, F.Negate(n), F.Negate(ns), nt,
																		F.Floor(k))),
														F.C1))),
								F.And(F.LessEqual(F.C0, k), F.LessEqual(F.Plus(n, ns, F.Negate(nt)), k), F.Less(k, n),
										F.Less(k, ns))),
								F.List(F.C1, F.Or(F.GreaterEqual(k, n), F.GreaterEqual(k, ns)))),
						F.C0);
			}
			return F.NIL;
		}

		@Override
		public IExpr pdf(IAST dist, IExpr k) {
			if (dist.isAST3()) {
				IExpr n = dist.arg1();
				IExpr ns = dist.arg2();
				IExpr nt = dist.arg3();
				// Piecewise({{(Binomial(ns, k)*Binomial(-ns + nt, -k + n))/Binomial(nt, n), 0 <= k <= n && n + ns - nt
				// <= k <= n && 0 <= k <= ns && n + ns - nt <= k <= ns}}, 0)
				return F.Piecewise(
						F.List(F.List(
								F.Times(F.Binomial(ns, k), F.Power(F.Binomial(nt, n), -1),
										F.Binomial(F.Plus(F.Negate(ns), nt), F.Plus(F.Negate(k), n))),
								F.And(F.LessEqual(F.C0, k, n), F.LessEqual(F.Plus(n, ns, F.Negate(nt)), k, n),
										F.LessEqual(F.C0, k, ns), F.LessEqual(F.Plus(n, ns, F.Negate(nt)), k, ns)))),
						F.C0);
			}
			return F.NIL;
		}

		@Override
		public IExpr variance(IAST dist) {
			if (dist.isAST3()) {
				int param[] = parameters(dist);
				if (param != null) {
					int N = param[0];
					int n = param[1];
					int m_n = param[2];
					IFraction rd1 = F.QQ(m_n - n, m_n);
					IFraction rd2 = F.QQ(m_n - N, m_n);
					IFraction rd3 = F.QQ(N, m_n - 1);
					IFraction rd4 = F.QQ(n, 1);
					return rd1.multiply(rd2).multiply(rd3).multiply(rd4);

				}
				IExpr N = dist.arg1();
				IExpr n = dist.arg2();
				IExpr mn = dist.arg3();
				// (n*(1 - n/m_n)*(m_n - N)*N)/((-1 + m_n)*m_n)
				return F.Times(F.Power(F.Plus(F.CN1, mn), -1), F.Power(mn, -1), n,
						F.Plus(F.C1, F.Times(F.CN1, F.Power(mn, -1), n)), F.Plus(mn, F.Negate(N)), N);
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	/**
	 * Compute the covariance.
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Covariance">Covariance</a>
	 * 
	 */
	private final static class Covariance extends AbstractMatrix1Expr {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);
			if (ast.size() == 2) {
				return super.evaluate(ast, engine);
			}

			if (ast.size() == 3) {
				final IAST arg1 = (IAST) ast.arg1();
				final IAST arg2 = (IAST) ast.arg2();
				return evaluateArg2(arg1, arg2, engine);
			}
			return F.NIL;
		}

		private IExpr evaluateArg2(final IAST arg1, final IAST arg2, EvalEngine engine) {
			try {
				int arg1Length = arg1.isVector();
				if (arg1Length > 1) {
					int arg2Length = arg2.isVector();
					if (arg1Length == arg2Length) {
						try {
							double[] arg1DoubleArray = arg1.toDoubleVector();
							double[] arg2DoubleArray = arg2.toDoubleVector();
							org.hipparchus.stat.correlation.Covariance cov = new org.hipparchus.stat.correlation.Covariance();
							return F.num(cov.covariance(arg1DoubleArray, arg2DoubleArray, true));
						} catch (Exception ex) {
							//
						}
						return vectorCovarianceSymbolic(arg1, arg2, arg1Length);
					}
				}
			} catch (final WrongArgumentType e) {
				// WrongArgumentType occurs in list2RealMatrix(),
				// if the matrix elements aren't pure numerical values
			} catch (final IndexOutOfBoundsException e) {
				if (Config.SHOW_STACKTRACE) {
					e.printStackTrace();
				}
			}
			return F.NIL;
		}

		public static IExpr vectorCovarianceSymbolic(final IAST arg1, final IAST arg2, int arg1Length) {
			if (arg1Length == 2) {
				return F.Times(F.C1D2, F.Subtract(arg1.arg1(), arg1.arg2()),
						F.Subtract(F.Conjugate(arg2.arg1()), F.Conjugate(arg2.arg2())));
			}
			IAST num1 = arg1.apply(F.Plus);
			IExpr factor = F.integer(-1 * (arg1.size() - 2));
			IASTAppendable v1 = F.PlusAlloc(arg1.size());
			v1.appendArgs(arg1.size(),
					i -> F.Times(F.CN1, num1.setAtClone(i, F.Times(factor, arg1.get(i))), F.Conjugate(arg2.get(i))));
			return F.Divide(v1, F.integer((arg1.argSize()) * (arg1.size() - 2)));
		}

		@Override
		public IExpr matrixEval(FieldMatrix<IExpr> matrix) {
			return F.NIL;
		}

		@Override
		public IExpr numericEval(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);
			if (ast.size() == 2) {
				return super.numericEval(ast, engine);
			}
			if (ast.size() == 3) {
				final IAST arg1 = (IAST) ast.arg1();
				final IAST arg2 = (IAST) ast.arg2();
				return evaluateArg2(arg1, arg2, engine);
			}
			return F.NIL;
		}

		@Override
		public IExpr realMatrixEval(RealMatrix matrix) {
			org.hipparchus.stat.correlation.Covariance cov = new org.hipparchus.stat.correlation.Covariance(matrix);
			return new ASTRealMatrix(cov.getCovarianceMatrix(), false);
		}
	}

	private final static class DiscreteUniformDistribution extends AbstractDiscreteDistribution
			implements IDistribution, IVariance, ICDF, IPDF, IRandomVariate {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);
			return F.NIL;
		}

		@Override
		public IExpr mean(IAST dist) {
			IExpr[] minMax = minmax(dist);
			if (minMax != null) {
				// (max + min)/2
				return F.Times(F.C1D2, F.Plus(minMax[0], minMax[1]));
			}
			return F.NIL;
		}

		@Override
		public IExpr variance(IAST dist) {
			IExpr[] minMax = minmax(dist);
			if (minMax != null) {
				// (1/12)*(-1+(1+max-min)^2)
				return F.Times(F.QQ(1L, 12L), F.Plus(F.CN1, F.Sqr(F.Plus(F.C1, minMax[1], F.Negate(minMax[0])))));
			}

			return F.NIL;
		}

		public IExpr[] minmax(IAST dist) {
			if (dist.size() == 2 && dist.arg1().isList()) {
				IAST list = (IAST) dist.arg1();
				if (list.isAST2()) {
					IExpr min = list.arg1();
					IExpr max = list.arg2();
					return new IExpr[] { min, max };
				}
			}
			return null;
		}

		@Override
		public IExpr cdf(IAST dist, IExpr k) {
			IExpr[] minMax = minmax(dist);
			if (minMax != null) {
				IExpr a = minMax[0];
				IExpr b = minMax[1];
				// Piecewise({{(1 - a + Floor(k))/(1 - a + b), a<=k<b)}, {1, k >= b}}, 0)
				return F.Piecewise(F.List(
						F.List(F.Times(F.Power(F.Plus(F.C1, F.Negate(a), b), -1),
								F.Plus(F.C1, F.Negate(a), F.Floor(k))), F.And(F.LessEqual(a, k), F.Less(k, b))),
						F.List(F.C1, F.GreaterEqual(k, b))), F.C0);
			}
			return F.NIL;
		}

		@Override
		public IExpr pdf(IAST dist, IExpr k) {
			IExpr[] minMax = minmax(dist);
			if (minMax != null) {
				IExpr a = minMax[0];
				IExpr b = minMax[1];
				// Piecewise({{1/(1 - a + b), a<=k<=b}}, 0)
				return F.Piecewise(F.List(F.List(F.Power(F.Plus(F.C1, F.Negate(a), b), -1), F.LessEqual(a, k, b))),
						F.C0);
			}
			return F.NIL;
		}

		@Override
		public IExpr lowerBound(IAST dist) {
			IExpr[] minMax = minmax(dist);
			if (minMax != null) {
				return minMax[0];
			}
			return F.NIL;
		}

		@Override
		protected IExpr protected_quantile(IAST dist, IExpr q) {
			IExpr[] minMax = minmax(dist);
			if (minMax != null) {
				IExpr min = minMax[0];
				IExpr max = minMax[1].inc();
				IExpr p = F.Power(F.Subtract(max, min), F.CN1);
				return min.add(F.Floor(q.multiply(p.inverse())));
			}
			return F.NIL;
		}

		@Override
		protected IExpr protected_p_equals(IAST dist, IExpr n) {
			IExpr[] minMax = minmax(dist);
			if (minMax != null) {
				IExpr min = minMax[0];
				IExpr max = minMax[1].inc();

				if (F.evalTrue(F.LessEqual(max, n))) {
					return F.C0;
				}
				return F.eval(F.Power(F.Subtract(max, min), F.CN1));
			}
			return F.NIL;
		}

		@Override
		public IExpr randomVariate(Random random, IAST dist) {
			IExpr[] minMax = minmax(dist);
			if (minMax != null) {
				int min = minMax[0].toIntDefault(Integer.MIN_VALUE);
				int max = minMax[1].toIntDefault(Integer.MIN_VALUE);
				if (min < max && min != Integer.MIN_VALUE) {
					return F.ZZ(new DiscreteUniformGenerator(min, max, random).nextValue());
				}
			}
			return F.NIL;
		}
	}

	private final static class ErlangDistribution extends AbstractEvaluator
			implements ICDF, IDistribution, IPDF, IVariance {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);
			return F.NIL;
		}

		@Override
		public IExpr mean(IAST dist) {
			if (dist.isAST2()) {
				// n/m
				return F.Divide(dist.arg1(), dist.arg2());
			}
			return F.NIL;
		}

		@Override
		public IExpr cdf(IAST dist, IExpr k) {
			if (dist.isAST2()) {
				IExpr n = dist.arg1();
				IExpr m = dist.arg2();
				// Piecewise({{GammaRegularized(n, 0, k*m), k > 0}}, 0)
				return F.Piecewise(F.List(F.List(F.GammaRegularized(n, F.C0, F.Times(k, m)), F.Greater(k, F.C0))),
						F.C0);
			}
			return F.NIL;
		}

		@Override
		public IExpr pdf(IAST dist, IExpr k) {
			if (dist.isAST2()) {
				IExpr n = dist.arg1();
				IExpr m = dist.arg2();
				// Piecewise({{(k^(-1 + n)*m^n)/(E^(k*m)*Gamma(n)), k > 0}}, 0)
				return F.Piecewise(
						F.List(F.List(
								F.Times(F.Power(k, F.Plus(F.CN1, n)), F.Power(m, n),
										F.Power(F.Times(F.Power(F.E, F.Times(k, m)), F.Gamma(n)), -1)),
								F.Greater(k, F.C0))),
						F.C0);
			}
			return F.NIL;
		}

		@Override
		public IExpr variance(IAST dist) {
			if (dist.isAST2()) {
				// n/(m^2)
				return F.Divide(dist.arg1(), F.Sqr(dist.arg2()));
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	private final static class Expectation extends AbstractEvaluator {
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			if (ast.arg2().isAST()) {
				IExpr arg1 = ast.arg1();
				IAST vars = VariablesSet.getVariables(arg1);
				IAST dist = (IAST) ast.arg2();
				if (dist.head().isSymbol()) {
					ISymbol head = (ISymbol) dist.head();
					if (head instanceof IBuiltInSymbol) {
						IEvaluator evaluator = ((IBuiltInSymbol) head).getEvaluator();
						if (evaluator instanceof IDiscreteDistribution) {
							IDiscreteDistribution distribution = (IDiscreteDistribution) evaluator;
							return of(dist, x -> {
								if (vars.size() > 1) {
									IExpr temp = arg1.replaceAll(F.Rule(vars.arg1(), x));
									if (temp.isPresent()) {
										return temp;
									}
								}
								return arg1;
							}, distribution);
						}
					}
				}
			}

			return F.NIL;
		}

		private static IExpr of(IAST dist, Function<IExpr, IExpr> function,
				IDiscreteDistribution discreteDistribution) {
			IExpr value = F.NIL;
			IExpr p_equals = F.C0;
			IExpr cumprob = F.C0;
			IExpr sample = discreteDistribution.lowerBound(dist);
			while (!isFinished(p_equals, cumprob)) {
				IExpr x = sample;
				p_equals = discreteDistribution.p_equals(dist, sample);
				cumprob = cumprob.add(p_equals);
				IExpr delta = function.apply(x).multiply(p_equals);
				value = value.isPresent() ? value.add(delta) : delta;
				sample = sample.inc();
			}
			return value;
		}

		static boolean isFinished(IExpr p_equals, IExpr cumprob) {
			if (cumprob.equals(F.C1)) {
				return true;
			}
			if (cumprob.isSignedNumber() && !(cumprob.isExactNumber())) {
				ISignedNumber sn = (ISignedNumber) cumprob.dec().abs();
				return sn.compareTo(ICDF.CDF_NUMERIC_THRESHOLD) < 0;
			}
			return false;
		}
	}

	private final static class ExponentialDistribution extends AbstractEvaluator
			implements ICDF, IDistribution, IPDF, IVariance, IRandomVariate {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);
			return F.NIL;
		}

		@Override
		public IExpr mean(IAST dist) {
			if (dist.isAST1()) {
				return F.Power(dist.arg1(), F.CN1);
			}
			return F.NIL;
		}

		@Override
		public IExpr variance(IAST dist) {
			if (dist.isAST1()) {
				return F.Power(dist.arg1(), F.CN2);
			}
			return F.NIL;
		}

		@Override
		public IExpr cdf(IAST dist, IExpr k) {
			if (dist.isAST1()) {
				IExpr n = dist.arg1();
				// Piecewise({{1 - E^((-k)*n), k >= 0}}, 0)
				return F.Piecewise(F.List(
						F.List(F.Plus(F.C1, F.Negate(F.Power(F.E, F.Times(F.CN1, k, n)))), F.GreaterEqual(k, F.C0))),
						F.C0);
			}
			return F.NIL;
		}

		@Override
		public IExpr pdf(IAST dist, IExpr k) {
			if (dist.isAST1()) {
				IExpr n = dist.arg1();
				// Piecewise({{n/E^(k*n), k >= 0}}, 0)
				return F.Piecewise(
						F.List(F.List(F.Times(F.Power(F.Power(F.E, F.Times(k, n)), -1), n), F.GreaterEqual(k, F.C0))),
						F.C0);
			}
			return F.NIL;
		}

		@Override
		public IExpr randomVariate(Random random, IAST dist) {
			if (dist.isAST1()) {
				if (dist.arg1().isSignedNumber() && dist.arg1().isPositiveResult()) {
					double rate = dist.arg1().evalDouble();
					return F.num(new ExponentialGenerator(rate, random).nextValue());
				}
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	private final static class Kurtosis extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);
			if (ast.arg1().isList()) {
				IAST list = (IAST) ast.arg1();
				return F.Divide(F.CentralMoment(list, F.C4), F.Power(F.CentralMoment(list, F.C2), F.C2));
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	private final static class LogNormalDistribution extends AbstractEvaluator
			implements ICDF, IDistribution, IPDF, IVariance {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			// Validate.checkSize(ast, 3);
			return F.NIL;
		}

		@Override
		public IExpr mean(IAST dist) {
			if (dist.isAST2()) {
				IExpr m = dist.arg1();
				IExpr s = dist.arg2();
				// (m,s) -> E^(m+s^2/2)
				return F.Power(F.E, F.Plus(m, F.Times(F.C1D2, F.Sqr(s))));
			}
			return F.NIL;
		}

		@Override
		public IExpr cdf(IAST dist, IExpr k) {
			if (dist.isAST2()) {
				IExpr n = dist.arg1();
				IExpr m = dist.arg2();
				// Piecewise({{(1/2)*Erfc((n - Log(k))/(Sqrt(2)*m)), k > 0}}, 0)
				return F.Piecewise(F.List(F.List(
						F.Times(F.C1D2,
								F.Erfc(F.Times(F.Power(F.Times(F.CSqrt2, m), -1), F.Plus(n, F.Negate(F.Log(k)))))),
						F.Greater(k, F.C0))), F.C0);
			}
			return F.NIL;
		}

		@Override
		public IExpr pdf(IAST dist, IExpr k) {
			if (dist.isAST2()) {
				IExpr n = dist.arg1();
				IExpr m = dist.arg2();
				// Piecewise({{1/(E^((-n + Log(k))^2/(2*m^2))*(k*m*Sqrt(2*Pi))), k > 0}}, 0)
				return F.Piecewise(
						F.List(F.List(F.Power(F.Times(
								F.Power(F.E,
										F.Times(F.Power(F.Times(F.C2, F.Sqr(m)), -1),
												F.Sqr(F.Plus(F.Negate(n), F.Log(k))))),
								k, m, F.Sqrt(F.Times(F.C2, F.Pi))), -1), F.Greater(k, F.C0))),
						F.C0);
			}
			return F.NIL;
		}

		@Override
		public IExpr variance(IAST dist) {
			if (dist.isAST2()) {
				IExpr m = dist.arg1();
				IExpr s = dist.arg2();
				// E^(2*m+s^2)*(-1+E^(s^2))
				return F.Times(F.Plus(F.CN1, F.Power(F.E, F.Sqr(s))), F.Power(F.E, F.Plus(F.Times(F.C2, m), F.Sqr(s))));
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	/**
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Arithmetic_mean">Arithmetic mean</a>
	 */
	private final static class Mean extends AbstractTrigArg1 {

		@Override
		public IExpr evaluateArg1(final IExpr arg1) {
			try {
				if (arg1.isRealVector()) {
					return F.num(StatUtils.mean(arg1.toDoubleVector()));
				}
				if (arg1.isList()) {
					final IAST list = (IAST) arg1;
					return F.Times(list.apply(F.Plus), F.Power(F.integer(list.argSize()), F.CN1));
				}

				if (arg1.isDistribution()) {
					IDistribution distribution = (IDistribution) ((IBuiltInSymbol) arg1.head()).getEvaluator();
					return distribution.mean((IAST) arg1);
				}
			} catch (Exception ex) {
				if (Config.SHOW_STACKTRACE) {
					ex.printStackTrace();
				}
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.NOATTRIBUTE);
		}

	}

	private final static class MeanDeviation extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			int[] dim = ast.arg1().isMatrix();
			if (dim == null && ast.arg1().isListOfLists()) {
				return F.NIL;
			}
			if (dim != null) {
				IAST matrix = (IAST) ast.arg1();
				return matrix.mapMatrixColumns(dim, x -> F.MeanDeviation(x));
			}

			int length = ast.arg1().isVector();
			if (length > 0) {
				IAST vector = (IAST) ast.arg1();
				int size = vector.size();
				IASTAppendable sum = F.PlusAlloc(size);
				final IExpr mean = F.eval(F.Mean(F.Negate(vector)));
				vector.forEach(x -> sum.append(F.Abs(F.Plus(x, mean))));
				return F.Times(F.Power(F.ZZ(size - 1), -1), sum);
			}

			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.NOATTRIBUTE);
		}

	}

	/**
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Median">Median</a>
	 */
	private final static class Median extends AbstractTrigArg1 {

		@Override
		public IExpr evaluateArg1(final IExpr arg1) {
			if (arg1.isRealVector()) {
				return F.num(StatUtils.percentile(arg1.toDoubleVector(), 50));
			}
			int[] dim = arg1.isMatrix();
			if (dim == null && arg1.isListOfLists()) {
				return F.NIL;
			}
			if (dim != null) {
				IAST matrix = (IAST) arg1;
				return matrix.mapMatrixColumns(dim, x -> F.Median(x));
			}
			if (arg1.isList()) {
				final IAST list = (IAST) arg1;
				if (list.size() > 1) {
					final IAST sortedList = EvalAttributes.copySortLess(list);
					int size = sortedList.size();
					if ((size & 0x00000001) == 0x00000001) {
						// odd number of elements
						size = size / 2;
						return F.Times(F.Plus(sortedList.get(size), sortedList.get(size + 1)), F.C1D2);
					} else {
						return sortedList.get(size / 2);
					}
				}
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.NOATTRIBUTE);
		}
	}

	private final static class NakagamiDistribution extends AbstractEvaluator
			implements ICDF, IDistribution, IPDF, IVariance {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			// Validate.checkSize(ast, 3);
			return F.NIL;
		}

		@Override
		public IExpr mean(IAST dist) {
			if (dist.isAST2()) {
				IExpr n = dist.arg1();
				IExpr m = dist.arg2();
				// (n,m) -> (Sqrt(m)*Pochhammer(n,1/2))/Sqrt(n)
				return F.Divide(F.Times(F.Sqrt(m), F.Pochhammer(n, F.C1D2)), F.Sqrt(n));
			}
			return F.NIL;
		}

		@Override
		public IExpr cdf(IAST dist, IExpr k) {
			if (dist.isAST2()) {
				IExpr n = dist.arg1();
				IExpr m = dist.arg2();
				// Piecewise({{GammaRegularized(n, 0, (k^2*n)/m), k > 0}}, 0)
				return F.Piecewise(F.List(
						F.List(F.GammaRegularized(n, F.C0, F.Times(F.Sqr(k), F.Power(m, -1), n)), F.Greater(k, F.C0))),
						F.C0);
			}
			return F.NIL;
		}

		@Override
		public IExpr pdf(IAST dist, IExpr k) {
			if (dist.isAST2()) {
				IExpr n = dist.arg1();
				IExpr m = dist.arg2();
				// Piecewise({{(2*k^(-1 + 2*n)*(n/m)^n)/(E^((k^2*n)/m)*Gamma(n)), k > 0}}, 0)
				return F.Piecewise(F.List(F.List(
						F.Times(F.C2, F.Power(k, F.Plus(F.CN1, F.Times(F.C2, n))),
								F.Power(F.Times(F.Power(m, -1), n), n),
								F.Power(F.Times(F.Power(F.E, F.Times(F.Sqr(k), F.Power(m, -1), n)), F.Gamma(n)), -1)),
						F.Greater(k, F.C0))), F.C0);
			}
			return F.NIL;
		}

		@Override
		public IExpr variance(IAST dist) {
			if (dist.isAST2()) {

				IExpr n = dist.arg1();
				IExpr m = dist.arg2();
				// m - (m*Pochhammer(n, 1/2)^2)/n
				return F.Subtract(m, F.Divide(F.Times(m, F.Sqr(F.Pochhammer(n, F.C1D2))), n));
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	/**
	 * <pre>
	 * NormalDistribution(m, s)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the normal distribution of mean <code>m</code> and sigma <code>s</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * NormalDistribution()
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the standard normal distribution for <code>m = 0</code> and <code>s = 1</code>.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:<br />
	 * </p>
	 * <ul>
	 * <li><a href="https://en.wikipedia.org/wiki/Normal_distribution">Wikipedia - Normal distribution</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * <p>
	 * The <a href="https://en.wikipedia.org/wiki/Probability_density">probability density function</a> of the normal
	 * distribution is
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; PDF(NormalDistribution(m, s), x)
	 * 1/(Sqrt(2)*E^((-m+x)^2/(2*s^2))*Sqrt(Pi)*s)
	 * </pre>
	 * <p>
	 * The <a href="https://en.wikipedia.org/wiki/Cumulative_distribution_function">cumulative distribution function</a>
	 * of the standard normal distribution is
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; CDF(NormalDistribution( ), x)
	 * 1/2*(2-Erfc(x/Sqrt(2)))
	 * </pre>
	 * <p>
	 * The <a href="https://en.wikipedia.org/wiki/Mean">mean</a> of the normal distribution is
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Mean(NormalDistribution(m, s))
	 * m
	 * </pre>
	 * <p>
	 * The <a href="https://en.wikipedia.org/wiki/Standard_deviation">standard deviation</a> of the normal distribution
	 * is
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; StandardDeviation(NormalDistribution(m, s))
	 * s
	 * </pre>
	 * <p>
	 * The <a href="https://en.wikipedia.org/wiki/Variance">variance</a> of the normal distribution is
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Variance(NormalDistribution(m, s))
	 * s^2
	 * </pre>
	 * <p>
	 * The <a href="https://en.wikipedia.org/wiki/Normal_distribution#Generating_values_from_normal_distribution">random
	 * variates</a> of a normal distribution can be generated with function <code>RandomVariate</code>
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; RandomVariate(NormalDistribution(2,3), 10^1)
	 * {1.14364,6.09674,5.16495,2.39937,-0.52143,-1.46678,3.60142,-0.85405,2.06373,-0.29795}
	 * </pre>
	 */
	private final static class NormalDistribution extends AbstractEvaluator
			implements IDistribution, IVariance, IRandomVariate {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			// 0 or 2 args are allowed
			return F.NIL;
		}

		@Override
		public IExpr mean(IAST dist) {
			if (dist.isAST0()) {
				return F.C0;
			}
			if (dist.isAST2()) {
				return dist.arg1();
			}
			return F.NIL;
		}

		@Override
		public IExpr variance(IAST dist) {
			if (dist.isAST0()) {
				return F.C1;
			}
			if (dist.isAST2()) {
				return F.Sqr(dist.arg2());
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

		@Override
		public IExpr randomVariate(Random random, IAST dist) {
			if (dist.isAST0()) {
				return F.num(random.nextGaussian());
			}
			// return protected_quantile(dist, F.num(ThreadLocalRandom.current().nextDouble()));
			if (dist.isAST2()) {
				if (dist.arg1().isSignedNumber() && dist.arg1().isPositiveResult()) {
					double mean = dist.arg1().evalDouble();
					double sigma = dist.arg2().evalDouble();
					return F.num(new GaussianGenerator(mean, sigma, random).nextValue());
				}
			}
			return F.NIL;
		}

	}

	/**
	 * Compute the probability density function
	 */
	private static class PDF extends AbstractFunctionEvaluator implements PDFRules {

		@Override
		public IAST getRuleAST() {
			return RULES;
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			// 1 or 2 arguments

			if (ast.isAST2()) {
				if (ast.arg1().isAST()) {
					try {
						IAST arg1 = (IAST) ast.arg1();
						IExpr xArg = ast.arg2();

						if (arg1.isDistribution()) {
							IExpr function = engine.evaluate(F.PDF(arg1));
							if (function.isFunction()) {
								if (ast.arg2().isList()) {
									return ((IAST) ast.arg2()).map(x -> F.unaryAST1(function, x), 1);
								}
								return F.unaryAST1(function, ast.arg2());
							}
						}

						if (arg1.isAST()) {
							IAST dist = arg1;

							if (dist.head().isSymbol()) {
								ISymbol head = (ISymbol) dist.head();

								if (dist.head().isSymbol()) {
									if (head instanceof IBuiltInSymbol) {
										IEvaluator evaluator = ((IBuiltInSymbol) head).getEvaluator();
										if (evaluator instanceof IPDF) {
											IPDF pdf = (IPDF) evaluator;
											return pdf.pdf(dist, xArg);
										}
									}
								}
							}
						}

					} catch (Exception ex) {
						if (Config.SHOW_STACKTRACE) {
							ex.printStackTrace();
						}
					}
				}
			}
			return F.NIL;
		}

	}

	private final static class PoissonDistribution extends AbstractEvaluator
			implements ICDF, IDistribution, IPDF, IVariance, IRandomVariate {
		// private static final int P_EQUALS_MAX = 1950; // probabilities are zero beyond that point

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);
			return F.NIL;
		}

		@Override
		public IExpr mean(IAST dist) {
			if (dist.isAST1()) {
				return dist.arg1();
			}
			return F.NIL;
		}

		@Override
		public IExpr cdf(IAST dist, IExpr k) {
			if (dist.isAST1()) {
				IExpr p = dist.arg1();
				// Piecewise({{GammaRegularized(1 + Floor(k), p), k >= 0}}, 0)
				return F.Piecewise(
						F.List(F.List(F.GammaRegularized(F.Plus(F.C1, F.Floor(k)), p), F.GreaterEqual(k, F.C0))), F.C0);
			}
			return F.NIL;
		}

		@Override
		public IExpr pdf(IAST dist, IExpr k) {
			if (dist.isAST1()) {
				IExpr p = dist.arg1();
				// Piecewise({{p^k/(E^p*k!), k >= 0}}, 0)
				return F.Piecewise(
						F.List(F.List(F.Times(F.Power(p, k), F.Power(F.Times(F.Power(F.E, p), F.Factorial(k)), -1)),
								F.GreaterEqual(k, F.C0))),
						F.C0);
			}
			return F.NIL;
		}

		@Override
		public IExpr variance(IAST dist) {
			if (dist.isAST1()) {
				return dist.arg1();
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

		// // @Override
		// public IExpr lowerBound(IAST dist) {
		// return F.C0;
		// }
		//
		// // @Override
		// protected IExpr protected_p_equals(IAST dist, IExpr nExpr) {
		// if (dist.isAST1() && nExpr.isInteger()) {
		// int n = nExpr.toIntDefault(Integer.MIN_VALUE);
		// if (n != Integer.MIN_VALUE) {
		// if (P_EQUALS_MAX < n) {
		// return F.C0;
		// }
		// IExpr lambda = dist.arg1();
		// IASTAppendable values = F.List();
		// values.append(F.Exp(lambda.negate()));
		// while (values.argSize() <= n) {
		// IExpr factor = lambda.times(F.fraction(1, values.argSize()));
		// values.append(values.last().times(factor));
		// }
		// return values.last();
		// }
		// }
		// return F.NIL;
		// }

		// @Override
		// protected IExpr protected_quantile(IAST dist, IExpr p) {
		// // TODO Auto-generated method stub
		// return F.NIL;
		// }

		@Override
		public IExpr randomVariate(Random random, IAST dist) {
			if (dist.isAST1()) {
				double mean = dist.arg1().evalDouble();
				return F.ZZ(new PoissonGenerator(mean, random).nextValue());
			}
			return F.NIL;
		}
	}

	/**
	 * <pre>
	 * Quantile(list, q)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the <code>q</code>-Quantile of <code>list</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * Quantile(list, {q1, q2, ...})
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns a list of the <code>q</code>-Quantiles of <code>list</code>.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:
	 * </p>
	 * <ul>
	 * <li><a href="https://en.wikipedia.org/wiki/Quantile">Wikipedia - Quantile</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Quantile({1,2}, 0.5)
	 * 1
	 * </pre>
	 */
	private final static class Quantile extends AbstractFunctionEvaluator implements QuantileRules {

		@Override
		public IAST getRuleAST() {
			return RULES;
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 4);

			IExpr arg1 = ast.arg1();
			int[] dim = arg1.isMatrix();
			if (dim == null && arg1.isListOfLists()) {
				return F.NIL;
			}
			if (dim != null) {
				IAST matrix = (IAST) arg1;
				return matrix.mapMatrixColumns(dim, (IExpr x) -> ast.setAtClone(1, x));
			}

			if (arg1.isList()) {
				IExpr a = F.C0;
				IExpr b = F.C0;
				IExpr c = F.C1;
				IExpr d = F.C0;
				if (ast.size() == 4) {
					IExpr arg3 = ast.arg3();
					int[] dimParameters = arg3.isMatrix();
					if (dimParameters == null || dimParameters[0] != 2 || dimParameters[1] != 2) {
						return F.NIL;
					}
					a = arg3.first().first();
					b = arg3.first().second();
					c = arg3.second().first();
					d = arg3.second().second();
				}

				IAST list = (IAST) arg1;
				int dim1 = list.argSize();
				try {
					if (dim1 >= 0 && ast.size() == 3) {

						final IAST s = EvalAttributes.copySortLess(list);
						final IInteger length = F.ZZ(s.argSize());

						IExpr q = ast.arg2();
						int dim2 = q.isVector();
						if (dim2 >= 0) {
							final IAST vector = ((IAST) q);
							if (vector.forAll(x -> x.isSignedNumber())) {
								return vector.map(scalar -> of(s, length, (ISignedNumber) scalar), 1);
							}
						} else {
							if (q.isSignedNumber()) {
								// x = a + (length + b) * q
								IExpr x = q.isZero() ? a : F.Plus.of(a, F.Times(F.Plus(length, b), q));
								if (x.isNumIntValue()) {
									int index = x.toIntDefault(Integer.MIN_VALUE);
									if (index != Integer.MIN_VALUE) {
										if (index < 1) {
											index = 1;
										} else if (index > s.argSize()) {
											index = s.argSize();
										}
										return s.get(index);
									}
								}
								if (x.isSignedNumber()) {
									ISignedNumber xi = (ISignedNumber) x;
									int xFloor = xi.floorFraction().toIntDefault(Integer.MIN_VALUE);
									int xCeiling = xi.ceilFraction().toIntDefault(Integer.MIN_VALUE);
									if (xFloor != Integer.MIN_VALUE && xCeiling != Integer.MIN_VALUE) {
										if (xFloor < 1) {
											xFloor = 1;
										}
										if (xCeiling > s.argSize()) {
											xCeiling = s.argSize();
										}
										// factor = c + d * FractionalPart(x);
										IExpr factor = d.isZero() || xi.isZero() ? c
												: F.Plus.of(c, F.Times(d, xi.fractionalPart()));
										// s[[Floor(x)]]+(s[[Ceiling(x)]]-s[[Floor(x)]]) * (c + d * FractionalPart(x))
										return F.Plus(s.get(xFloor), //
												F.Times(F.Subtract(s.get(xCeiling), s.get(xFloor)), factor));
									}
								}
								// return of(s, length, q);
							}
						}
					}
				} catch (ArithmeticException ae) {
					if (Config.SHOW_STACKTRACE) {
						ae.printStackTrace();
					}
				}
			} else if (arg1.isDistribution() && ast.size() >= 3) {
				IExpr function = engine.evaluate(F.Quantile(arg1));
				if (function.isFunction()) {
					if (ast.arg2().isList()) {
						return ((IAST) ast.arg2()).map(x -> F.unaryAST1(function, x), 1);
					}
					return F.unaryAST1(function, ast.arg2());
				}
			}
			return F.NIL;
		}

		private IExpr of(IAST sorted, IInteger length, ISignedNumber scalar) {
			if (scalar.isSignedNumber()) {
				int index = 0;
				if (scalar instanceof INum) {
					index = ((INum) scalar).multiply(length).ceilFraction().subtract(F.C1).toIntDefault(-1);
				} else {
					index = ((IRational) scalar).multiply(length).ceil().subtract(F.C1).toIntDefault(-1);
				}
				if (index >= 0) {
					return sorted.get(index + 1);
				}
			}
			throw new ArithmeticException();
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			super.setUp(newSymbol);
		}
	}

	private final static class RandomVariate extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			if (ast.arg1().isAST()) {
				IAST dist = (IAST) ast.arg1();
				if (dist.head().isSymbol()) {
					try {
						ISymbol head = (ISymbol) dist.head();
						if (head instanceof IBuiltInSymbol) {
							IEvaluator evaluator = ((IBuiltInSymbol) head).getEvaluator();
							if (evaluator instanceof IRandomVariate) {
								Random random = ThreadLocalRandom.current();
								IRandomVariate variate = (IRandomVariate) evaluator;
								if (ast.size() == 3) {
									IExpr arg2 = ast.arg2();
									if (arg2.isList()) {
										int[] indx = Validate.checkListOfInts(arg2, 0, Integer.MAX_VALUE);
										IASTAppendable list = F.ListAlloc(indx[0]);
										return createArray(indx, 0, list, () -> variate.randomVariate(random, dist));
									} else {
										int n = arg2.toIntDefault(Integer.MIN_VALUE);
										if (n >= 0) {
											IASTAppendable result = F.ListAlloc(n);
											for (int i = 0; i < n; i++) {
												result.append(variate.randomVariate(random, dist));
											}
											return result;
										}
									}
									return F.NIL;
								}
								return variate.randomVariate(random, dist);
							}
						}
					} catch (Exception ex) {
						if (Config.SHOW_STACKTRACE) {
							ex.printStackTrace();
						}
					}
				}
			}

			return F.NIL;
		}

		private static IAST createArray(int[] indx, int offset, IASTAppendable list, Supplier<IExpr> s) {
			if (indx.length <= offset) {
				list.append(s.get());
				return list;
			}
			IASTAppendable subList = F.ListAlloc(indx[offset]);
			for (int i = 1; i <= indx[offset]; i++) {
				createArray(indx, offset + 1, subList, s);
			}
			list.append(subList);
			return subList;
		}
	}

	/**
	 * <pre>
	 * Skewness(list)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * gives Pearson's moment coefficient of skewness for $list$ (a measure for estimating the symmetry of a
	 * distribution).
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt;&gt; Skewness({1.1, 1.2, 1.4, 2.1, 2.4})
	 * 0.40704
	 * </pre>
	 */
	private final static class Skewness extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);
			if (ast.arg1().isList()) {
				IAST list = (IAST) ast.arg1();
				return F.Divide(F.CentralMoment(list, F.C3), F.Power(F.CentralMoment(list, F.C2), F.C3D2));
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	private final static class StandardDeviation extends AbstractFunctionEvaluator implements StandardDeviationRules {

		@Override
		public IAST getRuleAST() {
			return RULES;
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			if (ast.arg1().isAST()) {
				IAST arg1 = (IAST) ast.arg1();
				int[] dim = arg1.isMatrix();
				if (dim == null && arg1.isListOfLists()) {
					return F.NIL;
				}
				if (dim != null) {
					IAST matrix = arg1;
					return matrix.mapMatrixColumns(dim, x -> F.StandardDeviation(x));
				}
			}
			return F.Sqrt(F.Variance(ast.arg1()));
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			super.setUp(newSymbol);
		}
	}

	private final static class Standardize extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);
			IExpr arg1 = ast.arg1();

			int[] dim = arg1.isMatrix();
			if (dim == null && arg1.isListOfLists()) {
				return F.NIL;
			}
			if (dim != null) {
				IAST matrix = (IAST) arg1;
				return F.Transpose(matrix.mapMatrixColumns(dim, v -> F.Standardize(v)));
			}

			IExpr sd = F.StandardDeviation.of(engine, arg1);
			if (!sd.isZero()) {
				return engine.evaluate(F.Divide(F.Subtract(arg1, F.Mean(arg1)), sd));
			}
			return F.NIL;
		}

	}

	private final static class StudentTDistribution extends AbstractEvaluator
			implements ICDF, IDistribution, IPDF, IVariance {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			// 1 or 3 args
			return F.NIL;
		}

		@Override
		public IExpr mean(IAST dist) {
			if (dist.isAST1()) {
				// (v) -> Piecewise[{{0, v > 1}}, Indeterminate]
				return F.Piecewise(F.List(F.List(F.C0, F.Greater(dist.arg1(), F.C1))), F.Indeterminate);
			}
			if (dist.isAST3()) {
				// (m,s,v) -> Piecewise[{{m, v > 1}}, Indeterminate]
				return F.Piecewise(F.List(F.List(dist.arg1(), F.Greater(dist.arg3(), F.C1))), F.Indeterminate);
			}
			return F.NIL;
		}

		@Override
		public IExpr cdf(IAST dist, IExpr k) {
			if (dist.isAST1()) {
				IExpr n = dist.arg1();
				// Piecewise({{(1/2)*BetaRegularized(n/(k^2 + n), n/2, 1/2), k <= 0}}, (1/2)*(1 +
				// BetaRegularized(k^2/(k^2 + n), 1/2, n/2)))
				return F.Piecewise(
						F.List(F.List(
								F.Times(F.C1D2,
										F.BetaRegularized(F.Times(n, F.Power(F.Plus(F.Sqr(k), n), -1)),
												F.Times(F.C1D2, n), F.C1D2)),
								F.LessEqual(k, F.C0))),
						F.Times(F.C1D2,
								F.Plus(F.C1, F.BetaRegularized(F.Times(F.Sqr(k), F.Power(F.Plus(F.Sqr(k), n), -1)),
										F.C1D2, F.Times(F.C1D2, n)))));
			}
			return F.NIL;
		}

		@Override
		public IExpr pdf(IAST dist, IExpr k) {
			if (dist.isAST1()) {
				IExpr n = dist.arg1();
				// (n/(k^2 + n))^((1 + n)/2)/(Sqrt(n)*Beta(n/2, 1/2))
				return F.Times(F.Power(n, F.CN1D2),
						F.Power(F.Times(n, F.Power(F.Plus(F.Sqr(k), n), -1)), F.Times(F.C1D2, F.Plus(F.C1, n))),
						F.Power(F.Beta(F.C1D2, F.Times(F.C1D2, n)), -1));
			}
			return F.NIL;
		}

		@Override
		public IExpr variance(IAST dist) {
			if (dist.isAST1()) {
				IExpr n = dist.arg1();
				return F.Piecewise(F.List(F.List(F.Divide(n, F.Plus(F.CN2, n)), F.Greater(n, F.C2))), F.Indeterminate);
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	/**
	 * Compute the variance for a list of elements
	 */
	private final static class Variance extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			if (ast.arg1().isAST()) {
				try {
					IAST arg1 = (IAST) ast.arg1();
					int[] matrixDimensions = arg1.isMatrix();
					if (matrixDimensions != null) {
						if (arg1.isRealMatrix()) {
							double[][] matrix = arg1.toDoubleMatrix();
							matrix = Convert.toDoubleTransposed(matrix);
							double[] result = new double[matrixDimensions[1]];
							for (int i = 0; i < matrix.length; i++) {
								result[i] = StatUtils.variance(matrix[i]);
							}
							return new ASTRealVector(result, false);
						}
						IASTAppendable result = F.ListAlloc(matrixDimensions[0]);
						for (int i = 1; i < matrixDimensions[1] + 1; i++) {
							final int ii = i;
							IASTAppendable list = F.ListAlloc(matrixDimensions[1]);
							IAST variance = F.Variance(list);
							list.appendArgs(matrixDimensions[0] + 1, j -> arg1.getPart(j, ii));
							result.append(variance);
						}
						return result;
					}

					int dim = arg1.isVector();
					if (dim >= 0) {
						if (arg1.isRealVector()) {
							return F.num(StatUtils.variance(arg1.toDoubleVector()));
						}
						return Covariance.vectorCovarianceSymbolic(arg1, arg1, dim);
					}

					if (arg1.isAST()) {
						IAST dist = arg1;
						if (dist.head().isSymbol()) {
							ISymbol head = (ISymbol) dist.head();
							if (head instanceof IBuiltInSymbol) {
								IEvaluator evaluator = ((IBuiltInSymbol) head).getEvaluator();
								if (evaluator instanceof IVariance) {
									IVariance distribution = (IVariance) evaluator;
									return distribution.variance(dist);
								}
							}
						}
					}
				} catch (Exception ex) {
					if (Config.SHOW_STACKTRACE) {
						ex.printStackTrace();
					}
				}
			}
			return F.NIL;
		}

	}

	private final static class WeibullDistribution extends AbstractEvaluator
			implements ICDF, IDistribution, IPDF, IVariance {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			// 2 or 3 args
			return F.NIL;
		}

		@Override
		public IExpr mean(IAST dist) {
			if (dist.isAST2()) {
				// (a,b) -> b*Gamma(1 + 1/a)
				return F.Times(dist.arg2(), F.Gamma(F.Plus(F.C1, F.Power(dist.arg1(), F.CN1))));
			}
			if (dist.isAST3()) {
				// (a,b,m) -> m + b*Gamma(1 + 1/a)
				return F.Plus(dist.arg3(), F.Times(dist.arg2(), F.Gamma(F.Plus(F.C1, F.Power(dist.arg1(), F.CN1)))));
			}

			return F.NIL;
		}

		@Override
		public IExpr cdf(IAST dist, IExpr k) {
			if (dist.isAST2()) {
				IExpr n = dist.arg1();
				IExpr m = dist.arg2();
				// Piecewise({{1 - E^(-(k/m)^n), k > 0}}, 0)
				return F.Piecewise(F.List(
						F.List(F.Plus(F.C1, F.Negate(F.Power(F.E, F.Negate(F.Power(F.Times(k, F.Power(m, -1)), n))))),
								F.Greater(k, F.C0))),
						F.C0);
			}
			return F.NIL;
		}

		@Override
		public IExpr pdf(IAST dist, IExpr k) {
			if (dist.isAST2()) {
				IExpr n = dist.arg1();
				IExpr m = dist.arg2();
				// Piecewise({{((k/m)^(-1 + n)*n)/(E^(k/m)^n*m), k > 0}}, 0)
				return F.Piecewise(F.List(F.List(
						F.Times(F.Power(F.Times(k, F.Power(m, -1)), F.Plus(F.CN1, n)),
								F.Power(F.Times(F.Power(F.E, F.Power(F.Times(k, F.Power(m, -1)), n)), m), -1), n),
						F.Greater(k, F.C0))), F.C0);
			}
			return F.NIL;
		}

		@Override
		public IExpr variance(IAST dist) {
			if (dist.isAST2()) {
				IExpr n = dist.arg1();
				IExpr m = dist.arg2();
				// m^2*(-Gamma(1 + 1/n)^2 + Gamma(1 + 2/n))
				return F.Times(F.Sqr(m), F.Plus(F.Negate(F.Sqr(F.Gamma(F.Plus(F.C1, F.Power(n, -1))))),
						F.Gamma(F.Plus(F.C1, F.Times(F.C2, F.Power(n, -1))))));
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	private final static StatisticsFunctions CONST = new StatisticsFunctions();

	public static StatisticsFunctions initialize() {
		return CONST;
	}

	private StatisticsFunctions() {

	}

}
