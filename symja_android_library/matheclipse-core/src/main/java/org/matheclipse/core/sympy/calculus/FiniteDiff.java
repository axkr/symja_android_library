package org.matheclipse.core.sympy.calculus;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.sympy.exception.ValueError;

/**
 * Finite difference weights. Ported from
 * <a href="https://github.com/sympy/sympy/blob/master/sympy/calculus/finite_diff.py">sympy/calculus/finite_diff.py</a>
 */
public class FiniteDiff {

  private FiniteDiff() {}

  /**
   * Calculates the finite difference weights for an arbitrarily spaced one-dimensional grid
   * (<code>xList</code>) for derivatives at <code>x0</code> of order 0, 1, ..., up to
   * <code>order</code> using the recursive formula of B. Fornberg.
   *
   * @param order up to what derivative order weights should be calculated. 0 corresponds to
   *        interpolation.
   * @param xList list of (unique) values for the independent variable
   * @param x0 value of the independent variable for which the finite difference weights should be
   *        generated
   * @return <code>delta[m][n][nu]</code> - for each derivative order <code>m</code> the weights
   *         for the increasing subsets <code>xList[0..n]</code>
   */
  public static IExpr[][][] finiteDiffWeightsArray(int order, IAST xList, IExpr x0) {
    // >>> res = finite_diff_weights(1, [-S(1)/2, S(1)/2, S(3)/2, S(5)/2], 0)
    // >>> res
    // [[[1, 0, 0, 0],
    // [1/2, 1/2, 0, 0],
    // [3/8, 3/4, -1/8, 0],
    // [5/16, 15/16, -5/16, 1/16]],
    // [[0, 0, 0, 0],
    // [-1, 1, 0, 0],
    // [-1, 1, 0, 0],
    // [-23/24, 7/8, 1/8, -1/24]]]
    if (order < 0) {
      throw new ValueError("Negative derivative order illegal.");
    }
    if (xList.argSize() < 1) {
      throw new ValueError("x_list must not be empty.");
    }
    // The notation below closely corresponds to the one used in the paper.
    EvalEngine engine = EvalEngine.get();
    final int M = order;
    final int N = xList.argSize() - 1;
    IExpr[][][] delta = new IExpr[M + 1][N + 1][N + 1];
    for (int m = 0; m <= M; m++) {
      for (int n = 0; n <= N; n++) {
        for (int nu = 0; nu <= N; nu++) {
          delta[m][n][nu] = F.C0;
        }
      }
    }
    delta[0][0][0] = F.C1;
    IExpr c1 = F.C1;
    for (int n = 1; n <= N; n++) {
      IExpr xn = xList.get(n + 1);
      IExpr c2 = F.C1;
      for (int nu = 0; nu < n; nu++) {
        IExpr c3 = engine.evaluate(F.Subtract(xn, xList.get(nu + 1)));
        c2 = engine.evaluate(F.Times(c2, c3));
        if (n <= M) {
          delta[n][n - 1][nu] = F.C0;
        }
        for (int m = 0; m <= Math.min(n, M); m++) {
          // delta[m][n][nu] = ((x_list[n]-x0)*delta[m][n-1][nu] - m*delta[m-1][n-1][nu]) / c3
          IExpr previous = (m == 0) ? F.C0 : F.Times(F.ZZ(m), delta[m - 1][n - 1][nu]);
          delta[m][n][nu] = engine.evaluate(F.Divide(
              F.Subtract(F.Times(F.Subtract(xn, x0), delta[m][n - 1][nu]), previous), c3));
        }
      }
      for (int m = 0; m <= Math.min(n, M); m++) {
        // delta[m][n][n] = c1/c2*(m*delta[m-1][n-1][n-1] - (x_list[n-1]-x0)*delta[m][n-1][n-1])
        IExpr previous = (m == 0) ? F.C0 : F.Times(F.ZZ(m), delta[m - 1][n - 1][n - 1]);
        delta[m][n][n] = engine.evaluate(F.Times(c1, F.Power(c2, F.CN1),
            F.Subtract(previous, F.Times(F.Subtract(xList.get(n), x0), delta[m][n - 1][n - 1]))));
      }
      c1 = c2;
    }
    return delta;
  }

  /**
   * See {@link #finiteDiffWeightsArray(int, IAST, IExpr)}. Returns the weights as nested lists.
   */
  public static IAST finiteDiffWeights(int order, IAST xList, IExpr x0) {
    IExpr[][][] delta = finiteDiffWeightsArray(order, xList, x0);
    IASTAppendable result = F.ListAlloc(delta.length);
    for (int m = 0; m < delta.length; m++) {
      IASTAppendable subsets = F.ListAlloc(delta[m].length);
      for (int n = 0; n < delta[m].length; n++) {
        subsets.append(F.List(delta[m][n]));
      }
      result.append(subsets);
    }
    return result;
  }

  /**
   * The finite difference weights for the derivative of order <code>order</code> using the full
   * <code>xList</code>.
   */
  public static IAST finiteDiffWeightsLast(int order, IAST xList, IExpr x0) {
    IExpr[][][] delta = finiteDiffWeightsArray(order, xList, x0);
    return F.List(delta[order][xList.argSize() - 1]);
  }

  /**
   * Calculates the finite difference approximation of the derivative of requested order at
   * <code>x0</code> from points provided in <code>xList</code> and <code>yList</code>.
   *
   * @param order derivative order, 0 corresponds to interpolation
   * @param xList list of (unique) values for the independent variable
   * @param yList the function value at corresponding values for the independent variable in
   *        <code>xList</code>
   * @param x0 at what value of the independent variable the derivative should be evaluated
   * @return
   */
  public static IExpr applyFiniteDiff(int order, IAST xList, IAST yList, IExpr x0) {
    // >>> from sympy import apply_finite_diff
    // >>> cube = lambda arg: (1.0*arg)**3
    // >>> xlist = range(-3,3+1)
    // >>> apply_finite_diff(2, xlist, map(cube, xlist), 2) - 12 # doctest: +SKIP
    // -3.55271367880050e-15
    if (xList.argSize() != yList.argSize()) {
      throw new ValueError("x_list and y_list not equal in length.");
    }
    final int N = xList.argSize() - 1;
    IExpr[][][] delta = finiteDiffWeightsArray(order, xList, x0);
    IASTAppendable derivative = F.PlusAlloc(N + 1);
    for (int nu = 0; nu <= N; nu++) {
      derivative.append(F.Times(delta[order][N][nu], yList.get(nu + 1)));
    }
    return EvalEngine.get().evaluate(derivative);
  }
}
