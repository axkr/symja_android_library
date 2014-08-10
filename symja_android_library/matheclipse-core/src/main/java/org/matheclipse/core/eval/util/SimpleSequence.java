package org.matheclipse.core.eval.util;


/**
 * A simple sequence specification implementation.
 * 
 * A sequence in the interval of <code>integer</code> numbers:
 * <code>[getStart(), getStart()+getStep(), getStart()+getStep(), ..., getEnd()]</code>
 * 
 */
public class SimpleSequence extends AbstractSequence {
	public SimpleSequence(final int startOffset, final int endOffset) {
		this(startOffset, endOffset, 1);
	}

	public SimpleSequence(final int startOffset, final int endOffset, final int step) {
		super(startOffset, endOffset, step);
		if (startOffset < 0) {
			throw new WrongSequenceException("Wrong start offset:" + startOffset);
		}
		if (endOffset < 0) {
			throw new WrongSequenceException("Wrong end offset:" + endOffset);
		}
		if (step < 0) {
			throw new WrongSequenceException("Negative step:" + step);
		}
	}

	/**
	 * This method is not used in this sequence implementation.
	 */
	public void setListSize(int size) {
	}

	public int getEnd() {
		return fEndOffset + 1;
	}

	public int getStart() {
		return fStartOffset;
	}

}
