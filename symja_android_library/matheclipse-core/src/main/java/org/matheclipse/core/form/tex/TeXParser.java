package org.matheclipse.core.form.tex;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.AbortException;
import org.matheclipse.core.eval.util.Lambda;
import org.matheclipse.core.expression.BuiltInDummy;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.operator.Operator;
import org.matheclipse.parser.client.operator.Precedence;
import org.matheclipse.parser.trie.TrieBuilder;
import org.matheclipse.parser.trie.TrieMatch;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uk.ac.ed.ph.snuggletex.InputError;
import uk.ac.ed.ph.snuggletex.SnuggleEngine;
import uk.ac.ed.ph.snuggletex.SnuggleInput;
import uk.ac.ed.ph.snuggletex.SnuggleSession;

public class TeXParser {
  private static final Logger LOGGER = LogManager.getLogger();

  static class BinaryOperator extends Operator {
    BiFunction<IExpr, IExpr, IExpr> binaryFunction;

    public BinaryOperator(
        final String oper,
        final String functionName,
        final int precedence,
        BiFunction<IExpr, IExpr, IExpr> binaryFunction) {
      super(oper, functionName, precedence);
      this.binaryFunction = binaryFunction;
    }

    public IExpr createFunction(final IExpr lhs, final IExpr rhs) {
      return binaryFunction.apply(lhs, rhs);
    }
  }

  static class PostfixOperator extends Operator {
    Function<IExpr, IExpr> function;

    public PostfixOperator(
        final String oper,
        final String functionName,
        final int precedence,
        Function<IExpr, IExpr> function) {
      super(oper, functionName, precedence);
      this.function = function;
    }

    public IExpr createFunction(final IExpr expr) {
      return function.apply(expr);
    }
  }

  static class PrefixOperator extends Operator {
    Function<IExpr, IExpr> function;

    public PrefixOperator(
        final String oper,
        final String functionName,
        final int precedence,
        Function<IExpr, IExpr> function) {
      super(oper, functionName, precedence);
      this.function = function;
    }

    public IExpr createFunction(final IExpr expr) {
      return function.apply(expr);
    }
  }

  static final boolean SHOW_UNICODE = false;

  static final PrefixOperator[] PREFIX_OPERATORS = { //
    new PrefixOperator("+", "Plus", 670, (x) -> x), //
    new PrefixOperator("-", "Minus", 485, (x) -> F.Negate(x)), //
    new PrefixOperator("\u00ac", "Not", 230, (x) -> F.Not(x)), //
  };

  static final PostfixOperator[] POSTFIX_OPERATORS = { //
    new PostfixOperator("!", "Factorial", Precedence.FACTORIAL, (x) -> F.Factorial(x)), //
  };

  static final BinaryOperator[] BINARY_OPERATORS = { //
    new BinaryOperator("=", "Equal", Precedence.EQUAL, (lhs, rhs) -> F.Equal(lhs, rhs)), //
    new BinaryOperator(
        "\u2264", "LessEqual", Precedence.EQUAL, (lhs, rhs) -> F.LessEqual(lhs, rhs)), //
    new BinaryOperator(
        "\u2265", "GreaterEqual", Precedence.EQUAL, (lhs, rhs) -> F.GreaterEqual(lhs, rhs)), //
    new BinaryOperator("<", "Less", Precedence.EQUAL, (lhs, rhs) -> F.Less(lhs, rhs)), //
    new BinaryOperator(">", "Greater", Precedence.EQUAL, (lhs, rhs) -> F.Greater(lhs, rhs)), //
    new BinaryOperator("\u2227", "And", 215, (lhs, rhs) -> F.And(lhs, rhs)), //
    new BinaryOperator("\u2228", "Or", 213, (lhs, rhs) -> F.Or(lhs, rhs)), //
    new BinaryOperator("\u21d2", "Implies", 120, (lhs, rhs) -> F.Implies(lhs, rhs)), // Rightarrow
    new BinaryOperator("\u2192", "Rule", 120, (lhs, rhs) -> F.Rule(lhs, rhs)), // rightarrow
    new BinaryOperator(
        "\u21d4", "Equivalent", 120, (lhs, rhs) -> F.Equivalent(lhs, rhs)), // Leftrightarrow
    new BinaryOperator("\u2261", "Equivalent", 120, (lhs, rhs) -> F.Equivalent(lhs, rhs)), // equiv
    new BinaryOperator("+", "Plus", Precedence.PLUS, (lhs, rhs) -> F.Plus(lhs, rhs)), //
    new BinaryOperator("-", "Subtract", Precedence.PLUS, (lhs, rhs) -> F.Subtract(lhs, rhs)), //
    new BinaryOperator("*", "Times", Precedence.TIMES, (lhs, rhs) -> F.Times(lhs, rhs)), //
    // x multiplication sign
    new BinaryOperator("\u00d7", "Times", Precedence.TIMES, (lhs, rhs) -> F.Times(lhs, rhs)), //
    // InvisibleTimes
    new BinaryOperator("\u2062", "Times", Precedence.TIMES, (lhs, rhs) -> F.Times(lhs, rhs)), //
    new BinaryOperator("/", "Divide", Precedence.DIVIDE, (lhs, rhs) -> F.Divide(lhs, rhs)), //
    // &#xf7; Division sign
    new BinaryOperator("\u00f7", "Divide", Precedence.DIVIDE, (lhs, rhs) -> F.Divide(lhs, rhs)), //
    new BinaryOperator("\u2208", "Element", 250, (lhs, rhs) -> F.Element(lhs, rhs)), //
  };

  private static Map<String, IExpr> UNICODE_OPERATOR_MAP;

  private static Map<String, IExpr> FUNCTION_HEADER_MAP;
  private static Map<String, BinaryOperator> BINARY_OPERATOR_MAP;
  private static Map<String, PrefixOperator> PREFIX_OPERATOR_MAP;
  private static Map<String, PostfixOperator> POSTFIX_OPERATOR_MAP;

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      UNICODE_OPERATOR_MAP =
          Config.TRIE_STRING2EXPR_BUILDER.withMatch(TrieMatch.EXACT).build(); // Tries.forStrings();
      UNICODE_OPERATOR_MAP.put("\u2218", S.Degree);
      UNICODE_OPERATOR_MAP.put("\u00B0", S.Degree);
      UNICODE_OPERATOR_MAP.put("\u222b", S.Integrate);
      UNICODE_OPERATOR_MAP.put("\u2211", S.Sum);
      UNICODE_OPERATOR_MAP.put("\u220f", S.Product);
      UNICODE_OPERATOR_MAP.put("\u03c0", S.Pi);
      UNICODE_OPERATOR_MAP.put("\u221e", F.CInfinity);
      UNICODE_OPERATOR_MAP.put("\u2148", F.CI); // double-struck italic letter i
      UNICODE_OPERATOR_MAP.put("\u2149", F.CI); // double-struck italic letter j
      UNICODE_OPERATOR_MAP.put("\u2107", S.E); // euler's constant

      FUNCTION_HEADER_MAP =
          Config.TRIE_STRING2EXPR_BUILDER.withMatch(TrieMatch.EXACT).build(); // Tries.forStrings();
      FUNCTION_HEADER_MAP.put("ln", S.Log);
      FUNCTION_HEADER_MAP.put("lim", S.Limit);

      TrieBuilder<String, BinaryOperator, ArrayList<BinaryOperator>> binaryBuilder =
          TrieBuilder.create();
      BINARY_OPERATOR_MAP = binaryBuilder.withMatch(TrieMatch.EXACT).build(); // Tries.forStrings();
      for (int i = 0; i < BINARY_OPERATORS.length; i++) {
        String headStr = BINARY_OPERATORS[i].getOperatorString();
        BINARY_OPERATOR_MAP.put(headStr, BINARY_OPERATORS[i]);
      }

      TrieBuilder<String, PrefixOperator, ArrayList<PrefixOperator>> prefixBuilder =
          TrieBuilder.create();
      PREFIX_OPERATOR_MAP = prefixBuilder.withMatch(TrieMatch.EXACT).build(); // Tries.forStrings();
      for (int i = 0; i < PREFIX_OPERATORS.length; i++) {
        String headStr = PREFIX_OPERATORS[i].getOperatorString();
        PREFIX_OPERATOR_MAP.put(headStr, PREFIX_OPERATORS[i]);
      }

      TrieBuilder<String, PostfixOperator, ArrayList<PostfixOperator>> postfixBuilder =
          TrieBuilder.create();
      POSTFIX_OPERATOR_MAP =
          postfixBuilder.withMatch(TrieMatch.EXACT).build(); // Tries.forStrings();
      for (int i = 0; i < POSTFIX_OPERATORS.length; i++) {
        String headStr = POSTFIX_OPERATORS[i].getOperatorString();
        POSTFIX_OPERATOR_MAP.put(headStr, POSTFIX_OPERATORS[i]);
      }
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private static String toUnicodeString(final String unicodeInput, final String inputEncoding) {
    final StringBuilder unicodeStringBuilder = new StringBuilder();
    String unicodeString = null;

    try {
      final String utf8String = new String(unicodeInput.getBytes(inputEncoding), "UTF-8");
      String hexValueString = null;
      int hexValueLength = 0;
      for (int i = 0; i < utf8String.length(); i++) {
        hexValueString = Integer.toHexString(utf8String.charAt(i));
        hexValueLength = hexValueString.length();
        if (hexValueLength < 4) {
          for (int j = 0; j < (4 - hexValueLength); j++) {
            hexValueString = "0" + hexValueString;
          }
        }
        unicodeStringBuilder.append("\\u");
        unicodeStringBuilder.append(hexValueString);
      }
      unicodeString = unicodeStringBuilder.toString();
    } catch (final UnsupportedEncodingException e) {
      LOGGER.error("TeXParser.toUnicodeString() failed", e);
    }
    return unicodeString;
  }

  int counter = 0;
  EvalEngine fEngine;

  public TeXParser(EvalEngine engine) {
    fEngine = engine;
  }

  private IExpr convert(NodeList list, int[] position, IExpr lhs, int precedence) {
    return convert(list, position, list.getLength(), lhs, precedence);
  }

  private IExpr convert(NodeList list, int[] position, int end, IExpr lhs, int precedence) {
    final int listSize = list.getLength();
    if (end > 1) {
      if (lhs == null) {
        Node lhsNode = list.item(position[0]++);
        String name = lhsNode.getNodeName();
        if (name.equals("mo")) {
          String text = lhsNode.getTextContent();
          PrefixOperator operator = PREFIX_OPERATOR_MAP.get(text);
          if (operator != null) {
            int currPrec = operator.getPrecedence();
            IExpr x = convert(list, position, end, null, currPrec);
            lhs = operator.createFunction(x);
          }
        }
        if (lhs == null) {
          lhs = toHeadExpr(lhsNode, list, position, precedence);
          if (position[0] >= listSize) {
            return lhs;
          }
        }

        int attribute = ISymbol.NOATTRIBUTE;
        if (lhs.isSymbol()) {
          attribute = ((ISymbol) lhs).getAttributes();
        }
        if ((attribute & ISymbol.CONSTANT) != ISymbol.CONSTANT) {
          if ((lhs.isFunction() || lhs.isSymbol() || lhs.isDerivative() != null)
              && //
              position[0] < listSize) {
            boolean isNumericFunction =
                ((attribute & ISymbol.NUMERICFUNCTION) == ISymbol.NUMERICFUNCTION);
            Node arg2 = list.item(position[0]);
            if (arg2.getNodeName().equals("mfenced")) {
              position[0]++;
              int[] position2 = new int[] {0};
              NodeList childNodes = arg2.getChildNodes();
              IExpr args = convertArgs(childNodes, position2);
              if (args.isSequence()) {
                ((IASTMutable) args).set(0, lhs);
                return args;
              }
              lhs = F.unaryAST1(lhs, args);
              if (position[0] == listSize) {
                return lhs;
              }
            } else if (isNumericFunction
                || (lhs.isBuiltInSymbol() && !(lhs instanceof BuiltInDummy))
                || lhs.isFunction()) {
              if (lhs.equals(S.Integrate)) {
                ISymbol test = F.Dummy("test");
                return integrate(list, position, test, test);
              }
              IExpr args = convert(list, position, end, null, 0);
              if (args.isSequence()) {
                ((IASTMutable) args).set(0, lhs);
                return args;
              }
              if (lhs.isFunction() && lhs.size() == 2) {
                IExpr temp = Lambda.replaceSlots(lhs.first(), F.List(args));
                if (temp.isPresent()) {
                  lhs = temp;
                }
              } else {
                lhs = F.unaryAST1(lhs, args);
              }
      if (position[0] == listSize) {
        return lhs;
      }
            }
          }
        }
      }
      IExpr result = lhs;
      int currPrec = 0;
      while (position[0] < end) {
        Node op = list.item(position[0]);
        String name = op.getNodeName();
        if (name.equals("mo")) {
          String text = op.getTextContent();
          if (SHOW_UNICODE) {
            System.out.println("mo: " + text + " - " + toUnicodeString(text, "UTF-8"));
          }
          BinaryOperator binaryOperator = BINARY_OPERATOR_MAP.get(text);
          if (binaryOperator != null) {
            currPrec = binaryOperator.getPrecedence();
            if (precedence >= currPrec) {
              return result;
            }
            position[0]++;
            IExpr rhs = convert(list, position, end, null, currPrec);
            result = binaryOperator.createFunction(result, rhs);
            continue;
          } else {
            PostfixOperator postfixOperator = POSTFIX_OPERATOR_MAP.get(text);
            if (postfixOperator != null) {
              currPrec = postfixOperator.getPrecedence();
              if (precedence >= currPrec) {
                return result;
              }
              result = postfixOperator.createFunction(lhs);
              position[0]++;
              continue;
            }
          }
          throw new AbortException();
        } else if (name.equals("mspace")) {
          position[0]++;
          continue;
        }

        // try to build a Times(...) expression
        currPrec = Precedence.TIMES;
        IExpr rhs = convert(list, position, end, null, currPrec);
        // invisible times?
        result = F.Times(lhs, rhs);
      }
      if (result.isPresent() && position[0] >= end) {
        return result;
      }
    }

    return convertArgs(list, position);
  }

  public IExpr convertArgs(NodeList list, int[] position) {
    IASTAppendable ast = F.Sequence();
    for (int i = 0; i < list.getLength(); i++) {
      Node temp = list.item(i);
      IExpr ex = toExpr(temp);
      ast.append(ex);
    }
    if (ast.size() == 2) {
      return ast.arg1();
    }
    if (ast.size() > 1) {
      if (ast.arg1().isBuiltInSymbol()) {
        return F.unaryAST1(ast.arg1(), ast.arg2());
      }
    }
    return ast;
  }

  /**
   * Create an identifier from multiple <code>&lt;mi&gt;</code> expressions.
   *
   * @param list
   * @param position
   * @return
   */
  private ISymbol identifier(NodeList list, int[] position) {
    StringBuilder buf = new StringBuilder();
    boolean evaled = false;
    while (position[0] < list.getLength()) {
      Node temp = list.item(position[0]);
      if (temp.getNodeName().equals("mi")) {
        position[0]++;
        buf.append(temp.getTextContent());
        evaled = true;
        continue;
      }
      break;
    }
    if (evaled) {
      return F.$s(buf.toString());
    }
    throw new AbortException();
  }

  /**
   * Get the position there the <code>d</code> of the <code>dx</code> or <code>dx/expr</code> in the
   * integral definition starts and ends and create an <code>F.Integrate(...,x)</code> or <code>
   * F.Integrate()...,{x,a,b})</code> expression.
   *
   * @param parentList
   * @param position
   * @param dummySymbol
   * @param symbolOrList
   * @return
   */
  private IExpr integrate(
      NodeList parentList, int[] position, ISymbol dummySymbol, IExpr symbolOrList) {
    ISymbol x = null;
    IExpr dxValue = F.C1;
    int dxStart = -1;
    int dxEnd = -1;

    int[] dxPosition1 = new int[] {position[0]};
    while (dxPosition1[0] < parentList.getLength()) {
      Node nd = parentList.item(dxPosition1[0]++);
      if (nd.getNodeName().equals("mi")
          && //
          nd.getTextContent().equals("d")) {
        if (dxPosition1[0] < parentList.getLength()) {
          nd = parentList.item(dxPosition1[0]);
          if (nd.getNodeName().equals("mi")) {
            dxStart = dxPosition1[0];
            ISymbol x1 = identifier(parentList, dxPosition1);
            dxEnd = dxPosition1[0];
            x = x1;
            break;
          }
        }
      } else if (nd.getNodeName().equals("mfrac")) {

        IExpr frac = mfrac(nd.getChildNodes());
        if (frac.isTimes() && frac.first().isSymbol()) {
          ISymbol d = (ISymbol) frac.first();
          String dStr = d.getSymbolName();
          if (dStr.startsWith("d")) {
            // dx/x
            dxStart = dxPosition1[0];
            dxEnd = dxPosition1[0];
            x = F.$s(dStr.substring(1));
            dxValue = frac.second();
            break;
          }
        }
      }
    }
    if (x == null) {
      throw new AbortException();
    }

    dxStart--;
    if (dxStart > position[0]) {
      IExpr arg1 = convert(parentList, position, dxStart, null, 0);
      position[0] = dxEnd;
      arg1 = F.subs(arg1, dummySymbol, x);
      symbolOrList = F.subs(symbolOrList, dummySymbol, x);
      return F.binaryAST2(S.Integrate, arg1, symbolOrList);
    } else if (dxStart == position[0]) {
      position[0] = dxEnd;
      symbolOrList = F.subs(symbolOrList, dummySymbol, x);
      return F.binaryAST2(S.Integrate, dxValue, symbolOrList);
    }
    throw new AbortException();
  }

  private IExpr mfrac(NodeList list) {
    IASTAppendable frac = F.TimesAlloc(2);
    if (list.getLength() > 0) {
      Node temp = list.item(0);
      frac.append(toExpr(temp));
      if (1 < list.getLength()) {
        temp = list.item(1);
        frac.append(F.Power(toExpr(temp), -1));
      } else {
        throw new AbortException();
      }
    }
    if (frac.isTimes()
        && frac.first().isSymbol()
        && frac.size() == 3
        && frac.second().isPowerReciprocal()) {
      ISymbol d = (ISymbol) frac.first();
      if (d.getSymbolName().equals("d")) {
        IExpr dDenominator = frac.second().first();
        if (dDenominator.isSymbol()) {
          String str = ((ISymbol) dDenominator).getSymbolName();
          if (str.startsWith("d")) {
            str = str.substring(1);
            return F.Function(F.D(F.Slot1, F.$s(str)));
          }
        }
      }
    }
    return frac;
  }

  private IExpr mi(Node node) {
    String text = node.getTextContent();
    if (text.length() == 1) {
      if (node.hasAttributes()) {
        Node value = node.getAttributes().getNamedItem("mathvariant");
        if (value != null) {
          if (value.getTextContent().equals("double-struck")) {
            if (text.equals("B")) {
              return S.Booleans;
            } else if (text.equals("C")) {
              return S.Complexes;
            } else if (text.equals("P")) {
              return S.Primes;
            } else if (text.equals("Q")) {
              return S.Rationals;
            } else if (text.equals("Z")) {
              return S.Integers;
            } else if (text.equals("R")) {
              return S.Reals;
            }
          }
        }
      }
      if (SHOW_UNICODE) {
        System.out.println("mi: " + text + " - " + toUnicodeString(text, "UTF-8"));
      }
      IExpr x = UNICODE_OPERATOR_MAP.get(text);
      if (x != null) {
        return x;
      }
    }
    IExpr x = FUNCTION_HEADER_MAP.get(text);
    if (x != null) {
      return x;
    }
    return F.$s(text);
  }

  private IExpr mn(Node node) {
    try {
      String text = node.getTextContent();
      if (text.contains(".") || text.contains("E")) {
        return F.num(text);
      }
      return F.integer(text, 10);
    } catch (RuntimeException rex) {
      LOGGER.debug("TeXParser.mn() failed", rex);
    }
    throw new AbortException();
  }

  private IExpr mo(Node node) {
    String text = node.getTextContent();
    if (text.length() == 1) {
      if (SHOW_UNICODE) {
        System.out.println("mo: " + text + " - " + toUnicodeString(text, "UTF-8"));
      }
      IExpr x = UNICODE_OPERATOR_MAP.get(text);
      if (x != null) {
        return x;
      }
    }
    return F.$s(text);
  }

  private IExpr mrow(Node node) {
    NodeList list = node.getChildNodes();
    boolean isSymbol = true;
    for (int i = 0; i < list.getLength(); i++) {
      Node temp = list.item(i);
      String n = temp.getNodeName();
      if (!n.equals("mi") || !(temp instanceof Element)) {
        isSymbol = false;
        break;
      }
    }
    if (isSymbol) {
      StringBuilder buf = new StringBuilder();
      for (int i = 0; i < list.getLength(); i++) {
        Node temp = list.item(i);
        buf.append(temp.getTextContent());
      }
      return F.$s(buf.toString());
    }
    int[] position = new int[] {0};
    return convert(list, position, null, 0);
  }

  private IExpr msqrt(NodeList list) {
    if (list.getLength() > 0) {
      Node temp = list.item(0);
      return F.Power(toExpr(temp), F.C1D2);
    }
    return F.NIL;
  }

  private IExpr msubsup(NodeList list, NodeList parentList, int[] position, int precedence) {
    // \\int_0^\\infty a dx
    if (list.getLength() > 0) {
      Node arg0 = list.item(0);
      IExpr head = toExpr(arg0);
      if (head.isBuiltInSymbol()) {
        ISymbol dummySymbol = F.Dummy("msubsup$" + counter++);
        IExpr arg2 = dummySymbol;
        if (list.getLength() >= 2) {
          Node arg1 = list.item(1);
          IExpr a1 = toExpr(arg1);
          if (list.getLength() == 3) {
            IExpr a2 = toExpr(list.item(2));
            arg2 = F.List(dummySymbol, a1, a2);
          } else if (list.getLength() == 2) {
            arg2 = F.List(dummySymbol, a1);
          }
        }
        if (parentList != null) {
          while (position[0] < parentList.getLength()) {
            if (head.equals(S.Integrate)) {
              return integrate(parentList, position, dummySymbol, arg2);
            } else {
              IExpr arg1 = convert(parentList, position, null, Integer.MAX_VALUE);
              return F.binaryAST2(head, arg1, arg2);
            }
          }
        }
      }
    }
    if (list.getLength() == 3) {
      Node node = list.item(0);
      IExpr a1 = toExpr(node);
      node = list.item(1);
      IExpr a2 = toExpr(node);
      node = list.item(2);
      IExpr a3 = toExpr(node);
      return F.ternaryAST3(S.Subsuperscript, a1, a2, a3);
    }
    throw new AbortException();
  }

  private IExpr msub(NodeList list) {
    if (list.getLength() == 2) {
      Node arg1 = list.item(0);
      Node arg2 = list.item(1);

      IExpr a1 = toExpr(arg1);
      IExpr a2 = toExpr(arg2);
      if (a1.equals(S.Limit)) {
        // Limit(#,a2)&
        return F.Function(F.Limit(F.Slot1, a2));
      }
      return F.binaryAST2(S.Subscript, a1, a2);
    }
    throw new AbortException();
  }

  private IExpr msup(NodeList list) {
    if (list.getLength() == 2) {
      Node arg1 = list.item(0);
      Node arg2 = list.item(1);
      return power(arg1, arg2);
    }
    throw new AbortException();
  }

  private IExpr munderover(NodeList list, NodeList parentList, int[] position, int precedence) {
    if (list.getLength() > 0) {
      Node arg0 = list.item(0);
      IExpr head = toExpr(arg0);
      if (head.isBuiltInSymbol()) {
        ISymbol sym = F.Dummy("munderover$" + counter++);
        IExpr arg2 = sym;
        if (list.getLength() >= 2) {
          Node arg1 = list.item(1);
          IExpr a1 = toExpr(arg1);
          if (a1.isEqual() && a1.first().isSymbol()) {
            sym = (ISymbol) a1.first();
            arg2 = sym;
            a1 = a1.second();
          }
          if (list.getLength() == 3) {
            IExpr a2 = toExpr(list.item(2));
            arg2 = F.List(sym, a1, a2);
          } else if (list.getLength() == 2) {
            arg2 = F.List(sym, a1);
          }
        }

        if (parentList != null && position[0] < parentList.getLength()) {
          IExpr arg1 = convert(parentList, position, null, Integer.MAX_VALUE);
          return F.binaryAST2(head, arg1, arg2);
        }
      }
    }
    // if (list.getLength() == 3) {
    // Node node = list.item(0);
    // IExpr a1 = toExpr(node);
    // node = list.item(1);
    // IExpr a2 = toExpr(node);
    // node = list.item(2);
    // IExpr a3 = toExpr(node);
    // return F.ternaryAST3(F.Underoverscript, a1, a2, a3);
    // }
    throw new AbortException();
  }

  /**
   * Override this method if you need special handling for <code>arg1 ^ arg2</code>.
   *
   * @param arg1
   * @param arg2
   * @return
   */
  public IExpr power(Node arg1, Node arg2) {
    IExpr a1 = toExpr(arg1);
    String name2 = arg2.getNodeName();
    String text2 = arg2.getTextContent();
    if (name2.equals("mi") && text2.equals("'")) {
      return F.unaryAST1(F.Derivative(F.C1), a1);
    }
    IExpr a2 = toExpr(arg2);
    if (a1.isBuiltInSymbol() && a2.isMinusOne()) {
      IExpr value = F.UNARY_INVERSE_FUNCTIONS.get(a1);
      if (value != null) {
        // typically Sin^(-1) -> ArcSin or similar...
        return value;
      }
    } else if (a2.equals(S.Degree)) {
      // case \sin 30 ^ { \circ } ==> Sin(30*Degree)
      return F.Times(a1, a2);
    }
    return F.Power(a1, a2);
  }

  private IExpr toExpr(Node node) {
    int[] position = new int[] {0};
    String name = node.getNodeName();
    if (name.equals("mi")) {
      return mi(node);
    } else if (name.equals("mo")) {
      return mo(node);
    } else if (name.equals("mn")) {
      return mn(node);
    } else if (name.equals("math")) {
      return convert(node.getChildNodes(), position, null, 0);
    } else if (name.equals("mfrac")) {
      return mfrac(node.getChildNodes());
    } else if (name.equals("msqrt")) {
      return msqrt(node.getChildNodes());
    } else if (name.equals("msub")) {
      return msub(node.getChildNodes());
    } else if (name.equals("msup")) {
      return msup(node.getChildNodes());
    } else if (name.equals("msubsup")) {
      return msubsup(node.getChildNodes(), null, position, 0);
    } else if (name.equals("munderover")) {
      return munderover(node.getChildNodes(), null, position, 0);
    } else if (name.equals("mrow")) {
      return mrow(node);
    } else if (name.equals("mfenced")) {
      return convertArgs(node.getChildNodes(), position);
    }
    // String n = node.getNodeName();
    // System.out.println(n.toString());
    NodeList list = node.getChildNodes();
    return convert(list, position, null, 0);
  }

  /**
   * Convert a tex math formula into a Symja expression. The SnuggleTeX engine first converts the
   * TeX expression in a MathML expression. This MathML expression is then converted to Symja.
   *
   * @param texStr the tex math formula
   * @return
   */
  public IExpr toExpression(String texStr) {
    SnuggleEngine engine = new SnuggleEngine();
    SnuggleSession session = engine.createSession();
    session.getConfiguration().setFailingFast(true);

    SnuggleInput input = new SnuggleInput("$$ " + texStr + " $$");
    try {
      if (session.parseInput(input)) {
        NodeList nodes = session.buildDOMSubtree();
        int[] position = new int[] {0};
        return convert(nodes, position, null, 0);
      }
      List<InputError> errors = session.getErrors();
      for (int i = 0; i < errors.size(); i++) {
        LOGGER.log(fEngine.getLogLevel(), errors.get(i));
      }
    } catch (Exception e) {
      LOGGER.debug("TeXParser.toExpression() failed", e);
    }
    return S.$Aborted;
  }

  private IExpr toHeadExpr(Node node, NodeList parentList, int[] position, int precedence) {
    String name = node.getNodeName();
    if (name.equals("mi")) {
      return mi(node);
    } else if (name.equals("mo")) {
      return mo(node);
    } else if (name.equals("mn")) {
      return mn(node);
    } else if (name.equals("math")) {
      return convert(node.getChildNodes(), position, null, 0);
    } else if (name.equals("mfrac")) {
      return mfrac(node.getChildNodes());
    } else if (name.equals("msqrt")) {
      return msqrt(node.getChildNodes());
    } else if (name.equals("msub")) {
      return msub(node.getChildNodes());
    } else if (name.equals("msup")) {
      return msup(node.getChildNodes());
    } else if (name.equals("msubsup")) {
      return msubsup(node.getChildNodes(), parentList, position, precedence);
    } else if (name.equals("munderover")) {
      return munderover(node.getChildNodes(), parentList, position, precedence);
    } else if (name.equals("mrow")) {
      return mrow(node);
    } else if (name.equals("mfenced")) {
      return convertArgs(node.getChildNodes(), position);
    }
    // String n = node.getNodeName();
    // System.out.println(n.toString());
    NodeList list = node.getChildNodes();
    return convert(list, position, null, 0);
  }
}
