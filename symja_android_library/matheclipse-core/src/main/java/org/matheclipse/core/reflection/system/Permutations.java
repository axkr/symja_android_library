package org.matheclipse.core.reflection.system;

import java.util.Iterator;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Generate a list of (multiset) permutations
 * 
 * See <a href=" http://en.wikipedia.org/wiki/Permutation">Permutation</a>
 * 
 * @see Partition
 * @see Subsets
 */
public class Permutations extends AbstractFunctionEvaluator {

	/**
	 * Generate an <code>java.lang.Iterable</code> for (multiset) permutations
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

		public KPermutationsIterable(final IAST fun, final int parts, final int headOffset) {
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
		 * Create an iterator which gives all possible permutations of
		 * <code>data</code> which contains at most <code>parts</code> number of
		 * elements. Repeated elements are treated as same.
		 * 
		 * @param data
		 *            a list of integers which should be permutated.
		 * @param parts
		 */
		public KPermutationsIterable(final int[] data, final int parts) {
			this(data, data.length, parts);
		}

		/**
		 * Create an iterator which gives all possible permutations of
		 * <code>data</code> which contains at most <code>parts</code> number of
		 * elements. Repeated elements are treated as same.
		 * 
		 * @param data
		 *            a list of integers which should be permutated.
		 * @param len
		 *            consider only the first <code>n</code> elements of
		 *            <code>data</code> for permutation
		 * @param parts
		 */
		public KPermutationsIterable(final int[] data, final int len, final int parts) {
			super();
			n = len;
			k = parts;
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

		@Override
		public boolean hasNext() {
			return fResultIndex != null;
		}

		@Override
		public Iterator<int[]> iterator() {
			return this;
		}

		@Override
		public int[] next() {
			System.arraycopy(fResultIndex, 0, fCopiedResultIndex, 0, fResultIndex.length);
			fResultIndex = nextBeforehand();
			return fCopiedResultIndex;
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
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * Generate an <code>java.lang.Iterable<IAST></code> for (multiset)
	 * permutations
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Permutation">Permutation</a>
	 */
	public final static class KPermutationsList implements Iterator<IAST>, Iterable<IAST> {

		final private IAST fList;
		final private IAST fResultList;
		final private int fOffset;
		final private int fParts;
		final private KPermutationsIterable fIterable;

		public KPermutationsList(final IAST list, final int parts, IAST resultList) {
			this(list, parts, resultList, 0);
		}

		/**
		 * Create an iterator which gives all possible permutations of
		 * <code>list</code> which contains at most <code>parts</code> number of
		 * elements. Repeated elements are treated as same.
		 * 
		 * @param list
		 *            a list of elements
		 * @param parts
		 *            contain at most parts elements in ech permutation
		 * @param resultList
		 *            a template AST where the elements could be appended.
		 * @param offset
		 *            the offset from which to start the list of elements in the
		 *            list
		 */
		public KPermutationsList(final IAST list, final int parts, IAST resultList, final int offset) {
			fIterable = new KPermutationsIterable(list, parts, offset);
			fList = list;
			fResultList = resultList;
			fOffset = offset;
			fParts = parts;
		}

		@Override
		public boolean hasNext() {
			return fIterable.hasNext();
		}

		@Override
		public Iterator<IAST> iterator() {
			return this;
		}

		/**
		 * Get the index array for the next permutation.
		 * 
		 * @return <code>null</code> if no further index array could be
		 *         generated
		 */
		@Override
		public IAST next() {
			int[] permutationsIndex = fIterable.next();
			if (permutationsIndex == null) {
				return null;
			}
			IAST temp = fResultList.clone();
			// parts <= permutationsIndex.length
			for (int i = 0; i < fParts; i++) {
				temp.append(fList.get(permutationsIndex[i] + fOffset));
			}
			return temp;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
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
			final IAST list = (IAST) ast.arg1();
			int parts = list.size() - 1;
			if (ast.isAST2() && ast.get(2).isList()) {
				IAST sequence = (IAST) ast.get(2);
				// TODO use ISequence here
				if (!sequence.isAST1() || !sequence.arg1().isInteger()) {
					return F.NIL;
				}
				parts = Validate.checkIntType(sequence.arg1());
				if (parts < 0 && parts > list.size() - 1) {
					return F.NIL;
				}
			}

			final IAST result = F.ast(list.head());
			if (list.size() <= 2) {
				if (list.isAST1()) {
					if (parts == 0) {
						result.append(F.List());
					} else {
						result.append(list);
					}
				}
				return result;
			}

			final KPermutationsList perm = new KPermutationsList(list, parts, F.ast(list.head()), 1);
			for (IAST temp : perm) {
				result.append(temp);
			}
			return result;

		}
		return F.NIL;
	}

}
