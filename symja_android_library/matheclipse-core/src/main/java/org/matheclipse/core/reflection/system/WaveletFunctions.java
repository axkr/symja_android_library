package org.matheclipse.core.reflection.system;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.hipparchus.analysis.solvers.LaguerreSolver;
import org.hipparchus.complex.Complex;
import org.hipparchus.exception.MathRuntimeException;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * The scaling function <code>WaveletPhi(wave, x)</code> and the wavelet function
 * <code>WaveletPsi(wave, x)</code> of the orthogonal wavelet families with compact support:
 * <code>HaarWavelet()</code>, <code>DaubechiesWavelet(n)</code> (default <code>n = 2</code>) and
 * <code>SymletWavelet(n)</code> (default <code>n = 4</code>).
 *
 * <p>
 * The low pass filter <code>h</code> of length <code>2n</code> (normalized to
 * <code>Total(h) == Sqrt(2)</code>) comes from the spectral factorization of the Daubechies
 * polynomial: the minimum phase factor for Daubechies wavelets, the factor with the most nearly
 * linear phase for symlets, taken in reverse order. The scaling function solves the refinement
 * equation <code>phi(x) == Sqrt(2)*Sum(h_k*phi(2*x-k))</code> on the support
 * <code>[0, 2n-1]</code>, and
 * the wavelet is <code>psi(x) == Sqrt(2)*Sum((-1)^k*h_(1-k)*phi(2*x-k))</code> on the support
 * <code>[1-n, n]</code>.
 *
 * <p>
 * A machine number <code>x</code> is a dyadic rational, so the values are computed exactly up to
 * rounding: the integer values <code>phi(0), phi(1), ...</code> are the eigenvector of the
 * refinement matrix for the eigenvalue <code>1</code>, and each binary digit of the fractional
 * part of <code>x</code> applies one of the two refinement matrices to that vector. This also
 * works for the rough scaling functions of short filters, which no interpolation on a grid
 * resolves. An exact <code>x</code> gives an exact result for the Haar wavelet and outside of the
 * support; other exact or symbolic arguments leave the call unevaluated.
 */
public class WaveletFunctions {

  /** The largest order of a wavelet family, beyond which the root finding loses its accuracy. */
  private static final int MAX_ORDER = 20;

  /** The filters and integer values of the scaling functions computed so far. */
  private static final Map<String, Scaling> CACHE = new ConcurrentHashMap<>();

  /** The low pass filter of a wavelet and its scaling function at the integers. */
  private static final class Scaling {
    /**
     * the refinement coefficients <code>c_k == Sqrt(2)*h_k</code> of the low pass filter, with
     * <code>Total(c) == 2</code>
     */
    final double[] c;
    /** <code>phi(0), phi(1), ..., phi(c.length-2)</code> */
    final double[] integerValues;

    Scaling(double[] c) {
      this.c = c;
      this.integerValues = integerValues(c);
    }

    /** The order <code>n</code> of the wavelet, half the filter length. */
    int order() {
      return c.length / 2;
    }

    /**
     * <code>{phi(y), phi(y+1), ..., phi(y+L-2)}</code> for <code>0 <= y < 1</code>, where
     * <code>L</code> is the filter length.
     */
    double[] values(double y) {
      int[] digits = new int[64];
      int count = 0;
      while (y > 0.0 && count < digits.length) {
        y *= 2.0;
        int digit = y >= 1.0 ? 1 : 0;
        y -= digit;
        digits[count++] = digit;
      }
      double[] v = integerValues.clone();
      for (int i = count - 1; i >= 0; i--) {
        v = refine(c, digits[i], v);
      }
      return v;
    }

    double phi(double x) {
      double n = Math.floor(x);
      if (n < 0 || n > c.length - 2) {
        return 0.0;
      }
      return values(x - n)[(int) n];
    }

    double psi(double x) {
      double t = 2.0 * x;
      double n = Math.floor(t);
      int length = c.length;
      if (n < 2 - length || n > length - 1) {
        return 0.0;
      }
      double[] v = values(t - n);
      int shift = (int) n;
      double sum = 0.0;
      for (int j = 0; j <= length - 2; j++) {
        // phi(t - k) with k = shift - j, weighted by (-1)^k * c_(1-k)
        int k = shift - j;
        int m = 1 - k;
        if (m >= 0 && m < length) {
          sum += ((k & 1) == 0 ? c[m] : -c[m]) * v[j];
        }
      }
      return sum;
    }
  }

  /**
   * <code>v(y) == T_d . v(2*y-d)</code> with <code>(T_d)_(k,j) == c_(2*k+d-j)</code>, the
   * refinement equation on the vector of the shifted values.
   */
  private static double[] refine(double[] c, int digit, double[] v) {
    int size = v.length;
    double[] result = new double[size];
    for (int k = 0; k < size; k++) {
      double sum = 0.0;
      for (int j = 0; j < size; j++) {
        int m = 2 * k + digit - j;
        if (m >= 0 && m < c.length) {
          sum += c[m] * v[j];
        }
      }
      result[k] = sum;
    }
    return result;
  }

  /**
   * The scaling function at the integers <code>0, 1, ..., L-2</code>: the eigenvector of
   * <code>T_0</code> for the eigenvalue <code>1</code>, normalized to the sum <code>1</code>.
   */
  private static double[] integerValues(double[] c) {
    int size = c.length - 1;
    double[][] a = new double[size][size + 1];
    for (int k = 0; k < size; k++) {
      for (int j = 0; j < size; j++) {
        int m = 2 * k - j;
        a[k][j] = (m >= 0 && m < c.length ? c[m] : 0.0) - (k == j ? 1.0 : 0.0);
      }
    }
    // the equations are dependent: one of them is replaced by the normalization
    for (int j = 0; j < size; j++) {
      a[size - 1][j] = 1.0;
    }
    a[size - 1][size] = 1.0;
    return solve(a, size);
  }

  /** Gaussian elimination with partial pivoting of the augmented matrix <code>a</code>. */
  private static double[] solve(double[][] a, int size) {
    for (int col = 0; col < size; col++) {
      int pivot = col;
      for (int row = col + 1; row < size; row++) {
        if (Math.abs(a[row][col]) > Math.abs(a[pivot][col])) {
          pivot = row;
        }
      }
      double[] swap = a[col];
      a[col] = a[pivot];
      a[pivot] = swap;
      if (a[col][col] == 0.0) {
        return null;
      }
      for (int row = col + 1; row < size; row++) {
        double factor = a[row][col] / a[col][col];
        if (factor != 0.0) {
          for (int j = col; j <= size; j++) {
            a[row][j] -= factor * a[col][j];
          }
        }
      }
    }
    double[] x = new double[size];
    for (int row = size - 1; row >= 0; row--) {
      double sum = a[row][size];
      for (int j = row + 1; j < size; j++) {
        sum -= a[row][j] * x[j];
      }
      x[row] = sum / a[row][row];
    }
    return x;
  }

  /**
   * The roots <code>z</code> inside the unit circle of the factors of the Daubechies polynomial
   * <code>Sum(Binomial(n-1+k, k)*y^k, {k, 0, n-1})</code> with <code>y == (2-z-1/z)/4</code>,
   * grouped so that a complex conjugate pair forms one group.
   */
  private static Complex[][] rootGroups(int n) {
    if (n <= 1) {
      return new Complex[0][];
    }
    double[] coefficients = new double[n];
    double binomial = 1.0;
    for (int k = 0; k < n; k++) {
      coefficients[k] = binomial;
      // Binomial(n+k, k+1) from Binomial(n-1+k, k)
      binomial = binomial * (n + k) / (k + 1);
    }
    Complex[] yRoots;
    try {
      yRoots = new LaguerreSolver(1.0e-15).solveAllComplex(coefficients, 100_000, 1.0);
    } catch (MathRuntimeException mre) {
      return null;
    }
    Complex[] zRoots = new Complex[yRoots.length];
    for (int i = 0; i < yRoots.length; i++) {
      // z^2 - (2-4*y)*z + 1 == 0, whose roots are z and 1/z
      Complex b = new Complex(2.0, 0.0).subtract(yRoots[i].multiply(4.0));
      Complex z = b.add(b.multiply(b).subtract(4.0).sqrt()).divide(2.0);
      if (z.norm() > 1.0) {
        z = z.reciprocal();
      }
      zRoots[i] = z;
    }
    boolean[] used = new boolean[zRoots.length];
    java.util.List<Complex[]> groups = new java.util.ArrayList<>();
    // a real root comes first: it anchors the orientation of a symlet
    for (int pass = 0; pass < 2; pass++) {
      for (int i = 0; i < zRoots.length; i++) {
        if (used[i]) {
          continue;
        }
        boolean real = Math.abs(zRoots[i].getImaginary()) < 1.0e-9;
        if (pass == 0 && real) {
          used[i] = true;
          groups.add(new Complex[] {new Complex(zRoots[i].getReal(), 0.0)});
        } else if (pass == 1 && !real) {
          used[i] = true;
          int conjugate = -1;
          for (int j = 0; j < zRoots.length; j++) {
            if (!used[j] && (conjugate < 0 || zRoots[j].subtract(zRoots[i].conjugate())
                .norm() < zRoots[conjugate].subtract(zRoots[i].conjugate()).norm())) {
              conjugate = j;
            }
          }
          if (conjugate < 0) {
            return null;
          }
          used[conjugate] = true;
          groups.add(new Complex[] {zRoots[i], zRoots[i].conjugate()});
        }
      }
    }
    if (groups.isEmpty() || Math.abs(groups.get(0)[0].getImaginary()) >= 1.0e-9) {
      // without a real root the group of the largest y root anchors the orientation
      int largest = 0;
      for (int i = 1; i < groups.size(); i++) {
        if (yOf(groups.get(i)[0]).norm() > yOf(groups.get(largest)[0]).norm()) {
          largest = i;
        }
      }
      Complex[] anchor = groups.remove(largest);
      groups.add(0, anchor);
    }
    return groups.toArray(new Complex[0][]);
  }

  /** <code>y == (2-z-1/z)/4</code> */
  private static Complex yOf(Complex z) {
    return new Complex(2.0, 0.0).subtract(z).subtract(z.reciprocal()).divide(4.0);
  }

  /**
   * The refinement coefficients of <code>(1+z)^n*Product(z-r)</code>, where a group of the roots
   * <code>r</code> is taken inside the unit circle if its bit in <code>outside</code> is not set,
   * normalized to <code>Total(c) == 2</code>; <code>c_0</code> is the highest coefficient.
   */
  private static double[] filter(int n, Complex[][] groups, int outside) {
    double[] re = {1.0};
    double[] im = {0.0};
    for (int i = 0; i < n; i++) {
      double[][] p = multiply(re, im, 1.0, 0.0);
      re = p[0];
      im = p[1];
    }
    for (int g = 0; g < groups.length; g++) {
      boolean reciprocal = ((outside >> g) & 1) != 0;
      for (Complex root : groups[g]) {
        Complex r = reciprocal ? root.reciprocal() : root;
        // multiply by (z - r)
        double[][] p = multiply(re, im, -r.getReal(), -r.getImaginary());
        re = p[0];
        im = p[1];
      }
    }
    double sum = 0.0;
    for (double c : re) {
      sum += c;
    }
    double[] h = new double[re.length];
    for (int i = 0; i < re.length; i++) {
      h[i] = re[re.length - 1 - i] * 2.0 / sum;
    }
    return h;
  }

  /** <code>p(z)*(z+c)</code> for the coefficients of <code>p</code> in ascending order. */
  private static double[][] multiply(double[] re, double[] im, double cRe, double cIm) {
    double[] rRe = new double[re.length + 1];
    double[] rIm = new double[re.length + 1];
    for (int i = 0; i < re.length; i++) {
      rRe[i + 1] += re[i];
      rIm[i + 1] += im[i];
      rRe[i] += re[i] * cRe - im[i] * cIm;
      rIm[i] += re[i] * cIm + im[i] * cRe;
    }
    return new double[][] {rRe, rIm};
  }

  /**
   * The deviation of the phase of <code>H(w) == Sum(h_k*Exp(-I*k*w))</code> from a linear
   * function: the sum of the squared residuals of the best fit <code>-tau*w</code> on
   * <code>0 < w < Pi</code>. The fit goes through the origin, where the phase of
   * <code>H(0) == Sqrt(2)</code> is <code>0</code>.
   */
  private static double phaseNonlinearity(double[] h) {
    int m = 256;
    double[] w = new double[m - 1];
    double[] phase = new double[m - 1];
    double previous = 0.0;
    for (int i = 1; i < m; i++) {
      double omega = Math.PI * i / m;
      double re = 0.0;
      double im = 0.0;
      for (int k = 0; k < h.length; k++) {
        re += h[k] * Math.cos(k * omega);
        im -= h[k] * Math.sin(k * omega);
      }
      double p = Math.atan2(im, re);
      if (i > 1) {
        while (p - previous > Math.PI) {
          p -= 2.0 * Math.PI;
        }
        while (p - previous < -Math.PI) {
          p += 2.0 * Math.PI;
        }
      }
      w[i - 1] = omega;
      phase[i - 1] = p;
      previous = p;
    }
    int count = w.length;
    double sww = 0.0, swp = 0.0;
    for (int i = 0; i < count; i++) {
      sww += w[i] * w[i];
      swp += w[i] * phase[i];
    }
    double slope = swp / sww;
    double sum = 0.0;
    for (int i = 0; i < count; i++) {
      double r = phase[i] - slope * w[i];
      sum += r * r;
    }
    return sum;
  }

  private static Scaling daubechies(int n) {
    return CACHE.computeIfAbsent("D" + n, key -> {
      if (n == 1) {
        return new Scaling(new double[] {1.0, 1.0});
      }
      Complex[][] groups = rootGroups(n);
      return groups == null ? null : new Scaling(filter(n, groups, 0));
    });
  }

  private static Scaling symlet(int n) {
    if (n <= 1) {
      return daubechies(1);
    }
    return CACHE.computeIfAbsent("S" + n, key -> {
      Complex[][] groups = rootGroups(n);
      if (groups == null) {
        return null;
      }
      // the first group stays inside the unit circle, which picks one of two mirror images
      double best = Double.MAX_VALUE;
      double[] bestFilter = null;
      for (int outside = 0; outside < (1 << groups.length); outside += 2) {
        double[] h = filter(n, groups, outside);
        double nonlinearity = phaseNonlinearity(h);
        if (nonlinearity < best - 1.0e-9) {
          best = nonlinearity;
          bestFilter = h;
        }
      }
      if (bestFilter == null) {
        return null;
      }
      // the scaling function of a symlet is built from the filter in reverse order
      double[] reversed = new double[bestFilter.length];
      for (int i = 0; i < reversed.length; i++) {
        reversed[i] = bestFilter[bestFilter.length - 1 - i];
      }
      return new Scaling(reversed);
    });
  }

  /**
   * The scaling data of <code>HaarWavelet()</code>, <code>DaubechiesWavelet(n)</code> or
   * <code>SymletWavelet(n)</code>.
   *
   * @return <code>null</code> if <code>wave</code> is no such wavelet
   */
  private static Scaling scaling(IExpr wave) {
    if (!wave.isAST() || wave.argSize() > 1) {
      return null;
    }
    if (wave.isAST(S.HaarWavelet)) {
      return wave.argSize() == 0 ? daubechies(1) : null;
    }
    boolean symlet = wave.isAST(S.SymletWavelet);
    if (!symlet && !wave.isAST(S.DaubechiesWavelet)) {
      return null;
    }
    int n = symlet ? 4 : 2;
    if (wave.argSize() == 1) {
      n = wave.first().toIntDefault();
      if (n < 1 || n > MAX_ORDER) {
        return null;
      }
    }
    return symlet ? symlet(n) : daubechies(n);
  }

  private abstract static class AbstractWaveletFunction extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      Scaling scaling = scaling(ast.arg1());
      IExpr x = ast.arg2();
      if (scaling == null || !x.isReal()) {
        return F.NIL;
      }
      double value = x.evalf();
      double[] support = support(scaling.order());
      if (value < support[0] || value >= support[1]) {
        return x.isInexactNumber() ? F.CD0 : F.C0;
      }
      if (x.isInexactNumber()) {
        return F.num(value(scaling, value));
      }
      if (scaling.order() == 1) {
        return exactHaar((IReal) x);
      }
      return F.NIL;
    }

    /** The support <code>{a, b}</code> of the function for a wavelet of order <code>n</code>. */
    abstract double[] support(int n);

    abstract double value(Scaling scaling, double x);

    /** The value of the Haar function at an exact <code>x</code> inside of its support. */
    abstract IExpr exactHaar(IReal x);

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.NUMERICFUNCTION);
    }
  }

  /** <code>WaveletPhi(wave, x)</code> - the scaling function of the wavelet at <code>x</code>. */
  public static final class WaveletPhi extends AbstractWaveletFunction {
    @Override
    double[] support(int n) {
      return new double[] {0.0, 2.0 * n - 1.0};
    }

    @Override
    double value(Scaling scaling, double x) {
      return scaling.phi(x);
    }

    @Override
    IExpr exactHaar(IReal x) {
      return F.C1;
    }
  }

  /** <code>WaveletPsi(wave, x)</code> - the wavelet function of the wavelet at <code>x</code>. */
  public static final class WaveletPsi extends AbstractWaveletFunction {
    @Override
    double[] support(int n) {
      return new double[] {1.0 - n, n};
    }

    @Override
    double value(Scaling scaling, double x) {
      return scaling.psi(x);
    }

    @Override
    IExpr exactHaar(IReal x) {
      return x.isLT(F.C1D2) ? F.C1 : F.CN1;
    }
  }

  private WaveletFunctions() {}
}
