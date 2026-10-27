package org.matheclipse.core.builtin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.interfaces.statistics.ICDF;
import org.matheclipse.core.interfaces.statistics.IContinuousDistribution;
import org.matheclipse.core.interfaces.statistics.IDiscreteDistribution;
import org.matheclipse.core.interfaces.statistics.IDistribution;
import org.matheclipse.core.interfaces.statistics.IPDF;
import org.matheclipse.core.interfaces.statistics.IRandomVariate;
import org.matheclipse.core.interfaces.statistics.IStatistics;

/**
 * Distributions which are derived from other distributions or from an explicit density:
 * {@link S#ProbabilityDistribution}, {@link S#TransformedDistribution},
 * {@link S#MixtureDistribution}, {@link S#ParameterMixtureDistribution},
 * {@link S#ProductDistribution}, {@link S#CensoredDistribution},
 * {@link S#TruncatedDistribution}, {@link S#MatrixPropertyDistribution} and
 * {@link S#SmoothKernelDistribution}.
 */
public class StatisticsDerivedDistributions {

  private static class Initializer {

    private static void init() {
      S.CensoredDistribution.setEvaluator(new CensoredDistribution());
      S.MatrixPropertyDistribution.setEvaluator(new MatrixPropertyDistribution());
      S.MixtureDistribution.setEvaluator(new MixtureDistribution());
      S.ParameterMixtureDistribution.setEvaluator(new ParameterMixtureDistribution());
      S.ProbabilityDistribution.setEvaluator(new ProbabilityDistribution());
      S.ProductDistribution.setEvaluator(new ProductDistribution());
      S.TransformedDistribution.setEvaluator(new TransformedDistribution());
      S.TruncatedDistribution.setEvaluator(new TruncatedDistribution());
      S.DataDistribution.setEvaluator(new DataDistribution());
      S.EmpiricalDistribution.setEvaluator(new EmpiricalDistribution());
      S.HistogramDistribution.setEvaluator(new HistogramDistribution());
      S.SmoothKernelDistribution.setEvaluator(new SmoothKernelDistribution());
    }
  }

  /**
   * <code>ProbabilityDistribution(pdf, {x, xmin, xmax})</code> - a distribution defined by an
   * explicit probability density function.
   */
  public static final class ProbabilityDistribution extends AbstractEvaluator
      implements ICDF, IContinuousDistribution, IPDF, IStatistics, IRandomVariate {

    /** The iterator specification <code>{x, xmin, xmax}</code> of the (first) variable. */
    private static IAST iterator(IAST dist) {
      if (dist.size() >= 3 && dist.arg2().isList3()) {
        return (IAST) dist.arg2();
      }
      return null;
    }

    /** The number of cells of the table of the numeric cumulative distribution function. */
    private static final int SAMPLER_CELLS = 8192;

    /**
     * Samples of a univariate density by inverse transform sampling on a table of its numeric
     * cumulative distribution function. An infinite range is mapped onto a bounded one with
     * <code>x = c + s*t/(1-t^2)</code> (or <code>x = c + s*t/(1-t)</code> for a half-infinite
     * range), centered at the largest value of the density found on a coarse search and scaled by
     * the width of that peak.
     */
    @Override
    public IExpr randomVariate(java.util.Random random, IAST dist, int size) {
      IAST iterator = iterator(dist);
      if (iterator == null || !dist.isAST2() || !iterator.arg1().isSymbol()) {
        return F.NIL;
      }
      EvalEngine engine = EvalEngine.get();
      final ISymbol x = (ISymbol) iterator.arg1();
      double lo = iterator.arg2().isNegativeInfinity() ? Double.NEGATIVE_INFINITY
          : iterator.arg2().evalfNaN();
      double hi = iterator.arg3().isInfinity() ? Double.POSITIVE_INFINITY
          : iterator.arg3().evalfNaN();
      if (Double.isNaN(lo) || Double.isNaN(hi) || !(lo < hi)) {
        return F.NIL;
      }
      final org.matheclipse.core.generic.UnaryNumerical pdf;
      try {
        pdf = new org.matheclipse.core.generic.UnaryNumerical(dist.arg1(), x, Double.NaN, engine);
      } catch (RuntimeException rex) {
        return F.NIL;
      }
      java.util.function.DoubleUnaryOperator density = value -> {
        double d = pdf.value(value);
        return Double.isFinite(d) && d > 0.0 ? d : 0.0;
      };
      // a coarse search for the peak of the density
      double center = Double.isFinite(lo) ? (Double.isFinite(hi) ? 0.5 * (lo + hi) : lo) : hi;
      if (!Double.isFinite(center)) {
        center = 0.0;
      }
      double peakX = center;
      double peak = density.applyAsDouble(center);
      if (!Double.isFinite(lo) || !Double.isFinite(hi)) {
        for (int k = -6; k <= 12; k++) {
          for (double mantissa : new double[] {1.0, 2.0, 5.0}) {
            double offset = mantissa * Math.pow(10.0, k);
            for (double candidate : new double[] {center + offset, center - offset}) {
              if (candidate > lo && candidate < hi) {
                double d = density.applyAsDouble(candidate);
                if (d > peak) {
                  peak = d;
                  peakX = candidate;
                }
              }
            }
          }
        }
        if (!(peak > 0.0)) {
          return F.NIL;
        }
      }
      final double c;
      final double t0;
      final double t1;
      final int mapping;
      // the width of the peak, or its distance from a finite end of the range
      double width = 1.0 / peak;
      if (Double.isFinite(lo) != Double.isFinite(hi)) {
        width = Math.max(width, Math.abs(peakX - center));
      }
      final double scale = Double.isFinite(lo) && Double.isFinite(hi) ? 1.0 : width;
      if (Double.isFinite(lo) && Double.isFinite(hi)) {
        mapping = 0;
        c = 0.0;
        t0 = lo;
        t1 = hi;
      } else if (Double.isFinite(lo)) {
        mapping = 1;
        c = lo;
        t0 = 0.0;
        t1 = 1.0;
      } else if (Double.isFinite(hi)) {
        mapping = -1;
        c = hi;
        t0 = 0.0;
        t1 = 1.0;
      } else {
        mapping = 2;
        c = peakX;
        t0 = -1.0;
        t1 = 1.0;
      }
      final int n = SAMPLER_CELLS;
      double[] ts = new double[n + 1];
      double[] xs = new double[n + 1];
      double[] weights = new double[n + 1];
      double h = (t1 - t0) / n;
      for (int i = 0; i <= n; i++) {
        double t = t0 + i * h;
        ts[i] = t;
        double value;
        double derivative;
        switch (mapping) {
          case 1:
          case -1:
            if (t >= 1.0) {
              xs[i] = mapping * Double.POSITIVE_INFINITY;
              weights[i] = 0.0;
              continue;
            }
            value = c + mapping * scale * t / (1.0 - t);
            derivative = scale / ((1.0 - t) * (1.0 - t));
            break;
          case 2:
            if (Math.abs(t) >= 1.0) {
              xs[i] = t * Double.POSITIVE_INFINITY;
              weights[i] = 0.0;
              continue;
            }
            value = c + scale * t / (1.0 - t * t);
            derivative = scale * (1.0 + t * t) / ((1.0 - t * t) * (1.0 - t * t));
            break;
          default:
            value = t;
            derivative = 1.0;
        }
        xs[i] = value;
        weights[i] = density.applyAsDouble(value) * derivative;
        if (!Double.isFinite(weights[i])) {
          weights[i] = 0.0;
        }
      }
      // the cumulative probabilities by the trapezoidal rule
      double[] cdf = new double[n + 1];
      for (int i = 1; i <= n; i++) {
        cdf[i] = cdf[i - 1] + 0.5 * h * (weights[i - 1] + weights[i]);
      }
      double total = cdf[n];
      if (!(total > 0.0) || !Double.isFinite(total)) {
        return F.NIL;
      }
      IASTAppendable result = F.ListAlloc(size);
      for (int s = 0; s < size; s++) {
        double u = random.nextDouble() * total;
        int index = java.util.Arrays.binarySearch(cdf, u);
        if (index < 0) {
          index = -index - 2;
        }
        index = Math.max(0, Math.min(n - 1, index));
        double cell = cdf[index + 1] - cdf[index];
        double fraction = cell > 0.0 ? (u - cdf[index]) / cell : 0.5;
        double t = ts[index] + fraction * h;
        double sample;
        switch (mapping) {
          case 1:
          case -1:
            sample = c + mapping * scale * t / (1.0 - t);
            break;
          case 2:
            sample = c + scale * t / (1.0 - t * t);
            break;
          default:
            sample = t;
        }
        if (!Double.isFinite(sample)) {
          sample = xs[index];
        }
        result.append(F.num(sample));
      }
      return result;
    }

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      IAST iterator = iterator(dist);
      if (iterator != null && dist.isAST2()) {
        IExpr x = iterator.arg1();
        IExpr min = iterator.arg2();
        // Integrate(pdf, {x, xmin, #}) &
        return callFunction(F.Function(F.Integrate(dist.arg1(), F.list(x, min, F.Slot1))), k);
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_INFINITY;
    }

    @Override
    public IExpr inverseCDF(IAST dist, IExpr k, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public IExpr mean(IAST dist) {
      return expectation(dist, F.NIL, EvalEngine.get());
    }

    @Override
    public IExpr median(IAST dist) {
      return F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      IAST iterator = iterator(dist);
      if (iterator != null && dist.isAST2()) {
        IExpr x = iterator.arg1();
        return callFunction(F.Function(F.subst(dist.arg1(), F.Rule(x, F.Slot1))), k);
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
    public IExpr survivalFunction(IAST dist, IExpr k, EvalEngine engine) {
      IAST iterator = iterator(dist);
      if (iterator != null && dist.isAST2()) {
        IExpr x = iterator.arg1();
        IExpr max = iterator.arg3();
        // Integrate(pdf, {x, #, xmax}) &
        return callFunction(F.Function(F.Integrate(dist.arg1(), F.list(x, F.Slot1, max))), k);
      }
      return F.NIL;
    }

    @Override
    public IExpr variance(IAST dist) {
      EvalEngine engine = EvalEngine.get();
      IExpr mean = expectation(dist, F.NIL, engine);
      if (mean.isPresent()) {
        IAST iterator = iterator(dist);
        if (iterator != null) {
          IExpr secondMoment = expectation(dist, F.Sqr(iterator.arg1()), engine);
          if (secondMoment.isPresent()) {
            return engine.evaluate(F.Subtract(secondMoment, F.Sqr(mean)));
          }
        }
      }
      return F.NIL;
    }

    /**
     * <code>Integrate(expr*pdf, {x, xmin, xmax}, ...)</code> over all variables of the
     * distribution. If <code>expr</code> is {@link F#NIL} the mean, i.e. the expectation of the
     * first variable, is computed.
     */
    public static IExpr expectation(IAST dist, IExpr expr, EvalEngine engine) {
      return expectation(dist, expr, F.NIL, engine);
    }

    /**
     * @param variable the random variable used in <code>expr</code>; it is replaced by the variable
     *        of the density if the two differ
     */
    public static IExpr expectation(IAST dist, IExpr expr, IExpr variable, EvalEngine engine) {
      IAST iterator = iterator(dist);
      if (iterator == null) {
        return F.NIL;
      }
      IExpr function = expr.isPresent() ? expr : iterator.arg1();
      if (expr.isPresent() && variable.isSymbol() && !variable.equals(iterator.arg1())) {
        function = F.subst(function, F.Rule(variable, iterator.arg1()));
      }
      IASTAppendable integrate = F.ast(S.Integrate, dist.size());
      integrate.append(F.Times(function, dist.arg1()));
      for (int i = 2; i < dist.size(); i++) {
        if (!dist.get(i).isList3()) {
          return F.NIL;
        }
        integrate.append(dist.get(i));
      }
      IExpr result = engine.evaluate(integrate);
      return result.isAST(S.Integrate) ? F.NIL : result;
    }
  }

  /**
   * <code>ProductDistribution(dist1, dist2, ...)</code> - the joint distribution of independent
   * components.
   */
  public static final class ProductDistribution extends AbstractEvaluator
      implements ICDF, IContinuousDistribution, IPDF, IStatistics {

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      if (k.isPresent() && k.isList() && k.size() == dist.size()) {
        IASTAppendable product = F.TimesAlloc(dist.argSize());
        for (int i = 1; i < dist.size(); i++) {
          product.append(F.CDF(dist.get(i), ((IAST) k).get(i)));
        }
        return product;
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }

    @Override
    public IExpr inverseCDF(IAST dist, IExpr k, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public IExpr mean(IAST dist) {
      return dist.map(S.List, x -> F.Mean(x));
    }

    @Override
    public IExpr median(IAST dist) {
      return dist.map(S.List, x -> F.Median(x));
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      if (k.isPresent() && k.isList() && k.size() == dist.size()) {
        IAST arguments = (IAST) k;
        IASTAppendable conditions = F.ListAlloc(dist.argSize());
        IASTAppendable product = F.TimesAlloc(dist.argSize());
        for (int i = 1; i < dist.size(); i++) {
          IExpr density = engine.evaluate(F.PDF(dist.get(i), arguments.get(i)));
          if (density.isAST(S.PDF)) {
            return F.NIL;
          }
          if (density.isAST(S.Piecewise, 3) && density.first().isList1()
              && density.first().first().isList2()) {
            // collect the conditions of all single component densities
            IAST branch = (IAST) density.first().first();
            product.append(branch.arg1());
            conditions.append(branch.arg2());
          } else {
            product.append(density);
          }
        }
        if (conditions.isEmpty()) {
          return product;
        }
        return F.Piecewise(
            F.list(
                F.list(product, conditions.isAST1() ? conditions.arg1() : conditions.apply(S.And))),
            F.C0);
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
      return dist.map(S.List, x -> F.Variance(x));
    }
  }

  /**
   * <code>MixtureDistribution({w1,...}, {dist1,...})</code> - a weighted mixture of distributions.
   */
  public static final class MixtureDistribution extends AbstractEvaluator
      implements ICDF, IContinuousDistribution, IPDF, IStatistics {

    /** The weights normalized to total 1, or <code>null</code> if the arguments don't match. */
    private static IAST normalizedWeights(IAST dist) {
      if (dist.isAST2() && dist.arg1().isList() && dist.arg2().isList()
          && dist.arg1().size() == dist.arg2().size() && dist.arg1().size() > 1) {
        IAST weights = (IAST) dist.arg1();
        IExpr total = EvalEngine.get().evaluate(weights.apply(S.Plus));
        return (IAST) weights.map(w -> F.Divide(w, total), 1);
      }
      return null;
    }

    /** The weighted sum <code>Sum(w_i*f(dist_i))</code>. */
    private static IExpr weightedSum(IAST dist, java.util.function.Function<IExpr, IExpr> f) {
      IAST weights = normalizedWeights(dist);
      if (weights == null) {
        return F.NIL;
      }
      IAST distributions = (IAST) dist.arg2();
      IASTAppendable sum = F.PlusAlloc(weights.argSize());
      for (int i = 1; i < weights.size(); i++) {
        sum.append(F.Times(weights.get(i), f.apply(distributions.get(i))));
      }
      return sum;
    }

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      if (k.isPresent() && !k.isList()) {
        return weightedSum(dist, d -> F.CDF(d, k));
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
    public IExpr inverseCDF(IAST dist, IExpr k, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public IExpr mean(IAST dist) {
      return weightedSum(dist, d -> F.Mean(d));
    }

    @Override
    public IExpr median(IAST dist) {
      return F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      if (k.isPresent() && !k.isList()) {
        return weightedSum(dist, d -> F.PDF(d, k));
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
      // law of total variance: Sum(w_i*(Var_i + Mean_i^2)) - Mean^2
      IExpr mean = mean(dist);
      if (mean.isNIL()) {
        return F.NIL;
      }
      IExpr secondMoment = weightedSum(dist, d -> F.Plus(F.Variance(d), F.Sqr(F.Mean(d))));
      if (secondMoment.isNIL()) {
        return F.NIL;
      }
      return EvalEngine.get().evaluate(F.Subtract(secondMoment, F.Sqr(mean)));
    }
  }

  /**
   * <code>ParameterMixtureDistribution(dist, Distributed(p, weightDist))</code> - a distribution
   * whose parameter is itself distributed. Only the closed forms which reduce to a standard
   * distribution are evaluated.
   */
  public static final class ParameterMixtureDistribution extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST2() && ast.arg1().isAST() && ast.arg2().isAST(S.Distributed, 3)) {
        IAST dist = (IAST) ast.arg1();
        IAST distributed = (IAST) ast.arg2();
        IExpr parameter = distributed.arg1();
        IExpr weightDistribution = distributed.arg2();
        // BinomialDistribution(n, p) with p ~ BetaDistribution(a, b) == BetaBinomialDistribution
        if (dist.isAST(S.BinomialDistribution, 3) && dist.arg2().equals(parameter)
            && weightDistribution.isAST(S.BetaDistribution, 3)) {
          IAST beta = (IAST) weightDistribution;
          return F.ternaryAST3(S.BetaBinomialDistribution, beta.arg1(), beta.arg2(), dist.arg1());
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.NOATTRIBUTE);
    }
  }

  /**
   * <code>TransformedDistribution(expr, x \[Distributed] dist)</code> - the distribution of
   * <code>expr</code>. Only affine transformations which stay inside the same distribution family
   * are evaluated.
   */
  public static final class TransformedDistribution extends AbstractFunctionEvaluator {

    /**
     * Decompose <code>expr</code> into <code>{scale, shift}</code> such that
     * <code>expr == scale*x + shift</code>, or <code>null</code>.
     */
    private static IExpr[] affine(IExpr expr, IExpr x, EvalEngine engine) {
      IExpr shift = engine.evaluate(F.subst(expr, F.Rule(x, F.C0)));
      IExpr scale = engine.evaluate(F.Subtract(F.subst(expr, F.Rule(x, F.C1)), shift));
      if (!shift.isFree(x) || !scale.isFree(x) || scale.isZero()) {
        return null;
      }
      // verify linearity
      IExpr check = engine.evaluate(F.Simplify(F.Subtract(expr, F.Plus(F.Times(scale, x), shift))));
      if (!check.isZero()) {
        return null;
      }
      return new IExpr[] {scale, shift};
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST2() && ast.arg2().isAST(S.Distributed, 3)) {
        IAST distributed = (IAST) ast.arg2();
        IExpr x = distributed.arg1();
        IExpr distribution = distributed.arg2();
        if (!x.isSymbol() || !distribution.isAST()) {
          return F.NIL;
        }
        IExpr[] affine = affine(ast.arg1(), x, engine);
        if (affine == null) {
          return F.NIL;
        }
        IExpr scale = affine[0];
        IExpr shift = affine[1];
        IAST dist = (IAST) distribution;
        if (dist.isAST(S.NormalDistribution, 3)) {
          // NormalDistribution(scale*m + shift, Abs(scale)*s)
          return F.NormalDistribution(F.Plus(F.Times(scale, dist.arg1()), shift),
              F.Times(F.Abs(scale), dist.arg2()));
        }
        if (dist.isAST(S.UniformDistribution, 2) && dist.arg1().isList2()) {
          IAST minMax = (IAST) dist.arg1();
          IExpr a = F.Plus(F.Times(scale, minMax.arg1()), shift);
          IExpr b = F.Plus(F.Times(scale, minMax.arg2()), shift);
          IExpr ordered = engine.evaluate(F.Less(a, b));
          if (ordered.isFalse()) {
            return F.UniformDistribution(F.list(b, a));
          }
          return F.UniformDistribution(F.list(a, b));
        }
        if (shift.isZero() && dist.isAST(S.ExponentialDistribution, 2)) {
          IExpr positive = engine.evaluate(F.Greater(scale, F.C0));
          if (positive.isTrue()) {
            // ExponentialDistribution(a/scale)
            return F.ExponentialDistribution(F.Divide(dist.arg1(), scale));
          }
        }
        if (shift.isZero() && dist.isAST(S.GammaDistribution, 3)) {
          IExpr positive = engine.evaluate(F.Greater(scale, F.C0));
          if (positive.isTrue()) {
            // GammaDistribution(a, scale*b)
            return F.GammaDistribution(dist.arg1(), F.Times(scale, dist.arg2()));
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.NOATTRIBUTE);
    }
  }

  /**
   * Canonicalizes the inner distribution of <code>head({xmin, xmax}, dist)</code>, e.g.
   * <code>NormalDistribution()</code> to <code>NormalDistribution(0,1)</code>.
   *
   * @return {@link F#NIL} if the inner distribution is unchanged or not a known distribution
   */
  private static IExpr canonicalizeInner(final IAST ast, ISymbol head, EvalEngine engine) {
    if (ast.isAST2() && ast.arg1().isList2() && ast.arg2().isAST()) {
      IExpr distribution = engine.evaluate(ast.arg2());
      IDistribution evaluator =
          distribution.isAST() ? ((IAST) distribution).headInstanceOf(IDistribution.class) : null;
      if (evaluator != null) {
        IAST checked = evaluator.checkParameters((IAST) distribution);
        if (checked.isPresent() && !checked.equals(ast.arg2())) {
          return F.binaryAST2(head, ast.arg1(), checked);
        }
      }
    }
    return F.NIL;
  }

  /**
   * <code>CensoredDistribution({xmin, xmax}, dist)</code> - the values of <code>dist</code>
   * censored to lie between <code>xmin</code> and <code>xmax</code>.
   */
  public static final class CensoredDistribution extends AbstractEvaluator
      implements IContinuousDistribution, IStatistics {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return canonicalizeInner(ast, S.CensoredDistribution, engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public IExpr mean(IAST dist) {
      return expectation(dist, F.NIL, EvalEngine.get());
    }

    @Override
    public IExpr median(IAST dist) {
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

    /**
     * <code>Expectation(expr(Min(Max(x, xmin), xmax)), x \[Distributed] dist)</code> - the
     * censoring is expressed as a transformation of the random variable.
     */
    public static IExpr expectation(IAST dist, IExpr expr, EvalEngine engine) {
      if (dist.isAST2() && dist.arg1().isList2() && dist.arg2().isAST()) {
        IAST bounds = (IAST) dist.arg1();
        IExpr min = bounds.arg1();
        IExpr max = bounds.arg2();
        IAST inner = (IAST) dist.arg2();
        ISymbol x = F.Dummy("x");
        // Min(Max(x, min), max)
        IExpr censored = F.Min(F.Max(x, min), max);
        IExpr function = expr.isPresent() ? F.subst(expr, F.Rule(expr, censored)) : censored;
        if (expr.isPresent()) {
          function = censored;
        }
        if (inner.isAST(S.ProbabilityDistribution)) {
          return censoredExpectation(inner, min, max, engine);
        }
      }
      return F.NIL;
    }

    /**
     * <code>xmin*P(X &lt;= xmin) + Integrate(x*pdf, {x, xmin, xmax}) + xmax*P(X &gt;= xmax)</code>
     */
    private static IExpr censoredExpectation(IAST inner, IExpr min, IExpr max, EvalEngine engine) {
      if (!inner.isAST2() || !inner.arg2().isList3()) {
        return F.NIL;
      }
      IAST iterator = (IAST) inner.arg2();
      IExpr x = iterator.arg1();
      IExpr density = inner.arg1();
      IExpr lower = engine.evaluate(F.CDF(inner, min));
      IExpr upper = engine.evaluate(F.SurvivalFunction(inner, max));
      IExpr middle = engine.evaluate(F.Integrate(F.Times(x, density), F.list(x, min, max)));
      if (lower.isAST(S.CDF) || upper.isAST(S.SurvivalFunction) || middle.isAST(S.Integrate)) {
        return F.NIL;
      }
      return engine.evaluate(F.Plus(F.Times(min, lower), middle, F.Times(max, upper)));
    }
  }


  /**
   * <code>TruncatedDistribution({min, max}, dist)</code> - <code>dist</code> conditioned on the
   * interval <code>min &lt;= x &lt;= max</code>, i.e. its density restricted to that interval and
   * renormalized by <code>CDF(dist,max) - CDF(dist,min)</code>.
   *
   * <p>
   * A discrete <code>dist</code> keeps the values <code>min &lt; x &lt;= max</code> - the lower end
   * is left out, so that the normalization is <code>CDF(dist,max) - CDF(dist,min)</code> as for a
   * continuous one. Each truncation is continuous or discrete as <code>dist</code> is.
   */
  public static final class TruncatedDistribution extends AbstractEvaluator implements
      IContinuousDistribution, IDiscreteDistribution, ICDF, IPDF, IStatistics, IRandomVariate {

    @Override
    public int kind(IAST distribution) {
      if (distribution.isAST2()) {
        if (distribution.arg2().isDiscreteDistribution()) {
          return DISCRETE;
        }
        if (distribution.arg2().isContinuousDistribution()) {
          return CONTINUOUS;
        }
      }
      return 0;
    }

    @Override
    public int getSupportLowerBound(IExpr distribution) {
      DiscreteWindow window =
          distribution.isAST() ? DiscreteWindow.of((IAST) distribution, EvalEngine.get()) : null;
      if (window == null || !window.first.isInteger()) {
        return Integer.MIN_VALUE;
      }
      int first = window.first.toIntDefault();
      return F.isPresent(first) ? first : Integer.MIN_VALUE;
    }

    @Override
    public int getSupportUpperBound(IExpr distribution) {
      DiscreteWindow window =
          distribution.isAST() ? DiscreteWindow.of((IAST) distribution, EvalEngine.get()) : null;
      if (window == null || !window.last.isInteger()) {
        return Integer.MAX_VALUE;
      }
      int last = window.last.toIntDefault();
      return F.isPresent(last) ? last : Integer.MAX_VALUE;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return canonicalizeInner(ast, S.TruncatedDistribution, engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    /**
     * @return <code>{min, max, dist}</code> or <code>null</code> if this isn't a truncation of a
     *         continuous distribution
     */
    private static IExpr[] parts(IAST dist) {
      if (dist.isAST2() && dist.arg1().isList2() && dist.arg2().isContinuousDistribution()) {
        IAST bounds = (IAST) dist.arg1();
        return new IExpr[] {bounds.arg1(), bounds.arg2(), dist.arg2()};
      }
      return null;
    }

    /**
     * @return <code>CDF(dist, bound)</code>, shortcut to <code>0</code> or <code>1</code> for an
     *         infinite bound, or {@link F#NIL}
     */
    private static IExpr cdfAt(IExpr distribution, IExpr bound, boolean upper, EvalEngine engine) {
      if (bound.isNegativeInfinity()) {
        return upper ? F.NIL : F.C0;
      }
      if (bound.isInfinity()) {
        return upper ? F.C1 : F.NIL;
      }
      return S.CDF.ofNIL(engine, distribution, bound);
    }

    /**
     * @return the probability mass <code>CDF(dist,max) - CDF(dist,min)</code> which the truncation
     *         renormalizes by, or {@link F#NIL}
     */
    private static IExpr normalization(IExpr distribution, IExpr min, IExpr max,
        EvalEngine engine) {
      IExpr lower = cdfAt(distribution, min, false, engine);
      IExpr upper = cdfAt(distribution, max, true, engine);
      if (lower.isNIL() || upper.isNIL()) {
        return F.NIL;
      }
      return engine.evaluate(F.Subtract(upper, lower));
    }

    /**
     * The truncation of a discrete distribution to <code>min &lt; x &lt;= max</code>: the integers
     * <code>first .. last</code> in it, the probability below them and their probability.
     */
    private static final class DiscreteWindow {
      final IExpr inner;
      final IExpr min;
      final IExpr max;
      final IExpr first;
      final IExpr last;
      final IExpr below;
      final IExpr mass;

      private DiscreteWindow(IExpr inner, IExpr min, IExpr max, IExpr first, IExpr last,
          IExpr below, IExpr mass) {
        this.inner = inner;
        this.min = min;
        this.max = max;
        this.first = first;
        this.last = last;
        this.below = below;
        this.mass = mass;
      }

      /** @return {@code null} if this isn't a truncation of a discrete distribution */
      static DiscreteWindow of(IAST dist, EvalEngine engine) {
        if (!dist.isAST2() || !dist.arg1().isList2() || !dist.arg2().isDiscreteDistribution()) {
          return null;
        }
        IExpr inner = dist.arg2();
        IExpr min = dist.arg1().first();
        IExpr max = dist.arg1().second();
        IExpr first =
            min.isNegativeInfinity() ? min : engine.evaluate(F.Plus(F.Floor(min), F.C1));
        IExpr last = max.isInfinity() ? max : engine.evaluate(F.Floor(max));
        if (!(first.isInteger() || first.isNegativeInfinity())
            || !(last.isInteger() || last.isInfinity())) {
          return null;
        }
        IExpr below = min.isNegativeInfinity() ? F.C0 : S.CDF.ofNIL(engine, inner, min);
        IExpr upTo = max.isInfinity() ? F.C1 : S.CDF.ofNIL(engine, inner, max);
        if (below.isNIL() || upTo.isNIL()) {
          return null;
        }
        IExpr mass = engine.evaluate(F.Subtract(upTo, below));
        if (mass.isZero()) {
          return null;
        }
        return new DiscreteWindow(inner, min, max, first, last, below, mass);
      }

      /** <code>min &lt; k &lt;= max</code>, without an infinite end */
      IExpr condition(IExpr k) {
        if (min.isNegativeInfinity()) {
          return F.LessEqual(k, max);
        }
        if (max.isInfinity()) {
          return F.Greater(k, min);
        }
        return F.function(S.Inequality, min, S.Less, k, S.LessEqual, max);
      }

      /** <code>Sum(k^n*PDF(dist,k), {k,first,last})/mass</code>, or {@link F#NIL} */
      IExpr rawMoment(int n, EvalEngine engine) {
        ISymbol k = F.Dummy("k");
        IExpr density = S.PDF.ofNIL(engine, inner, k);
        if (density.isNIL()) {
          return F.NIL;
        }
        if (density.isAST(S.Piecewise)) {
          // the cases of the density outside of the window don't take part in the sum
          density =
              engine.evaluate(F.Refine(density, F.And(condition(k), F.Element(k, S.Integers))));
        }
        IExpr sum = engine.evaluate(
            F.Sum(F.Times(F.Power(k, F.ZZ(n)), density), F.list(k, first, last)));
        if (!sum.isFree(S.Sum, true) || !sum.isFree(k, true)) {
          return F.NIL;
        }
        return engine.evaluate(F.Divide(sum, mass));
      }
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      IExpr[] parts = parts(dist);
      if (parts == null) {
        DiscreteWindow window = DiscreteWindow.of(dist, engine);
        if (window == null) {
          return F.NIL;
        }
        IExpr innerDensity = S.PDF.ofNIL(engine, window.inner, k);
        if (innerDensity.isNIL()) {
          return F.NIL;
        }
        if (innerDensity.isAST(S.Piecewise)) {
          // the cases of the density outside of the window are cut off anyway
          innerDensity = engine.evaluate(F.Refine(innerDensity, window.condition(k)));
        }
        // Piecewise({{PDF(dist,k)/z, min < k <= max}}, 0)
        return F.Piecewise(
            F.list(F.list(F.Divide(innerDensity, window.mass), window.condition(k))), F.C0);
      }
      IExpr density = S.PDF.ofNIL(engine, parts[2], k);
      IExpr z = normalization(parts[2], parts[0], parts[1], engine);
      if (density.isNIL() || z.isNIL()) {
        return F.NIL;
      }
      // Piecewise({{PDF(dist,k)/z, min <= k <= max}}, 0)
      return F.Piecewise(
          F.list(F.list(F.Divide(density, z), F.LessEqual(parts[0], k, parts[1]))), F.C0);
    }

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      IExpr[] parts = parts(dist);
      if (parts == null) {
        DiscreteWindow window = DiscreteWindow.of(dist, engine);
        if (window == null) {
          return F.NIL;
        }
        IExpr cumulative = S.CDF.ofNIL(engine, window.inner, k);
        if (cumulative.isNIL()) {
          return F.NIL;
        }
        if (cumulative.isAST(S.Piecewise)) {
          cumulative = engine.evaluate(F.Refine(cumulative, window.condition(k)));
        }
        IAST inside = F.list(F.Divide(F.Subtract(cumulative, window.below), window.mass),
            window.condition(k));
        if (window.max.isInfinity()) {
          return F.Piecewise(F.list(inside), F.C0);
        }
        // Piecewise({{1, k > max}, {(CDF(dist,k)-below)/z, min < k <= max}}, 0)
        return F.Piecewise(F.list(F.list(F.C1, F.Greater(k, window.max)), inside), F.C0);
      }
      IExpr lower = cdfAt(parts[2], parts[0], false, engine);
      IExpr z = normalization(parts[2], parts[0], parts[1], engine);
      IExpr cumulative = S.CDF.ofNIL(engine, parts[2], k);
      if (lower.isNIL() || z.isNIL() || cumulative.isNIL()) {
        return F.NIL;
      }
      // Piecewise({{0, k < min}, {(CDF(dist,k)-CDF(dist,min))/z, min <= k < max}}, 1)
      return F.Piecewise(F.list(//
          F.list(F.C0, F.Less(k, parts[0])), //
          F.list(F.Divide(F.Subtract(cumulative, lower), z),
              F.And(F.LessEqual(parts[0], k), F.Less(k, parts[1])))), //
          F.C1);
    }

    @Override
    public IExpr inverseCDF(IAST dist, IExpr k, EvalEngine engine) {
      IExpr[] parts = parts(dist);
      if (parts == null) {
        DiscreteWindow window = DiscreteWindow.of(dist, engine);
        if (window == null) {
          return F.NIL;
        }
        // the smallest k whose probability reaches below + p*z; for p = 0 that is min itself
        return S.InverseCDF.ofNIL(engine, window.inner,
            F.Plus(window.below, F.Times(k, window.mass)));
      }
      IExpr lower = cdfAt(parts[2], parts[0], false, engine);
      IExpr z = normalization(parts[2], parts[0], parts[1], engine);
      if (lower.isNIL() || z.isNIL()) {
        return F.NIL;
      }
      // InverseCDF(dist, CDF(dist,min) + k*z)
      return S.InverseCDF.ofNIL(engine, parts[2], F.Plus(lower, F.Times(k, z)));
    }

    /**
     * <code>Integrate(x^n*PDF(dist,x), {x,min,max})/z</code>
     *
     * @return {@link F#NIL} if the integral doesn't evaluate
     */
    private static IExpr rawMoment(IAST dist, int n, EvalEngine engine) {
      IExpr[] parts = parts(dist);
      if (parts == null) {
        return F.NIL;
      }
      ISymbol x = F.Dummy("x");
      IExpr density = S.PDF.ofNIL(engine, parts[2], x);
      IExpr z = normalization(parts[2], parts[0], parts[1], engine);
      if (density.isNIL() || z.isNIL() || z.isZero()) {
        return F.NIL;
      }
      IExpr integral = engine.evaluate(F.Integrate(F.Times(F.Power(x, F.ZZ(n)), density),
          F.list(x, parts[0], parts[1])));
      if (!integral.isFree(S.Integrate, true) || !integral.isFree(x, true)) {
        return F.NIL;
      }
      return engine.evaluate(F.Divide(integral, z));
    }

    /** The raw moment of a continuous or of a discrete truncation. */
    private static IExpr moment(IAST dist, int n, EvalEngine engine) {
      if (parts(dist) == null) {
        DiscreteWindow window = DiscreteWindow.of(dist, engine);
        return window == null ? F.NIL : window.rawMoment(n, engine);
      }
      return rawMoment(dist, n, engine);
    }

    @Override
    public IExpr mean(IAST dist) {
      return moment(dist, 1, EvalEngine.get());
    }

    @Override
    public IExpr variance(IAST dist) {
      EvalEngine engine = EvalEngine.get();
      IExpr firstMoment;
      IExpr secondMoment;
      if (parts(dist) == null) {
        // one window for both moments
        DiscreteWindow window = DiscreteWindow.of(dist, engine);
        if (window == null) {
          return F.NIL;
        }
        firstMoment = window.rawMoment(1, engine);
        secondMoment = window.rawMoment(2, engine);
      } else {
        firstMoment = rawMoment(dist, 1, engine);
        secondMoment = rawMoment(dist, 2, engine);
      }
      if (firstMoment.isNIL() || secondMoment.isNIL()) {
        return F.NIL;
      }
      return engine.evaluate(F.Subtract(secondMoment, F.Sqr(firstMoment)));
    }

    @Override
    public IExpr median(IAST dist) {
      return inverseCDF(dist, F.C1D2, EvalEngine.get());
    }

    @Override
    public IExpr skewness(IAST dist) {
      return F.NIL;
    }

    @Override
    public IExpr randomVariate(java.util.Random random, IAST dist, int size) {
      EvalEngine engine = EvalEngine.get();
      IExpr[] parts = parts(dist);
      if (parts == null) {
        DiscreteWindow window = DiscreteWindow.of(dist, engine);
        return window == null ? F.NIL : discreteVariates(random, window, size, engine);
      }
      IExpr lower = cdfAt(parts[2], parts[0], false, engine);
      IExpr z = normalization(parts[2], parts[0], parts[1], engine);
      if (lower.isNIL() || z.isNIL()) {
        return F.NIL;
      }
      double lowerDouble = engine.evalN(lower).evalfNaN();
      double zDouble = engine.evalN(z).evalfNaN();
      if (!Double.isFinite(lowerDouble) || !Double.isFinite(zDouble) || zDouble <= 0.0) {
        return F.NIL;
      }
      IASTAppendable result = F.ListAlloc(size);
      for (int i = 0; i < size; i++) {
        // inverse transform sampling on the truncated probability window
        IExpr probability = F.num(lowerDouble + random.nextDouble() * zDouble);
        IExpr variate = engine.evalN(S.InverseCDF.ofNIL(engine, parts[2], probability));
        if (!variate.isReal()) {
          return F.NIL;
        }
        result.append(variate);
      }
      return result;
    }

    /** The most values of a window whose cumulative probabilities a sample tabulates. */
    private static final int MAX_TABULATED = 100_000;

    /**
     * Inverse transform sampling on the window of a discrete truncation: the smallest value whose
     * cumulative probability within the window reaches a uniform number. Starting at the first
     * value of the window, the cumulative probabilities are tabulated once, as far as the samples
     * need them, and searched for each sample; a window without a first value asks
     * <code>InverseCDF</code> for every sample.
     */
    private static IExpr discreteVariates(java.util.Random random, DiscreteWindow window,
        int size, EvalEngine engine) {
      double below = engine.evalN(window.below).evalfNaN();
      double mass = engine.evalN(window.mass).evalfNaN();
      if (!Double.isFinite(below) || !Double.isFinite(mass) || mass <= 0.0) {
        return F.NIL;
      }
      IASTAppendable result = F.ListAlloc(size);
      if (!window.first.isInteger()) {
        for (int i = 0; i < size; i++) {
          // within (below, below + mass], and never above 1 through rounding
          double probability = Math.min(1.0, below + (1.0 - random.nextDouble()) * mass);
          IExpr variate = S.InverseCDF.ofNIL(engine, window.inner, F.num(probability));
          if (!variate.isInteger()) {
            return F.NIL;
          }
          if (window.last.isInteger() && ((IInteger) variate).isGT((IInteger) window.last)) {
            variate = window.last;
          }
          result.append(variate);
        }
        return result;
      }
      int first = window.first.toIntDefault();
      if (F.isNotPresent(first)) {
        return F.NIL;
      }
      long last = window.last.isInteger() ? window.last.toLongDefault() : Long.MAX_VALUE;
      // cumulative[i]: the probability of first .. first + i within the window
      double[] cumulative = new double[64];
      int count = 0;
      double total = 0.0;
      for (int i = 0; i < size; i++) {
        double u = 1.0 - random.nextDouble();
        while ((count == 0 || cumulative[count - 1] < u) && first + (long) count <= last
            && count < MAX_TABULATED) {
          double p = engine.evalN(F.PDF(window.inner, F.ZZ(first + count))).evalfNaN();
          if (!Double.isFinite(p)) {
            return F.NIL;
          }
          total += p / mass;
          if (count == cumulative.length) {
            cumulative = java.util.Arrays.copyOf(cumulative, 2 * count);
          }
          cumulative[count++] = total;
        }
        int index = java.util.Arrays.binarySearch(cumulative, 0, count, u);
        if (index < 0) {
          index = -index - 1;
        }
        // a u which the rounded total doesn't reach takes the last tabulated value
        result.append(F.ZZ(first + Math.min(index, count - 1)));
      }
      return result;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }


  /**
   * <code>EmpiricalDistribution({x1, x2, ...})</code> - the discrete distribution which assigns
   * each distinct data value its relative frequency. It evaluates to a {@link S#DataDistribution}.
   */
  public static final class EmpiricalDistribution extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.isAST1()) {
        return F.NIL;
      }
      IAST data;
      IAST weights = F.NIL;
      if (ast.arg1().isRuleAST() && ast.arg1().first().isList() && ast.arg1().second().isList()) {
        // EmpiricalDistribution({w1, w2, ...} -> {x1, x2, ...})
        weights = (IAST) ast.arg1().first();
        data = (IAST) ast.arg1().second();
        if (weights.size() != data.size()) {
          return F.NIL;
        }
        for (int i = 1; i < weights.size(); i++) {
          if (!weights.get(i).isReal() || weights.get(i).isNegative()) {
            return F.NIL;
          }
        }
      } else if (ast.arg1().isList()) {
        data = (IAST) ast.arg1();
      } else {
        return F.NIL;
      }
      if (data.size() < 2) {
        return F.NIL;
      }
      int n = data.argSize();
      Integer[] order = new Integer[n];
      boolean inexact = false;
      for (int i = 1; i < data.size(); i++) {
        if (!data.get(i).isReal()) {
          return F.NIL;
        }
        inexact |= data.get(i).isInexactNumber();
        if (weights.isPresent()) {
          inexact |= weights.get(i).isInexactNumber();
        }
        order[i - 1] = i;
      }
      final IAST values = data;
      Arrays.sort(order, (a, b) -> values.get(a).compareTo(values.get(b)));

      IASTAppendable distinct = F.ListAlloc(n);
      IASTAppendable sums = F.ListAlloc(n);
      IASTAppendable total = F.PlusAlloc(n);
      int i = 0;
      while (i < n) {
        IExpr value = values.get(order[i]);
        IASTAppendable sum = F.PlusAlloc(4);
        int count = 0;
        while (i + count < n && values.get(order[i + count]).equals(value)) {
          sum.append(weights.isPresent() ? weights.get(order[i + count]) : F.C1);
          count++;
        }
        distinct.append(value);
        sums.append(sum.oneIdentity0());
        total.append(sum.oneIdentity0());
        i += count;
      }
      IExpr totalWeight = engine.evaluate(total.oneIdentity0());
      if (totalWeight.isZero()) {
        return F.NIL;
      }
      // the probabilities are machine numbers for inexact values or weights
      final boolean numeric = inexact;
      IASTAppendable probabilities = F.mapRange(1, sums.size(), j -> {
        IExpr probability = engine.evaluate(F.Divide(sums.get(j), totalWeight));
        return numeric ? engine.evalN(probability) : probability;
      });
      return F.quaternary(S.DataDistribution, S.Empirical, F.list(probabilities, distinct, S.False),
          F.C1, F.ZZ(n));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.NOATTRIBUTE);
    }
  }

  /**
   * <code>HistogramDistribution(data)</code> or <code>HistogramDistribution(data, {w})</code> - the
   * piecewise constant distribution of a histogram of <code>data</code>. It evaluates to a
   * {@link S#DataDistribution}.
   */
  public static final class HistogramDistribution extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.argSize() < 1 || ast.argSize() > 2 || !ast.arg1().isList() || ast.arg1().size() < 2) {
        return F.NIL;
      }
      IAST data = (IAST) ast.arg1();
      double[] numeric = new double[data.argSize()];
      IExpr[] values = new IExpr[data.argSize()];
      for (int i = 1; i < data.size(); i++) {
        if (!data.get(i).isReal()) {
          return F.NIL;
        }
        values[i - 1] = data.get(i);
        numeric[i - 1] = data.get(i).evalfNaN();
        if (Double.isNaN(numeric[i - 1])) {
          return F.NIL;
        }
      }
      IExpr width = F.NIL;
      if (ast.isAST2()) {
        if (ast.arg2().isList1()) {
          width = ast.arg2().first();
        } else {
          return F.NIL;
        }
      }
      Arrays.sort(values, (a, b) -> a.compareTo(b));
      Arrays.sort(numeric);
      IExpr min = values[0];
      IExpr max = values[values.length - 1];
      if (width.isNIL()) {
        width = F.num(org.matheclipse.core.builtin.graphics.Histogram.binWidth(numeric,
            numeric[0], numeric[numeric.length - 1]));
      }
      IExpr origin = origin(min, width);
      if (origin.isInexactNumber() && !width.isInexactNumber()) {
        // the centered fallback is computed numerically, so the whole histogram becomes numeric
        width = engine.evaluate(F.N(width));
      }
      double widthDouble = width.evalfNaN();
      double originDouble = origin.evalfNaN();
      if (Double.isNaN(widthDouble) || Double.isNaN(originDouble) || widthDouble <= 0.0) {
        return F.NIL;
      }
      int binCount =
          (int) Math.floor((numeric[numeric.length - 1] - originDouble) / widthDouble) + 1;
      if (binCount < 1 || binCount > Config.MAX_AST_SIZE) {
        return F.NIL;
      }
      IASTAppendable bins = F.ListAlloc(binCount + 1);
      for (int i = 0; i <= binCount; i++) {
        bins.append(engine.evaluate(F.Plus(origin, F.Times(F.ZZ(i), width))));
      }
      int[] counts = new int[binCount];
      for (int i = 0; i < numeric.length; i++) {
        int bin = (int) Math.floor((numeric[i] - originDouble) / widthDouble);
        if (bin < 0) {
          bin = 0;
        } else if (bin >= binCount) {
          bin = binCount - 1;
        }
        counts[bin]++;
      }
      int n = numeric.length;
      IASTAppendable densities = F.ListAlloc(binCount);
      for (int i = 0; i < binCount; i++) {
        // count/(n*width)
        densities.append(engine.evaluate(F.Divide(F.ZZ(counts[i]), F.Times(F.ZZ(n), width))));
      }
      return F.quaternary(S.DataDistribution, S.Histogram, F.list(densities, bins), F.C1, F.ZZ(n));
    }

    /**
     * The left edge of the first bin: the largest multiple of <code>width</code> not greater than
     * <code>min</code>. If <code>min</code> is itself such a multiple the bins are centered on the
     * data instead, which is computed numerically.
     */
    private static IExpr origin(IExpr min, IExpr width) {
      EvalEngine engine = EvalEngine.get();
      IExpr multiple = engine.evaluate(F.Floor(F.Divide(min, width)));
      IExpr candidate = engine.evaluate(F.Times(multiple, width));
      if (engine.evaluate(F.Equal(candidate, min)).isTrue()) {
        return engine.evaluate(F.N(F.Subtract(min, F.Divide(width, F.C2))));
      }
      return candidate;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.NOATTRIBUTE);
    }
  }

  /**
   * <code>DataDistribution(Empirical|Histogram, payload, 1, n)</code> - the distribution derived
   * from a data set by {@link S#EmpiricalDistribution} or {@link S#HistogramDistribution}.
   */
  public static final class DataDistribution extends AbstractEvaluator
      implements ICDF, IContinuousDistribution, IPDF, IStatistics, IRandomVariate {

    /** <code>{probabilities, values}</code> of an empirical distribution. */
    private static IAST[] empirical(IAST dist) {
      if (dist.argSize() == 4 && dist.arg1() == S.Empirical && dist.arg2().isList()
          && dist.arg2().size() >= 3 && dist.arg2().first().isList()
          && dist.arg2().second().isList()) {
        return new IAST[] {(IAST) dist.arg2().first(), (IAST) dist.arg2().second()};
      }
      return null;
    }

    /**
     * <code>Expectation(expr, Distributed(x, dist))</code> of an empirical distribution: the sum of
     * the values of <code>expr</code> at the data values, weighted with their probabilities.
     *
     * @return {@link F#NIL} if <code>dist</code> isn't an empirical distribution
     */
    public static IExpr empiricalExpectation(IAST dist, IExpr expr, IExpr x, EvalEngine engine) {
      IAST[] e = empirical(dist);
      if (e == null || e[0].size() != e[1].size()) {
        return F.NIL;
      }
      IAST probabilities = e[0];
      IAST values = e[1];
      IASTAppendable sum = F.PlusAlloc(values.argSize());
      for (int i = 1; i < values.size(); i++) {
        sum.append(F.Times(probabilities.get(i), F.subst(expr, x, values.get(i))));
      }
      return engine.evaluate(sum);
    }

    /** <code>{densities, binBoundaries}</code> of a histogram distribution. */
    private static IAST[] histogram(IAST dist) {
      if (dist.argSize() == 4 && dist.arg1() == S.Histogram && dist.arg2().isList2()
          && dist.arg2().first().isList() && dist.arg2().second().isList()) {
        return new IAST[] {(IAST) dist.arg2().first(), (IAST) dist.arg2().second()};
      }
      return null;
    }

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      IAST[] e = empirical(dist);
      if (e != null) {
        if (k.isNIL()) {
          return F.NIL;
        }
        IAST probabilities = e[0];
        IAST values = e[1];
        IASTAppendable sum = F.PlusAlloc(values.argSize());
        for (int i = 1; i < values.size(); i++) {
          IExpr lessEqual = engine.evaluate(F.LessEqual(values.get(i), k));
          if (!lessEqual.isTrue() && !lessEqual.isFalse()) {
            return F.NIL;
          }
          if (lessEqual.isTrue()) {
            sum.append(probabilities.get(i));
          }
        }
        return sum;
      }
      IAST[] h = histogram(dist);
      if (h != null && k.isPresent()) {
        IAST densities = h[0];
        IAST bins = h[1];
        IExpr cumulative = F.C0;
        IASTAppendable sum = F.PlusAlloc(densities.argSize() + 1);
        sum.append(F.Boole(F.GreaterEqual(k, bins.get(bins.size() - 1))));
        for (int i = 1; i < densities.size(); i++) {
          IExpr left = bins.get(i);
          IExpr inside = F.Boole(F.Inequality(left, S.LessEqual, k, S.Less, bins.get(i + 1)));
          sum.append(
              F.Times(F.Plus(cumulative, F.Times(densities.get(i), F.Subtract(k, left))), inside));
          cumulative = engine.evaluate(
              F.Plus(cumulative, F.Times(densities.get(i), F.Subtract(bins.get(i + 1), left))));
        }
        return sum;
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_4_4;
    }

    @Override
    public IExpr inverseCDF(IAST dist, IExpr k, EvalEngine engine) {
      return quantile(dist, k, engine);
    }

    @Override
    public IExpr mean(IAST dist) {
      EvalEngine engine = EvalEngine.get();
      IAST[] e = empirical(dist);
      if (e != null) {
        return engine.evaluate(weightedSum(e[0], e[1], 1));
      }
      IAST[] h = histogram(dist);
      if (h != null) {
        return engine.evaluate(histogramMoment(h[0], h[1], 1));
      }
      return F.NIL;
    }

    @Override
    public IExpr median(IAST dist) {
      return quantile(dist, F.C1D2, EvalEngine.get());
    }

    /** <code>Sum(p_i*v_i^exponent)</code> */
    private static IExpr weightedSum(IAST probabilities, IAST values, int exponent) {
      IASTAppendable sum = F.PlusAlloc(values.argSize());
      for (int i = 1; i < values.size(); i++) {
        sum.append(F.Times(probabilities.get(i), F.Power(values.get(i), F.ZZ(exponent))));
      }
      return sum;
    }

    /**
     * <code>Integrate(x^exponent*density)</code> over all bins, i.e.
     * <code>Sum(d_i*(b_(i+1)^(e+1) - b_i^(e+1))/(e+1))</code>.
     */
    private static IExpr histogramMoment(IAST densities, IAST bins, int exponent) {
      IExpr power = F.ZZ(exponent + 1);
      IASTAppendable sum = F.PlusAlloc(densities.argSize());
      for (int i = 1; i < densities.size(); i++) {
        sum.append(F.Times(densities.get(i), F.Divide(
            F.Subtract(F.Power(bins.get(i + 1), power), F.Power(bins.get(i), power)), power)));
      }
      return sum;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      IAST[] e = empirical(dist);
      if (e != null) {
        if (k.isNIL()) {
          return F.NIL;
        }
        IAST probabilities = e[0];
        IAST values = e[1];
        for (int i = 1; i < values.size(); i++) {
          IExpr equal = engine.evaluate(F.Equal(values.get(i), k));
          if (!equal.isTrue() && !equal.isFalse()) {
            return F.NIL;
          }
          if (equal.isTrue()) {
            return probabilities.get(i);
          }
        }
        return F.C0;
      }
      IAST[] h = histogram(dist);
      if (h != null && k.isPresent()) {
        IAST densities = h[0];
        IAST bins = h[1];
        IASTAppendable sum = F.PlusAlloc(densities.argSize());
        for (int i = 1; i < densities.size(); i++) {
          sum.append(F.Times(densities.get(i),
              F.Boole(F.Inequality(bins.get(i), S.LessEqual, k, S.Less, bins.get(i + 1)))));
        }
        return sum;
      }
      return F.NIL;
    }

    /** The smallest value whose cumulative probability reaches <code>q</code>. */
    private static IExpr quantile(IAST dist, IExpr q, EvalEngine engine) {
      if (q.isNIL()) {
        return F.NIL;
      }
      IAST[] h = histogram(dist);
      if (h != null) {
        return histogramQuantile(h[0], h[1], q, engine);
      }
      IAST[] e = empirical(dist);
      if (e == null) {
        return F.NIL;
      }
      IAST probabilities = e[0];
      IAST values = e[1];
      IExpr cumulative = F.C0;
      for (int i = 1; i < values.size(); i++) {
        cumulative = engine.evaluate(F.Plus(cumulative, probabilities.get(i)));
        IExpr reached = engine.evaluate(F.GreaterEqual(cumulative, q));
        if (!reached.isTrue() && !reached.isFalse()) {
          return F.NIL;
        }
        if (reached.isTrue()) {
          return values.get(i);
        }
      }
      return values.get(values.argSize());
    }

    /**
     * The inverse of the piecewise linear CDF of a histogram: the point of the first bin with
     * positive mass at which the accumulated mass reaches <code>q</code>.
     */
    private static IExpr histogramQuantile(IAST densities, IAST bins, IExpr q,
        EvalEngine engine) {
      if (!q.isReal()) {
        return F.NIL;
      }
      if (((IReal) q).isLT(F.C0) || ((IReal) q).isGT(F.C1)) {
        return S.Undefined;
      }
      IExpr cumulative = F.C0;
      for (int i = 1; i < densities.size(); i++) {
        IExpr density = densities.get(i);
        IExpr left = bins.get(i);
        IExpr mass = engine.evaluate(F.Times(density, F.Subtract(bins.get(i + 1), left)));
        if (!mass.isReal()) {
          return F.NIL;
        }
        if (!((IReal) mass).isPositive()) {
          continue;
        }
        IExpr next = engine.evaluate(F.Plus(cumulative, mass));
        if (!next.isReal()) {
          return F.NIL;
        }
        if (!((IReal) next).isLT((IReal) q)) {
          // left + (q - cumulative)/density
          return engine.evaluate(F.Plus(left, F.Divide(F.Subtract(q, cumulative), density)));
        }
        cumulative = next;
      }
      return bins.last();
    }

    @Override
    public IExpr randomVariate(java.util.Random random, IAST dist, int size) {
      IAST[] h = histogram(dist);
      if (h != null) {
        EvalEngine engine = EvalEngine.get();
        IASTAppendable result = F.ListAlloc(size);
        for (int s = 0; s < size; s++) {
          IExpr sample = histogramQuantile(h[0], h[1], F.num(random.nextDouble()), engine);
          if (!sample.isReal()) {
            return F.NIL;
          }
          result.append(sample);
        }
        return result;
      }
      IAST[] e = empirical(dist);
      if (e == null) {
        return F.NIL;
      }
      IAST probabilities = e[0];
      IAST values = e[1];
      IASTAppendable result = F.ListAlloc(size);
      for (int s = 0; s < size; s++) {
        double u = random.nextDouble();
        double cumulative = 0.0;
        IExpr sample = values.get(values.argSize());
        for (int i = 1; i < values.size(); i++) {
          cumulative += probabilities.get(i).evalfNaN();
          if (u < cumulative) {
            sample = values.get(i);
            break;
          }
        }
        result.append(sample);
      }
      return size == 1 ? result.arg1() : result;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

    @Override
    public IExpr skewness(IAST dist) {
      return F.NIL;
    }

    @Override
    public IExpr survivalFunction(IAST dist, IExpr k, EvalEngine engine) {
      IExpr cdf = cdf(dist, k, engine);
      return cdf.isPresent() ? engine.evaluate(F.Subtract(F.C1, cdf)) : F.NIL;
    }

    @Override
    public IExpr variance(IAST dist) {
      EvalEngine engine = EvalEngine.get();
      IAST[] e = empirical(dist);
      if (e != null) {
        IExpr mean = engine.evaluate(weightedSum(e[0], e[1], 1));
        return engine.evaluate(F.Subtract(weightedSum(e[0], e[1], 2), F.Sqr(mean)));
      }
      IAST[] h = histogram(dist);
      if (h != null) {
        IExpr mean = engine.evaluate(histogramMoment(h[0], h[1], 1));
        return engine.evaluate(F.Subtract(histogramMoment(h[0], h[1], 2), F.Sqr(mean)));
      }
      return F.NIL;
    }
  }

  /**
   * <code>SmoothKernelDistribution(data)</code>, <code>SmoothKernelDistribution(data, bw)</code>
   * or <code>SmoothKernelDistribution(data, bw, kernel)</code> - the kernel density estimate of the
   * one dimensional <code>data</code>: the mixture with equal weights of the kernel, scaled to the
   * standard deviation <code>bw</code>, centered at each data point.
   *
   * <p>
   * The kernel is <code>"Gaussian"</code> (default), <code>"Epanechnikov"</code>,
   * <code>"Biweight"</code>, <code>"Triweight"</code>, <code>"Triangular"</code>,
   * <code>"Rectangular"</code>, <code>"Cosine"</code> or <code>"SemiCircle"</code>. The bandwidth
   * <code>Automatic</code> or <code>"Silverman"</code> is Silverman's rule
   * <code>0.9*Min(sigma, IQR/1.34)*n^(-1/5)</code>, with the sample standard deviation and the
   * interquartile range of the linearly interpolated quartiles. The distribution functions are
   * computed numerically.
   */
  public static final class SmoothKernelDistribution extends AbstractEvaluator
      implements ICDF, IContinuousDistribution, IPDF, IStatistics, IRandomVariate {

    /** A kernel with variance <code>1</code>. */
    private enum Kernel {
      GAUSSIAN(Double.POSITIVE_INFINITY), //
      EPANECHNIKOV(Math.sqrt(5.0)), //
      BIWEIGHT(Math.sqrt(7.0)), //
      TRIWEIGHT(3.0), //
      TRIANGULAR(Math.sqrt(6.0)), //
      RECTANGULAR(Math.sqrt(3.0)), //
      COSINE(1.0 / Math.sqrt(1.0 - 8.0 / (Math.PI * Math.PI))), //
      SEMICIRCLE(2.0);

      /** the half width <code>a</code> of the support <code>[-a, a]</code> */
      final double radius;

      Kernel(double radius) {
        this.radius = radius;
      }

      /** The density at <code>u</code>. */
      double pdf(double u) {
        if (this == GAUSSIAN) {
          return Math.exp(-0.5 * u * u) / Math.sqrt(2.0 * Math.PI);
        }
        double t = u / radius;
        if (t <= -1.0 || t >= 1.0) {
          return 0.0;
        }
        double s = 1.0 - t * t;
        double density;
        switch (this) {
          case EPANECHNIKOV:
            density = 0.75 * s;
            break;
          case BIWEIGHT:
            density = 15.0 / 16.0 * s * s;
            break;
          case TRIWEIGHT:
            density = 35.0 / 32.0 * s * s * s;
            break;
          case TRIANGULAR:
            density = 1.0 - Math.abs(t);
            break;
          case RECTANGULAR:
            density = 0.5;
            break;
          case COSINE:
            density = Math.PI / 4.0 * Math.cos(Math.PI * t / 2.0);
            break;
          default:
            // SEMICIRCLE
            density = 2.0 / Math.PI * Math.sqrt(s);
            break;
        }
        return density / radius;
      }

      /** The distribution function at <code>u</code>. */
      double cdf(double u) {
        if (this == GAUSSIAN) {
          return 0.5 * org.hipparchus.special.Erf.erfc(-u / Math.sqrt(2.0));
        }
        double t = u / radius;
        if (t <= -1.0) {
          return 0.0;
        }
        if (t >= 1.0) {
          return 1.0;
        }
        double t2 = t * t;
        switch (this) {
          case EPANECHNIKOV:
            return (2.0 + 3.0 * t - t * t2) / 4.0;
          case BIWEIGHT:
            return 0.5 + 15.0 / 16.0 * t * (1.0 - 2.0 * t2 / 3.0 + t2 * t2 / 5.0);
          case TRIWEIGHT:
            return 0.5 + 35.0 / 32.0 * t * (1.0 - t2 + 3.0 * t2 * t2 / 5.0 - t2 * t2 * t2 / 7.0);
          case TRIANGULAR:
            return t < 0.0 ? 0.5 * (1.0 + t) * (1.0 + t) : 1.0 - 0.5 * (1.0 - t) * (1.0 - t);
          case RECTANGULAR:
            return 0.5 * (1.0 + t);
          case COSINE:
            return 0.5 * (1.0 + Math.sin(Math.PI * t / 2.0));
          default:
            // SEMICIRCLE
            return 0.5 + (t * Math.sqrt(1.0 - t2) + Math.asin(t)) / Math.PI;
        }
      }

      /** A sample of the kernel. */
      double sample(java.util.Random random) {
        if (this == GAUSSIAN) {
          return random.nextGaussian();
        }
        // invert the distribution function by bisection
        double p = random.nextDouble();
        double low = -radius;
        double high = radius;
        for (int i = 0; i < 60; i++) {
          double middle = 0.5 * (low + high);
          if (cdf(middle) < p) {
            low = middle;
          } else {
            high = middle;
          }
        }
        return 0.5 * (low + high);
      }

      static Kernel of(IExpr name) {
        if (!name.isString()) {
          return null;
        }
        for (Kernel kernel : values()) {
          if (kernel.name().equalsIgnoreCase(name.toString())) {
            return kernel;
          }
        }
        return null;
      }
    }

    /** The data, the bandwidth and the kernel of a smooth kernel distribution. */
    private static final class Estimate {
      final double[] data;
      final double bandwidth;
      final Kernel kernel;

      Estimate(double[] data, double bandwidth, Kernel kernel) {
        this.data = data;
        this.bandwidth = bandwidth;
        this.kernel = kernel;
      }

      double pdf(double x) {
        double sum = 0.0;
        for (double v : data) {
          sum += kernel.pdf((x - v) / bandwidth);
        }
        return sum / (data.length * bandwidth);
      }

      double cdf(double x) {
        double sum = 0.0;
        for (double v : data) {
          sum += kernel.cdf((x - v) / bandwidth);
        }
        return sum / data.length;
      }

      double mean() {
        double sum = 0.0;
        for (double v : data) {
          sum += v;
        }
        return sum / data.length;
      }

      /** The central moment of the data points of the given order. */
      double dataMoment(int order) {
        double mean = mean();
        double sum = 0.0;
        for (double v : data) {
          sum += Math.pow(v - mean, order);
        }
        return sum / data.length;
      }

      double variance() {
        // the kernel has the variance bandwidth^2 and is independent of the data point
        return dataMoment(2) + bandwidth * bandwidth;
      }

      /** The inverse of the distribution function, by bisection. */
      double quantile(double q) {
        double width = kernel == Kernel.GAUSSIAN ? 40.0 : kernel.radius;
        double low = data[0] - width * bandwidth;
        double high = data[data.length - 1] + width * bandwidth;
        for (int i = 0; i < 200; i++) {
          double middle = 0.5 * (low + high);
          if (middle <= low || middle >= high) {
            break;
          }
          if (cdf(middle) < q) {
            low = middle;
          } else {
            high = middle;
          }
        }
        return 0.5 * (low + high);
      }
    }

    /**
     * The estimate of a distribution <code>SmoothKernelDistribution(data, bw, kernel)</code>.
     *
     * @return <code>null</code> if the arguments are not supported
     */
    private static Estimate estimate(IAST dist) {
      if (dist.argSize() < 1 || dist.argSize() > 3 || !dist.arg1().isList()
          || dist.arg1().argSize() < 1) {
        return null;
      }
      IAST list = (IAST) dist.arg1();
      double[] data = new double[list.argSize()];
      for (int i = 1; i < list.size(); i++) {
        if (!list.get(i).isReal()) {
          return null;
        }
        data[i - 1] = list.get(i).evalf();
        if (!Double.isFinite(data[i - 1])) {
          return null;
        }
      }
      Arrays.sort(data);
      Kernel kernel = Kernel.GAUSSIAN;
      if (dist.argSize() == 3) {
        kernel = Kernel.of(dist.arg3());
        if (kernel == null) {
          return null;
        }
      }
      double bandwidth;
      IExpr bw = dist.argSize() >= 2 ? dist.arg2() : S.Automatic;
      if (bw == S.Automatic || bw.isString("Silverman")) {
        bandwidth = silverman(data);
      } else if (bw.isReal()) {
        bandwidth = bw.evalf();
      } else {
        return null;
      }
      if (!(bandwidth > 0.0) || !Double.isFinite(bandwidth)) {
        return null;
      }
      return new Estimate(data, bandwidth, kernel);
    }

    /**
     * Silverman's rule <code>0.9*Min(sigma, IQR/1.34)*n^(-1/5)</code> for the sorted
     * <code>data</code>; a vanishing interquartile range falls back to <code>sigma</code>.
     */
    private static double silverman(double[] data) {
      int n = data.length;
      if (n < 2) {
        return Double.NaN;
      }
      double mean = 0.0;
      for (double v : data) {
        mean += v;
      }
      mean /= n;
      double squares = 0.0;
      for (double v : data) {
        squares += (v - mean) * (v - mean);
      }
      double sigma = Math.sqrt(squares / (n - 1));
      double iqr = (quartile(data, 0.75) - quartile(data, 0.25)) / 1.34;
      double spread = iqr > 0.0 ? Math.min(sigma, iqr) : sigma;
      return 0.9 * spread * Math.pow(n, -0.2);
    }

    /** The quantile with linear interpolation between the order statistics of sorted data. */
    private static double quartile(double[] data, double p) {
      double position = p * (data.length - 1);
      int below = (int) Math.floor(position);
      int above = Math.min(below + 1, data.length - 1);
      return data[below] + (position - below) * (data[above] - data[below]);
    }

    /** Apply <code>function</code> to a real number <code>x</code> or to the elements of a list. */
    private static IExpr map(IExpr x, java.util.function.DoubleUnaryOperator function) {
      if (x.isList()) {
        IAST list = (IAST) x;
        IASTAppendable result = F.ListAlloc(list.argSize());
        for (int i = 1; i < list.size(); i++) {
          IExpr value = map(list.get(i), function);
          if (value.isNIL()) {
            return F.NIL;
          }
          result.append(value);
        }
        return result;
      }
      if (x.isReal()) {
        return F.num(function.applyAsDouble(x.evalf()));
      }
      if (x.isInfinity()) {
        return F.num(function.applyAsDouble(Double.POSITIVE_INFINITY));
      }
      if (x.isNegativeInfinity()) {
        return F.num(function.applyAsDouble(Double.NEGATIVE_INFINITY));
      }
      return F.NIL;
    }

    @Override
    public IExpr cdf(IAST dist, IExpr x, EvalEngine engine) {
      Estimate estimate = estimate(dist);
      if (estimate == null || x.isNIL()) {
        return F.NIL;
      }
      return map(x, estimate::cdf);
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    @Override
    public IExpr inverseCDF(IAST dist, IExpr q, EvalEngine engine) {
      Estimate estimate = estimate(dist);
      if (estimate == null || !q.isReal()) {
        return F.NIL;
      }
      double p = q.evalf();
      if (p < 0.0 || p > 1.0) {
        return F.NIL;
      }
      if (p == 0.0) {
        return estimate.kernel == Kernel.GAUSSIAN ? F.CNInfinity
            : F.num(estimate.data[0] - estimate.kernel.radius * estimate.bandwidth);
      }
      if (p == 1.0) {
        return estimate.kernel == Kernel.GAUSSIAN ? F.CInfinity
            : F.num(estimate.data[estimate.data.length - 1]
                + estimate.kernel.radius * estimate.bandwidth);
      }
      return F.num(estimate.quantile(p));
    }

    @Override
    public IExpr mean(IAST dist) {
      Estimate estimate = estimate(dist);
      return estimate == null ? F.NIL : F.num(estimate.mean());
    }

    @Override
    public IExpr median(IAST dist) {
      Estimate estimate = estimate(dist);
      return estimate == null ? F.NIL : F.num(estimate.quantile(0.5));
    }

    @Override
    public IExpr pdf(IAST dist, IExpr x, EvalEngine engine) {
      Estimate estimate = estimate(dist);
      if (estimate == null || x.isNIL()) {
        return F.NIL;
      }
      return map(x, estimate::pdf);
    }

    @Override
    public IExpr randomVariate(java.util.Random random, IAST dist, int size) {
      Estimate estimate = estimate(dist);
      if (estimate == null) {
        return F.NIL;
      }
      IASTAppendable result = F.ListAlloc(size);
      for (int i = 0; i < size; i++) {
        double center = estimate.data[random.nextInt(estimate.data.length)];
        result.append(F.num(center + estimate.bandwidth * estimate.kernel.sample(random)));
      }
      return result;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

    @Override
    public IExpr skewness(IAST dist) {
      Estimate estimate = estimate(dist);
      if (estimate == null) {
        return F.NIL;
      }
      // a symmetric kernel adds nothing to the third central moment of the data points
      return F.num(estimate.dataMoment(3) / Math.pow(estimate.variance(), 1.5));
    }

    @Override
    public IExpr variance(IAST dist) {
      Estimate estimate = estimate(dist);
      return estimate == null ? F.NIL : F.num(estimate.variance());
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  /**
   * <code>MatrixPropertyDistribution(expr, x \[Distributed] mdist)</code> - the distribution of
   * the property <code>expr</code> of a random matrix <code>x</code> drawn from
   * <code>mdist</code>, e.g. of <code>Eigenvalues(x)</code>.
   *
   * <p>
   * Several random variables are given as a list of <code>Distributed(x, dist)</code>
   * specifications. The property is held and evaluated for each sample only, after the random
   * variables have been replaced by their sampled values.
   */
  public static final class MatrixPropertyDistribution extends AbstractEvaluator
      implements IDistribution, IRandomVariate {

    /**
     * The <code>Distributed(x, dist)</code> specifications of the distribution, or
     * <code>null</code> if the second argument isn't one or a list of them with symbols as
     * random variables.
     */
    private static IAST specifications(IAST dist) {
      if (!dist.isAST2()) {
        return null;
      }
      IExpr arg2 = dist.arg2();
      IAST specs;
      if (arg2.isAST(S.Distributed, 3)) {
        specs = F.list(arg2);
      } else if (arg2.isList() && arg2.argSize() > 0) {
        specs = (IAST) arg2;
      } else {
        return null;
      }
      if (!specs.forAll(spec -> spec.isAST(S.Distributed, 3) && spec.first().isSymbol()
          && spec.second().isAST())) {
        return null;
      }
      return specs;
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
      return F.NIL;
    }

    @Override
    public IExpr median(IAST dist) {
      return F.NIL;
    }

    @Override
    public IExpr randomVariate(Random random, IAST dist, int size) {
      IAST specs = specifications(dist);
      if (specs == null) {
        return F.NIL;
      }
      EvalEngine engine = EvalEngine.get();
      // draw all samples of each random variable at once
      IAST[] samples = new IAST[specs.size()];
      for (int i = 1; i < specs.size(); i++) {
        IExpr sample = engine.evaluate(F.RandomVariate(specs.get(i).second(), F.ZZ(size)));
        if (!sample.isList() || sample.argSize() != size) {
          return F.NIL;
        }
        samples[i] = (IAST) sample;
      }
      IExpr property = dist.arg1();
      IASTAppendable result = F.ListAlloc(size);
      Map<IExpr, IExpr> values = new HashMap<IExpr, IExpr>();
      for (int k = 1; k <= size; k++) {
        for (int i = 1; i < specs.size(); i++) {
          values.put(specs.get(i).first(), samples[i].get(k));
        }
        result.append(engine.evaluate(F.subst(property, values)));
      }
      return result;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      // the property is evaluated for the sampled values only, and the random variables stay
      // symbols even if they have a value
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  private StatisticsDerivedDistributions() {}

}
