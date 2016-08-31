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
import org.matheclipse.parser.client.ast.FractionNode;
import org.matheclipse.parser.client.ast.IParserFactory;
import org.matheclipse.parser.client.ast.IntegerNode;

public class DivideOperator extends InfixOperator {
	public DivideOperator(final String oper, final String functionName, final int precedence, final int grouping) {
		super(oper, functionName, precedence, grouping);
	}

	public ASTNode createFunction(final IParserFactory factory, final ASTNode lhs, final ASTNode rhs) {
		if (rhs instanceof IntegerNode) {
			if (lhs instanceof IntegerNode) {
				return new FractionNode((IntegerNode) lhs, (IntegerNode) rhs);
			}
			return factory.createFunction(factory.createSymbol("Times"), new FractionNode(IntegerNode.C1, (IntegerNode) rhs), lhs);
		}
		if (lhs.equals(IntegerNode.C1)) {
			return factory.createFunction(factory.createSymbol("Power"), rhs, factory.createInteger(-1));
		}
		return factory.createFunction(factory.createSymbol("Times"), lhs,
				factory.createFunction(factory.createSymbol("Power"), rhs, factory.createInteger(-1)));

	}
}
