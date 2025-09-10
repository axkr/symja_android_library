package org.matheclipse.core.interfaces.statistics;

import org.matheclipse.core.builtin.StatisticsDiscreteDistributions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;

/** probability density function */
public interface IPDF extends IDistribution {
  /**
   * For {@link StatisticsDiscreteDistributions.IExpectationDiscreteDistribution}, the function
   * returns the P(X == x), i.e. probability of random variable X == x
   *
   * <p>
   * For continuous distributions, the function
   *
   * <ul>
   * <li>returns the value of the probability density function, which is <em>not</em> identical to
   * P(X == x)]
   * </ul>
   *
   * @param x
   * @param engine TODO
   * @return
   */
  IExpr pdf(IAST dist, IExpr x, EvalEngine engine);

  /**
   * Call the pure (CDF, InverseCDF, PDF,...) function.
   *
   * @param function the pure function
   * @param x if <code>F.NIL</code> return the pure function unevaluated. If <code>List(...)
   *     </code> map the pure function over all elements.
   * @return
   */
  default IExpr callFunction(IExpr function, IExpr x) {
    IExpr pureFunction = function;
    if (pureFunction.isFunction()) {
      EvalEngine engine = EvalEngine.get();
      if (!engine.isNumericMode()) {
        ((IASTMutable) pureFunction).set(1, engine.evaluateNonNumeric(pureFunction.first()));
      }
    }
    if (x.isPresent()) {
      if (x.isList()) {
        return ((IAST) x).map(v -> F.unaryAST1(pureFunction, v), 1);
      }
      return F.unaryAST1(pureFunction, x);
    }
    return pureFunction;
  }
}