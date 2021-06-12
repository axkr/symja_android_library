/*
 * Copyright 2005-2013 Axel Kramer (axelclk@gmail.com)
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
package org.matheclipse.api.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.Apint;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.Arithmetic;
import org.matheclipse.core.builtin.PatternMatching;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.NumStr;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.VisitorExpr;
import org.matheclipse.parser.client.FEConfig;
import org.matheclipse.parser.client.Scanner;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.ast.IParserFactory;
import org.matheclipse.parser.client.operator.InfixOperator;
import org.matheclipse.parser.client.operator.Operator;

/**
 * Create an expression of the {@link IExpr} class-hierarchy from a math formula's string
 * representation.
 *
 * <p>See <a href="http://en.wikipedia.org/wiki/Operator-precedence_parser">Operator -precedence
 * parser</a> for the idea, how to parse the operators depending on their precedence.
 */
public class FuzzyParser extends Scanner {
  static class NVisitorExpr extends VisitorExpr {
    final int fPrecision;

    NVisitorExpr(int precision) {
      super();
      fPrecision = precision;
    }

    @Override
    public IExpr visit(INum element) {
      if (element instanceof NumStr) {
        Apfloat apfloatValue = new Apfloat(((NumStr) element).getFloatStr(), fPrecision);
        int exponent = ((NumStr) element).getExponent();
        if (exponent != 1) {
          // value * 10 ^ exponent
          return F.num(apfloatValue.multiply(ApfloatMath.pow(new Apint(10), new Apint(exponent))));
        }
        return F.num(apfloatValue);
      }
      return F.NIL;
    }
  }

  static {
    F.initSymbols(null, null, true);
  }

  public static final ISymbol DERIVATIVE = F.Derivative;

  public static int syntaxLength(final String str, EvalEngine engine) throws SyntaxError {
    try {
      FuzzyParser parser = new FuzzyParser(engine);
      parser.parse(str);
    } catch (final SyntaxError e) {
      return e.getStartOffset();
    }
    return str.length();
  }

  public static boolean test(final String str, EvalEngine engine) {
    try {
      FuzzyParser fParser = new FuzzyParser(engine);
      final IExpr parsedExpression = fParser.parse(str);
      // final Parser fParser = new Parser();
      // final ASTNode parsedAST = fParser.parse(str);
      // final IExpr parsedExpression = AST2Expr.CONST.convert(parsedAST);
      if (parsedExpression != null) {
        return true;
      }
    } catch (final SyntaxError e) {

    }
    return false;
  }

  private final EvalEngine fEngine;

  protected IParserFactory fFactory;

  public FuzzyParser(final EvalEngine engine) throws SyntaxError {
    this(engine, FuzzyParserFactory.RELAXED_STYLE_FACTORY, false, false);
  }

  /**
   * @param engine
   * @param factory
   * @throws SyntaxError
   */
  public FuzzyParser(final EvalEngine engine, IParserFactory factory) {
    this(engine, factory, false, FEConfig.EXPLICIT_TIMES_OPERATOR);
  }

  public FuzzyParser(
      final EvalEngine engine, IParserFactory factory, boolean packageMode, boolean explicitTimes) {
    super(packageMode, explicitTimes);
    this.fFactory = factory;
    this.fEngine = engine;
    // if (packageMode) {
    // fNodeList = new ArrayList<IExpr>(256);
    // }
  }

  private IExpr convert(IASTMutable ast) {
    int headID = ast.headID();
    if (headID >= ID.Blank) {
      IExpr expr = F.NIL;
      // ID.Blank is lowest integer >ID in switch statement
      switch (headID) {
        case ID.Exp:
          if (ast.isAST(S.Exp, 2)) {
            // rewrite from input: Exp(x) => E^x
            return F.Power(S.E, ast.getUnevaluated(1));
          }
          break;

        case ID.Hold:
        case ID.HoldForm:
          return ast;

          // case ID.N:
          // if (ast.isAST(F.N, 3)) {
          // return convertN(ast);
          // }
          // break;

        case ID.Sqrt:
          if (ast.isAST(S.Sqrt, 2)) {
            // rewrite from input: Sqrt(x) => Power(x, 1/2)
            return F.Power(ast.getUnevaluated(1), F.C1D2);
          }
          break;

        case ID.Power:
          if (ast.isPower() && ast.base().isPower() && ast.exponent().isMinusOne()) {
            IAST arg1Power = (IAST) ast.base();
            if (arg1Power.exponent().isNumber()) {
              // Division operator
              // rewrite from input: Power(Power(x, <number>),-1) => Power(x,
              // - <number>)
              return F.Power(arg1Power.getUnevaluated(1), arg1Power.getUnevaluated(2).negate());
            }
          }
          break;

        case ID.Pattern:
          expr = PatternMatching.Pattern.CONST.evaluate(ast, fEngine);
          break;

        case ID.Blank:
          expr = PatternMatching.Blank.CONST.evaluate(ast, fEngine);
          break;

        case ID.Complex:
          expr = Arithmetic.CONST_COMPLEX.evaluate(ast, fEngine);
          break;

        case ID.Rational:
          expr = Arithmetic.CONST_RATIONAL.evaluate(ast, fEngine);
          break;
      }
      return expr.orElse(ast);
    }
    return ast;
  }

  private static List<String> split(String sourceCode) {
    List<String> list = new ArrayList<String>();
    int startPosition = 0;

    Stack<Character> openBracketStack = new Stack<Character>();

    for (int j = 0; j < sourceCode.length(); j++) {
      char ch = sourceCode.charAt(j);
      switch (ch) {
        case '\n':
          if (openBracketStack.isEmpty()) {
            String str = sourceCode.substring(startPosition, j).trim();
            if (str.length() > 0) {
              list.add(str);
            }
            startPosition = j + 1;
          }
          break;
        case '{':
        case '(':
        case '[':
          openBracketStack.push(ch);
          break;
        case '}':
          if (openBracketStack.isEmpty()) {
            return null;
          }
          ch = openBracketStack.pop();
          if (!(ch == '{')) {
            return null;
          }
          break;
        case ')':
          if (openBracketStack.isEmpty()) {
            return null;
          }
          ch = openBracketStack.pop();
          if (!(ch == '(')) {
            return null;
          }
          break;
        case ']':
          if (openBracketStack.isEmpty()) {
            return null;
          }
          ch = openBracketStack.pop();
          if (!(ch == '[')) {
            return null;
          }
          break;
        default:
          // do nothing
      }
    }
    // if (!openBracketStack.isEmpty()) {
    // code is not "balanced"
    // }
    String str = sourceCode.substring(startPosition).trim();
    if (str.length() > 0) {
      list.add(str);
    }

    return list;
  }

  /**
   * Parse a list of comma separated expressions
   *
   * @param expression
   * @return the parsed expression
   * @throws SyntaxError
   */
  public IExpr parseFuzzyList(final String expression) throws SyntaxError {
    // String[] lines = expression.split("\\n");
    List<String> lines = split(expression);
    IASTAppendable function = F.NIL;
    IASTAppendable result = F.ListAlloc(16);
    for (int i = 0; i < lines.size(); i++) {
      String line = lines.get(i);
      if (line.length() > 0) {
        initialize(line);
        fRecursionDepth++;
        try {
          function = F.ListAlloc(16);
          do {
            function.append(parseExpression());
            if (fToken != TT_COMMA) {
              break;
            }
            getNextToken();
            if (fToken == TT_EOF) {
              break;
            }
          } while (true);
          result.append(function);

        } finally {
          fRecursionDepth--;
        }
      }
    }
    if (result.size() == 2) {
      return result.arg1();
    }
    return result;
  }

  protected IExpr convertSymbolOnInput(final String nodeStr, final String context) {
    String lowercaseStr = nodeStr.toLowerCase(Locale.ENGLISH);
    if (lowercaseStr.equals("alternatives")) {
      // special - convert on input
      return S.Or;
    }
    if (lowercaseStr.equals("function")) {
      // special - convert on input
      return S.And;
    }
    if (lowercaseStr.equals("set") || lowercaseStr.equals("setdelayed")) {
      // special - convert on input
      return S.Equal;
    }
    if (nodeStr.length() == 1) {
      if (nodeStr.equals("I")) {
        // special - convert on input
        return F.CI;
      }
      return F.symbol(nodeStr, context, null, fEngine);
    }
    if (lowercaseStr.equals("infinity")) {
      // special - convert on input
      return F.CInfinity;
    } else if (lowercaseStr.equals("complexinfinity")) {
      // special - convert on input
      return F.CComplexInfinity;
    }
    String temp = AST2Expr.PREDEFINED_ALIASES_MAP.get(lowercaseStr);
    if (temp != null) {
      return F.symbol(temp, context, null, fEngine);
    }
    return F.symbol(lowercaseStr, context, null, fEngine);
  }

  private IExpr createInfixFunction(FuzzyInfixExprOperator infixOperator, IExpr lhs, IExpr rhs) {
    IASTMutable temp = infixOperator.createFunction(fFactory, this, lhs, rhs);
    if (temp.isAST()) {
      return convert(temp);
    }
    return temp;
    // if (infixOperator.getOperatorString().equals("//")) {
    // // lhs // rhs ==> rhs[lhs]
    // IAST function = F.ast(rhs);
    // function.add(lhs);
    // return function;
    // }
    // return F.$(F.$s(infixOperator.getFunctionName()), lhs, rhs);
  }

  /**
   * Determine the current BinaryOperator
   *
   * @return <code>null</code> if no binary operator could be determined
   */
  private FuzzyInfixExprOperator determineBinaryOperator() {
    Operator oper = null;
    for (int i = 0; i < fOperList.size(); i++) {
      oper = fOperList.get(i);
      if (oper instanceof FuzzyInfixExprOperator) {
        return (FuzzyInfixExprOperator) oper;
      }
    }
    return null;
  }

  /**
   * Determine the current PostfixOperator
   *
   * @return <code>null</code> if no postfix operator could be determined
   */
  private FuzzyPostfixExprOperator determinePostfixOperator() {
    Operator oper = null;
    for (int i = 0; i < fOperList.size(); i++) {
      oper = fOperList.get(i);
      if (oper instanceof FuzzyPostfixExprOperator) {
        return (FuzzyPostfixExprOperator) oper;
      }
    }
    return null;
  }

  /**
   * Determine the current PrefixOperator
   *
   * @return <code>null</code> if no prefix operator could be determined
   */
  private FuzzyPrefixExprOperator determinePrefixOperator() {
    Operator oper = null;
    for (int i = 0; i < fOperList.size(); i++) {
      oper = fOperList.get(i);
      if (oper instanceof FuzzyPrefixExprOperator) {
        return (FuzzyPrefixExprOperator) oper;
      }
    }
    return null;
  }

  /** construct the arguments for an expression */
  private void getArguments(final IASTAppendable function) throws SyntaxError {
    do {
      function.append(parseExpression());

      if (fToken != TT_COMMA) {
        break;
      }

      getNextToken();
      if (fToken == TT_PRECEDENCE_CLOSE || fToken == TT_ARGUMENTS_CLOSE) {
        function.append(S.Null);
        break;
      }
    } while (true);
  }

  private IExpr getFactor(final int min_precedence) throws SyntaxError {
    IExpr temp = null;
    switch (fToken) {
      case TT_IDENTIFIER:
        temp = getSymbol();
        if (temp.isSymbol() && fToken >= TT_BLANK && fToken <= TT_BLANK_COLON) {
          temp = getBlankPatterns(temp);
        }
        return parseArguments(temp);

      case TT_PRECEDENCE_OPEN:
        fRecursionDepth++;
        try {
          getNextToken();

          temp = parseExpression();

          if (fToken != TT_PRECEDENCE_CLOSE) {
            if (fToken == TT_COMMA) {
              // in "fuzzy" mode assume this is a list
              try {
                return parseArguments(getList(temp, TT_PRECEDENCE_CLOSE));
              } finally {
                fRecursionDepth--;
              }
            }
            throwSyntaxError("\')\' expected.");
          }
        } finally {
          fRecursionDepth--;
        }
        getNextToken();
        if (fToken == TT_PRECEDENCE_OPEN) {
          if (!fExplicitTimes) {
            Operator oper = fFactory.get("Times");
            if (FEConfig.DOMINANT_IMPLICIT_TIMES || oper.getPrecedence() >= min_precedence) {
              return getTimesImplicit(temp);
            }
          }
        }
        if (fToken == TT_ARGUMENTS_OPEN) {
          return getFunctionArguments(temp);
        }
        return temp;

      case TT_LIST_OPEN:
        fRecursionDepth++;
        try {
          return parseArguments(getList(F.NIL, TT_LIST_CLOSE));
        } finally {
          fRecursionDepth--;
        }
      case TT_ARGUMENTS_OPEN:
        fRecursionDepth++;
        try {
          return parseArguments(getList(F.NIL, TT_ARGUMENTS_CLOSE));
        } finally {
          fRecursionDepth--;
        }

      case TT_DIGIT:
        return getNumber(false);

      case TT_STRING:
        IStringX str = getString();
        return parseArguments(str);
      case TT_PERCENT:
        final IASTAppendable out = F.ast(S.Out);
        int countPercent = 1;
        getNextToken();
        if (fToken == TT_DIGIT) {
          countPercent = getJavaInt();
          out.append(countPercent);
          return out;
        }

        while (fToken == TT_PERCENT) {
          countPercent++;
          getNextToken();
        }

        out.append(-countPercent);
        return parseArguments(out);

      case TT_SLOT:
        getNextToken();
        if (fToken == TT_DIGIT) {
          int slotNumber = getJavaInt();
          if (slotNumber == 1) {
            return parseArguments(F.Slot1);
          } else if (slotNumber == 2) {
            return parseArguments(F.Slot2);
          }
          final IASTAppendable slot = F.ast(S.Slot);
          slot.append(slotNumber);
          return parseArguments(slot);
        } else if (fToken == TT_IDENTIFIER) {
          String[] identifierContext = getIdentifier();
          final IASTAppendable slot = F.ast(S.Slot);
          slot.append(identifierContext[0]);
          getNextToken();
          return parseArguments(slot);
        } else if (fToken == TT_STRING) {
          final IASTAppendable slot = F.ast(S.Slot);
          slot.append(getString());
          return parseArguments(slot);
        }
        return parseArguments(F.Slot1);

      case TT_SLOTSEQUENCE:
        getNextToken();
        final IASTAppendable slotSequencce = F.ast(S.SlotSequence);
        if (fToken == TT_DIGIT) {
          slotSequencce.append(getNumber(false));
        } else {
          slotSequencce.append(F.C1);
        }
        return parseArguments(slotSequencce);
      case TT_ASSOCIATION_OPEN:
        final IASTAppendable function = F.ast(S.List);
        fRecursionDepth++;
        try {
          getNextToken();
          do {
            function.append(parseExpression());
            if (fToken != TT_COMMA) {
              break;
            }

            getNextToken();
          } while (true);

          if (fToken != TT_ASSOCIATION_CLOSE) {
            throwSyntaxError("\'|>\' expected.");
          }
          try {
            temp = F.assoc(function);
          } catch (RuntimeException rex) {
            // fallback if no rules were parsed
            function.set(0, S.Association);
            temp = function;
          }
          getNextToken();
          if (fToken == TT_PRECEDENCE_OPEN) {
            if (!fExplicitTimes) {
              Operator oper = fFactory.get("Times");
              if (FEConfig.DOMINANT_IMPLICIT_TIMES || oper.getPrecedence() >= min_precedence) {
                return getTimesImplicit(temp);
              }
            }
          }
          if (fToken == TT_ARGUMENTS_OPEN) {
            return getFunctionArguments(temp);
          }
          return temp;
        } finally {
          fRecursionDepth--;
        }
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

    throwSyntaxError(
        "Error in factor at character: '"
            + fCurrentChar
            + "' (Token:"
            + fToken
            + " \\u"
            + Integer.toHexString(fCurrentChar | 0x10000).substring(1)
            + ")");
    return null;
  }

  /**
   * Parse '_' expressions.
   *
   * @param temp
   * @return
   */
  private IExpr getBlanks(IExpr temp) {
    switch (fToken) {
      case TT_BLANK:
        if (isWhitespace()) {
          getNextToken();
          temp = F.$b();
          // temp = fFactory.createPattern(null, null);
        } else {
          getNextToken();
          if (fToken == TT_IDENTIFIER) {
            final IExpr check = getSymbol();
            temp = F.$b(check);
            // temp = fFactory.createPattern(null, check);
          } else {
            temp = F.$b();
            // temp = fFactory.createPattern(null, null);
          }
        }
        break;
      case TT_BLANK_BLANK:
        // read '__'
        if (isWhitespace()) {
          getNextToken();
          temp = F.$ps(null, null);
          // temp = fFactory.createPattern2(null, null);
        } else {
          getNextToken();
          if (fToken == TT_IDENTIFIER) {
            final IExpr check = getSymbol();
            temp = F.$ps(null, check);
            // temp = fFactory.createPattern2(null, check);
          } else {
            temp = F.$ps(null, null);
            // temp = fFactory.createPattern2(null, null);
          }
        }
        break;
      case TT_BLANK_BLANK_BLANK:
        // read '___'
        if (isWhitespace()) {
          getNextToken();
          temp = F.$ps(null, null, false, true);
          // temp = fFactory.createPattern3(null, null);
        } else {
          getNextToken();
          if (fToken == TT_IDENTIFIER) {
            final IExpr check = getSymbol();
            temp = F.$ps(null, check, false, true);
            // temp = fFactory.createPattern3(null, check);
          } else {
            temp = F.$ps(null, null, false, true);
            // temp = fFactory.createPattern3(null, null);
          }
        }
        break;
      case TT_BLANK_OPTIONAL:
        // read '_.'
        if (isWhitespace()) {
          getNextToken();
          temp = F.$b(null, true);
          // temp = fFactory.createPattern(null, null, true);
        } else {
          getNextToken();
          if (fToken == TT_IDENTIFIER) {
            final IExpr check = getSymbol();
            temp = F.$b(check, true);
            // temp = fFactory.createPattern(null, check, true);
          } else {
            temp = F.$b(null, true);
            // temp = fFactory.createPattern(null, null, true);
          }
        }
        break;
      case TT_BLANK_COLON:
        // read '_:'
        getNextToken();
        IExpr defaultValue = parseExpression();
        temp = F.Optional(F.$b(), defaultValue);
        break;
    }

    if (fToken == TT_OPERATOR && fOperatorString.equals(":")) {
      getNextToken();
      IExpr defaultValue = parseExpression();
      temp = F.Optional(temp, defaultValue);
    }
    return parseArguments(temp);
  }

  /**
   * Parse 'symbol_' pattern expressions.
   *
   * @param head
   * @return
   */
  private IExpr getBlankPatterns(final IExpr head) {
    IExpr temp = head;
    final ISymbol symbol = (ISymbol) head;
    switch (fToken) {
      case TT_BLANK:
        // read '_'
        if (isWhitespace()) {
          temp = F.$p(symbol, null);
          getNextToken();
        } else {
          getNextToken();
          if (fToken == TT_IDENTIFIER) {
            final IExpr check = getSymbol();
            temp = F.$p(symbol, check);
          } else {
            temp = F.$p(symbol, null);
          }
        }
        break;
      case TT_BLANK_BLANK:
        // read '__'
        if (isWhitespace()) {
          temp = F.$ps(symbol, null);
          getNextToken();
        } else {
          getNextToken();
          if (fToken == TT_IDENTIFIER) {
            final IExpr check = getSymbol();
            temp = F.$ps(symbol, check);
          } else {
            temp = F.$ps(symbol, null);
          }
        }
        break;
      case TT_BLANK_BLANK_BLANK:
        // read '___'
        if (isWhitespace()) {
          temp = F.$ps(symbol, null, false, true);
          getNextToken();
        } else {
          getNextToken();
          if (fToken == TT_IDENTIFIER) {
            final IExpr check = getSymbol();
            temp = F.$ps(symbol, check, false, true);
          } else {
            temp = F.$ps(symbol, null, false, true);
          }
        }
        break;
      case TT_BLANK_OPTIONAL:
        // read '_.'
        if (isWhitespace()) {
          temp = F.$p(symbol, null, true);
          getNextToken();
        } else {
          getNextToken();
          if (fToken == TT_IDENTIFIER) {
            final IExpr check = getSymbol();
            temp = F.$p(symbol, check, true);
          } else {
            temp = F.$p(symbol, null, true);
          }
        }
        break;
      case TT_BLANK_COLON:
        // read '_:'
        getNextToken();
        IExpr defaultValue = parseExpression();
        temp = F.Optional(F.$p(symbol), defaultValue);
        break;
    }
    if (fToken == TT_OPERATOR && fOperatorString.equals(":")) {
      getNextToken();
      IExpr defaultValue = parseExpression();
      temp = F.Optional(temp, defaultValue);
    }
    return temp;
  }

  public IParserFactory getFactory() {
    return fFactory;
  }

  /** Get a function f[...][...] */
  IASTMutable getFunction(final IExpr head) throws SyntaxError {

    getNextToken();

    if (fToken == TT_PRECEDENCE_CLOSE) {
      getNextToken();
      if (fToken == TT_PRECEDENCE_OPEN) {
        return F.headAST0(head);
      }
      if (fToken == TT_ARGUMENTS_OPEN) {
        return getFunctionArguments(F.headAST0(head));
      }
      return F.headAST0(head);
    }

    int size = determineSize(head, 10);
    final IASTAppendable function = F.ast(head, size, false);
    fRecursionDepth++;
    try {
      getArguments(function);
    } finally {
      fRecursionDepth--;
    }

    if (fToken == TT_PRECEDENCE_CLOSE) {
      getNextToken();
      if (fToken == TT_PRECEDENCE_OPEN) {
        reduceAST(function);
      }
      if (fToken == TT_ARGUMENTS_OPEN) {
        return getFunctionArguments(reduceAST(function));
      }
      return reduceAST(function);
    }

    throwSyntaxError("')' expected.");
    return null;
  }

  private static int determineSize(final IExpr head, int defaultSize) {
    if (head.isBuiltInSymbol()) {
      IEvaluator eval = ((IBuiltInSymbol) head).getEvaluator();
      if (eval instanceof IFunctionEvaluator) {
        int[] args = ((IFunctionEvaluator) eval).expectedArgSize(F.NIL);
        if (args != null && args[1] < 10) {
          defaultSize = args[1] + 1;
        }
      }
    }
    return defaultSize;
  }

  private static IASTMutable reduceAST(IASTMutable function) {
    int size = function.size();
    switch (size) {
      case 1:
        return F.headAST0(function.head());
      case 2:
        return F.unaryAST1(function.head(), function.arg1());
      case 3:
        return F.binaryAST2(function.head(), function.arg1(), function.arg2());
      case 4:
        return F.ternaryAST3(function.head(), function.arg1(), function.arg2(), function.arg3());
    }
    return function;
  }

  /** Get a function f[...][...] */
  IASTMutable getFunctionArguments(final IExpr head) throws SyntaxError {

    fRecursionDepth++;

    getNextToken();

    if (fToken == TT_ARGUMENTS_CLOSE) {
      fRecursionDepth--;
      getNextToken();
      if (fToken == TT_ARGUMENTS_OPEN) {
        return getFunctionArguments(F.headAST0(head));
      }
      return F.headAST0(head);
    }

    final IASTAppendable function = F.ast(head);
    getArguments(function);

    fRecursionDepth--;
    if (fToken == TT_ARGUMENTS_CLOSE) {
      getNextToken();
      if (fToken == TT_ARGUMENTS_OPEN) {
        return getFunctionArguments(reduceAST(function));
      }
      return reduceAST(function);
    }

    throwSyntaxError("']' expected.");
    return null;
  }

  /** Get a list {...} */
  private IExpr getList(IExpr first, int endToken) throws SyntaxError {
    fRecursionDepth++;
    IASTAppendable function = F.ListAlloc(16);
    if (first.isPresent()) {
      function.append(first);
    }
    try {
      getNextToken();
      if (fToken == endToken) {
        getNextToken();
        return F.CEmptyList;
      }
      getArguments(function);
    } finally {
      fRecursionDepth--;
    }
    if (fToken == endToken) {
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
  private IExpr getNumber(final boolean negative) throws SyntaxError {
    IExpr temp = null;
    final Object[] result = getNumberString();
    String number = (String) result[0];
    int numFormat = ((Integer) result[1]).intValue();
    try {
      if (negative) {
        number = '-' + number;
      }
      if (numFormat == 10 && fCurrentChar == '`') {
        numFormat = -1;
      }
      if (numFormat < 0) {
        if (fCurrentChar == '`' && isValidPosition()) {
          fCurrentPosition++;
          if (isValidPosition() && fInputString[fCurrentPosition] == '`') {
            fCurrentPosition += 2;
            long precision = getJavaLong();
            if (precision < FEConfig.MACHINE_PRECISION) {
              precision = FEConfig.MACHINE_PRECISION;
            }
            return F.num(new Apfloat(number, precision));
          } else {
            fCurrentPosition++;
            long precision = getJavaLong();
            if (precision < FEConfig.MACHINE_PRECISION) {
              precision = FEConfig.MACHINE_PRECISION;
            }
            return F.num(new Apfloat(number, precision));
          }
        }
        temp = new NumStr(number);
        // temp = fFactory.createDouble(number);
      } else {
        temp = F.ZZ(number, numFormat);
        // temp = fFactory.createInteger(number, numFormat);
      }
    } catch (final RuntimeException rex) {
      throwSyntaxError("Number format error: " + number, number.length());
    }
    getNextToken();
    return temp;
  }

  @Override
  protected boolean isOperatorCharacters() {
    return fFactory.isOperatorChar(fCurrentChar);
  }

  @Override
  protected boolean isOperatorCharacters(char ch) {
    return fFactory.isOperatorChar(ch);
  }

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
    throwSyntaxError(
        "Operator token not found: "
            + new String(fInputString, startPosition, endPosition - 1 - startPosition));
    return null;
  }

  /** Get a <i>part [[..]]</i> of an expression <code>{a,b,c}[[2]]</code> &rarr; <code>b</code> */
  private IExpr getPart(final int min_precedence) throws SyntaxError {
    IExpr temp = getFactor(min_precedence);

    if (fToken != TT_PARTOPEN) {
      return temp;
    }

    IASTAppendable function = null;
    do {
      if (function == null) {
        function = F.Part(2, temp);
        // function =
        // fFactory.createFunction(fFactory.createSymbol(IConstantOperators.Part),
        // temp);
      } else {
        function = F.Part(2, function);
        // function =
        // fFactory.createFunction(fFactory.createSymbol(IConstantOperators.Part),
        // function);
      }

      fRecursionDepth++;
      try {
        do {
          getNextToken();

          // if (fToken == TT_SPAN) {
          // IASTAppendable span = F.ast(F.Span);
          // function.append(span);
          // span.append(F.C1);
          // getNextToken();
          // if (fToken == TT_SPAN) {
          // span.append(F.All);
          // getNextToken();
          // if (fToken == TT_COMMA) {
          // continue;
          // }
          // if (fToken == TT_PARTCLOSE || fToken == TT_ARGUMENTS_CLOSE
          // || fToken == TT_PRECEDENCE_CLOSE) {
          // break;
          // }
          // } else if (fToken == TT_COMMA) {
          // span.append(F.All);
          // continue;
          // } else if (fToken == TT_PARTCLOSE || fToken == TT_ARGUMENTS_CLOSE
          // || fToken == TT_PRECEDENCE_CLOSE) {
          // span.append(F.All);
          // break;
          // }
          // span.append(parseExpression());
          // if (fToken == TT_COMMA) {
          // continue;
          // }
          // break;
          // }
          if (fToken == TT_ARGUMENTS_CLOSE) {
            if (fInputString.length > fCurrentPosition && fInputString[fCurrentPosition] == ']') {
              throwSyntaxError("Statement (i.e. index) expected in [[ ]].");
            }
          }

          temp = parseExpression();
          function.append(temp);
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
        // }
      } finally {
        fRecursionDepth--;
      }
      getNextToken();
    } while (fToken == TT_PARTOPEN);

    return parseArguments(function);
  }

  /**
   * Get the string as IStringX.
   *
   * @return
   * @throws SyntaxError
   */
  private IStringX getString() throws SyntaxError {
    final StringBuilder ident = getStringBuilder();

    getNextToken();

    return F.stringx(ident);
  }

  /**
   * Read the current identifier from the expression factories table
   *
   * @return
   * @see
   */
  private IExpr getSymbol() throws SyntaxError {
    String[] identifierContext = getIdentifier();
    if (!fFactory.isValidIdentifier(identifierContext[0])) {
      throwSyntaxError("Invalid identifier: " + identifierContext[0] + " detected.");
    }

    final IExpr symbol = convertSymbolOnInput(identifierContext[0], identifierContext[1]);
    getNextToken();
    return symbol;
  }

  private IExpr getTimesImplicit(IExpr temp) throws SyntaxError {
    IASTAppendable func = F.TimesAlloc(8);
    func.append(temp);
    do {
      getNextToken();
      temp = parseExpression();
      func.append(temp);
      if (fToken != TT_PRECEDENCE_CLOSE) {
        throwSyntaxError("\')\' expected.");
      }
      getNextToken();
    } while (fToken == TT_PRECEDENCE_OPEN);
    func.addEvalFlags(IAST.TIMES_PARSED_IMPLICIT);
    return func;
  }

  /**
   * Parse the given <code>expression</code> String into an {@link IExpr} based object tree.
   *
   * @param expression a formula string which should be parsed.
   * @return the parsed {@link IExpr} representation of the given formula string
   * @throws SyntaxError
   */
  public IExpr parse(final String expression) throws SyntaxError {
    initialize(expression);
    final IExpr temp = parseExpression();
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
    // determine the precision of the input before evaluation
    long precision = temp.determinePrecision();
    if (precision > fEngine.getNumericPrecision()) {
      fEngine.setNumericPrecision(precision);
    }
    return temp;
  }

  private IExpr parseArguments(IExpr head) {
    if (fToken == TT_ARGUMENTS_OPEN) {
      if (FEConfig.PARSER_USE_STRICT_SYNTAX) {
        if (head.isSymbolOrPattern()) {
          throwSyntaxError("'(' expected after symbol or pattern instead of '['.");
        }
      }
      IASTMutable ast = getFunctionArguments(head);
      return convert(ast);
    } else if (fToken == TT_PRECEDENCE_OPEN) {
      IASTMutable ast = getFunction(head);
      return convert(ast);
    }

    return head;
  }

  private IExpr parseCompoundExpressionNull(FuzzyInfixExprOperator infixOperator, IExpr rhs) {
    if (infixOperator.isOperator(";")) {
      if (fToken == TT_EOF
          || fToken == TT_ARGUMENTS_CLOSE
          || fToken == TT_LIST_CLOSE
          || fToken == TT_PRECEDENCE_CLOSE
          || fToken == TT_COMMA) {
        return createInfixFunction(infixOperator, rhs, S.Null);
        // return infixOperator.createFunction(fFactory, rhs,
        // fFactory.createSymbol("Null"));
      }
      // if (fPackageMode && fRecursionDepth < 1) {
      // return createInfixFunction(infixOperator, rhs, F.Null);
      // }
    }
    return null;
  }

  protected IExpr parseExpression() {
    if (fToken == TT_SPAN) {
      IASTAppendable span = F.ast(S.Span);
      span.append(F.C1);
      getNextToken();
      if (fToken == TT_SPAN) {
        span.append(S.All);
        getNextToken();
        if (fToken == TT_COMMA
            || fToken == TT_PARTCLOSE
            || fToken == TT_ARGUMENTS_CLOSE
            || fToken == TT_PRECEDENCE_CLOSE) {
          return span;
        }
      } else if (fToken == TT_COMMA
          || fToken == TT_PARTCLOSE
          || fToken == TT_ARGUMENTS_CLOSE
          || fToken == TT_PRECEDENCE_CLOSE) {
        span.append(S.All);
        return span;
      }
      span.append(parseExpression(parsePrimary(0), 0));
      return span;
    }
    IExpr temp = parseExpression(parsePrimary(0), 0);

    if (fToken == TT_SPAN) {
      IASTAppendable span = F.ast(S.Span);
      span.append(temp);
      getNextToken();
      if (fToken == TT_SPAN) {
        span.append(S.All);
        getNextToken();
        if (fToken == TT_COMMA
            || fToken == TT_PARTCLOSE
            || fToken == TT_ARGUMENTS_CLOSE
            || fToken == TT_PRECEDENCE_CLOSE) {
          return span;
        }
      } else if (fToken == TT_COMMA
          || fToken == TT_PARTCLOSE
          || fToken == TT_ARGUMENTS_CLOSE
          || fToken == TT_PRECEDENCE_CLOSE) {
        span.append(S.All);
        return span;
      }
      span.append(parseExpression(parsePrimary(0), 0));
      if (fToken == TT_SPAN) {
        getNextToken();
        if (fToken == TT_COMMA
            || fToken == TT_PARTCLOSE
            || fToken == TT_ARGUMENTS_CLOSE
            || fToken == TT_PRECEDENCE_CLOSE) {
          return span;
        }
        span.append(parseExpression(parsePrimary(0), 0));
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
  private IExpr parseExpression(IExpr lhs, final int min_precedence) {
    IExpr rhs = null;
    Operator oper;
    FuzzyInfixExprOperator infixOperator;
    FuzzyPostfixExprOperator postfixOperator;
    while (true) {
      if (fToken == TT_NEWLINE) {
        return lhs;
      }
      if ((fToken == TT_LIST_OPEN)
          || (fToken == TT_PRECEDENCE_OPEN)
          || (fToken == TT_ASSOCIATION_OPEN)
          || (fToken == TT_IDENTIFIER)
          || (fToken == TT_STRING)
          || (fToken == TT_DIGIT)
          || (fToken == TT_SLOT)
          || (fToken == TT_SLOTSEQUENCE)) {
        // if (fPackageMode && fRecursionDepth < 1) {
        // return lhs;
        // }
        // if (fPackageMode && fToken == TT_IDENTIFIER && fLastChar ==
        // '\n') {
        // return lhs;
        // }

        if (!fExplicitTimes) {
          // lazy evaluation of multiplication
          oper = fFactory.get("Times");
          if (FEConfig.DOMINANT_IMPLICIT_TIMES || oper.getPrecedence() >= min_precedence) {
            if (Config.FUZZY_PARSER && fToken == TT_IDENTIFIER) {
              rhs = parseExpression();
            } else {
              rhs = parseLookaheadOperator(oper.getPrecedence());
            }
            lhs = F.$(S.Times, lhs, rhs);
            ((IAST) lhs).addEvalFlags(IAST.TIMES_PARSED_IMPLICIT);
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
        infixOperator = determineBinaryOperator();

        if (infixOperator != null) {
          if (infixOperator.getPrecedence() >= min_precedence) {
            getNextToken();
            IExpr compoundExpressionNull = parseCompoundExpressionNull(infixOperator, lhs);
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
          postfixOperator = determinePostfixOperator();

          if (postfixOperator != null) {
            if (postfixOperator.getPrecedence() >= min_precedence) {
              lhs = parsePostfixOperator(lhs, postfixOperator);
              continue;
            }
            // } else {
            // throwSyntaxError("Operator: " + fOperatorString + " is no infix or postfix
            // operator.");
          }
        }
      }
      break;
    }
    return lhs;
  }

  private final IExpr parseInfixOperator(IExpr lhs, FuzzyInfixExprOperator infixOperator) {
    IExpr rhs;
    rhs = parseLookaheadOperator(infixOperator.getPrecedence());
    lhs = createInfixFunction(infixOperator, lhs, rhs);
    if (lhs instanceof IASTAppendable) {
      IASTAppendable ast = (IASTAppendable) lhs;
      int headID = ast.headID();
      if ((headID >= ID.Equal && headID <= ID.Unequal)
          && //
          (headID == ID.Equal
              || headID == ID.Greater
              || headID == ID.GreaterEqual
              || headID == ID.Less
              || headID == ID.LessEqual
              || headID == ID.Unequal)) {
        while (fToken == TT_OPERATOR
            && infixOperator.getGrouping() == InfixOperator.NONE
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
          ast.append(rhs);
        }
        return ast;
      }
      while (fToken == TT_OPERATOR
          && infixOperator.getGrouping() == InfixOperator.NONE
          && infixOperator.isOperator(fOperatorString)) {
        getNextToken();
        if (infixOperator.isOperator(";")) {
          if (fToken == TT_EOF
              || fToken == TT_ARGUMENTS_CLOSE
              || fToken == TT_LIST_CLOSE
              || fToken == TT_PRECEDENCE_CLOSE
              || fToken == TT_COMMA) {
            ast.append(S.Null);
            break;
          }
        }
        while (fToken == TT_NEWLINE) {
          getNextToken();
        }
        rhs = parseLookaheadOperator(infixOperator.getPrecedence());
        ast.append(rhs);
      }

      return ast;
    } else {
      if (fToken == TT_OPERATOR
          && infixOperator.getGrouping() == InfixOperator.NONE
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
  private IExpr parseInequality(final IAST ast, final FuzzyInfixExprOperator infixOperator) {
    // rewrite to Inequality
    IBuiltInSymbol head = (IBuiltInSymbol) ast.head();
    IASTAppendable result = F.ast(S.Inequality, ast.size() + 2, false);
    ast.forEach(
        x -> {
          result.append(x);
          result.append(head);
        });
    FuzzyInfixExprOperator compareOperator = determineBinaryOperator();
    result.set(result.size() - 1, F.$s(compareOperator.getFunctionName()));
    getNextToken();
    while (fToken == TT_NEWLINE) {
      getNextToken();
    }
    int precedence = infixOperator.getPrecedence();
    result.append(parseLookaheadOperator(precedence));

    while (fToken == TT_OPERATOR && isComparatorOperator(fOperatorString)) {
      compareOperator = determineBinaryOperator();
      result.append(F.$s(compareOperator.getFunctionName()));
      getNextToken();
      while (fToken == TT_NEWLINE) {
        getNextToken();
      }
      result.append(parseLookaheadOperator(precedence));
    }
    return result;
  }

  private final IExpr parsePostfixOperator(IExpr lhs, FuzzyPostfixExprOperator postfixOperator) {
    getNextToken();
    lhs = postfixOperator.createFunction(fFactory, lhs);
    lhs = parseArguments(lhs);
    return lhs;
  }

  private IExpr parseLookaheadOperator(final int min_precedence) {
    IExpr rhs = parsePrimary(min_precedence);

    while (true) {
      final int lookahead = fToken;
      if (fToken == TT_NEWLINE) {
        return rhs;
      }
      if ((fToken == TT_LIST_OPEN)
          || (fToken == TT_PRECEDENCE_OPEN)
          || (fToken == TT_ASSOCIATION_OPEN)
          || (fToken == TT_IDENTIFIER)
          || (fToken == TT_STRING)
          || (fToken == TT_DIGIT)
          || (fToken == TT_SLOT)) {
        if (!fExplicitTimes) {
          // lazy evaluation of multiplication
          FuzzyInfixExprOperator timesOperator = (FuzzyInfixExprOperator) fFactory.get("Times");
          if (FEConfig.DOMINANT_IMPLICIT_TIMES || timesOperator.getPrecedence() > min_precedence) {
            rhs = parseExpression(rhs, timesOperator.getPrecedence());
            continue;
          } else if ((timesOperator.getPrecedence() == min_precedence)
              && (timesOperator.getGrouping() == FuzzyInfixExprOperator.RIGHT_ASSOCIATIVE)) {
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
        FuzzyInfixExprOperator infixOperator = determineBinaryOperator();
        if (infixOperator != null) {
          if (infixOperator.getPrecedence() > min_precedence
              || ((infixOperator.getPrecedence() == min_precedence)
                  && (infixOperator.getGrouping() == FuzzyInfixExprOperator.RIGHT_ASSOCIATIVE))) {
            // if (infixOperator.isOperator(";")) {
            // rhs = F.Null;
            // if (fPackageMode && fRecursionDepth < 1) {
            // return createInfixFunction(infixOperator, lhs,
            // rhs);
            // }
            // }
            rhs = parseExpression(rhs, infixOperator.getPrecedence());
            continue;
          }

        } else {
          FuzzyPostfixExprOperator postfixOperator = determinePostfixOperator();
          if (postfixOperator != null) {
            if (postfixOperator.getPrecedence() >= min_precedence) {
              getNextToken();
              // rhs =
              // F.$(F.$s(postfixOperator.getFunctionName()),
              // rhs);
              rhs = postfixOperator.createFunction(fFactory, rhs);
              continue;
            }
          }
        }
      }
      break;
    }
    return rhs;
  }

  /**
   * Parse expressions like <code>expr''[x]</code>
   *
   * @param expr
   * @return
   */
  private IExpr parseDerivative(IExpr expr) {
    int derivativeCounter = 1;
    getNextToken();
    while (fToken == TT_DERIVATIVE) {
      derivativeCounter++;
      getNextToken();
    }
    IAST deriv = F.$(DERIVATIVE, F.ZZ(derivativeCounter));
    expr = F.$(deriv, expr);
    expr = parseArguments(expr);
    return expr;
  }

  public void parsePackage(final String expression) throws SyntaxError {
    initialize(expression);
    while (fToken == TT_NEWLINE) {
      getNextToken();
    }
    IExpr temp = parseExpression();
    fEngine.evaluate(temp);
    // fNodeList.add(temp);
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
        return;
        // return fNodeList;
      }
      temp = parseExpression();
      fEngine.evaluate(temp);
      // fNodeList.add(temp);
      // throwSyntaxError("End-of-file not reached.");
    }

    // return fNodeList;
  }

  private IExpr parsePrimary(final int min_precedence) {
    if (fToken == TT_OPERATOR) {
      if (fOperatorString.equals(".")) {
        fCurrentChar = '.';
        // fToken = TT_DIGIT;
        // return getPart();
        return getNumber(false);
      }
      final FuzzyPrefixExprOperator prefixOperator = determinePrefixOperator();
      if (prefixOperator != null) {
        return parsePrefixOperator(prefixOperator);
      }
      throwSyntaxError("Operator: " + fOperatorString + " is no prefix operator.");
    }
    return getPart(min_precedence);
  }

  private final IExpr parsePrefixOperator(final FuzzyPrefixExprOperator prefixOperator) {
    getNextToken();
    final IExpr temp = parseLookaheadOperator(prefixOperator.getPrecedence());
    if (prefixOperator.getFunctionName().equals("PreMinus")) {
      // special cases for negative numbers
      if (temp.isNumber()) {
        return temp.negate();
      }
    }
    return prefixOperator.createFunction(fFactory, temp);
  }

  public void setFactory(final IParserFactory factory) {
    this.fFactory = factory;
  }
}
