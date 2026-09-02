package org.matheclipse.core.builtin;

import java.util.Random;
import org.hipparchus.distribution.RealDistribution;
import org.hipparchus.random.RandomDataGenerator;
import org.hipparchus.stat.descriptive.StreamingStatistics;
import org.hipparchus.util.MathUtils;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.ASTRealVector;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IAST.PROPERTY;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.interfaces.statistics.ICDF;
import org.matheclipse.core.interfaces.statistics.ICentralMoment;
import org.matheclipse.core.interfaces.statistics.IContinuousDistribution;
import org.matheclipse.core.interfaces.statistics.ICovariance;
import org.matheclipse.core.interfaces.statistics.IGeneratingFunction;
import org.matheclipse.core.interfaces.statistics.IPDF;
import org.matheclipse.core.interfaces.statistics.IRandomVariate;
import org.matheclipse.core.interfaces.statistics.IStatistics;

public class StatisticsContinousDistribution {
  private static final class BetaDistribution extends AbstractEvaluator implements
      IContinuousDistribution, IRandomVariate, IStatistics, IPDF, ICDF, IGeneratingFunction {

    @Override
    public IExpr mgf(IAST dist, IExpr t, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        // Hypergeometric1F1(a, a + b, t)
        return F.Hypergeometric1F1(a, F.Plus(a, b), t);
      }
      return F.NIL;
    }

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2()) {
        //
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        if (!engine.isArbitraryMode() //
            && (a.isNumericArgument(true) //
                || b.isNumericArgument(true) //
                || k.isNumericArgument(true))) {
          double aDouble = a.evalfNaN();
          double bDouble = b.evalfNaN();
          double kDouble = k.evalfNaN();
          if (!Double.isNaN(aDouble) && !Double.isNaN(bDouble) && !Double.isNaN(kDouble)) {
            try {
              return F
                  .num(new org.hipparchus.distribution.continuous.BetaDistribution(aDouble, bDouble) //
                      .cumulativeProbability(kDouble));
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
              //
            }
          }
        }
        IExpr function =
            // [$ (Piecewise({{BetaRegularized(#, a, b), 0 < # < 1}, {1, # >= 1}}, 0)&) $]
            F.Function(F.Piecewise(
                F.list(F.list(F.BetaRegularized(F.Slot1, a, b), F.Less(F.C0, F.Slot1, F.C1)),
                    F.list(F.C1, F.GreaterEqual(F.Slot1, F.C1))),
                F.C0)); // $$;
        return callFunction(function, k);
      }

      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // 2 arguments
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public IExpr inverseCDF(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2()) {
        //
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        IExpr function =
            // [$ ( ConditionalExpression(Piecewise({{InverseBetaRegularized(#, a, b), 0 < # < 1},
            // {0, # <=
            // 0}}, 1), 0 <= # <= 1)& ) $]
            F.Function(
                F.ConditionalExpression(
                    F.Piecewise(
                        F.list(
                            F.list(F.InverseBetaRegularized(F.Slot1, a, b),
                                F.Less(F.C0, F.Slot1, F.C1)),
                            F.list(F.C0, F.LessEqual(F.Slot1, F.C0))),
                        F.C1),
                    F.LessEqual(F.C0, F.Slot1, F.C1))); // $$;
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr mean(IAST dist) {
      if (dist.isAST2()) {
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        // a / (a+b)
        return F.Divide(a, F.Plus(a, b));
      }
      return F.NIL;
    }

    @Override
    public IExpr median(IAST dist) {
      if (dist.isAST2()) {
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        // (a,b) => InverseBetaRegularized(1/2, a, b)
        return F.InverseBetaRegularized(F.C1D2, a, b);
      }
      return F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2()) {
        //
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        if (!engine.isArbitraryMode() && //
            (a.isNumericArgument(true) || b.isNumericArgument(true) || k.isNumericArgument(true))) {
          double aDouble = a.evalfNaN();
          double bDouble = b.evalfNaN();
          double kDouble = k.evalfNaN();
          if (!Double.isNaN(aDouble) && !Double.isNaN(bDouble) && !Double.isNaN(kDouble)) {
            try {
              return F
                  .num(new org.hipparchus.distribution.continuous.BetaDistribution(aDouble, bDouble) //
                      .density(kDouble));
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
              //
            }
          }
        }
        IExpr function =
            // [$ ( Piecewise({{((1 - #)^(-1 + b)*#^(-1 + a))/Beta(a, b), 0 < #< 1}}, 0)&) $]
            F.Function(F.Piecewise(F.list(F.list(F.Times(F.Power(F.Beta(a, b), F.CN1),
                F.Power(F.Subtract(F.C1, F.Slot1), F.Plus(F.CN1, b)),
                F.Power(F.Slot1, F.Plus(F.CN1, a))), F.Less(F.C0, F.Slot1, F.C1))), F.C0)); // $$;
        return callFunction(function, k);
      }

      return F.NIL;
    }

    @Override
    public IExpr randomVariate(Random random, IAST dist, int size) {
      if (dist.isAST2()) {
        // exception handling in RandonmVariate() function
        IReal a = dist.arg1().evalReal();
        IReal b = dist.arg2().evalReal();
        if (a != null && b != null) {
          RandomDataGenerator rdg = new RandomDataGenerator();
          double[] vector =
              rdg.nextDeviates(new org.hipparchus.distribution.continuous.BetaDistribution(
                  a.doubleValue(), b.doubleValue()), size);
          return new ASTRealVector(vector, false);
        }
      }
      return F.NIL;
    }

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
      if (dist.isAST2()) {
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        return
        // [$ ( (a*b)/((a + b)^2*(1 + a + b)) ) $]
        F.Times(a, b, F.Power(F.Times(F.Sqr(F.Plus(a, b)), F.Plus(F.C1, a, b)), F.CN1)); // $$;
      }
      return F.NIL;
    }
  }

  private static final class BinormalDistribution extends AbstractEvaluator
      implements IContinuousDistribution, ICDF, ICovariance, IPDF, IStatistics, IRandomVariate,
      ICentralMoment {

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public IExpr centralMoment(IAST dist, IExpr m, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public IExpr covariance(IAST dist, EvalEngine engine) {
      if (dist.isAST1()) {
        IExpr p = dist.arg2();
        return F.list(F.list(F.C1, p), F.list(p, F.C1));
      }
      if (dist.isAST2() && dist.first().isList2()) {
        IAST sigma = (IAST) dist.first();
        IExpr s1 = sigma.arg1();
        IExpr s2 = sigma.arg2();
        IExpr p = dist.arg2();
        IExpr v1 = F.Times(p, s1, s2);
        return F.list(F.list(F.Sqr(s1), v1), F.list(v1, F.Sqr(s2)));
      }
      if (dist.isAST3() && dist.first().isList2() && dist.second().isList2()) {
        // IAST mean = (IAST) dist.first();
        IAST sigma = (IAST) dist.second();
        IExpr s1 = sigma.arg1();
        IExpr s2 = sigma.arg2();
        IExpr p = dist.arg3();
        IExpr v1 = F.Times(p, s1, s2);
        return F.list(F.list(F.Sqr(s1), v1), F.list(v1, F.Sqr(s2)));
      }
      return F.NIL;
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
    public IExpr inverseCDF(IAST dist, IExpr k, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public IExpr kurtosis(IAST dist, EvalEngine engine) {
      // {3, 3}
      return F.List(F.C3, F.C3);
    }

    @Override
    public IExpr mean(IAST dist) {
      if (dist.isAST1()) {
        return F.CListC0C0;
      }
      if (dist.isAST2()) {
        return F.CListC0C0;
      }
      if (dist.isAST3()) {
        return dist.arg1();
      }
      return F.NIL;
    }

    @Override
    public IExpr median(IAST dist) {
      return F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr X, EvalEngine engine) {
      if (dist.isAST3()) {
        if (X.isList2()) {
          IExpr mu = dist.arg1();
          IExpr sigma = dist.arg2();
          IExpr rho = dist.arg3();
          if (mu.isList2() && sigma.isList2()) {
            IExpr mu1 = mu.first();
            IExpr mu2 = mu.second();
            IExpr sigma1 = sigma.first();
            IExpr sigma2 = sigma.second();
            IExpr x = X.first();
            IExpr y = X.second();

            // z = (x-mu1)^2/sigma1^2 - (2*rho*(x-mu1)*(y-mu2))/(sigma1*sigma2) + (y-mu2)^2/sigma2^2
            IExpr xTerm = F.Divide(F.Subtract(x, mu1), sigma1);
            IExpr yTerm = F.Divide(F.Subtract(y, mu2), sigma2);

            IExpr z = F.Plus(F.Sqr(xTerm), F.Times(F.CN2, rho, xTerm, yTerm), F.Sqr(yTerm));

            // 1 / (2*Pi*sigma1*sigma2*Sqrt(1-rho^2)) * Exp( -z / (2*(1-rho^2)) )
            IExpr oneMinusRhoSq = F.Subtract(F.C1, F.Sqr(rho));

            IExpr factor = F.Power(F.Times(F.C2Pi, sigma1, sigma2, F.Sqrt(oneMinusRhoSq)), F.CN1);
            IExpr exponent = F.Times(F.CN1, z, F.Power(F.Times(F.C2, oneMinusRhoSq), F.CN1));

            return F.Times(factor, F.Exp(exponent));
          }
        }
      }
      return F.NIL;
    }

    @Override
    public IExpr randomVariate(Random random, IAST dist, int size) {
      IExpr mu = F.List(F.C0, F.C0);
      IExpr rhoExpr;
      double[] means = new double[] {0.0, 0.0};
      double[][] covariances;
      if (dist.isAST1()) {
        rhoExpr = dist.arg1();
        double p = rhoExpr.evalfNaN();
        if (Double.isNaN(p)) {
          return F.NIL;
        }
        covariances = new double[][] {{1.0, p}, {p, 1.0}};
      } else if (dist.isAST2()) {
        IExpr sigma = dist.arg1();
        rhoExpr = dist.arg2();
        double s1 = sigma.first().evalfNaN();
        double s2 = sigma.second().evalfNaN();
        rhoExpr = dist.arg3();
        double p = rhoExpr.evalfNaN();
        if (Double.isNaN(s1) || Double.isNaN(s2) || Double.isNaN(p)) {
          return F.NIL;
        }
        double cov12 = p * s1 * s2;
        covariances = new double[][] {{s1 * s1, cov12}, {cov12, s2 * s2}};
      } else if (dist.isAST3()) {
        mu = dist.arg1();
        double mu1 = mu.first().evalfNaN();
        double mu2 = mu.second().evalfNaN();
        if (Double.isNaN(mu1) || Double.isNaN(mu2)) {
          return F.NIL;
        }
        means = new double[] {mu1, mu2};
        IExpr sigma = dist.arg2();
        double s1 = sigma.first().evalfNaN();
        double s2 = sigma.second().evalfNaN();
        rhoExpr = dist.arg3();
        double p = rhoExpr.evalfNaN();
        if (Double.isNaN(s1) || Double.isNaN(s2) || Double.isNaN(p)) {
          return F.NIL;
        }
        double cov12 = p * s1 * s2;
        covariances = new double[][] {{s1 * s1, cov12}, {cov12, s2 * s2}};
      } else {
        return F.NIL;
      }
      try {
        org.hipparchus.distribution.multivariate.MultivariateNormalDistribution mnd =
            new org.hipparchus.distribution.multivariate.MultivariateNormalDistribution(means,
                covariances);

        IASTAppendable list = F.ListAlloc(size);
        for (int i = 0; i < size; i++) {
          double[] sample = mnd.sample();
          list.append(F.List(F.num(sample[0]), F.num(sample[1])));
        }
        return list;

      } catch (RuntimeException rex) {
        // Fallback or NIL if evalf fails
      }

      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

    @Override
    public IExpr skewness(IAST dist) {
      // {0, 0}
      return F.List(F.C0, F.C0);
    }

    @Override
    public IExpr variance(IAST dist) {
      if (dist.isAST1()) {
        return F.List(F.C1, F.C1);
      }
      IExpr sigma = F.NIL;
      if (dist.isAST2()) {
        sigma = dist.arg1();
      }
      if (dist.isAST3()) {
        sigma = dist.arg2();
      }
      if (sigma.isList2()) {
        return F.List(F.Sqr(sigma.first()), F.Sqr(sigma.second()));
      }
      return F.NIL;
    }

  }

  private static final class CauchyDistribution extends AbstractEvaluator
      implements ICDF, IContinuousDistribution, IPDF, IStatistics, IRandomVariate, ICentralMoment,
      IGeneratingFunction {

    @Override
    public IExpr cf(IAST dist, IExpr t, EvalEngine engine) {
      // the moment generating function of the Cauchy distribution does not exist
      if (dist.isAST0()) {
        // E^(-Abs(t))
        return F.Exp(F.Negate(F.Abs(t)));
      }
      if (dist.isAST2()) {
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        // E^(I*a*t - b*Abs(t))
        return F.Exp(F.Plus(F.Times(F.CI, a, t), F.Times(F.CN1, b, F.Abs(t))));
      }
      return F.NIL;
    }

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST0() || dist.isAST2()) {
        IExpr a = F.C0;
        IExpr b = F.C1;
        if (dist.isAST2()) {
          a = dist.arg1();
          b = dist.arg2();
        }
        IExpr function =
            // [$ 1/2+ArcTan((-a+#)/b)/Pi & $]
            F.Function(F.Plus(F.C1D2, F.Times(F.Power(F.Pi, F.CN1),
                F.ArcTan(F.Times(F.Power(b, F.CN1), F.Plus(F.Negate(a), F.Slot1)))))); // $$;
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr centralMoment(IAST dist, IExpr m, EvalEngine engine) {
      if (dist.isAST0() || dist.isAST2()) {
        // Piecewise({{1,m==0}},Indeterminate);
        return F.Piecewise(F.list(F.list(F.C1, F.Equal(m, F.C0))), S.Indeterminate);
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_2;
    }

    @Override
    public IExpr inverseCDF(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST0() || dist.isAST2()) {
        IExpr a = F.C0;
        IExpr b = F.C1;
        if (dist.isAST2()) {
          a = dist.arg1();
          b = dist.arg2();
        }
        IExpr function =
            // [$ ( ConditionalExpression( Piecewise({{a+b*Tan((-(1/2)+#)*Pi), 0 <#< 1},
            // {-Infinity,#<= 0}}, Infinity), 0 <=#<= 1) & ) $]
            F.Function(
                F.ConditionalExpression(
                    F.Piecewise(
                        F.list(
                            F.list(
                                F.Plus(a,
                                    F.Times(b, F.Tan(F.Times(F.Plus(F.CN1D2, F.Slot1), F.Pi)))),
                                F.Less(F.C0, F.Slot1, F.C1)),
                            F.list(F.Negate(F.oo), F.LessEqual(F.Slot1, F.C0))),
                        F.oo),
                    F.LessEqual(F.C0, F.Slot1, F.C1))); // $$;
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr kurtosis(IAST dist, EvalEngine engine) {
      return S.Indeterminate;
    }

    @Override
    public IExpr mean(IAST dist) {
      if (dist.isAST0() || dist.isAST2()) {
        return S.Indeterminate;
      }
      return F.NIL;
    }

    @Override
    public IExpr median(IAST dist) {
      if (dist.isAST0() || dist.isAST2()) {
        IExpr a = F.C0;
        if (dist.isAST2()) {
          a = dist.arg1();
        }
        return a;
      }
      return F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST0() || dist.isAST2()) {
        IExpr a = F.C0;
        IExpr b = F.C1;
        if (dist.isAST2()) {
          a = dist.arg1();
          b = dist.arg2();
        }
        IExpr function =
            // [$ 1/(b*Pi*(1+(-a+#)^2/b^2))& $]
            F.Function(
                F.Power(
                    F.Times(b, F.Pi,
                        F.Plus(F.C1,
                            F.Times(F.Power(b, F.CN2), F.Sqr(F.Plus(F.Negate(a), F.Slot1))))),
                    F.CN1)); // $$;
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr randomVariate(Random random, IAST dist, int size) {
      if (dist.isAST0() || dist.isAST2()) {
        IExpr a = F.C0;
        IExpr b = F.C1;
        if (dist.isAST2()) {
          a = dist.arg1();
          b = dist.arg2();
        }
        // see exception handling in RandonmVariate() function
        double ad = a.evalfNaN();
        double bd = b.evalfNaN();
        if (Double.isNaN(ad) || Double.isNaN(bd)) {
          return F.NIL;
        }
        RandomDataGenerator rdg = new RandomDataGenerator();
        double[] vector = rdg.nextDeviates(
            new org.hipparchus.distribution.continuous.CauchyDistribution(ad, bd), size);
        return new ASTRealVector(vector, false);
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

    @Override
    public IExpr skewness(IAST dist) {
      if (dist.isAST0() || dist.isAST2()) {
        return S.Indeterminate;
      }
      return F.NIL;
    }

    @Override
    public IExpr variance(IAST dist) {
      if (dist.isAST0() || dist.isAST2()) {
        return S.Indeterminate;
      }
      return F.NIL;
    }

  }


  private static final class ChiSquareDistribution extends AbstractEvaluator
      implements ICDF, IContinuousDistribution, IPDF, IStatistics, IRandomVariate, ICentralMoment,
      IGeneratingFunction {

    @Override
    public IExpr mgf(IAST dist, IExpr t, EvalEngine engine) {
      if (dist.isAST1()) {
        IExpr k = dist.arg1();
        // (1 - 2*t)^(-k/2)
        return F.Power(F.Subtract(F.C1, F.Times(F.C2, t)), F.Times(F.CN1D2, k));
      }
      return F.NIL;
    }

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST1()) {
        IExpr v = dist.arg1();
        if (!engine.isArbitraryMode() && //
            (v.isNumericArgument(true) || k.isNumericArgument(true))) {
          double vDouble = v.evalfNaN();
          double kDouble = k.evalfNaN();
          if (!Double.isNaN(vDouble) && !Double.isNaN(kDouble)) {
            try {
              return F
                  .num(new org.hipparchus.distribution.continuous.ChiSquaredDistribution(vDouble) //
                      .cumulativeProbability(kDouble));
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
              //
            }
          }
        }
        IExpr function =
            // [$ Piecewise({{GammaRegularized(v/2, 0, #/2), # > 0}}, 0) & $]
            F.Function(F.Piecewise(F
                .list(F.list(F.GammaRegularized(F.Times(F.C1D2, v), F.C0, F.Times(F.C1D2, F.Slot1)),
                    F.Greater(F.Slot1, F.C0))),
                F.C0)); // $$;
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr centralMoment(IAST dist, IExpr m, EvalEngine engine) {
      // 2^m*HypergeometricU(-m,1-m-n/2,(-1)*1/2*n)
      if (dist.isAST1()) {
        IExpr n = dist.arg1();
        IExpr v1 = F.Negate(m);
        return F.Times(F.Power(F.C2, m),
            F.HypergeometricU(v1, F.Plus(F.C1, F.Times(F.CN1D2, n), v1), F.Times(F.CN1D2, n)));
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // 1 or 3 args
      return F.NIL;
    }

    @Override
    public IExpr inverseCDF(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST1()) {
        IExpr v = dist.arg1();
        IExpr function =
            // [$ ( ConditionalExpression(Piecewise({{2*InverseGammaRegularized(v/2, 0, #), 0 < # <
            // 1}, {0,
            // # <= 0}}, Infinity), 0 <= # <= 1)& ) $]
            F.Function(
                F.ConditionalExpression(
                    F.Piecewise(
                        F.list(
                            F.list(
                                F.Times(F.C2,
                                    F.InverseGammaRegularized(F.Times(F.C1D2, v), F.C0, F.Slot1)),
                                F.Less(F.C0, F.Slot1, F.C1)),
                            F.list(F.C0, F.LessEqual(F.Slot1, F.C0))),
                        F.oo),
                    F.LessEqual(F.C0, F.Slot1, F.C1))); // $$;
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr kurtosis(IAST dist, EvalEngine engine) {
      if (dist.isAST1()) {
        // 3+12/n
        IExpr n = dist.arg1();
        return F.Plus(F.C3, F.Times(F.ZZ(12L), F.Power(n, F.CN1)));
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
        IExpr v = dist.arg1();
        // [$ 2*InverseGammaRegularized(v/2, 0, 1/2) $]
        return F.Times(F.C2, F.InverseGammaRegularized(F.Times(F.C1D2, v), F.C0, F.C1D2)); // $$;
      }
      return F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST1()) {
        IExpr v = dist.arg1();
        if (!engine.isArbitraryMode() && //
            (v.isNumericArgument(true) || k.isNumericArgument(true))) {
          double vDouble = v.evalfNaN();
          double kDouble = k.evalfNaN();
          if (!Double.isNaN(vDouble) && !Double.isNaN(kDouble)) {
            try {
              return F
                  .num(new org.hipparchus.distribution.continuous.ChiSquaredDistribution(vDouble) //
                      .density(kDouble));
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
              //
            }
          }
        }
        IExpr function =
            // [$ Piecewise({{#^(-1 + v/2)/(2^(v/2)*E^(#/2)*Gamma(v/2)), # > 0}}, 0) & $]
            F.Function(F.Piecewise(F.list(F.list(
                F.Times(
                    F.Power(F.Times(F.Power(F.C2, F.Times(F.C1D2, v)),
                        F.Exp(F.Times(F.C1D2, F.Slot1)), F.Gamma(F.Times(F.C1D2, v))), F.CN1),
                    F.Power(F.Slot1, F.Plus(F.CN1, F.Times(F.C1D2, v)))),
                F.Greater(F.Slot1, F.C0))), F.C0)); // $$;
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr randomVariate(Random random, IAST dist, int size) {
      if (dist.isAST1()) {
        double v = dist.arg1().evalfNaN();
        if (Double.isNaN(v)) {
          return F.NIL;
        }
        RandomDataGenerator rdg = new RandomDataGenerator();
        double[] vector = rdg.nextDeviates(
            new org.hipparchus.distribution.continuous.ChiSquaredDistribution(v), size);
        return new ASTRealVector(vector, false);
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

    @Override
    public IExpr skewness(IAST dist) {
      if (dist.isAST1()) {
        IExpr s = dist.arg1();
        // 2*Sqrt(2)*Sqrt(1/s)
        return F.Times(F.C2, F.CSqrt2, F.Sqrt(F.Power(s, F.CN1)));
      }
      return F.NIL;
    }

    @Override
    public IExpr variance(IAST dist) {
      if (dist.isAST1()) {
        IExpr v = dist.arg1();
        // 2*v
        return F.Times(F.C2, v);
      }
      return F.NIL;
    }

  }


  private static final class EmpiricalDistribution extends AbstractEvaluator
      implements ICDF, IContinuousDistribution, IPDF, IStatistics, IRandomVariate, ICentralMoment {

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST1()) {
        org.hipparchus.stat.fitting.EmpiricalDistribution empiricalDistribution =
            (org.hipparchus.stat.fitting.EmpiricalDistribution) dist
                .getProperty(PROPERTY.EMPIRICAL_DISTRIBUTION);
        if (empiricalDistribution != null) {
          // if (!engine.isArbitraryMode()) {
          double x = k.evalfNaN();
          if (!Double.isNaN(x)) {
            try {
              return F.num(empiricalDistribution.cumulativeProbability(x));
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
              //
            }
          }
          // }
        }
      }
      return F.NIL;
    }

    @Override
    public IExpr centralMoment(IAST dist, IExpr m, EvalEngine engine) {
      if (dist.isAST1()) {
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      org.hipparchus.stat.fitting.EmpiricalDistribution empiricalDistribution =
          (org.hipparchus.stat.fitting.EmpiricalDistribution) ast
              .getProperty(PROPERTY.EMPIRICAL_DISTRIBUTION);
      if (empiricalDistribution == null) {
        int size = arg1.isVector();
        if (size > 0) {
          double[] sourceData = arg1.toDoubleVector();
          if (sourceData != null) {
            org.hipparchus.stat.fitting.EmpiricalDistribution dist =
                new org.hipparchus.stat.fitting.EmpiricalDistribution();
            dist.load(sourceData);
            ast.putProperty(PROPERTY.EMPIRICAL_DISTRIBUTION, dist);
          }
        }
      }
      // ast.builtinEvaled();
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public IExpr inverseCDF(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST1()) {
      }
      return F.NIL;
    }

    @Override
    public IExpr kurtosis(IAST dist, EvalEngine engine) {
      if (dist.isAST1()) {
      }
      return F.NIL;
    }

    @Override
    public IExpr mean(IAST dist) {
      if (dist.isAST1()) {
        org.hipparchus.stat.fitting.EmpiricalDistribution empiricalDistribution =
            (org.hipparchus.stat.fitting.EmpiricalDistribution) dist
                .getProperty(PROPERTY.EMPIRICAL_DISTRIBUTION);
        if (empiricalDistribution != null) {
          return F.num(empiricalDistribution.getNumericalMean());
        }
      }
      return F.NIL;
    }

    @Override
    public IExpr median(IAST dist) {
      if (dist.isAST1()) {
      }
      return F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST1()) {
        org.hipparchus.stat.fitting.EmpiricalDistribution empiricalDistribution =
            (org.hipparchus.stat.fitting.EmpiricalDistribution) dist
                .getProperty(PROPERTY.EMPIRICAL_DISTRIBUTION);
        if (empiricalDistribution != null) {
          // if (!engine.isArbitraryMode()) {
          double x = k.evalfNaN();
          if (!Double.isNaN(x)) {
            try {
              return F.num(empiricalDistribution.density(x));
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
              //
            }
          }
          // }
        }
      }
      return F.NIL;
    }

    @Override
    public IExpr randomVariate(Random random, IAST dist, int size) {
      if (dist.isAST1()) {
        org.hipparchus.stat.fitting.EmpiricalDistribution empiricalDistribution =
            (org.hipparchus.stat.fitting.EmpiricalDistribution) dist
                .getProperty(PROPERTY.EMPIRICAL_DISTRIBUTION);
        if (empiricalDistribution != null) {
          return F.num(empiricalDistribution.getNextValue());
        }
        // see exception handling in RandonmVariate() function
        // double rate = dist.arg1().evalf();
        // if (rate > 0.0) {
        // // return F.num(new ExponentialGenerator(rate, random).nextValue());
        // RandomDataGenerator rdg = new RandomDataGenerator();
        // double[] vector = rdg.nextDeviates(
        // new org.hipparchus.distribution.continuous.ExponentialDistribution(rate), size);
        // return new ASTRealVector(vector, false);
        // }
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

    @Override
    public IExpr skewness(IAST dist) {
      if (dist.isAST1()) {
      }
      return F.NIL;
    }

    @Override
    public IExpr variance(IAST dist) {
      if (dist.isAST1()) {
        org.hipparchus.stat.fitting.EmpiricalDistribution empiricalDistribution =
            (org.hipparchus.stat.fitting.EmpiricalDistribution) dist
                .getProperty(PROPERTY.EMPIRICAL_DISTRIBUTION);
        if (empiricalDistribution != null) {
          StreamingStatistics sampleStats =
              (StreamingStatistics) empiricalDistribution.getSampleStats();
          return F.num(sampleStats.getPopulationVariance());
        }
      }
      return F.NIL;
    }

  }


  /**
   *
   *
   * <pre>
   * ErlangDistribution({k, lambda})
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns a Erlang distribution.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Erlang_distribution">Wikipedia - Erlang
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
  private static final class ErlangDistribution extends AbstractEvaluator
      implements ICDF, IContinuousDistribution, IPDF, IStatistics, IGeneratingFunction {

    @Override
    public IExpr mgf(IAST dist, IExpr t, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        // (1 - t/m)^(-n)
        return F.Power(F.Subtract(F.C1, F.Divide(t, m)), F.Negate(n));
      }
      return F.NIL;
    }

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        IExpr function =
            // [$ (Piecewise({{GammaRegularized(n, 0, #*m), # > 0}}, 0)) & $]
            F.Function(F.Piecewise(F.list(
                F.list(F.GammaRegularized(n, F.C0, F.Times(F.Slot1, m)), F.Greater(F.Slot1, F.C0))),
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
      return ARGS_2_2;
    }

    @Override
    public IExpr inverseCDF(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        IExpr function =
            // [$ ( ConditionalExpression(Piecewise({{InverseGammaRegularized(n, 0, #)/m, 0 < # <
            // 1}, {0, #
            // <= 0}}, Infinity), 0 <= # <= 1)& ) $]
            F.Function(
                F.ConditionalExpression(
                    F.Piecewise(
                        F.list(
                            F.list(
                                F.Times(F.Power(m, F.CN1),
                                    F.InverseGammaRegularized(n, F.C0, F.Slot1)),
                                F.Less(F.C0, F.Slot1, F.C1)),
                            F.list(F.C0, F.LessEqual(F.Slot1, F.C0))),
                        F.oo),
                    F.LessEqual(F.C0, F.Slot1, F.C1))); // $$;
        return callFunction(function, k);
      }
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
    public IExpr median(IAST dist) {
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        // (n,m) => InverseGammaRegularized(n, 0, 1/2)/m
        return F.Times(F.Power(m, -1), F.InverseGammaRegularized(n, F.C0, F.C1D2));
      }
      return F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        //
        IExpr function =
            // [$ Piecewise({{(#^(-1 + n)*m^n)/(E^(#*m)*Gamma(n)), # > 0}}, 0) & $]
            F.Function(F.Piecewise(F.list(F.list(F.Times(F.Power(m, n),
                F.Power(F.Times(F.Exp(F.Times(F.Slot1, m)), F.Gamma(n)), F.CN1),
                F.Power(F.Slot1, F.Plus(F.CN1, n))), F.Greater(F.Slot1, F.C0))), F.C0)); // $$;
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

    @Override
    public IExpr skewness(IAST dist) {
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        // IExpr m = dist.arg2();
        // 2/Sqrt(n)
        return F.Divide(F.C2, F.Sqrt(n));
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
  }


  private static final class ExponentialDistribution extends AbstractEvaluator
      implements ICDF, IContinuousDistribution, IPDF, IStatistics, IRandomVariate, ICentralMoment,
      IGeneratingFunction {

    @Override
    public IExpr mgf(IAST dist, IExpr t, EvalEngine engine) {
      if (dist.isAST1()) {
        IExpr lambda = dist.arg1();
        // lambda/(lambda - t)
        return F.Divide(lambda, F.Subtract(lambda, t));
      }
      return F.NIL;
    }

    @Override
    public IExpr moment(IAST dist, IExpr n) {
      if (dist.isAST1() && n.isInteger() && !n.isNegative()) {
        // n!/a^n
        return F.Divide(F.Factorial(n), F.Power(dist.arg1(), n));
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
    public IExpr survivalFunction(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST1()) {
        IExpr n = dist.arg1();
        // Piecewise({{E^(-n*#), # >= 0}}, 1) &
        return callFunction(F.Function(F.Piecewise(
            F.list(F.list(F.Exp(F.Times(F.CN1, n, F.Slot1)), F.GreaterEqual(F.Slot1, F.C0))),
            F.C1)), k);
      }
      return F.NIL;
    }

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST1()) {
        IExpr n = dist.arg1();
        if (!engine.isArbitraryMode() && //
            (n.isNumericArgument(true) || k.isNumericArgument(true))) {
          double x = k.evalfNaN();
          double nDouble = n.evalfNaN();
          if (!Double.isNaN(x) && !Double.isNaN(nDouble)) {
            try {
              if (x <= 0.0) {
                return F.CD0;
              }
              return F.num(1.0 - Math.exp(-x * nDouble));

              // return F.num(new
              // org.hipparchus.distribution.continuous.ExponentialDistribution(n.evalDouble()) //
              // .cumulativeProbability(k.evalDouble()));
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
              //
            }
          }
        }
        IExpr function =
            // [$ (Piecewise({{1 - E^((-#)*n), # >= 0}}, 0)) & $]
            F.Function(
                F.Piecewise(F.list(F.list(F.Subtract(F.C1, F.Exp(F.Times(F.CN1, F.Slot1, n))),
                    F.GreaterEqual(F.Slot1, F.C0))), F.C0)); // $$;
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr centralMoment(IAST dist, IExpr m, EvalEngine engine) {
      if (dist.isAST1()) {
        IExpr n = dist.arg1();
        // Subfactorial(m)/n^m;
        return F.Times(F.Power(F.Power(n, m), F.CN1), F.Subfactorial(m));
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
        IExpr n = dist.arg1();
        if (!engine.isArbitraryMode() && //
            (n.isNumericArgument(true) || k.isNumericArgument(true))) {
          double x = k.evalfNaN();
          double nDouble = n.evalfNaN();
          if (!Double.isNaN(x) && !Double.isNaN(nDouble)) {
            try {
              if (F.isEqual(x, 1.0)) {
                return F.CInfinity;
              }
              return F.num(-Math.log(1.0 - x) / nDouble);
              // return F.num(new
              // org.hipparchus.distribution.continuous.ExponentialDistribution(n.evalDouble()) //
              // .inverseCumulativeProbability(k.evalDouble()));
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
              //
            }
          }
        }
        IExpr function =
            // [$ ( ConditionalExpression(Piecewise({{-(Log(1 - #)/n), # < 1}}, Infinity), 0 <= # <=
            // 1)& )
            // $]
            F.Function(F.ConditionalExpression(F.Piecewise(
                F.list(F.list(F.Times(F.CN1, F.Power(n, F.CN1), F.Log(F.Subtract(F.C1, F.Slot1))),
                    F.Less(F.Slot1, F.C1))),
                F.oo), F.LessEqual(F.C0, F.Slot1, F.C1))); // $$;
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr kurtosis(IAST dist, EvalEngine engine) {
      if (dist.isAST1()) {
        return F.C9;
      }
      return F.NIL;
    }

    @Override
    public IExpr mean(IAST dist) {
      if (dist.isAST1()) {
        // 1/x
        return F.Power(dist.arg1(), F.CN1);
      }
      return F.NIL;
    }

    @Override
    public IExpr median(IAST dist) {
      if (dist.isAST1()) {
        // Log(2)/x
        return F.Times(F.Log(F.C2), F.Power(dist.arg1(), F.CN1));
      }
      return F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST1()) {
        IExpr n = dist.arg1();
        //
        IExpr function =
            // [$ Piecewise({{n/E^(#*n), # >= 0}}, 0) & $]
            F.Function(
                F.Piecewise(F.list(F.list(F.Times(F.Power(F.Exp(F.Times(F.Slot1, n)), F.CN1), n),
                    F.GreaterEqual(F.Slot1, F.C0))), F.C0)); // $$;
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr randomVariate(Random random, IAST dist, int size) {
      if (dist.isAST1()) {
        // see exception handling in RandonmVariate() function
        double rate = dist.arg1().evalfNaN();
        if (rate > 0.0) {
          // return F.num(new ExponentialGenerator(rate, random).nextValue());
          RandomDataGenerator rdg = new RandomDataGenerator();
          double[] vector = rdg.nextDeviates(
              new org.hipparchus.distribution.continuous.ExponentialDistribution(rate), size);
          return new ASTRealVector(vector, false);
        }
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

    @Override
    public IExpr skewness(IAST dist) {
      if (dist.isAST1()) {
        // 2
        return F.C2;
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

  }


  private static final class FRatioDistribution extends AbstractEvaluator
      implements ICDF, IContinuousDistribution, IPDF, IStatistics {

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        if (!engine.isArbitraryMode() && //
            (n.isNumericArgument(true) || m.isNumericArgument(true) || k.isNumericArgument(true))) {
          double nDouble = n.evalfNaN();
          double mDouble = m.evalfNaN();
          double kDouble = k.evalfNaN();
          if (!Double.isNaN(nDouble) && !Double.isNaN(mDouble) && !Double.isNaN(kDouble)) {
            try {
              return F
                  .num(new org.hipparchus.distribution.continuous.FDistribution(nDouble, mDouble) //
                      .cumulativeProbability(kDouble));
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
              //
            }
          }
        }
        IExpr function =
            // [$ Piecewise({{BetaRegularized((#*n)/(m + #*n), n/2, m/2), # > 0}}, 0) & $]
            F.Function(F.Piecewise(F.list(F.list(F.BetaRegularized(
                F.Times(n, F.Power(F.Plus(m, F.Times(F.Slot1, n)), F.CN1), F.Slot1),
                F.Times(F.C1D2, n), F.Times(F.C1D2, m)), F.Greater(F.Slot1, F.C0))), F.C0)); // $$;
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public IExpr inverseCDF(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        IExpr function =
            // [$ ( ConditionalExpression(Piecewise({{(m*(-1 + 1/InverseBetaRegularized(1, -#, m/2,
            // n/2)))/n, 0 < # < 1}, {0, # <= 0}}, Infinity), 0 <= # <= 1)& ) $]
            F.Function(
                F.ConditionalExpression(
                    F.Piecewise(
                        F.list(
                            F.list(
                                F.Times(m, F.Power(n, F.CN1),
                                    F.Plus(F.CN1,
                                        F.Power(F.InverseBetaRegularized(F.C1, F.Negate(F.Slot1),
                                            F.Times(F.C1D2, m), F.Times(F.C1D2, n)), F.CN1))),
                                F.Less(F.C0, F.Slot1, F.C1)),
                            F.list(F.C0, F.LessEqual(F.Slot1, F.C0))),
                        F.oo),
                    F.LessEqual(F.C0, F.Slot1, F.C1))); // $$;
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr mean(IAST dist) {
      if (dist.isAST2()) {
        // IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        return
        // [$ Piecewise({{m/(-2 + m), m > 2}}, Indeterminate) $]
        F.Piecewise(
            F.list(F.list(F.Times(F.Power(F.Plus(F.CN2, m), F.CN1), m), F.Greater(m, F.C2))),
            F.Indeterminate); // $$;
      }
      return F.NIL;
    }

    @Override
    public IExpr median(IAST dist) {
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        // [$ (m*(-1 + 1/InverseBetaRegularized(1, -(1/2), m/2, n/2)))/n $]
        return F.Times(m, F.Power(n, F.CN1),
            F.Plus(F.CN1,
                F.Power(
                    F.InverseBetaRegularized(F.C1, F.CN1D2, F.Times(F.C1D2, m), F.Times(F.C1D2, n)),
                    F.CN1))); // $$;
      }
      return F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        if (!engine.isArbitraryMode() && //
            (n.isNumericArgument(true) || m.isNumericArgument(true) || k.isNumericArgument(true))) {
          double nDouble = n.evalfNaN();
          double mDouble = m.evalfNaN();
          double kDouble = k.evalfNaN();
          if (!Double.isNaN(nDouble) && !Double.isNaN(mDouble) && !Double.isNaN(kDouble)) {
            try {
              return F
                  .num(new org.hipparchus.distribution.continuous.FDistribution(nDouble, mDouble) //
                      .density(kDouble));
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
              //
            }
          }
        }
        IExpr function =
            // [$ Piecewise({{(#^(-1 + n/2)*m^(m/2)*n^(n/2)*(m + #*n)^((1/2)*(-m - n)))/Beta(n/2,
            // m/2), # >
            // 0}}, 0) & $]
            F.Function(F.Piecewise(F.list(F.list(
                F.Times(F.Power(m, F.Times(F.C1D2, m)), F.Power(n, F.Times(F.C1D2, n)),
                    F.Power(F.Plus(m, F.Times(F.Slot1, n)),
                        F.Times(F.C1D2, F.Subtract(F.Negate(m), n))),
                    F.Power(F.Beta(F.Times(F.C1D2, n), F.Times(F.C1D2, m)), F.CN1),
                    F.Power(F.Slot1, F.Plus(F.CN1, F.Times(F.C1D2, n)))),
                F.Greater(F.Slot1, F.C0))), F.C0)); // $$;
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr skewness(IAST dist) {
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        return
        // [$ Piecewise({{(2*Sqrt(2)*Sqrt(-4 + m)*(-2 + m + 2*n))/((-6 + m)*Sqrt(n)*Sqrt(-2 + m +
        // n)), m > 6}},
        // Indeterminate) $]
        F.Piecewise(
            F.list(F.list(
                F.Times(F.C2, F.CSqrt2, F.Sqrt(F.Plus(F.CN4, m)),
                    F.Plus(F.CN2, m, F.Times(F.C2, n)), F.Power(
                        F.Times(F.Plus(F.CN6, m), F.Sqrt(n), F.Sqrt(F.Plus(F.CN2, m, n))), F.CN1)),
                F.Greater(m, F.C6))),
            F.Indeterminate); // $$;
      }
      return F.NIL;
    }

    @Override
    public IExpr variance(IAST dist) {
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        return
        // [$ Piecewise({{(2*m^2*(-2 + m + n))/((-4 + m)*(-2 + m)^2*n), m > 4}}, Indeterminate) $]
        F.Piecewise(F.list(F.list(
            F.Times(F.C2, F.Sqr(m), F.Plus(F.CN2, m, n),
                F.Power(F.Times(F.Plus(F.CN4, m), F.Sqr(F.Plus(F.CN2, m)), n), F.CN1)),
            F.Greater(m, F.C4))), F.Indeterminate); // $$;
      }
      return F.NIL;
    }
  }


  /**
   *
   *
   * <pre>
   * FrechetDistribution(a, b)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns a Frechet distribution.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Fr%C3%A9chet_distribution">Wikipedia - Frechet
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
  private static final class FrechetDistribution extends AbstractEvaluator
      implements ICDF, IContinuousDistribution, IPDF, IStatistics, IRandomVariate {

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        IExpr function =
            // [$ (Piecewise({{E^(-(#/m)^(-n)), # > 0}}, 0)) & $]
            F.Function(F.Piecewise(F.list(
                F.list(F.Exp(F.Negate(F.Power(F.Times(F.Power(m, F.CN1), F.Slot1), F.Negate(n)))),
                    F.Greater(F.Slot1, F.C0))),
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
      return ARGS_2_2;
    }

    @Override
    public IExpr inverseCDF(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        IExpr function =
            // [$ ( ConditionalExpression(Piecewise({{m/(-Log[#])^n^(-1), 0 < # < 1}, {0, # <= 0}},
            // Infinity), 0 <= # <= 1)& ) $]
            F.Function(
                F.ConditionalExpression(
                    F.Piecewise(
                        F.list(
                            F.list(F.Times(m,
                                F.Power(F.Power(F.Negate(F.Log(F.Slot1)), F.Power(n, F.CN1)),
                                    F.CN1)),
                                F.Less(F.C0, F.Slot1, F.C1)),
                            F.list(F.C0, F.LessEqual(F.Slot1, F.C0))),
                        F.oo),
                    F.LessEqual(F.C0, F.Slot1, F.C1))); // $$;
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr mean(IAST dist) {
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        // Piecewise({{m*Gamma(1 - 1/n), 1 < n}}, Infinity)
        return F.Piecewise(
            F.list(
                F.list(F.Times(m, F.Gamma(F.Subtract(F.C1, F.Power(n, F.CN1)))), F.Less(F.C1, n))),
            F.CInfinity);
      }
      return F.NIL;
    }

    @Override
    public IExpr median(IAST dist) {
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        // (n,m) => m/Log(2)^n^(-1)
        return F.Times(m, F.Power(F.Log(F.C2), F.Negate(F.Power(n, -1))));
      }
      return F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        //
        IExpr function =
            // [$ Piecewise({{((#/m)^(-1 - n)*n)/(E^(#/m)^(-n)*m), # > 0}}, 0) & $]
            F.Function(
                F.Piecewise(
                    F.list(
                        F.list(
                            F.Times(
                                F.Power(F.Times(
                                    F.Exp(
                                        F.Power(F.Times(F.Power(m, F.CN1), F.Slot1), F.Negate(n))),
                                    m), F.CN1),
                                n,
                                F.Power(F.Times(F.Power(m, F.CN1), F.Slot1), F.Subtract(F.CN1, n))),
                            F.Greater(F.Slot1, F.C0))),
                    F.C0)); // $$;
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr randomVariate(Random random, IAST dist, int size) {
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        if (n.isReal() && m.isReal()) {
          double reference = random.nextDouble();
          double uniform =
              reference >= StatisticsFunctions.NEXTDOWNONE ? StatisticsFunctions.NEXTDOWNONE
                  : Math.nextUp(reference);
          uniform = -Math.log(uniform);
          return m.times(S.Power.funEval(F.num(uniform), n.reciprocal().negate()));
        }
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

    @Override
    public IExpr skewness(IAST dist) {
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        // IExpr m = dist.arg2();
        return
        // [$ Piecewise({{(Gamma(1 - 3/n) - 3*Gamma(1 - 2/n)*Gamma(1 - 1/n) +
        // 2*Gamma(1 - 1/n)^3)/(Gamma(1 - 2/n) - Gamma(1 - 1/n)^2)^(3/2), n > 3}},
        // Infinity) $]
        F.Piecewise(
            F.list(
                F.list(
                    F.Times(
                        F.Plus(
                            F.Gamma(F.Plus(F.C1,
                                F.Times(F.CN3, F.Power(n, F.CN1)))),
                            F.Times(
                                F.CN3, F.Gamma(F.Plus(F.C1,
                                    F.Times(F.CN2, F.Power(n, F.CN1)))),
                                F.Gamma(F.Subtract(F.C1, F.Power(n, F.CN1)))),
                            F.Times(F.C2,
                                F.Power(F.Gamma(F.Subtract(F.C1, F.Power(n, F.CN1))), F.C3))),
                        F.Power(
                            F.Subtract(F.Gamma(F.Plus(F.C1, F.Times(F.CN2, F.Power(n, F.CN1)))),
                                F.Sqr(F.Gamma(F.Subtract(F.C1, F.Power(n, F.CN1))))),
                            F.QQ(-3L, 2L))),
                    F.Greater(n, F.C3))),
            F.oo); // $$;
      }
      return F.NIL;
    }

    @Override
    public IExpr variance(IAST dist) {
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        // Piecewise({{m^2*(Gamma(1 - 2/n) - Gamma(1 - 1/n)^2), n > 2}}, Infinity)
        return F.Piecewise(F.list(F.list(
            F.Times(F.Sqr(m),
                F.Plus(F.Gamma(F.Plus(F.C1, F.Times(F.CN2, F.Power(n, -1)))),
                    F.Negate(F.Sqr(F.Gamma(F.Plus(F.C1, F.Negate(F.Power(n, -1)))))))),
            F.Greater(n, F.C2))), F.CInfinity);
      }
      return F.NIL;
    }
  }


  private static final class GammaDistribution extends AbstractEvaluator implements ICentralMoment,
      IContinuousDistribution, IRandomVariate, IStatistics, IPDF, ICDF, IGeneratingFunction {

    @Override
    public IExpr mgf(IAST dist, IExpr t, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        // (1 - b*t)^(-a)
        return F.Power(F.Subtract(F.C1, F.Times(b, t)), F.Negate(a));
      }
      return F.NIL;
    }

    @Override
    public IExpr moment(IAST dist, IExpr n) {
      if (dist.isAST2() && n.isInteger() && !n.isNegative()) {
        // b^n*Pochhammer(a, n)
        return F.Times(F.Power(dist.arg2(), n), F.Pochhammer(dist.arg1(), n));
      }
      return F.NIL;
    }

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2()) {
        //
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        if (!engine.isArbitraryMode() && //
            (a.isNumericArgument(true) || b.isNumericArgument(true) || k.isNumericArgument(true))) {
          double aDouble = a.evalfNaN();
          double bDouble = b.evalfNaN();
          double kDouble = k.evalfNaN();
          if (!Double.isNaN(aDouble) && !Double.isNaN(bDouble) && !Double.isNaN(kDouble)) {
            try {
              return F.num(
                  new org.hipparchus.distribution.continuous.GammaDistribution(aDouble, bDouble) //
                      .cumulativeProbability(kDouble));
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
              //
            }
          }
        }
        IExpr function =
            // [$ (Piecewise({{GammaRegularized(a, 0, #/b), # > 0}}, 0)&) $]
            F.Function(F.Piecewise(
                F.list(F.list(F.GammaRegularized(a, F.C0, F.Times(F.Power(b, F.CN1), F.Slot1)),
                    F.Greater(F.Slot1, F.C0))),
                F.C0)); // $$;
        return callFunction(function, k);
      } else if (dist.isAST(S.GammaDistribution, 5)) {
        //
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        IExpr g = dist.arg3();
        IExpr d = dist.arg4();
        IExpr function =
            // [$ (Piecewise({{GammaRegularized(a, 0, ((# - d)/b)^g), # > d}}, 0)&) $]
            F.Function(F.Piecewise(F.list(F.list(
                F.GammaRegularized(a, F.C0,
                    F.Power(F.Times(F.Power(b, F.CN1), F.Plus(F.Negate(d), F.Slot1)), g)),
                F.Greater(F.Slot1, d))), F.C0)); // $$;
        return callFunction(function, k);
      }

      return F.NIL;
    }

    @Override
    public IExpr centralMoment(IAST dist, IExpr n, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        IExpr aMinus = a.negate();
        IExpr nMinus = n.negate();
        // b^n*Hypergeometric1F1(-n, 1 - a - n, -a)*Pochhammer(a, n)
        return F.Together(F.Times(//
            F.Power(b, n), //
            F.Hypergeometric1F1(nMinus, F.Plus(F.C1, aMinus, nMinus), aMinus), //
            F.Pochhammer(a, n)));
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // 2 or 4 arguments
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_4;
    }

    @Override
    public IExpr inverseCDF(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2()) {
        //
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        IExpr function =
            // [$ ( ConditionalExpression(Piecewise({{b*InverseGammaRegularized(a, 0, #), 0 < # <
            // 1}, {0, #
            // <= 0}}, Infinity), 0 <= # <= 1)& ) $]
            F.Function(
                F.ConditionalExpression(
                    F.Piecewise(
                        F.list(
                            F.list(F.Times(b, F.InverseGammaRegularized(a, F.C0, F.Slot1)),
                                F.Less(F.C0, F.Slot1, F.C1)),
                            F.list(F.C0, F.LessEqual(F.Slot1, F.C0))),
                        F.oo),
                    F.LessEqual(F.C0, F.Slot1, F.C1))); // $$;
        return callFunction(function, k);
      } else if (dist.isAST(S.GammaDistribution, 5)) {
        //
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        IExpr g = dist.arg3();
        IExpr d = dist.arg4();
        IExpr function =
            // [$ ( ConditionalExpression(Piecewise({{d + b*InverseGammaRegularized(a, 0, #)^(1/g),
            // 0 < # <
            // 1}, {d, # <= 0}}, Infinity), 0 <= # <= 1)& ) $]
            F.Function(F.ConditionalExpression(
                F.Piecewise(F.list(
                    F.list(F.Plus(d,
                        F.Times(b,
                            F.Power(F.InverseGammaRegularized(a, F.C0, F.Slot1),
                                F.Power(g, F.CN1)))),
                        F.Less(F.C0, F.Slot1, F.C1)),
                    F.list(d, F.LessEqual(F.Slot1, F.C0))), F.oo),
                F.LessEqual(F.C0, F.Slot1, F.C1))); // $$;
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr kurtosis(IAST dist, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr a = dist.arg1();
        // 3+6/a
        return F.Plus(F.C3, F.Times(F.C6, F.Power(a, F.CN1)));
      } else if (dist.isAST(S.GammaDistribution, 5)) {
        // (-3*Gamma(a+1/g)^4+6*Gamma(a)*Gamma(a+1/g)^2*Gamma(a+2/g)-4*Gamma(a)^2*Gamma(a+1/g)*Gamma(a+3/g)+Gamma(a)^3*Gamma(a+4/g))/(Gamma(a+1/g)^2-Gamma(a)*Gamma(a+2/g))^2
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        IExpr g = dist.arg3();
        IExpr d = dist.arg4();
        IExpr v1 = F.Power(g, F.CN1);
        IExpr v2 = F.Gamma(a);
        IExpr v3 = F.Gamma(F.Plus(a, v1));
        IExpr v4 = F.Sqr(v3);
        IExpr v5 = F.Gamma(F.Plus(a, F.Times(F.C2, v1)));
        return F.Times(F.Power(F.Plus(v4, F.Times(F.CN1, v2, v5)), F.CN2),
            F.Plus(F.Times(F.CN3, F.Power(v3, F.C4)), F.Times(F.C6, v2, v4, v5),
                F.Times(F.CN4, F.Sqr(v2), v3, F.Gamma(F.Plus(a, F.Times(F.C3, v1)))),
                F.Times(F.Power(v2, F.C3), F.Gamma(F.Plus(a, F.Times(F.C4, v1))))));

      }
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
        F.Plus(d, F.Times(b, F.Power(F.Gamma(a), F.CN1), F.Gamma(F.Plus(a, F.Power(g, F.CN1))))); // $$;
      }
      return F.NIL;
    }

    @Override
    public IExpr median(IAST dist) {
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        // (n,m) => m*InverseGammaRegularized(n, 0, 1/2)
        return F.Times(m, F.InverseGammaRegularized(n, F.C0, F.C1D2));
      }
      if (dist.size() == 5) {
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        IExpr g = dist.arg3();
        IExpr d = dist.arg4();
        // (a,b,g,d) => d + b*InverseGammaRegularized(a, 1/2)^(1/g)
        return F.Plus(d, F.Times(b, F.Power(F.InverseGammaRegularized(a, F.C1D2), F.Power(g, -1))));
      }
      return F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2()) {
        //
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        if (!engine.isArbitraryMode() && //
            (a.isNumericArgument(true) || b.isNumericArgument(true) || k.isNumericArgument(true))) {
          double aDouble = a.evalfNaN();
          double bDouble = b.evalfNaN();
          double kDouble = k.evalfNaN();
          if (!Double.isNaN(aDouble) && !Double.isNaN(bDouble) && !Double.isNaN(kDouble)) {
            try {
              return F.num(
                  new org.hipparchus.distribution.continuous.GammaDistribution(aDouble, bDouble) //
                      .density(kDouble));
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
              //
            }
          }
        }
        IExpr function =
            // [$ ( Piecewise({{#^(-1 + a)/(b^a*E^(#/b)*Gamma(a)), # > 0}}, 0) & ) $]
            F.Function(
                F.Piecewise(
                    F.list(
                        F.list(
                            F.Times(
                                F.Power(
                                    F.Times(F.Power(b, a),
                                        F.Exp(F.Times(F.Power(b, F.CN1), F.Slot1)), F.Gamma(a)),
                                    F.CN1),
                                F.Power(F.Slot1, F.Plus(F.CN1, a))),
                            F.Greater(F.Slot1, F.C0))),
                    F.C0)); // $$;
        return callFunction(function, k);
      } else if (dist.isAST(S.GammaDistribution, 5)) {
        //
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        IExpr g = dist.arg3();
        IExpr d = dist.arg4();
        IExpr function =
            // [$ ( Piecewise( {{(((# - d)/b)^(-1 + a*g)*g)/(E^((# - d)/b)^g*(b*Gamma(a))), # > d}},
            // 0) & )
            // $]
            F.Function(F.Piecewise(F.list(F.list(F.Times(g,
                F.Power(F.Times(
                    F.Exp(F.Power(F.Times(F.Power(b, F.CN1), F.Plus(F.Negate(d), F.Slot1)), g)), b,
                    F.Gamma(a)), F.CN1),
                F.Power(F.Times(F.Power(b, F.CN1), F.Plus(F.Negate(d), F.Slot1)),
                    F.Plus(F.CN1, F.Times(a, g)))),
                F.Greater(F.Slot1, d))), F.C0)); // $$;
        return callFunction(function, k);
      }

      return F.NIL;
    }

    @Override
    public IExpr randomVariate(Random random, IAST dist, int size) {
      if (dist.isAST2()) {
        // see exception handling in RandonmVariate() function
        double a = dist.arg1().evalfNaN();
        double b = dist.arg2().evalfNaN();
        if (Double.isNaN(a) || Double.isNaN(b)) {
          return F.NIL;
        }

        // TODO cache RandomDataGenerator instance
        RandomDataGenerator rdg = new RandomDataGenerator();
        double[] vector = rdg.nextDeviates( //
            new org.hipparchus.distribution.continuous.GammaDistribution(a, b), //
            size);
        return new ASTRealVector(vector, false);
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

    @Override
    public IExpr skewness(IAST dist) {
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        return F.Divide(F.C2, F.Sqrt(n));
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
  }


  private static final class GompertzMakehamDistribution extends AbstractEvaluator
      implements ICDF, IContinuousDistribution, IPDF, IStatistics, IRandomVariate {

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr m = dist.arg1();
        IExpr n = dist.arg2();
        IExpr function =
            // [$ Piecewise({{1 - E^((1 - E^(#*m))*n), # >= 0}}, 0) & $]
            F.Function(F.Piecewise(F.list(F.list(
                F.Subtract(F.C1, F.Exp(F.Times(F.Subtract(F.C1, F.Exp(F.Times(F.Slot1, m))), n))),
                F.GreaterEqual(F.Slot1, F.C0))), F.C0)); // $$;
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // 0 or 2 args
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public IExpr inverseCDF(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr m = dist.arg1();
        IExpr n = dist.arg2();
        IExpr function =
            // [$ ConditionalExpression( Piecewise({{Log(1 - Log(1 - #)/n)/m, 0 < # < 1}, {0, # <=
            // 0}},
            // Infinity), 0 <= # <= 1) & $]
            F.Function(
                F.ConditionalExpression(
                    F.Piecewise(
                        F.list(
                            F.list(
                                F.Times(F.Power(m, F.CN1),
                                    F.Log(F.Plus(F.C1,
                                        F.Times(F.CN1, F.Power(n, F.CN1),
                                            F.Log(F.Subtract(F.C1, F.Slot1)))))),
                                F.Less(F.C0, F.Slot1, F.C1)),
                            F.list(F.C0, F.LessEqual(F.Slot1, F.C0))),
                        F.oo),
                    F.LessEqual(F.C0, F.Slot1, F.C1))); // $$;
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr mean(IAST dist) {
      if (dist.isAST2()) {
        IExpr m = dist.arg1();
        IExpr n = dist.arg2();
        return
        // [$ (E^n*Gamma(0, n))/m $]
        F.Times(F.Exp(n), F.Power(m, F.CN1), F.Gamma(F.C0, n)); // $$;
      }
      return F.NIL;
    }

    @Override
    public IExpr median(IAST dist) {
      if (dist.isAST2()) {
        IExpr m = dist.arg1();
        IExpr n = dist.arg2();
        return
        // [$ Log(1 + Log(2)/n)/m $]
        F.Times(F.Power(m, F.CN1), F.Log(F.Plus(F.C1, F.Times(F.Power(n, F.CN1), F.Log(F.C2))))); // $$;
      }
      return F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr m = dist.arg1();
        IExpr n = dist.arg2();
        IExpr function =
            // [$ Piecewise({{E^(#*m + (1 - E^(#*m))*n)*m*n, # >= 0}}, 0) & $]
            F.Function(F.Piecewise(F.list(F.list(
                F.Times(F.Exp(F.Plus(F.Times(F.Slot1, m),
                    F.Times(F.Subtract(F.C1, F.Exp(F.Times(F.Slot1, m))), n))), m, n),
                F.GreaterEqual(F.Slot1, F.C0))), F.C0)); // $$;
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr randomVariate(Random random, IAST dist, int size) {
      if (dist.isAST2()) {
        // see exception handling in RandonmVariate() function
        double lambda = dist.arg1().evalfNaN();
        double xi = dist.arg2().evalfNaN();
        if (Double.isNaN(lambda) || Double.isNaN(xi)) {
          return F.NIL;
        }
        // inverse transform sampling with the quantile function
        // Log(1 - Log(1 - #)/xi)/lambda, where 1 - # is uniform on (0, 1] as well
        double[] vector = new double[size];
        for (int i = 0; i < size; i++) {
          double reference = random.nextDouble();
          double uniform = Math.nextUp(reference);
          vector[i] = Math.log((xi - Math.log(uniform)) / xi) / lambda;
        }
        return new ASTRealVector(vector, false);
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

    @Override
    public IExpr skewness(IAST dist) {
      if (dist.isAST2()) {
        // The rate parameter dist.arg1() is a pure scale parameter, so it cancels in the
        // skewness and only the shape parameter n is left.
        //
        // With W exponentially distributed the quantile function gives X == Log(1 + W/n)/m, so the
        // raw moments are Moment(k) == E^n*d(k)/m^k where
        // d(1) == Gamma(0,n)
        // d(2) == g^2 + Pi^2/6 - 2*n*HypergeometricPFQ({1,1,1},{2,2,2},-n)
        // d(3) == -g^3 - (Pi^2*g)/2 - 2*Zeta(3) + 6*n*HypergeometricPFQ({1,1,1,1},{2,2,2,2},-n)
        // for g == EulerGamma + Log(n). d(2) is the form already used in variance() below.
        IExpr n = dist.arg2();
        IExpr g = F.Plus(F.EulerGamma, F.Log(n));
        IExpr d1 = F.Gamma(F.C0, n);
        IExpr d2 = F.Plus(F.Sqr(g), F.Times(F.QQ(1L, 6L), F.Sqr(F.Pi)),
            F.Times(F.CN2, n, F.HypergeometricPFQ(F.list(F.C1, F.C1, F.C1),
                F.list(F.C2, F.C2, F.C2), F.Negate(n))));
        IExpr d3 = F.Plus(F.Negate(F.Power(g, F.C3)), F.Times(F.CN1D2, F.Sqr(F.Pi), g),
            F.Times(F.CN2, F.Zeta(F.C3)),
            F.Times(F.C6, n, F.HypergeometricPFQ(F.List(F.C1, F.C1, F.C1, F.C1),
                F.List(F.C2, F.C2, F.C2, F.C2), F.Negate(n))));
        IExpr e1 = F.Exp(n);
        IExpr e2 = F.Exp(F.Times(F.C2, n));
        IExpr e3 = F.Exp(F.Times(F.C3, n));
        // CentralMoment(3)/Variance^(3/2), with the m^(-3) of both parts cancelled
        return F.Divide(
            F.Plus(F.Times(e1, d3), F.Times(F.CN3, e2, d1, d2),
                F.Times(F.C2, e3, F.Power(d1, F.C3))),
            F.Power(F.Subtract(F.Times(e1, d2), F.Times(e2, F.Sqr(d1))), F.QQ(3L, 2L)));
      }
      return F.NIL;
    }

    @Override
    public IExpr variance(IAST dist) {
      if (dist.isAST2()) {
        IExpr m = dist.arg1();
        IExpr n = dist.arg2();
        return
        // [$ -((E^n*(-6*EulerGamma^2 - Pi^2 + 6*E^n*ExpIntegralEi(-n)^2 +
        // 12*n*HypergeometricPFQ({1, 1, 1}, {2,
        // 2, 2}, -n) - 12*EulerGamma*Log(n) - 6*Log(n)^2))/(6*m^2)) $]
        F.Times(F.CN1, F.Exp(n), F.Power(F.Times(F.C6, F.Sqr(m)), F.CN1),
            F.Plus(F.Times(F.CN6, F.Sqr(F.EulerGamma)), F.Negate(F.Sqr(F.Pi)),
                F.Times(F.C6, F.Exp(n), F.Sqr(F.ExpIntegralEi(F.Negate(n)))),
                F.Times(F.ZZ(12L), n,
                    F.HypergeometricPFQ(F.list(F.C1, F.C1, F.C1), F.list(F.C2, F.C2, F.C2),
                        F.Negate(n))),
                F.Times(F.ZZ(-12L), F.EulerGamma, F.Log(n)), F.Times(F.CN6, F.Sqr(F.Log(n))))); // $$;
      }
      return F.NIL;
    }
  }


  /**
   *
   *
   * <pre>
   * <code>GumbelDistribution(a, b)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns a Gumbel distribution.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Gumbel_distribution">Wikipedia - Gumbel
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
  private static final class GumbelDistribution extends AbstractEvaluator implements ICDF,
      IContinuousDistribution, IPDF, IStatistics, IRandomVariate, IGeneratingFunction {

    @Override
    public IExpr mgf(IAST dist, IExpr t, EvalEngine engine) {
      // minimum convention: Mean(GumbelDistribution(n,m)) == n - EulerGamma*m
      if (dist.isAST0()) {
        // Gamma(1 + t)
        return F.Gamma(F.Plus(F.C1, t));
      }
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        // E^(n*t)*Gamma(1 + m*t)
        return F.Times(F.Exp(F.Times(n, t)), F.Gamma(F.Plus(F.C1, F.Times(m, t))));
      }
      return F.NIL;
    }

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST0()) {
        if (!engine.isArbitraryMode() && k.isNumericArgument(true)) {
          double kDouble = k.evalfNaN();
          if (!Double.isNaN(kDouble)) {
            try {
              return F.num(1.0 - Math.exp(-Math.exp(kDouble)));
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
            }
          }
        }
        IExpr function =
            // [$ (1 - E^(-E^#)) & $]
            F.Function(F.Subtract(F.C1, F.Exp(F.Negate(F.Exp(F.Slot1))))); // $$;
        return callFunction(function, k);
      } else if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        if (!engine.isArbitraryMode() && //
            (n.isNumericArgument(true) || m.isNumericArgument(true) || k.isNumericArgument(true))) {
          double kDouble = k.evalfNaN();
          double nDouble = n.evalfNaN();
          double mDouble = m.evalfNaN();
          if (!Double.isNaN(kDouble) && !Double.isNaN(nDouble) && !Double.isNaN(mDouble)) {
            try {
              final double z = (kDouble - nDouble) / mDouble;
              return F.num(1.0 - Math.exp(-Math.exp(z)));
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
              //
            }
          }
        }
        IExpr function =
            // [$ (1 - E^(-E^((# - n)/m))) & $]
            F.Function(F.Subtract(F.C1,
                F.Exp(F.Negate(F.Exp(F.Times(F.Power(m, F.CN1), F.Plus(F.Negate(n), F.Slot1))))))); // $$;
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // 0 or 2 args
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_2;
    }

    @Override
    public IExpr inverseCDF(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST0()) {
        if (!engine.isArbitraryMode() && k.isNumericArgument(true)) {
          double p = k.evalfNaN();
          if (!Double.isNaN(p)) {
            try {
              MathUtils.checkRangeInclusive(p, 0, 1);
              if (F.isZero(p)) {
                return F.CNInfinity;
              } else if (F.isEqual(p, 1.0)) {
                return F.CInfinity;
              }
              return F.num(Math.log(-Math.log(1.0 - p)));
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
            }
          }
        }
        IExpr function =
            // [$ ( ConditionalExpression(Piecewise({{Log(-Log(1 - #)), 0 < # < 1}, {-Infinity, # <=
            // 0}}, Infinity), 0 <= # <= 1)& ) $]
            F.Function(F.ConditionalExpression(
                F.Piecewise(F.list(
                    F.list(F.Log(F.Negate(F.Log(F.Subtract(F.C1, F.Slot1)))),
                        F.Less(F.C0, F.Slot1, F.C1)),
                    F.list(F.Negate(F.oo), F.LessEqual(F.Slot1, F.C0))), F.oo),
                F.LessEqual(F.C0, F.Slot1, F.C1))); // $$;
        return callFunction(function, k);
      } else if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        if (!engine.isArbitraryMode() && //
            (n.isNumericArgument(true) || m.isNumericArgument(true) || k.isNumericArgument(true))) {
          double p = k.evalfNaN();
          double nDouble = n.evalfNaN();
          double mDouble = m.evalfNaN();
          if (!Double.isNaN(p) && !Double.isNaN(nDouble) && !Double.isNaN(mDouble)) {
            try {
              MathUtils.checkRangeInclusive(p, 0, 1);
              if (F.isZero(p)) {
                return F.CNInfinity;
              } else if (F.isEqual(p, 1.0)) {
                return F.CInfinity;
              }
              return F.num(nDouble + mDouble * Math.log(-Math.log(1.0 - p)));
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
              //
            }
          }
        }
        IExpr function =
            // [$ ( ConditionalExpression(Piecewise({{n + m*Log(-Log(1 - #)), 0 < # < 1},
            // {-Infinity, # <= 0}}, Infinity), 0 <= # <= 1)& ) $]
            F.Function(
                F.ConditionalExpression(
                    F.Piecewise(F.list(
                        F.list(
                            F.Plus(n,
                                F.Times(m, F.Log(F.Negate(F.Log(F.Subtract(F.C1, F.Slot1)))))),
                            F.Less(F.C0, F.Slot1, F.C1)),
                        F.list(F.Negate(F.oo), F.LessEqual(F.Slot1, F.C0))), F.oo),
                    F.LessEqual(F.C0, F.Slot1, F.C1))); // $$;
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr mean(IAST dist) {
      if (dist.isAST0()) {
        // -EulerGamma
        return S.EulerGamma.negate();
      }
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        // -EulerGamma*m + n
        return F.Plus(F.Times(F.CN1, S.EulerGamma, m), n);
      }
      return F.NIL;
    }

    @Override
    public IExpr median(IAST dist) {
      if (dist.isAST0()) {
        // ( ) => Log(Log(2))
        return F.Log(F.Log(F.C2));
      }
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        // (n, m) => n + m*Log(Log(2))
        return F.Plus(n, F.Times(m, F.Log(F.Log(F.C2))));
      }
      return F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST0()) {
        if (!engine.isArbitraryMode() && k.isNumericArgument(true)) {
          double z = k.evalfNaN();
          if (!Double.isNaN(z)) {
            try {
              return F.num(Math.exp(-Math.exp(z) + z));
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
            }
          }
        }
        IExpr function =
            // [$ (E^(-E^# + #)) & $]
            F.Function(F.Exp(F.Plus(F.Negate(F.Exp(F.Slot1)), F.Slot1))); // $$;
        return callFunction(function, k);
      } else if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        IExpr function =
            // [$ (E^(-E^((# - n)/m) + (# - n)/m)/m) & $]
            F.Function(
                F.Times(
                    F.Exp(F.Plus(
                        F.Negate(F.Exp(F.Times(F.Power(m, F.CN1), F.Plus(F.Negate(n), F.Slot1)))),
                        F.Times(F.Power(m, F.CN1), F.Plus(F.Negate(n), F.Slot1)))),
                    F.Power(m, F.CN1))); // $$;
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr randomVariate(Random random, IAST dist, int size) {
      if (dist.isAST0()) {
        RandomDataGenerator rdg = new RandomDataGenerator();
        double[] vector = rdg.nextDeviates(
            new org.hipparchus.distribution.continuous.GumbelDistribution(0.0, 1.0), size);
        return new ASTRealVector(vector, false);
      } else if (dist.isAST2()) {
        // see exception handling in RandonmVariate() function
        double n = dist.arg1().evalfNaN();
        double m = dist.arg2().evalfNaN();
        if (Double.isNaN(n) || Double.isNaN(m)) {
          return F.NIL;
        }
        RandomDataGenerator rdg = new RandomDataGenerator();
        double[] vector = rdg.nextDeviates(
            new org.hipparchus.distribution.continuous.GumbelDistribution(n, m), size);
        return new ASTRealVector(vector, false);
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

    @Override
    public IExpr skewness(IAST dist) {
      if (dist.isAST0() || dist.isAST2()) {
        return
        // [$ -((12*Sqrt(6)*Zeta(3))/Pi^3) $]
        F.Times(F.CN1, F.ZZ(12L), F.CSqrt6, F.Power(F.Pi, F.CN3), F.Zeta(F.C3)); // $$;
      }
      return F.NIL;
    }

    @Override
    public IExpr variance(IAST dist) {
      if (dist.isAST0()) {
        // (Pi^2)/6
        return F.Times(F.QQ(1, 6), F.Sqr(S.Pi));
      } else if (dist.isAST2()) {
        IExpr m = dist.arg2();
        // (m^2*Pi^2)/6
        return F.Times(F.QQ(1, 6), F.Sqr(m), F.Sqr(S.Pi));
      }
      return F.NIL;
    }
  }


  private static class Initializer {

    private static void init() {
      S.BetaDistribution.setEvaluator(new BetaDistribution());
      S.BinormalDistribution.setEvaluator(new BinormalDistribution());
      S.CauchyDistribution.setEvaluator(new CauchyDistribution());
      S.ChiSquareDistribution.setEvaluator(new ChiSquareDistribution());

      S.ErlangDistribution.setEvaluator(new ErlangDistribution());
      S.ExponentialDistribution.setEvaluator(new ExponentialDistribution());
      S.FRatioDistribution.setEvaluator(new FRatioDistribution());
      S.FrechetDistribution.setEvaluator(new FrechetDistribution());
      S.GammaDistribution.setEvaluator(new GammaDistribution());
      S.GompertzMakehamDistribution.setEvaluator(new GompertzMakehamDistribution());
      S.GumbelDistribution.setEvaluator(new GumbelDistribution());
      // S.InverseGammaDistribution.setEvaluator(new InverseGammaDistribution());
      S.LogNormalDistribution.setEvaluator(new LogNormalDistribution());
      S.MultinormalDistribution.setEvaluator(new MultinormalDistribution());
      S.NakagamiDistribution.setEvaluator(new NakagamiDistribution());
      S.NormalDistribution.setEvaluator(new NormalDistribution());
      S.ParetoDistribution.setEvaluator(new ParetoDistribution());
      S.StudentTDistribution.setEvaluator(new StudentTDistribution());
      S.UniformDistribution.setEvaluator(new UniformDistribution());
      S.VonMisesDistribution.setEvaluator(new VonMisesDistribution());
      S.WeibullDistribution.setEvaluator(new WeibullDistribution());
      S.LaplaceDistribution.setEvaluator(new LaplaceDistribution());
      S.LogisticDistribution.setEvaluator(new LogisticDistribution());
      S.LogLogisticDistribution.setEvaluator(new LogLogisticDistribution());
      S.MaxwellDistribution.setEvaluator(new MaxwellDistribution());
      S.MoyalDistribution.setEvaluator(new MoyalDistribution());
      S.SechDistribution.setEvaluator(new SechDistribution());
      S.TriangularDistribution.setEvaluator(new TriangularDistribution());
      S.WignerSemicircleDistribution.setEvaluator(new WignerSemicircleDistribution());
      S.BenktanderGibratDistribution.setEvaluator(new BenktanderGibratDistribution());
      S.BenktanderWeibullDistribution.setEvaluator(new BenktanderWeibullDistribution());
      S.BetaPrimeDistribution.setEvaluator(new BetaPrimeDistribution());
      S.MeixnerDistribution.setEvaluator(new MeixnerDistribution());
      S.SinghMaddalaDistribution.setEvaluator(new SinghMaddalaDistribution());
      S.SuzukiDistribution.setEvaluator(new SuzukiDistribution());
      S.ExponentialPowerDistribution.setEvaluator(new ExponentialPowerDistribution());
      S.MaxStableDistribution.setEvaluator(new MaxStableDistribution());
      S.MinStableDistribution.setEvaluator(new MinStableDistribution());
      S.NoncentralChiSquareDistribution.setEvaluator(new NoncentralChiSquareDistribution());
      S.RiceDistribution.setEvaluator(new RiceDistribution());
      S.UniformSumDistribution.setEvaluator(new UniformSumDistribution());
      S.HypoexponentialDistribution.setEvaluator(new HypoexponentialDistribution());
    }
  }

  // private static final class InverseGammaDistribution extends AbstractEvaluator
  // implements IContinuousDistribution, IRandomVariate, IStatistics, IPDF, ICDF {
  //
  // @Override
  // public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
  // if (dist.isAST2()) {
  // //
  // IExpr a = dist.arg1();
  // IExpr b = dist.arg2();
  // if (!engine.isArbitraryMode() && //
  // (a.isNumericArgument(true) || b.isNumericArgument(true) || k.isNumericArgument(true))) {
  // try {
  // return F.num(new org.hipparchus.distribution.continuous.InvGammaDistribution(a.evalf(),
  // b.evalf()) //
  // .cumulativeProbability(k.evalf()));
  // } catch (RuntimeException rex) {
  // Errors.rethrowsInterruptException(rex);
  // //
  // }
  // }
  // IExpr function = //
  // F.Function(F.Piecewise(
  // F.list(F.list(F.GammaRegularized(a, F.Times(b, F.Power(F.Slot1, F.CN1)))),
  // F.Greater(F.Slot1, F.C0))),
  // F.C0);
  //
  //
  // return callFunction(function, k);
  // } else if (dist.isAST(S.InverseGammaDistribution, 5)) {
  // //
  // IExpr a = dist.arg1();
  // IExpr b = dist.arg2();
  // IExpr g = dist.arg3();
  // IExpr d = dist.arg4();
  // IExpr function = //
  // F.Function(F.Piecewise(F.list(F.list(
  // F.GammaRegularized(a,
  // F.Power(F.Times(b, F.Power(F.Plus(F.Negate(d), F.Slot1), F.CN1)), g)),
  // F.Greater(F.Slot1, d))), F.C0));
  // return callFunction(function, k);
  // }
  //
  // return F.NIL;
  // }
  //
  //
  // @Override
  // public IExpr evaluate(final IAST ast, EvalEngine engine) {
  // // 2 or 4 arguments
  // return F.NIL;
  // }
  //
  // @Override
  // public int[] expectedArgSize(IAST ast) {
  // return ARGS_2_4;
  // }
  //
  // @Override
  // public IExpr inverseCDF(IAST dist, IExpr k, EvalEngine engine) {
  // if (dist.isAST2()) {
  // //
  // IExpr a = dist.arg1();
  // IExpr b = dist.arg2();
  // if (!engine.isArbitraryMode() && //
  // (a.isNumericArgument(true) || b.isNumericArgument(true) || k.isNumericArgument(true))) {
  // try {
  // return F.num(new org.hipparchus.distribution.continuous.InvGammaDistribution(a.evalf(),
  // b.evalf()) //
  // .inverseCumulativeProbability(k.evalf()));
  // } catch (RuntimeException rex) {
  // Errors.rethrowsInterruptException(rex);
  // //
  // }
  // }
  // IExpr function = //
  // F.Function(
  // F.ConditionalExpression(F.Piecewise(
  // F.list(F.list(F.Times(b, F.Power(F.InverseGammaRegularized(a, F.Slot1), F.CN1)),
  // F.Less(F.C0, F.Slot1, F.C1)), F.list(F.C0, F.LessEqual(F.Slot1, F.C0))),
  // F.oo), F.LessEqual(F.C0, F.Slot1, F.C1)));
  // return callFunction(function, k);
  // } else if (dist.isAST(S.GammaDistribution, 5)) {
  // //
  // IExpr a = dist.arg1();
  // IExpr b = dist.arg2();
  // IExpr g = dist.arg3();
  // IExpr d = dist.arg4();
  // IExpr function = //
  // F.Function(
  // F.ConditionalExpression(
  // F.Piecewise(
  // F.list(
  // F.list(
  // F.Plus(d,
  // F.Times(b,
  // F.Power(F.Power(F.InverseGammaRegularized(a, F.Slot1),
  // F.Power(g, F.CN1)), F.CN1))),
  // F.Less(F.C0, F.Slot1, F.C1)),
  // F.list(d, F.LessEqual(F.Slot1, F.C0))),
  // F.oo),
  // F.LessEqual(F.C0, F.Slot1, F.C1)));
  // return callFunction(function, k);
  // }
  // return F.NIL;
  // }
  //
  // @Override
  // public IExpr mean(IAST dist) {
  // if (dist.isAST2()) {
  // IExpr a = dist.arg1();
  // IExpr b = dist.arg2();
  // // Piecewise({{b/(-1+a),a>1}},Indeterminate)
  // return F.Piecewise(
  // F.list(F.list(F.Times(F.Power(F.Plus(F.CN1, a), F.CN1), b), F.Greater(a, F.C1))),
  // F.Indeterminate);
  // }
  // if (dist.size() == 5) {
  // IExpr a = dist.arg1();
  // IExpr b = dist.arg2();
  // IExpr g = dist.arg3();
  // IExpr d = dist.arg4();
  // // Piecewise({{d+(b*Gamma(a-1/g))/Gamma(a),a*g>1}},Indeterminate)
  // return F
  // .Piecewise(F.list(F.list(
  // F.Plus(d,
  // F.Times(b, F.Power(F.Gamma(a), F.CN1),
  // F.Gamma(F.Subtract(a, F.Power(g, F.CN1))))),
  // F.Greater(F.Times(a, g), F.C1))), S.Indeterminate);
  // }
  // return F.NIL;
  // }
  //
  // @Override
  // public IExpr median(IAST dist) {
  // if (dist.isAST2()) {
  // IExpr a = dist.arg1();
  // IExpr b = dist.arg2();
  // // b/InverseGammaRegularized(a,1/2)
  // return F.Times(b, F.Power(F.InverseGammaRegularized(a, F.C1D2), F.CN1));
  // }
  // if (dist.size() == 5) {
  // IExpr a = dist.arg1();
  // IExpr b = dist.arg2();
  // IExpr g = dist.arg3();
  // IExpr d = dist.arg4();
  // // d+b*((1/InverseGammaRegularized(a,1/2)))^(1/g)
  // return F.Plus(d, F.Times(b,
  // F.Power(F.Power(F.InverseGammaRegularized(a, F.C1D2), F.CN1), F.Power(g, F.CN1))));
  // }
  // return F.NIL;
  // }
  //
  // @Override
  // public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
  // if (dist.isAST2()) {
  // //
  // IExpr a = dist.arg1();
  // IExpr b = dist.arg2();
  // if (!engine.isArbitraryMode() && //
  // (a.isNumericArgument(true) || b.isNumericArgument(true) || k.isNumericArgument(true))) {
  // try {
  // return F.num(new org.hipparchus.distribution.continuous.InvGammaDistribution(a.evalf(),
  // b.evalf()) //
  // .density(k.evalf()));
  // } catch (RuntimeException rex) {
  // Errors.rethrowsInterruptException(rex);
  // //
  // }
  // }
  // IExpr function = //
  // // Piecewise({{(b/#)^a/(E^(b/#)*#*Gamma(a)),#>0}},0)&
  // F.Function(
  // F.Piecewise(
  // F.list(F.list(
  // F.Times(
  // F.Power(F.Times(F.Exp(F.Times(b, F.Power(F.Slot1, F.CN1))), F.Slot1,
  // F.Gamma(a)), F.CN1),
  // F.Power(F.Times(b, F.Power(F.Slot1, F.CN1)), a)),
  // F.Greater(F.Slot1, F.C0))),
  // F.C0));
  // return callFunction(function, k);
  // } else if (dist.isAST(S.InverseGammaDistribution, 5)) {
  // //
  // IExpr a = dist.arg1();
  // IExpr b = dist.arg2();
  // IExpr g = dist.arg3();
  // IExpr d = dist.arg4();
  // IExpr function = //
  // // Piecewise({{(g*(b/(-d+#1))^(1+a*g))/(E^(b/(-d+#1))^g*b*Gamma(a)),#1>d}},0)&
  // F.Function(F.Piecewise(F.list(F.list(F.Times(g,
  // F.Power(F.Times(
  // F.Exp(F.Power(F.Times(b, F.Power(F.Plus(F.Negate(d), F.Slot1), F.CN1)), g)), b,
  // F.Gamma(a)), F.CN1),
  // F.Power(F.Times(b, F.Power(F.Plus(F.Negate(d), F.Slot1), F.CN1)),
  // F.Plus(F.C1, F.Times(a, g)))),
  // F.Greater(F.Slot1, d))), F.C0));
  // return callFunction(function, k);
  // }
  //
  // return F.NIL;
  // }
  //
  // @Override
  // public IExpr randomVariate(Random random, IAST dist, int size) {
  // if (dist.isAST2()) {
  // // see exception handling in RandomVariate() function
  // double a = dist.arg1().evalf();
  // double b = dist.arg2().evalf();
  //
  // // TODO cache RandomDataGenerator instance
  // RandomDataGenerator rdg = new RandomDataGenerator();
  // double[] vector = rdg.nextDeviates( //
  // new org.hipparchus.distribution.continuous.InvGammaDistribution(a, b), //
  // size);
  // return new ASTRealVector(vector, false);
  // }
  // return F.NIL;
  // }
  //
  // @Override
  // public void setUp(final ISymbol newSymbol) {}
  //
  // @Override
  // public IExpr skewness(IAST dist) {
  // if (dist.isAST2()) {
  // IExpr a = dist.arg1();
  // // Piecewise({{(4*Sqrt(-2+a))/(-3+a),a>3}},Indeterminate)
  // return F.Piecewise(
  // F.list(F.list(F.Times(F.C4, F.Power(F.Plus(F.CN3, a), F.CN1), F.Sqrt(F.Plus(F.CN2, a))),
  // F.Greater(a, F.C3))),
  // F.Indeterminate);
  // } else if (dist.isAST(S.InverseGammaDistribution, 5)) {
  // //
  // IExpr a = dist.arg1();
  // IExpr g = dist.arg3();
  // // Piecewise({{(Gamma(a)^2*Gamma(a-3/g)-3*Gamma(a)*Gamma(a-2/g)*Gamma(a-1/g)+2*Gamma(a-
  // // 1/g)^3)/(Gamma(a)*Gamma(a-2/g)-Gamma(a-1/g)^2)^(3/2),a*g>3}},Indeterminate)
  // return F
  // .Piecewise(
  // F.list(
  // F.list(
  // F.Times(
  // F.Power(
  // F.Subtract(
  // F.Times(F.Gamma(a),
  // F.Gamma(F.Plus(a, F.Times(F.CN2, F.Power(g, F.CN1))))),
  // F.Sqr(F.Gamma(F.Subtract(a, F.Power(g, F.CN1))))),
  // F.QQ(-3L, 2L)),
  // F.Plus(
  // F.Times(F.Sqr(F.Gamma(a)),
  // F.Gamma(F.Plus(a, F.Times(F.CN3, F.Power(g, F.CN1))))),
  // F.Times(
  // F.CN3, F.Gamma(a),
  // F.Gamma(F.Plus(a, F.Times(F.CN2, F.Power(g, F.CN1)))),
  // F.Gamma(F.Subtract(a, F.Power(g, F.CN1)))),
  // F.Times(F.C2,
  // F.Power(F.Gamma(F.Subtract(a, F.Power(g, F.CN1))), F.C3)))),
  // F.Greater(F.Times(a, g), F.C3))),
  // S.Indeterminate);
  //
  // }
  // return F.NIL;
  // }
  //
  // @Override
  // public IExpr variance(IAST dist) {
  // if (dist.isAST2()) {
  // IExpr a = dist.arg1();
  // IExpr b = dist.arg2();
  // // Piecewise({{b^2/((-2+a)*(-1+a)^2),a>2}},Indeterminate)
  // return F.Piecewise(F.list(F.list(
  // F.Times(F.Power(F.Times(F.Plus(F.CN2, a), F.Sqr(F.Plus(F.CN1, a))), F.CN1), F.Sqr(b)),
  // F.Greater(a, F.C2))), S.Indeterminate);
  // } else if (dist.isAST(S.InverseGammaDistribution, 5)) {
  // //
  // IExpr a = dist.arg1();
  // IExpr b = dist.arg2();
  // IExpr g = dist.arg3();
  // // Piecewise({{(b^2*(Gamma(a)*Gamma(a-2/g)-Gamma(a-1/g)^2))/Gamma(a)^2,a*g>2}},Indeterminate)
  // return F
  // .Piecewise(
  // F.list(F.list(
  // F.Times(F.Sqr(b), F.Power(F.Gamma(a), F.CN2),
  // F.Subtract(
  // F.Times(F.Gamma(a),
  // F.Gamma(F.Plus(a, F.Times(F.CN2, F.Power(g, F.CN1))))),
  // F.Sqr(F.Gamma(F.Subtract(a, F.Power(g, F.CN1)))))),
  // F.Greater(F.Times(a, g), F.C2))),
  // F.Indeterminate);
  //
  // }
  // return F.NIL;
  // }
  // }

  /**
   *
   *
   * <pre>
   * LogNormalDistribution(m, s)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns a log-normal distribution.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Log-normal_distribution">Wikipedia - Log-normal
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
  private static final class LogNormalDistribution extends AbstractEvaluator
      implements ICDF, IContinuousDistribution, IPDF, IStatistics, IRandomVariate, ICentralMoment {

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        //
        if (!engine.isArbitraryMode() && //
            (n.isNumericArgument(true) || m.isNumericArgument(true) || k.isNumericArgument(true))) {
          double nDouble = n.evalfNaN();
          double mDouble = m.evalfNaN();
          double kDouble = k.evalfNaN();
          if (!Double.isNaN(nDouble) && !Double.isNaN(mDouble) && !Double.isNaN(kDouble)) {
            try {
              return F.num(
                  new org.hipparchus.distribution.continuous.LogNormalDistribution(nDouble, mDouble) //
                      .cumulativeProbability(kDouble));
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
              //
            }
          }
        }
        IExpr function =
            // [$ Piecewise({{(1/2)*Erfc((n - Log(#))/(Sqrt(2)*m)), # > 0}}, 0) & $]
            F.Function(
                F.Piecewise(
                    F.list(
                        F.list(F.Times(F.C1D2,
                            F.Erfc(F.Times(F.Power(F.Times(F.CSqrt2, m), F.CN1),
                                F.Subtract(n, F.Log(F.Slot1))))),
                            F.Greater(F.Slot1, F.C0))),
                    F.C0)); // $$;
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr centralMoment(IAST dist, IExpr n, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public IExpr inverseCDF(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        if (!engine.isArbitraryMode() && //
            (n.isNumericArgument(true) || m.isNumericArgument(true) || k.isNumericArgument(true))) {
          double nDouble = n.evalfNaN();
          double mDouble = m.evalfNaN();
          double kDouble = k.evalfNaN();
          if (!Double.isNaN(nDouble) && !Double.isNaN(mDouble) && !Double.isNaN(kDouble)) {
            try {
              return F.num(
                  new org.hipparchus.distribution.continuous.LogNormalDistribution(nDouble, mDouble) //
                      .inverseCumulativeProbability(kDouble));
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
              //
            }
          }
        }
        IExpr function =
            // [$ ( ConditionalExpression(Piecewise({{E^(n - Sqrt(2)*m*InverseErfc(2*#)), 0 < # <
            // 1}, {0, #
            // <= 0}}, Infinity), 0 <= # <= 1)& ) $]
            F.Function(
                F.ConditionalExpression(
                    F.Piecewise(
                        F.list(
                            F.list(
                                F.Exp(F.Plus(n,
                                    F.Times(F.CN1, F.CSqrt2, m,
                                        F.InverseErfc(F.Times(F.C2, F.Slot1))))),
                                F.Less(F.C0, F.Slot1, F.C1)),
                            F.list(F.C0, F.LessEqual(F.Slot1, F.C0))),
                        F.oo),
                    F.LessEqual(F.C0, F.Slot1, F.C1))); // $$;
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr kurtosis(IAST dist, EvalEngine engine) {
      if (dist.isAST2()) {
        // IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        // -3+3*E^(2*b^2)+2*E^(3*b^2)+E^(4*b^2)
        IExpr v1 = F.Sqr(b);
        return F.Plus(F.CN3, F.Times(F.C3, F.Exp(F.Times(F.C2, v1))),
            F.Times(F.C2, F.Exp(F.Times(F.C3, v1))), F.Exp(F.Times(F.C4, v1)));

      }
      return F.NIL;
    }

    @Override
    public IExpr mean(IAST dist) {
      if (dist.isAST2()) {
        IExpr m = dist.arg1();
        IExpr s = dist.arg2();
        // (m,s) -> E^(m+s^2/2)
        return F.Power(S.E, F.Plus(m, F.Times(F.C1D2, F.Sqr(s))));
      }
      return F.NIL;
    }

    @Override
    public IExpr median(IAST dist) {
      if (dist.isAST2()) {
        // (m,s) -> E^(m+s^2/2)
        return F.Power(S.E, dist.arg1());
      }
      return F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        //
        if (!engine.isArbitraryMode() && //
            (n.isNumericArgument(true) || m.isNumericArgument(true) || k.isNumericArgument(true))) {
          double nDouble = n.evalfNaN();
          double mDouble = m.evalfNaN();
          double kDouble = k.evalfNaN();
          if (!Double.isNaN(nDouble) && !Double.isNaN(mDouble) && !Double.isNaN(kDouble)) {
            try {
              return F.num(
                  new org.hipparchus.distribution.continuous.LogNormalDistribution(nDouble, mDouble) //
                      .density(kDouble));
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
              //
            }
          }
        }
        IExpr function =
            // [$ (Piecewise({{1/(E^((-n + Log(#))^2/(2*m^2))*(#*m*Sqrt(2*Pi))), # > 0}}, 0)) & $]
            F.Function(F.Piecewise(F.list(F.list(F.Power(
                F.Times(F.Exp(F.Times(F.Power(F.Times(F.C2, F.Sqr(m)), F.CN1),
                    F.Sqr(F.Plus(F.Negate(n), F.Log(F.Slot1))))), F.Slot1, m, F.Sqrt(F.C2Pi)),
                F.CN1), F.Greater(F.Slot1, F.C0))), F.C0)); // $$;
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr randomVariate(Random random, IAST dist, int size) {
      if (dist.isAST2()) {
        // see exception handling in RandonmVariate() function
        double mean = dist.arg1().evalfNaN();
        double sigma = dist.arg2().evalfNaN();
        if (Double.isNaN(mean)) {
          return F.NIL;
        }
        if (sigma > 0) {
          RandomDataGenerator rdg = new RandomDataGenerator();
          double[] vector = rdg.nextDeviates(
              new org.hipparchus.distribution.continuous.LogNormalDistribution(mean, sigma), size);
          return new ASTRealVector(vector, false);
          // return F.num(Math.exp(new GaussianGenerator(mean, sigma, random).nextValue()));
        }
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

    @Override
    public IExpr skewness(IAST dist) {
      if (dist.isAST2()) {
        // IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        return
        // [$ Sqrt(-1+E^m^2)*(2+E^m^2) $]
        F.Times(F.Sqrt(F.Plus(F.CN1, F.Exp(F.Sqr(m)))), F.Plus(F.C2, F.Exp(F.Sqr(m)))); // $$;
      }
      return F.NIL;
    }

    @Override
    public IExpr variance(IAST dist) {
      if (dist.isAST2()) {
        IExpr m = dist.arg1();
        IExpr s = dist.arg2();
        // E^(2*m+s^2)*(-1+E^(s^2))
        return F.Times(F.Plus(F.CN1, F.Power(S.E, F.Sqr(s))),
            F.Power(S.E, F.Plus(F.Times(F.C2, m), F.Sqr(s))));
      }
      return F.NIL;
    }

  }

  private static final class MultinormalDistribution extends AbstractEvaluator
      implements IContinuousDistribution, ICDF, ICovariance, IPDF, IStatistics, ICentralMoment,
      IRandomVariate {

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      // No simple closed form for the CDF of a general MultinormalDistribution.
      return F.NIL;
    }

    @Override
    public IExpr centralMoment(IAST dist, IExpr m, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public IExpr covariance(IAST dist, EvalEngine engine) {
      if (dist.isAST1()) {
        return dist.arg1();
      } else if (dist.isAST2()) {
        return dist.arg2();
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
    public IExpr kurtosis(IAST dist, EvalEngine engine) {
      // The kurtosis of a normal distribution is 3.
      // For Multinormal, this returns a vector of component-wise kurtosis values.
      IExpr mu = mean(dist);
      if (mu.isList()) {
        return F.constantArray(F.C3, mu.argSize());
      }
      return F.NIL;
    }

    @Override
    public IExpr mean(IAST dist) {
      if (dist.isAST2()) {
        IExpr sigmaMatrix = dist.arg2();
        if (isSigmaMatrix(dist, sigmaMatrix, F.C2)) {
          return dist.arg1();
        }
      } else if (dist.isAST1()) {
        // Assume zero mean vector, dimension inferred from Sigma
        IExpr sigmaMatrix = dist.arg1();
        if (isSigmaMatrix(dist, sigmaMatrix, F.C1)) {
          // Sigma is an nxn matrix, so mean is a vector of size n
          return F.constantArray(F.C0, sigmaMatrix.argSize());
        }
      }
      return F.NIL;
    }

    private static boolean isSigmaMatrix(IAST dist, IExpr sigmaMatrix, IInteger position) {
      int[] sigmaDimensions = sigmaMatrix.isMatrix(false);
      if (sigmaDimensions == null || sigmaDimensions[0] != sigmaDimensions[1]
          || sigmaDimensions[0] == 0) {
        // The value `1` at position `2` in `3` is expected to be a symmetric positive definite
        // matrix
        Errors.printMessage(S.MultinormalDistribution, "posdefprm",
            F.List(sigmaMatrix, position, dist));
        return false;
      }
      if (!S.SymmetricMatrixQ.ofQ(EvalEngine.get(), sigmaMatrix)) {
        // The value `1` at position `2` in `3` is expected to be a symmetric positive definite
        // matrix
        Errors.printMessage(S.MultinormalDistribution, "posdefprm",
            F.List(sigmaMatrix, position, dist));
        return false;
      }
      // TODO test symmetric positive definite
      // if (!S.PositiveDefiniteMatrixQ.ofQ(EvalEngine.get(), sigmaMatrix)) {
      // // The value `1` at position `2` in `3` is expected to be a symmetric positive definite
      // // matrix
      // Errors.printMessage(S.MultinormalDistribution, "posdefprm",
      // F.List(sigmaMatrix, position, dist));
      // return false;
      // }

      return true;
    }

    @Override
    public IExpr median(IAST dist) {
      return F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr X, EvalEngine engine) {
      IExpr mu = F.NIL;
      IExpr sigma = F.NIL;

      if (dist.isAST1()) {
        sigma = dist.arg1();
        if (sigma.isList()) {
          mu = F.constantArray(F.C0, sigma.argSize());
        }
      } else if (dist.isAST2()) {
        mu = dist.arg1();
        sigma = dist.arg2();
      }
      int[] dims = sigma.isMatrix();
      if (mu.isPresent() && dims != null && dims[0] == dims[1]) {
        // Dimension k
        int k = dims[0]; // Rows of sigma
        IExpr kExpr = F.ZZ(k);

        // PDF = (2*Pi)^(-k/2) * Det(Sigma)^(-1/2) * Exp( -1/2 * (x-mu).Inverse(Sigma).(x-mu) )

        // Pre-factor: ( (2*Pi)^k * Det(Sigma) ) ^ (-1/2)
        IExpr preFactor = F.Power(F.Times(F.Power(F.C2Pi, kExpr), F.Det(sigma)), F.CN1D2);

        // Exponent term: -1/2 * (X - Mu) . Inverse(Sigma) . (X - Mu)
        IExpr diff = F.Subtract(X, mu);
        IExpr invSigma = F.Inverse(sigma);
        IExpr exponent = F.Times(F.CN1D2, F.Dot(diff, invSigma, diff));

        return F.Times(preFactor, F.Exp(exponent));
      }

      return F.NIL;
    }

    @Override
    public IExpr randomVariate(Random random, IAST dist, int size) {
      try {
        double[] meanVector;
        double[][] covMatrix;

        if (dist.isAST1()) {
          // MultinormalDistribution(Sigma) -> Mu is zeros
          IExpr sigmaExpr = dist.arg1();
          covMatrix = sigmaExpr.toDoubleMatrix();
          if (covMatrix == null) {
            return F.NIL;
          }
          int dim = covMatrix.length;
          meanVector = new double[dim]; // Java doubles default to 0.0
        } else if (dist.isAST2()) {
          // MultinormalDistribution(Mu, Sigma)
          IExpr muExpr = dist.arg1();
          IExpr sigmaExpr = dist.arg2();
          meanVector = muExpr.toDoubleVector();
          covMatrix = sigmaExpr.toDoubleMatrix();
        } else {
          return F.NIL;
        }

        if (meanVector != null && covMatrix != null) {
          org.hipparchus.distribution.multivariate.MultivariateNormalDistribution mnd =
              new org.hipparchus.distribution.multivariate.MultivariateNormalDistribution(
                  meanVector, covMatrix);

          IASTAppendable list = F.ListAlloc(size);
          for (int i = 0; i < size; i++) {
            double[] sample = mnd.sample();
            list.append(F.List(sample));
          }
          return list;
        }

      } catch (RuntimeException rex) {
        // Fallback if numerical evaluation fails (e.g., symbolic parameters)
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

    @Override
    public IExpr skewness(IAST dist) {
      // Skewness of multivariate normal distribution is a vector of zeros
      IExpr mu = mean(dist);
      if (mu.isList()) {
        return F.constantArray(F.C0, mu.argSize());
      }
      return F.NIL;
    }

    @Override
    public IExpr variance(IAST dist) {
      // Variance vector contains the diagonal elements of the covariance matrix
      IExpr sigma = F.NIL;
      if (dist.isAST1()) {
        sigma = dist.arg1();
      } else if (dist.isAST2()) {
        sigma = dist.arg2();
      }
      int[] dims = sigma.isMatrix();
      if (dims != null && dims[0] == dims[1]) {
        IAST matrix = (IAST) sigma;
        int dim = matrix.argSize();
        IASTAppendable varianceVec = F.ListAlloc(dim);
        for (int i = 1; i <= dim; i++) {
          // Get the i-th row (IAST) and then the i-th element
          IExpr row = matrix.get(i);
          if (row.isList() && row.size() > i) {
            varianceVec.append(row.get(i));
          } else {
            return F.NIL;
          }
        }
        return varianceVec;
      }
      return F.NIL;
    }
  }

  private static final class NakagamiDistribution extends AbstractEvaluator
      implements ICDF, IContinuousDistribution, IPDF, IRandomVariate, IStatistics {

    @Override
    public IExpr parameterAssumptions(IAST dist) {
      if (dist.isAST2()) {
        return F.And(F.Greater(dist.arg1(), F.C0), F.Greater(dist.arg2(), F.C0));
      }
      return F.NIL;
    }

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        if (!engine.isArbitraryMode() && //
            (n.isNumericArgument(true) || m.isNumericArgument(true) || k.isNumericArgument(true))) {
          double nDouble = n.evalfNaN();
          double mDouble = m.evalfNaN();
          double kDouble = k.evalfNaN();
          if (!Double.isNaN(nDouble) && !Double.isNaN(mDouble) && !Double.isNaN(kDouble)) {
            try {
              return F.num(
                  new org.hipparchus.distribution.continuous.NakagamiDistribution(nDouble, mDouble) //
                      .cumulativeProbability(kDouble));
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
              //
            }
          }
        }
        IExpr function =
            // [$ Piecewise({{GammaRegularized(n, 0, (#^2*n)/m), # > 0}}, 0) & $]
            F.Function(F.Piecewise(F.list(
                F.list(F.GammaRegularized(n, F.C0, F.Times(F.Power(m, F.CN1), n, F.Sqr(F.Slot1))),
                    F.Greater(F.Slot1, F.C0))),
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
    public IExpr inverseCDF(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        IExpr function =
            // [$ ( ConditionalExpression(Piecewise({{Sqrt((m*InverseGammaRegularized(n, 0, #))/n),
            // 0 < # <
            // 1}, {0, # <= 0}}, Infinity), 0 <= # <= 1)& ) $]
            F.Function(
                F.ConditionalExpression(
                    F.Piecewise(
                        F.list(
                            F.list(
                                F.Sqrt(F.Times(m, F.Power(n, F.CN1),
                                    F.InverseGammaRegularized(n, F.C0, F.Slot1))),
                                F.Less(F.C0, F.Slot1, F.C1)),
                            F.list(F.C0, F.LessEqual(F.Slot1, F.C0))),
                        F.oo),
                    F.LessEqual(F.C0, F.Slot1, F.C1))); // $$;
        return callFunction(function, k);
      }
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
    public IExpr median(IAST dist) {
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        // (n,m) -> Sqrt((m*InverseGammaRegularized(n, 0, 1/2))/n)
        return F.Sqrt(F.Times(m, F.Power(n, -1), F.InverseGammaRegularized(n, F.C0, F.C1D2)));
      }
      return F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        //
        if (!engine.isArbitraryMode() && //
            (n.isNumericArgument(true) || m.isNumericArgument(true) || k.isNumericArgument(true))) {
          double nDouble = n.evalfNaN();
          double mDouble = m.evalfNaN();
          double kDouble = k.evalfNaN();
          if (!Double.isNaN(nDouble) && !Double.isNaN(mDouble) && !Double.isNaN(kDouble)) {
            try {
              return F.num(
                  new org.hipparchus.distribution.continuous.NakagamiDistribution(nDouble, mDouble) //
                      .density(kDouble));
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
              //
            }
          }
        }
        IExpr function =
            // [$ (Piecewise({{(2*#^(-1 + 2*n)*(n/m)^n)/(E^((#^2*n)/m)*Gamma(n)), # > 0}}, 0)) & $]
            F.Function(
                F.Piecewise(F.list(F.list(
                    F.Times(F.C2, F.Power(F.Times(F.Power(m, F.CN1), n), n),
                        F.Power(F.Times(F.Exp(F.Times(F.Power(m, F.CN1), n, F.Sqr(F.Slot1))),
                            F.Gamma(n)), F.CN1),
                        F.Power(F.Slot1, F.Plus(F.CN1, F.Times(F.C2, n)))),
                    F.Greater(F.Slot1, F.C0))), F.C0)); // $$;
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr randomVariate(Random random, IAST dist, int size) {
      if (dist.isAST2()) {
        // see exception handling in RandonmVariate() function
        double n = dist.arg1().evalfNaN();
        double m = dist.arg2().evalfNaN();
        if (Double.isNaN(n) || Double.isNaN(m)) {
          return F.NIL;
        }

        // TODO cache RandomDataGenerator instance
        RandomDataGenerator rdg = new RandomDataGenerator();
        double[] vector = rdg.nextDeviates( //
            new org.hipparchus.distribution.continuous.NakagamiDistribution(n, m), //
            size);
        return new ASTRealVector(vector, false);
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

    @Override
    public IExpr skewness(IAST dist) {
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        // IExpr m = dist.arg2();
        return
        // [$ (Pochhammer(n,1/2)*(1/2 - 2*(n - Pochhammer(n, 1/2)^2)))/(n-Pochhammer(n,
        // 1/2)^2)^(3/2) $]
        F.Times(F.Pochhammer(n, F.C1D2),
            F.Plus(F.C1D2, F.Times(F.CN2, F.Subtract(n, F.Sqr(F.Pochhammer(n, F.C1D2))))),
            F.Power(F.Subtract(n, F.Sqr(F.Pochhammer(n, F.C1D2))), F.QQ(-3L, 2L))); // $$;
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
  }


  /**
   *
   *
   * <pre>
   * NormalDistribution(m, s)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the normal distribution of mean <code>m</code> and sigma <code>s</code>.
   *
   * </blockquote>
   *
   * <pre>
   * NormalDistribution()
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the standard normal distribution for <code>m = 0</code> and <code>s = 1</code>.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Normal_distribution">Wikipedia - Normal
   * distribution</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <p>
   * The <a href="https://en.wikipedia.org/wiki/Probability_density">probability density
   * function</a> of the normal distribution is
   *
   * <pre>
   * &gt;&gt; PDF(NormalDistribution(m, s), x)
   * 1/(Sqrt(2)*E^((-m+x)^2/(2*s^2))*Sqrt(Pi)*s)
   * </pre>
   *
   * <p>
   * The <a href="https://en.wikipedia.org/wiki/Cumulative_distribution_function">cumulative
   * distribution function</a> of the standard normal distribution is
   *
   * <pre>
   * &gt;&gt; CDF(NormalDistribution( ), x)
   * 1/2*(2-Erfc(x/Sqrt(2)))
   * </pre>
   *
   * <p>
   * The <a href="https://en.wikipedia.org/wiki/Mean">mean</a> of the normal distribution is
   *
   * <pre>
   * &gt;&gt; Mean(NormalDistribution(m, s))
   * m
   * </pre>
   *
   * <p>
   * The <a href="https://en.wikipedia.org/wiki/Standard_deviation">standard deviation</a> of the
   * normal distribution is
   *
   * <pre>
   * &gt;&gt; StandardDeviation(NormalDistribution(m, s))
   * s
   * </pre>
   *
   * <p>
   * The <a href="https://en.wikipedia.org/wiki/Variance">variance</a> of the normal distribution is
   *
   * <pre>
   * &gt;&gt; Variance(NormalDistribution(m, s))
   * s^2
   * </pre>
   *
   * <p>
   * The <a href=
   * "https://en.wikipedia.org/wiki/Normal_distribution#Generating_values_from_normal_distribution">random
   * variates</a> of a normal distribution can be generated with function <code>RandomVariate</code>
   *
   * <pre>
   * &gt;&gt; RandomVariate(NormalDistribution(2,3), 10^1)
   * {1.14364,6.09674,5.16495,2.39937,-0.52143,-1.46678,3.60142,-0.85405,2.06373,-0.29795}
   * </pre>
   */
  private static final class NormalDistribution extends AbstractEvaluator implements ICentralMoment,
      IContinuousDistribution, IStatistics, IRandomVariate, IPDF, ICDF, IGeneratingFunction {

    @Override
    public IExpr mgf(IAST dist, IExpr t, EvalEngine engine) {
      if (dist.isAST0()) {
        // E^(t^2/2)
        return F.Exp(F.Divide(F.Sqr(t), F.C2));
      }
      if (dist.isAST2()) {
        IExpr m = dist.arg1();
        IExpr s = dist.arg2();
        // E^(m*t + s^2*t^2/2)
        return F.Exp(F.Plus(F.Times(m, t), F.Divide(F.Times(F.Sqr(s), F.Sqr(t)), F.C2)));
      }
      return F.NIL;
    }

    @Override
    public IExpr moment(IAST dist, IExpr n) {
      int order = n.toIntDefault();
      if (order < 0) {
        return F.NIL;
      }
      IExpr m = F.C0;
      IExpr s = F.C1;
      if (dist.isAST2()) {
        m = dist.arg1();
        s = dist.arg2();
      } else if (!dist.isAST0()) {
        return F.NIL;
      }
      // E[X^n] = Sum(Binomial(n, 2*k)*m^(n - 2*k)*s^(2*k)*(2*k - 1)!!, {k, 0, Floor(n/2)})
      IASTAppendable sum = F.PlusAlloc(order / 2 + 1);
      for (int k = 0; 2 * k <= order; k++) {
        int meanExponent = order - 2 * k;
        // avoid 0^0 for a mean of 0
        IExpr meanPower = meanExponent == 0 ? F.C1 : F.Power(m, F.ZZ(meanExponent));
        IExpr deviationPower = k == 0 ? F.C1 : F.Power(s, F.ZZ(2 * k));
        sum.append(F.Times(F.Binomial(F.ZZ(order), F.ZZ(2 * k)), meanPower, deviationPower,
            F.Factorial2(F.ZZ(2 * k - 1))));
      }
      return sum;
    }

    @Override
    public IExpr parameterAssumptions(IAST dist) {
      if (dist.isAST0()) {
        return S.True;
      }
      if (dist.isAST2()) {
        return F.Greater(dist.arg2(), F.C0);
      }
      return F.NIL;
    }

    @Override
    public IExpr standardDeviation(IAST dist) {
      if (dist.isAST0()) {
        return F.C1;
      }
      if (dist.isAST2()) {
        return dist.arg2();
      }
      return F.NIL;
    }

    @Override
    public IExpr survivalFunction(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST0()) {
        // Erfc(#/Sqrt(2))/2 &
        return callFunction(F.Function(F.Times(F.C1D2, F.Erfc(F.Times(F.C1DSqrt2, F.Slot1)))), k);
      }
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        // Erfc((# - n)/(Sqrt(2)*m))/2 &
        return callFunction(F.Function(F.Times(F.C1D2,
            F.Erfc(F.Times(F.Power(F.Times(F.CSqrt2, m), F.CN1), F.Subtract(F.Slot1, n))))), k);
      }
      return F.NIL;
    }

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST0()) {
        IExpr function =
            // [$ ( (1/2)*Erfc(-(#/Sqrt(2))) & ) $]
            F.Function(F.Times(F.C1D2, F.Erfc(F.Times(F.CN1, F.C1DSqrt2, F.Slot1)))); // $$;
        return callFunction(function, k);
      } else if (dist.isAST2()) {
        //
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        if (!engine.isArbitraryMode() && //
            (n.isNumericArgument(true) || m.isNumericArgument(true) || k.isNumericArgument(true))) {
          double nDouble = n.evalfNaN();
          double mDouble = m.evalfNaN();
          double kDouble = k.evalfNaN();
          if (!Double.isNaN(nDouble) && !Double.isNaN(mDouble) && !Double.isNaN(kDouble)) {
            try {
              return F.num(
                  new org.hipparchus.distribution.continuous.NormalDistribution(nDouble, mDouble) //
                      .cumulativeProbability(kDouble));
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
              //
            }
          }
        }
        IExpr function =
            // [$ ( (1/2)*Erfc((-# + n)/(Sqrt(2)*m)) &) $]
            F.Function(F.Times(F.C1D2, F.Erfc(
                F.Times(F.Power(F.Times(F.CSqrt2, m), F.CN1), F.Plus(F.Negate(F.Slot1), n))))); // $$;
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr centralMoment(IAST dist, IExpr n, EvalEngine engine) {
      IExpr b = F.C1;
      if (dist.isAST0() || dist.isAST2()) {
        if (dist.isAST2()) {
          b = dist.arg2();
        }
        // don't use EvenQ, it evals to false in most cases
        // Piecewise({{b^n*Factorial2(-1 + n), Mod(n,2)==0 && n >= 0}}, 0)
        IExpr function = engine.evaluate(F.Times(//
            F.Power(b, n), //
            F.Factorial2(F.Plus(F.CN1, n))));
        return F.Piecewise(
            F.List(F.List(function, F.And(F.Equal(F.Mod(n, F.C2), F.C0), F.GreaterEqual(n, F.C0)))),
            F.C0);
      }
      return F.NIL;
    }

    @Override
    public IAST checkParameters(IAST dist) {
      if (dist.isAST0()) {
        return dist;
      }
      if (dist.isAST2()) {
        double v = dist.arg2().toDoubleDefault();
        if (v <= 0.0) {
          if (v > Double.MIN_VALUE) {
            // Parameter `1` at position `2` in `3` is expected to be positive.
            return Errors.printMessage(S.NormalDistribution, "posprm",
                F.list(dist.arg2(), F.C2, dist), EvalEngine.get());
          }
          return F.NIL;
        }
        return dist;
      }
      return F.NIL;
    }

    @Override
    public RealDistribution dist() {
      return new org.hipparchus.distribution.continuous.NormalDistribution(0, 1);
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // 0 or 2 args are allowed
      if (ast.isAST0()) {
        return F.NormalDistribution(F.C0, F.C1);
      }
      if (ast.isAST1()) {
        // `1` called with 1 argument; `2` arguments are expected.
        return Errors.printMessage(ast.topHead(), "argr", F.List(S.NormalDistribution, F.C2),
            engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_2;
    }

    @Override
    public IExpr inverseCDF(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST0()) {
        IExpr function =
            // [$ (ConditionalExpression((-Sqrt(2))*InverseErfc(2*#1), 0 <= #1 <= 1) & ) $]
            F.Function(F.ConditionalExpression(
                F.Times(F.CN1, F.CSqrt2, F.InverseErfc(F.Times(F.C2, F.Slot1))),
                F.LessEqual(F.C0, F.Slot1, F.C1))); // $$;
        return callFunction(function, k);
      } else if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        if (!engine.isArbitraryMode() && //
            (n.isNumericArgument(true) || m.isNumericArgument(true) || k.isNumericArgument(true))) {
          double nDouble = n.evalfNaN();
          double mDouble = m.evalfNaN();
          double kDouble = k.evalfNaN();
          if (!Double.isNaN(nDouble) && !Double.isNaN(mDouble) && !Double.isNaN(kDouble)) {
            try {
              return F.num(
                  new org.hipparchus.distribution.continuous.NormalDistribution(nDouble, mDouble) //
                      .inverseCumulativeProbability(kDouble));
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
              //
            }
          }
        }
        IExpr function =
            // [$ ( ConditionalExpression(n - Sqrt(2)*m*InverseErfc(2*#1), 0 <= #1 <= 1) &) $]
            F.Function(F.ConditionalExpression(
                F.Plus(n, F.Times(F.CN1, F.CSqrt2, m, F.InverseErfc(F.Times(F.C2, F.Slot1)))),
                F.LessEqual(F.C0, F.Slot1, F.C1))); // $$;
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr kurtosis(IAST dist, EvalEngine engine) {
      if (dist.isAST0() || dist.isAST2()) {
        return F.C3;
      }
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
    public IExpr median(IAST dist) {
      if (dist.isAST0()) {
        return F.C0;
      }
      if (dist.isAST2()) {
        return dist.arg1();
      }
      return F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST0()) {
        IExpr function =
            // [$ ( 1/(E^(#^2/2)*Sqrt(2*Pi)) & ) $]
            F.Function(
                F.Power(F.Times(F.Exp(F.Times(F.C1D2, F.Sqr(F.Slot1))), F.Sqrt(F.C2Pi)), F.CN1)); // $$;
        return callFunction(function, k);
      } else if (dist.isAST2()) {
        //
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        if (!engine.isArbitraryMode() && //
            (n.isNumericArgument(true) || m.isNumericArgument(true) || k.isNumericArgument(true))) {
          double nDouble = n.evalfNaN();
          double mDouble = m.evalfNaN();
          double kDouble = k.evalfNaN();
          if (!Double.isNaN(nDouble) && !Double.isNaN(mDouble) && !Double.isNaN(kDouble)) {
            try {
              return F.num(
                  new org.hipparchus.distribution.continuous.NormalDistribution(nDouble, mDouble) //
                      .density(kDouble));
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
              //
            }
          }
        }
        IExpr function =
            // [$ ( 1/(E^((# - n)^2/(2*m^2))*(m*Sqrt(2*Pi))) & ) $]
            F.Function(F.Power(F.Times(F.Exp(F.Times(F.Power(F.Times(F.C2, F.Sqr(m)), F.CN1),
                F.Sqr(F.Plus(F.Negate(n), F.Slot1)))), m, F.Sqrt(F.C2Pi)), F.CN1)); // $$;
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr randomVariate(Random random, IAST dist, int size) {
      if (dist.isAST0()) {
        return F.num(random.nextGaussian());
      }
      if (dist.isAST2()) {
        // see exception handling in RandonmVariate() function
        double mean = dist.arg1().evalfNaN();
        double sigma = dist.arg2().evalfNaN();
        if (Double.isNaN(mean)) {
          return F.NIL;
        }
        if (sigma > 0) {
          // double mean = dist.arg1().evalDouble();
          // double sigma = dist.arg2().evalDouble();
          // return F.num(new GaussianGenerator(mean, sigma, random).nextValue());
          RandomDataGenerator rdg = new RandomDataGenerator();
          double[] vector = rdg.nextDeviates(
              new org.hipparchus.distribution.continuous.NormalDistribution(mean, sigma), size);
          return new ASTRealVector(vector, false);
        }
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

    @Override
    public IExpr skewness(IAST dist) {
      if (dist.isAST0()) {
        return F.C0;
      }
      if (dist.isAST2()) {
        return F.C0;
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
  }


  private static final class ParetoDistribution extends AbstractEvaluator
      implements IContinuousDistribution, IStatistics, IPDF, ICDF {

    @Override
    public IExpr survivalFunction(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        // Piecewise({{a^b*#^(-b), # >= a}}, 1) &
        // the expanded form (instead of (a/#)^b) lets PDF/SurvivalFunction cancel to a/# in
        // HazardFunction
        return callFunction(F.Function(
            F.Piecewise(F.list(F.list(F.Times(F.Power(a, b), F.Power(F.Slot1, F.Negate(b))),
                F.GreaterEqual(F.Slot1, a))), F.C1)),
            k);
      }
      return F.NIL;
    }

    @Override
    public IExpr cdf(IAST dist, IExpr x, EvalEngine engine) {
      if (dist.isAST2()) {
        //
        IExpr k = dist.arg1();
        IExpr a = dist.arg2();

        IExpr function =
            // [$ Piecewise({{1-(k/#)^a, #>=k}}, 0) & $]
            F.Function(F.Piecewise(
                F.list(F.list(F.Subtract(F.C1, F.Power(F.Times(k, F.Power(F.Slot1, F.CN1)), a)),
                    F.GreaterEqual(F.Slot1, k))),
                F.C0)); // $$;
        return callFunction(function, x);
      } else if (dist.isAST3()) {
        //
        IExpr k = dist.arg1();
        IExpr a = dist.arg2();
        IExpr m = dist.arg3();
        IExpr function =
            // [$ Piecewise({{1-(1+(-m+#)/k)^(-a),#>=m}},0) & $]
            F.Function(F.Piecewise(F.list(F.list(F.Subtract(F.C1,
                F.Power(F.Plus(F.C1, F.Times(F.Power(k, F.CN1), F.Plus(F.Negate(m), F.Slot1))),
                    F.Negate(a))),
                F.GreaterEqual(F.Slot1, m))), F.C0)); // $$;
        return callFunction(function, x);
      } else if (dist.argSize() == 4) {
        //
        IExpr k = dist.arg1();
        IExpr a = dist.arg2();
        IExpr g = dist.arg3();
        IExpr m = dist.arg4();
        IExpr function =
            // [$ Piecewise({{1-(1+((-m+#)/k)^(1/g))^(-a), #>=m}}, 0) & $]
            F.Function(
                F.Piecewise(
                    F.list(
                        F.list(
                            F.Subtract(F.C1,
                                F.Power(
                                    F.Plus(F.C1,
                                        F.Power(F.Times(F.Power(k, F.CN1),
                                            F.Plus(F.Negate(m), F.Slot1)), F.Power(g, F.CN1))),
                                    F.Negate(a))),
                            F.GreaterEqual(F.Slot1, m))),
                    F.C0)); // $$;
        return callFunction(function, x);
      }
      return F.NIL;
    }

    @Override
    public IAST checkParameters(IAST dist) {
      if (dist.isAST2()) {
        return dist;
      }
      if (dist.isAST3()) {
        return dist;
      }
      if (dist.argSize() == 4) {
        return dist;
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // 2 up to 4 args are allowed
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_4;
    }

    @Override
    public IExpr inverseCDF(IAST dist, IExpr x, EvalEngine engine) {
      if (dist.isAST2()) {
        //
        IExpr k = dist.arg1();
        IExpr a = dist.arg2();

        IExpr function =
            // [$ ConditionalExpression(Piecewise({{k/(1-#)^a^(-1), #<1}}, Infinity), 0 <=#<=1) & $]
            F.Function(F.ConditionalExpression(F.Piecewise(F.list(F.list(
                F.Times(k, F.Power(F.Power(F.Subtract(F.C1, F.Slot1), F.Power(a, F.CN1)), F.CN1)),
                F.Less(F.Slot1, F.C1))), F.oo), F.LessEqual(F.C0, F.Slot1, F.C1))); // $$;
        return callFunction(function, x);
      } else if (dist.isAST3()) {
        //
        IExpr k = dist.arg1();
        IExpr a = dist.arg2();
        IExpr m = dist.arg3();
        IExpr function =
            // [$ ConditionalExpression(Piecewise({{m + k*(-1 + (1 -#)^(-a^(-1))), 0 <#< 1}, {m,
            // #<=0}}, Infinity), 0 <=#<= 1) & $]
            F.Function(
                F.ConditionalExpression(
                    F.Piecewise(
                        F.list(
                            F.list(
                                F.Plus(m,
                                    F.Times(k,
                                        F.Plus(F.CN1,
                                            F.Power(F.Subtract(F.C1, F.Slot1),
                                                F.Negate(F.Power(a, F.CN1)))))),
                                F.Less(F.C0, F.Slot1, F.C1)),
                            F.list(m, F.LessEqual(F.Slot1, F.C0))),
                        F.oo),
                    F.LessEqual(F.C0, F.Slot1, F.C1))); // $$;
        return callFunction(function, x);
      } else if (dist.argSize() == 4) {
        //
        IExpr k = dist.arg1();
        IExpr a = dist.arg2();
        IExpr g = dist.arg3();
        IExpr m = dist.arg4();
        IExpr function =
            // [$ ConditionalExpression[Piecewise[{{m + k*(-1 + (1 - #)^(-a^(-1)))^g, 0<#< 1},
            // {m, #<=0}}, Infinity], 0 <=#<= 1] & $]
            F.Function(
                F.ConditionalExpression(
                    F.Piecewise(
                        F.list(
                            F.list(
                                F.Plus(m,
                                    F.Times(k,
                                        F.Power(F.Plus(F.CN1,
                                            F.Power(F.Subtract(F.C1, F.Slot1),
                                                F.Negate(F.Power(a, F.CN1)))),
                                            g))),
                                F.Less(F.C0, F.Slot1, F.C1)),
                            F.list(m, F.LessEqual(F.Slot1, F.C0))),
                        F.oo),
                    F.LessEqual(F.C0, F.Slot1, F.C1))); // $$;
        return callFunction(function, x);
      }
      return F.NIL;
    }

    @Override
    public IExpr mean(IAST dist) {
      if (dist.isAST2()) {
        IExpr k = dist.arg1();
        IExpr a = dist.arg2();
        //
        IExpr function =
            // [$ Piecewise({{(k*a)/(-1 + a), a > 1}}, Indeterminate) $]
            F.Piecewise(
                F.list(F.list(F.Times(F.Power(F.Plus(F.CN1, a), F.CN1), a, k), F.Greater(a, F.C1))),
                F.Indeterminate); // $$;
        //
        return function;
      } else if (dist.isAST3()) {
        IExpr k = dist.arg1();
        IExpr a = dist.arg2();
        IExpr m = dist.arg3();
        //
        IExpr function =
            // [$ Piecewise({{k/(-1 + a) + m, a > 1}}, Indeterminate) $]
            F.Piecewise(F.list(F.list(F.Plus(F.Times(F.Power(F.Plus(F.CN1, a), F.CN1), k), m),
                F.Greater(a, F.C1))), F.Indeterminate); // $$;
        //
        return function;
      } else if (dist.argSize() == 4) {
        IExpr k = dist.arg1();
        IExpr a = dist.arg2();
        IExpr g = dist.arg3();
        IExpr m = dist.arg4();
        //
        IExpr function =
            // [$ Piecewise({{m + (k*Gamma(a - g)*Gamma(1 + g))/Gamma(a), a > g}}, Indeterminate) $]
            F.Piecewise(
                F.list(F.list(F.Plus(m, F.Times(k, F.Power(F.Gamma(a), F.CN1),
                    F.Gamma(F.Subtract(a, g)), F.Gamma(F.Plus(F.C1, g)))), F.Greater(a, g))),
                F.Indeterminate); // $$;
        //
        return function;
      }
      return F.NIL;
    }

    @Override
    public IExpr median(IAST dist) {
      if (dist.isAST2()) {
        IExpr k = dist.arg1();
        IExpr a = dist.arg2();
        //
        IExpr function =
            // [$ 2^(1/a)*k $]
            F.Times(F.Power(F.C2, F.Power(a, F.CN1)), k); // $$;
        //
        return function;
      } else if (dist.isAST3()) {
        IExpr k = dist.arg1();
        IExpr a = dist.arg2();
        IExpr m = dist.arg3();
        //
        IExpr function =
            // [$ (-1 + 2^(1/a))*k + m $]
            F.Plus(F.Times(F.Plus(F.CN1, F.Power(F.C2, F.Power(a, F.CN1))), k), m); // $$;
        //
        return function;
      } else if (dist.argSize() == 4) {
        IExpr k = dist.arg1();
        IExpr a = dist.arg2();
        IExpr g = dist.arg3();
        IExpr m = dist.arg4();
        //
        IExpr function =
            // [$ (-1 + 2^(1/a))^g*k + m $]
            F.Plus(F.Times(F.Power(F.Plus(F.CN1, F.Power(F.C2, F.Power(a, F.CN1))), g), k), m); // $$;
        //
        return function;
      }
      return F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr x, EvalEngine engine) {
      if (dist.isAST2()) {
        //
        IExpr k = dist.arg1();
        IExpr a = dist.arg2();

        IExpr function =
            // [$ Piecewise({{a*k^a*#^(-1 - a), # >= k}}, 0) & $]
            F.Function(F.Piecewise(
                F.list(F.list(F.Times(a, F.Power(k, a), F.Power(F.Slot1, F.Subtract(F.CN1, a))),
                    F.GreaterEqual(F.Slot1, k))),
                F.C0)); // $$;
        return callFunction(function, x);
      } else if (dist.isAST3()) {
        //
        IExpr k = dist.arg1();
        IExpr a = dist.arg2();
        IExpr m = dist.arg3();
        IExpr function =
            // [$ Piecewise({{(a*((k-m+#)/k)^(-1-a))/k, #>=m}}, 0) & $]
            F.Function(F.Piecewise(F.list(F.list(F.Times(a, F.Power(k, F.CN1), F.Power(
                F.Times(F.Power(k, F.CN1), F.Plus(k, F.Negate(m), F.Slot1)), F.Subtract(F.CN1, a))),
                F.GreaterEqual(F.Slot1, m))), F.C0)); // $$;
        return callFunction(function, x);
      } else if (dist.argSize() == 4) {
        //
        IExpr k = dist.arg1();
        IExpr a = dist.arg2();
        IExpr g = dist.arg3();
        IExpr m = dist.arg4();
        IExpr function =
            // [$ Piecewise({{(a*(-m+#)^(-1+1/g)*(1+(k/(-m+#))^(-g^(-1)))^(-1-a))/(k^g^(-1)*g),
            // #>=m}}, 0) & $]
            F.Function(
                F.Piecewise(
                    F.list(
                        F.list(
                            F.Times(
                                a, F.Power(F.Times(F.Power(k, F.Power(g, F.CN1)),
                                    g), F.CN1),
                                F.Power(F.Plus(F
                                    .Negate(m), F.Slot1), F.Plus(F.CN1,
                                        F.Power(g, F.CN1))),
                                F.Power(F.Plus(F.C1,
                                    F.Power(
                                        F.Times(k, F.Power(F.Plus(F.Negate(m), F.Slot1), F.CN1)),
                                        F.Negate(F.Power(g, F.CN1)))),
                                    F.Subtract(F.CN1, a))),
                            F.GreaterEqual(F.Slot1, m))),
                    F.C0)); // $$;
        return callFunction(function, x);
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

    @Override
    public IExpr skewness(IAST dist) {//
      if (dist.isAST2() || dist.isAST3()) {
        // IExpr k = dist.arg1();
        IExpr a = dist.arg2();

        IExpr function =
            // [$ Piecewise({{(2*Sqrt[(-2 + a)/a]*(1 + a))/(-3 + a), a > 3}}, Indeterminate) $]
            F.Piecewise(F.list(F.list(
                F.Times(F.C2, F.Power(F.Plus(F.CN3, a), F.CN1),
                    F.Sqrt(F.Times(F.Power(a, F.CN1), F.Plus(F.CN2, a))), F.Plus(F.C1, a)),
                F.Greater(a, F.C3))), F.Indeterminate); // $$;
        return function;
      } else if (dist.argSize() == 4) {
        IExpr k = dist.arg1();
        IExpr a = dist.arg2();
        IExpr g = dist.arg3();
        // IExpr m = dist.arg4();
        //
        IExpr function =
            // [$ Piecewise({{(k^3*(2*Gamma(a - g)^3*Gamma(1 + g)^3 - 3*Gamma(a)*Gamma(a -
            // 2*g)*Gamma(a - g)*Gamma(1 + g)*Gamma(1 + 2*g) + Gamma(a)^2*Gamma(a - 3*g)*Gamma(1 +
            // 3*g)))/(k^2*((-Gamma(a - g)^2)*Gamma(1 + g)^2+Gamma(a)*Gamma(a - 2*g)*Gamma(1 +
            // 2*g)))^(3/2), a > 3*g}}, Indeterminate) $]
            F.Piecewise(
                F.list(F.list(
                    F.Times(
                        F.Power(k, F.C3), F
                            .Power(
                                F.Times(F.Sqr(k),
                                    F.Plus(
                                        F.Times(F.CN1, F.Sqr(F.Gamma(F.Subtract(a, g))),
                                            F.Sqr(F.Gamma(F.Plus(F.C1, g)))),
                                        F.Times(F.Gamma(a), F.Gamma(F.Plus(a, F.Times(F.CN2, g))),
                                            F.Gamma(F.Plus(F.C1, F.Times(F.C2, g)))))),
                                F.QQ(-3L, 2L)),
                        F.Plus(
                            F.Times(F.C2, F.Power(F.Gamma(F.Subtract(a, g)), F.C3),
                                F.Power(F.Gamma(F.Plus(F.C1, g)), F.C3)),
                            F.Times(F.CN3, F.Gamma(a), F.Gamma(F.Plus(a, F.Times(F.CN2, g))),
                                F.Gamma(F.Subtract(a, g)), F.Gamma(F.Plus(F.C1, g)),
                                F.Gamma(F.Plus(F.C1, F.Times(F.C2, g)))),
                            F.Times(F.Sqr(F.Gamma(a)), F.Gamma(F.Plus(a, F.Times(F.CN3, g))),
                                F.Gamma(F.Plus(F.C1, F.Times(F.C3, g)))))),
                    F.Greater(a, F.Times(F.C3, g)))),
                F.Indeterminate); // $$;
        //
        return function;
      }
      return F.NIL;
    }

    @Override
    public IExpr variance(IAST dist) {
      if (dist.isAST2() || dist.isAST3()) {
        IExpr k = dist.arg1();
        IExpr a = dist.arg2();
        //
        IExpr function =
            // [$ Piecewise({{(a*k^2)/((-2 + a)*(-1 + a)^2), a > 2}}, Indeterminate) $]
            F.Piecewise(F.list(
                F.list(F.Times(F.Power(F.Times(F.Plus(F.CN2, a), F.Sqr(F.Plus(F.CN1, a))), F.CN1),
                    a, F.Sqr(k)), F.Greater(a, F.C2))),
                F.Indeterminate); // $$;
        return function;
      } else if (dist.argSize() == 4) {
        IExpr k = dist.arg1();
        IExpr a = dist.arg2();
        IExpr g = dist.arg3();
        //
        IExpr function =
            // [$ Piecewise({{(k^2*((-Gamma(a - g)^2)*Gamma(1 + g)^2 + Gamma(a)*Gamma(a - 2*g)*
            // Gamma(1 + 2*g)))/Gamma(a)^2, a > 2*g}}, Indeterminate) $]
            F.Piecewise(F.list(F.list(F.Times(F.Sqr(k), F.Power(F.Gamma(a), F.CN2), F.Plus(
                F.Times(F.CN1, F.Sqr(F.Gamma(F.Subtract(a, g))), F.Sqr(F.Gamma(F.Plus(F.C1, g)))),
                F.Times(F.Gamma(a), F.Gamma(F.Plus(a, F.Times(F.CN2, g))),
                    F.Gamma(F.Plus(F.C1, F.Times(F.C2, g)))))),
                F.Greater(a, F.Times(F.C2, g)))), F.Indeterminate); // $$;
        return function;
      }
      return F.NIL;
    }


  }


  private static final class StudentTDistribution extends AbstractEvaluator implements ICDF,
      IContinuousDistribution, IPDF, IStatistics, IRandomVariate, IGeneratingFunction {

    @Override
    public IExpr cf(IAST dist, IExpr t, EvalEngine engine) {
      // the moment generating function of the student t distribution does not exist
      if (dist.isAST1()) {
        IExpr v = dist.arg1();
        return studentTCharacteristic(v, F.Abs(t));
      }
      if (dist.isAST3()) {
        IExpr m = dist.arg1();
        IExpr s = dist.arg2();
        IExpr v = dist.arg3();
        return F.Times(F.Exp(F.Times(F.CI, m, t)), studentTCharacteristic(v, F.Abs(F.Times(s, t))));
      }
      return F.NIL;
    }

    /**
     * <code>(Sqrt(v)*a)^(v/2) * BesselK(v/2, Sqrt(v)*a) / (Gamma(v/2) * 2^(v/2-1))</code> with
     * <code>a == Abs(t)</code>.
     */
    private static IExpr studentTCharacteristic(IExpr v, IExpr absT) {
      IExpr halfV = F.Divide(v, F.C2);
      IExpr sqrtVAbsT = F.Times(F.Sqrt(v), absT);
      return F.Times(F.Power(sqrtVAbsT, halfV), F.BesselK(halfV, sqrtVAbsT),
          F.Power(F.C2, F.Subtract(F.C1, halfV)), F.Power(F.Gamma(halfV), F.CN1));
    }

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST1()) {
        IExpr n = dist.arg1();
        if (!engine.isArbitraryMode() && //
            (n.isNumericArgument(true) || k.isNumericArgument(true))) {
          double nDouble = n.evalfNaN();
          double kDouble = k.evalfNaN();
          if (!Double.isNaN(nDouble) && !Double.isNaN(kDouble)) {
            try {
              return F.num(new org.hipparchus.distribution.continuous.TDistribution(nDouble) //
                  .cumulativeProbability(kDouble));
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
              //
            }
          }
        }
        //
        IExpr function =
            // [$ Piecewise({{(1/2)*BetaRegularized(n/(#^2 + n), n/2, 1/2), # <= 0}}, (1/2)*(1 +
            // BetaRegularized(#^2/(#^2 + n), 1/2, n/2))) & $]
            F.Function(F.Piecewise(
                F.list(F.list(F.Times(F.C1D2,
                    F.BetaRegularized(F.Times(n, F.Power(F.Plus(F.Sqr(F.Slot1), n), F.CN1)),
                        F.Times(F.C1D2, n), F.C1D2)),
                    F.LessEqual(F.Slot1, F.C0))),
                F.Times(F.C1D2,
                    F.Plus(F.C1,
                        F.BetaRegularized(
                            F.Times(F.Power(F.Plus(F.Sqr(F.Slot1), n), F.CN1), F.Sqr(F.Slot1)),
                            F.C1D2, F.Times(F.C1D2, n)))))); // $$;
        return callFunction(function, k);
      } else if (dist.isAST3()) {
        IExpr m = dist.arg1();
        IExpr s = dist.arg2();
        IExpr v = dist.arg3();
        if (!engine.isArbitraryMode() && //
            (m.isNumericArgument(true) || s.isNumericArgument(true) || v.isNumericArgument(true)
                || k.isNumericArgument(true))) {
          double vDouble = v.evalfNaN();
          double kDouble = k.evalfNaN();
          double mDouble = m.evalfNaN();
          double sDouble = s.evalfNaN();
          if (!Double.isNaN(vDouble) && !Double.isNaN(kDouble) && !Double.isNaN(mDouble)
              && !Double.isNaN(sDouble)) {
            try {
              return F.num(new org.hipparchus.distribution.continuous.TDistribution(vDouble) //
                  .cumulativeProbability((kDouble - mDouble) / sDouble));
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
            }
          }
        }
        IExpr z = F.Times(F.Subtract(F.Slot1, m), F.Power(s, F.CN1));
        IExpr zSq = F.Sqr(z);
        IExpr frac1 = F.Times(v, F.Power(F.Plus(zSq, v), F.CN1));
        IExpr frac2 = F.Times(zSq, F.Power(F.Plus(zSq, v), F.CN1));

        IExpr function = F.Function(F.Piecewise(
            F.list(F.list(F.Times(F.C1D2, F.BetaRegularized(frac1, F.Times(F.C1D2, v), F.C1D2)),
                F.LessEqual(z, F.C0))),
            F.Times(F.C1D2, F.Plus(F.C1, F.BetaRegularized(frac2, F.C1D2, F.Times(F.C1D2, v))))));
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // 1 or 3 args
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    @Override
    public IExpr inverseCDF(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST1()) {
        IExpr n = dist.arg1();
        IExpr function =
            // [$ ( ConditionalExpression(Piecewise({{(-Sqrt(n))*Sqrt(-1 +
            // 1/InverseBetaRegularized(2*#,
            // n/2, 1/2)), 0 < # < 1/2}, {0, # == 1/2}, {Sqrt(n)*Sqrt(-1 +
            // 1/InverseBetaRegularized(2*(1 -
            // #), n/2, 1/2)), 1/2 < # < 1}, {-Infinity, # <= 0}}, Infinity), 0 <= # <= 1)& ) $]
            F.Function(
                F.ConditionalExpression(
                    F.Piecewise(F.List(
                        F.list(
                            F.Times(F.CN1, F.Sqrt(n),
                                F.Sqrt(
                                    F.Plus(F.CN1,
                                        F.Power(
                                            F.InverseBetaRegularized(F.Times(F.C2, F.Slot1),
                                                F.Times(F.C1D2, n), F.C1D2),
                                            F.CN1)))),
                            F.Less(F.C0, F.Slot1, F.C1D2)),
                        F.list(F.C0, F.Equal(F.Slot1, F.C1D2)),
                        F.list(
                            F.Times(F.Sqrt(n),
                                F.Sqrt(F.Plus(F.CN1,
                                    F.Power(F.InverseBetaRegularized(
                                        F.Times(F.C2, F.Subtract(F.C1, F.Slot1)),
                                        F.Times(F.C1D2, n), F.C1D2), F.CN1)))),
                            F.Less(F.C1D2, F.Slot1, F.C1)),
                        F.list(F.Negate(F.oo), F.LessEqual(F.Slot1, F.C0))), F.oo),
                    F.LessEqual(F.C0, F.Slot1, F.C1))); // $$;
        return callFunction(function, k);
      } else if (dist.isAST3()) {
        IExpr m = dist.arg1();
        IExpr s = dist.arg2();
        IExpr v = dist.arg3();
        IExpr function =
            F.Function(F.ConditionalExpression(
                F.Piecewise(F.List(
                    F.list(F.Plus(m,
                        F.Times(F.CN1, s, F.Sqrt(v), F.Sqrt(F.Plus(F.CN1,
                            F.Power(F.InverseBetaRegularized(F.Times(F.C2, F.Slot1),
                                F.Times(F.C1D2, v), F.C1D2), F.CN1))))),
                        F.Less(F.C0, F.Slot1, F.C1D2)),
                    F.list(m, F.Equal(F.Slot1,
                        F.C1D2)),
                    F.list(
                        F.Plus(m,
                            F.Times(s, F.Sqrt(v),
                                F.Sqrt(F.Plus(F.CN1,
                                    F.Power(F.InverseBetaRegularized(
                                        F.Times(F.C2, F.Subtract(F.C1, F.Slot1)),
                                        F.Times(F.C1D2, v), F.C1D2), F.CN1))))),
                        F.Less(F.C1D2, F.Slot1, F.C1)),
                    F.list(F.Negate(F.oo), F.LessEqual(F.Slot1, F.C0))), F.oo),
                F.LessEqual(F.C0, F.Slot1, F.C1)));
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr mean(IAST dist) {
      if (dist.isAST1()) {
        IExpr arg1 = dist.arg1();
        if (EvalEngine.get().isDoubleMode() || arg1.isNumericArgument(true)) {
          double arg1Double = arg1.evalfNaN();
          if (!Double.isNaN(arg1Double)) {
            return F.num(new org.hipparchus.distribution.continuous.TDistribution(arg1Double)
                .getNumericalMean());
          }
        }
        // (v) -> Piecewise({{0, v > 1}}, Indeterminate)
        return F.Piecewise(F.list(F.list(F.C0, F.Greater(dist.arg1(), F.C1))), S.Indeterminate);
      }
      if (dist.isAST3()) {
        // (m,s,v) -> Piecewise({{m, v > 1}}, Indeterminate)
        return F.Piecewise(F.list(F.list(dist.arg1(), F.Greater(dist.arg3(), F.C1))),
            S.Indeterminate);
      }
      return F.NIL;
    }

    @Override
    public IExpr median(IAST dist) {
      if (dist.isAST1()) {
        return F.C0;
      }
      if (dist.isAST3()) {
        // (m,s,v) -> m
        return dist.arg1();
      }
      return F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST1()) {
        IExpr n = dist.arg1();
        //
        if (!engine.isArbitraryMode() && //
            (n.isNumericArgument(true) || k.isNumericArgument(true))) {
          double nDouble = n.evalfNaN();
          double kDouble = k.evalfNaN();
          if (!Double.isNaN(nDouble) && !Double.isNaN(kDouble)) {
            try {
              return F.num(new org.hipparchus.distribution.continuous.TDistribution(nDouble) //
                  .density(kDouble));
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
              //
            }
          }
        }
        IExpr function =
            // [$ (n/(#^2 + n))^((1 + n)/2)/(Sqrt(n)*Beta(n/2, 1/2)) & $]
            F.Function(F.Times(
                F.Power(F.Times(n, F.Power(F.Plus(F.Sqr(F.Slot1), n), F.CN1)),
                    F.Times(F.C1D2, F.Plus(F.C1, n))),
                F.Power(F.Times(F.Sqrt(n), F.Beta(F.Times(F.C1D2, n), F.C1D2)), F.CN1))); // $$;
        return callFunction(function, k);
      } else if (dist.isAST3()) {
        IExpr m = dist.arg1();
        IExpr s = dist.arg2();
        IExpr v = dist.arg3();
        if (!engine.isArbitraryMode() && //
            (m.isNumericArgument(true) || s.isNumericArgument(true) || v.isNumericArgument(true)
                || k.isNumericArgument(true))) {
          double sDouble = s.evalfNaN();
          double vDouble = v.evalfNaN();
          double kDouble = k.evalfNaN();
          double mDouble = m.evalfNaN();
          if (!Double.isNaN(sDouble) && !Double.isNaN(vDouble) && !Double.isNaN(kDouble)
              && !Double.isNaN(mDouble)) {
            try {
              return F.num((1.0 / sDouble)
                  * new org.hipparchus.distribution.continuous.TDistribution(vDouble) //
                      .density((kDouble - mDouble) / sDouble));
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
            }
          }
        }
        IExpr z = F.Times(F.Subtract(F.Slot1, m), F.Power(s, F.CN1));
        IExpr function = F.Function(F.Times(F.Power(s, F.CN1),
            F.Power(F.Times(v, F.Power(F.Plus(F.Sqr(z), v), F.CN1)),
                F.Times(F.C1D2, F.Plus(F.C1, v))),
            F.Power(F.Times(F.Sqrt(v), F.Beta(F.Times(F.C1D2, v), F.C1D2)), F.CN1)));
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr randomVariate(Random random, IAST dist, int size) {
      if (dist.isAST1()) {
        double n = dist.arg1().evalfNaN();
        if (Double.isNaN(n)) {
          return F.NIL;
        }
        RandomDataGenerator rdg = new RandomDataGenerator();
        double[] vector =
            rdg.nextDeviates(new org.hipparchus.distribution.continuous.TDistribution(n), size);
        return new ASTRealVector(vector, false);
      } else if (dist.isAST3()) {
        double m = dist.arg1().evalfNaN();
        double s = dist.arg2().evalfNaN();
        double v = dist.arg3().evalfNaN();
        if (Double.isNaN(m) || Double.isNaN(s) || Double.isNaN(v)) {
          return F.NIL;
        }
        RandomDataGenerator rdg = new RandomDataGenerator();
        double[] vector =
            rdg.nextDeviates(new org.hipparchus.distribution.continuous.TDistribution(v), size);
        for (int i = 0; i < vector.length; i++) {
          vector[i] = m + s * vector[i];
        }
        return new ASTRealVector(vector, false);
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

    @Override
    public IExpr skewness(IAST dist) {
      if (dist.isAST1()) {
        IExpr n = dist.arg1();
        return F.Piecewise(F.list(F.list(F.C0, F.Greater(n, F.C3))), S.Indeterminate);
      } else if (dist.isAST3()) {
        IExpr v = dist.arg3();
        return F.Piecewise(F.list(F.list(F.C0, F.Greater(v, F.C3))), S.Indeterminate);
      }
      return F.NIL;
    }

    @Override
    public IExpr variance(IAST dist) {
      if (dist.isAST1()) {
        IExpr n = dist.arg1();
        if (EvalEngine.get().isDoubleMode() || n.isNumericArgument(true)) {
          double nDouble = n.evalfNaN();
          if (!Double.isNaN(nDouble)) {
            return F.num(new org.hipparchus.distribution.continuous.TDistribution(nDouble)
                .getNumericalVariance());
          }
        }
        return F.Piecewise(F.list(F.list(F.Divide(n, F.Plus(F.CN2, n)), F.Greater(n, F.C2))),
            S.Indeterminate);
      } else if (dist.isAST3()) {
        IExpr s = dist.arg2();
        IExpr v = dist.arg3();
        if (EvalEngine.get().isDoubleMode()
            || (s.isNumericArgument(true) && v.isNumericArgument(true))) {
          double sDouble = s.evalfNaN();
          double vDouble = v.evalfNaN();
          if (!Double.isNaN(sDouble) && !Double.isNaN(vDouble)) {
            return F.num(sDouble * sDouble
                * new org.hipparchus.distribution.continuous.TDistribution(vDouble)
                    .getNumericalVariance());
          }
        }
        return F.Piecewise(
            F.list(F.list(F.Times(F.Sqr(s), F.Divide(v, F.Plus(F.CN2, v))), F.Greater(v, F.C2))),
            S.Indeterminate);
      }
      return F.NIL;
    }
  }


  /**
   *
   *
   * <pre>
   * <code>UniformDistribution({min, max})
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns a uniform distribution.
   *
   * </blockquote>
   *
   * <pre>
   * <code>UniformDistribution( )
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns a uniform distribution with <code>min = 0</code> and <code>max = 1</code>.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Uniform_distribution_(continuous)">Wikipedia -
   * Uniform distribution (continous)1</a>
   * </ul>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="CDF.md">CDF</a>, <a href="Mean.md">Mean</a>, <a href="Mean.md">Median</a>,
   * <a href="PDF.md">PDF</a>, <a href="Quantile.md">Quantile</a>,
   * <a href="StandardDeviation.md">StandardDeviation</a>, <a href="Variance.md">Variance</a>
   */
  private static final class UniformDistribution extends AbstractEvaluator implements
      IContinuousDistribution, IStatistics, ICDF, IPDF, IRandomVariate, IGeneratingFunction {

    @Override
    public IExpr mgf(IAST dist, IExpr t, EvalEngine engine) {
      IExpr[] minMax = minmax(dist);
      if (minMax != null) {
        IExpr a = minMax[0];
        IExpr b = minMax[1];
        // (E^(b*t) - E^(a*t))/(t*(b - a))
        return F.Divide(F.Subtract(F.Exp(F.Times(b, t)), F.Exp(F.Times(a, t))),
            F.Times(t, F.Subtract(b, a)));
      }
      return F.NIL;
    }

    @Override
    public IExpr moment(IAST dist, IExpr n) {
      IExpr[] minMax = minmax(dist);
      if (minMax != null && n.isInteger() && !n.isNegative()) {
        IExpr a = minMax[0];
        IExpr b = minMax[1];
        IExpr exponent = F.Plus(n, F.C1);
        // (b^(n + 1) - a^(n + 1))/((n + 1)*(b - a))
        return F.Divide(F.Subtract(F.Power(b, exponent), F.Power(a, exponent)),
            F.Times(exponent, F.Subtract(b, a)));
      }
      return F.NIL;
    }

    @Override
    public IExpr parameterAssumptions(IAST dist) {
      IExpr[] minMax = minmax(dist);
      if (minMax != null) {
        return F.LessEqual(minMax[0], minMax[1]);
      }
      return F.NIL;
    }

    @Override
    public IExpr survivalFunction(IAST dist, IExpr k, EvalEngine engine) {
      IExpr[] minMax = minmax(dist);
      if (minMax != null) {
        IExpr a = minMax[0];
        IExpr b = minMax[1];
        // Piecewise({{(b - #)/(b - a), a <= # <= b}, {0, # > b}}, 1) &
        return callFunction(F.Function(F.Piecewise(F.list(//
            F.list(F.Times(F.Power(F.Subtract(b, a), F.CN1), F.Subtract(b, F.Slot1)),
                F.LessEqual(a, F.Slot1, b)), //
            F.list(F.C0, F.Greater(F.Slot1, b))), F.C1)), k);
      }
      return F.NIL;
    }

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      IExpr[] minMax = minmax(dist);
      if (minMax != null) {
        IExpr a = minMax[0];
        IExpr b = minMax[1];
        if (!engine.isArbitraryMode() && //
            (a.isNumericArgument(true) || b.isNumericArgument(true) || k.isNumericArgument(true))) {
          double aDouble = a.evalfNaN();
          double bDouble = b.evalfNaN();
          double kDouble = k.evalfNaN();
          if (!Double.isNaN(aDouble) && !Double.isNaN(bDouble) && !Double.isNaN(kDouble)) {
            try {
              return F
                  .num(new org.hipparchus.distribution.continuous.UniformRealDistribution(aDouble,
                      bDouble) //
                          .cumulativeProbability(kDouble));
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
              //
            }
          }
        }
        IExpr function =
            // [$ Piecewise({{(# - a)/(b - a), a <= # <= b}, {1, # > b}}, 0) & $]
            F.Function(
                F.Piecewise(F.list(
                    F.list(F.Times(F.Power(F.Plus(F.Negate(a), b), F.CN1),
                        F.Plus(F.Negate(a), F.Slot1)), F.LessEqual(a, F.Slot1, b)),
                    F.list(F.C1, F.Greater(F.Slot1, b))), F.C0)); // $$;
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST0()) {
        return F.UniformDistribution(F.List(F.C0, F.C1));
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_1;
    }

    @Override
    public IExpr inverseCDF(IAST dist, IExpr k, EvalEngine engine) {
      IExpr[] minMax = minmax(dist);
      if (minMax != null) {
        IExpr a = minMax[0];
        IExpr b = minMax[1];
        if (!engine.isArbitraryMode() && //
            (a.isNumericArgument(true) || b.isNumericArgument(true) || k.isNumericArgument(true))) {
          double aDouble = a.evalfNaN();
          double bDouble = b.evalfNaN();
          double kDouble = k.evalfNaN();
          if (!Double.isNaN(aDouble) && !Double.isNaN(bDouble) && !Double.isNaN(kDouble)) {
            try {
              return F
                  .num(new org.hipparchus.distribution.continuous.UniformRealDistribution(aDouble,
                      bDouble) //
                          .inverseCumulativeProbability(kDouble));
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
              //
            }
          }
        }
        IExpr function =
            // [$ ( ConditionalExpression(Piecewise({{(1 - #)*a + #*b, 0 < # < 1}, {a, # <= 0}}, b),
            // 0 <= #
            // <= 1)& ) $]
            F.Function(
                F.ConditionalExpression(F.Piecewise(
                    F.list(
                        F.list(F.Plus(F.Times(F.Subtract(F.C1, F.Slot1), a), F.Times(F.Slot1, b)),
                            F.Less(F.C0, F.Slot1, F.C1)),
                        F.list(a, F.LessEqual(F.Slot1, F.C0))),
                    b), F.LessEqual(F.C0, F.Slot1, F.C1))); // $$;
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
        return
        // [$ (l + r)/2 $]
        F.Times(F.C1D2, F.Plus(l, r)); // $$;
      }
      return F.NIL;
    }

    public IExpr[] minmax(IAST dist) {
      if (dist.size() == 2 && dist.arg1().isList()) {
        IAST list = (IAST) dist.arg1();
        if (list.isAST2()) {
          IExpr l = list.arg1();
          IExpr r = list.arg2();
          return new IExpr[] {l, r};
        }
      } else if (dist.isAST0()) {
        return new IExpr[] {F.C0, F.C1};
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
            // [$ Piecewise({{1/(b - a), a <= # <= b}}, 0)& $]
            F.Function(F.Piecewise(
                F.list(F.list(F.Power(F.Plus(F.Negate(a), b), F.CN1), F.LessEqual(a, F.Slot1, b))),
                F.C0)); // $$;
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr randomVariate(Random random, IAST dist, int size) {
      IExpr[] minMax = minmax(dist);
      if (minMax != null) {
        // see exception handling in RandonmVariate() function
        double min = minMax[0].evalfNaN();
        double max = minMax[1].evalfNaN();
        if (Double.isNaN(min) || Double.isNaN(max)) {
          return F.NIL;
        }
        RandomDataGenerator rdg = new RandomDataGenerator();
        double[] vector = rdg.nextDeviates(
            new org.hipparchus.distribution.continuous.UniformRealDistribution(min, max), size);
        return new ASTRealVector(vector, false);
      }
      return F.NIL;
    }

    @Override
    public IExpr skewness(IAST dist) {
      IExpr[] minMax = minmax(dist);
      if (minMax != null) {
        // IExpr n = minMax[0];
        // IExpr m = minMax[1];
        return F.C0;
      }
      return F.NIL;
    }

    @Override
    public IExpr variance(IAST dist) {
      IExpr[] minMax = minmax(dist);
      if (minMax != null) {
        IExpr l = minMax[0];
        IExpr r = minMax[1];
        return
        // [$ (1/12)*(l - r)^2 $]
        F.Times(F.QQ(1L, 12L), F.Sqr(F.Subtract(l, r))); // $$;
      }

      return F.NIL;
    }
  }


  /**
   *
   *
   * <pre>
   * WeibullDistribution(a, b)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns a Weibull distribution.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Weibull_distribution">Wikipedia - Weibull
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
  /**
   * <code>VonMisesDistribution(m, k)</code> - the circular normal distribution with mean direction
   * <code>m</code> and concentration <code>k</code>, supported on
   * <code>m-Pi &lt;= x &lt;= m+Pi</code>.
   *
   * <p>
   * Neither the CDF nor the variance have an elementary closed form - the CDF is evaluated by
   * quadrature of the density once all parameters are numbers, and the variance is left to the
   * numeric fallback of {@link S#Variance}.
   */
  private static final class VonMisesDistribution extends AbstractEvaluator
      implements ICDF, IContinuousDistribution, IPDF, IStatistics, IRandomVariate {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr m = dist.arg1();
        IExpr n = dist.arg2();
        // Piecewise({{E^(n*Cos(k-m))/(2*Pi*BesselI(0,n)), m-Pi <= k <= m+Pi}}, 0)
        return F.Piecewise(F.list(F.list(//
            F.Divide(F.Exp(F.Times(n, F.Cos(F.Subtract(k, m)))),
                F.Times(F.C2, F.Pi, F.BesselI(F.C0, n))),
            F.LessEqual(F.Subtract(m, F.Pi), k, F.Plus(m, F.Pi)))), F.C0);
      }
      return F.NIL;
    }

    /**
     * <code>Integrate(E^(k*(Cos(u)-1)), {u, from, to})</code> by Simpson's rule.
     *
     * <p>
     * The <code>E^-k</code> factor keeps the integrand in <code>(0,1]</code> for every
     * concentration; it cancels in {@link #cdfDouble(double, double, double)}. The density is
     * peaked around <code>0</code> with a width of about <code>1/Sqrt(k)</code>, so the number of
     * panels grows with <code>Sqrt(k)</code>.
     */
    private static double shiftedIntegral(double concentration, double from, double to) {
      int panels = (int) Math.max(1000, 400.0 * Math.sqrt(concentration));
      panels += panels & 1; // Simpson needs an even number of panels
      double h = (to - from) / panels;
      double sum = 0.0;
      for (int i = 0; i <= panels; i++) {
        double weight = (i == 0 || i == panels) ? 1.0 : (((i & 1) == 1) ? 4.0 : 2.0);
        sum += weight * Math.exp(concentration * (Math.cos(from + i * h) - 1.0));
      }
      return sum * h / 3.0;
    }

    /** @return the CDF of <code>VonMisesDistribution(m, concentration)</code> at <code>x</code> */
    private static double cdfDouble(double m, double concentration, double x) {
      double z = x - m;
      if (z <= -Math.PI) {
        return 0.0;
      }
      if (z >= Math.PI) {
        return 1.0;
      }
      return shiftedIntegral(concentration, -Math.PI, z)
          / shiftedIntegral(concentration, -Math.PI, Math.PI);
    }

    /**
     * @return the three parameters as doubles or <code>null</code> if any of them isn't a real
     *         number or the concentration isn't positive
     */
    private static double[] doubleParameters(IAST dist, IExpr k) {
      if (!dist.isAST2()) {
        return null;
      }
      double m = dist.arg1().evalfNaN();
      double concentration = dist.arg2().evalfNaN();
      double x = k.evalfNaN();
      if (Double.isNaN(m) || Double.isNaN(concentration) || Double.isNaN(x)
          || concentration <= 0.0) {
        return null;
      }
      return new double[] {m, concentration, x};
    }

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2() && k.isNumericFunction(true) && dist.arg1().isNumericFunction(true)
          && dist.arg2().isNumericFunction(true)) {
        // no elementary closed form - integrate the density
        double[] parameters = doubleParameters(dist, k);
        if (parameters != null) {
          return F.num(cdfDouble(parameters[0], parameters[1], parameters[2]));
        }
      }
      return F.NIL;
    }

    @Override
    public IExpr inverseCDF(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2() && k.isNumericFunction(true) && dist.arg1().isNumericFunction(true)
          && dist.arg2().isNumericFunction(true)) {
        double[] parameters = doubleParameters(dist, k);
        if (parameters == null) {
          return F.NIL;
        }
        double m = parameters[0];
        double concentration = parameters[1];
        double probability = parameters[2];
        if (probability < 0.0 || probability > 1.0) {
          return F.NIL;
        }
        if (probability == 0.0) {
          return F.num(m - Math.PI);
        }
        if (probability == 1.0) {
          return F.num(m + Math.PI);
        }
        // the CDF is strictly increasing on the support, so bisection converges
        double total = shiftedIntegral(concentration, -Math.PI, Math.PI);
        double low = -Math.PI;
        double high = Math.PI;
        for (int i = 0; i < 200 && high - low > 1.0e-15 * (1.0 + Math.abs(low)); i++) {
          double middle = 0.5 * (low + high);
          if (shiftedIntegral(concentration, -Math.PI, middle) / total < probability) {
            low = middle;
          } else {
            high = middle;
          }
        }
        return F.num(m + 0.5 * (low + high));
      }
      return F.NIL;
    }

    @Override
    public IExpr mean(IAST dist) {
      return dist.isAST2() ? dist.arg1() : F.NIL;
    }

    @Override
    public IExpr median(IAST dist) {
      return dist.isAST2() ? dist.arg1() : F.NIL;
    }

    @Override
    public IExpr skewness(IAST dist) {
      // the density is symmetric around the mean direction
      return dist.isAST2() ? F.C0 : F.NIL;
    }

    @Override
    public IExpr variance(IAST dist) {
      // Integrate((x-m)^2*pdf) has no elementary closed form
      return F.NIL;
    }

    @Override
    public IExpr randomVariate(Random random, IAST dist, int size) {
      if (dist.isAST2()) {
        // see exception handling in RandomVariate() function
        double m = dist.arg1().evalfNaN();
        double n = dist.arg2().evalfNaN();
        if (Double.isNaN(m) || Double.isNaN(n) || n <= 0.0) {
          return F.NIL;
        }
        // rejection sampling after Best and Fisher
        final double a = 1.0 + Math.sqrt(1.0 + 4.0 * n * n);
        final double b = (a - Math.sqrt(2.0 * a)) / (2.0 * n);
        final double r = (1.0 + b * b) / (2.0 * b);
        double[] vector = new double[size];
        for (int i = 0; i < size; i++) {
          double f;
          while (true) {
            double z = Math.cos(Math.PI * random.nextDouble());
            f = (1.0 + r * z) / (r + z);
            double c = n * (r - f);
            double u = random.nextDouble();
            if (c * (2.0 - c) > u || Math.log(c / u) + 1.0 - c >= 0.0) {
              break;
            }
          }
          double angle = Math.acos(Math.max(-1.0, Math.min(1.0, f)));
          vector[i] = m + (random.nextDouble() < 0.5 ? -angle : angle);
        }
        return new ASTRealVector(vector, false);
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }


  private static final class WeibullDistribution extends AbstractEvaluator
      implements ICDF, IContinuousDistribution, IPDF, IStatistics, IRandomVariate {

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        //
        if (!engine.isArbitraryMode() && //
            (n.isNumericArgument(true) || m.isNumericArgument(true) || k.isNumericArgument(true))) {
          double nDouble = n.evalfNaN();
          double mDouble = m.evalfNaN();
          double kDouble = k.evalfNaN();
          if (!Double.isNaN(nDouble) && !Double.isNaN(mDouble) && !Double.isNaN(kDouble)) {
            try {
              return F.num(
                  new org.hipparchus.distribution.continuous.WeibullDistribution(nDouble, mDouble) //
                      .cumulativeProbability(kDouble));
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
              //
            }
          }
        }
        IExpr function =
            // [$ Piecewise({{1 - E^(-(#/m)^n),# > 0}}, 0) & $]
            F.Function(F.Piecewise(F.list(F.list(
                F.Subtract(F.C1, F.Exp(F.Negate(F.Power(F.Times(F.Power(m, F.CN1), F.Slot1), n)))),
                F.Greater(F.Slot1, F.C0))), F.C0)); // $$;
        return callFunction(function, k);
      } else if (dist.isAST3()) {
        IExpr n = dist.arg1();
        IExpr scale = dist.arg2();
        IExpr loc = dist.arg3();
        //
        if (!engine.isArbitraryMode() && //
            (n.isNumericArgument(true) || scale.isNumericArgument(true)
                || loc.isNumericArgument(true) || k.isNumericArgument(true))) {
          double nDouble = n.evalfNaN();
          double scaleDouble = scale.evalfNaN();
          double kDouble = k.evalfNaN();
          double locDouble = loc.evalfNaN();
          if (!Double.isNaN(nDouble) && !Double.isNaN(scaleDouble) && !Double.isNaN(kDouble)
              && !Double.isNaN(locDouble)) {
            try {
              // Shift x by the location parameter before calculating CDF
              return F.num(new org.hipparchus.distribution.continuous.WeibullDistribution(nDouble,
                  scaleDouble) //
                      .cumulativeProbability(kDouble - locDouble));
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
              //
            }
          }
        }
        IExpr function =
            // [$ Piecewise({{1 - E^(-((# - loc)/scale)^n),# > loc}}, 0) & $]
            F.Function(
                F.Piecewise(
                    F.list(F.list(
                        F.Subtract(F.C1,
                            F.Exp(F.Negate(F.Power(
                                F.Times(F.Power(scale, F.CN1), F.Subtract(F.Slot1, loc)), n)))),
                        F.Greater(F.Slot1, loc))),
                    F.C0)); // $$;
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // 2 or 3 args
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }

    @Override
    public IExpr inverseCDF(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        if (!engine.isArbitraryMode() && //
            (n.isNumericArgument(true) || m.isNumericArgument(true) || k.isNumericArgument(true))) {
          double nDouble = n.evalfNaN();
          double mDouble = m.evalfNaN();
          double kDouble = k.evalfNaN();
          if (!Double.isNaN(nDouble) && !Double.isNaN(mDouble) && !Double.isNaN(kDouble)) {
            try {
              return F.num(
                  new org.hipparchus.distribution.continuous.WeibullDistribution(nDouble, mDouble) //
                      .inverseCumulativeProbability(kDouble));
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
              //
            }
          }
        }
        IExpr function =
            // [$ ( ConditionalExpression(Piecewise({{m*(-Log(1 - #))^(1/n), 0 < # < 1}, {0, # <=
            // 0}},
            // Infinity), 0 <= # <= 1)& ) $]
            F.Function(
                F.ConditionalExpression(
                    F.Piecewise(
                        F.list(
                            F.list(F.Times(m,
                                F.Power(F.Negate(F.Log(F.Subtract(F.C1, F.Slot1))),
                                    F.Power(n, F.CN1))),
                                F.Less(F.C0, F.Slot1, F.C1)),
                            F.list(F.C0, F.LessEqual(F.Slot1, F.C0))),
                        F.oo),
                    F.LessEqual(F.C0, F.Slot1, F.C1))); // $$;
        return callFunction(function, k);
      } else if (dist.isAST3()) {
        IExpr n = dist.arg1();
        IExpr scale = dist.arg2();
        IExpr loc = dist.arg3();
        if (!engine.isArbitraryMode() && //
            (n.isNumericArgument(true) || scale.isNumericArgument(true)
                || loc.isNumericArgument(true) || k.isNumericArgument(true))) {
          double locDouble = loc.evalfNaN();
          double nDouble = n.evalfNaN();
          double scaleDouble = scale.evalfNaN();
          double kDouble = k.evalfNaN();
          if (!Double.isNaN(locDouble) && !Double.isNaN(nDouble) && !Double.isNaN(scaleDouble)
              && !Double.isNaN(kDouble)) {
            try {
              // Shift InverseCDF result by the location parameter
              return F.num(locDouble
                  + new org.hipparchus.distribution.continuous.WeibullDistribution(nDouble,
                      scaleDouble) //
                          .inverseCumulativeProbability(kDouble));
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
              //
            }
          }
        }
        IExpr function =
            // [$ ( ConditionalExpression(Piecewise({{loc + scale*(-Log(1 - #))^(1/n), 0 < # < 1},
            // {loc,
            // # <= 0}}, Infinity), 0 <= # <= 1)& ) $]
            F.Function(F.ConditionalExpression(
                F.Piecewise(F.list(
                    F.list(F.Plus(loc,
                        F.Times(scale,
                            F.Power(F.Negate(F.Log(F.Subtract(F.C1, F.Slot1))),
                                F.Power(n, F.CN1)))),
                        F.Less(F.C0, F.Slot1, F.C1)),
                    F.list(loc, F.LessEqual(F.Slot1, F.C0))), F.oo),
                F.LessEqual(F.C0, F.Slot1, F.C1))); // $$;
        return callFunction(function, k);
      }
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
        return F.Plus(dist.arg3(),
            F.Times(dist.arg2(), F.Gamma(F.Plus(F.C1, F.Power(dist.arg1(), F.CN1)))));
      }

      return F.NIL;
    }

    @Override
    public IExpr median(IAST dist) {
      if (dist.isAST2()) {
        // (a,b) -> b*Log(2)^(1/a)
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        return F.Times(b, F.Power(F.Log(F.C2), F.Power(a, -1)));
      }

      if (dist.isAST3()) {
        // (a,b,m) -> m + b*Log(2)^(1/a)
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        IExpr m = dist.arg3();
        return F.Plus(m, F.Times(b, F.Power(F.Log(F.C2), F.Power(a, -1))));
      }

      return F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        //
        if (!engine.isArbitraryMode() && //
            (n.isNumericArgument(true) || m.isNumericArgument(true) || k.isNumericArgument(true))) {
          double nDouble = n.evalfNaN();
          double mDouble = m.evalfNaN();
          double kDouble = k.evalfNaN();
          if (!Double.isNaN(nDouble) && !Double.isNaN(mDouble) && !Double.isNaN(kDouble)) {
            try {
              return F.num(
                  new org.hipparchus.distribution.continuous.WeibullDistribution(nDouble, mDouble) //
                      .density(kDouble));
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
              //
            }
          }
        }
        IExpr function =
            // [$ Piecewise({{((#/m)^(-1 + n)*n)/(E^(#/m)^n*m), # > 0}}, 0) & $]
            F.Function(
                F.Piecewise(
                    F.list(
                        F.list(
                            F.Times(
                                F.Power(
                                    F.Times(F.Exp(F.Power(F.Times(F.Power(m, F.CN1), F.Slot1), n)),
                                        m),
                                    F.CN1),
                                n, F.Power(F.Times(F.Power(m, F.CN1), F.Slot1), F.Plus(F.CN1, n))),
                            F.Greater(F.Slot1, F.C0))),
                    F.C0)); // $$;
        return callFunction(function, k);
      } else if (dist.isAST3()) {
        IExpr n = dist.arg1();
        IExpr scale = dist.arg2();
        IExpr loc = dist.arg3();
        //
        if (!engine.isArbitraryMode() && //
            (n.isNumericArgument(true) || scale.isNumericArgument(true)
                || loc.isNumericArgument(true) || k.isNumericArgument(true))) {
          double nDouble = n.evalfNaN();
          double scaleDouble = scale.evalfNaN();
          double kDouble = k.evalfNaN();
          double locDouble = loc.evalfNaN();
          if (!Double.isNaN(nDouble) && !Double.isNaN(scaleDouble) && !Double.isNaN(kDouble)
              && !Double.isNaN(locDouble)) {
            try {
              return F.num(new org.hipparchus.distribution.continuous.WeibullDistribution(nDouble,
                  scaleDouble) //
                      .density(kDouble - locDouble));
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
              //
            }
          }
        }
        IExpr function =
            // [$ Piecewise({{(((# - loc)/scale)^(-1 + n)*n)/(E^((# - loc)/scale)^n*scale), # >
            // loc}},
            // 0) & $]
            F.Function(
                F.Piecewise(
                    F.list(
                        F.list(F.Times(
                            F.Power(F.Times(
                                F.Exp(F.Power(
                                    F.Times(F.Power(scale, F.CN1), F.Subtract(F.Slot1, loc)), n)),
                                scale), F.CN1),
                            n, F.Power(F.Times(F.Power(scale, F.CN1), F.Subtract(F.Slot1, loc)),
                                F.Plus(F.CN1, n))),
                            F.Greater(F.Slot1, loc))),
                    F.C0)); // $$;
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr randomVariate(Random random, IAST dist, int size) {
      if (dist.isAST2()) {
        double n = dist.arg1().evalfNaN();
        double m = dist.arg2().evalfNaN();
        if (Double.isNaN(n) || Double.isNaN(m)) {
          return F.NIL;
        }
        RandomDataGenerator rdg = new RandomDataGenerator();
        double[] vector = rdg.nextDeviates(
            new org.hipparchus.distribution.continuous.WeibullDistribution(n, m), size);
        return new ASTRealVector(vector, false);
      } else if (dist.isAST3()) {
        double n = dist.arg1().evalfNaN();
        double scale = dist.arg2().evalfNaN();
        double loc = dist.arg3().evalfNaN();
        if (Double.isNaN(n) || Double.isNaN(scale) || Double.isNaN(loc)) {
          return F.NIL;
        }
        RandomDataGenerator rdg = new RandomDataGenerator();
        double[] vector = rdg.nextDeviates(
            new org.hipparchus.distribution.continuous.WeibullDistribution(n, scale), size);
        // Shift values by location
        for (int i = 0; i < vector.length; i++) {
          vector[i] += loc;
        }
        return new ASTRealVector(vector, false);
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

    @Override
    public IExpr skewness(IAST dist) {
      if (dist.isAST2() || dist.isAST3()) {
        IExpr n = dist.arg1();
        // Shift does not affect Skewness
        return
        // [$ (2*Gamma(1+1/n)^3 - 3*Gamma(1+1/n)*Gamma(1+2/n) + Gamma(1+3/n))/ (-Gamma(1+1/n)^2 +
        // Gamma(1+2/n))^(3/2) $]
        F.Times(
            F.Power(F.Plus(F.Negate(F.Sqr(F.Gamma(F.Plus(F.C1, F.Power(n, F.CN1))))),
                F.Gamma(F.Plus(F.C1, F.Times(F.C2, F.Power(n, F.CN1))))), F.QQ(-3L, 2L)),
            F.Plus(F.Times(F.C2, F.Power(F.Gamma(F.Plus(F.C1, F.Power(n, F.CN1))), F.C3)),
                F.Times(F.CN3, F.Gamma(F.Plus(F.C1, F.Power(n, F.CN1))),
                    F.Gamma(F.Plus(F.C1, F.Times(F.C2, F.Power(n, F.CN1))))),
                F.Gamma(F.Plus(F.C1, F.Times(F.C3, F.Power(n, F.CN1)))))); // $$;
      }
      return F.NIL;
    }

    @Override
    public IExpr variance(IAST dist) {
      if (dist.isAST2() || dist.isAST3()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        // Shift does not affect Variance
        // m^2*(-Gamma(1 + 1/n)^2 + Gamma(1 + 2/n))
        return F.Times(F.Sqr(m), F.Plus(F.Negate(F.Sqr(F.Gamma(F.Plus(F.C1, F.Power(n, -1))))),
            F.Gamma(F.Plus(F.C1, F.Times(F.C2, F.Power(n, -1))))));
      }
      return F.NIL;
    }
  }


  public static void initialize() {
    Initializer.init();
  }


  /**
   * <code>LaplaceDistribution(mu, beta)</code> - the Laplace (double exponential) distribution.
   */
  private static final class LaplaceDistribution extends AbstractEvaluator
      implements ICDF, IContinuousDistribution, IPDF, IStatistics, IGeneratingFunction {

    @Override
    public IExpr mgf(IAST dist, IExpr t, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr m = dist.arg1();
        IExpr b = dist.arg2();
        // E^(m*t)/(1 - b^2*t^2)
        return F.Divide(F.Exp(F.Times(m, t)), F.Subtract(F.C1, F.Times(F.Sqr(b), F.Sqr(t))));
      }
      return F.NIL;
    }

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        // Piecewise({{E^((# - n)/m)/2, # < n}}, 1 - 1/(2*E^((# - n)/m))) &
        IExpr z = F.Divide(F.Subtract(F.Slot1, n), m);
        return callFunction(
            F.Function(F.Piecewise(F.list(F.list(F.Times(F.C1D2, F.Exp(z)), F.Less(F.Slot1, n))),
                F.Subtract(F.C1, F.Times(F.C1D2, F.Exp(F.Negate(z)))))),
            k);
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
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        // Piecewise({{n + m*Log(2*#), 0 < # < 1/2}, {n - m*Log(2 - 2*#), 1/2 <= # < 1}}, Infinity)
        // &
        return callFunction(F.Function(F.Piecewise(F.list(//
            F.list(F.Plus(n, F.Times(m, F.Log(F.Times(F.C2, F.Slot1)))),
                F.Less(F.C0, F.Slot1, F.C1D2)), //
            F.list(F.Subtract(n, F.Times(m, F.Log(F.Subtract(F.C2, F.Times(F.C2, F.Slot1))))),
                F.And(F.LessEqual(F.C1D2, F.Slot1), F.Less(F.Slot1, F.C1)))),
            F.oo)), k);
      }
      return F.NIL;
    }

    @Override
    public IExpr mean(IAST dist) {
      return dist.isAST2() ? dist.arg1() : F.NIL;
    }

    @Override
    public IExpr median(IAST dist) {
      return dist.isAST2() ? dist.arg1() : F.NIL;
    }

    @Override
    public IExpr parameterAssumptions(IAST dist) {
      return dist.isAST2() ? F.Greater(dist.arg2(), F.C0) : F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        // 1/(2*m*E^(Abs(# - n)/m)) &
        return callFunction(F.Function(
            F.Divide(F.C1, F.Times(F.C2, m, F.Exp(F.Divide(F.Abs(F.Subtract(F.Slot1, n)), m))))),
            k);
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

    @Override
    public IExpr skewness(IAST dist) {
      return dist.isAST2() ? F.C0 : F.NIL;
    }

    @Override
    public IExpr standardDeviation(IAST dist) {
      return dist.isAST2() ? F.Times(F.CSqrt2, dist.arg2()) : F.NIL;
    }

    @Override
    public IExpr variance(IAST dist) {
      // 2*m^2
      return dist.isAST2() ? F.Times(F.C2, F.Sqr(dist.arg2())) : F.NIL;
    }
  }

  /**
   * <code>TriangularDistribution()</code>, <code>TriangularDistribution({min, max})</code> or
   * <code>TriangularDistribution({min, max}, c)</code> - the triangular distribution.
   */
  private static final class TriangularDistribution extends AbstractEvaluator
      implements ICDF, IContinuousDistribution, IPDF, IStatistics {

    /** <code>{min, max, mode}</code> or <code>null</code> if the arguments don't match. */
    private static IExpr[] parameters(IAST dist) {
      if (dist.isAST0()) {
        return new IExpr[] {F.C0, F.C1, F.C1D2};
      }
      if (dist.isAST1() && dist.arg1().isList2()) {
        IAST minMax = (IAST) dist.arg1();
        return new IExpr[] {minMax.arg1(), minMax.arg2(),
            F.Times(F.C1D2, F.Plus(minMax.arg1(), minMax.arg2()))};
      }
      if (dist.isAST2() && dist.arg1().isList2()) {
        IAST minMax = (IAST) dist.arg1();
        return new IExpr[] {minMax.arg1(), minMax.arg2(), dist.arg2()};
      }
      return null;
    }

    /** <code>true</code> for the symmetric forms, where mean/variance have a simpler form. */
    private static boolean isSymmetric(IAST dist) {
      return dist.isAST0() || dist.isAST1();
    }

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      IExpr[] p = parameters(dist);
      if (p != null) {
        IExpr a = p[0];
        IExpr b = p[1];
        IExpr c = p[2];
        // Piecewise({{(# - a)^2/((b - a)*(c - a)), a <= # <= c},
        // {1 - (b - #)^2/((b - a)*(b - c)), c < # <= b}, {1, # > b}}, 0) &
        return callFunction(F.Function(F.Piecewise(F.list(//
            F.list(F.Divide(F.Sqr(F.Subtract(F.Slot1, a)),
                F.Times(F.Subtract(b, a), F.Subtract(c, a))), F.LessEqual(a, F.Slot1, c)), //
            F.list(
                F.Subtract(F.C1,
                    F.Divide(F.Sqr(F.Subtract(b, F.Slot1)),
                        F.Times(F.Subtract(b, a), F.Subtract(b, c)))),
                F.Inequality(c, S.Less, F.Slot1, S.LessEqual, b)), //
            F.list(F.C1, F.Greater(F.Slot1, b))), F.C0)), k);
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_2;
    }

    @Override
    public IExpr inverseCDF(IAST dist, IExpr k, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public IExpr mean(IAST dist) {
      IExpr[] p = parameters(dist);
      if (p != null) {
        if (isSymmetric(dist)) {
          // (a + b)/2
          return F.Times(F.C1D2, F.Plus(p[0], p[1]));
        }
        // (a + b + c)/3
        return F.Divide(F.Plus(p[0], p[1], p[2]), F.C3);
      }
      return F.NIL;
    }

    @Override
    public IExpr median(IAST dist) {
      return F.NIL;
    }

    @Override
    public IExpr parameterAssumptions(IAST dist) {
      IExpr[] p = parameters(dist);
      if (p != null) {
        if (isSymmetric(dist)) {
          return F.Less(p[0], p[1]);
        }
        return F.And(F.Less(p[0], p[2]), F.Less(p[2], p[1]));
      }
      return F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      IExpr[] p = parameters(dist);
      if (p != null) {
        IExpr a = p[0];
        IExpr b = p[1];
        IExpr c = p[2];
        // Piecewise({{(2*(# - a))/((b - a)*(c - a)), a <= # <= c},
        // {(2*(b - #))/((b - a)*(b - c)), c < # <= b}}, 0) &
        return callFunction(F.Function(F.Piecewise(F.list(//
            F.list(F.Divide(F.Times(F.C2, F.Subtract(F.Slot1, a)),
                F.Times(F.Subtract(b, a), F.Subtract(c, a))), F.LessEqual(a, F.Slot1, c)), //
            F.list(
                F.Divide(F.Times(F.C2, F.Subtract(b, F.Slot1)),
                    F.Times(F.Subtract(b, a), F.Subtract(b, c))),
                F.Inequality(c, S.Less, F.Slot1, S.LessEqual, b))),
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
    public IExpr standardDeviation(IAST dist) {
      IExpr[] p = parameters(dist);
      if (p != null && isSymmetric(dist)) {
        // (b - a)/(2*Sqrt(6))
        return F.Divide(F.Subtract(p[1], p[0]), F.Times(F.C2, F.Sqrt(F.C6)));
      }
      return F.NIL;
    }

    @Override
    public IExpr variance(IAST dist) {
      IExpr[] p = parameters(dist);
      if (p != null) {
        IExpr a = p[0];
        IExpr b = p[1];
        IExpr c = p[2];
        if (isSymmetric(dist)) {
          // (b - a)^2/24
          return F.Divide(F.Sqr(F.Subtract(b, a)), F.ZZ(24));
        }
        // (a^2 - a*b + b^2 - a*c - b*c + c^2)/18
        return F.Divide(F.Plus(F.Sqr(a), F.Times(F.CN1, a, b), F.Sqr(b), F.Times(F.CN1, a, c),
            F.Times(F.CN1, b, c), F.Sqr(c)), F.ZZ(18));
      }
      return F.NIL;
    }
  }

  /**
   * <code>MaxwellDistribution(sigma)</code> - the Maxwell(-Boltzmann) distribution.
   */
  private static final class MaxwellDistribution extends AbstractEvaluator
      implements ICDF, IContinuousDistribution, IPDF, IStatistics {

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST1()) {
        IExpr s = dist.arg1();
        // Piecewise({{Erf(#/(Sqrt(2)*s)) - (Sqrt(2/Pi)*#)/(E^(#^2/(2*s^2))*s), # > 0}}, 0) &
        return callFunction(F.Function(F.Piecewise(F.list(F.list(//
            F.Subtract(F.Erf(F.Divide(F.Slot1, F.Times(F.CSqrt2, s))),
                F.Divide(F.Times(F.Sqrt(F.Divide(F.C2, F.Pi)), F.Slot1),
                    F.Times(F.Exp(F.Divide(F.Sqr(F.Slot1), F.Times(F.C2, F.Sqr(s)))), s))),
            F.Greater(F.Slot1, F.C0))), F.C0)), k);
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
      // 2*Sqrt(2/Pi)*s
      return dist.isAST1() ? F.Times(F.C2, F.Sqrt(F.Divide(F.C2, F.Pi)), dist.arg1()) : F.NIL;
    }

    @Override
    public IExpr median(IAST dist) {
      return F.NIL;
    }

    @Override
    public IExpr parameterAssumptions(IAST dist) {
      return dist.isAST1() ? F.Greater(dist.arg1(), F.C0) : F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST1()) {
        IExpr s = dist.arg1();
        // Piecewise({{(Sqrt(2/Pi)*#^2)/(E^(#^2/(2*s^2))*s^3), # > 0}}, 0) &
        return callFunction(F.Function(F.Piecewise(F.list(F.list(//
            F.Divide(F.Times(F.Sqrt(F.Divide(F.C2, F.Pi)), F.Sqr(F.Slot1)), F
                .Times(F.Exp(F.Divide(F.Sqr(F.Slot1), F.Times(F.C2, F.Sqr(s)))), F.Power(s, F.C3))),
            F.Greater(F.Slot1, F.C0))), F.C0)), k);
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
        // ((3*Pi - 8)*s^2)/Pi
        return F.Divide(F.Times(F.Plus(F.CN8, F.Times(F.C3, F.Pi)), F.Sqr(dist.arg1())), F.Pi);
      }
      return F.NIL;
    }
  }

  /**
   * <code>WignerSemicircleDistribution(r)</code> or <code>WignerSemicircleDistribution(a, r)</code>
   * - the Wigner semicircle distribution.
   */
  private static final class WignerSemicircleDistribution extends AbstractEvaluator
      implements ICDF, IContinuousDistribution, IPDF, IStatistics {

    /** <code>{center, radius}</code> */
    private static IExpr[] parameters(IAST dist) {
      if (dist.isAST1()) {
        return new IExpr[] {F.C0, dist.arg1()};
      }
      if (dist.isAST2()) {
        return new IExpr[] {dist.arg1(), dist.arg2()};
      }
      return null;
    }

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      IExpr[] p = parameters(dist);
      if (p != null) {
        IExpr a = p[0];
        IExpr r = p[1];
        IExpr z = F.Subtract(F.Slot1, a);
        // Piecewise({{1/2 + (z*Sqrt(1 - z^2/r^2))/(Pi*r) + ArcSin(z/r)/Pi, a - r < # < a + r},
        // {1, # >= a + r}}, 0) &
        return callFunction(F.Function(F.Piecewise(F.list(//
            F.list(
                F.Plus(F.C1D2,
                    F.Divide(F.Times(z, F.Sqrt(F.Subtract(F.C1, F.Divide(F.Sqr(z), F.Sqr(r))))),
                        F.Times(F.Pi, r)),
                    F.Divide(F.ArcSin(F.Divide(z, r)), F.Pi)),
                F.Less(F.Subtract(a, r), F.Slot1, F.Plus(a, r))), //
            F.list(F.C1, F.GreaterEqual(F.Slot1, F.Plus(a, r)))), F.C0)), k);
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
      IExpr[] p = parameters(dist);
      return p != null ? p[0] : F.NIL;
    }

    @Override
    public IExpr median(IAST dist) {
      IExpr[] p = parameters(dist);
      return p != null ? p[0] : F.NIL;
    }

    @Override
    public IExpr parameterAssumptions(IAST dist) {
      IExpr[] p = parameters(dist);
      return p != null ? F.Greater(p[1], F.C0) : F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      IExpr[] p = parameters(dist);
      if (p != null) {
        IExpr a = p[0];
        IExpr r = p[1];
        IExpr z = F.Subtract(F.Slot1, a);
        // Piecewise({{(2*Sqrt(1 - z^2/r^2))/(Pi*r), a - r < # < a + r}}, 0) &
        return callFunction(F.Function(F.Piecewise(F.list(F.list(//
            F.Divide(F.Times(F.C2, F.Sqrt(F.Subtract(F.C1, F.Divide(F.Sqr(z), F.Sqr(r))))),
                F.Times(F.Pi, r)),
            F.Less(F.Subtract(a, r), F.Slot1, F.Plus(a, r)))), F.C0)), k);
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

    @Override
    public IExpr skewness(IAST dist) {
      IExpr[] p = parameters(dist);
      return p != null ? F.C0 : F.NIL;
    }

    @Override
    public IExpr variance(IAST dist) {
      IExpr[] p = parameters(dist);
      // r^2/4
      return p != null ? F.Divide(F.Sqr(p[1]), F.C4) : F.NIL;
    }
  }

  /**
   * <code>SechDistribution()</code> or <code>SechDistribution(mu, sigma)</code> - the hyperbolic
   * secant distribution.
   */
  private static final class SechDistribution extends AbstractEvaluator
      implements ICDF, IContinuousDistribution, IPDF, IStatistics {

    /** <code>{location, scale}</code> */
    private static IExpr[] parameters(IAST dist) {
      if (dist.isAST0()) {
        return new IExpr[] {F.C0, F.C1};
      }
      if (dist.isAST2()) {
        return new IExpr[] {dist.arg1(), dist.arg2()};
      }
      return null;
    }

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      IExpr[] p = parameters(dist);
      if (p != null) {
        // (2*ArcTan(E^((Pi*(# - m))/(2*s))))/Pi &
        IExpr z = F.Divide(F.Times(F.Pi, F.Subtract(F.Slot1, p[0])), F.Times(F.C2, p[1]));
        return callFunction(F.Function(F.Divide(F.Times(F.C2, F.ArcTan(F.Exp(z))), F.Pi)), k);
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_2;
    }

    @Override
    public IExpr inverseCDF(IAST dist, IExpr k, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public IExpr mean(IAST dist) {
      IExpr[] p = parameters(dist);
      return p != null ? p[0] : F.NIL;
    }

    @Override
    public IExpr median(IAST dist) {
      IExpr[] p = parameters(dist);
      return p != null ? p[0] : F.NIL;
    }

    @Override
    public IExpr parameterAssumptions(IAST dist) {
      IExpr[] p = parameters(dist);
      return p != null ? F.Greater(p[1], F.C0) : F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      IExpr[] p = parameters(dist);
      if (p != null) {
        // Sech((Pi*(# - m))/(2*s))/(2*s) &
        IExpr z = F.Divide(F.Times(F.Pi, F.Subtract(F.Slot1, p[0])), F.Times(F.C2, p[1]));
        return callFunction(F.Function(F.Divide(F.Sech(z), F.Times(F.C2, p[1]))), k);
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

    @Override
    public IExpr skewness(IAST dist) {
      IExpr[] p = parameters(dist);
      return p != null ? F.C0 : F.NIL;
    }

    @Override
    public IExpr standardDeviation(IAST dist) {
      IExpr[] p = parameters(dist);
      return p != null ? p[1] : F.NIL;
    }

    @Override
    public IExpr variance(IAST dist) {
      IExpr[] p = parameters(dist);
      return p != null ? F.Sqr(p[1]) : F.NIL;
    }
  }

  /**
   * <code>MoyalDistribution()</code> or <code>MoyalDistribution(mu, sigma)</code> - the Moyal
   * distribution.
   */
  private static final class MoyalDistribution extends AbstractEvaluator
      implements ICDF, IContinuousDistribution, IPDF, IStatistics {

    /** <code>{location, scale}</code> */
    private static IExpr[] parameters(IAST dist) {
      if (dist.isAST0()) {
        return new IExpr[] {F.C0, F.C1};
      }
      if (dist.isAST2()) {
        return new IExpr[] {dist.arg1(), dist.arg2()};
      }
      return null;
    }

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      IExpr[] p = parameters(dist);
      if (p != null) {
        // Erfc(1/(Sqrt(2)*E^((# - m)/(2*s)))) &
        IExpr z = F.Divide(F.Subtract(F.Slot1, p[0]), F.Times(F.C2, p[1]));
        return callFunction(F.Function(F.Erfc(F.Divide(F.C1, F.Times(F.CSqrt2, F.Exp(z))))), k);
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_2;
    }

    @Override
    public IExpr inverseCDF(IAST dist, IExpr k, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public IExpr mean(IAST dist) {
      IExpr[] p = parameters(dist);
      if (p != null) {
        // m + s*(EulerGamma + Log(2))
        return F.Plus(p[0], F.Times(p[1], F.Plus(S.EulerGamma, F.Log(F.C2))));
      }
      return F.NIL;
    }

    @Override
    public IExpr median(IAST dist) {
      return F.NIL;
    }

    @Override
    public IExpr parameterAssumptions(IAST dist) {
      IExpr[] p = parameters(dist);
      return p != null ? F.Greater(p[1], F.C0) : F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      IExpr[] p = parameters(dist);
      if (p != null) {
        IExpr m = p[0];
        IExpr s = p[1];
        // E^(-(1/2)*E^(-((# - m)/s)) - (# - m)/(2*s))/(Sqrt(2*Pi)*s) &
        IExpr z = F.Divide(F.Subtract(F.Slot1, m), s);
        return callFunction(F.Function(
            F.Divide(F.Exp(F.Subtract(F.Times(F.CN1D2, F.Exp(F.Negate(z))), F.Times(F.C1D2, z))),
                F.Times(F.Sqrt(F.Times(F.C2, F.Pi)), s))),
            k);
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
      IExpr[] p = parameters(dist);
      // (Pi^2*s^2)/2
      return p != null ? F.Divide(F.Times(F.Sqr(F.Pi), F.Sqr(p[1])), F.C2) : F.NIL;
    }
  }

  /**
   * <code>LogLogisticDistribution(gamma, sigma)</code> - the log-logistic (Fisk) distribution.
   */
  /**
   * <code>LogisticDistribution(a, b)</code> - the logistic distribution with mean <code>a</code>
   * and scale parameter <code>b</code>. <code>LogisticDistribution()</code> is equivalent to
   * <code>LogisticDistribution(0, 1)</code>.
   */
  private static final class LogisticDistribution extends AbstractEvaluator
      implements ICDF, ICentralMoment, IContinuousDistribution, IGeneratingFunction, IPDF,
      IRandomVariate, IStatistics {

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST0()) {
        if (!engine.isArbitraryMode() && k.isNumericArgument(true)) {
          double kDouble = k.evalfNaN();
          if (!Double.isNaN(kDouble)) {
            try {
              return F.num(1.0 / (1.0 + Math.exp(-kDouble)));
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
            }
          }
        }
        // 1/(1 + E^(-#)) &
        return callFunction(F.Function(F.Power(F.Plus(F.C1, F.Exp(F.Negate(F.Slot1))), F.CN1)), k);
      } else if (dist.isAST2()) {
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        if (!engine.isArbitraryMode() && //
            (a.isNumericArgument(true) || b.isNumericArgument(true) || k.isNumericArgument(true))) {
          double kDouble = k.evalfNaN();
          double aDouble = a.evalfNaN();
          double bDouble = b.evalfNaN();
          if (!Double.isNaN(kDouble) && !Double.isNaN(aDouble) && !Double.isNaN(bDouble)) {
            try {
              return F.num(1.0 / (1.0 + Math.exp((aDouble - kDouble) / bDouble)));
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
            }
          }
        }
        // 1/(1 + E^((a - #)/b)) &
        return callFunction(
            F.Function(F.Power(F.Plus(F.C1, F.Exp(F.Divide(F.Subtract(a, F.Slot1), b))), F.CN1)),
            k);
      }
      return F.NIL;
    }

    @Override
    public IExpr centralMoment(IAST dist, IExpr m, EvalEngine engine) {
      // no simple closed form for a general order
      return F.NIL;
    }

    @Override
    public IAST checkParameters(IAST dist) {
      if (dist.isAST0()) {
        return dist;
      }
      if (dist.isAST2()) {
        IExpr b = dist.arg2();
        if (b.isReal() && !b.isPositive()) {
          // Parameter `1` at position `2` in `3` is expected to be positive.
          Errors.printMessage(S.LogisticDistribution, "posprm", F.list(b, F.C2, dist),
              EvalEngine.get());
          return F.NIL;
        }
        return dist;
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // 0 or 2 args
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_2;
    }

    @Override
    public IExpr inverseCDF(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST0()) {
        if (!engine.isArbitraryMode() && k.isNumericArgument(true)) {
          double p = k.evalfNaN();
          if (!Double.isNaN(p)) {
            try {
              MathUtils.checkRangeInclusive(p, 0, 1);
              if (F.isZero(p)) {
                return F.CNInfinity;
              } else if (F.isEqual(p, 1.0)) {
                return F.CInfinity;
              }
              return F.num(Math.log(p / (1.0 - p)));
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
            }
          }
        }
        // ConditionalExpression(Piecewise({{Log(#/(1 - #)), 0 < # < 1}, {-Infinity, # <= 0}},
        // Infinity), 0 <= # <= 1) &
        return callFunction(F.Function(F.ConditionalExpression(
            F.Piecewise(F.list(
                F.list(F.Log(F.Divide(F.Slot1, F.Subtract(F.C1, F.Slot1))),
                    F.Less(F.C0, F.Slot1, F.C1)),
                F.list(F.Negate(F.oo), F.LessEqual(F.Slot1, F.C0))), F.oo),
            F.LessEqual(F.C0, F.Slot1, F.C1))), k);
      } else if (dist.isAST2()) {
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        if (!engine.isArbitraryMode() && //
            (a.isNumericArgument(true) || b.isNumericArgument(true) || k.isNumericArgument(true))) {
          double p = k.evalfNaN();
          double aDouble = a.evalfNaN();
          double bDouble = b.evalfNaN();
          if (!Double.isNaN(p) && !Double.isNaN(aDouble) && !Double.isNaN(bDouble)) {
            try {
              MathUtils.checkRangeInclusive(p, 0, 1);
              if (F.isZero(p)) {
                return F.CNInfinity;
              } else if (F.isEqual(p, 1.0)) {
                return F.CInfinity;
              }
              return F.num(aDouble + bDouble * Math.log(p / (1.0 - p)));
            } catch (RuntimeException rex) {
              Errors.rethrowsInterruptException(rex);
            }
          }
        }
        // ConditionalExpression(Piecewise({{a + b*Log(#/(1 - #)), 0 < # < 1}, {-Infinity, # <= 0}},
        // Infinity), 0 <= # <= 1) &
        return callFunction(
            F.Function(
                F.ConditionalExpression(
                    F.Piecewise(
                        F.list(
                            F.list(
                                F.Plus(a,
                                    F.Times(b,
                                        F.Log(F.Divide(F.Slot1, F.Subtract(F.C1, F.Slot1))))),
                                F.Less(F.C0, F.Slot1, F.C1)),
                            F.list(F.Negate(F.oo), F.LessEqual(F.Slot1, F.C0))),
                        F.oo),
                    F.LessEqual(F.C0, F.Slot1, F.C1))),
            k);
      }
      return F.NIL;
    }

    @Override
    public IExpr kurtosis(IAST dist, EvalEngine engine) {
      if (dist.isAST0() || dist.isAST2()) {
        // 3 + 6/5
        return F.QQ(21, 5);
      }
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
    public IExpr median(IAST dist) {
      return mean(dist);
    }

    @Override
    public IExpr mgf(IAST dist, IExpr t, EvalEngine engine) {
      // the Gamma function form is used, because it evaluates to 1 for t == 0 and can be
      // differentiated symbolically;
      // Gamma(1 - b*t)*Gamma(1 + b*t) == (Pi*b*t)/Sin(Pi*b*t)
      if (dist.isAST0()) {
        // Gamma(1 - t)*Gamma(1 + t)
        return F.Times(F.Gamma(F.Subtract(F.C1, t)), F.Gamma(F.Plus(F.C1, t)));
      }
      if (dist.isAST2()) {
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        // E^(a*t)*Gamma(1 - b*t)*Gamma(1 + b*t)
        return F.Times(F.Exp(F.Times(a, t)), F.Gamma(F.Subtract(F.C1, F.Times(b, t))),
            F.Gamma(F.Plus(F.C1, F.Times(b, t))));
      }
      return F.NIL;
    }

    @Override
    public IExpr moment(IAST dist, IExpr n) {
      int order = n.toIntDefault();
      if (order == 0) {
        return F.C1;
      }
      if (order == 1) {
        return mean(dist);
      }
      if (order == 2) {
        IExpr mean = mean(dist);
        IExpr variance = variance(dist);
        if (mean.isPresent() && variance.isPresent()) {
          // E[X^2] == Mean^2 + Variance
          return F.Plus(F.Sqr(mean), variance);
        }
      }
      // no simple closed form for a general order
      return F.NIL;
    }

    @Override
    public IExpr parameterAssumptions(IAST dist) {
      if (dist.isAST2()) {
        return F.Greater(dist.arg2(), F.C0);
      }
      return F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST0()) {
        // E^(-#)/(1 + E^(-#))^2 &
        return callFunction(
            F.Function(
                F.Divide(F.Exp(F.Negate(F.Slot1)), F.Sqr(F.Plus(F.C1, F.Exp(F.Negate(F.Slot1)))))),
            k);
      } else if (dist.isAST2()) {
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        // E^((a - #)/b)/(b*(1 + E^((a - #)/b))^2) &
        IExpr z = F.Divide(F.Subtract(a, F.Slot1), b);
        return callFunction(
            F.Function(F.Divide(F.Exp(z), F.Times(b, F.Sqr(F.Plus(F.C1, F.Exp(z)))))), k);
      }
      return F.NIL;
    }

    @Override
    public IExpr randomVariate(Random random, IAST dist, int size) {
      if (dist.isAST0()) {
        RandomDataGenerator rdg = new RandomDataGenerator();
        double[] vector = rdg.nextDeviates(
            new org.hipparchus.distribution.continuous.LogisticDistribution(0.0, 1.0), size);
        return new ASTRealVector(vector, false);
      } else if (dist.isAST2()) {
        // see exception handling in RandomVariate() function
        double a = dist.arg1().evalfNaN();
        double b = dist.arg2().evalfNaN();
        if (!Double.isNaN(a) && !Double.isNaN(b) && b > 0.0) {
          RandomDataGenerator rdg = new RandomDataGenerator();
          double[] vector = rdg.nextDeviates(
              new org.hipparchus.distribution.continuous.LogisticDistribution(a, b), size);
          return new ASTRealVector(vector, false);
        }
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

    @Override
    public IExpr skewness(IAST dist) {
      if (dist.isAST0() || dist.isAST2()) {
        return F.C0;
      }
      return F.NIL;
    }

    @Override
    public IExpr standardDeviation(IAST dist) {
      if (dist.isAST0()) {
        // Pi/Sqrt(3)
        return F.Divide(F.Pi, F.Sqrt(F.C3));
      }
      if (dist.isAST2()) {
        // (b*Pi)/Sqrt(3)
        return F.Divide(F.Times(dist.arg2(), F.Pi), F.Sqrt(F.C3));
      }
      return F.NIL;
    }

    @Override
    public IExpr variance(IAST dist) {
      if (dist.isAST0()) {
        // Pi^2/3
        return F.Divide(F.Sqr(F.Pi), F.C3);
      }
      if (dist.isAST2()) {
        // (b^2*Pi^2)/3
        return F.Divide(F.Times(F.Sqr(dist.arg2()), F.Sqr(F.Pi)), F.C3);
      }
      return F.NIL;
    }
  }


  private static final class LogLogisticDistribution extends AbstractEvaluator
      implements ICDF, IContinuousDistribution, IPDF, IStatistics {

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr g = dist.arg1();
        IExpr s = dist.arg2();
        // Piecewise({{(1 + (#/s)^(-g))^(-1), # > 0}}, 0) &
        return callFunction(F.Function(F.Piecewise(F.list(F.list(//
            F.Power(F.Plus(F.C1, F.Power(F.Divide(F.Slot1, s), F.Negate(g))), F.CN1),
            F.Greater(F.Slot1, F.C0))), F.C0)), k);
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
      if (dist.isAST2()) {
        IExpr g = dist.arg1();
        IExpr s = dist.arg2();
        // Piecewise({{(Pi*s*Csc(Pi/g))/g, g > 1}}, Indeterminate)
        return F.Piecewise(F.list(
            F.list(F.Divide(F.Times(F.Pi, s, F.Csc(F.Divide(F.Pi, g))), g), F.Greater(g, F.C1))),
            S.Indeterminate);
      }
      return F.NIL;
    }

    @Override
    public IExpr median(IAST dist) {
      return dist.isAST2() ? dist.arg2() : F.NIL;
    }

    @Override
    public IExpr parameterAssumptions(IAST dist) {
      if (dist.isAST2()) {
        return F.And(F.Greater(dist.arg1(), F.C0), F.Greater(dist.arg2(), F.C0));
      }
      return F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr g = dist.arg1();
        IExpr s = dist.arg2();
        // Piecewise({{(g*#^(g - 1))/(s^g*(1 + (#/s)^g)^2), # > 0}}, 0) &
        return callFunction(F.Function(F.Piecewise(F.list(F.list(//
            F.Divide(F.Times(g, F.Power(F.Slot1, F.Subtract(g, F.C1))),
                F.Times(F.Power(s, g), F.Sqr(F.Plus(F.C1, F.Power(F.Divide(F.Slot1, s), g))))),
            F.Greater(F.Slot1, F.C0))), F.C0)), k);
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
        IExpr g = dist.arg1();
        IExpr s = dist.arg2();
        // Piecewise({{(Pi*s^2*(2*g*Csc((2*Pi)/g) - Pi*Csc(Pi/g)^2))/g^2, g > 2}}, Indeterminate)
        return F.Piecewise(F.list(F.list(//
            F.Divide(F.Times(F.Pi, F.Sqr(s),
                F.Subtract(F.Times(F.C2, g, F.Csc(F.Divide(F.Times(F.C2, F.Pi), g))),
                    F.Times(F.Pi, F.Sqr(F.Csc(F.Divide(F.Pi, g)))))),
                F.Sqr(g)),
            F.Greater(g, F.C2))), S.Indeterminate);
      }
      return F.NIL;
    }
  }


  /**
   * <code>BetaPrimeDistribution(p, q)</code>, <code>BetaPrimeDistribution(p, q, beta)</code> or
   * <code>BetaPrimeDistribution(p, q, alpha, beta)</code> - the beta prime distribution.
   */
  private static final class BetaPrimeDistribution extends AbstractEvaluator
      implements ICDF, IContinuousDistribution, IPDF, IStatistics {

    /** <code>{p, q, alpha, beta}</code> - shape, shape, inner shape and scale. */
    private static IExpr[] parameters(IAST dist) {
      if (dist.isAST2()) {
        return new IExpr[] {dist.arg1(), dist.arg2(), F.C1, F.C1};
      }
      if (dist.isAST3()) {
        return new IExpr[] {dist.arg1(), dist.arg2(), F.C1, dist.arg3()};
      }
      if (dist.size() == 5) {
        return new IExpr[] {dist.arg1(), dist.arg2(), dist.arg3(), dist.arg4()};
      }
      return null;
    }

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      IExpr[] par = parameters(dist);
      if (par != null) {
        IExpr p = par[0];
        IExpr q = par[1];
        IExpr alpha = par[2];
        IExpr beta = par[3];
        IExpr z;
        if (dist.size() == 5) {
          // #^alpha/(beta^alpha + #^alpha)
          z = F.Divide(F.Power(F.Slot1, alpha),
              F.Plus(F.Power(beta, alpha), F.Power(F.Slot1, alpha)));
        } else {
          // #/(beta + #)
          z = F.Divide(F.Slot1, F.Plus(beta, F.Slot1));
        }
        // Piecewise({{BetaRegularized(z, p, q), # > 0}}, 0) &
        return callFunction(F.Function(F
            .Piecewise(F.list(F.list(F.BetaRegularized(z, p, q), F.Greater(F.Slot1, F.C0))), F.C0)),
            k);
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_4;
    }

    @Override
    public IExpr inverseCDF(IAST dist, IExpr k, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public IExpr mean(IAST dist) {
      IExpr[] par = parameters(dist);
      if (par != null) {
        IExpr p = par[0];
        IExpr q = par[1];
        IExpr alpha = par[2];
        IExpr beta = par[3];
        if (dist.size() == 5) {
          // (beta*Gamma(p + 1/alpha)*Gamma(q - 1/alpha))/(Gamma(p)*Gamma(q))
          IExpr inverseAlpha = F.Power(alpha, F.CN1);
          return F.Divide(
              F.Times(beta, F.Gamma(F.Plus(p, inverseAlpha)), F.Gamma(F.Subtract(q, inverseAlpha))),
              F.Times(F.Gamma(p), F.Gamma(q)));
        }
        // Piecewise({{(p*beta)/(q - 1), q > 1}}, Infinity)
        return F.Piecewise(
            F.list(F.list(F.Divide(F.Times(p, beta), F.Subtract(q, F.C1)), F.Greater(q, F.C1))),
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
      IExpr[] par = parameters(dist);
      if (par != null) {
        return F.And(F.Greater(par[0], F.C0), F.Greater(par[1], F.C0), F.Greater(par[2], F.C0),
            F.Greater(par[3], F.C0));
      }
      return F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      IExpr[] par = parameters(dist);
      if (par != null) {
        IExpr p = par[0];
        IExpr q = par[1];
        IExpr alpha = par[2];
        IExpr beta = par[3];
        IExpr value;
        if (dist.size() == 5) {
          // (alpha*(#/beta)^(alpha*p - 1)*(1 + (#/beta)^alpha)^(-p - q))/(beta*Beta(p, q))
          IExpr scaled = F.Divide(F.Slot1, beta);
          value = F.Divide(
              F.Times(alpha, F.Power(scaled, F.Subtract(F.Times(alpha, p), F.C1)),
                  F.Power(F.Plus(F.C1, F.Power(scaled, alpha)), F.Subtract(F.Negate(p), q))),
              F.Times(beta, F.Beta(p, q)));
        } else if (dist.isAST3()) {
          // ((#/beta)^(p - 1)*(1 + #/beta)^(-p - q))/(beta*Beta(p, q))
          IExpr scaled = F.Divide(F.Slot1, beta);
          value = F.Divide(
              F.Times(F.Power(scaled, F.Subtract(p, F.C1)),
                  F.Power(F.Plus(F.C1, scaled), F.Subtract(F.Negate(p), q))),
              F.Times(beta, F.Beta(p, q)));
        } else {
          // (#^(p - 1)*(1 + #)^(-p - q))/Beta(p, q)
          value = F.Divide(F.Times(F.Power(F.Slot1, F.Subtract(p, F.C1)),
              F.Power(F.Plus(F.C1, F.Slot1), F.Subtract(F.Negate(p), q))), F.Beta(p, q));
        }
        return callFunction(
            F.Function(F.Piecewise(F.list(F.list(value, F.Greater(F.Slot1, F.C0))), F.C0)), k);
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
      IExpr[] par = parameters(dist);
      if (par != null && dist.size() != 5) {
        IExpr p = par[0];
        IExpr q = par[1];
        IExpr beta = par[3];
        // Piecewise({{(p*(p + q - 1)*beta^2)/((q - 2)*(q - 1)^2), q > 2}}, Indeterminate)
        return F.Piecewise(F.list(F.list(//
            F.Divide(F.Times(p, F.Plus(F.CN1, p, q), F.Sqr(beta)),
                F.Times(F.Subtract(q, F.C2), F.Sqr(F.Subtract(q, F.C1)))),
            F.Greater(q, F.C2))), S.Indeterminate);
      }
      return F.NIL;
    }
  }

  /**
   * <code>SinghMaddalaDistribution(q, a, b)</code> - the Singh-Maddala (Burr XII) distribution.
   */
  private static final class SinghMaddalaDistribution extends AbstractEvaluator
      implements ICDF, IContinuousDistribution, IPDF, IStatistics {

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST3()) {
        IExpr q = dist.arg1();
        IExpr a = dist.arg2();
        IExpr b = dist.arg3();
        // Piecewise({{1 - (1 + (#/b)^a)^(-q), # > 0}}, 0) &
        return callFunction(F.Function(F.Piecewise(F.list(F.list(//
            F.Subtract(F.C1, F.Power(F.Plus(F.C1, F.Power(F.Divide(F.Slot1, b), a)), F.Negate(q))),
            F.Greater(F.Slot1, F.C0))), F.C0)), k);
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
        IExpr q = dist.arg1();
        IExpr a = dist.arg2();
        IExpr b = dist.arg3();
        IExpr inverseA = F.Power(a, F.CN1);
        // Piecewise({{(b*Gamma(1 + 1/a)*Gamma(q - 1/a))/Gamma(q), a*q > 1}}, Indeterminate)
        return F.Piecewise(F.list(F.list(//
            F.Divide(F.Times(b, F.Gamma(F.Plus(F.C1, inverseA)), F.Gamma(F.Subtract(q, inverseA))),
                F.Gamma(q)),
            F.Greater(F.Times(a, q), F.C1))), S.Indeterminate);
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
            F.Greater(dist.arg3(), F.C0));
      }
      return F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST3()) {
        IExpr q = dist.arg1();
        IExpr a = dist.arg2();
        IExpr b = dist.arg3();
        // Piecewise({{(a*q*#^(a - 1)*(1 + (#/b)^a)^(-1 - q))/b^a, # > 0}}, 0) &
        return callFunction(F.Function(F.Piecewise(F.list(F.list(//
            F.Divide(
                F.Times(a, q, F.Power(F.Slot1, F.Subtract(a, F.C1)),
                    F.Power(F.Plus(F.C1, F.Power(F.Divide(F.Slot1, b), a)), F.Subtract(F.CN1, q))),
                F.Power(b, a)),
            F.Greater(F.Slot1, F.C0))), F.C0)), k);
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
   * <code>MeixnerDistribution(a, b, m, d)</code> - the Meixner distribution. Only <code>Mean</code>
   * and <code>Variance</code> have a closed form here.
   */
  private static final class MeixnerDistribution extends AbstractEvaluator
      implements IContinuousDistribution, IStatistics {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_4_4;
    }

    @Override
    public IExpr mean(IAST dist) {
      if (dist.size() == 5) {
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        IExpr m = dist.arg3();
        IExpr d = dist.arg4();
        // m + a*d*Tan(b/2)
        return F.Plus(m, F.Times(a, d, F.Tan(F.Times(F.C1D2, b))));
      }
      return F.NIL;
    }

    @Override
    public IExpr median(IAST dist) {
      return F.NIL;
    }

    @Override
    public IExpr parameterAssumptions(IAST dist) {
      if (dist.size() == 5) {
        return F.And(F.Greater(dist.arg1(), F.C0), F.Less(F.Negate(F.Pi), dist.arg2(), F.Pi),
            F.Greater(dist.arg4(), F.C0));
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
      if (dist.size() == 5) {
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        IExpr d = dist.arg4();
        // (a^2*d*Sec(b/2)^2)/2
        return F.Divide(F.Times(F.Sqr(a), d, F.Sqr(F.Sec(F.Times(F.C1D2, b)))), F.C2);
      }
      return F.NIL;
    }
  }

  /**
   * <code>SuzukiDistribution(mu, nu)</code> - the Suzuki distribution. Only <code>Mean</code> and
   * <code>Variance</code> have a closed form here.
   */
  private static final class SuzukiDistribution extends AbstractEvaluator
      implements IContinuousDistribution, IStatistics {

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
        IExpr m = dist.arg1();
        IExpr n = dist.arg2();
        // E^(m + n^2/2)*Sqrt(Pi/2)
        return F.Times(F.Exp(F.Plus(m, F.Times(F.C1D2, F.Sqr(n)))), F.Sqrt(F.Divide(F.Pi, F.C2)));
      }
      return F.NIL;
    }

    @Override
    public IExpr median(IAST dist) {
      return F.NIL;
    }

    @Override
    public IExpr parameterAssumptions(IAST dist) {
      return dist.isAST2() ? F.Greater(dist.arg2(), F.C0) : F.NIL;
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
        IExpr m = dist.arg1();
        IExpr n = dist.arg2();
        // E^(2*m + n^2)*(2*E^(n^2) - Pi/2)
        return F.Times(F.Exp(F.Plus(F.Times(F.C2, m), F.Sqr(n))),
            F.Subtract(F.Times(F.C2, F.Exp(F.Sqr(n))), F.Divide(F.Pi, F.C2)));
      }
      return F.NIL;
    }
  }

  /**
   * <code>BenktanderGibratDistribution(a, b)</code> - the Benktander distribution of type I.
   */
  private static final class BenktanderGibratDistribution extends AbstractEvaluator
      implements ICDF, IContinuousDistribution, IPDF, IStatistics {

    @Override
    public IAST checkParameters(IAST dist) {
      if (dist.isAST2()) {
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        // 0 < b <= a*(1 + a)/2
        IExpr condition = EvalEngine.get().evaluate(
            F.And(F.Greater(b, F.C0), F.LessEqual(b, F.Times(F.C1D2, a, F.Plus(F.C1, a)))));
        return condition.isFalse() ? F.NIL : dist;
      }
      return F.NIL;
    }

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      if (checkParameters(dist).isPresent()) {
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        // Piecewise({{1 - (#^(-1 - a)*(1 + (2*b*Log(#))/a))/E^(b*Log(#)^2), # >= 1}}, 0) &
        return callFunction(F.Function(F.Piecewise(F.list(F.list(//
            F.Subtract(F.C1,
                F.Divide(
                    F.Times(F.Power(F.Slot1, F.Subtract(F.CN1, a)),
                        F.Plus(F.C1, F.Divide(F.Times(F.C2, b, F.Log(F.Slot1)), a))),
                    F.Exp(F.Times(b, F.Sqr(F.Log(F.Slot1)))))),
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
      return ARGS_2_2;
    }

    @Override
    public IExpr inverseCDF(IAST dist, IExpr k, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public IExpr mean(IAST dist) {
      if (checkParameters(dist).isPresent()) {
        // 1 + 1/a
        return F.Plus(F.C1, F.Power(dist.arg1(), F.CN1));
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
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        return F.And(F.Greater(a, F.C0), F.Greater(b, F.C0),
            F.LessEqual(b, F.Times(F.C1D2, a, F.Plus(F.C1, a))));
      }
      return F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      if (checkParameters(dist).isPresent()) {
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        // Piecewise({{(#^(-2 - a)*((-2*b)/a + (1 + a + 2*b*Log(#))*(1 + (2*b*Log(#))/a)))/
        // E^(b*Log(#)^2), # >= 1}}, 0) &
        IExpr log = F.Log(F.Slot1);
        return callFunction(F.Function(F.Piecewise(F.list(F.list(//
            F.Divide(
                F.Times(F.Power(F.Slot1, F.Subtract(F.CN2, a)),
                    F.Plus(F.Divide(F.Times(F.CN2, b), a),
                        F.Times(F.Plus(F.C1, a, F.Times(F.C2, b, log)),
                            F.Plus(F.C1, F.Divide(F.Times(F.C2, b, log), a))))),
                F.Exp(F.Times(b, F.Sqr(log)))),
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
      if (checkParameters(dist).isPresent()) {
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        // (-1 + (a*E^((a - 1)^2/(4*b))*Sqrt(Pi)*Erfc((a - 1)/(2*Sqrt(b))))/Sqrt(b))/a^2
        return F
            .Divide(F.Plus(F.CN1,
                F.Divide(F.Times(a, F.Exp(F.Divide(F.Sqr(F.Subtract(a, F.C1)), F.Times(F.C4, b))),
                    F.Sqrt(F.Pi), F.Erfc(F.Divide(F.Subtract(a, F.C1), F.Times(F.C2, F.Sqrt(b))))),
                    F.Sqrt(b))),
                F.Sqr(a));
      }
      return F.NIL;
    }
  }

  /**
   * <code>BenktanderWeibullDistribution(a, b)</code> - the Benktander distribution of type II.
   */
  private static final class BenktanderWeibullDistribution extends AbstractEvaluator
      implements ICDF, IContinuousDistribution, IPDF, IStatistics {

    @Override
    public IAST checkParameters(IAST dist) {
      if (dist.isAST2()) {
        IExpr condition = EvalEngine.get().evaluate(F.And(F.Greater(dist.arg1(), F.C0),
            F.Greater(dist.arg2(), F.C0), F.LessEqual(dist.arg2(), F.C1)));
        return condition.isFalse() ? F.NIL : dist;
      }
      return F.NIL;
    }

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      if (checkParameters(dist).isPresent()) {
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        // Piecewise({{1 - E^((a*(1 - #^b))/b)*#^(b - 1), # >= 1}}, 0) &
        return callFunction(F.Function(F.Piecewise(F.list(F.list(//
            F.Subtract(F.C1,
                F.Times(F.Exp(F.Divide(F.Times(a, F.Subtract(F.C1, F.Power(F.Slot1, b))), b)),
                    F.Power(F.Slot1, F.Subtract(b, F.C1)))),
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
      return ARGS_2_2;
    }

    @Override
    public IExpr inverseCDF(IAST dist, IExpr k, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public IExpr mean(IAST dist) {
      if (checkParameters(dist).isPresent()) {
        // 1 + 1/a
        return F.Plus(F.C1, F.Power(dist.arg1(), F.CN1));
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
        return F.And(F.Greater(dist.arg1(), F.C0), F.Greater(dist.arg2(), F.C0),
            F.LessEqual(dist.arg2(), F.C1));
      }
      return F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      if (checkParameters(dist).isPresent()) {
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        // Piecewise({{E^((a*(1 - #^b))/b)*#^(b - 2)*(1 - b + a*#^b), # >= 1}}, 0) &
        return callFunction(F.Function(F.Piecewise(F.list(F.list(//
            F.Times(F.Exp(F.Divide(F.Times(a, F.Subtract(F.C1, F.Power(F.Slot1, b))), b)),
                F.Power(F.Slot1, F.Subtract(b, F.C2)),
                F.Plus(F.C1, F.Negate(b), F.Times(a, F.Power(F.Slot1, b)))),
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
      if (checkParameters(dist).isPresent()) {
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        // (-1 + (2*a*E^(a/b)*ExpIntegralE(1 - 1/b, a/b))/b)/a^2
        return F.Divide(
            F.Plus(F.CN1,
                F.Divide(F.Times(F.C2, a, F.Exp(F.Divide(a, b)),
                    F.ExpIntegralE(F.Subtract(F.C1, F.Power(b, F.CN1)), F.Divide(a, b))), b)),
            F.Sqr(a));
      }
      return F.NIL;
    }
  }


  /**
   * <code>ExponentialPowerDistribution(k)</code> or
   * <code>ExponentialPowerDistribution(k, mu, sigma)</code> - the exponential power (generalized
   * normal) distribution.
   */
  private static final class ExponentialPowerDistribution extends AbstractEvaluator
      implements ICDF, IContinuousDistribution, IPDF, IStatistics {

    /** <code>{shape, location, scale}</code> */
    private static IExpr[] parameters(IAST dist) {
      if (dist.isAST1()) {
        return new IExpr[] {dist.arg1(), F.C0, F.C1};
      }
      if (dist.isAST3()) {
        return new IExpr[] {dist.arg1(), dist.arg2(), dist.arg3()};
      }
      return null;
    }

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      IExpr[] p = parameters(dist);
      if (p != null) {
        IExpr shape = p[0];
        IExpr m = p[1];
        IExpr s = p[2];
        IExpr inverseShape = F.Power(shape, F.CN1);
        // Piecewise({{GammaRegularized(1/k, ((m - #)/s)^k/k)/2, # < m}},
        // 1 - GammaRegularized(1/k, ((# - m)/s)^k/k)/2) &
        IExpr below = F.Times(F.C1D2, F.GammaRegularized(inverseShape,
            F.Divide(F.Power(F.Divide(F.Subtract(m, F.Slot1), s), shape), shape)));
        IExpr above = F.Subtract(F.C1, F.Times(F.C1D2, F.GammaRegularized(inverseShape,
            F.Divide(F.Power(F.Divide(F.Subtract(F.Slot1, m), s), shape), shape))));
        return callFunction(
            F.Function(F.Piecewise(F.list(F.list(below, F.Less(F.Slot1, m))), above)), k);
      }
      return F.NIL;
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
    public IExpr inverseCDF(IAST dist, IExpr k, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public IExpr mean(IAST dist) {
      IExpr[] p = parameters(dist);
      return p != null ? p[1] : F.NIL;
    }

    @Override
    public IExpr median(IAST dist) {
      IExpr[] p = parameters(dist);
      return p != null ? p[1] : F.NIL;
    }

    @Override
    public IExpr parameterAssumptions(IAST dist) {
      IExpr[] p = parameters(dist);
      return p != null ? F.And(F.Greater(p[0], F.C0), F.Greater(p[2], F.C0)) : F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      IExpr[] p = parameters(dist);
      if (p != null) {
        IExpr shape = p[0];
        IExpr m = p[1];
        IExpr s = p[2];
        IExpr inverseShape = F.Power(shape, F.CN1);
        // 1/(2*E^(((# - m)/s)^k/k)*k^(1/k)*s*Gamma(1 + 1/k)) for # >= m, mirrored below m
        IExpr denominator =
            F.Times(F.C2, F.Power(shape, inverseShape), s, F.Gamma(F.Plus(F.C1, inverseShape)));
        IExpr above = F.Divide(F.C1,
            F.Times(F.Exp(F.Divide(F.Power(F.Divide(F.Subtract(F.Slot1, m), s), shape), shape)),
                denominator));
        IExpr below = F.Divide(F.C1,
            F.Times(F.Exp(F.Divide(F.Power(F.Divide(F.Subtract(m, F.Slot1), s), shape), shape)),
                denominator));
        IExpr function;
        IExpr evaluatedAbove = engine.evaluate(above);
        IExpr evaluatedBelow = engine.evaluate(below);
        if (evaluatedAbove.equals(evaluatedBelow)) {
          // both branches are identical, e.g. for an even shape parameter
          function = F.Function(evaluatedAbove);
        } else {
          function =
              F.Function(F.Piecewise(F.list(F.list(above, F.GreaterEqual(F.Slot1, m))), below));
        }
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

    @Override
    public IExpr skewness(IAST dist) {
      IExpr[] p = parameters(dist);
      return p != null ? F.C0 : F.NIL;
    }

    @Override
    public IExpr variance(IAST dist) {
      IExpr[] p = parameters(dist);
      if (p != null) {
        IExpr shape = p[0];
        IExpr s = p[2];
        IExpr inverseShape = F.Power(shape, F.CN1);
        // (k^(2/k)*s^2*Gamma(3/k))/Gamma(1/k)
        return F.Divide(F.Times(F.Power(shape, F.Times(F.C2, inverseShape)), F.Sqr(s),
            F.Gamma(F.Times(F.C3, inverseShape))), F.Gamma(inverseShape));
      }
      return F.NIL;
    }
  }

  /**
   * <code>RiceDistribution(alpha, beta)</code> - the Rice distribution.
   */
  private static final class RiceDistribution extends AbstractEvaluator
      implements ICDF, IContinuousDistribution, IPDF, IStatistics {

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        // Piecewise({{MarcumQ(1, a/b, 0, #/b), # > 0}}, 0) &
        return callFunction(F.Function(F.Piecewise(F.list(F.list(//
            F.MarcumQ(F.C1, F.Divide(a, b), F.C0, F.Divide(F.Slot1, b)), F.Greater(F.Slot1, F.C0))),
            F.C0)), k);
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

    /** <code>LaguerreL(1/2, -a^2/(2*b^2))</code> */
    private static IExpr laguerre(IExpr a, IExpr b) {
      return F.LaguerreL(F.C1D2, F.Times(F.CN1D2, F.Divide(F.Sqr(a), F.Sqr(b))));
    }

    @Override
    public IExpr mean(IAST dist) {
      if (dist.isAST2()) {
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        // b*Sqrt(Pi/2)*LaguerreL(1/2, -a^2/(2*b^2))
        return F.Times(b, F.Sqrt(F.Divide(F.Pi, F.C2)), laguerre(a, b));
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
        return F.And(F.GreaterEqual(dist.arg1(), F.C0), F.Greater(dist.arg2(), F.C0));
      }
      return F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        // Piecewise({{(E^((-a^2 - #^2)/(2*b^2))*#*BesselI(0, (a*#)/b^2))/b^2, # > 0}}, 0) &
        return callFunction(F.Function(F.Piecewise(F.list(F.list(//
            F.Divide(F.Times(
                F.Exp(F.Divide(F.Subtract(F.Negate(F.Sqr(a)), F.Sqr(F.Slot1)),
                    F.Times(F.C2, F.Sqr(b)))),
                F.Slot1, F.BesselI(F.C0, F.Divide(F.Times(a, F.Slot1), F.Sqr(b)))), F.Sqr(b)),
            F.Greater(F.Slot1, F.C0))), F.C0)), k);
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
        IExpr b = dist.arg2();
        // a^2 + 2*b^2 - (b^2*Pi*LaguerreL(1/2, -a^2/(2*b^2))^2)/2
        return F.Subtract(F.Plus(F.Sqr(a), F.Times(F.C2, F.Sqr(b))),
            F.Divide(F.Times(F.Sqr(b), F.Pi, F.Sqr(laguerre(a, b))), F.C2));
      }
      return F.NIL;
    }
  }

  /**
   * <code>NoncentralChiSquareDistribution(nu, lambda)</code> - the noncentral chi-square
   * distribution.
   */
  private static final class NoncentralChiSquareDistribution extends AbstractEvaluator
      implements ICDF, IContinuousDistribution, IPDF, IStatistics {

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr v = dist.arg1();
        IExpr l = dist.arg2();
        // Piecewise({{MarcumQ(v/2, Sqrt(l), 0, Sqrt(#)), # > 0}}, 0) &
        return callFunction(F.Function(F.Piecewise(F.list(F.list(//
            F.MarcumQ(F.Times(F.C1D2, v), F.Sqrt(l), F.C0, F.Sqrt(F.Slot1)),
            F.Greater(F.Slot1, F.C0))), F.C0)), k);
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
      // v + l
      return dist.isAST2() ? F.Plus(dist.arg1(), dist.arg2()) : F.NIL;
    }

    @Override
    public IExpr median(IAST dist) {
      return F.NIL;
    }

    @Override
    public IExpr parameterAssumptions(IAST dist) {
      if (dist.isAST2()) {
        return F.And(F.Greater(dist.arg1(), F.C0), F.GreaterEqual(dist.arg2(), F.C0));
      }
      return F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr v = dist.arg1();
        IExpr l = dist.arg2();
        IExpr halfV = F.Times(F.C1D2, v);
        // Piecewise({{(E^((-l - #)/2)*#^(v/2 - 1)*
        // Hypergeometric0F1Regularized(v/2, (l*#)/4))/2^(v/2), # > 0}}, 0) &
        return callFunction(F.Function(F.Piecewise(F.list(F.list(//
            F.Divide(
                F.Times(F.Exp(F.Times(F.C1D2, F.Subtract(F.Negate(l), F.Slot1))),
                    F.Power(F.Slot1, F.Subtract(halfV, F.C1)),
                    F.Hypergeometric0F1Regularized(halfV, F.Divide(F.Times(l, F.Slot1), F.C4))),
                F.Power(F.C2, halfV)),
            F.Greater(F.Slot1, F.C0))), F.C0)), k);
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
      // 2*v + 4*l
      return dist.isAST2() ? F.Plus(F.Times(F.C2, dist.arg1()), F.Times(F.C4, dist.arg2())) : F.NIL;
    }
  }

  /**
   * <code>MinStableDistribution(mu, sigma, xi)</code> - the generalized extreme value distribution
   * of the minimum.
   */
  private static final class MinStableDistribution extends AbstractEvaluator
      implements ICDF, IContinuousDistribution, IPDF, IStatistics {

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST3()) {
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        IExpr g = dist.arg3();
        // z = 1 + (g*(a - #))/b
        IExpr z = F.Plus(F.C1, F.Divide(F.Times(g, F.Subtract(a, F.Slot1)), b));
        IExpr tail = F.Subtract(F.C1, F.Exp(F.Negate(F.Power(z, F.Negate(F.Power(g, F.CN1))))));
        if (g.isNumber() && !g.isZero()) {
          // for an explicit shape the third branch covers the complement of the second one, so it
          // becomes the default value
          return callFunction(F.Function(
              F.Piecewise(F.list(F.list(tail, F.Greater(z, F.C0))), g.isPositive() ? F.C1 : F.C0)),
              k);
        }
        return callFunction(F.Function(F.Piecewise(F.list(//
            F.list(F.Subtract(F.C1, F.Exp(F.Negate(F.Exp(F.Divide(F.Subtract(F.Slot1, a), b))))),
                F.Equal(g, F.C0)), //
            F.list(tail, F.And(F.Unequal(g, F.C0), F.Greater(z, F.C0))), //
            F.list(F.C1, F.And(F.Greater(g, F.C0), F.LessEqual(z, F.C0)))), F.C0)), k);
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
        IExpr g = dist.arg3();
        // Piecewise({{a - b*EulerGamma, g == 0},
        // {(b + a*g - b*Gamma(1 - g))/g, g != 0 && g < 1}}, Indeterminate)
        return F.Piecewise(F.list(//
            F.list(F.Subtract(a, F.Times(b, S.EulerGamma)), F.Equal(g, F.C0)), //
            F.list(
                F.Divide(F.Plus(b, F.Times(a, g), F.Times(F.CN1, b, F.Gamma(F.Subtract(F.C1, g)))),
                    g),
                F.And(F.Unequal(g, F.C0), F.Less(g, F.C1)))),
            S.Indeterminate);
      }
      return F.NIL;
    }

    @Override
    public IExpr median(IAST dist) {
      return F.NIL;
    }

    @Override
    public IExpr parameterAssumptions(IAST dist) {
      return dist.isAST3() ? F.Greater(dist.arg2(), F.C0) : F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST3()) {
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        IExpr g = dist.arg3();
        IExpr z = F.Plus(F.C1, F.Divide(F.Times(g, F.Subtract(a, F.Slot1)), b));
        IExpr inverseG = F.Power(g, F.CN1);
        return callFunction(F.Function(F.Piecewise(F.list(//
            F.list(F.Divide(F.Exp(F.Subtract(F.Negate(F.Exp(F.Divide(F.Subtract(F.Slot1, a), b))),
                F.Divide(F.Subtract(a, F.Slot1), b))), b), F.Equal(g, F.C0)), //
            F.list(
                F.Divide(F.Power(z, F.Subtract(F.CN1, inverseG)),
                    F.Times(b, F.Exp(F.Power(z, F.Negate(inverseG))))),
                F.And(F.Unequal(g, F.C0), F.Greater(z, F.C0)))),
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
      if (dist.isAST3()) {
        IExpr b = dist.arg2();
        IExpr g = dist.arg3();
        // Piecewise({{(b^2*Pi^2)/6, g == 0},
        // {(b^2*(Gamma(1 - 2*g) - Gamma(1 - g)^2))/g^2, g != 0 && 2*g < 1}}, Indeterminate)
        return F.Piecewise(F.list(//
            F.list(F.Divide(F.Times(F.Sqr(b), F.Sqr(F.Pi)), F.C6), F.Equal(g, F.C0)), //
            F.list(
                F.Divide(F.Times(F.Sqr(b),
                    F.Subtract(F.Gamma(F.Subtract(F.C1, F.Times(F.C2, g))),
                        F.Sqr(F.Gamma(F.Subtract(F.C1, g))))),
                    F.Sqr(g)),
                F.And(F.Unequal(g, F.C0), F.Less(F.Times(F.C2, g), F.C1)))),
            S.Indeterminate);
      }
      return F.NIL;
    }
  }

  /**
   * <code>MaxStableDistribution(mu, sigma, xi)</code> - the generalized extreme value distribution
   * of the maximum.
   */
  private static final class MaxStableDistribution extends AbstractEvaluator
      implements ICDF, IContinuousDistribution, IPDF, IStatistics {

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST3()) {
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        IExpr g = dist.arg3();
        // z = 1 + (g*(# - a))/b
        IExpr z = F.Plus(F.C1, F.Divide(F.Times(g, F.Subtract(F.Slot1, a)), b));
        IExpr tail = F.Exp(F.Negate(F.Power(z, F.Negate(F.Power(g, F.CN1)))));
        if (g.isNumber() && !g.isZero()) {
          // for an explicit shape the third branch covers the complement of the second one, so it
          // becomes the default value
          return callFunction(F.Function(
              F.Piecewise(F.list(F.list(tail, F.Greater(z, F.C0))), g.isPositive() ? F.C0 : F.C1)),
              k);
        }
        return callFunction(F.Function(F.Piecewise(F.list(//
            F.list(F.Exp(F.Negate(F.Exp(F.Divide(F.Subtract(a, F.Slot1), b)))), F.Equal(g, F.C0)), //
            F.list(tail, F.And(F.Unequal(g, F.C0), F.Greater(z, F.C0))), //
            F.list(F.C0, F.And(F.Greater(g, F.C0), F.LessEqual(z, F.C0)))), F.C1)), k);
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
        IExpr g = dist.arg3();
        // Piecewise({{a + b*EulerGamma, g == 0},
        // {(-b + a*g + b*Gamma(1 - g))/g, g != 0 && g < 1}}, Indeterminate)
        return F.Piecewise(F.list(//
            F.list(F.Plus(a, F.Times(b, S.EulerGamma)), F.Equal(g, F.C0)), //
            F.list(F.Divide(
                F.Plus(F.Negate(b), F.Times(a, g), F.Times(b, F.Gamma(F.Subtract(F.C1, g)))), g),
                F.And(F.Unequal(g, F.C0), F.Less(g, F.C1)))),
            S.Indeterminate);
      }
      return F.NIL;
    }

    @Override
    public IExpr median(IAST dist) {
      return F.NIL;
    }

    @Override
    public IExpr parameterAssumptions(IAST dist) {
      return dist.isAST3() ? F.Greater(dist.arg2(), F.C0) : F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST3()) {
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        IExpr g = dist.arg3();
        IExpr z = F.Plus(F.C1, F.Divide(F.Times(g, F.Subtract(F.Slot1, a)), b));
        IExpr inverseG = F.Power(g, F.CN1);
        return callFunction(F.Function(F.Piecewise(F.list(//
            F.list(F.Divide(F.Exp(F.Subtract(F.Negate(F.Exp(F.Divide(F.Subtract(a, F.Slot1), b))),
                F.Divide(F.Subtract(F.Slot1, a), b))), b), F.Equal(g, F.C0)), //
            F.list(
                F.Divide(F.Power(z, F.Subtract(F.CN1, inverseG)),
                    F.Times(b, F.Exp(F.Power(z, F.Negate(inverseG))))),
                F.And(F.Unequal(g, F.C0), F.Greater(z, F.C0)))),
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
      if (dist.isAST3()) {
        IExpr b = dist.arg2();
        IExpr g = dist.arg3();
        return F.Piecewise(F.list(//
            F.list(F.Divide(F.Times(F.Sqr(b), F.Sqr(F.Pi)), F.C6), F.Equal(g, F.C0)), //
            F.list(
                F.Divide(F.Times(F.Sqr(b),
                    F.Subtract(F.Gamma(F.Subtract(F.C1, F.Times(F.C2, g))),
                        F.Sqr(F.Gamma(F.Subtract(F.C1, g))))),
                    F.Sqr(g)),
                F.And(F.Unequal(g, F.C0), F.Less(F.Times(F.C2, g), F.C1)))),
            S.Indeterminate);
      }
      return F.NIL;
    }
  }


  /**
   * <code>UniformSumDistribution(n)</code> or <code>UniformSumDistribution(n, {min, max})</code> -
   * the distribution of a sum of <code>n</code> uniformly distributed variables (Irwin-Hall).
   */
  private static final class UniformSumDistribution extends AbstractEvaluator
      implements ICDF, IContinuousDistribution, IPDF, IStatistics {

    /** <code>{n, min, max}</code> */
    private static IExpr[] parameters(IAST dist) {
      if (dist.isAST1()) {
        return new IExpr[] {dist.arg1(), F.C0, F.C1};
      }
      if (dist.isAST2() && dist.arg2().isList2()) {
        IAST minMax = (IAST) dist.arg2();
        return new IExpr[] {dist.arg1(), minMax.arg1(), minMax.arg2()};
      }
      return null;
    }

    /**
     * The Irwin-Hall density on the interval <code>[j, j+1)</code>:
     * <code>Sum((-1)^i*Binomial(n,i)*(x - i)^(n-1), {i, 0, j})/(n-1)!</code>.
     *
     * <p>
     * By the symmetry <code>f(x) == f(n - x)</code> the branches of the upper half are written in
     * terms of <code>n - x</code>, which is also how they are printed in WMA.
     */
    private static IExpr densityBranch(int n, int j, IExpr x) {
      boolean mirror = 2 * j > n - 1;
      IExpr variable = mirror ? F.Subtract(F.ZZ(n), x) : x;
      int last = mirror ? n - 1 - j : j;
      IASTAppendable sum = F.PlusAlloc(last + 1);
      for (int i = 0; i <= last; i++) {
        sum.append(F.Times(F.ZZ(i % 2 == 0 ? 1 : -1), F.Binomial(F.ZZ(n), F.ZZ(i)),
            F.Power(F.Subtract(variable, F.ZZ(i)), F.ZZ(n - 1))));
      }
      return F.Divide(sum, F.Factorial(F.ZZ(n - 1)));
    }

    /**
     * The Irwin-Hall distribution function on <code>[j, j+1)</code>:
     * <code>Sum((-1)^i*Binomial(n,i)*(x - i)^n, {i, 0, j})/n!</code>, mirrored as
     * <code>1 - F(n - x)</code> for the upper half.
     */
    private static IExpr distributionBranch(int n, int j, IExpr x) {
      boolean mirror = 2 * j > n - 1;
      IExpr variable = mirror ? F.Subtract(F.ZZ(n), x) : x;
      int last = mirror ? n - 1 - j : j;
      IASTAppendable sum = F.PlusAlloc(last + 1);
      for (int i = 0; i <= last; i++) {
        sum.append(F.Times(F.ZZ(i % 2 == 0 ? 1 : -1), F.Binomial(F.ZZ(n), F.ZZ(i)),
            F.Power(F.Subtract(variable, F.ZZ(i)), F.ZZ(n))));
      }
      IExpr value = F.Divide(F.Expand(sum), F.Factorial(F.ZZ(n)));
      return mirror ? F.Subtract(F.C1, value) : value;
    }

    /** The condition of the branch <code>[j, j+1)</code>; the last one includes its right end. */
    private static IExpr branchCondition(int n, int j) {
      if (j == n - 1) {
        return F.LessEqual(F.ZZ(j), F.Slot1, F.ZZ(n));
      }
      return F.Inequality(F.ZZ(j), S.LessEqual, F.Slot1, S.Less, F.ZZ(j + 1));
    }

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      IExpr[] p = parameters(dist);
      if (p != null && p[1].isZero() && p[2].isOne()) {
        int n = p[0].toIntDefault();
        if (n > 0 && n <= 32) {
          IASTAppendable branches = F.ListAlloc(n + 1);
          branches.append(F.list(F.C0, F.Less(F.Slot1, F.C0)));
          for (int j = 0; j < n; j++) {
            branches.append(F.list(distributionBranch(n, j, F.Slot1), branchCondition(n, j)));
          }
          return callFunction(F.Function(F.Piecewise(branches, F.C1)), k);
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
      return ARGS_1_2;
    }

    @Override
    public IExpr inverseCDF(IAST dist, IExpr k, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public IExpr mean(IAST dist) {
      IExpr[] p = parameters(dist);
      if (p != null) {
        if (p[1].isZero() && p[2].isOne()) {
          // n/2
          return F.Divide(p[0], F.C2);
        }
        // ((a + b)*n)/2
        return F.Divide(F.Times(F.Plus(p[1], p[2]), p[0]), F.C2);
      }
      return F.NIL;
    }

    @Override
    public IExpr median(IAST dist) {
      IExpr[] p = parameters(dist);
      return p != null ? mean(dist) : F.NIL;
    }

    @Override
    public IExpr parameterAssumptions(IAST dist) {
      IExpr[] p = parameters(dist);
      if (p != null) {
        return F.And(F.Greater(p[0], F.C0), F.Less(p[1], p[2]));
      }
      return F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      IExpr[] p = parameters(dist);
      if (p != null && p[1].isZero() && p[2].isOne()) {
        int n = p[0].toIntDefault();
        if (n > 0 && n <= 32) {
          IASTAppendable branches = F.ListAlloc(n + 1);
          if (n > 1) {
            branches.append(F.list(F.C0, F.Less(F.Slot1, F.C0)));
          }
          for (int j = 0; j < n; j++) {
            branches.append(F.list(densityBranch(n, j, F.Slot1), branchCondition(n, j)));
          }
          return callFunction(F.Function(F.Piecewise(branches, F.C0)), k);
        }
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
      IExpr[] p = parameters(dist);
      if (p != null) {
        if (p[1].isZero() && p[2].isOne()) {
          // n/12
          return F.Divide(p[0], F.ZZ(12));
        }
        // ((b - a)^2*n)/12
        return F.Divide(F.Times(F.Sqr(F.Subtract(p[2], p[1])), p[0]), F.ZZ(12));
      }
      return F.NIL;
    }
  }


  /**
   * <code>HypoexponentialDistribution({lambda1, lambda2, ...})</code> - the distribution of a sum
   * of independent exponentially distributed variables with the given rates.
   */
  private static final class HypoexponentialDistribution extends AbstractEvaluator
      implements IContinuousDistribution, IStatistics {

    /** The list of rates, or <code>null</code> if the argument is not a list of rates. */
    private static IAST rates(IAST dist) {
      if (dist.isAST1() && dist.arg1().isList() && dist.arg1().size() > 1) {
        return (IAST) dist.arg1();
      }
      return null;
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
    public IExpr mean(IAST dist) {
      IAST rates = rates(dist);
      // Sum(1/lambda_i)
      return rates != null ? rates.map(S.Plus, x -> F.Power(x, F.CN1)) : F.NIL;
    }

    /**
     * The median is the solution of <code>SurvivalFunction(x) == 1/2</code>. With
     * <code>u == E^(-x)</code> the survival function <code>Sum(c_i*E^(-lambda_i*x))</code> becomes
     * a polynomial in <code>u</code> whenever all rates are integers, so the median can be solved
     * for exactly.
     */
    @Override
    public IExpr median(IAST dist) {
      IAST rates = rates(dist);
      if (rates == null) {
        return F.NIL;
      }
      for (int i = 1; i < rates.size(); i++) {
        if (!rates.get(i).isInteger() || !rates.get(i).isPositive()) {
          return F.NIL;
        }
      }
      EvalEngine engine = EvalEngine.get();
      ISymbol u = F.Dummy("u");
      // SurvivalFunction == Sum(Product(l_j/(l_j - l_i), j != i)*u^l_i)
      IASTAppendable survival = F.PlusAlloc(rates.argSize());
      for (int i = 1; i < rates.size(); i++) {
        IExpr rate = rates.get(i);
        IASTAppendable coefficient = F.TimesAlloc(rates.argSize());
        for (int j = 1; j < rates.size(); j++) {
          if (j != i) {
            IExpr other = rates.get(j);
            if (other.equals(rate)) {
              // repeated rates need a different partial fraction decomposition
              return F.NIL;
            }
            coefficient.append(F.Divide(other, F.Subtract(other, rate)));
          }
        }
        survival.append(F.Times(coefficient, F.Power(u, rate)));
      }
      IExpr solutions = engine.evaluate(F.Solve(F.Equal(survival, F.C1D2), u));
      if (!solutions.isListOfLists()) {
        return F.NIL;
      }
      IExpr median = F.NIL;
      for (int i = 1; i < solutions.size(); i++) {
        IAST rule = (IAST) solutions.getAt(i);
        if (!rule.isAST1() || !rule.arg1().isRuleAST()) {
          return F.NIL;
        }
        IExpr root = rule.arg1().second();
        IExpr numeric = engine.evaluate(F.N(root));
        if (!numeric.isReal()) {
          continue;
        }
        double value = numeric.evalfNaN();
        if (Double.isNaN(value) || value <= 0.0 || value >= 1.0) {
          continue;
        }
        if (median.isPresent()) {
          // not a unique root in (0, 1): don't guess
          return F.NIL;
        }
        // u == E^(-median)
        median = engine.evaluate(F.Negate(F.Log(root)));
      }
      return median;
    }

    @Override
    public IExpr parameterAssumptions(IAST dist) {
      IAST rates = rates(dist);
      return rates != null ? rates.map(S.And, x -> F.Greater(x, F.C0)) : F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

    @Override
    public IExpr skewness(IAST dist) {
      return F.NIL;
    }

    @Override
    public IExpr variance(IAST dist) {
      IAST rates = rates(dist);
      // Sum(1/lambda_i^2)
      return rates != null ? rates.map(S.Plus, x -> F.Power(x, F.CN2)) : F.NIL;
    }
  }

}
