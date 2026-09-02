package org.matheclipse.core.reflection.system;

import org.hipparchus.stat.descriptive.DescriptiveStatistics;
import org.matheclipse.core.builtin.StatisticsFunctions;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.ASTRealVector;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.interfaces.ITensorAccess;
import org.matheclipse.core.patternmatching.IPatternMatcher;

public class MovingMedian extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {

    // an Association contributes its values, a Dataset its rows
    IExpr arg1 = StatisticsFunctions.normalizeData(ast.arg1());
    if (StatisticsFunctions.isQuantityVector(arg1)) {
      // a message from here is the whole answer; do not fall through to the arg1 message as well
      return StatisticsFunctions.quantityStatistic(ast.setAtCopy(1, arg1), engine);
    }
    IExpr arg2 = ast.arg2();
    if (arg1.isMatrix() != null && arg2.isInteger()) {
      // each window is a block of rows, and its median is columnwise - Median already does that
      IAST matrix = (IAST) arg1;
      int r = arg2.toIntDefault();
      int n = matrix.argSize();
      if (r > 0 && r <= n) {
        int outSize = n - r + 1;
        IASTAppendable result = F.ListAlloc(outSize);
        for (int i = 1; i <= outSize; i++) {
          IASTAppendable window = F.ListAlloc(r);
          for (int j = 0; j < r; j++) {
            window.append(matrix.get(i + j));
          }
          result.append(engine.evaluate(F.Median(window)));
        }
        return result;
      }
      return windowMessage(arg2, n, engine);
    }
    if (!(arg1 instanceof ITensorAccess)) {
      // The first argument `1` is expected to be `1`.
      return Errors.printMessage(S.MovingMedian, "arg1",
          F.List(arg1, F.stringx("a vector or matrix of real values")), engine);
    }
    ITensorAccess list = (ITensorAccess) arg1;
    int n = list.isVector();
    if (n < 0) {
      // The first argument `1` is expected to be `1`.
      return Errors.printMessage(S.MovingMedian, "arg1",
          F.List(arg1, F.stringx("a vector or matrix of real values")), engine);
    }
    if (arg2.isInteger()) {
      int r = arg2.toIntDefault();

      if (r > 0 && r <= n) {
        int outSize = n - r + 1;
        if (list.hasNumericArgument()) {
          double[] data = list.toDoubleVector();
          if (data == null) {
            // The first argument `1` is expected to be `1`.
            return Errors.printMessage(S.MovingMedian, "arg1",
                F.List(arg1, F.stringx("a vector or matrix of real values")), engine);
          }
          double[] res = new double[outSize];

          // Setting a fixed window size automatically creates a rolling FIFO buffer
          DescriptiveStatistics stats = new DescriptiveStatistics(r);

          // Prime the window with the first (r - 1) elements
          for (int i = 0; i < r - 1; i++) {
            stats.addValue(data[i]);
          }

          // Slide the window and compute the median for each step. DescriptiveStatistics#
          // getPercentile(50) would interpolate the even-window case as lo + 0.5*(hi-lo), which
          // overflows when the two middle values straddle zero near the top of the double range.
          for (int i = 0; i < outSize; i++) {
            stats.addValue(data[i + r - 1]);
            res[i] = StatisticsFunctions.medianOfSorted(stats.getSortedValues());
          }

          return new ASTRealVector(res, false);
        }

        // Generic AST evaluation for symbolic expressions and exact math (fractions, etc.)
        IASTAppendable result = F.ListAlloc(outSize);
        for (int i = 1; i <= outSize; i++) {
          IASTAppendable window = F.ListAlloc(r);
          for (int j = 0; j < r; j++) {
            IExpr elem = list.get(i + j);
            if (!elem.isReal()) {
              // The first argument `1` is expected to be `1`.
              return Errors.printMessage(S.MovingMedian, "arg1",
                  F.List(arg1, F.stringx("a vector or matrix of real values")), engine);
            }
            window.append(elem);
          }
          result.append(engine.evaluate(F.Median(window)));
        }
        return result;
      }
    }

    return windowMessage(arg2, n, engine);
  }

  /**
   * Report a window specification that is not a positive integer no larger than the data.
   *
   * <p>
   * The <code>arg2</code> message is registered on <code>MovingMedian</code> itself in
   * {@link #setUp(ISymbol)}: the tag is the one Wolfram Language uses, but it is already taken in
   * the shared table by an unrelated text about dividing an equation by zero, and
   * {@link Errors#printMessage} consults the symbol before the shared table.
   *
   * @param n the length of the first argument
   */
  private static IExpr windowMessage(IExpr arg2, int n, EvalEngine engine) {
    // The second argument `1` must be a positive integer less than or equal to the length `2` of
    // the first argument.
    return Errors.printMessage(S.MovingMedian, "arg2", F.List(arg2, F.ZZ(n)), engine);
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.putMessage(IPatternMatcher.SET, "arg2", F.stringx(
        "The second argument `1` must be a positive integer less than or equal to the length `2` of the first argument."));
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_2;
  }
}
