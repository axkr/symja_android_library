/*
 * Copyright 2005-2023 Axel Kramer (axelclk@gmail.com)
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
package org.matheclipse.core.parser;

import java.util.List;
import java.util.Locale;
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
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.ParserConfig;
import org.matheclipse.parser.client.Scanner;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.ast.IParserFactory;
import org.matheclipse.parser.client.operator.InfixOperator;
import org.matheclipse.parser.client.operator.Operator;
import org.matheclipse.parser.client.operator.Precedence;

/**
 * Create an expression of the {@link IExpr} class-hierarchy from a math formula's string
 * representation.
 *
 * <p>
 * See <a href="http://en.wikipedia.org/wiki/Operator-precedence_parser">Operator -precedence
 * parser</a> for the idea, how to parse the operators depending on their precedence.
 */
public class ExprParser extends Scanner {

  static {
    F.initSymbols();
  }

  public static final ISymbol DERIVATIVE = F.Derivative;

  public static int syntaxLength(final String str, EvalEngine engine) throws SyntaxError {
    try {
      ExprParser parser = new ExprParser(engine);
      parser.parse(str);
    } catch (final SyntaxError e) {
      return e.getStartOffset();
    }
    return str.length();
  }

  public static boolean test(final String str, EvalEngine engine) {
    try {
      ExprParser fParser = new ExprParser(engine);
      final IExpr parsedExpression = fParser.parse(str);
      if (parsedExpression != null) {
        return true;
      }
    } catch (final SyntaxError e) {

    }
    return false;
  }

  /** Set to true if the expression shouldn't be evaluated on input */
  private boolean fHoldExpression;

  /**
   * If <code>true</code> the parser doesn't distinguish between lower- or uppercase symbols (i.e.
   * constants, function names,...), with the exception of symbols with only one character (i.e. the
   * variable &quot;i&quot; is different from the imaginary unit &quot;I&quot;)
   */
  private final boolean fRelaxedSyntax;

  private final EvalEngine fEngine;

  protected IParserFactory fFactory;

  public ExprParser(final EvalEngine engine) {
    this(engine, ExprParserFactory.MMA_STYLE_FACTORY, engine.isRelaxedSyntax(), false,
        ParserConfig.EXPLICIT_TIMES_OPERATOR);
  }

  /**
   * @param engine
   * @param relaxedSyntax if <code>true</code>, use '('...')' as brackets for arguments
   * @throws SyntaxError
   */
  public ExprParser(final EvalEngine engine, final boolean relaxedSyntax) {
    this(engine, ExprParserFactory.MMA_STYLE_FACTORY, relaxedSyntax);
  }

  /**
   * @param engine
   * @param factory
   * @param relaxedSyntax if <code>true</code>, use '('...')' as brackets for arguments
   * @throws SyntaxError
   */
  public ExprParser(final EvalEngine engine, IParserFactory factory, final boolean relaxedSyntax) {
    this(engine, factory, relaxedSyntax, false, ParserConfig.EXPLICIT_TIMES_OPERATOR);
  }

  public ExprParser(final EvalEngine engine, IParserFactory factory, final boolean relaxedSyntax,
      boolean packageMode, boolean explicitTimes) {
    super(packageMode, explicitTimes);
    this.fRelaxedSyntax = relaxedSyntax;
    this.fFactory = factory;
    this.fEngine = engine;
  }

  private IExpr convert(IASTMutable ast) {
    int headID = ast.headID();
    if (headID >= ID.Blank && headID <= ID.Sqrt) {
      // ID.Blank is lowest and ID.Sqrt is highest integer ID in followinh switch statement
      IExpr expr = F.NIL;
      switch (headID) {
        case ID.Get:
          if (ast.isAST1() && ast.arg1().isString()) {
            return S.Get.of(ast.arg1());
          }
          break;
        case ID.Import:
          if (ast.isAST1() && ast.arg1().isString()) {
            return S.Import.of(ast.arg1());
          }
          break;
        case ID.Exp:
          if (ast.isAST1()) {
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
          if (ast.isAST1()) {
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

        case ID.Blank:
          expr = PatternMatching.Blank.CONST.evaluate(ast, fEngine);
          break;
        case ID.BlankSequence:
          expr = PatternMatching.BlankSequence.CONST.evaluate(ast, fEngine);
          break;
        case ID.BlankNullSequence:
          expr = PatternMatching.BlankNullSequence.CONST.evaluate(ast, fEngine);
          break;
        case ID.Pattern:
          expr = PatternMatching.Pattern.CONST.evaluate(ast, fEngine);
          break;
        case ID.Optional:
          expr = PatternMatching.Optional.CONST.evaluate(ast, fEngine);
          break;
        // case ID.OptionsPattern:
        // expr = PatternMatching.OptionsPattern.CONST.evaluate(ast, fEngine);
        // break;
        case ID.Repeated:
          expr = PatternMatching.Repeated.CONST.evaluate(ast, fEngine);
          break;
        case ID.Complex:
          expr = Arithmetic.CONST_COMPLEX.evaluate(ast, fEngine);
          break;

        case ID.Rational:
          expr = Arithmetic.CONST_RATIONAL.evaluate(ast, fEngine);
          break;
        default:
          break;
      }
      return expr.orElse(ast);
    }
    return ast;
  }

  protected IExpr convertSymbolOnInput(final String nodeStr, final String context,
      boolean convertOnSymbol) {
    if (fRelaxedSyntax) {
      if (nodeStr.length() == 1) {
        if (convertOnSymbol && nodeStr.equals("I")) {
          // special - convert on input
          return F.CI;
        }
        return F.symbol(nodeStr, context, null, fEngine);
      }
      String lowercaseStr = nodeStr.toLowerCase(Locale.ENGLISH);
      if (convertOnSymbol) {
        if (lowercaseStr.equals("infinity")) {
          // special - convert on input
          return F.CInfinity;
        } else if (lowercaseStr.equals("complexinfinity")) {
          // special - convert on input
          return F.CComplexInfinity;
        }
      }
      String temp = AST2Expr.PREDEFINED_ALIASES_MAP.get(lowercaseStr);
      if (temp != null) {
        return F.symbol(temp, context, null, fEngine);
      }
      return F.symbol(lowercaseStr, context, null, fEngine);
    } else {
      String lowercaseStr = nodeStr;
      if (Config.RUBI_CONVERT_SYMBOLS) {
        Integer num = AST2Expr.RUBI_STATISTICS_MAP.get(lowercaseStr);
        if (num == null) {
          AST2Expr.RUBI_STATISTICS_MAP.put(lowercaseStr, 1);
        } else {
          AST2Expr.RUBI_STATISTICS_MAP.put(lowercaseStr, num + 1);
        }
      }

      if (convertOnSymbol) {
        if (lowercaseStr.equals("I")) {
          // special - convert on input
          return F.CI;
        } else if (lowercaseStr.equals("Infinity")) {
          // special - convert on input
          return F.CInfinity;
        } else if (lowercaseStr.equals("ComplexInfinity")) {
          // special - convert on input
          return F.CComplexInfinity;
        }
      }
      return F.symbol(lowercaseStr, context, null, fEngine);
    }
  }

  protected IExpr convertSymbolOnInput(final ISymbol symbol) {
    if (symbol == S.I) {
      return F.CI;
    } else if (symbol == S.Infinity) {
      return F.CInfinity;
    } else if (symbol == S.ComplexInfinity) {
      return F.CComplexInfinity;
    }
    return symbol;
  }

  private IExpr createInfixFunction(InfixExprOperator infixOperator, IExpr lhs, IExpr rhs) {
    IASTMutable temp = infixOperator.createFunction(fFactory, this, lhs, rhs);
    if (temp.isAST()) {
      return convert(temp);
    }
    return temp;
  }

  /**
   * Determine the current BinaryOperator
   *
   * @return <code>null</code> if no binary operator could be determined
   */
  private InfixExprOperator determineBinaryOperator() {
    Operator oper = null;
    for (int i = 0; i < fOperList.size(); i++) {
      oper = fOperList.get(i);
      if (oper instanceof InfixExprOperator) {
        return (InfixExprOperator) oper;
      }
    }
    return null;
  }

  /**
   * Determine the current PostfixOperator
   *
   * @return <code>null</code> if no postfix operator could be determined
   */
  private PostfixExprOperator determinePostfixOperator() {
    Operator oper = null;
    for (int i = 0; i < fOperList.size(); i++) {
      oper = fOperList.get(i);
      if (oper instanceof PostfixExprOperator) {
        return (PostfixExprOperator) oper;
      }
    }
    return null;
  }

  /**
   * Determine the current PrefixOperator
   *
   * @return <code>null</code> if no prefix operator could be determined
   */
  private PrefixExprOperator determinePrefixOperator() {
    Operator oper = null;
    for (int i = 0; i < fOperList.size(); i++) {
      oper = fOperList.get(i);
      if (oper instanceof PrefixExprOperator) {
        return (PrefixExprOperator) oper;
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
        temp = getSymbol(false);
        if (temp.isSymbol()) {
          ISymbol symbol = (ISymbol) temp;
          if (fToken >= TT_BLANK && fToken <= TT_BLANK_COLON) {
            temp = getBlankPatterns(symbol);
          } else {
            temp = convertSymbolOnInput(symbol);
          }
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
          if (!fExplicitTimes) {
            // Operator oper = fFactory.get("Times");
            if (ParserConfig.DOMINANT_IMPLICIT_TIMES || Precedence.TIMES >= min_precedence) {
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
          return parseArguments(getList());
        } finally {
          fRecursionDepth--;
        }

      case TT_BLANK:
      case TT_BLANK_BLANK:
      case TT_BLANK_BLANK_BLANK:
      case TT_BLANK_OPTIONAL:
      case TT_BLANK_COLON:
        return getBlanks(temp);

      case TT_DIGIT:
        return getNumber(false);

      case TT_STRING:
        IStringX str = getString();
        return parseArguments(str);
      case TT_PERCENT:
        int countPercent = 1;
        getNextToken();
        if (fToken == TT_DIGIT) {
          countPercent = getJavaInt();
          return F.Out(countPercent);
        }

        while (fToken == TT_PERCENT) {
          countPercent++;
          getNextToken();
        }
        return parseArguments(F.Out(-countPercent));
      case TT_SLOT:
        getNextToken();
        if (fToken == TT_DIGIT) {
          int slotNumber = getJavaInt();
          if (slotNumber == 1) {
            return parseArguments(F.Slot1);
          } else if (slotNumber == 2) {
            return parseArguments(F.Slot2);
          }
          return parseArguments(F.Slot(slotNumber));
        } else if (fToken == TT_IDENTIFIER) {
          String[] identifierContext = getIdentifier();
          final IAST slot = F.Slot(identifierContext[0]);
          getNextToken();
          return parseArguments(slot);
        } else if (fToken == TT_STRING) {
          return parseArguments(F.Slot(getString()));
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
        final IASTAppendable function = F.ListAlloc(31);
        fRecursionDepth++;
        try {
          getNextToken();
          if (fToken != TT_ASSOCIATION_CLOSE) {
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
          }
          // try {
          // temp = F.assoc(function);
          // } catch (MathException mex) {
          // // fallback if no rules were parsed
          function.set(0, S.Association);
          temp = function;
          // }
          getNextToken();
          if (fToken == TT_PRECEDENCE_OPEN) {
            if (!fExplicitTimes) {
              // Operator oper = fFactory.get("Times");
              if (ParserConfig.DOMINANT_IMPLICIT_TIMES || Precedence.TIMES >= min_precedence) {
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

      default:
        break;
    }

    throwSyntaxError("Error in factor at character: '" + fCurrentChar + "' (Token:" + fToken
        + " \\u" + Integer.toHexString(fCurrentChar | 0x10000).substring(1) + ")");
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
        } else {
          getNextToken();
          if (fToken == TT_IDENTIFIER) {
            final IExpr check = getSymbol(true);
            temp = F.$b(check);
          } else {
            temp = F.$b();
          }
        }
        break;
      case TT_BLANK_BLANK:
        // read '__'
        if (isWhitespace()) {
          getNextToken();
          temp = F.$ps(null, null);
        } else {
          getNextToken();
          if (fToken == TT_IDENTIFIER) {
            final IExpr check = getSymbol(true);
            temp = F.$ps(null, check);
          } else {
            temp = F.$ps(null, null);
          }
        }
        break;
      case TT_BLANK_BLANK_BLANK:
        // read '___'
        if (isWhitespace()) {
          getNextToken();
          temp = F.$ps(null, null, false, true);
        } else {
          getNextToken();
          if (fToken == TT_IDENTIFIER) {
            final IExpr check = getSymbol(true);
            temp = F.$ps(null, check, false, true);
          } else {
            temp = F.$ps(null, null, false, true);
          }
        }
        break;
      case TT_BLANK_OPTIONAL:
        // read '_.'
        if (isWhitespace()) {
          getNextToken();
          temp = F.$b(null, true);
        } else {
          getNextToken();
          if (fToken == TT_IDENTIFIER) {
            final IExpr check = getSymbol(true);
            temp = F.$b(check, true);
          } else {
            temp = F.$b(null, true);
          }
        }
        break;
      case TT_BLANK_COLON:
        // read '_:'
        getNextToken();
        IExpr defaultValue = parseExpression();
        temp = F.Optional(F.$b(), defaultValue);
        break;
      default:
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
            final IExpr check = getSymbol(true);
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
            final IExpr check = getSymbol(true);
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
            final IExpr check = getSymbol(true);
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
            final IExpr check = getSymbol(true);
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
      default:
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

    if (fRelaxedSyntax) {
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
    } else {
      if (fToken == TT_ARGUMENTS_CLOSE) {
        getNextToken();
        if (fToken == TT_ARGUMENTS_OPEN) {
          return getFunctionArguments(F.headAST0(head));
        }
        return F.headAST0(head);
      }
    }

    int size = determineSize(head, 10);
    final IASTAppendable function = F.ast(head, size);
    fRecursionDepth++;
    try {
      getArguments(function);
    } finally {
      fRecursionDepth--;
    }
    if (fRelaxedSyntax) {
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
    } else {
      if (fToken == TT_ARGUMENTS_CLOSE) {
        getNextToken();
        if (fToken == TT_ARGUMENTS_OPEN) {
          return getFunctionArguments(reduceAST(function));
        }
        return reduceAST(function);
      }
    }

    throwSyntaxError(fRelaxedSyntax ? "')' expected." : "']' expected.");
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
  private IExpr getList() throws SyntaxError {
    fRecursionDepth++;
    IASTAppendable function = null;
    try {
      getNextToken();
      if (fToken == TT_LIST_CLOSE) {
        getNextToken();
        return F.CEmptyList;
      }
      function = F.ListAlloc(31);
      getArguments(function);
    } finally {
      fRecursionDepth--;
    }
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
  private IExpr getNumber(final boolean negative) throws SyntaxError {
    IExpr temp = null;
    final Object[] result = getNumberString();
    String numberStr = (String) result[0];
    int numFormat = ((Integer) result[1]);
    String exponentStr = (String) result[2];
    try {
      if (negative) {
        numberStr = '-' + numberStr;
      }
      if (numFormat == 10 && fCurrentChar == '`') {
        numFormat = -1;
      }
      if (numFormat < 0) {
        if (fCurrentChar == '`' && isValidPosition()) {
          fCurrentPosition++;
          if (isValidPosition() && fInputString[fCurrentPosition] == '*') {
            fCurrentPosition++;
            if (isValidPosition() && fInputString[fCurrentPosition] == '^') {
              fCurrentPosition += 2;
              long exponent = getJavaLong();
              Double d = Double.valueOf(numberStr + "E" + exponent);
              return F.num(d);
            }
          } else if (isValidPosition() && fInputString[fCurrentPosition] == '`') {
            fCurrentPosition += 2;
            long precision = getJavaLong();
            if (precision < ParserConfig.MACHINE_PRECISION) {
              precision = ParserConfig.MACHINE_PRECISION;
            }
            return F.num(numberStr, precision);
          } else {
            if (isValidPosition() && Character.isDigit(fInputString[fCurrentPosition])) {
              fCurrentPosition++;
              long precision = getJavaLong();
              if (precision < ParserConfig.MACHINE_PRECISION) {
                precision = ParserConfig.MACHINE_PRECISION;
              }
              return F.num(numberStr, precision);
            } else {
              getNextToken();
              return F.num(numberStr);
            }
          }
          throwSyntaxError("Number format error: " + numberStr, numberStr.length());
        }
        temp = new NumStr(numberStr);
      } else {
        if (exponentStr == null || exponentStr.equals("1")) {
          temp = F.ZZ(numberStr, numFormat);
        } else {
          if (numFormat == 10) {
            try {
              int exponent = Integer.parseInt(exponentStr, numFormat);
              if (exponent < 0) {
                exponent = -exponent;
                StringBuilder buf = createPowersOf10(exponent);
                temp = F.Times(F.ZZ(numberStr, numFormat),
                    F.Power(F.ZZ(buf.toString(), numFormat), F.CN1));
              } else {
                StringBuilder buf = createPowersOf10(exponent);
                temp = F.Times(F.ZZ(numberStr, numFormat), F.ZZ(buf.toString(), numFormat));
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
    } catch (final RuntimeException rex) {
      throwSyntaxError("Number format error: " + numberStr, numberStr.length());
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
    char lastChar = fCurrentChar;
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
    final int endPosition = fCurrentPosition;
    fCurrentPosition = startPosition;
    throwSyntaxError("Operator token not found: "
        + new String(fInputString, startPosition, endPosition - 1 - startPosition));
    return null;
  }

  /** Get a <i>part [[..]]</i> of an expression <code>{a,b,c}[[2]]</code> &rarr; <code>b</code> */
  private IExpr getPart(final int min_precedence) throws SyntaxError {
    IASTAppendable function = null;
    IExpr temp = getFactor(min_precedence);
    if (fToken != TT_PARTOPEN) {
      return temp;
    }

    do {
      if (function == null) {
        function = F.Part(2, temp);
      } else {
        function = F.Part(2, function);
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
   * @param convertOnInput TODO
   *
   * @return
   * @see
   */
  private IExpr getSymbol(boolean convertOnInput) throws SyntaxError {
    String[] identifierContext = getIdentifier();
    if (!fFactory.isValidIdentifier(identifierContext[0])) {
      throwSyntaxError("Invalid identifier: " + identifierContext[0] + " detected.");
    }

    final IExpr symbol =
        convertSymbolOnInput(identifierContext[0], identifierContext[1], convertOnInput);
    getNextToken();
    return symbol;
  }

  /**
   * Precondition <code>fToken == TT_PRECEDENCE_OPEN</code>
   * 
   * @param temp
   * @return
   * @throws SyntaxError
   */
  private IExpr getTimesImplicit(IExpr temp) throws SyntaxError {
    do {
      temp = parseExpression(temp, Precedence.TIMES);// parseExpression();
      // parseExpression() has already called getNextToken() here:
    } while (fToken == TT_PRECEDENCE_OPEN);
    return temp;
  }

  /**
   * Test if the current expression shouldn't be evaluated on input
   *
   * @return <code>true</code> if the current expression shouldn't be evaluated on input
   */
  public boolean isHoldOrHoldFormOrDefer() {
    return fHoldExpression;
  }

  /**
   * Parse the given <code>expression</code> String into an IExpr.
   *
   * @param expression a formula string which should be parsed.
   * @return the parsed IExpr representation of the given formula string
   * @throws SyntaxError
   */
  public IExpr parse(final String expression) throws SyntaxError {
    initialize(expression);
    if (fToken == TT_EOF) {
      // empty expression string or only a comment available in the string
      return S.Null;
    }
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
    boolean localHoldExpression = fHoldExpression;
    try {
      if (head.isHoldOrHoldFormOrDefer()) {
        fHoldExpression = true;
      }
      if (fRelaxedSyntax) {
        if (fToken == TT_ARGUMENTS_OPEN) {
          if (ParserConfig.PARSER_USE_STRICT_SYNTAX) {
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
      } else {
        if (fToken == TT_ARGUMENTS_OPEN) {
          IASTMutable ast = getFunctionArguments(head);
          return convert(ast);
        }
      }
      return head;
    } finally {
      fHoldExpression = localHoldExpression;
    }
  }

  private IExpr parseCompoundExpressionNull(InfixExprOperator infixOperator, IExpr lhs) {
    if (infixOperator.isOperator(";")) {
      if (fToken == TT_EOF || fToken == TT_ARGUMENTS_CLOSE || fToken == TT_LIST_CLOSE
          || fToken == TT_PRECEDENCE_CLOSE || fToken == TT_COMMA) {
        return createInfixFunction(infixOperator, lhs, S.Null);
      }
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
        if (fToken == TT_COMMA || fToken == TT_PARTCLOSE || fToken == TT_ARGUMENTS_CLOSE
            || fToken == TT_PRECEDENCE_CLOSE) {
          return span;
        }
      } else if (fToken == TT_COMMA || fToken == TT_PARTCLOSE || fToken == TT_ARGUMENTS_CLOSE
          || fToken == TT_PRECEDENCE_CLOSE) {
        span.append(S.All);
        return span;
      } else if (fToken == TT_OPERATOR) {
        InfixExprOperator infixOperator = determineBinaryOperator();
        if (infixOperator != null && //
            infixOperator.getOperatorString().equals(";")) {
          span.append(S.All);
          getNextToken();
          IExpr compoundExpressionNull = parseCompoundExpressionNull(infixOperator, span);
          if (compoundExpressionNull != null) {
            return compoundExpressionNull;
          }
          while (fToken == TT_NEWLINE) {
            getNextToken();
          }
          return parseInfixOperator(span, infixOperator);
        }
      }
      span.append(parseExpression());
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
        if (fToken == TT_COMMA || fToken == TT_PARTCLOSE || fToken == TT_ARGUMENTS_CLOSE
            || fToken == TT_PRECEDENCE_CLOSE) {
          return span;
        } else if (fToken == TT_OPERATOR) {
          return parseExpression(F.Times(span, F.Span(F.C1, S.All)), 0);
        }
      } else if (fToken == TT_COMMA || fToken == TT_PARTCLOSE || fToken == TT_ARGUMENTS_CLOSE
          || fToken == TT_PRECEDENCE_CLOSE) {
        span.append(S.All);
        return span;
      } else if (fToken == TT_OPERATOR) {
        InfixExprOperator infixOperator = determineBinaryOperator();
        if (infixOperator != null && //
            infixOperator.getOperatorString().equals(";")) {
          span.append(S.All);
          getNextToken();
          IExpr compoundExpressionNull = parseCompoundExpressionNull(infixOperator, span);
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
        span.append(S.All);
        getNextToken();
      } else {
        span.append(parseExpression(parsePrimary(0), 0));
      }
      if (fToken == TT_SPAN) {
        getNextToken();
        if (fToken == TT_COMMA || fToken == TT_PARTCLOSE || fToken == TT_ARGUMENTS_CLOSE
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
    // Operator oper;
    InfixExprOperator infixOperator;
    PostfixExprOperator postfixOperator;
    while (true) {
      if (fToken == TT_NEWLINE) {
        return lhs;
      }
      if ((fToken == TT_LIST_OPEN) || (fToken == TT_PRECEDENCE_OPEN)
          || (fToken == TT_ASSOCIATION_OPEN) || (fToken == TT_IDENTIFIER) || (fToken == TT_STRING)
          || (fToken == TT_DIGIT) || (fToken == TT_SLOT) || (fToken == TT_SLOTSEQUENCE)) {
        // if (fPackageMode && fRecursionDepth < 1) {
        // return lhs;
        // }
        // if (fPackageMode && fToken == TT_IDENTIFIER && fLastChar ==
        // '\n') {
        // return lhs;
        // }

        if (!fExplicitTimes) {
          // lazy evaluation of multiplication
          // oper = fFactory.get("Times");
          if (ParserConfig.DOMINANT_IMPLICIT_TIMES || Precedence.TIMES >= min_precedence) {
            rhs = parseLookaheadOperator(Precedence.TIMES);
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

  private final IExpr parseInfixOperator(IExpr lhs, InfixExprOperator infixOperator) {
    IExpr rhs;
    rhs = parseLookaheadOperator(infixOperator.getPrecedence());
    lhs = createInfixFunction(infixOperator, lhs, rhs);
    if (lhs instanceof IASTAppendable) {
      IASTAppendable ast = (IASTAppendable) lhs;
      int headID = ast.headID();
      if ((headID >= ID.Equal && headID <= ID.Unequal) && //
          (headID == ID.Equal || headID == ID.Greater || headID == ID.GreaterEqual
              || headID == ID.Less || headID == ID.LessEqual || headID == ID.Unequal)) {
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
          ast.append(rhs);
        }
        return ast;
      }
      while (fToken == TT_OPERATOR && infixOperator.getGrouping() == InfixOperator.NONE
          && infixOperator.isOperator(fOperatorString)) {
        getNextToken();
        if (infixOperator.isOperator(";")) {
          if (fToken == TT_EOF || fToken == TT_ARGUMENTS_CLOSE || fToken == TT_LIST_CLOSE
              || fToken == TT_PRECEDENCE_CLOSE || fToken == TT_COMMA) {
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

      return infixOperator.endFunction(fFactory, ast, this);
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
  private IExpr parseInequality(final IAST ast, final InfixExprOperator infixOperator) {
    // rewrite to Inequality
    IBuiltInSymbol head = (IBuiltInSymbol) ast.head();
    IASTAppendable result = F.ast(S.Inequality, ast.size() + 8);
    ast.forEach(x -> {
      result.append(x);
      result.append(head);
    });
    InfixExprOperator compareOperator = determineBinaryOperator();
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

  private final IExpr parsePostfixOperator(IExpr lhs, PostfixExprOperator postfixOperator) {
    getNextToken();
    lhs = convert(postfixOperator.createFunction(fFactory, lhs));
    lhs = parseArguments(lhs);
    if (fToken == TT_ARGUMENTS_OPEN) {
      return getFunctionArguments(lhs);
    }
    return lhs;
  }

  private IExpr parseLookaheadOperator(final int min_precedence) {
    IExpr rhs = parsePrimary(min_precedence);

    while (true) {
      final int lookahead = fToken;
      if (fToken == TT_NEWLINE) {
        break;
      }
      if ((fToken == TT_LIST_OPEN) || (fToken == TT_PRECEDENCE_OPEN)
          || (fToken == TT_ASSOCIATION_OPEN) || (fToken == TT_IDENTIFIER) || (fToken == TT_STRING)
          || (fToken == TT_DIGIT) || (fToken == TT_SLOT)) {
        if (!fExplicitTimes) {
          // lazy evaluation of multiplication
          InfixExprOperator timesOperator = (InfixExprOperator) fFactory.get("Times");
          if (ParserConfig.DOMINANT_IMPLICIT_TIMES
              || timesOperator.getPrecedence() > min_precedence) {
            rhs = parseExpression(rhs, timesOperator.getPrecedence());
            continue;
          } else if ((timesOperator.getPrecedence() == min_precedence)
              && (timesOperator.getGrouping() == InfixExprOperator.RIGHT_ASSOCIATIVE)) {
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
        InfixExprOperator infixOperator = determineBinaryOperator();
        if (infixOperator != null) {
          if (infixOperator.getPrecedence() > min_precedence
              || (fOperatorString.equals(":") && rhs.isSymbol())
              || ((infixOperator.getPrecedence() == min_precedence)
                  && (infixOperator.getGrouping() == InfixExprOperator.RIGHT_ASSOCIATIVE))) {
            rhs = parseExpression(rhs, infixOperator.getPrecedence());
            continue;
          }

        } else {
          PostfixExprOperator postfixOperator = determinePostfixOperator();
          if (postfixOperator != null) {
            if (postfixOperator.getPrecedence() >= min_precedence) {
              getNextToken();
              rhs = convert(postfixOperator.createFunction(fFactory, rhs));
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
      }
      temp = parseExpression();
      fEngine.evaluate(temp);
    }

  }

  private IExpr parsePrimary(final int min_precedence) {
    if (fToken == TT_OPERATOR) {
      if (fOperatorString.equals(".")) {
        fCurrentChar = '.';
        return getNumber(false);
      }
      final PrefixExprOperator prefixOperator = determinePrefixOperator();
      if (prefixOperator != null) {
        return parsePrefixOperator(prefixOperator);
      }
      throwSyntaxError("Operator: " + fOperatorString + " is no prefix operator.");
    }
    return getPart(min_precedence);
  }

  private final IExpr parsePrefixOperator(final PrefixExprOperator prefixOperator) {
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
