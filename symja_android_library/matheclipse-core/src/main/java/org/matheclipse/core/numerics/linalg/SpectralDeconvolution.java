package org.matheclipse.core.numerics.linalg;

import org.matheclipse.core.eval.EvalEngine;

/**
 * Regularized deconvolution of a vector or a matrix by a kernel, solved in a spectral basis which
 * diagonalizes the convolution.
 *
 * <p>
 * The data <code>b</code> is modelled as <code>b = k * x</code> with the kernel <code>k</code>
 * centred on its middle element (an even dimension of the kernel gets a trailing zero first, so a
 * kernel <code>{a, b}</code> acts as <code>{a, b, 0}</code>). A kernel which equals its reflection
 * along every dimension is diagonalized by the orthonormal discrete cosine transform (reflexive
 * boundary conditions), every other kernel by the discrete Fourier transform (periodic boundary
 * conditions). With the eigenvalues <code>s</code> of the kernel, the data in the basis
 * <code>bh</code> and a regularization weight <code>d</code>, the solution in the basis is
 *
 * <pre>
 * xh = conj(s) * bh / (|s|^2 + lambda^2 * d)
 * </pre>
 *
 * <ul>
 * <li>{@link Method#DAMPED_LS}: damped least squares, <code>d</code> is the square of the
 * eigenvalue of the discrete Laplacian (second differences in every dimension), so constant data is
 * not damped,
 * <li>{@link Method#TIKHONOV}: <code>d = 1</code>,
 * <li>{@link Method#WIENER}: <code>d = 1</code> with the noise to signal ratio
 * <code>lambda^2</code> (<code>0.01</code> if none is given), always in the Fourier basis,
 * <li>{@link Method#TSVD}: the truncated spectral decomposition <code>xh = bh/|s|</code> for the
 * components with <code>|s| &gt; tau</code> and <code>0</code> for the others.
 * </ul>
 * Without a given <code>lambda</code>, it minimizes the generalized cross validation function
 *
 * <pre>
 * G(lambda) = sum(|(1 - phi) * bh|^2) / sum(1 - phi)^2,   phi = |s|^2 / (|s|^2 + lambda^2 * d)
 * </pre>
 *
 * on the interval <code>[Min(|s|), Max(|s|)]</code> with the bounded golden section and parabolic
 * interpolation search of Forsythe, Malcolm and Moler (tolerance <code>10^-4</code>). The truncation
 * <code>tau</code> of {@link Method#TSVD} without a given value is the <code>k</code>-th largest
 * <code>|s|</code> for the <code>k</code> in <code>1 ... n-1</code> which minimizes the residual
 * of the <code>n-k</code> smallest components divided by <code>(n-k)^2</code>; as the comparison is
 * strict, the <code>k</code>-th component itself is dropped too.
 *
 * <p>
 * Data with a dimension shorter than 3 is continued by one element on every side (repeating the
 * element at the edge) before, and cut back after.
 */
public final class SpectralDeconvolution {

  /** The regularization. */
  public enum Method {
    /** Damped least squares, regularized with the discrete Laplacian. */
    DAMPED_LS,
    /** Tikhonov regularization with the identity. */
    TIKHONOV,
    /** The Wiener filter with a constant noise to signal ratio. */
    WIENER,
    /** The truncated spectral decomposition. */
    TSVD
  }

  /** The noise to signal ratio of {@link Method#WIENER}. */
  public static final double WIENER_NOISE_TO_SIGNAL = 0.01;

  /** The tolerance of the search for the regularization parameter. */
  private static final double SEARCH_TOLERANCE = 1.0e-4;

  private SpectralDeconvolution() {}

  /**
   * Deconvolve the rectangular matrix <code>data</code> by the rectangular <code>kernel</code>,
   * which is not larger than <code>data</code> in any dimension. A vector is a matrix of one row.
   *
   * @param kernel the kernel
   * @param data the data
   * @param method the regularization
   * @param lambda the regularization parameter (the noise to signal ratio of {@link Method#WIENER},
   *        the truncation of {@link Method#TSVD}), or {@link Double#NaN} for the automatic choice
   * @param vector <code>true</code> if <code>data</code> and <code>kernel</code> are vectors (of one
   *        row), <code>false</code> if they are matrices
   * @return the deconvolved matrix of the dimensions of <code>data</code>
   */
  public static double[][] deconvolve(double[][] kernel, double[][] data, Method method,
      double lambda, boolean vector) {
    final int rows = data.length;
    final int columns = data[0].length;
    if (columns < 3 || (!vector && rows < 3)) {
      final int rowMargin = vector ? 0 : 1;
      double[][] padded = new double[rows + 2 * rowMargin][columns + 2];
      for (int r = 0; r < padded.length; r++) {
        int sourceRow = Math.min(Math.max(r - rowMargin, 0), rows - 1);
        for (int c = 0; c < padded[0].length; c++) {
          padded[r][c] = data[sourceRow][Math.min(Math.max(c - 1, 0), columns - 1)];
        }
      }
      double[][] solved = solve(kernel, padded, method, lambda);
      double[][] result = new double[rows][];
      for (int r = 0; r < rows; r++) {
        result[r] = java.util.Arrays.copyOfRange(solved[r + rowMargin], 1, columns + 1);
      }
      return result;
    }
    return solve(kernel, data, method, lambda);
  }

  private static double[][] solve(double[][] kernel, double[][] data, Method method,
      double lambda) {
    final double[][] k = oddKernel(kernel);
    final int rows = data.length;
    final int columns = data[0].length;
    final boolean cosine = method != Method.WIENER && isSymmetric(k);
    final double[][] eigenRe;
    final double[][] eigenIm;
    final double[][] dataRe;
    final double[][] dataIm;
    if (cosine) {
      eigenRe = cosineEigenvalues(k, rows, columns);
      eigenIm = null;
      dataRe = copy(data);
      dct2(dataRe, false);
      dataIm = null;
    } else {
      eigenRe = new double[rows][columns];
      eigenIm = new double[rows][columns];
      fourierEigenvalues(k, eigenRe, eigenIm);
      dataRe = copy(data);
      dataIm = new double[rows][columns];
      fft2(dataRe, dataIm, false);
    }
    final double[][] weight = new double[rows][columns];
    final double[][] eigenSquared = new double[rows][columns];
    final double[][] dataSquared = new double[rows][columns];
    double minEigen = Double.POSITIVE_INFINITY;
    double maxEigen = 0.0;
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < columns; c++) {
        double re = eigenRe[r][c];
        double im = cosine ? 0.0 : eigenIm[r][c];
        eigenSquared[r][c] = re * re + im * im;
        double magnitude = Math.sqrt(eigenSquared[r][c]);
        minEigen = Math.min(minEigen, magnitude);
        maxEigen = Math.max(maxEigen, magnitude);
        double dr = dataRe[r][c];
        double di = cosine ? 0.0 : dataIm[r][c];
        dataSquared[r][c] = dr * dr + di * di;
        if (method == Method.DAMPED_LS) {
          double laplacian = laplacian(r, rows, cosine) + laplacian(c, columns, cosine);
          weight[r][c] = laplacian * laplacian;
        } else {
          weight[r][c] = 1.0;
        }
      }
    }
    if (method == Method.TSVD) {
      double tau = Double.isNaN(lambda) ? truncation(eigenSquared, dataSquared) : lambda;
      for (int r = 0; r < rows; r++) {
        for (int c = 0; c < columns; c++) {
          double magnitude = Math.sqrt(eigenSquared[r][c]);
          double factor = magnitude > tau ? 1.0 / magnitude : 0.0;
          dataRe[r][c] *= factor;
          if (!cosine) {
            dataIm[r][c] *= factor;
          }
        }
      }
      if (cosine) {
        dct2(dataRe, true);
      } else {
        fft2(dataRe, dataIm, true);
      }
      return dataRe;
    }
    final double lambdaSquared;
    if (method == Method.WIENER) {
      lambdaSquared = Double.isNaN(lambda) ? WIENER_NOISE_TO_SIGNAL : lambda;
    } else if (Double.isNaN(lambda)) {
      double best = minimize(alpha -> gcv(alpha * alpha, eigenSquared, weight, dataSquared),
          minEigen, maxEigen, SEARCH_TOLERANCE);
      lambdaSquared = best * best;
    } else {
      lambdaSquared = lambda * lambda;
    }
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < columns; c++) {
        double denominator = eigenSquared[r][c] + lambdaSquared * weight[r][c];
        if (denominator == 0.0) {
          dataRe[r][c] = 0.0;
          if (!cosine) {
            dataIm[r][c] = 0.0;
          }
          continue;
        }
        if (cosine) {
          dataRe[r][c] = eigenRe[r][c] * dataRe[r][c] / denominator;
        } else {
          // conj(s) * bh
          double sr = eigenRe[r][c];
          double si = eigenIm[r][c];
          double br = dataRe[r][c];
          double bi = dataIm[r][c];
          dataRe[r][c] = (sr * br + si * bi) / denominator;
          dataIm[r][c] = (sr * bi - si * br) / denominator;
        }
      }
    }
    if (cosine) {
      dct2(dataRe, true);
    } else {
      fft2(dataRe, dataIm, true);
    }
    return dataRe;
  }

  /**
   * The generalized cross validation function at <code>lambdaSquared</code>. A component whose
   * eigenvalue and weight are both zero is not reproduced at all.
   */
  private static double gcv(double lambdaSquared, double[][] eigenSquared, double[][] weight,
      double[][] dataSquared) {
    double residual = 0.0;
    double trace = 0.0;
    for (int r = 0; r < eigenSquared.length; r++) {
      for (int c = 0; c < eigenSquared[r].length; c++) {
        double damping = lambdaSquared * weight[r][c];
        double denominator = eigenSquared[r][c] + damping;
        double complement = denominator == 0.0 ? 1.0 : damping / denominator;
        residual += complement * complement * dataSquared[r][c];
        trace += complement;
      }
    }
    return residual / (trace * trace);
  }

  /**
   * The truncation of {@link Method#TSVD} chosen by generalized cross validation: with the
   * components sorted by decreasing <code>|s|</code>, the <code>|s|</code> of the component
   * <code>k</code> in <code>1 ... n-1</code> which minimizes <code>(sum of |bh|^2 of the components
   * after k) / (n-k)^2</code>, the first one on a tie.
   */
  private static double truncation(double[][] eigenSquared, double[][] dataSquared) {
    final int rows = eigenSquared.length;
    final int columns = eigenSquared[0].length;
    final int n = rows * columns;
    if (n < 2) {
      return 0.0;
    }
    Integer[] order = new Integer[n];
    for (int i = 0; i < n; i++) {
      order[i] = i;
    }
    java.util.Arrays.sort(order, (i, j) -> Double.compare(eigenSquared[j / columns][j % columns],
        eigenSquared[i / columns][i % columns]));
    // the residual of the components after the first k
    double residual = 0.0;
    double[] tail = new double[n];
    for (int k = n - 1; k >= 1; k--) {
      residual += dataSquared[order[k] / columns][order[k] % columns];
      tail[k] = residual;
    }
    int best = 1;
    double bestValue = Double.POSITIVE_INFINITY;
    for (int k = 1; k < n; k++) {
      double value = tail[k] / ((double) (n - k) * (n - k));
      if (value < bestValue) {
        bestValue = value;
        best = k;
      }
    }
    int index = order[best - 1];
    return Math.sqrt(eigenSquared[index / columns][index % columns]);
  }

  /** The eigenvalue of the discrete second difference at the frequency <code>k</code>. */
  private static double laplacian(int k, int n, boolean cosine) {
    return 2.0 - 2.0 * Math.cos((cosine ? Math.PI : 2.0 * Math.PI) * k / n);
  }

  /**
   * The minimum of <code>f</code> on <code>[a, b]</code> by golden section search and parabolic
   * interpolation (Forsythe, Malcolm and Moler, <i>Computer Methods for Mathematical
   * Computations</i>, procedure <code>fmin</code>), stopping at the absolute tolerance
   * <code>tolerance</code>.
   */
  static double minimize(java.util.function.DoubleUnaryOperator f, double a, double b,
      double tolerance) {
    final double golden = 0.5 * (3.0 - Math.sqrt(5.0));
    final double sqrtEps = Math.sqrt(Math.ulp(1.0));
    double v = a + golden * (b - a);
    double w = v;
    double x = v;
    double d = 0.0;
    double e = 0.0;
    double fx = f.applyAsDouble(x);
    double fv = fx;
    double fw = fx;
    double xm = 0.5 * (a + b);
    double tol1 = sqrtEps * Math.abs(x) + tolerance / 3.0;
    double tol2 = 2.0 * tol1;
    EvalEngine engine = EvalEngine.get();
    int iterations = 0;
    while (Math.abs(x - xm) > (tol2 - 0.5 * (b - a)) && iterations++ < 1000) {
      engine.checkTimeLimit();
      boolean goldenStep = true;
      if (Math.abs(e) > tol1) {
        goldenStep = false;
        double r = (x - w) * (fx - fv);
        double q = (x - v) * (fx - fw);
        double p = (x - v) * q - (x - w) * r;
        q = 2.0 * (q - r);
        if (q > 0.0) {
          p = -p;
        }
        q = Math.abs(q);
        r = e;
        e = d;
        if (Math.abs(p) < Math.abs(0.5 * q * r) && p > q * (a - x) && p < q * (b - x)) {
          // a parabolic interpolation step
          d = p / q;
          double u = x + d;
          // f must not be evaluated too close to a or b
          if ((u - a) < tol2 || (b - u) < tol2) {
            d = xm >= x ? tol1 : -tol1;
          }
        } else {
          goldenStep = true;
        }
      }
      if (goldenStep) {
        e = x >= xm ? a - x : b - x;
        d = golden * e;
      }
      // f must not be evaluated too close to x
      double u = x + (d >= 0.0 ? 1.0 : -1.0) * Math.max(Math.abs(d), tol1);
      double fu = f.applyAsDouble(u);
      if (fu <= fx) {
        if (u >= x) {
          a = x;
        } else {
          b = x;
        }
        v = w;
        fv = fw;
        w = x;
        fw = fx;
        x = u;
        fx = fu;
      } else {
        if (u < x) {
          a = u;
        } else {
          b = u;
        }
        if (fu <= fw || w == x) {
          v = w;
          fv = fw;
          w = u;
          fw = fu;
        } else if (fu <= fv || v == x || v == w) {
          v = u;
          fv = fu;
        }
      }
      xm = 0.5 * (a + b);
      tol1 = sqrtEps * Math.abs(x) + tolerance / 3.0;
      tol2 = 2.0 * tol1;
    }
    return x;
  }

  /** The kernel with a trailing zero appended to each dimension of even length. */
  private static double[][] oddKernel(double[][] kernel) {
    int rows = kernel.length;
    int columns = kernel[0].length;
    int oddRows = rows | 1;
    int oddColumns = columns | 1;
    double[][] result = new double[oddRows][oddColumns];
    for (int r = 0; r < rows; r++) {
      System.arraycopy(kernel[r], 0, result[r], 0, columns);
    }
    return result;
  }

  /** Whether the kernel equals its reflection along each dimension. */
  private static boolean isSymmetric(double[][] kernel) {
    int rows = kernel.length;
    int columns = kernel[0].length;
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < columns; c++) {
        double value = kernel[r][c];
        if (value != kernel[rows - 1 - r][c] || value != kernel[r][columns - 1 - c]) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * The eigenvalues of the convolution with a symmetric kernel under reflexive boundary
   * conditions: <code>s(p,q) = Sum(k(i,j) cos(Pi p i/m) cos(Pi q j/n))</code> with the offsets
   * <code>i, j</code> from the centre of the kernel.
   */
  private static double[][] cosineEigenvalues(double[][] kernel, int rows, int columns) {
    int centerRow = kernel.length / 2;
    int centerColumn = kernel[0].length / 2;
    double[][] inner = new double[kernel.length][columns];
    for (int i = 0; i < kernel.length; i++) {
      for (int q = 0; q < columns; q++) {
        double sum = 0.0;
        for (int j = 0; j < kernel[0].length; j++) {
          sum += kernel[i][j] * cosine(q * (long) (j - centerColumn), 2 * columns);
        }
        inner[i][q] = sum;
      }
    }
    double[][] result = new double[rows][columns];
    for (int p = 0; p < rows; p++) {
      for (int q = 0; q < columns; q++) {
        double sum = 0.0;
        for (int i = 0; i < kernel.length; i++) {
          sum += inner[i][q] * cosine(p * (long) (i - centerRow), 2 * rows);
        }
        result[p][q] = sum;
      }
    }
    return result;
  }

  /**
   * The eigenvalues of the convolution with a kernel under periodic boundary conditions:
   * <code>s(p,q) = Sum(k(i,j) exp(-2 Pi I (p i/m + q j/n)))</code> with the offsets <code>i,
   * j</code> from the centre of the kernel.
   */
  private static void fourierEigenvalues(double[][] kernel, double[][] re, double[][] im) {
    int rows = re.length;
    int columns = re[0].length;
    int centerRow = kernel.length / 2;
    int centerColumn = kernel[0].length / 2;
    double[][] innerRe = new double[kernel.length][columns];
    double[][] innerIm = new double[kernel.length][columns];
    for (int i = 0; i < kernel.length; i++) {
      for (int q = 0; q < columns; q++) {
        double sumRe = 0.0;
        double sumIm = 0.0;
        for (int j = 0; j < kernel[0].length; j++) {
          long angle = q * (long) (j - centerColumn);
          sumRe += kernel[i][j] * cosine(angle, columns);
          sumIm -= kernel[i][j] * sine(angle, columns);
        }
        innerRe[i][q] = sumRe;
        innerIm[i][q] = sumIm;
      }
    }
    for (int p = 0; p < rows; p++) {
      for (int q = 0; q < columns; q++) {
        double sumRe = 0.0;
        double sumIm = 0.0;
        for (int i = 0; i < kernel.length; i++) {
          long angle = p * (long) (i - centerRow);
          double c = cosine(angle, rows);
          double s = -sine(angle, rows);
          sumRe += innerRe[i][q] * c - innerIm[i][q] * s;
          sumIm += innerRe[i][q] * s + innerIm[i][q] * c;
        }
        re[p][q] = sumRe;
        im[p][q] = sumIm;
      }
    }
  }

  /** <code>cos(2 Pi k/n)</code> with <code>k</code> reduced modulo <code>n</code>. */
  private static double cosine(long k, int n) {
    return Math.cos(2.0 * Math.PI * Math.floorMod(k, (long) n) / n);
  }

  /** <code>sin(2 Pi k/n)</code> with <code>k</code> reduced modulo <code>n</code>. */
  private static double sine(long k, int n) {
    return Math.sin(2.0 * Math.PI * Math.floorMod(k, (long) n) / n);
  }

  private static double[][] copy(double[][] matrix) {
    double[][] result = new double[matrix.length][];
    for (int r = 0; r < matrix.length; r++) {
      result[r] = matrix[r].clone();
    }
    return result;
  }

  /** The orthonormal two dimensional DCT-II (or its inverse, the DCT-III) in place. */
  private static void dct2(double[][] matrix, boolean inverse) {
    int rows = matrix.length;
    int columns = matrix[0].length;
    EvalEngine engine = EvalEngine.get();
    for (int r = 0; r < rows; r++) {
      engine.checkTimeLimit();
      dct(matrix[r], inverse);
    }
    double[] column = new double[rows];
    for (int c = 0; c < columns; c++) {
      engine.checkTimeLimit();
      for (int r = 0; r < rows; r++) {
        column[r] = matrix[r][c];
      }
      dct(column, inverse);
      for (int r = 0; r < rows; r++) {
        matrix[r][c] = column[r];
      }
    }
  }

  /**
   * The orthonormal DCT-II of a vector, or its inverse, in place, through a discrete Fourier
   * transform of twice the length.
   */
  private static void dct(double[] x, boolean inverse) {
    final int n = x.length;
    if (n == 1) {
      return;
    }
    final double first = Math.sqrt(1.0 / n);
    final double other = Math.sqrt(2.0 / n);
    double[] re = new double[2 * n];
    double[] im = new double[2 * n];
    if (!inverse) {
      // the even extension x(0), ..., x(n-1), x(n-1), ..., x(0)
      for (int j = 0; j < n; j++) {
        re[j] = x[j];
        re[2 * n - 1 - j] = x[j];
      }
      fft(re, im, false);
      for (int k = 0; k < n; k++) {
        double angle = -Math.PI * k / (2.0 * n);
        double value = 0.5 * (re[k] * Math.cos(angle) - im[k] * Math.sin(angle));
        x[k] = value * (k == 0 ? first : other);
      }
    } else {
      // x(j) = Re(Sum(c(k) X(k) exp(I Pi k/(2n)) exp(2 Pi I k j/(2n))))
      for (int k = 0; k < n; k++) {
        double scaled = x[k] * (k == 0 ? first : other);
        double angle = Math.PI * k / (2.0 * n);
        re[k] = scaled * Math.cos(angle);
        im[k] = scaled * Math.sin(angle);
      }
      fft(re, im, true);
      for (int j = 0; j < n; j++) {
        x[j] = re[j] * (2 * n);
      }
    }
  }

  /**
   * The two dimensional discrete Fourier transform in place; the inverse includes the factor
   * <code>1/(m n)</code>.
   */
  private static void fft2(double[][] re, double[][] im, boolean inverse) {
    int rows = re.length;
    int columns = re[0].length;
    EvalEngine engine = EvalEngine.get();
    for (int r = 0; r < rows; r++) {
      engine.checkTimeLimit();
      fft(re[r], im[r], inverse);
    }
    double[] columnRe = new double[rows];
    double[] columnIm = new double[rows];
    for (int c = 0; c < columns; c++) {
      engine.checkTimeLimit();
      for (int r = 0; r < rows; r++) {
        columnRe[r] = re[r][c];
        columnIm[r] = im[r][c];
      }
      fft(columnRe, columnIm, inverse);
      for (int r = 0; r < rows; r++) {
        re[r][c] = columnRe[r];
        im[r][c] = columnIm[r];
      }
    }
  }

  /**
   * The discrete Fourier transform <code>X(k) = Sum(x(j) exp(-2 Pi I j k/n))</code> of any length
   * in place, a radix 2 transform for a power of two and Bluestein's chirp transform otherwise. The
   * inverse uses <code>exp(+2 Pi I j k/n)</code> and divides by <code>n</code>.
   */
  static void fft(double[] re, double[] im, boolean inverse) {
    final int n = re.length;
    if (n <= 1) {
      return;
    }
    if (Integer.bitCount(n) == 1) {
      radix2(re, im, inverse);
    } else {
      bluestein(re, im, inverse);
    }
    if (inverse) {
      for (int i = 0; i < n; i++) {
        re[i] /= n;
        im[i] /= n;
      }
    }
  }

  /** The unnormalized radix 2 transform of a power of two length. */
  private static void radix2(double[] re, double[] im, boolean inverse) {
    final int n = re.length;
    for (int i = 1, j = 0; i < n; i++) {
      int bit = n >> 1;
      for (; (j & bit) != 0; bit >>= 1) {
        j ^= bit;
      }
      j ^= bit;
      if (i < j) {
        double t = re[i];
        re[i] = re[j];
        re[j] = t;
        t = im[i];
        im[i] = im[j];
        im[j] = t;
      }
    }
    final double sign = inverse ? 1.0 : -1.0;
    for (int length = 2; length <= n; length <<= 1) {
      int half = length >> 1;
      for (int k = 0; k < half; k++) {
        double angle = sign * 2.0 * Math.PI * k / length;
        double wr = Math.cos(angle);
        double wi = Math.sin(angle);
        for (int start = 0; start < n; start += length) {
          int a = start + k;
          int b = a + half;
          double tr = re[b] * wr - im[b] * wi;
          double ti = re[b] * wi + im[b] * wr;
          re[b] = re[a] - tr;
          im[b] = im[a] - ti;
          re[a] += tr;
          im[a] += ti;
        }
      }
    }
  }

  /** The unnormalized transform of any length as a convolution of power of two length. */
  private static void bluestein(double[] re, double[] im, boolean inverse) {
    final int n = re.length;
    int m = Integer.highestOneBit(2 * n - 1);
    if (m < 2 * n - 1) {
      m <<= 1;
    }
    final double sign = inverse ? 1.0 : -1.0;
    // the chirp exp(sign I Pi k^2/n), with k^2 reduced modulo 2n
    double[] chirpRe = new double[n];
    double[] chirpIm = new double[n];
    for (int k = 0; k < n; k++) {
      long square = ((long) k * k) % (2L * n);
      double angle = sign * Math.PI * square / n;
      chirpRe[k] = Math.cos(angle);
      chirpIm[k] = Math.sin(angle);
    }
    double[] aRe = new double[m];
    double[] aIm = new double[m];
    for (int k = 0; k < n; k++) {
      aRe[k] = re[k] * chirpRe[k] - im[k] * chirpIm[k];
      aIm[k] = re[k] * chirpIm[k] + im[k] * chirpRe[k];
    }
    double[] bRe = new double[m];
    double[] bIm = new double[m];
    bRe[0] = chirpRe[0];
    bIm[0] = -chirpIm[0];
    for (int k = 1; k < n; k++) {
      bRe[k] = bRe[m - k] = chirpRe[k];
      bIm[k] = bIm[m - k] = -chirpIm[k];
    }
    radix2(aRe, aIm, false);
    radix2(bRe, bIm, false);
    for (int i = 0; i < m; i++) {
      double r = aRe[i] * bRe[i] - aIm[i] * bIm[i];
      aIm[i] = aRe[i] * bIm[i] + aIm[i] * bRe[i];
      aRe[i] = r;
    }
    radix2(aRe, aIm, true);
    for (int k = 0; k < n; k++) {
      double r = aRe[k] / m;
      double i = aIm[k] / m;
      re[k] = r * chirpRe[k] - i * chirpIm[k];
      im[k] = r * chirpIm[k] + i * chirpRe[k];
    }
  }
}
