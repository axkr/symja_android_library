package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class FourierCosTransform extends AbstractFunctionEvaluator {

  public FourierCosTransform() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr f = ast.arg1();
    IExpr t = ast.arg2();
    IExpr omega = ast.arg3();
    IExpr assumptions = F.Rule(S.Assumptions, F.Greater(omega, F.C0));

    IAST integral = //
        F.Times(F.Sqrt(F.Divide(F.C2, S.Pi)), //
            F.Integrate(F.Times(f, F.Cos(F.Times(omega, t))), //
                F.List(t, F.C0, F.CInfinity), //
                assumptions));
    IExpr FF = engine.evaluate(integral);
    if (FF.isFree(x -> x == S.Integrate, true)) {
      return FF;
    }
    return F.NIL;
  }


  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_3_3;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {}
}
