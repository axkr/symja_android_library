package org.matheclipse.core.combinatoric;

import java.util.Iterator;
import java.util.NoSuchElementException;
import com.google.common.math.LongMath;


/**
 * Iterable for all k-combinations from a set
 *
 * <p>
 * See <a href="http://en.wikipedia.org/wiki/Combination">Combination</a>
 */
public final class KSubsetsIterable implements Iterable<int[]> {

  private final int n;
  private final int k;

  private long bin;
  private boolean first;

  private class KSubsetsIterator implements Iterator<int[]> {
    private final int x[];

    private KSubsetsIterator() {
      x = new int[n];
      for (int a = 0; a < n; a++) {
        x[a] = a;
      }
    }

    @Override
    public int[] next() {
      if (bin-- == 0) {
        throw new NoSuchElementException();
      }
      if (first) {
        first = false;
        return x;
      }
      int i = k - 1;
      while (x[i] == n - k + i) {
        i--;
      }
      x[i] = x[i] + 1;
      for (int j = i + 1; j < n; j++) {
        x[j] = x[j - 1] + 1;
      }
      return x;
    }

    @Override
    public boolean hasNext() {
      return bin > 0;
    }
  }

  public KSubsetsIterable(final int len, final int parts) {
    super();
    n = len;
    k = parts;
    if (k > n || k < 0) {
      throw new IllegalArgumentException("KSubsets: k>n - " + k + " > " + n);
    }

    bin = LongMath.binomial(n, k);
    first = true;
  }

  @Override
  public Iterator<int[]> iterator() {
    return new KSubsetsIterator();
  }

}
