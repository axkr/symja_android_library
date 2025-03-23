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

public class FourierSinTransform extends AbstractFunctionEvaluator {

  public FourierSinTransform() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr expr = ast.arg1();
    IExpr t = ast.arg2();
    IExpr omega = ast.arg3();
    IExpr assumptions = F.Rule(S.Assumptions, F.Greater(omega, F.C0));

    IAST integral = //
        F.Times(F.Sqrt(F.Divide(F.C2, S.Pi)), //
            F.Integrate(F.Times(expr, F.Sin(F.Times(omega, t))), //
                F.List(t, F.C0, F.CInfinity), //
                assumptions));
    IExpr fst = engine.evaluate(integral);
    if (fst.isFree(x -> x == S.Integrate, true)) {
      return fst;
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
