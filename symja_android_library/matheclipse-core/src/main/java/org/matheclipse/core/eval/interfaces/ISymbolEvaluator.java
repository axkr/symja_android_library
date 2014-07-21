package org.matheclipse.core.eval.interfaces;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Interface for evaluating constant symbols like Pi, Degree,...
 */
public interface ISymbolEvaluator extends IEvaluator {

  /**
   * Symbolic evaluation of a symbol
   * @param symbol the symbol which should be evaluated
   *
   * @return the evaluated object or <code>null</code>,
   *         if evaluation isn't possible
   */
  public IExpr evaluate(ISymbol symbol);

  /**
   * Numerical evaluation of a symbol
   * @param symbol the symbol which should be evaluated
   *
   * @return the evaluated object or <code>null</code>,
   *         if evaluation isn't possible
   */
  public IExpr numericEval(ISymbol symbol);
  
  /**
   * Numerical evaluation of a symbol
   * @param symbol the symbol which should be evaluated
 * @param engine TODO
   *
   * @return the evaluated object or <code>null</code>,
   *         if evaluation isn't possible
   */
  public IExpr apfloatEval(ISymbol symbol, EvalEngine engine);
}
