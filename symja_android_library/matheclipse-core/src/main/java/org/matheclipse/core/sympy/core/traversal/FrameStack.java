package org.matheclipse.core.sympy.core.traversal;

import java.util.Arrays;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The explicit stack of the iterative tree walks in this package. One frame is a node, a second
 * {@link IAST} per node, and the position of the next child.
 *
 * <p>
 * The three parallel arrays need no allocation per node, which is what a stack of
 * {@code Iterator}s or of frame objects would cost. Pushing a node once instead of pushing all of
 * its children bounds the memory by the depth of the tree and not by its width, so the
 * {@code Plus} with a million summands costs one frame.
 */
final class FrameStack {
  private static final int INITIAL_CAPACITY = 32;

  /** The nodes of the current path from the root. */
  IAST[] nodes = new IAST[INITIAL_CAPACITY];

  /**
   * The second node of a frame: the lazy copy of the rewriting walk, or the argument order of the
   * iterators.
   */
  IAST[] aux = new IAST[INITIAL_CAPACITY];

  /** The position of the next child for each frame. */
  int[] index = new int[INITIAL_CAPACITY];

  /**
   * The argument a rewriting walk is working on below each frame. <code>get()</code> can't answer
   * that question later: a packed vector or matrix builds a new object in each call, which then
   * looks like a changed argument.
   */
  IExpr[] child = new IExpr[INITIAL_CAPACITY];

  /** The index of the top frame, or <code>-1</code> if the stack is empty. */
  int top = -1;

  void push(IAST node, IAST second, int position) {
    if (++top == nodes.length) {
      final int capacity = top * 2;
      nodes = Arrays.copyOf(nodes, capacity);
      aux = Arrays.copyOf(aux, capacity);
      index = Arrays.copyOf(index, capacity);
      child = Arrays.copyOf(child, capacity);
    }
    nodes[top] = node;
    aux[top] = second;
    index[top] = position;
  }

  /** Remove the top frame and release its references. */
  void pop() {
    nodes[top] = null;
    aux[top] = null;
    child[top] = null;
    top--;
  }

  boolean isEmpty() {
    return top < 0;
  }
}
