package org.matheclipse.parser.client.math;

import javax.naming.ldap.ControlFactory;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.exception.FlowControlException;
import org.matheclipse.core.interfaces.IExpr;

public class MathException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3520033778672500363L;

	/**
	 * @param exprs
	 * @return exception with message consisting of truncated string expressions of given tensors
	 * @throws Exception
	 *             if any of the listed tensors is null
	 */
	public static MathException of(IExpr... exprs) {
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < exprs.length; i++) {
			buf.append(exprs[i].toString());
			if (i < exprs.length - 1) {
				buf.append(", ");
			}
		}
		return new MathException(buf.toString());
	}

	/**
	 * Constructs a new MathException with the specified detail <code>message=null</code>, <code>cause=null</code>,
	 * <code>enableSuppression=false</code>, and <code>writableStackTrace=false</code> .
	 * 
	 * @see FlowControlException
	 */
	public MathException() {
		super(null, null, false, false);
	}

	public MathException(String message) {
		super(message);
	}

	public MathException(String message, Throwable cause) {
		super(message, cause);
	}

	public MathException(Throwable cause) {
		super(cause);
	}

	@Override
	public synchronized Throwable fillInStackTrace() {
		if (Config.SHOW_STACKTRACE) {
			// doesn't fill the stack for FlowControlExceptions
			return super.fillInStackTrace();
		} else {
			return this;
		}
	}
}