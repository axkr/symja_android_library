package org.matheclipse.core.eval;

import java.util.Iterator;
import org.matheclipse.core.interfaces.IAST;

/**
 * Generate an <code>java.lang.Iterable</code> for (multiset) permutations
 *
 * <p>
 * See <a href="http://en.wikipedia.org/wiki/Permutation">Permutation</a>
 */
public final class KPermutationsIterable implements Iterable<int[]> {

  private final int n;

  private final int k;

  private final int fPermutationsIndex[];

  private final int y[];

  private boolean first;

  private int h, i, m;

  private final int fCopiedResultIndex[];

  private class KPermutationsIterator implements Iterator<int[]> {
    private int fResultIndex[];

    private KPermutationsIterator() {
      fResultIndex = nextBeforehand();
    }

    /** */
    private final int[] nextBeforehand() {
      if (first) {
        first = false;
        return fPermutationsIndex;
      }
      do {
        if (y[i] < (n - 1)) {
          y[i] = y[i] + 1;
          if (fPermutationsIndex[i] != fPermutationsIndex[y[i]]) {
            // check fixpoint
            h = fPermutationsIndex[i];
            fPermutationsIndex[i] = fPermutationsIndex[y[i]];
            fPermutationsIndex[y[i]] = h;
            i = m - 1;
            return fPermutationsIndex;
          }
          continue;
        }
        do {
          h = fPermutationsIndex[i];
          fPermutationsIndex[i] = fPermutationsIndex[y[i]];
          fPermutationsIndex[y[i]] = h;
          y[i] = y[i] - 1;
        } while (y[i] > i);
        i--;
      } while (i != -1);
      return null;
    }

    @Override
    public boolean hasNext() {
      return fResultIndex != null;
    }

    @Override
    public int[] next() {
      System.arraycopy(fResultIndex, 0, fCopiedResultIndex, 0, fResultIndex.length);
      fResultIndex = nextBeforehand();
      return fCopiedResultIndex;
    }
  }

  public KPermutationsIterable(final IAST fun, final int parts, final int headOffset) {
    super();
    n = fun.size() - headOffset;
    k = parts;
    if (parts > n || parts < 1) {
      throw new IllegalArgumentException("KPermutationsIterable: parts " + parts + " > " + n);
    }

    fPermutationsIndex = new int[n];
    y = new int[n];
    fCopiedResultIndex = new int[n];
    fPermutationsIndex[0] = 0;
    y[0] = 0;
    for (int a = 1; a < n; a++) {
      if (fun.get(a + headOffset).equals(fun.get(a + headOffset - 1))) {
        fPermutationsIndex[a] = fPermutationsIndex[a - 1];
      } else {
        fPermutationsIndex[a] = a;
      }
      y[a] = a;
    }
    if (k == n) {
      m = k - 1;
    } else {
      m = k;
    }
    first = true;
    i = m - 1;
  }

  /**
   * Create an iterator which gives all possible permutations of <code>data</code> which contains
   * at most <code>parts</code> number of elements. Repeated elements are treated as same.
   *
   * @param data a list of integers which should be permutated.
   * @param parts
   */
  public KPermutationsIterable(final int[] data, final int parts) {
    this(data, data.length, parts);
  }

  /**
   * Create an iterator which gives all possible permutations of <code>data</code> which contains
   * at most <code>parts</code> number of elements. Repeated elements are treated as same.
   *
   * @param data a list of integers which should be permutated.
   * @param len consider only the first <code>n</code> elements of <code>data</code> for
   *        permutation
   * @param parts
   */
  public KPermutationsIterable(final int[] data, final int len, final int parts) {
    super();
    n = len;
    k = parts;
    if (parts > n || parts < 1) {
      throw new IllegalArgumentException("KPermutationsIterable: parts " + parts + " > " + n);
    }
    fPermutationsIndex = new int[n];
    y = new int[n];
    fCopiedResultIndex = new int[n];
    for (int a = 0; a < n; a++) {
      fPermutationsIndex[a] = data[a];
      y[a] = a;
    }
    if (k == n) {
      m = k - 1;
    } else {
      m = k;
    }
    first = true;
    i = m - 1;
    // fResultIndex = nextBeforehand();
  }

  @Override
  public Iterator<int[]> iterator() {
    return new KPermutationsIterator();
  }

}