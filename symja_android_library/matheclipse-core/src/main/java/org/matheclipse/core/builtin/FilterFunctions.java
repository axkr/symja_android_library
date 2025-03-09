package org.matheclipse.core.builtin;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import it.unimi.dsi.fastutil.ints.IntList;

public class FilterFunctions {

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

    protected boolean isValid(IExpr arg1) {
      return true;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        if (ast.arg1().isList()) {
          IAST list = (IAST) ast.arg1();
          IntList dims = LinearAlgebra.dimensions(list);
          if (dims.size() != 1) {
            // Function `1` not implemented
            return Errors.printMessage(ast.topHead(), "zznotimpl",
                F.List(F.stringx("\"with dimension other than 1\"")), engine);
          }
          if (isValid(list)) {
            final int radius = ast.arg2().toIntDefault();
            if (radius >= 0) {
              return filterHead(list, radius, filterHead(), engine);
            }
          }
        }
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        return Errors.printMessage(S.MinFilter, rex, engine);
      }
      return F.NIL;
    }

    private static IExpr filterHead(IAST list, final int radius, IExpr filterHead,
        EvalEngine engine) {
      final IASTMutable result = list.copy();
      final int size = list.size();
      list.forEach((x, i) -> result.set(i, engine.evaluate( //
          F.unaryAST1( //
              filterHead, //
              list.slice(Math.max(1, i - radius), Math.min(size, i + radius + 1)) //
          ))));
      return result;
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
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
    protected boolean isValid(IExpr arg1) {
      return arg1.forAllLeaves(x -> x.isRealResult());
    }

    @Override
    protected IExpr filterHead() {
      return S.Median;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private FilterFunctions() {}
}
