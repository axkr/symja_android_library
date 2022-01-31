package org.matheclipse.api.parser;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.ast.IParserFactory;
import org.matheclipse.parser.client.operator.Operator;

class FuzzyPostfixExprOperator extends Operator {

  public FuzzyPostfixExprOperator(final String oper, final String functionName,
      final int precedence) {
    super(oper, functionName, precedence);
  }

  public IExpr createFunction(final IParserFactory factory, final IExpr argument) {
    return F.$(F.$s(getFunctionName()), argument);
  }
}
