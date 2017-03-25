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
import org.matheclipse.parser.client.ast.FunctionNode;
import org.matheclipse.parser.client.ast.IParserFactory;

/**
 * @@@ operator (not @@ operator)
 *
 */
public class ApplyOperator extends InfixOperator {
	public ApplyOperator(final String oper, final String functionName, final int precedence, final int grouping) {
		super(oper, functionName, precedence, grouping);
	}

	public ASTNode createFunction(final IParserFactory factory, final ASTNode lhs, final ASTNode rhs) {
		FunctionNode fn = factory.createFunction(factory.createSymbol("Apply"), lhs, rhs);
		if (fOperatorString.equals("@@")) {
			return fn;
		}
		fn.add(factory.createFunction(factory.createSymbol("List"), factory.createInteger(1)));
		return fn;
	}
}
