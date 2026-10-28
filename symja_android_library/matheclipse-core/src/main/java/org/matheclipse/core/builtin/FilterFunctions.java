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
      S.GeometricMeanFilter.setEvaluator(new GeometricMeanFilter());
      S.LaplacianFilter.setEvaluator(new LaplacianFilter());
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
   * The rows of the numeric vector (as a single row) or matrix <code>list</code>, or
   * <code>null</code> if <code>list</code> is not numeric.
   */
  private static double[][] numericRows(IAST list, boolean vector) {
    double[][] data = vector ? new double[][] {list.toDoubleVector()} : list.toDoubleMatrix();
    if (data == null || data[0] == null) {
      return null;
    }
    return data;
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
   *
   * <p>
   * <code>GaussianMatrix({{r1, r2, ...}})</code> and <code>GaussianMatrix({{r1, r2, ...},
   * sigma})</code> return the array of the dimensions <code>2*r1+1, 2*r2+1, ...</code> which is
   * the product of the one dimensional kernels of the radii; <code>GaussianMatrix({{r}})</code> is
   * the vector of the one dimensional kernel. The standard deviation is a number for all
   * dimensions, a list of one for each, or <code>ri/2</code> by default.
   * </p>
   *
   * <p>
   * <code>GaussianMatrix(r, {n1, n2})</code> is the matrix of the discrete derivatives of the
   * orders <code>n1</code> along the rows and <code>n2</code> along the columns;
   * <code>GaussianMatrix(r, n)</code> means <code>{n, n}</code>.
   * </p>
   */
  private static class GaussianMatrix extends AbstractEvaluator {

    /**
     * The kernel array of <code>{{r1, r2, ...}}</code> or <code>{{r1, r2, ...}, sigma}</code>.
     *
     * @return {@link F#NIL} if the specification is not valid
     */
    private static IExpr productKernel(IExpr spec, EvalEngine engine) {
      IAST radii = (IAST) spec.first();
      final int n = radii.argSize();
      if (n == 0) {
        return F.NIL;
      }
      IExpr sigmas = spec.isList2() ? spec.second() : F.NIL;
      if (sigmas.isList() && sigmas.argSize() != n) {
        return F.NIL;
      }
      final double[][] kernels = new double[n][];
      for (int i = 0; i < n; i++) {
        int radius = radii.get(i + 1).toIntDefault();
        if (radius < 0) {
          return F.NIL;
        }
        double sigma = sigmas.isNIL() ? radius / 2.0
            : (sigmas.isList() ? sigmas.getAt(i + 1) : sigmas).evalfNaN();
        if (Double.isNaN(sigma) || sigma < 0.0) {
          return F.NIL;
        }
        kernels[i] = gaussianKernel(radius, sigma, engine);
      }
      return productArray(kernels, 0, 1.0);
    }

    /** The nested list of the products <code>factor * kernels[d][i_d] * ...</code>. */
    private static IExpr productArray(double[][] kernels, int dimension, double factor) {
      final double[] kernel = kernels[dimension];
      if (dimension == kernels.length - 1) {
        return F.mapRange(0, kernel.length, i -> F.num(factor * kernel[i]));
      }
      return F.mapRange(0, kernel.length,
          i -> productArray(kernels, dimension + 1, factor * kernel[i]));
    }

    /**
     * The one dimensional factor of the derivative order <code>n</code>: the normalized kernel for
     * <code>n == 0</code>, otherwise the central difference applied <code>n</code> times to the
     * untruncated discrete Gaussian <code>T(k) == Exp(-t)*BesselI(k, t)</code>,
     * <code>t == sigma^2</code>, i.e. <code>Sum((-1)^i*Binomial(n,i)*T(k+n-2*i), {i,0,n})/2^n</code>.
     * The factor is even or odd with <code>n</code> and computed for <code>k &gt;= 0</code> only, so
     * that symmetric entries agree exactly.
     */
    private static double[] derivativeFactor(int radius, double sigma, int n, EvalEngine engine) {
      if (n == 0) {
        return gaussianKernel(radius, sigma, engine);
      }
      final double t = sigma * sigma;
      final double[] discrete = new double[radius + n + 1];
      for (int k = 0; k < discrete.length; k++) {
        discrete[k] = engine.evalN(F.BesselI(F.ZZ(k), F.num(t))).evalf() * Math.exp(-t);
      }
      final double scale = Math.pow(2.0, n);
      final double mirror = n % 2 == 0 ? 1.0 : -1.0;
      double[] factor = new double[2 * radius + 1];
      for (int k = 0; k <= radius; k++) {
        double sum = 0.0;
        if (k != 0 || n % 2 == 0) {
          double binomial = 1.0;
          for (int i = 0; i <= n; i++) {
            sum += (i % 2 == 0 ? binomial : -binomial) * discrete[Math.abs(k + n - 2 * i)];
            binomial = binomial * (n - i) / (i + 1);
          }
        }
        factor[radius + k] = sum / scale;
        factor[radius - k] = mirror * sum / scale;
      }
      return factor;
    }

    /**
     * The derivative matrix of the orders <code>n1, n2</code>: the outer product of the factors,
     * less its mean if both orders are even, scaled so that
     * <code>Sum(m[i,j]*i^n1*j^n2) == (-1)^(n1+n2)*n1!*n2!</code> over the offsets from the centre.
     * The derivatives of a constant vanish and those of the matching monomial are exact.
     */
    private static IExpr derivativeMatrix(int radius, double sigma, int n1, int n2,
        EvalEngine engine) {
      final double[] rows = derivativeFactor(radius, sigma, n1, engine);
      final double[] columns = derivativeFactor(radius, sigma, n2, engine);
      final int size = 2 * radius + 1;
      final double[][] m = new double[size][size];
      double sum = 0.0;
      for (int i = 0; i < size; i++) {
        for (int j = 0; j < size; j++) {
          m[i][j] = rows[i] * columns[j];
          sum += m[i][j];
        }
      }
      if (n1 + n2 > 0) {
        final double mean = n1 % 2 == 0 && n2 % 2 == 0 ? sum / (size * size) : 0.0;
        double moment = 0.0;
        for (int i = 0; i < size; i++) {
          for (int j = 0; j < size; j++) {
            m[i][j] -= mean;
            moment += m[i][j] * Math.pow(i - radius, n1) * Math.pow(j - radius, n2);
          }
        }
        double scale = ((n1 + n2) % 2 == 0 ? 1.0 : -1.0) * factorial(n1) * factorial(n2) / moment;
        for (int i = 0; i < size; i++) {
          for (int j = 0; j < size; j++) {
            m[i][j] *= scale;
          }
        }
      }
      return F.matrix((i, j) -> F.num(m[i][j]), size, size);
    }

    private static double factorial(int n) {
      double result = 1.0;
      for (int i = 2; i <= n; i++) {
        result *= i;
      }
      return result;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      int n1 = 0;
      int n2 = 0;
      if (ast.isAST2()) {
        IExpr orders = ast.arg2();
        if (orders.isList() ? !((IAST) orders).forAll(n -> n.toIntDefault() >= 0)
            : orders.toIntDefault() < 0) {
          // Single or list of non-negative machine-sized integers expected at position `1` of
          // `2`.
          return Errors.printMessage(S.GaussianMatrix, "ilsmn", F.list(F.C2, ast), engine);
        }
        if (orders.isList()) {
          if (orders.argSize() != 2) {
            // the orders of an array of another rank
            return F.NIL;
          }
          n1 = orders.first().toIntDefault();
          n2 = orders.second().toIntDefault();
        } else {
          n1 = orders.toIntDefault();
          n2 = n1;
        }
      }
      if (arg1.isList() && (arg1.isList1() || arg1.isList2()) && arg1.first().isList()) {
        return ast.isAST2() ? F.NIL : productKernel(arg1, engine);
      }
      double[] spec = radiusAndSigma(ast.arg1());
      if (spec == null) {
        return F.NIL;
      }
      if (n1 > 0 || n2 > 0) {
        return derivativeMatrix((int) spec[0], spec[1], n1, n2, engine);
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
      return ARGS_1_2;
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
      // CPD-OFF filter preamble; the caller needs the rank flag
      }
      IAST list = (IAST) ast.arg1();
      IntList dims = LinearAlgebraUtil.dimensions(list);
      if (dims.size() != 1 && dims.size() != 2) {
        return F.NIL;
      }
      final boolean vector = dims.size() == 1;
      double[][] data = numericRows(list, vector);
      if (data == null) {
      // CPD-ON
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

  /**
   * The radius of a <code>LaplacianFilter</code> range <code>r</code> or <code>{r, sigma}</code>,
   * where the standard deviation must be at least <code>r/2</code> and doesn't change the kernel.
   *
   * @return <code>-1</code> if the range isn't valid
   */
  public static int laplacianRadius(IExpr spec) {
    int radius = spec.isList2() ? spec.first().toIntDefault() : spec.toIntDefault();
    if (radius < 1) {
      return -1;
    }
    if (spec.isList2()) {
      double sigma = spec.second().evalfNaN();
      if (!(sigma >= radius / 2.0)) {
        return -1;
      }
    }
    return radius;
  }

  /**
   * The Laplacian of <code>data</code> (one row for a list) with the kernel of radius
   * <code>r</code>: along each axis the second derivative of the band limited interpolation, the
   * taps <code>2*(-1)^(k+1)/k^2</code> beside a centre of <code>-2*Pi^2/3</code>, and the whole
   * <code>(2r+1)</code> window (block for a matrix) shifted by one constant so that the kernel sums
   * to zero. The values beyond the border are those of the border ("Fixed" padding).
   */
  public static double[][] laplacianFilter(double[][] data, int radius, boolean vector) {
    final int rows = data.length;
    final int columns = data[0].length;
    final int width = 2 * radius + 1;
    double[] axis = new double[radius + 1];
    for (int k = 1; k <= radius; k++) {
      axis[k] = (k % 2 != 0 ? 2.0 : -2.0) / (k * k);
    }
    final double centre = -2.0 * Math.PI * Math.PI / 3.0;
    double coreSum = centre;
    for (int k = 1; k <= radius; k++) {
      coreSum += (vector ? 2.0 : 4.0) * axis[k];
    }
    final double shift = -coreSum / (vector ? width : (double) width * width);
    double[][] result = new double[rows][columns];
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        double sum = centre * data[i][j];
        double window = 0.0;
        for (int k = -radius; k <= radius; k++) {
          int column = Math.min(columns - 1, Math.max(0, j + k));
          if (k != 0) {
            sum += axis[Math.abs(k)] * data[i][column];
            if (!vector) {
              sum += axis[Math.abs(k)] * data[Math.min(rows - 1, Math.max(0, i + k))][j];
            }
          }
          if (vector) {
            window += data[i][column];
          } else {
            for (int l = -radius; l <= radius; l++) {
              window += data[Math.min(rows - 1, Math.max(0, i + l))][column];
            }
          }
        }
        result[i][j] = sum + shift * window;
      }
    }
    return result;
  }

  /**
   * <pre>
   * <code>LaplacianFilter(data, r)
   * </code>
   * </pre>
   *
   * <p>
   * the Laplacian of the list or matrix <code>data</code> with a kernel of radius <code>r</code>.
   * </p>
   */
  private static class LaplacianFilter extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.arg1().isList()) {
        return F.NIL;
      }
      int radius = laplacianRadius(ast.arg2());
      if (radius < 0) {
        // `1` is not a valid neighborhood range specification.
        return Errors.printMessage(S.LaplacianFilter, "bdrad", F.list(ast.arg2()), engine);
      // CPD-OFF filter preamble; the caller needs the rank flag
      }
      IAST list = (IAST) ast.arg1();
      IntList dims = LinearAlgebraUtil.dimensions(list);
      if (dims.size() != 1 && dims.size() != 2) {
        return F.NIL;
      }
      final boolean vector = dims.size() == 1;
      double[][] data = numericRows(list, vector);
      if (data == null || data[0].length == 0) {
      // CPD-ON
        return F.NIL;
      }
      final double[][] filtered = laplacianFilter(data, radius, vector);
      if (vector) {
        return F.mapRange(0, filtered[0].length, j -> F.num(filtered[0][j]));
      }
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

  /**
   * <code>GeometricMeanFilter(list, r)</code> - the geometric mean of the elements up to
   * <code>r</code> places away, of those which are in the list.
   */
  private static class GeometricMeanFilter extends MinFilter {
    @Override
    protected IExpr filterHead() {
      return S.GeometricMean;
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
