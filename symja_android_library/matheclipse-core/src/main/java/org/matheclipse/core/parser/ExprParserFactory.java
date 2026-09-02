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
import org.matheclipse.parser.client.operator.OperatorTable;
import org.matheclipse.parser.client.operator.Precedence;
import org.matheclipse.parser.trie.Trie;
import org.matheclipse.parser.trie.TrieMatch;
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
          // don't use F.Power() here; an IASTMutable has to be returned
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
            // don't use F.Optional() here; an IASTMutable has to be returned
            return F.binaryAST2(S.Optional, F.binaryAST2(S.Pattern, lhs, pn.getSymbol()),
                subPattern);
          }
        }
        return F.binaryAST2(S.Pattern, lhs, rhs);
      }
      // don't use F.Optional() here; an IASTMutable has to be returned
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

  public static final ExprParserFactory MMA_STYLE_FACTORY = new ExprParserFactory();

  public static final ExprParserFactory RELAXED_STYLE_FACTORY = new ExprParserFactory();

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

      fOperatorMap = ParserConfig.TRIE_STRING2OPERATOR_BUILDER.withMatch(TrieMatch.EXACT).build();
      fOperatorTokenStartSet =
          ParserConfig.TRIE_STRING2OPERATORLIST_BUILDER.withMatch(TrieMatch.EXACT).build();

      // Every operator comes from OperatorTable, so that this table and ASTNodeFactory's cannot
      // describe different languages. Only the operators whose expansion is more than "wrap the
      // arguments in a call to the head" need a class of their own; see createOperator.
      StringBuilder operatorCharacters = new StringBuilder(BASIC_OPERATOR_CHARACTERS);
      for (OperatorTable.Row row : OperatorTable.ROWS) {
        final Operator operator = createOperator(row);
        if (row.outputForm) {
          addOperator(fOperatorMap, fOperatorTokenStartSet, row.token, row.head, operator);
        } else {
          // A parse-only row is registered under its token only, exactly as an alias is, so it
          // never wins the head key. See OperatorTable.Row#outputForm.
          addUnicodeOperator(fOperatorMap, fOperatorTokenStartSet, row.token, operator);
        }
        appendOperatorCharacters(operatorCharacters, row.token);
        for (String alias : row.aliases) {
          // The same instance under every spelling: the parser decides whether a chain flattens by
          // comparing operator identity, so a second instance would make the unicode spelling of a
          // flat operator nest where the ASCII one flattens.
          addUnicodeOperator(fOperatorMap, fOperatorTokenStartSet, alias, operator);
          appendOperatorCharacters(operatorCharacters, alias);
        }
      }
      OPERATOR_MATCHER = CharMatcher.anyOf(operatorCharacters.toString());
    }
  }

  /**
   * Build the operator a row describes.
   *
   * <p>
   * Most rows are a plain infix, prefix or postfix operator. The rest either need behaviour the
   * table cannot express - {@code Divide} folds an integer denominator into a Rational,
   * {@code TagSet} looks inside its right operand, {@code ~} collects its arguments and rebuilds
   * them in {@code endFunction} - or are published as constants which other code compares against
   * by identity, so the same instance has to be the one registered.
   */
  private static Operator createOperator(final OperatorTable.Row row) {
    final int grouping = row.grouping.toInfixOperatorConstant();
    switch (row.head) {
      case "Information":
        return row.token.equals("?") ? INFORMATION_SHORT : INFORMATION_LONG;
      case "Apply":
        return row.token.equals("@") ? APPLY_HEAD_OPERATOR : APPLY_OPERATOR;
      case "MapApply":
        return MAPAPPLY_OPERATOR;
      case "Equal":
        return EQUAL_OPERATOR;
      case "NonCommutativeMultiply":
        return NON_COMMUTATIVE_MULTIPLY_OPERATOR;
      case "Power":
        return POWER_OPERATOR;
      case "Set":
        return SET_OPERATOR;
      case "TagSet":
        return TAG_SET_OPERATOR;
      case "Divide":
        return new DivideExprOperator(row.token, row.head, row.precedence, grouping);
      case "Pattern":
        return new PatternExprOperator(row.token, row.head, row.precedence, grouping);
      case "Subtract":
        return new SubtractExprOperator(row.token, row.head, row.precedence, grouping);
      case "PreMinus":
        return new PreMinusExprOperator(row.token, row.head, row.precedence);
      case "PrePlus":
        return new PrePlusExprOperator(row.token, row.head, row.precedence);
      case "\u00a7TILDE\u00a7":
        return new TildeExprOperator(row.token, row.head, row.precedence, grouping);
      default:
        break;
    }
    switch (row.affix) {
      case PREFIX:
        return new PrefixExprOperator(row.token, row.head, row.precedence);
      case POSTFIX:
        return new PostfixExprOperator(row.token, row.head, row.precedence);
      default:
        return new InfixExprOperator(row.token, row.head, row.precedence, grouping);
    }
  }

  /**
   * Record every character a token is spelled with as one the scanner may find inside an operator.
   * The ASCII tokens are already covered by {@link IParserFactory#BASIC_OPERATOR_CHARACTERS}; this
   * is what makes the unicode spellings reachable at all.
   */
  private static void appendOperatorCharacters(StringBuilder buf, String token) {
    for (int i = 0; i < token.length(); i++) {
      if (buf.indexOf(token.substring(i, i + 1)) < 0) {
        buf.append(token.charAt(i));
      }
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  public static void addOperator(final Map<String, Operator> operatorMap,
      final Map<String, ArrayList<Operator>> operatorTokenStartSet, final String operatorToken,
      final String headStr, final Operator operator) {
    ArrayList<Operator> list;
    // First row wins the name key. Several heads have more than one operator - Function is the
    // postfix "&" and the infix "|->", Information is "?" and "??" - and this map is what
    // OutputFormFactory resolves an operator form through, so which one wins decides how the
    // expression prints. Rows are ordered by token, so "&" is reached before "|->" and Function
    // keeps printing as "f&" rather than "|->".
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
        && !(previous instanceof InfixExprOperator && !(operator instanceof InfixExprOperator));
    if (!keepPrevious) {
      operatorMap.put(headStr, operator);
    }
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

  /**
   * Look the token up directly, without materializing it. The trie compares element by element
   * through its sequencer, so any {@link CharSequence} works as a key.
   */
  @Override
  public List<Operator> getOperatorList(final CharSequence key) {
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
