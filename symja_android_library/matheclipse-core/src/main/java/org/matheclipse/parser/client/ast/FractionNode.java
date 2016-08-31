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
 * A node for a parsed fraction string
 * 
 */
public class FractionNode extends NumberNode {
	protected final IntegerNode fNumerator;

	protected final IntegerNode fDenominator;

	public FractionNode(final IntegerNode numerator, final IntegerNode denominator) {
		super(null);
		fNumerator = numerator;
		fDenominator = denominator;
	}

	public IntegerNode getDenominator() {
		return fDenominator;
	}

	public IntegerNode getNumerator() {
		return fNumerator;
	}

	public String toString() {
		final StringBuffer buff = new StringBuffer();
		if (sign) {
			buff.append('-');
		}
		if (fNumerator != null) {
			buff.append(fNumerator.toString());
		}
		buff.append('/');
		if (fDenominator != null) {
			buff.append(fDenominator.toString());
		}
		return buff.toString();
	}

	public double doubleValue() {
		double numer = Double.parseDouble(fNumerator.toString());
		double denom = Double.parseDouble(fDenominator.toString());
		if (sign) {
			return -1.0 * numer / denom;
		}
		return numer / denom;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof FractionNode) {
			return fNumerator.equals(((FractionNode) obj).fNumerator) && fDenominator.equals(((FractionNode) obj).fDenominator)
					&& sign == ((FractionNode) obj).sign;
		}
		return false;
	}

	public int hashCode() {
		if (sign) {
			return fNumerator.hashCode() + fDenominator.hashCode() * 17;
		}
		return fNumerator.hashCode() + fDenominator.hashCode();
	}
}
