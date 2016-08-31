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

import org.matheclipse.parser.client.ast.ASTNode;
import org.matheclipse.parser.client.ast.IParserFactory;

public class SubtractOperator extends InfixOperator {
	public SubtractOperator(final String oper, final String functionName, final int precedence, final int grouping) {
		super(oper, functionName, precedence, grouping);
	}

	public ASTNode createFunction(final IParserFactory factory, final ASTNode lhs, final ASTNode rhs) {
		return factory.createFunction(factory.createSymbol("Plus"), lhs, factory.createFunction(factory.createSymbol("Times"), factory
				.createInteger(-1), rhs));

		// alternatively we can introduce a Subtract operator.
		// see http://code.google.com/p/symja/issues/detail?id=36
		// return factory.createFunction(factory.createSymbol("Subtract"), lhs,
		// rhs);
	}
}
