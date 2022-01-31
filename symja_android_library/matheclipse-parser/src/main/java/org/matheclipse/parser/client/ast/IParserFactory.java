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
package org.matheclipse.parser.client.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.matheclipse.parser.client.operator.Operator;

/** Basic common parser factory methods. */
public interface IParserFactory {

  /** The default set of characters, which could form an operator */
  public static final String BASIC_OPERATOR_CHARACTERS = ".-:=<>*+;!^|&/@?~";

  /** Check if the ch is in the set of operator characters. */
  public boolean isOperatorChar(char ch);

  /**
   * Get the identifier to operator map
   *
   * @return the map which stores the Operators for a given head string like Times, Plus, Sin,...
   */
  public Map<String, ? extends Operator> getIdentifier2OperatorMap();

  /**
   * Get the operator-string to possible operator-list map
   *
   * @return the map which stores the operator-list for a given operator string like *, +, ==...
   */
  public Map<String, ArrayList<Operator>> getOperator2ListMap();

  /**
   * Get the operator for a given identifier string like Times, Plus, Sin,...
   *
   * @param identifier
   * @return
   */
  public Operator get(String identifier);

  /**
   * Get the operator-list for a given operator-string
   *
   * @return the operator-list for a given operator string like *, +, ==...
   */
  public List<Operator> getOperatorList(String operatorString);

  /**
   * Check if the identifier name is valid.
   *
   * @param identifier the currently parsed identifier
   * @return <code>false</code> if the identifier is not valid (in this case the parser creates a
   *         SyntaxError exception); otherwise return <code>true</code>
   */
  public boolean isValidIdentifier(String identifier);
}
