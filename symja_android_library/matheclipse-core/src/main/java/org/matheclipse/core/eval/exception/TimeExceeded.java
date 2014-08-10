package org.matheclipse.core.eval.exception;

import org.matheclipse.parser.client.math.MathException;

public class TimeExceeded extends MathException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3341162294495378861L;

	public TimeExceeded() {
		super("[Time exceeded] Evaluation stopped.\n");
	}
}
