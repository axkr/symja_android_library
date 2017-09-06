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
package org.matheclipse.parser.client.eval;

import org.matheclipse.parser.client.ast.ASTNode;
import org.matheclipse.parser.client.math.MathUtils;

/**
 * 
 */
final public class DoubleNode extends ASTNode {

	private final double value;

	public DoubleNode(double value) {
		super("DoubleNode");
		this.value = value;
	}

	public double doubleValue() {
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof DoubleNode) {
			return value == ((DoubleNode) obj).value;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return MathUtils.hash(value);// Double.doubleToLongBits(value);
		// return (int)(bits ^ (bits >>> 32));
	}

	@Override
	public String toString() {
		return Double.toString(value);
	}

}
