package org.matheclipse.core.parser;

abstract class AbstractExprOperator {
	protected String fFunctionName;

	protected String fOperatorString;

	protected int fPrecedence;

	public AbstractExprOperator(final String oper, final String functionName, final int precedence) {
		fOperatorString = oper;
		fFunctionName = functionName;
		fPrecedence = precedence;
	}

	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof AbstractExprOperator) {
			return fFunctionName.equals(((AbstractExprOperator) obj).fFunctionName);
		}
		return false;
	}

	/**
	 * @return the name of the head of the associated function
	 */
	public String getFunctionName() {
		return fFunctionName;
	}

	/**
	 * @return the operator string of this operator
	 */
	public String getOperatorString() {
		return fOperatorString;
	}

	/**
	 * @return the precedence of this operator
	 */
	public int getPrecedence() {
		return fPrecedence;
	}

	/**
	 * @return the hashCode of the function name
	 */
	public int hashCode() {
		return fFunctionName.hashCode();
	}

	public String toString() {
		return "[" + fFunctionName + "," + fOperatorString + "," + fPrecedence + "]";
	}

}