package org.matheclipse.core.builtin;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * The interactive front end heads: <code>Manipulate</code>, <code>Animate</code>,
 * <code>ListAnimate</code> and <code>Animator</code>.
 *
 * <p>
 * These stay unevaluated. They describe a widget, and only a front end can show one; the browser
 * interface parses the unevaluated expression with
 * {@link org.matheclipse.core.manipulate.ManipulateSpec} and evaluates the body again for each set
 * of control values.
 *
 * <p>
 * The previous implementation transpiled the body to JavaScript for the mathcell and JSXGraph
 * libraries, which is why it only accepted a fixed list of body shapes - <code>Plot</code>,
 * <code>Plot3D</code>, <code>ListPlot</code> and a few more - and quietly returned the expression
 * unevaluated for everything else. Evaluating the body on the server instead puts no restriction on
 * it at all.
 */
public class ManipulateFunction {

  private static class Initializer {

    private static void init() {
      S.Manipulate.setEvaluator(new HeldFrontEndObject());
      S.Animate.setEvaluator(new HeldFrontEndObject());
      S.ListAnimate.setEvaluator(new HeldFrontEndObject());
      S.Animator.setEvaluator(new HeldFrontEndObject());
    }
  }

  /**
   * A head that holds its arguments and never evaluates: the expression itself is the result, for a
   * front end to render.
   */
  private static class HeldFrontEndObject extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private ManipulateFunction() {}
}
