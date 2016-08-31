/*
 * Copyright 2005-2008 Axel Kramer (axelclk@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.matheclipse.parser.client.operator;

public abstract class Operator {
	protected String fFunctionName;

	protected String fOperatorString;

	protected int fPrecedence;

	public Operator(final String oper, final String functionName, final int precedence) {
		fOperatorString = oper;
		fFunctionName = functionName;
		fPrecedence = precedence;
	}

	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof Operator) {
			return fFunctionName.equals(((Operator) obj).fFunctionName);
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
