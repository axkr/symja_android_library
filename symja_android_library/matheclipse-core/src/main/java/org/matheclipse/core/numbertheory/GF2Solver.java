package org.matheclipse.core.numbertheory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.matheclipse.core.eval.exception.TimeoutException;

/**
 * Dependencies between the rows of a sparse matrix over <code>GF(2)</code>: sets of rows whose sum
 * is the zero vector. This is the linear algebra step of the quadratic sieve, where a row is a
 * relation and its entries are the primes with an odd exponent in it.
 *
 * <p>
 * The matrix is first reduced: a column with a single entry can't take part in a dependency, so
 * its row is removed, until no such column is left. The remaining rows, with only as many more rows
 * than columns as dependencies are wanted, are reduced by Gaussian elimination on bit-packed rows.
 * The columns are eliminated from the sparsest to the densest one, which keeps the fill-in small,
 * and every row carries the set of original rows it is the sum of. Larger matrices are solved
 * with Montgomery's block Lanczos method, which needs only the sparse matrix and a few vectors of
 * 64 bit words.
 *
 * <p>
 * See P. L. Montgomery, "A block Lanczos algorithm for finding dependencies over GF(2)",
 * EUROCRYPT '95, LNCS 921 (1995).
 */
final class GF2Solver {

  /** The number of rows kept in excess of the number of columns. */
  private static final int EXCESS = 64;

  /** Matrices with at least this many rows are solved with the block Lanczos method. */
  static final int LANCZOS_ROWS = 3000;

  /** The largest number of rows for the dense Gaussian elimination, about 100 MB. */
  private static final int DENSE_ROWS = 20000;

  private GF2Solver() {}

  private static void checkInterrupted() {
    if (Thread.currentThread().isInterrupted()) {
      throw TimeoutException.TIMED_OUT;
    }
  }

  /**
   * Determine sets of rows whose sum is zero.
   *
   * @param rows the column indices of the nonzero entries of each row, without duplicates
   * @param columns the number of columns
   * @param wanted the maximal number of dependencies to return
   * @return the dependencies as arrays of row indices, at most <code>wanted</code>
   * @throws TimeoutException if the thread is interrupted
   */
  static List<int[]> dependencies(int[][] rows, int columns, int wanted) {
    final int n = rows.length;
    int[] weight = new int[columns];
    for (int[] row : rows) {
      for (int c : row) {
        weight[c]++;
      }
    }
    boolean[] alive = new boolean[n];
    Arrays.fill(alive, true);
    boolean changed = true;
    while (changed) {
      changed = false;
      for (int r = 0; r < n; r++) {
        if (alive[r]) {
          for (int c : rows[r]) {
            if (weight[c] == 1) {
              alive[r] = false;
              for (int c2 : rows[r]) {
                weight[c2]--;
              }
              changed = true;
              break;
            }
          }
        }
      }
    }
    // the remaining columns ordered by ascending weight
    Integer[] order = new Integer[columns];
    int m = 0;
    for (int c = 0; c < columns; c++) {
      if (weight[c] > 0) {
        order[m++] = c;
      }
    }
    final int[] w = weight;
    Arrays.sort(order, 0, m, (a, b) -> Integer.compare(w[a], w[b]));
    int[] columnIndex = new int[columns];
    Arrays.fill(columnIndex, -1);
    for (int i = 0; i < m; i++) {
      columnIndex[order[i]] = i;
    }
    int[] kept = new int[n];
    int nr = 0;
    for (int r = 0; r < n && nr < m + EXCESS; r++) {
      if (alive[r]) {
        kept[nr++] = r;
      }
    }
    if (nr == 0) {
      return new ArrayList<>();
    }
    if (nr >= LANCZOS_ROWS) {
      for (long seed = 1; seed <= 3; seed++) {
        List<int[]> result = blockLanczos(rows, kept, nr, columnIndex, m, seed);
        if (result != null && !result.isEmpty()) {
          return result.size() > wanted ? result.subList(0, wanted) : result;
        }
      }
      if (nr > DENSE_ROWS) {
        // the dense matrix would need too much memory
        return new ArrayList<>();
      }
    }
    return eliminate(rows, kept, nr, columnIndex, m, wanted);
  }

  /** Gaussian elimination of the rows <code>kept[0..nr-1]</code> with <code>m</code> columns. */
  private static List<int[]> eliminate(int[][] rows, int[] kept, int nr, int[] columnIndex, int m,
      int wanted) {
    final int mw = (m + 63) >>> 6;
    final int hw = (nr + 63) >>> 6;
    final int width = mw + hw;
    long[][] matrix = new long[nr][];
    // the range of nonzero words of the history part of each row
    int[] historyLow = new int[nr];
    int[] historyHigh = new int[nr];
    for (int r = 0; r < nr; r++) {
      long[] bits = new long[width];
      for (int c : rows[kept[r]]) {
        int col = columnIndex[c];
        bits[col >>> 6] |= 1L << (col & 63);
      }
      bits[mw + (r >>> 6)] |= 1L << (r & 63);
      historyLow[r] = historyHigh[r] = mw + (r >>> 6);
      matrix[r] = bits;
    }
    int pivot = 0;
    for (int col = 0; col < m && pivot < nr; col++) {
      if ((col & 63) == 0) {
        checkInterrupted();
      }
      final int word = col >>> 6;
      final long bit = 1L << (col & 63);
      int r = pivot;
      while (r < nr && (matrix[r][word] & bit) == 0) {
        r++;
      }
      if (r == nr) {
        continue;
      }
      swap(matrix, historyLow, historyHigh, r, pivot);
      final long[] p = matrix[pivot];
      final int low = historyLow[pivot];
      final int high = historyHigh[pivot];
      for (r = pivot + 1; r < nr; r++) {
        final long[] row = matrix[r];
        if ((row[word] & bit) != 0) {
          for (int k = word; k < mw; k++) {
            row[k] ^= p[k];
          }
          for (int k = low; k <= high; k++) {
            row[k] ^= p[k];
          }
          if (low < historyLow[r]) {
            historyLow[r] = low;
          }
          if (high > historyHigh[r]) {
            historyHigh[r] = high;
          }
        }
      }
      pivot++;
    }
    List<int[]> result = new ArrayList<>();
    for (int r = pivot; r < nr && result.size() < wanted; r++) {
      final long[] row = matrix[r];
      int count = 0;
      for (int k = mw; k < width; k++) {
        count += Long.bitCount(row[k]);
      }
      if (count == 0) {
        continue;
      }
      int[] dependency = new int[count];
      int i = 0;
      for (int k = mw; k < width; k++) {
        for (long bits = row[k]; bits != 0; bits &= bits - 1) {
          dependency[i++] = kept[((k - mw) << 6) + Long.numberOfTrailingZeros(bits)];
        }
      }
      result.add(dependency);
    }
    return result;
  }

  private static void swap(long[][] matrix, int[] low, int[] high, int i, int j) {
    if (i != j) {
      long[] t = matrix[i];
      matrix[i] = matrix[j];
      matrix[j] = t;
      int x = low[i];
      low[i] = low[j];
      low[j] = x;
      x = high[i];
      high[i] = high[j];
      high[j] = x;
    }
  }
  /**
   * The block Lanczos method for the null space of the matrix <code>B</code> whose columns are the
   * rows <code>kept[0..nr-1]</code>; it works with the symmetric <code>A = B^T*B</code>.
   *
   * @return the dependencies, or <code>null</code> if the iteration broke down
   */
  private static List<int[]> blockLanczos(int[][] rows, int[] kept, int nr, int[] columnIndex,
      int m, long seed) {
    final int n = nr;
    final int[] start = new int[n + 1];
    for (int c = 0; c < n; c++) {
      start[c + 1] = start[c] + rows[kept[c]].length;
    }
    final int[] entry = new int[start[n]];
    for (int c = 0, k = 0; c < n; c++) {
      for (int x : rows[kept[c]]) {
        entry[k++] = columnIndex[x];
      }
    }
    final long[] work = new long[m];
    Random random = new Random(seed);
    long[] y = new long[n];
    for (int k = 0; k < n; k++) {
      y[k] = random.nextLong();
    }
    final long[] v0 = new long[n];
    multiplyA(start, entry, y, v0, work);
    long[] v = v0.clone();
    long[] vPrev = new long[n];
    long[] vPrev2 = new long[n];
    long[] next = new long[n];
    final long[] x = new long[n];
    final long[] av = new long[n];
    long[] winvPrev = new long[64];
    long[] winvPrev2 = new long[64];
    long[] vAvPrev = new long[64];
    long[] vAAvPrev = new long[64];
    long sPrev = -1L;
    final int maxIterations = n / 60 + 64;
    for (int iteration = 0;; iteration++) {
      checkInterrupted();
      if (iteration > maxIterations) {
        return null;
      }
      multiplyA(start, entry, v, av, work);
      long[] vAv = innerProduct(v, av);
      long[] vAAv = innerProduct(av, av);
      if (isZero(vAv)) {
        break;
      }
      long[] winv = new long[64];
      long s = selectColumns(vAv, sPrev, winv);
      if (s == 0 || (s | sPrev) != -1L) {
        return null;
      }
      // X += V * Winv * (V^T * V0)
      addMultiple(x, v, multiply(winv, innerProduct(v, v0)));
      // D = I - Winv * (V^T*A^2*V * S*S^T + V^T*A*V)
      long[] t = new long[64];
      for (int r = 0; r < 64; r++) {
        t[r] = (vAAv[r] & s) ^ vAv[r];
      }
      long[] d = multiply(winv, t);
      for (int r = 0; r < 64; r++) {
        d[r] ^= 1L << r;
      }
      // E = -WinvPrev * V^T*A*V * S*S^T
      long[] masked = new long[64];
      for (int r = 0; r < 64; r++) {
        masked[r] = vAv[r] & s;
      }
      long[] e = multiply(winvPrev, masked);
      // F = -WinvPrev2 * (I - vAvPrev*WinvPrev) * (vAAvPrev * SPrev*SPrev^T + vAvPrev) * S*S^T
      long[] f1 = multiply(vAvPrev, winvPrev);
      for (int r = 0; r < 64; r++) {
        f1[r] ^= 1L << r;
      }
      long[] f2 = new long[64];
      for (int r = 0; r < 64; r++) {
        f2[r] = (vAAvPrev[r] & sPrev) ^ vAvPrev[r];
      }
      long[] f = multiply(winvPrev2, multiply(f1, f2));
      for (int r = 0; r < 64; r++) {
        f[r] &= s;
      }
      // V_{i+1} = A*V*S*S^T + V*D + VPrev*E + VPrev2*F
      for (int k = 0; k < n; k++) {
        next[k] = av[k] & s;
      }
      addMultiple(next, v, d);
      addMultiple(next, vPrev, e);
      addMultiple(next, vPrev2, f);
      long[] recycled = vPrev2;
      vPrev2 = vPrev;
      vPrev = v;
      v = next;
      next = recycled;
      winvPrev2 = winvPrev;
      winvPrev = winv;
      vAvPrev = vAv;
      vAAvPrev = vAAv;
      sPrev = s;
    }
    // A*(X+Y) = 0 and V^T*A*V = 0: combine the columns of X+Y and V into null vectors of B
    for (int k = 0; k < n; k++) {
      x[k] ^= y[k];
    }
    long[] bx = new long[m];
    long[] bv = new long[m];
    multiplyB(start, entry, x, bx);
    multiplyB(start, entry, v, bv);
    long[][] kernel = kernel(bx, bv, m);
    if (kernel.length == 0) {
      return null;
    }
    long[] solution = new long[n];
    for (int k = 0; k < n; k++) {
      long bits = 0;
      for (int i = 0; i < kernel.length; i++) {
        long parity = Long.bitCount(x[k] & kernel[i][0]) + Long.bitCount(v[k] & kernel[i][1]);
        bits |= (parity & 1L) << i;
      }
      solution[k] = bits;
    }
    // keep only the columns which really are null vectors of B
    long[] check = new long[m];
    multiplyB(start, entry, solution, check);
    long bad = 0;
    for (long word : check) {
      bad |= word;
    }
    List<int[]> result = new ArrayList<>();
    for (int i = 0; i < kernel.length; i++) {
      if ((bad >>> i & 1L) != 0) {
        continue;
      }
      int count = 0;
      for (int k = 0; k < n; k++) {
        count += (int) (solution[k] >>> i & 1L);
      }
      if (count == 0) {
        continue;
      }
      int[] dependency = new int[count];
      for (int k = 0, j = 0; k < n; k++) {
        if ((solution[k] >>> i & 1L) != 0) {
          dependency[j++] = kept[k];
        }
      }
      result.add(dependency);
    }
    return result;
  }

  /**
   * <code>out = B*x</code> for the matrix whose column <code>c</code> has its entries in the rows
   * <code>entry[start[c]..start[c+1]-1]</code>.
   */
  private static void multiplyB(int[] start, int[] entry, long[] x, long[] out) {
    Arrays.fill(out, 0L);
    final int n = start.length - 1;
    for (int c = 0; c < n; c++) {
      final long xc = x[c];
      if (xc != 0) {
        for (int k = start[c], end = start[c + 1]; k < end; k++) {
          out[entry[k]] ^= xc;
        }
      }
    }
  }

  /** <code>out = B^T*B*x</code> with the temporary vector <code>work</code>. */
  private static void multiplyA(int[] start, int[] entry, long[] x, long[] out, long[] work) {
    multiplyB(start, entry, x, work);
    final int n = start.length - 1;
    for (int c = 0; c < n; c++) {
      long sum = 0;
      for (int k = start[c], end = start[c + 1]; k < end; k++) {
        sum ^= work[entry[k]];
      }
      out[c] = sum;
    }
  }

  /**
   * The 64x64 matrix <code>v^T*w</code> of two n x 64 matrices; row <code>r</code> belongs to bit
   * <code>r</code> of <code>v</code>.
   */
  static long[] innerProduct(long[] v, long[] w) {
    final long[] table = new long[8 * 256];
    for (int k = 0; k < v.length; k++) {
      final long a = v[k];
      if (a != 0) {
        final long b = w[k];
        table[(int) (a & 0xFF)] ^= b;
        table[256 + (int) (a >>> 8 & 0xFF)] ^= b;
        table[512 + (int) (a >>> 16 & 0xFF)] ^= b;
        table[768 + (int) (a >>> 24 & 0xFF)] ^= b;
        table[1024 + (int) (a >>> 32 & 0xFF)] ^= b;
        table[1280 + (int) (a >>> 40 & 0xFF)] ^= b;
        table[1536 + (int) (a >>> 48 & 0xFF)] ^= b;
        table[1792 + (int) (a >>> 56 & 0xFF)] ^= b;
      }
    }
    long[] result = new long[64];
    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        long sum = 0;
        for (int index = 1; index < 256; index++) {
          if ((index >>> j & 1) != 0) {
            sum ^= table[256 * i + index];
          }
        }
        result[8 * i + j] = sum;
      }
    }
    return result;
  }

  /** <code>out += v*matrix</code> for the n x 64 matrix <code>v</code>. */
  static void addMultiple(long[] out, long[] v, long[] matrix) {
    final long[] table = new long[8 * 256];
    for (int i = 0; i < 8; i++) {
      for (int index = 1; index < 256; index++) {
        table[256 * i + index] = table[256 * i + (index & (index - 1))]
            ^ matrix[8 * i + Integer.numberOfTrailingZeros(index)];
      }
    }
    for (int k = 0; k < v.length; k++) {
      final long a = v[k];
      if (a != 0) {
        out[k] ^= table[(int) (a & 0xFF)] ^ table[256 + (int) (a >>> 8 & 0xFF)]
            ^ table[512 + (int) (a >>> 16 & 0xFF)] ^ table[768 + (int) (a >>> 24 & 0xFF)]
            ^ table[1024 + (int) (a >>> 32 & 0xFF)] ^ table[1280 + (int) (a >>> 40 & 0xFF)]
            ^ table[1536 + (int) (a >>> 48 & 0xFF)] ^ table[1792 + (int) (a >>> 56 & 0xFF)];
      }
    }
  }

  /** The product of two 64x64 matrices. */
  static long[] multiply(long[] p, long[] q) {
    long[] result = new long[64];
    for (int r = 0; r < 64; r++) {
      long sum = 0;
      for (long bits = p[r]; bits != 0; bits &= bits - 1) {
        sum ^= q[Long.numberOfTrailingZeros(bits)];
      }
      result[r] = sum;
    }
    return result;
  }

  private static boolean isZero(long[] matrix) {
    for (long row : matrix) {
      if (row != 0) {
        return false;
      }
    }
    return true;
  }

  /**
   * Choose the columns <code>S</code> of the symmetric <code>t = V^T*A*V</code> for which
   * <code>S^T*t*S</code> is invertible, preferring the columns which weren't chosen in the
   * previous iteration, and store <code>S*(S^T*t*S)^-1*S^T</code> in <code>winv</code>.
   *
   * @return the bit mask of the chosen columns, or <code>0</code> if the elimination failed
   */
  static long selectColumns(long[] t, long previous, long[] winv) {
    long[] left = t.clone();
    long[] right = new long[64];
    for (int r = 0; r < 64; r++) {
      right[r] = 1L << r;
    }
    int[] order = new int[64];
    int count = 0;
    for (int c = 0; c < 64; c++) {
      if ((previous >>> c & 1L) == 0) {
        order[count++] = c;
      }
    }
    for (int c = 0; c < 64; c++) {
      if ((previous >>> c & 1L) != 0) {
        order[count++] = c;
      }
    }
    long selected = 0;
    for (int i = 0; i < 64; i++) {
      final long mask = 1L << order[i];
      int j = i;
      while (j < 64 && (left[order[j]] & mask) == 0) {
        j++;
      }
      if (j < 64) {
        swapRows(left, right, order[i], order[j]);
        final int pivot = order[i];
        for (int r = 0; r < 64; r++) {
          if (r != pivot && (left[r] & mask) != 0) {
            left[r] ^= left[pivot];
            right[r] ^= right[pivot];
          }
        }
        selected |= mask;
        continue;
      }
      // no pivot in t: eliminate with the inverse part and drop the row
      j = i;
      while (j < 64 && (right[order[j]] & mask) == 0) {
        j++;
      }
      if (j == 64) {
        return 0;
      }
      swapRows(left, right, order[i], order[j]);
      final int pivot = order[i];
      for (int r = 0; r < 64; r++) {
        if (r != pivot && (right[r] & mask) != 0) {
          left[r] ^= left[pivot];
          right[r] ^= right[pivot];
        }
      }
      left[pivot] = 0;
      right[pivot] = 0;
    }
    System.arraycopy(right, 0, winv, 0, 64);
    return selected;
  }

  private static void swapRows(long[] left, long[] right, int i, int j) {
    long t = left[i];
    left[i] = left[j];
    left[j] = t;
    t = right[i];
    right[i] = right[j];
    right[j] = t;
  }

  /**
   * The combinations of the 128 columns of the two m x 64 matrices <code>bx</code> and
   * <code>bv</code> which sum to zero, each as two words of coefficients.
   */
  private static long[][] kernel(long[] bx, long[] bv, int m) {
    final int words = (m + 63) >>> 6;
    long[][] vectors = new long[128][words];
    for (int r = 0; r < m; r++) {
      final long bit = 1L << (r & 63);
      final int word = r >>> 6;
      for (long bits = bx[r]; bits != 0; bits &= bits - 1) {
        vectors[Long.numberOfTrailingZeros(bits)][word] |= bit;
      }
      for (long bits = bv[r]; bits != 0; bits &= bits - 1) {
        vectors[64 + Long.numberOfTrailingZeros(bits)][word] |= bit;
      }
    }
    long[][] history = new long[128][2];
    int[] pivotPosition = new int[128];
    int[] pivotVector = new int[128];
    int pivots = 0;
    List<long[]> result = new ArrayList<>();
    for (int i = 0; i < 128; i++) {
      history[i][i >>> 6] = 1L << (i & 63);
      final long[] vector = vectors[i];
      for (int p = 0; p < pivots; p++) {
        final int position = pivotPosition[p];
        if ((vector[position >>> 6] >>> (position & 63) & 1L) != 0) {
          final long[] other = vectors[pivotVector[p]];
          for (int k = 0; k < words; k++) {
            vector[k] ^= other[k];
          }
          history[i][0] ^= history[pivotVector[p]][0];
          history[i][1] ^= history[pivotVector[p]][1];
        }
      }
      int position = -1;
      for (int k = 0; k < words; k++) {
        if (vector[k] != 0) {
          position = (k << 6) + Long.numberOfTrailingZeros(vector[k]);
          break;
        }
      }
      if (position < 0) {
        if (result.size() < 64) {
          result.add(history[i]);
        }
      } else {
        pivotPosition[pivots] = position;
        pivotVector[pivots++] = i;
      }
    }
    return result.toArray(new long[0][]);
  }
}
