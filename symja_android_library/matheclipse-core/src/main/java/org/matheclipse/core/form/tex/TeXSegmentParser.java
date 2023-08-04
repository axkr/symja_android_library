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
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.exception.AbortException;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.BuiltInDummy;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.operator.Operator;
import org.matheclipse.parser.client.operator.Precedence;
import org.matheclipse.parser.trie.TrieBuilder;
import org.matheclipse.parser.trie.TrieMatch;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.google.common.base.CharMatcher;
import uk.ac.ed.ph.snuggletex.InputError;
import uk.ac.ed.ph.snuggletex.SnuggleEngine;
import uk.ac.ed.ph.snuggletex.SnuggleInput;
import uk.ac.ed.ph.snuggletex.SnuggleSession;

/* package private */
class TeXSegmentParser {
  private static final Logger LOGGER = LogManager.getLogger();

  /**
   * Because the {@link TeXParser} uses the default slots, use this "dummy slot" inside this class.
   */
  private static ISymbol DUMMY_SUB_SLOT = F.Dummy("$SLOT$");

  static class BinaryOperator extends Operator {
    BiFunction<IExpr, IExpr, IExpr> binaryFunction;

    public BinaryOperator(final String oper, final String functionName, final int precedence,
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

    public PostfixOperator(final String oper, final String functionName, final int precedence,
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

    public PrefixOperator(final String oper, final String functionName, final int precedence,
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
      new PrefixOperator("\u00b1", "PlusMinus", Precedence.PLUSMINUS, (x) -> F.PlusMinus(x)),};

  static final PostfixOperator[] POSTFIX_OPERATORS = { //
      new PostfixOperator("!", "Factorial", Precedence.FACTORIAL, (x) -> F.Factorial(x)), //
  };

  static final BinaryOperator[] BINARY_OPERATORS = { //
      new BinaryOperator("=", "Equal", Precedence.EQUAL, (lhs, rhs) -> F.Equal(lhs, rhs)), //

      // new BinaryOperator("\u2061", "Apply", Precedence.APPLY, //
      // (lhs, rhs) -> F.Apply(lhs, rhs)), //
      // angle sign
      new BinaryOperator("\u2220", "FromPolarCoordinates", Precedence.EQUAL, //
          (lhs, rhs) -> F.FromPolarCoordinates(F.List(lhs, rhs))), //
      new BinaryOperator("\u2260", "Unequal", Precedence.UNEQUAL,
          (lhs, rhs) -> F.Unequal(lhs, rhs)), //
      new BinaryOperator("\u2264", "LessEqual", Precedence.EQUAL,
          (lhs, rhs) -> F.LessEqual(lhs, rhs)), //
      new BinaryOperator("\u2265", "GreaterEqual", Precedence.EQUAL,
          (lhs, rhs) -> F.GreaterEqual(lhs, rhs)), //
      new BinaryOperator("<", "Less", Precedence.EQUAL, (lhs, rhs) -> F.Less(lhs, rhs)), //
      new BinaryOperator(">", "Greater", Precedence.EQUAL, (lhs, rhs) -> F.Greater(lhs, rhs)), //
      new BinaryOperator("\u2227", "And", 215, (lhs, rhs) -> F.And(lhs, rhs)), //
      new BinaryOperator("\u2228", "Or", 213, (lhs, rhs) -> F.Or(lhs, rhs)), //
      new BinaryOperator("\u21d2", "Implies", 120, (lhs, rhs) -> F.Implies(lhs, rhs)), // Rightarrow
      new BinaryOperator("\u2192", "Rule", 120, (lhs, rhs) -> F.Rule(lhs, rhs)), // rightarrow
      new BinaryOperator("\u21d4", "Equivalent", 120, (lhs, rhs) -> F.Equivalent(lhs, rhs)), // Leftrightarrow
      new BinaryOperator("\u2261", "Equivalent", 120, (lhs, rhs) -> F.Equivalent(lhs, rhs)), // equiv
      new BinaryOperator("\u00b1", "PlusMinus", Precedence.PLUSMINUS,
          (lhs, rhs) -> F.PlusMinus(lhs, rhs)),
      // //
      new BinaryOperator("+", "Plus", Precedence.PLUS, (lhs, rhs) -> F.Plus(lhs, rhs)), //
      new BinaryOperator("-", "Subtract", Precedence.PLUS, (lhs, rhs) -> F.Subtract(lhs, rhs)), //
      new BinaryOperator("*", "Times", Precedence.TIMES, (lhs, rhs) -> F.Times(lhs, rhs)), //
      new BinaryOperator("⋅", "Times", Precedence.TIMES, (lhs, rhs) -> F.Times(lhs, rhs)), //
      // x multiplication sign
      new BinaryOperator("\u00d7", "Times", Precedence.TIMES, (lhs, rhs) -> F.Times(lhs, rhs)), //
      // InvisibleTimes
      new BinaryOperator("\u2062", "Times", Precedence.TIMES, (lhs, rhs) -> F.Times(lhs, rhs)), //
      new BinaryOperator("/", "Divide", Precedence.DIVIDE, (lhs, rhs) -> F.Divide(lhs, rhs)), //
      // &#xf7; Division sign
      new BinaryOperator("\u00f7", "Divide", Precedence.DIVIDE, (lhs, rhs) -> F.Divide(lhs, rhs)), //
      new BinaryOperator("\u2208", "Element", 250, (lhs, rhs) -> F.Element(lhs, rhs)), //
      // \cap
      new BinaryOperator("\u2229", "Intersection", Precedence.INTERSECTION,
          (lhs, rhs) -> F.Intersection(lhs, rhs)),
      // \cup
      new BinaryOperator("\u222A", "Union", Precedence.UNION, (lhs, rhs) -> F.Union(lhs, rhs)),};

  protected static Map<String, IExpr> UNICODE_OPERATOR_MAP;

  /**
   * Built-in functions names with 1 argument mapping to the corresponding Symja header built-in
   * symbol {@link IBuiltInSymbol}.
   */
  protected static Map<String, IExpr> FUNCTION_HEADER_MAP_ARG1;
  /**
   * Built-in functions names mapping to the corresponding Symja header built-in symbol
   * {@link IBuiltInSymbol}.
   */
  protected static Map<String, IExpr> FUNCTION_HEADER_MAP;

  protected static Map<String, BinaryOperator> BINARY_OPERATOR_MAP;
  protected static Map<String, PrefixOperator> PREFIX_OPERATOR_MAP;
  protected static Map<String, PostfixOperator> POSTFIX_OPERATOR_MAP;

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      UNICODE_OPERATOR_MAP = Config.TRIE_STRING2EXPR_BUILDER.withMatch(TrieMatch.EXACT).build();
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

      FUNCTION_HEADER_MAP_ARG1 = Config.TRIE_STRING2EXPR_BUILDER.withMatch(TrieMatch.EXACT).build();
      FUNCTION_HEADER_MAP = Config.TRIE_STRING2EXPR_BUILDER.withMatch(TrieMatch.EXACT).build();

      FUNCTION_HEADER_MAP_ARG1.put("arccos", S.ArcCos);
      FUNCTION_HEADER_MAP_ARG1.put("arccot", S.ArcCot);
      FUNCTION_HEADER_MAP_ARG1.put("arccsc", S.ArcCsc);
      FUNCTION_HEADER_MAP_ARG1.put("arcsec", S.ArcSec);
      FUNCTION_HEADER_MAP_ARG1.put("arcsin", S.ArcSin);
      FUNCTION_HEADER_MAP_ARG1.put("arctan", S.ArcTan);

      FUNCTION_HEADER_MAP_ARG1.put("arccosh", S.ArcCosh);
      FUNCTION_HEADER_MAP_ARG1.put("arccoth", S.ArcCoth);
      FUNCTION_HEADER_MAP_ARG1.put("arccsch", S.ArcCsch);
      FUNCTION_HEADER_MAP_ARG1.put("arcsech", S.ArcSech);
      FUNCTION_HEADER_MAP_ARG1.put("arcsinh", S.ArcSinh);
      FUNCTION_HEADER_MAP_ARG1.put("arctanh", S.ArcTanh);

      FUNCTION_HEADER_MAP_ARG1.put("cos", S.Cos);
      FUNCTION_HEADER_MAP_ARG1.put("cot", S.Cot);
      FUNCTION_HEADER_MAP_ARG1.put("csc", S.Csc);
      FUNCTION_HEADER_MAP_ARG1.put("sec", S.Sec);
      FUNCTION_HEADER_MAP_ARG1.put("sin", S.Sin);
      FUNCTION_HEADER_MAP_ARG1.put("tan", S.Tan);

      FUNCTION_HEADER_MAP_ARG1.put("cosh", S.Cosh);
      FUNCTION_HEADER_MAP_ARG1.put("coth", S.Coth);
      FUNCTION_HEADER_MAP_ARG1.put("csch", S.Csch);
      FUNCTION_HEADER_MAP_ARG1.put("sech", S.Sech);
      FUNCTION_HEADER_MAP_ARG1.put("sinh", S.Sinh);
      FUNCTION_HEADER_MAP_ARG1.put("tanh", S.Tanh);

      FUNCTION_HEADER_MAP_ARG1.put("arg", S.Arg);
      FUNCTION_HEADER_MAP_ARG1.put("det", S.Det);
      FUNCTION_HEADER_MAP_ARG1.put("exp", S.Exp);
      FUNCTION_HEADER_MAP_ARG1.put("lg", S.Log2);
      FUNCTION_HEADER_MAP_ARG1.put("ln", S.Log);
      FUNCTION_HEADER_MAP_ARG1.put("log", S.Log10);
      FUNCTION_HEADER_MAP.putAll(FUNCTION_HEADER_MAP_ARG1);

      FUNCTION_HEADER_MAP.put("lim", S.Limit);
      FUNCTION_HEADER_MAP.put("min", S.Min);
      FUNCTION_HEADER_MAP.put("max", S.Max);

      TrieBuilder<String, BinaryOperator, ArrayList<BinaryOperator>> binaryBuilder =
          TrieBuilder.create();
      BINARY_OPERATOR_MAP = binaryBuilder.withMatch(TrieMatch.EXACT).build();
      for (int i = 0; i < BINARY_OPERATORS.length; i++) {
        String headStr = BINARY_OPERATORS[i].getOperatorString();
        BINARY_OPERATOR_MAP.put(headStr, BINARY_OPERATORS[i]);
      }

      TrieBuilder<String, PrefixOperator, ArrayList<PrefixOperator>> prefixBuilder =
          TrieBuilder.create();
      PREFIX_OPERATOR_MAP = prefixBuilder.withMatch(TrieMatch.EXACT).build();
      for (int i = 0; i < PREFIX_OPERATORS.length; i++) {
        String headStr = PREFIX_OPERATORS[i].getOperatorString();
        PREFIX_OPERATOR_MAP.put(headStr, PREFIX_OPERATORS[i]);
      }

      TrieBuilder<String, PostfixOperator, ArrayList<PostfixOperator>> postfixBuilder =
          TrieBuilder.create();
      POSTFIX_OPERATOR_MAP = postfixBuilder.withMatch(TrieMatch.EXACT).build();
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
  // EvalEngine fEngine;

  /**
   * If <code>true</code>, a <code>msub, msup, msubsup, munderover</code> expression is parsed;
   */
  private boolean subOrSup;

  public TeXSegmentParser() {}

  private IExpr convert(NodeList list, int[] position, IExpr lhs, int precedence) {
    return convert(list, position, list.getLength(), lhs, precedence);
  }

  private IExpr convert(NodeList list, int[] position, int end, IExpr lhs, int precedence) {
    final int listSize = list.getLength();
    if (end > 1) {
      if (lhs == null) {
        IExpr commaSeparatedSequence = commaSeparatedSequence(list, position, end);
        if (commaSeparatedSequence.isPresent()) {
          return commaSeparatedSequence;
        }

        Node lhsNode = list.item(position[0]++);
        if (lhsNode == null) {
          return S.Null;
        }
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
          // if (lhs.isAST(S.Log, 3) && !lhs.isFree(DUMMY_SUB_SLOT)) {
          if (!lhs.isFree(DUMMY_SUB_SLOT)) {
            // IExpr rhs = convert(list, position, end, null, Precedence.NO_PRECEDENCE);
            IExpr rhs = convertNextArg(list, position);
            lhs = F.subs(lhs, DUMMY_SUB_SLOT, rhs);
          }
        }

        int attribute = ISymbol.NOATTRIBUTE;
        if (lhs.isSymbol()) {
          attribute = ((ISymbol) lhs).getAttributes();
        }
        if ((attribute & ISymbol.CONSTANT) != ISymbol.CONSTANT) {
          if ((lhs.isFunction() || lhs.isSymbol() || lhs.isDerivative() != null) && //
              position[0] < listSize) {
            boolean isNumericFunction =
                ((attribute & ISymbol.NUMERICFUNCTION) == ISymbol.NUMERICFUNCTION);
            if (isFunctionArg1(lhs)) {
              IExpr nextArg = convertNextArg(list, position);
              lhs = F.unaryAST1(lhs, nextArg);
              if (position[0] == listSize) {
                return lhs;
              }
            } else {

              Node arg2 = list.item(position[0]);
              String arg2NodeName = arg2.getNodeName();
              if (arg2NodeName.equals("mfenced")) {
                if (lhs.equals(S.Integrate)) {
                  ISymbol test = F.Dummy("test");
                  IExpr temp = integrate(list, position, test, test);
                  if (position[0] == listSize) {
                    return temp;
                  }
                  lhs = temp;
                } else {
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
                }
              } else if (!arg2NodeName.equals("mo") //
                  && (isNumericFunction || lhs.isBuiltInSymbolID() || lhs.isFunction())) {
                if (lhs.equals(S.Integrate)) {
                  ISymbol test = F.Dummy("test");
                  IExpr temp = integrate(list, position, test, test);
                  if (position[0] == listSize) {
                    return temp;
                  }
                  lhs = temp;
                } else {
                  IExpr args = convert(list, position, end, null, precedence);
                  if (args.isSequence()) {
                    ((IASTMutable) args).set(0, lhs);
                    return args;
                  }
                  if (lhs.isFunction() && lhs.size() == 2) {
                    IExpr temp = F.subs(lhs.first(), DUMMY_SUB_SLOT, args);
                    // IExpr temp = Lambda.replaceSlots(lhs.first(), F.list(args));
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
            LOGGER.info("mo: {} - {}", () -> text, () -> toUnicodeString(text, "UTF-8"));
          }
          // issue #712 start
          if (text.equals("|")) {
            IExpr sequence = convertArgs(list, 1, list.getLength() - 1);
            if (sequence.isSequence()) {
              sequence = ((IAST) sequence).setAtCopy(0, S.Times);
            }
            return F.Abs(sequence);
          } else if (text.equals("∫")) {
            ISymbol test = F.Dummy("test");
            position[0]++;
            IExpr rhs = integrate(list, position, test, test);
            result = F.Times(lhs, rhs);
            continue;
          }
          // issue #712 end
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
              result = postfixOperator.createFunction(result);
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
        if (lhs.isASTSizeGE(S.Subsuperscript, 3) && rhs.isASTSizeGE(S.Subsuperscript, 3)
            && lhs.first().isSequence() && rhs.first().isSymbol()) {
          IExpr n = lhs.second();
          ISymbol functionHead = (ISymbol) rhs.first();
          IExpr k = rhs.second();
          if (functionHead.isString("C")) {
            // combinations counting: C(n,k) ==> F.Binomial(n, k)
            return F.Binomial(n, k);
          } else if (functionHead.isString("P")) {
            // permutations counting: P(n,k) ==> Pochhammer(k, n-k+1)
            return F.Pochhammer(k, F.Plus(F.C1, n, F.Negate(k)));
          }
        }

        // invisible times?
        if (!lhs.isFree(DUMMY_SUB_SLOT)) {
          // issue #712
          lhs = F.subs(lhs, DUMMY_SUB_SLOT, rhs);
          result = lhs;
        } else {
          result = F.Times(lhs, rhs);
        }
      }
      if (result.isPresent() && position[0] >= end) {
        return result;
      }
    }

    return convertArgs(list, position[0], end);
  }

  /**
   * Parse a comma separated {@link S#Sequence} if possible, otherwise return {@link F#NIL}.
   * 
   * @param list
   * @param position
   * @param end
   * @return
   */
  private IExpr commaSeparatedSequence(NodeList list, int[] position, int end) {
    IASTAppendable resultList = F.Sequence();
    int i = position[0];
    while (i < end) {
      Node op = list.item(i);
      String name = op.getNodeName();
      if (name.equals("mo")) {
        String text = op.getTextContent();
        if (text.equals(",")) {
          IExpr temp = convert(list, position, i, null, Precedence.NO_PRECEDENCE);
          resultList.append(temp);
          position[0] = i + 1;
        }
      }
      i++;
    }
    if (resultList.argSize() > 0) {
      IExpr temp = convert(list, position, i, null, Precedence.NO_PRECEDENCE);
      resultList.append(temp);
      return resultList;
    }
    return F.NIL;
  }

  public IExpr convertArgs(NodeList list, int[] position) {
    return convertArgs(list, 0, list.getLength());
  }

  private IExpr convertArgs(NodeList list, int start, int end) {
    IASTAppendable ast = F.Sequence();
    return convertArgs(ast, list, start, end);
  }

  private IExpr convertArgs(IASTAppendable ast, NodeList list, int start, int end) {
    if (ast.isSequence()) {
      for (int i = start; i < end; i++) {
        Node temp = list.item(i);
        IExpr ex = toExpr(temp);
        ast.append(ex);
      }
      if (ast.argSize() == 1) {
        return ast.arg1();
      }
      if (ast.argSize() == 2) {
        if (ast.arg1().isBuiltInSymbol()) {
          return F.unaryAST1(ast.arg1(), ast.arg2());
        }
      }
    } else {
      for (int i = start; i < end; i++) {
        Node temp = list.item(i);
        IExpr ex = toExpr(temp);
        ast.append(ex);
      }
    }
    return ast;
  }

  private IExpr convertNextArg(NodeList list, int[] position) {
    Node arg1 = list.item(position[0]++);
    String arg1NodeName = arg1.getNodeName();
    IExpr expr1 = toExpr(arg1);
    if (list.getLength() > position[0]) {
      Node arg2 = list.item(position[0]);
      String arg2NodeName = arg2.getNodeName();
      if (arg2NodeName.equals("mfenced")) {
        position[0]++;
        IExpr expr2 = toExpr(arg2);
        return F.unaryAST1(expr1, expr2);
      } else if (arg1NodeName.equals("mi") && arg2NodeName.equals("mi")) {
        IASTAppendable times = F.TimesAlloc(5);
        times.append(expr1);
        while (arg2NodeName.equals("mi")) {
          position[0]++;
          IExpr expr2 = toExpr(arg2);
          times.append(expr2);
          if (list.getLength() <= position[0]) {
            break;
          }
          arg2 = list.item(position[0]);
          arg2NodeName = arg2.getNodeName();
        }
        return times;
      }
    }
    return expr1;
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
      } else {
        break;
      }
    }
    if (evaled) {
      return createSymbol(buf.toString());
    }
    throw new AbortException();
  }

  protected static ISymbol createFunction(String str) {
    if (CharMatcher.javaLetterOrDigit().matchesAllOf(str)) {
      return F.symbol(str);
    }
    return F.$s(str);

  }

  protected ISymbol createSymbol(String str) {
    if (str.length() == 1) {
      char ch = str.charAt(0);
      if (!subOrSup) {
        // i is very often an index in \\sum or \\prod
        if (ch == 'i') {
          return S.I;
        }
      }
      if (ch == 'e') {
        return S.E;
      }
    }
    if (CharMatcher.javaLetterOrDigit().matchesAllOf(str)) {
      return F.symbol(str);
    }
    return F.$s(str);

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
  private IExpr integrate(NodeList parentList, int[] position, ISymbol dummySymbol,
      IExpr symbolOrList) {
    ISymbol x = null;
    IExpr dxValue = F.C1;
    int dxStart = -1;
    int dxEnd = -1;

    int[] dxPosition1 = new int[] {position[0]};
    while (dxPosition1[0] < parentList.getLength()) {
      Node nd = parentList.item(dxPosition1[0]++);
      if (nd.getNodeName().equals("mi") && //
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
        if (frac.isTimes()) {
          IExpr dxArg = frac.first();
          ISymbol d = null;
          if (dxArg.isTimes() //
              && dxArg.argSize() == 2 //
              && dxArg.first().isSymbol() //
              && dxArg.second().isSymbol()) {
            d = (ISymbol) dxArg.first();
            String dStr = d.getSymbolName();
            if (dStr.equals("d")) {
              // (d*x)/x
              dxStart = dxPosition1[0];
              dxEnd = dxPosition1[0];
              x = (ISymbol) dxArg.second();
              dxValue = frac.second();
              break;
            }
          } else if (dxArg.isSymbol()) {
            d = (ISymbol) dxArg;
            String dStr = d.getSymbolName();
            if (dStr.startsWith("d")) {
              // dx/x
              dxStart = dxPosition1[0];
              dxEnd = dxPosition1[0];
              x = createSymbol(dStr.substring(1));
              dxValue = frac.second();
              break;
            }
          }

        }
      }
    }
    if (x == null) {
      return F.unaryAST1(S.Integrate, DUMMY_SUB_SLOT);
      // throw new AbortException();
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

  /**
   * 
   * @param list
   * @return the fraction unsorted so that the numerator is in the first argument.
   */
  private IExpr mfrac(NodeList list) {
    IASTAppendable frac = F.TimesAlloc(2);
    if (list.getLength() > 0) {
      IExpr numerator = toExpr(list.item(0));
      frac.append(numerator);
      if (1 < list.getLength()) {
        IExpr denominator = toExpr(list.item(1));
        if (numerator.isInteger() && denominator.isInteger() && !denominator.isZero()) {
          return F.QQ((IInteger) numerator, (IInteger) denominator);
        }
        if (numerator.argSize() == 1 && numerator.isAST() && numerator.head().isSymbol()
            && denominator.isTimes()) {
          ISymbol head = (ISymbol) numerator.head();
          if (head.isString("\u2202")) {// \[PartialD]
            int indx = denominator.indexOf(head);
            if (indx > 0) {
              return F.D(numerator.first(), ((IAST) denominator).removeAtCopy(indx).oneIdentity1());
            }
          }
        } else if (numerator.isTimes() && numerator.first().isString("d")
            && denominator.isTimes()) {
          int indx = denominator.indexOf(numerator.first());
          if (indx > 0) {
            return F.D(((IAST) numerator).removeAtCopy(1).oneIdentity1(),
                ((IAST) denominator).removeAtCopy(indx).oneIdentity1());
          }
        } else if (numerator.isSymbol() && numerator.isString("d") && denominator.isTimes()) {
          int indx = denominator.indexOf(numerator);
          if (indx > 0) {
            return F.D(DUMMY_SUB_SLOT, ((IAST) denominator).removeAtCopy(indx).oneIdentity1());
          }
        }

        frac.append(F.Power(denominator, -1));
      } else {
        throw new AbortException();
      }
    }
    if (frac.isTimes() && frac.first().isSymbol() && frac.size() == 3
        && frac.second().isPowerReciprocal()) {
      ISymbol d = (ISymbol) frac.first();
      if (d.getSymbolName().equals("d")) {
        IExpr dDenominator = frac.second().first();
        if (dDenominator.isSymbol()) {
          String str = ((ISymbol) dDenominator).getSymbolName();
          if (str.startsWith("d")) {
            str = str.substring(1);
            return F.Function(F.D(DUMMY_SUB_SLOT, createSymbol(str)));
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
        LOGGER.info("mi: {} - {}", () -> text, () -> toUnicodeString(text, "UTF-8"));
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
    return createSymbol(text);
  }

  private IExpr mn(Node node) {
    try {
      String text = node.getTextContent();
      if (text.contains(".") || text.contains("E")) {
        return F.num(text);
      }
      return F.ZZ(text, 10);
    } catch (RuntimeException rex) {
      LOGGER.debug("TeXParser.mn() failed", rex);
    }
    throw new AbortException();
  }

  private IExpr mo(Node node) {
    String text = node.getTextContent();
    if (text.length() == 1) {
      if (SHOW_UNICODE) {
        LOGGER.info("mo: {} - {}", () -> text, () -> toUnicodeString(text, "UTF-8"));
      }
      IExpr x = UNICODE_OPERATOR_MAP.get(text);
      if (x != null) {
        return x;
      }
    }
    return createSymbol(text);
  }

  private IExpr mrow(Node node) {
    NodeList list = node.getChildNodes();
    if (list.getLength() > 1) {
      Node temp = list.item(0);
      IExpr headExpr = toExpr(temp);
      if (isFunctionArg1(headExpr)) {
        IExpr arg1 = toExpr(list.item(1));
        if (list.getLength() == 2) {
          return F.unaryAST1(headExpr, arg1);
        }
        int[] position = new int[] {1};
        IExpr args = convert(list, position, null, 0);
        return F.unaryAST1(headExpr, args);
      }
    }
    // IExpr dummySymbol = getDummySymbol(list);
    // if (dummySymbol.isPresent()) {
    // return dummySymbol;
    // }

    int[] position = new int[] {0};
    return convert(list, position, null, 0);
  }

  /**
   * The <code>symbol</code> is a built-in function with one possible argument.
   * 
   * @param symbol
   * @return
   */
  private boolean isFunctionArg1(IExpr head) {
    if (head.isBuiltInSymbolID()) {
      IEvaluator evaluator = ((IBuiltInSymbol) head).getEvaluator();
      if (evaluator instanceof IFunctionEvaluator) {
        int[] expectedArgSize = ((IFunctionEvaluator) evaluator).expectedArgSize(null);
        if (expectedArgSize != null && expectedArgSize[0] == 1) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Test if the node is the operator in the text form.
   * 
   * @param node
   * @param textForm
   * @return
   */
  private static boolean isOperator(Node node, String textForm) {
    return node.getNodeName().equals("mo") //
        && node.getTextContent().equals(textForm);
  }

  /**
   * If the list contains only text symbols return a {@link BuiltInDummy} symbol.
   * 
   * @param list
   * @return
   */
  // private static IExpr getDummySymbol(NodeList list) {
  // boolean isSymbol = true;
  // for (int i = 0; i < list.getLength(); i++) {
  // Node temp = list.item(i);
  // String n = temp.getNodeName();
  // if (!n.equals("mi") || !(temp instanceof Element)) {
  // isSymbol = false;
  // break;
  // }
  // }
  // if (isSymbol) {
  // // generate a symbol name from the tokens
  // StringBuilder buf = new StringBuilder();
  // for (int i = 0; i < list.getLength(); i++) {
  // Node temp = list.item(i);
  // buf.append(temp.getTextContent());
  // }
  // return createSymbol(buf.toString());
  // }
  // return F.NIL;
  // }

  private IExpr msqrt(NodeList list) {
    if (list.getLength() > 0) {
      Node temp = list.item(0);
      return F.Power(toExprList(temp), F.C1D2);
    }
    return F.NIL;
  }

  private IExpr mroot(NodeList list) {
    if (list.getLength() == 2) {
      IExpr base = toExprList(list.item(0));
      IExpr expDenominator = toExprList(list.item(1));
      return F.Power(base, F.Rational(F.C1, expDenominator));
    }
    return F.NIL;
  }

  private IExpr msubsup(NodeList list, NodeList parentList, int[] position, int precedence) {
    boolean oldSubOrSup = subOrSup;
    try {
      subOrSup = true;
      // \\int_0^\\infty a dx
      if (list.getLength() > 0) {
        Node arg0 = list.item(0);
        IExpr head = toExpr(arg0);
        if (head.isBuiltInSymbol()) {
          ISymbol dummySymbol = F.Dummy("msubsup$" + counter++);
          IExpr arg2 = dummySymbol;
          if (head.isBuiltInSymbolID() && head != S.Integrate && !head.isString("C")) {
            if (list.getLength() >= 2) {
              Node arg1 = list.item(1);
              IExpr a1 = toExpr(arg1);
              if (list.getLength() == 3) {
                IExpr a2 = toExpr(list.item(2));
                if (head == S.Log10) {
                  head = S.Log;
                }
                return F.Power(F.binaryAST2(head, a1, DUMMY_SUB_SLOT), a2);
              } else if (list.getLength() == 2) {
                return F.binaryAST2(head, a1, DUMMY_SUB_SLOT);
              }
            }
          } else {
            if (list.getLength() >= 2) {
              Node arg1 = list.item(1);
              IExpr a1 = toExpr(arg1);
              if (list.getLength() == 3) {
                IExpr a2 = toExpr(list.item(2));
                arg2 = F.list(dummySymbol, a1, a2);
              } else if (list.getLength() == 2) {
                arg2 = F.list(dummySymbol, a1);
              }
            }
          }
          if (parentList != null) {
            while (position[0] < parentList.getLength()) {
              if (head == S.Integrate) {
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
    } finally {
      subOrSup = oldSubOrSup;
    }
    throw new AbortException();
  }

  private IExpr msub(NodeList list) {
    // boolean oldSubOrSup = subOrSup;
    // try {
    // subOrSup = true;
    if (list.getLength() == 2) {
      Node arg1 = list.item(0);
      Node arg2 = list.item(1);

      IExpr a1 = toExpr(arg1);
      IExpr a2 = toExpr(arg2);
      if (a1.equals(S.Limit)) {
        // Limit(#,a2)&
        if (a2.isAST(S.Implies, 3)) { // \Rightarrow
          a2 = F.Rule(a2.first(), a2.second());
        }
        IExpr direction = F.NIL;
        if (a2.isRule() && a2.second().isPower()) {
          IAST pow = (IAST) a2.second();
          if (pow.exponent() instanceof BuiltInDummy) {

            String directionString = pow.exponent().toString();
            if (directionString.equals("+")) {
              // from below
              a2 = F.Rule(a2.first(), pow.first());
              direction = F.Rule(S.Direction, F.C1);
            } else if (directionString.equals("-")) {
              // from above
              a2 = F.Rule(a2.first(), pow.first());
              direction = F.Rule(S.Direction, F.CN1);
            }
          }
        }
        if (direction.isPresent()) {
          return F.Limit(DUMMY_SUB_SLOT, a2, direction);
        }
        return F.Limit(DUMMY_SUB_SLOT, a2);
      }
      if (a1 == S.Log10) {
        return F.binaryAST2(S.Log, a2, DUMMY_SUB_SLOT);
      }
      return F.binaryAST2(S.Subscript, a1, a2);
    }
    // } finally {
    // subOrSup = oldSubOrSup;
    // }
    throw new AbortException();

  }

  private IExpr msup(NodeList list) {
    // boolean oldSubOrSup = subOrSup;
    // try {
    // subOrSup = true;
    if (list.getLength() == 2) {
      Node arg1 = list.item(0);
      Node arg2 = list.item(1);
      return power(arg1, arg2);
    }
    // } finally {
    // subOrSup = oldSubOrSup;
    // }
    throw new AbortException();
  }

  private IExpr munderover(NodeList list, NodeList parentList, int[] position, int precedence) {
    boolean oldSubOrSup = subOrSup;
    try {
      subOrSup = true;
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
              arg2 = F.list(sym, a1, a2);
            } else if (list.getLength() == 2) {
              arg2 = F.list(sym, a1);
            }
          }

          if (parentList != null && position[0] < parentList.getLength()) {
            IExpr arg1 = convert(parentList, position, null, Integer.MAX_VALUE);
            return F.binaryAST2(head, arg1, arg2);
          } else {
            return F.binaryAST2(head, DUMMY_SUB_SLOT, arg2);
          }
        }
      }
    } finally {
      subOrSup = oldSubOrSup;
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
    IExpr a1 = toExprList(arg1);
    String name2 = arg2.getNodeName();
    String text2 = arg2.getTextContent();
    if (name2.equals("mi") && (text2.equals("'") || text2.equals("′"))) {
      return F.unaryAST1(F.Derivative(F.C1), a1);
    }
    IExpr a2 = toExprList(arg2);
    if (a1.isBuiltInSymbol() && a2.isMinusOne()) {
      IExpr value = F.getUnaryInverseFunction(a1);
      if (value != null) {
        // typically Sin^(-1) -> ArcSin or similar...
        return value;
      }
    } else if (a2.equals(S.Degree)) {
      // case \sin 30 ^ { \circ } ==> Sin(30*Degree)
      return F.Times(a1, a2);
    }
    if (isFunctionArg1(a1)) {
      return F.Power(F.unaryAST1(a1, DUMMY_SUB_SLOT), a2);
    }
    return F.Power(a1, a2);
  }

  private IExpr toExprList(Node node) {
    IExpr expr = toExpr(node);
    if (expr.isSequence()) {
      return ((IAST) expr).setAtCopy(0, S.List);
    }
    return expr;
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
    } else if (name.equals("mroot")) {
      return mroot(node.getChildNodes());
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
    // LOGGER.info(node.getNodeName());
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

    SnuggleInput input = new SnuggleInput("\\[ " + texStr + " \\]");
    try {

      if (session.parseInput(input)) {
        NodeList nodes = session.buildDOMSubtree();
        int[] position = new int[] {0};
        IExpr temp = convert(nodes, position, null, 0);
        if (temp.isSequence()) {
          return ((IAST) temp).setAtCopy(0, S.List);
        }
        return temp;
      }
      List<InputError> errors = session.getErrors();
      for (int i = 0; i < errors.size(); i++) {
        LOGGER.warn(errors.get(i));
      }
    } catch (Exception e) {
      if (Config.SHOW_STACKTRACE) {
        System.out.println(texStr);
        e.printStackTrace();
      }
      // `1`.
      Errors.printMessage(S.ToExpression, "error", F.List(F.stringx(e.getMessage())));
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
    } else if (name.equals("mroot")) {
      return mroot(node.getChildNodes());
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
    // LOGGER.info(node.getNodeName());
    NodeList list = node.getChildNodes();
    return convert(list, position, null, 0);
  }
}
