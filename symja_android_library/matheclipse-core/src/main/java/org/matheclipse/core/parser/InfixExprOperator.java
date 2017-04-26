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
package org.matheclipse.core.parser;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class InfixExprOperator extends AbstractExprOperator {
	private int fGrouping;

	public final static int NONE = 0;

	public final static int RIGHT_ASSOCIATIVE = 1;

	public final static int LEFT_ASSOCIATIVE = 2;

	public InfixExprOperator(final String oper, final String functionName, final int precedence, final int grouping) {
		super(oper, functionName, precedence);
		fGrouping = grouping;
	}

	/**
	 * Return the grouping of the Infix-Operator (i.e. NONE,LEFT_ASSOCIATIVE, RIGHT_ASSOCIATIVE)
	 * 
	 * @return
	 */
	public int getGrouping() {
		return fGrouping;
	}

	public IExpr createFunction(final IExprParserFactory factory, ExprParser parser, final IExpr lhs, final IExpr rhs) {
		if (fOperatorString.equals("//")) {
			// lhs // rhs ==> rhs[lhs]
			IAST function = F.ast(rhs);
			function.append(lhs);
			return function;
		}
		IAST function = F.ast(F.$s(getFunctionName()), 10, false);
		function.append(lhs);
		function.append(rhs);
		return function;
	}
}
