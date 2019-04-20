package org.matheclipse.core.eval.exception;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Exception for the <code>Throw[]</code> function.
 *
 */
public class ThrowException extends FlowControlException {

	public final static ThrowException THROW_FALSE = new ThrowException(F.False);

	public final static ThrowException THROW_TRUE = new ThrowException(F.True);
	/**
	 * 
	 */
	private static final long serialVersionUID = -8468658696375569705L;

	final private IExpr value;
	final private IExpr tag;

	public ThrowException() {
		this(null);
	}

	public ThrowException(final IExpr val) {
		this(val, F.None);
	}

	public ThrowException(final IExpr val, final IExpr tag) {
		super();
		this.value = val;
		this.tag = tag;
	}

	public IExpr getValue() {
		return value;
	}
	
	public IExpr getTag() {
		return tag;
	}
}
