package org.matheclipse.core.eval.util;

/**
 * Exception for wrong <code>ISequence</code> definitions.
 * 
 */
public class WrongSequenceException extends RuntimeException {
	private static final long serialVersionUID = 1249168527105735837L;

	public WrongSequenceException(final String message) {
		super(message);
	}
}
