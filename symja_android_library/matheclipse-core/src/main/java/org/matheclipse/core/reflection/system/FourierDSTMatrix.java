package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

public class FourierDSTMatrix extends AbstractFunctionEvaluator {

  public FourierDSTMatrix() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    int n = ast.arg1().toIntDefault();
    if (n <= 0) {
      // Positive integer argument expected in `1`.
      return Errors.printMessage(ast.topHead(), "intpp", F.List(ast.arg1()), engine);
    }
    int method = 2;
    if (ast.isAST2()) {
      method = ast.arg2().toIntDefault();
      if (method < 1 || method > 4) {
        // The transform type `1` should be 1, 2, 3 or 4.
        return Errors.printMessage(ast.topHead(), "fttype", F.List(ast.arg1()), engine);
      }
    }
    IInteger nZZ = F.ZZ(n);
    IFraction nReciprocalQQ = F.QQ(1, n);
    switch (method) {
      case 1:
        IExpr factor1 = engine.evaluate(F.Power(F.QQ(2, n + 1), F.C1D2));
        IFraction nD1ReciprocalQQ = F.QQ(1, n + 1);
        return F.matrix((r, s) -> dst1(factor1, nD1ReciprocalQQ, r, s), n, n);
      case 2:
        IExpr factor2 = engine.evaluate(F.Power(nZZ, F.CN1D2));
        return F.matrix((r, s) -> dst2(factor2, nReciprocalQQ, r, s), n, n);
      case 3:
        IExpr factor3 = engine.evaluate(F.Power(nZZ, F.CN1D2));
        return F.matrix((r, s) -> dst3(factor3, nReciprocalQQ, n, r, s), n, n);
      case 4:
        IExpr factor4 = engine.evaluate(F.Sqrt(F.QQ(2, n)));
        return F.matrix((r, s) -> dst4(factor4, nReciprocalQQ, r, s), n, n);
    }
    return F.NIL;
  }

  private static IExpr dst1(IExpr factor, IFraction nD1ReciprocalQQ, int r, int s) {
    return F.Times(factor, F.Sin(F.Times(nD1ReciprocalQQ, S.Pi, F.ZZ(r + 1), F.ZZ(s + 1))));
  }

  private static IASTMutable dst2(IExpr factor, IFraction nReciprocalQQ, int r, int s) {
    return F.Times(factor,
        F.Sin(F.Times(nReciprocalQQ, S.Pi, F.Plus(F.ZZ(r + 1), F.CN1D2), F.ZZ(s + 1))));
  }

  private static IExpr dst3(IExpr factor, IFraction nReciprocalQQ, int n, int r, int s) {
    if (r + 1 == n) {
      return F.Times(factor, F.CN1.pow(s));
    }
    return F.Times(factor, F.C2,
        F.Sin(F.Times(nReciprocalQQ, S.Pi, F.ZZ(r + 1), F.Plus(F.ZZ(s + 1), F.CN1D2))));
  }

  private static IASTMutable dst4(IExpr factor, IFraction nReciprocalQQ, int r, int s) {
    return F.Times(factor, F.Sin(
        F.Times(nReciprocalQQ, S.Pi, F.Plus(F.ZZ(r + 1), F.CN1D2), F.Plus(F.ZZ(s + 1), F.CN1D2))));
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_2;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {}
}
