package org.matheclipse.combinatoric;

/**
 * Abstract step visitor for <code>int</code> multisets.
 * 
 */
public abstract class AbstractMultsetIntStepVisitor implements IStepVisitor {
	protected int[] multiset;

	public AbstractMultsetIntStepVisitor(int[] multiset) {
		this.multiset = multiset;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.matheclipse.combinatoric.IStepVisitor#visit(int[][])
	 */
	public boolean visit(int[][] result) {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.matheclipse.combinatoric.IStepVisitor#visit(int[][])
	 */
	public boolean visit(int[] result) {
		return true;
	}
	
	public int[] getMultisetArray() {
		return multiset;
	}
}
