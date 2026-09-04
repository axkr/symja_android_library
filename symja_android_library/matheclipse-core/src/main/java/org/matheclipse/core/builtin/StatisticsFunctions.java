package org.matheclipse.core.builtin;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.function.DoubleUnaryOperator;
import java.util.function.LongPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.hipparchus.distribution.IntegerDistribution;
import org.hipparchus.distribution.RealDistribution;
import org.hipparchus.exception.MathRuntimeException;
import org.hipparchus.linear.Array2DRowRealMatrix;
import org.hipparchus.linear.FieldMatrix;
import org.hipparchus.linear.RealMatrix;
import org.hipparchus.stat.StatUtils;
import org.hipparchus.stat.correlation.PearsonsCorrelation;
import org.hipparchus.stat.descriptive.DescriptiveStatistics;
import org.hipparchus.stat.projection.PCA;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.LinearAlgebraUtil;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractMatrix1Expr;
import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.expression.ASTRealMatrix;
import org.matheclipse.core.expression.ASTRealVector;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.IntervalDataSym;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IASTDataset;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInexactNumber;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.interfaces.statistics.ICDF;
import org.matheclipse.core.interfaces.statistics.ICentralMoment;
import org.matheclipse.core.interfaces.statistics.ICovariance;
import org.matheclipse.core.interfaces.statistics.IDiscreteDistribution;
import org.matheclipse.core.interfaces.statistics.IDistribution;
import org.matheclipse.core.interfaces.statistics.IPDF;
import org.matheclipse.core.interfaces.statistics.IRandomVariate;
import org.matheclipse.core.interfaces.statistics.IStatistics;
import org.matheclipse.core.reflection.system.NSum;
import org.matheclipse.external.fastutil.ints.IntArrayList;
import org.matheclipse.external.fastutil.ints.IntList;

public class StatisticsFunctions {

  // avoid result -Infinity when reference is close to 1.0
  static final double NEXTDOWNONE = Math.nextDown(1.0);

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.AbsoluteCorrelation.setEvaluator(new AbsoluteCorrelation());
      S.ArithmeticGeometricMean.setEvaluator(new ArithmeticGeometricMean());
      S.CDF.setEvaluator(new CDF());
      S.PDF.setEvaluator(new PDF());
      S.BernoulliProcess.setEvaluator(new BernoulliProcess());
      S.BinCounts.setEvaluator(new BinCounts());
      S.BinomialProcess.setEvaluator(new BinomialProcess());
      S.BrownianBridgeProcess.setEvaluator(new BrownianBridgeProcess());
      S.Correlation.setEvaluator(new Correlation());
      S.Covariance.setEvaluator(new Covariance());
      S.DistributionParameterQ.setEvaluator(new DistributionParameterQ());
      S.FindDistributionParameters.setEvaluator(new FindDistributionParameters());
      S.Expectation.setEvaluator(new Expectation());
      S.HazardFunction.setEvaluator(new HazardFunction());
      S.InverseSurvivalFunction.setEvaluator(new InverseSurvivalFunction());
      S.MedianDeviation.setEvaluator(new MedianDeviation());
      S.FiveNum.setEvaluator(new FiveNum());
      S.GeometricMean.setEvaluator(new GeometricMean());
      S.HarmonicMean.setEvaluator(new HarmonicMean());
      S.InterquartileRange.setEvaluator(new InterquartileRange());
      S.InverseCDF.setEvaluator(new InverseCDF());
      S.KolmogorovSmirnovTest.setEvaluator(new KolmogorovSmirnovTest());
      S.Kurtosis.setEvaluator(new Kurtosis());
      S.Mean.setEvaluator(new Mean());
      S.MeanDeviation.setEvaluator(new MeanDeviation());
      S.Median.setEvaluator(new Median());
      S.NExpectation.setEvaluator(new NExpectation());
      S.NProbability.setEvaluator(new NProbability());
      S.PearsonCorrelationTest.setEvaluator(new PearsonCorrelationTest());
      S.PoissonProcess.setEvaluator(new PoissonProcess());
      S.PrincipalComponents.setEvaluator(new PrincipalComponents());
      S.Probability.setEvaluator(new Probability());
      S.Quantile.setEvaluator(new Quantile());
      S.Quartiles.setEvaluator(new Quartiles());
      S.RandomVariate.setEvaluator(new RandomVariate());
      S.Rescale.setEvaluator(new Rescale());
      S.RootMeanSquare.setEvaluator(new RootMeanSquare());
      S.Skewness.setEvaluator(new Skewness());
      S.StandardDeviation.setEvaluator(new StandardDeviation());
      S.Standardize.setEvaluator(new Standardize());
      S.SurvivalFunction.setEvaluator(new SurvivalFunction());
      S.TTest.setEvaluator(new TTest());
      S.Variance.setEvaluator(new Variance());
    }
  }

  public static IDistribution getDistribution(final IExpr arg1) {
    return (IDistribution) ((IBuiltInSymbol) arg1.head()).getEvaluator();
  }

  public static IDiscreteDistribution getDiscreteDistribution(final IExpr arg1) {
    return (IDiscreteDistribution) ((IBuiltInSymbol) arg1.head()).getEvaluator();
  }

  /** Test for a non-empty list whose elements are all <code>Distributed(x, dist)</code>. */
  private static boolean isDistributedList(IExpr expr) {
    if (expr.isList() && expr.argSize() > 0) {
      return ((IAST) expr).forAll(e -> e.isAST(S.Distributed, 3));
    }
    return false;
  }

  /**
   * Conditional expectation <code>E(expr | pred) == E(expr * Boole(pred)) / P(pred)</code>,
   * computed with the given expectation and probability heads (symbolic or numeric variants).
   */
  private static IExpr conditionedExpectation(IBuiltInSymbol expectationHead,
      IBuiltInSymbol probabilityHead, IAST conditioned, IExpr distributed, EvalEngine engine) {
    IExpr expr = conditioned.arg1();
    IExpr predicate = conditioned.arg2();
    IExpr numerator = engine
        .evaluate(F.binaryAST2(expectationHead, F.Times(expr, F.Boole(predicate)), distributed));
    if (!numerator.isFree(expectationHead, true) || !numerator.isFree(S.Integrate, true)
        || !numerator.isFree(S.NIntegrate, true)) {
      return F.NIL;
    }
    IExpr denominator = engine.evaluate(F.binaryAST2(probabilityHead, predicate, distributed));
    if (!denominator.isFree(probabilityHead, true) || denominator.isZero()) {
      return F.NIL;
    }
    return F.Divide(numerator, denominator);
  }

  /**
   * If <code>expr</code> is a <code>Boole(pred)</code> factor (or contains one as a
   * <code>Times</code> factor) whose predicate converts into an interval region of <code>x</code>,
   * return <code>{remainingFactor, region}</code>, otherwise <code>null</code>.
   */
  private static IExpr[] extractBooleFactor(IExpr expr, IExpr x) {
    if (expr.isAST(S.Boole, 2)) {
      IAST region = DistributionRegion.regionFromPredicate(expr.first(), x);
      if (region.isPresent()) {
        return new IExpr[] {F.C1, region};
      }
    } else if (expr.isTimes()) {
      IAST times = (IAST) expr;
      for (int i = 1; i < times.size(); i++) {
        IExpr factor = times.get(i);
        if (factor.isAST(S.Boole, 2)) {
          IAST region = DistributionRegion.regionFromPredicate(factor.first(), x);
          if (region.isPresent()) {
            IASTAppendable rest = F.TimesAlloc(times.argSize());
            for (int j = 1; j < times.size(); j++) {
              if (j != i) {
                rest.append(times.get(j));
              }
            }
            return new IExpr[] {rest.oneIdentity1(), region};
          }
        }
      }
    }
    return null;
  }

  /**
   * <code>Integrate(integrand, {x, min_i, max_i})</code> summed over all pieces of the region.
   *
   * @param integrateHead {@link S#Integrate} or {@link S#NIntegrate}
   * @return {@link F#NIL} if an integral does not evaluate
   */
  private static IExpr integrateOverRegion(IExpr integrand, IExpr x, IAST region,
      IBuiltInSymbol integrateHead, EvalEngine engine) {
    IASTAppendable sum = F.PlusAlloc(region.argSize() + 1);
    for (int i = 1; i < region.size(); i++) {
      IAST piece = (IAST) region.get(i);
      if (piece.arg1().equals(piece.arg4())) {
        // a point has measure zero
        continue;
      }
      sum.append(F.binaryAST2(integrateHead, integrand, F.list(x, piece.arg1(), piece.arg4())));
    }
    IExpr result = engine.evaluate(sum);
    return result.isFree(integrateHead, true) ? result : F.NIL;
  }

  /**
   * The branch list <code>{{value1, cond1}, {value2, cond2}, ...}</code> of a piecewise function
   * whose default value is <code>0</code>.
   *
   * @return <code>null</code> if <code>piecewise</code> has another structure
   */
  private static IAST piecewiseZeroDefaultBranches(IExpr piecewise) {
    if (!piecewise.isAST(S.Piecewise) || piecewise.size() < 2
        || !((IAST) piecewise).arg1().isListOfLists()) {
      return null;
    }
    IAST piecewiseAST = (IAST) piecewise;
    if (piecewiseAST.argSize() >= 2 && !piecewiseAST.arg2().isZero()) {
      return null;
    }
    return (IAST) piecewiseAST.arg1();
  }

  /**
   * <code>Sum(factor*branchValue, {x, lo, hi})</code> over the integer windows on which the
   * branches of a piecewise density are valid, intersected with <code>windows</code>. Unwrapping
   * the {@link S#Piecewise} enables closed form summation of exponential generating series like
   * <code>Expectation(E^(2*x), x \[Distributed] PoissonDistribution(l))</code>, which {@link S#Sum}
   * cannot see through the piecewise.
   *
   * @param windows the summation windows (usually the distribution support, possibly restricted by
   *        a <code>Boole</code> factor)
   * @return {@link F#NIL} if the piecewise cannot be decomposed or the sum does not evaluate to a
   *         closed form
   */
  private static IExpr sumProductOverPiecewise(IExpr factor, IExpr piecewisePdf, IExpr x,
      long[] windows, EvalEngine engine) {
    IAST branches = piecewiseZeroDefaultBranches(piecewisePdf);
    if (branches == null) {
      return F.NIL;
    }
    IASTAppendable sum = F.PlusAlloc(branches.argSize() + 1);
    for (int i = 1; i < branches.size(); i++) {
      IAST branch = (IAST) branches.get(i);
      if (branch.size() != 3) {
        return F.NIL;
      }
      IAST region = DistributionRegion.regionFromPredicate(branch.arg2(), x);
      if (region.isNIL()) {
        return F.NIL;
      }
      long[] branchWindows = DistributionRegion.integerWindows(region, engine);
      if (branchWindows == null) {
        return F.NIL;
      }
      branchWindows = DistributionRegion.intersectWindows(branchWindows, windows);
      // Sum does not thread over a Plus summand on an infinite range - expand and emit one
      // Sum per additive term
      IExpr expanded = engine.evaluate(F.Expand(F.Times(factor, branch.arg1())));
      IAST terms = expanded.isPlus() ? (IAST) expanded : F.List(expanded);
      for (int w = 0; w < branchWindows.length; w += 2) {
        IExpr lower = branchWindows[w] == Long.MIN_VALUE ? F.CNInfinity : F.ZZ(branchWindows[w]);
        IExpr upper =
            branchWindows[w + 1] == Long.MAX_VALUE ? F.CInfinity : F.ZZ(branchWindows[w + 1]);
        for (int t = 1; t < terms.size(); t++) {
          sum.append(F.Sum(terms.get(t), F.List(x, lower, upper)));
        }
      }
    }
    IExpr result = engine.evaluate(sum);
    if (result.isFree(S.Sum, true) && result.isFree(S.Boole, true)) {
      return result;
    }
    return F.NIL;
  }

  /** Evaluate <code>expr</code> with the numeric mode switched off. */
  private static IExpr evaluateNonNumeric(IExpr expr, EvalEngine engine) {
    boolean numericMode = engine.isNumericMode();
    try {
      engine.setNumericMode(false);
      return engine.evaluate(expr);
    } finally {
      engine.setNumericMode(numericMode);
    }
  }

  /**
   * The number of samples for <code>Method -&gt; "MonteCarlo"</code> or
   * <code>Method -&gt; {"MonteCarlo", n}</code>.
   *
   * @param options the options determined by the evaluator; <code>options[0]</code> is the
   *        <code>Method</code> value
   * @return <code>-1</code> if the monte carlo method was not requested
   */
  private static int monteCarloSamples(IExpr[] options) {
    if (options != null && options.length > 0 && options[0] != null && options[0].isPresent()) {
      IExpr method = options[0];
      if (method.isString() && method.toString().equals("MonteCarlo")) {
        return 10000;
      }
      if (method.isList() && method.argSize() >= 1 && method.first().isString()
          && method.first().toString().equals("MonteCarlo")) {
        if (method.argSize() >= 2) {
          int n = method.second().toIntDefault();
          if (n > 0) {
            return Math.min(n, 10_000_000);
          }
        }
        return 10000;
      }
    }
    return -1;
  }

  /** Monte carlo estimate of <code>E[function(x)]</code> based on {@link S#RandomVariate}. */
  private static IExpr monteCarloExpectation(IExpr function, IExpr x, IExpr distribution,
      int samples, EvalEngine engine) {
    IExpr list = engine.evaluate(F.RandomVariate(distribution, F.ZZ(samples)));
    if (list.isList() && list.argSize() == samples) {
      DoubleUnaryOperator compiled = DistributionRegion.compile(function, x, engine);
      if (compiled == null) {
        return F.NIL;
      }
      IAST values = (IAST) list;
      double sum = 0.0;
      double compensation = 0.0;
      for (int i = 1; i < values.size(); i++) {
        double sample = values.get(i).evalfNaN();
        double term = compiled.applyAsDouble(sample);
        if (!Double.isFinite(term)) {
          return F.NIL;
        }
        double y = term - compensation;
        double t = sum + y;
        compensation = (t - sum) - y;
        sum = t;
      }
      return F.num(sum / samples);
    }
    return F.NIL;
  }

  /** Monte carlo estimate of <code>P(predicate)</code> based on {@link S#RandomVariate}. */
  private static IExpr monteCarloProbability(IExpr predicate, IExpr x, IExpr distribution,
      int samples, EvalEngine engine) {
    IExpr list = engine.evaluate(F.RandomVariate(distribution, F.ZZ(samples)));
    if (list.isList() && list.argSize() == samples) {
      IAST values = (IAST) list;
      int trueCounter = 0;
      for (int i = 1; i < values.size(); i++) {
        if (engine.evalTrue(F.subst(predicate, x, values.get(i)))) {
          trueCounter++;
        }
      }
      return F.num(((double) trueCounter) / samples);
    }
    return F.NIL;
  }

  /**
   * Integrate <code>factor * piecewise</code> over the validity regions of the piecewise branches
   * (optionally intersected with an additional region from a <code>Boole</code> factor). The
   * default value of the piecewise function must be <code>0</code>.
   *
   * @param integrateHead {@link S#Integrate} or {@link S#NIntegrate}
   * @return {@link F#NIL} if the piecewise cannot be decomposed or an integral does not evaluate
   */
  private static IExpr integrateProductOverPiecewise(IExpr factor, IExpr piecewise, IExpr x,
      IAST booleRegion, IBuiltInSymbol integrateHead, EvalEngine engine) {
    IAST branches = piecewiseZeroDefaultBranches(piecewise);
    if (branches == null) {
      return F.NIL;
    }
    IASTAppendable sum = F.PlusAlloc(branches.argSize() + 1);
    for (int i = 1; i < branches.size(); i++) {
      IAST branch = (IAST) branches.get(i);
      if (branch.size() != 3) {
        return F.NIL;
      }
      IAST region = DistributionRegion.regionFromPredicate(branch.arg2(), x);
      if (region.isNIL()) {
        return F.NIL;
      }
      if (booleRegion != null && booleRegion.isPresent()) {
        region = IntervalDataSym.intersection(region, booleRegion);
        if (!region.isIntervalData()) {
          return F.NIL;
        }
      }
      for (int p = 1; p < region.size(); p++) {
        IAST piece = (IAST) region.get(p);
        if (piece.arg1().equals(piece.arg4())) {
          continue;
        }
        sum.append(F.binaryAST2(integrateHead, F.Times(factor, branch.arg1()),
            F.list(x, piece.arg1(), piece.arg4())));
      }
    }
    IExpr result = engine.evaluate(sum);
    return result.isFree(integrateHead, true) ? result : F.NIL;
  }

  private static final class AbsoluteCorrelation extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr a = ast.arg1();
      IExpr b = ast.arg2();
      int dim1 = a.isVector();
      int dim2 = b.isVector();
      if (dim1 >= 0 && dim1 == dim2) {
        return F.Divide(F.Dot(a, F.Conjugate(b)), F.Length(a));
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  private static class InverseCDF extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // 1 or 2 arguments
      if (ast.size() == 2 || ast.size() == 3) {
        try {
          if (ast.arg1().isAST()) {
            IAST dist = (IAST) ast.arg1();
            ICDF evaluator = dist.headInstanceOf(ICDF.class);
            if (evaluator != null) {
              IExpr xArg = F.NIL;
              if (ast.isAST2()) {
                xArg = ast.arg2();
              }
              return evaluator.inverseCDF(dist, xArg, engine);
            }
          }
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          return Errors.printMessage(S.InverseCDF, rex, engine);
        }
      }

      return F.NIL;
    }
  }

  /**
   *
   *
   * <pre>
   * ArithmeticGeometricMean(a, b)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the arithmetic geometric mean of <code>a</code> and <code>b</code>.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Arithmetic%E2%80%93geometric_mean">Wikipedia -
   * Arithmetic-geometric mean)</a>
   * </ul>
   */
  private static class ArithmeticGeometricMean extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr a = ast.arg1();
      IExpr b = ast.arg2();
      if (a.isZero() || a.equals(b)) {
        return a;
      }
      if (b.isZero()) {
        return b;
      }
      return F.NIL;
    }

    // @Override
    // public IExpr e2ApcomplexArg(final ApcomplexNum a, final ApcomplexNum b) {
    // return F.complexNum(ApcomplexMath.agm(a.apcomplexValue(), b.apcomplexValue()));
    // }
    //
    // @Override
    // public IExpr e2ApfloatArg(final ApfloatNum a, final ApfloatNum b) {
    // return F.num(ApfloatMath.agm(a.apfloatValue(), b.apfloatValue()));
    // }

    // @Override
    // public IExpr e2DblComArg(final IComplexNum a, final IComplexNum b) {
    // ApcomplexNum a1 = a.apcomplexNumValue();
    // ApcomplexNum b1 = b.apcomplexNumValue();
    // Apcomplex agm = ApcomplexMath.agm(a1.apcomplexValue(), b1.apcomplexValue());
    // return F.complex(agm.real().doubleValue(), agm.imag().doubleValue());
    // // IComplexNum a1 = a;
    // // IComplexNum b1 = b;
    // // while (a1.subtract(b1).abs().evalDouble() >= Config.DOUBLE_TOLERANCE) {
    // // IComplexNum arith = a1.add(b1).multiply(F.complexNum(1 / 2.0));
    // // IComplexNum geom = a1.multiply(b1).pow(F.complexNum(1 / 2.0));
    // // a1 = arith;
    // // b1 = geom;
    // // }
    // // return a1;
    // }

    // @Override
    // public IExpr e2DblArg(final INum a, final INum b) {
    // return F.num(ApfloatMath.agm(new Apfloat(a.doubleValue()), new Apfloat(b.doubleValue()))
    // .doubleValue());
    // // double a1 = a.doubleValue();
    // // double b1 = b.doubleValue();
    // // while (Math.abs(a1 - b1) >= Config.DOUBLE_TOLERANCE) {
    // // double arith = (a1 + b1) / 2.0;
    // // double geom = Math.sqrt(a1 * b1);
    // // a1 = arith;
    // // b1 = geom;
    // // }
    // // return F.num(a1);
    // }

    @Override
    public IExpr numericFunction(IAST ast, final EvalEngine engine) {
      if (ast.argSize() == 2) {
        IInexactNumber a = (IInexactNumber) ast.arg1();
        IInexactNumber b = (IInexactNumber) ast.arg2();
        return a.agm(b);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    /** {@inheritDoc} */
    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.ORDERLESS, Attribute.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  /**
   *
   *
   * <pre>
   * CDF(distribution, value)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the cumulative distribution function of <code>value</code>.
   *
   * </blockquote>
   *
   * <pre>
   * PDF(distribution, {list} )
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the cumulative distribution function of the values of list.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Cumulative_distribution_function">Wikipedia -
   * cumulative distribution function</a>
   * </ul>
   *
   * <p>
   * <code>CDF</code> can be applied to the following distributions:
   *
   * <blockquote>
   *
   * <p>
   * <a href="BernoulliDistribution.md">BernoulliDistribution</a>,
   * <a href="BinomialDistribution.md">BinomialDistribution</a>,
   * <a href="DiscreteUniformDistribution.md">DiscreteUniformDistribution</a>,
   * <a href="ErlangDistribution.md">ErlangDistribution</a>,
   * <a href="ExponentialDistribution.md">ExponentialDistribution</a>,
   * <a href="FrechetDistribution.md">FrechetDistribution</a>,
   * <a href="GammaDistribution.md">GammaDistribution</a>,
   * <a href="GeometricDistribution.md">GeometricDistribution</a>,
   * <a href="GumbelDistribution.md">GumbelDistribution</a>,
   * <a href="HypergeometricDistribution.md">HypergeometricDistribution</a>,
   * <a href="LogNormalDistribution.md">LogNormalDistribution</a>,
   * <a href="NakagamiDistribution.md">NakagamiDistribution</a>,
   * <a href="NormalDistribution.md">NormalDistribution</a>,
   * <a href="PoissonDistribution.md">PoissonDistribution</a>,
   * <a href="StudentTDistribution.md">StudentTDistribution</a>,
   * <a href="WeibullDistribution.md">WeibullDistribution</a>
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; CDF(NormalDistribution(),-0.41)
   * 0.3409
   *
   * &gt;&gt; Table(CDF(NormalDistribution(0, s), x), {s, {.75, 1, 2}}, {x, -6,6}) // N
   * {{0.0,0.0,0.0,0.00003,0.00383,0.09121,0.5,0.90879,0.99617,0.99997,1.0,1.0,1.0},{0.0,0.0,0.00003,0.00135,0.02275,0.15866,0.5,0.84134,0.97725,0.99865,0.99997,1.0,1.0},{0.00135,0.00621,0.02275,0.06681,0.15866,0.30854,0.5,0.69146,0.84134,0.93319,0.97725,0.99379,0.99865}}
   * </pre>
   */
  private static class CDF extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1() || ast.isAST2()) {
        // check because of pure function form ?
        try {
          if (ast.arg1().isAST()) {
            IAST dist = (IAST) ast.arg1();
            ICDF evaluator = dist.headInstanceOf(ICDF.class);
            if (evaluator != null) {
              IExpr xArg = F.NIL;
              if (ast.isAST2()) {
                xArg = ast.arg2();
              }
              return evaluator.cdf(dist, xArg, engine);
            }
          }
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          return Errors.printMessage(S.CDF, rex, engine);
        }
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static final class BernoulliProcess extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.head().isAST() && ast.isAST1()) {
        // operator form
        IAST headAST = (IAST) ast.head();
        if (headAST.isAST1()) {
          return F.BernoulliDistribution(headAST.arg1());
        }
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

  }

  /**
   *
   *
   * <pre>
   * BinCounts(list, width - of - bin)
   * </pre>
   *
   * <p>
   * or
   *
   * <pre>
   * BinCounts(list, {min, max, width-of-bin} )
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * count the number of elements, if <code>list</code>, is divided into successive bins with width
   * <code>width-of-bin</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; BinCounts({1,2,3,4,5},5)
   * {4,1}
   *
   * &gt;&gt; BinCounts({1,2,3,4,5},10)
   * {5}
   * </pre>
   */
  private static final class BinCounts extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        if (ast.arg1().isList()) {
          IAST vector = (IAST) ast.arg1();
          vector = dropNonReals(engine, vector);
          if (ast.size() == 3) {
            return binCounts(ast, vector, ast.arg2(), engine);
          } else if (ast.size() == 2) {
            return binCounts(ast, vector, F.C1, engine);
          }
        }
      } catch (ArithmeticException aex) {
        return Errors.printMessage(S.BinCounts, aex, engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    private static IExpr binCounts(IAST ast, IAST vector, final IExpr arg2, EvalEngine engine) {
      INum dxNum = F.CD1;
      int dx = 1;
      int xMin = 0;
      int xMax = Integer.MIN_VALUE;
      if (arg2.isList()) {
        IAST list = (IAST) arg2;
        if (list.size() == 4) {
          dx = list.arg3().toIntDefault();
          if (F.isNotPresent(dx)) {
            return F.NIL;
          }
          if (dx < 0) {
            // The step size `1` is expected to be positive
            return Errors.printMessage(ast.topHead(), "step", F.list(list.arg3()), engine);
          }
          xMin = list.arg1().toIntDefault();
          if (F.isNotPresent(xMin)) {
            return F.NIL;
          }
          xMax = list.arg2().toIntDefault();
          if (F.isNotPresent(xMax)) {
            return F.NIL;
          }
          if (xMax <= xMin) {
            return F.CEmptyList;
          }
          xMin = xMin / dx;
          xMax = xMax / dx;
        }
      } else {
        dx = Integer.MIN_VALUE;
        double dxDouble = arg2.evalfNaN();
        if (Double.isNaN(dxDouble)) {
          return F.NIL;
        }
        dxNum = F.num(dxDouble);
        IExpr dXMax = S.Max.of(engine, vector);
        xMax = S.Floor.of(engine, F.Divide(F.Plus(dXMax, arg2), arg2)).toIntDefault();
        if (xMax < 0) {
          return F.NIL;
        }
      }
      if (xMax >= xMin) {
        final int capacity = xMax - xMin;
        if (capacity > Config.MAX_AST_SIZE) {
          ASTElementLimitExceeded.throwIt(capacity);
        }
        int[] res = new int[capacity];
        for (int i = 1; i < vector.size(); i++) {
          IExpr temp = vector.get(i);
          int index = -1;
          if (F.isPresent(dx)) {
            index = (((IReal) temp).floorFraction()).div(dx).toIntDefault();
            if ((dx > 1) && temp.isInteger() && ((IInteger) temp).mod(dx).isZero()) {
              index--;
            }
          } else {
            index = S.Floor.of(engine, (((IReal) temp).divide(dxNum))).toIntDefault();
          }
          if (F.isNotPresent(index)) {
            return F.NIL;
          }
          int binIndex = index - xMin;
          if (binIndex < 0 || binIndex >= res.length) {
            continue;
          }
          res[binIndex]++;
        }
        return F.mapRange(0, res.length, i -> F.ZZ(res[i]));
      }

      return F.NIL;
    }

    /**
     * Drop non real expressions from this vecrtor
     *
     * @param engine
     * @param vector
     * @return
     */
    private static IAST dropNonReals(EvalEngine engine, IAST vector) {
      IAST[] filter = vector.filterNIL(x -> {
        if (x.isReal()) {
          return x;
        }
        IExpr d = engine.evalN(x);
        if (d.isReal()) {
          return d;
        }
        return F.NIL;
      });
      vector = filter[0];
      return vector;
    }
  }

  private static final class BinomialProcess extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.head().isAST() && ast.isAST1()) {
        // operator form
        IAST headAST = (IAST) ast.head();
        if (headAST.isAST1()) {
          return F.BinomialDistribution(ast.arg1(), headAST.arg1());
        }
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

  }

  private static final class BrownianBridgeProcess extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.head() == S.BrownianBridgeProcess) {
        if (ast.isAST2()) {
          if (ast.arg1().isList2() && ast.arg2().isList2()) {
            return F.BrownianBridgeProcess(F.C1, ast.arg1(), ast.arg2());
          }
          return F.BrownianBridgeProcess(F.C1, F.List(ast.arg1(), F.C0), F.List(ast.arg2(), F.C0));
        } else if (ast.isAST0()) {
          return F.BrownianBridgeProcess(F.C1, F.List(F.C0, F.C0), F.List(F.C1, F.C0));
        }
        return F.NIL;
      }
      if (ast.head().isAST() && ast.isAST1()) {
        IExpr t = ast.arg1();
        // operator form
        IAST headAST = (IAST) ast.head();
        if (headAST.isAST3() && headAST.arg2().isList2() && headAST.arg3().isList2()) {
          IExpr s = headAST.arg1();
          IAST list1 = (IAST) headAST.arg2();
          IExpr t1 = list1.arg1();
          IExpr a = list1.arg2();
          IAST list2 = (IAST) headAST.arg3();
          IExpr t2 = list2.arg1();
          IExpr b = list2.arg2();
          return brownianBridgeProcess(s, t1, a, t2, b, t);
        }
      }
      return F.NIL;
    }


    private IExpr brownianBridgeProcess(IExpr s, IExpr t1, IExpr a, IExpr t2, IExpr b, IExpr t) {
      IExpr function =
          // [$ NormalDistribution((b*(t - t1))/(-t1 + t2) + (a*(-t + t2))/(-t1 + t2),
          // s*Sqrt(((t - t1)*(-t + t2))/(-t1 + t2))) $]
          F.NormalDistribution(
              F.Plus(F.Times(b, F.Subtract(t, t1), F.Power(F.Plus(F.Negate(t1), t2), F.CN1)),
                  F.Times(a, F.Plus(F.Negate(t), t2), F.Power(F.Plus(F.Negate(t1), t2), F.CN1))),
              F.Times(s, F.Sqrt(F.Times(F.Subtract(t, t1), F.Plus(F.Negate(t), t2),
                  F.Power(F.Plus(F.Negate(t1), t2), F.CN1))))); // $$;
      return function;
    }


    @Override
    public void setUp(final ISymbol newSymbol) {}

  }

  /**
   *
   *
   * <pre>
   * <code>Correlation(a, b)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * computes Pearson's correlation of two equal-sized vectors <code>a</code> and <code>b</code>.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Pearson_correlation_coefficient">Wikipedia - Pearson
   * correlation coefficient</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; Correlation({a,b},{c,d})
   * ((a-b)*(Conjugate(c)-Conjugate(d)))/(Sqrt((a-b)*(Conjugate(a)-Conjugate(b)))*Sqrt((c-d)*(Conjugate(c)-Conjugate(d))))
   *
   * &gt;&gt; Correlation({10, 8, 13, 9, 11, 14, 6, 4, 12, 7, 5}, {8.04, 6.95, 7.58, 8.81, 8.33, 9.96, 7.24, 4.26, 10.84, 4.82, 5.68})
   * 0.81642
   * </code>
   * </pre>
   */
  private static final class Correlation extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        if (ast.isAST1()) {
          if (ast.arg1() instanceof ASTRealMatrix) {
            PearsonsCorrelation pc =
                new PearsonsCorrelation(((ASTRealMatrix) ast.arg1()).getRealMatrix());
            return new ASTRealMatrix(pc.getCorrelationMatrix(), false);
          }
          int[] dim = ast.arg1().isMatrix();
          if (dim != null && dim[0] > 1 && dim[1] > 1) {
            RealMatrix matrix = ast.arg1().toRealMatrix();
            if (matrix != null) {
              PearsonsCorrelation pc = new PearsonsCorrelation(matrix);
              return new ASTRealMatrix(pc.getCorrelationMatrix(), false);
            }
          }
          return F.NIL;
        }
        IExpr arg1 = ast.arg1();
        IExpr arg2 = ast.arg2();
        int dim1 = arg1.isVector();
        int dim2 = arg2.isVector();
        if (dim1 >= 0 && dim1 == dim2) {
          if (engine.isDoubleMode() || arg1.isNumericAST() || arg2.isNumericAST()) {
            double[] a = arg1.toDoubleVector();
            if (a != null) {
              double[] b = arg2.toDoubleVector();
              if (b != null) {
                PearsonsCorrelation pc = new PearsonsCorrelation();
                return F.num(pc.correlation(a, b));
              }
            }
          }
          return F.Divide(F.Covariance(arg1, arg2),
              F.Times(F.StandardDeviation(arg1), F.StandardDeviation(arg2)));
        }
      } catch (MathRuntimeException mrex) {
        return Errors.printMessage(S.Correlation, mrex, engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   *
   *
   * <pre>
   * FiveNum({dataset})
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * the Tuckey five-number summary is a set of descriptive statistics that provide information
   * about a <code>dataset</code>. It consists of the five most important sample percentiles:
   *
   * <ol>
   * <li>the sample minimum (smallest observation)
   * <li>the lower quartile or first quartile
   * <li>the median (the middle value)
   * <li>the upper quartile or third quartile
   * <li>the sample maximum (largest observation)
   * </ol>
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Five-number_summary">Wikipedia - Five-number
   * summary</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; FiveNum({0, 0, 1, 2, 63, 61, 27, 13})
   * {0,1/2,15/2,44,63}
   * </pre>
   */
  private static final class FiveNum extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int size = ast.arg1().isVector();
      if (size >= 0) {
        final IAST param = F.list(//
            F.list(F.C1D2, F.C0), //
            F.list(F.C0, F.C1) //
        );
        final IExpr list = ast.arg1();
        // if (engine.isDoubleMode()) {
        // RealVector doubleArray = list.toRealVector();
        // if (doubleArray != null) {
        // IASTAppendable result = F.ListAlloc(5);
        // result.append(doubleArray.getMinValue());
        // result.append(F.Quantile(list, F.C1D4, param));
        // result.append(F.Median(list));
        // result.append(F.Quantile(list, F.C3D4, param));
        // result.append(doubleArray.getMaxValue());
        //
        // return result;
        // }
        // }

        return F.List(//
            F.Min(list), //
            F.Quantile(list, F.C1D4, param), //
            F.Median(list), //
            F.Quantile(list, F.C3D4, param), //
            F.Max(list) //
        );
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class GeometricMean extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isNonEmptyList()) {
        IAST list = (IAST) arg1;

        int[] dim = list.isMatrix();
        if (dim == null && arg1.isListOfLists()) {
          return F.NIL;
        }
        if (dim != null) {
          return list.mapMatrixColumns(dim, x -> F.GeometricMean(x)).normal(false);
        }
        if (arg1.isRealVector()) {
          double[] arg1DoubleArray = arg1.toDoubleVector();
          if (arg1DoubleArray == null) {
            return F.NIL;
          }
          return F.num(StatUtils.geometricMean(arg1DoubleArray));
        }
        return F.Power(list.apply(S.Times), F.QQ(1, arg1.argSize()));
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public IExpr numericEval(final IAST ast, EvalEngine engine) {
      double[] values = ast.get(1).toDoubleVector();
      if (values == null) {
        return F.NIL;
      }
      return F.num(StatUtils.geometricMean(values));
    }
  }

  private static class HarmonicMean extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isNonEmptyList()) {
        IAST list = (IAST) arg1;

        int[] dim = list.isMatrix();
        if (dim == null && arg1.isListOfLists()) {
          return F.NIL;
        }
        if (dim != null) {
          return list.mapMatrixColumns(dim, x -> F.HarmonicMean(x)).normal(false);
        }

        IASTAppendable result = F.PlusAlloc(8);
        INumber number = F.C0;
        final int size = list.size();
        final int argSize = size - 1;
        for (int i = 1; i < size; i++) {
          IExpr x = list.get(i);
          if (x.isZero()) {
            return F.C0;
          }
          x = x.inverse();
          if (x.isNumber()) {
            number = number.plus((INumber) x);
          } else {
            result.append(x);
          }
        }
        if (result.argSize() == 0) {
          if (number.isZero()) {
            return F.C0;
          }
          return F.Times(F.ZZ(argSize), F.Power(number, F.CN1));
        }
        result.append(number);
        IExpr temp = engine.evaluate(result);
        if (temp.isZero()) {
          return F.C0;
        }
        return F.Times(F.ZZ(argSize), F.Power(result, F.CN1));
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   *
   *
   * <pre>
   * <code>Covariance(a, b)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * computes the covariance between the equal-sized vectors <code>a</code> and <code>b</code>.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Covariance">Wikipedia - Covariance</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; Covariance({10, 8, 13, 9, 11, 14, 6, 4, 12, 7, 5}, {8.04, 6.95, 7.58, 8.81, 8.33, 9.96, 7.24, 4.26, 10.84, 4.82, 5.68})
   * 5.501
   *
   * &gt;&gt; Covariance({0.2, 0.3, 0.1}, {0.3, 0.3, -0.2})
   * 0.025
   * </code>
   * </pre>
   */
  private static final class Covariance extends AbstractMatrix1Expr {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        try {
          if (ast.arg1().isAST()) {
            IAST dist = (IAST) ast.arg1();
            if (dist.head().isSymbol()) {
              ISymbol head = (ISymbol) dist.head();

              if (dist.head().isSymbol()) {
                if (head instanceof IBuiltInSymbol) {
                  IEvaluator evaluator = ((IBuiltInSymbol) head).getEvaluator();
                  if (evaluator instanceof ICovariance) {
                    ICovariance covariance = (ICovariance) evaluator;
                    return covariance.covariance(dist, engine);
                  }
                }
              }
            }
          }
        } catch (RuntimeException rex) {
        }
        int[] dimension = ast.arg1().isMatrix();
        if (dimension != null && dimension[0] > 1 && dimension[1] > 0
            && !ast.arg1().isRealMatrix()) {
          // exact or symbolic data. The numeric path stays with realMatrixEval below, which uses
          // Hipparchus and answers machine numbers; this one keeps 5/3 as 5/3.
          IExpr normal = ast.arg1().normal(false);
          if (normal.isList()) {
            return columnCovariances((IAST) normal, dimension, engine);
          }
        }
        return super.evaluate(ast, engine);
      }

      try {
        if (ast.size() == 3 && ast.arg1().isAST() && ast.arg2().isAST()) {
          final IAST arg1 = (IAST) ast.arg1();
          final IAST arg2 = (IAST) ast.arg2();
          return evaluateArg2(arg1, arg2, engine);
        }
      } catch (final MathRuntimeException mrex) {
        // org.hipparchus.exception.MathIllegalArgumentException: inconsistent dimensions: 0 != 3
        return Errors.printMessage(S.Covariance, mrex, engine);
      } catch (final IndexOutOfBoundsException iobe) {
        return Errors.printMessage(S.Covariance, iobe, engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    private static IExpr evaluateArg2(final IAST arg1, final IAST arg2, EvalEngine engine) {
      int arg1Length = arg1.isVector();
      if (arg1Length > 1) {
        int arg2Length = arg2.isVector();
        if (arg1Length == arg2Length) {
          if (engine.isNumericMode()) {
            double[] arg1DoubleArray = arg1.toDoubleVector();
            double[] arg2DoubleArray = arg2.toDoubleVector();
            if (arg1DoubleArray != null && arg2DoubleArray != null) {
              org.hipparchus.stat.correlation.Covariance cov =
                  new org.hipparchus.stat.correlation.Covariance();
              return F.num(cov.covariance(arg1DoubleArray, arg2DoubleArray, true));
            }
          }
          return vectorCovarianceSymbolic(arg1, arg2, arg1Length);
        }
      }
      return F.NIL;
    }

    /**
     * The covariance matrix of the columns of <code>matrix</code>: entry <code>(i,j)</code> is the
     * covariance of column <code>i</code> with column <code>j</code>, so the diagonal holds the
     * variances of the columns.
     *
     * <p>
     * Each entry is the two-vector <code>Covariance</code> that this class already computes, which
     * is the sample form dividing by <code>n-1</code> - matching <code>Variance</code> on the
     * diagonal.
     *
     * @return {@link F#NIL} if any pair of columns cannot be handled
     */
    private static IExpr columnCovariances(IAST matrix, int[] dimension, EvalEngine engine) {
      int rows = dimension[0];
      int columns = dimension[1];
      IASTAppendable columnVectors = F.ListAlloc(columns);
      for (int c = 1; c <= columns; c++) {
        IASTAppendable column = F.ListAlloc(rows);
        for (int r = 1; r <= rows; r++) {
          column.append(matrix.get(r).getAt(c));
        }
        columnVectors.append(column);
      }

      IASTAppendable result = F.ListAlloc(columns);
      for (int i = 1; i <= columns; i++) {
        IASTAppendable row = F.ListAlloc(columns);
        for (int j = 1; j <= columns; j++) {
          IExpr entry = evaluateArg2((IAST) columnVectors.get(i), (IAST) columnVectors.get(j),
              engine);
          if (entry.isNIL()) {
            return F.NIL;
          }
          row.append(engine.evaluate(entry));
        }
        result.append(row);
      }
      return result;
    }

    private static IExpr vectorCovarianceSymbolic(final IAST arg1, final IAST arg2,
        int arg1Length) {
      if (arg1Length == 2) {
        return F.Times(F.C1D2, F.Subtract(arg1.arg1(), arg1.arg2()),
            F.Subtract(F.Conjugate(arg2.arg1()), F.Conjugate(arg2.arg2())));
      }
      IAST num1 = arg1.apply(S.Plus);
      IInteger factor = F.ZZ(-1 * (arg1.size() - 2));
      IExpr v1 = F.sum(i -> F.Times(F.CN1, num1.setAtCopy(i.toInt(), factor.times(arg1.get(i))),
          F.Conjugate(arg2.get(i))), 1, arg1.argSize());
      return F.Divide(v1, F.ZZ((arg1.argSize()) * ((arg1.size()) - 2L)));
    }

    @Override
    public IExpr matrixEval(FieldMatrix<IExpr> matrix, Predicate<IExpr> zeroChecker, IAST ast) {
      return F.NIL;
    }

    @Override
    public IExpr numericEval(final IAST ast, EvalEngine engine) {
      if (ast.size() == 2) {
        return super.numericEval(ast, engine);
      }
      if (ast.size() == 3 && ast.arg1().isAST() && ast.arg2().isAST()) {
        final IAST arg1 = (IAST) ast.arg1();
        final IAST arg2 = (IAST) ast.arg2();
        return evaluateArg2(arg1, arg2, engine);
      }
      return F.NIL;
    }

    @Override
    public IExpr realMatrixEval(RealMatrix matrix, EvalEngine engine, IAST ast) {
      if (matrix.getRowDimension() <= 1) {
        // The argument `1` should have at least `2` elements.
        return Errors.printMessage(S.Covariance, "shlen",
            F.List(new ASTRealMatrix(matrix, false), F.stringx("two")), EvalEngine.get());
      }
      org.hipparchus.stat.correlation.Covariance cov =
          new org.hipparchus.stat.correlation.Covariance(matrix);
      return new ASTRealMatrix(cov.getCovarianceMatrix(), false);
    }
  }

  /**
   *
   *
   * <pre>
   * Expectation(pure - function, data - set)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the expected value of the <code>pure-function</code> for the given <code>data-set
   * </code>.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Expected_value">Wikipedia - Expected value</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Expectation((#^3)&amp;, {a,b,c})
   * 1/3*(a^3+b^3+c^3)
   *
   * &gt;&gt; Expectation(2*x+3,Distributed(x,{a,b,c,d}))
   * 1/4*(12+2*a+2*b+2*c+2*d)
   * </pre>
   */
  private static class Expectation extends AbstractFunctionOptionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] options,
        final EvalEngine engine, IAST originalAST) {
      if (argSize != 2) {
        return F.NIL;
      }
      try {
        IExpr xExpr = ast.arg1();
        IExpr arg2 = ast.arg2();
        if (xExpr.isAST(S.Conditioned, 3)) {
          // E(expr | pred) == E(expr*Boole(pred)) / P(pred)
          return conditionedExpectation(S.Expectation, S.Probability, (IAST) xExpr, arg2, engine);
        }
        if (xExpr.isFunction() && arg2.isList()) {
          IAST data = (IAST) arg2;
          IASTAppendable sum = F.PlusAlloc(data.size());
          for (int i = 1; i < data.size(); i++) {
            sum.append(F.unaryAST1(xExpr, data.get(i)));
          }
          return sum.divide(F.ZZ(data.argSize()));
        }
        if (isDistributedList(arg2)) {
          // independent random variables: iterated expectation, the last variable is summed
          // (integrated) first
          IAST distributedList = (IAST) arg2;
          IExpr nested = xExpr;
          for (int i = distributedList.argSize(); i >= 1; i--) {
            nested = F.Expectation(nested, distributedList.get(i));
          }
          IExpr result = engine.evaluate(nested);
          return result.isFree(S.Expectation, true) ? result : F.NIL;
        }
        if (arg2.isAST(S.Distributed, 3)) {
          IExpr x = arg2.first();
          IExpr distribution = arg2.second();
          if (xExpr.isAST(S.Boole, 2)) {
            // Expectation(Boole(pred), x \[Distributed] dist) == Probability(pred, ...)
            return F.Probability(xExpr.first(), arg2);
          }
          if (distribution.isAST(S.ProbabilityDistribution)) {
            IExpr result = StatisticsDerivedDistributions.ProbabilityDistribution
                .expectation((IAST) distribution, xExpr, x, engine);
            if (result.isPresent()) {
              return result;
            }
          }
          if (distribution.isAST(S.CensoredDistribution)) {
            IExpr result = StatisticsDerivedDistributions.CensoredDistribution
                .expectation((IAST) distribution, xExpr, engine);
            if (result.isPresent()) {
              return result;
            }
          }
          if (distribution.isAST() && distribution.isDistribution() && x.isSymbol()) {
            IExpr result = polynomialExpectation(xExpr, (ISymbol) x, (IAST) distribution, engine);
            if (result.isPresent()) {
              return result;
            }
          }
          if (distribution.isList()) {
            IAST data = (IAST) distribution;
            // Sum( predicate , data ) / data.argSize()
            IASTAppendable sum = F.PlusAlloc(data.size());
            for (int i = 1; i < data.size(); i++) {
              sum.append(F.subst(xExpr, x, data.get(i)));
            }
            return sum.divide(F.ZZ(data.argSize()));
          } else if (distribution.isContinuousDistribution()) {
            IExpr result = expectationContinuous(xExpr, x, distribution, engine);
            if (result.isPresent()) {
              return result;
            }
          } else if (distribution.isDiscreteDistribution()) {
            IExpr result = expectationDiscrete(xExpr, x, distribution, engine);
            if (result.isPresent()) {
              return result;
            }
          }
        }
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        return Errors.printMessage(S.Expectation, rex, engine);
      }
      if (engine.isNumericMode() && ast.arg2().isAST(S.Distributed, 3)) {
        // N(Expectation(...)) falls back to NExpectation
        IExpr temp = engine.evaluate(F.binaryAST2(S.NExpectation, ast.arg1(), ast.arg2()));
        if (temp.isFree(S.NExpectation, true)) {
          return temp;
        }
      }
      return F.NIL;
    }

    /**
     * Expectation for a continuous distribution: integrate <code>expr*pdf</code>. A single
     * <code>Boole</code> factor restricts the integration region; a piecewise density restricts the
     * integration to the branch validity intervals.
     */
    private static IExpr expectationContinuous(IExpr xExpr, IExpr x, IExpr distribution,
        EvalEngine engine) {
      IExpr pdf = S.PDF.ofNIL(engine, distribution, x);
      if (pdf.isNIL()) {
        return F.NIL;
      }
      IExpr remainder = xExpr;
      IAST booleRegion = F.NIL;
      IExpr[] extracted = extractBooleFactor(xExpr, x);
      if (extracted != null) {
        remainder = extracted[0];
        booleRegion = (IAST) extracted[1];
      }
      if (pdf.isFree(S.Piecewise)) {
        if (booleRegion.isPresent()) {
          return integrateOverRegion(F.Times(remainder, pdf), x, booleRegion, S.Integrate, engine);
        }
        return F.Integrate(F.Times(xExpr, pdf), F.list(x, F.CNInfinity, F.CInfinity));
      }
      if (booleRegion.isNIL() && pdf.isAST(S.Piecewise, 3) && pdf.first().isList1()
          && pdf.first().first().isList2() && x.isSymbol()) {
        // a density which is 0 outside a single interval: integrate over that interval only
        IAST branch = (IAST) pdf.first().first();
        IAST interval = IntervalDataSym.relationToIntervalSet((IAST) branch.arg2(), (ISymbol) x);
        if (interval.isIntervalData() && interval.argSize() == 1) {
          IAST bounds = (IAST) interval.arg1();
          return F.Integrate(F.Times(xExpr, branch.arg1()),
              F.list(x, bounds.arg1(), bounds.arg4()));
        }
      }
      return integrateProductOverPiecewise(remainder, pdf, x, booleRegion, S.Integrate, engine);
    }

    /**
     * Expectation for a discrete distribution: enumerate <code>Sum(expr*pmf)</code> exactly over a
     * small finite support, otherwise try the symbolic <code>Sum</code> over the support.
     */
    private static IExpr expectationDiscrete(IExpr xExpr, IExpr x, IExpr distribution,
        EvalEngine engine) {
      if (!x.isSymbol()) {
        return F.NIL;
      }
      IExpr pdf = S.PDF.ofNIL(engine, distribution, x);
      if (pdf.isNIL()) {
        return F.NIL;
      }
      IDiscreteDistribution dist = getDiscreteDistribution(distribution);
      long supportLo = DistributionRegion.supportLowerBound(dist, distribution);
      long supportHi = DistributionRegion.supportUpperBound(dist, distribution);
      IExpr remainder = xExpr;
      long[] windows = new long[] {supportLo, supportHi};
      IExpr[] extracted = extractBooleFactor(xExpr, x);
      if (extracted != null) {
        long[] restricted = DistributionRegion.integerWindows((IAST) extracted[1], engine);
        if (restricted != null) {
          remainder = extracted[0];
          windows = DistributionRegion.clampWindows(restricted, supportLo, supportHi);
        }
      }
      if (DistributionRegion
          .countWindows(windows) <= DistributionRegion.SYMBOLIC_ENUMERATION_LIMIT) {
        IExpr result = DistributionRegion.enumerateSymbolic(F.Times(remainder, pdf), x, windows,
            F.NIL, engine);
        if (result.isPresent()) {
          return result;
        }
      }
      if (pdf.isAST(S.Piecewise)) {
        // unwrap the piecewise density, so that Sum can find closed forms for e.g. exponential
        // generating series
        IExpr result = sumProductOverPiecewise(remainder, pdf, x, windows, engine);
        if (result.isPresent()) {
          return result;
        }
      }
      IExpr lower = supportLo == Long.MIN_VALUE ? F.CNInfinity : F.ZZ(supportLo);
      IExpr upper = supportHi == Long.MAX_VALUE ? F.CInfinity : F.ZZ(supportHi);
      IExpr sum = engine.evaluate(F.Sum(F.Times(xExpr, pdf), F.List(x, lower, upper)));
      if (sum.isFree(S.Sum, true) && sum.isFree(S.Boole, true)) {
        return sum;
      }
      return F.NIL;
    }

    /**
     * If <code>expr</code> expands into a sum of terms <code>c * x^n * b^(a*x + d)</code>, replace
     * every power <code>x^n</code> by the raw moment <code>E[X^n]</code> of the distribution and
     * every exponential part by a value of the moment generating function
     * (<code>E[X^n*E^(s*X)]</code> is the <code>n</code>-th derivative of the moment generating
     * function at <code>s</code>). The generating function is only used for distributions where it
     * is an entire function, so that no divergent expectation can be turned into a finite formal
     * value.
     *
     * @return {@link F#NIL} if a term has another structure or the distribution has no closed form
     *         for one of the required moments or generating function values
     */
    private static IExpr polynomialExpectation(IExpr expr, ISymbol x, IAST distribution,
        EvalEngine engine) {
      IStatistics statistics = distribution.headInstanceOf(IStatistics.class);
      if (statistics == null) {
        return F.NIL;
      }
      IExpr expanded = engine.evaluate(F.Expand(expr));
      IAST terms = expanded.isPlus() ? (IAST) expanded : F.List(expanded);
      IASTAppendable sum = F.PlusAlloc(terms.size());
      for (int i = 1; i < terms.size(); i++) {
        IExpr term = terms.get(i);
        if (term.isFree(x)) {
          sum.append(term);
          continue;
        }
        // split the term into a coefficient, a power x^n and exponential factors b^(a*x+d)
        IAST factors = term.isTimes() ? (IAST) term : F.List(term);
        IASTAppendable coefficient = F.TimesAlloc(factors.size());
        IExpr power = F.NIL;
        IExpr exponentialRate = F.NIL;
        for (int j = 1; j < factors.size(); j++) {
          IExpr factor = factors.get(j);
          if (factor.isFree(x)) {
            coefficient.append(factor);
            continue;
          }
          if (factor.equals(x)
              || (factor.isPower() && factor.base().equals(x) && factor.exponent().isInteger())) {
            if (power.isPresent()) {
              return F.NIL;
            }
            power = factor;
            continue;
          }
          if (factor.isPower() && factor.base().isFree(x)) {
            // exponential factor b^e(x) with a linear exponent e(x) == a*x + d
            IExpr exponent = factor.exponent();
            IExpr a = engine.evaluate(F.D(exponent, x));
            if (a.isFree(x) && !a.isZero()) {
              IExpr d = engine.evaluate(F.subst(exponent, x, F.C0));
              IExpr nonLinearRest = engine.evaluate(F.Subtract(exponent, F.Plus(F.Times(a, x), d)));
              if (nonLinearRest.isZero()) {
                IExpr base = factor.base();
                // b^(a*x) == E^(a*Log(b)*x)
                IExpr rate = base == S.E ? a : F.Times(a, F.Log(base));
                exponentialRate = exponentialRate.isNIL() ? rate : F.Plus(exponentialRate, rate);
                if (!d.isZero()) {
                  coefficient.append(F.Power(base, d));
                }
                continue;
              }
            }
          }
          return F.NIL;
        }
        if (exponentialRate.isPresent()) {
          int derivativeOrder = 0;
          if (power.isPresent()) {
            derivativeOrder = power.equals(x) ? 1 : power.exponent().toIntDefault();
            if (derivativeOrder < 1 || derivativeOrder > 8) {
              return F.NIL;
            }
          }
          IExpr value = momentGeneratingFunctionValue(distribution,
              engine.evaluate(exponentialRate), derivativeOrder, engine);
          if (value.isNIL()) {
            return F.NIL;
          }
          sum.append(F.Times(coefficient, value));
        } else if (power.isPresent()) {
          IExpr exponent = power.equals(x) ? F.C1 : power.exponent();
          IExpr moment = statistics.moment(distribution, exponent);
          if (moment.isNIL()) {
            return F.NIL;
          }
          sum.append(F.Times(coefficient, moment));
        } else {
          return F.NIL;
        }
      }
      return engine.evaluate(sum);
    }

    /**
     * Test if the moment generating function of the distribution is an entire function (finite
     * support or e.g. Poisson, Normal, Beta): only for those every formal generating function value
     * is a correct expectation.
     */
    private static boolean hasEntireGeneratingFunction(IExpr distribution) {
      if (!distribution.isAST()) {
        return false;
      }
      switch (((IAST) distribution).headID()) {
        case ID.BenfordDistribution:
        case ID.BernoulliDistribution:
        case ID.BetaBinomialDistribution:
        case ID.BetaDistribution:
        case ID.BinomialDistribution:
        case ID.DiscreteUniformDistribution:
        case ID.HypergeometricDistribution:
        case ID.NormalDistribution:
        case ID.PoissonDistribution:
        case ID.UniformDistribution:
          return true;
        default:
          return false;
      }
    }

    /**
     * <code>E[X^n * E^(t*X)]</code> as the <code>n</code>-th derivative of the moment generating
     * function at <code>t</code>. Only distributions with an entire generating function are used -
     * see {@link #hasEntireGeneratingFunction(IExpr)}.
     *
     * @param derivativeOrder <code>n >= 0</code>
     */
    private static IExpr momentGeneratingFunctionValue(IExpr distribution, IExpr t,
        int derivativeOrder, EvalEngine engine) {
      if (!hasEntireGeneratingFunction(distribution)) {
        return F.NIL;
      }
      if (derivativeOrder == 0) {
        IExpr value = engine.evaluate(F.binaryAST2(S.MomentGeneratingFunction, distribution, t));
        return value.isFree(S.MomentGeneratingFunction, true) ? value : F.NIL;
      }
      ISymbol dummy = F.Dummy("t");
      IExpr mgf = engine.evaluate(F.binaryAST2(S.MomentGeneratingFunction, distribution, dummy));
      if (!mgf.isFree(S.MomentGeneratingFunction, true)) {
        return F.NIL;
      }
      IExpr derivative = engine.evaluate(F.D(mgf, F.list(dummy, F.ZZ(derivativeOrder))));
      if (!derivative.isFree(S.D, true)) {
        return F.NIL;
      }
      return engine.evaluate(F.subst(derivative, dummy, t));
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol,
          new IBuiltInSymbol[] {S.Method, S.Assumptions, S.GenerateConditions, S.WorkingPrecision,
              S.AccuracyGoal, S.PrecisionGoal},
          new IExpr[] {S.Automatic, S.Automatic, S.False, S.Automatic, S.Automatic, S.Automatic});
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  private static class NExpectation extends AbstractFunctionOptionEvaluator {
    /**
     * Tail probability which is cut off on each side when a numeric expectation falls back to the
     * effective support of the distribution.
     */
    private static final double EFFECTIVE_SUPPORT_TAIL = 1.0e-13;


    @Override
    public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] options,
        final EvalEngine engine, IAST originalAST) {
      if (argSize != 2) {
        return F.NIL;
      }
      try {
        IExpr xExpr = ast.arg1();
        IExpr arg2 = ast.arg2();
        if (xExpr.isAST(S.Conditioned, 3)) {
          // E(expr | pred) == E(expr*Boole(pred)) / P(pred)
          return conditionedExpectation(S.NExpectation, S.NProbability, (IAST) xExpr, arg2, engine);
        }
        if (xExpr.isFunction() && arg2.isList()) {
          IAST data = (IAST) arg2;
          IASTAppendable sum = F.PlusAlloc(data.size());
          for (int i = 1; i < data.size(); i++) {
            sum.append(F.unaryAST1(xExpr, data.get(i)));
          }
          return engine.evalN(sum.divide(F.ZZ(data.argSize())));
        }
        if (isDistributedList(arg2)) {
          // iterated expectation over independent variables: compute symbolically and numericize
          IExpr temp = evaluateNonNumeric(F.Expectation(xExpr, arg2), engine);
          if (temp.isFree(S.Expectation, true)) {
            return engine.evalN(temp);
          }
          return F.NIL;
        }
        if (arg2.isAST(S.Distributed, 3)) {
          IExpr x = arg2.first();
          IExpr distribution = arg2.second();
          if (xExpr.isAST(S.Boole, 2)) {
            // NExpectation(Boole(pred), x \[Distributed] dist) == NProbability(pred, ...)
            return F.binaryAST2(S.NProbability, xExpr.first(), arg2);
          }
          int samples = monteCarloSamples(options);
          if (samples > 0) {
            IExpr result = monteCarloExpectation(xExpr, x, distribution, samples, engine);
            if (result.isPresent()) {
              return result;
            }
          }
          if (distribution.isAST(S.ProbabilityDistribution)
              || distribution.isAST(S.CensoredDistribution)) {
            // numericize the symbolic expectation of the derived distribution: its plain PDF is
            // not limited to the distribution domain, so the generic integration is not usable
            IExpr temp = evaluateNonNumeric(F.Expectation(xExpr, arg2), engine);
            if (temp.isFree(S.Expectation, true) && temp.isNumericFunction(true)) {
              return engine.evalN(temp);
            }
            return F.NIL;
          }
          if (distribution.isList()) {
            IAST data = (IAST) distribution;
            // Sum( predicate , data ) / data.argSize()
            IASTAppendable sum = F.PlusAlloc(data.size());
            INumber sumValue = F.C0;
            sum.append(sumValue);
            for (int i = 1; i < data.size(); i++) {
              IExpr summand = engine.evaluate(F.subst(xExpr, x, data.get(i)));
              if (summand.isNumber()) {
                sumValue = sumValue.plus((INumber) summand);
              } else {
                sum.append(summand);
              }
            }
            sum.set(1, sumValue);
            return engine.evalN(sum.divide(F.ZZ(data.argSize())));
          } else if (distribution.isContinuousDistribution()) {
            IExpr result = nExpectationContinuous(xExpr, x, distribution, engine);
            if (result.isPresent()) {
              return result;
            }
          } else if (distribution.isDiscreteDistribution()) {
            IExpr result = nExpectationDiscrete(xExpr, x, distribution, engine);
            if (result.isPresent()) {
              return result;
            }
          }
        }
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        return Errors.printMessage(S.NExpectation, rex, engine);
      }
      return F.NIL;
    }

    /**
     * Numeric expectation for a continuous distribution: <code>NIntegrate(expr*pdf)</code>. A
     * single <code>Boole</code> factor restricts the integration region; a piecewise density
     * restricts the integration to the branch validity intervals - this avoids integrating a
     * discontinuous integrand over an infinite interval.
     */
    private static IExpr nExpectationContinuous(IExpr xExpr, IExpr x, IExpr distribution,
        EvalEngine engine) {
      IExpr pdf = S.PDF.ofNIL(engine, distribution, x);
      if (pdf.isNIL()) {
        return F.NIL;
      }
      IExpr remainder = xExpr;
      IAST booleRegion = F.NIL;
      IExpr[] extracted = extractBooleFactor(xExpr, x);
      if (extracted != null) {
        remainder = extracted[0];
        booleRegion = (IAST) extracted[1];
      }
      if (pdf.isFree(S.Piecewise)) {
        if (booleRegion.isPresent()) {
          IExpr result =
              integrateOverRegion(F.Times(remainder, pdf), x, booleRegion, S.NIntegrate, engine);
          if (result.isPresent()) {
            return result;
          }
        } else {
          IExpr full = engine
              .evaluate(F.NIntegrate(F.Times(xExpr, pdf), F.list(x, F.CNInfinity, F.CInfinity)));
          if (full.isFree(S.NIntegrate, true)) {
            return full;
          }
          IExpr result = integrateOverEffectiveSupport(xExpr, x, distribution, pdf, engine);
          if (result.isPresent()) {
            return result;
          }
        }
        return F.NIntegrate(F.Times(xExpr, pdf), F.list(x, F.CNInfinity, F.CInfinity));
      }
      IExpr result =
          integrateProductOverPiecewise(remainder, pdf, x, booleRegion, S.NIntegrate, engine);
      if (result.isPresent()) {
        return result;
      }
      if (booleRegion.isNIL()) {
        result = integrateOverEffectiveSupport(xExpr, x, distribution, pdf, engine);
        if (result.isPresent()) {
          return result;
        }
      }
      return F.NIntegrate(F.Times(xExpr, pdf), F.list(x, F.CNInfinity, F.CInfinity));
    }

    /**
     * Integrate <code>expr*pdf</code> over the interval which carries the probability mass of the
     * distribution instead of over its full - possibly infinite - support.
     *
     * <p>
     * An infinite integration bound forces the quadrature to sample the far tail of the density,
     * where the density is numerically <code>0</code> but its evaluation can overflow. Replacing
     * the bounds by the {@code EFFECTIVE_SUPPORT_TAIL} and
     * <code>1-{@code EFFECTIVE_SUPPORT_TAIL}</code> quantiles avoids that. This is only used as a
     * fallback after the quadrature over the full support didn't return a number.
     *
     * @return {@link F#NIL} if the quantiles are not finite real numbers or the quadrature still
     *         doesn't return a number
     */
    private static IExpr integrateOverEffectiveSupport(IExpr xExpr, IExpr x, IExpr distribution,
        IExpr pdf, EvalEngine engine) {
      IExpr min = numericQuantile(distribution, F.num(EFFECTIVE_SUPPORT_TAIL), engine);
      IExpr max = numericQuantile(distribution, F.num(1.0 - EFFECTIVE_SUPPORT_TAIL), engine);
      if (min.isNIL() || max.isNIL() || !engine.evalGreater(max, min)) {
        return F.NIL;
      }
      IExpr result =
          engine.evaluate(F.NIntegrate(F.Times(xExpr, pdf), F.list(x, min, max)));
      return result.isFree(S.NIntegrate, true) ? result : F.NIL;
    }

    /**
     * @return the numeric <code>InverseCDF(distribution, probability)</code> or {@link F#NIL} if it
     *         is not a finite real number
     */
    private static IExpr numericQuantile(IExpr distribution, IExpr probability, EvalEngine engine) {
      IExpr quantile = S.InverseCDF.ofNIL(engine, distribution, probability);
      if (quantile.isNIL()) {
        return F.NIL;
      }
      quantile = engine.evalN(quantile);
      double value = quantile.evalfNaN();
      return Double.isFinite(value) ? F.num(value) : F.NIL;
    }

    /**
     * Numeric expectation for a discrete distribution. If the distribution maps onto a hipparchus
     * implementation, the sum runs over the quantile window with overflow safe probabilities and an
     * adaptively extended tail; otherwise a bounded support is enumerated or the computation falls
     * back to {@link NSum}.
     */
    private static IExpr nExpectationDiscrete(IExpr xExpr, IExpr x, IExpr distribution,
        EvalEngine engine) {
      IDiscreteDistribution dist = getDiscreteDistribution(distribution);
      long supportLo = DistributionRegion.supportLowerBound(dist, distribution);
      long supportHi = DistributionRegion.supportUpperBound(dist, distribution);
      IExpr remainder = xExpr;
      long[] restriction = null;
      IExpr[] extracted = extractBooleFactor(xExpr, x);
      if (extracted != null) {
        long[] windows = DistributionRegion.integerWindows((IAST) extracted[1], engine);
        if (windows != null) {
          remainder = extracted[0];
          restriction = DistributionRegion.clampWindows(windows, supportLo, supportHi);
        }
      }
      IntegerDistribution hipparchus = DistributionRegion.hipparchusDiscrete(distribution);
      if (hipparchus != null && x.isSymbol()) {
        DoubleUnaryOperator function = DistributionRegion.compile(remainder, x, engine);
        if (function != null) {
          double result;
          if (restriction == null) {
            result =
                DistributionRegion.expectationNumeric(hipparchus, function, supportLo, supportHi);
          } else {
            long[] windows = DistributionRegion.clampWindows(restriction, Integer.MIN_VALUE + 2L,
                Integer.MAX_VALUE - 2L);
            long[] quantile = DistributionRegion.quantileWindow(hipparchus);
            if (quantile != null) {
              // avoid enumerating huge index ranges where the probability mass vanishes
              windows = DistributionRegion.clampWindows(windows, quantile[0] - 1, quantile[1] + 1);
            }
            result = DistributionRegion.kahanSum(
                j -> function.applyAsDouble(j) * hipparchus.probability((int) j), null, windows);
          }
          if (!Double.isNaN(result)) {
            return F.num(result);
          }
        }
      }
      IExpr pdf = S.PDF.ofNIL(engine, distribution, x);
      if (pdf.isNIL()) {
        return F.NIL;
      }
      if (x.isSymbol() && supportLo != Long.MIN_VALUE && supportHi != Long.MAX_VALUE) {
        long[] windows = restriction != null ? restriction : new long[] {supportLo, supportHi};
        if (DistributionRegion
            .countWindows(windows) <= DistributionRegion.NUMERIC_ENUMERATION_LIMIT) {
          DoubleUnaryOperator term = DistributionRegion.compile(F.Times(remainder, pdf), x, engine);
          if (term != null) {
            double result = DistributionRegion.kahanSum(term, null, windows);
            if (!Double.isNaN(result)) {
              return F.num(result);
            }
          }
        }
      }
      IExpr lower = supportLo == Long.MIN_VALUE ? F.CNInfinity : F.ZZ(supportLo);
      IExpr upper = supportHi == Long.MAX_VALUE ? F.CInfinity : F.ZZ(supportHi);
      return NSum.nsum(F.Times(xExpr, pdf), x, lower, upper);
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol,
          new IBuiltInSymbol[] {S.Method, S.Assumptions, S.GenerateConditions, S.WorkingPrecision,
              S.AccuracyGoal, S.PrecisionGoal},
          new IExpr[] {S.Automatic, S.Automatic, S.False, S.Automatic, S.Automatic, S.Automatic});
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  private static class NProbability extends AbstractFunctionOptionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] options,
        final EvalEngine engine, IAST originalAST) {
      if (argSize != 2) {
        return F.NIL;
      }
      try {
        IExpr predicate = ast.arg1();
        IExpr arg2 = ast.arg2();
        if (arg2.isList() && !isDistributedList(arg2)) {
          if (predicate.isFunction()) {
            IAST data = (IAST) arg2;
            // Sum( Boole(predicate), data ) / data.argSize()
            int sum = 0;
            for (int i = 1; i < data.size(); i++) {
              if (engine.evalTrue(predicate, data.get(i))) {
                sum++;
              }
            }
            return engine.evalN(F.QQ(sum, data.argSize()));
          }
          return F.NIL;
        }
        if (isDistributedList(arg2)) {
          // independent random variables: compute symbolically and numericize
          IExpr temp = evaluateNonNumeric(F.Probability(predicate, arg2), engine);
          if (temp.isFree(S.Probability, true)) {
            return engine.evalN(temp);
          }
          return F.NIL;
        }
        if (arg2.isAST(S.Distributed, 3)) {
          IExpr x = arg2.first();
          IExpr distribution = arg2.second();
          if (predicate.isTrue()) {
            return F.CD1;
          }
          if (predicate.isFalse()) {
            return F.CD0;
          }
          if (predicate.isAST(S.Conditioned, 3)) {
            // P(a | b) == P(a && b)/P(b)
            IExpr joint = engine.evaluate(
                F.binaryAST2(S.NProbability, F.And(predicate.first(), predicate.second()), arg2));
            IExpr condition =
                engine.evaluate(F.binaryAST2(S.NProbability, predicate.second(), arg2));
            if (joint.isNumber() && condition.isNumber() && !condition.isZero()) {
              return F.Divide(joint, condition);
            }
            return F.NIL;
          }
          if (distribution.isList()) {
            IAST data = (IAST) distribution;
            // Sum( Boole(predicate), data ) / data.argSize()
            int sum = 0;
            for (int i = 1; i < data.size(); i++) {
              if (engine.evalTrue(F.subst(predicate, x, data.get(i)))) {
                sum++;
              }
            }
            return engine.evalN(F.QQ(sum, data.argSize()));
          }
          int samples = monteCarloSamples(options);
          if (samples > 0) {
            IExpr result = monteCarloProbability(predicate, x, distribution, samples, engine);
            if (result.isPresent()) {
              return result;
            }
          }
          IAST region = DistributionRegion.regionFromPredicate(predicate, x);
          if (region.isNIL()) {
            // Reduce may solve nonlinear predicates; the result is verified by sampling
            region = DistributionRegion.regionFromPredicateViaReduce(predicate, x, engine);
          }
          if (distribution.isDiscreteDistribution()) {
            IExpr result = nProbabilityDiscrete(predicate, x, distribution, region, engine);
            if (result.isPresent()) {
              return result;
            }
          } else if (distribution.isContinuousDistribution()) {
            IExpr result = nProbabilityContinuous(predicate, x, distribution, region, engine);
            if (result.isPresent()) {
              return result;
            }
          }
        }
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        return Errors.printMessage(S.NProbability, rex, engine);
      }
      return F.NIL;
    }

    /**
     * Machine probability for a continuous distribution: numeric CDF differences over the region
     * pieces, numeric integration of the density over the pieces as first fallback and the
     * <code>Boole</code> integrand over the whole real axis as last resort.
     */
    private static IExpr nProbabilityContinuous(IExpr predicate, IExpr x, IExpr distribution,
        IAST region, EvalEngine engine) {
      if (region.isPresent()) {
        double result =
            DistributionRegion.probabilityNumericContinuous(distribution, region, engine);
        if (!Double.isNaN(result)) {
          return F.num(Math.min(1.0, Math.max(0.0, result)));
        }
        IExpr pdf = S.PDF.ofNIL(engine, distribution, x);
        if (pdf.isPresent()) {
          IExpr integrated = integrateOverRegion(pdf, x, region, S.NIntegrate, engine);
          if (integrated.isPresent()) {
            return integrated;
          }
        }
      }
      IExpr pdf = S.PDF.ofNIL(engine, distribution, x);
      if (pdf.isPresent()) {
        return engine.evaluate(
            F.NIntegrate(F.Times(F.Boole(predicate), pdf), F.List(x, F.CNInfinity, F.CInfinity)));
      }
      return F.NIL;
    }

    /**
     * Machine probability for a discrete distribution: cumulative probability differences of the
     * hipparchus implementation over the integer windows, numeric CDF differences as first
     * fallback, then predicate filtered summation over the quantile window and finally
     * <code>NSum</code> over the support.
     */
    private static IExpr nProbabilityDiscrete(IExpr predicate, IExpr x, IExpr distribution,
        IAST region, EvalEngine engine) {
      IDiscreteDistribution dist = getDiscreteDistribution(distribution);
      long supportLo = DistributionRegion.supportLowerBound(dist, distribution);
      long supportHi = DistributionRegion.supportUpperBound(dist, distribution);
      IntegerDistribution hipparchus = DistributionRegion.hipparchusDiscrete(distribution);
      if (region.isPresent()) {
        long[] windows = DistributionRegion.integerWindows(region, engine);
        if (windows != null) {
          windows = DistributionRegion.clampWindows(windows, supportLo, supportHi);
          if (windows.length == 0) {
            return F.CD0;
          }
          if (hipparchus != null) {
            double result = DistributionRegion.windowsProbability(hipparchus, windows);
            if (!Double.isNaN(result)) {
              return F.num(result);
            }
          }
          double result =
              DistributionRegion.probabilityNumericDiscrete(distribution, windows, engine);
          if (!Double.isNaN(result)) {
            return F.num(Math.min(1.0, Math.max(0.0, result)));
          }
          IExpr pdf = S.PDF.ofNIL(engine, distribution, x);
          if (pdf.isPresent() && x.isSymbol()) {
            DoubleUnaryOperator term = DistributionRegion.compile(pdf, x, engine);
            if (term != null) {
              double sum = DistributionRegion.kahanSum(term, null, windows);
              if (!Double.isNaN(sum)) {
                return F.num(Math.min(1.0, Math.max(0.0, sum)));
              }
            }
          }
        }
      }
      // arbitrary predicate: enumerate the quantile window and filter with the predicate
      if (hipparchus != null && x.isSymbol()) {
        long[] window = DistributionRegion.quantileWindow(hipparchus);
        if (window != null) {
          long[] windows = DistributionRegion.clampWindows(new long[] {window[0], window[1]},
              supportLo, supportHi);
          boolean[] undecidable = new boolean[] {false};
          LongPredicate filter = j -> {
            IExpr truth = DistributionRegion.definiteTruthValue(predicate, x, j, engine);
            if (truth.isNIL()) {
              undecidable[0] = true;
              return false;
            }
            return truth.isTrue();
          };
          double result =
              DistributionRegion.kahanSum(j -> hipparchus.probability((int) j), filter, windows);
          if (!undecidable[0] && !Double.isNaN(result)) {
            return F.num(Math.min(1.0, Math.max(0.0, result)));
          }
        }
      }
      IExpr pdf = S.PDF.ofNIL(engine, distribution, x);
      if (pdf.isPresent() && predicate.isNumericFunction(x)) {
        IExpr lower = supportLo == Long.MIN_VALUE ? F.CNInfinity : F.ZZ(supportLo);
        IExpr upper = supportHi == Long.MAX_VALUE ? F.CInfinity : F.ZZ(supportHi);
        return engine.evaluate(F.NSum(F.Times(F.Boole(predicate), pdf), F.List(x, lower, upper)));
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol,
          new IBuiltInSymbol[] {S.Method, S.Assumptions, S.GenerateConditions, S.WorkingPrecision,
              S.AccuracyGoal, S.PrecisionGoal},
          new IExpr[] {S.Automatic, S.Automatic, S.False, S.Automatic, S.Automatic, S.Automatic});
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }


  private static final class InterquartileRange extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = normalizeData(ast.arg1());
      if (isQuantityVector(arg1)) {
        // a message from here is the whole answer; do not fall through to the gate as well
        return quantityStatistic(ast.setAtCopy(1, arg1), engine);
      }
      // InterquartileRange(data, {{a,b},{c,d}}) uses that parameterization for both quartiles
      IExpr parameter = ast.isAST2() ? ast.arg2() : F.NIL;

      if (arg1.isAST(S.WeightedData, 3) && arg1.first().isList() && arg1.second().isList()) {
        // the empirical distribution, not the data list - see Median#weightedQuartile
        IExpr lower = Median.weightedQuantile((IAST) arg1, F.C1D4, engine);
        IExpr upper = Median.weightedQuantile((IAST) arg1, F.C3D4, engine);
        return lower.isPresent() && upper.isPresent() ? F.Subtract(upper, lower) : F.NIL;
      }

      final IntList dimensions =
          LinearAlgebraUtil.dimensions(arg1, S.List, Integer.MAX_VALUE, false);
      if (dimensions.size() >= 2) {
        // columnwise, like every other head in this family. This has to come before the vector
        // branch below: the quartiles of a 3-column matrix are themselves a 3-list, which that
        // branch would otherwise read as one vector's {q1,q2,q3}.
        if (dimensions.size() == 2) {
          final IExpr param = parameter;
          return arg1.mapMatrixColumns(dimensions.toIntArray(),
              x -> param.isNIL() ? F.InterquartileRange(x)
                  : F.binaryAST2(S.InterquartileRange, x, param))
              .normal(false);
        }
        return F.ArrayReduce(F.Function(ast.setAtCopy(1, F.Slot1)), arg1, F.C1);
      }
      if (dimensions.size() == 1) {
        IExpr resultQuartiles = parameter.isNIL() //
            ? engine.evaluate(F.Quartiles(arg1))
            : engine.evaluate(F.binaryAST2(S.Quartiles, arg1, parameter));
        if (resultQuartiles.isList3()) {
          return F.Subtract(resultQuartiles.getAt(3), resultQuartiles.getAt(1));
        }
        return F.NIL;
      }
      if (arg1.isDistribution() && parameter.isNIL()) {
        return F.Subtract(F.Quantile(arg1, F.C3D4), F.Quantile(arg1, F.C1D4));
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      super.setUp(newSymbol);
    }
  }


  /**
   *
   *
   * <pre>
   * <code>KolmogorovSmirnovTest(data)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * Computes the <code>p-value</code>, or <i>observed significance level</i>, of a one-sample
   * <a href="http://en.wikipedia.org/wiki/Kolmogorov-Smirnov_test">Wikipedia:Kolmogorov-Smirnov
   * test</a> evaluating the null hypothesis that <code>data</code> conforms to the <code>
   * NormalDistribution()</code>.
   *
   * </blockquote>
   *
   * <pre>
   * <code>KolmogorovSmirnovTest(data, distribution)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * Computes the <code>p-value</code>, or <i>observed significance level</i>, of a one-sample
   * <a href="http://en.wikipedia.org/wiki/Kolmogorov-Smirnov_test">Wikipedia:Kolmogorov-Smirnov
   * test</a> evaluating the null hypothesis that <code>data</code> conforms to the (continuous)
   * <code>distribution</code>.
   *
   * </blockquote>
   *
   * <pre>
   * <code>KolmogorovSmirnovTest(data, distribution, &quot;TestData&quot;)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * Computes the <code>test statistic</code> and the <code>p-value</code>, or <i>observed
   * significance level</i>, of a one-sample
   * <a href="http://en.wikipedia.org/wiki/Kolmogorov-Smirnov_test">Wikipedia:Kolmogorov-Smirnov
   * test</a> evaluating the null hypothesis that <code>data</code> conforms to the (continuous)
   * <code>distribution</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; data = { 0.53236606, -1.36750258, -1.47239199, -0.12517888, -1.24040594, 1.90357309,
   *             -0.54429527, 2.22084140, -1.17209146, -0.68824211, -1.75068914, 0.48505896,
   *             2.75342248, -0.90675303, -1.05971929, 0.49922388, -1.23214498, 0.79284888,
   *             0.85309580, 0.17903487, 0.39894754, -0.52744720, 0.08516943, -1.93817962,
   *             0.25042913, -0.56311389, -1.08608388, 0.11912253, 2.87961007, -0.72674865,
   *             1.11510699, 0.39970074, 0.50060532, -0.82531807, 0.14715616, -0.96133601,
   *             -0.95699473, -0.71471097, -0.50443258, 0.31690224, 0.04325009, 0.85316056,
   *             0.83602606, 1.46678847, 0.46891827, 0.69968175, 0.97864326, 0.66985742, -0.20922486, -0.15265994}
   *
   * &gt;&gt; KolmogorovSmirnovTest(data)
   * 0.744855
   *
   * &gt;&gt; KolmogorovSmirnovTest(data, NormalDistribution(), &quot;TestData&quot;)
   * {0.0930213,0.744855}
   * </code>
   * </pre>
   */
  private static final class KolmogorovSmirnovTest extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        if (ast.isAST1()) {
          // KolmogorovSmirnovTest(data1)
          double[] data1 = ast.arg1().toDoubleVector();
          if (data1 != null && data1.length > 0) {
            org.hipparchus.stat.inference.KolmogorovSmirnovTest test =
                new org.hipparchus.stat.inference.KolmogorovSmirnovTest();
            double d = test.kolmogorovSmirnovTest(
                new org.hipparchus.distribution.continuous.NormalDistribution(), data1, false);
            return F.num(d);
          }
        } else if (ast.size() == 3 || ast.size() == 4) {
          int property = 0;
          if (ast.size() == 4) {
            IExpr arg3 = ast.arg3();
            if (!arg3.isString()) {
              return F.NIL;
            }
            IStringX str = (IStringX) arg3;
            if (str.toString().equals("PValue")) {
              // KolmogorovSmirnovTest(data1, data2, "PValue")
              property = 0;
            } else if (str.toString().equals("TestData")) {
              // KolmogorovSmirnovTest(data1, data2, "TestData")
              property = 1;
            } else {
              return F.NIL;
            }
          }
          int len1 = ast.arg1().isVector();
          if (len1 > 0) {
            double[] data1 = ast.arg1().toDoubleVector();
            if (data1 != null) {
              double d, p;
              int len2 = ast.arg2().isVector();
              if (len2 > 0) {
                double[] data2 = ast.arg2().toDoubleVector();
                if (data2 != null) {
                  // KolmogorovSmirnovTest(data1, data2)
                  org.hipparchus.stat.inference.KolmogorovSmirnovTest test =
                      new org.hipparchus.stat.inference.KolmogorovSmirnovTest();
                  switch (property) {
                    case 0:
                      p = test.kolmogorovSmirnovTest(data1, data2, false);
                      return F.num(p);
                    case 1:
                      p = test.kolmogorovSmirnovTest(data1, data2, false);
                      d = test.kolmogorovSmirnovStatistic(data1, data2);
                      return new ASTRealVector(new double[] {d, p}, false);
                  }
                }
                return F.NIL;
              }
              IExpr head = ast.arg2().head();
              if (head instanceof IBuiltInSymbol) {
                IEvaluator evaluator = ((IBuiltInSymbol) head).getEvaluator();
                if (evaluator instanceof IDistribution) {
                  RealDistribution dist = ((IDistribution) evaluator).dist();
                  if (dist != null) {
                    // KolmogorovSmirnovTest(data1, dist)
                    org.hipparchus.stat.inference.KolmogorovSmirnovTest test =
                        new org.hipparchus.stat.inference.KolmogorovSmirnovTest();
                    switch (property) {
                      case 0:
                        p = test.kolmogorovSmirnovTest(dist, data1, false);
                        return F.num(p);
                      case 1:
                        p = test.kolmogorovSmirnovTest(dist, data1, false);
                        d = test.kolmogorovSmirnovStatistic(dist, data1);
                        return new ASTRealVector(new double[] {d, p}, false);
                    }
                  }
                }
              }
            }
          }
        }
      } catch (MathRuntimeException mrex) {
        return Errors.printMessage(S.KolmogorovSmirnovTest, mrex, engine);
      }
      return F.NIL;
    }
  }


  /**
   *
   *
   * <pre>
   * Kurtosis(list)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * gives the Pearson measure of kurtosis for <code>list</code> (a measure of existing outliers).
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Kurtosis({1.1, 1.2, 1.4, 2.1, 2.4})
   * 1.42098
   * </pre>
   */
  private static final class Kurtosis extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isAST()) {
        if (arg1.isList()) {
          IAST list = (IAST) arg1;
          if (list.argSize() < 2) {
            // The argument `1` should have at least `2` elements.
            return Errors.printMessage(ast.topHead(), "shlen", F.List(list, F.C2), engine);
          }
          IExpr centralMoment = engine.evaluate(F.CentralMoment(list, 2));
          if (centralMoment.isPossibleZero(true, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
            return S.Indeterminate;
          }
          // TODO
          // if (engine.isNumericMode() || list.isNumericArgument(true)) {
          // double[] doubleVector = list.toDoubleVector();
          // if (doubleVector != null) {
          // DescriptiveStatistics descriptiveStatistics =
          // new org.hipparchus.stat.descriptive.DescriptiveStatistics(doubleVector);
          // return F.num(descriptiveStatistics.getKurtosis());
          // }
          // }
          return F.Divide(F.CentralMoment(list, 4), F.Power(centralMoment, F.C2));
        }

        IAST dist = (IAST) arg1;
        if (dist.head().isSymbol()) {
          ISymbol head = (ISymbol) dist.head();
          if (dist.head().isSymbol()) {
            if (head instanceof IBuiltInSymbol) {
              IEvaluator evaluator = ((IBuiltInSymbol) head).getEvaluator();
              if (evaluator instanceof ICentralMoment) {
                ICentralMoment centralMoment = (ICentralMoment) evaluator;
                dist = centralMoment.checkParameters(dist);
                if (dist.isPresent()) {
                  IExpr result = centralMoment.kurtosis(dist, engine);
                  if (result.isPresent()) {
                    return result;
                  }
                }
              }
            }
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }


  /**
   *
   *
   * <pre>
   * Mean(list)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the statistical mean of <code>list</code>.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Mean">Wikipedia - Mean</a>
   * </ul>
   *
   * <p>
   * <code>Mean</code> can be applied to the following distributions:
   *
   * <blockquote>
   *
   * <p>
   * <a href="BernoulliDistribution.md">BernoulliDistribution</a>,
   * <a href="BinomialDistribution.md">BinomialDistribution</a>,
   * <a href="DiscreteUniformDistribution.md">DiscreteUniformDistribution</a>,
   * <a href="ErlangDistribution.md">ErlangDistribution</a>,
   * <a href="ExponentialDistribution.md">ExponentialDistribution</a>,
   * <a href="FrechetDistribution.md">FrechetDistribution</a>,
   * <a href="GammaDistribution.md">GammaDistribution</a>,
   * <a href="GeometricDistribution.md">GeometricDistribution</a>,
   * <a href="GumbelDistribution.md">GumbelDistribution</a>,
   * <a href="HypergeometricDistribution.md">HypergeometricDistribution</a>,
   * <a href="LogNormalDistribution.md">LogNormalDistribution</a>,
   * <a href="NakagamiDistribution.md">NakagamiDistribution</a>,
   * <a href="NormalDistribution.md">NormalDistribution</a>,
   * <a href="PoissonDistribution.md">PoissonDistribution</a>,
   * <a href="StudentTDistribution.md">StudentTDistribution</a>,
   * <a href="WeibullDistribution.md">WeibullDistribution</a>
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Mean({26, 64, 36})
   * 42
   *
   * &gt;&gt; Mean({1, 1, 2, 3, 5, 8})
   * 10/3
   *
   * &gt;&gt; Mean({a, b})
   * 1/2*(a+b)
   * </pre>
   *
   * <p>
   * The <a href="https://en.wikipedia.org/wiki/Mean">mean</a> of the normal distribution is
   *
   * <pre>
   * &gt;&gt; Mean(NormalDistribution(m, s))
   * m
   * </pre>
   */
  private static final class Mean extends AbstractTrigArg1 {

    @Override
    public boolean evalIsReal(IAST ast) {
      return false;
    }

    @Override
    public IExpr evaluateArg1(final IExpr argument, EvalEngine engine) {
      // the mean of a dataset is the mean of its rows - see IASTDataset#normalizeDataset
      final IExpr arg1 = IASTDataset.normalizeDataset(argument);
      try {
        if (arg1.isRealVector()) {
          double[] values = arg1.toDoubleVector();
          if (values == null) {
            return F.NIL;
          }
          return F.num(StatUtils.mean(values));
        }
        if (arg1.isListOrAssociation()) {
          final IAST list = (IAST) arg1;
          return F.Times(list.apply(S.Plus), F.Power(F.ZZ(list.argSize()), F.CN1));
        }

        if (arg1.isDistribution()) {
          return getDistribution(arg1).mean((IAST) arg1);
        }
        final IntList dimensions =
            LinearAlgebraUtil.dimensions(arg1, S.List, Integer.MAX_VALUE, false);
        if (dimensions.size() >= 2) {
          return F.ArrayReduce(S.Variance, arg1, F.C1);
        }
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        return Errors.printMessage(S.Mean, rex, engine);
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.NOATTRIBUTE);
    }
  }


  /**
   * <code>DistributionParameterQ(dist)</code> - test whether the parameters of <code>dist</code>
   * are consistent.
   *
   * <p>
   * Symbolic parameters are assumed to be valid, so only an explicitly {@link S#False} assumption
   * makes the result {@link S#False}. For an argument which is not a distribution the expression
   * stays unevaluated.
   */
  private static final class DistributionParameterQ extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (!arg1.isAST() || !arg1.head().isBuiltInSymbol()) {
        return F.NIL;
      }
      IEvaluator evaluator = ((IBuiltInSymbol) arg1.head()).getEvaluator();
      if (!(evaluator instanceof IDistribution)) {
        return F.NIL;
      }
      IAST dist = (IAST) arg1;
      IDistribution distribution = (IDistribution) evaluator;
      IExpr assumptions = distribution.parameterAssumptions(dist);
      if (assumptions.isNIL()) {
        // no assumptions known for this distribution: only the argument count can be checked
        return F.bool(distribution.checkParameters(dist).isPresent());
      }
      IExpr condition = engine.evaluate(assumptions);
      if (condition.isFalse()) {
        return S.False;
      }
      return S.True;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.NOATTRIBUTE);
    }
  }

  /**
   * <code>FindDistributionParameters(data, dist)</code> - maximum likelihood estimates for the
   * symbolic parameters of <code>dist</code>.
   */
  private static final class FindDistributionParameters extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.isAST2() || !ast.arg2().isAST2()) {
        return F.NIL;
      }
      double[] data = ast.arg1().toDoubleVector();
      if (data == null || data.length == 0) {
        return F.NIL;
      }
      IAST dist = (IAST) ast.arg2();
      IExpr firstParameter = dist.arg1();
      IExpr secondParameter = dist.arg2();
      if (!firstParameter.isSymbol() || !secondParameter.isSymbol()) {
        return F.NIL;
      }
      if (dist.isAST(S.NormalDistribution, 3)) {
        double mean = StatUtils.mean(data);
        double variance = 0.0;
        for (int i = 0; i < data.length; i++) {
          variance += (data[i] - mean) * (data[i] - mean);
        }
        // maximum likelihood uses the population standard deviation
        return F.list(F.Rule(firstParameter, F.num(mean)),
            F.Rule(secondParameter, F.num(Math.sqrt(variance / data.length))));
      }
      if (dist.isAST(S.LaplaceDistribution, 3)) {
        double[] sorted = data.clone();
        Arrays.sort(sorted);
        int n = sorted.length;
        double median = (n % 2 == 1) ? sorted[n / 2] //
            : (sorted[n / 2 - 1] + sorted[n / 2]) / 2.0;
        double meanAbsoluteDeviation = 0.0;
        for (int i = 0; i < n; i++) {
          meanAbsoluteDeviation += Math.abs(sorted[i] - median);
        }
        return F.list(F.Rule(firstParameter, F.num(median)),
            F.Rule(secondParameter, F.num(meanAbsoluteDeviation / n)));
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.NOATTRIBUTE);
    }
  }

  /**
   * <code>HazardFunction(dist, x)</code> - the hazard function <code>PDF/SurvivalFunction</code>.
   */
  private static final class HazardFunction extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.arg1().isAST()) {
        return F.NIL;
      }
      IAST dist = (IAST) ast.arg1();
      if (!dist.isContinuousDistribution()) {
        // for discrete distributions WMA uses a different definition, which is not supported yet
        return F.NIL;
      }
      if (ast.isAST1()) {
        return IPDF.operatorForm(F.HazardFunction(dist, F.Slot1));
      }
      IExpr x = ast.arg2();
      if (x.isList()) {
        return ((IAST) x).mapThread(ast, 2);
      }
      IExpr pdf = engine.evaluate(F.PDF(dist, x));
      if (pdf.isAST(S.PDF)) {
        return F.NIL;
      }
      IExpr survival = engine.evaluate(F.SurvivalFunction(dist, x));
      if (survival.isAST(S.SurvivalFunction)) {
        return F.NIL;
      }
      if (pdf.isAST(S.Piecewise, 3) && survival.isAST(S.Piecewise, 3)) {
        // divide the matching branches instead of dividing the two Piecewise expressions
        IExpr quotient = dividePiecewise((IAST) pdf, (IAST) survival, engine);
        if (quotient.isPresent()) {
          return quotient;
        }
      }
      return F.Divide(pdf, survival);
    }

    /**
     * Divide two {@link S#Piecewise} expressions which use exactly the same conditions, by dividing
     * the values of the corresponding branches.
     */
    private static IExpr dividePiecewise(IAST pdf, IAST survival, EvalEngine engine) {
      IAST pdfBranches = (IAST) pdf.arg1();
      IAST survivalBranches = (IAST) survival.arg1();
      if (pdfBranches.size() != survivalBranches.size()) {
        return F.NIL;
      }
      IASTAppendable branches = F.ListAlloc(pdfBranches.argSize());
      for (int i = 1; i < pdfBranches.size(); i++) {
        IExpr p = pdfBranches.get(i);
        IExpr s = survivalBranches.get(i);
        if (!p.isList2() || !s.isList2() || !p.second().equals(s.second())) {
          return F.NIL;
        }
        branches.append(F.list(engine.evaluate(F.Divide(p.first(), s.first())), p.second()));
      }
      return F.Piecewise(branches, engine.evaluate(F.Divide(pdf.arg2(), survival.arg2())));
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
   * <code>InverseSurvivalFunction(dist, q)</code> - the <code>(1-q)</code>th quantile of
   * <code>dist</code>.
   */
  private static final class InverseSurvivalFunction extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.arg1().isAST()) {
        return F.NIL;
      }
      IAST dist = (IAST) ast.arg1();
      if (!dist.isDistribution()) {
        return F.NIL;
      }
      if (ast.isAST1()) {
        return IPDF.operatorForm(F.InverseSurvivalFunction(dist, F.Slot1));
      }
      IExpr q = ast.arg2();
      if (q.isList()) {
        return ((IAST) q).mapThread(ast, 2);
      }
      return F.Quantile(dist, F.Subtract(F.C1, q));
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
   * <code>MedianDeviation(list)</code> - the median absolute deviation from the median of the
   * elements in <code>list</code>.
   */
  private static final class MedianDeviation extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // an Association contributes its values, a Dataset its rows - see #normalizeData
      IExpr arg1 = normalizeData(ast.arg1());
      if (isQuantityVector(arg1)) {
        // a message from here is the whole answer; do not fall through to the gate as well
        return quantityStatistic(ast.setAtCopy(1, arg1), engine);
      }

      final IntList dimensions =
          LinearAlgebraUtil.dimensions(arg1, S.List, Integer.MAX_VALUE, false);
      if (dimensions.size() != 0) {
        switch (dimensions.size()) {
          case 1:
            arg1 = arg1.normal(false);
            if (arg1.isList()) {
              IAST vector = (IAST) arg1;
              IExpr median = engine.evaluate(F.Median(vector));
              if (median.isAST(S.Median)) {
                return F.NIL;
              }
              return F.Median(vector.map(x -> F.Abs(F.Subtract(x, median)), 1));
            }
            return F.NIL;
          case 2:
            return arg1.mapMatrixColumns(dimensions.toIntArray(), x -> F.MedianDeviation(x))
                .normal(false);
          default:
            return F.ArrayReduce(S.MedianDeviation, arg1, F.C1);
        }
      }
      if (arg1.isNumber()) {
        // Rectangular array expected at position `1` in `2`.
        return Errors.printMessage(ast.topHead(), "rectt", F.list(F.C1, ast), engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.NOATTRIBUTE);
    }
  }

  private static final class MeanDeviation extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // an Association contributes its values, a Dataset its rows - see #normalizeData
      IExpr arg1 = normalizeData(ast.arg1());
      if (isQuantityVector(arg1)) {
        // a message from here is the whole answer; do not fall through to the gate as well
        return quantityStatistic(ast.setAtCopy(1, arg1), engine);
      }

      final IntList dimensions =
          LinearAlgebraUtil.dimensions(arg1, S.List, Integer.MAX_VALUE, false);
      if (dimensions.size() != 0) {
        switch (dimensions.size()) {
          case 1:
            int length = dimensions.getInt(0);
            if (length == 0) {
              // Argument `1` is neither a nonempty vector nor a nonempty matrix.
              return Errors.printMessage(ast.topHead(), "vecmat1", F.list(ast.arg1()), engine);
            }
            if (arg1.isRealVector()) {
              double[] values = arg1.toDoubleVector();
              if (values == null) {
                return F.NIL;
              }
              double dataMean = StatisticsFunctions.mean(values);
              double[] newValues = new double[length];
              for (int i = 0; i < length; i++) {
                newValues[i] = Math.abs(values[i] - dataMean);
              }
              return F.num(StatisticsFunctions.mean(newValues));
            }
            arg1 = arg1.normal(false);
            if (arg1.isList()) {
              IAST vector = (IAST) arg1;
              int size = vector.size();
              IASTAppendable sum = F.PlusAlloc(size);
              final IExpr mean = S.Mean.funEval(engine, vector.negate());
              vector.forEach(x -> sum.append(F.Abs(F.Plus(x, mean))));
              return F.Times(F.Power(F.ZZ(size - 1), -1), sum);
            }
            return F.NIL;
          case 2:
            return arg1.mapMatrixColumns(dimensions.toIntArray(), x -> F.MeanDeviation(x))
                .normal(false);
          default:
            return F.ArrayReduce(S.MeanDeviation, arg1, F.C1);
        }
      }
      if (arg1.isNumber()) {
        // Rectangular array expected at position `1` in `2`.
        return Errors.printMessage(ast.topHead(), "rectt", F.list(F.C1, ast), engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.NOATTRIBUTE);
    }
  }


  /**
   *
   *
   * <pre>
   * Median(list)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the median of <code>list</code>.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Median">Wikipedia - Median</a>
   * </ul>
   *
   * <p>
   * <code>Median</code> can be applied to the following distributions:
   *
   * <blockquote>
   *
   * <p>
   * <a href="BernoulliDistribution.md">BernoulliDistribution</a>,
   * <a href="BinomialDistribution.md">BinomialDistribution</a>,
   * <a href="DiscreteUniformDistribution.md">DiscreteUniformDistribution</a>,
   * <a href="ErlangDistribution.md">ErlangDistribution</a>,
   * <a href="ExponentialDistribution.md">ExponentialDistribution</a>,
   * <a href="FrechetDistribution.md">FrechetDistribution</a>,
   * <a href="GammaDistribution.md">GammaDistribution</a>,
   * <a href="GeometricDistribution.md">GeometricDistribution</a>,
   * <a href="GumbelDistribution.md">GumbelDistribution</a>,
   * <a href="HypergeometricDistribution.md">HypergeometricDistribution</a>,
   * <a href="LogNormalDistribution.md">LogNormalDistribution</a>,
   * <a href="NakagamiDistribution.md">NakagamiDistribution</a>,
   * <a href="NormalDistribution.md">NormalDistribution</a>,
   * <a href="StudentTDistribution.md">StudentTDistribution</a>,
   * <a href="WeibullDistribution.md">WeibullDistribution</a>
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Median({26, 64, 36})
   * 36
   * </pre>
   *
   * <p>
   * For lists with an even number of elements, Median returns the mean of the two middle values:
   *
   * <pre>
   * &gt;&gt; Median({-11, 38, 501, 1183})
   * 539/2
   * </pre>
   *
   * <p>
   * Passing a matrix returns the medians of the respective columns:
   *
   * <pre>
   * &gt;&gt; Median({{100, 1, 10, 50}, {-1, 1, -2, 2}})
   * {99/2,1,4,26}
   *
   * &gt;&gt; Median(LogNormalDistribution(m,s))
   * E^m
   * </pre>
   */
  public static class Median extends AbstractTrigArg1 {

    @Override
    public boolean evalIsReal(IAST ast) {
      return false;
    }

    /**
     * See <a href="https://stackoverflow.com/a/4859279/24819">Get the indices of an array after
     * sorting?</a>
     */
    private static final class ArrayIndexComparator implements Comparator<Integer> {
      protected final IAST ast;
      protected EvalEngine engine;

      public ArrayIndexComparator(IAST ast, EvalEngine engine) {
        this.ast = ast;
        this.engine = engine;
      }

      public Integer[] createIndexArray() {
        int size = ast.size();
        Integer[] indexes = new Integer[size - 1];
        for (int i = 1; i < size; i++) {
          indexes[i - 1] = i;
        }
        return indexes;
      }

      @Override
      public int compare(Integer index1, Integer index2) {
        IExpr arg1 = ast.get(index1);
        IExpr arg2 = ast.get(index2);
        if (arg1.isNumericFunction(true) && arg2.isNumericFunction(true)) {
          if (engine.evalGreater(arg1, arg2)) {
            return 1;
          }
          if (engine.evalLess(arg1, arg2)) {
            return -1;
          }
        }
        // fall back for symbolic values
        return arg1.compareTo(arg2);
      }
    }

    @Override
    public IExpr evaluateArg1(final IExpr argument, EvalEngine engine) {
      // an Association contributes its values, a Dataset its rows - see #normalizeData
      final IExpr arg1 = normalizeData(argument);
      if (isQuantityVector(arg1)) {
        // a message from here is the whole answer; do not fall through to the gate as well
        return quantityStatistic(F.unaryAST1(S.Median, arg1), engine);
      }
      if (arg1.isRealVector()) {
        double[] values = arg1.toDoubleVector();
        if (values == null || values.length == 0) {
          return F.NIL;
        }
        return F.num(StatisticsFunctions.median(values));
      }
      if (arg1.isAST(S.WeightedData, 3) && arg1.first().isList() && arg1.second().isList()) {
        return weightedQuantile((IAST) arg1, F.C1D2, engine);
      }
      final IntList dimensions =
          LinearAlgebraUtil.dimensions(arg1, S.List, Integer.MAX_VALUE, false);
      if (dimensions.size() > 0) {
        // Rectangular array of real numbers is expected at position `1` in `2`.
        if (!isRealOrQuantityData(arg1)) {
          return Errors.printMessage(S.Median, "rectn", F.List(F.C1, F.Median(arg1)), engine);
        }
        if (dimensions.size() >= 2) {
          IExpr result = engine.evaluate(F.ArrayReduce(S.Median, arg1, F.C1));
          return result.normal(false);
        }
        if (dimensions.size() == 1) {
          IExpr normal = arg1.normal(false);
          if (normal.isList()) {
            IAST list = (IAST) normal;
            if (list.size() > 1) {
              return medianList(list);
            }
          }
        }
      }

      if (arg1.isDistribution()) {
        return getDistribution(arg1).median((IAST) arg1);
      }
      return F.NIL;
    }

    private static IExpr medianList(final IAST list) {
      final IAST sortedList = EvalAttributes.copySortLess(list);
      int size = sortedList.size();
      if ((size & 0x00000001) == 0x00000001) {
        // odd number of elements
        size = size / 2;
        return F.Times(F.Plus(sortedList.get(size), sortedList.get(size + 1)), F.C1D2);
      }
      return sortedList.get(size / 2);
    }

    /**
     * Evaluate the median value of the weighted data.
     *
     * @param weightedData
     * @param engine
     * @return <code>F.NIL</code> if the input <code>data, weight</code> lists aren't of the same
     *         length.
     */
    /**
     * The <code>q</code>-quantile of a <code>WeightedData</code>: the smallest data point whose
     * cumulative weight fraction reaches <code>q</code>, which is the inverse of the underlying
     * empirical CDF.
     *
     * <p>
     * A <code>WeightedData</code> quantile is NOT the quantile of its data list. Mathematica gives
     * <code>Quartiles[WeightedData[{1,2,3,4},{1,1,1,1}]]</code> as <code>{1,2,3}</code>, not the
     * <code>{3/2,5/2,7/2}</code> that the same numbers as a plain list produce, because the
     * parameterization never enters - the empirical distribution does.
     *
     * @param q the quantile, expected to lie in <code>[0,1]</code>
     */
    private static IExpr weightedQuantile(final IAST weightedData, IExpr q, EvalEngine engine) {
      IAST data = (IAST) weightedData.arg1();
      IAST weights = (IAST) weightedData.arg2();
      final int size = data.size();
      if (size > 1 && size == weights.size()) {
        IASTAppendable[] res = sortWeightedData(data, weights, engine);
        data = res[0];
        weights = res[1];

        if (q.isZero()) {
          // no cumulative fraction is < 0, so the loop below would select nothing; the inverse
          // CDF at 0 is the smallest data point
          return data.arg1();
        }
        IExpr denominator = engine.evaluate(weights.apply(S.Plus));
        IASTAppendable result = F.PlusAlloc(size);
        for (int i = 1; i < size; i++) {
          IASTAppendable rhs = F.PlusAlloc(size);
          for (int j = 1; j <= i; j++) {
            rhs.append(F.Divide(weights.get(j), denominator));
          }
          IExpr lhs = rhs.splice(rhs.argSize());
          // Boole( Inequality(lhs < q <= rhs) );
          IExpr boole = engine.evaluate(F.Boole(F.Inequality(lhs, S.Less, q, S.LessEqual, rhs)));
          if (boole.isOne()) {
            result.append(data.get(i));
          } else if (!boole.isZero()) {
            result.append(F.Times(data.get(i), boole));
          }
        }
        return result;
      }
      return F.NIL;
    }

    /**
     * Sort <code>data</code> (and the associated <code>weights</code>) in order from smallest to
     * greatest.
     *
     * @param data
     * @param weights
     * @param engine the evaluation engine
     * @return the sorted data at offset <code>0</code> and the new associated weights in the same
     *         order at offset <code>1</code>
     */
    private static IASTAppendable[] sortWeightedData(IAST data, IAST weights, EvalEngine engine) {
      final int size = data.size();
      ArrayIndexComparator comparator = new ArrayIndexComparator(data, engine);
      Integer[] indexes = comparator.createIndexArray();
      Arrays.sort(indexes, comparator);
      IASTAppendable newData = data.copyHead(size);
      IASTAppendable newWeights = weights.copyHead(size);
      IASTAppendable[] result = new IASTAppendable[] {newData, newWeights};
      for (int i = 0; i < indexes.length; i++) {
        newData.append(data.get(indexes[i]));
        newWeights.append(weights.get(indexes[i]));
      }
      return result;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.NOATTRIBUTE);
    }
  }


  private static final class PrincipalComponents extends AbstractFunctionOptionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] option,
        final EvalEngine engine, IAST originalAST) {
      int[] dimension = ast.arg1().isMatrix();
      if (dimension == null) {
        return F.NIL;
      }
      if (dimension[0] == 1) {
        if (dimension[1] == 0) {
          return F.CEmptyList;
        }
        return F.List(F.constantArray(F.CD0, dimension[1]));
      }

      if (dimension[0] < 1 || dimension[1] <= 0) {
        return F.NIL;
      }

      RealMatrix matrix = ast.arg1().toRealMatrix();
      if (matrix == null) {
        return F.NIL;
      }

      PCA pca = null;
      String method = option[0].toString();
      if (method.equals("Covariance")) {
        pca = new PCA(dimension[1]);
      } else if (method.equals("Correlation")) {
        pca = new PCA(dimension[1], true, true);
      } else {
        pca = new PCA(dimension[1]);
      }
      double[][] data = pca.fitAndTransform(matrix.getData());
      return Convert.matrix2List(new Array2DRowRealMatrix(data));
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, S.Method, F.stringx("Covariance"));
    }
  }


  /**
   *
   *
   * <pre>
   * Probability(pure - function, data - set)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the probability of the <code>pure-function</code> for the given <code>data-set
   * </code>.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Probability">Wikipedia - Probability</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Probability(#^2 + 3*# &lt; 11 &amp;, {-0.21848,1.67503,0.78687,4.9887,7.06587,-1.27856,0.79225,-0.01164,2.48227,-0.07223})
   * 7/10
   *
   * &gt;&gt; Probability(x^2 + 3*x &lt; 11,Distributed(x,{-0.21848,1.67503,0.78687,0.9887,2.06587,-1.27856,0.79225,-0.01164,2.48227,-0.07223}))
   * 9/10
   * </pre>
   */
  private static class Probability extends AbstractFunctionOptionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] options,
        final EvalEngine engine, IAST originalAST) {
      if (argSize != 2) {
        return F.NIL;
      }
      try {
        IExpr predicate = ast.arg1();
        IExpr arg2 = ast.arg2();
        if (arg2.isList() && !isDistributedList(arg2)) {
          if (predicate.isFunction()) {
            IAST data = (IAST) arg2;
            // Sum( Boole(predicate), data ) / data.argSize()
            int sum = 0;
            for (int i = 1; i < data.size(); i++) {
              if (engine.evalTrue(predicate, data.get(i))) {
                sum++;
              }
            }
            return F.QQ(sum, data.argSize());
          }
          return F.NIL;
        }
        if (isDistributedList(arg2)) {
          // independent random variables: P(pred) == E(Boole(pred))
          IExpr result = engine.evaluate(F.Expectation(F.Boole(predicate), arg2));
          return result.isFree(S.Expectation, true) && result.isFree(S.Probability, true) ? result
              : F.NIL;
        }
        if (arg2.isAST(S.Distributed, 3)) {
          IExpr x = arg2.first();
          IExpr distribution = arg2.second();
          if (predicate.isTrue()) {
            return F.C1;
          }
          if (predicate.isFalse()) {
            return F.C0;
          }
          if (predicate.isAST(S.Conditioned, 3)) {
            // P(a | b) == P(a && b)/P(b)
            IExpr joint =
                engine.evaluate(F.Probability(F.And(predicate.first(), predicate.second()), arg2));
            IExpr condition = engine.evaluate(F.Probability(predicate.second(), arg2));
            if (!joint.isAST(S.Probability) && !condition.isAST(S.Probability)
                && !condition.isZero()) {
              return F.Divide(joint, condition);
            }
            return F.NIL;
          }
          if (distribution.isAST(S.ProbabilityDistribution) && x.isSymbol()
              && (predicate.isRelational() || predicate.isAST(S.And))) {
            // for an explicit density the integration below can not handle the Boole() factor,
            // so use the CDF instead. Other distributions keep their existing (often simpler)
            // result.
            IExpr result = probabilityFromCDF(predicate, (ISymbol) x, distribution, engine);
            if (result.isPresent()) {
              return result;
            }
          }
          if (distribution.isList()) {
            IAST data = (IAST) distribution;
            // Sum( Boole(predicate), data ) / data.argSize()
            int sum = 0;
            for (int i = 1; i < data.size(); i++) {
              if (engine.evalTrue(F.subst(predicate, x, data.get(i)))) {
                sum++;
              }
            }
            return F.QQ(sum, data.argSize());
          } else if (distribution.isDiscreteDistribution()) {
            IExpr result = probabilityDiscrete(predicate, x, distribution, engine);
            if (result.isPresent()) {
              return result;
            }
          } else if (distribution.isContinuousDistribution()) {
            IAST region = DistributionRegion.regionFromPredicate(predicate, x);
            if (region.isPresent()) {
              // the probability is the sum of CDF differences over the region pieces
              IExpr result =
                  DistributionRegion.probabilityFromCDFContinuous(distribution, region, engine);
              if (result.isPresent()) {
                return result;
              }
            }
            IExpr pdf = S.PDF.ofNIL(engine, distribution, x);
            if (pdf.isPresent()) {
              return engine.evaluate(F.Integrate(F.Times(F.Boole(predicate), pdf),
                  F.List(x, F.CNInfinity, F.CInfinity)));
            }
          }
        }
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        return Errors.printMessage(S.Probability, rex, engine);
      }
      if (engine.isNumericMode() && ast.arg2().isAST(S.Distributed, 3)) {
        // N(Probability(...)) falls back to NProbability
        IExpr temp = engine.evaluate(F.binaryAST2(S.NProbability, ast.arg1(), ast.arg2()));
        if (temp.isFree(S.NProbability, true)) {
          return temp;
        }
      }
      return F.NIL;
    }

    /**
     * Exact probability for a discrete distribution. Strategy: enumerate the integer points of the
     * predicate region if there are only a few, otherwise enumerate the complement (
     * <code>P == 1 - P(complement)</code>), otherwise use CDF differences; the last resort is a
     * symbolic <code>Sum</code> over the support.
     */
    private static IExpr probabilityDiscrete(IExpr predicate, IExpr x, IExpr distribution,
        EvalEngine engine) {
      IDiscreteDistribution dist = getDiscreteDistribution(distribution);
      long supportLo = DistributionRegion.supportLowerBound(dist, distribution);
      long supportHi = DistributionRegion.supportUpperBound(dist, distribution);
      IExpr pdf = S.PDF.ofNIL(engine, distribution, x);
      IAST region = DistributionRegion.regionFromPredicate(predicate, x);
      if (region.isPresent()) {
        long[] windows = DistributionRegion.integerWindows(region, engine);
        if (windows != null) {
          windows = DistributionRegion.clampWindows(windows, supportLo, supportHi);
          if (windows.length == 0) {
            return F.C0;
          }
          if (pdf.isPresent() && DistributionRegion
              .countWindows(windows) <= DistributionRegion.SYMBOLIC_ENUMERATION_LIMIT) {
            IExpr result = DistributionRegion.enumerateSymbolic(pdf, x, windows, predicate, engine);
            if (result.isPresent()) {
              return result;
            }
          }
          if (supportLo != Long.MIN_VALUE) {
            long[] complement = DistributionRegion.complementWindows(windows, supportLo, supportHi);
            if (pdf.isPresent() && DistributionRegion
                .countWindows(complement) <= DistributionRegion.SYMBOLIC_ENUMERATION_LIMIT) {
              // P(pred) == 1 - P(complement)
              IExpr sum = DistributionRegion.enumerateSymbolic(pdf, x, complement, F.NIL, engine);
              if (sum.isPresent()) {
                return engine.evaluate(F.Subtract(F.C1, sum));
              }
            }
          }
          IExpr result =
              DistributionRegion.probabilityFromCDFDiscrete(distribution, windows, engine);
          if (result.isPresent()) {
            return result;
          }
        }
      }
      if (pdf.isPresent() && x.isSymbol() && supportLo != Long.MIN_VALUE) {
        long[] windows = new long[] {supportLo, supportHi};
        if (DistributionRegion
            .countWindows(windows) <= DistributionRegion.SYMBOLIC_ENUMERATION_LIMIT) {
          // a small finite support can always be enumerated with the predicate as filter
          IExpr result = DistributionRegion.enumerateSymbolic(pdf, x, windows, predicate, engine);
          if (result.isPresent()) {
            return result;
          }
        } else if (predicateEventuallyFalse(predicate, x, supportLo, engine)) {
          // predicates like E^x < 3 hold only near the lower end of the support
          long hi =
              Math.min(supportHi, supportLo + DistributionRegion.SYMBOLIC_ENUMERATION_LIMIT - 1);
          IExpr result = DistributionRegion.enumerateSymbolic(pdf, x, new long[] {supportLo, hi},
              predicate, engine);
          if (result.isPresent()) {
            return result;
          }
        }
      }
      if (pdf.isPresent()) {
        IExpr lower = supportLo == Long.MIN_VALUE ? F.CNInfinity : F.ZZ(supportLo);
        IExpr upper = supportHi == Long.MAX_VALUE ? F.CInfinity : F.ZZ(supportHi);
        IExpr sum =
            engine.evaluate(F.Sum(F.Times(F.Boole(predicate), pdf), F.List(x, lower, upper)));
        if (sum.isFree(S.Sum, true) && sum.isFree(S.Boole, true)) {
          return sum;
        }
      }
      return F.NIL;
    }

    /**
     * Heuristic test whether the predicate is false everywhere except near the lower end of the
     * support: it must evaluate to <code>False</code> at the sampled magnitudes and to a definite
     * truth value at the support start.
     */
    private static boolean predicateEventuallyFalse(IExpr predicate, IExpr x, long supportLo,
        EvalEngine engine) {
      IExpr first = DistributionRegion.definiteTruthValue(predicate, x, supportLo, engine);
      if (first.isNIL()) {
        return false;
      }
      for (long offset : new long[] {1000L, 1000000L, 1000000000L}) {
        if (!DistributionRegion.definiteTruthValue(predicate, x, supportLo + offset, engine)
            .isFalse()) {
          return false;
        }
      }
      return true;
    }

    /**
     * Compute <code>P(predicate)</code> from the CDF of the distribution, by turning the relational
     * <code>predicate</code> into an interval of the random variable.
     */
    private static IExpr probabilityFromCDF(IExpr predicate, ISymbol x, IExpr distribution,
        EvalEngine engine) {
      if (predicate.isAST(S.And)) {
        // combine the single relations into one relation, e.g. x > 1/2 && x > 1/4 -> x > 1/2
        IExpr reduced = engine.evaluate(F.Reduce(predicate, x));
        if (!reduced.isRelational()) {
          return F.NIL;
        }
        predicate = reduced;
      }
      IAST interval = IntervalDataSym.relationToIntervalSet((IAST) predicate, x);
      if (!interval.isIntervalData() || interval.argSize() != 1) {
        return F.NIL;
      }
      IAST bounds = (IAST) interval.arg1();
      IExpr min = bounds.arg1();
      IExpr max = bounds.arg4();
      IExpr upper = max.isInfinity() ? F.C1 : engine.evaluate(F.CDF(distribution, max));
      IExpr lower = min.isNegativeInfinity() ? F.C0 : engine.evaluate(F.CDF(distribution, min));
      if (upper.isAST(S.CDF) || lower.isAST(S.CDF)) {
        return F.NIL;
      }
      return engine.evaluate(F.Subtract(upper, lower));
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol,
          new IBuiltInSymbol[] {S.Method, S.Assumptions, S.GenerateConditions, S.WorkingPrecision,
              S.AccuracyGoal, S.PrecisionGoal},
          new IExpr[] {S.Automatic, S.Automatic, S.False, S.Automatic, S.Automatic, S.Automatic});
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }


  /**
   *
   *
   * <pre>
   * PDF(distribution, value)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the probability density function of <code>value</code>.
   *
   * </blockquote>
   *
   * <pre>
   * PDF(distribution, {list} )
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the probability density function of the values of list.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Probability_density_function">Wikipedia -
   * probability density function</a>
   * </ul>
   *
   * <p>
   * <code>PDF</code> can be applied to the following distributions:
   *
   * <blockquote>
   *
   * <p>
   * <a href="BernoulliDistribution.md">BernoulliDistribution</a>,
   * <a href="BinomialDistribution.md">BinomialDistribution</a>,
   * <a href="DiscreteUniformDistribution.md">DiscreteUniformDistribution</a>,
   * <a href="ErlangDistribution.md">ErlangDistribution</a>,
   * <a href="ExponentialDistribution.md">ExponentialDistribution</a>,
   * <a href="FrechetDistribution.md">FrechetDistribution</a>,
   * <a href="GammaDistribution.md">GammaDistribution</a>,
   * <a href="GeometricDistribution.md">GeometricDistribution</a>,
   * <a href="GumbelDistribution.md">GumbelDistribution</a>,
   * <a href="HypergeometricDistribution.md">HypergeometricDistribution</a>,
   * <a href="LogNormalDistribution.md">LogNormalDistribution</a>,
   * <a href="NakagamiDistribution.md">NakagamiDistribution</a>,
   * <a href="NormalDistribution.md">NormalDistribution</a>,
   * <a href="PoissonDistribution.md">PoissonDistribution</a>,
   * <a href="StudentTDistribution.md">StudentTDistribution</a>,
   * <a href="WeibullDistribution.md">WeibullDistribution</a>
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; PDF(NormalDistribution(n, m))
   * 1/(Sqrt(2)*E^((-n+#1)^2/(2*m^2))*m*Sqrt(Pi))&amp;
   *
   * &gt;&gt; PDF(GumbelDistribution(n, m),k)
   * E^(-E^((k-n)/m)+(k-n)/m)/m
   *
   * &gt;&gt; Table(PDF(NormalDistribution( ), x), {m, {-1, 1, 2}},{x, {-1, 1, 2}})//N
   * {{0.24197,0.24197,0.05399},{0.24197,0.24197,0.05399},{0.24197,0.24197,0.05399}}
   * </pre>
   */
  private static class PDF extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1() || ast.isAST2()) {
        // check because of pure function form ?
        try {
          if (ast.arg1().isAST()) {
            IAST dist = (IAST) ast.arg1();
            IExpr xArg = F.NIL;
            if (ast.isAST2()) {
              xArg = ast.arg2();
            }
            IPDF pdf = dist.headInstanceOf(IPDF.class);
            if (pdf != null) {
              dist = pdf.checkParameters(dist);
              if (dist.isPresent()) {
                return pdf.pdf(dist, xArg, engine);
              }
            }
          }
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          return Errors.printMessage(S.PDF, rex, engine);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }


  private static final class PearsonCorrelationTest extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      int dimension1 = arg1.isVector();
      int dimension2 = arg2.isVector();
      String property = "TestData";
      if (ast.isAST3()) {
        if (!ast.arg3().isString()) {
          return F.NIL;
        }
        property = ast.arg3().toString();
        if (!property.equals("TestData") //
            && !property.equals("TestStatistic") //
            && !property.equals("PValue") //
            && !property.equals("PValueTable")) {
          return F.NIL;
        }
      }
      if (dimension1 > 1 && dimension2 > 1) {
        if (dimension1 != dimension2) {
          // The argument `1` at position `2` should be a vector of real numbers with length equal
          // to the vector given at position `3`.
          return Errors.printMessage(S.PearsonCorrelationTest, "vctnln3", F.List(arg1, F.C1, F.C2),
              engine);
        }
        double[] vector1 = arg1.toDoubleVector();
        if (vector1 == null) {
          // The argument `1` at position `2` should be a vector of real numbers with length equal
          // to the vector given at position `3`.
          return Errors.printMessage(S.PearsonCorrelationTest, "vctnln3", F.List(arg1, F.C1, F.C2),
              engine);
        }
        double[] vector2 = arg2.toDoubleVector();
        if (vector2 == null) {
          // The argument `1` at position `2` should be a vector of real numbers with length equal
          // to the vector given at position `3`.
          return Errors.printMessage(S.PearsonCorrelationTest, "vctnln3", F.List(arg2, F.C2, F.C1),
              engine);
        }
        try {
          RealMatrix m = new Array2DRowRealMatrix(dimension1, dimension2);
          m.setColumn(0, vector1);
          m.setColumn(1, vector2);
          org.hipparchus.stat.correlation.PearsonsCorrelation test =
              new org.hipparchus.stat.correlation.PearsonsCorrelation(m);
          if (property.equals("TestData")) {
            return testData(vector1, vector2, test);
          }
          if (property.equals("TestStatistic")) {
            double value = test.correlation(vector1, vector2);
            return F.num(value);
          }
          if (property.equals("PValue")) {
            RealMatrix correlationPValues = test.getCorrelationPValues();
            if (correlationPValues != null) {
              double pValue = correlationPValues.getEntry(1, 0);
              return F.num(pValue);
            }
            return F.NIL;
          }
          // if (property.equals("PValueTable")) {
          // RealMatrix correlationPValues = test.getCorrelationPValues();
          // if (correlationPValues != null) {
          // return new ASTRealMatrix(correlationPValues, false);
          // // return Convert.matrix2List(correlationPValues);
          // }
          // return F.NIL;
          // }
          return testData(vector1, vector2, test);
        } catch (MathRuntimeException miae) {
          return Errors.printMessage(S.PearsonCorrelationTest, miae, engine);
        }
      }
      // The argument `1` at position `2` should be a vector of real numbers with length greater
      // than `3`
      return Errors.printMessage(S.PearsonCorrelationTest, "vctnln", F.List(arg1, F.C1, F.C1),
          engine);
    }

    private static IExpr testData(double[] vector1, double[] vector2,
        org.hipparchus.stat.correlation.PearsonsCorrelation test) {
      RealMatrix correlationPValues = test.getCorrelationPValues();
      if (correlationPValues != null) {
        double value = test.correlation(vector1, vector2);
        double pValue = correlationPValues.getEntry(1, 0);
        return F.List(value, pValue);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }


  private static final class PoissonProcess extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.head().isAST() && ast.isAST1()) {
        IExpr t = ast.arg1();
        // operator form
        IAST headAST = (IAST) ast.head();
        if (headAST.isAST1()) {
          IExpr m = headAST.arg1();
          return F.PoissonDistribution(F.Times(t, m));
        }
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

  }


  /**
   *
   *
   * <pre>
   * Quantile(list, q)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the <code>q</code>-Quantile of <code>list</code>.
   *
   * </blockquote>
   *
   * <pre>
   * Quantile(list, {q1, q2, ...})
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns a list of the <code>q</code>-Quantiles of <code>list</code>.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Quantile">Wikipedia - Quantile</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Quantile({1,2}, 0.5)
   * 1
   * </pre>
   */
  private static final class Quantile extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = normalizeData(ast.arg1());
      if (isQuantityVector(arg1)) {
        // a message from here is the whole answer; do not fall through to the gate as well
        return quantityStatistic(ast.setAtCopy(1, arg1), engine);
      }
      if (ast.argSize() == 2 && arg1.isAST(S.WeightedData, 3) && arg1.first().isList()
          && arg1.second().isList()) {
        IExpr q = ast.arg2();
        if (q.isList()) {
          return ((IAST) q).mapThread(ast, 2);
        }
        if (q.isReal() && ((IReal) q).isRange(F.C0, F.C1)) {
          return Median.weightedQuantile((IAST) arg1, q, engine);
        }
        return F.NIL;
      }
      if (ast.argSize() > 1) {
        final IntList dimensions =
            LinearAlgebraUtil.dimensions(arg1, S.List, Integer.MAX_VALUE, false);
        if (dimensions.size() > 0) {
          // Rectangular array of real numbers is expected at position `1` in `2`.
          if (!isRealOrQuantityData(arg1)) {
            return Errors.printMessage(S.Quantile, "rectn", F.List(F.C1, ast), engine);
          }
          if (dimensions.size() > 2) {
            return F.ArrayReduce(F.Function(ast.setAtCopy(1, F.Slot1)), arg1, F.C1);
          }
        }

        int[] dim = arg1.isMatrix();
        if (dim == null && arg1.isListOfLists()) {
          return F.NIL;
        }
        if (dim != null) {
          return arg1.mapMatrixColumns(dim, (IExpr x) -> ast.setAtCopy(1, x)).normal(false);
        }

        int dimension = arg1.isVector();
        if (dimension >= 0 || arg1.isList()) {
          IExpr normal = arg1.normal(false);
          if (normal.isList()) {
            IAST list = (IAST) normal;
            IExpr a = F.C0;
            IExpr b = F.C0;
            IExpr c = F.C1;
            IExpr d = F.C0;
            if (ast.size() == 4) {
              IAST parameters = quantileParameters(ast.arg3());
              if (parameters.isNIL()) {
                // The parameters `1` in `2` should be given as a 2x2 matrix ...
                return Errors.printMessage(ast.topHead(), "parm", F.list(ast.arg3(), ast), engine);
              }
              a = parameters.arg1();
              b = parameters.arg2();
              c = parameters.arg3();
              d = parameters.arg4();
            }

            int dim1 = list.argSize();
            try {
              if (dim1 == 0) {
                // no message: an empty list leaves Median, Quartiles and InterquartileRange
                // unevaluated in silence too
                return F.NIL;
              }
              if (dim1 > 0 && ast.size() >= 3) {

                final IAST s = EvalAttributes.copySortLess(list);
                final IInteger length = F.ZZ(s.argSize());

                IExpr q = ast.arg2();
                int dim2 = q.isVector();
                if (dim2 >= 0 && q.isList()) {
                  final IAST vector = ((IAST) q);
                  if (vector.exists(x -> x.isReal() && !((IReal) x).isRange(F.C0, F.C1))) {
                    // The Quantile specification `1` should be a number or a list of numbers
                    // between `2` and `3`.
                    return Errors.printMessage(ast.topHead(), "nquan", F.list(q, F.C0, F.C1),
                        engine);
                  }
                  // all quantiles share the one sorted copy above; only a q this cannot compute
                  // - a symbolic one, say - falls back to re-entering Quantile per element
                  IAST points = quantilePoints(s, length, vector, a, b, c, d, engine);
                  return points.isPresent() ? points : vector.mapThread(ast, 2);
                } else {
                  if (q.isReal()) {
                    IReal qi = (IReal) q;
                    if (!qi.isRange(F.C0, F.C1)) {
                      // The Quantile specification `1` should be a number or a list of numbers
                      // between `2` and `3`.
                      return Errors.printMessage(ast.topHead(), "nquan", F.list(qi, F.C0, F.C1),
                          engine);
                    }
                    return quantilePoint(s, length, qi, a, b, c, d, engine);
                  }
                }
              }
            } catch (ArithmeticException ae) {
              return Errors.printMessage(S.Quantile, ae, engine);
            }
          }
        }
      }

      if (arg1.isDistribution()) {
        if (ast.argSize() == 1) {
          return F.Function(F.InverseCDF(arg1, F.Slot1));
        }
        if (ast.argSize() >= 2) {
          if (ast.arg2().isList()) {
            return ast.arg2().mapThread(ast, 2);
          }
          return F.InverseCDF(arg1, ast.arg2());
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      super.setUp(newSymbol);
    }
  }


  private static class Quartiles extends AbstractFunctionEvaluator {

    private static final IAST Q = F.list(F.C1D4, F.C1D2, F.C3D4);

    /** the default parameterization, in the form the user writes it */
    private static final IAST PARAMETER = F.list(F.list(F.C1D2, F.C0), F.list(F.C0, F.C1));

    /** the same defaults, flattened to <code>{a,b,c,d}</code> - see #quantileParameters */
    private static final IAST PARAMETERS = F.List(F.C1D2, F.C0, F.C0, F.C1);

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = normalizeData(ast.arg1());
      if (isQuantityVector(arg1)) {
        // a message from here is the whole answer; do not fall through to the gate as well
        return quantityStatistic(ast.setAtCopy(1, arg1), engine);
      }
      if (ast.isAST1() && arg1.isAST(S.WeightedData, 3) && arg1.first().isList()
          && arg1.second().isList()) {
        // the empirical distribution, not the data list - see Median#weightedQuantile
        return Q.map(q -> Median.weightedQuantile((IAST) arg1, q, engine), 1);
      }
      final IntList dimensions =
          LinearAlgebraUtil.dimensions(arg1, S.List, Integer.MAX_VALUE, false);
      if (dimensions.size() > 0) {
        // Rectangular array of real numbers is expected at position `1` in `2`.
        if (!isRealOrQuantityData(arg1)) {
          return Errors.printMessage(S.Quartiles, "rectn", F.List(F.C1, ast), engine);
        }
        if (dimensions.size() > 2) {
          return F.ArrayReduce(F.Function(ast.setAtCopy(1, F.Slot1)), arg1, F.C1);
        }
      }

      // FIX: Handle distributions separately without empirical interpolation parameters
      if (arg1.isDistribution()) {
        return engine.evaluate(F.Quantile(arg1, Q));
      }

      if (arg1.isNonEmptyList()) {
        IAST list = (IAST) arg1;
        IAST parameters = PARAMETERS;
        IExpr parameter = PARAMETER;
        if (ast.size() == 3) {
          parameter = ast.arg2();
          parameters = quantileParameters(parameter);
          if (parameters.isNIL()) {
            // The parameters `1` in `2` should be given as a 2x2 matrix ...
            return Errors.printMessage(ast.topHead(), "parm", F.list(parameter, ast), engine);
          }
        }
        if (dimensions.size() == 1) {
          // a flat list, already past the real-number gate above: sort it once and read all
          // three quartiles off that copy. Handing the job to Quantile instead would sort and
          // re-gate once per quartile. Anything else - a matrix, a ragged list - keeps the
          // Quantile path below, which is where the columnwise and threading rules live.
          final IAST s = EvalAttributes.copySortLess(list);
          IAST points = quantilePoints(s, F.ZZ(s.argSize()), Q, parameters.arg1(),
              parameters.arg2(), parameters.arg3(), parameters.arg4(), engine);
          if (points.isPresent()) {
            return points;
          }
        }
        return engine.evaluate(F.Quantile(list, Q, parameter));
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }


  private static final class RandomVariate extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isAST()) {
        IAST dist = (IAST) ast.arg1();
        if (dist.head().isSymbol()) {
          try {
            ISymbol head = (ISymbol) dist.head();
            if (head instanceof IBuiltInSymbol) {
              IEvaluator evaluator = ((IBuiltInSymbol) head).getEvaluator();
              if (evaluator instanceof IRandomVariate) {
                Random random = engine.getRandom();
                IRandomVariate variate = (IRandomVariate) evaluator;
                if (ast.size() == 3) {
                  IExpr arg2 = ast.arg2();
                  if (arg2.isList()) {
                    // n1 x n2 x n3 ... array
                    int[] dimension =
                        Validate.checkListOfInts(ast, arg2, 0, Integer.MAX_VALUE, engine);
                    if (dimension == null || dimension.length == 0) {
                      return F.NIL;
                    }
                    if (dimension.length == 1) {
                      // create a list
                      if (dimension[0] >= Config.MAX_AST_SIZE) {
                        ASTElementLimitExceeded.throwIt(dimension[0]);
                      }
                      return variate.randomVariate(random, dist, dimension[0]);
                    }
                    // create a tensor recursively
                    int sampleSize = dimension[dimension.length - 1];
                    System.arraycopy(dimension, 0, dimension, 1, dimension.length - 1);
                    IASTAppendable list = F.ListAlloc(dimension[0]);
                    return createTensorRecursive(dimension, 1, list,
                        () -> variate.randomVariate(random, dist, sampleSize));
                  } else {
                    int n = arg2.toMachineInt();
                    if (n >= 0) {
                      if (n >= Config.MAX_AST_SIZE) {
                        ASTElementLimitExceeded.throwIt(n);
                      }
                      return variate.randomVariate(random, dist, n);
                    }
                  }
                  return F.NIL;
                }
                // RandomVariate(dist) without a size specification.
                // Dimensions(RandomVariate(dist,spec)) == Join(spec,componentShape), with
                // componentShape {} for a univariate and {n} for a multivariate distribution. The
                // empty specification therefore drops the outermost axis of a 1 element sample:
                // a univariate distribution returns a number and a multivariate one a vector.
                IExpr sample = variate.randomVariate(random, dist, 1);
                return sample.isList1() ? sample.first() : sample;
              } else if (!(evaluator instanceof IDistribution)) {
                return printMessageUdist(head, ast, dist, engine);
              }
            } else {
              return printMessageUdist(head, ast, dist, engine);
            }
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            return Errors.printMessage(S.RandomVariate, rex, engine);
          }
        }
      } else {
        // The first argument `1` is not a valid distribution.
        return Errors.printMessage(ast.topHead(), "unsdst", F.list(ast.arg1()), engine);
      }
      return F.NIL;
    }

    /**
     * Print the <code>udist</code> message:
     *
     * <p>
     * <code>The specification `1` is not a random distribution recognized by the system.</code>
     *
     * @param head
     * @param ast
     * @param dist
     * @param engine
     * @return
     */
    private static IExpr printMessageUdist(ISymbol head, final IAST ast, IAST dist,
        EvalEngine engine) {
      // The specification `1` is not a random distribution recognized by the system.
      // if (head.getSymbolName().toLowerCase(Locale.US).endsWith("distribution")) {
      return Errors.printMessage(ast.topHead(), "udist", F.list(dist), engine);
      // }
      // return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    private static IAST createTensorRecursive(int[] indx, int offset, IASTAppendable list,
        Supplier<IExpr> s) {
      if (indx.length <= offset) {
        list.append(s.get());
        return list;
      }
      if (indx[offset] >= Config.MAX_AST_SIZE) {
        ASTElementLimitExceeded.throwIt(indx[offset]);
      }
      IASTAppendable subList = F.ListAlloc(indx[offset]);
      for (int i = 1; i <= indx[offset]; i++) {
        createTensorRecursive(indx, offset + 1, subList, s);
      }
      list.append(subList);
      return subList;
    }
  }


  /**
   *
   *
   * <pre>
   * Rescale(list)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns <code>Rescale(list,{Min(list), Max(list)})</code>.
   *
   * </blockquote>
   *
   * <pre>
   * Rescale(x,{xmin, xmax})
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns <code>x/(xmax-xmin)-xmin/(xmax-xmin)</code>.
   *
   * </blockquote>
   *
   * <pre>
   * Rescale(x,{xmin, xmax},{ymin, ymax})
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns <code>(x*(ymax-ymin))/(xmax-xmin)-(xmin*ymax-xmax*ymin)/(xmax-xmin)</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Rescale({a,b})
   * {a/(Max(a,b)-Min(a,b))-Min(a,b)/(Max(a,b)-Min(a,b)),b/(Max(a,b)-Min(a,b))-Min(a,b)/(Max(a,b)-Min(a,b))}
   *
   * &gt;&gt; Rescale({1, 2, 3, 4, 5}, {-100, 100})
   * {101/200,51/100,103/200,13/25,21/40}
   *
   * &gt;&gt; Rescale(x,{xmin, xmax},{ymin, ymax})
   * (x*(ymax-ymin))/(xmax-xmin)-(xmin*ymax-xmax*ymin)/(xmax-xmin)
   * </pre>
   */
  private static final class Rescale extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr x = ast.arg1();
      if (ast.size() == 2 && x.isList()) {
        IExpr min = S.Min.funEval(engine, x);
        IExpr max = S.Max.funEval(engine, x);
        return rescale(x, min, max, engine);
      }
      if (ast.size() >= 3) {
        if (ast.arg2().isList2()) {
          IAST list1 = (IAST) ast.arg2();
          IExpr min = list1.arg1();
          IExpr max = list1.arg2();
          if (ast.size() == 4) {
            if (ast.arg3().isList2()) {
              IAST list2 = (IAST) ast.arg3();
              IExpr ymin = list2.arg1();
              IExpr ymax = list2.arg2();
              // (arg1*(ymax - ymin))/(max - min) - (min*ymax - max*ymin)/(max - min)
              return F.Plus(
                  F.Times(x, F.Power(F.Plus(max, F.Negate(min)), -1), F.Plus(ymax, F.Negate(ymin))),
                  F.Times(F.CN1, F.Power(F.Plus(max, F.Negate(min)), -1),
                      F.Plus(F.Times(min, ymax), F.Times(F.CN1, max, ymin))))
                  .eval(engine);
            }
            return F.NIL;
          }
          return rescale(x, min, max, engine);
        }
        return F.NIL;
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    private static IExpr rescale(IExpr x, IExpr min, IExpr max, EvalEngine engine) {
      IExpr difference = engine.evaluate(F.Subtract(max, min));
      IExpr inverseDifference = engine.evaluate(F.Power(difference, -1));
      if (difference.isZero()) {
        if (max.isNumber() && min.isNumber()) {
          return F.CComplexInfinity;
        }
        return S.Indeterminate;
      }
      return F.Times(F.Subtract(x, min), inverseDifference);
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }


  private static final class RootMeanSquare extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isList()) {
        IAST list = (IAST) arg1;
        int[] matrix = list.isMatrix();
        if (matrix != null) {
          IASTAppendable[] columnElements = new IASTAppendable[matrix[1]];
          for (int i = 0; i < matrix[1]; i++) {
            columnElements[i] = F.ast(S.List, matrix[0] * matrix[1]);
          }
          for (int j = 1; j <= matrix[1]; j++) {
            IASTAppendable rootMeanList = columnElements[j - 1];
            for (int i = 1; i <= matrix[0]; i++) {
              IAST row = (IAST) list.get(i);
              rootMeanList.append(row.get(j));
            }
          }
          IASTAppendable result = F.ListAlloc(matrix[1]);
          for (int i = 0; i < matrix[1]; i++) {
            result.append(rootMeanSquareVector(columnElements[i]));
          }
          return result;
        } else {
          return rootMeanSquareVector(list);
        }
      }
      return F.NIL;
    }

    private static IExpr rootMeanSquareVector(IAST list) {
      if (list.isRealVector()) {
        double[] doubleVector = list.toDoubleVector();
        DescriptiveStatistics descriptiveStatistics = new DescriptiveStatistics(doubleVector);
        double quadraticMean = descriptiveStatistics.getQuadraticMean();
        return F.num(quadraticMean);
      }
      IExpr sum = F.sum(i -> list.get(i).times(list.get(i)), 1, list.argSize());
      return F.Times(F.Power(sum, F.C1D2), F.Power(F.ZZ(list.argSize()), F.CN1D2));
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }


  /**
   *
   *
   * <pre>
   * Skewness(list)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * gives Pearson's moment coefficient of skewness for $list$ (a measure for estimating the
   * symmetry of a distribution).
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt;&gt; Skewness({1.1, 1.2, 1.4, 2.1, 2.4})
   * 0.40704
   * </pre>
   */
  private static final class Skewness extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isList()) {
        IAST list = (IAST) ast.arg1();
        return F.Divide(F.CentralMoment(list, 3), F.Power(F.CentralMoment(list, 2), F.C3D2));
      }
      if (arg1.isAST()) {
        IAST dist = (IAST) arg1;
        if (dist.head().isSymbol()) {
          ISymbol head = (ISymbol) dist.head();
          if (head instanceof IBuiltInSymbol) {
            IEvaluator evaluator = ((IBuiltInSymbol) head).getEvaluator();
            if (evaluator instanceof IStatistics) {
              IStatistics distribution = (IStatistics) evaluator;
              return distribution.skewness(dist);
            }
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }


  /**
   *
   *
   * <pre>
   * StandardDeviation(list)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * computes the standard deviation of <code>list</code>. <code>list</code> may consist of
   * numerical values or symbols. Numerical values may be real or complex.
   *
   * </blockquote>
   *
   * <p>
   * <code>StandardDeviation({{a1, a2, ...}, {b1, b2, ...}, ...})</code> will yield <code>
   * {StandardDeviation({a1, b1, ...}, StandardDeviation({a2, b2, ...}), ...}</code>.
   *
   * <p>
   * <code>StandardDeviation</code> can be applied to the following distributions:
   *
   * <blockquote>
   *
   * <p>
   * <a href="BernoulliDistribution.md">BernoulliDistribution</a>,
   * <a href="BinomialDistribution.md">BinomialDistribution</a>,
   * <a href="DiscreteUniformDistribution.md">DiscreteUniformDistribution</a>,
   * <a href="ErlangDistribution.md">ErlangDistribution</a>,
   * <a href="ExponentialDistribution.md">ExponentialDistribution</a>,
   * <a href="FrechetDistribution.md">FrechetDistribution</a>,
   * <a href="GammaDistribution.md">GammaDistribution</a>,
   * <a href="GeometricDistribution.md">GeometricDistribution</a>,
   * <a href="GumbelDistribution.md">GumbelDistribution</a>,
   * <a href="HypergeometricDistribution.md">HypergeometricDistribution</a>,
   * <a href="LogNormalDistribution.md">LogNormalDistribution</a>,
   * <a href="NakagamiDistribution.md">NakagamiDistribution</a>,
   * <a href="NormalDistribution.md">NormalDistribution</a>,
   * <a href="PoissonDistribution.md">PoissonDistribution</a>,
   * <a href="StudentTDistribution.md">StudentTDistribution</a>,
   * <a href="WeibullDistribution.md">WeibullDistribution</a>
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; StandardDeviation({1, 2, 3})
   * 1
   *
   * &gt;&gt; StandardDeviation({7, -5, 101, 100})
   * Sqrt(13297)/2
   *
   * &gt;&gt; StandardDeviation({a, a})
   * 0
   *
   * &gt;&gt; StandardDeviation({{1, 10}, {-1, 20}})
   * {Sqrt(2),5*Sqrt(2)}
   *
   * &gt;&gt; StandardDeviation(LogNormalDistribution(0, 1))
   * Sqrt((-1+E)*E)
   * </pre>
   */
  private static final class StandardDeviation extends AbstractFunctionEvaluator {


    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isSparseArray()) {
        arg1 = arg1.normal(false);
      }
      if (arg1.isList()) {
        IAST list = (IAST) arg1;
        int[] dim = list.isMatrix();
        if (dim == null) {
          int length = list.isVector();
          if (length > 1) {
            if (arg1.isRealVector()) {
              double[] values = list.toDoubleVector();
              if (values == null) {
                return F.NIL;
              }
              org.hipparchus.stat.descriptive.moment.StandardDeviation sd =
                  new org.hipparchus.stat.descriptive.moment.StandardDeviation();
              return F.num(sd.evaluate(values));
            }
            return standardDeviation(arg1);
          }
          IntArrayList dimensions = LinearAlgebraUtil.dimensions(list);
          if (dimensions.size() > 2) {
            return F.ArrayReduce(S.StandardDeviation, list, F.C1);
          }
          // The argument `1` should have at least `2` elements.
          return Errors.printMessage(S.StandardDeviation, "shlen", F.List(list, F.C2));
        }
        return arg1.mapMatrixColumns(dim, x -> F.StandardDeviation(x)).normal(false);
      } else if (arg1.isAssociation()) {
        IAssociation assoc = (IAssociation) arg1;
        int[] dim = assoc.isAssociationMatrix();
        if (dim == null) {
          int vectorLength = assoc.isAssociationVector();
          if (vectorLength > 0) {
            if (vectorLength > 1) {
              IAST list = Convert.assoc2List(assoc);
              return F.Sqrt(F.Variance(list));
            }
            // The argument `1` should have at least `2` elements.
            return Errors.printMessage(S.StandardDeviation, "shlen", F.List(assoc, F.C2));
          }
          // Rectangular array expected at position `1` in `2`.
          return Errors.printMessage(S.StandardDeviation, "rectt", F.List(F.C1, ast));
        }
        return arg1.mapMatrixColumns(dim, x -> F.StandardDeviation(x));
      } else if (arg1.isDistribution()) {
        IAST dist = (IAST) arg1;
        IStatistics stat = dist.headInstanceOf(IStatistics.class);
        if (stat != null) {
          IExpr result = stat.standardDeviation(dist);
          if (result.isPresent()) {
            return result;
          }
          result = Variance.numericVariance(dist, engine);
          if (result.isPresent()) {
            return F.Sqrt(result);
          }
        }
        return standardDeviation(arg1);
      } else if (arg1.isNumber()) {
        // Rectangular array expected at position `1` in `2`.
        return Errors.printMessage(S.StandardDeviation, "rectt", F.List(F.C1, ast));
      }
      return F.NIL;
    }

    private static IAST standardDeviation(IExpr expr) {
      return F.Sqrt(F.Variance(expr));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      super.setUp(newSymbol);
    }
  }


  private static final class Standardize extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      final IExpr f1 = ast.getArg(2, S.Mean);
      final IExpr f2 = ast.getArg(3, S.StandardDeviation);

      int[] dim = arg1.isMatrix();
      if (dim == null && arg1.isListOfLists()) {
        return F.NIL;
      }
      if (dim != null) {
        IExpr temp = arg1.mapMatrixColumns(dim, v -> F.Standardize(v, f1, f2)).normal(false);
        return temp.ifPresent(x -> F.Transpose(x));
      }


      IExpr standardDeviation = engine.evaluateNIL(F.unaryAST1(f2, arg1));
      if (standardDeviation.isPresent() && !standardDeviation.isZero()) {
        IExpr mean = engine.evaluate(F.unaryAST1(f1, arg1));
        if (mean.isPresent()) {
          return F.Divide(F.Subtract(arg1, mean), standardDeviation).eval(engine);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }
  }


  private static final class SurvivalFunction extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1() && ast.first().isAST()) {
        IAST dist = (IAST) ast.arg1();
        if (dist.isDistribution()) {
          IExpr closedForm = closedForm(dist, F.Slot1, engine);
          if (closedForm.isPresent()) {
            return IPDF.operatorForm(closedForm);
          }
          return IPDF.operatorForm(F.Expand(F.Subtract(F.C1, F.CDF(dist, F.Slot1))));
        }
        return F.NIL;
      }
      if (ast.isAST2() && ast.first().isAST()) {
        IAST dist = (IAST) ast.arg1();
        if (dist.isDistribution()) {
          if (ast.arg2().isList()) {
            return ast.arg2().mapThread(ast, 2);
          }
          IExpr closedForm = closedForm(dist, ast.arg2(), engine);
          if (closedForm.isPresent()) {
            return closedForm;
          }
          return F.Expand(F.Subtract(F.C1, F.CDF(dist, ast.arg2())));
        }
        return F.NIL;
      }
      return F.NIL;
    }

    /**
     * Ask the distribution for a dedicated closed form of the survival function, which is usually
     * much simpler than the literal <code>1 - CDF(dist, x)</code>.
     */
    private static IExpr closedForm(IAST dist, IExpr x, EvalEngine engine) {
      ICDF cdf = dist.headInstanceOf(ICDF.class);
      if (cdf != null) {
        IAST checked = cdf.checkParameters(dist);
        if (checked.isPresent()) {
          return cdf.survivalFunction(checked, x, engine);
        }
      }
      return F.NIL;
    }
  }


  private static final class TTest extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isList2()) {
        IAST list = (IAST) arg1;
        int dimension1 = list.first().isVector();
        if (dimension1 > 0) {
          int dimension2 = list.second().isVector();
          if (dimension2 > 0) {
            double[] vector1 = list.first().toDoubleVector();
            if (vector1 != null) {
              double[] vector2 = list.second().toDoubleVector();
              if (vector2 != null) {
                if (vector1.length <= 1 || vector2.length <= 1) {
                  // The argument `1` at position `2` should be a rectangular array of real
                  // numbers with length greater than the dimension of the array or two such arrays
                  // with
                  // of equal dimension.
                  return Errors.printMessage(ast.topHead(), "rctndm1", F.list(arg1, F.C1), engine);
                }
                org.hipparchus.stat.inference.TTest tTest =
                    new org.hipparchus.stat.inference.TTest();
                double value = tTest.homoscedasticTTest(vector1, vector2);
                return F.num(value);
              }
            }
          }
        }
        return F.NIL;
      }
      int dimension = arg1.isVector();
      if (dimension > 0) {
        double[] vector = arg1.toDoubleVector();
        if (vector != null) {
          if (vector.length <= 1) {
            // The argument `1` at position `2` should be a rectangular array of real
            // numbers with length greater than the dimension of the array or two such arrays with
            // of equal dimension.
            return Errors.printMessage(ast.topHead(), "rctndm1", F.list(arg1, F.C1), engine);
          }

          org.hipparchus.stat.inference.TTest tTest = new org.hipparchus.stat.inference.TTest();
          double value = tTest.tTest(0.0, vector);
          return F.num(value);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }


  /**
   *
   *
   * <pre>
   * Variance(list)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * computes the variance of <code>list</code>. <code>list</code> may consist of numerical values
   * or symbols. Numerical values may be real or complex.
   *
   * </blockquote>
   *
   * <p>
   * <code>Variance({{a1, a2, ...}, {b1, b2, ...}, ...})</code> will yield <code>
   * {Variance({a1, b1, ...}, Variance({a2, b2, ...}), ...}</code>.
   *
   * <p>
   * <code>Variance</code> can be applied to the following distributions:
   *
   * <blockquote>
   *
   * <p>
   * <a href="BernoulliDistribution.md">BernoulliDistribution</a>,
   * <a href="BinomialDistribution.md">BinomialDistribution</a>,
   * <a href="DiscreteUniformDistribution.md">DiscreteUniformDistribution</a>,
   * <a href="ErlangDistribution.md">ErlangDistribution</a>,
   * <a href="ExponentialDistribution.md">ExponentialDistribution</a>,
   * <a href="FrechetDistribution.md">FrechetDistribution</a>,
   * <a href="GammaDistribution.md">GammaDistribution</a>,
   * <a href="GeometricDistribution.md">GeometricDistribution</a>,
   * <a href="GumbelDistribution.md">GumbelDistribution</a>,
   * <a href="HypergeometricDistribution.md">HypergeometricDistribution</a>,
   * <a href="LogNormalDistribution.md">LogNormalDistribution</a>,
   * <a href="NakagamiDistribution.md">NakagamiDistribution</a>,
   * <a href="NormalDistribution.md">NormalDistribution</a>,
   * <a href="PoissonDistribution.md">PoissonDistribution</a>,
   * <a href="StudentTDistribution.md">StudentTDistribution</a>,
   * <a href="WeibullDistribution.md">WeibullDistribution</a>
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Variance({1, 2, 3})
   * 1
   *
   * &gt;&gt; Variance({7, -5, 101, 3})
   * 7475/3
   *
   * &gt;&gt; Variance({1 + 2*I, 3 - 10*I})
   * 74
   *
   * &gt;&gt; Variance({a, a})
   * 0
   *
   * &gt;&gt; Variance({{1, 3, 5}, {4, 10, 100}})
   * {9/2,49/2,9025/2}
   * </pre>
   */
  private static final class Variance extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isSparseArray()) {
        // FieldVector<IExpr> vector = ((ISparseArray) arg1).toFieldVector(false);
        // if (vector != null) {
        // return ;
        // }
        // FieldMatrix<IExpr> matrix = ((ISparseArray) arg1).toFieldMatrix(false);
        arg1 = arg1.normal(false);
      }
      try {
        if (arg1.isList()) {
          IAST list1 = (IAST) arg1;
          int[] matrixDimensions = list1.isMatrix();
          if (matrixDimensions != null) {
            if (list1.isRealMatrix()) {
              double[][] matrix = list1.toDoubleMatrix(true);
              if (matrix == null) {
                return F.NIL;
              }
              matrix = Convert.toDoubleTransposed(matrix);
              double[] result = new double[matrixDimensions[1]];
              for (int i = 0; i < matrix.length; i++) {
                result[i] = StatUtils.variance(matrix[i]);
              }
              return new ASTRealVector(result, false);
            }
            return F.mapRange(1, matrixDimensions[1] + 1, i -> {
              final int ii = i;
              IASTAppendable list = F.ListAlloc(matrixDimensions[1])
                  .appendArgs(matrixDimensions[0] + 1, j -> list1.getPart(j, ii));
              return F.Variance(list);
            });
          }

          int vectorLength = list1.isVector();
          if (vectorLength >= 0) {
            if (vectorLength > 1) {
              if (list1.isRealVector()) {
                double[] values = list1.toDoubleVector();
                if (values == null) {
                  return F.NIL;
                }
                return F.num(StatUtils.variance(values));
              }
              return Covariance.vectorCovarianceSymbolic(list1, list1, vectorLength);
            }
            // The argument `1` should have at least `2` elements.
            return Errors.printMessage(S.Variance, "shlen", F.List(list1, F.C2));
          }
        } else if (arg1.isAssociation()) {
          IAssociation assoc = (IAssociation) arg1;
          int[] dim = assoc.isAssociationMatrix();
          if (dim == null) {
            int vectorLength = assoc.isAssociationVector();
            if (vectorLength > 0) {
              if (vectorLength > 1) {
                IAST list = Convert.assoc2List(assoc);
                return Covariance.vectorCovarianceSymbolic(list, list, vectorLength);
              }
              // The argument `1` should have at least `2` elements.
              return Errors.printMessage(S.Variance, "shlen", F.List(assoc, F.C2));
            }
            // Rectangular array expected at position `1` in `2`.
            return Errors.printMessage(S.Variance, "rectt", F.List(F.C1, ast));
          }
          return F.mapRange(1, dim[1] + 1, i -> {
            final int ii = i;
            IASTAppendable list =
                F.ListAlloc(dim[1] + dim[0]).appendArgs(dim[0] + 1, j -> assoc.getPart(j, ii));
            return F.Variance(list);
          });

        } else if (arg1.isAST()) {
          IAST dist = (IAST) arg1;
          if (dist.head().isSymbol()) {
            ISymbol head = (ISymbol) dist.head();
            if (head instanceof IBuiltInSymbol) {
              IEvaluator evaluator = ((IBuiltInSymbol) head).getEvaluator();
              if (evaluator instanceof IStatistics) {
                IStatistics distribution = (IStatistics) evaluator;
                IExpr variance = distribution.variance(dist);
                if (variance.isPresent()) {
                  return variance;
                }
                return numericVariance(dist, engine);
              }
            }
          }
        } else if (arg1.isNumber()) {
          // Rectangular array expected at position `1` in `2`.
          return Errors.printMessage(S.Variance, "rectt", F.List(F.C1, ast));
        }
        final IntList dimensions =
            LinearAlgebraUtil.dimensions(arg1, S.List, Integer.MAX_VALUE, false);
        if (dimensions.size() >= 2) {
          return F.ArrayReduce(S.Variance, arg1, F.C1);
        }
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        return Errors.printMessage(S.Variance, rex, engine);
      }

      return F.NIL;
    }

    /**
     * <code>NExpectation((x-Mean(dist))^2, x \[Distributed] dist)</code> for a distribution which
     * has no closed form variance.
     *
     * <p>
     * Only used in numeric mode, so a distribution with symbolic parameters keeps returning
     * unevaluated instead of a number which its parameters don't justify.
     *
     * @return {@link F#NIL} if the mean or the quadrature doesn't produce a real number
     */
    private static IExpr numericVariance(IAST dist, EvalEngine engine) {
      if (!engine.isNumericMode()) {
        return F.NIL;
      }
      IExpr mean = engine.evalN(S.Mean.ofNIL(engine, dist));
      if (!mean.isReal()) {
        return F.NIL;
      }
      ISymbol x = F.Dummy("x");
      IExpr result = engine.evaluate(F.binaryAST2(S.NExpectation, F.Sqr(F.Subtract(x, mean)),
          F.Distributed(x, dist)));
      return result.isReal() ? result : F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

  }


  /**
   * Returns an array of random deviates from the given unary function.
   *
   * @param function the unary function to sample from
   * @param size the number of values to return
   * @return an array of {@code size} values following the given unary function distribution
   */
  // private static double[] nextDeviates(Random random, DoubleUnaryOperator function, int size) {
  // double[] out = new double[size];
  // for (int i = 0; i < size; i++) {
  // double p = random.nextDouble();
  // out[i] = function.applyAsDouble(p);
  // }
  // return out;
  // }

  /**
   * Interpolate between two neighbouring order statistics without forming a difference that can
   * overflow.
   *
   * <p>
   * The textbook form <code>lo + w*(hi-lo)</code> is an identity in exact arithmetic but not in
   * machine arithmetic. When <code>lo</code> and <code>hi</code> straddle zero near the top of the
   * <code>double</code> range, <code>hi-lo</code> rounds to <code>Infinity</code>, and that
   * infinity survives the multiply and the add: <code>Quantile({-1.0*10^308, 1.0*10^308}, 1/2,
   * {{1/2,0},{0,1}})</code> returned <code>Infinity</code> where the answer is <code>0.0</code>. At
   * <code>w == 0</code> the same expression produces <code>0*Infinity</code>, that is
   * <code>Indeterminate</code>, instead of the element it is supposed to select.
   *
   * <p>
   * For a weight in the unit interval this uses the convex combination
   * <code>(1-w)*lo + w*hi</code>, which never forms a quantity larger in magnitude than the two
   * values it interpolates between, and selects the named element outright at either endpoint.
   * Outside the unit interval - which a user supplied <code>{{a,b},{c,d}}</code> permits, though no
   * standard quantile type uses it - the convex form is the unsafe one, because <code>(1-w)</code>
   * and <code>w</code> then have opposite signs and the two products can overflow independently
   * into <code>Infinity + (-Infinity)</code>; the difference form is kept there. Neither form is
   * safe everywhere, so each is used only where it is.
   *
   * <p>
   * Symbolic <code>lo</code>, <code>hi</code> or <code>w</code> also keep the difference form: they
   * cannot overflow, and rewriting them would only churn the printed form of symbolic results.
   *
   * @param lo the lower order statistic
   * @param hi the upper order statistic
   * @param w the interpolation weight
   */
  public static IExpr interpolate(IExpr lo, IExpr hi, IExpr w) {
    if (w.isReal() && lo.isReal() && hi.isReal() && ((IReal) w).isRange(F.C0, F.C1)) {
      if (w.isZero()) {
        return lo;
      }
      if (w.isOne()) {
        return hi;
      }
      return F.Plus(F.Times(F.Subtract(F.C1, w), lo), F.Times(w, hi));
    }
    return F.Plus(lo, F.Times(F.Subtract(hi, lo), w));
  }

  /**
   * The values a statistics function should operate on, as an expression it can walk.
   *
   * <p>
   * An <code>Association</code> contributes its values and drops its keys, a
   * <code>SparseArray</code> its dense form, and a <code>Dataset</code> its rows. Anything else is
   * returned unchanged, so an evaluator can normalize once at its entry and then test for a list
   * without knowing which container it was handed.
   */
  public static IExpr normalizeData(IExpr arg) {
    if (arg.isAssociation()) {
      return ((IAssociation) arg).values();
    }
    if (arg.isSparseArray()) {
      return arg.normal(false);
    }
    if (arg.isAST(S.QuantityArray, 3)) {
      // an array of quantities, which is what the wrapper below and the columnwise branches
      // already know how to walk. A QuantityArray with one unit per column becomes a matrix whose
      // columns are each uniform, so the columnwise recursion handles the units for free.
      IExpr normal = QuantityFunctions.quantityArrayNormal((IAST) arg);
      if (normal.isPresent()) {
        return normal;
      }
    }
    return IASTDataset.normalizeDataset(arg);
  }

  /**
   * Whether every leaf is real-valued, or every leaf is a {@link S#Quantity}.
   *
   * <p>
   * Mixing the two is data this family cannot make sense of, and Mathematica agrees:
   * <code>Median({Quantity(1,"Meters"), 2})</code> reports <code>rectn</code>.
   */
  private static boolean isRealOrQuantityData(IExpr data) {
    return data.forAllLeaves(x -> x.isRealResult()) || isQuantityArray(data);
  }

  /**
   * Whether <code>data</code> is a (possibly nested) array whose elements are all quantities.
   *
   * <p>
   * This cannot use <code>forAllLeaves</code>: a <code>Quantity</code> is an AST, so that walk
   * descends past it and tests its magnitude and unit string instead. Here a quantity IS the leaf.
   */
  private static boolean isQuantityArray(IExpr data) {
    if (data.isQuantity()) {
      return true;
    }
    return data.isList() && ((IAST) data).forAll(x -> isQuantityArray(x));
  }

  /** Whether <code>data</code> is a non-empty flat list of quantities. */
  public static boolean isQuantityVector(IExpr data) {
    return data.isList() && data.argSize() > 0 && ((IAST) data).forAll(x -> x.isQuantity());
  }

  /**
   * Evaluate a statistics function on quantity data by converting to one common unit, computing on
   * the plain magnitudes, and re-attaching the unit to the result.
   *
   * <p>
   * Every head in this family answers in the unit of its data - unlike <code>Variance</code>,
   * whose result is squared - so re-attaching a single unit is enough. The magnitudes then travel
   * the ordinary real-number path, which is what keeps the overflow-safe interpolation and the
   * shared sort in play without any of them having to know about units. The result is reported in
   * the unit of the FIRST element, the convention <code>Plus</code> already uses here:
   * <code>Median({Quantity(1,"Meters"), Quantity(300,"Centimeters"), Quantity(2,"Meters")})</code>
   * is <code>Quantity(2,"Meters")</code>.
   *
   * <p>
   * Only a flat list of quantities is handled here. A matrix is left to the columnwise branch of
   * each evaluator, which re-enters this method once per column - which is also what makes a
   * <code>QuantityArray</code> carrying one unit per column come out right.
   *
   * @return {@link F#NIL} when <code>ast.arg1()</code> is not a non-empty flat list of quantities
   */
  public static IExpr quantityStatistic(IAST ast, EvalEngine engine) {
    if (!isQuantityVector(ast.arg1())) {
      return F.NIL;
    }
    IAST data = (IAST) ast.arg1();
    IAST first = (IAST) data.arg1();
    IExpr unit = first.arg2();
    IASTAppendable magnitudes = F.ListAlloc(data.argSize());
    for (int i = 1; i < data.size(); i++) {
      IAST quantity = (IAST) data.get(i);
      IExpr magnitude = org.matheclipse.core.units.QuantityOps.magnitudeInFirstUnit(first,
          quantity, engine);
      if (magnitude.isNIL()) {
        // `1` and `2` are incompatible units
        return Errors.printMessage(S.Quantity, "compat", F.list(unit, quantity.arg2()), engine);
      }
      magnitudes.append(magnitude);
    }

    IExpr result = engine.evaluate(ast.setAtCopy(1, magnitudes));
    if (result.isList()) {
      IAST list = (IAST) result.normal(false);
      IASTAppendable quantities = F.ListAlloc(list.argSize());
      for (int i = 1; i < list.size(); i++) {
        quantities.append(F.Quantity(list.get(i), unit));
      }
      return quantities;
    }
    // anything else - an unevaluated head, say - is not a magnitude to re-attach a unit to
    return result.isNumber() ? F.Quantity(result, unit) : F.NIL;
  }

  /**
   * The four numbers of a quantile parameterization.
   *
   * <p>
   * Both spellings are accepted: the 2x2 matrix <code>{{a,b},{c,d}}</code>, and the two-element
   * "plot point" form <code>{a,b}</code>, which means <code>{{a,b},{0,1}}</code> - the
   * linear-interpolation family. The pair form is absent from the <code>Quantile</code> reference
   * page and named only in the message text for a malformed specification; it was pinned against
   * Mathematica by <code>Quantile[Range[10],3/10,{1,2}] == 23/5</code> and
   * <code>Quantile[Range[10],7/20,{0,0}] == 7/2</code>, which rule out reading <code>{a,b}</code>
   * as a constant weight - the other reading that fits <code>Quartiles[{1,2,3},{1,2}]</code>.
   *
   * @return <code>{a,b,c,d}</code>, or {@link F#NIL} if <code>spec</code> is neither form
   */
  private static IAST quantileParameters(IExpr spec) {
    int[] dimension = spec.isMatrix();
    if (dimension != null) {
      if (dimension[0] == 2 && dimension[1] == 2) {
        return F.List(spec.first().first(), spec.first().second(), spec.second().first(),
            spec.second().second());
      }
      return F.NIL;
    }
    if (spec.isList2()) {
      return F.List(spec.first(), spec.second(), F.C0, F.C1);
    }
    return F.NIL;
  }

  /**
   * The quantile of an ascending-sorted list under the parameterization
   * <code>{{a,b},{c,d}}</code>: with <code>h = a + (n+b)*q</code> and
   * <code>w = c + d*FractionalPart(h)</code> the result is <code>s[[Floor(h)]]</code>
   * interpolated toward <code>s[[Ceiling(h)]]</code> by <code>w</code>, both indices clamped to
   * the list. At an integer <code>h</code> the two neighbours coincide and the element is selected
   * outright.
   *
   * <p>
   * The result is returned unevaluated, for the engine to evaluate as it does for any other
   * evaluator result.
   *
   * @param s the data, sorted ascending
   * @param length the number of data points
   * @param q the quantile, already checked to lie in <code>[0,1]</code>
   * @return {@link F#NIL} when the point is not computable, for instance when a symbolic
   *         parameter leaves <code>h</code> non-real
   */
  private static IExpr quantilePoint(IAST s, IInteger length, IReal q, IExpr a, IExpr b, IExpr c,
      IExpr d, EvalEngine engine) {
    // x = a + (length + b) * q
    IExpr x = q.isZero() ? a : S.Plus.of(engine, a, F.Times(F.Plus(length, b), q));
    if (x.isNumIntValue()) {
      int index = x.toIntDefault();
      if (F.isPresent(index)) {
        if (index < 1) {
          index = 1;
        } else if (index > s.argSize()) {
          index = s.argSize();
        }
        return s.get(index);
      }
    }
    if (x.isReal()) {
      IReal xi = (IReal) x;
      int xFloor = xi.floorFraction().toIntDefault();
      int xCeiling = xi.ceilFraction().toIntDefault();
      if (F.isPresent(xFloor) && F.isPresent(xCeiling)) {
        if (xFloor < 1) {
          xFloor = 1;
        }
        if (xFloor > s.argSize()) {
          xFloor = s.argSize();
        }
        if (xCeiling < 1) {
          xCeiling = 1;
        }
        if (xCeiling > s.argSize()) {
          xCeiling = s.argSize();
        }
        // factor = c + d * FractionalPart(x);
        IExpr factor =
            d.isZero() || xi.isZero() ? c : S.Plus.of(engine, c, F.Times(d, xi.fractionalPart()));
        // s[[Floor(x)]] interpolated toward s[[Ceiling(x)]] by factor
        return interpolate(s.get(xFloor), s.get(xCeiling), factor);
      }
    }
    return F.NIL;
  }

  /**
   * The quantile points of one sorted list at every <code>q</code> in <code>qList</code>.
   *
   * <p>
   * Sorting the data and walking its real-number gate dominate the cost of a quantile, and both
   * are independent of <code>q</code>. Re-entering <code>Quantile</code> once per element repeats
   * them: <code>Quartiles</code> used to pay for three sorts and four gate walks to produce three
   * numbers, and <code>InterquartileRange</code> for a fifth gate walk on top of that.
   *
   * @return {@link F#NIL} unless every point is computable, so that a caller can fall back to its
   *         own per-element path without having to reason about a partial result
   */
  private static IAST quantilePoints(IAST s, IInteger length, IAST qList, IExpr a, IExpr b,
      IExpr c, IExpr d, EvalEngine engine) {
    IASTAppendable result = F.ListAlloc(qList.argSize());
    for (int i = 1; i < qList.size(); i++) {
      IExpr q = qList.get(i);
      if (!q.isReal()) {
        return F.NIL;
      }
      IExpr point = quantilePoint(s, length, (IReal) q, a, b, c, d, engine);
      if (point.isNIL()) {
        return F.NIL;
      }
      result.append(point);
    }
    return result;
  }

  /**
   * The median of <code>values</code>, which is sorted in place.
   *
   * <p>
   * Replaces Hipparchus <code>StatUtils#percentile(values, 50)</code>, which interpolates the
   * even-length case as <code>lo + 0.5*(hi-lo)</code> and so returned <code>Infinity</code> for
   * <code>Median({-1.0*10^308, 1.0*10^308})</code>. That also left the machine-precision path
   * disagreeing with the exact path through {@link #interpolate(IExpr, IExpr, IExpr)} on the same
   * data.
   *
   * @return the median, or <code>Double.NaN</code> for an empty array
   */
  public static double median(double[] values) {
    Arrays.sort(values);
    return medianOfSorted(values);
  }

  /**
   * The median of an already ascending-sorted array. See {@link #median(double[])}.
   *
   * @return the median, or <code>Double.NaN</code> for an empty array
   */
  public static double medianOfSorted(double[] sorted) {
    int n = sorted.length;
    if (n == 0) {
      return Double.NaN;
    }
    int half = n >> 1;
    if ((n & 1) == 1) {
      return sorted[half];
    }
    // the convex combination, not (sorted[half - 1] + sorted[half]) / 2.0, which overflows
    return 0.5 * sorted[half - 1] + 0.5 * sorted[half];
  }

  /**
   * The arithmetic mean of <code>values</code>, falling back to an incremental algorithm when
   * summation overflows.
   *
   * <p>
   * The Hipparchus corrected two-pass mean is the more accurate of the two and is used whenever it
   * works, but its first pass forms the plain sum. For
   * <code>MeanDeviation({-1.0*10^308, 1.0*10^308})</code> the deviations are both
   * <code>1.0*10^308</code>, their sum is <code>Infinity</code>, the correction term is then
   * <code>-Infinity</code>, and the result is <code>NaN</code> where the answer is
   * <code>1.0*10^308</code>. Overflow always shows up as a non-finite result, so the fallback runs
   * only on data the fast path has already failed on and accurate results are returned unchanged.
   * Data that is genuinely non-finite yields the same non-finite value from either algorithm.
   *
   * @return the mean, or <code>Double.NaN</code> for an empty array
   */
  public static double mean(double[] values) {
    double result = StatUtils.mean(values);
    if (Double.isFinite(result) || values.length == 0) {
      return result;
    }
    // no partial mean ever exceeds the range of the data, so this cannot overflow
    double incremental = 0.0;
    for (int i = 0; i < values.length; i++) {
      incremental += (values[i] - incremental) / (i + 1);
    }
    return incremental;
  }

  public static void initialize() {
    Initializer.init();
  }

  private StatisticsFunctions() {}
}
