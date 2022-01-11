package org.matheclipse.core.builtin;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.reflection.system.rules.RGBColorRules;

public class EntityFunctions {

  private static class Initializer {

    private static void init() {
      S.RGBColor.setEvaluator(new RGBColor());
    }
  }

  private static class RGBColor extends AbstractFunctionEvaluator implements RGBColorRules {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public IAST getRuleAST() {
      return RULES;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private EntityFunctions() {}
}
