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
package org.matheclipse.parser.client.ast;

/**
 * A node for a parsed integer string
 * 
 */
public class IntegerNode extends NumberNode {
	public final static IntegerNode C1 = new IntegerNode("1", 10);

	private final int fNumberFormat; 

	private final int fIntValue;

	public IntegerNode(final String value) {
		this(value, 10);
	}

	public IntegerNode(final String value, final int numberFormat) {
		super(value);
		fNumberFormat = numberFormat;
		fIntValue = 0;
	}

	public IntegerNode(final int intValue) {
		super(null);
		fNumberFormat = 10;
		fIntValue = intValue;
	}

	public String toString() {
		if (fStringValue == null) {
			if (sign) {
				return Integer.toString(fIntValue * (-1));
			}
			return Integer.toString(fIntValue);
		}
		if (sign) {
			return "-" + fStringValue;
		}
		return fStringValue;
	}

	public int getNumberFormat() {
		return fNumberFormat;
	}

	public int getIntValue() {
		return fIntValue;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof IntegerNode) {
			if (fStringValue == null) {
				return toString().equals(obj.toString());
			}
			return fStringValue.equals(((NumberNode) obj).fStringValue) && sign == ((NumberNode) obj).sign;
		}
		return false;
	}

	public int hashCode() {
		if (fStringValue == null) {
			return toString().hashCode();
		}
		return super.hashCode();
	}
}
