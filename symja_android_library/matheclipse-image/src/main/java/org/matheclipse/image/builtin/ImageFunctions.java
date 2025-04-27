package org.matheclipse.image.builtin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.image.expression.data.ImageExpr;

public class ImageFunctions {
  private static final Logger LOGGER = LogManager.getLogger(ImageFunctions.class);

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.Image.setEvaluator(new Image());
    }
  }

  private static class Image extends AbstractEvaluator {

    public Image() {}

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isAST()) {
        try {
          ImageExpr imageExpr = ImageExpr.toImageExpr((IAST) ast.arg1());
          if (imageExpr != null) {
            return imageExpr;
          }
        } catch (RuntimeException rex) {
          rex.printStackTrace();
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return IFunctionEvaluator.ARGS_1_2;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private ImageFunctions() {}
}
