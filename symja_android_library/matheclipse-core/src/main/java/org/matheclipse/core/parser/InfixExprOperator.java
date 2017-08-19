package org.matheclipse.core.parser;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

class InfixExprOperator extends AbstractExprOperator {
	private int fGrouping;

	public final static int NONE = 0;

	public final static int RIGHT_ASSOCIATIVE = 1;

	public final static int LEFT_ASSOCIATIVE = 2;

	public InfixExprOperator(final String oper, final String functionName, final int precedence, final int grouping) {
		super(oper, functionName, precedence);
		fGrouping = grouping;
	}

	/**
	 * Return the grouping of the Infix-Operator (i.e. NONE,LEFT_ASSOCIATIVE,
	 * RIGHT_ASSOCIATIVE)
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