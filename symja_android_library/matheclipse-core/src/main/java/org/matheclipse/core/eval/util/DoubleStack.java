package org.matheclipse.core.eval.util;


public class DoubleStack {
	private double[] stack;

	int top;

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

	final public boolean isEmpty() {
		return top < 0;
	}

	final public double peek() {
		return stack[top];
	}

	final public double push(final double item) {
		stack[++top] = item;
		return item;
	}

	final public double pop() {
		return stack[top--];
	}

}
