package org.matheclipse.core.eval.exception;

import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
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

	public static void throwNIL() {
		// unexpected NIL expression encountered.
		String str = IOFunctions.getMessage("nil", F.CEmptyList, EvalEngine.get());
		throw new ArgumentTypeException(str);
	}

	@Override
	public String getMessage(ISymbol symbol) {
		return symbol.toString() + ": " + fMessage;
	}

}
