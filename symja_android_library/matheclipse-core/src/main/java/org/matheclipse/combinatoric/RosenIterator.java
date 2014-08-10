package org.matheclipse.combinatoric;

import java.util.Iterator;

import org.matheclipse.combinatoric.util.ArrayUtils;

/**
 * <p>
 * An iterator that generates combinations of <code>n</code> elements,
 * <code>C(n)</code>, in lexicographic order, based on Rosen's algorithm.
 * </p>
 * 
 * @author Alistair A. Israel
 * @see "Kenneth H. Rosen, Discrete Mathematics and Its Applications, 2nd
 *      edition (NY: McGraw-Hill, 1991), pp. 284-286"
 */
public class RosenIterator implements Iterator<int[]> {

	protected final int n;

	protected final int k;

	private int[] a;

	protected long count;

	/**
	 * @param n
	 *          the number of elements
	 * @param k
	 *          taken k at a time
	 */
	public RosenIterator(final int n, final int k) {
		super();
		this.n = n;
		this.k = k;
		this.count = count(n, k);
	}

	public void reset() {
		this.count = count(n, k);
		a = null;
	}

	/**
	 * Computes the number of unique combinations of <code>n</code> elements taken
	 * <code>k</code> at a time, which can be computed as:
	 * <code>n! / k! (n - k)!</code>
	 * 
	 * @param n
	 *          the number of elements
	 * @param k
	 *          subset/sample size
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

	/**
	 * {@inheritDoc}
	 * 
	 * @see Iterator#hasNext()
	 */
	public final boolean hasNext() {
		return count > 0;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see Iterator#next()
	 */
	public int[] next() {
		if (a == null) {
			initialize();
		} else {
			int i = k - 1;
			while (a[i] == n - k + i) {
				i--;
			}
			a[i] = a[i] + 1;
			for (int j = i + 1; j < k; j++) {
				a[j] = a[i] + j - i;
			}
		}
		--count;
		return a;
	}

	/**
*
*/
	private void initialize() {
		this.a = ArrayUtils.identityPermutation(k);
	}
 
	/**
	 * @author Alistair A. Israel
	 */
	public static class Factory implements Iterable<int[]> {

		private final int n;

		private final int k;

		/**
		 * @param n
		 *          the number of elements
		 * @param k
		 *          taken k at a time
		 */
		public Factory(final int n, final int k) {
			this.n = n;
			this.k = k;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.lang.Iterable#iterator()
		 */
		public final Iterator<int[]> iterator() {
			return new RosenIterator(n, k);
		}

	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("This iterator doesn't support the remove() method");
	}
}