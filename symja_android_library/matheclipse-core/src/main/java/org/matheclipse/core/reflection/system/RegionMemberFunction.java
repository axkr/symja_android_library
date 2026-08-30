package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.RegionMemberFunctionExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>RegionMemberFunction(region)</code> - the membership test of a region as a function object.
 *
 * @see RegionMember
 */
public class RegionMemberFunction extends AbstractEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr head = ast.head();

    // 1. Intercept an applied function: RegionMemberFunctionExpr(region)(point)
    if (head instanceof RegionMemberFunctionExpr) {
      return ((RegionMemberFunctionExpr) head).evaluate(ast, engine);
    }

    // 2. Intercept the generator function: RegionMemberFunction(region)
    else if (head == S.RegionMemberFunction) {
      if (ast.isAST1()) {
        IExpr arg1 = ast.arg1();

        // Unwrap Region display wrapper if present
        if (arg1.isAST(S.Region, 1)) {
          arg1 = arg1.first();
        }

        if (arg1.isAST()) {
          return RegionMemberFunctionExpr.newInstance((IAST) arg1);
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
}
