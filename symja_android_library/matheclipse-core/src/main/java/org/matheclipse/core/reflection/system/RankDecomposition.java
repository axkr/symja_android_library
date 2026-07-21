package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class RankDecomposition extends AbstractFunctionEvaluator {

  public RankDecomposition() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    int[] dims = arg1.isMatrix(false);

    if (dims != null) {
      int rows = dims[0];

      // Calculate the Reduced Row Echelon Form (RREF)
      IExpr rrefExpr = engine.evaluate(F.RowReduce(arg1));

      if (rrefExpr.isMatrix(false) != null) {
        IAST rref = (IAST) rrefExpr.normal(false);
        IAST matrixM = (IAST) arg1.normal(false);

        IASTAppendable matrixB = F.ListAlloc(rows);
        List<Integer> pivotColumns = new ArrayList<>();

        // Identify pivot columns and collect non-zero rows for matrix B
        for (int i = 1; i <= rref.argSize(); i++) {
          IAST row = (IAST) rref.get(i);
          boolean isZeroRow = true;

          for (int j = 1; j <= row.argSize(); j++) {
            IExpr element = row.get(j);
            // Use PossibleZeroQ for robust zero testing
            if (!engine.evaluate(F.PossibleZeroQ(element)).isTrue()) {
              isZeroRow = false;
              pivotColumns.add(j);
              break;
            }
          }

          if (!isZeroRow) {
            matrixB.append(row);
          }
        }

        // Handle the rank 0 / zero-matrix edge case
        if (pivotColumns.isEmpty()) {
          // A rank decomposition does not exist for a rank 0 matrix.
          return Errors.printMessage(ast.topHead(), "rnkz", F.CEmptyList, engine);
        }

        // Extract the linearly independent columns from M to construct matrix A
        IASTAppendable matrixA = F.ListAlloc(rows);
        for (int i = 1; i <= matrixM.argSize(); i++) {
          IAST rowM = (IAST) matrixM.get(i);
          IASTAppendable newRowA = F.ListAlloc(pivotColumns.size());

          for (int j : pivotColumns) {
            newRowA.append(rowM.get(j));
          }
          matrixA.append(newRowA);
        }

        return F.List(matrixA, matrixB);
      }
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {}
}
