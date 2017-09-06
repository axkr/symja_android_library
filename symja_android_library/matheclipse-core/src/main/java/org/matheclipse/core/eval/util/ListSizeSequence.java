package org.matheclipse.core.eval.util;

/**
 * A sequence specification.
 * 
 */
public class ListSizeSequence extends AbstractSequence {
	protected final int fHeadOffset;

	protected int fListSize;

	public ListSizeSequence() {
		this(0);
	}

	public ListSizeSequence(final int startOffset) {
		this(startOffset, Integer.MIN_VALUE, 1, 0);
	}

	public ListSizeSequence(final int startOffset, final int endOffset) {
		this(startOffset, endOffset, 1, 0);
	}

	public ListSizeSequence(final int startOffset, final int endOffset, final int step) {
		this(startOffset, endOffset, step, 0);
	}

	public ListSizeSequence(final int startOffset, final int endOffset, final int step, final int headOffset) {
		super(startOffset, endOffset, step);
		// if (step < 0) {
		// throw new WrongSequenceException("Negative step:" + step);
		// }
		fHeadOffset = headOffset;
		fListSize = Integer.MIN_VALUE;
	}

	/**
	 * Set the current lists size. If no default <code>fEndOffset</code> is set
	 * the current list size is used as the end offset (exclusive)
	 */
	@Override
	public void setListSize(int size) {
		fListSize = size;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getStart() {
		if (fStartOffset >= 0) {
			return fStartOffset;
		}
		return fListSize + fStartOffset;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getEnd() {
		if (fEndOffset < 0) {
			return fListSize + fEndOffset + 1;
		}
		if (fEndOffset > fListSize) {
			return fListSize;
		}
		return fEndOffset + 1;
	}

}
