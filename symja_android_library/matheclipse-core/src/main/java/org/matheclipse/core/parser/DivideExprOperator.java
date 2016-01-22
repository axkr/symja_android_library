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
import org.matheclipse.core.interfaces.IInteger;

public class DivideExprOperator extends InfixExprOperator {
	public DivideExprOperator(final String oper, final String functionName, final int precedence, final int grouping) {
		super(oper, functionName, precedence, grouping);
	}

	public IExpr createFunction(final IExprParserFactory factory, final IExpr lhs, final IExpr rhs) {
		if (rhs.isInteger() && !rhs.isZero()) {
			if (lhs.isInteger()) {
				return F.fraction((IInteger) lhs, (IInteger) rhs);
			}
			return F.Times(F.fraction(F.C1, (IInteger) rhs), lhs);
		}
		if (lhs.equals(F.C1)) {
			return F.Power(rhs, F.CN1);
		}
		if (rhs.isPower() && ((IAST) rhs).arg2().isNumber()) {
			return F.Times(lhs, F.Power(((IAST) rhs).arg1(), ((IAST) rhs).arg2().negate()));
		}
		return F.Times(lhs, F.Power(rhs, F.CN1));
	}
}
