package org.matheclipse.core.eval.exception;

import org.matheclipse.core.expression.Context;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class RuleCreationError extends ValidateException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4289111239388531874L;

	IExpr fLHS = null;

	public RuleCreationError(final IExpr lhs) {
		fLHS = lhs;
	}

	@Override
	public String getMessage() {
		if (fLHS == null) {
			return "Operation not allowed in server mode.";
		}
		// if (fCondition != null) {
		// return "Error in rule creation: Condition not allowed in rules containing no pattern (" + fLHS.toString()
		// + " " + fRHS.toString() + " " + fCondition.toString() + ")";
		// }
		// if (fRHS != null) {
		// return "Error in rule creation: " + fLHS.toString() + " " + fRHS.toString();
		// }
		Context context = fLHS.topHead().getContext();
		return "Not allowed left-hand-side expression: \"" + fLHS.toString() + "\" from context \"" + context.toString()
				+ "\"\nPlease use names which aren't predefined by the system.";
	}

	@Override
	public String getMessage(ISymbol symbol) {
		if (fLHS == null) {
			return symbol.toString() + ": " + "Operation not allowed in server mode.";
		}
		Context context = fLHS.topHead().getContext();
		return symbol.toString() + ": " + "Not allowed left-hand-side expression: \"" + fLHS.toString()
				+ "\" from context \"" + context.toString()
				+ "\"\nPlease use names which aren't predefined by the system.";
	}

}
