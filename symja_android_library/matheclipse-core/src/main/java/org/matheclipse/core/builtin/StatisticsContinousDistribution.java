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
import org.matheclipse.core.interfaces.statistics.IPDF;
import org.matheclipse.core.interfaces.statistics.IRandomVariate;
import org.matheclipse.core.interfaces.statistics.IStatistics;

public class StatisticsContinousDistribution {
  private static final class BetaDistribution extends AbstractEvaluator
      implements IContinuousDistribution, IRandomVariate, IStatistics, IPDF, ICDF {

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
          try {
            return F.num(
                new org.hipparchus.distribution.continuous.BetaDistribution(a.evalf(), b.evalf()) //
                    .cumulativeProbability(k.evalf()));
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            //
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
        if (!engine.isArbitraryMode() && //
            (a.isNumericArgument(true) || b.isNumericArgument(true) || k.isNumericArgument(true))) {
          try {
            return F.num(
                new org.hipparchus.distribution.continuous.BetaDistribution(a.evalf(), b.evalf()) //
                    .inverseCumulativeProbability(k.evalf()));
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            //
          }
        }
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
          try {
            return F.num(
                new org.hipparchus.distribution.continuous.BetaDistribution(a.evalf(), b.evalf()) //
                    .density(k.evalf()));
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            //
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
        double p = rhoExpr.evalf();
        covariances = new double[][] {{1.0, p}, {p, 1.0}};
      } else if (dist.isAST2()) {
        IExpr sigma = dist.arg1();
        rhoExpr = dist.arg2();
        double s1 = sigma.first().evalf();
        double s2 = sigma.second().evalf();
        rhoExpr = dist.arg3();
        double p = rhoExpr.evalf();
        double cov12 = p * s1 * s2;
        covariances = new double[][] {{s1 * s1, cov12}, {cov12, s2 * s2}};
      } else if (dist.isAST3()) {
        mu = dist.arg1();
        double mu1 = mu.first().evalf();
        double mu2 = mu.second().evalf();
        means = new double[] {mu1, mu2};
        IExpr sigma = dist.arg2();
        double s1 = sigma.first().evalf();
        double s2 = sigma.second().evalf();
        rhoExpr = dist.arg3();
        double p = rhoExpr.evalf();
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
      implements ICDF, IContinuousDistribution, IPDF, IStatistics, IRandomVariate, ICentralMoment {

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
        double ad = a.evalf();
        double bd = b.evalf();
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
      implements ICDF, IContinuousDistribution, IPDF, IStatistics, IRandomVariate, ICentralMoment {

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST1()) {
        IExpr v = dist.arg1();
        if (!engine.isArbitraryMode() && //
            (v.isNumericArgument(true) || k.isNumericArgument(true))) {
          try {
            return F
                .num(new org.hipparchus.distribution.continuous.ChiSquaredDistribution(v.evalf()) //
                    .cumulativeProbability(k.evalf()));
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            //
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
        if (!engine.isArbitraryMode() && //
            (v.isNumericArgument(true) || k.isNumericArgument(true))) {
          try {
            return F
                .num(new org.hipparchus.distribution.continuous.ChiSquaredDistribution(v.evalf()) //
                    .inverseCumulativeProbability(k.evalf()));
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            //
          }
        }
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
        F.Times(F.C2, F.InverseGammaRegularized(F.Times(F.C1D2, v), F.C0, F.C1D2)); // $$;
      }
      return F.NIL;
    }

    @Override
    public IExpr pdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST1()) {
        IExpr v = dist.arg1();
        if (!engine.isArbitraryMode() && //
            (v.isNumericArgument(true) || k.isNumericArgument(true))) {
          try {
            return F
                .num(new org.hipparchus.distribution.continuous.ChiSquaredDistribution(v.evalf()) //
                    .density(k.evalf()));
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            //
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
        double v = dist.arg1().evalf();
        // see exception handling in RandonmVariate() function
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
          try {
            double x = k.evalf();
            return F.num(empiricalDistribution.cumulativeProbability(x));
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            //
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
      ast.builtinEvaled();
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
          try {
            double x = k.evalf();
            return F.num(empiricalDistribution.density(x));
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            //
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
      implements ICDF, IContinuousDistribution, IPDF, IStatistics {

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
      implements ICDF, IContinuousDistribution, IPDF, IStatistics, IRandomVariate, ICentralMoment {

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST1()) {
        IExpr n = dist.arg1();
        if (!engine.isArbitraryMode() && //
            (n.isNumericArgument(true) || k.isNumericArgument(true))) {
          try {
            double x = k.evalf();
            if (x <= 0.0) {
              return F.CD0;
            }
            return F.num(1.0 - Math.exp(-x * n.evalf()));

            // return F.num(new
            // org.hipparchus.distribution.continuous.ExponentialDistribution(n.evalDouble()) //
            // .cumulativeProbability(k.evalDouble()));
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            //
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
          try {
            double x = k.evalf();
            if (F.isEqual(x, 1.0)) {
              return F.CInfinity;
            }
            return F.num(-Math.log(1.0 - x) / n.evalf());
            // return F.num(new
            // org.hipparchus.distribution.continuous.ExponentialDistribution(n.evalDouble()) //
            // .inverseCumulativeProbability(k.evalDouble()));
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            //
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
        double rate = dist.arg1().evalf();
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
          try {
            return F
                .num(new org.hipparchus.distribution.continuous.FDistribution(n.evalf(), m.evalf()) //
                    .cumulativeProbability(k.evalf()));
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            //
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
        if (!engine.isArbitraryMode() && //
            (n.isNumericArgument(true) || m.isNumericArgument(true) || k.isNumericArgument(true))) {
          try {
            return F
                .num(new org.hipparchus.distribution.continuous.FDistribution(n.evalf(), m.evalf()) //
                    .inverseCumulativeProbability(k.evalf()));
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            //
          }
        }
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
        F.Times(m, F.Power(n, F.CN1),
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
          try {
            return F
                .num(new org.hipparchus.distribution.continuous.FDistribution(n.evalf(), m.evalf()) //
                    .density(k.evalf()));
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            //
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
          return m.times(S.Power.of(F.num(uniform), n.reciprocal().negate()));
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


  private static final class GammaDistribution extends AbstractEvaluator
      implements ICentralMoment, IContinuousDistribution, IRandomVariate, IStatistics, IPDF, ICDF {

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2()) {
        //
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        if (!engine.isArbitraryMode() && //
            (a.isNumericArgument(true) || b.isNumericArgument(true) || k.isNumericArgument(true))) {
          try {
            return F.num(
                new org.hipparchus.distribution.continuous.GammaDistribution(a.evalf(), b.evalf()) //
                    .cumulativeProbability(k.evalf()));
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            //
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
        if (!engine.isArbitraryMode() && //
            (a.isNumericArgument(true) || b.isNumericArgument(true) || k.isNumericArgument(true))) {
          try {
            return F.num(
                new org.hipparchus.distribution.continuous.GammaDistribution(a.evalf(), b.evalf()) //
                    .inverseCumulativeProbability(k.evalf()));
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            //
          }
        }
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
          try {
            return F.num(
                new org.hipparchus.distribution.continuous.GammaDistribution(a.evalf(), b.evalf()) //
                    .density(k.evalf()));
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            //
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
        double a = dist.arg1().evalf();
        double b = dist.arg2().evalf();

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
        double lambda = dist.arg1().evalf();
        double xi = dist.arg2().evalf();
        double reference = random.nextDouble();
        double uniform = Math.nextUp(reference);
        double result = Math.log((xi - Math.log(uniform)) / xi) / lambda;
        return F.num(result);
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

    @Override
    public IExpr skewness(IAST dist) {
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
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
  private static final class GumbelDistribution extends AbstractEvaluator
      implements ICDF, IContinuousDistribution, IPDF, IStatistics, IRandomVariate {

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        if (!engine.isArbitraryMode() && //
            (n.isNumericArgument(true) || m.isNumericArgument(true) || k.isNumericArgument(true))) {
          try {
            final double z = (k.evalf() - n.evalf()) / m.evalf();
            return F.num(1.0 - Math.exp(-Math.exp(z)));
            // return F.num(1.0-new
            // org.hipparchus.distribution.continuous.GumbelDistribution(n.evalDouble(),
            // m.evalDouble()) //
            // .cumulativeProbability(k.evalDouble()));
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            //
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
    public IExpr inverseCDF(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        if (!engine.isArbitraryMode() && //
            (n.isNumericArgument(true) || m.isNumericArgument(true) || k.isNumericArgument(true))) {
          try {
            double p = k.evalf();
            MathUtils.checkRangeInclusive(p, 0, 1);
            if (F.isZero(p)) {
              return F.CNInfinity;
            } else if (F.isEqual(p, 1.0)) {
              return F.CInfinity;
            }
            return F.num(n.evalf() + m.evalf() * Math.log(-Math.log(1.0 - p)));
            // return F.num(new
            // org.hipparchus.distribution.continuous.GumbelDistribution(n.evalDouble(),
            // m.evalDouble()) //
            // .inverseCumulativeProbability(k.evalDouble()));
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            //
          }
        }
        IExpr function =
            // [$ ( ConditionalExpression(Piecewise({{n + m*Log(-Log(1 - #)), 0 < # < 1},
            // {-Infinity, # <=
            // 0}}, Infinity), 0 <= # <= 1)& ) $]
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
      if (dist.isAST2()) {
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
      if (dist.isAST2()) {
        // see exception handling in RandonmVariate() function
        double n = dist.arg1().evalf();
        double m = dist.arg2().evalf();
        // avoid result -Infinity when reference is close to 1.0
        // double reference = random.nextDouble();
        // double uniform = reference == NEXTDOWNONE ? NEXTDOWNONE : Math.nextUp(reference);
        // uniform = Math.log(-Math.log(uniform));
        // return m.add(n.times(F.num(uniform)));
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
      if (dist.isAST2()) {
        return
        // [$ -((12*Sqrt(6)*Zeta(3))/Pi^3) $]
        F.Times(F.CN1, F.ZZ(12L), F.CSqrt6, F.Power(F.Pi, F.CN3), F.Zeta(F.C3)); // $$;
      }
      return F.NIL;
    }

    @Override
    public IExpr variance(IAST dist) {
      if (dist.isAST2()) {
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

      S.EmpiricalDistribution.setEvaluator(new EmpiricalDistribution());
      S.ErlangDistribution.setEvaluator(new ErlangDistribution());
      S.ExponentialDistribution.setEvaluator(new ExponentialDistribution());
      S.FRatioDistribution.setEvaluator(new FRatioDistribution());
      S.FrechetDistribution.setEvaluator(new FrechetDistribution());
      S.GammaDistribution.setEvaluator(new GammaDistribution());
      S.GompertzMakehamDistribution.setEvaluator(new GompertzMakehamDistribution());
      S.GumbelDistribution.setEvaluator(new GumbelDistribution());
      S.InverseGammaDistribution.setEvaluator(new InverseGammaDistribution());
      S.LogNormalDistribution.setEvaluator(new LogNormalDistribution());
      S.MultinormalDistribution.setEvaluator(new MultinormalDistribution());
      S.NakagamiDistribution.setEvaluator(new NakagamiDistribution());
      S.NormalDistribution.setEvaluator(new NormalDistribution());
      S.ParetoDistribution.setEvaluator(new ParetoDistribution());
      S.StudentTDistribution.setEvaluator(new StudentTDistribution());
      S.UniformDistribution.setEvaluator(new UniformDistribution());
      S.WeibullDistribution.setEvaluator(new WeibullDistribution());
    }
  }

  private static final class InverseGammaDistribution extends AbstractEvaluator
      implements IContinuousDistribution, IRandomVariate, IStatistics, IPDF, ICDF {

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2()) {
        //
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        if (!engine.isArbitraryMode() && //
            (a.isNumericArgument(true) || b.isNumericArgument(true) || k.isNumericArgument(true))) {
          try {
            return F.num(new org.hipparchus.distribution.continuous.InvGammaDistribution(a.evalf(),
                b.evalf()) //
                    .cumulativeProbability(k.evalf()));
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            //
          }
        }
        IExpr function = //
            F.Function(F.Piecewise(
                F.list(F.list(F.GammaRegularized(a, F.Times(b, F.Power(F.Slot1, F.CN1)))),
                    F.Greater(F.Slot1, F.C0))),
                F.C0);


        return callFunction(function, k);
      } else if (dist.isAST(S.InverseGammaDistribution, 5)) {
        //
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        IExpr g = dist.arg3();
        IExpr d = dist.arg4();
        IExpr function = //
            F.Function(F.Piecewise(F.list(F.list(
                F.GammaRegularized(a,
                    F.Power(F.Times(b, F.Power(F.Plus(F.Negate(d), F.Slot1), F.CN1)), g)),
                F.Greater(F.Slot1, d))), F.C0));
        return callFunction(function, k);
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
        if (!engine.isArbitraryMode() && //
            (a.isNumericArgument(true) || b.isNumericArgument(true) || k.isNumericArgument(true))) {
          try {
            return F.num(new org.hipparchus.distribution.continuous.InvGammaDistribution(a.evalf(),
                b.evalf()) //
                    .inverseCumulativeProbability(k.evalf()));
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            //
          }
        }
        IExpr function = //
            F.Function(
                F.ConditionalExpression(F.Piecewise(
                    F.list(F.list(F.Times(b, F.Power(F.InverseGammaRegularized(a, F.Slot1), F.CN1)),
                        F.Less(F.C0, F.Slot1, F.C1)), F.list(F.C0, F.LessEqual(F.Slot1, F.C0))),
                    F.oo), F.LessEqual(F.C0, F.Slot1, F.C1)));
        return callFunction(function, k);
      } else if (dist.isAST(S.GammaDistribution, 5)) {
        //
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        IExpr g = dist.arg3();
        IExpr d = dist.arg4();
        IExpr function = //
            F.Function(
                F.ConditionalExpression(
                    F.Piecewise(
                        F.list(
                            F.list(
                                F.Plus(d,
                                    F.Times(b,
                                        F.Power(F.Power(F.InverseGammaRegularized(a, F.Slot1),
                                            F.Power(g, F.CN1)), F.CN1))),
                                F.Less(F.C0, F.Slot1, F.C1)),
                            F.list(d, F.LessEqual(F.Slot1, F.C0))),
                        F.oo),
                    F.LessEqual(F.C0, F.Slot1, F.C1)));
        return callFunction(function, k);
      }
      return F.NIL;
    }

    @Override
    public IExpr mean(IAST dist) {
      if (dist.isAST2()) {
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        // Piecewise({{b/(-1+a),a>1}},Indeterminate)
        return F.Piecewise(
            F.list(F.list(F.Times(F.Power(F.Plus(F.CN1, a), F.CN1), b), F.Greater(a, F.C1))),
            F.Indeterminate);
      }
      if (dist.size() == 5) {
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        IExpr g = dist.arg3();
        IExpr d = dist.arg4();
        // Piecewise({{d+(b*Gamma(a-1/g))/Gamma(a),a*g>1}},Indeterminate)
        return F
            .Piecewise(F.list(F.list(
                F.Plus(d,
                    F.Times(b, F.Power(F.Gamma(a), F.CN1),
                        F.Gamma(F.Subtract(a, F.Power(g, F.CN1))))),
                F.Greater(F.Times(a, g), F.C1))), S.Indeterminate);
      }
      return F.NIL;
    }

    @Override
    public IExpr median(IAST dist) {
      if (dist.isAST2()) {
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        // b/InverseGammaRegularized(a,1/2)
        return F.Times(b, F.Power(F.InverseGammaRegularized(a, F.C1D2), F.CN1));
      }
      if (dist.size() == 5) {
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        IExpr g = dist.arg3();
        IExpr d = dist.arg4();
        // d+b*((1/InverseGammaRegularized(a,1/2)))^(1/g)
        return F.Plus(d, F.Times(b,
            F.Power(F.Power(F.InverseGammaRegularized(a, F.C1D2), F.CN1), F.Power(g, F.CN1))));
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
          try {
            return F.num(new org.hipparchus.distribution.continuous.InvGammaDistribution(a.evalf(),
                b.evalf()) //
                    .density(k.evalf()));
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            //
          }
        }
        IExpr function = //
            // Piecewise({{(b/#)^a/(E^(b/#)*#*Gamma(a)),#>0}},0)&
            F.Function(
                F.Piecewise(
                    F.list(F.list(
                        F.Times(
                            F.Power(F.Times(F.Exp(F.Times(b, F.Power(F.Slot1, F.CN1))), F.Slot1,
                                F.Gamma(a)), F.CN1),
                            F.Power(F.Times(b, F.Power(F.Slot1, F.CN1)), a)),
                        F.Greater(F.Slot1, F.C0))),
                    F.C0));
        return callFunction(function, k);
      } else if (dist.isAST(S.InverseGammaDistribution, 5)) {
        //
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        IExpr g = dist.arg3();
        IExpr d = dist.arg4();
        IExpr function = //
            // Piecewise({{(g*(b/(-d+#1))^(1+a*g))/(E^(b/(-d+#1))^g*b*Gamma(a)),#1>d}},0)&
            F.Function(F.Piecewise(F.list(F.list(F.Times(g,
                F.Power(F.Times(
                    F.Exp(F.Power(F.Times(b, F.Power(F.Plus(F.Negate(d), F.Slot1), F.CN1)), g)), b,
                    F.Gamma(a)), F.CN1),
                F.Power(F.Times(b, F.Power(F.Plus(F.Negate(d), F.Slot1), F.CN1)),
                    F.Plus(F.C1, F.Times(a, g)))),
                F.Greater(F.Slot1, d))), F.C0));
        return callFunction(function, k);
      }

      return F.NIL;
    }

    @Override
    public IExpr randomVariate(Random random, IAST dist, int size) {
      if (dist.isAST2()) {
        // see exception handling in RandomVariate() function
        double a = dist.arg1().evalf();
        double b = dist.arg2().evalf();

        // TODO cache RandomDataGenerator instance
        RandomDataGenerator rdg = new RandomDataGenerator();
        double[] vector = rdg.nextDeviates( //
            new org.hipparchus.distribution.continuous.InvGammaDistribution(a, b), //
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
        IExpr a = dist.arg1();
        // Piecewise({{(4*Sqrt(-2+a))/(-3+a),a>3}},Indeterminate)
        return F.Piecewise(
            F.list(F.list(F.Times(F.C4, F.Power(F.Plus(F.CN3, a), F.CN1), F.Sqrt(F.Plus(F.CN2, a))),
                F.Greater(a, F.C3))),
            F.Indeterminate);
      } else if (dist.isAST(S.InverseGammaDistribution, 5)) {
        //
        IExpr a = dist.arg1();
        IExpr g = dist.arg3();
        // Piecewise({{(Gamma(a)^2*Gamma(a-3/g)-3*Gamma(a)*Gamma(a-2/g)*Gamma(a-1/g)+2*Gamma(a-
        // 1/g)^3)/(Gamma(a)*Gamma(a-2/g)-Gamma(a-1/g)^2)^(3/2),a*g>3}},Indeterminate)
        return F
            .Piecewise(
                F.list(
                    F.list(
                        F.Times(
                            F.Power(
                                F.Subtract(
                                    F.Times(F.Gamma(a),
                                        F.Gamma(F.Plus(a, F.Times(F.CN2, F.Power(g, F.CN1))))),
                                    F.Sqr(F.Gamma(F.Subtract(a, F.Power(g, F.CN1))))),
                                F.QQ(-3L, 2L)),
                            F.Plus(
                                F.Times(F.Sqr(F.Gamma(a)),
                                    F.Gamma(F.Plus(a, F.Times(F.CN3, F.Power(g, F.CN1))))),
                                F.Times(
                                    F.CN3, F.Gamma(a),
                                    F.Gamma(F.Plus(a, F.Times(F.CN2, F.Power(g, F.CN1)))),
                                    F.Gamma(F.Subtract(a, F.Power(g, F.CN1)))),
                                F.Times(F.C2,
                                    F.Power(F.Gamma(F.Subtract(a, F.Power(g, F.CN1))), F.C3)))),
                        F.Greater(F.Times(a, g), F.C3))),
                S.Indeterminate);

      }
      return F.NIL;
    }

    @Override
    public IExpr variance(IAST dist) {
      if (dist.isAST2()) {
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        // Piecewise({{b^2/((-2+a)*(-1+a)^2),a>2}},Indeterminate)
        return F.Piecewise(F.list(F.list(
            F.Times(F.Power(F.Times(F.Plus(F.CN2, a), F.Sqr(F.Plus(F.CN1, a))), F.CN1), F.Sqr(b)),
            F.Greater(a, F.C2))), S.Indeterminate);
      } else if (dist.isAST(S.InverseGammaDistribution, 5)) {
        //
        IExpr a = dist.arg1();
        IExpr b = dist.arg2();
        IExpr g = dist.arg3();
        // Piecewise({{(b^2*(Gamma(a)*Gamma(a-2/g)-Gamma(a-1/g)^2))/Gamma(a)^2,a*g>2}},Indeterminate)
        return F
            .Piecewise(
                F.list(F.list(
                    F.Times(F.Sqr(b), F.Power(F.Gamma(a), F.CN2),
                        F.Subtract(
                            F.Times(F.Gamma(a),
                                F.Gamma(F.Plus(a, F.Times(F.CN2, F.Power(g, F.CN1))))),
                            F.Sqr(F.Gamma(F.Subtract(a, F.Power(g, F.CN1)))))),
                    F.Greater(F.Times(a, g), F.C2))),
                F.Indeterminate);

      }
      return F.NIL;
    }
  }

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
          try {
            return F.num(new org.hipparchus.distribution.continuous.LogNormalDistribution(n.evalf(),
                m.evalf()) //
                    .cumulativeProbability(k.evalf()));
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            //
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
          try {
            return F.num(new org.hipparchus.distribution.continuous.LogNormalDistribution(n.evalf(),
                m.evalf()) //
                    .inverseCumulativeProbability(k.evalf()));
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            //
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
          try {
            return F.num(new org.hipparchus.distribution.continuous.LogNormalDistribution(n.evalf(),
                m.evalf()) //
                    .density(k.evalf()));
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            //
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
        double mean = dist.arg1().evalf();
        double sigma = dist.arg2().evalf();
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
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
        if (!engine.isArbitraryMode() && //
            (n.isNumericArgument(true) || m.isNumericArgument(true) || k.isNumericArgument(true))) {
          try {
            return F.num(new org.hipparchus.distribution.continuous.NakagamiDistribution(n.evalf(),
                m.evalf()) //
                    .cumulativeProbability(k.evalf()));
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            //
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
        if (!engine.isArbitraryMode() && //
            (n.isNumericArgument(true) || m.isNumericArgument(true) || k.isNumericArgument(true))) {
          try {
            return F.num(new org.hipparchus.distribution.continuous.NakagamiDistribution(n.evalf(),
                m.evalf()) //
                    .inverseCumulativeProbability(k.evalf()));
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            //
          }
        }
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
          try {
            return F.num(new org.hipparchus.distribution.continuous.NakagamiDistribution(n.evalf(),
                m.evalf()) //
                    .density(k.evalf()));
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            //
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
        double n = dist.arg1().evalf();
        double m = dist.arg2().evalf();

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
  private static final class NormalDistribution extends AbstractEvaluator
      implements ICentralMoment, IContinuousDistribution, IStatistics, IRandomVariate, IPDF, ICDF {

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
          try {
            return F.num(
                new org.hipparchus.distribution.continuous.NormalDistribution(n.evalf(), m.evalf()) //
                    .cumulativeProbability(k.evalf()));
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            //
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
          try {
            return F.num(
                new org.hipparchus.distribution.continuous.NormalDistribution(n.evalf(), m.evalf()) //
                    .inverseCumulativeProbability(k.evalf()));
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            //
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
          try {
            return F.num(
                new org.hipparchus.distribution.continuous.NormalDistribution(n.evalf(), m.evalf()) //
                    .density(k.evalf()));
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            //
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
        double mean = dist.arg1().evalf();
        double sigma = dist.arg2().evalf();
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


  private static final class StudentTDistribution extends AbstractEvaluator
      implements ICDF, IContinuousDistribution, IPDF, IStatistics {

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      if (dist.isAST1()) {
        IExpr n = dist.arg1();
        if (!engine.isArbitraryMode() && //
            (n.isNumericArgument(true) || k.isNumericArgument(true))) {
          try {
            return F.num(new org.hipparchus.distribution.continuous.TDistribution(n.evalf()) //
                .cumulativeProbability(k.evalf()));
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            //
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
        IExpr n = dist.arg1();
        if (!engine.isArbitraryMode() && //
            (n.isNumericArgument(true) || k.isNumericArgument(true))) {
          try {
            return F.num(new org.hipparchus.distribution.continuous.TDistribution(n.evalf()) //
                .inverseCumulativeProbability(k.evalf()));
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            //
          }
        }
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
      }
      return F.NIL;
    }

    @Override
    public IExpr mean(IAST dist) {

      if (dist.isAST1()) {
        IExpr arg1 = dist.arg1();
        if (EvalEngine.get().isDoubleMode() || arg1.isNumericArgument(true)) {
          return F.num(new org.hipparchus.distribution.continuous.TDistribution(arg1.evalf())
              .getNumericalMean());
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
          try {
            return F.num(new org.hipparchus.distribution.continuous.TDistribution(n.evalf()) //
                .density(k.evalf()));
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            //
          }
        }
        IExpr function =
            // [$ (n/(#^2 + n))^((1 + n)/2)/(Sqrt(n)*Beta(n/2, 1/2)) & $]
            F.Function(F.Times(
                F.Power(F.Times(n, F.Power(F.Plus(F.Sqr(F.Slot1), n), F.CN1)),
                    F.Times(F.C1D2, F.Plus(F.C1, n))),
                F.Power(F.Times(F.Sqrt(n), F.Beta(F.Times(F.C1D2, n), F.C1D2)), F.CN1))); // $$;
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
        // Piecewise({{0, n > 3}}, Indeterminate)
        return F.Piecewise(F.list(F.list(F.C0, F.Greater(n, F.C3))), S.Indeterminate);
      }
      return F.NIL;
    }

    @Override
    public IExpr variance(IAST dist) {
      if (dist.isAST1()) {
        IExpr n = dist.arg1();
        if (EvalEngine.get().isDoubleMode() || n.isNumericArgument(true)) {
          return F.num(new org.hipparchus.distribution.continuous.TDistribution(n.evalf())
              .getNumericalVariance());
        }
        return F.Piecewise(F.list(F.list(F.Divide(n, F.Plus(F.CN2, n)), F.Greater(n, F.C2))),
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
  private static final class UniformDistribution extends AbstractEvaluator
      implements IContinuousDistribution, IStatistics, ICDF, IPDF, IRandomVariate {

    @Override
    public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
      IExpr[] minMax = minmax(dist);
      if (minMax != null) {
        IExpr a = minMax[0];
        IExpr b = minMax[1];
        if (!engine.isArbitraryMode() && //
            (a.isNumericArgument(true) || b.isNumericArgument(true) || k.isNumericArgument(true))) {
          try {
            return F
                .num(new org.hipparchus.distribution.continuous.UniformRealDistribution(a.evalf(),
                    b.evalf()) //
                        .cumulativeProbability(k.evalf()));
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            //
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
        if (!engine.isArbitraryMode() && //
            (a.isNumericArgument(true) || b.isNumericArgument(true) || k.isNumericArgument(true))) {
          try {
            return F
                .num(new org.hipparchus.distribution.continuous.UniformRealDistribution(a.evalf(),
                    b.evalf()) //
                        .inverseCumulativeProbability(k.evalf()));
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            //
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
        double min = minMax[0].evalf();
        double max = minMax[1].evalf();
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
          try {
            return F.num(
                new org.hipparchus.distribution.continuous.WeibullDistribution(n.evalf(), m.evalf()) //
                    .cumulativeProbability(k.evalf()));
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            //
          }
        }
        IExpr function =
            // [$ Piecewise({{1 - E^(-(#/m)^n),# > 0}}, 0) & $]
            F.Function(F.Piecewise(F.list(F.list(
                F.Subtract(F.C1, F.Exp(F.Negate(F.Power(F.Times(F.Power(m, F.CN1), F.Slot1), n)))),
                F.Greater(F.Slot1, F.C0))), F.C0)); // $$;
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
          try {
            return F.num(
                new org.hipparchus.distribution.continuous.WeibullDistribution(n.evalf(), m.evalf()) //
                    .inverseCumulativeProbability(k.evalf()));
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            //
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
          try {
            return F.num(
                new org.hipparchus.distribution.continuous.WeibullDistribution(n.evalf(), m.evalf()) //
                    .density(k.evalf()));
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            //
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
      }
      return F.NIL;
    }

    @Override
    public IExpr randomVariate(Random random, IAST dist, int size) {
      if (dist.isAST2()) {
        double n = dist.arg1().evalf();
        double m = dist.arg2().evalf();
        // see exception handling in RandonmVariate() function
        RandomDataGenerator rdg = new RandomDataGenerator();
        double[] vector = rdg.nextDeviates(
            new org.hipparchus.distribution.continuous.WeibullDistribution(n, m), size);
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
        //
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
      if (dist.isAST2()) {
        IExpr n = dist.arg1();
        IExpr m = dist.arg2();
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

}
