package org.matheclipse.core.combinatoric;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Combinations of a multiset
 *
 * <p>
 * These are the combinations of <code>k</code> elements chosen from a sorted <code>int[]</code>
 * array, that can contain duplicates (a multiset).
 *
 * <p>
 * For example, given the multiset <code>{0, 1, 1, 2, 2, 2, 3}</code>, the 4-combinations are:
 *
 * <pre>
 * [0, 1, 1, 2]
 * [0, 1, 1, 3]
 * [0, 1, 2, 2]
 * [0, 1, 2, 3]
 * [0, 2, 2, 2]
 * [0, 2, 2, 3]
 * [1, 1, 2, 2]
 * [1, 1, 2, 3]
 * [1, 2, 2, 2]
 * [1, 2, 2, 3]
 * [2, 2, 2, 3]
 * </pre>
 *
 * <p>
 * This algorithm produces the combinations in lexicographic order, with the elements in each
 * combination in increasing order.
 *
 * <p>
 * Begin with the first combination, which is the first <var>k</var> elements of the multiset ([0,
 * 1, 1, 2] in the example above), and then at each step:
 *
 * <ol>
 * <li>Find the rightmost element that is less than the maximum value it can have (which is the
 * element in the multiset that is the same distance from the right).
 * <li>Replace it with the first multiset element greater than it.
 * <li>Replace the remainder of the combination with the elements that follow the replacement in the
 * multiset.
 * </ol>
 *
 * <p>
 * See: <a href=
 * "http://www.martinbroadhurst.com/combinatorial-algorithms.html#combinations-of-a-multiset"
 * >martinbroadhurst.com Combinations of a Multiset</a>
 */
public class MultisetCombinationIterator implements Iterator<int[]> {

  private int[] result;
  private final int[] multiset;
  private final int n;
  private final int k;
  private boolean firstRun;

  /**
   * Combinations of a multiset
   *
   * <p>
   * These are the combinations of <code>k</code> elements chosen from a sorted <code>int[]
   * </code> array, that can contain duplicates (a multiset).
   */
  public MultisetCombinationIterator(int[] multiset, final int k) {
    this.multiset = multiset;
    this.n = multiset.length;
    this.k = k;
    if (k > n || k < 1) {
      throw new IllegalArgumentException("MultisetCombinationIterator: k " + k + " > " + n);
    }
    this.result = new int[k];
    reset();
  }

  public void reset() {
    System.arraycopy(multiset, 0, result, 0, k);
    this.firstRun = true;
  }

  /**
   * {@inheritDoc}
   *
   * @see Iterator#next()
   */
  @Override
  public int[] next() {
    if (firstRun) {
      firstRun = false;
      return result;
    }
    for (int i = k - 1; i >= 0; i--) {
      if (result[i] < multiset[i + (n - k)]) {
        // Find the successor
        int j;
        for (j = 0; multiset[j] <= result[i]; j++) {
        }

        // Replace this element with it
        result[i] = multiset[j];
        if (i < k - 1) {
          // Make the elements after it the same as this part of the multiset
          int l;
          for (l = i + 1, j = j + 1; l < k; l++, j++) {
            result[l] = multiset[j];
          }
        }
        return result;
      }
    }
    throw new NoSuchElementException();
  }

  /**
   * {@inheritDoc}
   *
   * @see Iterator#hasNext()
   */
  @Override
  public final boolean hasNext() {
    if (!firstRun) {
      for (int i = k - 1; i >= 0; i--) {
        if (result[i] < multiset[i + (n - k)]) {
          return true;
        }
      }
    }
    return firstRun;
  }
}
