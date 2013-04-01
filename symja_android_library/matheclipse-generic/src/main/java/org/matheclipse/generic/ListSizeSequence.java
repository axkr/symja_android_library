package org.matheclipse.generic;

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
		if (step < 0) {
			throw new WrongSequenceException("Negative step:" + step);
		}
		fHeadOffset = headOffset;
		fListSize = Integer.MIN_VALUE;
	}

	/**
	 * Set the current lists size. If no default <code>fEndOffset</code> is set
	 * the current list size is used as the end offset (exclusive)
	 */
	public void setListSize(int size) {
		fListSize = size;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getStart() {
		if (fEndOffset < 0) {
			if (fStartOffset < 0) {
				// final int tailOffset = fList.size() + fFrom;
				final int tailOffset = fListSize + fStartOffset;
				if (tailOffset >= 0) {
					return tailOffset;
				}
			}
			return fHeadOffset;
		} else {
			if (fStartOffset >= 0) {
				return fStartOffset;
			}
		}
		throw new WrongSequenceException("Wrong start offset:" + fStartOffset);
	}

	/**
	 * {@inheritDoc}
	 */
	public int getEnd() {
		if (fEndOffset < 0) {
			if (fStartOffset < 0) {
				return fListSize;
			}
			if ((fStartOffset + fHeadOffset) <= fListSize) {
				return fStartOffset + fHeadOffset;
			}
			throw new WrongSequenceException("Wrong end offset:" + fEndOffset);
		}
		return fEndOffset + 1;
	}

}
