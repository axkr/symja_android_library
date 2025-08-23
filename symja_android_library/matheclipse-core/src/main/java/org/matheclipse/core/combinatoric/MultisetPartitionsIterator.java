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

/**
 * A stack implementation for {@link MultisetCombinationIterator}s.
 *
 */
private final static class PartitioningStack<T> {
  final private T[] array;
  private int size;

  public PartitioningStack(int capacity) {
    this.array = (T[]) new Object[capacity];
    this.size = 0;
  }

  public void push(T element) {
    // if (size == array.length) {
    // throw new IllegalStateException("Stack is full");
    // }
    array[size++] = element;
  }

  public T pop() {
    // if (isEmpty()) {
    // throw new NoSuchElementException("Stack is empty");
    // }
    T element = array[--size];
    array[size] = null; // Avoid memory leaks
    return element;
  }

  public T peek() {
    // if (isEmpty()) {
    // throw new NoSuchElementException("Stack is empty");
    // }
    return array[size - 1];
  }

  public boolean isEmpty() {
    return size == 0;
  }

  public int size() {
    return size;
  }

  // public T get(int index) {
  // if (index < 0 || index >= size) {
  // throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
  // }
  // return array[index];
  // }
  //
  // public void set(int index, T element) {
  // if (index < 0 || index > size) {
  // throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
  // }
  // if (index == size) {
  // push(element);
  // } else {
  // array[index] = element;
  // }
  // }
}


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
    int k = currentRosen.length;
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
    PartitioningStack<MultisetCombinationIterator> iteratorStack = new PartitioningStack<>(k);
    PartitioningStack<int[]> multisetStack = new PartitioningStack<>(k + 1);
    multisetStack.push(this.multiset);

    int i = 0;
    while (i >= 0) {
      if (i < k) {
        // Going forward
        if (iteratorStack.size() <= i) {
          int[] currentMultiset = multisetStack.peek();
          MultisetCombinationIterator iter =
              new MultisetCombinationIterator(currentMultiset, currentRosen[i]);
          iteratorStack.push(iter);
        }

        MultisetCombinationIterator currentIter = iteratorStack.peek();
        if (currentIter.hasNext()) {
          if (Config.MAX_PATTERN_MATCHING_COMBINATIONS > 0
              && ++iterationCounter > Config.MAX_PATTERN_MATCHING_COMBINATIONS) {
            throw new StopException();
          }
          int[] currentSubset = currentIter.next();
          result[i] = currentSubset;
          int[] parentMultiset = multisetStack.peek();
          int[] remainingMultiset = ArrayUtils.deleteSubset(parentMultiset, currentSubset);
          multisetStack.push(remainingMultiset);
          i++;
        } else {
          // Backtrack
          iteratorStack.pop();
          multisetStack.pop();
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
        multisetStack.pop();
        i--;
      }
    }
    return false; // Continue
  }

  @Override
  public String toString() {
    return handler.toString(result);
  }
}