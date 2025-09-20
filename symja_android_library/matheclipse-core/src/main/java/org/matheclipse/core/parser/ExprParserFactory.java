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
package org.matheclipse.core.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.expression.B2;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.PatternNested;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.parser.client.ParserConfig;
import org.matheclipse.parser.client.Scanner;
import org.matheclipse.parser.client.ast.IParserFactory;
import org.matheclipse.parser.client.operator.InfixOperator;
import org.matheclipse.parser.client.operator.Operator;
import org.matheclipse.parser.client.operator.Precedence;
import org.matheclipse.parser.trie.Trie;
import org.matheclipse.parser.trie.TrieMatch;
import org.matheclipse.parser.wma.tablegen.WMAOperatorTables;
import com.google.common.base.CharMatcher;

public class ExprParserFactory implements IParserFactory {
  /** The matcher for characters, which could form an operator */
  public static CharMatcher OPERATOR_MATCHER = null;

  /** The set of characters, which could form an operator */
  @Override
  public boolean isOperatorChar(char ch) {
    return OPERATOR_MATCHER.matches(ch);
  }

  private static class InformationOperator extends PrefixExprOperator {
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
  private static class ApplyOperator extends InfixExprOperator {
    public ApplyOperator(final String oper, final String functionName, final int precedence,
        final int grouping) {
      super(oper, functionName, precedence, grouping);
    }

    @Override
    public IASTMutable createFunction(final IParserFactory factory, ExprParser parser,
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

  private static class TagSetOperator extends InfixExprOperator {
    public TagSetOperator(final String oper, final String functionName, final int precedence,
        final int grouping) {
      super(oper, functionName, precedence, grouping);
    }

    @Override
    public IASTMutable createFunction(final IParserFactory factory, ExprParser parser,
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

  private static class TildeExprOperator extends InfixExprOperator {

    public TildeExprOperator(final String oper, final String functionName, final int precedence,
        final int grouping) {
      super(oper, functionName, precedence, grouping);
    }

    @Override
    public IASTAppendable createFunction(final IParserFactory factory, ExprParser parser,
        final IExpr lhs, final IExpr rhs) {
      IASTAppendable result = F.ast(F.NIL);
      result.append(lhs);
      result.append(rhs);
      return result;
    }

    @Override
    public IAST endFunction(final IParserFactory factory, final IAST function,
        final Scanner scanner) {
      final int size = function.size();
      if (size < 4 || (size & 0x01) != 0x00) {
        scanner.throwSyntaxError("Operator ~ requires even number of arguments");
      }

      IAST result = F.binaryAST2(function.arg2(), function.arg1(), function.arg3());
      for (int i = 4; i < size; i += 2) {
        IAST temp = F.binaryAST2(function.get(i), result, function.get(i + 1));
        result = temp;
      }

      return result;
    }
  }

  private static class DivideExprOperator extends InfixExprOperator {
    public DivideExprOperator(final String oper, final String functionName, final int precedence,
        final int grouping) {
      super(oper, functionName, precedence, grouping);
    }

    @Override
    public IASTMutable createFunction(final IParserFactory factory, ExprParser parser,
        final IExpr lhs, final IExpr rhs) {

      if (Config.USER_STEPS_PARSER) {
        if (rhs.isInteger() && !rhs.isZero()) {
          if (lhs.isInteger()) {
            return (IASTMutable) F.Rational(lhs, rhs);
          }
        }
        return new B2.Divide(lhs, rhs);
      } else {

        if (rhs.isInteger() && !rhs.isZero()) {
          if (lhs.isInteger()) {
            if (!parser.isHoldOrHoldFormOrDefer()) {
              return (IASTMutable) F.Rational(lhs, rhs);
            }
          }
          return F.Times(F.fraction(F.C1, (IInteger) rhs), lhs);
        }


        if (lhs.equals(F.C1)) {
          return F.binaryAST2(S.Power, rhs, F.CN1);
        }
        if (rhs.isPower() && rhs.exponent().isNumber()) {
          return F.Times(lhs, F.Power(rhs.base(), rhs.exponent().negate()));
        }
      }
      return F.Times(lhs, F.Power(rhs, F.CN1));
    }
  }

  private static class PatternExprOperator extends InfixExprOperator {
    public PatternExprOperator(final String oper, final String functionName, final int precedence,
        final int grouping) {
      super(oper, functionName, precedence, grouping);
    }

    @Override
    public IASTMutable createFunction(final IParserFactory factory, ExprParser parser,
        final IExpr lhs, final IExpr rhs) {

      if (lhs.isSymbol()) {

        if (rhs instanceof PatternNested) {
          PatternNested pn = (PatternNested) rhs;
          IExpr subPattern = pn.getPatternExpr();
          if (subPattern instanceof PatternNested && pn.getSymbol() != null) {
            return F.binaryAST2(S.Optional, F.binaryAST2(S.Pattern, lhs, pn.getSymbol()),
                subPattern);
          }
        }
        return F.binaryAST2(S.Pattern, lhs, rhs);
      }
      return F.binaryAST2(S.Optional, lhs, rhs);
    }
  }

  private static class PreMinusExprOperator extends PrefixExprOperator {

    public PreMinusExprOperator(final String oper, final String functionName,
        final int precedence) {
      super(oper, functionName, precedence);
    }

    @Override
    public IExpr createFunction(final IParserFactory factory, final IExpr argument) {
      return F.Times(F.CN1, argument);
    }
  }

  private static class PrePlusExprOperator extends PrefixExprOperator {

    public PrePlusExprOperator(final String oper, final String functionName, final int precedence) {
      super(oper, functionName, precedence);
    }

    @Override
    public IExpr createFunction(final IParserFactory factory, final IExpr argument) {
      return argument;
    }
  }

  private static class SubtractExprOperator extends InfixExprOperator {
    public SubtractExprOperator(final String oper, final String functionName, final int precedence,
        final int grouping) {
      super(oper, functionName, precedence, grouping);
    }

    @Override
    public IASTMutable createFunction(final IParserFactory factory, ExprParser parser,
        final IExpr lhs, final IExpr rhs) {
      if (Config.USER_STEPS_PARSER) {
        return new B2.Subtract(lhs, rhs);
      } else {
        if (rhs.isNumber()) {
          return F.Plus(lhs, rhs.negate());
        }
        if (rhs.isTimes() && rhs.first().isNumber()) {
          return F.Plus(lhs, ((IAST) rhs).setAtCopy(1, rhs.first().negate()));
        }
        return F.Plus(lhs, F.Times(F.CN1, rhs));
      }
    }
  }

  public static final InformationOperator INFORMATION_SHORT = //
      new InformationOperator("?", "Information", 720);

  public static final InformationOperator INFORMATION_LONG = //
      new InformationOperator("??", "Information", 720);

  public static final ApplyOperator APPLY_HEAD_OPERATOR = //
      new ApplyOperator("@", "Apply", Precedence.APPLY_HEAD, InfixExprOperator.RIGHT_ASSOCIATIVE);

  public static final ApplyOperator APPLY_OPERATOR = //
      new ApplyOperator("@@", "Apply", Precedence.APPLY, InfixExprOperator.RIGHT_ASSOCIATIVE);

  public static final ApplyOperator MAPAPPLY_OPERATOR = //
      new ApplyOperator("@@@", "MapApply", Precedence.MAPAPPLY,
          InfixExprOperator.RIGHT_ASSOCIATIVE);

  public static final InfixExprOperator EQUAL_OPERATOR = //
      new InfixExprOperator("==", "Equal", Precedence.EQUAL, InfixExprOperator.NONE);

  public static final InfixExprOperator NON_COMMUTATIVE_MULTIPLY_OPERATOR = //
      new InfixExprOperator("**", "NonCommutativeMultiply", Precedence.NONCOMMUTATIVEMULTIPLY,
          InfixExprOperator.NONE);

  public static final InfixExprOperator POWER_OPERATOR = //
      new InfixExprOperator("^", "Power", Precedence.POWER, InfixExprOperator.RIGHT_ASSOCIATIVE);

  public static final InfixExprOperator SET_OPERATOR = //
      new InfixExprOperator("=", "Set", Precedence.SET, InfixExprOperator.RIGHT_ASSOCIATIVE);

  public static final TagSetOperator TAG_SET_OPERATOR = //
      new TagSetOperator("/:", "TagSet", Precedence.TAGSET, InfixExprOperator.NONE);

  static final String[] HEADER_STRINGS = {"MessageName", "Information", "Information", "Get",
      "PatternTest", "MapAll", "TimesBy", "Plus", "UpSet", "CompoundExpression", "Apply", "Map",
      "Unset", "Apply", "Apply", "ReplaceRepeated", "Less", "And", "Divide", "Set", "Increment",
      "Factorial2", "LessEqual", "NonCommutativeMultiply", "Factorial", "Times", "Power", "Dot",
      "Not", "PreMinus", "SameQ", "RuleDelayed", "GreaterEqual", "Condition",
      // "Colon",
      "//", "DivideBy", "Or", "Span", "Equal", "StringJoin", "Unequal", "Decrement", "SubtractFrom",
      "PrePlus", "RepeatedNull", "UnsameQ", "Rule", "UpSetDelayed", "PreIncrement", "Function",
      "Function", "Greater", "PreDecrement", "Subtract", "SetDelayed", "Alternatives", "AddTo",
      "Repeated", "ReplaceAll", "TagSet", "Composition", "RightComposition", "StringExpression",
      "Pattern", "TwoWayRule", "TwoWayRule", "DirectedEdge", "UndirectedEdge", "CenterDot",
      "CircleDot", "CircleTimes", "Distributed", "Element", "NotElement", "Intersection",
      "NotEqual", "Wedge", "TensorProduct", "Equivalent", "Implies", "PlusMinus", "PlusMinus", //
      "Star", //


      "DifferenceDelta", //
      "Exists", //
      "ForAll", //
      "Product", //
      "Put", //
      "PutAppend", //
      "RightTee", //
      "RoundImplies", //
      "Sqrt", //
      "Square", //
      "SuchThat", //
      "Sum", //
      "Therefore", //
      "Transpose", //
      "UpTee", //


      "§TILDE§" //

  };



  static final String[] OPERATOR_STRINGS = {"::", "<<", "?", "??", "?", "//@", "*=", "+", "^=", ";",
      "@", "/@", "=.", "@@", "@@@", "//.", "<", "&&", "/", "=", "++", "!!", "<=", "**", "!", "*",
      "^", ".", "!", "-", "===", ":>", ">=", "/;", "//", "/=", "||", ";;", "==", "<>", "!=", "--",
      "-=", "+", "...", "=!=", "->", "^:=", "++", "|->", "&", ">", "--", "-", ":=", "|", "+=", "..",
      "/.", "/:", "@*", "/*", "~~", //
      ":", // Pattern
      "<->", // TwoWayRule
      "\uF120", // TwoWayRule
      "\uF3D5", // DirectedEdge
      "\uF3D4", // UndirectedEdge
      "\u00B7", // CenterDot
      "\u2299", // CircleDot
      "\u2297", // CircleTimes
      "\uF3D2", // Distributed
      "\u2208", // Element
      "\u2209", // NotElement
      "\u22C2", // Intersection
      "\u2260", // NotEqual
      "\u22C0", // Wedge
      "\uF3DA", // TensorProduct
      "\u29E6", // Equivalent
      "\uF523", // Implies
      "\u001b", // PlusMinus infix operator
      "\u001b", // PlusMinus prefix operator
      "\u22c6", // Star infix operator


      "∆", // DifferenceDelta", 550),
      "∃", // Exists", 240), //
      "∀", // ForAll", 240),
      "∏", // Product", 380),
      ">>", // "Put", 30, InfixExprOperator.LEFT_ASSOCIATIVE),
      ">>>", // "PutAppend", 30, InfixExprOperator.LEFT_ASSOCIATIVE),
      "⊢", // "RightTee", 190, InfixExprOperator.RIGHT_ASSOCIATIVE),
      "⥰", // "RoundImplies", 193, InfixExprOperator.RIGHT_ASSOCIATIVE),
      "√", // "Sqrt", 570), //
      "▫", // "Square", 540), //
      "∍", // "SuchThat", 180, InfixExprOperator.RIGHT_ASSOCIATIVE),
      "∑", // "Sum", 325), //
      "∴", // "Therefore", 50, InfixExprOperator.RIGHT_ASSOCIATIVE),
      "ᵀ", // "Transpose", 670),
      "⊥", // UpTee

      "~"};

  private static Operator[] OPERATORS;

  public static final ExprParserFactory MMA_STYLE_FACTORY = new ExprParserFactory();

  public static final ExprParserFactory RELAXED_STYLE_FACTORY = new ExprParserFactory();

  /** */
  private static Trie<String, Operator> fOperatorMap;

  /** */
  private static Trie<String, ArrayList<Operator>> fOperatorTokenStartSet;

  public static final String[] ADDITONAL_FUNCTION_STRINGS =
      {"DifferenceDelta", "Exists", "ForAll", "Product", "Put", "PutAppend", "RightTee",
          "RoundImplies", "Sqrt", "Square", "SuchThat", "Sum", "Therefore", "Transpose", "UpTee"};

  public static void main(String[] args) {
    initialize();
    for (int i = 0; i < AST2Expr.FUNCTION_STRINGS.length; i++) {
      String function = AST2Expr.FUNCTION_STRINGS[i];
      if (WMAOperatorTables.isLeftAssociative(function) //
          || WMAOperatorTables.isRightAssociative(function)
          || WMAOperatorTables.isPostfixOperator(function)
          || WMAOperatorTables.isPrefixOperator(function)) {
        Operator operator = fOperatorMap.get(function);
        if (operator == null) {
          List<String> list = WMAOperatorTables.OPERATOR_TO_CHARACTERS.get(function);
          if (list != null) {
            System.out.println("\"" + function + "\",");
          }

        }
      }
    }
    for (int i = 0; i < ADDITONAL_FUNCTION_STRINGS.length; i++) {
      String operator = ADDITONAL_FUNCTION_STRINGS[i];
      if (WMAOperatorTables.isLeftAssociative(operator)) {
        // InfixExprOperator("//.", "ReplaceRepeated", precedence,
        // InfixExprOperator.LEFT_ASSOCIATIVE)
        List<String> list = WMAOperatorTables.OPERATOR_TO_CHARACTERS.get(operator);
        if (list == null) {
          System.out.println(operator);
        }
        Integer precedence = WMAOperatorTables.getOperatorPrecedence(operator);
        for (int j = 0; j < list.size(); j++) {
          System.out.println("new InfixExprOperator(\"" //
              + list.get(j) + "\",\""//
              + operator + "\"," //
              + precedence + ", InfixExprOperator.LEFT_ASSOCIATIVE);");
        }
      } else if (WMAOperatorTables.isRightAssociative(operator)) {
        List<String> list = WMAOperatorTables.OPERATOR_TO_CHARACTERS.get(operator);
        if (list == null) {
          System.out.println(operator);
        }
        Integer precedence = WMAOperatorTables.getOperatorPrecedence(operator);
        for (int j = 0; j < list.size(); j++) {

          System.out.println("new InfixExprOperator(\"" //
              + list.get(j) + "\",\""//
              + operator + "\"," //
              + precedence + ", InfixExprOperator.RIGHT_ASSOCIATIVE);");
        }
      } else if (WMAOperatorTables.isPrefixOperator(operator)) {
        List<String> list = WMAOperatorTables.OPERATOR_TO_CHARACTERS.get(operator);
        if (list == null) {
          System.out.println(operator);
        }
        Integer precedence = WMAOperatorTables.getOperatorPrecedence(operator);
        for (int j = 0; j < list.size(); j++) {

          System.out.println("new PrefixExprOperator(\"" //
              + list.get(j) + "\",\""//
              + operator + "\"," //
              + precedence + ");");
        }
      } else if (WMAOperatorTables.isPostfixOperator(operator)) {
        List<String> list = WMAOperatorTables.OPERATOR_TO_CHARACTERS.get(operator);
        if (list == null) {
          System.out.println(operator);
        }
        Integer precedence = WMAOperatorTables.getOperatorPrecedence(operator);
        for (int j = 0; j < list.size(); j++) {
          System.out.println("new PostfixExprOperator(\"" //
              + list.get(j) + "\",\""//
              + operator + "\"," //
              + precedence + ");");
        }
      }

    }
  }

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {

      OPERATORS = new Operator[] {
          new InfixExprOperator("::", "MessageName", Precedence.MESSAGENAME,
              InfixExprOperator.NONE),
          new PrefixExprOperator("<<", "Get", Precedence.GET), //
          INFORMATION_SHORT, //
          INFORMATION_LONG, //
          new InfixExprOperator("?", "PatternTest", Precedence.PATTERNTEST, InfixExprOperator.NONE), //
          new InfixExprOperator("//@", "MapAll", Precedence.MAPALL,
              InfixExprOperator.RIGHT_ASSOCIATIVE), //
          new InfixExprOperator("*=", "TimesBy", Precedence.TIMESBY,
              InfixExprOperator.RIGHT_ASSOCIATIVE), //
          new InfixExprOperator("+", "Plus", Precedence.PLUS, InfixExprOperator.NONE), //
          new InfixExprOperator("^=", "UpSet", Precedence.UPSET,
              InfixExprOperator.RIGHT_ASSOCIATIVE), //
          new InfixExprOperator(";", "CompoundExpression", Precedence.COMPOUNDEXPRESSION,
              InfixExprOperator.NONE), //
          APPLY_HEAD_OPERATOR, //
          new InfixExprOperator("/@", "Map", Precedence.MAP, InfixExprOperator.RIGHT_ASSOCIATIVE), //
          new PostfixExprOperator("=.", "Unset", Precedence.UNSET), //
          APPLY_OPERATOR, //
          MAPAPPLY_OPERATOR, //
          new InfixExprOperator("//.", "ReplaceRepeated", Precedence.REPLACEREPEATED,
              InfixExprOperator.LEFT_ASSOCIATIVE), //
          new InfixExprOperator("<", "Less", Precedence.LESS, InfixExprOperator.NONE), //
          new InfixExprOperator("&&", "And", Precedence.AND, InfixExprOperator.NONE), //
          new DivideExprOperator("/", "Divide", Precedence.DIVIDE,
              InfixExprOperator.LEFT_ASSOCIATIVE), //
          SET_OPERATOR, //
          new PostfixExprOperator("++", "Increment", Precedence.INCREMENT), //
          new PostfixExprOperator("!!", "Factorial2", Precedence.FACTORIAL2), //
          new InfixExprOperator("<=", "LessEqual", Precedence.LESSEQUAL, InfixExprOperator.NONE), //
          NON_COMMUTATIVE_MULTIPLY_OPERATOR, //
          new PostfixExprOperator("!", "Factorial", Precedence.FACTORIAL), //
          new InfixExprOperator("*", "Times", Precedence.TIMES, InfixExprOperator.NONE), //
          POWER_OPERATOR, //
          new InfixExprOperator(".", "Dot", Precedence.DOT, InfixExprOperator.NONE), //
          new PrefixExprOperator("!", "Not", Precedence.NOT), //
          new PreMinusExprOperator("-", "PreMinus", Precedence.PREMINUS), //
          new InfixExprOperator("===", "SameQ", Precedence.SAMEQ, InfixExprOperator.NONE), //
          new InfixExprOperator(":>", "RuleDelayed", Precedence.RULEDELAYED,
              InfixExprOperator.RIGHT_ASSOCIATIVE), //
          new InfixExprOperator(">=", "GreaterEqual", Precedence.GREATEREQUAL,
              InfixExprOperator.NONE), //
          new InfixExprOperator("/;", "Condition", Precedence.CONDITION,
              InfixExprOperator.LEFT_ASSOCIATIVE), //
          // new InfixExprOperator(":", "Colon", Precedence.COLON,InfixExprOperator.NONE), //
          new InfixExprOperator("//", "//", 70, InfixExprOperator.LEFT_ASSOCIATIVE), //
          new InfixExprOperator("/=", "DivideBy", Precedence.DIVIDEBY,
              InfixExprOperator.RIGHT_ASSOCIATIVE), //
          new InfixExprOperator("||", "Or", Precedence.OR, InfixExprOperator.NONE), //
          new InfixExprOperator(";;", "Span", Precedence.SPAN, InfixExprOperator.NONE), //
          EQUAL_OPERATOR, //
          new InfixExprOperator("<>", "StringJoin", Precedence.STRINGJOIN, InfixExprOperator.NONE), //
          new InfixExprOperator("!=", "Unequal", Precedence.UNEQUAL, InfixExprOperator.NONE), //
          new PostfixExprOperator("--", "Decrement", Precedence.DECREMENT), //
          new InfixExprOperator("-=", "SubtractFrom", Precedence.SUBTRACTFROM,
              InfixExprOperator.RIGHT_ASSOCIATIVE), //
          new PrePlusExprOperator("+", "PrePlus", Precedence.PREPLUS), //
          new PostfixExprOperator("...", "RepeatedNull", Precedence.REPEATEDNULL), //
          new InfixExprOperator("=!=", "UnsameQ", Precedence.UNSAMEQ, InfixExprOperator.NONE), //
          new InfixExprOperator("->", "Rule", Precedence.RULE, InfixExprOperator.RIGHT_ASSOCIATIVE), //
          new InfixExprOperator("^:=", "UpSetDelayed", Precedence.UPSETDELAYED,
              InfixExprOperator.RIGHT_ASSOCIATIVE), //
          new PrefixExprOperator("++", "PreIncrement", Precedence.PREINCREMENT), // // the order of
                                                                                 // the 2 operators
                                                                                 // matters for
                                                                                 // Function
          new InfixExprOperator("|->", "Function", Precedence.FUNCTION,
              InfixOperator.RIGHT_ASSOCIATIVE),
          new PostfixExprOperator("&", "Function", Precedence.FUNCTION), //
          new InfixExprOperator(">", "Greater", Precedence.GREATER, InfixExprOperator.NONE), //
          new PrefixExprOperator("--", "PreDecrement", Precedence.PREDECREMENT), //
          new SubtractExprOperator("-", "Subtract", Precedence.SUBTRACT,
              InfixExprOperator.LEFT_ASSOCIATIVE), //
          new InfixExprOperator(":=", "SetDelayed", Precedence.SETDELAYED,
              InfixExprOperator.RIGHT_ASSOCIATIVE), //
          new InfixExprOperator("|", "Alternatives", Precedence.ALTERNATIVES,
              InfixExprOperator.NONE), //
          new InfixExprOperator("+=", "AddTo", Precedence.ADDTO,
              InfixExprOperator.RIGHT_ASSOCIATIVE), //
          new PostfixExprOperator("..", "Repeated", Precedence.REPEATED), //
          new InfixExprOperator("/.", "ReplaceAll", Precedence.REPLACEALL,
              InfixExprOperator.LEFT_ASSOCIATIVE), //
          TAG_SET_OPERATOR, //
          new InfixExprOperator("@*", "Composition", Precedence.COMPOSITION, InfixOperator.NONE),
          new InfixExprOperator("/*", "RightComposition", Precedence.RIGHTCOMPOSITION,
              InfixOperator.NONE),
          new InfixExprOperator("~~", "StringExpression", Precedence.STRINGEXPRESSION,
              InfixOperator.NONE),
          new PatternExprOperator(":", "Pattern", Precedence.PATTERN, InfixOperator.NONE),
          new InfixExprOperator("<->", "TwoWayRule", Precedence.TWOWAYRULE,
              InfixOperator.RIGHT_ASSOCIATIVE), //
          new InfixExprOperator("\uF120", "TwoWayRule", Precedence.TWOWAYRULE,
              InfixOperator.RIGHT_ASSOCIATIVE), //
          new InfixExprOperator("\uF3D5", "DirectedEdge", Precedence.DIRECTEDEDGE,
              InfixOperator.RIGHT_ASSOCIATIVE), //
          new InfixExprOperator("\uF3D4", "UndirectedEdge", Precedence.UNDIRECTEDEDGE,
              InfixOperator.RIGHT_ASSOCIATIVE), //
          new InfixExprOperator("\u00B7", "CenterDot", Precedence.CENTERDOT,
              InfixExprOperator.NONE), //
          new InfixExprOperator("\u2299", "CircleDot", Precedence.CIRCLEDOT,
              InfixExprOperator.NONE), //
          new InfixExprOperator("\u2297", "CircleTimes", Precedence.CIRCLETIMES,
              InfixExprOperator.NONE), //
          new InfixExprOperator("\uF3D2", "Distributed", Precedence.DISTRIBUTED,
              InfixExprOperator.NONE), //
          new InfixExprOperator("\u2208", "Element", Precedence.ELEMENT, InfixExprOperator.NONE), //
          new InfixExprOperator("\u2209", "NotElement", Precedence.NOTELEMENT,
              InfixExprOperator.NONE), //
          new InfixExprOperator("\u22C2", "Intersection", Precedence.INTERSECTION,
              InfixExprOperator.NONE), //
          new InfixExprOperator("\u2260", "Unequal", Precedence.UNEQUAL, InfixExprOperator.NONE), //
          new InfixExprOperator("\u22C0", "Wedge", Precedence.WEDGE, InfixExprOperator.NONE), //
          new InfixExprOperator("\uF3DA", "TensorProduct", Precedence.TENSORPRODUCT,
              InfixExprOperator.NONE),
          new InfixExprOperator("\u29E6", "Equivalent", Precedence.EQUIVALENT, InfixOperator.NONE),
          new InfixExprOperator("\uF523", "Implies", Precedence.IMPLIES,
              InfixOperator.RIGHT_ASSOCIATIVE),
          new InfixExprOperator("\u00b1", "PlusMinus", Precedence.PLUSMINUS,
              InfixOperator.LEFT_ASSOCIATIVE),
          new PrefixExprOperator("\u00b1", "PlusMinus", Precedence.PLUSMINUS),
          new InfixExprOperator("\u22c6", "Star", Precedence.STAR, InfixExprOperator.NONE),

          new PrefixExprOperator("∆", "DifferenceDelta", 550),
          new PrefixExprOperator("∃", "Exists", 240), //
          new PrefixExprOperator("∀", "ForAll", 240), new PrefixExprOperator("∏", "Product", 380),
          new InfixExprOperator(">>", "Put", 30, InfixExprOperator.LEFT_ASSOCIATIVE),
          new InfixExprOperator(">>>", "PutAppend", 30, InfixExprOperator.LEFT_ASSOCIATIVE),
          new InfixExprOperator("⊢", "RightTee", 190, InfixExprOperator.RIGHT_ASSOCIATIVE),
          new InfixExprOperator("⥰", "RoundImplies", 193, InfixExprOperator.RIGHT_ASSOCIATIVE),
          new PrefixExprOperator("√", "Sqrt", 570), //
          new PrefixExprOperator("▫", "Square", 540), //
          new InfixExprOperator("∍", "SuchThat", 180, InfixExprOperator.RIGHT_ASSOCIATIVE),
          new PrefixExprOperator("∑", "Sum", 325), //
          new InfixExprOperator("∴", "Therefore", 50, InfixExprOperator.RIGHT_ASSOCIATIVE),
          new PostfixExprOperator("ᵀ", "Transpose", 670),
          new InfixExprOperator("⊥", "UpTee", 197, InfixExprOperator.LEFT_ASSOCIATIVE),


          new TildeExprOperator("~", "§TILDE§", Precedence.TILDE_OPERATOR, InfixOperator.NONE)};
      StringBuilder buf = new StringBuilder(BASIC_OPERATOR_CHARACTERS);

      fOperatorMap = ParserConfig.TRIE_STRING2OPERATOR_BUILDER.withMatch(TrieMatch.EXACT).build();
      fOperatorTokenStartSet =
          ParserConfig.TRIE_STRING2OPERATORLIST_BUILDER.withMatch(TrieMatch.EXACT).build();

      for (int i = 0; i < HEADER_STRINGS.length; i++) {
        addOperator(fOperatorMap, fOperatorTokenStartSet, OPERATOR_STRINGS[i], HEADER_STRINGS[i],
            OPERATORS[i]);
        String unicodeChar =
            org.matheclipse.parser.client.Characters.NamedCharactersMap.get(HEADER_STRINGS[i]);
        if (unicodeChar != null) {
          addUnicodeOperator(fOperatorMap, fOperatorTokenStartSet, unicodeChar, OPERATORS[i]);
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
      final Map<String, ArrayList<Operator>> operatorTokenStartSet, final String operatorToken,
      final String headStr, final Operator operator) {
    ArrayList<Operator> list;
    operatorMap.put(headStr, operator);
    list = operatorTokenStartSet.get(operatorToken);
    if (list == null) {
      list = new ArrayList<Operator>(2);
      list.add(operator);
      operatorTokenStartSet.put(operatorToken, list);
    } else {
      list.add(operator);
    }
  }

  private static void addUnicodeOperator(final Map<String, Operator> operatorMap,
      final Map<String, ArrayList<Operator>> operatorTokenStartSet,
      final String operatorUnicodeToken, final Operator operator) {
    ArrayList<Operator> list = operatorTokenStartSet.get(operatorUnicodeToken);
    if (list == null) {
      list = new ArrayList<Operator>(2);
      list.add(operator);
      operatorTokenStartSet.put(operatorUnicodeToken, list);
    } else {
      list.add(operator);
    }
  }

  /** Create a default ASTNode factory */
  public ExprParserFactory() {
    // this.fIgnoreCase = ignoreCase;
  }

  @Override
  public Operator get(final String identifier) {
    return fOperatorMap.get(identifier);
  }

  /** public Map<String, Operator> getIdentifier2OperatorMap() */
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
