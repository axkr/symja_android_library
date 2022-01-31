package org.matheclipse.core.eval.util;

/**
 * The Stack class represents a last-in-first-out (LIFO) stack of <code>double</code> values. The
 * usual push and pop operations are provided, as well as a method to peek at the top item on the
 * stack, a method to test for whether the stack is empty.
 *
 * <p>
 * When a stack is first created, it contains no items.
 */
public class DoubleStack {
  private double[] stack;

  int top;

  /**
   * Creates an empty Stack.
   *
   * @param initialCapacity the initial capacity which should be allocated for this stack.
   */
  public DoubleStack(final int initialCapacity) {
    stack = new double[initialCapacity];
    top = -1;
  }

  public void ensureCapacity(final int minCapacity) {
    final int oldCapacity = stack.length;
    if (minCapacity > oldCapacity) {
      final double oldData[] = stack;
      int newCapacity = (oldCapacity * 3) / 2 + 1;
      if (newCapacity < minCapacity) {
        newCapacity = minCapacity;
      }
      stack = new double[newCapacity];
      System.arraycopy(oldData, 0, stack, 0, oldCapacity);
    }
  }

  /**
   * Tests if this stack is empty.
   *
   * @return
   */
  public final boolean isEmpty() {
    return top < 0;
  }

  /**
   * Looks at the object at the top of this stack without removing it from the stack.
   *
   * @return
   */
  public final double peek() {
    return stack[top];
  }

  /**
   * Pushes an item onto the top of this stack.
   *
   * @param item
   * @return
   * @throws ArrayIndexOutOfBoundsException if no more capacity is available
   */
  public final double push(final double item) {
    stack[++top] = item;
    return item;
  }

  /**
   * Removes the object at the top of this stack and returns that object as the value of this
   * function. s
   *
   * @return
   */
  public final double pop() {
    return stack[top--];
  }
}
