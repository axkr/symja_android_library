package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionExpand;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInexactNumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Implementation of the Barnes G-function.
 * <p>
 */
public class DawsonF extends AbstractFunctionEvaluator implements IFunctionExpand {


  public DawsonF() {}

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {

    IExpr arg = ast.arg1();
    if (arg.isZero()) {
      return F.C0;
    }
    IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg);
    if (negExpr.isPresent()) {
      return F.Times(F.CN1, F.unaryAST1(S.DawsonF, negExpr));
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_1;
  }

  @Override
  public IExpr functionExpand(IAST ast, EvalEngine engine) {
    if (ast.isAST1()) {
      IExpr z = ast.arg1();
      // (Sqrt(Pi)*Erfi(z))/(2*E^z^2)
      return F.Times(F.C1D2, F.Power(F.Exp(F.Sqr(z)), F.CN1), F.CSqrtPi, F.Erfi(z));
    }
    return F.NIL;
  }

  @Override
  public IExpr numericFunction(IAST ast, final EvalEngine engine) {
    if (ast.argSize() == 1) {
      try {
        IInexactNumber z = (IInexactNumber) ast.arg1();
        return z.dawsonF();
      } catch (RuntimeException rex) {
        return Errors.printMessage(S.DawsonF, rex);
      }
    }
    return F.NIL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    super.setUp(newSymbol);
    newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
  }
}
