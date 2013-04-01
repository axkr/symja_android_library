package org.matheclipse.generic;

import org.matheclipse.generic.interfaces.ISequence;

/**
 * Basic abstract class for sequence specifications.
 * 
 * A sequence in the interval of <code>integer</code> numbers:
 * <code>[getStart(), getStart()+getStep(), getStart()+getStep(), ..., getEnd()]</code>
 * 
 */
public abstract class AbstractSequence implements ISequence {

	protected final int fStartOffset;

	protected final int fEndOffset;

	protected final int fStep;

	protected AbstractSequence(int startOffset, int endOffset, int step) {
		super();
		fStartOffset = startOffset;
		fEndOffset = endOffset;
		fStep = step;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getStep() {
		return fStep;
	}

	/**
	 * {@inheritDoc}
	 */
	public int[] getIndices() {
		int j = getStart();
		int step = getStep();
		int end = getEnd();
		int alloc = end - j;
		if (alloc % step == 0) {
			alloc = alloc / step - 1;
		} else {
			alloc = alloc / step;
		}
		int k = 0;
		int[] result = new int[alloc + 1];
		for (int i = j; i < end; i += step) {
			result[k++] = i;
		}
		return result;

	}

}