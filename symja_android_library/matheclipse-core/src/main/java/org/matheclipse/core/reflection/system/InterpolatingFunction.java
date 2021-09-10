package org.matheclipse.core.reflection.system;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.data.InterpolatingFunctionExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class InterpolatingFunction extends AbstractEvaluator {
  private static final Logger LOGGER = LogManager.getLogger();

  public InterpolatingFunction() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr head = ast.head();
    if (head instanceof InterpolatingFunctionExpr) {
      try {
        return ((InterpolatingFunctionExpr<IExpr>) head).evaluate(ast, engine);
      } catch (RuntimeException rex) {
        LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
      }
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_INFINITY_0;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {}
}
