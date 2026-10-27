package org.matheclipse.core.reflection.system;

import java.util.Random;
import org.hipparchus.random.RandomDataGenerator;
import org.matheclipse.core.builtin.RandomFunctions;
import org.matheclipse.core.builtin.StatisticsContinousDistribution;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.interfaces.statistics.ICDF;
import org.matheclipse.core.interfaces.statistics.ICentralMoment;
import org.matheclipse.core.interfaces.statistics.IContinuousDistribution;
import org.matheclipse.core.interfaces.statistics.ICovariance;
import org.matheclipse.core.interfaces.statistics.IPDF;
import org.matheclipse.core.interfaces.statistics.IRandomVariate;
import org.matheclipse.core.interfaces.statistics.IStatistics;

public class MultivariateTDistribution extends AbstractEvaluator implements IContinuousDistribution,
    ICDF, ICovariance, IPDF, IStatistics, ICentralMoment, IRandomVariate {

  public MultivariateTDistribution() {}

  @Override
  public IExpr cdf(IAST dist, IExpr k, EvalEngine engine) {
    // No simple closed form for the CDF of a general MultivariateTDistribution.
    return F.NIL;
  }

  @Override
  public IExpr centralMoment(IAST dist, IExpr m, EvalEngine engine) {
    return F.NIL;
  }

  @Override
  public IExpr covariance(IAST dist, EvalEngine engine) {
    IExpr sigma = F.NIL;
    IExpr nu = F.NIL;
    if (dist.isAST2()) {
      sigma = dist.arg1();
      nu = dist.arg2();
    } else if (dist.isAST3()) {
      sigma = dist.arg2();
      nu = dist.arg3();
    }
    if (sigma.isPresent() && nu.isPresent()) {
      // (nu / (nu - 2)) * sigma for nu > 2
      IExpr cov = F.Times(F.Divide(nu, F.Subtract(nu, F.C2)), sigma);
      return F.Piecewise(F.list(F.list(cov, F.Greater(nu, F.C2))), S.Indeterminate);
    }
    return F.NIL;
  }

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_3;
  }

  @Override
  public IExpr inverseCDF(IAST dist, IExpr k, EvalEngine engine) {
    return F.NIL;
  }

  @Override
  public IExpr kurtosis(IAST dist, EvalEngine engine) {
    int dimension = dimension(dist);
    if (dimension >= 0) {
      IExpr nu = nu(dist);
      // Marginal kurtosis is 3 + 6/(nu - 4) for nu > 4
      IExpr kVal = F.Plus(F.C3, F.Divide(F.C6, F.Subtract(nu, F.C4)));
      IExpr kVec = F.constantArray(kVal, dimension);
      return F.Piecewise(F.list(F.list(kVec, F.Greater(nu, F.C4))), S.Indeterminate);
    }
    return F.NIL;
  }

  /**
   * The dimension of <code>MultivariateTDistribution(sigma, nu)</code> or
   * <code>MultivariateTDistribution(mu, sigma, nu)</code>: the size of the square matrix
   * <code>sigma</code> or the length of the vector <code>mu</code>.
   *
   * @return <code>-1</code> if it is not known
   */
  private static int dimension(IAST dist) {
    if (dist.isAST2()) {
      int[] dims = dist.arg1().isMatrix();
      return dims != null && dims[0] == dims[1] ? dims[0] : -1;
    }
    if (dist.isAST3() && dist.arg1().isList()) {
      return dist.arg1().argSize();
    }
    return -1;
  }

  /** The degrees of freedom <code>nu</code>, the last argument of the distribution. */
  private static IExpr nu(IAST dist) {
    return dist.isAST2() || dist.isAST3() ? dist.last() : F.NIL;
  }

  @Override
  public IExpr mean(IAST dist) {
    IExpr mu = F.NIL;
    IExpr nu = F.NIL;
    if (dist.isAST2()) {
      IExpr sigma = dist.arg1();
      nu = dist.arg2();
      int[] dims = sigma.isMatrix(false);
      if (dims == null || dims[0] != dims[1]) {
        return F.NIL;
      }
      mu = F.constantArray(F.C0, dims[0]);
      return F.Piecewise(F.list(F.list(mu, F.Greater(nu, F.C1))), S.Indeterminate);
    } else if (dist.isAST3()) {
      mu = dist.arg1();
      IExpr sigma = dist.arg2();
      nu = dist.arg3();
      int[] dims = sigma.isMatrix(false);
      if (dims == null || dims[0] != dims[1]) {
        return F.NIL;
      }
      return F.Piecewise(F.list(F.list(mu, F.Greater(nu, F.C1))), S.Indeterminate);
    }
    return F.NIL;
  }

  @Override
  public IExpr median(IAST dist) {
    // The median vector of a multivariate t-distribution is identically equal to its mean
    return mean(dist);
  }

  @Override
  public IExpr pdf(IAST dist, IExpr X, EvalEngine engine) {
    IExpr mu = F.NIL;
    IExpr sigma = F.NIL;
    IExpr nu = F.NIL;

    if (dist.isAST2()) {
      sigma = dist.arg1();
      nu = dist.arg2();
      int[] dims = sigma.isMatrix();
      if (dims != null && dims[0] == dims[1]) {
        mu = F.constantArray(F.C0, dims[0]);
      }
    } else if (dist.isAST3()) {
      mu = dist.arg1();
      sigma = dist.arg2();
      nu = dist.arg3();
    }

    int[] dims = sigma.isMatrix();
    if (mu.isPresent() && dims != null && dims[0] == dims[1]) {
      int p = dims[0];
      IExpr pExpr = F.ZZ(p);

      IExpr nuPlusPOver2 = F.Times(F.C1D2, F.Plus(nu, pExpr));
      IExpr nuOver2 = F.Times(F.C1D2, nu);

      // Gamma((nu + p)/2) / (Gamma(nu/2) * (nu * Pi)^(p/2) * Sqrt(Det(Sigma)))
      IExpr numGamma = F.Gamma(nuPlusPOver2);
      IExpr denGamma = F.Gamma(nuOver2);
      IExpr denConst = F.Power(F.Times(nu, S.Pi), F.Times(F.C1D2, pExpr));
      IExpr denDet = F.Sqrt(F.Det(sigma));

      IExpr preFactor = F.Divide(numGamma, F.Times(denGamma, denConst, denDet));

      // [1 + 1/nu * (X - mu)^T * Inverse(Sigma) * (X - mu)]^(-(nu + p)/2)
      IExpr diff = F.Subtract(X, mu);
      IExpr invSigma = F.Inverse(sigma);
      IExpr quadForm = F.Dot(diff, invSigma, diff);

      IExpr base = F.Plus(F.C1, F.Times(F.Power(nu, F.CN1), quadForm));
      IExpr exponent = F.Negate(nuPlusPOver2);

      IExpr term = F.Power(base, exponent);

      return F.Times(preFactor, term);
    }
    return F.NIL;
  }

  @Override
  public IExpr randomVariate(Random random, IAST dist, int size) {
    try {
      double[] meanVector;
      double[][] covMatrix;
      double nu;

      if (dist.isAST2()) {
        IExpr sigmaExpr = dist.arg1();
        IExpr nuExpr = dist.arg2();
        covMatrix = sigmaExpr.toDoubleMatrix();
        if (covMatrix == null) {
          return F.NIL;
        }
        nu = nuExpr.evalfNaN();
        int dim = covMatrix.length;
        meanVector = new double[dim]; // Initialize to zero vector
      } else if (dist.isAST3()) {
        IExpr muExpr = dist.arg1();
        IExpr sigmaExpr = dist.arg2();
        IExpr nuExpr = dist.arg3();
        meanVector = muExpr.toDoubleVector();
        covMatrix = sigmaExpr.toDoubleMatrix();
        if (meanVector == null || covMatrix == null) {
          return F.NIL;
        }
        nu = nuExpr.evalfNaN();
      } else {
        return F.NIL;
      }

      if (nu > 0) {
        // Hipparchus decoupled RNG requires RandomDataGenerator for standard deviates
        RandomDataGenerator rdg = RandomFunctions.randomDataGenerator(random);

        // Setup the ChiSquared distribution and generate all Y deviates at once
        org.hipparchus.distribution.continuous.ChiSquaredDistribution chiSq =
            new org.hipparchus.distribution.continuous.ChiSquaredDistribution(nu);
        double[] yValues = rdg.nextDeviates(chiSq, size);

        // Setup the Multivariate Normal distribution and its Sampler for Z vectors
        org.hipparchus.distribution.multivariate.MultivariateNormalDistribution mnd =
            new org.hipparchus.distribution.multivariate.MultivariateNormalDistribution(RandomFunctions.hipparchusGenerator(random),
                
                new double[meanVector.length], covMatrix);

        IASTAppendable list = F.ListAlloc(size);
        for (int i = 0; i < size; i++) {
          double y = yValues[i];
          double[] z = mnd.sample();
          double factor = Math.sqrt(nu / y);

          double[] sample = new double[meanVector.length];
          for (int j = 0; j < meanVector.length; j++) {
            sample[j] = meanVector[j] + z[j] * factor;
          }
          list.append(F.List(sample));
        }
        return list;
      }
    } catch (RuntimeException rex) {
      // Fallback for symbolic parameters or non-positive-definite matrices
    }
    return F.NIL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.NOATTRIBUTE);
  }

  @Override
  public IExpr skewness(IAST dist) {
    int dimension = dimension(dist);
    if (dimension >= 0) {
      IExpr zeros = F.constantArray(F.C0, dimension);
      return F.Piecewise(F.list(F.list(zeros, F.Greater(nu(dist), F.C3))), S.Indeterminate);
    }
    return F.NIL;
  }

  @Override
  public IExpr variance(IAST dist) {
    IExpr sigma = F.NIL;
    IExpr nu = F.NIL;
    if (dist.isAST2()) {
      sigma = dist.arg1();
      nu = dist.arg2();
    } else if (dist.isAST3()) {
      sigma = dist.arg2();
      nu = dist.arg3();
    }
    if (sigma.isPresent() && nu.isPresent()) {
      IExpr varianceVec = StatisticsContinousDistribution.diagonalOfSquareMatrix(sigma);
      if (varianceVec.isPresent()) {
        IExpr var = F.Times(F.Divide(nu, F.Subtract(nu, F.C2)), varianceVec);
        return F.Piecewise(F.list(F.list(var, F.Greater(nu, F.C2))), S.Indeterminate);
      }
    }
    return F.NIL;
  }
}
