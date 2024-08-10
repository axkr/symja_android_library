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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.matheclipse.parser.client.ParserConfig;
import org.matheclipse.parser.client.Scanner;
import org.matheclipse.parser.client.ast.ASTNode;
import org.matheclipse.parser.client.ast.FloatNode;
import org.matheclipse.parser.client.ast.FractionNode;
import org.matheclipse.parser.client.ast.FunctionNode;
import org.matheclipse.parser.client.ast.IConstantOperators;
import org.matheclipse.parser.client.ast.INodeParserFactory;
import org.matheclipse.parser.client.ast.IntegerNode;
import org.matheclipse.parser.client.ast.Pattern2Node;
import org.matheclipse.parser.client.ast.Pattern3Node;
import org.matheclipse.parser.client.ast.PatternNode;
import org.matheclipse.parser.client.ast.StringNode;
import org.matheclipse.parser.client.ast.SymbolNode;
import org.matheclipse.parser.trie.Trie;
import org.matheclipse.parser.trie.TrieMatch;

public class ASTNodeFactory implements INodeParserFactory {
  /** The matcher for characters, which could form an operator */
  public static String OPERATOR_CHARACTERS = null;

  @Override
  public boolean isOperatorChar(char ch) {
    return OPERATOR_CHARACTERS != null && OPERATOR_CHARACTERS.indexOf(ch) >= 0;
  }

  /** @@@ operator (not @@ operator) */
  private static class ApplyOperator extends InfixOperator {
    public ApplyOperator(final String oper, final String functionName, final int precedence,
        final int grouping) {
      super(oper, functionName, precedence, grouping);
    }

    @Override
    public ASTNode createFunction(final INodeParserFactory factory, final ASTNode lhs,
        final ASTNode rhs) {
      if (fOperatorString.equals("@")) {
        return factory.unaryAST(lhs, rhs);
      }
      FunctionNode fn = factory.createFunction(factory.createSymbol("Apply"), lhs, rhs);
      if (fOperatorString.equals("@@")) {
        return fn;
      }
      // case "@@@"
      fn.add(factory.createFunction(factory.createSymbol(IConstantOperators.List),
          factory.createInteger(1)));
      return fn;
    }
  }

  private static class TagSetOperator extends InfixOperator {
    public TagSetOperator(final String oper, final String functionName, final int precedence,
        final int grouping) {
      super(oper, functionName, precedence, grouping);
    }

    @Override
    public ASTNode createFunction(final INodeParserFactory factory, final ASTNode lhs,
        final ASTNode rhs) {
      if (rhs instanceof FunctionNode) {
        FunctionNode r = (FunctionNode) rhs;
        if (r.size() == 3) {
          if (r.get(0).equals(factory.createSymbol("Set"))) {
            return factory.createFunction(factory.createSymbol("TagSet"), lhs, r.get(1), r.get(2));
          } else if (r.get(0).equals(factory.createSymbol("SetDelayed"))) {
            return factory.createFunction(factory.createSymbol("TagSetDelayed"), lhs, r.get(1),
                r.get(2));
          }
        }
      }
      return factory.createFunction(factory.createSymbol("TagSet"), lhs, rhs);
    }
  }

  private static class DivideOperator extends InfixOperator {
    public DivideOperator(final String oper, final String functionName, final int precedence,
        final int grouping) {
      super(oper, functionName, precedence, grouping);
    }

    @Override
    public ASTNode createFunction(final INodeParserFactory factory, final ASTNode lhs,
        final ASTNode rhs) {
      if (rhs instanceof IntegerNode) {
        if (lhs instanceof IntegerNode) {
          return new FractionNode((IntegerNode) lhs, (IntegerNode) rhs);
        }
        return factory.createFunction(factory.createSymbol("Times"),
            new FractionNode(IntegerNode.C1, (IntegerNode) rhs), lhs);
      }
      if (lhs.equals(IntegerNode.C1)) {
        return factory.createFunction(factory.createSymbol("Power"), rhs,
            factory.createInteger(-1));
      }
      return factory.createFunction(factory.createSymbol("Times"), lhs,
          factory.createFunction(factory.createSymbol("Power"), rhs, factory.createInteger(-1)));
    }
  }

  private static class StarOperator extends InfixOperator {
    public StarOperator(final String oper, final String functionName, final int precedence,
        final int grouping) {
      super(oper, functionName, precedence, grouping);
    }

    @Override
    public ASTNode createFunction(final INodeParserFactory factory, final ASTNode lhs,
        final ASTNode rhs) {
      return factory.createFunction(factory.createSymbol("Star"), lhs, rhs);
    }
  }

  private static class PatternOperator extends InfixOperator {
    public PatternOperator(final String oper, final String functionName, final int precedence,
        final int grouping) {
      super(oper, functionName, precedence, grouping);
    }

    @Override
    public ASTNode createFunction(final INodeParserFactory factory, final ASTNode lhs,
        final ASTNode rhs) {

      if (lhs instanceof SymbolNode) {
        if (rhs instanceof FunctionNode) {
          FunctionNode pn1 = (FunctionNode) rhs;
          if (pn1.size() == 3 && pn1.get(0).equals(factory.createSymbol(IConstantOperators.Pattern))
              && (pn1.get(1) instanceof SymbolNode) && (pn1.get(2) instanceof FunctionNode)) {
            FunctionNode pn2 = (FunctionNode) pn1.get(2);
            if (pn2.size() == 3
                && pn2.get(0).equals(factory.createSymbol(IConstantOperators.Pattern))) {
              return factory.createFunction(factory.createSymbol(IConstantOperators.Optional),
                  factory.createFunction(factory.createSymbol(IConstantOperators.Pattern), lhs,
                      pn1.get(1)),
                  pn2);
            }
          }
        }
        return factory.createFunction(factory.createSymbol(IConstantOperators.Pattern), lhs, rhs);
      }
      return factory.createFunction(factory.createSymbol(IConstantOperators.Optional), lhs, rhs);
    }
  }

  private static class TildeOperator extends InfixOperator {

    public TildeOperator(final String oper, final String functionName, final int precedence,
        final int grouping) {
      super(oper, functionName, precedence, grouping);
    }

    @Override
    public ASTNode createFunction(final INodeParserFactory factory, final ASTNode lhs,
        final ASTNode rhs) {
      return factory.createFunction(factory.createSymbol("§TILDE§"), lhs, rhs);
    }

    @Override
    public FunctionNode endFunction(final INodeParserFactory factory, final FunctionNode function,
        final Scanner scanner) {
      final int size = function.size();
      if (size < 4 || (size & 0x01) != 0x00) {
        scanner.throwSyntaxError("Operator ~ requires even number of arguments");
      }

      FunctionNode result = factory.createAST(function.get(2));
      result.add(function.get(1));
      result.add(function.get(3));
      for (int i = 4; i < size; i += 2) {
        FunctionNode temp = factory.createAST(function.get(i));
        temp.add(result);
        temp.add(function.get(i + 1));
        result = temp;
      }

      return result;
    }
  }

  private static class MessageNameOperator extends InfixOperator {
    public MessageNameOperator(final String oper, final String functionName, final int precedence,
        final int grouping) {
      super(oper, functionName, precedence, grouping);
    }

    @Override
    public ASTNode createFunction(final INodeParserFactory factory, final ASTNode lhs,
        final ASTNode rhs) {
      if (rhs instanceof SymbolNode) {
        return factory.createFunction(factory.createSymbol(getFunctionName()), lhs,
            new StringNode(rhs.toString()));
      }
      return factory.createFunction(factory.createSymbol(getFunctionName()), lhs, rhs);
    }
  }

  private static class PreMinusOperator extends PrefixOperator {

    public PreMinusOperator(final String oper, final String functionName, final int precedence) {
      super(oper, functionName, precedence);
    }

    @Override
    public ASTNode createFunction(final INodeParserFactory factory, final ASTNode argument) {
      return factory.createFunction(factory.createSymbol("Times"), factory.createInteger(-1),
          argument);
    }
  }

  private static class PrePlusOperator extends PrefixOperator {

    public PrePlusOperator(final String oper, final String functionName, final int precedence) {
      super(oper, functionName, precedence);
    }

    @Override
    public ASTNode createFunction(final INodeParserFactory factory, final ASTNode argument) {
      return argument;
    }
  }

  private static class SubtractOperator extends InfixOperator {
    public SubtractOperator(final String oper, final String functionName, final int precedence,
        final int grouping) {
      super(oper, functionName, precedence, grouping);
    }

    @Override
    public ASTNode createFunction(final INodeParserFactory factory, final ASTNode lhs,
        final ASTNode rhs) {
      return factory.createFunction(factory.createSymbol("Plus"), lhs,
          factory.createFunction(factory.createSymbol("Times"), factory.createInteger(-1), rhs));
    }
  }

  static final String[] HEADER_STRINGS = {"MessageName", "Get", "PatternTest", "MapAll", "TimesBy",
      "Plus", "UpSet", "CompoundExpression", "Apply", "Map", "Unset", "Apply", "Apply",
      "ReplaceRepeated", "Less", "And", "Divide", "Set", "Increment", "Factorial2", "LessEqual",
      "NonCommutativeMultiply", "Factorial", "Times", "Power", "Dot", "Not", "PreMinus", "SameQ",
      "RuleDelayed", "GreaterEqual", "Condition",
      // "Colon",
      "//", "DivideBy", "Or", "Span", "Equal", "StringJoin", "Unequal", "Decrement", "SubtractFrom",
      "PrePlus", "RepeatedNull", "UnsameQ", "Rule", "UpSetDelayed", "PreIncrement", "Function",
      "Function", "Greater", "PreDecrement", "Subtract", "SetDelayed", "Alternatives", "AddTo",
      "Repeated", "ReplaceAll", "TagSet", "Composition", "RightComposition", "StringExpression",
      "Pattern", "TwoWayRule", "TwoWayRule", "DirectedEdge", "UndirectedEdge", "CenterDot",
      "CircleDot", "CircleTimes", "Distributed", "Element", "NotElement", "Intersection",
      "NotEqual", "Wedge", "TensorProduct", "Equivalent", "Implies", "PlusMinus", "PlusMinus", //
      "Star", // Rubi Star operator
      "§TILDE§"};

  static final String[] OPERATOR_STRINGS =
      {"::", "<<", "?", "//@", "*=", "+", "^=", ";", "@", "/@", "=.", "@@", "@@@", "//.", "<", "&&",
          "/", "=", "++", "!!", "<=", "**", "!", "*", "^", ".", "!", "-", "===", ":>", ">=", "/;",
          // ":",
          "//", "/=", "||", ";;", "==", "<>", "!=", "--", "-=", "+", "...", "=!=", "->", "^:=",
          "++", "|->", "&", ">", "--", "-", ":=", "|", "+=", "..", "/.", "/:", "@*", "/*", "~~", // StringExpression
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
          "\u2260", // NotEqual,
          "\u22C0", // Wedge
          "\uF3DA", // TensorProduct
          "\u29E6", // Equivalent
          "\uF523", // Implies
          "\u00b1", // PlusMinus infix operator
          "\u00b1", // PlusMinus prefix operator
          "\u22c6", // Rubi Star infix operator
          "~"};

  public static final ApplyOperator APPLY_HEAD_OPERATOR =
      new ApplyOperator("@", "Apply", Precedence.APPLY_HEAD, InfixOperator.RIGHT_ASSOCIATIVE);
  public static final ApplyOperator APPLY_OPERATOR =
      new ApplyOperator("@@", "Apply", Precedence.APPLY, InfixOperator.RIGHT_ASSOCIATIVE);
  public static final ApplyOperator APPLY_LEVEL_OPERATOR =
      new ApplyOperator("@@@", "Apply", Precedence.APPLY, InfixOperator.RIGHT_ASSOCIATIVE);

  public static final TagSetOperator TAG_SET_OPERATOR =
      new TagSetOperator("/:", "TagSet", Precedence.TAGSET, InfixOperator.NONE);

  private static Operator[] OPERATORS;

  public static final ASTNodeFactory MMA_STYLE_FACTORY = new ASTNodeFactory(false);

  public static final ASTNodeFactory RELAXED_STYLE_FACTORY = new ASTNodeFactory(true);

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
          new MessageNameOperator("::", "MessageName", Precedence.MESSAGENAME, InfixOperator.NONE),
          new PrefixOperator("<<", "Get", Precedence.GET),
          new InfixOperator("?", "PatternTest", Precedence.PATTERNTEST, InfixOperator.NONE),
          new InfixOperator("//@", "MapAll", Precedence.MAPALL, InfixOperator.RIGHT_ASSOCIATIVE),
          new InfixOperator("*=", "TimesBy", Precedence.TIMESBY, InfixOperator.RIGHT_ASSOCIATIVE),
          new InfixOperator("+", "Plus", Precedence.PLUS, InfixOperator.NONE),
          new InfixOperator("^=", "UpSet", Precedence.UPSET, InfixOperator.RIGHT_ASSOCIATIVE),
          new InfixOperator(";", "CompoundExpression", Precedence.COMPOUNDEXPRESSION,
              InfixOperator.NONE),
          APPLY_HEAD_OPERATOR,
          new InfixOperator("/@", "Map", Precedence.MAP, InfixOperator.RIGHT_ASSOCIATIVE),
          new PostfixOperator("=.", "Unset", Precedence.UNSET), APPLY_OPERATOR,
          APPLY_LEVEL_OPERATOR,
          // new ApplyOperator("@@", "Apply", APPLY_PRECEDENCE,
          // InfixOperator.RIGHT_ASSOCIATIVE),
          // new ApplyOperator("@@@", "Apply", APPLY_PRECEDENCE,
          // InfixOperator.RIGHT_ASSOCIATIVE),
          new InfixOperator("//.", "ReplaceRepeated", Precedence.REPLACEREPEATED,
              InfixOperator.LEFT_ASSOCIATIVE),
          new InfixOperator("<", "Less", Precedence.LESS, InfixOperator.NONE),
          new InfixOperator("&&", "And", Precedence.AND, InfixOperator.NONE),
          new DivideOperator("/", "Divide", Precedence.DIVIDE, InfixOperator.LEFT_ASSOCIATIVE),
          new InfixOperator("=", "Set", Precedence.SET, InfixOperator.RIGHT_ASSOCIATIVE),
          new PostfixOperator("++", "Increment", Precedence.INCREMENT), //
          new PostfixOperator("!!", "Factorial2", Precedence.FACTORIAL2),
          new InfixOperator("<=", "LessEqual", Precedence.LESSEQUAL, InfixOperator.NONE),
          new InfixOperator("**", "NonCommutativeMultiply", 510, InfixOperator.NONE),
          new PostfixOperator("!", "Factorial", Precedence.FACTORIAL),
          new InfixOperator("*", "Times", Precedence.TIMES, InfixOperator.NONE),
          new InfixOperator("^", "Power", Precedence.POWER, InfixOperator.RIGHT_ASSOCIATIVE),
          new InfixOperator(".", "Dot", Precedence.DOT, InfixOperator.NONE), //
          new PrefixOperator("!", "Not", Precedence.NOT),
          new PreMinusOperator("-", "PreMinus", Precedence.PREMINUS),
          new InfixOperator("===", "SameQ", Precedence.SAMEQ, InfixOperator.NONE),
          new InfixOperator(":>", "RuleDelayed", Precedence.RULEDELAYED,
              InfixOperator.RIGHT_ASSOCIATIVE),
          new InfixOperator(">=", "GreaterEqual", Precedence.GREATEREQUAL, InfixOperator.NONE),
          new InfixOperator("/;", "Condition", Precedence.CONDITION,
              InfixOperator.LEFT_ASSOCIATIVE),
          // new InfixOperator(":", "Colon", Precedence.COLON, InfixOperator.NONE),
          new InfixOperator("//", "//", 70, InfixOperator.LEFT_ASSOCIATIVE),
          new InfixOperator("/=", "DivideBy", Precedence.DIVIDEBY, InfixOperator.RIGHT_ASSOCIATIVE),
          new InfixOperator("||", "Or", Precedence.OR, InfixOperator.NONE),
          new InfixOperator(";;", "Span", Precedence.SPAN, InfixOperator.NONE),
          new InfixOperator("==", "Equal", Precedence.EQUAL, InfixOperator.NONE),
          new InfixOperator("<>", "StringJoin", Precedence.STRINGJOIN, InfixOperator.NONE),
          new InfixOperator("!=", "Unequal", Precedence.UNEQUAL, InfixOperator.NONE),
          new PostfixOperator("--", "Decrement", Precedence.DECREMENT),
          new InfixOperator("-=", "SubtractFrom", Precedence.SUBTRACTFROM,
              InfixOperator.RIGHT_ASSOCIATIVE),
          new PrePlusOperator("+", "PrePlus", Precedence.PREPLUS), //
          new PostfixOperator("...", "RepeatedNull", Precedence.REPEATEDNULL),
          new InfixOperator("=!=", "UnsameQ", Precedence.UNSAMEQ, InfixOperator.NONE),
          new InfixOperator("->", "Rule", Precedence.RULE, InfixOperator.RIGHT_ASSOCIATIVE),
          new InfixOperator("^:=", "UpSetDelayed", Precedence.UPSETDELAYED,
              InfixOperator.RIGHT_ASSOCIATIVE),
          new PrefixOperator("++", "PreIncrement", Precedence.PREINCREMENT), //
          // the order of the 2 operators matters for Function
          new InfixOperator("|->", "Function", Precedence.FUNCTION,
              InfixOperator.RIGHT_ASSOCIATIVE),
          new PostfixOperator("&", "Function", Precedence.FUNCTION),
          new InfixOperator(">", "Greater", 290, InfixOperator.NONE),
          new PrefixOperator("--", "PreDecrement", Precedence.PREDECREMENT),
          new SubtractOperator("-", "Subtract", Precedence.SUBTRACT,
              InfixOperator.LEFT_ASSOCIATIVE),
          new InfixOperator(":=", "SetDelayed", Precedence.SETDELAYED,
              InfixOperator.RIGHT_ASSOCIATIVE),
          new InfixOperator("|", "Alternatives", Precedence.ALTERNATIVES, InfixOperator.NONE),
          new InfixOperator("+=", "AddTo", Precedence.ADDTO, InfixOperator.RIGHT_ASSOCIATIVE),
          new PostfixOperator("..", "Repeated", Precedence.REPEATED),
          new InfixOperator("/.", "ReplaceAll", Precedence.REPLACEALL,
              InfixOperator.LEFT_ASSOCIATIVE), //
          TAG_SET_OPERATOR, //
          new InfixOperator("@*", "Composition", Precedence.COMPOSITION, InfixOperator.NONE),
          new InfixOperator("/*", "RightComposition", Precedence.RIGHTCOMPOSITION,
              InfixOperator.NONE),
          new InfixOperator("~~", "StringExpression", Precedence.STRINGEXPRESSION,
              InfixOperator.NONE),
          new PatternOperator(":", "Pattern", Precedence.PATTERN, InfixOperator.NONE),
          new InfixOperator("<->", "TwoWayRule", Precedence.TWOWAYRULE,
              InfixOperator.RIGHT_ASSOCIATIVE),
          new InfixOperator("\uF120", "TwoWayRule", Precedence.TWOWAYRULE,
              InfixOperator.RIGHT_ASSOCIATIVE),
          new InfixOperator("\uF3D5", "DirectedEdge", Precedence.DIRECTEDEDGE,
              InfixOperator.RIGHT_ASSOCIATIVE),
          new InfixOperator("\uF3D4", "UndirectedEdge", Precedence.UNDIRECTEDEDGE,
              InfixOperator.RIGHT_ASSOCIATIVE),
          new InfixOperator("\u00B7", "CenterDot", Precedence.CENTERDOT, InfixOperator.NONE), //
          new InfixOperator("\u2299", "CircleDot", Precedence.CIRCLEDOT, InfixOperator.NONE), //
          new InfixOperator("\u2297", "CircleTimes", Precedence.CIRCLETIMES, InfixOperator.NONE), //
          new InfixOperator("\uF3D2", "Distributed", Precedence.DISTRIBUTED, InfixOperator.NONE), //
          new InfixOperator("\u2208", "Element", Precedence.ELEMENT, InfixOperator.NONE), //
          new InfixOperator("\u2209", "NotElement", Precedence.NOTELEMENT, InfixOperator.NONE), //
          new InfixOperator("\u22C2", "Intersection", Precedence.INTERSECTION, InfixOperator.NONE), //
          new InfixOperator("\u2260", "Unequal", Precedence.UNEQUAL, InfixOperator.NONE), //
          new InfixOperator("\u22C0", "Wedge", Precedence.WEDGE, InfixOperator.NONE), //
          new InfixOperator("\uF3DA", "TensorProduct", Precedence.TENSORPRODUCT,
              InfixOperator.NONE),
          new InfixOperator("\u29E6", "Equivalent", Precedence.EQUIVALENT, InfixOperator.NONE),
          new InfixOperator("\uF523", "Implies", Precedence.IMPLIES,
              InfixOperator.RIGHT_ASSOCIATIVE),
          new InfixOperator("\u00b1", "PlusMinus", Precedence.PLUSMINUS,
              InfixOperator.LEFT_ASSOCIATIVE),
          new PrefixOperator("\u00b1", "PlusMinus", Precedence.PLUSMINUS),
          new StarOperator("\u22c6", "Star", Precedence.STAR, InfixOperator.NONE),
          new TildeOperator("~", "§TILDE§", Precedence.TILDE_OPERATOR, InfixOperator.NONE)};
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
      OPERATOR_CHARACTERS = buf.toString();
    }
  }

  static {
    Initializer.init();
  }

  protected final boolean fIgnoreCase;

  /** Create a default ASTNode factory */
  public ASTNodeFactory(boolean ignoreCase) {
    this.fIgnoreCase = ignoreCase;
  }

  public static void addOperator(final Map<String, Operator> operatorMap,
      final Map<String, ArrayList<Operator>> operatorTokenStartSet, final String operatorToken,
      final String headStr, final Operator operator) {
    operatorMap.put(headStr, operator);
    ArrayList<Operator> list = operatorTokenStartSet.get(operatorToken);
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
      final String unicodeOperatorToken, final Operator operator) {
    ArrayList<Operator> list = operatorTokenStartSet.get(unicodeOperatorToken);
    if (list == null) {
      list = new ArrayList<Operator>(2);
      list.add(operator);
      operatorTokenStartSet.put(unicodeOperatorToken, list);
    } else {
      list.add(operator);
    }
  }

  @Override
  public Map<String, Operator> getIdentifier2OperatorMap() {
    return fOperatorMap;
  }

  @Override
  public Operator get(final String identifier) {
    return fOperatorMap.get(identifier);
  }

  @Override
  public Map<String, ArrayList<Operator>> getOperator2ListMap() {
    return fOperatorTokenStartSet;
  }

  @Override
  public List<Operator> getOperatorList(final String key) {
    return fOperatorTokenStartSet.get(key);
  }

  public static InfixOperator createInfixOperator(final String operatorStr, final String headStr,
      final int precedence, final int grouping) {
    if (headStr.equals("Apply")) {
      return new ApplyOperator(operatorStr, headStr, precedence, grouping);
    } else if (headStr.equals("Divide")) {
      return new DivideOperator(operatorStr, headStr, precedence, grouping);
    } else if (headStr.equals("Subtract")) {
      return new SubtractOperator(operatorStr, headStr, precedence, grouping);
    }
    return new InfixOperator(operatorStr, headStr, precedence, grouping);
  }

  public static PrefixOperator createPrefixOperator(final String operatorStr, final String headStr,
      final int precedence) {
    if (headStr.equals("PreMinus")) {
      return new PreMinusOperator(operatorStr, headStr, precedence);
    } else if (headStr.equals("PrePlus")) {
      return new PrePlusOperator(operatorStr, headStr, precedence);
    }
    return new PrefixOperator(operatorStr, headStr, precedence);
  }

  public static PostfixOperator createPostfixOperator(final String operatorStr,
      final String headStr, final int precedence) {
    return new PostfixOperator(operatorStr, headStr, precedence);
  }

  @Override
  public ASTNode createDouble(final String doubleString) {
    return new FloatNode(doubleString);
  }

  @Override
  public FunctionNode createFunction(final SymbolNode head) {
    return new FunctionNode(head);
  }

  @Override
  public FunctionNode createFunction(final SymbolNode head, final ASTNode arg0) {
    return new FunctionNode(head, arg0);
  }

  @Override
  public FunctionNode createFunction(final SymbolNode head, final ASTNode arg1,
      final ASTNode arg2) {
    return new FunctionNode(head, arg1, arg2);
  }

  @Override
  public FunctionNode createFunction(final SymbolNode head, final ASTNode arg1, final ASTNode arg2,
      final ASTNode arg3) {
    return new FunctionNode(head, arg1, arg2, arg3);
  }

  /** Creates a new list with no arguments from the given header object . */
  @Override
  public FunctionNode createAST(final ASTNode headExpr) {
    return new FunctionNode(headExpr);
  }

  @Override
  public FunctionNode unaryAST(final ASTNode head, final ASTNode arg0) {
    return new FunctionNode(head, arg0);
  }

  @Override
  public IntegerNode createInteger(final String integerString, final int numberFormat) {
    return new IntegerNode(integerString, numberFormat);
  }

  @Override
  public IntegerNode createInteger(final int intValue) {
    return new IntegerNode(intValue);
  }

  @Override
  public FractionNode createFraction(final IntegerNode numerator, final IntegerNode denominator) {
    return new FractionNode(numerator, denominator);
  }

  @Override
  public PatternNode createPattern(final SymbolNode patternName, final ASTNode check) {
    return new PatternNode(patternName, check);
  }

  @Override
  public PatternNode createPattern(final SymbolNode patternName, final ASTNode check,
      boolean optional) {
    return new PatternNode(patternName, check, optional);
  }

  @Override
  public PatternNode createPattern(final SymbolNode patternName, final ASTNode check,
      final ASTNode defaultValue) {
    return new PatternNode(patternName, check, defaultValue);
  }

  @Override
  public PatternNode createPattern2(final SymbolNode patternName, final ASTNode check) {
    return new Pattern2Node(patternName, check);
  }

  @Override
  public PatternNode createPattern3(final SymbolNode patternName, final ASTNode check) {
    return new Pattern3Node(patternName, check);
  }

  @Override
  public StringNode createString(final StringBuilder buffer) {
    return new StringNode(buffer.toString());
  }

  @Override
  public SymbolNode createSymbol(final String symbolName, final String context) {
    String name = symbolName;
    if (fIgnoreCase) {
      if (name.length() > 1) {
        name = symbolName.toLowerCase(Locale.US);
      }
    }
    // if (ParserConfig.RUBI_CONVERT_SYMBOLS) {
    // name = toRubiString(name);
    // }
    // if (fIgnoreCase) {
    // return new SymbolNode(symbolName.toLowerCase());
    // }
    return new SymbolNode(name);
  }

  @Override
  public SymbolNode createSymbol(final String symbolName) {
    return createSymbol(symbolName, "");
  }

  @Override
  public boolean isValidIdentifier(String identifier) {
    return true;
  }
}
