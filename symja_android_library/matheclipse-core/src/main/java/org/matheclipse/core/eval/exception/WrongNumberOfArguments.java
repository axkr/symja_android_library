package org.matheclipse.core.eval.exception;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.parser.client.math.MathException;

/**
 */
public class WrongNumberOfArguments extends MathException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 754625729654866796L;

	int fAllowed;

	int fCurrent;

	IAST fExpr;
	
	int fTextNumber;

	/**
	 * Expected number of arguments: {@code allowed} but got {@code current}
	 * arguments:<br/> {@code expr}.
	 * 
	 * @param expr
	 * @param allowed
	 * @param current
	 */
	public WrongNumberOfArguments(final IAST expr, final int allowed, final int current) {
		fAllowed = allowed;
		fCurrent = current;
		fExpr = expr;
		fTextNumber = 0;
	}

	/**
	 * Expected number of arguments: {@code allowed} but got {@code current}
	 * arguments:<br/> {@code expr}.
	 * 
	 * @param expr
	 * @param allowed
	 * @param current
	 */
	public WrongNumberOfArguments(final int textNumber, final IAST expr,final int current) {
		fAllowed = 0;
		fCurrent = current;
		fExpr = expr;
		fTextNumber = textNumber;
	}
	
	@Override
	public String getMessage() {
		switch(fTextNumber){
		case 0:
			return "Expected number of arguments: " + fAllowed + " but got " + fCurrent + " arguments:\n" + fExpr.toString();
		case 1:
			return "Expected even number of arguments but got " + fCurrent + " arguments:\n" + fExpr.toString();
		case 2:
			return "Expected odd number of arguments but got " + fCurrent + " arguments:\n" + fExpr.toString();
		}
		return "Wrong text number: " + fAllowed + " but got " + fCurrent + " arguments:\n" + fExpr.toString();
	}

}
