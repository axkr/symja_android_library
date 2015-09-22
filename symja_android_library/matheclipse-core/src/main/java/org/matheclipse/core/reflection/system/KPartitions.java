package org.matheclipse.core.reflection.system;

import java.util.Iterator;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Generate a list of all all k-partitions for a given list with N elements. <br/>
 * 
 * See <a href="http://en.wikipedia.org/wiki/Partition_of_a_set">Wikipedia - Partition of a set</a>
 * 
 */
public class KPartitions extends AbstractFunctionEvaluator {
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
	 * Example: KPartitionsIterable(3,5) gives the following sequences [0, 1, 2], [0, 1, 3], [0, 1, 4], [0, 2, 3], [0, 2, 4], [0, 3,
	 * 4] <br/>
	 * If you interpret these integer lists as indexes for a list {a,b,c,d,e} which should be partitioned into 3 parts the results
	 * are: <br/>
	 * {{{a},{b},{c,d,e}}, {{a},{b,c},{d,e}}, {{a},{b,c,d},{e}}, {{a,b},{c},{d,e}}, {{a,b},{c,d},{e}}, {{a,b,c},{d},{e}}}
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
				for (i = fNumberOfParts - 1; (i >= 0) && (fPartitionsIndex[i] >= fLength - fNumberOfParts + i); --i) {
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

		public KPartitionsList(final IAST list, final int kParts, IAST resultList) {
			this(list, kParts, resultList, 0);
		}

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
			IAST part = fResultList.clone();
			IAST temp;
			// System.out.println("Part:");
			int j = 0;
			for (int i = 1; i < partitionsIndex.length; i++) {
				// System.out.println(partitionsIndex[i] + ",");
				temp = fResultList.clone();
				for (int m = j; m < partitionsIndex[i]; m++) {
					temp.add(fList.get(m + fOffset));
				}
				j = partitionsIndex[i];
				part.add(temp);
			}

			temp = fResultList.clone();
			int n = fList.size() - fOffset;
			for (int m = j; m < n; m++) {
				temp.add(fList.get(m + fOffset));
			}
			part.add(temp);
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
				result.add(part);
			}
			return result;
		}
		return null;
	}
}
