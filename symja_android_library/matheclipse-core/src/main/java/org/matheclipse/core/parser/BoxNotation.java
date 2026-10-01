package org.matheclipse.core.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.Characters;
import org.matheclipse.parser.client.Scanner;
import org.matheclipse.parser.client.SyntaxError;

/**
 * The box escapes of the input syntax: <code>\( ... \)</code> writes boxes and
 * <code>\!\( ... \)</code> the expression which the boxes are the typeset form of. A notebook
 * copies a formula or an image as such an escape into plain text, for example
 *
 * <pre>
 * \!\(\*SuperscriptBox[\(x\), \(2\)]\)
 * \!\(\*GraphicsBox[TagBox[RasterBox[CompressedData["..."], ...], BoxForm`ImageTag["Byte"]]]\)
 * \!\(\*Graphics3DBox[TagBox[Raster3DBox[CompressedData["..."], ...], BoxForm`ImageTag["Byte"]]]\)
 * </pre>
 *
 * <p>
 * The text between the delimiters is read in two steps:
 * <ol>
 * <li>{@link #boxes(String, SourceParser)} reads the linear syntax - <code>\^</code>,
 * <code>\_</code>, <code>\/</code>, <code>\@</code>, <code>\+</code>, <code>\&amp;</code>,
 * <code>\%</code>, <code>\`</code>, nested <code>\( ... \)</code> and <code>\*</code> followed by a
 * box expression in ordinary syntax - into a tree of boxes whose leaves are strings.
 * <li>{@link #toExpression(IExpr, SourceParser)} writes the tree as the source it is the typeset
 * form of and parses that: <code>FractionBox[a, b]</code> is <code>(a)/(b)</code>, a
 * <code>RowBox</code> is its strings one after the other. Whatever in the tree is an expression
 * already - the second argument of an <code>InterpretationBox</code>, the image of a
 * <code>GraphicsBox</code> - goes into the source as a placeholder symbol and replaces it after
 * the source is parsed.
 * </ol>
 *
 * <p>
 * Boxes which have no reading here leave the escape unread; the parser then keeps its text in a
 * <code>HoldComplete</code>.
 */
public final class BoxNotation {

  /** Parses source in Mathematica syntax. The parser which meets a box escape supplies it. */
  @FunctionalInterface
  public interface SourceParser {
    IExpr parse(String source) throws SyntaxError;
  }

  /** A box, or a text, which has no reading. */
  private static final class Unreadable extends RuntimeException {
    private static final long serialVersionUID = 1L;

    Unreadable() {
      super(null, null, false, false);
    }
  }

  /** The name of the symbol a value has in the source until the source is parsed. */
  private static final String SLOT = "$BoxSlot";

  /** The operators of the linear syntax, the characters which follow a backslash. */
  private static final String OPERATORS = "^_+&%@/`";

  /** Characters which are an atom of their own in the linear syntax. */
  private static final String PUNCTUATION = "()+-*/=,";

  /** What a character of a box string is in source: an operator, a space, or nothing. */
  private static final Map<Character, String> CODE = new HashMap<Character, String>();

  private static final char SUM = named("Sum");
  private static final char PRODUCT = named("Product");
  private static final char INTEGRAL = named("Integral");
  private static final char DIFFERENTIAL_D = named("DifferentialD");
  private static final char PARTIAL_D = named("PartialD");
  private static final char PRIME = named("Prime");
  private static final char LEFT_BAR = named("LeftBracketingBar");
  private static final char RIGHT_BAR = named("RightBracketingBar");

  static {
    code("Rule", "->");
    code("RuleDelayed", ":>");
    code("Equal", "==");
    code("Prime", "'");
    code("NotEqual", "!=");
    code("LessEqual", "<=");
    code("GreaterEqual", ">=");
    code("And", "&&");
    code("Or", "||");
    code("InvisibleTimes", "*");
    code("IndentingNewLine", "\n");
    for (String name : new String[] {"NoBreak", "InvisibleSpace", "InvisibleComma", "ImplicitPlus",
        "AutoSpace", "NonBreakingSpace", "InvisiblePrefixScriptBase",
        "InvisiblePostfixScriptBase", "Null", "SpanFromLeft", "SpanFromAbove", "SpanFromBoth"}) {
      code(name, "");
    }
    for (String name : new String[] {"ThinSpace", "VeryThinSpace", "MediumSpace", "ThickSpace",
        "NegativeThinSpace", "NegativeVeryThinSpace", "NegativeMediumSpace",
        "NegativeThickSpace"}) {
      code(name, " ");
    }
  }

  private static char named(String name) {
    String character = Characters.NamedCharactersMap.get(name);
    return character == null || character.length() != 1 ? '\0' : character.charAt(0);
  }

  private static void code(String name, String source) {
    char character = named(name);
    if (character != '\0') {
      CODE.put(character, source);
    }
  }

  private final SourceParser parser;

  /** The values which the source has placeholder symbols for. */
  private final List<IExpr> slots = new ArrayList<IExpr>();

  /** How deep the escape which is read is nested in other ones. */
  private int depth;

  /** Escapes nested deeper than this have no reading; the text is input of a user. */
  private static final int MAX_DEPTH = 64;

  private BoxNotation(SourceParser parser) {
    this.parser = parser;
  }

  /**
   * The boxes of <code>\( text \)</code>.
   *
   * @return <code>null</code> if the text has no reading
   */
  public static IExpr boxes(String text, SourceParser parser) {
    try {
      return new BoxNotation(parser).notation(text);
    } catch (Unreadable | SyntaxError e) {
      return null;
    }
  }

  /**
   * The expression of <code>\!\( text \)</code>.
   *
   * @return <code>null</code> if the text has no reading
   */
  public static IExpr interpret(String text, SourceParser parser) {
    try {
      if (isPlainSource(text)) {
        // \!\(2 + 2\) is the expression itself
        return parser.parse(text);
      }
      BoxNotation notation = new BoxNotation(parser);
      IExpr boxes = notation.notation(text);
      return boxes == null ? null : notation.expression(boxes);
    } catch (Unreadable | SyntaxError e) {
      return null;
    }
  }

  /**
   * The expression which <code>boxes</code> are the typeset form of.
   *
   * @return <code>null</code> if the boxes have no reading
   */
  public static IExpr toExpression(IExpr boxes, SourceParser parser) {
    try {
      return new BoxNotation(parser).expression(boxes);
    } catch (Unreadable | SyntaxError e) {
      return null;
    }
  }

  /**
   * What an escape which has no reading is kept as: its text, as a string would hold it, in
   * <code>HoldComplete</code>.
   */
  public static IExpr unread(String text, boolean interpret) {
    StringBuilder buf = new StringBuilder(text.length() + 3);
    if (interpret) {
      buf.append(Scanner.BOX_BANG);
    }
    buf.append(Scanner.BOX_OPEN);
    for (int i = 0; i < text.length(); i++) {
      char ch = text.charAt(i);
      if (ch == '\\' && i + 1 < text.length()) {
        char stored = stored(text.charAt(i + 1));
        if (stored != '\0') {
          buf.append(stored);
          i++;
          continue;
        }
      }
      buf.append(ch);
    }
    return F.HoldComplete(F.stringx(buf.append(Scanner.BOX_CLOSE).toString()));
  }

  /** The character a string keeps for the box escape <code>\ch</code>, or <code>0</code>. */
  private static char stored(char ch) {
    switch (ch) {
      case '!':
        return Scanner.BOX_BANG;
      case '(':
        return Scanner.BOX_OPEN;
      case ')':
        return Scanner.BOX_CLOSE;
      case '*':
        return Scanner.BOX_SEPARATOR;
      case '`':
        return Scanner.BOX_FORM;
      default:
        return '\0';
    }
  }

  /**
   * The text of a string as it is written in input: the characters which a string keeps for the
   * box escapes <code>\!</code>, <code>\(</code>, <code>\*</code>, <code>\)</code> and
   * <code>\`</code> are written as these escapes again.
   */
  public static String writeEscapes(String text) {
    StringBuilder buf = null;
    for (int i = 0; i < text.length(); i++) {
      char ch = text.charAt(i);
      char ascii = ch == Scanner.BOX_BANG ? '!'
          : ch == Scanner.BOX_OPEN ? '('
              : ch == Scanner.BOX_CLOSE ? ')'
                  : ch == Scanner.BOX_SEPARATOR ? '*' : ch == Scanner.BOX_FORM ? '`' : '\0';
      if (ascii != '\0' && buf == null) {
        buf = new StringBuilder(text.length() + 16).append(text, 0, i);
      }
      if (buf != null) {
        if (ascii != '\0') {
          buf.append('\\').append(ascii);
        } else {
          buf.append(ch);
        }
      }
    }
    return buf == null ? text : buf.toString();
  }

  /** Whether <code>expr</code> is built from a box - and isn't an expression to evaluate. */
  public static boolean isBox(IExpr expr) {
    String head = headName(expr);
    return head != null && head.endsWith("box");
  }

  private static boolean isPlainSource(String text) {
    for (int i = 0; i < text.length(); i++) {
      char ch = text.charAt(i);
      if (ch == '\\' || ch == Scanner.BOX_OPEN || ch == Scanner.BOX_CLOSE
          || ch == Scanner.BOX_SEPARATOR || ch == Scanner.BOX_FORM || ch == Scanner.BOX_BANG) {
        return false;
      }
    }
    return true;
  }

  // ------------------------------------------------------------------ linear syntax

  /** One piece of the linear syntax: an operator, or a box. */
  private static final class Token {
    /** the character of the operator, or <code>0</code> */
    final char operator;
    final IExpr box;
    /** a box which was written as <code>\( ... \)</code> or <code>\* ...</code> */
    final boolean nested;

    Token(char operator, IExpr box, boolean nested) {
      this.operator = operator;
      this.box = box;
      this.nested = nested;
    }

    boolean isAtom(String text) {
      return operator == 0 && !nested && box.isString() && box.toString().equals(text);
    }
  }

  /** A box and the index of the token behind it. */
  private static final class Parsed {
    final IExpr box;
    final int end;

    Parsed(IExpr box, int end) {
      this.box = box;
      this.end = end;
    }
  }

  private static int delimiter(String s, int position, char ascii, char stored) {
    if (position >= s.length()) {
      return 0;
    }
    if (s.charAt(position) == stored) {
      return 1;
    }
    return s.charAt(position) == '\\' && position + 1 < s.length()
        && s.charAt(position + 1) == ascii ? 2 : 0;
  }

  /** The index behind the string literal which starts at <code>position</code>. */
  private static int stringEnd(String s, int position) {
    int i = position + 1;
    while (i < s.length() && s.charAt(i) != '"') {
      i += s.charAt(i) == '\\' ? 2 : 1;
    }
    return Math.min(i + 1, s.length());
  }

  /** The index of the <code>\)</code> which closes the escape whose text starts at <code>i</code>. */
  private static int closing(String s, int i) {
    int depth = 1;
    while (i < s.length()) {
      if (s.charAt(i) == '"') {
        i = stringEnd(s, i);
        continue;
      }
      int length = delimiter(s, i, '(', Scanner.BOX_OPEN);
      if (length > 0) {
        depth++;
        i += length;
        continue;
      }
      length = delimiter(s, i, ')', Scanner.BOX_CLOSE);
      if (length > 0) {
        if (--depth == 0) {
          return i;
        }
        i += length;
        continue;
      }
      i += s.charAt(i) == '\\' ? 2 : 1;
    }
    throw new Unreadable();
  }

  /**
   * The index behind the box expression which follows a <code>\*</code>: a string, or a name and
   * the brackets behind it.
   */
  private static int expressionEnd(String s, int i) {
    while (i < s.length() && Character.isWhitespace(s.charAt(i))) {
      i++;
    }
    if (i < s.length() && s.charAt(i) == '"') {
      return stringEnd(s, i);
    }
    while (i < s.length() && (Character.isLetterOrDigit(s.charAt(i)) || s.charAt(i) == '$'
        || s.charAt(i) == '`')) {
      i++;
    }
    int bracket = i;
    while (bracket < s.length() && Character.isWhitespace(s.charAt(bracket))) {
      bracket++;
    }
    if (bracket >= s.length() || s.charAt(bracket) != '[') {
      return i;
    }
    int depth = 0;
    i = bracket;
    while (i < s.length()) {
      char ch = s.charAt(i);
      if (ch == '"') {
        i = stringEnd(s, i);
        continue;
      }
      if (ch == '\\') {
        i += 2;
        continue;
      }
      if (ch == '[') {
        depth++;
      } else if (ch == ']' && --depth == 0) {
        return i + 1;
      }
      i++;
    }
    throw new Unreadable();
  }

  private static boolean startsEscape(String s, int i) {
    char ch = s.charAt(i);
    if (ch == Scanner.BOX_OPEN || ch == Scanner.BOX_CLOSE || ch == Scanner.BOX_SEPARATOR
        || ch == Scanner.BOX_FORM) {
      return true;
    }
    return ch == '\\' && i + 1 < s.length()
        && (OPERATORS.indexOf(s.charAt(i + 1)) >= 0 || "()*".indexOf(s.charAt(i + 1)) >= 0);
  }

  private List<Token> tokenize(String s) {
    List<Token> tokens = new ArrayList<Token>();
    int i = 0;
    while (i < s.length()) {
      char ch = s.charAt(i);
      if (Character.isWhitespace(ch)) {
        i++;
        continue;
      }
      int length = delimiter(s, i, '(', Scanner.BOX_OPEN);
      if (length > 0) {
        int end = closing(s, i + length);
        IExpr nested = notation(s.substring(i + length, end));
        if (nested == null) {
          throw new Unreadable();
        }
        tokens.add(new Token('\0', nested, true));
        i = end + delimiter(s, end, ')', Scanner.BOX_CLOSE);
        continue;
      }
      length = delimiter(s, i, '*', Scanner.BOX_SEPARATOR);
      if (length > 0) {
        int end = expressionEnd(s, i + length);
        tokens.add(new Token('\0', parser.parse(s.substring(i + length, end)), true));
        i = end;
        continue;
      }
      if (ch == Scanner.BOX_FORM) {
        tokens.add(new Token('`', null, false));
        i++;
        continue;
      }
      if (ch == '\\' && i + 1 < s.length() && OPERATORS.indexOf(s.charAt(i + 1)) >= 0) {
        tokens.add(new Token(s.charAt(i + 1), null, false));
        i += 2;
        continue;
      }
      if (delimiter(s, i, ')', Scanner.BOX_CLOSE) > 0) {
        throw new Unreadable();
      }
      if (ch == '"') {
        int end = stringEnd(s, i);
        tokens.add(new Token('\0', F.stringx(s.substring(i, end)), false));
        i = end;
        continue;
      }
      if (PUNCTUATION.indexOf(ch) >= 0) {
        tokens.add(new Token('\0', F.stringx(String.valueOf(ch)), false));
        i++;
        continue;
      }
      int start = i;
      while (i < s.length()) {
        ch = s.charAt(i);
        if (Character.isWhitespace(ch) || PUNCTUATION.indexOf(ch) >= 0 || ch == '"'
            || startsEscape(s, i)) {
          break;
        }
        i++;
      }
      tokens.add(new Token('\0', F.stringx(s.substring(start, i)), false));
    }
    return tokens;
  }

  /**
   * The boxes of the text between <code>\(</code> and <code>\)</code>.
   *
   * @return <code>null</code> if the text is empty
   */
  private IExpr notation(String text) {
    String inner = text.trim();
    if (inner.isEmpty()) {
      return null;
    }
    if (++depth > MAX_DEPTH) {
      throw new Unreadable();
    }
    try {
      List<Token> tokens = tokenize(inner);
      if (tokens.isEmpty()) {
        return null;
      }
      Parsed chain = chain(tokens, 0);
      if (chain != null && chain.end == tokens.size()) {
        return chain.box;
      }
      return row(tokens, 0, tokens.size());
    } finally {
      depth--;
    }
  }

  /** The row of the tokens <code>from ... to</code>; a <code>( ... )</code> is a row of its own. */
  private IExpr row(List<Token> tokens, int from, int to) {
    IASTAppendable parts = F.ListAlloc(to - from);
    int i = from;
    while (i < to) {
      if (tokens.get(i).isAtom("(")) {
        int close = closingParenthesis(tokens, i, to);
        parts.append(group(tokens, i, close));
        i = close + 1;
        continue;
      }
      Parsed chained = chain(tokens.subList(0, to), i);
      if (chained != null && chained.end > i + 1) {
        parts.append(chained.box);
        i = chained.end;
        continue;
      }
      parts.append(unit(tokens.get(i)));
      i++;
    }
    if (parts.argSize() == 0) {
      throw new Unreadable();
    }
    return parts.argSize() == 1 ? parts.arg1() : F.unaryAST1(S.RowBox, parts);
  }

  private static int closingParenthesis(List<Token> tokens, int open, int to) {
    int depth = 1;
    for (int j = open + 1; j < to; j++) {
      if (tokens.get(j).isAtom("(")) {
        depth++;
      } else if (tokens.get(j).isAtom(")") && --depth == 0) {
        return j;
      }
    }
    throw new Unreadable();
  }

  /** <code>RowBox[{"(", inner, ")"}]</code> of the tokens between two parentheses. */
  private IExpr group(List<Token> tokens, int open, int close) {
    return F.unaryAST1(S.RowBox,
        F.List(F.stringx("("), row(tokens, open + 1, close), F.stringx(")")));
  }

  private static IExpr unit(Token token) {
    if (token.operator != 0) {
      throw new Unreadable();
    }
    return token.box;
  }

  private Parsed unitOrGroup(List<Token> tokens, int start) {
    if (start >= tokens.size()) {
      throw new Unreadable();
    }
    if (tokens.get(start).isAtom("(")) {
      int close = closingParenthesis(tokens, start, tokens.size());
      return new Parsed(group(tokens, start, close), close + 1);
    }
    return new Parsed(unit(tokens.get(start)), start + 1);
  }

  /**
   * A box and the operators which follow it.
   *
   * @return <code>null</code> if no box starts at <code>start</code>
   */
  private Parsed chain(List<Token> tokens, int start) {
    if (start >= tokens.size()) {
      return null;
    }
    Token first = tokens.get(start);
    if (first.operator == '@') {
      Parsed argument = chain(tokens, start + 1);
      return argument == null ? null
          : new Parsed(F.unaryAST1(S.SqrtBox, argument.box), argument.end);
    }
    if (first.operator != 0) {
      return null;
    }
    IExpr lhs = first.box;
    int index = start + 1;
    while (index < tokens.size()) {
      char operator = tokens.get(index).operator;
      if (operator == '^' || operator == '_') {
        // right associative: the whole chain is the script
        Parsed rhs = chain(tokens, index + 1);
        if (rhs == null) {
          return null;
        }
        return new Parsed(
            F.binaryAST2(operator == '^' ? S.SuperscriptBox : S.SubscriptBox, lhs, rhs.box),
            rhs.end);
      } else if (operator == '/') {
        Parsed rhs = unitOrGroup(tokens, index + 1);
        lhs = F.binaryAST2(S.FractionBox, lhs, rhs.box);
        index = rhs.end;
      } else if (operator == '`') {
        // form \` body
        IExpr body = row(tokens, index + 1, tokens.size());
        IExpr form = lhs.isString() ? parser.parse(lhs.toString()) : lhs;
        return new Parsed(F.binaryAST2(S.FormBox, body, form), tokens.size());
      } else if (operator == '+' || operator == '&') {
        if (index + 1 >= tokens.size()) {
          return null;
        }
        IExpr rhs = unit(tokens.get(index + 1));
        int end = index + 2;
        if (end < tokens.size() && tokens.get(end).operator == '%') {
          if (end + 1 >= tokens.size()) {
            return null;
          }
          IExpr third = unit(tokens.get(end + 1));
          end += 2;
          lhs = operator == '+' ? F.ternaryAST3(S.UnderoverscriptBox, lhs, rhs, third)
              : F.ternaryAST3(S.UnderoverscriptBox, lhs, third, rhs);
        } else {
          lhs = F.binaryAST2(operator == '+' ? S.UnderscriptBox : S.OverscriptBox, lhs, rhs);
        }
        index = end;
      } else {
        break;
      }
    }
    return new Parsed(lhs, index);
  }

  // ------------------------------------------------------------------ boxes to source

  /** The name of the head in lower case, <code>null</code> if the head is no symbol. */
  private static String headName(IExpr expr) {
    if (expr.isAST() && expr.head().isSymbol()) {
      return ((ISymbol) expr.head()).getSymbolName().toLowerCase(Locale.US);
    }
    return null;
  }

  private static boolean isHead(IExpr expr, String lowerCaseName) {
    return lowerCaseName.equals(headName(expr));
  }

  /**
   * Whether <code>expr</code> is the symbol of this name. The name decides, not the identity: a
   * parser which doesn't share the case of its symbols with the engine reads another symbol.
   */
  private static boolean isSymbol(IExpr expr, String name) {
    return expr.isSymbol() && ((ISymbol) expr).getSymbolName().equalsIgnoreCase(name);
  }

  /** The arguments without the options, which say how a box is drawn. */
  private static List<IExpr> positional(IAST box) {
    List<IExpr> arguments = new ArrayList<IExpr>(box.argSize());
    for (int i = 1; i < box.size(); i++) {
      if (!box.get(i).isRuleAST()) {
        arguments.add(box.get(i));
      }
    }
    return arguments;
  }

  private IExpr expression(IExpr boxes) {
    if (isHead(boxes, "formbox")) {
      List<IExpr> arguments = positional((IAST) boxes);
      if (arguments.size() == 2) {
        if (isSymbol(arguments.get(1), "StandardForm")
            || isSymbol(arguments.get(1), "TraditionalForm")) {
          return expression(arguments.get(0));
        }
        throw new Unreadable();
      }
    }
    String source = source(boxes);
    if (source.trim().isEmpty()) {
      throw new Unreadable();
    }
    IExpr expr = parser.parse(source);
    if (slots.isEmpty()) {
      return expr;
    }
    return expr.replaceAll(x -> {
      if (x.isSymbol()) {
        String name = ((ISymbol) x).getSymbolName();
        if (name.regionMatches(true, 0, SLOT, 0, SLOT.length())) {
          try {
            int index = Integer.parseInt(name.substring(SLOT.length()));
            if (index >= 0 && index < slots.size()) {
              return slots.get(index);
            }
          } catch (NumberFormatException nfe) {
            // a symbol of the user
          }
        }
      }
      return F.NIL;
    }).orElse(expr);
  }

  /** The symbol which stands for <code>value</code> in the source. */
  private String slot(IExpr value) {
    slots.add(value);
    return SLOT + (slots.size() - 1);
  }

  private boolean parses(String source) {
    try {
      parser.parse(source);
      return true;
    } catch (SyntaxError e) {
      return false;
    }
  }

  private static String quoted(String text) {
    return "\"" + text.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
  }

  /** The source of a string of a box: its characters, the typeset ones as operators. */
  private static String codeText(String text) {
    if (text.startsWith("\"")) {
      // a string literal, whose characters are its content
      return text;
    }
    StringBuilder buf = null;
    for (int i = 0; i < text.length(); i++) {
      String code = CODE.get(text.charAt(i));
      if (code != null && buf == null) {
        buf = new StringBuilder(text.length() + 8).append(text, 0, i);
      }
      if (buf != null) {
        if (code != null) {
          buf.append(code);
        } else {
          buf.append(text.charAt(i));
        }
      }
    }
    return buf == null ? text : buf.toString();
  }

  /** Whether the source draws nothing: the base of a prefix script is such a text. */
  private static boolean drawsNothing(String source) {
    return source.trim().isEmpty();
  }

  /** The source which <code>box</code> is the typeset form of. */
  private String source(IExpr box) {
    if (box.isString()) {
      return codeText(box.toString());
    }
    String head = headName(box);
    if (head == null) {
      return slot(box);
    }
    IAST ast = (IAST) box;
    if (head.equals("rowbox")) {
      if (ast.argSize() >= 1 && ast.arg1().isList()) {
        return rowSource(elements((IAST) ast.arg1()));
      }
      throw new Unreadable();
    }
    if (head.equals("graphicsbox")) {
      return slot(graphics(ast));
    }
    if (head.equals("graphics3dbox")) {
      // the box of a 3D image; the boxes of a 3D graphics have no reading
      List<IExpr> image = imageArguments(ast, "raster3dbox");
      if (image == null) {
        throw new Unreadable();
      }
      StringBuilder buf = new StringBuilder("Image3D[");
      for (int i = 0; i < image.size(); i++) {
        buf.append(i == 0 ? "" : ", ").append(slot(image.get(i)));
      }
      return buf.append(']').toString();
    }
    if (head.equals("templatebox")) {
      return templateSource(ast);
    }
    List<IExpr> args = positional(ast);
    final int size = args.size();
    switch (head) {
      case "fractionbox":
        if (size == 2) {
          return "(" + source(args.get(0)) + ")/(" + source(args.get(1)) + ")";
        }
        break;
      case "superscriptbox":
        if (size == 2) {
          return superscriptSource(args.get(0), args.get(1));
        }
        break;
      case "subscriptbox":
        if (size == 2) {
          return subscriptSource(args.get(0), args.get(1));
        }
        break;
      case "subsuperscriptbox":
        if (size == 3) {
          return "(Subscript[" + source(args.get(0)) + ", " + source(args.get(1)) + "])^("
              + source(args.get(2)) + ")";
        }
        break;
      case "overscriptbox":
        if (size == 2) {
          // the mark is a hat or a bar, no code: a bare _ would be read as a pattern
          return "Overscript[" + source(args.get(0)) + ", " + quoted(source(args.get(1))) + "]";
        }
        break;
      case "underscriptbox":
        if (size == 2) {
          return "Underscript[" + source(args.get(0)) + ", " + quoted(source(args.get(1))) + "]";
        }
        break;
      case "sqrtbox":
        if (size == 1) {
          return "Sqrt[" + source(args.get(0)) + "]";
        }
        break;
      case "radicalbox":
        if (size == 2) {
          return "Surd[" + source(args.get(0)) + ", " + source(args.get(1)) + "]";
        }
        break;
      case "tagbox":
      case "stylebox":
      case "framebox":
      case "adjustmentbox":
      case "formbox":
        if (size >= 1) {
          return source(args.get(0));
        }
        break;
      case "interpretationbox":
        if (size >= 2) {
          IExpr value = args.get(1);
          if (value.isAST1() && (isSymbol(value.head(), "InputForm")
              || isSymbol(value.head(), "OutputForm") || isSymbol(value.head(), "StandardForm")
              || isSymbol(value.head(), "TraditionalForm"))) {
            value = value.first();
          }
          return slot(value);
        }
        break;
      case "gridbox":
        if (size >= 1 && args.get(0).isList()) {
          return gridSource((IAST) args.get(0));
        }
        break;
      default:
        if (!head.endsWith("box")) {
          // an expression in the place of a box stands for itself
          return slot(box);
        }
        break;
    }
    throw new Unreadable();
  }

  private static List<IExpr> elements(IAST list) {
    List<IExpr> elements = new ArrayList<IExpr>(list.argSize());
    for (int i = 1; i < list.size(); i++) {
      elements.add(list.get(i));
    }
    return elements;
  }

  /** The number of primes if the script is prime marks only, else <code>0</code>. */
  private static int primeMarks(IExpr script) {
    if (!script.isString()) {
      return 0;
    }
    String text = script.toString().trim();
    for (int i = 0; i < text.length(); i++) {
      if (text.charAt(i) != PRIME && text.charAt(i) != '\'') {
        return 0;
      }
    }
    return text.length();
  }

  /**
   * The orders of <code>TagBox[(n1, n2, ...), Derivative]</code>, the script of a derivative
   * which isn't written with primes.
   *
   * @return <code>null</code> for any other script
   */
  private String derivativeOrders(IExpr script) {
    if (!isHead(script, "tagbox")) {
      return null;
    }
    List<IExpr> args = positional((IAST) script);
    if (args.size() != 2 || !isSymbol(args.get(1), "Derivative")) {
      return null;
    }
    String rendered = source(args.get(0)).trim();
    if (rendered.length() < 3 || rendered.charAt(0) != '('
        || rendered.charAt(rendered.length() - 1) != ')') {
      return null;
    }
    String orders = rendered.substring(1, rendered.length() - 1).trim();
    return orders.isEmpty() ? null : orders;
  }

  private String superscriptSource(IExpr baseBox, IExpr scriptBox) {
    int primes = primeMarks(scriptBox);
    if (primes > 0) {
      StringBuilder buf = new StringBuilder(source(baseBox));
      for (int i = 0; i < primes; i++) {
        buf.append('\'');
      }
      return buf.toString();
    }
    String base = source(baseBox);
    String orders = derivativeOrders(scriptBox);
    if (orders != null) {
      return "Derivative[" + orders + "][" + base + "]";
    }
    String script = source(scriptBox);
    if (drawsNothing(base)) {
      // a prefix script has an empty base
      return "Superscript[\"\", " + script + "]";
    }
    if (!parses(script)) {
      // a script which is no expression - the charge of an ion - is kept as it is drawn
      return "Superscript[" + base + ", " + quoted(script) + "]";
    }
    return "(" + base + ")^(" + script + ")";
  }

  private String subscriptSource(IExpr baseBox, IExpr scriptBox) {
    String base = source(baseBox);
    String script = source(scriptBox);
    String part = script.trim();
    if (part.length() >= 2 && ((part.charAt(0) == '〚' && part.endsWith("〛"))
        || (part.charAt(0) == '⟦' && part.endsWith("⟧")))) {
      // a part specification written as a subscript
      return "Part[" + base + ", " + part.substring(1, part.length() - 1).trim() + "]";
    }
    if (!parses(script)) {
      script = quoted(script);
    }
    return drawsNothing(base) ? "Subscript[\"\", " + script + "]"
        : "Subscript[" + base + ", " + script + "]";
  }

  private String gridSource(IAST rows) {
    StringBuilder buf = new StringBuilder("{");
    for (int i = 1; i < rows.size(); i++) {
      if (i > 1) {
        buf.append(", ");
      }
      IExpr row = rows.get(i);
      if (!row.isList()) {
        buf.append(source(row));
        continue;
      }
      buf.append('{');
      for (int j = 1; j < ((IAST) row).size(); j++) {
        if (j > 1) {
          buf.append(", ");
        }
        buf.append(source(((IAST) row).get(j)));
      }
      buf.append('}');
    }
    return buf.append('}').toString();
  }

  /**
   * A <code>TemplateBox[{slots...}, tag]</code>: the quantity and the row templates are written
   * as what they are the box of, any other template as its first slot.
   */
  private String templateSource(IAST box) {
    List<IExpr> args = positional(box);
    if (args.size() < 2 || !args.get(0).isList()) {
      throw new Unreadable();
    }
    List<IExpr> parts = elements((IAST) args.get(0));
    IExpr tagExpr = args.get(args.size() - 1);
    String tag = tagExpr.toString();
    if ((tag.equals("Quantity") || tag.equals("QuantityPrefix")) && parts.size() >= 4) {
      String number = source(parts.get(0));
      IExpr unit = parts.get(3);
      if (!unit.isString()) {
        // a compound unit is a box of its own
        return "Quantity[" + number + ", " + source(unit) + "]";
      }
      String name = unit.toString();
      while (name.length() >= 2 && name.startsWith("\"") && name.endsWith("\"")) {
        name = name.substring(1, name.length() - 1);
      }
      return "Quantity[" + number + ", " + quoted(name.replace("\\\"", "")) + "]";
    }
    if (tag.equals("RowDefault") || tag.equals("RowWithSeparator")
        || tag.equals("RowWithSeparators")) {
      int first = 0;
      String separator = null;
      if (tag.equals("RowWithSeparators") && parts.size() >= 2) {
        // the separator is there twice: as it is drawn and as it was written
        separator = source(parts.get(1));
        first = 2;
      } else if (tag.equals("RowWithSeparator") && parts.size() >= 1) {
        separator = source(parts.get(0));
        first = 1;
      }
      StringBuilder buf = new StringBuilder("Row[{");
      for (int i = first; i < parts.size(); i++) {
        if (i > first) {
          buf.append(", ");
        }
        buf.append(source(parts.get(i)));
      }
      buf.append('}');
      if (separator != null) {
        buf.append(", ").append(separator);
      }
      return buf.append(']').toString();
    }
    if (parts.isEmpty()) {
      throw new Unreadable();
    }
    return source(parts.get(0));
  }

  // ------------------------------------------------------------------ rows

  private static boolean isCharacter(IExpr part, char ch) {
    if (!part.isString() || ch == '\0') {
      return false;
    }
    String text = part.toString().trim();
    return text.length() == 1 && text.charAt(0) == ch;
  }

  private static boolean isText(IExpr part, String text) {
    return part.isString() && part.toString().equals(text);
  }

  /**
   * Append a piece of source behind another one. Two pieces which would run together into one
   * name are a product.
   */
  private static void juxtapose(StringBuilder result, String piece) {
    boolean pieceStartsWithLetter = piece.length() > 0 && Character.isLetter(piece.charAt(0));
    boolean runHasLetter = false;
    for (int i = result.length() - 1; i >= 0; i--) {
      char ch = result.charAt(i);
      if (!Character.isLetterOrDigit(ch) && ch != '_' && ch != '$') {
        break;
      }
      runHasLetter |= Character.isLetter(ch) || ch == '$';
    }
    if (pieceStartsWithLetter) {
      if (result.length() > 0 && result.charAt(result.length() - 1) == '#') {
        result.append(' ');
      } else if (runHasLetter) {
        result.append('*');
      }
    }
    result.append(piece);
  }

  /** <code>{head, iterator}</code> of the box of a sum or a product, which stands before its body. */
  private String[] bigOperator(IExpr part) {
    boolean under = isHead(part, "underscriptbox");
    if (!under && !isHead(part, "underoverscriptbox")) {
      return null;
    }
    List<IExpr> args = positional((IAST) part);
    if (args.size() != (under ? 2 : 3)) {
      return null;
    }
    String head = isCharacter(args.get(0), SUM) ? "Sum"
        : isCharacter(args.get(0), PRODUCT) ? "Product" : null;
    if (head == null) {
      return null;
    }
    String lower = source(args.get(1)).trim();
    if (under) {
      return new String[] {head, lower};
    }
    String upper = source(args.get(2)).trim();
    int equals = assignmentIndex(lower);
    if (equals < 0) {
      return new String[] {head, "{" + lower + ", " + upper + "}"};
    }
    return new String[] {head, "{" + lower.substring(0, equals).trim() + ", "
        + lower.substring(equals + 1).trim() + ", " + upper + "}"};
  }

  /** The index of the <code>=</code> of <code>n = 1</code>, which is no part of an operator. */
  private static int assignmentIndex(String s) {
    int depth = 0;
    for (int i = 0; i < s.length(); i++) {
      char ch = s.charAt(i);
      if (ch == '[' || ch == '{' || ch == '(') {
        depth++;
      } else if (ch == ']' || ch == '}' || ch == ')') {
        depth--;
      } else if (ch == '=' && depth == 0) {
        char previous = i > 0 ? s.charAt(i - 1) : ' ';
        char next = i + 1 < s.length() ? s.charAt(i + 1) : ' ';
        if ("=<>!:/+-".indexOf(previous) >= 0 || next == '=') {
          continue;
        }
        return i;
      }
    }
    return -1;
  }

  /** Whether the source has an assignment outside of every bracket. */
  private static boolean hasTopLevelAssignment(String s) {
    int depth = 0;
    boolean inString = false;
    for (int i = 0; i < s.length(); i++) {
      char ch = s.charAt(i);
      if (inString) {
        if (ch == '\\') {
          i++;
        } else if (ch == '"') {
          inString = false;
        }
        continue;
      }
      if (ch == '"') {
        inString = true;
      } else if (ch == '[' || ch == '{' || ch == '(') {
        depth++;
      } else if (ch == ']' || ch == '}' || ch == ')') {
        depth--;
      } else if (ch == '=' && depth == 0) {
        if (i + 1 < s.length() && (s.charAt(i + 1) == '=' || s.charAt(i + 1) == '!')) {
          // == === =!=
          while (i + 1 < s.length() && (s.charAt(i + 1) == '=' || s.charAt(i + 1) == '!')) {
            i++;
          }
          continue;
        }
        if (i > 0 && "<>!".indexOf(s.charAt(i - 1)) >= 0) {
          continue;
        }
        return true;
      }
    }
    return false;
  }

  private static boolean hasTopLevelComma(String s) {
    int depth = 0;
    boolean inString = false;
    for (int i = 0; i < s.length(); i++) {
      char ch = s.charAt(i);
      if (inString) {
        if (ch == '\\') {
          i++;
        } else if (ch == '"') {
          inString = false;
        }
      } else if (ch == '"') {
        inString = true;
      } else if (ch == '[' || ch == '{' || ch == '(') {
        depth++;
      } else if (ch == ']' || ch == '}' || ch == ')') {
        depth--;
      } else if (ch == ',' && depth == 0) {
        return true;
      }
    }
    return false;
  }

  /** Whether a value stands before the part <code>i</code>, which it would be multiplied with. */
  private static boolean hasPredecessor(List<IExpr> parts, int i) {
    for (int j = i - 1; j >= 0; j--) {
      IExpr part = parts.get(j);
      if (part.isString()) {
        String text = part.toString();
        if (text.equals("\n") || text.equals(",") || text.equals(";") || text.equals("{")
            || text.equals("(") || text.equals("[")) {
          return false;
        }
        if (codeText(text).trim().isEmpty()) {
          continue;
        }
      }
      return true;
    }
    return false;
  }

  /** <code>{lower, upper}</code>, an empty array for an integral without limits, or <code>null</code>. */
  private String[] integralLimits(IExpr part) {
    if (isCharacter(part, INTEGRAL)) {
      return new String[0];
    }
    if (isHead(part, "subsuperscriptbox")) {
      List<IExpr> args = positional((IAST) part);
      if (args.size() == 3 && isCharacter(args.get(0), INTEGRAL)) {
        return new String[] {source(args.get(1)).trim(), source(args.get(2)).trim()};
      }
    }
    return null;
  }

  /** <code>{integrand, variable}</code> of the boxes behind an integral sign, or <code>null</code>. */
  private String[] integralBody(List<IExpr> parts) {
    if (parts.size() == 1 && isHead(parts.get(0), "rowbox")
        && ((IAST) parts.get(0)).argSize() >= 1 && parts.get(0).first().isList()) {
      return integralBody(elements((IAST) parts.get(0).first()));
    }
    if (parts.size() < 2) {
      return null;
    }
    // the last part is the row of the differential and its variable
    IExpr last = parts.get(parts.size() - 1);
    if (!isHead(last, "rowbox") || ((IAST) last).argSize() < 1 || !last.first().isList()) {
      return null;
    }
    List<IExpr> differential = elements((IAST) last.first());
    if (differential.size() == 3 && differential.get(1).isString()
        && differential.get(1).toString().trim().isEmpty()) {
      differential.remove(1);
    }
    if (differential.size() != 2 || !(isCharacter(differential.get(0), DIFFERENTIAL_D)
        || isCharacter(differential.get(0), 'ⅆ'))) {
      return null;
    }
    String integrand = rowSource(parts.subList(0, parts.size() - 1));
    return integrand.trim().isEmpty() ? null
        : new String[] {integrand, source(differential.get(1)).trim()};
  }

  /** The arguments of <code>f ( x , y )</code>, or <code>null</code> if the row has no comma. */
  private List<String> callArguments(IExpr part) {
    if (!isHead(part, "rowbox") || ((IAST) part).argSize() < 1 || !part.first().isList()) {
      return null;
    }
    List<IExpr> elements = elements((IAST) part.first());
    boolean comma = false;
    for (IExpr element : elements) {
      comma |= isText(element, ",");
    }
    if (!comma) {
      return null;
    }
    List<String> arguments = new ArrayList<String>();
    StringBuilder current = new StringBuilder();
    for (IExpr element : elements) {
      if (isText(element, ",")) {
        arguments.add(current.toString());
        current.setLength(0);
      } else {
        current.append(source(element));
      }
    }
    arguments.add(current.toString());
    return arguments;
  }

  /** The source of a row: its parts one after the other. */
  private String rowSource(List<IExpr> parts) {
    StringBuilder result = new StringBuilder();
    int i = 0;
    while (i < parts.size()) {
      IExpr part = parts.get(i);
      if (i + 1 < parts.size()) {
        // a sum or a product stands before its body, which is the rest of the row
        String[] operator = bigOperator(part);
        if (operator != null) {
          String body = rowSource(parts.subList(i + 1, parts.size()));
          juxtapose(result, operator[0] + "[" + body + ", " + operator[1] + "]");
          break;
        }
        // so does the integral sign; the differential which ends the body names the variable
        String[] limits = integralLimits(part);
        if (limits != null) {
          String[] body = integralBody(parts.subList(i + 1, parts.size()));
          if (body != null) {
            String iterator = limits.length == 0 ? body[1]
                : "{" + body[1] + ", " + limits[0] + ", " + limits[1] + "}";
            juxtapose(result, "Integrate[" + body[0] + ", " + iterator + "]");
            break;
          }
        }
        // and the partial derivative operator
        if (isHead(part, "subscriptbox")) {
          List<IExpr> args = positional((IAST) part);
          if (args.size() == 2 && isCharacter(args.get(0), PARTIAL_D)) {
            String body = rowSource(parts.subList(i + 1, parts.size()));
            result.append("(D[").append(body).append(", ").append(source(args.get(1)))
                .append("])");
            break;
          }
        }
      }
      if (i + 2 < parts.size() && isCharacter(part, LEFT_BAR)
          && isCharacter(parts.get(i + 2), RIGHT_BAR)) {
        juxtapose(result, "Abs[" + source(parts.get(i + 1)) + "]");
        i += 3;
        continue;
      }
      if (i + 3 < parts.size() && isText(parts.get(i + 1), "(")
          && isText(parts.get(i + 3), ")")) {
        // f(x, y) written the way a textbook writes it
        List<String> arguments = callArguments(parts.get(i + 2));
        if (arguments != null) {
          result.append(source(part)).append('[').append(String.join(", ", arguments))
              .append(']');
          i += 4;
          continue;
        }
      }
      String piece = source(part);
      if (isHead(part, "rowbox") && hasPredecessor(parts, i) && hasTopLevelAssignment(piece)
          && !hasTopLevelComma(piece)) {
        // a row of its own is a group: its assignment must not take what stands before it
        int semicolons = 0;
        while (semicolons < piece.length()
            && piece.charAt(piece.length() - 1 - semicolons) == ';') {
          semicolons++;
        }
        piece = semicolons % 2 == 1 ? "(" + piece.substring(0, piece.length() - 1) + ");"
            : "(" + piece + ")";
      }
      juxtapose(result, piece);
      i++;
    }
    return result.toString();
  }

  // ------------------------------------------------------------------ graphics

  /** <code>Uncompress["..."]</code> for <code>CompressedData["..."]</code>, the data else. */
  private static IExpr data(IExpr data) {
    if (isHead(data, "compresseddata") && data.isAST1() && data.first().isString()) {
      String text = data.first().toString();
      StringBuilder buf = new StringBuilder(text.length());
      for (int i = 0; i < text.length(); i++) {
        // the notebook breaks the lines of the text
        if (!Character.isWhitespace(text.charAt(i))) {
          buf.append(text.charAt(i));
        }
      }
      return F.unaryAST1(S.Uncompress, F.stringx(buf.toString()));
    }
    return data;
  }

  /** The image or the graphics a <code>GraphicsBox</code> is the box of. */
  private static IExpr graphics(IAST box) {
    IExpr image = image(box);
    return image != null ? image : vector(box);
  }

  /**
   * The arguments <code>data, type, options...</code> of the image which
   * <code>GraphicsBox[TagBox[RasterBox[data, rectangle, range, ...], BoxForm`ImageTag[type, options...], ...], ...]</code>
   * is the box of - or, with a <code>Raster3DBox</code> in a <code>Graphics3DBox</code>, of the
   * 3D image.
   *
   * @param rasterHead <code>"rasterbox"</code> or <code>"raster3dbox"</code>
   * @return <code>null</code> if the box isn't the box of an image
   */
  private static List<IExpr> imageArguments(IAST box, String rasterHead) {
    if (box.argSize() < 1 || !isHead(box.arg1(), "tagbox")) {
      return null;
    }
    IAST tagBox = (IAST) box.arg1();
    if (tagBox.argSize() < 2 || !isHead(tagBox.arg1(), rasterHead)) {
      return null;
    }
    IAST imageTag = null;
    for (int i = 2; i < tagBox.size(); i++) {
      if (isHead(tagBox.get(i), "imagetag")) {
        imageTag = (IAST) tagBox.get(i);
        break;
      }
    }
    IAST raster = (IAST) tagBox.arg1();
    if (imageTag == null || raster.argSize() < 1) {
      return null;
    }
    List<IExpr> arguments = new ArrayList<IExpr>(imageTag.argSize() + 1);
    arguments.add(data(raster.arg1()));
    for (int i = 1; i < imageTag.size(); i++) {
      IExpr argument = imageTag.get(i);
      if (i == 1 || argument.isRuleAST()) {
        arguments.add(argument);
      }
    }
    return arguments;
  }

  /**
   * The image a <code>GraphicsBox</code> is the box of: <code>Image[data, type, options...]</code>.
   *
   * @return <code>null</code> if the box isn't the box of an image
   */
  private static IExpr image(IAST box) {
    List<IExpr> arguments = imageArguments(box, "rasterbox");
    if (arguments == null) {
      return null;
    }
    IASTAppendable image = F.ast(S.Image, arguments.size());
    for (IExpr argument : arguments) {
      image.append(argument);
    }
    // the rectangle {{0, h}, {w, 0}} of an image says that its rows run from the top; one which
    // isn't turned over has them from the bottom
    IAST raster = (IAST) ((IAST) box.arg1()).arg1();
    if (raster.argSize() >= 2 && raster.arg2().isList2() && raster.arg2().first().isList2()
        && raster.arg2().second().isList2()) {
      double y0 = raster.arg2().first().second().evalfNaN();
      double y1 = raster.arg2().second().second().evalfNaN();
      if (y0 < y1) {
        return F.binaryAST2(S.ImageReflect, image, F.Rule(S.Top, S.Bottom));
      }
    }
    return image;
  }

  /** The primitive which a box of a graphics is the box of. */
  private static ISymbol primitive(String head) {
    switch (head) {
      case "graphicsbox":
        return S.Graphics;
      case "pointbox":
        return S.Point;
      case "linebox":
        return S.Line;
      case "diskbox":
        return S.Disk;
      case "rectanglebox":
        return S.Rectangle;
      case "polygonbox":
        return S.Polygon;
      case "circlebox":
        return S.Circle;
      case "arrowbox":
        return S.Arrow;
      case "insetbox":
        return S.Inset;
      case "beziercurvebox":
        return S.BezierCurve;
      case "rasterbox":
        return S.Raster;
      default:
        return null;
    }
  }

  /** The graphics of a <code>GraphicsBox</code> of primitives. */
  private static IExpr vector(IExpr box) {
    if (!box.isAST()) {
      return box;
    }
    IAST ast = (IAST) box;
    String head = headName(ast);
    if (head != null) {
      if (head.equals("compresseddata")) {
        return data(ast);
      }
      if (head.equals("tagbox") && ast.argSize() >= 1) {
        return vector(ast.arg1());
      }
      if (head.equals("stylebox") && ast.argSize() >= 1) {
        // the directives stand before what they style
        IASTAppendable list = F.ListAlloc(ast.argSize());
        for (int i = 2; i < ast.size(); i++) {
          if (!ast.get(i).isRuleAST()) {
            list.append(vector(ast.get(i)));
          }
        }
        list.append(vector(ast.arg1()));
        return list;
      }
      if (head.equals("filledcurvebox") && ast.argSize() >= 2) {
        return F.unaryAST1(S.Polygon, vector(ast.arg2()));
      }
      if (head.equals("joinedcurvebox") && ast.argSize() >= 2) {
        return F.unaryAST1(S.JoinedCurve, F.List(F.unaryAST1(S.Line, vector(ast.arg2()))));
      }
      ISymbol primitive = primitive(head);
      if (primitive != null) {
        IASTAppendable result = F.ast(primitive, ast.argSize());
        for (int i = 1; i < ast.size(); i++) {
          result.append(vector(ast.get(i)));
        }
        return result;
      }
    }
    IASTAppendable result = F.ast(ast.head(), ast.argSize());
    for (int i = 1; i < ast.size(); i++) {
      result.append(vector(ast.get(i)));
    }
    return result;
  }
}
