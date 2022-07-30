package org.matheclipse.core.combinatoric;

import org.hipparchus.util.RosenNumberPartitionIterator;
import org.matheclipse.core.patternmatching.FlatOrderlessStepVisitor;

/**
 * Partition an ordered multi-set and visit all steps of the algorithm with a <code>IStepVisitor
 * </code>
 *
 * @see IStepVisitor
 */
public class MultisetPartitionsIterator {

  private final int n;
  private final int[] multiset;
  private final int[][] result;
  private RosenNumberPartitionIterator rosen;
  private int[] currentRosen;
  private final FlatOrderlessStepVisitor handler;

  /**
   * Partition an ordered multi-set and visit all steps of the algorithm with an <code>IStepVisitor
   * </code>.
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
  }

  public void reset() {
    rosen.reset();
    for (int i = 0; i < result.length; i++) {
      result[i] = null;
    }
    initPatternMap();
  }

  public void initPatternMap() {
    handler.initPatternMap();
  }

  public boolean execute() {
    while (rosen.hasNext()) {
      currentRosen = rosen.next();
      if (multisetCombinationRecursive(multiset, 0)) {
        return false;
      }
    }
    return true;
  }

  private boolean multisetCombinationRecursive(int[] multiset, int i) {
    if (i < currentRosen.length) {
      final MultisetCombinationIterator iter =
          new MultisetCombinationIterator(multiset, currentRosen[i]);
      while (iter.hasNext()) {
        final int[] currentSubset = iter.next();
        result[i] = currentSubset;
        int[] wc = ArrayUtils.deleteSubset(multiset, currentSubset);
        if (multisetCombinationRecursive(wc, i + 1)) {
          return true;
        }
      }
      return false;
    }
    return !handler.visit(result);
  }

  @Override
  public String toString() {
    return handler.toString(result);
    // StringBuilder buf = new StringBuilder();
    // for (int i = 0; i < result.length; i++) {
    // if (result[i] == null) {
    // buf.append("[null]");
    // } else {
    // buf.append("[");
    // for (int j = 0; j < result[i].length; j++) {
    // buf.append(result[i][j]);
    // if (j < result[i].length-1) {
    // buf.append(",");
    // }
    // }
    // buf.append("]");
    // }
    // }
    // buf.append('\n');
    // return buf.toString();
  }
}
