package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

public class BoxMatrix extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    IExpr arg2 = ast.argSize() == 2 ? ast.arg2() : null;

    int dimension = 2;
    IAST listR = null;
    IAST listW = null;
    boolean rIsAll = arg1 == S.All;

    // Determine dimensions from r
    if (arg1.isList()) {
      listR = (IAST) arg1;
      dimension = listR.argSize();
    } else if (!rIsAll) {
      // Validate that it evaluates to a number
      try {
        arg1.evalf();
      } catch (Exception e) {
        return F.NIL;
      }
    }

    // Determine dimensions from w
    if (arg2 != null) {
      if (arg2.isList()) {
        listW = (IAST) arg2;
        if (listR == null) {
          dimension = listW.argSize();
        } else if (listR.argSize() != listW.argSize()) {
          return F.NIL; // Dimensions mismatch
        }
      } else {
        if (listR != null) {
          dimension = listR.argSize();
        } else {
          dimension = 2; // Both r and w are scalars
        }
      }
    }

    double[] r = new double[dimension];
    int[] w = new int[dimension];

    // Parse radii
    for (int i = 0; i < dimension; i++) {
      if (rIsAll) {
        r[i] = Double.POSITIVE_INFINITY;
      } else if (listR != null) {
        IExpr ri = listR.get(i + 1);
        if (ri == S.All) {
          r[i] = Double.POSITIVE_INFINITY;
        } else {
          try {
            r[i] = ri.evalf();
          } catch (Exception e) {
            return F.NIL;
          }
        }
      } else {
        try {
          r[i] = arg1.evalf();
        } catch (Exception e) {
          return F.NIL;
        }
      }
    }

    // Parse target dimensions w
    for (int i = 0; i < dimension; i++) {
      if (arg2 == null) {
        if (Double.isInfinite(r[i])) {
          return F.NIL;
        }
        int maxDist = (int) Math.floor(Math.abs(r[i] + 0.5));
        w[i] = 2 * maxDist + 1;
        } else {
        // Check for All in the list element
        boolean wIsAll = false;
        if (listW != null) {
          wIsAll = listW.get(i + 1) == S.All;
        } else {
          wIsAll = arg2 == S.All;
        }

        if (wIsAll) {
          // All means: use the natural size derived from r
          if (Double.isInfinite(r[i])) {
            return F.NIL; // Cannot determine size with both All
          }
          int maxDist = (int) Math.floor(Math.abs(r[i] + 0.5));
          w[i] = 2 * maxDist + 1;
        } else {
          int wVal;
          if (listW != null) {
            wVal = listW.get(i + 1).toIntDefault();
          } else {
            wVal = arg2.toIntDefault();
          }
          if (wVal < 0) {
            return F.NIL;
          }
          w[i] = wVal;
        }
        }
    }


    int[] currentIndices = new int[dimension];
    return buildMatrix(w, r, 0, currentIndices);
  }

  /**
   * Recursively constructs the nested List representation of the matrix.
   * 
   * @param w target sizes of each dimension
   * @param r required radii
   * @param depth current dimension nesting depth
   * @param currentIndices tracked indices for distance evaluation
   * @return the constructed IExpr (either a List or a 1 / 0)
   */
  private static IExpr buildMatrix(int[] w, double[] r, int depth, int[] currentIndices) {
    // Base case: we are at the deepest level, evaluate if element falls within the box
    if (depth == w.length) {
      boolean isInsideBox = true;
      for (int i = 0; i < w.length; i++) {
        if (Double.isInfinite(r[i])) {
          continue; // Skip check for All
        }

        // Continuous center for calculation
        double center = (w[i] + 1) / 2.0;
        double dist = Math.abs(currentIndices[i] - center);

        // Chessboard distance condition
        if (dist > Math.abs(r[i] + 0.5)) {
          isInsideBox = false;
          break;
        }
      }
      return isInsideBox ? F.C1 : F.C0;
    }

    // Recursive case: allocate list and iterate current dimension
    IASTAppendable list = F.ListAlloc(w[depth]);
    for (int i = 1; i <= w[depth]; i++) {
      currentIndices[depth] = i;
      list.append(buildMatrix(w, r, depth + 1, currentIndices));
    }
    return list;
  }


  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }
}
