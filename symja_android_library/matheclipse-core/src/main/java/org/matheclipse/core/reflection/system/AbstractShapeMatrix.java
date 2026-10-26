package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Shared evaluation of <code>CrossMatrix</code>, <code>DiamondMatrix</code> and
 * <code>DiskMatrix</code>: <code>f(r)</code>, <code>f(r, w)</code> and
 * <code>f({r1, r2, ...}, {w1, w2, ...})</code> build a nested list of 0s and 1s, where an element is
 * 1 if its index lies inside the shape of radius <code>ri</code> centered in the array.
 *
 * <p>
 * A radius <code>All</code> extends the shape to the array boundary; a missing width or a width
 * <code>All</code> is derived from the radius by {@link #defaultWidth(double)}.
 */
public abstract class AbstractShapeMatrix extends AbstractFunctionEvaluator {

  /**
   * The width of a dimension whose width is not given.
   *
   * @param radius the radius in this dimension
   * @return an odd width that fits the shape
   */
  protected abstract int defaultWidth(double radius);

  /**
   * Test whether the element at <code>indices</code> belongs to the shape.
   *
   * @param indices the 0-based index of the element in each dimension
   * @param radii the radius in each dimension
   * @param center the center coordinate in each dimension
   * @return <code>true</code> if the element is 1
   */
  protected abstract boolean contains(int[] indices, double[] radii, double[] center);

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

    if (dims == 0) {
      return F.NIL;
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
      if (evalR == S.All) {
        rIsAll[i] = true;
        rArr[i] = -1.0;
      } else {
        rIsAll[i] = false;
        rArr[i] = evalR.evalfNaN();
        if (Double.isNaN(rArr[i])) {
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
        if (evalW == S.All) {
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
        wArr[i] = defaultWidth(rArr[i]);
      }

      if (rIsAll[i]) {
        // Extend the radius to the boundaries of the matrix width
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
      return build(0, indices, wArr, rArr, cArr);
    } catch (ArrayIndexOutOfBoundsException aieoobe) {
      return F.NIL; // Handle any unexpected dimension issues gracefully
    }
  }

  /** Recursively builds the N-dimensional nested arrays. */
  private IAST build(int currentDim, int[] indices, int[] wArr, double[] rArr, double[] cArr) {
    int currentW = wArr[currentDim];
    IASTAppendable list = F.ListAlloc(currentW);

    for (int i = 0; i < currentW; i++) {
      indices[currentDim] = i;
      if (currentDim == wArr.length - 1) {
        list.append(contains(indices, rArr, cArr) ? F.C1 : F.C0);
      } else {
        // Traverse deeper into the dimensions
        list.append(build(currentDim + 1, indices, wArr, rArr, cArr));
      }
    }
    return list;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }
}
