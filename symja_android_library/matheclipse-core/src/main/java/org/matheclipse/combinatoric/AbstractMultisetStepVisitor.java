package org.matheclipse.combinatoric;

import java.util.List;

/**
 * Abstract step visitor for multisets of type <code>T</code>.
 * 
 */
public abstract class AbstractMultisetStepVisitor<T> extends AbstractListStepVisitor<T> {
	protected int[] multiset;

	public AbstractMultisetStepVisitor(List<? extends T> sortedList) {
		this(sortedList, 0, sortedList.size());
	}

	public AbstractMultisetStepVisitor(List<? extends T> sortedList, int start) {
		this(sortedList, start, sortedList.size());
	}

	public AbstractMultisetStepVisitor(List<? extends T> sortedList, int start, int end) {
		super(sortedList,start,end);
		toIntArray(sortedList, start, end);
	}

	/**
	 * Convert the <code>sortedList</code> to an <code>int[]</code> array. Equal
	 * elements get the same index in the resulting <code>int[]</code> array.
	 * 
	 * @param <T>
	 * @param sortedList
	 * @param start
	 * @param end
	 * @return
	 */
	final private void toIntArray(List<? extends T> sortedList, int start, int end) {
		multiset = new int[end - start];
		array = new Object[end - start];
		T lastT = sortedList.get(start);
		T currentT;
		int index = 0;
		int j = 0;
		multiset[j++] = index;
		array[index] = lastT;
		for (int i = start + 1; i < end; i++) {
			currentT = sortedList.get(i);
			if (currentT.equals(lastT)) {
				multiset[j++] = index;
			} else {
				multiset[j++] = ++index;
				array[index] = currentT;
			}
			lastT = currentT;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.matheclipse.combinatoric.IStepVisitor#visit(int[][])
	 */
	// public boolean visit(int[][] result) {
	// // print the result to console
	// for (int j = 0; j < result.length; j++) {
	// System.out.print("[");
	// for (int i = 0; i < result[j].length; i++) {
	// System.out.print(array[result[j][i]].toString());
	// System.out.print(",");
	// }
	// System.out.print("], ");
	// }
	// System.out.println("");
	// return true;
	// }

	@Override
	public int[] getMultisetArray() {
		return multiset;
	}
}
