package org.matheclipse.core.builtin;

import java.util.Random;
import org.hipparchus.random.RandomDataGenerator;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.interfaces.statistics.ICDF;
import org.matheclipse.core.interfaces.statistics.ICentralMoment;
import org.matheclipse.core.interfaces.statistics.IDiscreteDistribution;
import org.matheclipse.core.interfaces.statistics.IPDF;
import org.matheclipse.core.interfaces.statistics.IRandomVariate;
import org.matheclipse.core.interfaces.statistics.IStatistics;

public class StatisticsDiscreteDistributions {
  /**
   *
   *
   * <pre>
   * BernoulliDistribution(p)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the Bernoulli distribution.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Bernoulli_distribution">Wikipedia - Bernoulli
   * distribution</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <p>
   * The probability density function of the Bernoulli distribution is
   *
   * <pre>
   * &gt;&gt; PDF(BernoulliDistribution(p), x)
   * Piecewise({{1-p,x==0},{p,x==1}},0)
   * </pre>
   *
   * <p>
   * The cumulative distribution function of the Bernoulli distribution is
   *
   * <pre>
   * &gt;&gt; CDF(BernoulliDistribution(p), x)
   * Piecewise({{0,x&lt;0},{1-p,0&lt;=x&amp;&amp;x&lt;1}},1)
   * </pre>
   *
   * <p>
   * The mean of the Bernoulli distribution is
   *
   * <pre>
   * &gt;&gt; Mean(BernoulliDistribution(p))
   * p
   * </pre>
   *
   * <p>
   * The standard deviation of the Bernoulli distribution is
   *
   * <pre>
   * &gt;&gt; StandardDeviation(BernoulliDistribution(p))
   * Sqrt((1-p)*p)
   * </pre>
   *
   * <p>
   * The variance of the Bernoulli distribution is
   *
   * <pre>
   * &gt;&gt; Variance(BernoulliDistribution(p))
   * (1-p)*p
   * </pre>
   *
   * <p>
   * The random variates of a Bernoulli distribution can be generated with function <code>
   * RandomVariate</code>
   *
   * <pre>
   * &gt;&gt; RandomVariate(BernoulliDistribution(0.25), 10^1)
   * {1,0,0,0,1,1,0,0,0,0}
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="CDF.md">CDF</a>, <a href="Mean.md">Mean</a>, <a href="Mean.md">Median</a>,
   * <a href="PDF.md">PDF</a>, <a href="Quantile.md">Quantile</a>,
   * <a href="StandardDeviation.md">StandardDeviation</a>, <a href="Variance.md">Variance</a>
   */
  private static final class BernoulliDistribution extends AbstractEvaluator
      implements ICDF, IDiscreteDistribution, IPDF, IStatistics, IRandomVariate, ICentralMoment {

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST1()) {
        IExpr p = dist.arg1();
        IExpr function =
            // [$ (Piecewise({{0, # < 0}, {1 - p, 0<=#<1 }}, 1)) & $]
            F.Function(
                F.Piecewise(F.list(F.list(F.C0, F.Less(F.Slot1, F.C0)), F.list(F.Subtract(F.C1, p),
                    F.And(F.LessEqual(F.C0, F.Slot1), F.Less(F.Slot1, F.C1)))), F.C1)); // $$;
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr centralMoment(IAST dist, IExpr m, EvalEngine engine) {
      if (dist.isAST1()) {
        IExpr n = dist.arg1();
        // Piecewise({{1,m==0},{((1-n)^(-1+m)-1/(-n)^(1-m))*(1-n)*n,m>0}},0);
        IExpr v1 = F.Plus(F.CN1, m);
        IExpr v2 = F.Subtract(F.C1, n);
        IExpr v3 = F.Negate(n);
        return F.Piecewise(
            F.list(F.list(F.C1, F.Equal(m, F.C0)), F.list(
                F.Times(n, v2, F.Subtract(F.Power(v2, v1), F.Power(v3, v1))), F.Greater(m, F.C0))),
            F.C0);
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public IExpr inverseCDF(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST1()) {
        IExpr p = dist.arg1();
        IExpr function =
            // [$ ( ConditionalExpression(Piecewise({{1, # > 1 - p}}, 0), 0 <= # <= 1)& ) $]
            F.Function(F.ConditionalExpression(
                F.Piecewise(F.list(F.list(F.C1, F.Greater(F.Slot1, F.Subtract(F.C1, p)))), F.C0),
                F.LessEqual(F.C0, F.Slot1, F.C1))); // $$;
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr kurtosis(IAST dist, EvalEngine engine) {
      if (dist.isAST1()) {
        IExpr a = dist.arg1();
        // 3+(1-6*(1-a)*a)/((1-a)*a);
        IExpr v1 = F.Subtract(F.C1, a);
        return F.Plus(F.C3,
            F.Times(F.Power(a, F.CN1), F.Power(v1, F.CN1), F.Plus(F.C1, F.Times(F.CN6, a, v1))));
      }
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
    public IExpr median(IAST dist) {
      if (dist.isAST1()) {
        // (p) => Piecewise({{1, p > 1/2}}, 0)
        return F.Piecewise(F.list(F.list(F.C1, F.Greater(dist.arg1(), F.C1D2))), F.C0);
      }
      return F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST1()) {
        IExpr p = dist.arg1();
        //
        IExpr function =
            // [$ Piecewise({{1 - p, # == 0}, {p, # == 1}}, 0) & $]
            F.Function(F.Piecewise(F.list(F.list(F.Subtract(F.C1, p), F.Equal(F.Slot1, F.C0)),
                F.list(p, F.Equal(F.Slot1, F.C1))), F.C0)); // $$;
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr randomVariate(Random random, IAST dist, int size) {
      if (dist.isAST1()) {
        // see exception handling in RandonmVariate() function
        double p = dist.arg1().evalf();
        if (0 <= p && p <= 1) {
          RandomDataGenerator rdg = new RandomDataGenerator();
          int[] vector = rdg.nextDeviates(
              new org.hipparchus.distribution.discrete.BinomialDistribution(1, p), size);
          return F.List(vector);
        }
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

    @Override
    public IExpr skewness(IAST dist) {
      if (dist.isAST1()) {
        IExpr s = dist.arg1();
        // (1 - 2*s)/Sqrt((1 - s)*s)
        return F.Divide(F.Subtract(F.C1, F.Times(F.C2, s)),
            F.Sqrt(F.Times(F.Subtract(F.C1, s), s)));
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

  }


  /**
   *
   *
   * <pre>
   * BinomialDistribution(n, p)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the binomial distribution.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Binomial_distribution">Wikipedia - Binomial
   * distribution</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <p>
   * The probability density function of the binomial distribution is
   *
   * <pre>
   * &gt;&gt; PDF(BinomialDistribution(n, p), x)
   * Piecewise({{(1-p)^(n-x)*p^x*Binomial(n,x),0&lt;=x&lt;=n}},0)
   * </pre>
   *
   * <p>
   * The cumulative distribution function of the binomial distribution is
   *
   * <pre>
   * &gt;&gt; CDF(BinomialDistribution(n, p), x)
   * Piecewise({{BetaRegularized(1-p,n-Floor(x),1+Floor(x)),0&lt;=x&amp;&amp;x&lt;n},{1,x&gt;=n}},0)
   * </pre>
   *
   * <p>
   * The mean of the binomial distribution is
   *
   * <pre>
   * &gt;&gt; Mean(BinomialDistribution(n, p))
   * n*p
   * </pre>
   *
   * <p>
   * The standard deviation of the binomial distribution is
   *
   * <pre>
   * &gt;&gt; StandardDeviation(BinomialDistribution(n, p))
   * Sqrt(n*(1-p)*p)
   * </pre>
   *
   * <p>
   * The variance of the binomial distribution is
   *
   * <pre>
   * &gt;&gt; Variance(BinomialDistribution(n, p))
   * n*(1-p)*p
   * </pre>
   *
   * <p>
   * The random variates of a binomial distribution can be generated with function <code>
   * RandomVariate</code>
   *
   * <pre>
   * &gt;&gt; RandomVariate(BinomialDistribution(10,0.25), 10^1)
   * {1,2,1,1,4,1,1,3,2,5}
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="CDF.md">CDF</a>, <a href="Mean.md">Mean</a>, <a href="Mean.md">Median</a>,
   * <a href="PDF.md">PDF</a>, <a href="Quantile.md">Quantile</a>,
   * <a href="StandardDeviation.md">StandardDeviation</a>, <a href="Variance.md">Variance</a>
   */
  private static final class BinomialDistribution extends AbstractEvaluator
      implements ICDF, IDiscreteDistribution, IPDF, IStatistics, IRandomVariate, ICentralMoment {

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        IExpr function =
            // [$ (Piecewise({{BetaRegularized(1 - m, n - Floor(#), 1 + Floor(#)), 0<=#<n}, {1, # >=
            // n}},
            // 0)) & $]
            F.Function(F.Piecewise(F.list(
                F.list(
                    F.BetaRegularized(F.Subtract(F.C1, m), F.Subtract(n, F.Floor(F.Slot1)),
                        F.Plus(F.C1, F.Floor(F.Slot1))),
                    F.And(F.LessEqual(F.C0, F.Slot1), F.Less(F.Slot1, n))),
                F.list(F.C1, F.GreaterEqual(F.Slot1, n))), F.C0)); // $$;
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr centralMoment(IAST dist, IExpr m, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public IExpr inverseCDF(IAST dist, IExpr k, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public IExpr kurtosis(IAST dist, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        // 3+(1-6*(1-b)*b)/(a*(1-b)*b);
        IExpr v1 = F.Subtract(F.C1, b);
        return F.Plus(F.C3, F.Times(F.Power(a, F.CN1), F.Power(b, F.CN1), F.Power(v1, F.CN1),
            F.Plus(F.C1, F.Times(F.CN6, b, v1))));

      }
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
    public IExpr median(IAST dist) {
      return F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        //
        IExpr function =
            // [$ Piecewise({{(1 - m)^(-# + n)*m^#*Binomial(n, #), 0 <= # <= n}}, 0) & $]
            F.Function(
                F.Piecewise(F.list(F.list(
                    F.Times(F.Power(F.Subtract(F.C1, m), F.Plus(F.Negate(F.Slot1), n)),
                        F.Power(m, F.Slot1), F.Binomial(n, F.Slot1)),
                    F.LessEqual(F.C0, F.Slot1, n))), F.C0)); // $$;
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr randomVariate(Random random, IAST dist, int size) {
      if (dist.isAST2()) {
        int n = dist.arg1().toIntDefault(-1);
        if (n > 0) {
          // see exception handling in RandonmVariate() function
          double p = dist.arg2().evalf();
          if (0 <= p && p <= 1) {
            RandomDataGenerator rdg = new RandomDataGenerator();
            int[] vector = rdg.nextDeviates(
                new org.hipparchus.distribution.discrete.BinomialDistribution(n, p), size);
            return F.List(vector);
            // return F.ZZ(new BinomialGenerator(n, p, random).nextValue());
          }
        }
      }
      return F.NIL;
    }

    @Override
    public IExpr skewness(IAST dist) {
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        // (1 - 2*m)/Sqrt((1 - m)*m*n)
        return F.Divide(F.Subtract(F.C1, F.Times(F.C2, m)),
            F.Sqrt(F.Times(F.Subtract(F.C1, m), m, n)));
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

  }


  /**
   *
   *
   * <pre>
   * DiscreteUniformDistribution({min, max})
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns a discrete uniform distribution.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Discrete_uniform_distribution">Wikipedia - Discrete
   * uniform distribution</a>
   * </ul>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="CDF.md">CDF</a>, <a href="Mean.md">Mean</a>, <a href="Mean.md">Median</a>,
   * <a href="PDF.md">PDF</a>, <a href="Quantile.md">Quantile</a>,
   * <a href="StandardDeviation.md">StandardDeviation</a>, <a href="Variance.md">Variance</a>
   */
  private static final class DiscreteUniformDistribution extends AbstractEvaluator
      implements IDiscreteDistribution, IStatistics, ICDF, IPDF, IRandomVariate {

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      IExpr[] minMax = minmax(dist);
      if (minMax != null) {
        IExpr a = minMax[0];
        IExpr b = minMax[1];
        IExpr function =
            // [$ (Piecewise({{(1 - a + Floor(#))/(1 - a + b), a<=#<b}, {1, # >= b}}, 0)) & $]
            F.Function(F.Piecewise(F.list(
                F.list(
                    F.Times(F.Power(F.Plus(F.C1, F.Negate(a), b), F.CN1),
                        F.Plus(F.C1, F.Negate(a), F.Floor(F.Slot1))),
                    F.And(F.LessEqual(a, F.Slot1), F.Less(F.Slot1, b))),
                F.list(F.C1, F.GreaterEqual(F.Slot1, b))), F.C0)); // $$;
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public IExpr inverseCDF(IAST dist, IExpr k, EvalEngine engine) {
      IExpr[] minMax = minmax(dist);
      if (minMax != null) {
        IExpr a = minMax[0];
        IExpr b = minMax[1];
        IExpr function =
            // [$ ( ConditionalExpression(Piecewise({{-1 + a + Max(1, Ceiling(#*(1 - a + b))), 0 < #
            // < 1},
            // {a, # <= 0}}, b), 0 <= # <= 1)& ) $]
            F.Function(
                F.ConditionalExpression(
                    F.Piecewise(
                        F.list(
                            F.list(
                                F.Plus(F.CN1, a,
                                    F.Max(F.C1,
                                        F.Ceiling(F.Times(F.Slot1, F.Plus(F.C1, F.Negate(a), b))))),
                                F.Less(F.C0, F.Slot1, F.C1)),
                            F.list(a, F.LessEqual(F.Slot1, F.C0))),
                        b),
                    F.LessEqual(F.C0, F.Slot1, F.C1))); // $$;
        return callFunction(function, k);
      }
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
    public IExpr median(IAST dist) {
      IExpr[] minMax = minmax(dist);
      if (minMax != null) {
        IExpr l = minMax[0];
        IExpr r = minMax[1];
        // (l,r) => -1 + l + Max(1, Ceiling((1/2)*(1 - l + r)))
        return F.Plus(F.CN1, l,
            F.Max(F.C1, F.Ceiling(F.Times(F.C1D2, F.Plus(F.C1, F.Negate(l), r)))));
      }
      return F.NIL;
    }

    public IExpr[] minmax(IAST dist) {
      if (dist.size() == 2 && dist.arg1().isList()) {
        IAST list = (IAST) dist.arg1();
        if (list.isAST2()) {
          IExpr min = list.arg1();
          IExpr max = list.arg2();
          return new IExpr[] {min, max};
        }
      }
      return null;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      IExpr[] minMax = minmax(dist);
      if (minMax != null) {
        IExpr a = minMax[0];
        IExpr b = minMax[1];
        IExpr function =
            // [$ ( Piecewise({{1/(1 - a + b), a <= # <= b}}, 0) & ) $]
            F.Function(F.Piecewise(F.list(
                F.list(F.Power(F.Plus(F.C1, F.Negate(a), b), F.CN1), F.LessEqual(a, F.Slot1, b))),
                F.C0)); // $$;
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr randomVariate(Random random, IAST dist, int size) {
      IExpr[] minMax = minmax(dist);
      if (minMax != null) {
        int min = minMax[0].toIntDefault();
        int max = minMax[1].toIntDefault();
        if (min < max && F.isPresent(min)) {
          RandomDataGenerator rdg = new RandomDataGenerator();
          int[] vector = rdg.nextDeviates(
              new org.hipparchus.distribution.discrete.UniformIntegerDistribution(min, max), size);
          return F.List(vector);
          // return F.ZZ(new DiscreteUniformGenerator(min, max, random).nextValue());
        }
      }
      return F.NIL;
    }

    @Override
    public IExpr skewness(IAST dist) {
      IExpr[] minMax = minmax(dist);
      if (minMax != null) {
        return F.C0;
      }
      return F.NIL;
    }

    @Override
    public IExpr variance(IAST dist) {
      IExpr[] minMax = minmax(dist);
      if (minMax != null) {
        // (1/12)*(-1+(1+max-min)^2)
        return F.Times(F.QQ(1L, 12L),
            F.Plus(F.CN1, F.Sqr(F.Plus(F.C1, minMax[1], F.Negate(minMax[0])))));
      }

      return F.NIL;
    }
  }


  private static final class GeometricDistribution extends AbstractEvaluator
      implements ICDF, IDiscreteDistribution, IPDF, IStatistics, ICentralMoment { // ,
    // IRandomVariate

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST1()) {
        IExpr n = dist.arg1();
        IExpr function =
            // [$ (Piecewise({{1 - (1 - n)^(1 + Floor(#)), # >= 0}}, 0)) & $]
            F.Function(F.Piecewise(F.list(F.list(
                F.Subtract(F.C1, F.Power(F.Subtract(F.C1, n), F.Plus(F.C1, F.Floor(F.Slot1)))),
                F.GreaterEqual(F.Slot1, F.C0))), F.C0)); // $$;
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr centralMoment(IAST dist, IExpr m, EvalEngine engine) {
      if (dist.isAST1()) {
        IExpr n = dist.arg1();
        // Piecewise({{n*HurwitzLerchPhi(1-n,-m,1-1/n),m>=0}},0)
        return F
            .Piecewise(
                F.list(
                    F.list(
                        F.Times(n,
                            F.HurwitzLerchPhi(F.Subtract(F.C1, n), F.Negate(m),
                                F.Subtract(F.C1, F.Power(n, F.CN1)))),
                        F.GreaterEqual(m, F.C0))),
                F.C0);
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public IExpr inverseCDF(IAST dist, IExpr k, EvalEngine engine) {
      return F.NIL;
    }


    @Override
    public IExpr kurtosis(IAST dist, EvalEngine engine) {
      if (dist.isAST1()) {
        IExpr n = dist.arg1();
        // 3+(6-6*n+n^2)/(1-n)
        return F.Plus(F.C3, F.Times(F.Power(F.Subtract(F.C1, n), F.CN1),
            F.Plus(F.C6, F.Times(F.CN6, n), F.Sqr(n))));
      }
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
    public IExpr median(IAST dist) {
      return F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST1()) {
        IExpr n = dist.arg1();
        //
        IExpr function =
            // [$ (Piecewise({{(1 - n)^#*n, # >= 0}}, 0)) & $]
            F.Function(F.Piecewise(F.list(F.list(F.Times(F.Power(F.Subtract(F.C1, n), F.Slot1), n),
                F.GreaterEqual(F.Slot1, F.C0))), F.C0)); // $$;
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

    @Override
    public IExpr skewness(IAST dist) {
      if (dist.isAST1()) {
        IExpr n = dist.arg1();
        return
        // [$ (2 - n)/Sqrt(1 - n) $]
        F.Times(F.Power(F.Subtract(F.C1, n), F.CN1D2), F.Subtract(F.C2, n)); // $$;
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

  }


  /**
   *
   *
   * <pre>
   * <code>HypergeometricDistribution(n, s, t)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns a hypergeometric distribution.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Hypergeometric_distribution">Wikipedia -
   * Hypergeometric distribution</a>
   * </ul>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="CDF.md">CDF</a>, <a href="Mean.md">Mean</a>, <a href="Mean.md">Median</a>,
   * <a href="PDF.md">PDF</a>, <a href="Quantile.md">Quantile</a>,
   * <a href="StandardDeviation.md">StandardDeviation</a>, <a href="Variance.md">Variance</a>
   */
  private static final class HypergeometricDistribution extends AbstractEvaluator
      implements ICDF, IDiscreteDistribution, IPDF, IStatistics, IRandomVariate {

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST3()) {
        IExpr n = dist.arg1();
        IExpr ns = dist.arg2();
        IExpr nt = dist.arg3();
        //
        IExpr function =
            // [$ Piecewise({{1 - (ns!*(-ns + nt)!*HypergeometricPFQRegularized({1, 1 - n +
            // Floor(#), 1 - ns
            // + Floor(#)}, {2 + Floor(#), 2 - n - ns + nt + Floor(#)}, 1))/(Binomial(nt, n)*(-1 + n
            // -
            // Floor(#))!*(-1 + ns - Floor(#))!), 0 <= # && n + ns - nt <= # && # < n && # < ns},
            // {1, # >= n
            // || # >= ns}}, 0) & $]
            F.Function(F.Piecewise(
                F.list(
                    F.list(
                        F.Plus(F.C1, F.Times(F.CN1, F.Factorial(ns),
                            F.Factorial(F.Plus(F.Negate(ns), nt)),
                            F.Power(
                                F.Times(F.Binomial(nt, n),
                                    F.Factorial(F.Plus(F.CN1, n, F.Negate(F.Floor(F.Slot1)))),
                                    F.Factorial(F.Plus(F.CN1, ns, F.Negate(F.Floor(F.Slot1))))),
                                F.CN1),
                            F.HypergeometricPFQRegularized(
                                F.list(F.C1, F.Plus(F.C1, F.Negate(n), F.Floor(F.Slot1)),
                                    F.Plus(F.C1, F.Negate(ns), F.Floor(F.Slot1))),
                                F.list(F.Plus(F.C2, F.Floor(F.Slot1)),
                                    F.Plus(F.C2, F.Negate(n), F.Negate(ns), nt, F.Floor(F.Slot1))),
                                F.C1))),
                        F.And(F.LessEqual(F.C0, F.Slot1),
                            F.LessEqual(F.Plus(n, ns, F.Negate(nt)), F.Slot1), F.Less(F.Slot1, n),
                            F.Less(F.Slot1, ns))),
                    F.list(F.C1, F.Or(F.GreaterEqual(F.Slot1, n), F.GreaterEqual(F.Slot1, ns)))),
                F.C0)); // $$;
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }

    @Override
    public IExpr inverseCDF(IAST dist, IExpr k, EvalEngine engine) {
      return F.NIL;
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
    public IExpr median(IAST dist) {
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
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST3()) {
        IExpr n = dist.arg1();
        IExpr ns = dist.arg2();
        IExpr nt = dist.arg3();
        IExpr function =
            // [$ (Piecewise({{(Binomial(ns, #)*Binomial(-ns + nt, -# + n))/Binomial(nt, n), 0 <= #
            // <= n &&
            // n + ns - nt <= # <= n && 0 <= # <= ns && n + ns - nt <= # <= ns}}, 0)) & $]
            F.Function(F.Piecewise(F.list(F.list(
                F.Times(F.Binomial(ns, F.Slot1), F.Power(F.Binomial(nt, n), F.CN1),
                    F.Binomial(F.Plus(F.Negate(ns), nt), F.Plus(F.Negate(F.Slot1), n))),
                F.And(F.LessEqual(F.C0, F.Slot1, n),
                    F.LessEqual(F.Plus(n, ns, F.Negate(nt)), F.Slot1, n),
                    F.LessEqual(F.C0, F.Slot1, ns),
                    F.LessEqual(F.Plus(n, ns, F.Negate(nt)), F.Slot1, ns)))),
                F.C0)); // $$;
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr randomVariate(Random random, IAST dist, int size) {
      if (dist.isAST3()) {
        int param[] = parameters(dist);
        if (param != null) {
          RandomDataGenerator rdg = new RandomDataGenerator();
          int[] vector =
              rdg.nextDeviates(new org.hipparchus.distribution.discrete.HypergeometricDistribution(
                  param[2], param[1], param[0]), size);
          return F.List(vector);
        }
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

    @Override
    public IExpr skewness(IAST dist) {
      if (dist.isAST3()) {
        int param[] = parameters(dist);
        if (param != null) {
          return F.NIL;
        }
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
  }


  /** Functionality for a discrete probability distribution */
  private interface IExpectationDiscreteDistribution extends IDiscreteDistribution {
    /**
     * @param n
     * @return P(X == n), i.e. probability of random variable X == n
     */
    IExpr p_equals(IAST dist, IExpr n);

    IExpr randomVariate(Random random, IAST dist, int size);
  }


  private static class Initializer {

    private static void init() {
      S.BernoulliDistribution
          .setEvaluator(new StatisticsDiscreteDistributions.BernoulliDistribution());
      S.BinomialDistribution
          .setEvaluator(new StatisticsDiscreteDistributions.BinomialDistribution());
      S.DiscreteUniformDistribution
          .setEvaluator(new StatisticsDiscreteDistributions.DiscreteUniformDistribution());
      S.GeometricDistribution
          .setEvaluator(new StatisticsDiscreteDistributions.GeometricDistribution());
      S.HypergeometricDistribution
          .setEvaluator(new StatisticsDiscreteDistributions.HypergeometricDistribution());
      S.PoissonDistribution.setEvaluator(new StatisticsDiscreteDistributions.PoissonDistribution());

    }
  }


  /**
   *
   *
   * <pre>
   * PoissonDistribution(m)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns a Poisson distribution.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Poisson_distribution">Wikipedia - Poisson
   * distribution</a>
   * </ul>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="CDF.md">CDF</a>, <a href="Mean.md">Mean</a>, <a href="Mean.md">Median</a>,
   * <a href="PDF.md">PDF</a>, <a href="Quantile.md">Quantile</a>,
   * <a href="StandardDeviation.md">StandardDeviation</a>, <a href="Variance.md">Variance</a>
   */
  private static final class PoissonDistribution extends AbstractEvaluator
      implements ICDF, IDiscreteDistribution, IPDF, IStatistics, IRandomVariate {

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST1()) {
        IExpr p = dist.arg1();
        if (p.isReal() && !p.isPositive()) {
          // Parameter `1` at position `2` in `3` is expected to be positive.
          return Errors.printMessage(S.PoissonDistribution, "posprm", F.List(p, F.C1, dist));
        }
        //
        IExpr function =
            // [$ Piecewise({{GammaRegularized(1 + Floor(#), p), # >= 0}}, 0) & $]
            F.Function(
                F.Piecewise(F.list(F.list(F.GammaRegularized(F.Plus(F.C1, F.Floor(F.Slot1)), p),
                    F.GreaterEqual(F.Slot1, F.C0))), F.C0)); // $$;
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public int getSupportUpperBound(IExpr discreteDistribution) {
      // probabilities are zero beyond that point
      return 1950;
    }

    @Override
    public IExpr inverseCDF(IAST dist, IExpr k, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public IExpr mean(IAST dist) {
      if (dist.isAST1()) {
        IExpr arg1 = dist.arg1();
        if (arg1.isReal() && !arg1.isPositive()) {
          // Parameter `1` at position `2` in `3` is expected to be positive.
          return Errors.printMessage(S.PoissonDistribution, "posprm", F.List(arg1, F.C1, dist));
        }
        return arg1;
      }
      return F.NIL;
    }

    @Override
    public IExpr median(IAST dist) {
      if (dist.isAST1()) {
        IExpr arg1 = dist.arg1();
        if (arg1.isReal() && !arg1.isPositive()) {
          // Parameter `1` at position `2` in `3` is expected to be positive.
          return Errors.printMessage(S.PoissonDistribution, "posprm", F.List(arg1, F.C1, dist));
        }
      }
      return F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST1()) {
        IExpr p = dist.arg1();
        if (p.isReal() && !p.isPositive()) {
          // Parameter `1` at position `2` in `3` is expected to be positive.
          return Errors.printMessage(S.PoissonDistribution, "posprm", F.List(p, F.C1, dist));
        }
        //
        IExpr function =
            // [$ Piecewise({{p^#/(E ^ p * #!), # >= 0}}, 0) & $]
            F.Function(F.Piecewise(F.list(F.list(
                F.Times(F.Power(p, F.Slot1),
                    F.Power(F.Times(F.Exp(p), F.Factorial(F.Slot1)), F.CN1)),
                F.GreaterEqual(F.Slot1, F.C0))), F.C0)); // $$;
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr randomVariate(Random random, IAST dist, int size) {
      if (dist.isAST1()) {
        // see exception handling in RandonmVariate() function
        IExpr arg1 = dist.arg1();
        if (arg1.isReal() && !arg1.isPositive()) {
          // Parameter `1` at position `2` in `3` is expected to be positive.
          return Errors.printMessage(S.PoissonDistribution, "posprm", F.List(arg1, F.C1, dist));
        }
        double mean = arg1.evalf();
        // return F.ZZ(new PoissonGenerator(mean, random).nextValue());
        RandomDataGenerator rdg = new RandomDataGenerator();
        int[] vector = rdg
            .nextDeviates(new org.hipparchus.distribution.discrete.PoissonDistribution(mean), size);
        return F.List(vector);
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

    @Override
    public IExpr skewness(IAST dist) {
      if (dist.isAST1()) {
        IExpr n = dist.arg1();
        if (n.isReal() && !n.isPositive()) {
          // Parameter `1` at position `2` in `3` is expected to be positive.
          return Errors.printMessage(S.PoissonDistribution, "posprm", F.List(n, F.C1, dist));
        }
        return n.sqrt().inverse();
      }
      return F.NIL;
    }

    @Override
    public IExpr variance(IAST dist) {
      if (dist.isAST1()) {
        IExpr arg1 = dist.arg1();
        if (arg1.isReal() && !arg1.isPositive()) {
          // Parameter `1` at position `2` in `3` is expected to be positive.
          return Errors.printMessage(S.PoissonDistribution, "posprm", F.List(arg1, F.C1, dist));
        }
        return arg1;
      }
      return F.NIL;
    }
  }


  public static void initialize() {
    Initializer.init();
  }
}
