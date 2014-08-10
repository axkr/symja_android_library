package org.matheclipse.core.eval.exception;

import org.matheclipse.core.interfaces.IExpr;

public class ConditionException extends FlowControlException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1175359074220162860L;

	final private IExpr value;

	public ConditionException(final IExpr val) {
		super("Condition[] exception.");
		value = val;
	}

	public IExpr getValue() {
		return value;
	}

	@Override
	public String getMessage() {
		return "Condition[] exception: " + value.toString();
	}
}
