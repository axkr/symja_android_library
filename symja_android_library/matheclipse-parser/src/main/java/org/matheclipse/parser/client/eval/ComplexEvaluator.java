/*
 * Copyright 2005-2009 Axel Kramer (axelclk@gmail.com)
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

import org.matheclipse.parser.client.eval.api.IASTVisitor;
import org.matheclipse.parser.client.eval.api.ObjectEvaluator;
import org.matheclipse.parser.client.math.Complex;

/**
 * Evaluate math expressions to {@code Complex} numbers.
 * 
 */
public class ComplexEvaluator extends ObjectEvaluator<Complex, ComplexVariable, Complex> {
	public ComplexEvaluator() {
		this(new ComplexEvalVisitor());
	}

	public ComplexEvaluator(IASTVisitor<Complex, ComplexVariable, Complex> visitor) {
		super(visitor);
	}

	/**
	 * Returns a <code>String</code> representation of the given
	 * <code>Complex</code> number.
	 * 
	 * @param c
	 * @return
	 * 
	 */
	public static String toString(Complex c) {
		double real = c.getReal();
		double imag = c.getImaginary();
		if (imag == 0.0) {
			return Double.valueOf(real).toString();
		} else {
			if (imag >= 0.0) {
				return Double.valueOf(real).toString() + "+I*" + Double.valueOf(imag).toString();
			} else {
				return Double.valueOf(real).toString() + "+I*(" + Double.valueOf(imag).toString() + ")";
			}
		}
	}

}
