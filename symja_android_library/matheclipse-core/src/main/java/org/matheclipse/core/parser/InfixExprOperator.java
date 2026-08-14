package org.matheclipse.core.parser;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.Scanner;
import org.matheclipse.parser.client.ast.IParserFactory;
import org.matheclipse.parser.client.operator.Operator;

class InfixExprOperator extends ExprOperator {
  private int fGrouping;

  /**
   * Whether this is one of the six comparison operators. Decided once, from the operator's own
   * token, so that the chaining loop can ask the question of the operator rather than of the token
   * text - the two differ for a unicode spelling.
   */
  private final boolean fComparator;

  public static final int NONE = 0;

  public static final int RIGHT_ASSOCIATIVE = 1;

  public static final int LEFT_ASSOCIATIVE = 2;

  public InfixExprOperator(final String oper, final String functionName, final int precedence,
      final int grouping) {
    super(oper, functionName, precedence);
    fGrouping = grouping;
    fComparator = Scanner.isComparatorOperator(oper);
  }

  /**
   * @return <code>true</code> if a chain mixing this operator with another comparison operator
   *         becomes an <code>Inequality(...)</code>
   */
  public boolean isComparator() {
    return fComparator;
  }

  /**
   * Return the grouping of the Infix-Operator (i.e. NONE,LEFT_ASSOCIATIVE, RIGHT_ASSOCIATIVE)
   *
   * @return
   */
  public int getGrouping() {
    return fGrouping;
  }

  public IASTMutable createFunction(final IParserFactory factory, ExprParser parser,
      final IExpr lhs, final IExpr rhs) {
    if (fOperatorString.equals("//")) {
      // lhs // rhs ==> rhs[lhs]
      IASTAppendable function = F.ast(rhs);
      function.append(lhs);
      return function;
    }
    IASTAppendable function = F.ast(headSymbol(), 10);
    function.append(lhs);
    function.append(rhs);
    return function;
  }

  public IAST endFunction(final IParserFactory factory, final IAST function,
      final Scanner scanner) {
    return function;
  }
}
