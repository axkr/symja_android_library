package org.matheclipse.combinatoric;

import java.util.Iterator;

/**
 *  
 */
public class RosenNumberPartitionIterator extends RosenIterator {

	/**
	 * @param n
	 *          the number of elements
	 * @param k
	 *          the number of partitioning the n elements into k parts
	 */
	public RosenNumberPartitionIterator(final int n, final int k) {
		super(n - 1, k - 1);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see Iterator#next()
	 */
	@Override
	public final int[] next() {
		int[] actual = super.next();
		final int[] temp = new int[k + 1];
		for (int i = 0; i < temp.length; i++) {
			if (i == 0) {
				temp[i] = actual[i] + 1;
			} else {
				if (i == temp.length - 1) {
					temp[i] = n - actual[i - 1];
				} else {
					temp[i] = actual[i] - actual[i - 1];
				}
			}
		}
		return temp;
	}

}