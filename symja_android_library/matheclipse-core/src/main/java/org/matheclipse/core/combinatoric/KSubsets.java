package org.matheclipse.core.combinatoric;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class KSubsets {

  /**
   * Iterable for all k-combinations from a set
   *
   * <p>
   * See <a href="http://en.wikipedia.org/wiki/Combination">Combination</a>
   */
  public static final class KSubsetsIterable implements Iterable<int[]> {

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
          return null;
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
        return true;
      }
    }

    public KSubsetsIterable(final int len, final int parts) {
      super();
      n = len;
      k = parts;
      if (k > n || k < 0) {
        throw new IllegalArgumentException("KSubsets: k>n - " + k + " > " + n);
      }

      bin = binomial(n, k);
      first = true;
    }

    @Override
    public Iterator<int[]> iterator() {
      return new KSubsetsIterator();
    }
  }

  /**
   * Iterate over the lists of all k-combinations from a given list
   *
   * <p>
   * See <a href="http://en.wikipedia.org/wiki/Combination">Combination</a>
   */
  public static final class KSubsetsList<E, T extends List<E>> implements Iterable<T> {

    private final T fList;
    private final int fOffset;
    private final int fK;

    private class KSubsetsListIterator implements Iterator<T> {
      private final Iterator<int[]> fIterable;

      private KSubsetsListIterator() {
        fIterable = new KSubsetsIterable(fList.size() - fOffset, fK).iterator();
      }

      /**
       * Get the index array for the next partition.
       *
       * @return <code>null</code> if no further index array could be generated
       */
      @Override
      public T next() {
        int j[] = fIterable.next();
        if (j == null) {
          return null;
        }

        T temp = (T) new ArrayList<E>(fK);
        for (int i = 0; i < fK; i++) {
          temp.add(fList.get(j[i] + fOffset));
        }

        return temp;
      }

      @Override
      public boolean hasNext() {
        return fIterable.hasNext();
      }
    }

    public KSubsetsList(final T list, final int k, final int offset) {
      fList = list;
      fK = k;
      fOffset = offset;
    }

    @Override
    public Iterator<T> iterator() {
      return new KSubsetsListIterator();
    }
  }

  public static <E, T extends List<E>> KSubsetsList<E, T> createKSubsets(final T list, final int k,
      final int offset) {
    return new KSubsetsList<E, T>(list, k, offset);
  }

  public static long binomial(final long n, final long k) {
    long bin = 1;
    long kSub = k;
    if (kSub > (n / 2)) {
      kSub = n - kSub;
    }
    for (long i = 1; i <= kSub; i++) {
      bin = (bin * (n - i + 1)) / i;
    }
    return bin;
  }
}
