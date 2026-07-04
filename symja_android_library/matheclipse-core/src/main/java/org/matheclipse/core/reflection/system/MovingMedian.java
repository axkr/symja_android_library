package org.matheclipse.core.reflection.system;

import org.hipparchus.stat.descriptive.DescriptiveStatistics;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.ASTRealVector;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ITensorAccess;

public class MovingMedian extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {

    IExpr arg1 = ast.arg1();
    IExpr arg2 = ast.arg2();
    if (!(arg1 instanceof ITensorAccess)) {
      // The first argument `1` is expected to be `1`.
      return Errors.printMessage(S.MovingMedian, "arg1",
          F.List(arg1, F.stringx("a vector or matrix of real values")), engine);
    }
    ITensorAccess list = (ITensorAccess) arg1;
    int n = list.isVector();
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

          // Slide the window and compute the median for each step
          for (int i = 0; i < outSize; i++) {
            stats.addValue(data[i + r - 1]);
            res[i] = stats.getPercentile(50.0);
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

    return F.NIL;

  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_2;
  }
}
