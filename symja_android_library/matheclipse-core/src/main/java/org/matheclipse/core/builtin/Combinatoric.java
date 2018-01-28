package org.matheclipse.core.builtin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.matheclipse.combinatoric.KSubsets;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.LevelSpecification;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

public final class Combinatoric {

	static {
		F.CartesianProduct.setEvaluator(new CartesianProduct());
		F.DiceDissimilarity.setEvaluator(new DiceDissimilarity());
		F.IntegerPartitions.setEvaluator(new IntegerPartitions());
		F.JaccardDissimilarity.setEvaluator(new JaccardDissimilarity());
		F.KOrderlessPartitions.setEvaluator(new KOrderlessPartitions());
		F.KPartitions.setEvaluator(new KPartitions());
		F.MatchingDissimilarity.setEvaluator(new MatchingDissimilarity());
		F.Partition.setEvaluator(new Partition());
		F.Permutations.setEvaluator(new Permutations());
		F.RogersTanimotoDissimilarity.setEvaluator(new RogersTanimotoDissimilarity());
		F.RussellRaoDissimilarity.setEvaluator(new RussellRaoDissimilarity());
		F.SokalSneathDissimilarity.setEvaluator(new SokalSneathDissimilarity());
		F.Subsets.setEvaluator(new Subsets());
		F.Tuples.setEvaluator(new Tuples());
		F.YuleDissimilarity.setEvaluator(new YuleDissimilarity());
	}

	/**
	 * Cartesian product for multiple lists.
	 * 
	 * See: <a href="http://en.wikipedia.org/wiki/Cartesian_product">Wikipedia - Cartesian product</a>
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

			IASTAppendable current;

			boolean empty;

			/**
			 * CartesianProduct iterator constructor.
			 * 
			 * @param comps
			 *            components of the cartesian product.
			 */
			public CartesianProductIterator(List<IAST> comps, IASTAppendable emptyResultList) {
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
				IAST res = current.copyAppendable();
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
		 * See <a href="http://en.wikipedia.org/wiki/Cartesian_product">Wikipedia - Cartesian product</a>
		 * 
		 * @author Heinz Kredel
		 * @author Axel Kramer (Modifications for Symja)
		 */
		final static class CartesianProductList implements Iterable<IAST> {

			/**
			 * data structure.
			 */
			public final List<IAST> comps;

			private final IASTAppendable fEmptyResultList;

			/**
			 * CartesianProduct constructor.
			 * 
			 * @param comps
			 *            components of the cartesian product.
			 */
			public CartesianProductList(List<IAST> comps, IASTAppendable emptyResultList) {
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

			int size() {
				return comps.size();
			}
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3);
			List<IAST> la = new ArrayList<IAST>(ast.argSize());
			for (int i = 1; i < ast.size(); i++) {
				if (ast.get(i).isList()) {
					la.add((IAST) ast.get(i));
				} else {
					return F.NIL;
				}
			}
			CartesianProductList cpi = new CartesianProductList(la, F.ListAlloc(la.size()));
			IASTAppendable result = F.ListAlloc(cpi.size());
			for (IAST iast : cpi) {
				result.append(iast);
			}
			return result;
		}
	}

	private final static class DiceDissimilarity extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			int dim1 = ast.arg1().isVector();
			int dim2 = ast.arg2().isVector();
			if (dim1 == dim2 && dim1 > 0) {
				IAST u = (IAST) ast.arg1();
				IAST v = (IAST) ast.arg2();
				int length = u.size();
				int n10 = 0;
				int n01 = 0;
				int n11 = 0;
				IExpr x, y;
				for (int i = 1; i < length; i++) {
					x = u.get(i);
					y = v.get(i);
					if ((x.isOne() || x.isTrue()) && (y.isZero() || y.isFalse())) {
						n10++;
						continue;
					}
					if ((x.isZero() || x.isFalse()) && (y.isOne() || y.isTrue())) {
						n01++;
						continue;
					}
					if ((x.isOne() || x.isTrue()) && (y.isOne() || y.isTrue())) {
						n11++;
						continue;
					}
					if ((x.isZero() || x.isFalse() || x.isOne() || x.isTrue())
							&& (y.isZero() || y.isFalse() || y.isOne() || y.isTrue())) {
						continue;
					}
					return F.NIL;
				}

				return F.Divide(F.integer((long) n10 + (long) n01), F.integer(2L * n11 + n10 + n01));
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	/**
	 * Generate all integer partitions for a given integer number. See
	 * <a href="http://en.wikipedia.org/wiki/Integer_partition">Wikipedia - Integer partition</a>
	 * 
	 */
	private final static class IntegerPartitions extends AbstractFunctionEvaluator {
		/**
		 * Returns all partitions of a given int number (i.e. NumberPartitions(3) => [3,0,0] [2,1,0] [1,1,1] ).
		 * 
		 * See <a href="http://en.wikipedia.org/wiki/Integer_partition">Wikipedia - Integer partition</a>
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

		/** {@inheritDoc} */
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			if (ast.arg1().isInteger()) {
				final int n = ((IInteger) ast.arg1()).toBigNumerator().intValue();

				IASTAppendable temp;
				final NumberPartitionsIterable comb = new NumberPartitionsIterable(n);
				IASTAppendable result = F.ListAlloc(16);
				for (int j[] : comb) {
					temp = F.ListAlloc(j.length);
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

	private static class JaccardDissimilarity extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			int dim1 = ast.arg1().isVector();
			int dim2 = ast.arg2().isVector();
			if (dim1 == dim2 && dim1 > 0) {
				IAST u = (IAST) ast.arg1();
				IAST v = (IAST) ast.arg2();
				int length = u.size();
				int n10 = 0;
				int n01 = 0;
				int n11 = 0;
				IExpr x, y;
				for (int i = 1; i < length; i++) {
					x = u.get(i);
					y = v.get(i);
					if ((x.isOne() || x.isTrue()) && (y.isZero() || y.isFalse())) {
						n10++;
						continue;
					}
					if ((x.isZero() || x.isFalse()) && (y.isOne() || y.isTrue())) {
						n01++;
						continue;
					}
					if ((x.isOne() || x.isTrue()) && (y.isOne() || y.isTrue())) {
						n11++;
						continue;
					}
					if ((x.isZero() || x.isFalse() || x.isOne() || x.isTrue())
							&& (y.isZero() || y.isFalse() || y.isOne() || y.isTrue())) {
						continue;
					}
					return F.NIL;
				}

				return F.Divide(F.integer((long) n10 + (long) n01), F.integer((long) n11 + (long) n10 + n01));
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	private final static class KOrderlessPartitions extends AbstractFunctionEvaluator {

		/** {@inheritDoc} */
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);
			if (ast.arg1().isAST() && ast.arg2().isInteger()) {
				final IAST listArg0 = (IAST) ast.arg1();
				final ISymbol sym = listArg0.topHead();
				final int n = listArg0.argSize();
				final int k = ((IInteger) ast.arg2()).toBigNumerator().intValue();
				final IASTAppendable result = F.ast(F.List);
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
			IASTAppendable partitionElement;
			int partitionStartIndex;
			IASTAppendable partition = F.ListAlloc(partitionsIndex.length + 1);

			final int n = listArg0.argSize();
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
	 * Generate a list of all all k-partitions for a given list with N elements. <br/>
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Partition_of_a_set">Wikipedia - Partition of a set</a>
	 * 
	 */
	private static final class KPartitions extends AbstractFunctionEvaluator {
		/**
		 * This class returns the indexes for partitioning a list of N elements. <br/>
		 * Usage pattern:
		 * 
		 * <pre>
		 * final KPartitionsIterable iter = new KPartitionsIterable(n, k);
		 * for (int[] partitionsIndex : iter) {
		 *   ...
		 * }
		 * </pre>
		 * 
		 * Example: KPartitionsIterable(3,5) gives the following sequences [0, 1, 2], [0, 1, 3], [0, 1, 4], [0, 2, 3],
		 * [0, 2, 4], [0, 3, 4] <br/>
		 * If you interpret these integer lists as indexes for a list {a,b,c,d,e} which should be partitioned into 3
		 * parts the results are: <br/>
		 * {{{a},{b},{c,d,e}}, {{a},{b,c},{d,e}}, {{a},{b,c,d},{e}}, {{a,b},{c},{d,e}}, {{a,b},{c,d},{e}},
		 * {{a,b,c},{d},{e}}}
		 * 
		 * <br/>
		 * <br/>
		 * See <a href="http://en.wikipedia.org/wiki/Partition_of_a_set">Wikipedia - Partition of a set</a>
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
			 * @return <code>null</code> if no further index array could be generated
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
			 * @return <code>null</code> if no further index array could be generated
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
		 * This <code>Iterable</code> iterates through all k-partition lists for a given list with N elements. <br/>
		 * 
		 * See <a href="http://en.wikipedia.org/wiki/Partition_of_a_set">Wikipedia - Partition of a set</a>
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
			 * @return <code>null</code> if no further index array could be generated
			 */
			@Override
			public IAST next() {
				int[] partitionsIndex = fIterable.next();
				if (partitionsIndex == null) {
					return null;
				}
				IASTAppendable part = fResultList.copyAppendable();
				IASTAppendable temp;
				int j = 0;
				for (int i = 1; i < partitionsIndex.length; i++) {
					temp = fResultList.copyAppendable();
					for (int m = j; m < partitionsIndex[i]; m++) {
						temp.append(fList.get(m + fOffset));
					}
					j = partitionsIndex[i];
					part.append(temp);
				}

				temp = fResultList.copyAppendable();
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

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);
			if (ast.arg1().isAST() && ast.arg2().isInteger()) {
				final IAST listArg0 = (IAST) ast.arg1();
				final int k = Validate.checkIntType(ast, 2);
				final KPartitionsList iter = new KPartitionsList(listArg0, k, F.ast(F.List), 1);
				final IASTAppendable result = F.ListAlloc(16);
				for (IAST part : iter) {
					result.append(part);
				}
				return result;
			}
			return F.NIL;
		}
	}

	private final static class MatchingDissimilarity extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			int dim1 = ast.arg1().isVector();
			int dim2 = ast.arg2().isVector();
			if (dim1 == dim2 && dim1 > 0) {
				IAST u = (IAST) ast.arg1();
				IAST v = (IAST) ast.arg2();
				int length = u.size();
				int n10 = 0;
				int n01 = 0;
				IExpr x, y;
				for (int i = 1; i < length; i++) {
					x = u.get(i);
					y = v.get(i);
					if ((x.isOne() || x.isTrue()) && (y.isZero() || y.isFalse())) {
						n10++;
						continue;
					}
					if ((x.isZero() || x.isFalse()) && (y.isOne() || y.isTrue())) {
						n01++;
						continue;
					}
					if ((x.isZero() || x.isFalse() || x.isOne() || x.isTrue())
							&& (y.isZero() || y.isFalse() || y.isOne() || y.isTrue())) {
						continue;
					}
					return F.NIL;
				}

				return F.Divide(F.integer((long) n10 + (long) n01), F.integer(length - 1));
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
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
					final IASTAppendable result = F.ast(f.head());
					IASTAppendable temp;
					int i = n;
					int v = n;
					if ((ast.isAST3()) && ast.arg3().isInteger()) {
						v = ((IInteger) ast.arg3()).toBigNumerator().intValue();
					}
					while (i <= f.argSize()) {
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
			 * Create an iterator which gives all possible permutations of <code>data</code> which contains at most
			 * <code>parts</code> number of elements. Repeated elements are treated as same.
			 * 
			 * @param data
			 *            a list of integers which should be permutated.
			 * @param parts
			 */
			public KPermutationsIterable(final int[] data, final int parts) {
				this(data, data.length, parts);
			}

			/**
			 * Create an iterator which gives all possible permutations of <code>data</code> which contains at most
			 * <code>parts</code> number of elements. Repeated elements are treated as same.
			 * 
			 * @param data
			 *            a list of integers which should be permutated.
			 * @param len
			 *            consider only the first <code>n</code> elements of <code>data</code> for permutation
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
		 * Generate an <code>java.lang.Iterable<IAST></code> for (multiset) permutations
		 * 
		 * See <a href="http://en.wikipedia.org/wiki/Permutation">Permutation</a>
		 */
		private final static class KPermutationsList implements Iterator<IAST>, Iterable<IAST> {

			final private IAST fList;
			final private IAST fResultList;
			final private int fOffset;
			final private int fParts;
			final private KPermutationsIterable fIterable;

			/**
			 * Create an iterator which gives all possible permutations of <code>list</code> which contains at most
			 * <code>parts</code> number of elements. Repeated elements are treated as same.
			 * 
			 * @param list
			 *            a list of elements
			 * @param parts
			 *            contain at most parts elements in ech permutation
			 * @param resultList
			 *            a template AST where the elements could be appended.
			 * @param offset
			 *            the offset from which to start the list of elements in the list
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
			 * @return <code>null</code> if no further index array could be generated
			 */
			@Override
			public IAST next() {
				int[] permutationsIndex = fIterable.next();
				if (permutationsIndex == null) {
					return null;
				}
				IASTAppendable temp = fResultList.copyAppendable();
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
				int parts = list.argSize();
				if (ast.isAST2()) {
					if (ast.arg2().isInteger()) {
						int maxPart = ((IInteger) ast.arg2()).toIntDefault(-1);
						if (maxPart >= 0) {
							maxPart = maxPart < parts ? maxPart : parts;
							final IASTAppendable result = F.ListAlloc(100);
							for (int i = 0; i <= maxPart; i++) {
								createPermutationsWithNParts(list, i, result);
							}
							return result;
						}
						return F.NIL;
					}
					if (ast.arg2().isList()) {
						IAST sequence = (IAST) ast.arg2();
						// TODO use ISequence here
						if (!sequence.isAST1() || !sequence.arg1().isInteger()) {
							return F.NIL;
						}
						parts = Validate.checkIntType(sequence.arg1());
						if (parts < 0 && parts > list.argSize()) {
							return F.NIL;
						}
					}
				}

				final IASTAppendable result = F.ListAlloc(100);
				return createPermutationsWithNParts(list, parts, result);
			}
			return F.NIL;
		}

		private IAST createPermutationsWithNParts(final IAST list, int parts, final IASTAppendable result) {
			if (parts == 0) {
				result.append(F.List());
				return result;
			}
			if (list.size() <= 2) {
				if (list.isAST1()) {
					result.append(list);
				}
				return result;
			}

			final KPermutationsList perm = new KPermutationsList(list, parts, F.ast(list.head()), 1);
			for (IAST temp : perm) {
				result.append(temp);
			}
			return result;
		}

	}

	private final static class RogersTanimotoDissimilarity extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			int dim1 = ast.arg1().isVector();
			int dim2 = ast.arg2().isVector();
			if (dim1 == dim2 && dim1 > 0) {
				IAST u = (IAST) ast.arg1();
				IAST v = (IAST) ast.arg2();
				int length = u.size();
				int n10 = 0;
				int n01 = 0;
				int n00 = 0;
				int n11 = 0;
				IExpr x, y;
				for (int i = 1; i < length; i++) {
					x = u.get(i);
					y = v.get(i);
					if ((x.isOne() || x.isTrue()) && (y.isZero() || y.isFalse())) {
						n10++;
						continue;
					}
					if ((x.isZero() || x.isFalse()) && (y.isOne() || y.isTrue())) {
						n01++;
						continue;
					}
					if ((x.isZero() || x.isFalse()) && (y.isZero() || y.isFalse())) {
						n00++;
						continue;
					}
					if ((x.isOne() || x.isTrue()) && (y.isOne() || y.isTrue())) {
						n11++;
						continue;
					}
					if ((x.isZero() || x.isFalse() || x.isOne() || x.isTrue())
							&& (y.isZero() || y.isFalse() || y.isOne() || y.isTrue())) {
						continue;
					}
					return F.NIL;
				}

				long r = 2L * ((long) n10 + (long) n01);
				if (r == 0L) {
					return F.C0;
				}
				return F.Divide(F.integer(r), F.integer((long) n11 + (long) n00 + r));
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	private final static class RussellRaoDissimilarity extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			int dim1 = ast.arg1().isVector();
			int dim2 = ast.arg2().isVector();
			if (dim1 == dim2 && dim1 > 0) {
				IAST u = (IAST) ast.arg1();
				IAST v = (IAST) ast.arg2();
				int length = u.size();
				int n10 = 0;
				int n01 = 0;
				int n00 = 0;
				IExpr x, y;
				for (int i = 1; i < length; i++) {
					x = u.get(i);
					y = v.get(i);
					if ((x.isOne() || x.isTrue()) && (y.isZero() || y.isFalse())) {
						n10++;
						continue;
					}
					if ((x.isZero() || x.isFalse()) && (y.isOne() || y.isTrue())) {
						n01++;
						continue;
					}
					if ((x.isZero() || x.isFalse()) && (y.isZero() || y.isFalse())) {
						n00++;
						continue;
					}
					if ((x.isZero() || x.isFalse() || x.isOne() || x.isTrue())
							&& (y.isZero() || y.isFalse() || y.isOne() || y.isTrue())) {
						continue;
					}
					return F.NIL;
				}

				long r = (long) n10 + (long) n01 + n00;
				if (r == 0L) {
					return F.C0;
				}
				return F.Divide(F.integer(r), F.integer(length - 1));
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	private final static class SokalSneathDissimilarity extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			int dim1 = ast.arg1().isVector();
			int dim2 = ast.arg2().isVector();
			if (dim1 == dim2 && dim1 > 0) {
				IAST u = (IAST) ast.arg1();
				IAST v = (IAST) ast.arg2();
				int length = u.size();
				int n10 = 0;
				int n01 = 0;
				int n11 = 0;
				IExpr x, y;
				for (int i = 1; i < length; i++) {
					x = u.get(i);
					y = v.get(i);
					if ((x.isOne() || x.isTrue()) && (y.isZero() || y.isFalse())) {
						n10++;
						continue;
					}
					if ((x.isZero() || x.isFalse()) && (y.isOne() || y.isTrue())) {
						n01++;
						continue;
					}
					if ((x.isZero() || x.isFalse()) && (y.isZero() || y.isFalse())) {
						continue;
					}
					if ((x.isOne() || x.isTrue()) && (y.isOne() || y.isTrue())) {
						n11++;
						continue;
					}
					if ((x.isZero() || x.isFalse() || x.isOne() || x.isTrue())
							&& (y.isZero() || y.isFalse() || y.isOne() || y.isTrue())) {
						continue;
					}
					return F.NIL;
				}

				long r = 2L * ((long) n10 + (long) n01);
				if (r == 0L) {
					return F.C0;
				}
				return F.Divide(F.integer(r), F.integer(n11 + r));
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	/**
	 * Generate a list of all k-combinations from a given list
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Combination">Combination</a>
	 */
	public final static class Subsets extends AbstractFunctionEvaluator {

		/**
		 * Iterate over the lists of all k-combinations from a given list
		 * 
		 * See <a href="http://en.wikipedia.org/wiki/Combination">Combination</a>
		 */
		public final static class KSubsetsList implements Iterator<IAST>, Iterable<IAST> {

			final private IAST fList;
			final private IAST fResultList;
			final private int fOffset;
			final private Iterator<int[]> fIterable;
			final private int fK;

			public KSubsetsList(final Iterator<int[]> iterable, final IAST list, final int k, IAST resultList) {
				this(iterable, list, k, resultList, 0);
			}

			public KSubsetsList(final Iterator<int[]> iterable, final IAST list, final int k, IAST resultList,
					final int offset) {
				fIterable = iterable;
				fList = list;
				fK = k;
				fResultList = resultList;
				fOffset = offset;
			}

			/**
			 * Get the index array for the next partition.
			 * 
			 * @return <code>null</code> if no further index array could be generated
			 */
			@Override
			public IAST next() {
				int j[] = fIterable.next();
				if (j == null) {
					return null;
				}

				IASTAppendable temp = fResultList.copyAppendable();
				return temp.appendArgs(0, fK, i -> fList.get(j[i] + fOffset));
				// for (int i = 0; i < fK; i++) {
				// temp.append(fList.get(j[i] + fOffset));
				// }
				//
				// return temp;
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

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 1, 3);
			if (ast.isAST0()) {
				return F.NIL;
			}
			if (ast.arg1().isAST()) {
				final IAST f = (IAST) ast.arg1();
				int n = f.argSize();
				final LevelSpecification level;
				if (ast.isAST2()) {
					if (ast.arg2().isInteger()) {
						n = ((IInteger) ast.arg2()).toIntDefault(Integer.MIN_VALUE);
						if (n > Integer.MIN_VALUE) {
							level = new LevelSpecification(0, n);
						} else {
							return F.NIL;
						}
					} else {
						level = new LevelSpecification(ast.arg2(), false);
					}
				} else {
					level = new LevelSpecification(0, n);
				}

				int k;
				final IASTAppendable result = F.ast(f.head());
				level.setFromLevelAsCurrent();
				while (level.isInRange()) {
					k = level.getCurrentLevel();
					final KSubsetsList iter = createKSubsets(f, k, F.ast(F.List), 1);
					for (IAST part : iter) {
						if (part == null) {
							break;
						}
						result.append(part);
					}
					level.incCurrentLevel();
				}

				return result;
			}
			return F.NIL;
		}

		public static KSubsetsList createKSubsets(final IAST list, final int k, IAST resultList, final int offset) {
			return new KSubsetsList(new KSubsets.KSubsetsIterable(list.size() - offset, k), list, k, resultList,
					offset);
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
					IASTAppendable result = F.ListAlloc(16);
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

				int k = ((IInteger) arg2).toIntDefault(Integer.MIN_VALUE);
				if (k > Integer.MIN_VALUE) {
					IASTAppendable result = F.ListAlloc(16);
					IAST temp = F.ast(arg1.head());
					tuples((IAST) arg1, k, result, temp);
					return result;
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
		private void tuples(final IAST originalList, final int n, IASTAppendable result, IAST subResult) {
			if (n == 0) {
				result.append(subResult);
				return;
			}
			IASTAppendable temp;
			for (int j = 1; j < originalList.size(); j++) {
				temp = subResult.copyAppendable();
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
		 *            the current subList which should be inserted in the result list
		 */
		private void tuplesOfLists(final IAST originalList, final int k, IASTAppendable result, IAST subResult) {
			if (k == originalList.size()) {
				result.append(subResult);
				return;
			}
			IASTAppendable temp;
			IAST subAST = (IAST) originalList.get(k);
			for (int j = 1; j < subAST.size(); j++) {
				temp = subResult.copyAppendable();
				temp.append(subAST.get(j));
				tuplesOfLists(originalList, k + 1, result, temp);
			}

		}

	}

	private final static class YuleDissimilarity extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			int dim1 = ast.arg1().isVector();
			int dim2 = ast.arg2().isVector();
			if (dim1 == dim2 && dim1 > 0) {
				IAST u = (IAST) ast.arg1();
				IAST v = (IAST) ast.arg2();
				int length = u.size();
				int n10 = 0;
				int n01 = 0;
				int n00 = 0;
				int n11 = 0;
				IExpr x, y;
				for (int i = 1; i < length; i++) {
					x = u.get(i);
					y = v.get(i);
					if ((x.isOne() || x.isTrue()) && (y.isZero() || y.isFalse())) {
						n10++;
						continue;
					}
					if ((x.isZero() || x.isFalse()) && (y.isOne() || y.isTrue())) {
						n01++;
						continue;
					}
					if ((x.isZero() || x.isFalse()) && (y.isZero() || y.isFalse())) {
						n00++;
						continue;
					}
					if ((x.isOne() || x.isTrue()) && (y.isOne() || y.isTrue())) {
						n11++;
						continue;
					}
					if ((x.isZero() || x.isFalse() || x.isOne() || x.isTrue())
							&& (y.isZero() || y.isFalse() || y.isOne() || y.isTrue())) {
						continue;
					}
					return F.NIL;
				}

				long r = 2L * n10 * n01;
				if (r == 0L) {
					return F.C0;
				}
				return F.Divide(F.integer(r), F.integer((long) n11 * (long) n00 + (long) n10 * (long) n01));
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	final static Combinatoric CONST = new Combinatoric();

	public static Combinatoric initialize() {
		return CONST;
	}

	private Combinatoric() {

	}
}
