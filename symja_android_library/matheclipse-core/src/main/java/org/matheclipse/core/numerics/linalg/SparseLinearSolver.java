package org.matheclipse.core.numerics.linalg;

import java.util.Arrays;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.data.SparseArrayExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.parser.client.ParserConfig;
import org.matheclipse.parser.trie.TrieNode;

/**
 * The solution of <code>A . x == b</code> for a real square sparse matrix <code>A</code> without
 * forming the dense matrix.
 *
 * <p>
 * The matrix is stored by rows (compressed sparse rows) in doubles. A system is solved in machine
 * arithmetic by a banded LU decomposition with partial pivoting where the band is narrow enough,
 * else by a Krylov method with the diagonal as preconditioner: conjugate gradients for a symmetric
 * matrix, BiCGSTAB otherwise, and by the banded decomposition up to a larger cost when the
 * iteration doesn't converge.
 *
 * <p>
 * With entries of a higher precision the machine solver corrects the solution by iterative
 * refinement: the residual <code>b - A . x</code> is computed with the precision of the entries,
 * the machine solution of <code>A . d == residual</code> is added to <code>x</code>, and every step
 * gains the digits of the machine solution.
 */
public final class SparseLinearSolver {

  /** The flops of a banded decomposition which is tried before an iteration. */
  private static final double CHEAP_BANDED = 2.0e8;

  /** The flops of a banded decomposition which is tried after an iteration failed. */
  private static final double AFFORDABLE_BANDED = 4.0e9;

  /** The largest number of doubles of the band storage. */
  private static final long MAX_BAND_STORAGE = 60_000_000L;

  /** The relative residual at which a Krylov iteration stops. */
  private static final double KRYLOV_TOLERANCE = 1.0e-15;

  /**
   * The relative residual of the best iterate which is accepted when an iteration stagnates above
   * {@link #KRYLOV_TOLERANCE}: rounding keeps the residual of an ill-conditioned system larger, and
   * the refinement steps correct the rest.
   */
  private static final double KRYLOV_ACCEPTABLE = 1.0e-8;

  /** The largest number of refinement steps at a higher precision. */
  private static final int MAX_REFINEMENTS = 40;

  private final int n;
  private final int[] rowStart;
  private final int[] columns;
  private final double[] values;
  /** the entries with their own precision, or <code>null</code> at machine precision */
  private final Apfloat[] precise;
  private final double[] diagonal;
  private final int lower;
  private final int upper;
  private final boolean symmetric;
  private final EvalEngine engine;

  /** the banded LU decomposition, once it is computed */
  private double[][] band;
  private int[] pivots;
  private boolean bandFailed;

  private SparseLinearSolver(int n, int[] rowStart, int[] columns, double[] values,
      Apfloat[] precise, EvalEngine engine) {
    this.n = n;
    this.rowStart = rowStart;
    this.columns = columns;
    this.values = values;
    this.precise = precise;
    this.engine = engine;
    this.diagonal = new double[n];
    int lo = 0;
    int up = 0;
    for (int i = 0; i < n; i++) {
      for (int k = rowStart[i]; k < rowStart[i + 1]; k++) {
        int j = columns[k];
        if (j == i) {
          diagonal[i] = values[k];
        }
        lo = Math.max(lo, i - j);
        up = Math.max(up, j - i);
      }
    }
    this.lower = lo;
    this.upper = up;
    this.symmetric = isSymmetric();
  }

  /**
   * The solver for a square sparse matrix of numbers with the default value <code>0</code>.
   *
   * @param precision the digits of the entries, or {@link ParserConfig#MACHINE_PRECISION}
   * @return <code>null</code> if the matrix isn't of this kind
   */
  public static SparseLinearSolver of(SparseArrayExpr matrix, long precision, EvalEngine engine) {
    int[] dimension = matrix.getDimension();
    if (dimension.length != 2 || dimension[0] != dimension[1] || !matrix.getDefaultValue().isZero()) {
      return null;
    }
    final int n = dimension[0];
    int[] counts = new int[n + 1];
    int nonZeros = 0;
    for (TrieNode<int[], IExpr> entry : matrix.toData().nodeSet()) {
      IExpr value = entry.getValue();
      if (!value.isReal()) {
        return null;
      }
      counts[entry.getKey()[0]]++;
      nonZeros++;
    }
    int[] rowStart = new int[n + 1];
    for (int i = 0; i < n; i++) {
      rowStart[i + 1] = rowStart[i] + counts[i + 1];
    }
    int[] fill = Arrays.copyOf(rowStart, n);
    int[] columns = new int[nonZeros];
    double[] values = new double[nonZeros];
    final boolean arbitrary = precision > ParserConfig.MACHINE_PRECISION;
    Apfloat[] precise = arbitrary ? new Apfloat[nonZeros] : null;
    for (TrieNode<int[], IExpr> entry : matrix.toData().nodeSet()) {
      int[] key = entry.getKey();
      int i = key[0] - 1;
      int k = fill[i]++;
      columns[k] = key[1] - 1;
      IExpr value = entry.getValue();
      values[k] = value.evalf();
      if (!Double.isFinite(values[k])) {
        return null;
      }
      if (arbitrary) {
        precise[k] = apfloatOf(value, precision, engine);
        if (precise[k] == null) {
          return null;
        }
      }
    }
    // sort the columns of each row
    for (int i = 0; i < n; i++) {
      sortRow(columns, values, precise, rowStart[i], rowStart[i + 1]);
    }
    return new SparseLinearSolver(n, rowStart, columns, values, precise, engine);
  }

  /**
   * The value of a real number with <code>digits</code> digits: an exact number is evaluated to
   * them, not just widened from the default precision of its conversion.
   *
   * @return <code>null</code> if it has no such value
   */
  private static Apfloat apfloatOf(IExpr value, long digits, EvalEngine engine) {
    IExpr number = value.isInexactNumber() ? value : engine.evalN(value, digits);
    if (!(number instanceof IReal)) {
      return null;
    }
    return ((IReal) number).apfloatValue().precision(digits);
  }

  private static void sortRow(int[] columns, double[] values, Apfloat[] precise, int from,
      int to) {
    for (int a = from + 1; a < to; a++) {
      int c = columns[a];
      double v = values[a];
      Apfloat p = precise != null ? precise[a] : null;
      int b = a - 1;
      while (b >= from && columns[b] > c) {
        columns[b + 1] = columns[b];
        values[b + 1] = values[b];
        if (precise != null) {
          precise[b + 1] = precise[b];
        }
        b--;
      }
      columns[b + 1] = c;
      values[b + 1] = v;
      if (precise != null) {
        precise[b + 1] = p;
      }
    }
  }

  private boolean isSymmetric() {
    for (int i = 0; i < n; i++) {
      for (int k = rowStart[i]; k < rowStart[i + 1]; k++) {
        int j = columns[k];
        if (j != i) {
          int pos = Arrays.binarySearch(columns, rowStart[j], rowStart[j + 1], i);
          if (pos < 0 || values[pos] != values[k]) {
            return false;
          }
        }
      }
    }
    return true;
  }

  /**
   * Solve <code>A . x == b</code>.
   *
   * @param b the right-hand side, a list of real numbers
   * @param precision the digits of the solution, or {@link ParserConfig#MACHINE_PRECISION}
   * @return the solution as a list, or {@link F#NIL} if no method solved the system
   */
  public IExpr solve(IAST b, long precision) {
    if (b.argSize() != n || !b.forAll(IExpr::isReal)) {
      return F.NIL;
    }
    double[] rhs = new double[n];
    for (int i = 0; i < n; i++) {
      rhs[i] = b.get(i + 1).evalf();
    }
    double[] x = machineSolve(rhs);
    if (x == null) {
      return F.NIL;
    }
    if (precision <= ParserConfig.MACHINE_PRECISION || precise == null) {
      // one refinement step in doubles
      double[] residual = new double[n];
      multiply(x, residual);
      for (int i = 0; i < n; i++) {
        residual[i] = rhs[i] - residual[i];
      }
      double[] correction = machineSolve(residual);
      if (correction != null) {
        for (int i = 0; i < n; i++) {
          x[i] += correction[i];
        }
      }
      final double[] solution = x;
      return F.mapRange(0, n, i -> F.num(solution[i]));
    }
    return refine(b, x, precision);
  }

  /** Iterative refinement of the machine solution <code>x0</code> to <code>precision</code>. */
  private IExpr refine(IAST b, double[] x0, long precision) {
    final long work = precision + 10;
    FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(work);
    Apfloat[] bp = new Apfloat[n];
    Apfloat bNorm = Apfloat.ZERO;
    for (int i = 0; i < n; i++) {
      bp[i] = apfloatOf(b.get(i + 1), work, engine);
      if (bp[i] == null) {
        return F.NIL;
      }
      bNorm = maxAbs(bNorm, bp[i]);
    }
    Apfloat[] x = new Apfloat[n];
    for (int i = 0; i < n; i++) {
      x[i] = new Apfloat(x0[i], work);
    }
    Apfloat tolerance = h.multiply(bNorm, h.pow(new Apfloat(10, work), -(precision + 2)));
    // the row sum norm of the matrix: the attainable residual is about its product with the
    // size of x and the unit roundoff of the working digits
    Apfloat matrixNorm = Apfloat.ZERO;
    for (int i = 0; i < n; i++) {
      Apfloat rowSum = Apfloat.ZERO;
      for (int k = rowStart[i]; k < rowStart[i + 1]; k++) {
        rowSum = h.add(rowSum, ApfloatMath.abs(precise[k]));
      }
      matrixNorm = rowSum.compareTo(matrixNorm) > 0 ? rowSum : matrixNorm;
    }
    Apfloat previous = null;
    for (int step = 0; step < MAX_REFINEMENTS; step++) {
      engine.checkTimeLimit();
      Apfloat[] residual = new Apfloat[n];
      Apfloat norm = Apfloat.ZERO;
      for (int i = 0; i < n; i++) {
        Apfloat sum = bp[i];
        for (int k = rowStart[i]; k < rowStart[i + 1]; k++) {
          sum = h.subtract(sum, h.multiply(precise[k], x[columns[k]]));
        }
        residual[i] = sum;
        norm = maxAbs(norm, sum);
      }
      if (norm.signum() == 0 || norm.compareTo(tolerance) <= 0) {
        break;
      }
      if (previous != null && norm.compareTo(h.multiply(previous, new Apfloat("0.5", work))) > 0) {
        // no more progress: the solution is as good as the conditioning allows when the residual
        // is at the rounding level of A . x, else the machine solver doesn't solve the system
        Apfloat xNorm = Apfloat.ZERO;
        for (Apfloat v : x) {
          xNorm = maxAbs(xNorm, v);
        }
        Apfloat attainable = h.multiply(h.multiply(matrixNorm, xNorm),
            h.pow(new Apfloat(10, work), -precision));
        if (norm.compareTo(attainable) <= 0) {
          break;
        }
        return F.NIL;
      }
      previous = norm;
      // the machine solution of A . d == residual, scaled into the range of a double
      double scale = norm.doubleValue();
      Apfloat apScale = norm;
      double[] r = new double[n];
      for (int i = 0; i < n; i++) {
        r[i] = h.divide(residual[i], apScale).doubleValue();
      }
      double[] d = machineSolve(r);
      if (d == null || !Double.isFinite(scale)) {
        return F.NIL;
      }
      for (int i = 0; i < n; i++) {
        x[i] = h.add(x[i], h.multiply(new Apfloat(d[i], work), apScale));
      }
    }
    final Apfloat[] solution = x;
    return F.mapRange(0, n, i -> ApfloatNum.valueOf(solution[i].precision(precision)));
  }

  private static Apfloat maxAbs(Apfloat max, Apfloat value) {
    Apfloat abs = value.signum() < 0 ? value.negate() : value;
    return abs.compareTo(max) > 0 ? abs : max;
  }

  /** The machine solution, or <code>null</code>. */
  private double[] machineSolve(double[] rhs) {
    double cost = (double) n * lower * (lower + upper + 1);
    if (cost <= CHEAP_BANDED) {
      double[] x = bandedSolve(rhs);
      if (x != null) {
        return x;
      }
    }
    double[] x = symmetric ? conjugateGradient(rhs) : biCGStab(rhs);
    if (x != null) {
      return x;
    }
    if (cost <= AFFORDABLE_BANDED) {
      return bandedSolve(rhs);
    }
    return null;
  }

  private void multiply(double[] x, double[] y) {
    for (int i = 0; i < n; i++) {
      double sum = 0.0;
      for (int k = rowStart[i]; k < rowStart[i + 1]; k++) {
        sum += values[k] * x[columns[k]];
      }
      y[i] = sum;
    }
  }

  private void precondition(double[] r, double[] z) {
    for (int i = 0; i < n; i++) {
      z[i] = diagonal[i] != 0.0 ? r[i] / diagonal[i] : r[i];
    }
  }

  private static double dot(double[] a, double[] b) {
    double sum = 0.0;
    for (int i = 0; i < a.length; i++) {
      sum += a[i] * b[i];
    }
    return sum;
  }

  private int maxIterations() {
    return Math.max(1000, 20 * (int) Math.sqrt(n) + 200);
  }

  /** Conjugate gradients with the diagonal as preconditioner, <code>null</code> if no convergence. */
  private double[] conjugateGradient(double[] b) {
    double bNorm = Math.sqrt(dot(b, b));
    double[] x = new double[n];
    if (bNorm == 0.0) {
      return x;
    }
    double[] r = b.clone();
    double[] z = new double[n];
    precondition(r, z);
    double[] p = z.clone();
    double[] q = new double[n];
    double rz = dot(r, z);
    double[] best = null;
    double bestResidual = Double.POSITIVE_INFINITY;
    for (int it = 0; it < maxIterations(); it++) {
      if ((it & 31) == 0) {
        engine.checkTimeLimit();
      }
      multiply(p, q);
      double pq = dot(p, q);
      if (pq == 0.0 || !Double.isFinite(pq)) {
        return null;
      }
      double alpha = rz / pq;
      for (int i = 0; i < n; i++) {
        x[i] += alpha * p[i];
        r[i] -= alpha * q[i];
      }
      double residual = Math.sqrt(dot(r, r));
      if (residual <= KRYLOV_TOLERANCE * bNorm) {
        return x;
      }
      if (residual < bestResidual) {
        bestResidual = residual;
        best = x.clone();
      }
      precondition(r, z);
      double rzNext = dot(r, z);
      double beta = rzNext / rz;
      rz = rzNext;
      for (int i = 0; i < n; i++) {
        p[i] = z[i] + beta * p[i];
      }
    }
    return bestResidual <= KRYLOV_ACCEPTABLE * bNorm ? best : null;
  }

  /** BiCGSTAB with the diagonal as preconditioner, <code>null</code> if no convergence. */
  private double[] biCGStab(double[] b) {
    double bNorm = Math.sqrt(dot(b, b));
    double[] x = new double[n];
    if (bNorm == 0.0) {
      return x;
    }
    double[] r = b.clone();
    double[] r0 = b.clone();
    double[] p = new double[n];
    double[] v = new double[n];
    double[] s = new double[n];
    double[] t = new double[n];
    double[] y = new double[n];
    double[] z = new double[n];
    double rho = 1.0;
    double alpha = 1.0;
    double omega = 1.0;
    double[] best = null;
    double bestResidual = Double.POSITIVE_INFINITY;
    for (int it = 0; it < maxIterations(); it++) {
      if ((it & 31) == 0) {
        engine.checkTimeLimit();
      }
      double rhoNext = dot(r0, r);
      if (rhoNext == 0.0 || omega == 0.0) {
        return null;
      }
      double beta = (rhoNext / rho) * (alpha / omega);
      rho = rhoNext;
      for (int i = 0; i < n; i++) {
        p[i] = r[i] + beta * (p[i] - omega * v[i]);
      }
      precondition(p, y);
      multiply(y, v);
      double r0v = dot(r0, v);
      if (r0v == 0.0) {
        return null;
      }
      alpha = rho / r0v;
      for (int i = 0; i < n; i++) {
        s[i] = r[i] - alpha * v[i];
      }
      if (Math.sqrt(dot(s, s)) <= KRYLOV_TOLERANCE * bNorm) {
        for (int i = 0; i < n; i++) {
          x[i] += alpha * y[i];
        }
        return x;
      }
      precondition(s, z);
      multiply(z, t);
      double tt = dot(t, t);
      if (tt == 0.0) {
        return null;
      }
      omega = dot(t, s) / tt;
      for (int i = 0; i < n; i++) {
        x[i] += alpha * y[i] + omega * z[i];
        r[i] = s[i] - omega * t[i];
      }
      if (!Double.isFinite(r[0])) {
        break;
      }
      double residual = Math.sqrt(dot(r, r));
      if (residual <= KRYLOV_TOLERANCE * bNorm) {
        return x;
      }
      if (residual < bestResidual) {
        bestResidual = residual;
        best = x.clone();
      }
    }
    return bestResidual <= KRYLOV_ACCEPTABLE * bNorm ? best : null;
  }

  /**
   * The solution by the banded LU decomposition with partial pivoting, which is computed once.
   * Pivoting widens the upper band by the lower one.
   */
  private double[] bandedSolve(double[] rhs) {
    if (bandFailed) {
      return null;
    }
    final int kl = lower;
    final int ku = upper + lower;
    final int width = kl + ku + 1;
    if (band == null) {
      if ((long) n * width > MAX_BAND_STORAGE) {
        bandFailed = true;
        return null;
      }
      // band[i][j - i + kl]: row i, column j
      double[][] a = new double[n][width];
      for (int i = 0; i < n; i++) {
        for (int k = rowStart[i]; k < rowStart[i + 1]; k++) {
          a[i][columns[k] - i + kl] = values[k];
        }
      }
      int[] piv = new int[n];
      for (int col = 0; col < n; col++) {
        if ((col & 255) == 0) {
          engine.checkTimeLimit();
        }
        int last = Math.min(n - 1, col + kl);
        int best = col;
        double max = Math.abs(a[col][kl]);
        for (int i = col + 1; i <= last; i++) {
          double v = Math.abs(a[i][col - i + kl]);
          if (v > max) {
            max = v;
            best = i;
          }
        }
        if (max == 0.0) {
          bandFailed = true;
          return null;
        }
        piv[col] = best;
        int right = Math.min(n - 1, col + ku);
        if (best != col) {
          for (int j = col; j <= right; j++) {
            double tmp = a[col][j - col + kl];
            a[col][j - col + kl] = a[best][j - best + kl];
            a[best][j - best + kl] = tmp;
          }
        }
        double pivot = a[col][kl];
        for (int i = col + 1; i <= last; i++) {
          double factor = a[i][col - i + kl] / pivot;
          if (factor == 0.0) {
            continue;
          }
          a[i][col - i + kl] = factor;
          for (int j = col + 1; j <= right; j++) {
            a[i][j - i + kl] -= factor * a[col][j - col + kl];
          }
        }
      }
      band = a;
      pivots = piv;
    }
    double[] x = rhs.clone();
    for (int col = 0; col < n; col++) {
      int p = pivots[col];
      if (p != col) {
        double tmp = x[col];
        x[col] = x[p];
        x[p] = tmp;
      }
      int last = Math.min(n - 1, col + kl);
      for (int i = col + 1; i <= last; i++) {
        x[i] -= band[i][col - i + kl] * x[col];
      }
    }
    for (int i = n - 1; i >= 0; i--) {
      double sum = x[i];
      int right = Math.min(n - 1, i + ku);
      for (int j = i + 1; j <= right; j++) {
        sum -= band[i][j - i + kl] * x[j];
      }
      x[i] = sum / band[i][kl];
    }
    for (double v : x) {
      if (!Double.isFinite(v)) {
        return null;
      }
    }
    return x;
  }
}
