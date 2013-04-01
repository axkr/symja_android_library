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
import org.matheclipse.parser.client.math.Complex;
import org.matheclipse.parser.client.math.MathUtils;

/**
 * 
 */
public class ComplexNode extends ASTNode {

	private final Complex value; 

	public ComplexNode(Complex comp) {
		super("ComplexNode");
		this.value = comp;
	}

	public ComplexNode(double real) {
		super("ComplexNode");
		this.value = new Complex(real, 0.0);
	}

	public ComplexNode(double real, double imag) {
		super("ComplexNode");
		this.value = new Complex(real, imag);
	}

	public Complex complexValue() {
		return value;
	}

	public String toString() {
		return ComplexEvaluator.toString(value);
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof ComplexNode) {
			return value == ((ComplexNode) obj).value;
		}
		return false;
	}

	public int hashCode() {
		long rbits = MathUtils.hash(value.getReal());// Double.doubleToLongBits(value.getReal());
		long ibits = MathUtils.hash(value.getImaginary());// Double.doubleToLongBits(value.getImaginary());
		return (int) (rbits ^ (ibits >>> 32));
	}
}
