package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.ASTRealVector;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ITensorAccess;

public class MovingAverage extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    IExpr arg2 = ast.arg2();

    if (arg1 instanceof ITensorAccess) {
      ITensorAccess list = (ITensorAccess) arg1;
      int n = list.argSize();

      // Case 1: MovingAverage(list, r)
      if (arg2.isInteger()) {
        int r = arg2.toIntDefault();
        if (r > 0) {
          if (r > n) {
            return F.NIL; // Block size cannot be larger than the list size
          }
          int outSize = n - r + 1;

          // Optimization for primitive double arrays
          if (list.hasNumericArgument()) {
            double[] data = list.toDoubleVector();
            if (data != null) {
              double[] res = new double[outSize];
              for (int i = 0; i < outSize; i++) {
                double s = 0.0;
                for (int j = 0; j < r; j++) {
                  s += data[i + j];
                }
                res[i] = s / r;
              }
              return new ASTRealVector(res, false);
            }
          }

          // Generic AST evaluation for symbolic and exact math
          IASTAppendable result = F.ListAlloc(outSize);
          IExpr denom = F.ZZ(r);
          for (int i = 1; i <= outSize; i++) {
            IASTAppendable sum = F.PlusAlloc(r);
            for (int j = 0; j < r; j++) {
              IExpr elem = list.get(i + j);
              sum.append(elem);
            }
            result.append(engine.evaluate(F.Divide(sum, denom)));
          }
          return result;
        }
      }

      // Case 2: MovingAverage(list, {w1, w2, ..., wr})
      if (arg2.isList()) {
        IAST wts = (IAST) arg2;
        int r = wts.argSize();
        if (r > 0) {
          if (r > n) {
            return F.NIL;
          }
          int outSize = n - r + 1;

          // Optimization for primitive double arrays
          if (list.isRealVector() && wts.isRealVector()) {
            double[] data = list.toDoubleVector();
            double[] weights = wts.toDoubleVector();
            if (data != null && weights != null) {
              double wtsSum = 0.0;
              for (int i = 0; i < weights.length; i++) {
                wtsSum += weights[i];
              }
              // Fallback to symbolic if weight sum is exactly zero to let Symja handle
              // ComplexInfinity
              if (wtsSum != 0.0) {
                double[] res = new double[outSize];
                for (int i = 0; i < outSize; i++) {
                  double s = 0.0;
                  for (int j = 0; j < r; j++) {
                    s += weights[j] * data[i + j];
                  }
                  res[i] = s / wtsSum;
                }
                return new ASTRealVector(res, false);
              }
            }
          }

          // Generic AST evaluation for symbolic weights/data
          IExpr totalWts = engine.evaluate(wts.apply(S.Plus));
          IASTAppendable result = F.ListAlloc(outSize);
          for (int i = 1; i <= outSize; i++) {
            IASTAppendable sum = F.PlusAlloc(r);
            for (int j = 0; j < r; j++) {
              sum.append(F.Times(wts.get(j + 1), list.get(i + j)));
            }
            result.append(engine.evaluate(F.Divide(sum, totalWts)));
          }
          return result;
        }
      }
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_2;
  }
}