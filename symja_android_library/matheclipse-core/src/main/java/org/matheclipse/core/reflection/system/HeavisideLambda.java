package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionExpand;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * HeavisideLambda function returns <code>0</code> for all <code>Abs(x)</code> greater than
 * <code>1</code> and <code>Abs(x)</code> for all x less than <code>1</code>,
 */
public class HeavisideLambda extends AbstractEvaluator implements IFunctionExpand {


  @Override
  public IExpr functionExpand(IAST ast, EvalEngine engine) {
    if (ast.argSize() == 1) {
      final IExpr x = ast.arg1();
      return heavisideLambda(x, engine);
    }
    return F.NIL;
  }

  private static IExpr heavisideLambda(IExpr x, EvalEngine engine) {
    // -HeavisideTheta(-1+x)+x*HeavisideTheta(-1+x)-2*x*HeavisideTheta(x)+HeavisideTheta(1+x)+x*HeavisideTheta(1+x)
    IExpr v2 = F.HeavisideTheta(F.Plus(F.C1, x));
    IExpr v1 = F.HeavisideTheta(F.Plus(F.CN1, x));
    return engine.evaluate(F.Plus(F.Negate(v1), v2, F.Times(v1, x), F.Times(v2, x),
        F.Times(F.CN2, x, F.HeavisideTheta(x))));
  }

  public HeavisideLambda() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    int size = ast.size();
    if (size > 1) {
      IExpr result = F.NIL;
      for (int i = 1; i < size; i++) {
        IExpr expr = ast.get(i);
        IReal temp = expr.evalReal();
        if (temp != null) {
          if (temp.isGE(F.C1) || temp.isLE(F.CN1)) {
            return F.C0;
          }
          if (temp.isZero()) {
            result = F.C1;
            continue;
          }

          IExpr heavisideLambda = heavisideLambda(expr, engine);
          if (heavisideLambda.isPresent()) {
            result = heavisideLambda;
            continue;
          }
        }
        return F.NIL;
      }
      return result;
    }
    return F.C1;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    // TODO implement for more than 1 argument
    return ARGS_1_1;
  }

  @Override
  public void setUp(ISymbol newSymbol) {
    newSymbol.setAttributes(
        ISymbol.HOLDALL | ISymbol.ORDERLESS | ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
  }

}
