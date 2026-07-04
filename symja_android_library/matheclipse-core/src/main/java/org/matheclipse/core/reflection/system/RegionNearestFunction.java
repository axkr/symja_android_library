package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.RegionNearestFunctionExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class RegionNearestFunction extends AbstractEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr head = ast.head();

    // 1. Intercept an applied function: RegionNearestFunctionExpr(region)(point)
    if (head instanceof RegionNearestFunctionExpr) {
      return ((RegionNearestFunctionExpr) head).evaluate(ast, engine);
    }

    // 2. Intercept the generator function: RegionNearestFunction(region)
    else if (head == S.RegionNearestFunction) {
      if (ast.isAST1()) {
        IExpr arg1 = ast.arg1();

        // Unwrap Region display wrapper if present
        if (arg1.isAST(S.Region, 1)) {
          arg1 = arg1.first();
        }

        if (arg1.isAST()) {
          return RegionNearestFunctionExpr.newInstance((IAST) arg1);
        }
      }
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_INFINITY_0;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    // newSymbol.setAttributes(ISymbol.HOLDALL);
  }
}