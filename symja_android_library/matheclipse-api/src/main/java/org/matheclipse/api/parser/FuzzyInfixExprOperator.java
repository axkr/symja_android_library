package org.matheclipse.api.parser;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.ast.IParserFactory;
import org.matheclipse.parser.client.operator.Operator;

class FuzzyInfixExprOperator extends Operator {
  private int fGrouping;

  public static final int NONE = 0;

  public static final int RIGHT_ASSOCIATIVE = 1;

  public static final int LEFT_ASSOCIATIVE = 2;

  public FuzzyInfixExprOperator(final String oper, final String functionName, final int precedence,
      final int grouping) {
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

  public IASTMutable createFunction(final IParserFactory factory, FuzzyParser parser,
      final IExpr lhs, final IExpr rhs) {
    if (fOperatorString.equals("//")) {
      // lhs // rhs ==> rhs[lhs]
      IASTAppendable function = F.ast(rhs);
      function.append(lhs);
      return function;
    }
    IASTAppendable function = F.ast(F.$s(getFunctionName()), 10, false);
    function.append(lhs);
    function.append(rhs);
    return function;
  }
}
