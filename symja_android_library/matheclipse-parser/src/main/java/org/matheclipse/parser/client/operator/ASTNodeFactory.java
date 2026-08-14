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


  public static final ApplyOperator APPLY_HEAD_OPERATOR =
      new ApplyOperator("@", "Apply", Precedence.APPLY_HEAD, InfixOperator.RIGHT_ASSOCIATIVE);
  public static final ApplyOperator APPLY_OPERATOR =
      new ApplyOperator("@@", "Apply", Precedence.APPLY, InfixOperator.RIGHT_ASSOCIATIVE);
  public static final ApplyOperator MAPAPPLY_OPERATOR =
      new ApplyOperator("@@@", "MapApply", Precedence.MAPAPPLY, InfixOperator.RIGHT_ASSOCIATIVE);

  public static final TagSetOperator TAG_SET_OPERATOR =
      new TagSetOperator("/:", "TagSet", Precedence.TAGSET, InfixOperator.NONE);


  public static final ASTNodeFactory MMA_STYLE_FACTORY = new ASTNodeFactory(false);

  public static final ASTNodeFactory RELAXED_STYLE_FACTORY = new ASTNodeFactory(true);

  /** */
  private static Trie<String, Operator> fOperatorMap;

  /**
   * Every operator a head expands from, in table order. {@code fOperatorMap} keeps one winner per
   * head, which is enough for the parsers but not for output: several heads have more than one
   * operator, and which one a printed expression should use depends on its arity.
   */
  private static java.util.HashMap<String, ArrayList<Operator>> fHeadToOperators;

  /** */
  private static Trie<String, ArrayList<Operator>> fOperatorTokenStartSet;

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      fOperatorMap = ParserConfig.TRIE_STRING2OPERATOR_BUILDER.withMatch(TrieMatch.EXACT).build();
      fOperatorTokenStartSet =
          ParserConfig.TRIE_STRING2OPERATORLIST_BUILDER.withMatch(TrieMatch.EXACT).build();

      // Every operator comes from OperatorTable, the same rows ExprParserFactory builds from, so
      // that the two parsers cannot describe different languages.
      StringBuilder operatorCharacters = new StringBuilder(BASIC_OPERATOR_CHARACTERS);
      fHeadToOperators = new java.util.HashMap<>();
      for (OperatorTable.Row row : OperatorTable.ROWS) {
        final Operator operator = createOperator(row);
        addOperator(fOperatorMap, fOperatorTokenStartSet, row.token, row.head, operator);
        fHeadToOperators.computeIfAbsent(row.head, k -> new ArrayList<>(2)).add(operator);
        appendOperatorCharacters(operatorCharacters, row.token);
        for (String alias : row.aliases) {
          // The same instance under every spelling - the parser decides whether a chain flattens by
          // comparing operator identity.
          addUnicodeOperator(fOperatorMap, fOperatorTokenStartSet, alias, operator);
          appendOperatorCharacters(operatorCharacters, alias);
        }
      }
      OPERATOR_CHARACTERS = operatorCharacters.toString();
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
    // First row wins the name key: several heads have more than one operator - Function is
    // the postfix "&" and the infix "|->" - and this map is what OutputFormFactory resolves an
    // operator form through, so which one wins decides how the expression prints.
    // A head can have more than one operator - Function is the postfix "&" and the infix
    // "|->", PlusMinus is a prefix and an infix "\u00b1" - but this map holds one per name, and it
    // is
    // what OutputFormFactory resolves an operator form through. Prefer the prefix or postfix
    // reading, which is the one that prints as an operator for a one-argument call; among readings
    // of the same kind the later row wins, as it did when the table was an array.
    Operator previous = operatorMap.get(headStr);
    // First row wins, except that a prefix or postfix reading replaces an infix one: that is the
    // reading which prints as an operator for a one-argument call. Keeping the first otherwise
    // matters for Unequal, which has both "!=" and "\u2260" - the ASCII spelling is the one Symja
    // prints, and it only stayed so before because the "\u2260" row was filed under a misspelled
    // head.
    boolean keepPrevious = previous != null
        && !(previous instanceof InfixOperator && !(operator instanceof InfixOperator));
    if (!keepPrevious) {
      operatorMap.put(headStr, operator);
    }
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

  /**
   * All operators registered for a head, in table order, or <code>null</code> if the head has none.
   * Unlike {@link #get(String)} this loses nothing when a head has several operators - Function has
   * the postfix <code>&amp;</code> and the infix <code>|-&gt;</code>, Information has
   * <code>?</code> and <code>??</code>.
   */
  public List<Operator> getOperatorsByHead(final String head) {
    return fHeadToOperators.get(head);
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

  /**
   * Look the token up directly, without materializing it. The trie compares element by element
   * through its sequencer, so any {@link CharSequence} works as a key.
   */
  @Override
  public List<Operator> getOperatorList(final CharSequence key) {
    return fOperatorTokenStartSet.get(key);
  }

  /**
   * Build the operator a row describes. Mirrors {@code ExprParserFactory#createOperator}: most rows
   * are a plain infix, prefix or postfix operator, and the rest either carry behaviour the table
   * cannot express or are published as constants which other code compares against by identity.
   */
  private static Operator createOperator(final OperatorTable.Row row) {
    final int grouping = row.grouping.toInfixOperatorConstant();
    switch (row.head) {
      case "Apply":
        return row.token.equals("@") ? APPLY_HEAD_OPERATOR : APPLY_OPERATOR;
      case "MapApply":
        return MAPAPPLY_OPERATOR;
      case "TagSet":
        return TAG_SET_OPERATOR;
      case "MessageName":
        return new MessageNameOperator(row.token, row.head, row.precedence, grouping);
      case "Divide":
        return new DivideOperator(row.token, row.head, row.precedence, grouping);
      case "Star":
        return new StarOperator(row.token, row.head, row.precedence, grouping);
      case "Pattern":
        return new PatternOperator(row.token, row.head, row.precedence, grouping);
      case "Subtract":
        return new SubtractOperator(row.token, row.head, row.precedence, grouping);
      case "PreMinus":
        return new PreMinusOperator(row.token, row.head, row.precedence);
      case "PrePlus":
        return new PrePlusOperator(row.token, row.head, row.precedence);
      case "\u00a7TILDE\u00a7":
        return new TildeOperator(row.token, row.head, row.precedence, grouping);
      default:
        break;
    }
    switch (row.affix) {
      case PREFIX:
        return new PrefixOperator(row.token, row.head, row.precedence);
      case POSTFIX:
        return new PostfixOperator(row.token, row.head, row.precedence);
      default:
        return new InfixOperator(row.token, row.head, row.precedence, grouping);
    }
  }

  /** Record every character a token is spelled with as one the scanner may find in an operator. */
  private static void appendOperatorCharacters(StringBuilder buf, String token) {
    for (int i = 0; i < token.length(); i++) {
      if (buf.indexOf(token.substring(i, i + 1)) < 0) {
        buf.append(token.charAt(i));
      }
    }
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
