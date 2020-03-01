package org.matheclipse.core.eval.util;

/**
 * A Set specification definition for number of elements in a set.
 * 
 */
public class SetSpec {
	protected int fMinCount;

	protected int fMaxCount;

	protected int fCurrentCount;

	public SetSpec() {
		this(0);
	}

	/**
	 * Define a set specification for all elements containing elements less than <code>n</code>.
	 * 
	 * @param n
	 */
	public SetSpec(final int n) {
		this(n, n);
	} 

	public SetSpec(final int min, final int max ) {
		fMinCount = min;
		fMaxCount = max;
		fCurrentCount = 0;
	}

	/**
	 * Get the current counter
	 * 
	 */
	public final int getCurrentCounter() {
		return fCurrentCount;
	}

	public final void setMinCountAsCurrent() {
		fCurrentCount = fMinCount;
	}

	/**
	 * Increments the current counter
	 * 
	 */
	public final void incCurrentCounter() {
		fCurrentCount++;
	}

	/**
	 * Decrements the current counter
	 * 
	 */
	public final void decCurrentLevel() {
		fCurrentCount--;
	}

	public boolean isInRange() {
		return (fCurrentCount >= fMinCount) && (fCurrentCount <= fMaxCount);
	}

	/**
	 * @return the minimum counter
	 */
	public int getMinCount() {
		return fMinCount;
	}

	/**
	 * @return the maximum counter
	 */
	public int getMaxCount() {
		return fMaxCount;
	}

}
