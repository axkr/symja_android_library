package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * See issue <a href="https://github.com/axkr/symja_android_library/issues/455">#455 Implement the
 * Gruntz algorithm for limit calculation</a>
 *
 */
public class NewLimit extends AbstractEvaluator {
  /** Direction of limit computation */
  private static enum Direction {
    /** Compute the limit approaching from larger real values. */
    FROM_ABOVE(-1),

    /** Compute the limit approaching from larger or smaller real values automatically. */
    TWO_SIDED(0),

    /** Compute the limit approaching from smaller real values. */
    FROM_BELOW(1);

    private int direction;

    /**
     * Convert the direction <code>FROM_ABOVE, TWO_SIDED, FROM_BELOW</code> to the corresponding
     * value <code>-1, 0, 1</code>
     *
     * @return
     */
    int toInt() {
      return direction;
    }

    private Direction(int direction) {
      this.direction = direction;
    }
  }

  public NewLimit() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    Errors.printExperimental(S.NewLimit);
    if (ast.argSize() > 0) {
    }

    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_3;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    super.setUp(newSymbol);
  }
}
