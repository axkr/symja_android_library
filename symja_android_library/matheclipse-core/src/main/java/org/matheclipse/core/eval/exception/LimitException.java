package org.matheclipse.core.eval.exception;

import org.matheclipse.core.basic.Config;
import org.matheclipse.parser.client.math.MathException;

/**
 * Base exception for exceptions, which are used to implement &quot;limit control&quot; functions like for example
 * <code>ASTElementLimitExceeded</code> and <code>RecursionLimitExceeded</code> .
 * 
 * 
 */
public abstract class LimitException extends MathException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8898766046639353179L;

	/**
	 * Constructs a new FlowControlException with the specified detail <code>message=null</code>,
	 * <code>cause=null</code>, <code>enableSuppression=false</code>, and <code>writableStackTrace=false</code> .
	 * 
	 */
	public LimitException() {
		super();
	}
}
