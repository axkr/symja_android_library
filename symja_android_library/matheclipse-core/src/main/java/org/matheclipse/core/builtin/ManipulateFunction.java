package org.matheclipse.core.builtin;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * The interactive front end heads: <code>Manipulate</code>, <code>Animate</code>,
 * <code>ListAnimate</code>, <code>Animator</code> and <code>Dynamic</code>.
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
 *
 * <p>
 * <code>Dynamic</code> stays unevaluated for the same reason, and holds only its first argument:
 * the displayed expression is what a front end re-evaluates, while the setter functions that follow
 * it are ordinary values. What a front end then does with one is
 * {@link org.matheclipse.core.manipulate.Dynamics}.
 */
public class ManipulateFunction {

  private static class Initializer {

    private static void init() {
      S.Manipulate.setEvaluator(new HeldFrontEndObject());
      S.Animate.setEvaluator(new HeldFrontEndObject());
      S.ListAnimate.setEvaluator(new HeldFrontEndObject());
      S.Animator.setEvaluator(new HeldFrontEndObject());
      S.Dynamic.setEvaluator(new DynamicObject());
      S.DynamicWrapper.setEvaluator(new DynamicObject());
      S.Refresh.setEvaluator(new Refresh());
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

  /**
   * <code>Dynamic[expr]</code> and <code>DynamicWrapper[expr]</code>: held like the widget heads
   * above, but only in the first argument.
   *
   * <p>
   * The difference matters. <code>Dynamic[x]</code> has to keep the symbol <code>x</code> so that a
   * control built from it knows what to write to; holding everything as well would keep the setter
   * of <code>Dynamic[x, f]</code> from being the function <code>f</code> names.
   */
  private static class DynamicObject extends AbstractEvaluator {

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
      newSymbol.setAttributes(ISymbol.HOLDFIRST | ISymbol.PROTECTED | ISymbol.READPROTECTED);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  /**
   * <code>Refresh[expr, opts]</code> - the value of <code>expr</code>.
   *
   * <p>
   * <code>Refresh</code> only says <em>when</em> a surrounding <code>Dynamic</code> should look at
   * an expression again; what it stands for is always the expression itself, so outside a front end
   * it is its first argument and nothing more.
   */
  private static class Refresh extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return ast.arg1();
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private ManipulateFunction() {}
}
