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
package org.matheclipse.core.parser;

import java.util.List;
import java.util.Locale;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.NumStr;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.EvalFlags.Flag;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
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
    F.initSymja();
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

  /**
   * Check if the given string is valid Symja syntax for the given engine. This test is used for
   * {@link S#SyntaxQ}. In the case of an parser error, the method returns <code>false</code> and
   * doesn't print any syntax error message.
   * 
   * @param str
   * @param engine
   * @return <code>true</code> if the string is valid Symja syntax
   */
  public static boolean isSyntax(final String str, EvalEngine engine) {
    try {
      ExprParser fParser = new ExprParser(engine);
      final IExpr parsedExpression = fParser.parse(str);
      if (parsedExpression != null) {
        return true;
      }
    } catch (final SyntaxError e) {
      // syntax errors give false for SyntaxQ
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
      boolean scriptMode, boolean explicitTimes) {
    super(scriptMode, explicitTimes);
    this.fRelaxedSyntax = relaxedSyntax;
    this.fFactory = factory;
    this.fEngine = engine;
  }

  private IExpr convert(IAST ast) {
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
          if (!Config.USER_STEPS_PARSER) {
            if (ast.isAST1()) {
              // rewrite from input: Sqrt(x) => Power(x, 1/2)
              return F.Power(ast.getUnevaluated(1), F.C1D2);
            }
          }
          break;

        case ID.Power:
          if (!Config.USER_STEPS_PARSER) {
            if (ast.isPower() && ast.base().isPower() && ast.exponent().isMinusOne()) {
              IAST arg1Power = (IAST) ast.base();
              if (arg1Power.exponent().isNumber()) {
                // Division operator
                // rewrite from input: Power(Power(x, <number>),-1) => Power(x,
                // - <number>)
                return F.Power(arg1Power.getUnevaluated(1), arg1Power.getUnevaluated(2).negate());
              }
            }
          }
          break;

        case ID.Blank:
          expr = S.Blank.getEvaluator().evaluate(ast, fEngine);
          break;
        case ID.BlankSequence:
          expr = S.BlankSequence.getEvaluator().evaluate(ast, fEngine);
          break;
        case ID.BlankNullSequence:
          expr = S.BlankNullSequence.getEvaluator().evaluate(ast, fEngine);
          break;
        case ID.Pattern:
          expr = S.Pattern.getEvaluator().evaluate(ast, fEngine);
          break;
        case ID.Optional:
          expr = S.Optional.getEvaluator().evaluate(ast, fEngine);
          break;
        // case ID.OptionsPattern:
        // expr = PatternMatching.OptionsPattern.CONST.evaluate(ast, fEngine);
        // break;
        case ID.Repeated:
          expr = S.Repeated.getEvaluator().evaluate(ast, fEngine);
          break;
        case ID.Complex:
          if (!Config.USER_STEPS_PARSER) {
            expr = S.Complex.getEvaluator().evaluate(ast, fEngine);
          }
          break;

        case ID.Rational:
          if (!Config.USER_STEPS_PARSER) {
            expr = S.Rational.getEvaluator().evaluate(ast, fEngine);
          }
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
   * The infix, prefix and postfix reading of the operator token the scanner is currently on, or
   * <code>null</code> where the token has no reading of that kind.
   *
   * <p>
   * A token can stand for more than one operator - <code>+</code> is Plus or PrePlus,
   * <code>-</code> is Subtract or PreMinus, <code>!</code> is Factorial or Not - so the parser has
   * to pick the reading that fits the position. It used to pick by walking {@link #fOperList} and
   * testing each entry with <code>instanceof</code>, once per question asked, and the questions are
   * asked up to three times for a single token. The walk now happens once, in
   * {@link #classifyOperators(List)}, when the scanner produces the token.
   */
  private InfixExprOperator fInfixOperator;

  private PrefixExprOperator fPrefixOperator;

  private PostfixExprOperator fPostfixOperator;

  /**
   * Split the operators a token can stand for into the three positions they can be read in. Called
   * once per operator token, from {@link #getOperator()}.
   */
  private void classifyOperators(final List<Operator> operators) {
    fInfixOperator = null;
    fPrefixOperator = null;
    fPostfixOperator = null;
    for (int i = 0; i < operators.size(); i++) {
      Operator oper = operators.get(i);
      if (oper instanceof InfixExprOperator) {
        if (fInfixOperator == null) {
          fInfixOperator = (InfixExprOperator) oper;
        }
      } else if (oper instanceof PrefixExprOperator) {
        if (fPrefixOperator == null) {
          fPrefixOperator = (PrefixExprOperator) oper;
        }
      } else if (oper instanceof PostfixExprOperator) {
        if (fPostfixOperator == null) {
          fPostfixOperator = (PostfixExprOperator) oper;
        }
      }
    }
  }

  /**
   * The infix reading of the current operator token.
   *
   * @return <code>null</code> if the token cannot be read as an infix operator
   */
  private InfixExprOperator determineBinaryOperator() {
    return fInfixOperator;
  }

  /**
   * The postfix reading of the current operator token.
   *
   * @return <code>null</code> if the token cannot be read as a postfix operator
   */
  private PostfixExprOperator determinePostfixOperator() {
    return fPostfixOperator;
  }

  /**
   * The prefix reading of the current operator token.
   *
   * @return <code>null</code> if the token cannot be read as a prefix operator
   */
  private PrefixExprOperator determinePrefixOperator() {
    return fPrefixOperator;
  }

  /**
   * Whether the operator token the scanner is on is a comparison operator.
   *
   * <p>
   * Asks the operator, not the token text. A unicode spelling is registered as a second token for
   * the very same operator instance, so testing the text made the chaining loops skip
   * <code>a \u2264 b \u2264 c</code> while running for <code>a &lt;= b &lt;= c</code> - the first
   * nested, the second flattened, for what is treated as one expression.
   */
  private boolean isComparatorToken() {
    return fInfixOperator != null && fInfixOperator.isComparator();
  }


  /** construct the arguments for an expression */
  private void getArguments(final IASTAppendable function) throws SyntaxError {
    do {
      if (fToken == TT_COMMA) {
        function.append(S.Null);
      } else {
        function.append(parseExpression());
      }

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
          scanIdentifier();
          final IAST slot = F.Slot(fIdentifier);
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
  IAST getFunction(final IExpr head) throws SyntaxError {

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
    if (head.isBuiltInSymbolID()) {
      IFunctionEvaluator eval = ((IBuiltInSymbol) head).getEvaluator();
      int[] args = eval.expectedArgSize(F.NIL);
      if (args != null && args[1] < 10) {
        defaultSize = args[1] + 1;
      }
    }
    return defaultSize;
  }

  private static IAST reduceAST(IASTMutable function) {
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
  IAST getFunctionArguments(final IExpr head) throws SyntaxError {

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
    scanNumber();
    String numberStr = fNumberString;
    int numFormat = fNumberFormat;
    String exponentStr = fNumberExponent;
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
            String precisionStr = getJavaDoubleString();
            double doublePrecision = 0;
            try {
              doublePrecision = Double.parseDouble(precisionStr);
            } catch (final NumberFormatException e) {
              throwSyntaxError("Number format error (not a double type): " + precisionStr,
                  precisionStr.length());
            }
            if (doublePrecision < ParserConfig.MACHINE_PRECISION) {
              doublePrecision = ParserConfig.MACHINE_PRECISION_DOUBLE;
              precisionStr = "" + ParserConfig.MACHINE_PRECISION;
            }
            return F.num(numberStr, doublePrecision);
            // long precision = getJavaLong();
            // if (precision < ParserConfig.MACHINE_PRECISION) {
            // precision = ParserConfig.MACHINE_PRECISION;
            // }
            // return F.num(numberStr, precision);
          } else {
            if (isValidPosition() && Character.isDigit(fInputString[fCurrentPosition])) {
              fCurrentPosition++;
              // Without the token, so that a *^ exponent following the precision can still be
              // seen: getJavaDoubleString() would read past the '*' and leave the '^' standing
              // alone, which is why 1.5`20*^3 answered "Operator: ^ is no prefix operator" even
              // though 1.5*^3 parses. This is the form InputForm and FullForm print.
              String precisionStr = getJavaDoubleStringWithoutToken();
              double doublePrecision = 0;
              try {
                doublePrecision = Double.parseDouble(precisionStr);
              } catch (final NumberFormatException e) {
                throwSyntaxError("Number format error (not a double type): " + precisionStr,
                    precisionStr.length());
              }
              if (doublePrecision < ParserConfig.MACHINE_PRECISION) {
                doublePrecision = ParserConfig.MACHINE_PRECISION_DOUBLE;
                precisionStr = "" + ParserConfig.MACHINE_PRECISION;
              }
              String mantissaStr = numberStr;
              if (isValidPosition() && fInputString[fCurrentPosition] == '*'
                  && fCurrentPosition + 1 < fInputString.length
                  && fInputString[fCurrentPosition + 1] == '^') {
                int beforeExponent = fCurrentPosition;
                fCurrentPosition += 2;
                int exponentStart = fCurrentPosition;
                if (isValidPosition() && (fInputString[fCurrentPosition] == '+'
                    || fInputString[fCurrentPosition] == '-')) {
                  fCurrentPosition++;
                }
                int digits = 0;
                while (isValidPosition() && Character.isDigit(fInputString[fCurrentPosition])) {
                  fCurrentPosition++;
                  digits++;
                }
                if (digits > 0) {
                  mantissaStr = numberStr + "E"
                      + new String(fInputString, exponentStart, fCurrentPosition - exponentStart);
                } else {
                  // a '*' that begins something else, e.g. 1.5`20*x - leave it to the parser
                  fCurrentPosition = beforeExponent;
                }
              }
              getNextToken();
              return F.num(mantissaStr, doublePrecision);
              // long precision = getJavaLong();
              // if (precision < ParserConfig.MACHINE_PRECISION) {
              // precision = ParserConfig.MACHINE_PRECISION;
              // }
              // return F.num(numberStr, precision);
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
    // Longest match wins: probe every prefix of the token through a view over the input rather
    // than building a String for each one.
    fOperatorWindow.set(startPosition, fCurrentPosition - startPosition);
    List<Operator> list = fFactory.getOperatorList(fOperatorWindow);
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
      fOperatorWindow.set(startPosition, fCurrentPosition - startPosition);
      list = fFactory.getOperatorList(fOperatorWindow);
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
      // Built from the prefix that actually matched. Previously fOperatorString was left holding
      // whichever prefix was probed last, which is a longer, unmatched one whenever the scan
      // overshot - so for an input like "a&&&b" the parser went on to compare "&&&" against the
      // operator it had really selected for "&&".
      fOperatorString =
          new String(fInputString, startPosition, lastOperatorPosition - startPosition);
      classifyOperators(lastList);
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
    scanIdentifier();
    if (!fFactory.isValidIdentifier(fIdentifier)) {
      throwSyntaxError("Invalid identifier: " + fIdentifier + " detected.");
    }

    final IExpr symbol = convertSymbolOnInput(fIdentifier, fIdentifierContext, convertOnInput);
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
    fEngine.setDeterminePrecision(temp, true);
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
          IAST ast = getFunctionArguments(head);
          return convert(ast);
        } else if (fToken == TT_PRECEDENCE_OPEN) {
          IAST ast = getFunction(head);
          return convert(ast);
        }
      } else {
        if (fToken == TT_ARGUMENTS_OPEN) {
          IAST ast = getFunctionArguments(head);
          return convert(ast);
        }
      }
      return head;
    } finally {
      fHoldExpression = localHoldExpression;
    }
  }

  private IExpr parseCompoundExpressionNull(InfixExprOperator infixOperator, IExpr lhs) {
    if (infixOperator.headSymbol() == S.CompoundExpression) {
      if (fToken == TT_EOF || fToken == TT_ARGUMENTS_CLOSE || fToken == TT_LIST_CLOSE
          || fToken == TT_PRECEDENCE_CLOSE || fToken == TT_COMMA) {
        return createInfixFunction(infixOperator, lhs, S.Null);
      }
      if (fScriptMode && fRecursionDepth < 1 && fToken == TT_NEWLINE) {
        // A line of a script which ends in `;` ends there: what comes after the newline is the
        // next expression, not the right hand side of this one. Without this the whole script
        // collapses into one CompoundExpression, and an expression which changes the context
        // cannot be evaluated before the expressions after it are parsed.
        //
        // The newline is what ends it, not the `;` on its own:
        // {@link org.matheclipse.parser.client.Parser} ends the expression at every `;` written
        // outside brackets, so it reads `a = 1; b = 2` on one line as two expressions. That is not
        // what the Wolfram Language does with it, and for input typed by hand - where writing
        // several short statements on one line is ordinary - it would put each of them in the
        // output history separately.
        return createInfixFunction(infixOperator, lhs, S.Null);
      }
    }
    return null;
  }

  /**
   * The tokens which end a {@code Span} where a further part could have stood - a comma or a
   * closing bracket. {@code a[[2;;]]} and {@code {1,2;;3}} both reach a Span this way.
   */
  private boolean isSpanEnd() {
    return fToken == TT_COMMA || fToken == TT_PARTCLOSE || fToken == TT_ARGUMENTS_CLOSE
        || fToken == TT_PRECEDENCE_CLOSE;
  }

  /**
   * Finish a {@code Span} whose next token is the {@code ;} operator, as in {@code a[[1;;]]; rest}
   * - the Span takes {@code All} as its missing part and becomes the first argument of the
   * CompoundExpression.
   *
   * @return <code>null</code> if the current token is not {@code ;}, in which case the caller
   *         carries on parsing the Span's next part
   */
  private IExpr parseSpanCompoundExpression(IASTAppendable span) {
    InfixExprOperator infixOperator = determineBinaryOperator();
    if (infixOperator == null || !infixOperator.getOperatorString().equals(";")) {
      return null;
    }
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

  /**
   * Parse a {@code Span} which begins with {@code ;;}, so that its first part is implicitly
   * {@code 1} - {@code ;;3} is {@code Span(1, 3)}.
   */
  private IExpr parseSpanWithoutFirstPart() {
    IASTAppendable span = F.ast(S.Span);
    span.append(F.C1);
    getNextToken();
    if (fToken == TT_SPAN) {
      span.append(S.All);
      getNextToken();
      if (isSpanEnd()) {
        return span;
      }
    } else if (isSpanEnd()) {
      span.append(S.All);
      return span;
    } else if (fToken == TT_OPERATOR) {
      IExpr compoundExpression = parseSpanCompoundExpression(span);
      if (compoundExpression != null) {
        return compoundExpression;
      }
    }
    span.append(parseExpression());
    return span;
  }

  /**
   * Parse the rest of a {@code Span} whose first part has already been read - {@code 1;;3} arrives
   * here with {@code 1} in hand and the scanner on {@code ;;}.
   */
  private IExpr parseSpanAfterFirstPart(IExpr firstPart) {
    IASTAppendable span = F.ast(S.Span);
    span.append(firstPart);
    getNextToken();
    if (fToken == TT_SPAN) {
      span.append(S.All);
      getNextToken();
      if (isSpanEnd()) {
        return span;
      } else if (fToken == TT_OPERATOR) {
        return parseExpression(F.Times(span, F.Span(F.C1, S.All)), 0);
      }
    } else if (isSpanEnd()) {
      span.append(S.All);
      return span;
    } else if (fToken == TT_OPERATOR) {
      IExpr compoundExpression = parseSpanCompoundExpression(span);
      if (compoundExpression != null) {
        return compoundExpression;
      }
    }
    if (fToken == TT_NEWLINE || fToken == TT_EOF) {
      span.append(S.All);
      getNextToken();
    } else {
      span.append(parseExpression(parsePrimary(0), 0));
    }
    if (fToken == TT_SPAN) {
      // the step, as in a[[1;;10;;2]]
      getNextToken();
      if (isSpanEnd()) {
        return span;
      }
      span.append(parseExpression(parsePrimary(0), 0));
    }
    return span;
  }

  protected IExpr parseExpression() {
    if (fToken == TT_SPAN) {
      return parseSpanWithoutFirstPart();
    }
    IExpr temp = parseExpression(parsePrimary(0), 0);
    if (fToken == TT_SPAN) {
      return parseSpanAfterFirstPart(temp);
    }
    return temp;
  }

  /**
   * Whether the current token could begin an operand, and so stands next to the expression already
   * parsed rather than combining with it - which is what makes {@code 2 x} a product.
   *
   * <p>
   * {@code ##} ({@code TT_SLOTSEQUENCE}) is included. The two climbing loops used to disagree about
   * it - only {@link #parseExpression(IExpr, int)} counted it - so {@code a ##} was an implicit
   * product while the {@code a + b ##} which reaches {@link #parseLookaheadOperator(int)} was not.
   */
  private boolean isOperandStart() {
    return fToken == TT_LIST_OPEN || fToken == TT_PRECEDENCE_OPEN || fToken == TT_ASSOCIATION_OPEN
        || fToken == TT_IDENTIFIER || fToken == TT_STRING || fToken == TT_DIGIT || fToken == TT_SLOT
        || fToken == TT_SLOTSEQUENCE;
  }

  /**
   * Climb operators while they bind at least as tightly as <code>min_precedence</code>, folding
   * each into <code>lhs</code>.
   *
   * <p>
   * This and {@link #parseLookaheadOperator(int)} are the two halves of one precedence-climbing
   * parser: this one is the "loop while the operator binds at least this tightly" half, the other
   * is the "read an operand, then recurse while the operator binds more tightly" half, and they
   * call each other. Merging them into a single loop over an explicit operand/operator stack is the
   * point of the shunting-yard rewrite; what follows is where they actually differ, which is the
   * specification that merge has to satisfy.
   *
   * <table border="1">
   * <caption>differences between the two halves</caption>
   * <tr>
   * <th></th>
   * <th>parseExpression(IExpr,int)</th>
   * <th>parseLookaheadOperator(int)</th>
   * </tr>
   * <tr>
   * <td>entry</td>
   * <td>takes an already-parsed lhs</td>
   * <td>calls parsePrimary first</td>
   * </tr>
   * <tr>
   * <td>newline</td>
   * <td>returns lhs</td>
   * <td>breaks, then still checks for a trailing <code>[</code></td>
   * </tr>
   * <tr>
   * <td>operand start</td>
   * <td>counts <code>##</code></td>
   * <td>does not</td>
   * </tr>
   * <tr>
   * <td>implicit times</td>
   * <td><code>TIMES &gt;= min</code>; builds <code>Times(lhs,rhs)</code> and sets
   * TIMES_PARSED_IMPLICIT</td>
   * <td><code>TIMES &gt; min</code>; recurses and lets the other half build the Times</td>
   * </tr>
   * <tr>
   * <td>leaving the loop</td>
   * <td><code>fToken != TT_OPERATOR</code></td>
   * <td>also accepts a token which <em>was</em> an operator before a Derivative was folded in</td>
   * </tr>
   * <tr>
   * <td>infix accepted when</td>
   * <td><code>prec &gt;= min</code></td>
   * <td><code>prec &gt;
   * min</code>, or <code>:</code> after a symbol, or <code>prec == min</code> and
   * right-associative</td>
   * </tr>
   * <tr>
   * <td>infix action</td>
   * <td>checks for a trailing <code>;</code>, skips newlines, then parseInfixOperator - which is
   * where flat chains and the Inequality rewrite happen</td>
   * <td>recurses; none of that</td>
   * </tr>
   * <tr>
   * <td>postfix action</td>
   * <td>parsePostfixOperator, which also handles arguments and a following <code>[</code></td>
   * <td>inline, and does neither</td>
   * </tr>
   * </table>
   *
   * <p>
   * The last row looks like a latent bug rather than a deliberate difference, and the {@code ##}
   * row likewise; see {@link #isOperandStart()}. Neither is changed here, because changing them is
   * a language change and not a refactoring.
   *
   * @param lhs the already parsed left-hand-side of the operator
   * @param min_precedence the loosest operator this call may fold in
   */
  private IExpr parseExpression(IExpr lhs, final int min_precedence) {
    return climbOperators(lhs, min_precedence, true);
  }

  /**
   * The one operator-climbing loop, shared by both halves of the parser.
   *
   * <p>
   * <code>foldEqualPrecedence</code> is what used to distinguish them. Reading a left-hand side and
   * continuing from it folds an operator of exactly {@code min_precedence} into what is already
   * there; looking ahead for a right-hand side must leave such an operator for the caller, or the
   * two would both claim it and left-associative operators would come out right-associative. The
   * remaining difference - how an accepted operator is applied - follows from that same distinction
   * and is spelled out below.
   *
   * <p>
   * The lookahead half also used to keep a <code>lookahead</code> copy of the token from the top of
   * the iteration and carry on when <em>either</em> it or the current token was an operator. That
   * condition could never differ from testing the current token alone: the only thing which moves
   * the scanner between the two reads is a derivative, and that branch is reached only when the
   * token is {@code TT_DERIVATIVE}, which is not {@code TT_OPERATOR}. The variable is gone.
   */
  private IExpr climbOperators(IExpr lhs, final int min_precedence,
      final boolean foldEqualPrecedence) {
    while (true) {
      if (fToken == TT_NEWLINE) {
        return lhs;
      }
      if (isOperandStart()) {
        if (fExplicitTimes) {
          break;
        }
        // Juxtaposition is a product. The lookahead half stops at an equal precedence here too.
        if (ParserConfig.DOMINANT_IMPLICIT_TIMES //
            || (foldEqualPrecedence ? Precedence.TIMES >= min_precedence
                : Precedence.TIMES > min_precedence)) {
          if (foldEqualPrecedence) {
            lhs = F.$(S.Times, lhs, parseLookaheadOperator(Precedence.TIMES));
            ((IAST) lhs).addFlag(Flag.TIMES_PARSED_IMPLICIT);
          } else {
            lhs = climbOperators(lhs, Precedence.TIMES, true);
          }
          continue;
        }
        break;
      }
      if (fToken == TT_DERIVATIVE) {
        lhs = parseDerivative(lhs);
      }
      if (fToken != TT_OPERATOR) {
        break;
      }
      final InfixExprOperator infixOperator = determineBinaryOperator();
      if (infixOperator != null) {
        final int precedence = infixOperator.getPrecedence();
        final boolean accept = foldEqualPrecedence //
            ? precedence >= min_precedence
            : precedence > min_precedence //
                || (fOperatorString.equals(":") && lhs.isSymbol()) || (precedence == min_precedence
                    && infixOperator.getGrouping() == InfixExprOperator.RIGHT_ASSOCIATIVE);
        if (!accept) {
          break;
        }
        if (!foldEqualPrecedence) {
          // Hand the operator to a fold, which reads its right-hand side and builds the node.
          lhs = climbOperators(lhs, precedence, true);
          continue;
        }
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
      final PostfixExprOperator postfixOperator = determinePostfixOperator();
      if (postfixOperator != null && postfixOperator.getPrecedence() >= min_precedence) {
        lhs = parsePostfixOperator(lhs, postfixOperator);
        continue;
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
            && isComparatorToken()) {
          if (infixOperator != fInfixOperator) {
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
          && infixOperator == fInfixOperator) {
        getNextToken();
        if (infixOperator.headSymbol() == S.CompoundExpression) {
          if (fToken == TT_EOF || fToken == TT_ARGUMENTS_CLOSE || fToken == TT_LIST_CLOSE
              || fToken == TT_PRECEDENCE_CLOSE || fToken == TT_COMMA) {
            ast.append(S.Null);
            break;
          }
          if (fScriptMode && fRecursionDepth < 1 && fToken == TT_NEWLINE) {
            // the `;` which ends a line ends the expression, the same way it does for the first
            // one in {@link #parseCompoundExpressionNull}. This is the second and later `;` of a
            // line such as `a = 1; b = 2;`, which this loop collects into one flat node.
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
          && infixOperator == fInfixOperator) {
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
    result.set(result.argSize(), compareOperator.headSymbol());
    getNextToken();
    while (fToken == TT_NEWLINE) {
      getNextToken();
    }
    int precedence = infixOperator.getPrecedence();
    result.append(parseLookaheadOperator(precedence));

    while (fToken == TT_OPERATOR && isComparatorToken()) {
      compareOperator = determineBinaryOperator();
      result.append(compareOperator.headSymbol());
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

  /**
   * Read one operand and then climb operators which bind more tightly than
   * <code>min_precedence</code>. The other half of the parser described on
   * {@link #parseExpression(IExpr, int)}, where the differences between the two are tabulated.
   */
  private IExpr parseLookaheadOperator(final int min_precedence) {
    IExpr rhs = climbOperators(parsePrimary(min_precedence), min_precedence, false);
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

  /**
   * Start reading an input which holds several expressions, separated by newlines.
   *
   * <p>
   * Call {@link #nextScriptExpression()} until it returns {@link F#NIL}. The parser has to have
   * been created with <code>scriptMode</code>, or a newline is read as whitespace and the whole
   * input comes back as one expression.
   *
   * @param expression the input to read
   */
  public void beginScript(final String expression) throws SyntaxError {
    initialize(expression);
  }

  /**
   * The next expression of the input given to {@link #beginScript(String)}, or {@link F#NIL} once
   * the input is used up.
   *
   * <p>
   * One expression at a time rather than all of them at once, so that the caller can evaluate each
   * before the next is parsed. That is what lets a <code>Begin</code> take effect: this parser
   * resolves a name to a context while it parses, so an expression which changes the context has to
   * have been evaluated before the expressions after it are read.
   */
  public IExpr nextScriptExpression() throws SyntaxError {
    while (fToken == TT_NEWLINE) {
      getNextToken();
    }
    if (fToken == TT_EOF) {
      return F.NIL;
    }
    if (fToken == TT_PRECEDENCE_CLOSE) {
      throwSyntaxError("Too many closing ')'; End-of-file not reached.");
    }
    if (fToken == TT_LIST_CLOSE) {
      throwSyntaxError("Too many closing '}'; End-of-file not reached.");
    }
    if (fToken == TT_ARGUMENTS_CLOSE) {
      throwSyntaxError("Too many closing ']'; End-of-file not reached.");
    }
    return parseExpression();
  }

  /** Read and evaluate every expression of a script, in the order they are written. */
  public void parseScript(final String expression) throws SyntaxError {
    beginScript(expression);
    IExpr expr = nextScriptExpression();
    while (expr.isPresent()) {
      expr.eval(fEngine);
      expr = nextScriptExpression();
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
