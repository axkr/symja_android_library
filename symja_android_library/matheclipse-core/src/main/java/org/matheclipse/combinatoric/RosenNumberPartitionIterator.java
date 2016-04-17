package org.matheclipse.combinatoric;

import org.matheclipse.combinatoric.util.ReadOnlyIterator;

/**
 * <p>
 * An iterator that generates all partitions of <code>n</code> elements, into
 * <code>k</code> parts containing the number of elements in each part, based on
 * Rosen's algorithm.
 * </p>
 * <p>
 * 
 * <pre>
 * public static void main(String[] args) {
 * 	RosenNumberPartitionIterator i = new RosenNumberPartitionIterator(10, 3);
 * 	while (i.hasNext()) {
 * 		int[] t = i.next();
 * 		for (int j = 0; j < t.length; j++) {
 * 			System.out.print(t[j]);
 * 			System.out.print(" ");
 * 		}
 * 		System.out.println();
 * 
 * 	}
 * }
 * </pre>
 * </p>
 * <p>
 * See Kenneth H. Rosen, Discrete Mathematics and Its Applications, 2nd edition
 * (NY: McGraw-Hill, 1991), pp. 284-286
 * </p>
 *
 */
public class RosenNumberPartitionIterator extends ReadOnlyIterator<int[]> {

	/**
	 * Computes the number of unique combinations of <code>n</code> elements
	 * taken <code>k</code> at a time, which can be computed as:
	 * <code>n! / k! (n - k)!</code>
	 * 
	 * @param n
	 *            the number of elements
	 * @param k
	 *            subset/sample size
	 * @return the number of combinations of <code>n</code> elements taken
	 *         <code>k</code> at a time
	 */
	public static long count(final int n, final int k) {
		if (k < 0 || k > n) {
			throw new IllegalArgumentException("0 <= k <= " + n + "!");
		}
		long count = 1;
		for (int i = 0; i < k; ++i) {
			count = count * (n - i) / (i + 1);
		}
		return count;
	}

	protected final int n;

	protected final int k;

	private int[] a;

	protected long count;

	/**
	 * @param n
	 *            the number of elements
	 * @param k
	 *            divided into k parts
	 */
	public RosenNumberPartitionIterator(final int n, final int k) {
		this.n = n - 1;
		this.k = k - 1;
		this.count = count(this.n, this.k);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public final boolean hasNext() {
		return count > 0;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.util.Iterator#next()
	 */
	@Override
	public final int[] next() {
		// rosenNext start
		if (a == null) {
			this.a = ArrayUtils.identityPermutation(k);
		} else {
			int i = k - 1;
			while (a[i] == n - k + i) {
				i--;
			}
			final int t = ++a[i] - i++;
			int j = i;
			while (j < k) {
				a[j] = t + j++;
			}
		}
		--count;
		// rosenNext end

		final int kPlus1 = k + 1;
		final int[] temp = new int[kPlus1];

		for (int i = 0; i < kPlus1; i++) {
			if (i == 0) {
				temp[i] = a[i] + 1;
			} else {
				if (i == k) {
					temp[i] = n - a[i - 1];
				} else {
					temp[i] = a[i] - a[i - 1];
				}
			}
		}
		return temp;
	}

	/**
	 * Reset this iterator to the start condition.
	 */
	public void reset() {
		this.count = count(n, k);
		a = null;
	}

}