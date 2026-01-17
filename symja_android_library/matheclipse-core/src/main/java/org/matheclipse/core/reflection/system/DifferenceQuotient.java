package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Implementation of the {@code DifferenceQuotient} function. *
 * <p>
 * DifferenceQuotient gives the slope of the secant line joining two nearby points on a curve.
 * DifferenceQuotient(f,{x,n,h}) is equivalent to DifferenceDelta(f,{x,n,h})/h^n.
 */
public class DifferenceQuotient extends AbstractFunctionEvaluator {

  public DifferenceQuotient() {}

  @Override
  public IExpr evaluate(final IAST ast, final EvalEngine engine) {
    IExpr f = ast.arg1();

    if (f.isList()) {
      // thread elementwise over list in f
      return f.mapThread(ast.setAtCopy(1, F.Slot1), 1);
    }

    // Iterate over the variable specifications (x, {x}, {x, n}, {x, n, h}, etc.)
    for (int i = 2; i < ast.size(); i++) {
      IExpr arg = ast.get(i);
      IExpr x;
      IExpr n = F.C1; // Default order
      IExpr h = F.C1; // Default step

      if (arg.isAST(S.List, 3, 4)) {
        IAST list = (IAST) arg;
        x = list.arg1();
        if (list.isAST2()) {
          // {x, h}
          h = list.arg2();
        } else if (list.isAST3()) {
          // {x, n, h}
          n = list.arg2();
          h = list.arg3();
        }
      } else {
        // Invalid argument type
        return F.NIL;
      }

      // Calculate DifferenceDelta(f, {x, n, h})
      IExpr differenceDelta = engine.evaluateNIL(F.DifferenceDelta(f, F.List(x, n, h)));
      if (differenceDelta.isNIL()) {
        return F.NIL;
      }
      // Result = DifferenceDelta / h^n
      // We update 'f' so the next iteration applies to the result of the previous one (threading)
      f = engine.evaluate(//
          F.Simplify(F.Times(differenceDelta, F.Power(h, n.negate()))));
    }

    return f;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_INFINITY;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

}
