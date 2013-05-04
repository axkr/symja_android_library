package org.matheclipse.combinatoric;

import org.matheclipse.combinatoric.util.ArrayUtils;

/**
 * Partition an ordered multi-set and visit all steps of the algorithm with a
 * <code>IStepVisitor</code>
 * 
 * @see IStepVisitor
 */
public class MultisetPartitionsIterator {

	private final int n;
	private final int[] multiset;
	private final int[][] result;
	private RosenNumberPartitionIterator rosen;
	private IStepVisitor handler;

	/**
	 * Partition an ordered multi-set and visit all steps of the algorithm with an
	 * <code>IStepVisitor</code>.
	 * 
	 * @param visitor
	 *          the visitor which controls the steps of the algorithm
	 * @param k
	 *          the number of partitioning the n elements into k parts
	 */
	public MultisetPartitionsIterator(IStepVisitor visitor, final int k) {
		int[] mset = visitor.getMultisetArray();
		this.n = mset.length;
		this.multiset = mset;
		this.result = new int[k][];
		this.rosen = new RosenNumberPartitionIterator(n, k);
		this.handler = visitor;
	}

	public void reset() {
		rosen.reset();
		for (int i = 0; i < result.length; i++) {
			result[i] = null;
		}
	}

	public boolean execute() {
		try {
			while (rosen.hasNext()) {
				final int[] actualRosen = rosen.next();
				recursiveMultisetCombination(multiset, actualRosen, 0);
			}
		} catch (StopException e) {
			// stop this algorithm immediately
			return false;
		}
		return true;

	}

	private void recursiveMultisetCombination(int[] multiset, final int[] actualRosen, int i) throws StopException {
		if (i < actualRosen.length) {
			final MultisetCombinationIterator iter = new MultisetCombinationIterator(multiset, actualRosen[i]);
			while (iter.hasNext()) {
				final int[] currentSubset = iter.next();
				result[i] = currentSubset;
				// Status status = handler.verify(i, result);
				// if (status != Status.OK) {
				// if (status == Status.FORWARD) {
				// continue;
				// } else if (status == Status.BACKTRACK) {
				// return;
				// }
				// throw new StopException();
				// }
				int[] wc = ArrayUtils.deleteSubset(multiset, currentSubset);
				recursiveMultisetCombination(wc, actualRosen, i + 1);
			}
		} else {
			if (!handler.visit(result)) {
				throw new StopException();
			}
		}
	}
}
