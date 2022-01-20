package org.matheclipse.core.reflection.system;

import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.exception.MathIllegalStateException;
import org.hipparchus.exception.MathRuntimeException;
import org.hipparchus.optim.nonlinear.scalar.GoalType;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class FindMaximum extends FindMinimum {

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    GoalType goalType = GoalType.MAXIMIZE;
    try {
      return findExtremum(ast, engine, goalType);
    } catch (MathIllegalStateException miae) {
      // `1`.
      return IOFunctions.printMessage(ast.topHead(), "error", F.List(F.$str(miae.getMessage())),
          engine);
    } catch (MathIllegalArgumentException miae) {
      // `1`.
      IOFunctions.printMessage(ast.topHead(), "error", F.List(F.$str(miae.getMessage())), engine);
      return F.CEmptyList;
    } catch (MathRuntimeException mre) {
      IOFunctions.printMessage(ast.topHead(), "error", F.List(F.$str(mre.getMessage())), engine);
      return F.CEmptyList;
    }
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_INFINITY;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.HOLDALL);
  }
}
