package org.matheclipse.core.generic.combinatoric;

import java.util.Iterator;
import java.util.List;

/**
 * Generate an Iterable for permutations
 * 
 * See <a href="http://en.wikipedia.org/wiki/Permutation">Permutation</a>
 */
public class KPermutationsIterable implements Iterator<int[]>, Iterable<int[]> {

	final private int n;

	final private int k;

	final private int fPermutationsIndex[];

	final private int y[];

	private boolean first;

	private int h, i, m;

	final private int fCopiedResultIndex[];

	private int fResultIndex[];

	/**
	 *
	 */
	public KPermutationsIterable(final int[] data, final int parts) {
		this(data, data.length, parts);
	}

	public KPermutationsIterable(final int[] data, final int len, final int parts) {
		super();
		n = len;
		k = parts;
		// f = fun;
		fPermutationsIndex = new int[n];
		y = new int[n];
		fCopiedResultIndex = new int[n];
		for (int a = 0; a < n; a++) {
			fPermutationsIndex[a] = data[a];
			y[a] = a;
		}
		if (k == n) {
			m = k - 1;
		} else {
			m = k;
		}
		first = true;
		i = m - 1;
		fResultIndex = nextBeforehand();
	}

	public <T> KPermutationsIterable(final List<T> fun, final int parts, final int headOffset) {
		super();
		n = fun.size() - headOffset;
		k = parts;

		fPermutationsIndex = new int[n];
		y = new int[n];
		fCopiedResultIndex = new int[n];
		fPermutationsIndex[0] = 0;
		y[0] = 0;
		for (int a = 1; a < n; a++) {
			if (fun.get(a + headOffset).equals(fun.get(a + headOffset - 1))) {
				fPermutationsIndex[a] = fPermutationsIndex[a - 1];
			} else {
				fPermutationsIndex[a] = a;
			}
			y[a] = a;
		}
		if (k == n) {
			m = k - 1;
		} else {
			m = k;
		}
		first = true;
		i = m - 1;
		fResultIndex = nextBeforehand();
	}

	/**
	 *
	 */
	private final int[] nextBeforehand() {
		if (first) {
			first = false;
			return fPermutationsIndex;
		}
		do {
			if (y[i] < (n - 1)) {
				y[i] = y[i] + 1;
				if (fPermutationsIndex[i] != fPermutationsIndex[y[i]]) {
					// check fixpoint
					h = fPermutationsIndex[i];
					fPermutationsIndex[i] = fPermutationsIndex[y[i]];
					fPermutationsIndex[y[i]] = h;
					i = m - 1;
					return fPermutationsIndex;
				}
				continue;
			}
			do {
				h = fPermutationsIndex[i];
				fPermutationsIndex[i] = fPermutationsIndex[y[i]];
				fPermutationsIndex[y[i]] = h;
				y[i] = y[i] - 1;
			} while (y[i] > i);
			i--;
		} while (i != -1);
		return null;
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
