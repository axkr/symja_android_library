package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.data.DifferenceRootExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class DifferenceRoot extends AbstractFunctionEvaluator {

  public DifferenceRoot() {}

  @Override
  public IExpr evaluate(final IAST ast, final EvalEngine engine) {
    IExpr f = ast.arg1();
    if (f.isFunction()) {
      return new DifferenceRootExpr(f);
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    super.setUp(newSymbol);
    newSymbol.setAttributes(ISymbol.NHOLDALL);
  }
}