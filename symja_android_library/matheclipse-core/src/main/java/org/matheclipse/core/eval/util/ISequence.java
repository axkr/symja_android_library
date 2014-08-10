package org.matheclipse.core.eval.util;

/**
 * Interface for sequence specifications.
 * 
 * A sequence in the interval of <code>integer</code> numbers:
 * <code>[getStart(), getStart()+getStep(), getStart()+2*getStep(), ..., getEnd()]</code>
 * 
 */
public interface ISequence {

	/**
	 * Get the end index for this sequence
	 * 
	 * @return the <code>endOffset</code> of this sequence + 1;
	 */
	public abstract int getEnd();

	/**
	 * Get the generated sequence of indices for this ISequence. Especially useful
	 * for unit tests to check the sequence implementation.
	 * 
	 * @return <code>[getStart(), getStart()+getStep(), getStart()+2*getStep(), ..., getEnd()]</code>
	 */
	public abstract int[] getIndices();

	/**
	 * Get the start index for this sequence
	 * 
	 * @return the start offset of this sequence;
	 */
	public abstract int getStart();

	/**
	 * The step for calculating the next index in the sequence.
	 * 
	 * @return the step size of this sequence;
	 */
	public abstract int getStep();

	/**
	 * Sets the list size of the current used list in the generics library.
	 * Sometimes this setting is used by special sequence implementations.
	 * 
	 * @param size
	 */
	public abstract void setListSize(int size);
}