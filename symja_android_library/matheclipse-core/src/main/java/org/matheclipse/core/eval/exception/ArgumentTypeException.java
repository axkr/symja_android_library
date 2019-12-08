package org.matheclipse.core.eval.exception;

import org.matheclipse.core.interfaces.ISymbol;

/**
 */
public class ArgumentTypeException extends ValidateException {

	private static final long serialVersionUID = 4017342168597803850L;

	final String fMessage;

	public ArgumentTypeException(String message) {
		fMessage = message;
	}

	@Override
	public String getMessage() {
		return fMessage;
	}

	public String getMessage(ISymbol symbol) {
		return symbol.toString() + ": " + fMessage;
	}

}
