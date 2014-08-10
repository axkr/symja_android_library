package org.matheclipse.combinatoric;

public interface IStepVisitor {

	/**
	 * Get the current result of the combinatorics algorithm step.
	 * 
	 * @param result
	 * @return <code>false</code> if the combinatorics algorithm should stop
	 *         immediately; <code>true</code> otherwise.
	 */
	public abstract boolean visit(int[][] result);
 
	/**
	 * Get the current result of the combinatorics algorithm step.
	 * 
	 * @param result
	 * @return <code>false</code> if the combinatorics algorithm should stop
	 *         immediately; <code>true</code> otherwise.
	 */
	public abstract boolean visit(int[] result);
	
	/**
	 * Get the sorted multi-set array indices, which should be transformed.
	 * 
	 * @return
	 */
	public abstract int[] getMultisetArray();
}