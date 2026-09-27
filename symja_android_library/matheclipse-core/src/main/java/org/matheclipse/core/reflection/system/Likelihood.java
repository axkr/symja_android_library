package org.matheclipse.core.reflection.system;

import org.matheclipse.core.builtin.StochasticProcesses;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <code>Likelihood(dist, {x1, x2, ...})</code> - the product of the densities
 * <code>PDF(dist, xi)</code>; <code>LogLikelihood(dist, {x1, x2, ...})</code> - the sum of their
 * logarithms.
 *
 * <p>
 * The data points are single values, or vectors for a multivariate distribution. For an
 * <code>ARMAProcess</code> the data is one path of the process, whose values are not independent:
 * its likelihood is the multivariate normal density with the process's own autocovariances.
 */
public class Likelihood extends AbstractFunctionEvaluator {

  private final boolean logarithm;

  /** @param logarithm <code>true</code> for <code>LogLikelihood</code> */
  public Likelihood(boolean logarithm) {
    this.logarithm = logarithm;
  }

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr distribution = ast.arg1();
    IExpr data = ast.arg2();
    if (!data.isList() || data.argSize() == 0) {
      return F.NIL;
    }
    if (distribution.isAST(S.ARMAProcess)) {
      double logLikelihood = StochasticProcesses.armaLogLikelihood((IAST) distribution,
          (IAST) data);
      if (Double.isNaN(logLikelihood)) {
        return F.NIL;
      }
      return F.num(logarithm ? logLikelihood : Math.exp(logLikelihood));
    }
    if (!distribution.isDistribution()) {
      return F.NIL;
    }
    IASTAppendable terms = logarithm ? F.PlusAlloc(data.argSize()) : F.TimesAlloc(data.argSize());
    for (IExpr x : (IAST) data) {
      IExpr density = engine.evaluate(F.PDF(distribution, x));
      if (density.isAST(S.PDF)) {
        return F.NIL;
      }
      // the parameters of a distribution are positive where they have to be, so the logarithm of
      // a density can be expanded
      terms.append(logarithm ? engine.evaluate(F.PowerExpand(F.Log(density))) : density);
    }
    return engine.evaluate(terms);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_2;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {}
}
