package org.matheclipse.core.eval.exception;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

public class ConditionException extends FlowControlException {

	public final static ConditionException CONDITION_NIL = new ConditionException(F.NIL);
	/**
	 * 
	 */
	private static final long serialVersionUID = -1175359074220162860L;

	final private IExpr value;

	public ConditionException(final IExpr val) {
		super();
		value = F.NIL;
	}

	public IExpr getValue() {
		return value;
	}

	@Override
	public String getMessage() {
		return "Condition[] exception: " + value.toString();
	}
}
