package org.matheclipse.core.builtin;

import java.util.Arrays;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.interfaces.statistics.ICDF;
import org.matheclipse.core.interfaces.statistics.IContinuousDistribution;
import org.matheclipse.core.interfaces.statistics.IDistribution;
import org.matheclipse.core.interfaces.statistics.IPDF;
import org.matheclipse.core.interfaces.statistics.IRandomVariate;
import org.matheclipse.core.interfaces.statistics.IStatistics;

/**
 * Distributions which are derived from other distributions or from an explicit density:
 * {@link S#ProbabilityDistribution}, {@link S#TransformedDistribution},
 * {@link S#MixtureDistribution}, {@link S#ParameterMixtureDistribution},
 * {@link S#ProductDistribution} and {@link S#CensoredDistribution}.
 */
public class StatisticsDerivedDistributions {

  private static class Initializer {

    private static void init() {
      S.CensoredDistribution.setEvaluator(new CensoredDistribution());
      S.MixtureDistribution.setEvaluator(new MixtureDistribution());
      S.ParameterMixtureDistribution.setEvaluator(new ParameterMixtureDistribution());
      S.ProbabilityDistribution.setEvaluator(new ProbabilityDistribution());
      S.ProductDistribution.setEvaluator(new ProductDistribution());
      S.TransformedDistribution.setEvaluator(new TransformedDistribution());
      S.DataDistribution.setEvaluator(new DataDistribution());
      S.EmpiricalDistribution.setEvaluator(new EmpiricalDistribution());
      S.HistogramDistribution.setEvaluator(new HistogramDistribution());
    }
  }

  /**
   * <code>ProbabilityDistribution(pdf, {x, xmin, xmax})</code> - a distribution defined by an
   * explicit probability density function.
   */
  public static final class ProbabilityDistribution extends AbstractEvaluator
      implements ICDF, IContinuousDistribution, IPDF, IStatistics {

    /** The iterator specification <code>{x, xmin, xmax}</code> of the (first) variable. */
    private static IAST iterator(IAST dist) {
      if (dist.size() >= 3 && dist.arg2().isList3()) {
        return (IAST) dist.arg2();
      }
      return null;
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
   * <code>CensoredDistribution({xmin, xmax}, dist)</code> - the values of <code>dist</code>
   * censored to lie between <code>xmin</code> and <code>xmax</code>.
   */
  public static final class CensoredDistribution extends AbstractEvaluator
      implements IContinuousDistribution, IStatistics {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST2() && ast.arg1().isList2() && ast.arg2().isAST()) {
        // canonicalize the inner distribution, e.g. NormalDistribution() -> NormalDistribution(0,1)
        IExpr distribution = engine.evaluate(ast.arg2());
        IDistribution evaluator =
            distribution.isAST() ? ((IAST) distribution).headInstanceOf(IDistribution.class) : null;
        if (evaluator != null) {
          IAST checked = evaluator.checkParameters((IAST) distribution);
          if (checked.isPresent() && !checked.equals(ast.arg2())) {
            return F.binaryAST2(S.CensoredDistribution, ast.arg1(), checked);
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
   * <code>EmpiricalDistribution({x1, x2, ...})</code> - the discrete distribution which assigns
   * each distinct data value its relative frequency. It evaluates to a {@link S#DataDistribution}.
   */
  public static final class EmpiricalDistribution extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.isAST1() || !ast.arg1().isList() || ast.arg1().size() < 2) {
        return F.NIL;
      }
      IAST data = (IAST) ast.arg1();
      IExpr[] values = new IExpr[data.argSize()];
      for (int i = 1; i < data.size(); i++) {
        if (!data.get(i).isReal()) {
          return F.NIL;
        }
        values[i - 1] = data.get(i);
      }
      Arrays.sort(values, (a, b) -> a.compareTo(b));

      IASTAppendable distinct = F.ListAlloc(values.length);
      IASTAppendable probabilities = F.ListAlloc(values.length);
      int n = values.length;
      int i = 0;
      while (i < n) {
        int count = 1;
        while (i + count < n && values[i + count].equals(values[i])) {
          count++;
        }
        distinct.append(values[i]);
        probabilities.append(F.QQ(count, n));
        i += count;
      }
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
        width = F.num(niceWidth(numeric));
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

    /** Sturges' rule, rounded up to a "nice" bin width of the form 1, 2, 2.5 or 5 times 10^k. */
    private static double niceWidth(double[] sorted) {
      double range = sorted[sorted.length - 1] - sorted[0];
      if (range <= 0.0) {
        return 1.0;
      }
      int bins = (int) Math.ceil(Math.log(sorted.length) / Math.log(2.0)) + 1;
      double raw = range / bins;
      double magnitude = Math.pow(10.0, Math.floor(Math.log10(raw)));
      for (double factor : new double[] {1.0, 2.0, 2.5, 5.0, 10.0}) {
        if (factor * magnitude >= raw) {
          return factor * magnitude;
        }
      }
      return 10.0 * magnitude;
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
      IAST[] e = empirical(dist);
      if (e == null || q.isNIL()) {
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

    @Override
    public IExpr randomVariate(java.util.Random random, IAST dist, int size) {
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

  public static void initialize() {
    Initializer.init();
  }

  private StatisticsDerivedDistributions() {}
}
