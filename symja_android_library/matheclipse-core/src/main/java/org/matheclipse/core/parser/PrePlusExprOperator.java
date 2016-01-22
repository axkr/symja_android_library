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

import org.matheclipse.core.interfaces.IExpr;

public class PrePlusExprOperator extends PrefixExprOperator {

	public PrePlusExprOperator(final String oper, final String functionName, final int precedence) {
		super(oper, functionName, precedence);
	}

	public IExpr createFunction(final IExprParserFactory factory, final IExpr argument) {
		return argument;
	}
}
