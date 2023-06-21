package org.matheclipse.core.builtin;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class PiecewiseFunctions {

  private static class Initializer {

    private static void init() {
      // S.SawtoothWave.setEvaluator(new SawtoothWave());
    }
  }

  private static class SawtoothWave extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr a = ast.arg1();
      IExpr b = ast.arg2();
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

  }


  public static void initialize() {
    Initializer.init();
  }

  private PiecewiseFunctions() {}
}
