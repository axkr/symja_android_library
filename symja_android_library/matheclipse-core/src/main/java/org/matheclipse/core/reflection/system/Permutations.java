package org.matheclipse.core.reflection.system;

import java.util.Iterator;
import java.util.List;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Generate a list of permutations
 * 
 * See <a href=" http://en.wikipedia.org/wiki/Permutation">Permutation</a>
 * 
 * @see Partition
 * @see Subsets
 */
public class Permutations extends AbstractFunctionEvaluator {

	/**
	 * Generate an Iterable for permutations
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Permutation">Permutation</a>
	 */
	public final static class KPermutationsIterable implements Iterator<int[]>, Iterable<int[]> {

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

		@Override
		public int[] next() {
			System.arraycopy(fResultIndex, 0, fCopiedResultIndex, 0, fResultIndex.length);
			fResultIndex = nextBeforehand();
			return fCopiedResultIndex;
		}

		@Override
		public boolean hasNext() {
			return fResultIndex != null;
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
	 * Generate a list of permutations
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Permutation">Permutation</a>
	 */
	public final static class KPermutationsList implements Iterator<IAST>, Iterable<IAST> {

		final private IAST fList;
		final private IAST fResultList;
		final private int fOffset;
		final private KPermutationsIterable fIterable;

		public KPermutationsList(final IAST list, final int parts, IAST resultList) {
			this(list, parts, resultList, 0);
		}

		public KPermutationsList(final IAST list, final int parts, IAST resultList, final int offset) {
			fIterable = new KPermutationsIterable(list, parts, offset);
			fList = list;
			fResultList = resultList;
			fOffset = offset;
		}

		/**
		 * Get the index array for the next permutation.
		 * 
		 * @return <code>null</code> if no further index array could be generated
		 */
		@Override
		public IAST next() {
			int[] permutationsIndex = fIterable.next();
			if (permutationsIndex == null) {
				return null;
			}
			IAST temp = fResultList.clone();
			for (int i = 0; i < permutationsIndex.length; i++) {
				temp.add(fList.get(permutationsIndex[i] + fOffset));
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
		public Iterator<IAST> iterator() {
			return this;
		}

	}

	public Permutations() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 3);

		if (ast.arg1().isAST()) {
			final IAST f = (IAST) ast.arg1();
			final IAST result = F.ast(f.head());
			if (f.size() <= 2) {
				if (f.size() == 2) {
					result.add(f);
				}
				return result;
			}

			int k = f.size() - 1;
			if (ast.size() == 3) {
				if (!ast.arg2().isInteger()) {
					return null;
				}
				k = Validate.checkIntType(ast, 2);
				if (k > f.size() - 1) {
					return null;
				}
			}
			final KPermutationsList perm = new KPermutationsList(f, k, F.ast(f.head()), 1);
			for (IAST temp : perm) {
				result.add(temp);
			}
			return result;

		}
		return null;
	}

}
