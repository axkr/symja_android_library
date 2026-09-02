package org.matheclipse.core.builtin;

import java.util.Random;
import java.util.function.LongToDoubleFunction;
import org.hipparchus.random.RandomDataGenerator;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.interfaces.statistics.ICDF;
import org.matheclipse.core.interfaces.statistics.ICentralMoment;
import org.matheclipse.core.interfaces.statistics.ICovariance;
import org.matheclipse.core.interfaces.statistics.IDiscreteDistribution;
import org.matheclipse.core.interfaces.statistics.IGeneratingFunction;
import org.matheclipse.core.interfaces.statistics.IPDF;
import org.matheclipse.core.interfaces.statistics.IRandomVariate;
import org.matheclipse.core.interfaces.statistics.IStatistics;
import org.matheclipse.core.interfaces.IReal;

public class StatisticsDiscreteDistributions {

  /**
   * The quantile of a discrete distribution: the smallest integer <code>k</code> in
   * <code>[lo, hi]</code> whose cumulative distribution function reaches <code>q</code>.
   *
   * <p>
   * The caller supplies an initial guess from a continuous approximation; from there the bracket
   * is widened by doubling and then bisected, so a guess which is off by a few units costs a
   * handful of CDF evaluations and a badly wrong one still terminates in <code>O(log)</code>
   * steps. The search is exact at a tie, which is what makes
   * <code>Quantile(BinomialDistribution(10, 1/2), 1/2)</code> come out as <code>5</code>.
   * </p>
   *
   * @param q the probability, strictly between <code>0</code> and <code>1</code>
   * @param guess a starting point, not required to be inside <code>[lo, hi]</code>
   * @param lo the lowest value of the support
   * @param hi the highest value of the support
   * @param cdf the cumulative distribution function of the distribution
   * @return the smallest <code>k</code> with <code>cdf(k) >= q</code>
   */
  private static long discreteQuantile(double q, long guess, long lo, long hi,
      LongToDoubleFunction cdf) {
    // invariant: cdf(lower) < q <= cdf(upper), with lo-1 and hi as the virtual end points
    long lower = lo - 1;
    long upper = hi;
    long k = Math.max(lo, Math.min(hi, guess));
    long step = 1;
    if (cdf.applyAsDouble(k) >= q) {
      upper = k;
      while (upper > lo) {
        long next = Math.max(lo, upper - step);
        if (cdf.applyAsDouble(next) < q) {
          lower = next;
          break;
        }
        upper = next;
        step <<= 1;
      }
    } else {
      lower = k;
      while (lower < hi) {
        long next = Math.min(hi, lower + step);
        if (cdf.applyAsDouble(next) >= q) {
          upper = next;
          break;
        }
        lower = next;
        step <<= 1;
      }
    }
    while (upper - lower > 1) {
      long mid = lower + (upper - lower) / 2;
      if (cdf.applyAsDouble(mid) >= q) {
        upper = mid;
      } else {
        lower = mid;
      }
    }
    return upper;
  }

  /**
   * Cornish-Fisher expansion of the quantile of a distribution with the given first three moments,
   * used only as the starting point of {@link #discreteQuantile}.
   */
  /**
   * Upper end of the {@link #discreteQuantile} search for the unbounded Poisson support. The
   * bracket is found by doubling from the Cornish-Fisher guess, so this only bounds the runtime of
   * a pathological input.
   */
  private static final long POISSON_QUANTILE_LIMIT = 1L << 62;

  private static long cornishFisherGuess(double q, double mean, double sd, double skewness) {
    double z = org.hipparchus.util.FastMath.sqrt(2.0) * org.hipparchus.special.Erf.erfInv(2.0 * q - 1.0);
    double x = mean + sd * z + skewness * (z * z - 1.0) / 6.0;
    if (!Double.isFinite(x)) {
      return Math.round(mean);
    }
    return (long) org.hipparchus.util.FastMath.floor(x + 0.5);
  }

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
      implements ICDF, IDiscreteDistribution, IPDF, IStatistics, IRandomVariate, ICentralMoment,
      IGeneratingFunction {

    @Override
    public IExpr pgf(IAST dist, IExpr z, EvalEngine engine) {
      if (dist.isAST1()) {
        IExpr p = dist.arg1();
        // 1 - p + p*z
        return F.Plus(F.C1, F.Negate(p), F.Times(p, z));
      }
      return F.NIL;
    }

    @Override
    public int getSupportUpperBound(IExpr discreteDistribution) {
      // Bernoulli random variables only take the values 0 and 1
      return 1;
    }

    @Override
    public IExpr moment(IAST dist, IExpr n) {
      if (dist.isAST1() && n.isInteger() && n.isPositive()) {
        // E[X^n] == p for every n >= 1, because X only takes the values 0 and 1
        return dist.arg1();
      }
      return F.NIL;
    }

    @Override
    public IExpr parameterAssumptions(IAST dist) {
      if (dist.isAST1()) {
        return F.LessEqual(F.C0, dist.arg1(), F.C1);
      }
      return F.NIL;
    }

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
        double p = dist.arg1().evalfNaN();
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
      implements ICDF, IDiscreteDistribution, IPDF, IStatistics, IRandomVariate, ICentralMoment,
      IGeneratingFunction {

    @Override
    public IExpr pgf(IAST dist, IExpr z, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr p = dist.arg2();
        // (1 - p + p*z)^n
        return F.Power(F.Plus(F.C1, F.Negate(p), F.Times(p, z)), n);
      }
      return F.NIL;
    }

    @Override
    public int getSupportUpperBound(IExpr discreteDistribution) {
      if (discreteDistribution.isAST2()) {
        int n = discreteDistribution.first().toIntDefault();
        if (n >= 0) {
          return n;
        }
      }
      return Integer.MAX_VALUE;
    }

    @Override
    public IExpr parameterAssumptions(IAST dist) {
      if (dist.isAST2()) {
        return F.And(F.GreaterEqual(dist.arg1(), F.C0), F.LessEqual(F.C0, dist.arg2(), F.C1));
      }
      return F.NIL;
    }

    @Override
    public IExpr standardDeviation(IAST dist) {
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        // Sqrt(n*(1-m)*m)
        return F.Sqrt(F.Times(n, F.Subtract(F.C1, m), m));
      }
      return F.NIL;
    }

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
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        if (!n.isReal() || !m.isReal() || !k.isReal()) {
          return F.NIL;
        }
        double trials = ((IReal) n).doubleValue();
        double p = ((IReal) m).doubleValue();
        double q = ((IReal) k).doubleValue();
        if (!(trials >= 0.0) || trials != Math.rint(trials) || !(p >= 0.0) || !(p <= 1.0)
            || !(q >= 0.0) || !(q <= 1.0)) {
          return F.NIL;
        }
        long count = (long) trials;
        if (q == 0.0) {
          return F.C0;
        }
        if (q == 1.0) {
          return F.ZZ(count);
        }
        double sd = Math.sqrt(trials * p * (1.0 - p));
        double skewness = sd > 0.0 ? (1.0 - 2.0 * p) / sd : 0.0;
        long guess = cornishFisherGuess(q, trials * p, sd, skewness);
        return F.ZZ(discreteQuantile(q, guess, 0L, count, //
            j -> binomialCDF(j, trials, p)));
      }
      return F.NIL;
    }

    /** <code>BetaRegularized(1-p, n-j, 1+j)</code>, the CDF used by {@link #cdf}. */
    private static double binomialCDF(long j, double trials, double p) {
      if (j < 0) {
        return 0.0;
      }
      if (j >= trials) {
        return 1.0;
      }
      return org.hipparchus.special.Beta.regularizedBeta(1.0 - p, trials - j, 1.0 + j);
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
          double p = dist.arg2().evalfNaN();
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
  private static final class DiscreteUniformDistribution extends AbstractEvaluator implements
      IDiscreteDistribution, IStatistics, ICDF, IPDF, IRandomVariate, IGeneratingFunction {

    @Override
    public IExpr pgf(IAST dist, IExpr z, EvalEngine engine) {
      IExpr[] minMax = minmax(dist);
      if (minMax != null) {
        IExpr a = minMax[0];
        IExpr b = minMax[1];
        // (z^a - z^(b+1)) / ((1 + b - a)*(1 - z))
        return F.Divide(F.Subtract(F.Power(z, a), F.Power(z, F.Plus(b, F.C1))),
            F.Times(F.Plus(F.C1, b, F.Negate(a)), F.Subtract(F.C1, z)));
      }
      return F.NIL;
    }

    @Override
    public int getSupportLowerBound(IExpr discreteDistribution) {
      IExpr[] minMax = minmax((IAST) discreteDistribution);
      if (minMax != null) {
        int min = minMax[0].toIntDefault();
        if (F.isPresent(min)) {
          return min;
        }
      }
      return Integer.MIN_VALUE;
    }

    @Override
    public int getSupportUpperBound(IExpr discreteDistribution) {
      IExpr[] minMax = minmax((IAST) discreteDistribution);
      if (minMax != null) {
        int max = minMax[1].toIntDefault();
        if (F.isPresent(max)) {
          return max;
        }
      }
      return Integer.MAX_VALUE;
    }

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


  private static final class GeometricDistribution extends AbstractEvaluator implements ICDF,
      IDiscreteDistribution, IPDF, IStatistics, ICentralMoment, IGeneratingFunction { // ,

    @Override
    public IExpr pgf(IAST dist, IExpr z, EvalEngine engine) {
      if (dist.isAST1()) {
        IExpr p = dist.arg1();
        // p/(1 - (1-p)*z)
        return F.Divide(p, F.Subtract(F.C1, F.Times(F.Subtract(F.C1, p), z)));
      }
      return F.NIL;
    }
    // IRandomVariate

    @Override
    public IExpr parameterAssumptions(IAST dist) {
      if (dist.isAST1()) {
        return F.LessEqual(F.C0, dist.arg1(), F.C1);
      }
      return F.NIL;
    }

    @Override
    public IExpr standardDeviation(IAST dist) {
      if (dist.isAST1()) {
        IExpr n = dist.arg1();
        // Sqrt(1-n)/n
        return F.Divide(F.Sqrt(F.Subtract(F.C1, n)), n);
      }
      return F.NIL;
    }

    @Override
    public IExpr survivalFunction(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST1()) {
        IExpr n = dist.arg1();
        // Piecewise({{(1 - n)^(1 + Floor(#)), # >= 0}}, 1) &
        return callFunction(F.Function(
            F.Piecewise(F.list(F.list(F.Power(F.Subtract(F.C1, n), F.Plus(F.C1, F.Floor(F.Slot1))),
                F.GreaterEqual(F.Slot1, F.C0))), F.C1)),
            k);
      }
      return F.NIL;
    }

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
  private static final class HypergeometricDistribution extends AbstractEvaluator implements ICDF,
      IDiscreteDistribution, IPDF, IStatistics, IRandomVariate, IGeneratingFunction {

    @Override
    public IExpr pgf(IAST dist, IExpr z, EvalEngine engine) {
      if (dist.isAST3()) {
        IExpr n = dist.arg1();
        IExpr ns = dist.arg2();
        IExpr nt = dist.arg3();
        int nInt = n.toIntDefault();
        int nsInt = ns.toIntDefault();
        int ntInt = nt.toIntDefault();
        if (nInt != Integer.MIN_VALUE && nsInt != Integer.MIN_VALUE && ntInt != Integer.MIN_VALUE
            && nInt + nsInt > ntInt) {
          // the support does not start at 0, the series form below is not valid
          return F.NIL;
        }
        // (Binomial(nt - ns, n)/Binomial(nt, n)) * Hypergeometric2F1(-n, -ns, nt - ns - n + 1, z)
        return F.Times(F.Divide(F.Binomial(F.Subtract(nt, ns), n), F.Binomial(nt, n)),
            F.Hypergeometric2F1(F.Negate(n), F.Negate(ns),
                F.Plus(F.C1, nt, F.Negate(ns), F.Negate(n)), z));
      }
      return F.NIL;
    }

    @Override
    public int getSupportLowerBound(IExpr discreteDistribution) {
      int[] param = parameters((IAST) discreteDistribution);
      if (param != null) {
        return Math.max(0, param[0] + param[1] - param[2]);
      }
      return 0;
    }

    @Override
    public int getSupportUpperBound(IExpr discreteDistribution) {
      int[] param = parameters((IAST) discreteDistribution);
      if (param != null) {
        return Math.min(param[0], param[1]);
      }
      return Integer.MAX_VALUE;
    }

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
      S.MultivariatePoissonDistribution
          .setEvaluator(new StatisticsDiscreteDistributions.MultivariatePoissonDistribution());
      S.BenfordDistribution.setEvaluator(new StatisticsDiscreteDistributions.BenfordDistribution());
      S.BetaBinomialDistribution
          .setEvaluator(new StatisticsDiscreteDistributions.BetaBinomialDistribution());
      S.BorelTannerDistribution
          .setEvaluator(new StatisticsDiscreteDistributions.BorelTannerDistribution());
      S.LogSeriesDistribution
          .setEvaluator(new StatisticsDiscreteDistributions.LogSeriesDistribution());
      S.PoissonConsulDistribution
          .setEvaluator(new StatisticsDiscreteDistributions.PoissonConsulDistribution());
      S.WaringYuleDistribution
          .setEvaluator(new StatisticsDiscreteDistributions.WaringYuleDistribution());
      S.ZipfDistribution.setEvaluator(new StatisticsDiscreteDistributions.ZipfDistribution());
    }
  }

  private static final class MultivariatePoissonDistribution extends AbstractEvaluator
      implements IDiscreteDistribution, ICDF, IPDF, IStatistics, IRandomVariate, ICovariance {

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr m0 = dist.arg1();
        IExpr lambda = dist.arg2();
        if (lambda.isList1() && k.isList1()) {
          IExpr m1 = lambda.first();
          IExpr x = k.first();
          // Piecewise({{GammaRegularized(1+Floor(x),m0+m1),Floor(x)>=0}},0)
          IExpr v1 = F.Floor(x);
          return F.Piecewise(F.list(F.list(F.GammaRegularized(F.Plus(F.C1, v1), F.Plus(m0, m1)),
              F.GreaterEqual(v1, F.C0))), F.C0);
        }
      }
      return F.NIL;
    }

    @Override
    public IExpr covariance(IAST dist, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr theta = dist.arg1();
        IExpr lambda = dist.arg2();
        if (lambda.isList()) {
          final int n = lambda.argSize();
          // Cov(X_i, X_j) = theta if i != j, else theta + lambda_i
          IASTAppendable matrix = F.ListAlloc(n);
          for (int i = 1; i <= n; i++) {
            IASTAppendable row = F.ListAlloc(n);
            for (int j = 1; j <= n; j++) {
              if (i == j) {
                row.append(F.Plus(theta, lambda.get(i)));
              } else {
                row.append(theta);
              }
            }
            matrix.append(row);
          }
          return matrix;
        }
      }
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
    public IExpr inverseCDF(IAST dist, IExpr x, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public IExpr mean(IAST dist) {
      if (dist.isAST2()) {
        IExpr theta = dist.arg1();
        IExpr lambda = dist.arg2();
        // Mean vector: {theta + lambda1, theta + lambda2, ...}
        if (lambda.isList()) {
          return ((IAST) lambda).map(val -> F.Plus(theta, val));
        }
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
        IExpr m0 = dist.arg1();
        IExpr lambda = dist.arg2();
        if (lambda.isList1() && k.isList1()) {
          IExpr m1 = lambda.first();
          IExpr x = k.first();
          // Piecewise({{(m0+m1)^x/(E^(m0+m1)*x!),x>=0}},0)
          return F.Piecewise(
              F.list(F.list(F.Times(F.Exp(F.Subtract(F.Negate(m0), m1)), F.Power(F.Plus(m0, m1), x),
                  F.Power(F.Factorial(x), F.CN1)), F.GreaterEqual(x, F.C0))),
              F.C0);
        }
        if (lambda.isList2() && k.isList2()) {
          IExpr m1 = lambda.first();
          IExpr m2 = lambda.second();
          IExpr x = k.first();
          IExpr y = k.second();
          // Piecewise({{((-m0)^x*HypergeometricU(-x,1-x+y,(-m1*m2)/m0))/(E^(m0+m1+m2)*m2^(x-y)*x!*y!),x>=
          // 0&&y>=0}},0)
          IExpr v2 = F.Negate(m0);
          IExpr v1 = F.Negate(x);
          return F.Piecewise(F.list(F.list(
              F.Times(F.Exp(F.Plus(v2, F.Negate(m1), F.Negate(m2))), F.Power(v2, x),
                  F.Power(m2, F.Plus(v1, y)),
                  F.Power(F.Times(F.Factorial(x), F.Factorial(y)), F.CN1),
                  F.HypergeometricU(v1, F.Plus(F.C1, v1, y),
                      F.Times(F.CN1, F.Power(m0, F.CN1), m1, m2))),
              F.And(F.GreaterEqual(x, F.C0), F.GreaterEqual(y, F.C0)))), F.C0);
        }
      }
      return F.NIL;
    }

    @Override
    public IExpr randomVariate(Random random, IAST dist, int size) {
      if (dist.isAST2()) {
        IExpr thetaExpr = dist.arg1();
        IExpr lambdaExpr = dist.arg2();
        if (lambdaExpr.isList()) {
          try {
            double theta = thetaExpr.evalfNaN();
            double[] lambdas = lambdaExpr.toDoubleVector();
            if (lambdas != null && theta > 0) {
              RandomDataGenerator rdg = new RandomDataGenerator();

              // Generate the shared component Y_0 ~ Poisson(theta)
              org.hipparchus.distribution.discrete.PoissonDistribution p0 =
                  new org.hipparchus.distribution.discrete.PoissonDistribution(theta);
              int[] y0_vec = rdg.nextDeviates(p0, size);

              // Generate independent components Y_i ~ Poisson(lambda_i)
              int[][] yi_vecs = new int[lambdas.length][];
              for (int j = 0; j < lambdas.length; j++) {
                org.hipparchus.distribution.discrete.PoissonDistribution pj =
                    new org.hipparchus.distribution.discrete.PoissonDistribution(lambdas[j]);
                yi_vecs[j] = rdg.nextDeviates(pj, size);
              }

              // Combine: X_i = Y_0 + Y_i
              IASTAppendable resultList = F.ListAlloc(size);
              for (int s = 0; s < size; s++) {
                IASTAppendable sampleVec = F.ListAlloc(lambdas.length);
                for (int j = 0; j < lambdas.length; j++) {
                  sampleVec.append(y0_vec[s] + yi_vecs[j][s]);
                }
                resultList.append(sampleVec);
              }
              return resultList;
            }
          } catch (Exception e) {
            // Fallback for symbolic or invalid numeric inputs
          }
        }
      }
      return F.NIL;
    }

    @Override
    public IExpr skewness(IAST dist) {
      if (dist.isAST2()) {
        IExpr theta = dist.arg1();
        IExpr lambda = dist.arg2();
        // Mean vector: {theta + lambda1, theta + lambda2, ...}
        if (lambda.isList()) {
          return ((IAST) lambda).map(val -> F.Power(F.Plus(theta, val), F.CN1D2));
        }
      }
      return F.NIL;
    }

    @Override
    public IExpr variance(IAST dist) {
      // For multivariate Poisson X_i = Y_0 + Y_i, Var(X_i) = Var(Y_0) + Var(Y_i) = theta + lambda_i
      // This is identical to the Mean vector.
      return mean(dist);
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
  private static final class PoissonDistribution extends AbstractEvaluator implements ICDF,
      IDiscreteDistribution, IPDF, IStatistics, IRandomVariate, IGeneratingFunction {

    @Override
    public IExpr pgf(IAST dist, IExpr z, EvalEngine engine) {
      if (dist.isAST1()) {
        IExpr lambda = dist.arg1();
        // E^(lambda*(z-1))
        return F.Exp(F.Times(lambda, F.Subtract(z, F.C1)));
      }
      return F.NIL;
    }

    @Override
    public IExpr moment(IAST dist, IExpr n) {
      int order = n.toIntDefault();
      if (dist.isAST1() && order >= 0) {
        // E[X^n] = Sum(StirlingS2(n, k)*lambda^k, {k, 0, n})
        IExpr lambda = dist.arg1();
        IASTAppendable sum = F.PlusAlloc(order + 1);
        for (int k = 0; k <= order; k++) {
          sum.append(F.Times(F.StirlingS2(F.ZZ(order), F.ZZ(k)), F.Power(lambda, F.ZZ(k))));
        }
        return sum;
      }
      return F.NIL;
    }

    @Override
    public IExpr parameterAssumptions(IAST dist) {
      if (dist.isAST1()) {
        return F.Greater(dist.arg1(), F.C0);
      }
      return F.NIL;
    }

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
    public IExpr inverseCDF(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST1()) {
        IExpr p = dist.arg1();
        if (p.isReal() && !p.isPositive()) {
          // Parameter `1` at position `2` in `3` is expected to be positive.
          return Errors.printMessage(S.PoissonDistribution, "posprm", F.List(p, F.C1, dist));
        }
        if (!p.isReal() || !k.isReal()) {
          return F.NIL;
        }
        double lambda = ((IReal) p).doubleValue();
        double q = ((IReal) k).doubleValue();
        if (!(lambda > 0.0) || !(q >= 0.0) || !(q <= 1.0)) {
          return F.NIL;
        }
        if (q == 0.0) {
          return F.C0;
        }
        if (q == 1.0) {
          return F.CInfinity;
        }
        double sd = Math.sqrt(lambda);
        long guess = cornishFisherGuess(q, lambda, sd, 1.0 / sd);
        return F.ZZ(discreteQuantile(q, guess, 0L, POISSON_QUANTILE_LIMIT, //
            j -> poissonCDF(j, lambda)));
      }
      return F.NIL;
    }

    /** <code>GammaRegularized(1+j, lambda)</code>, the CDF used by {@link #cdf}. */
    private static double poissonCDF(long j, double lambda) {
      if (j < 0) {
        return 0.0;
      }
      return org.hipparchus.special.Gamma.regularizedGammaQ(1.0 + j, lambda);
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
        double mean = arg1.evalfNaN();
        if (Double.isNaN(mean)) {
          return F.NIL;
        }
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

  /**
   * <code>BenfordDistribution(b)</code> - Benford's law for the leading digit in base
   * <code>b</code>.
   */
  private static final class BenfordDistribution extends AbstractEvaluator
      implements ICDF, IDiscreteDistribution, IPDF, IStatistics {

    @Override
    public int getSupportLowerBound(IExpr discreteDistribution) {
      return 1;
    }

    @Override
    public int getSupportUpperBound(IExpr discreteDistribution) {
      if (discreteDistribution.isAST1()) {
        int b = discreteDistribution.first().toIntDefault();
        if (b > 1) {
          return b - 1;
        }
      }
      return 9;
    }

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST1()) {
        IExpr b = dist.arg1();
        if (k.isPresent() && !k.isList()) {
          IExpr floor = engine.evaluate(F.Floor(k));
          if (floor.isReal()) {
            if (floor.isNegativeResult() || floor.isZero()) {
              return F.C0;
            }
            if (b.isReal() && floor.greaterEqual(F.Subtract(b, F.C1)).isTrue()) {
              return F.C1;
            }
            // Log(1 + Floor(k))/Log(b)
            return F.Divide(F.Log(F.Plus(F.C1, floor)), F.Log(b));
          }
        }
        // Piecewise({{Log(1 + Floor(#))/Log(b), 1 <= # < b - 1}, {1, # >= b - 1}}, 0) &
        return callFunction(F.Function(F.Piecewise(F.list(//
            F.list(F.Divide(F.Log(F.Plus(F.C1, F.Floor(F.Slot1))), F.Log(b)),
                F.And(F.LessEqual(F.C1, F.Slot1), F.Less(F.Slot1, F.Subtract(b, F.C1)))), //
            F.list(F.C1, F.GreaterEqual(F.Slot1, F.Subtract(b, F.C1)))), F.C0)), k);
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
    public IExpr mean(IAST dist) {
      if (dist.isAST1()) {
        IExpr b = dist.arg1();
        // b - Log(b!)/Log(b)
        return F.Subtract(b, F.Divide(F.Log(F.Factorial(b)), F.Log(b)));
      }
      return F.NIL;
    }

    @Override
    public IExpr median(IAST dist) {
      return F.NIL;
    }

    @Override
    public IExpr parameterAssumptions(IAST dist) {
      if (dist.isAST1()) {
        return F.GreaterEqual(dist.arg1(), F.C2);
      }
      return F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST1()) {
        IExpr b = dist.arg1();
        if (isZeroProbability(k)) {
          return F.C0;
        }
        // Piecewise({{Log((1 + #)/#)/Log(b), 1 <= # <= b - 1}}, 0) &
        return callFunction(F.Function(F.Piecewise(
            F.list(F.list(F.Divide(F.Log(F.Divide(F.Plus(F.C1, F.Slot1), F.Slot1)), F.Log(b)),
                F.LessEqual(F.C1, F.Slot1, F.Subtract(b, F.C1)))),
            F.C0)), k);
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

    @Override
    public IExpr skewness(IAST dist) {
      return F.NIL;
    }

    @Override
    public IExpr variance(IAST dist) {
      return F.NIL;
    }
  }

  /**
   * <code>BetaBinomialDistribution(a, b, n)</code> - a binomial distribution whose success
   * probability is beta distributed.
   */
  private static final class BetaBinomialDistribution extends AbstractEvaluator
      implements ICDF, IDiscreteDistribution, IPDF, IStatistics, IGeneratingFunction {

    @Override
    public IExpr pgf(IAST dist, IExpr z, EvalEngine engine) {
      if (dist.isAST3()) {
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        IExpr n = dist.arg3();
        // Hypergeometric2F1(-n, a, a + b, 1 - z)
        return F.Hypergeometric2F1(F.Negate(n), a, F.Plus(a, b), F.Subtract(F.C1, z));
      }
      return F.NIL;
    }

    @Override
    public int getSupportUpperBound(IExpr discreteDistribution) {
      if (discreteDistribution.isAST3()) {
        int n = ((IAST) discreteDistribution).arg3().toIntDefault();
        if (n >= 0) {
          return n;
        }
      }
      return Integer.MAX_VALUE;
    }

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST3()) {
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        IExpr n = dist.arg3();
        if (k.isPresent() && !k.isList()) {
          IExpr floor = engine.evaluate(F.Floor(k));
          if (floor.isReal()) {
            if (floor.isNegative()) {
              return F.C0;
            }
            if (n.isInteger() && floor.greaterEqual(n).isTrue()) {
              return F.C1;
            }
            if (floor.isInteger() && n.isInteger()) {
              int last = ((IInteger) floor).toIntDefault();
              if (last >= 0 && last < Config.MAX_AST_SIZE) {
                IASTAppendable sum = F.PlusAlloc(last + 1);
                for (int i = 0; i <= last; i++) {
                  sum.append(probability(a, b, n, F.ZZ(i)));
                }
                return sum;
              }
            }
          }
        }
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
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        IExpr n = dist.arg3();
        // (a*n)/(a + b)
        return F.Divide(F.Times(a, n), F.Plus(a, b));
      }
      return F.NIL;
    }

    @Override
    public IExpr median(IAST dist) {
      return F.NIL;
    }

    @Override
    public IExpr parameterAssumptions(IAST dist) {
      if (dist.isAST3()) {
        return F.And(F.Greater(dist.arg1(), F.C0), F.Greater(dist.arg2(), F.C0),
            F.GreaterEqual(dist.arg3(), F.C0));
      }
      return F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST3()) {
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        IExpr n = dist.arg3();
        if (isZeroProbability(k)) {
          return F.C0;
        }
        // Piecewise({{(Binomial(n,#)*Pochhammer(a,#)*Pochhammer(b,n-#))/Pochhammer(a+b,n),
        // 0 <= # <= n}}, 0) &
        return callFunction(F.Function(F.Piecewise(
            F.list(F.list(probability(a, b, n, F.Slot1), F.LessEqual(F.C0, F.Slot1, n))), F.C0)),
            k);
      }
      return F.NIL;
    }

    /** <code>(Binomial(n,k)*Pochhammer(a,k)*Pochhammer(b,n-k))/Pochhammer(a+b,n)</code> */
    private static IExpr probability(IExpr a, IExpr b, IExpr n, IExpr k) {
      return F.Divide(
          F.Times(F.Binomial(n, k), F.Pochhammer(a, k), F.Pochhammer(b, F.Subtract(n, k))),
          F.Pochhammer(F.Plus(a, b), n));
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

    @Override
    public IExpr skewness(IAST dist) {
      return F.NIL;
    }

    @Override
    public IExpr variance(IAST dist) {
      if (dist.isAST3()) {
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        IExpr n = dist.arg3();
        // (a*b*n*(a + b + n))/((a + b)^2*(1 + a + b))
        return F.Divide(F.Times(a, b, n, F.Plus(a, b, n)),
            F.Times(F.Sqr(F.Plus(a, b)), F.Plus(F.C1, a, b)));
      }
      return F.NIL;
    }
  }

  /**
   * <code>BorelTannerDistribution(alpha, n)</code> - the Borel-Tanner distribution.
   */
  private static final class BorelTannerDistribution extends AbstractEvaluator
      implements IDiscreteDistribution, IPDF, IStatistics {

    @Override
    public int getSupportLowerBound(IExpr discreteDistribution) {
      if (discreteDistribution.isAST2()) {
        int n = ((IAST) discreteDistribution).arg2().toIntDefault();
        if (n >= 0) {
          return n;
        }
      }
      return 0;
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
    public IExpr mean(IAST dist) {
      if (dist.isAST2()) {
        // n/(1 - a)
        return F.Divide(dist.arg2(), F.Subtract(F.C1, dist.arg1()));
      }
      return F.NIL;
    }

    @Override
    public IExpr median(IAST dist) {
      return F.NIL;
    }

    @Override
    public IExpr parameterAssumptions(IAST dist) {
      if (dist.isAST2()) {
        return F.And(F.Less(F.C0, dist.arg1(), F.C1), F.Greater(dist.arg2(), F.C0));
      }
      return F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr a = dist.arg1();
        IExpr n = dist.arg2();
        if (isZeroProbability(k)) {
          return F.C0;
        }
        // Piecewise({{(n*a^(# - n)*#^(# - n - 1))/(E^(a*#)*(# - n)!), # >= n}}, 0) &
        return callFunction(F.Function(F.Piecewise(F.list(F.list(//
            F.Divide(
                F.Times(n, F.Power(a, F.Subtract(F.Slot1, n)),
                    F.Power(F.Slot1, F.Subtract(F.Subtract(F.Slot1, n), F.C1))),
                F.Times(F.Exp(F.Times(a, F.Slot1)), F.Factorial(F.Subtract(F.Slot1, n)))),
            F.GreaterEqual(F.Slot1, n))), F.C0)), k);
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

    @Override
    public IExpr skewness(IAST dist) {
      return F.NIL;
    }

    @Override
    public IExpr variance(IAST dist) {
      if (dist.isAST2()) {
        IExpr a = dist.arg1();
        IExpr n = dist.arg2();
        // (a*n)/(1 - a)^3
        return F.Divide(F.Times(a, n), F.Power(F.Subtract(F.C1, a), F.C3));
      }
      return F.NIL;
    }
  }

  /**
   * <code>LogSeriesDistribution(t)</code> - the logarithmic series distribution.
   */
  private static final class LogSeriesDistribution extends AbstractEvaluator
      implements ICDF, IDiscreteDistribution, IPDF, IStatistics, IGeneratingFunction {

    @Override
    public IExpr pgf(IAST dist, IExpr z, EvalEngine engine) {
      if (dist.isAST1()) {
        IExpr t = dist.arg1();
        // Log(1 - t*z)/Log(1 - t)
        return F.Divide(F.Log(F.Subtract(F.C1, F.Times(t, z))), F.Log(F.Subtract(F.C1, t)));
      }
      return F.NIL;
    }

    @Override
    public int getSupportLowerBound(IExpr discreteDistribution) {
      return 1;
    }

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST1()) {
        IExpr t = dist.arg1();
        // Piecewise({{1 + Beta(t, 1 + Floor(#), 0)/Log(1 - t), # >= 1}}, 0) &
        return callFunction(F.Function(F.Piecewise(F.list(F.list(//
            F.Plus(F.C1, F.Divide(F.Beta(t, F.Plus(F.C1, F.Floor(F.Slot1)), F.C0),
                F.Log(F.Subtract(F.C1, t)))),
            F.GreaterEqual(F.Slot1, F.C1))), F.C0)), k);
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
    public IExpr mean(IAST dist) {
      if (dist.isAST1()) {
        IExpr t = dist.arg1();
        // -(t/((1 - t)*Log(1 - t)))
        return F.Negate(F.Divide(t, F.Times(F.Subtract(F.C1, t), F.Log(F.Subtract(F.C1, t)))));
      }
      return F.NIL;
    }

    @Override
    public IExpr median(IAST dist) {
      return F.NIL;
    }

    @Override
    public IExpr parameterAssumptions(IAST dist) {
      if (dist.isAST1()) {
        return F.Less(F.C0, dist.arg1(), F.C1);
      }
      return F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST1()) {
        IExpr t = dist.arg1();
        if (isZeroProbability(k)) {
          return F.C0;
        }
        // Piecewise({{-(t^#/(#*Log(1 - t))), # >= 1}}, 0) &
        return callFunction(F.Function(F.Piecewise(F.list(F.list(//
            F.Negate(F.Divide(F.Power(t, F.Slot1), F.Times(F.Slot1, F.Log(F.Subtract(F.C1, t))))),
            F.GreaterEqual(F.Slot1, F.C1))), F.C0)), k);
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

    @Override
    public IExpr skewness(IAST dist) {
      return F.NIL;
    }

    @Override
    public IExpr variance(IAST dist) {
      if (dist.isAST1()) {
        IExpr t = dist.arg1();
        // -((t*(t + Log(1 - t)))/((-1 + t)^2*Log(1 - t)^2))
        return F.Negate(F.Divide(F.Times(t, F.Plus(t, F.Log(F.Subtract(F.C1, t)))),
            F.Times(F.Sqr(F.Plus(F.CN1, t)), F.Sqr(F.Log(F.Subtract(F.C1, t))))));
      }
      return F.NIL;
    }
  }

  /**
   * <code>PoissonConsulDistribution(mu, lambda)</code> - the generalized (Consul) Poisson
   * distribution.
   */
  private static final class PoissonConsulDistribution extends AbstractEvaluator
      implements IDiscreteDistribution, IPDF, IStatistics {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public IExpr mean(IAST dist) {
      if (dist.isAST2()) {
        // m/(1 - lambda)
        return F.Divide(dist.arg1(), F.Subtract(F.C1, dist.arg2()));
      }
      return F.NIL;
    }

    @Override
    public IExpr median(IAST dist) {
      return F.NIL;
    }

    @Override
    public IExpr parameterAssumptions(IAST dist) {
      if (dist.isAST2()) {
        return F.And(F.Greater(dist.arg1(), F.C0), F.LessEqual(F.C0, dist.arg2()),
            F.Less(dist.arg2(), F.C1));
      }
      return F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr m = dist.arg1();
        IExpr lambda = dist.arg2();
        if (isZeroProbability(k)) {
          return F.C0;
        }
        // Piecewise({{(m*(m + lambda*#)^(# - 1))/(E^(m + lambda*#)*#!), # >= 0}}, 0) &
        IExpr argument = F.Plus(m, F.Times(lambda, F.Slot1));
        return callFunction(F.Function(F.Piecewise(F.list(F.list(//
            F.Divide(F.Times(m, F.Power(argument, F.Subtract(F.Slot1, F.C1))),
                F.Times(F.Exp(argument), F.Factorial(F.Slot1))),
            F.GreaterEqual(F.Slot1, F.C0))), F.C0)), k);
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

    @Override
    public IExpr skewness(IAST dist) {
      return F.NIL;
    }

    @Override
    public IExpr variance(IAST dist) {
      if (dist.isAST2()) {
        // m/(1 - lambda)^3
        return F.Divide(dist.arg1(), F.Power(F.Subtract(F.C1, dist.arg2()), F.C3));
      }
      return F.NIL;
    }
  }

  /**
   * <code>WaringYuleDistribution(a)</code> or <code>WaringYuleDistribution(a, b)</code> - the
   * Waring-Yule distribution.
   */
  private static final class WaringYuleDistribution extends AbstractEvaluator
      implements ICDF, IDiscreteDistribution, IPDF, IStatistics {

    /** The two shape parameters; the 1-argument form uses <code>b == 1</code>. */
    private static IExpr[] shapes(IAST dist) {
      if (dist.isAST1()) {
        return new IExpr[] {dist.arg1(), F.C1};
      }
      if (dist.isAST2()) {
        return new IExpr[] {dist.arg1(), dist.arg2()};
      }
      return null;
    }

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      IExpr[] shapes = shapes(dist);
      if (shapes != null) {
        IExpr a = shapes[0];
        IExpr b = shapes[1];
        // Piecewise({{1 - Pochhammer(b, 1 + Floor(#))/Pochhammer(a + b, 1 + Floor(#)), # >= 0}},
        // 0) &
        IExpr n = F.Plus(F.C1, F.Floor(F.Slot1));
        return callFunction(F.Function(F.Piecewise(F.list(F.list(//
            F.Subtract(F.C1, F.Divide(F.Pochhammer(b, n), F.Pochhammer(F.Plus(a, b), n))),
            F.GreaterEqual(F.Slot1, F.C0))), F.C0)), k);
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public IExpr inverseCDF(IAST dist, IExpr k, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public IExpr mean(IAST dist) {
      IExpr[] shapes = shapes(dist);
      if (shapes != null) {
        IExpr a = shapes[0];
        IExpr b = shapes[1];
        // Piecewise({{b/(a - 1), a > 1}}, Infinity)
        return F.Piecewise(F.list(F.list(F.Divide(b, F.Subtract(a, F.C1)), F.Greater(a, F.C1))),
            F.oo);
      }
      return F.NIL;
    }

    @Override
    public IExpr median(IAST dist) {
      return F.NIL;
    }

    @Override
    public IExpr parameterAssumptions(IAST dist) {
      IExpr[] shapes = shapes(dist);
      if (shapes != null) {
        return F.And(F.Greater(shapes[0], F.C0), F.Greater(shapes[1], F.C0));
      }
      return F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      IExpr[] shapes = shapes(dist);
      if (shapes != null) {
        IExpr a = shapes[0];
        IExpr b = shapes[1];
        if (isZeroProbability(k)) {
          return F.C0;
        }
        // Piecewise({{(a*Pochhammer(b, #))/Pochhammer(a + b, 1 + #), # >= 0}}, 0) &
        return callFunction(F.Function(F.Piecewise(F.list(F.list(//
            F.Divide(F.Times(a, F.Pochhammer(b, F.Slot1)),
                F.Pochhammer(F.Plus(a, b), F.Plus(F.C1, F.Slot1))),
            F.GreaterEqual(F.Slot1, F.C0))), F.C0)), k);
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

    @Override
    public IExpr skewness(IAST dist) {
      return F.NIL;
    }

    @Override
    public IExpr variance(IAST dist) {
      IExpr[] shapes = shapes(dist);
      if (shapes != null) {
        IExpr a = shapes[0];
        IExpr b = shapes[1];
        // Piecewise({{(a*b*(a + b - 1))/((a - 2)*(a - 1)^2), a > 2}}, Infinity)
        return F
            .Piecewise(
                F.list(F.list(
                    F.Divide(F.Times(a, b, F.Plus(a, b, F.CN1)),
                        F.Times(F.Subtract(a, F.C2), F.Sqr(F.Subtract(a, F.C1)))),
                    F.Greater(a, F.C2))),
                F.oo);
      }
      return F.NIL;
    }
  }

  /**
   * <code>ZipfDistribution(rho)</code> (zeta distribution) or <code>ZipfDistribution(n, rho)</code>
   * (finite Zipf distribution).
   */
  private static final class ZipfDistribution extends AbstractEvaluator
      implements IDiscreteDistribution, IPDF, IStatistics, IGeneratingFunction {

    @Override
    public IExpr pgf(IAST dist, IExpr z, EvalEngine engine) {
      if (dist.isAST1()) {
        IExpr r = dist.arg1();
        // PolyLog(1 + r, z)/Zeta(1 + r)
        return F.Divide(F.PolyLog(F.Plus(F.C1, r), z), F.Zeta(F.Plus(F.C1, r)));
      }
      // the finite 2 argument form is covered by the generic enumeration of the support
      return F.NIL;
    }

    @Override
    public int getSupportLowerBound(IExpr discreteDistribution) {
      return 1;
    }

    @Override
    public int getSupportUpperBound(IExpr discreteDistribution) {
      if (discreteDistribution.isAST2()) {
        int n = ((IAST) discreteDistribution).arg1().toIntDefault();
        if (n >= 1) {
          return n;
        }
      }
      return Integer.MAX_VALUE;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public IExpr mean(IAST dist) {
      if (dist.isAST1()) {
        IExpr r = dist.arg1();
        // Piecewise({{Zeta(r)/Zeta(1 + r), r > 1}}, Infinity)
        return F.Piecewise(
            F.list(F.list(F.Divide(F.Zeta(r), F.Zeta(F.Plus(F.C1, r))), F.Greater(r, F.C1))), F.oo);
      }
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr r = dist.arg2();
        // HarmonicNumber(n, r)/HarmonicNumber(n, 1 + r)
        return F.Divide(F.HarmonicNumber(n, r), F.HarmonicNumber(n, F.Plus(F.C1, r)));
      }
      return F.NIL;
    }

    @Override
    public IExpr median(IAST dist) {
      return F.NIL;
    }

    @Override
    public IExpr parameterAssumptions(IAST dist) {
      if (dist.isAST1()) {
        return F.Greater(dist.arg1(), F.C0);
      }
      if (dist.isAST2()) {
        return F.And(F.GreaterEqual(dist.arg1(), F.C1), F.Greater(dist.arg2(), F.C0));
      }
      return F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST1()) {
        IExpr r = dist.arg1();
        if (isZeroProbability(k)) {
          return F.C0;
        }
        // Piecewise({{#^(-1 - r)/Zeta(1 + r), # >= 1}}, 0) &
        return callFunction(F.Function(F.Piecewise(F.list(F.list(//
            F.Divide(F.Power(F.Slot1, F.Subtract(F.CN1, r)), F.Zeta(F.Plus(F.C1, r))),
            F.GreaterEqual(F.Slot1, F.C1))), F.C0)), k);
      }
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr r = dist.arg2();
        if (isZeroProbability(k)) {
          return F.C0;
        }
        // Piecewise({{#^(-1 - r)/HarmonicNumber(n, 1 + r), 1 <= # <= n}}, 0) &
        return callFunction(F.Function(F.Piecewise(F.list(F.list(//
            F.Divide(F.Power(F.Slot1, F.Subtract(F.CN1, r)), F.HarmonicNumber(n, F.Plus(F.C1, r))),
            F.LessEqual(F.C1, F.Slot1, n))), F.C0)), k);
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

    @Override
    public IExpr skewness(IAST dist) {
      return F.NIL;
    }

    @Override
    public IExpr variance(IAST dist) {
      if (dist.isAST1()) {
        IExpr r = dist.arg1();
        // Piecewise({{-(Zeta(r)^2/Zeta(1 + r)^2) + Zeta(-1 + r)/Zeta(1 + r), r > 2}}, Infinity)
        return F.Piecewise(F.list(F.list(//
            F.Plus(F.Negate(F.Divide(F.Sqr(F.Zeta(r)), F.Sqr(F.Zeta(F.Plus(F.C1, r))))),
                F.Divide(F.Zeta(F.Plus(F.CN1, r)), F.Zeta(F.Plus(F.C1, r)))),
            F.Greater(r, F.C2))), F.oo);
      }
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr r = dist.arg2();
        // HarmonicNumber(n,-1+r)/HarmonicNumber(n,1+r)
        // - (HarmonicNumber(n,r)/HarmonicNumber(n,1+r))^2
        IExpr denominator = F.HarmonicNumber(n, F.Plus(F.C1, r));
        return F.Subtract(F.Divide(F.HarmonicNumber(n, F.Plus(F.CN1, r)), denominator),
            F.Sqr(F.Divide(F.HarmonicNumber(n, r), denominator)));
      }
      return F.NIL;
    }
  }

  /**
   * Test whether <code>k</code> is an explicit non-integer number, i.e. a point where the
   * probability of a discrete distribution is <code>0</code> no matter what the parameters are.
   */
  private static boolean isZeroProbability(IExpr k) {
    return k.isPresent() && k.isNumber() && !k.isInteger();
  }

}
