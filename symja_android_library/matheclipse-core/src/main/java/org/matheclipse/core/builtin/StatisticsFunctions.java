package org.matheclipse.core.builtin;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.function.Predicate;
import java.util.function.Supplier;
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
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.IntervalDataSym;
import org.matheclipse.core.expression.S;
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
import org.matheclipse.core.interfaces.statistics.IDiscreteDistribution;
import org.matheclipse.core.interfaces.statistics.IDistribution;
import org.matheclipse.core.interfaces.statistics.IPDF;
import org.matheclipse.core.interfaces.statistics.IRandomVariate;
import org.matheclipse.core.interfaces.statistics.IStatistics;
import org.matheclipse.core.reflection.system.NSum;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

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
      S.Expectation.setEvaluator(new Expectation());
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
            IExpr xArg = F.NIL;
            if (ast.isAST2()) {
              xArg = ast.arg2();
            }
            if (dist.head().isSymbol()) {
              ISymbol head = (ISymbol) dist.head();

              if (dist.head().isSymbol()) {
                if (head instanceof IBuiltInSymbol) {
                  IEvaluator evaluator = ((IBuiltInSymbol) head).getEvaluator();
                  if (evaluator instanceof ICDF) {
                    ICDF inverseCDF = (ICDF) evaluator;
                    return inverseCDF.inverseCDF(dist, xArg, engine);
                  }
                }
              }
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
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.ORDERLESS | ISymbol.NUMERICFUNCTION);
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
            IExpr xArg = F.NIL;
            if (ast.isAST2()) {
              xArg = ast.arg2();
            }
            if (dist.head().isSymbol()) {
              ISymbol head = (ISymbol) dist.head();

              if (dist.head().isSymbol()) {
                if (head instanceof IBuiltInSymbol) {
                  IEvaluator evaluator = ((IBuiltInSymbol) head).getEvaluator();
                  if (evaluator instanceof ICDF) {
                    ICDF cdf = (ICDF) evaluator;
                    return cdf.cdf(dist, xArg, engine);
                  }
                }
              }
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
        dxNum = F.num(arg2.evalf());
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
          return list.mapMatrixColumns(dim, x -> F.GeometricMean(x));
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
          return list.mapMatrixColumns(dim, x -> F.HarmonicMean(x));
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
      if (ast.size() == 2) {
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
  private static class Expectation extends AbstractFunctionEvaluator {
    // static final double CDF_NUMERIC_THRESHOLD = Config.DOUBLE_EPSILON;
    //
    // static boolean isFinished(IExpr p_equals, IExpr cumprob) {
    // boolean finished = false;
    // finished |= cumprob.isOne();
    // finished |= // !ExactScalarQ.of(cumprob) && //
    // p_equals.isZero() && //
    // F.isZero(cumprob.subtract(F.C1).abs().evalDouble(), CDF_NUMERIC_THRESHOLD);
    // return finished;
    // }
    //
    // private static IExpr expect(Function<IExpr, IExpr> function, IAST distribution,
    // IDiscreteDistribution discreteDistribution) {
    // IExpr value = null;
    // IExpr p_equals = F.C0;
    // IExpr cumprob = F.C0;
    // int sample = discreteDistribution.getSupportLowerBound(distribution);
    // while (!isFinished(p_equals, cumprob)) {
    // IExpr x = F.QQ(sample, 1);
    // p_equals = discreteDistribution.pEquals(sample, distribution);
    // cumprob = cumprob.add(p_equals);
    // IExpr delta = function.apply(x).multiply(p_equals);
    // value = Objects.isNull(value) ? delta : value.add(delta);
    // ++sample;
    // }
    // return value;
    // }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      try {
        IExpr xExpr = ast.arg1();
        if (xExpr.isFunction() && ast.arg2().isList()) {
          IAST data = (IAST) ast.arg2();
          IASTAppendable sum = F.PlusAlloc(data.size());
          for (int i = 1; i < data.size(); i++) {
            sum.append(F.unaryAST1(xExpr, data.get(i)));
          }
          return sum.divide(F.ZZ(data.argSize()));
          // int sum = 0;
          // for (int i = 1; i < data.size(); i++) {
          // if (engine.evalTrue(F.unaryAST1(predicate, data.get(i)))) {
          // sum++;
          // }
          // }
          // return F.QQ(sum, data.argSize());
        }
        if (ast.arg2().isAST(S.Distributed, 3)) {
          IExpr x = ast.arg2().first();
          IExpr distribution = ast.arg2().second();
          if (distribution.isList()) {
            IAST data = (IAST) distribution;
            // Sum( predicate , data ) / data.argSize()
            IASTAppendable sum = F.PlusAlloc(data.size());
            for (int i = 1; i < data.size(); i++) {
              sum.append(F.subst(xExpr, F.Rule(x, data.get(i))));
            }
            return sum.divide(F.ZZ(data.argSize()));
          } else if (distribution.isContinuousDistribution()) {
            IExpr pdf = S.PDF.of(engine, distribution, x);
            if (pdf.isFree(S.Piecewise)) {
              return F.Integrate(F.Times(ast.arg1(), pdf), F.list(x, F.CNInfinity, F.CInfinity));
            } else {
              // TODO improve integration of Piecewise function
              // if (pdf.isAST2()) {
              // IExpr arg1 = pdf.first();
              // IExpr arg2 = pdf.second();
              // int[] dims = arg1.isMatrix(false);
              // if (arg1.isListOfLists() && dims != null && dims.length == 2 && dims[1] == 2) {
              // IAST piecewiseList = (IAST) arg1;
              // IASTAppendable result = F.ListAlloc(piecewiseList.size());
              // for (int i = 1; i < piecewiseList.size(); i++) {
              // IAST pair = (IAST) piecewiseList.get(i);
              // IExpr integrate = F.Integrate.of(engine, F.Times(ast.arg1(), pair.arg1()), x);
              // result.append(F.List(integrate, pair.arg2()));
              // }
              // IExpr integrate = F.Integrate.of(engine, F.Times(ast.arg1(), arg2),
              // F.list(x, F.CNInfinity, F.CInfinity));
              // return F.Piecewise(result, integrate);
              // }
              // }
            }
          } else if (distribution.isDiscreteDistribution()) {
            // TODO
          }
        }
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        return Errors.printMessage(S.Expectation, rex, engine);
      }

      return F.NIL;
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

  private static class NExpectation extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      try {
        IExpr xExpr = ast.arg1();
        if (xExpr.isFunction() && ast.arg2().isList()) {
          IAST data = (IAST) ast.arg2();
          IASTAppendable sum = F.PlusAlloc(data.size());
          for (int i = 1; i < data.size(); i++) {
            sum.append(F.unaryAST1(xExpr, data.get(i)));
          }
          return sum.divide(F.ZZ(data.argSize()));
        }
        if (ast.arg2().isAST(S.Distributed, 3)) {
          IExpr x = ast.arg2().first();
          IExpr distribution = ast.arg2().second();
          if (distribution.isList()) {
            IAST data = (IAST) distribution;
            // Sum( predicate , data ) / data.argSize()
            IASTAppendable sum = F.PlusAlloc(data.size());
            INumber sumValue = F.C0;
            sum.append(sumValue);
            for (int i = 1; i < data.size(); i++) {
              IExpr summand = engine.evaluate(F.subst(xExpr, F.Rule(x, data.get(i))));
              if (summand.isNumber()) {
                sumValue = sumValue.plus((INumber) summand);
              } else {
                sum.append(summand);
              }
            }
            sum.set(1, sumValue);
            return engine.evaluate(sum.divide(F.ZZ(data.argSize())));
          } else if (distribution.isContinuousDistribution()) {
            IExpr pdf = S.PDF.of(engine, distribution, x);
            if (pdf.isPresent()) {
              return F.NIntegrate(F.Times(ast.arg1(), pdf), F.list(x, F.CNInfinity, F.CInfinity));
            }
          } else if (distribution.isDiscreteDistribution()) {
            IExpr pdf = S.PDF.of(engine, distribution, x);
            if (pdf.isPresent()) {
              IDiscreteDistribution dist = getDiscreteDistribution(distribution);
              int supportUpperBound = dist.getSupportUpperBound(distribution);
              if (supportUpperBound < Integer.MAX_VALUE) {
                int supportLowerBound = dist.getSupportLowerBound(distribution);
                if (supportLowerBound > Integer.MIN_VALUE
                    && supportLowerBound < supportUpperBound) {
                  IAST function = F.Times(ast.arg1(), pdf);
                  int lowerBound = dist.getSupportLowerBound(distribution);
                  return NSum.nsum(function, x, F.ZZ(lowerBound), F.ZZ(supportUpperBound));
                }
              }
              IAST function = F.Times(ast.arg1(), pdf);
              return NSum.nsum(function, x, F.CNInfinity, F.CInfinity);
            }
          }
        }
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        return Errors.printMessage(S.Expectation, rex, engine);
      }

      return F.NIL;
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

  private static class NProbability extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.size() == 3) {
        try {
          if (ast.arg2().isList()) {
            IExpr predicate = ast.arg1();
            IAST data = (IAST) ast.arg2();
            if (predicate.isFunction()) {
              // Sum( Boole(predicate), data ) / data.argSize()
              int sum = 0;
              for (int i = 1; i < data.size(); i++) {
                if (engine.evalTrue(predicate, data.get(i))) {
                  sum++;
                }
              }
              return F.QQ(sum, data.argSize());
            }
          } else if (ast.arg2().isAST(S.Distributed, 3)) {
            IExpr predicate = ast.arg1();
            IExpr x = ast.arg2().first();
            IExpr distribution = ast.arg2().second();
            if (distribution.isList()) {
              IAST data = (IAST) distribution;
              // Sum( Boole(predicate), data ) / data.argSize()
              int sum = 0;
              for (int i = 1; i < data.size(); i++) {
                if (engine.evalTrue(F.subst(predicate, F.Rule(x, data.get(i))))) {
                  sum++;
                }
              }
              return F.QQ(sum, data.argSize());
            } else if (distribution.isDiscreteDistribution()) {
              IExpr pdf = S.PDF.ofNIL(engine, distribution, x);
              if (pdf.isPresent()) {
                IDiscreteDistribution dist = getDiscreteDistribution(distribution);
                IntArrayList interval = dist.range(distribution, predicate, x);
                if (interval != null) {
                  // for discrete distributions take the sum:
                  IASTAppendable sum = F.PlusAlloc(10);
                  for (int i = 0; i < interval.size(); i += 2) {
                    for (int j = interval.getInt(i); j <= interval.getInt(i + 1); j++) {
                      if (engine.evalTrue(F.subst(predicate, F.Rule(x, F.num(j))))) {
                        sum.append(F.subst(pdf, F.Rule(x, F.num(j))));
                      }
                    }
                  }
                  return sum;
                } else {
                  return engine.evaluate(F.NSum(F.Times(F.Boole(predicate), pdf),
                      F.List(x, F.CNInfinity, F.CInfinity)));

                }
              }
            } else if (distribution.isContinuousDistribution()) {
              IExpr pdf = S.PDF.ofNIL(engine, distribution, x);
              if (pdf.isPresent()) {
                if (predicate.isRelational()) {
                  IAST interval = IntervalDataSym.relationToIntervalSet((IAST) predicate, x);
                  if (interval.isIntervalData() && interval.argSize() == 1) {
                    IAST intervalList = (IAST) interval.arg1();
                    return engine.evaluate(
                        F.NIntegrate(pdf, F.List(x, intervalList.arg1(), intervalList.arg4())));
                  }
                }
                return engine.evaluate(F.NIntegrate(F.Times(F.Boole(predicate), pdf),
                    F.List(x, F.CNInfinity, F.CInfinity)));
              }
            }
          }
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          return Errors.printMessage(S.Probability, rex, engine);
        }
      }
      return F.NIL;
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
      IExpr arg1 = ast.arg1();
      int length = arg1.isVector();
      if (length > 0) {
        IExpr resultQuartiles = engine.evaluate(F.Quartiles(arg1));
        if (resultQuartiles.isList3()) {
          return F.Subtract(resultQuartiles.getAt(3), resultQuartiles.getAt(1));
        }
      } else if (arg1.isDistribution()) {
        return F.Subtract(F.Quantile(arg1, F.C3D4), F.Quantile(arg1, F.C1D4));
      }
      return F.NIL;
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
    public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
      try {
        if (arg1.isRealVector()) {
          double[] values = arg1.toDoubleVector();
          if (values == null) {
            return F.NIL;
          }
          return F.num(StatUtils.mean(values));
        }
        if (arg1.isList()) {
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


  private static final class MeanDeviation extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();

      final IntList dimensions =
          LinearAlgebraUtil.dimensions(arg1, S.List, Integer.MAX_VALUE, false);
      if (dimensions.size() != 0) {
        switch (dimensions.size()) {
          case 1:
            int length = dimensions.getInt(0);
            if (arg1.isRealVector()) {
              double[] values = arg1.toDoubleVector();
              if (values == null) {
                return F.NIL;
              }
              double mean = StatUtils.mean(values);
              double[] newValues = new double[length];
              for (int i = 0; i < length; i++) {
                newValues[i] = Math.abs(values[i] - mean);
              }
              return F.num(StatUtils.mean(newValues));
            }
            arg1 = arg1.normal(false);
            if (arg1.isList()) {
              IAST vector = (IAST) arg1;
              int size = vector.size();
              IASTAppendable sum = F.PlusAlloc(size);
              final IExpr mean = S.Mean.of(engine, F.Negate(vector));
              vector.forEach(x -> sum.append(F.Abs(F.Plus(x, mean))));
              return F.Times(F.Power(F.ZZ(size - 1), -1), sum);
            }
            return F.NIL;
          case 2:
            return arg1.mapMatrixColumns(dimensions.toIntArray(), x -> F.MeanDeviation(x));
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
    public IExpr evaluateArg1(final IExpr arg1, EvalEngine engine) {
      if (arg1.isRealVector()) {
        double[] values = arg1.toDoubleVector();
        if (values == null) {
          return F.NIL;
        }
        return F.num(StatUtils.percentile(values, 50));
      }
      if (arg1.isAST(S.WeightedData, 3) && arg1.first().isList() && arg1.second().isList()) {
        return median((IAST) arg1, engine);
      }
      final IntList dimensions =
          LinearAlgebraUtil.dimensions(arg1, S.List, Integer.MAX_VALUE, false);
      if (dimensions.size() > 0) {
        // Rectangular array of real numbers is expected at position `1` in `2`.
        if (!arg1.forAllLeaves(x -> x.isRealResult())) {
          return Errors.printMessage(S.Median, "rectn", F.List(F.C1, F.Median(arg1)), engine);
        }
        if (dimensions.size() >= 2) {
          IExpr result = engine.evaluate(F.ArrayReduce(S.Median, arg1, F.C1));
          return result.normal(false);
        }
        if (dimensions.size() == 2) {
          return arg1.mapMatrixColumns(dimensions.toIntArray(), x -> F.Median(x));
        }
        if (dimensions.size() == 1) {
          IExpr normal = arg1.normal(false);
          if (normal.isList()) {
            IAST list = (IAST) normal;
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
        }
      }

      if (arg1.isDistribution()) {
        return getDistribution(arg1).median((IAST) arg1);
      }
      return F.NIL;
    }

    /**
     * Evaluate the median value of the weighted data.
     *
     * @param weightedData
     * @param engine
     * @return <code>F.NIL</code> if the input <code>data, weight</code> lists aren't of the same
     *         length.
     */
    private static IExpr median(final IAST weightedData, EvalEngine engine) {
      IAST data = (IAST) weightedData.arg1();
      IAST weights = (IAST) weightedData.arg2();
      final int size = data.size();
      if (size > 1 && size == weights.size()) {
        IASTAppendable[] res = sortWeightedData(data, weights, engine);
        data = res[0];
        weights = res[1];

        IExpr denominator = engine.evaluate(weights.apply(S.Plus));
        IASTAppendable result = F.PlusAlloc(size);
        for (int i = 1; i < size; i++) {
          IASTAppendable rhs = F.PlusAlloc(size);
          for (int j = 1; j <= i; j++) {
            rhs.append(F.Divide(weights.get(j), denominator));
          }
          IExpr lhs = rhs.splice(rhs.argSize());
          // Boole( Inequality(lhs < 1/2 <= rhs) );
          IExpr boole =
              engine.evaluate(F.Boole(F.Inequality(lhs, S.Less, F.C1D2, S.LessEqual, rhs)));
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
  private static class Probability extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.size() == 3) {
        try {
          if (ast.arg2().isList()) {
            IExpr predicate = ast.arg1();
            IAST data = (IAST) ast.arg2();
            if (predicate.isFunction()) {
              // Sum( Boole(predicate), data ) / data.argSize()
              int sum = 0;
              for (int i = 1; i < data.size(); i++) {
                if (engine.evalTrue(predicate, data.get(i))) {
                  sum++;
                }
              }
              return F.QQ(sum, data.argSize());
            }
          } else if (ast.arg2().isAST(S.Distributed, 3)) {
            IExpr predicate = ast.arg1();
            IExpr x = ast.arg2().first();
            IExpr distribution = ast.arg2().second();
            if (distribution.isList()) {
              IAST data = (IAST) distribution;
              // Sum( Boole(predicate), data ) / data.argSize()
              int sum = 0;
              for (int i = 1; i < data.size(); i++) {
                if (engine.evalTrue(F.subst(predicate, F.Rule(x, data.get(i))))) {
                  sum++;
                }
              }
              return F.QQ(sum, data.argSize());
            } else if (distribution.isDiscreteDistribution()) {
              IDiscreteDistribution dist = getDiscreteDistribution(distribution);
              IExpr pdf = S.PDF.ofNIL(engine, distribution, x);
              if (pdf.isPresent()) {
                IntArrayList interval = dist.range(distribution, predicate, x);
                if (interval != null) {
                  // for discrete distributions take the sum:
                  IASTAppendable sum = F.PlusAlloc(10);
                  for (int i = 0; i < interval.size(); i += 2) {
                    for (int j = interval.getInt(i); j <= interval.getInt(i + 1); j++) {
                      if (engine.evalTrue(F.subst(predicate, F.Rule(x, F.ZZ(j))))) {
                        sum.append(F.subst(pdf, F.Rule(x, F.ZZ(j))));
                      }
                    }
                  }
                  return sum;
                } else {
                  return engine.evaluate(F.Sum(F.Times(F.Boole(predicate), pdf),
                      F.List(x, F.CNInfinity, F.CInfinity)));
                }
              }
            } else if (distribution.isContinuousDistribution()) {
              IExpr pdf = S.PDF.ofNIL(engine, distribution, x);
              if (pdf.isPresent()) {
                // if (predicate.isRelational()) {
                // IAST interval = IntervalDataSym.relationToInterval((IAST) predicate, x);
                // if (interval.isIntervalData() && interval.argSize() == 1) {
                // IAST intervalList = (IAST) interval.arg1();
                // return engine.evaluate(
                // F.Integrate(pdf, F.List(x, intervalList.arg1(), intervalList.arg4())));
                // }
                // }
                return engine.evaluate(F.Integrate(F.Times(F.Boole(predicate), pdf),
                    F.List(x, F.CNInfinity, F.CInfinity)));
              }
            }
          }
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          return Errors.printMessage(S.Probability, rex, engine);
        }
      }
      return F.NIL;
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
            if (dist.head().isSymbol()) {
              ISymbol head = (ISymbol) dist.head();

              if (dist.head().isSymbol()) {
                if (head instanceof IBuiltInSymbol) {
                  IEvaluator evaluator = ((IBuiltInSymbol) head).getEvaluator();
                  if (evaluator instanceof IPDF) {
                    IPDF pdf = (IPDF) evaluator;
                    dist = pdf.checkParameters(dist);
                    if (dist.isPresent()) {
                      return pdf.pdf(dist, xArg, engine);
                    }
                  }
                }
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
      IExpr arg1 = ast.arg1();

      final IntList dimensions =
          LinearAlgebraUtil.dimensions(arg1, S.List, Integer.MAX_VALUE, false);
      if (dimensions.size() > 0) {
        // Rectangular array of real numbers is expected at position `1` in `2`.
        if (!arg1.forAllLeaves(x -> x.isRealResult())) {
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
        return arg1.mapMatrixColumns(dim, (IExpr x) -> ast.setAtCopy(1, x));
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

          int dim1 = list.argSize();
          try {
            if (dim1 == 0) {
              // Argument `1` should be a non-empty list.
              return Errors.printMessage(ast.topHead(), "empt", F.list(list), engine);
            }
            if (dim1 > 0 && ast.size() >= 3) {

              final IAST s = EvalAttributes.copySortLess(list);
              final IInteger length = F.ZZ(s.argSize());

              IExpr q = ast.arg2();
              int dim2 = q.isVector();
              if (dim2 >= 0 && q.isList()) {
                final IAST vector = ((IAST) q);
                if (vector.exists(x -> x.isReal() && !((IReal) x).isRange(F.C0, F.C1))) {
                  // The Quantile specification `1` should be a number or a list of numbers between
                  // `2` and `3`.
                  return Errors.printMessage(ast.topHead(), "nquan", F.list(q, F.C0, F.C1), engine);
                }
                return vector.mapThread(ast, 2);
              } else {
                if (q.isReal()) {
                  IReal qi = (IReal) q;
                  if (!qi.isRange(F.C0, F.C1)) {
                    // The Quantile specification `1` should be a number or a list of numbers
                    // between `2` and `3`.
                    return Errors.printMessage(ast.topHead(), "nquan", F.list(qi, F.C0, F.C1),
                        engine);
                  }
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
                      IExpr factor = d.isZero() || xi.isZero() ? c
                          : S.Plus.of(engine, c, F.Times(d, xi.fractionalPart()));
                      // s[[Floor(x)]]+(s[[Ceiling(x)]]-s[[Floor(x)]]) * (c + d *
                      // FractionalPart(x))
                      return F.Plus(s.get(xFloor), //
                          F.Times(F.Subtract(s.get(xCeiling), s.get(xFloor)), factor));
                    }
                  }
                }
              }
            }
          } catch (ArithmeticException ae) {
            return Errors.printMessage(S.Quantile, ae, engine);
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

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      super.setUp(newSymbol);
    }
  }


  private static class Quartiles extends AbstractFunctionEvaluator {

    private static final IAST Q = F.list(F.C1D4, F.C1D2, F.C3D4);

    private static final IAST PARAMETER = F.list(F.list(F.C1D2, F.C0), F.list(F.C0, F.C1));

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      final IntList dimensions =
          LinearAlgebraUtil.dimensions(arg1, S.List, Integer.MAX_VALUE, false);
      if (dimensions.size() > 0) {
        // Rectangular array of real numbers is expected at position `1` in `2`.
        if (!arg1.forAllLeaves(x -> x.isRealResult())) {
          return Errors.printMessage(S.Quartiles, "rectn", F.List(F.C1, ast), engine);
        }
        if (dimensions.size() > 2) {
          return F.ArrayReduce(F.Function(ast.setAtCopy(1, F.Slot1)), arg1, F.C1);
        }
      }

      if ((arg1.isNonEmptyList()) || arg1.isDistribution()) {
        IAST list = (IAST) arg1;
        if (ast.size() == 3) {
          IExpr arg2 = ast.arg2();
          int[] dimParameters = arg2.isMatrix();
          if (dimParameters == null || dimParameters[0] != 2 || dimParameters[1] != 2) {
            return F.NIL;
          }
          return engine.evaluate(F.Quantile(list, Q, arg2));
        }
        return engine.evaluate(F.Quantile(list, Q, PARAMETER));
        // System.out.println("Quartiles: " + temp);
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
                    int n = arg2.toIntDefault();
                    if (n >= 0) {
                      if (n >= Config.MAX_AST_SIZE) {
                        ASTElementLimitExceeded.throwIt(n);
                      }
                      return variate.randomVariate(random, dist, n);
                    }
                  }
                  return F.NIL;
                }
                return variate.randomVariate(random, dist, 1);
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
        IExpr min = S.Min.of(engine, x);
        IExpr max = S.Max.of(engine, x);
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
      IExpr inverseDifference = engine.evaluate(F.Power(F.Subtract(max, min), -1));
      return engine
          .evaluate(F.Plus(F.Times(F.CN1, inverseDifference, min), F.Times(inverseDifference, x)));
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
        return arg1.mapMatrixColumns(dim, x -> F.StandardDeviation(x));
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
        IExpr temp = arg1.mapMatrixColumns(dim, v -> F.Standardize(v, f1, f2));
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
        if (isDistribution(dist)) {
          return F.Expand(F.Subtract(F.C1, F.CDF(dist)));
        }
        return F.NIL;
      }
      if (ast.isAST2() && ast.first().isAST()) {
        IAST dist = (IAST) ast.arg1();
        if (isDistribution(dist)) {
          if (ast.arg2().isList()) {
            return ast.arg2().mapThread(ast, 2);
          }
          return F.Expand(F.Subtract(F.C1, F.CDF(dist, ast.arg2())));
        }
        return F.NIL;
      }
      return F.NIL;
    }

    /**
     * Check if <code>dist</code> is a distribution AST.
     *
     * @param dist
     * @return <code>F.NIL</code> if <code>dist</code> is not a distribution.
     */
    private static boolean isDistribution(IAST dist) {
      if (dist.head().isSymbol()) {
        ISymbol head = (ISymbol) dist.head();
        if (head instanceof IBuiltInSymbol) {
          IEvaluator evaluator = ((IBuiltInSymbol) head).getEvaluator();
          if (evaluator instanceof IDistribution) {
            return true;
          }
        }
      }
      return false;
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
                return distribution.variance(dist);
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

  public static void initialize() {
    Initializer.init();
  }

  private StatisticsFunctions() {}
}
