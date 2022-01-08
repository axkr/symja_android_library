package org.matheclipse.io.builtin;

import java.awt.Dimension;
import javax.swing.SwingUtilities;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.JavaObjectExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.ParserConfig;

public class DynamicSwingFunctions {
	
  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      if (!Config.FUZZY_PARSER) {
        if (!ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
          S.JavaShow.setEvaluator(new JavaShow());
        }
      }
    }
  }

  private static class JavaShow extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg = ast.arg1();
      if (arg instanceof JavaObjectExpr) {
        Object obj = ((JavaObjectExpr) arg).toData();
        if (obj instanceof java.awt.Window) {
          final java.awt.Window window = (java.awt.Window) obj;
          SwingUtilities.invokeLater(
              new Runnable() {
                public void run() {
                  window.setSize(new Dimension(640, 400));
                  // center the window
                  window.setLocationRelativeTo(null);
                  // display the window
                  window.setVisible(true);
                }
              });
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private DynamicSwingFunctions() {}
}
