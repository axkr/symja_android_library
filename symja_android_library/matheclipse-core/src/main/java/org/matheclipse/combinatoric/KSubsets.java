package org.matheclipse.combinatoric;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class KSubsets {
	/**
	 * Iterable for all k-combinations from a set
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Combination">Combination</a>
	 */
	public final static class KSubsetsIterable implements Iterator<int[]>, Iterable<int[]> {

		final private int n;
		final private int k;
		final private int x[];
		private long bin;
		private boolean first;

		public KSubsetsIterable(final int len, final int parts) {
			super();
			n = len;
			k = parts;
			// f = fun;
			x = new int[n];
			for (int a = 0; a < n; a++) {
				x[a] = a;
			}
			bin = binomial(n, k);
			first = true;
		}

		@Override
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

		@Override
		public boolean hasNext() {
			return true;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Iterator<int[]> iterator() {
			return this;
		}
	}

	/**
	 * Iterate over the lists of all k-combinations from a given list
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Combination">Combination</a>
	 */
	public final static class KSubsetsList<E, T extends List<E>> implements Iterator<T>, Iterable<T> {

		final private T fList;
		final private int fOffset;
		final private Iterator<int[]> fIterable;
		final private int fK;

		public KSubsetsList(final Iterator<int[]> iterable, final T list, final int k) {
			this(iterable, list, k, 0);
		}

		public KSubsetsList(final Iterator<int[]> iterable, final T list, final int k, final int offset) {
			fIterable = iterable;
			fList = list;
			fK = k;
			fOffset = offset;
		}

		/**
		 * Get the index array for the next partition.
		 * 
		 * @return <code>null</code> if no further index array could be generated
		 */
		@Override
		public T next() {
			int j[] = fIterable.next();
			if (j == null) {
				return null;
			}

			T temp = (T) new ArrayList<E>(fK);
			for (int i = 0; i < fK; i++) {
				temp.add(fList.get(j[i] + fOffset));
			}

			return temp;
		}

		@Override
		public boolean hasNext() {
			return fIterable.hasNext();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Iterator<T> iterator() {
			return this;
		}

	}

	public static <E, T extends List<E>> KSubsetsList<E, T> createKSubsets(final T list, final int k,
			final int offset) {
		return new KSubsetsList<E, T>(new KSubsetsIterable(list.size() - offset, k), list, k, offset);
	}
}
