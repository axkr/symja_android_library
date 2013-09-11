package org.matheclipse.core.generic.combinatoric;

import java.util.Iterator;

/**
 * Iterable for all k-combinations from a set
 * 
 * See <a href="http://en.wikipedia.org/wiki/Combination">Combination</a>
 */
public class KSubsetsIterable implements Iterator<int[]>, Iterable<int[]> {

	final private int n;
	final private int k;
	final private int x[];
	private long bin;
	private boolean first;

	public KSubsetsIterable(final int len, final int parts) {
		super();
		n = len;
		k = parts;
		//f = fun;
		x = new int[n];
		for (int a = 0; a < n; a++) {
			x[a] = a;
		}
		bin = binomial(n, k);
		first = true;
	}

	public int[] next() {
		if (bin-- == 0) {
			return null;
		}
		if (first) {
			first = false;
			return x;
		}
		int i = k - 1;
		while (x[i] == n - k + i) {
			i--;
		}
		x[i] = x[i] + 1;
		for (int j = i + 1; j < n; j++) {
			x[j] = x[j - 1] + 1;
		}
		return x;
	}

	public static long binomial(final long n, final long k) {
		long bin = 1;
		long kSub = k;
		if (kSub > (n / 2)) {
			kSub = n - kSub;
		}
		for (long i = 1; i <= kSub; i++) {
			bin = (bin * (n - i + 1)) / i;
		}
		return bin;
	}

	public boolean hasNext() {
		return true;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

	public Iterator<int[]> iterator() {
		return this;
	}
}
