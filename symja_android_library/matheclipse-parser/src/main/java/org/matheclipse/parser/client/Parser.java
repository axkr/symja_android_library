/*
 * Copyright 2005-2013 Axel Kramer (axelclk@gmail.com)
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
package org.matheclipse.parser.client;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.parser.client.ast.ASTNode;
import org.matheclipse.parser.client.ast.FunctionNode;
import org.matheclipse.parser.client.ast.IConstantOperators;
import org.matheclipse.parser.client.ast.INodeParserFactory;
import org.matheclipse.parser.client.ast.IntegerNode;
import org.matheclipse.parser.client.ast.NumberNode;
import org.matheclipse.parser.client.ast.PatternNode;
import org.matheclipse.parser.client.ast.SymbolNode;
import org.matheclipse.parser.client.operator.ASTNodeFactory;
import org.matheclipse.parser.client.operator.InfixOperator;
import org.matheclipse.parser.client.operator.Operator;
import org.matheclipse.parser.client.operator.PostfixOperator;
import org.matheclipse.parser.client.operator.PrefixOperator;

/**
 * Create an expression of the <code>ASTNode</code> class-hierarchy from a math formulas string
 * representation.
 * 
 * <p>
 * See <a href="http://en.wikipedia.org/wiki/Operator-precedence_parser">Operator -precedence
 * parser</a> for the idea, how to parse the operators depending on their precedence.
 */
public class Parser extends Scanner {
  /** SymbolNode for <code>Derivative</code> corresponding to <code>F#Derivative</code> */
  public static final SymbolNode DERIVATIVE = new SymbolNode("Derivative");

  /** Use '('...')' as brackets for function arguments */
  private final boolean fRelaxedSyntax;

  protected INodeParserFactory fFactory;

  /** List of ASTNodes used for parsing packages */
  private List<ASTNode> fNodeList = null;

  public Parser() {
    this(ASTNodeFactory.MMA_STYLE_FACTORY, false, false);
  }

  /**
   * Create an expression of the <code>ASTNode</code> class-hierarchy from a math formulas string
   * representation
   *
   * <p>
   * See <a href="http://en.wikipedia.org/wiki/Operator-precedence_parser">Operator -precedence
   * parser</a> for the idea, how to parse the operators depending on their precedence.
   *
   * @param relaxedSyntax if <code>true</code>, use '('...')' as brackets for function arguments
   */
  public Parser(final boolean relaxedSyntax) {
    this(ASTNodeFactory.MMA_STYLE_FACTORY, relaxedSyntax);
  }

  /**
   * Create an expression of the <code>ASTNode</code> class-hierarchy from a math formulas string
   * representation
   *
   * <p>
   * See <a href="http://en.wikipedia.org/wiki/Operator-precedence_parser">Operator -precedence
   * parser</a> for the idea, how to parse the operators depending on their precedence.
   *
   * @param relaxedSyntax if <code>true</code>, use '('...')' as brackets for function arguments
   * @param packageMode parse in &quot;package mode&quot; and initialize an internal list of
   *        ASTNodes
   */
  public Parser(final boolean relaxedSyntax, boolean packageMode) {
    this(ASTNodeFactory.MMA_STYLE_FACTORY, relaxedSyntax, packageMode);
  }

  /**
   * Create an expression of the <code>ASTNode</code> class-hierarchy from a math formulas string
   * representation
   *
   * <p>
   * See <a href="http://en.wikipedia.org/wiki/Operator-precedence_parser">Operator -precedence
   * parser</a> for the idea, how to parse the operators depending on their precedence.
   *
   * @param factory a parser factory
   * @param relaxedSyntax if <code>true</code>, use '('...')' as brackets for function arguments
   */
  public Parser(INodeParserFactory factory, final boolean relaxedSyntax) {
    this(factory, relaxedSyntax, false);
  }

  /**
   * Create an expression of the <code>ASTNode</code> class-hierarchy from a math formulas string
   * representation
   *
   * <p>
   * See <a href="http://en.wikipedia.org/wiki/Operator-precedence_parser">Operator -precedence
   * parser</a> for the idea, how to parse the operators depending on their precedence.
   *
   * @param factory a parser factory
   * @param relaxedSyntax if <code>true</code>, use '('...')' as brackets for function arguments
   * @param packageMode parse in &quot;package mode&quot; and initialize an internal list of
   *        ASTNodes
   */
  public Parser(INodeParserFactory factory, final boolean relaxedSyntax, boolean packageMode) {
    super(packageMode, ParserConfig.EXPLICIT_TIMES_OPERATOR);
    this.fRelaxedSyntax = relaxedSyntax;
    this.fFactory = factory;
    if (packageMode) {
      fNodeList = new ArrayList<ASTNode>(256);
    }
  }

  /**
   * Determine the current BinaryOperator
   *
   * @return <code>null</code> if no binary operator could be determined
   */
  private InfixOperator determineBinaryOperator() {
    Operator oper;
    for (int i = 0; i < fOperList.size(); i++) {
      oper = fOperList.get(i);
      if (oper instanceof InfixOperator) {
        return (InfixOperator) oper;
      }
    }
    return null;
  }

  /**
   * Determine the current PostfixOperator
   *
   * @return <code>null</code> if no postfix operator could be determined
   */
  private PostfixOperator determinePostfixOperator() {
    Operator oper;
    for (int i = 0; i < fOperList.size(); i++) {
      oper = fOperList.get(i);
      if (oper instanceof PostfixOperator) {
        return (PostfixOperator) oper;
      }
    }
    return null;
  }

  /**
   * Determine the current PrefixOperator
   *
   * @return <code>null</code> if no prefix operator could be determined
   */
  private PrefixOperator determinePrefixOperator() {
    Operator oper;
    for (int i = 0; i < fOperList.size(); i++) {
      oper = fOperList.get(i);
      if (oper instanceof PrefixOperator) {
        return (PrefixOperator) oper;
      }
    }
    return null;
  }

  /** construct the arguments for an expression */
  private void getArguments(final FunctionNode function) throws SyntaxError {
    do {
      function.add(parseExpression());

      if (fToken != TT_COMMA) {
        break;
      }

      getNextToken();
      if (fToken == TT_PRECEDENCE_CLOSE || fToken == TT_ARGUMENTS_CLOSE) {
        function.add(fFactory.createSymbol("Null"));
        break;
      }

    } while (true);
  }

  private ASTNode getFactor(final int min_precedence) throws SyntaxError {
    ASTNode temp = null;

    switch (fToken) {
      case TT_IDENTIFIER:
        final SymbolNode symbol = getSymbol();
        // if (fToken == TT_OPERATOR && fOperatorString.equals(":")) {
        // getNextToken();
        // temp = parseExpression();
        // final FunctionNode pattern =
        // fFactory.createFunction(fFactory.createSymbol(IConstantOperators.Pattern));
        // pattern.add(symbol);
        // pattern.add(temp);
        // return pattern;
        // }

        // return symbol;
        // getNextToken();
        // temp = parseExpression();
        // final FunctionNode pattern =
        //
        // fFactory.createFunction(fFactory.createSymbol(IConstantOperators.Pattern));
        // pattern.add(symbol);
        // pattern.add(temp);
        // return pattern;
        //
        // // if (fToken == TT_IDENTIFIER) {
        // // temp = getSymbol();
        // //
        // // if (fRelaxedSyntax) {
        // // if (fToken == TT_ARGUMENTS_OPEN) {
        // // temp = parseArguments(temp);
        // // final FunctionNode pattern =
        // //
        // // fFactory.createFunction(fFactory.createSymbol(IConstantOperators.Pattern));
        // // pattern.add(symbol);
        // // pattern.add(temp);
        // // return pattern;
        // // } else if (fToken == TT_PRECEDENCE_OPEN) {
        // // temp = parseArguments(temp);
        // // final FunctionNode pattern =
        // //
        // // fFactory.createFunction(fFactory.createSymbol(IConstantOperators.Pattern));
        // // pattern.add(symbol);
        // // pattern.add(temp);
        // // return pattern;
        // // }
        // // } else {
        // // if (fToken == TT_ARGUMENTS_OPEN) {
        // // temp = parseArguments(temp);
        // // final FunctionNode pattern =
        // //
        // // fFactory.createFunction(fFactory.createSymbol(IConstantOperators.Pattern));
        // // pattern.add(symbol);
        // // pattern.add(temp);
        // // return pattern;
        // // }
        // // }
        // //
        // // if (fToken == TT_COLON) {
        // // temp = parseArguments(temp);
        // // final FunctionNode pattern =
        // //
        // // fFactory.createFunction(fFactory.createSymbol(IConstantOperators.Pattern));
        // // pattern.add(symbol);
        // // pattern.add(temp);
        // // return pattern;
        // // }
        // // if (fToken >= TT_BLANK && fToken <= TT_BLANK_COLON) {
        // // temp = getBlankPatterns((SymbolNode) temp);
        // // }
        // // temp = parseExpression(temp, 150);
        // // final FunctionNode pattern =
        // //
        // // fFactory.createFunction(fFactory.createSymbol(IConstantOperators.Pattern));
        // // pattern.add(symbol);
        // // pattern.add(temp);
        // // return pattern;
        // //
        // // } else {
        // // temp = getFactor(0);
        // // }
        // // final FunctionNode pattern =
        // //
        // // fFactory.createFunction(fFactory.createSymbol(IConstantOperators.Pattern));
        // // pattern.add(symbol);
        // // pattern.add(temp);
        // // temp = pattern;
        // } else
        if (fToken >= TT_BLANK && fToken <= TT_BLANK_COLON) {
          temp = getBlankPatterns(symbol);
        } else {
          temp = symbol;
        }
        return parseArguments(temp);
      case TT_PRECEDENCE_OPEN:
        fRecursionDepth++;
        try {
          getNextToken();

          temp = parseExpression();

          if (fToken != TT_PRECEDENCE_CLOSE) {
            throwSyntaxError("\')\' expected.");
          }
        } finally {
          fRecursionDepth--;
        }
        getNextToken();
        if (fToken == TT_PRECEDENCE_OPEN) {
          if (!ParserConfig.EXPLICIT_TIMES_OPERATOR) {
            Operator oper = fFactory.get("Times");
            if (ParserConfig.DOMINANT_IMPLICIT_TIMES || oper.getPrecedence() >= min_precedence) {
              return getTimesImplicit(temp);
            }
          }
        }
        if (fToken == TT_ARGUMENTS_OPEN) {
          return getFunctionArguments(temp);
        }
        return temp;

      case TT_LIST_OPEN:
        return parseArguments(getList());
      case TT_BLANK:
      case TT_BLANK_BLANK:
      case TT_BLANK_BLANK_BLANK:
      case TT_BLANK_OPTIONAL:
      case TT_BLANK_COLON:
        return getBlanks(temp);
      case TT_DIGIT:
        return getNumber(false);
      case TT_STRING:
        ASTNode str = getString();
        return parseArguments(str);
      case TT_PERCENT:
        final FunctionNode out =
            fFactory.createFunction(fFactory.createSymbol(IConstantOperators.Out));

        int countPercent = 1;
        getNextToken();
        if (fToken == TT_DIGIT) {
          countPercent = getJavaInt();
          out.add(fFactory.createInteger(countPercent));
          return out;
        }

        while (fToken == TT_PERCENT) {
          countPercent++;
          getNextToken();
        }

        out.add(fFactory.createInteger(-countPercent));
        return parseArguments(out);
      case TT_SLOT:
        getNextToken();
        final FunctionNode slot =
            fFactory.createFunction(fFactory.createSymbol(IConstantOperators.Slot));
        if (fToken == TT_DIGIT) {
          slot.add(fFactory.createInteger(getJavaInt()));
        } else if (fToken == TT_IDENTIFIER) {
          slot.add(getSymbol());
        } else if (fToken == TT_STRING) {
          slot.add(getString());
        } else {
          slot.add(fFactory.createInteger(1));
        }
        return parseArguments(slot);
      case TT_SLOTSEQUENCE:
        getNextToken();
        final FunctionNode slotSequencce =
            fFactory.createFunction(fFactory.createSymbol(IConstantOperators.SlotSequence));
        if (fToken == TT_DIGIT) {
          slotSequencce.add(getNumber(false));
        } else {
          slotSequencce.add(fFactory.createInteger(1));
        }
        return parseArguments(slotSequencce);
      case TT_ASSOCIATION_OPEN:
        final FunctionNode function =
            fFactory.createFunction(fFactory.createSymbol(IConstantOperators.List));
        fRecursionDepth++;
        try {
          getNextToken();
          if (fToken != TT_ASSOCIATION_CLOSE) {
            do {
              function.add(parseExpression());
              if (fToken != TT_COMMA) {
                break;
              }

              getNextToken();
            } while (true);

            if (fToken != TT_ASSOCIATION_CLOSE) {
              throwSyntaxError("\'|>\' expected.");
            }
          }
          final FunctionNode assoc =
              fFactory.createFunction(fFactory.createSymbol(IConstantOperators.Association));
          assoc.add(function);
          temp = assoc;
          getNextToken();
          if (fToken == TT_PRECEDENCE_OPEN) {
            if (!fExplicitTimes) {
              Operator oper = fFactory.get("Times");
              if (ParserConfig.DOMINANT_IMPLICIT_TIMES || oper.getPrecedence() >= min_precedence) {
                return getTimesImplicit(temp);
              }
            }
          }
          if (fToken == TT_ARGUMENTS_OPEN) {
            return getFunctionArguments(temp);
          }

        } finally {
          fRecursionDepth--;
        }
        return temp;

      case TT_PRECEDENCE_CLOSE:
        throwSyntaxError("Too much closing ) in factor.");
        break;
      case TT_LIST_CLOSE:
        throwSyntaxError("Too much closing } in factor.");
        break;
      case TT_ARGUMENTS_CLOSE:
        throwSyntaxError("Too much closing ] in factor.");
        break;
      case TT_ASSOCIATION_CLOSE:
        throwSyntaxError("Too much closing |> in factor.");
        break;
    }

    throwSyntaxError("Error in factor at character: '" + fCurrentChar + "' (Token:" + fToken
        + " \\u" + Integer.toHexString(fCurrentChar | 0x10000).substring(1) + ")");
    return null;
  }

  private ASTNode getBlanks(ASTNode temp) {
    if (fToken == TT_BLANK) {
      if (isWhitespace()) {
        getNextToken();
        temp = fFactory.createPattern(null, null);
      } else {
        getNextToken();
        if (isSymbolIdentifier()) {
          final ASTNode check = getSymbol();
          temp = fFactory.createPattern(null, check);
        } else {
          temp = fFactory.createPattern(null, null);
        }
      }
    } else if (fToken == TT_BLANK_BLANK) {
      // read '__'
      if (isWhitespace()) {
        getNextToken();
        temp = fFactory.createPattern2(null, null);
      } else {
        getNextToken();
        if (isSymbolIdentifier()) {
          final ASTNode check = getSymbol();
          temp = fFactory.createPattern2(null, check);
        } else {
          temp = fFactory.createPattern2(null, null);
        }
      }
    } else if (fToken == TT_BLANK_BLANK_BLANK) {
      // read '___'
      if (isWhitespace()) {
        getNextToken();
        temp = fFactory.createPattern3(null, null);
      } else {
        getNextToken();
        if (isSymbolIdentifier()) {
          final ASTNode check = getSymbol();
          temp = fFactory.createPattern3(null, check);
        } else {
          temp = fFactory.createPattern3(null, null);
        }
      }
    } else if (fToken == TT_BLANK_OPTIONAL) {
      // read '_.'
      if (isWhitespace()) {
        getNextToken();
        temp = fFactory.createPattern(null, null, true);
      } else {
        getNextToken();
        if (isSymbolIdentifier()) {
          final ASTNode check = getSymbol();
          temp = fFactory.createPattern(null, check, true);
        } else {
          temp = fFactory.createPattern(null, null, true);
        }
      }
    } else if (fToken == TT_BLANK_COLON) {
      // read '_:'
      getNextToken();
      ASTNode defaultValue = parseExpression();
      // temp = fFactory.createPattern(null, null, defaultValue);

      final FunctionNode function = fFactory.createAST(fFactory.createSymbol("Optional"));
      function.add(fFactory.createPattern(null, null, false));
      function.add(defaultValue);
      temp = function;
    }

    if (fToken == TT_OPERATOR && fOperatorString.equals(":")) {
      getNextToken();
      ASTNode defaultValue = parseExpression();
      final FunctionNode function = fFactory.createAST(fFactory.createSymbol("Optional"));
      function.add(temp);
      function.add(defaultValue);
      temp = function;
    }

    return parseArguments(temp);
  }

  private ASTNode getBlankPatterns(final SymbolNode symbol) {
    ASTNode temp = null;
    if (fToken == TT_BLANK) {
      // read '_'
      if (isWhitespace()) {
        temp = fFactory.createPattern(symbol, null);
        getNextToken();
      } else {
        getNextToken();
        if (isSymbolIdentifier()) {
          final ASTNode check = getSymbol();
          temp = fFactory.createPattern(symbol, check);
        } else {
          temp = fFactory.createPattern(symbol, null);
        }
      }
    } else if (fToken == TT_BLANK_BLANK) {
      // read '__'
      if (isWhitespace()) {
        temp = fFactory.createPattern2(symbol, null);
        getNextToken();
      } else {
        getNextToken();
        if (isSymbolIdentifier()) {
          final ASTNode check = getSymbol();
          temp = fFactory.createPattern2(symbol, check);
        } else {
          temp = fFactory.createPattern2(symbol, null);
        }
      }
    } else if (fToken == TT_BLANK_BLANK_BLANK) {
      // read '___'
      if (isWhitespace()) {
        temp = fFactory.createPattern3(symbol, null);
        getNextToken();
      } else {
        getNextToken();
        if (isSymbolIdentifier()) {
          final ASTNode check = getSymbol();
          temp = fFactory.createPattern3(symbol, check);
        } else {
          temp = fFactory.createPattern3(symbol, null);
        }
      }
    } else if (fToken == TT_BLANK_OPTIONAL) {
      // read '_.'
      if (isWhitespace()) {
        temp = fFactory.createPattern(symbol, null, true);
        getNextToken();
      } else {
        getNextToken();
        if (isSymbolIdentifier()) {
          final ASTNode check = getSymbol();
          temp = fFactory.createPattern(symbol, check, true);
        } else {
          temp = fFactory.createPattern(symbol, null, true);
        }
      }
    } else if (fToken == TT_BLANK_COLON) {
      // read '_:'
      getNextToken();
      ASTNode defaultValue = parseExpression();
      temp = fFactory.createFunction(fFactory.createSymbol("Optional"), //
          fFactory.createPattern(symbol, null, false), //
          defaultValue);
    }

    if (fToken == TT_OPERATOR && fOperatorString.equals(":")) {
      getNextToken();
      ASTNode defaultValue = parseExpression();
      temp = fFactory.createFunction(fFactory.createSymbol("Optional"), temp, defaultValue);
    }
    return temp;
  }

  /**
   * The current token is <code>TT_IDENTIFIER</code> and it is not followed by a <code>_</code>
   * &quot;pattern character&quot;.
   *
   * @return
   */
  private boolean isSymbolIdentifier() {
    return fToken == TT_IDENTIFIER && fCurrentPosition < fInputString.length
        && fInputString[fCurrentPosition] != '_';
  }

  public INodeParserFactory getFactory() {
    return fFactory;
  }

  /** Get a function f[...][...] */
  FunctionNode getFunction(final ASTNode head) throws SyntaxError {
    final FunctionNode function = fFactory.createAST(head);

    getNextToken();

    FunctionNode fun = functionNodeClosed(function);
    if (fun != null) {
      return fun;
    }
    fRecursionDepth++;
    try {
      getArguments(function);
    } finally {
      fRecursionDepth--;
    }
    fun = functionNodeClosed(function);
    if (fun != null) {
      return fun;
    }
    if (fRelaxedSyntax) {
      throwSyntaxError("')' expected.");
    } else {
      throwSyntaxError("']' expected.");
    }
    return null;
  }

  /**
   * Test if the current token is closing parenthesis.
   *
   * @param function
   * @return <code>null</code> if function wasn't closed
   */
  private FunctionNode functionNodeClosed(final FunctionNode function) {
    if (fRelaxedSyntax) {
      if (fToken == TT_PRECEDENCE_CLOSE) {
        getNextToken();
        if (fToken == TT_PRECEDENCE_OPEN) {
          return function;
        }
        if (fToken == TT_ARGUMENTS_OPEN) {
          return getFunctionArguments(function);
        }
        return function;
      }
    } else {
      if (fToken == TT_ARGUMENTS_CLOSE) {
        getNextToken();
        if (fToken == TT_ARGUMENTS_OPEN) {
          return getFunctionArguments(function);
        }
        return function;
      }
    }
    return null;
  }

  /** Get a function f[...][...] */
  FunctionNode getFunctionArguments(final ASTNode head) throws SyntaxError {
    final FunctionNode function = fFactory.createAST(head);
    fRecursionDepth++;

    getNextToken();
    if (fToken == TT_ARGUMENTS_CLOSE) {
      fRecursionDepth--;
      getNextToken();
      if (fToken == TT_ARGUMENTS_OPEN) {
        return getFunctionArguments(function);
      }
      return function;
    }
    getArguments(function);
    fRecursionDepth--;

    if (fToken == TT_ARGUMENTS_CLOSE) {
      getNextToken();
      if (fToken == TT_ARGUMENTS_OPEN) {
        return getFunctionArguments(function);
      }
      return function;
    }

    throwSyntaxError("']' expected.");
    return null;
  }

  @Override
  protected boolean isOperatorCharacters() {
    return fFactory.isOperatorChar(fCurrentChar);
  }

  @Override
  protected boolean isOperatorCharacters(char ch) {
    return fFactory.isOperatorChar(ch);
  }

  /**
   * protected List<Operator> getOperator()
   *
   * @return
   */
  @Override
  protected final List<Operator> getOperator() {
    char lastChar;
    final int startPosition = fCurrentPosition - 1;
    fOperatorString = new String(fInputString, startPosition, fCurrentPosition - startPosition);
    List<Operator> list = fFactory.getOperatorList(fOperatorString);
    List<Operator> lastList = null;
    int lastOperatorPosition = -1;
    if (list != null) {
      lastList = list;
      lastOperatorPosition = fCurrentPosition;
    }
    getChar();
    while (fFactory.isOperatorChar(fCurrentChar)) {
      if (fCurrentChar == '.' && isValidPosition() && Character.isDigit(charAtPosition())) {
        // special case "dot is start of floating number" -- 1/.2 => 0.5
        break;
      }
      lastChar = fCurrentChar;
      fOperatorString = new String(fInputString, startPosition, fCurrentPosition - startPosition);
      list = fFactory.getOperatorList(fOperatorString);
      if (list != null) {
        lastList = list;
        lastOperatorPosition = fCurrentPosition;
      }
      getChar();
      if (lastChar == ';' && fCurrentChar != ';') {
        break;
      }
    }
    if (lastOperatorPosition > 0) {
      fCurrentPosition = lastOperatorPosition;
      return lastList;
    }
    final int endPosition = fCurrentPosition--;
    fCurrentPosition = startPosition;
    throwSyntaxError("Operator token not found: "
        + new String(fInputString, startPosition, endPosition - 1 - startPosition));
    return null;
  }

  /** Get a list {...} */
  private ASTNode getList() throws SyntaxError {
    final FunctionNode function =
        fFactory.createFunction(fFactory.createSymbol(IConstantOperators.List));
    fRecursionDepth++;
    getNextToken();
    if (fToken == TT_LIST_CLOSE) {
      fRecursionDepth--;
      getNextToken();

      return function;
    }
    getArguments(function);
    fRecursionDepth--;
    if (fToken == TT_LIST_CLOSE) {
      getNextToken();

      return function;
    }

    throwSyntaxError("'}' expected.");
    return null;
  }

  /**
   * Method Declaration.
   *
   * @return
   * @see
   */
  private ASTNode getNumber(final boolean negative) throws SyntaxError {
    ASTNode temp = null;
    String numberStr = "";
    try {
      final Object[] result = getNumberString();
      numberStr = (String) result[0];
      final int numFormat = ((Integer) result[1]);
      String exponentStr = (String) result[2];
      if (negative) {
        numberStr = '-' + numberStr;
      }
      if (numFormat < 0) {
        if (fCurrentChar == '`' && isValidPosition()) {
          fCurrentPosition++;
          if (isValidPosition() && fInputString[fCurrentPosition] == '*') {
            fCurrentPosition++;
            if (isValidPosition() && fInputString[fCurrentPosition] == '^') {
              fCurrentPosition += 2;
              long exponent = getJavaLong();
              // Double d = Double.valueOf(number + "E" + exponent);
              return fFactory.createDouble(numberStr + "E" + exponent);
            }
          } else if (isValidPosition() && fInputString[fCurrentPosition] == '`') {
            fCurrentPosition += 2;
            long precision = getJavaLong();
            if (precision < ParserConfig.MACHINE_PRECISION) {
              precision = ParserConfig.MACHINE_PRECISION;
            }
            return fFactory.createDouble(numberStr);
          } else {
            if (isValidPosition() && Character.isDigit(fInputString[fCurrentPosition])) {
              fCurrentPosition++;
              long precision = getJavaLong();
              if (precision < ParserConfig.MACHINE_PRECISION) {
                precision = ParserConfig.MACHINE_PRECISION;
              }
              return fFactory.createDouble(numberStr);
            } else {
              getNextToken();
              return fFactory.createDouble(numberStr);
            }
          }
          throwSyntaxError("Number format error: " + numberStr, numberStr.length());
        }
        temp = fFactory.createDouble(numberStr);
      } else {
        if (exponentStr == null || exponentStr.equals("1")) {
          temp = fFactory.createInteger(numberStr, numFormat);
        } else {
          if (numFormat == 10) {
            try {
              int exponent = Integer.parseInt(exponentStr, 10);
              if (exponent < 0) {
                exponent = -exponent;
                StringBuilder buf = createPowersOf10(exponent);
                temp = fFactory.createFunction(new SymbolNode("Times"),
                    fFactory.createInteger(numberStr, numFormat),
                    fFactory.createFunction(new SymbolNode("Power"),
                        fFactory.createInteger(buf.toString(), numFormat), IntegerNode.CN1));
              } else {
                StringBuilder buf = createPowersOf10(exponent);
                temp = fFactory.createFunction(new SymbolNode("Times"),
                    fFactory.createInteger(numberStr, numFormat),
                    fFactory.createInteger(buf.toString(), numFormat));
              }

            } catch (final NumberFormatException e) {
              throwSyntaxError("Number format error (not an int type): " + exponentStr,
                  exponentStr.length());
            }
          } else {
            throwSyntaxError("Number format error: " + numberStr, numberStr.length());
          }
        }
      }
    } catch (final SyntaxError e) {
      throwSyntaxError("Number format error: " + numberStr, numberStr.length());
    }
    getNextToken();
    return temp;
  }

  /** Get a <i>part [[..]]</i> of an expression <code>{a,b,c}[[2]]</code> &rarr; <code>b</code> */
  private ASTNode getPart(final int min_precedence) throws SyntaxError {
    FunctionNode function = null;
    ASTNode temp = getFactor(min_precedence);
    // if (fToken == TT_COLON) {
    // getNextToken();
    // function = fFactory.createFunction(fFactory.createSymbol(IConstantOperators.Optional),
    // temp);
    // function.add(parseExpression());
    // return function;
    // }
    if (fToken != TT_PARTOPEN) {
      return temp;
    }

    do {
      if (function == null) {
        function = fFactory.createFunction(fFactory.createSymbol(IConstantOperators.Part), temp);
      } else {
        function =
            fFactory.createFunction(fFactory.createSymbol(IConstantOperators.Part), function);
      }

      fRecursionDepth++;
      try {
        do {
          getNextToken();

          if (fToken == TT_ARGUMENTS_CLOSE) {
            skipWhitespace();
            // scanner-step begin: (instead of getNextToken() call):
            if (fInputString.length > fCurrentPosition) {
              if (fInputString[fCurrentPosition] == ']') {
                fCurrentPosition++;
                getNextToken();
                // fToken = TT_PARTCLOSE;
                return function;
              }
            }
            // scanner-step end
            // if (fInputString.length > fCurrentPosition && fInputString[fCurrentPosition] == ']')
            // {
            // throwSyntaxError("Statement (i.e. index) expected in [[ ]].");
            // }
          }

          function.add(parseExpression());
        } while (fToken == TT_COMMA);

        if (fToken == TT_ARGUMENTS_CLOSE) {
          skipWhitespace();
          // scanner-step begin: (instead of getNextToken() call):
          if (fInputString.length > fCurrentPosition) {
            if (fInputString[fCurrentPosition] == ']') {
              fCurrentPosition++;
              fToken = TT_PARTCLOSE;
            }
          }
          // scanner-step end
        }
        if (fToken != TT_PARTCLOSE) {
          throwSyntaxError("']]' expected.");
        }
      } finally {
        fRecursionDepth--;
      }
      getNextToken();
    } while (fToken == TT_PARTOPEN);

    return parseArguments(function);
  }

  private ASTNode getString() throws SyntaxError {
    final StringBuilder ident = getStringBuilder();

    getNextToken();

    return fFactory.createString(ident);
  }

  /**
   * Read the current identifier from the expression factories table
   *
   * @return
   * @see
   */
  private SymbolNode getSymbol() throws SyntaxError {
    String[] identifierContext = getIdentifier();
    if (!fFactory.isValidIdentifier(identifierContext[0])) {
      throwSyntaxError("Invalid identifier: " + identifierContext[0] + " detected.");
    }

    final SymbolNode symbol = fFactory.createSymbol(identifierContext[0], identifierContext[1]);
    getNextToken();
    return symbol;
  }

  private ASTNode getTimesImplicit(ASTNode temp) throws SyntaxError {
    FunctionNode func = fFactory.createAST(new SymbolNode("Times"));
    func.add(temp);
    do {
      getNextToken();

      temp = parseExpression();
      func.add(temp);
      if (fToken != TT_PRECEDENCE_CLOSE) {
        throwSyntaxError("\')\' expected.");
      }
      getNextToken();
    } while (fToken == TT_PRECEDENCE_OPEN);
    return func;
  }

  /**
   * Parse the given <code>expression</code> String into an ASTNode.
   *
   * @param expression a formula string which should be parsed.
   * @return the parsed ASTNode representation of the given formula string
   * @throws SyntaxError
   */
  public ASTNode parse(final String expression) throws SyntaxError {
    initialize(expression);
    if (fToken == TT_EOF) {
      // empty expression string or only a comment available in the string
      return fFactory.createSymbol("Null");
    }
    final ASTNode temp = parseExpression();
    if (fToken != TT_EOF) {
      if (fToken == TT_PRECEDENCE_CLOSE) {
        throwSyntaxError("Too many closing ')'; End-of-file not reached.");
      }
      if (fToken == TT_LIST_CLOSE) {
        throwSyntaxError("Too many closing '}'; End-of-file not reached.");
      }
      if (fToken == TT_ARGUMENTS_CLOSE) {
        throwSyntaxError("Too many closing ']'; End-of-file not reached.");
      }

      throwSyntaxError("End-of-file not reached.");
    }

    return temp;
  }

  private ASTNode parseArguments(ASTNode lhs) {
    if (fRelaxedSyntax) {
      if (fToken == TT_ARGUMENTS_OPEN) {
        if (ParserConfig.PARSER_USE_STRICT_SYNTAX) {
          if (lhs instanceof SymbolNode || lhs instanceof PatternNode) {
            throwSyntaxError("'(' expected after symbol or pattern instead of '['.");
          }
        }
        lhs = getFunctionArguments(lhs);
      } else if (fToken == TT_PRECEDENCE_OPEN) {
        lhs = getFunction(lhs);
      }
    } else {
      if (fToken == TT_ARGUMENTS_OPEN) {
        lhs = getFunctionArguments(lhs);
      }
    }
    return lhs;
  }

  private ASTNode parseCompoundExpressionNull(InfixOperator infixOperator, ASTNode lhs) {
    if (infixOperator.isOperator(";")) {
      if (fToken == TT_EOF || fToken == TT_ARGUMENTS_CLOSE || fToken == TT_LIST_CLOSE
          || fToken == TT_PRECEDENCE_CLOSE || fToken == TT_COMMA) {
        return infixOperator.createFunction(fFactory, lhs, fFactory.createSymbol("Null"));
      }
      if (fPackageMode && fRecursionDepth < 1) {
        return infixOperator.createFunction(fFactory, lhs, fFactory.createSymbol("Null"));
      }
    }
    return null;
  }

  private ASTNode parseExpression() {
    if (fToken == TT_SPAN) {
      FunctionNode span = fFactory.createFunction(fFactory.createSymbol(IConstantOperators.Span));
      span.add(fFactory.createInteger(1));
      getNextToken();
      if (fToken == TT_SPAN) {
        span.add(fFactory.createSymbol(IConstantOperators.All));
        getNextToken();
        if (fToken == TT_COMMA || fToken == TT_PARTCLOSE || fToken == TT_ARGUMENTS_CLOSE
            || fToken == TT_PRECEDENCE_CLOSE) {
          return span;
        }
      } else if (fToken == TT_COMMA || fToken == TT_PARTCLOSE || fToken == TT_ARGUMENTS_CLOSE
          || fToken == TT_PRECEDENCE_CLOSE) {
        span.add(fFactory.createSymbol(IConstantOperators.All));
        return span;
      } else if (fToken == TT_OPERATOR) {
        InfixOperator infixOperator = determineBinaryOperator();
        if (infixOperator != null //
            && infixOperator.getOperatorString().equals(";")) {
          span.add(fFactory.createSymbol(IConstantOperators.All));
          getNextToken();
          ASTNode compoundExpressionNull = parseCompoundExpressionNull(infixOperator, span);
          if (compoundExpressionNull != null) {
            return compoundExpressionNull;
          }
          while (fToken == TT_NEWLINE) {
            getNextToken();
          }
          return parseInfixOperator(span, infixOperator);
        }
      }
      span.add(parseExpression(parsePrimary(0), 0));
      if (fToken == TT_SPAN) {
        getNextToken();
        if (fToken == TT_COMMA || fToken == TT_PARTCLOSE || fToken == TT_ARGUMENTS_CLOSE
            || fToken == TT_PRECEDENCE_CLOSE) {
          return span;
        }
        span.add(parseExpression(parsePrimary(0), 0));
      }
      return span;
    }
    ASTNode temp = parseExpression(parsePrimary(0), 0);

    if (fToken == TT_SPAN) {
      FunctionNode span = fFactory.createFunction(fFactory.createSymbol(IConstantOperators.Span));
      span.add(temp);
      getNextToken();
      if (fToken == TT_SPAN) {
        span.add(fFactory.createSymbol(IConstantOperators.All));
        getNextToken();
        if (fToken == TT_COMMA || fToken == TT_PARTCLOSE || fToken == TT_ARGUMENTS_CLOSE
            || fToken == TT_PRECEDENCE_CLOSE) {
          return span;
        } else if (fToken == TT_OPERATOR) {
          FunctionNode times = fFactory.createAST(new SymbolNode("Times"));
          times.add(span);
          span = fFactory.createFunction(fFactory.createSymbol(IConstantOperators.Span));
          span.add(fFactory.createInteger(1));
          span.add(fFactory.createSymbol(IConstantOperators.All));
          times.add(span);
          return parseExpression(times, 0);
        }
      } else if (fToken == TT_COMMA || fToken == TT_PARTCLOSE || fToken == TT_ARGUMENTS_CLOSE
          || fToken == TT_PRECEDENCE_CLOSE) {
        span.add(fFactory.createSymbol(IConstantOperators.All));
        return span;
      } else if (fToken == TT_OPERATOR) {
        InfixOperator infixOperator = determineBinaryOperator();
        if (infixOperator != null && //
            infixOperator.getOperatorString().equals(";")) {
          span.add(fFactory.createSymbol(IConstantOperators.All));
          getNextToken();
          ASTNode compoundExpressionNull = parseCompoundExpressionNull(infixOperator, span);
          if (compoundExpressionNull != null) {
            return compoundExpressionNull;
          }
          while (fToken == TT_NEWLINE) {
            getNextToken();
          }
          return parseInfixOperator(span, infixOperator);
        }
      }
      if (fToken == TT_NEWLINE || fToken == TT_EOF) {
        span.add(new SymbolNode("All"));
        getNextToken();
      } else {
        span.add(parseExpression(parsePrimary(0), 0));
      }
      if (fToken == TT_SPAN) {
        getNextToken();
        if (fToken == TT_COMMA || fToken == TT_PARTCLOSE || fToken == TT_ARGUMENTS_CLOSE
            || fToken == TT_PRECEDENCE_CLOSE) {
          return span;
        }
        span.add(parseExpression(parsePrimary(0), 0));
      }

      return span;
    }
    return temp;
  }

  /**
   * See <a href="http://en.wikipedia.org/wiki/Operator-precedence_parser">Operator -precedence
   * parser</a> for the idea, how to parse the operators depending on their precedence.
   *
   * @param lhs the already parsed left-hand-side of the operator
   * @param min_precedence
   * @return
   */
  private ASTNode parseExpression(ASTNode lhs, final int min_precedence) {
    ASTNode rhs;
    Operator oper;
    while (true) {
      if (fToken == TT_NEWLINE) {
        return lhs;
      }
      if ((fToken == TT_LIST_OPEN) || (fToken == TT_PRECEDENCE_OPEN) || (fToken == TT_IDENTIFIER)
          || (fToken == TT_STRING) || (fToken == TT_DIGIT) || (fToken == TT_SLOT)
          || (fToken == TT_SLOTSEQUENCE)) {
        if (!ParserConfig.EXPLICIT_TIMES_OPERATOR) {
          // lazy evaluation of multiplication
          oper = fFactory.get("Times");
          if (ParserConfig.DOMINANT_IMPLICIT_TIMES || oper.getPrecedence() >= min_precedence) {
            rhs = parseLookaheadOperator(oper.getPrecedence());
            lhs = fFactory.createFunction(fFactory.createSymbol(oper.getFunctionName()), lhs, rhs);
            continue;
          }
        }
      } else {
        if (fToken == TT_DERIVATIVE) {
          lhs = parseDerivative(lhs);
        }
        if (fToken != TT_OPERATOR) {
          break;
        }
        InfixOperator infixOperator = determineBinaryOperator();

        if (infixOperator != null) {
          if (infixOperator.getPrecedence() >= min_precedence) {

            getNextToken();
            ASTNode compoundExpressionNull = parseCompoundExpressionNull(infixOperator, lhs);
            if (compoundExpressionNull != null) {
              return compoundExpressionNull;
            }

            while (fToken == TT_NEWLINE) {
              getNextToken();
            }
            lhs = parseInfixOperator(lhs, infixOperator);
            continue;
          }
        } else {
          PostfixOperator postfixOperator = determinePostfixOperator();

          if (postfixOperator != null && postfixOperator.getPrecedence() >= min_precedence) {
            lhs = parsePostfixOperator(lhs, postfixOperator);
            continue;
          }
        }
      }
      break;
    }
    return lhs;
  }

  private final ASTNode parsePostfixOperator(ASTNode lhs, PostfixOperator postfixOperator) {
    getNextToken();
    lhs = postfixOperator.createFunction(fFactory, lhs);
    lhs = parseArguments(lhs);
    if (fToken == TT_ARGUMENTS_OPEN) {
      return getFunctionArguments(lhs);
    }
    return lhs;
  }

  private final ASTNode parseInfixOperator(ASTNode lhs, InfixOperator infixOperator) {
    ASTNode rhs;
    rhs = parseLookaheadOperator(infixOperator.getPrecedence());
    lhs = infixOperator.createFunction(fFactory, lhs, rhs);
    if (lhs instanceof FunctionNode) {
      FunctionNode ast = ((FunctionNode) lhs);
      String infixOperatorString = infixOperator.getOperatorString();
      if (isComparatorOperator(infixOperatorString)) {
        while (fToken == TT_OPERATOR && infixOperator.getGrouping() == InfixOperator.NONE
            && isComparatorOperator(fOperatorString)) {
          if (!infixOperator.isOperator(fOperatorString)) {
            // rewrite to Inequality
            return parseInequality(ast, infixOperator);
          }
          getNextToken();
          while (fToken == TT_NEWLINE) {
            getNextToken();
          }
          rhs = parseLookaheadOperator(infixOperator.getPrecedence());
          ast.add(rhs);
        }
        return ast;
      }

      while (fToken == TT_OPERATOR && infixOperator.getGrouping() == InfixOperator.NONE
          && infixOperatorString.equals(fOperatorString)) {
        getNextToken();
        if (";".equals(infixOperatorString)) {
          if (fToken == TT_EOF || fToken == TT_ARGUMENTS_CLOSE || fToken == TT_LIST_CLOSE
              || fToken == TT_PRECEDENCE_CLOSE || fToken == TT_COMMA) {
            ((FunctionNode) lhs).add(fFactory.createSymbol("Null"));
            break;
          }
        }
        while (fToken == TT_NEWLINE) {
          getNextToken();
        }
        rhs = parseLookaheadOperator(infixOperator.getPrecedence());
        ast.add(rhs);
      }
      lhs = infixOperator.endFunction(fFactory, ast, this);
    } else {
      if (fToken == TT_OPERATOR && infixOperator.getGrouping() == InfixOperator.NONE
          && infixOperator.isOperator(fOperatorString)) {
        throwSyntaxError(
            "Operator: \'" + fOperatorString + "\' not created properly (no grouping defined)");
      }
    }
    return lhs;
  }

  /**
   * Rewrite a chain of different comparator operators to an <code>Inequality(...)</code>
   * expression.
   *
   * @param ast the ast which should be rewritten
   * @param infixOperator
   * @return
   */
  private ASTNode parseInequality(final FunctionNode ast, final InfixOperator infixOperator) {
    // rewrite to Inequality
    SymbolNode head = (SymbolNode) ast.get(0);
    final FunctionNode result =
        fFactory.createFunction(fFactory.createSymbol(IConstantOperators.Inequality));
    for (int i = 1; i < ast.size(); i++) {
      result.add(ast.get(i));
      result.add(head);
    }
    InfixOperator compareOperator = determineBinaryOperator();
    result.set(result.size() - 1, fFactory.createSymbol(compareOperator.getFunctionName()));
    getNextToken();
    while (fToken == TT_NEWLINE) {
      getNextToken();
    }
    int precedence = infixOperator.getPrecedence();
    result.add(parseLookaheadOperator(precedence));

    while (fToken == TT_OPERATOR && isComparatorOperator(fOperatorString)) {
      compareOperator = determineBinaryOperator();
      result.add(fFactory.createSymbol(compareOperator.getFunctionName()));
      getNextToken();
      while (fToken == TT_NEWLINE) {
        getNextToken();
      }
      result.add(parseLookaheadOperator(precedence));
    }
    return result;
  }

  private ASTNode parseLookaheadOperator(final int min_precedence) {
    ASTNode rhs = parsePrimary(min_precedence);

    while (true) {
      final int lookahead = fToken;
      if (fToken == TT_NEWLINE) {
        break;
      }
      if ((fToken == TT_LIST_OPEN) || (fToken == TT_PRECEDENCE_OPEN) || (fToken == TT_IDENTIFIER)
          || (fToken == TT_STRING) || (fToken == TT_DIGIT) || (fToken == TT_SLOT)) {
        if (!ParserConfig.EXPLICIT_TIMES_OPERATOR) {
          // lazy evaluation of multiplication
          InfixOperator timesOperator = (InfixOperator) fFactory.get("Times");
          if (ParserConfig.DOMINANT_IMPLICIT_TIMES
              || timesOperator.getPrecedence() > min_precedence) {
            rhs = parseExpression(rhs, timesOperator.getPrecedence());
            continue;
          } else if ((timesOperator.getPrecedence() == min_precedence)
              && (timesOperator.getGrouping() == InfixOperator.RIGHT_ASSOCIATIVE)) {
            rhs = parseExpression(rhs, timesOperator.getPrecedence());
            continue;
          }
        }
      } else {
        if (fToken == TT_DERIVATIVE) {
          rhs = parseDerivative(rhs);
        }
        if (lookahead != TT_OPERATOR) {
          break;
        }
        InfixOperator infixOperator = determineBinaryOperator();
        if (infixOperator != null) {
          if (infixOperator.getPrecedence() > min_precedence
              || (fOperatorString.equals(":") && (rhs instanceof SymbolNode))
              || ((infixOperator.getPrecedence() == min_precedence)
                  && (infixOperator.getGrouping() == InfixOperator.RIGHT_ASSOCIATIVE))) {
            if (infixOperator.isOperator(";")) {
              if (fPackageMode && fRecursionDepth < 1) {
                return infixOperator.createFunction(fFactory, rhs, fFactory.createSymbol("Null"));
              }
            }
            rhs = parseExpression(rhs, infixOperator.getPrecedence());
            continue;
          }

        } else {
          PostfixOperator postfixOperator = determinePostfixOperator();
          if (postfixOperator != null) {
            if (postfixOperator.getPrecedence() >= min_precedence) {
              // getNextToken();
              rhs = parsePostfixOperator(rhs, postfixOperator);
              // rhs = postfixOperator.createFunction(fFactory, rhs);
              // if (fToken == TT_ARGUMENTS_OPEN) {
              // return getFunctionArguments(rhs);
              // }
              continue;
            }
          }
        }
      }
      break;
    }
    if (fToken == TT_ARGUMENTS_OPEN) {
      rhs = parseArguments(rhs);
    }
    return rhs;
  }

  private ASTNode parseDerivative(ASTNode expr) {
    int derivativeCounter = 1;
    getNextToken();
    while (fToken == TT_DERIVATIVE) {
      derivativeCounter++;
      getNextToken();
    }
    FunctionNode head = fFactory.createFunction(DERIVATIVE, new IntegerNode(derivativeCounter));
    FunctionNode deriv = fFactory.unaryAST(head, expr);
    expr = parseArguments(deriv);
    return expr;
  }

  /**
   * Parse a package.
   *
   * @param expression
   * @return
   * @throws SyntaxError
   */
  public List<ASTNode> parsePackage(final String expression) throws SyntaxError {
    String input = expression.trim();
    initialize(input);
    if (fToken == TT_EOF) {
      // empty expression string or only a comment available in the string
      return new ArrayList<ASTNode>(1);
    }

    while (fToken == TT_NEWLINE) {
      getNextToken();
    }
    ASTNode temp = parseExpression();
    fNodeList.add(temp);
    while (fToken != TT_EOF) {
      if (fToken == TT_PRECEDENCE_CLOSE) {
        throwSyntaxError("Too many closing ')'; End-of-file not reached.");
      }
      if (fToken == TT_LIST_CLOSE) {
        throwSyntaxError("Too many closing '}'; End-of-file not reached.");
      }
      if (fToken == TT_ARGUMENTS_CLOSE) {
        throwSyntaxError("Too many closing ']'; End-of-file not reached.");
      }
      while (fToken == TT_NEWLINE) {
        getNextToken();
      }
      if (fToken == TT_EOF) {
        return fNodeList;
      }
      temp = parseExpression();
      fNodeList.add(temp);
    }

    return fNodeList;
  }

  private ASTNode parsePrimary(final int min_precedence) {
    if (fToken == TT_OPERATOR) {
      if (".".equals(fOperatorString)) {
        fCurrentChar = '.';
        return getNumber(false);
      }
      final PrefixOperator prefixOperator = determinePrefixOperator();
      if (prefixOperator != null) {
        return parsePrefixOperator(prefixOperator);
      }
      throwSyntaxError("Operator: " + fOperatorString + " is no prefix operator.");
    }
    return getPart(min_precedence);
  }

  private final ASTNode parsePrefixOperator(final PrefixOperator prefixOperator) {
    getNextToken();
    final ASTNode temp = parseLookaheadOperator(prefixOperator.getPrecedence());
    if ("PreMinus".equals(prefixOperator.getFunctionName()) && temp instanceof NumberNode) {
      // special cases for negative numbers
      ((NumberNode) temp).toggleSign();
      return temp;
    }
    return prefixOperator.createFunction(fFactory, temp);
  }

  public void setFactory(final INodeParserFactory factory) {
    this.fFactory = factory;
  }
}
