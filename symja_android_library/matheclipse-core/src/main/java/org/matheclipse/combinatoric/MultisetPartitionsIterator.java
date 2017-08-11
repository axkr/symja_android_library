package org.matheclipse.combinatoric;

/**
 * Partition an ordered multi-set and visit all steps of the algorithm with a <code>IStepVisitor</code>
 * 
 * @see IStepVisitor
 */
public class MultisetPartitionsIterator {

	private final int n;
	private final int[] multiset;
	private final int[][] result;
	private RosenNumberPartitionIterator rosen;
	private final IStepVisitor handler;

	/**
	 * Partition an ordered multi-set and visit all steps of the algorithm with an <code>IStepVisitor</code>.
	 * 
	 * @param visitor
	 *            the visitor which controls the steps of the algorithm
	 * @param k
	 *            the number of partitioning the n elements into k parts
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
		while (rosen.hasNext()) {
			if (recursiveMultisetCombination(multiset, rosen.next(), 0)) {
				return false;
			}
		}
		return true;
	}

	private boolean recursiveMultisetCombination(int[] multiset, final int[] currentRosen, int i) {
		if (i < currentRosen.length) {
			final MultisetCombinationIterator iter = new MultisetCombinationIterator(multiset, currentRosen[i]);
			while (iter.hasNext()) {
				final int[] currentSubset = iter.next();
				result[i] = currentSubset;
				int[] wc = ArrayUtils.deleteSubset(multiset, currentSubset);
				if (recursiveMultisetCombination(wc, currentRosen, i + 1)) {
					return true;
				}
			}
			return false;
		}
		return !handler.visit(result);
	}
}
