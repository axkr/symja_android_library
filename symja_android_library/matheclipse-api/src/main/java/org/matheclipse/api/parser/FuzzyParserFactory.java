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
package org.matheclipse.api.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.parser.client.ParserConfig;
import org.matheclipse.parser.client.ast.IParserFactory;
import org.matheclipse.parser.client.operator.InfixOperator;
import org.matheclipse.parser.client.operator.Operator;
import org.matheclipse.parser.trie.Trie;
import org.matheclipse.parser.trie.TrieMatch;
import com.google.common.base.CharMatcher;

public class FuzzyParserFactory implements IParserFactory {
  /** The matcher for characters, which could form an operator */
  public static CharMatcher OPERATOR_MATCHER = null;

  /** The set of characters, which could form an operator */
  @Override
  public boolean isOperatorChar(char ch) {
    return OPERATOR_MATCHER.matches(ch);
  }

  private static class InformationOperator extends FuzzyPrefixExprOperator {
    public InformationOperator(final String oper, final String functionName, final int precedence) {
      super(oper, functionName, precedence);
    }

    @Override
    public IExpr createFunction(final IParserFactory factory, final IExpr argument) {
      if (fOperatorString.equals("?")) {
        return F.Information(argument, F.Rule(S.LongForm, S.False));
      }
      // ?? operator:
      return F.Information(argument);
    }
  }

  /** @@@ operator (not @@ operator) */
  private static class ApplyOperator extends FuzzyInfixExprOperator {
    public ApplyOperator(final String oper, final String functionName, final int precedence,
        final int grouping) {
      super(oper, functionName, precedence, grouping);
    }

    @Override
    public IASTMutable createFunction(final IParserFactory factory, FuzzyParser parser,
        final IExpr lhs, final IExpr rhs) {
      if (fOperatorString.equals("@")) {
        return F.unaryAST1(lhs, rhs);
      }
      if (fOperatorString.equals("@@")) {
        return F.Apply(lhs, rhs);
      }
      // case "@@@"
      return F.MapApply(lhs, rhs);
    }
  }

  private static class TagSetOperator extends FuzzyInfixExprOperator {
    public TagSetOperator(final String oper, final String functionName, final int precedence,
        final int grouping) {
      super(oper, functionName, precedence, grouping);
    }

    @Override
    public IASTMutable createFunction(final IParserFactory factory, FuzzyParser parser,
        final IExpr lhs, final IExpr rhs) {
      if (rhs.isAST()) {
        IAST r = (IAST) rhs;

        if (r.isAST(S.Set, 3)) {
          return F.TagSet(lhs, r.arg1(), r.arg2());
        } else if (r.isAST(S.SetDelayed, 3)) {
          return F.TagSetDelayed(lhs, r.arg1(), r.arg2());
        }
      }
      return F.binaryAST2(S.TagSet, lhs, rhs);
    }
  }

  private static class DivideExprOperator extends FuzzyInfixExprOperator {
    public DivideExprOperator(final String oper, final String functionName, final int precedence,
        final int grouping) {
      super(oper, functionName, precedence, grouping);
    }

    @Override
    public IASTMutable createFunction(final IParserFactory factory, FuzzyParser parser,
        final IExpr lhs, final IExpr rhs) {

      if (rhs.isInteger() && !rhs.isZero()) {
        if (lhs.isInteger()) {
          return (IASTMutable) F.Rational(lhs, rhs);
        }
        return F.Times(F.fraction(F.C1, (IInteger) rhs), lhs);
      }

      if (lhs.equals(F.C1)) {
        return (IASTMutable) F.Power(rhs, F.CN1);
      }
      if (rhs.isPower() && rhs.exponent().isNumber()) {
        return F.Times(lhs, F.Power(rhs.base(), rhs.exponent().negate()));
      }
      return F.Times(lhs, F.Power(rhs, F.CN1));
    }
  }

  private static class PreMinusExprOperator extends FuzzyPrefixExprOperator {

    public PreMinusExprOperator(final String oper, final String functionName,
        final int precedence) {
      super(oper, functionName, precedence);
    }

    @Override
    public IExpr createFunction(final IParserFactory factory, final IExpr argument) {
      return F.Times(F.CN1, argument);
    }
  }

  private static class PrePlusExprOperator extends FuzzyPrefixExprOperator {

    public PrePlusExprOperator(final String oper, final String functionName, final int precedence) {
      super(oper, functionName, precedence);
    }

    @Override
    public IExpr createFunction(final IParserFactory factory, final IExpr argument) {
      return argument;
    }
  }

  private static class SubtractExprOperator extends FuzzyInfixExprOperator {
    public SubtractExprOperator(final String oper, final String functionName, final int precedence,
        final int grouping) {
      super(oper, functionName, precedence, grouping);
    }

    @Override
    public IASTMutable createFunction(final IParserFactory factory, FuzzyParser parser,
        final IExpr lhs, final IExpr rhs) {
      if (rhs.isNumber()) {
        return (IASTMutable) F.Plus(lhs, rhs.negate());
      }
      if (rhs.isTimes() && rhs.first().isNumber()) {
        return (IASTMutable) F.Plus(lhs, ((IAST) rhs).setAtCopy(1, rhs.first().negate()));
      }
      return (IASTMutable) F.Plus(lhs, F.Times(F.CN1, rhs));
    }
  }

  public static final int EQUAL_PRECEDENCE = 290;

  public static final int PLUS_PRECEDENCE = 310;

  public static final int TIMES_PRECEDENCE = 400;

  public static final int DIVIDE_PRECEDENCE = 470;

  public static final int POWER_PRECEDENCE = 590;

  public static final int FACTORIAL_PRECEDENCE = 610;

  public static final int APPLY_PRECEDENCE = 620;

  public static final InformationOperator INFORMATION_SHORT = //
      new InformationOperator("?", "Information", 720);

  public static final InformationOperator INFORMATION_LONG = //
      new InformationOperator("??", "Information", 720);

  public static final FuzzyInfixExprOperator ALTERNATIVES_OPERATOR =
      new FuzzyInfixExprOperator("|", "Alternatives", 160, FuzzyInfixExprOperator.NONE);

  public static final FuzzyInfixExprOperator OR_OPERATOR =
      new FuzzyInfixExprOperator("||", "Or", 213, FuzzyInfixExprOperator.NONE);

  public static final FuzzyInfixExprOperator AND_OPERATOR =
      new FuzzyInfixExprOperator("&&", "And", 215, FuzzyInfixExprOperator.NONE);

  public static final FuzzyPostfixExprOperator FUNCTION_OPERATOR =
      new FuzzyPostfixExprOperator("&", "Function", 90);

  public static final ApplyOperator APPLY_HEAD_OPERATOR = //
      new ApplyOperator("@", "Apply", 640, FuzzyInfixExprOperator.RIGHT_ASSOCIATIVE);

  public static final ApplyOperator APPLY_OPERATOR = //
      new ApplyOperator("@@", "Apply", 620, FuzzyInfixExprOperator.RIGHT_ASSOCIATIVE);

  public static final ApplyOperator APPLY_LEVEL_OPERATOR = //
      new ApplyOperator("@@@", "MapApply", 620,
          FuzzyInfixExprOperator.RIGHT_ASSOCIATIVE);

  public static final FuzzyInfixExprOperator EQUAL_OPERATOR = //
      new FuzzyInfixExprOperator("==", "Equal", EQUAL_PRECEDENCE, FuzzyInfixExprOperator.NONE);

  public static final FuzzyInfixExprOperator NON_COMMUTATIVE_MULTIPLY_OPERATOR = //
      new FuzzyInfixExprOperator("**", "NonCommutativeMultiply", 510, FuzzyInfixExprOperator.NONE);

  public static final FuzzyInfixExprOperator POWER_OPERATOR = //
      new FuzzyInfixExprOperator("^", "Power", POWER_PRECEDENCE,
          FuzzyInfixExprOperator.RIGHT_ASSOCIATIVE);

  public static final FuzzyInfixExprOperator SET_OPERATOR = //
      new FuzzyInfixExprOperator("=", "Set", 40, FuzzyInfixExprOperator.RIGHT_ASSOCIATIVE);

  public static final FuzzyInfixExprOperator SET_DELAYED_OPERATOR =
      new FuzzyInfixExprOperator(":=", "SetDelayed", 40, FuzzyInfixExprOperator.RIGHT_ASSOCIATIVE);

  public static final TagSetOperator TAG_SET_OPERATOR = //
      new TagSetOperator("/:", "TagSet", 40, FuzzyInfixExprOperator.NONE);

  static final String[] HEADER_STRINGS = {"MessageName", "Information", "Information", "Get",
      "PatternTest", "MapAll", "TimesBy", "Plus", "UpSet", "CompoundExpression", "Apply", "Map",
      "Unset", "Apply", "Apply", "ReplaceRepeated", "Less", "And", "Divide", "Set", "Increment",
      "Factorial2", "LessEqual", "NonCommutativeMultiply", "Factorial", "Times", "Power", "Dot",
      "Not", "PreMinus", "SameQ", "RuleDelayed", "GreaterEqual", "Condition", "Colon", "//",
      "DivideBy", "Or", "Span", "Equal", "StringJoin", "Unequal", "Decrement", "SubtractFrom",
      "PrePlus", "RepeatedNull", "UnsameQ", "Rule", "UpSetDelayed", "PreIncrement", "Function",
      "Greater", "PreDecrement", "Subtract", "SetDelayed", "Alternatives", "AddTo", "Repeated",
      "ReplaceAll", "TagSet", "Composition", "StringExpression", "TwoWayRule", "TwoWayRule",
      "DirectedEdge", "UndirectedEdge", "CenterDot", "CircleDot"};

  static final String[] OPERATOR_STRINGS =
      {"::", "<<", "?", "??", "?", "//@", "*=", "+", "^=", ";", "@", "/@", "=.", "@@", "@@@", "//.",
          "<", "&&", "/", "=", "++", "!!", "<=", "**", "!", "*", "^", ".", "!", "-", "===", ":>",
          ">=", "/;", ":", "//", "/=", "||", ";;", "==", "<>", "!=", "--", "-=", "+", "...", "=!=",
          "->", "^:=", "++", "&", ">", "--", "-", ":=", "|", "+=", "..", "/.", "/:", "@*", "~~", //
          "<->", // TwoWayRule
          "\uF120", // TwoWayRule
          "\uF3D5", // DirectedEdge
          "\uF3D4", // UndirectedEdge
          "\u00B7", // CenterDot
          "\u2299" // CircleDot
      };
  private static Operator[] OPERATORS;

  public static final FuzzyParserFactory RELAXED_STYLE_FACTORY = new FuzzyParserFactory();

  /** */
  private static Trie<String, Operator> fOperatorMap;

  /** */
  private static Trie<String, ArrayList<Operator>> fOperatorTokenStartSet;

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {

      OPERATORS = new Operator[] {
          new FuzzyInfixExprOperator("::", "MessageName", 750, FuzzyInfixExprOperator.NONE),
          new FuzzyPrefixExprOperator("<<", "Get", 720), //
          INFORMATION_SHORT, //
          INFORMATION_LONG, //
          new FuzzyInfixExprOperator("?", "PatternTest", 680, FuzzyInfixExprOperator.NONE), //
          new FuzzyInfixExprOperator("//@", "MapAll", 620,
              FuzzyInfixExprOperator.RIGHT_ASSOCIATIVE), //
          new FuzzyInfixExprOperator("*=", "TimesBy", 100,
              FuzzyInfixExprOperator.RIGHT_ASSOCIATIVE), //
          new FuzzyInfixExprOperator("+", "Plus", PLUS_PRECEDENCE, FuzzyInfixExprOperator.NONE), //
          new FuzzyInfixExprOperator("^=", "UpSet", 40, FuzzyInfixExprOperator.RIGHT_ASSOCIATIVE), //
          new FuzzyInfixExprOperator(";", "CompoundExpression", 10, FuzzyInfixExprOperator.NONE), //
          APPLY_HEAD_OPERATOR, //
          new FuzzyInfixExprOperator("/@", "Map", 620, FuzzyInfixExprOperator.RIGHT_ASSOCIATIVE), //
          new FuzzyPostfixExprOperator("=.", "Unset", 670), //
          APPLY_OPERATOR, //
          APPLY_LEVEL_OPERATOR, //
          new FuzzyInfixExprOperator("//.", "ReplaceRepeated", 110,
              FuzzyInfixExprOperator.LEFT_ASSOCIATIVE), //
          new FuzzyInfixExprOperator("<", "Less", 290, FuzzyInfixExprOperator.NONE), //
          AND_OPERATOR, //
          new DivideExprOperator("/", "Divide", DIVIDE_PRECEDENCE,
              FuzzyInfixExprOperator.LEFT_ASSOCIATIVE), //
          Config.FUZZY_PARSER ? EQUAL_OPERATOR : SET_OPERATOR, //
          new FuzzyPostfixExprOperator("++", "Increment", 660), //
          new FuzzyPostfixExprOperator("!!", "Factorial2", 610), //
          new FuzzyInfixExprOperator("<=", "LessEqual", 290, FuzzyInfixExprOperator.NONE), //
          Config.FUZZY_PARSER ? POWER_OPERATOR : NON_COMMUTATIVE_MULTIPLY_OPERATOR, //
          new FuzzyPostfixExprOperator("!", "Factorial", FACTORIAL_PRECEDENCE), //
          new FuzzyInfixExprOperator("*", "Times", TIMES_PRECEDENCE, FuzzyInfixExprOperator.NONE), //
          POWER_OPERATOR, //
          new FuzzyInfixExprOperator(".", "Dot", 490, FuzzyInfixExprOperator.NONE), //
          new FuzzyPrefixExprOperator("!", "Not", 230), //
          new PreMinusExprOperator("-", "PreMinus", 485), //
          new FuzzyInfixExprOperator("===", "SameQ", 290, FuzzyInfixExprOperator.NONE), //
          new FuzzyInfixExprOperator(":>", "RuleDelayed", 120,
              FuzzyInfixExprOperator.RIGHT_ASSOCIATIVE), //
          new FuzzyInfixExprOperator(">=", "GreaterEqual", 290, FuzzyInfixExprOperator.NONE), //
          new FuzzyInfixExprOperator("/;", "Condition", 130,
              FuzzyInfixExprOperator.LEFT_ASSOCIATIVE), //
          new FuzzyInfixExprOperator(":", "Colon", 80, FuzzyInfixExprOperator.NONE), //
          new FuzzyInfixExprOperator("//", "//", 70, FuzzyInfixExprOperator.LEFT_ASSOCIATIVE), //
          new FuzzyInfixExprOperator("/=", "DivideBy", 100,
              FuzzyInfixExprOperator.RIGHT_ASSOCIATIVE), //
          OR_OPERATOR, //
          new FuzzyInfixExprOperator(";;", "Span", 305, FuzzyInfixExprOperator.NONE), //
          EQUAL_OPERATOR, //
          new FuzzyInfixExprOperator("<>", "StringJoin", 600, FuzzyInfixExprOperator.NONE), //
          new FuzzyInfixExprOperator("!=", "Unequal", 290, FuzzyInfixExprOperator.NONE), //
          new FuzzyPostfixExprOperator("--", "Decrement", 660), //
          new FuzzyInfixExprOperator("-=", "SubtractFrom", 100,
              FuzzyInfixExprOperator.RIGHT_ASSOCIATIVE), //
          new PrePlusExprOperator("+", "PrePlus", 670), //
          new FuzzyPostfixExprOperator("...", "RepeatedNull", 170), //
          new FuzzyInfixExprOperator("=!=", "UnsameQ", 290, FuzzyInfixExprOperator.NONE), //
          new FuzzyInfixExprOperator("->", "Rule", 120, FuzzyInfixExprOperator.RIGHT_ASSOCIATIVE), //
          new FuzzyInfixExprOperator("^:=", "UpSetDelayed", 40,
              FuzzyInfixExprOperator.RIGHT_ASSOCIATIVE), //
          new FuzzyPrefixExprOperator("++", "PreIncrement", 660), //
          Config.FUZZY_PARSER ? AND_OPERATOR : FUNCTION_OPERATOR, //
          new FuzzyInfixExprOperator(">", "Greater", 290, FuzzyInfixExprOperator.NONE), //
          new FuzzyPrefixExprOperator("--", "PreDecrement", 660), //
          new SubtractExprOperator("-", "Subtract", 310, FuzzyInfixExprOperator.LEFT_ASSOCIATIVE), //
          Config.FUZZY_PARSER ? EQUAL_OPERATOR : SET_DELAYED_OPERATOR, //
          Config.FUZZY_PARSER ? OR_OPERATOR : ALTERNATIVES_OPERATOR, //
          new FuzzyInfixExprOperator("+=", "AddTo", 100, FuzzyInfixExprOperator.RIGHT_ASSOCIATIVE), //
          new FuzzyPostfixExprOperator("..", "Repeated", 170), //
          new FuzzyInfixExprOperator("/.", "ReplaceAll", 110,
              FuzzyInfixExprOperator.LEFT_ASSOCIATIVE), //
          TAG_SET_OPERATOR, //
          new FuzzyInfixExprOperator("@*", "Composition", 625, InfixOperator.NONE),
          new FuzzyInfixExprOperator("~~", "StringExpression", 135, InfixOperator.NONE),
          new FuzzyInfixExprOperator("<->", "TwoWayRule", 125, InfixOperator.RIGHT_ASSOCIATIVE), //
          new FuzzyInfixExprOperator("\uF120", "TwoWayRule", 125, InfixOperator.RIGHT_ASSOCIATIVE), //
          new FuzzyInfixExprOperator("\uF3D5", "DirectedEdge", 120,
              InfixOperator.RIGHT_ASSOCIATIVE), //
          new FuzzyInfixExprOperator("\uF3D4", "UndirectedEdge", 120,
              InfixOperator.RIGHT_ASSOCIATIVE), //
          new FuzzyInfixExprOperator("\u00B7", "CenterDot", 410, FuzzyInfixExprOperator.NONE), //
          new FuzzyInfixExprOperator("\u2299", "CircleDot", 520, FuzzyInfixExprOperator.NONE) //
      };
      StringBuilder buf = new StringBuilder(BASIC_OPERATOR_CHARACTERS);

      fOperatorMap = ParserConfig.TRIE_STRING2OPERATOR_BUILDER.withMatch(TrieMatch.EXACT).build();
      fOperatorTokenStartSet =
          ParserConfig.TRIE_STRING2OPERATORLIST_BUILDER.withMatch(TrieMatch.EXACT).build();

      // if (fuzzyParser) {
      // for (int i = 0; i < HEADER_STRINGS.length; i++) {
      // if (OPERATOR_STRINGS[i] == "=") {
      // addOperator(fOperatorMap, fOperatorTokenStartSet, OPERATOR_STRINGS[i], "Equal",
      // EQUAL_OPERATOR);
      // } else {
      // addOperator(fOperatorMap, fOperatorTokenStartSet, OPERATOR_STRINGS[i], HEADER_STRINGS[i],
      // OPERATORS[i]);
      // }
      // String unicodeChar = org.matheclipse.parser.client.Characters.NamedCharactersMap
      // .get(HEADER_STRINGS[i]);
      // if (unicodeChar != null) {
      // addOperator(fOperatorMap, fOperatorTokenStartSet, unicodeChar, HEADER_STRINGS[i],
      // OPERATORS[i]);
      // buf.append(unicodeChar);
      // }
      // }
      // } else {
      for (int i = 0; i < HEADER_STRINGS.length; i++) {
        addOperator(fOperatorMap, fOperatorTokenStartSet, OPERATOR_STRINGS[i], HEADER_STRINGS[i],
            OPERATORS[i]);
        String unicodeChar =
            org.matheclipse.parser.client.Characters.NamedCharactersMap.get(HEADER_STRINGS[i]);
        if (unicodeChar != null) {
          addOperator(fOperatorMap, fOperatorTokenStartSet, unicodeChar, HEADER_STRINGS[i],
              OPERATORS[i]);
          buf.append(unicodeChar);
        }
      }
      // }
      OPERATOR_MATCHER = CharMatcher.anyOf(buf.toString());
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  public static void addOperator(final Map<String, Operator> operatorMap,
      final Map<String, ArrayList<Operator>> operatorTokenStartSet, final String operatorStr,
      final String headStr, final Operator oper) {
    ArrayList<Operator> list;
    operatorMap.put(headStr, oper);
    list = operatorTokenStartSet.get(operatorStr);
    if (list == null) {
      list = new ArrayList<Operator>(2);
      list.add(oper);
      operatorTokenStartSet.put(operatorStr, list);
    } else {
      list.add(oper);
    }
  }

  /** Create a default ASTNode factory */
  public FuzzyParserFactory() {
    // this.fIgnoreCase = ignoreCase;
  }

  @Override
  public Operator get(final String identifier) {
    return fOperatorMap.get(identifier);
  }

  @Override
  public Map<String, Operator> getIdentifier2OperatorMap() {
    return fOperatorMap;
  }

  /** */
  @Override
  public Map<String, ArrayList<Operator>> getOperator2ListMap() {
    return fOperatorTokenStartSet;
  }

  /** */
  @Override
  public List<Operator> getOperatorList(final String key) {
    return fOperatorTokenStartSet.get(key);
  }

  @Override
  public boolean isValidIdentifier(String identifier) {
    return true;
  }

  // private String toRubiString(final String nodeStr) {
  // if (!ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
  // if (nodeStr.length() == 1) {
  // return nodeStr;
  // }
  // String lowercaseName = nodeStr.toLowerCase();
  // String temp = AST2Expr.PREDEFINED_SYMBOLS_MAP.get(lowercaseName);
  // if (temp != null) {
  // if (!temp.equals(nodeStr)) {
  // temp = F.PREDEFINED_INTERNAL_FORM_STRINGS.get(nodeStr);
  // if (temp == null) {
  // if (lowercaseName.length() > 1) {
  // if (!lowercaseName.equals("sin") && !lowercaseName.equals("cos")
  // && !lowercaseName.equals("tan") && !lowercaseName.equals("cot")
  // && !lowercaseName.equals("csc") && !lowercaseName.equals("sec")) {
  // System.out.println(nodeStr + " => §" + lowercaseName);
  // }
  // }
  // return "§" + lowercaseName;
  // }
  // }
  // } else {
  // if (!nodeStr.equals(nodeStr.toLowerCase())) {
  // temp = F.PREDEFINED_INTERNAL_FORM_STRINGS.get(nodeStr);
  // if (temp == null) {
  // if (lowercaseName.length() > 1) {
  // System.out.println(nodeStr + " => §" + lowercaseName);
  // }
  // return "§" + lowercaseName;
  // }
  // }
  // }
  // }
  // return nodeStr;
  // }
}
