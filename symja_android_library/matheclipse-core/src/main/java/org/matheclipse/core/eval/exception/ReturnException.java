package org.matheclipse.core.eval.exception;

import javax.annotation.Nonnull;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

public class ReturnException extends FlowControlException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6165872840807864554L;

	public final static ReturnException RETURN_FALSE = new ReturnException(F.False);
	
	public final static ReturnException RETURN_TRUE = new ReturnException(F.True);
	
	final private IExpr value;

	public ReturnException() {
		this(F.Null);
	}

	public ReturnException(@Nonnull final IExpr val) {
		super("Return from a function definition.");
		value = val;
	}

	public IExpr getValue() {
		return value;
	}
}
