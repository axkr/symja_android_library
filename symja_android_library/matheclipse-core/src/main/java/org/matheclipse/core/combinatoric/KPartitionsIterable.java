package org.matheclipse.core.combinatoric;

import java.util.Iterator;

/**
 * This class returns the indexes for partitioning a list of N elements. <br>
 * Usage pattern:
 *
 * <pre>
 * final KPartitionsIterable iter = new KPartitionsIterable(n, k);
 * for (int[] partitionsIndex : iter) {
 *   ...
 * }
 * </pre>
 *
 * Example: KPartitionsIterable(3,5) gives the following sequences [0, 1, 2], [0, 1, 3], [0, 1, 4],
 * [0, 2, 3], [0, 2, 4], [0, 3, 4] <br>
 * If you interpret these integer lists as indexes for a list {a,b,c,d,e} which should be
 * partitioned into 3 parts the results are: <br>
 * {{{a},{b},{c,d,e}}, {{a},{b,c},{d,e}}, {{a},{b,c,d},{e}}, {{a,b},{c},{d,e}}, {{a,b},{c,d},{e}},
 * {{a,b,c},{d},{e}}} <br>
 * <br>
 * See <a href="http://en.wikipedia.org/wiki/Partition_of_a_set">Wikipedia - Partition of a set</a>
 */
public final class KPartitionsIterable implements Iterable<int[]> {

  private final int fLength;

  private final int fNumberOfParts;

  private final int fPartitionsIndex[];

  private final int fCopiedResultIndex[];

  private class KPartitionsIterator implements Iterator<int[]> {

    private int fResultIndex[];

    private KPartitionsIterator() {
      fResultIndex = nextBeforehand();
    }

    /**
     * Get the index array for the next partition.
     *
     * @return <code>null</code> if no further index array could be generated
     */
    @Override
    public int[] next() {
      System.arraycopy(fResultIndex, 0, fCopiedResultIndex, 0, fResultIndex.length);
      fResultIndex = nextBeforehand();
      return fCopiedResultIndex;
    }

    @Override
    public boolean hasNext() {
      return fResultIndex != null;
    }

    /**
     * Get the index array for the next partition.
     *
     * @return <code>null</code> if no further index array could be generated
     */
    private final int[] nextBeforehand() {
      if (fPartitionsIndex[0] < 0) {
        for (int i = 0; i < fNumberOfParts; ++i) {
          fPartitionsIndex[i] = i;
        }
        return fPartitionsIndex;
      } else {
        int i = 0;
        for (i = fNumberOfParts - 1; (i >= 0)
            && (fPartitionsIndex[i] >= fLength - fNumberOfParts + i); --i) {
        }
        if (i <= 0) {
          return null;
        }
        fPartitionsIndex[i]++;
        for (int m = i + 1; m < fNumberOfParts; ++m) {
          fPartitionsIndex[m] = fPartitionsIndex[m - 1] + 1;
        }
        return fPartitionsIndex;
      }
    }
  }

  public KPartitionsIterable(final int length, final int parts) {
    super();
    if (parts > length || parts < 1) {
      throw new IllegalArgumentException("KPartitionsIterable: parts " + parts + " > " + length);
    }
    fLength = length;
    fNumberOfParts = parts;
    fPartitionsIndex = new int[fNumberOfParts];
    fCopiedResultIndex = new int[fNumberOfParts];
    fPartitionsIndex[0] = -1;
  }

  @Override
  public Iterator<int[]> iterator() {
    return new KPartitionsIterator();
  }
}



