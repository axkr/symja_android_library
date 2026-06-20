package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.interfaces.statistics.ICDF;

/**
 * <pre>
 * MarginalDistribution(dist, k)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * gives the kth marginal distribution of dist.
 * </p>
 * </blockquote>
 * 
 * <pre>
 * MarginalDistribution(dist, {k1, ..., km})
 * </pre>
 * 
 * <blockquote>
 * <p>
 * gives the joint marginal distribution for the specified indices.
 * </p>
 * </blockquote>
 */
public class MarginalDistribution extends AbstractFunctionEvaluator implements ICDF {

  public MarginalDistribution() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr distExpr = ast.arg1();
    IExpr indicesExpr = ast.arg2();

    if (!distExpr.isAST()) {
      return F.NIL;
    }

    IAST distAST = (IAST) distExpr;

    int[] indices = getIndices(indicesExpr);
    if (indices == null || indices.length == 0) {
      return F.NIL;
    }

    switch (distAST.headID()) {
      case ID.CauchyDistribution:
      case ID.NormalDistribution:
      case ID.PoissonDistribution:
      case ID.StudentTDistribution:
        if (indices.length == 1 && indices[0] == 1) {
          return distAST;
        }
        return F.NIL;

      case ID.BinormalDistribution:
        if (distAST.isAST(S.BinormalDistribution, 2, 4)) {
          return evaluateBinormalMarginal(distAST, indices);
        }
        return F.NIL;
      case ID.MultinormalDistribution:
        if (distAST.isAST(S.MultinormalDistribution, 2, 3)) {
          return evaluateMultinormalMarginal(distAST, indices, engine);
        }
        return F.NIL;
      case ID.MultivariateTDistribution:
        if (distAST.isAST(S.MultivariateTDistribution, 3, 4)) {
          return evaluateMultivariateTMarginal(distAST, indices, engine);
        }
        return F.NIL;
    }

    return F.NIL;
  }

  /**
   * Helper to evaluate marginals for MultivariateTDistribution. Collapses to CauchyDistribution
   * when degrees of freedom (nu) == 1.
   */
  private IExpr evaluateMultivariateTMarginal(IAST distAST, int[] indices, EvalEngine engine) {
    int argSize = distAST.argSize();
    IExpr mu = null;
    IExpr sigma = null;
    IExpr nu = null;

    if (argSize == 2) {
      // MultivariateTDistribution(Sigma, nu)
      sigma = distAST.arg1();
      nu = distAST.arg2();
    } else if (argSize == 3) {
      // MultivariateTDistribution(Mu, Sigma, nu)
      mu = distAST.arg1();
      sigma = distAST.arg2();
      nu = distAST.arg3();
    } else {
      return F.NIL;
    }

    if (indices.length == 1) {
      int k = indices[0];
      IExpr m_k = (mu == null) ? F.C0 : evaluatePart(mu, engine, k);
      IExpr var_k = evaluatePart(sigma, engine, k, k);
      IExpr s_k = engine.evaluate(F.Sqrt(var_k));

      // Mathematically, a t-distribution with 1 degree of freedom is a Cauchy distribution.
      if (nu.isOne()) {
        if (m_k.isZero() && s_k.isOne()) {
          return F.ast(S.CauchyDistribution);
        }
        return F.CauchyDistribution(m_k, s_k);
      }

      // Otherwise, return standard or location-scale StudentTDistribution
      if (m_k.isZero() && s_k.isOne()) {
        return F.StudentTDistribution(nu);
      }
      return F.StudentTDistribution(m_k, s_k, nu);
    }

    if (indices.length > 1) {
      IASTAppendable newMu = (mu == null) ? null : F.ListAlloc(indices.length);
      IASTAppendable newSigma = F.ListAlloc(indices.length);

      for (int r : indices) {
        if (newMu != null) {
          newMu.append(evaluatePart(mu, engine, r));
        }
        IASTAppendable row = F.ListAlloc(indices.length);
        for (int c : indices) {
          row.append(evaluatePart(sigma, engine, r, c));
        }
        newSigma.append(row);
      }

      if (newMu == null) {
        return F.MultivariateTDistribution(newSigma, nu);
      }
      return F.MultivariateTDistribution(newMu, newSigma, nu);
    }

    return F.NIL;
  }

  /**
   * Helper to evaluate marginals for BinormalDistribution
   */
  private IExpr evaluateBinormalMarginal(IAST distAST, int[] indices) {
    int argSize = distAST.argSize();

    if (indices.length == 1) {
      int k = indices[0];
      if (k != 1 && k != 2) {
        return F.NIL;
      }

      IExpr mu = F.C0;
      IExpr sigma = F.C1;

      if (argSize == 1) {
        // BinormalDistribution(rho)
        mu = F.C0;
        sigma = F.C1;
      } else if (argSize == 3) {
        // BinormalDistribution(sigma1, sigma2, rho)
        mu = F.C0;
        sigma = distAST.get(k);
      } else if (argSize == 5) {
        // BinormalDistribution(mu1, mu2, sigma1, sigma2, rho)
        mu = distAST.get(k);
        sigma = distAST.get(k + 2);
      } else {
        return F.NIL;
      }
      return F.NormalDistribution(mu, sigma);
    }

    if (indices.length == 2) {
      if (indices[0] == 1 && indices[1] == 2) {
        return distAST;
      }
      if (indices[0] == 2 && indices[1] == 1) {
        // Swap marginals
        if (argSize == 1) {
          return distAST;
        } else if (argSize == 3) {
          return F.BinormalDistribution(distAST.arg2(), distAST.arg1(), distAST.arg3());
        } else if (argSize == 5) {
          return F.BinormalDistribution(distAST.arg2(), distAST.arg1(), distAST.arg4(),
              distAST.arg3(), distAST.arg5());
        }
      }
    }

    return F.NIL;
  }

  /**
   * Helper to evaluate marginals for MultinormalDistribution
   */
  private IExpr evaluateMultinormalMarginal(IAST distAST, int[] indices, EvalEngine engine) {
    int argSize = distAST.argSize();
    IExpr mu = null;
    IExpr sigma = null;

    if (argSize == 1) {
      // MultinormalDistribution(Sigma)
      sigma = distAST.arg1();
    } else if (argSize == 2) {
      // MultinormalDistribution(Mu, Sigma)
      mu = distAST.arg1();
      sigma = distAST.arg2();
    } else {
      return F.NIL;
    }

    if (indices.length == 1) {
      int k = indices[0];
      IExpr m_k = (mu == null) ? F.C0 : evaluatePart(mu, engine, k);
      IExpr var_k = evaluatePart(sigma, engine, k, k);
      IExpr s_k = engine.evaluate(F.Sqrt(var_k)); // NormalDistribution uses standard deviation
      return F.NormalDistribution(m_k, s_k);
    }

    if (indices.length > 1) {
      IASTAppendable newMu = (mu == null) ? null : F.ListAlloc(indices.length);
      IASTAppendable newSigma = F.ListAlloc(indices.length);

      for (int r : indices) {
        if (newMu != null) {
          newMu.append(evaluatePart(mu, engine, r));
        }
        IASTAppendable row = F.ListAlloc(indices.length);
        for (int c : indices) {
          row.append(evaluatePart(sigma, engine, r, c));
        }
        newSigma.append(row);
      }

      if (newMu == null) {
        return F.MultinormalDistribution(newSigma);
      }
      return F.MultinormalDistribution(newMu, newSigma);
    }

    return F.NIL;
  }

  /**
   * Helper to safely evaluate the Part of an expression (matrix/vector extraction)
   */
  private IExpr evaluatePart(IExpr expr, EvalEngine engine, int... indices) {
    IASTAppendable partAST = F.ast(S.Part);
    partAST.append(expr);
    for (int idx : indices) {
      partAST.append(F.ZZ(idx));
    }
    return engine.evaluate(partAST);
  }

  /**
   * Parses the second argument into a 1-based integer array
   */
  private int[] getIndices(IExpr expr) {
    if (expr.isInteger()) {
      int val = expr.toIntDefault(-1);
      return val > 0 ? new int[] {val} : null;
    }
    if (expr.isList()) {
      IAST list = (IAST) expr;
      int size = list.argSize();
      int[] res = new int[size];
      for (int i = 1; i <= size; i++) {
        int val = list.get(i).toIntDefault(-1);
        if (val <= 0)
          return null;
        res[i - 1] = val;
      }
      return res;
    }
    return null;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_2;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.NOATTRIBUTE);
    super.setUp(newSymbol);
  }

  @Override
  public IExpr mean(IAST distribution) {
    EvalEngine engine = EvalEngine.get();
    IExpr evaluated = evaluate(distribution, engine);
    if (evaluated.isPresent()) {
      return engine.evaluate(F.Mean(evaluated));
    }

    if (distribution.isAST2()) {
      IExpr distExpr = distribution.arg1();
      IExpr distMean = engine.evaluate(F.Mean(distExpr));
      if (distMean.isList()) {
        int[] indices = getIndices(distribution.arg2());
        if (indices != null && indices.length > 0) {
          if (indices.length == 1) {
            return evaluatePart(distMean, engine, indices[0]);
          }
          IASTAppendable result = F.ListAlloc(indices.length);
          for (int k : indices) {
            result.append(evaluatePart(distMean, engine, k));
          }
          return result;
        }
      }
    }
    return F.NIL;
  }

  @Override
  public IExpr median(IAST distribution) {
    EvalEngine engine = EvalEngine.get();
    IExpr evaluated = evaluate(distribution, engine);
    if (evaluated.isPresent()) {
      return engine.evaluate(F.Median(evaluated));
    }
    return F.NIL;
  }

  @Override
  public IExpr cdf(IAST dist, IExpr x, EvalEngine engine) {
    IExpr evaluated = evaluate(dist, engine);
    if (evaluated.isPresent()) {
      return engine.evaluate(F.CDF(evaluated, x));
    }
    return F.NIL;
  }

  @Override
  public IExpr inverseCDF(IAST dist, IExpr x, EvalEngine engine) {
    IExpr evaluated = evaluate(dist, engine);
    if (evaluated.isPresent()) {
      return engine.evaluate(F.InverseCDF(evaluated, x));
    }
    return F.NIL;
  }
}
