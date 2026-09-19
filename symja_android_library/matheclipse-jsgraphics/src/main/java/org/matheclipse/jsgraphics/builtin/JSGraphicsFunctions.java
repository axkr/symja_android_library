package org.matheclipse.jsgraphics.builtin;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.svg.Scene2D;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.jsgraphics.JSRenderer;
import org.matheclipse.jsgraphics.echarts.EChartsRenderer;
import org.matheclipse.jsgraphics.jsxgraph.JSXGraphRenderer;
import org.matheclipse.jsgraphics.mathcell.MathCellRenderer;

/**
 * <code>JSXGraph(graphics)</code>, <code>ECharts(graphics)</code> and
 * <code>MathCell(graphics)</code>: a 2D graphic drawn by a JavaScript library instead of as SVG.
 *
 * <p>
 * Each returns <code>JSFormData(js, "library")</code>, which the front ends show in a sandboxed
 * iframe loading the library from its CDN. The graphic is laid out by the same code the SVG
 * renderer uses ({@link Scene2D}), so the plot range, styles and labels agree with the default
 * picture.
 */
public class JSGraphicsFunctions {

  /** The size the graphic is laid out at; the library then scales it to its frame. */
  private static final double WIDTH = 600;
  private static final double HEIGHT = 400;

  private static class Initializer {
    static void init() {
      S.JSXGraph.setEvaluator(new JSGraphics(JSXGraphRenderer::new));
      S.ECharts.setEvaluator(new JSGraphics(EChartsRenderer::new));
      S.MathCell.setEvaluator(new MathCell());
    }
  }

  /** A renderer per call: they keep the script they are writing in a field. */
  @FunctionalInterface
  interface RendererFactory {
    JSRenderer create();
  }

  private static class JSGraphics extends AbstractFunctionEvaluator {
    final RendererFactory factory;

    JSGraphics(RendererFactory factory) {
      this.factory = factory;
    }

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      return render(ast, ast.arg1(), factory.create(), engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }
  }

  /**
   * <code>MathCell(Plot(f, {x, a, b}))</code> hands the function itself to MathCell when it
   * translates to JavaScript, so the argument is held until that has been tried.
   */
  private static final class MathCell extends JSGraphics {

    MathCell() {
      super(MathCellRenderer::new);
    }

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isAST()) {
        String js = MathCellRenderer.functionPlot((IAST) arg1, engine);
        if (js != null) {
          return F.JSFormData(js, org.matheclipse.core.form.output.OutputFormats.MATHCELL_STR);
        }
      }
      return render(ast, engine.evaluate(arg1), factory.create(), engine);
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDFIRST);
    }
  }

  /**
   * The graphic {@code arg} drawn by {@code renderer}, or {@link F#NIL} with a message when it is
   * not a single 2D graphic or holds something the library cannot draw.
   */
  static IExpr render(IAST ast, IExpr arg, JSRenderer renderer, EvalEngine engine) {
    if (!isGraphics2D(arg)) {
      // `1` currently not supported in `2`.
      return Errors.printMessage(ast.topHead(), "unsupported",
          F.list(F.stringx(arg.isAST() ? arg.head().toString() : arg.toString()), ast.topHead()),
          engine);
    }
    try {
      Scene2D scene = Scene2D.of((IAST) arg, WIDTH, HEIGHT);
      if (scene == null) {
        return Errors.printMessage(ast.topHead(), "unsupported",
            F.list(F.stringx(arg.head().toString()), ast.topHead()), engine);
      }
      String unsupported = renderer.unsupported(scene);
      if (unsupported != null) {
        return Errors.printMessage(ast.topHead(), "unsupported",
            F.list(F.stringx(unsupported), ast.topHead()), engine);
      }
      return F.JSFormData(renderer.toJavaScript(scene), renderer.type());
    } catch (RuntimeException rex) {
      if (Config.SHOW_STACKTRACE) {
        rex.printStackTrace();
      }
      return Errors.printMessage(ast.topHead(), rex, engine);
    }
  }

  /** A <code>Graphics</code>, bare or inside a display wrapper such as <code>Legended</code>. */
  private static boolean isGraphics2D(IExpr arg) {
    while (arg.isAST() && IExpr.isPictureWrapperHead(arg.head()) && arg.argSize() >= 1) {
      arg = arg.first();
    }
    return arg.isAST(S.Graphics) && arg.argSize() >= 1;
  }

  private JSGraphicsFunctions() {}

  public static void initialize() {
    Initializer.init();
  }
}
