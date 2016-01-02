package org.matheclipse.combinatoric;

import org.matheclipse.combinatoric.util.ReadOnlyIterator;

/**
 * <p>
 * An iterator that generates all partitions of <code>n</code> elements, into <code>k</code> parts containing the number of elements in each
 * part, based on Rosen's algorithm.
 * </p>
 * <p>
 * See Kenneth H. Rosen, Discrete Mathematics and Its Applications, 2nd edition (NY: McGraw-Hill, 1991), pp. 284-286
 * </p>
 *
 * @author Alistair A. Israel
 */
public class RosenNumberPartitionIterator extends ReadOnlyIterator<int[]> {

    private final int n;
    private final int k;
    private final RosenIterator rosen;

    /**
     * @param n
     *            the number of elements
     * @param k
     *            divided into k parts
     */
    public RosenNumberPartitionIterator(final int n, final int k) {
        this.n = n;
        this.k = k;
        this.rosen = new RosenIterator(n - 1, k - 1);
    }

    /**
     * {@inheritDoc}
     *
     * @see java.util.Iterator#next()
     */
    public final int[] next() {
        final int[] actual = rosen.next();
        final int[] temp = new int[k];
        for (int i = 0; i < temp.length; i++) {
            if (i == 0) {
                temp[i] = actual[i] + 1;
            } else {
                if (i == temp.length - 1) {
                    temp[i] = n - 1 - actual[i - 1];
                } else {
                    temp[i] = actual[i] - actual[i - 1];
                }
            }
        }
        return temp;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.util.Iterator#hasNext()
     */
    public final boolean hasNext() {
        return rosen.hasNext();
    }


	public void reset() {
		rosen.reset();
	}
}