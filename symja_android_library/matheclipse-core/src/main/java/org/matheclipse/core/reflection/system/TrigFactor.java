package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.AlgebraUtil;
import org.matheclipse.core.eval.CompareUtil;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;

public class TrigFactor extends AbstractEvaluator {


  public TrigFactor() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr temp = CompareUtil.threadListLogicEquationOperators(ast.arg1(), ast, 1);
    if (temp.isPresent()) {
      return temp;
    }

    IExpr expr = ast.arg1();
    if (expr.isFree(x -> x.isTrigFunction(), false)) {
      return expr;
    }
    if (expr.isAST()) {
      expr = F.subst(expr, x -> TrigExpand.rewriteCircularHyperbolic(x));
    }
    IExpr trigToExp = engine.evaluate(F.TrigToExp(expr));

    IExpr[] fractionParts = AlgebraUtil.numeratorDenominator((IAST) trigToExp, false, engine);
    if (fractionParts != null) {
      IExpr p0 = fractionParts[0];
      if (fractionParts[0].isPlusTimesPower()) {
        p0 = factorExpToTrig(fractionParts[0], engine);
      }
      IExpr p1 = fractionParts[1];
      if (fractionParts[1].isPlusTimesPower()) {
        p1 = factorExpToTrig(fractionParts[1], engine);
      }
      if (p1.isOne()) {
        return p0;
      }
      return F.Divide(p0, p1);
    }

    return factorExpToTrig(trigToExp, engine);
  }

  private IExpr factorExpToTrig(IExpr trigToExp, EvalEngine engine) {
    IExpr factor = engine.evaluate(F.Factor(trigToExp));
    IExpr expToTrig = engine.evaluate(F.ExpToTrig(factor));
    if (expToTrig.isTimes()) {
      IASTMutable copy = ((IAST) expToTrig).copy();
      for (int i = 1; i < copy.size(); i++) {
        IExpr temp = engine.evaluate(F.FullSimplify(copy.get(i)));
        copy.set(i, temp);
      }
      return copy;
    } else {
      IExpr simpExpr = engine.evaluate(F.FullSimplify(expToTrig));
      return simpExpr;
    }
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_1;
  }

  @Override
  public int status() {
    // TODO TrigFactor is experimental and working very slow at the moment
    return ImplementationStatus.EXPERIMENTAL;
  }
}
