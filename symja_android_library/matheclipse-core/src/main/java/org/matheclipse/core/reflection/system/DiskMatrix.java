package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <pre>
 * DiskMatrix(r)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * gives a matrix whose elements are 1 in a circular region that extends r index positions to each
 * side, and are 0 otherwise.
 * </p>
 * </blockquote>
 * 
 * <pre>
 * DiskMatrix(r, w)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * gives a w x w matrix containing a circular region of 1s.
 * </p>
 * </blockquote>
 * 
 * <pre>
 * DiskMatrix({r1, r2, ...}, ...)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * yields an array whose elements are 1 in a generalized ellipsoidal region that extends ri index
 * positions in the i-th direction.
 * </p>
 * </blockquote>
 */
public class DiskMatrix extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    int argSize = ast.argSize();
    if (argSize != 1 && argSize != 2) {
      return F.NIL;
    }

    boolean arg1IsList = ast.arg1().isList();
    boolean arg2IsList = argSize == 2 && ast.arg2().isList();

    // Determine the number of dimensions
    int dims = 2; // Default to 2D matrix
    if (arg1IsList && arg2IsList) {
      dims = Math.max(ast.arg1().argSize(), ast.arg2().argSize());
    } else if (arg1IsList) {
      dims = ast.arg1().argSize();
    } else if (arg2IsList) {
      dims = ast.arg2().argSize();
    }

    double[] rArr = new double[dims];
    int[] wArr = new int[dims];
    boolean[] rIsAll = new boolean[dims];

    // Extract radii and widths for each dimension
    for (int i = 0; i < dims; i++) {
      IExpr rExpr;
      if (arg1IsList) {
        if (i < ast.arg1().argSize()) {
          rExpr = ((IAST) ast.arg1()).get(i + 1);
        } else {
          return F.NIL; // List dimension mismatch
        }
      } else {
        rExpr = ast.arg1(); // Broadcast scalar radius
      }

      IExpr wExpr = null;
      if (argSize == 2) {
        if (arg2IsList) {
          if (i < ast.arg2().argSize()) {
            wExpr = ((IAST) ast.arg2()).get(i + 1);
          } else {
            return F.NIL; // List dimension mismatch
          }
        } else {
          wExpr = ast.arg2(); // Broadcast scalar width
        }
      }

      // Parse the radius value
      IExpr evalR = engine.evaluate(rExpr);
      if (evalR.equals(S.All)) {
        rIsAll[i] = true;
        rArr[i] = -1.0;
      } else {
        rIsAll[i] = false;
        try {
          rArr[i] = evalR.evalf();
        } catch (Exception e) {
          return F.NIL;
        }
        if (rArr[i] < 0) {
          return F.NIL; // Negative radius is invalid
        }
      }

      // Parse the width value
      if (wExpr == null) {
        wArr[i] = -1;
      } else {
        IExpr evalW = engine.evaluate(wExpr);
        if (evalW.equals(S.All)) {
          wArr[i] = -1;
        } else {
          wArr[i] = evalW.toIntDefault(-1);
          if (wArr[i] <= 0) {
            return F.NIL;
          }
        }
      }

      // Validate dependencies between All and values
      if (wArr[i] == -1 && rIsAll[i]) {
        return F.NIL; // Cannot infer both width and radius simultaneously
      }

      // Infer missing dimensions
      if (wArr[i] == -1) {
        // Automatically choose an odd width to fit the disk exactly
        wArr[i] = (int) (2 * Math.floor(rArr[i]) + 1);
      }

      if (rIsAll[i]) {
        // Extend the disk radius to the boundaries of the matrix width
        rArr[i] = (wArr[i] - 1) / 2.0;
      }
    }

    try {
      // Determine the center coordinate for each dimension
      double[] cArr = new double[dims];
      for (int i = 0; i < dims; i++) {
        cArr[i] = (wArr[i] - 1) / 2.0;
      }

      int[] indices = new int[dims];
      return buildDisk(0, indices, wArr, rArr, cArr);
    } catch (ArrayIndexOutOfBoundsException aieoobe) {
      return F.NIL; // Handle any unexpected dimension issues gracefully
    }
  }

  /**
   * Recursively builds the N-dimensional nested arrays.
   */
  private IAST buildDisk(int currentDim, int[] indices, int[] wArr, double[] rArr, double[] cArr) {
    int currentW = wArr[currentDim];
    IASTAppendable list = F.ListAlloc(currentW);

    for (int i = 0; i < currentW; i++) {
      indices[currentDim] = i;

      if (currentDim == wArr.length - 1) {
        // Evaluate the scaled Euclidean (L2) distance condition for the current coordinate
        double sum = 0.0;
        for (int d = 0; d < wArr.length; d++) {
          if (rArr[d] > 0) {
            double diff = (indices[d] - cArr[d]) / rArr[d];
            sum += diff * diff;
          } else if (Math.abs(indices[d] - cArr[d]) > 0) {
            sum += 10000.0; // Acts as infinity for zero radius constraints
          }
        }

        // Include a small epsilon tolerance for floating-point inaccuracies
        if (sum <= 1.0 + 1e-9) {
          list.append(F.C1);
        } else {
          list.append(F.C0);
        }
      } else {
        // Traverse deeper into the dimensions
        list.append(buildDisk(currentDim + 1, indices, wArr, rArr, cArr));
      }
    }

    return list;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }
}
