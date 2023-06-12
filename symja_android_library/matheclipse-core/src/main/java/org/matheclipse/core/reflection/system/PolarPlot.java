package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/** Plots x/y functions */
public class PolarPlot extends AbstractFunctionOptionEvaluator {
  /** Constructor for the singleton */
  public static final PolarPlot CONST = new PolarPlot();

  public PolarPlot() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options,
      final EvalEngine engine) {
    if (argSize > 0 && argSize < ast.size()) {
      ast = ast.copyUntil(argSize + 1);
    }
    if (options[0].isTrue()) {
      IExpr temp = S.Manipulate.of(engine, ast);
      if (temp.headID() == ID.JSFormData) {
        return temp;
      }
      return F.NIL;
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_2;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.HOLDALL);
    setOptions(newSymbol, S.JSForm, S.True);
  }
}
