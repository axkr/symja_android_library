package org.matheclipse.core.combinatoric;

import java.util.Iterator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;

/**
 * This <code>Iterable</code> iterates through all k-partition lists for a given list with N
 * elements. <br>
 * See <a href="http://en.wikipedia.org/wiki/Partition_of_a_set">Wikipedia - Partition of a set</a>
 */
public final class KPartitionsList implements Iterable<IAST> {

  private final IAST fList;
  private final IAST fResultList;
  private final int fKParts;
  private final int fOffset;

  private class KPartitionsListIterator implements Iterator<IAST> {

    private final Iterator<int[]> fIterable;

    private KPartitionsListIterator() {
      this.fIterable = new KPartitionsIterable(fList.size() - fOffset, fKParts).iterator();
    }

    /**
     * Get the index array for the next partition.
     *
     * @return <code>null</code> if no further index array could be generated
     */
    @Override
    public IAST next() {
      int[] partitionsIndex = fIterable.next();
      if (partitionsIndex == null) {
        return null;
      }
      IASTAppendable part = fResultList.copyAppendable();
      int j = 0;
      for (int i = 1; i < partitionsIndex.length; i++) {
        IASTAppendable temp = fResultList.copyAppendable();
        for (int m = j; m < partitionsIndex[i]; m++) {
          temp.append(fList.get(m + fOffset));
        }
        j = partitionsIndex[i];
        part.append(temp);
      }

      IASTAppendable temp = fResultList.copyAppendable();
      int n = fList.size() - fOffset;
      for (int m = j; m < n; m++) {
        temp.append(fList.get(m + fOffset));
      }
      part.append(temp);
      return part;
    }

    @Override
    public boolean hasNext() {
      return fIterable.hasNext();
    }
  }

  public KPartitionsList(final IAST list, final int kParts, IAST resultList, final int offset) {
    super();
    this.fList = list;
    this.fKParts = kParts;
    this.fResultList = resultList;
    this.fOffset = offset;
  }

  @Override
  public Iterator<IAST> iterator() {
    return new KPartitionsListIterator();
  }
}