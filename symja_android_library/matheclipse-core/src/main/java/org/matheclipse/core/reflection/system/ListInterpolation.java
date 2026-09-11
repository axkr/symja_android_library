package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.InterpolatingFunctionExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <code>ListInterpolation(list)</code>, <code>ListInterpolation(list, {{xmin, xmax}})</code>,
 * <code>ListInterpolation(matrix)</code>, <code>ListInterpolation(matrix, {{xmin, xmax}, {ymin,
 * ymax}})</code> - an <code>InterpolatingFunction</code> through values given on an evenly spaced
 * grid: <code>f(i)</code> is <code>list[[i]]</code> and <code>f(i, j)</code> is
 * <code>matrix[[i, j]]</code> unless a domain is given.
 *
 * <p>
 * A list is interpolated as <code>Interpolation</code> interpolates the points
 * <code>{{x1, f1}, ...}</code>. A matrix is interpolated by a bicubic spline for
 * <code>InterpolationOrder -&gt; 3</code>, the default, where there are at least 5 values along
 * each axis, and bilinearly otherwise.
 */
public class ListInterpolation extends AbstractFunctionOptionEvaluator {

  public ListInterpolation() {}

  @Override
  public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
      IAST originalAST) {
    IExpr data = engine.evalN(ast.arg1());
    if (!data.isList() || data.argSize() < 2) {
      return F.NIL;
    }
    int order = options[0].toIntDefault();
    if (order < 0) {
      order = 3;
    }
    IExpr domain = argSize >= 2 ? engine.evalN(ast.arg2()) : F.NIL;

    int[] dims = data.isMatrix();
    if (dims != null && dims[0] >= 2 && dims[1] >= 2) {
      double[][] values = new double[dims[0]][dims[1]];
      for (int i = 0; i < dims[0]; i++) {
        for (int j = 0; j < dims[1]; j++) {
          double v = data.getAt(i + 1).getAt(j + 1).evalfNaN();
          if (!Double.isFinite(v)) {
            return F.NIL;
          }
          values[i][j] = v;
        }
      }
      double[] xs = axis(domain, 1, dims[0]);
      double[] ys = axis(domain, 2, dims[1]);
      if (xs == null || ys == null) {
        return F.NIL;
      }
      return grid(xs, ys, values, order);
    }

    // a list of values: the points {x_i, f_i} are what Interpolation interpolates
    IAST list = (IAST) data;
    for (IExpr value : list) {
      if (!value.isNumber()) {
        return F.NIL;
      }
    }
    double[] xs = axis(domain, 1, list.argSize());
    if (xs == null) {
      return F.NIL;
    }
    IASTAppendable points = F.ListAlloc(list.argSize());
    for (int i = 1; i <= list.argSize(); i++) {
      points.append(F.List(F.num(xs[i - 1]), list.get(i)));
    }
    return engine.evaluate(F.unaryAST1(S.Interpolation, points));
  }

  /**
   * An <code>InterpolatingFunction</code> of two variables through <code>values[i][j]</code> at
   * <code>(xs[i], ys[j])</code> - what <code>ListInterpolation</code> of a matrix answers, and what
   * <code>NDSolve</code> of a partial differential equation hands back.
   */
  public static IExpr grid(double[] xs, double[] ys, double[][] values, int order) {
    return InterpolatingFunctionExpr.newGrid(xs, ys, values, order);
  }

  /**
   * The <code>n</code> evenly spaced points of axis <code>k</code>: <code>1 .. n</code>, or the
   * domain's <code>{min, max}</code> for that axis; <code>null</code> where the domain is not a
   * pair of numbers.
   */
  private static double[] axis(IExpr domain, int k, int n) {
    double min = 1.0;
    double max = n;
    if (domain.isPresent()) {
      if (!domain.isList() || domain.argSize() < k || !domain.getAt(k).isList2()) {
        return null;
      }
      min = domain.getAt(k).first().evalfNaN();
      max = domain.getAt(k).second().evalfNaN();
      if (!(max > min)) {
        return null;
      }
    }
    double[] axis = new double[n];
    for (int i = 0; i < n; i++) {
      axis[i] = n == 1 ? min : min + (max - min) * i / (n - 1);
    }
    return axis;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_2;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol, //
        new IBuiltInSymbol[] {S.InterpolationOrder}, //
        new IExpr[] {S.Automatic});
  }
}
