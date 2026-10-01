package org.matheclipse.core.builtin;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.LinearAlgebraUtil;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.external.fastutil.ints.IntList;

public class FilterFunctions {

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.GaussianFilter.setEvaluator(new GaussianFilter());
      S.GaussianMatrix.setEvaluator(new GaussianMatrix());
      S.MaxFilter.setEvaluator(new MaxFilter());
      S.MeanFilter.setEvaluator(new MeanFilter());
      S.MedianFilter.setEvaluator(new MedianFilter());
      S.MinFilter.setEvaluator(new MinFilter());
    }
  }

  private static class MinFilter extends AbstractEvaluator {

    protected IExpr filterHead() {
      return S.Min;
    }

    protected boolean isValid(IExpr arg1) {
      return true;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        if (ast.arg1().isList()) {
          IAST list = (IAST) ast.arg1();
          IntList dims = LinearAlgebraUtil.dimensions(list);
          if (dims.size() != 1 && dims.size() != 2) {
            // Function `1` not implemented
            return Errors.printMessage(ast.topHead(), "zznotimpl",
                F.List(F.stringx("\"with dimension other than 1 or 2\"")), engine);
          }
          if (isValid(list)) {
            final int radius = ast.arg2().toMachineInt();
            if (radius >= 0) {
              return dims.size() == 1 ? filterHead(list, radius, filterHead(), engine)
                  : filterMatrix(list, dims.getInt(0), dims.getInt(1), radius, filterHead(),
                      engine);
            }
          }
        }
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        return Errors.printMessage(S.MinFilter, rex, engine);
      }
      return F.NIL;
    }

    private static IExpr filterHead(IAST list, final int radius, IExpr filterHead,
        EvalEngine engine) {
      final IASTMutable result = list.copy();
      final int size = list.size();
      list.forEach((x, i) -> result.set(i, engine.evaluate( //
          F.unaryAST1( //
              filterHead, //
              list.slice(Math.max(1, i - radius), Math.min(size, i + radius + 1)) //
          ))));
      return result;
    }

    /**
     * The filter of a matrix: <code>filterHead</code> applied to the
     * <code>(2*radius+1) x (2*radius+1)</code> block around every element, which is smaller at the
     * border like the range of the list filter.
     */
    private static IExpr filterMatrix(IAST matrix, int rows, int columns, final int radius,
        IExpr filterHead, EvalEngine engine) {
      final IASTAppendable result = F.ListAlloc(rows);
      for (int i = 1; i <= rows; i++) {
        final IASTAppendable resultRow = F.ListAlloc(columns);
        for (int j = 1; j <= columns; j++) {
          final IASTAppendable block = F.ListAlloc((2 * radius + 1) * (2 * radius + 1));
          for (int k = Math.max(1, i - radius); k <= Math.min(rows, i + radius); k++) {
            final IAST row = (IAST) matrix.get(k);
            for (int l = Math.max(1, j - radius); l <= Math.min(columns, j + radius); l++) {
              block.append(row.get(l));
            }
          }
          resultRow.append(engine.evaluate(F.unaryAST1(filterHead, block)));
        }
        result.append(resultRow);
      }
      return result;
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   * The weights <code>w(-r), ..., w(r)</code> of the discrete Gaussian kernel of radius
   * <code>r</code> and standard deviation <code>sigma</code>:
   * <code>w(k) == Exp(-sigma^2)*BesselI(k, sigma^2)</code>, scaled to the sum <code>1</code>. This
   * is the kernel of T. Lindeberg's discrete scale space, which is used by default; the sampled
   * Gaussian <code>Exp(-k^2/(2*sigma^2))</code> is a different kernel.
   */
  public static double[] gaussianKernel(int radius, double sigma, EvalEngine engine) {
    double[] kernel = new double[2 * radius + 1];
    if (!(sigma > 0.0)) {
      kernel[radius] = 1.0;
      return kernel;
    }
    final double t = sigma * sigma;
    double sum = 0.0;
    for (int k = 0; k <= radius; k++) {
      double weight = engine.evalN(F.BesselI(F.ZZ(k), F.num(t))).evalf() * Math.exp(-t);
      kernel[radius + k] = weight;
      kernel[radius - k] = weight;
      sum += k == 0 ? weight : 2.0 * weight;
    }
    for (int i = 0; i < kernel.length; i++) {
      kernel[i] /= sum;
    }
    return kernel;
  }

  /**
   * Correlate every row and then every column of <code>data</code> with the symmetric
   * <code>kernel</code>. The values beyond the border are those of the border ("Fixed" padding).
   */
  public static double[][] separableFilter(double[][] data, double[] kernel) {
    final int rows = data.length;
    final int columns = rows == 0 ? 0 : data[0].length;
    final int radius = kernel.length / 2;
    double[][] horizontal = new double[rows][columns];
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        double sum = 0.0;
        for (int k = -radius; k <= radius; k++) {
          sum += kernel[k + radius] * data[i][Math.min(columns - 1, Math.max(0, j + k))];
        }
        horizontal[i][j] = sum;
      }
    }
    double[][] result = new double[rows][columns];
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        double sum = 0.0;
        for (int k = -radius; k <= radius; k++) {
          sum += kernel[k + radius] * horizontal[Math.min(rows - 1, Math.max(0, i + k))][j];
        }
        result[i][j] = sum;
      }
    }
    return result;
  }

  /**
   * <code>{radius, sigma}</code> of the specifications <code>r</code> (with
   * <code>sigma == r/2</code>) and <code>{r, sigma}</code>, or <code>null</code>.
   */
  private static double[] radiusAndSigma(IExpr spec) {
    if (spec.isList2()) {
      int radius = spec.first().toIntDefault();
      double sigma = spec.second().evalfNaN();
      return radius < 0 || Double.isNaN(sigma) || sigma < 0.0 ? null : new double[] {radius, sigma};
    }
    int radius = spec.toIntDefault();
    return radius < 0 ? null : new double[] {radius, radius / 2.0};
  }

  /**
   * <pre>
   * <code>GaussianMatrix(r)
   * </code>
   * </pre>
   *
   * <p>
   * returns the <code>(2*r+1) x (2*r+1)</code> matrix of the discrete Gaussian kernel with the
   * standard deviation <code>r/2</code>; <code>GaussianMatrix({r, sigma})</code> states the
   * standard deviation.
   * </p>
   */
  private static class GaussianMatrix extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      double[] spec = radiusAndSigma(ast.arg1());
      if (spec == null) {
        return F.NIL;
      }
      final double[] kernel = gaussianKernel((int) spec[0], spec[1], engine);
      return F.matrix((i, j) -> F.num(kernel[i] * kernel[j]), kernel.length, kernel.length);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   * <pre>
   * <code>GaussianFilter(data, r)
   * </code>
   * </pre>
   *
   * <p>
   * filters the list or matrix <code>data</code> with the discrete Gaussian kernel of radius
   * <code>r</code> and standard deviation <code>r/2</code>;
   * <code>GaussianFilter(data, {r, sigma})</code> states the standard deviation.
   * </p>
   */
  private static class GaussianFilter extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.arg1().isList()) {
        return F.NIL;
      }
      double[] spec = radiusAndSigma(ast.arg2());
      if (spec == null) {
        return F.NIL;
      }
      IAST list = (IAST) ast.arg1();
      IntList dims = LinearAlgebraUtil.dimensions(list);
      if (dims.size() != 1 && dims.size() != 2) {
        return F.NIL;
      }
      final boolean vector = dims.size() == 1;
      double[][] data = vector ? new double[][] {list.toDoubleVector()} : list.toDoubleMatrix();
      if (data == null || data[0] == null) {
        return F.NIL;
      }
      double[] kernel = gaussianKernel((int) spec[0], spec[1], engine);
      if (vector) {
        // filter the row only
        double[] row = data[0];
        final int n = row.length;
        final int radius = kernel.length / 2;
        double[] result = new double[n];
        for (int j = 0; j < n; j++) {
          double sum = 0.0;
          for (int k = -radius; k <= radius; k++) {
            sum += kernel[k + radius] * row[Math.min(n - 1, Math.max(0, j + k))];
          }
          result[j] = sum;
        }
        final double[] filteredRow = result;
        return F.mapRange(0, n, j -> F.num(filteredRow[j]));
      }
      final double[][] filtered = separableFilter(data, kernel);
      return F.matrix((i, j) -> F.num(filtered[i][j]), filtered.length, filtered[0].length);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  private static class MaxFilter extends MinFilter {
    @Override
    protected IExpr filterHead() {
      return S.Max;
    }
  }

  private static class MeanFilter extends MinFilter {
    @Override
    protected IExpr filterHead() {
      return S.Mean;
    }
  }

  private static class MedianFilter extends MinFilter {

    @Override
    protected boolean isValid(IExpr arg1) {
      return arg1.forAllLeaves(x -> x.isRealResult());
    }

    @Override
    protected IExpr filterHead() {
      return S.Median;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private FilterFunctions() {}
}
