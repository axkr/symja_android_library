/*
 * Copyright 2005-2024 Axel Kramer (axelclk@gmail.com)
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

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.parser.client.Scanner;
import org.matheclipse.parser.client.operator.Operator;
import org.matheclipse.parser.client.operator.PrefixOperator;
import org.matheclipse.parser.wma.tablegen.WMAOperatorTables;

/**
 * Scanner implementation specific to Wolfram Mathematical Assistant (WMA) syntax. This extends the
 * base Scanner functionality with WMA-specific operator handling.
 */
public class WMAScanner extends Scanner {

  /**
   * Initialize WMAScanner without a math-expression
   */
  public WMAScanner(boolean packageMode, boolean explicitTimes) {
    super(packageMode, explicitTimes);
  }

  /**
   * Get a list of operators for the operator string determined with TT_OPERATOR token detection.
   * Uses the WMAOperatorTables to find operators.
   *
   * @return List of operators that match the current operator string
   */
  @Override
  protected List<Operator> getOperator() {
    final int startPosition = fCurrentPosition - 1;
    StringBuilder operatorBuilder = new StringBuilder();
    operatorBuilder.append(fCurrentChar);

    // First attempt to read the operator as-is
    fOperatorString = operatorBuilder.toString();
    List<Operator> operList = findOperatorList(fOperatorString);

    // Record the best match so far
    List<Operator> bestMatch = operList;
    int bestPosition = fCurrentPosition;

    // Keep consuming characters as long as they could form valid operators
    getChar();
    while (isOperatorCharacters()) {
      operatorBuilder.append(fCurrentChar);
      fOperatorString = operatorBuilder.toString();

      List<Operator> newList = findOperatorList(fOperatorString);
      if (newList != null) {
        bestMatch = newList;
        bestPosition = fCurrentPosition;
      }

      getChar();
    }

    // If we found a valid operator at some point, use it
    if (bestMatch != null) {
      // Reset position to right after the best matching operator
      fCurrentPosition = bestPosition;
      fOperatorString = operatorBuilder.substring(0, bestPosition - startPosition);
      return bestMatch;
    }

    // No valid operator found
    final int endPosition = fCurrentPosition;
    fCurrentPosition = startPosition;
    throwSyntaxError("Operator token not found: "
        + new String(fInputString, startPosition, endPosition - 1 - startPosition));
    return null;
  }

  /**
   * Find a list of operators that match the given operator string.
   * 
   * @param operatorString The string representation of the operator
   * @return A list of matching operators, or null if none found
   */
  private List<Operator> findOperatorList(String operatorString) {
    // Check if this is a known operator string
    if (WMAOperatorTables.ALL_OPERATOR_STRINGS.contains(operatorString)) {
      // Find the operator name for this string
      String operatorName = WMAOperatorTables.getOperatorForCharacter(operatorString);
      if (operatorName != null) {
        // Get the precedence value for this operator
        Integer precedence = WMAOperatorTables.getOperatorPrecedence(operatorName);
        if (precedence != null) {
          List<Operator> operList = new ArrayList<>();

          // Determine operator type based on the tables
          if (WMAOperatorTables.PREFIX_OPERATORS.containsKey(operatorName)) {
            // Prefix operator
            operList.add(new PrefixOperator(operatorName, operatorString, precedence));
          } else if (WMAOperatorTables.POSTFIX_OPERATORS.containsKey(operatorName)) {
            // Postfix operator
            operList.add(new WMAPostfixOperator(operatorName, operatorString, precedence));
          } else {
            // Binary operator (infix)
            int associativity = Operator.NON_ASSOCIATIVE;
            if (WMAOperatorTables.LEFT_BINARY_OPERATORS.containsKey(operatorName)) {
              associativity = Operator.LEFT_ASSOCIATIVE;
            } else if (WMAOperatorTables.RIGHT_BINARY_OPERATORS.containsKey(operatorName)) {
              associativity = Operator.RIGHT_ASSOCIATIVE;
            } else if (WMAOperatorTables.FLAT_BINARY_OPERATORS.containsKey(operatorName)) {
              associativity = Operator.FLAT;
            }

            operList
                .add(new WMAInfixOperator(operatorName, operatorString, precedence, associativity));
          }

          return operList;
        }
      }
    }
    return null;
  }

  /**
   * Determine if current character is part of an operator. This is used to determine if the scanner
   * should attempt to read an operator.
   */
  @Override
  protected boolean isOperatorCharacters() {
    // Check if the current character could start an operator
    if (fCurrentChar == ' ' || fCurrentChar == '\t' || fCurrentChar == '\r'
        || fCurrentChar == '\n') {
      return false;
    }

    // Special case for certain characters that are always operators
    if ("+-*/=<>^&|!:;,.()[]{}".indexOf(fCurrentChar) >= 0) {
      return true;
    }

    // Check for Unicode operators
    String singleCharStr = String.valueOf(fCurrentChar);
    return WMAOperatorTables.ALL_OPERATOR_STRINGS.contains(singleCharStr);
  }

  /**
   * Check if a specific character is part of an operator.
   */
  @Override
  protected boolean isOperatorCharacters(char ch) {
    // Similar logic to isOperatorCharacters() but for a specific character
    if (ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n') {
      return false;
    }

    if ("+-*/=<>^&|!:;,.()[]{}".indexOf(ch) >= 0) {
      return true;
    }

    // Check for Unicode operators
    String charStr = String.valueOf(ch);
    return WMAOperatorTables.ALL_OPERATOR_STRINGS.contains(charStr);
  }
}
