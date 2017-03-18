package org.matheclipse.core.builtin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.Subsets;

public final class Combinatoric {

	static {
		F.CartesianProduct.setEvaluator(new CartesianProduct());
		F.IntegerPartitions.setEvaluator(new IntegerPartitions());
		F.KOrderlessPartitions.setEvaluator(new KOrderlessPartitions());
		F.KPartitions.setEvaluator(new KPartitions());
		F.Partition.setEvaluator(new Partition());
		F.Permutations.setEvaluator(new Permutations());
		F.Tuples.setEvaluator(new Tuples());
	}

	/**
	 * Cartesian product for multiple lists.
	 * 
	 * See: <a href="http://en.wikipedia.org/wiki/Cartesian_product">Wikipedia -
	 * Cartesian product</a>
	 */
	private final static class CartesianProduct extends AbstractFunctionEvaluator {

		/**
		 * Cartesian product iterator.
		 * 
		 * @author Heinz Kredel
		 */
		final static class CartesianProductIterator implements Iterator<IAST> {

			/**
			 * data structure.
			 */
			final List<IAST> comps;

			final List<Iterator<IExpr>> compit;

			IAST current;

			boolean empty;

			/**
			 * CartesianProduct iterator constructor.
			 * 
			 * @param comps
			 *            components of the cartesian product.
			 */
			public CartesianProductIterator(List<IAST> comps, IAST emptyResultList) {
				if (comps == null) {
					throw new IllegalArgumentException("null comps not allowed");
				}
				this.comps = comps;
				current = emptyResultList;
				compit = new ArrayList<Iterator<IExpr>>(comps.size());
				empty = false;
				for (IAST ci : comps) {
					Iterator<IExpr> it = ci.iterator();
					if (!it.hasNext()) {
						empty = true;
						current.clear();
						break;
					}
					current.append(it.next());
					compit.add(it);
				}
			}

			/**
			 * Test for availability of a next tuple.
			 * 
			 * @return true if the iteration has more tuples, else false.
			 */
			@Override
			public synchronized boolean hasNext() {
				return !empty;
			}

			/**
			 * Get next tuple.
			 * 
			 * @return next tuple.
			 */
			@Override
			public synchronized IAST next() {
				if (empty) {
					throw new RuntimeException("invalid call of next()");
				}
				// IAST res = (IAST) current.clone();
				IAST res = current.clone();
				// search iterator which hasNext
				int i = compit.size() - 1;
				for (; i >= 0; i--) {
					Iterator<IExpr> iter = compit.get(i);
					if (iter.hasNext()) {
						break;
					}
				}
				if (i < 0) {
					empty = true;
					return res;
				}
				// update iterators
				for (int j = i + 1; j < compit.size(); j++) {
					Iterator<IExpr> iter = comps.get(j).iterator();
					compit.set(j, iter);
				}
				// update current
				for (int j = i; j < compit.size(); j++) {
					Iterator<IExpr> iter = compit.get(j);
					IExpr el = iter.next();
					current.set(j + 1, el);
				}
				return res;
			}

			/**
			 * Remove a tuple if allowed.
			 * 
			 * @throws UnsupportedOperationException
			 */
			@Override
			public void remove() {
				throw new UnsupportedOperationException("cannnot remove tuples");
			}

		}

		/**
		 * Cartesian product iterable.
		 * 
		 * <br/>
		 * See
		 * <a href="http://en.wikipedia.org/wiki/Cartesian_product">Wikipedia -
		 * Cartesian product</a>
		 * 
		 * @author Heinz Kredel
		 * @author Axel Kramer (Modifications for Symja)
		 */
		final static class CartesianProductList implements Iterable<IAST> {

			/**
			 * data structure.
			 */
			public final List<IAST> comps;

			private final IAST fEmptyResultList;

			/**
			 * CartesianProduct constructor.
			 * 
			 * @param comps
			 *            components of the cartesian product.
			 */
			public CartesianProductList(List<IAST> comps, IAST emptyResultList) {
				if (comps == null) {
					throw new IllegalArgumentException("null components not allowed");
				}
				this.comps = comps;
				this.fEmptyResultList = emptyResultList;
			}

			/**
			 * Get an iterator over subsets.
			 * 
			 * @return an iterator.
			 */
			@Override
			public Iterator<IAST> iterator() {
				return new CartesianProductIterator(comps, fEmptyResultList);
			}

		}

		public CartesianProduct() {
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3);
			List<IAST> la = new ArrayList<IAST>(ast.size() - 1);
			for (int i = 1; i < ast.size(); i++) {
				if (ast.get(i).isList()) {
					la.add((IAST) ast.get(i));
				} else {
					return F.NIL;
				}
			}
			CartesianProductList cpi = new CartesianProductList(la, F.List());
			IAST result = F.List();
			for (IAST iast : cpi) {
				result.append(iast);
			}
			return result;
		}
	}

	/**
	 * Generate all integer partitions for a given integer number. See
	 * <a href="http://en.wikipedia.org/wiki/Integer_partition">Wikipedia -
	 * Integer partition</a>
	 * 
	 */
	private final static class IntegerPartitions extends AbstractFunctionEvaluator {
		/**
		 * Returns all partitions of a given int number (i.e.
		 * NumberPartitions(3) => [3,0,0] [2,1,0] [1,1,1] ).
		 * 
		 * See
		 * <a href="http://en.wikipedia.org/wiki/Integer_partition">Wikipedia -
		 * Integer partition</a>
		 */
		public final static class NumberPartitionsIterable implements Iterator<int[]>, Iterable<int[]> {

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

		public IntegerPartitions() {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			if (ast.arg1().isInteger()) {
				final int n = ((IInteger) ast.arg1()).toBigNumerator().intValue();
				final IAST result = F.List();
				IAST temp;
				final NumberPartitionsIterable comb = new NumberPartitionsIterable(n);
				for (int j[] : comb) {
					temp = F.List();
					for (int i = 0; i < j.length; i++) {
						if (j[i] != 0) {
							temp.append(F.integer(j[i]));
						}
					}
					result.append(temp);
				}
				return result;
			}
			return F.NIL;
		}

	}

	private final static class KOrderlessPartitions extends AbstractFunctionEvaluator {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);
			if (ast.arg1().isAST() && ast.arg2().isInteger()) {
				final IAST listArg0 = (IAST) ast.arg1();
				final ISymbol sym = listArg0.topHead();
				final int n = listArg0.size() - 1;
				final int k = ((IInteger) ast.arg2()).toBigNumerator().intValue();
				final IAST result = F.ast(F.List);
				final Permutations.KPermutationsIterable permutationIterator = new Permutations.KPermutationsIterable(
						listArg0, n, 1);
				final KPartitions.KPartitionsIterable partitionIterator = new KPartitions.KPartitionsIterable(n, k);
				IAST partition;

				// first generate all permutations:
				for (int permutationsIndex[] : permutationIterator) {
					// second generate all partitions:
					for (int partitionsIndex[] : partitionIterator) {
						partition = createSinglePartition(listArg0, sym, permutationsIndex, partitionsIndex);
						if (partition.isPresent()) {
							result.append(partition);
						}
					}
					partitionIterator.reset();
				}
				return result;
			}
			return F.NIL;
		}

		private IAST createSinglePartition(final IAST listArg0, final ISymbol sym, final int[] permutationsIndex,
				final int[] partitionsIndex) {
			IAST partition;
			IAST partitionElement;
			int partitionStartIndex;
			partition = F.List();

			final int n = listArg0.size() - 1;
			// 0 is always the first index of a partition
			partitionStartIndex = 0;
			for (int i = 1; i < partitionsIndex.length; i++) {
				partitionElement = F.ast(sym);
				if (partitionStartIndex + 1 == partitionsIndex[i]) {
					// OneIdentity check here
					if (sym.hasOneIdentityAttribute()) {
						partition.append(listArg0.get(permutationsIndex[partitionStartIndex] + 1));
					} else {
						partitionElement.append(listArg0.get(permutationsIndex[partitionStartIndex] + 1));
						partition.append(partitionElement);
					}
				} else {
					for (int m = partitionStartIndex; m < partitionsIndex[i]; m++) {
						if (m + 1 < partitionsIndex[i]) {
							if ((listArg0.get(permutationsIndex[m + 1] + 1))
									.isLTOrdered(listArg0.get(permutationsIndex[m] + 1))) {
								return F.NIL;
							}
						}
						partitionElement.append(listArg0.get(permutationsIndex[m] + 1));
					}
					partition.append(partitionElement);
				}
				partitionStartIndex = partitionsIndex[i];

			}
			// generate all elements for the last partitionElement of a
			// partition:
			partitionElement = F.ast(sym);
			if (partitionStartIndex + 1 == n) {
				// OneIdentity check here
				if ((sym.getAttributes() & ISymbol.ONEIDENTITY) == ISymbol.ONEIDENTITY) {
					partition.append(listArg0.get(permutationsIndex[partitionStartIndex] + 1));
				} else {
					partitionElement.append(listArg0.get(permutationsIndex[partitionStartIndex] + 1));
					partition.append(partitionElement);
				}
			} else {
				for (int m = partitionStartIndex; m < n; m++) {
					if (m + 1 < n) {
						if ((listArg0.get(permutationsIndex[m + 1] + 1))
								.isLTOrdered(listArg0.get(permutationsIndex[m] + 1))) {
							return F.NIL;
						}
					}
					partitionElement.append(listArg0.get(permutationsIndex[m] + 1));
				}
				partition.append(partitionElement);
			}

			return partition;
		}

	}

	/**
	 * Generate a list of all all k-partitions for a given list with N elements.
	 * <br/>
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Partition_of_a_set">Wikipedia -
	 * Partition of a set</a>
	 * 
	 */
	private static final class KPartitions extends AbstractFunctionEvaluator {
		/**
		 * This class returns the indexes for partitioning a list of N elements.
		 * <br/>
		 * Usage pattern:
		 * 
		 * <pre>
		 * final KPartitionsIterable iter = new KPartitionsIterable(n, k);
		 * for (int[] partitionsIndex : iter) {
		 *   ...
		 * }
		 * </pre>
		 * 
		 * Example: KPartitionsIterable(3,5) gives the following sequences [0,
		 * 1, 2], [0, 1, 3], [0, 1, 4], [0, 2, 3], [0, 2, 4], [0, 3, 4] <br/>
		 * If you interpret these integer lists as indexes for a list
		 * {a,b,c,d,e} which should be partitioned into 3 parts the results are:
		 * <br/>
		 * {{{a},{b},{c,d,e}}, {{a},{b,c},{d,e}}, {{a},{b,c,d},{e}},
		 * {{a,b},{c},{d,e}}, {{a,b},{c,d},{e}}, {{a,b,c},{d},{e}}}
		 * 
		 * <br/>
		 * <br/>
		 * See
		 * <a href="http://en.wikipedia.org/wiki/Partition_of_a_set">Wikipedia -
		 * Partition of a set</a>
		 */
		public final static class KPartitionsIterable implements Iterator<int[]>, Iterable<int[]> {

			final private int fLength;

			final private int fNumberOfParts;

			final private int fPartitionsIndex[];

			final private int fCopiedResultIndex[];

			private int fResultIndex[];

			public KPartitionsIterable(final int length, final int parts) {
				super();
				fLength = length;
				fNumberOfParts = parts;
				fPartitionsIndex = new int[fNumberOfParts];
				fCopiedResultIndex = new int[fNumberOfParts];
				fPartitionsIndex[0] = -1;
				fResultIndex = nextBeforehand();
			}

			public final void reset() {
				fResultIndex = null;
				for (int i = 1; i < fNumberOfParts; i++) {
					fPartitionsIndex[i] = 0;
				}
				fPartitionsIndex[0] = -1;
				fResultIndex = nextBeforehand();
			}

			/**
			 * Get the index array for the next partition.
			 * 
			 * @return <code>null</code> if no further index array could be
			 *         generated
			 */
			private final int[] nextBeforehand() {
				if (fPartitionsIndex[0] < 0) {
					for (int i = 0; i < fNumberOfParts; ++i) {
						fPartitionsIndex[i] = i;
					}
					return fPartitionsIndex;
				} else {
					int i = 0;
					for (i = fNumberOfParts - 1; (i >= 0)
							&& (fPartitionsIndex[i] >= fLength - fNumberOfParts + i); --i) {
					}
					if (i <= 0) {
						return null;
					}
					fPartitionsIndex[i]++;
					for (int m = i + 1; m < fNumberOfParts; ++m) {
						fPartitionsIndex[m] = fPartitionsIndex[m - 1] + 1;
					}
					return fPartitionsIndex;
				}
			}

			/**
			 * Get the index array for the next partition.
			 * 
			 * @return <code>null</code> if no further index array could be
			 *         generated
			 */
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
		 * This <code>Iterable</code> iterates through all k-partition lists for
		 * a given list with N elements. <br/>
		 * 
		 * See
		 * <a href="http://en.wikipedia.org/wiki/Partition_of_a_set">Wikipedia -
		 * Partition of a set</a>
		 * 
		 */
		public final static class KPartitionsList implements Iterator<IAST>, Iterable<IAST> {

			final private IAST fList;
			final private IAST fResultList;
			final private int fOffset;
			final private KPartitionsIterable fIterable;

			public KPartitionsList(final IAST list, final int kParts, IAST resultList, final int offset) {
				super();
				fIterable = new KPartitionsIterable(list.size() - offset, kParts);
				fList = list;
				fResultList = resultList;
				fOffset = offset;
			}

			/**
			 * Get the index array for the next partition.
			 * 
			 * @return <code>null</code> if no further index array could be
			 *         generated
			 */
			@Override
			public IAST next() {
				int[] partitionsIndex = fIterable.next();
				if (partitionsIndex == null) {
					return null;
				}
				IAST part = fResultList.clone();
				IAST temp;
				int j = 0;
				for (int i = 1; i < partitionsIndex.length; i++) {
					temp = fResultList.clone();
					for (int m = j; m < partitionsIndex[i]; m++) {
						temp.append(fList.get(m + fOffset));
					}
					j = partitionsIndex[i];
					part.append(temp);
				}

				temp = fResultList.clone();
				int n = fList.size() - fOffset;
				for (int m = j; m < n; m++) {
					temp.append(fList.get(m + fOffset));
				}
				part.append(temp);
				return part;
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

		public KPartitions() {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);
			if (ast.arg1().isAST() && ast.arg2().isInteger()) {
				final IAST listArg0 = (IAST) ast.arg1();
				final int k = Validate.checkIntType(ast, 2);
				final IAST result = F.List();
				final KPartitionsList iter = new KPartitionsList(listArg0, k, F.ast(F.List), 1);
				for (IAST part : iter) {
					result.append(part);
				}
				return result;
			}
			return F.NIL;
		}
	}

	/**
	 * 
	 * @see Permutations
	 * @see Subsets
	 */
	private static class Partition extends AbstractFunctionEvaluator {

		public Partition() {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3, 4);

			if (ast.arg1().isAST()) {
				if (ast.arg2().isInteger()) {
					final IAST f = (IAST) ast.arg1();
					final int n = ((IInteger) ast.arg2()).toBigNumerator().intValue();
					final IAST result = F.ast(f.head());
					IAST temp;
					int i = n;
					int v = n;
					if ((ast.isAST3()) && ast.arg3().isInteger()) {
						v = ((IInteger) ast.arg3()).toBigNumerator().intValue();
					}
					while (i <= f.size() - 1) {
						temp = F.ast(f.head());
						for (int j = i - n; j < i; j++) {
							temp.append(f.get(j + 1));
						}
						i += v;
						result.append(temp);

					}
					return result;
				}
			}
			return F.NIL;
		}

	}

	/**
	 * Generate a list of (multiset) permutations
	 * 
	 * See <a href=" http://en.wikipedia.org/wiki/Permutation">Permutation</a>
	 * 
	 * @see Partition
	 * @see Subsets
	 */
	public final static class Permutations extends AbstractFunctionEvaluator {

		/**
		 * Generate an <code>java.lang.Iterable</code> for (multiset)
		 * permutations
		 * 
		 * See
		 * <a href="http://en.wikipedia.org/wiki/Permutation">Permutation</a>
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
			 * <code>data</code> which contains at most <code>parts</code>
			 * number of elements. Repeated elements are treated as same.
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
			 * <code>data</code> which contains at most <code>parts</code>
			 * number of elements. Repeated elements are treated as same.
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
		 * See
		 * <a href="http://en.wikipedia.org/wiki/Permutation">Permutation</a>
		 */
		private final static class KPermutationsList implements Iterator<IAST>, Iterable<IAST> {

			final private IAST fList;
			final private IAST fResultList;
			final private int fOffset;
			final private int fParts;
			final private KPermutationsIterable fIterable;

			/**
			 * Create an iterator which gives all possible permutations of
			 * <code>list</code> which contains at most <code>parts</code>
			 * number of elements. Repeated elements are treated as same.
			 * 
			 * @param list
			 *            a list of elements
			 * @param parts
			 *            contain at most parts elements in ech permutation
			 * @param resultList
			 *            a template AST where the elements could be appended.
			 * @param offset
			 *            the offset from which to start the list of elements in
			 *            the list
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

	/**
	 * Generate tuples from elements of a list.
	 */
	private final static class Tuples extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			IExpr arg1 = ast.arg1();
			if (ast.isAST1() && arg1.isList()) {
				try {
					IAST list = (IAST) arg1;
					for (int i = 1; i < list.size(); i++) {
						if (!list.get(i).isAST()) {
							return F.NIL;
						}
					}
					IAST result = F.List();
					IAST temp = F.List();
					tuplesOfLists(list, 1, result, temp);
					return result;
				} catch (ArithmeticException ae) {
					return F.NIL;
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
				return F.NIL;
			} else if (ast.isAST2() && arg1.isAST() && ast.arg2().isInteger()) {
				IExpr arg2 = ast.arg2();
				try {
					int k = ((IInteger) arg2).toInt();
					IAST result = F.List();
					IAST temp = F.ast(arg1.head());
					tuples((IAST) arg1, k, result, temp);
					return result;
				} catch (ArithmeticException ae) {
					// because of toInt() method
				}
			}
			return F.NIL;
		}

		/**
		 * Generate all n-tuples form a list.
		 * 
		 * @param originalList
		 * @param n
		 * @param result
		 * @param subResult
		 */
		private void tuples(final IAST originalList, final int n, IAST result, IAST subResult) {
			if (n == 0) {
				result.append(subResult);
				return;
			}
			IAST temp;
			for (int j = 1; j < originalList.size(); j++) {
				temp = subResult.clone();
				temp.append(originalList.get(j));
				tuples(originalList, n - 1, result, temp);
			}

		}

		/**
		 * Generate all tuples from a list of lists.
		 * 
		 * @param originalList
		 *            the list of lists
		 * @param k
		 * @param result
		 *            the result list
		 * @param subResult
		 *            the current subList which should be inserted in the result
		 *            list
		 */
		private void tuplesOfLists(final IAST originalList, final int k, IAST result, IAST subResult) {
			if (k == originalList.size()) {
				result.append(subResult);
				return;
			}
			IAST temp;
			IAST subAST = (IAST) originalList.get(k);
			for (int j = 1; j < subAST.size(); j++) {
				temp = subResult.clone();
				temp.append(subAST.get(j));
				tuplesOfLists(originalList, k + 1, result, temp);
			}

		}

	}

	final static Combinatoric CONST = new Combinatoric();

	public static Combinatoric initialize() {
		return CONST;
	}

	private Combinatoric() {

	}
}
