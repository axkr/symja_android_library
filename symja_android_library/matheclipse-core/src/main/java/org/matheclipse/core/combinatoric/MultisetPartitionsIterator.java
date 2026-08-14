package org.matheclipse.core.combinatoric;

import org.hipparchus.util.RosenNumberPartitionIterator;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.exception.FlowControlException;
import org.matheclipse.core.patternmatching.FlatOrderlessStepVisitor;

/**
 * Partition an ordered multi-set and visit all steps of the algorithm with a
 * {@link FlatOrderlessStepVisitor}
 *
 * @see FlatOrderlessStepVisitor
 */
public final class MultisetPartitionsIterator {

  /** StopException will be thrown, if maximum number of Cases results are reached */
  public static class StopException extends FlowControlException {
    private static final long serialVersionUID = -8839477630696222675L;

    public StopException() {
      super("Stop MultisetPartitionsIterator evaluation");
    }
  }

  private int iterationCounter = 0;
  private final int n;
  private final int[] multiset;
  private final int[][] result;
  private RosenNumberPartitionIterator rosen;
  private int[] currentRosen;
  private final FlatOrderlessStepVisitor handler;

  /**
   * Reusable per-level scratch state, allocated once and reused across every Rosen composition and
   * every DFS node so that {@link #multisetCombinationIterative()} performs no per-node array
   * allocation. {@code levelBuffer[d]} holds the (sorted) remaining multiset when descending to
   * level {@code d}, {@code levelLen[d]} its logical length, and {@code iters[d]} the combination
   * iterator over it.
   */
  private final int[][] levelBuffer;
  private final int[] levelLen;
  private final MultisetCombinationIterator[] iters;

  /**
   * Partition an ordered multi-set and visit all steps of the algorithm with an
   * {@link FlatOrderlessStepVisitor}
   *
   * @param visitor the visitor which controls the steps of the algorithm
   * @param k the number of partitioning the n elements into k parts
   */
  public MultisetPartitionsIterator(FlatOrderlessStepVisitor visitor, final int k) {
    int[] mset = visitor.getMultisetArray();
    this.n = mset.length;
    if (k > n || k < 1) {
      throw new IllegalArgumentException("MultisetPartitionsIterator: k " + k + " > " + n);
    }
    this.multiset = mset;
    this.result = new int[k][];
    this.rosen = new RosenNumberPartitionIterator(n, k);
    this.handler = visitor;

    // Preallocate the reusable DFS scratch state once. levelBuffer[0] aliases the full multiset
    // (read-only); levelBuffer[1..k] are filled in place while descending, replacing the fresh
    // arrays that ArrayUtils.deleteSubset used to allocate at every node.
    this.levelBuffer = new int[k + 1][];
    this.levelBuffer[0] = mset;
    for (int d = 1; d <= k; d++) {
      this.levelBuffer[d] = new int[n];
    }
    this.levelLen = new int[k + 1];
    this.levelLen[0] = n;
    this.iters = new MultisetCombinationIterator[k];
  }

  public boolean execute() {
    iterationCounter = 0;
    try {
      while (rosen.hasNext()) {
        currentRosen = rosen.next();
        if (multisetCombinationIterative()) {
          return false;
        }
      }
    } catch (StopException e) {
      //
    }
    return true;
  }

  private boolean multisetCombinationIterative() {
    final int k = currentRosen.length;
    if (k == 0) {
      if (n == 0) {
        if (Config.MAX_PATTERN_MATCHING_COMBINATIONS > 0
            && ++iterationCounter > Config.MAX_PATTERN_MATCHING_COMBINATIONS) {
          throw new StopException();
        }
        return !handler.visit(result);
      }
      return false;
    }

    // levelBuffer/levelLen/iters are preallocated (see constructor) and reused here, so this DFS
    // allocates no arrays per node. iters is cleared up front because an early-stopped previous run
    // may have left stale iterators (a run that completes normally unwinds them all back to null).
    for (int d = 0; d < k; d++) {
      iters[d] = null;
    }

    int i = 0;
    while (i >= 0) {
      if (i < k) {
        // Going forward
        MultisetCombinationIterator currentIter = iters[i];
        if (currentIter == null) {
          currentIter =
              new MultisetCombinationIterator(levelBuffer[i], levelLen[i], currentRosen[i]);
          iters[i] = currentIter;
        }

        if (currentIter.hasNext()) {
          if (Config.MAX_PATTERN_MATCHING_COMBINATIONS > 0
              && ++iterationCounter > Config.MAX_PATTERN_MATCHING_COMBINATIONS) {
            throw new StopException();
          }
          int[] currentSubset = currentIter.next();
          result[i] = currentSubset;
          // remaining multiset for the next level, written in place into the reused buffer
          levelLen[i + 1] =
              reduceInto(levelBuffer[i], levelLen[i], currentSubset, levelBuffer[i + 1]);
          i++;
        } else {
          // Backtrack
          iters[i] = null;
          i--;
        }
      } else { // i == k, found a partition
        if (Config.MAX_PATTERN_MATCHING_COMBINATIONS > 0
            && ++iterationCounter > Config.MAX_PATTERN_MATCHING_COMBINATIONS) {
          throw new StopException();
        }
        if (!handler.visit(result)) {
          return true; // Stop
        }
        // Backtrack from solution
        i--;
      }
    }
    return false; // Continue
  }

  /**
   * Remove the (sorted) <code>subset</code> from the first <code>srcLen</code> entries of the
   * (sorted) <code>src</code> multiset, writing the remaining elements into <code>dest</code> and
   * returning how many were written. <code>subset</code> is a sub-multiset of <code>src</code>; the
   * first matching occurrence of each value is removed (a two-pointer merge, since both are sorted
   * ascending). Allocation-free replacement for {@code ArrayUtils.deleteSubset}: <code>dest</code>
   * must have capacity at least <code>srcLen - subset.length</code> and must not alias
   * <code>src</code>.
   *
   * @return the number of elements written into <code>dest</code>
   */
  private static int reduceInto(int[] src, int srcLen, int[] subset, int[] dest) {
    final int subLen = subset.length;
    int di = 0;
    int si = 0;
    for (int i = 0; i < srcLen; i++) {
      final int v = src[i];
      if (si < subLen && v == subset[si]) {
        si++;
      } else {
        dest[di++] = v;
      }
    }
    return di;
  }

  @Override
  public String toString() {
    return handler.toString(result);
  }
}
