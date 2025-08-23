/*
 * Copyright 2005-2008 Axel Kramer (axelclk@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.matheclipse.parser.wma;

import org.matheclipse.parser.client.Scanner;
import org.matheclipse.parser.client.ast.ASTNode;
import org.matheclipse.parser.client.ast.FunctionNode;
import org.matheclipse.parser.client.ast.INodeParserFactory;
import org.matheclipse.parser.client.operator.Operator;

public class WMAInfixOperator extends Operator {
  private int fGrouping;

  public WMAInfixOperator(final String oper, final String functionName, final int precedence,
      final int grouping) {
    super(oper, functionName, precedence);// WMAOperator.INFIX_OPERATOR);
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

  public FunctionNode createFunction(final INodeParserFactory factory, final ASTNode lhs,
      final ASTNode rhs) {
    if (fOperatorString.equals("//")) {
      // lhs // rhs ==> rhs[lhs]
      return factory.unaryAST(rhs, lhs);
    }
    return factory.createFunction(factory.createSymbol(getFunctionName()), lhs, rhs);
  }

  /**
   * At the end of parsing infix operators with multiple arguments this method will be called.
   *
   * @param factory
   * @param function
   * @param scanner can throw SyntaxError exceptions if necessary
   * @return
   */
  public FunctionNode endFunction(final INodeParserFactory factory, final FunctionNode function,
      final Scanner scanner) {
    return function;
  }
}
