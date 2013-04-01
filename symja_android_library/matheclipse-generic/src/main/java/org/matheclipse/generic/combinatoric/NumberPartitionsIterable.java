package org.matheclipse.generic.combinatoric;

import java.util.Iterator;

/**
 * Returns all partitions of a given int number (i.e. NumberPartitions(3) =>
 * [3,0,0] [2,1,0] [1,1,1] ).
 * 
 * See <a href="http://en.wikipedia.org/wiki/Integer_partition">Wikipedia -
 * Integer partition</a>
 */
public class NumberPartitionsIterable implements Iterator<int[]>, Iterable<int[]> {

	final private int n;

	final private int len;

	final private int fPartititionsIndex[];

	private int i;

	private int k;

	final private int fCopiedResultIndex[];

	private int fResultIndex[];

	/**
	 * 
	 */
	public NumberPartitionsIterable(final int num) {
		this(num, num);
	}

	public NumberPartitionsIterable(final int num, final int l) {
		super();
		n = num;
		len = l;
		int size = n;
		if (len > n) {
			size = len;
		}
		fPartititionsIndex = new int[size];
		fCopiedResultIndex = new int[size];
		for (int i = 0; i < size; i++) {
			fPartititionsIndex[i] = 0;
		}
		fResultIndex = nextBeforehand();
	}

	/**
	 * 
	 */
	private final int[] nextBeforehand() {
		int l;
		int k1;
		if (i == -1) {
			return null;
		}
		if (fPartititionsIndex[0] != 0) {
			k1 = k;
			while (fPartititionsIndex[k1] == 1) {
				fPartititionsIndex[k1--] = 0;
			}
			while (true) {
				l = k - i;
				k = i;
				// if (i != len - 1) {
				fPartititionsIndex[i] -= 1;
				// }
				while (fPartititionsIndex[k] <= l) {
					l = l - fPartititionsIndex[k++];
					fPartititionsIndex[k] = fPartititionsIndex[k - 1];
				}
				if (k != n - 1) {
					fPartititionsIndex[++k] = l + 1;
					if (fPartititionsIndex[i] != 1) {
						i = k;
					}
					if (fPartititionsIndex[i] == 1) {
						i--;
					}

				} else {
					k++;
					// if (i == len - 1) {
					// l += 1;
					// i--;
					// continue;
					// }
					if (fPartititionsIndex[i] != 1) {
						i = k;
					}
					if (fPartititionsIndex[i] == 1) {
						i--;
					}
					continue;
				}

				return fPartititionsIndex;
			}
		} else {
			fPartititionsIndex[0] = n;

			k = 0;
			i = 0;
		}
		return fPartititionsIndex;
	}

	public int[] next() {
		System.arraycopy(fResultIndex, 0, fCopiedResultIndex, 0, fResultIndex.length);
		fResultIndex = nextBeforehand();
		return fCopiedResultIndex;
	}

	public boolean hasNext() {
		return fResultIndex != null;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

	public Iterator<int[]> iterator() {
		return this;
	}
}
