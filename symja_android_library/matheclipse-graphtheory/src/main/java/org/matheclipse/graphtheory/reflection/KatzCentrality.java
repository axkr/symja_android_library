package org.matheclipse.graphtheory.reflection;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.graphtheory.eval.GraphUtil;
import org.matheclipse.graphtheory.eval.GraphView;
import org.matheclipse.graphtheory.expression.data.GraphExpr;

/**
 * <code>KatzCentrality(g, a)</code>, <code>KatzCentrality(g, a, b)</code> - the solution of
 * <code>x = a A^T x + b</code>; <code>b</code> is 1, a number or a list of numbers.
 */
public class KatzCentrality extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
    if (gex == null) {
      return F.NIL;
    }
    GraphView view = GraphView.of(gex.toData());
    final int n = view.n;
    IExpr bExpr = ast.isAST3() ? ast.arg3() : F.C1;
    IAST bList;
    if (bExpr.isList()) {
      if (bExpr.argSize() != n) {
        return F.NIL;
      }
      bList = (IAST) bExpr;
    } else {
      bList = F.constantArray(bExpr, n);
    }
    if (ast.arg2().isZero() || view.m == 0) {
      // x = b exactly
      return bList;
    }
    double a = ast.arg2().evalfNaN();
    if (Double.isNaN(a) || n > 4000) {
      return F.NIL;
    }
    double[] b = new double[n];
    for (int i = 0; i < n; i++) {
      b[i] = bList.get(i + 1).evalfNaN();
      if (Double.isNaN(b[i])) {
        return F.NIL;
      }
    }
    // (I - a A^T) x = b
    double[][] m = new double[n][n];
    for (int v = 0; v < n; v++) {
      m[v][v] = 1.0;
      for (int e : view.in[v]) {
        m[v][view.other(e, v)] -= a;
      }
    }
    double[] x = solve(m, b);
    if (x == null) {
      return F.NIL;
    }
    return F.mapRange(0, n, v -> F.num(x[v]));
  }

  /** Gaussian elimination with partial pivoting; <code>null</code> for a singular system. */
  private static double[] solve(double[][] m, double[] b) {
    final int n = b.length;
    double scale = 0.0;
    for (double[] row : m) {
      for (double d : row) {
        scale = Math.max(scale, Math.abs(d));
      }
    }
    for (int col = 0; col < n; col++) {
      int pivot = col;
      for (int row = col + 1; row < n; row++) {
        if (Math.abs(m[row][col]) > Math.abs(m[pivot][col])) {
          pivot = row;
        }
      }
      if (Math.abs(m[pivot][col]) <= 1.0e-12 * scale) {
        return null;
      }
      double[] t = m[col];
      m[col] = m[pivot];
      m[pivot] = t;
      double tb = b[col];
      b[col] = b[pivot];
      b[pivot] = tb;
      for (int row = col + 1; row < n; row++) {
        double factor = m[row][col] / m[col][col];
        if (factor != 0.0) {
          for (int k = col; k < n; k++) {
            m[row][k] -= factor * m[col][k];
          }
          b[row] -= factor * b[col];
        }
      }
    }
    double[] x = new double[n];
    for (int row = n - 1; row >= 0; row--) {
      double sum = b[row];
      for (int k = row + 1; k < n; k++) {
        sum -= m[row][k] * x[k];
      }
      x[row] = sum / m[row][row];
    }
    return x;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_3;
  }
}
