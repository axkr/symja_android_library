package org.matheclipse.core.builtin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;

public class ImageFunctions {
  private static final Logger LOGGER = LogManager.getLogger();

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.MaxFilter.setEvaluator(new MaxFilter());
      S.MeanFilter.setEvaluator(new MeanFilter());
      S.MedianFilter.setEvaluator(new MedianFilter());
      S.MinFilter.setEvaluator(new MinFilter());
    }
  }

  private static class MinFilter extends AbstractEvaluator {

    protected IExpr filterHead() {
      return S.Min;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        if (ast.arg1().isList()) {
          IAST list = (IAST) ast.arg1();
          final int radius = ast.arg2().toIntDefault();
          if (radius >= 0) {
            return filterHead(list, radius, filterHead(), engine);
          }
        }
      } catch (RuntimeException rex) {
        LOGGER.debug("MinFilter.evaluate() failed", rex);
      }
      return F.NIL;
    }

    private static IExpr filterHead(
        IAST list, final int radius, IExpr filterHead, EvalEngine engine) {
      final IASTMutable result = list.copy();
      final int size = list.size();
      list.forEach(
          (x, i) ->
              result.set(
                  i,
                  engine.evaluate( //
                      F.unaryAST1( //
                          filterHead, //
                          list.slice(Math.max(1, i - radius), Math.min(size, i + radius + 1)) //
                          ))));
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  private static class MaxFilter extends MinFilter {
    @Override
    protected IExpr filterHead() {
      return S.Max;
    }
  }

  private static class MeanFilter extends MinFilter {
    @Override
    protected IExpr filterHead() {
      return S.Mean;
    }
  }

  private static class MedianFilter extends MinFilter {
    @Override
    protected IExpr filterHead() {
      return S.Median;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private ImageFunctions() {}
}
