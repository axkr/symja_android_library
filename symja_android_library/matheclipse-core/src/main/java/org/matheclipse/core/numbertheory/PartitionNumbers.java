package org.matheclipse.core.numbertheory;

import java.lang.ref.SoftReference;
import java.math.BigInteger;
import java.util.Arrays;
import org.matheclipse.core.eval.EvalEngine;

/**
 * The partition numbers <code>p(n)</code> and the numbers <code>q(n)</code> of partitions into
 * distinct parts by Euler's pentagonal number recurrences in <code>O(n^(3/2))</code> additions.
 *
 * <p>
 * With <code>E(x) = prod(1 - x^k) = sum((-1)^j x^(j(3j-1)/2), j in Z)</code> the generating
 * functions satisfy <code>P(x) E(x) = 1</code> and <code>Q(x) E(x) = E(x^2)</code>. The values up
 * to {@link #TABLE_LIMIT} are kept in shared tables, so a run over consecutive arguments costs one
 * recurrence. The values of the last larger argument are kept softly referenced.
 */
public final class PartitionNumbers {

  /** Arguments up to this bound are remembered. */
  private static final int TABLE_LIMIT = 20_000;

  private static BigInteger[] pTable = {BigInteger.ONE};

  private static BigInteger[] qTable = {BigInteger.ONE};

  /** The last values beyond {@link #TABLE_LIMIT}, released under memory pressure. */
  private static SoftReference<BigInteger[]> pLarge = new SoftReference<BigInteger[]>(null);

  private static SoftReference<BigInteger[]> qLarge = new SoftReference<BigInteger[]>(null);

  private PartitionNumbers() {}

  /** The number of partitions of <code>n &gt;= 0</code>. */
  public static BigInteger partitionsP(int n) {
    return values(n, false)[n];
  }

  /** The number of partitions of <code>n &gt;= 0</code> into distinct parts. */
  public static BigInteger partitionsQ(int n) {
    return values(n, true)[n];
  }

  private static BigInteger[] values(int n, boolean distinct) {
    BigInteger[] table;
    BigInteger[] large;
    synchronized (PartitionNumbers.class) {
      table = distinct ? qTable : pTable;
      large = distinct ? qLarge.get() : pLarge.get();
    }
    if (n < table.length) {
      return table;
    }
    if (large != null && n < large.length) {
      return large;
    }
    BigInteger[] start = large != null && large.length > table.length ? large : table;
    BigInteger[] result = Arrays.copyOf(start, n + 1);
    extend(result, start.length, distinct);
    synchronized (PartitionNumbers.class) {
      if (n <= TABLE_LIMIT) {
        if (distinct) {
          if (qTable.length < result.length) {
            qTable = result;
          }
        } else if (pTable.length < result.length) {
          pTable = result;
        }
      } else if (distinct) {
        qLarge = new SoftReference<BigInteger[]>(result);
      } else {
        pLarge = new SoftReference<BigInteger[]>(result);
      }
    }
    return result;
  }

  /** Fills <code>v[from..]</code> from the values below. */
  private static void extend(BigInteger[] v, int from, boolean distinct) {
    EvalEngine engine = EvalEngine.get();
    for (int m = from; m < v.length; m++) {
      BigInteger sum = BigInteger.ZERO;
      // generalized pentagonal numbers g = j(3j-1)/2 for j = 1, -1, 2, -2, ...
      for (int j = 1;; j++) {
        long g1 = (long) j * (3 * j - 1) / 2;
        if (g1 > m) {
          break;
        }
        boolean add = (j & 1) == 1;
        sum = add ? sum.add(v[(int) (m - g1)]) : sum.subtract(v[(int) (m - g1)]);
        long g2 = (long) j * (3 * j + 1) / 2;
        if (g2 <= m) {
          sum = add ? sum.add(v[(int) (m - g2)]) : sum.subtract(v[(int) (m - g2)]);
        }
      }
      if (distinct) {
        // the coefficient of x^m in E(x^2): (-1)^j for m = j(3j-1)
        int e = pentagonalSign(m);
        if (e != 0) {
          sum = e > 0 ? sum.add(BigInteger.ONE) : sum.subtract(BigInteger.ONE);
        }
      }
      v[m] = sum;
      if ((m & 255) == 0) {
        engine.checkTimeLimit();
      }
    }
  }

  /**
   * <code>(-1)^j</code> if <code>m = j(3j-1)</code> for an integer <code>j</code>, otherwise 0.
   */
  private static int pentagonalSign(int m) {
    // m = j(3j-1)  <=>  12m + 1 = (6j - 1)^2
    long d = 12L * m + 1;
    long r = (long) Math.sqrt((double) d);
    while (r * r > d) {
      r--;
    }
    while ((r + 1) * (r + 1) <= d) {
      r++;
    }
    if (r * r != d) {
      return 0;
    }
    // 6j - 1 = r or 6j - 1 = -r
    long j;
    if ((r + 1) % 6 == 0) {
      j = (r + 1) / 6;
    } else if ((1 - r) % 6 == 0) {
      j = (1 - r) / 6;
    } else {
      return 0;
    }
    return (j & 1) == 0 ? 1 : -1;
  }
}
