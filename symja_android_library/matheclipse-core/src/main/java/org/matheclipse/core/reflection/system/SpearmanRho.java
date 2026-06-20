package org.matheclipse.core.reflection.system;

import org.hipparchus.exception.MathRuntimeException;
import org.hipparchus.linear.RealMatrix;
import org.hipparchus.stat.correlation.SpearmansCorrelation;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.ASTRealMatrix;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <pre>
 * SpearmanRho(v1, v2)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * gives Spearman's rank correlation coefficient between v1 and v2.
 * </p>
 * </blockquote>
 * 
 * <pre>
 * SpearmanRho(m)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * gives a matrix of the rank correlations between columns of m.
 * </p>
 * </blockquote>
 * 
 * <pre>
 * SpearmanRho(dist)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * gives a matrix of the rank correlations between the variables of dist.
 * </p>
 * </blockquote>
 * 
 * <pre>
 * SpearmanRho(dist, i, j)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * gives the rank correlation between the ith and jth marginals of dist.
 * </p>
 * </blockquote>
 */
public class SpearmanRho extends AbstractFunctionEvaluator {

  public SpearmanRho() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    try {
      // Handle the single argument case: SpearmanRho(m) or SpearmanRho(dist)
      if (ast.isAST1()) {
        IExpr arg1 = ast.arg1();

        if (arg1 instanceof ASTRealMatrix) {
          SpearmansCorrelation sc =
              new SpearmansCorrelation(((ASTRealMatrix) arg1).getRealMatrix());
          return new ASTRealMatrix(sc.getCorrelationMatrix(), false);
        }

        int[] dim = arg1.isMatrix(false);
        if (dim != null && dim[0] > 1 && dim[1] > 1) {
          RealMatrix matrix = arg1.toRealMatrix();
          if (matrix != null) {
            SpearmansCorrelation sc = new SpearmansCorrelation(matrix);
            return new ASTRealMatrix(sc.getCorrelationMatrix(), false);
          }
        }

        // Support for distributions
        if (arg1.isDistribution()) {
          return evaluateDistribution(arg1);
        }

        return F.NIL;
      }

      // Handle the two-argument vector case: SpearmanRho(v1, v2)
      if (ast.isAST2()) {
        IExpr arg1 = ast.arg1();
        IExpr arg2 = ast.arg2();

        int dim1 = arg1.isVector();
        int dim2 = arg2.isVector();

        if (dim1 >= 0 && dim1 == dim2) {
          // Perform numerical evaluation if the engine is in double mode or inputs are numeric
          double[] a = arg1.toDoubleVector();
          if (a != null) {
            double[] b = arg2.toDoubleVector();
            if (b != null) {
              SpearmansCorrelation sc = new SpearmansCorrelation();
              return F.num(sc.correlation(a, b));
            }
          }
        }
        // Fallback for symbolic evaluations where ranking isn't straightforward
        return F.NIL;
      }

      // Handle the three-argument case: SpearmanRho(dist, i, j)
      if (ast.isAST3()) {
        IExpr arg1 = ast.arg1();
        if (arg1.isDistribution()) {
          IExpr matrix = evaluateDistribution(arg1);
          if (matrix.isList()) {
            // Extract the [i, j] element using the engine's Part evaluation
            return engine.evaluate(F.Part(matrix, ast.arg2(), ast.arg3()));
          }
          // return generalSpearmanRhoFormula(arg1, ast.arg2(), ast.arg3());
        }
        return F.NIL;
      }

    } catch (MathRuntimeException mrex) {
      // Catch exceptions (like mismatched dimensions) and print standard errors
      return Errors.printMessage(S.SpearmanRho, mrex, engine);
    }

    return F.NIL;
  }

  /**
   * Evaluates Spearman's rho for supported multivariate distributions.
   *
   * @param distExpr the distribution expression
   * @return the Spearman rho correlation matrix, or F.NIL if unsupported
   */
  private IExpr evaluateDistribution(IExpr distExpr) {
    if (!distExpr.isAST()) {
      return F.NIL;
    }
    IAST dist = (IAST) distExpr;
    IExpr head = dist.head();

    if (head == S.BinormalDistribution) {
      IExpr rho;
      if (dist.isAST1()) {
        rho = dist.arg1();
      } else if (dist.isAST2()) {
        rho = dist.arg2();
      } else if (dist.isAST3()) {
        rho = dist.arg3();
      } else {
        return F.NIL;
      }
      // val = 6/Pi * ArcSin(rho/2)
      IExpr val = F.Times(F.QQ(6, 1), F.Power(S.Pi, F.CN1), F.ArcSin(F.Times(F.C1D2, rho)));
      return F.List(F.List(F.C1, val), F.List(val, F.C1));
    }

    if (head == S.MultinormalDistribution) {
      IExpr sigma;
      if (dist.isAST1()) {
        sigma = dist.arg1(); // MultinormalDistribution(Sigma)
      } else if (dist.isAST2()) {
        sigma = dist.arg2(); // MultinormalDistribution(Mu, Sigma)
      } else {
        return F.NIL;
      }

      int[] dim = sigma.isMatrix(false);
      if (dim != null && dim[0] == dim[1] && dim[0] > 0) {
        IAST matrix = (IAST) sigma;
        int n = dim[0];
        IASTAppendable result = F.ListAlloc(n);

        for (int i = 1; i <= n; i++) {
          IASTAppendable row = F.ListAlloc(n);
          for (int j = 1; j <= n; j++) {
            if (i == j) {
              row.append(F.C1);
            } else {
              IExpr covIJ = matrix.getPart(i, j);
              IExpr varI = matrix.getPart(i, i);
              IExpr varJ = matrix.getPart(j, j);

              // r_ij = cov_ij / Sqrt(var_i * var_j)
              IExpr rIJ = F.Divide(covIJ, F.Sqrt(F.Times(varI, varJ)));

              // val = 6/Pi * ArcSin(r_ij / 2)
              IExpr val = F.Times(F.QQ(6, 1), F.Power(S.Pi, F.CN1), F.ArcSin(F.Times(F.C1D2, rIJ)));
              row.append(val);
            }
          }
          result.append(row);
        }
        return result;
      }
    }

    return F.NIL;
  }

  /**
   * Generates the general expectation formula for SpearmanRho[dist, i, j] 12 *
   * Expectation[CDF[dist_i, x] * CDF[dist_j, y], {x, y} \[Distributed] dist_ij] - 3
   */
  // private static IExpr generalSpearmanRhoFormula(IExpr dist, IExpr i, IExpr j) {
  // // Generate unique dummy variables to avoid naming collisions in evaluation
  // ISymbol x = F.Dummy("x");
  // ISymbol y = F.Dummy("y");
  //
  // // F[x] and G[y]: The CDFs of the i and j marginals
  // IExpr marginalI = F.MarginalDistribution(dist, i);
  // IExpr marginalJ = F.MarginalDistribution(dist, j);
  //
  // IExpr cdfI = F.CDF(marginalI, x);
  // IExpr cdfJ = F.CDF(marginalJ, y);
  //
  // // dist_ij: The joint marginal of dist for i and j
  // IAST distIJ = F.MarginalDistribution(dist, F.List(i, j));
  //
  // // Expectation[F[x] * G[y], {x, y} \[Distributed] distIJ]
  // IExpr expectation = F.Expectation(F.Times(cdfI, cdfJ), F.Distributed(F.List(x, y), distIJ));
  //
  // // 12 * Expectation[...] - 3
  // return F.Plus(F.CN3, F.Times(F.ZZ(12), expectation));
  // }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_3;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.NOATTRIBUTE);
    super.setUp(newSymbol);
  }
}
