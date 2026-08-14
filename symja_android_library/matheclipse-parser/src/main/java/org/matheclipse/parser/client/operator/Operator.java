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
package org.matheclipse.parser.client.operator;

public abstract class Operator {

  /**
   * Constants for associativity.
   *
   * <p>
   * <b>These are not the values {@link InfixOperator} uses.</b> That subclass declares its own
   * {@code NONE = 0}, {@code RIGHT_ASSOCIATIVE = 1}, {@code LEFT_ASSOCIATIVE = 2}, so the same name
   * means a different number depending on the static type through which it is read, and two of the
   * numbers collide outright: {@code Operator.LEFT_ASSOCIATIVE} equals {@code InfixOperator.NONE},
   * and {@code Operator.FLAT} equals {@code InfixOperator.LEFT_ASSOCIATIVE}.
   *
   * <p>
   * Storing a grouping through one set and reading it through the other therefore silently changes
   * it. The experimental WMA parser fork did exactly that - it assigned with the constants below
   * and compared against {@code InfixOperator}'s, so left-associative operators behaved as flat -
   * which is part of why that fork was removed. Nothing uses these constants any more; anything
   * building an {@link InfixOperator} must use {@code InfixOperator}'s own.
   */
  public static final int LEFT_ASSOCIATIVE = 0;
  public static final int RIGHT_ASSOCIATIVE = 1;
  public static final int FLAT = 2;
  public static final int NON_ASSOCIATIVE = 3;

  private final String fFunctionName;

  protected final String fOperatorString;

  private final int fPrecedence;

  public Operator(final String oper, final String functionName, final int precedence) {
    fOperatorString = oper;
    fFunctionName = functionName;
    fPrecedence = precedence;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof Operator) {
      return fFunctionName.equals(((Operator) obj).fFunctionName);
    }
    return false;
  }

  /** @return the name of the head of the associated function */
  public String getFunctionName() {
    return fFunctionName;
  }

  /** @return the operator string of this operator */
  public String getOperatorString() {
    return fOperatorString;
  }

  /** @return <code>true</code> if the operator string equals str */
  public boolean isOperator(String str) {
    return fOperatorString.equals(str);
  }

  /** @return the precedence of this operator */
  public int getPrecedence() {
    return fPrecedence;
  }

  /** @return the hashCode of the function name */
  @Override
  public int hashCode() {
    return fFunctionName.hashCode();
  }

  @Override
  public String toString() {
    return "[" + fFunctionName + "," + fOperatorString + "," + fPrecedence + "]";
  }
}
