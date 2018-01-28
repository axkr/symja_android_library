package org.matheclipse.combinatoric;

import java.util.List;

import org.matheclipse.core.interfaces.IAST;

/**
 * Abstract step visitor for <code>java.util.List</code>s.
 * 
 */
public abstract class AbstractListStepVisitor<T> implements IStepVisitor {
	protected final IAST list;
	protected Object[] array;

	public AbstractListStepVisitor(IAST ast) {
		this.list = ast;
		toIntArray(ast);
	} 

	/**
	 * Convert the <code>sortedList</code> to an <code>int[]</code> array. Equal elements get the same index in the
	 * resulting <code>int[]</code> array.
	 * 
	 * @param <T>
	 * @param sortedList
	 * @param start
	 * @param end
	 * @return
	 */
	final private void toIntArray(IAST sortedList) {
		array = new Object[sortedList.argSize()];
		int index = 0;
		for (int i = 1; i < sortedList.size(); i++) {
			array[index++] = sortedList.get(i);
		}
	}

	/**
	 * Convert the <code>sortedList</code> to an <code>int[]</code> array. Equal elements get the same index in the
	 * resulting <code>int[]</code> array.
	 * 
	 * @param <T>
	 * @param sortedList
	 * @param start
	 * @param end
	 * @return
	 */
	final private void toIntArray(List<? extends T> sortedList, int start, int end) {
		array = new Object[end - start];
		int index = 0;
		for (int i = start; i < end; i++) {
			array[index++] = sortedList.get(i);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.matheclipse.combinatoric.IStepVisitor#visit(int[][])
	 */
	@Override
	public boolean visit(int[][] result) {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.matheclipse.combinatoric.IStepVisitor#visit(int[][])
	 */
	@Override
	public boolean visit(int[] result) {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.matheclipse.combinatoric.IStepVisitor#getMultisetArray()
	 */
	@Override
	public int[] getMultisetArray() {
		int[] result = new int[array.length];
		int counter = 0;
		for (int i = 0; i < result.length; i++) {
			result[i] = counter++;
		}
		return result;
	}
}
