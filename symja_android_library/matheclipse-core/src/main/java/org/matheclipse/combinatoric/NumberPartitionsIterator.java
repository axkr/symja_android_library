package org.matheclipse.combinatoric;

/**
 * Partition a set and visit all steps of the algorithm with an <code>IStepVisitor</code>
 * 
 * @see IStepVisitor
 */
public class NumberPartitionsIterator {
	private final int[][] result;
	private RosenNumberPartitionIterator rosen;
	private IStepVisitor handler;

	/**
	 * Partition an ordered multi-set and visit all steps of the algorithm with an <code>IStepVisitor</code>.
	 * 
	 * @param visitor
	 *            the visitor which controls the steps of the algorithm
	 * @param k
	 *            the number of partitioning the n elements into k parts
	 */
	public NumberPartitionsIterator(IStepVisitor visitor, final int n, final int k) {
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
			int counter = 0;
			final int[] currentRosen = rosen.next();
			for (int i = 0; i < currentRosen.length; i++) {
				result[i] = new int[currentRosen[i]];
				for (int j = 0; j < currentRosen[i]; j++) {
					result[i][j] = counter++;
				}
				// Status status = handler.verify(i, result);
				// if (status != Status.OK) {
				// if (status == Status.FORWARD) {
				// continue;
				// } else if (status == Status.BACKTRACK) {
				// continue;
				// }
				// return false;
				// }
			}
			if (!handler.visit(result)) {
				return false;
			}
		}
		return true;

	}

}
