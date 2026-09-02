package org.matheclipse.parser.client.operator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The one description of Symja's operators: which token spells an operator, what it is called, in
 * which position it can be read, how tightly it binds and how it groups.
 *
 * <p>
 * Both operator tables are built from these rows - {@link ASTNodeFactory} here and
 * {@code ExprParserFactory} in matheclipse-core - so that the two parsers cannot describe different
 * languages. They previously kept a copy each, as three index-aligned {@code String[]}/
 * {@code Operator[]} arrays, and had drifted: {@code Information} ({@code ?}, {@code ??}) and
 * {@code Conditioned} exist only in the core table, so {@code ?x} parses with one parser and not
 * the other and has no operator form on output; and a name inserted into one array but not the
 * others shifted every row after it, which is how {@code get("Get")} came to return the {@code ??}
 * operator.
 *
 * <p>
 * Rows are data, not behaviour. An operator whose expansion is more than "wrap the arguments in a
 * call to the head" - {@code Divide}, {@code Subtract}, {@code TagSet}, {@code ~} - still needs a
 * class of its own; the table says which head to look that class up under, and each factory maps
 * heads to its own node types.
 *
 * <p>
 * The precedences here are Symja's, cross-checked against Mathics3 by {@code OperatorTableReport}.
 */
public final class OperatorTable {

  /** The position an operator is read in, relative to its operands. */
  public enum Affix {
    PREFIX, INFIX, POSTFIX
  }

  /**
   * How repeated applications of one infix operator group.
   *
   * <p>
   * Spelled as an enum because the {@code int} constants it replaces were a trap: {@link Operator}
   * and {@link InfixOperator} both declare {@code LEFT_ASSOCIATIVE}, with different values, and two
   * of those values collide across the two sets.
   */
  public enum Grouping {
    /** Arguments collect into one flat call - {@code a+b+c} is {@code Plus(a,b,c)}. */
    FLAT(InfixOperator.NONE),
    /** {@code a-b-c} is {@code (a-b)-c}. */
    LEFT(InfixOperator.LEFT_ASSOCIATIVE),
    /** {@code a->b->c} is {@code a->(b->c)}. */
    RIGHT(InfixOperator.RIGHT_ASSOCIATIVE);

    private final int grouping;

    Grouping(int grouping) {
      this.grouping = grouping;
    }

    /** The value the {@code InfixOperator} constructors take. */
    public int toInfixOperatorConstant() {
      return grouping;
    }
  }

  /** One operator: a token, the head it expands to, and how it binds. */
  public static final class Row {
    /** The operator's own spelling, the one an operator form prints back out as. */
    public final String token;

    /**
     * Further spellings of the <em>same</em> operator, typically the unicode character for the
     * head. A factory must register one operator instance under the token and every alias: the
     * parser decides whether a chain flattens by comparing operator identity, so registering a
     * separate instance per spelling would make {@code a≤b≤c} nest where {@code a<=b<=c} flattens.
     */
    public final List<String> aliases;

    /** Name of the function the operator expands to, or a sentinel such as {@code //}. */
    public final String head;

    public final Affix affix;

    public final int precedence;

    /** Meaningful for {@link Affix#INFIX} only; {@link Grouping#FLAT} otherwise. */
    public final Grouping grouping;

    /**
     * Whether this row is the operator form its head prints as, rather than only a spelling the
     * parser accepts.
     *
     * <p>
     * A token that renders as a visible character is both, which is almost every row. The
     * exceptions are the operators whose only WMA spelling is a private-use character (U+E000
     * to U+F8FF): those render as nothing outside WMA's own fonts, so an operator form built from
     * one loses the head. {@code Conjugate(a)} would come out as bare {@code a}, and
     * {@code Xnor(a,b)} as {@code ab}, which reads as one symbol. Such a row is registered for
     * parsing only, and the head keeps printing in function form.
     */
    public final boolean outputForm;

    Row(String token, String head, Affix affix, int precedence, Grouping grouping,
        boolean outputForm, String... aliases) {
      this.token = token;
      this.head = head;
      this.affix = affix;
      this.precedence = precedence;
      this.grouping = grouping;
      this.outputForm = outputForm;
      this.aliases = Collections.unmodifiableList(Arrays.asList(aliases));
    }
  }

  private static Row row(String token, String head, Affix affix, int precedence, Grouping grouping,
      String... aliases) {
    return new Row(token, head, affix, precedence, grouping, true, aliases);
  }

  /**
   * A row the parser accepts but which never becomes its head's output form; see
   * {@link Row#outputForm}.
   */
  private static Row inputOnlyRow(String token, String head, Affix affix, int precedence,
      Grouping grouping, String... aliases) {
    return new Row(token, head, affix, precedence, grouping, false, aliases);
  }

  private OperatorTable() {}

  /** Every operator Symja parses, ordered by token. */
  public static final List<Row> ROWS = Collections.unmodifiableList(Arrays.asList(//
      row("!!", "Factorial2", Affix.POSTFIX, 610, Grouping.FLAT),
      row("!", "Factorial", Affix.POSTFIX, 610, Grouping.FLAT),
      row("!", "Not", Affix.PREFIX, 230, Grouping.FLAT, "\u00ac"),
      row("!=", "Unequal", Affix.INFIX, 290, Grouping.FLAT),
      row("&", "Function", Affix.POSTFIX, 90, Grouping.FLAT, "\uf4a1"),
      row("&&", "And", Affix.INFIX, 225, Grouping.FLAT, "\u2227"),
      row("*", "Times", Affix.INFIX, 400, Grouping.FLAT, "\u00d7"),
      row("**", "NonCommutativeMultiply", Affix.INFIX, 510, Grouping.FLAT),
      row("*=", "TimesBy", Affix.INFIX, 100, Grouping.RIGHT),
      row("+", "Plus", Affix.INFIX, 310, Grouping.FLAT),
      row("+", "PrePlus", Affix.PREFIX, 670, Grouping.FLAT),
      row("++", "Increment", Affix.POSTFIX, 660, Grouping.FLAT),
      row("++", "PreIncrement", Affix.PREFIX, 660, Grouping.FLAT),
      row("+=", "AddTo", Affix.INFIX, 100, Grouping.RIGHT),
      row("-", "PreMinus", Affix.PREFIX, 485, Grouping.FLAT),
      row("-", "Subtract", Affix.INFIX, 310, Grouping.LEFT),
      row("--", "Decrement", Affix.POSTFIX, 660, Grouping.FLAT),
      row("--", "PreDecrement", Affix.PREFIX, 660, Grouping.FLAT),
      row("-=", "SubtractFrom", Affix.INFIX, 100, Grouping.RIGHT),
      row("->", "Rule", Affix.INFIX, 120, Grouping.RIGHT, "\uf522"),
      row(".", "Dot", Affix.INFIX, 490, Grouping.FLAT),
      row("..", "Repeated", Affix.POSTFIX, 170, Grouping.FLAT),
      row("...", "RepeatedNull", Affix.POSTFIX, 170, Grouping.FLAT),
      row("/", "Divide", Affix.INFIX, 470, Grouping.LEFT, "\u00f7"),
      row("/*", "RightComposition", Affix.INFIX, 624, Grouping.FLAT),
      row("/.", "ReplaceAll", Affix.INFIX, 110, Grouping.LEFT),
      row("//", "//", Affix.INFIX, 70, Grouping.LEFT),
      row("//.", "ReplaceRepeated", Affix.INFIX, 110, Grouping.LEFT),
      row("//@", "MapAll", Affix.INFIX, 620, Grouping.RIGHT),
      row("/:", "TagSet", Affix.INFIX, 40, Grouping.FLAT),
      row("/;", "Condition", Affix.INFIX, 130, Grouping.LEFT),
      row("/=", "DivideBy", Affix.INFIX, 100, Grouping.RIGHT),
      row("/@", "Map", Affix.INFIX, 620, Grouping.RIGHT),
      row(":", "Pattern", Affix.INFIX, 150, Grouping.FLAT),
      row("::", "MessageName", Affix.INFIX, 750, Grouping.FLAT),
      row(":=", "SetDelayed", Affix.INFIX, 40, Grouping.RIGHT),
      row(":>", "RuleDelayed", Affix.INFIX, 120, Grouping.RIGHT, "\uf51f"),
      row(";", "CompoundExpression", Affix.INFIX, 10, Grouping.FLAT),
      row(";;", "Span", Affix.INFIX, 305, Grouping.FLAT),
      row("<", "Less", Affix.INFIX, 290, Grouping.FLAT),
      row("<->", "TwoWayRule", Affix.INFIX, 125, Grouping.RIGHT, "\uf120"),
      row("<<", "Get", Affix.PREFIX, 720, Grouping.FLAT),
      row("<=", "LessEqual", Affix.INFIX, 290, Grouping.FLAT, "\u2264"),
      row("<>", "StringJoin", Affix.INFIX, 600, Grouping.FLAT),
      row("=!=", "UnsameQ", Affix.INFIX, 290, Grouping.FLAT),
      row("=", "Set", Affix.INFIX, 40, Grouping.RIGHT),
      row("=.", "Unset", Affix.POSTFIX, 670, Grouping.FLAT),
      row("==", "Equal", Affix.INFIX, 290, Grouping.FLAT, "\uf431"),
      row("===", "SameQ", Affix.INFIX, 290, Grouping.FLAT),
      row(">", "Greater", Affix.INFIX, 290, Grouping.FLAT),
      row(">=", "GreaterEqual", Affix.INFIX, 290, Grouping.FLAT, "\u2265"),
      row(">>", "Put", Affix.INFIX, 30, Grouping.LEFT),
      row(">>>", "PutAppend", Affix.INFIX, 30, Grouping.LEFT),
      row("?", "Information", Affix.PREFIX, 720, Grouping.FLAT),
      row("?", "PatternTest", Affix.INFIX, 680, Grouping.LEFT),
      row("??", "Information", Affix.PREFIX, 720, Grouping.FLAT),
      row("@", "Apply", Affix.INFIX, 621, Grouping.RIGHT),
      row("@*", "Composition", Affix.INFIX, 625, Grouping.FLAT),
      row("@@", "Apply", Affix.INFIX, 620, Grouping.RIGHT),
      row("@@@", "MapApply", Affix.INFIX, 620, Grouping.RIGHT),
      // The old table also listed "\u001b" (ESC) as a PlusMinus token - a typo for "\u00b1".
      // It was registered but unreachable, because ESC never became an operator character, and
      // it is left out here rather than carried forward.
      row("\u00b1", "PlusMinus", Affix.INFIX, 310, Grouping.LEFT),
      row("\u00b1", "PlusMinus", Affix.PREFIX, 310, Grouping.FLAT),
      row("\u00b7", "CenterDot", Affix.INFIX, 410, Grouping.FLAT),
      row("\u2190", "LeftArrow", Affix.INFIX, 270, Grouping.FLAT),
      row("\u2191", "ShortUpArrow", Affix.INFIX, 580, Grouping.FLAT, "\uf52a"),
      row("\u2192", "RightArrow", Affix.INFIX, 270, Grouping.FLAT),
      row("\u2193", "DownArrow", Affix.INFIX, 580, Grouping.FLAT),
      row("\u2194", "LeftRightArrow", Affix.INFIX, 270, Grouping.FLAT),
      row("\u2195", "UpDownArrow", Affix.INFIX, 580, Grouping.FLAT),
      row("\u2196", "UpperLeftArrow", Affix.INFIX, 270, Grouping.FLAT),
      row("\u2197", "UpperRightArrow", Affix.INFIX, 270, Grouping.FLAT),
      row("\u2198", "LowerRightArrow", Affix.INFIX, 270, Grouping.FLAT),
      row("\u2199", "LowerLeftArrow", Affix.INFIX, 270, Grouping.FLAT),
      row("\u21a4", "LeftTeeArrow", Affix.INFIX, 270, Grouping.FLAT),
      row("\u21a5", "UpTeeArrow", Affix.INFIX, 580, Grouping.FLAT),
      row("\u21a6", "RightTeeArrow", Affix.INFIX, 270, Grouping.FLAT),
      row("\u21a7", "DownTeeArrow", Affix.INFIX, 580, Grouping.FLAT),
      row("\u21bc", "LeftVector", Affix.INFIX, 270, Grouping.FLAT),
      row("\u21bd", "DownLeftVector", Affix.INFIX, 270, Grouping.FLAT),
      row("\u21be", "RightUpVector", Affix.INFIX, 580, Grouping.FLAT),
      row("\u21bf", "LeftUpVector", Affix.INFIX, 580, Grouping.FLAT),
      row("\u21c0", "RightVector", Affix.INFIX, 270, Grouping.FLAT),
      row("\u21c1", "DownRightVector", Affix.INFIX, 270, Grouping.FLAT),
      row("\u21c2", "RightDownVector", Affix.INFIX, 580, Grouping.FLAT),
      row("\u21c3", "LeftDownVector", Affix.INFIX, 580, Grouping.FLAT),
      row("\u21c4", "RightArrowLeftArrow", Affix.INFIX, 270, Grouping.FLAT),
      row("\u21c5", "UpArrowDownArrow", Affix.INFIX, 580, Grouping.FLAT),
      row("\u21c6", "LeftArrowRightArrow", Affix.INFIX, 270, Grouping.FLAT),
      row("\u21cb", "ReverseEquilibrium", Affix.INFIX, 290, Grouping.FLAT),
      row("\u21cc", "Equilibrium", Affix.INFIX, 290, Grouping.FLAT),
      row("\u21d0", "DoubleLeftArrow", Affix.INFIX, 270, Grouping.FLAT),
      row("\u21d1", "DoubleUpArrow", Affix.INFIX, 580, Grouping.FLAT),
      row("\u21d2", "DoubleRightArrow", Affix.INFIX, 270, Grouping.FLAT),
      row("\u21d3", "DoubleDownArrow", Affix.INFIX, 580, Grouping.FLAT),
      row("\u21d4", "DoubleLeftRightArrow", Affix.INFIX, 270, Grouping.FLAT),
      row("\u21d5", "DoubleUpDownArrow", Affix.INFIX, 580, Grouping.FLAT),
      row("\u21e4", "LeftArrowBar", Affix.INFIX, 270, Grouping.FLAT),
      row("\u21e5", "RightArrowBar", Affix.INFIX, 270, Grouping.FLAT),
      row("\u21f5", "DownArrowUpArrow", Affix.INFIX, 580, Grouping.FLAT),
      row("\u2200", "ForAll", Affix.PREFIX, 240, Grouping.FLAT),
      row("\u2202", "PartialD", Affix.PREFIX, 550, Grouping.FLAT),
      row("\u2203", "Exists", Affix.PREFIX, 240, Grouping.FLAT),
      row("\u2204", "NotExists", Affix.PREFIX, 240, Grouping.FLAT),
      row("\u2206", "DifferenceDelta", Affix.PREFIX, 550, Grouping.FLAT),
      row("\u2207", "Del", Affix.PREFIX, 550, Grouping.FLAT),
      row("\u2208", "Element", Affix.INFIX, 250, Grouping.FLAT),
      row("\u2209", "NotElement", Affix.INFIX, 250, Grouping.FLAT),
      row("\u220b", "ReverseElement", Affix.INFIX, 250, Grouping.FLAT),
      row("\u220c", "NotReverseElement", Affix.INFIX, 250, Grouping.FLAT),
      row("\u220d", "SuchThat", Affix.INFIX, 180, Grouping.RIGHT),
      row("\u220f", "Product", Affix.PREFIX, 380, Grouping.FLAT),
      row("\u2210", "Coproduct", Affix.INFIX, 360, Grouping.FLAT),
      row("\u2211", "Sum", Affix.PREFIX, 325, Grouping.FLAT),
      row("\u2212", "Minus", Affix.PREFIX, 480, Grouping.FLAT),
      row("\u2213", "MinusPlus", Affix.INFIX, 310, Grouping.LEFT),
      row("\u2216", "Backslash", Affix.INFIX, 460, Grouping.FLAT),
      row("\u2218", "SmallCircle", Affix.INFIX, 530, Grouping.FLAT),
      row("\u221a", "Sqrt", Affix.PREFIX, 570, Grouping.FLAT),
      row("\u221d", "Proportional", Affix.INFIX, 290, Grouping.FLAT),
      row("\u2223", "Divides", Affix.INFIX, 470, Grouping.FLAT),
      row("\u2225", "DoubleVerticalBar", Affix.INFIX, 280, Grouping.FLAT),
      row("\u2226", "NotDoubleVerticalBar", Affix.INFIX, 280, Grouping.FLAT),
      row("\u2234", "Therefore", Affix.INFIX, 50, Grouping.RIGHT),
      row("\u2235", "Because", Affix.INFIX, 50, Grouping.LEFT),
      row("\u2236", "Colon", Affix.INFIX, 80, Grouping.FLAT),
      row("\u2237", "Proportion", Affix.INFIX, 290, Grouping.FLAT),
      row("\u223c", "Tilde", Affix.INFIX, 290, Grouping.FLAT),
      row("\u2240", "VerticalTilde", Affix.INFIX, 370, Grouping.FLAT),
      row("\u2241", "NotTilde", Affix.INFIX, 290, Grouping.FLAT),
      row("\u2242", "EqualTilde", Affix.INFIX, 290, Grouping.FLAT),
      row("\u2243", "TildeEqual", Affix.INFIX, 290, Grouping.FLAT),
      row("\u2244", "NotTildeEqual", Affix.INFIX, 290, Grouping.FLAT),
      row("\u2245", "TildeFullEqual", Affix.INFIX, 290, Grouping.FLAT),
      row("\u2247", "NotTildeFullEqual", Affix.INFIX, 290, Grouping.FLAT),
      row("\u2248", "TildeTilde", Affix.INFIX, 290, Grouping.FLAT),
      row("\u2249", "NotTildeTilde", Affix.INFIX, 290, Grouping.FLAT),
      row("\u224d", "CupCap", Affix.INFIX, 290, Grouping.FLAT),
      row("\u224e", "HumpDownHump", Affix.INFIX, 290, Grouping.FLAT),
      row("\u224f", "HumpEqual", Affix.INFIX, 290, Grouping.FLAT),
      row("\u2250", "DotEqual", Affix.INFIX, 290, Grouping.FLAT),
      row("\u2260", "Unequal", Affix.INFIX, 290, Grouping.FLAT),
      row("\u2261", "Congruent", Affix.INFIX, 290, Grouping.FLAT),
      row("\u2262", "NotCongruent", Affix.INFIX, 290, Grouping.FLAT),
      row("\u2266", "LessFullEqual", Affix.INFIX, 290, Grouping.FLAT),
      row("\u2267", "GreaterFullEqual", Affix.INFIX, 290, Grouping.FLAT),
      row("\u2268", "NotLessFullEqual", Affix.INFIX, 290, Grouping.FLAT),
      row("\u2269", "NotGreaterFullEqual", Affix.INFIX, 290, Grouping.FLAT),
      row("\u226a", "LessLess", Affix.INFIX, 290, Grouping.FLAT),
      row("\u226b", "GreaterGreater", Affix.INFIX, 290, Grouping.FLAT),
      row("\u226d", "NotCupCap", Affix.INFIX, 290, Grouping.FLAT),
      row("\u226e", "NotLess", Affix.INFIX, 290, Grouping.FLAT),
      row("\u226f", "NotGreater", Affix.INFIX, 290, Grouping.FLAT),
      row("\u2270", "NotLessEqual", Affix.INFIX, 290, Grouping.FLAT),
      row("\u2271", "NotGreaterEqual", Affix.INFIX, 290, Grouping.FLAT),
      row("\u2272", "LessTilde", Affix.INFIX, 290, Grouping.FLAT),
      row("\u2273", "GreaterTilde", Affix.INFIX, 290, Grouping.FLAT),
      row("\u2274", "NotLessTilde", Affix.INFIX, 290, Grouping.FLAT),
      row("\u2275", "NotGreaterTilde", Affix.INFIX, 290, Grouping.FLAT),
      row("\u2276", "LessGreater", Affix.INFIX, 290, Grouping.FLAT),
      row("\u2277", "GreaterLess", Affix.INFIX, 290, Grouping.FLAT),
      row("\u2278", "NotLessGreater", Affix.INFIX, 290, Grouping.FLAT),
      row("\u2279", "NotGreaterLess", Affix.INFIX, 290, Grouping.FLAT),
      row("\u227a", "Precedes", Affix.INFIX, 290, Grouping.FLAT),
      row("\u227b", "Succeeds", Affix.INFIX, 290, Grouping.FLAT),
      row("\u227c", "PrecedesSlantEqual", Affix.INFIX, 290, Grouping.FLAT),
      row("\u227d", "SucceedsSlantEqual", Affix.INFIX, 290, Grouping.FLAT),
      row("\u227e", "PrecedesTilde", Affix.INFIX, 290, Grouping.FLAT),
      row("\u227f", "SucceedsTilde", Affix.INFIX, 290, Grouping.FLAT),
      row("\u2280", "NotPrecedes", Affix.INFIX, 290, Grouping.FLAT),
      row("\u2281", "NotSucceeds", Affix.INFIX, 290, Grouping.FLAT),
      row("\u2282", "Subset", Affix.INFIX, 250, Grouping.FLAT),
      row("\u2283", "Superset", Affix.INFIX, 250, Grouping.FLAT),
      row("\u2284", "NotSubset", Affix.INFIX, 250, Grouping.FLAT),
      row("\u2285", "NotSuperset", Affix.INFIX, 250, Grouping.FLAT),
      row("\u2286", "SubsetEqual", Affix.INFIX, 250, Grouping.FLAT),
      row("\u2287", "SupersetEqual", Affix.INFIX, 250, Grouping.FLAT),
      row("\u2288", "NotSubsetEqual", Affix.INFIX, 250, Grouping.FLAT),
      row("\u2289", "NotSupersetEqual", Affix.INFIX, 250, Grouping.FLAT),
      row("\u228e", "UnionPlus", Affix.INFIX, 300, Grouping.FLAT),
      row("\u228f", "SquareSubset", Affix.INFIX, 250, Grouping.FLAT),
      row("\u2290", "SquareSuperset", Affix.INFIX, 250, Grouping.FLAT),
      row("\u2291", "SquareSubsetEqual", Affix.INFIX, 250, Grouping.FLAT),
      row("\u2292", "SquareSupersetEqual", Affix.INFIX, 250, Grouping.FLAT),
      row("\u2293", "SquareIntersection", Affix.INFIX, 305, Grouping.FLAT),
      row("\u2294", "SquareUnion", Affix.INFIX, 300, Grouping.FLAT),
      row("\u2295", "CirclePlus", Affix.INFIX, 330, Grouping.FLAT),
      row("\u2296", "CircleMinus", Affix.INFIX, 330, Grouping.FLAT),
      row("\u2297", "CircleTimes", Affix.INFIX, 420, Grouping.FLAT),
      row("\u2299", "CircleDot", Affix.INFIX, 520, Grouping.FLAT),
      row("\u22a2", "RightTee", Affix.INFIX, 190, Grouping.RIGHT),
      row("\u22a3", "LeftTee", Affix.INFIX, 190, Grouping.LEFT),
      row("\u22a4", "DownTee", Affix.INFIX, 190, Grouping.LEFT),
      row("\u22a5", "UpTee", Affix.INFIX, 197, Grouping.LEFT),
      row("\u22a8", "DoubleRightTee", Affix.INFIX, 190, Grouping.RIGHT),
      row("\u22b2", "LeftTriangle", Affix.INFIX, 290, Grouping.FLAT),
      row("\u22b3", "RightTriangle", Affix.INFIX, 290, Grouping.FLAT),
      row("\u22b4", "LeftTriangleEqual", Affix.INFIX, 290, Grouping.FLAT),
      row("\u22b5", "RightTriangleEqual", Affix.INFIX, 290, Grouping.FLAT),
      row("\u22bb", "Xor", Affix.INFIX, 220, Grouping.FLAT),
      row("\u22bc", "Nand", Affix.INFIX, 225, Grouping.FLAT),
      row("\u22bd", "Nor", Affix.INFIX, 215, Grouping.FLAT),
      row("\u22c0", "Wedge", Affix.INFIX, 440, Grouping.FLAT),
      row("\u22c1", "Vee", Affix.INFIX, 430, Grouping.FLAT),
      row("\u22c2", "Intersection", Affix.INFIX, 305, Grouping.FLAT),
      row("\u22c3", "Union", Affix.INFIX, 300, Grouping.FLAT),
      row("\u22c4", "Diamond", Affix.INFIX, 450, Grouping.FLAT),
      row("\u22c6", "Star", Affix.INFIX, 390, Grouping.FLAT),
      row("\u22da", "LessEqualGreater", Affix.INFIX, 290, Grouping.FLAT),
      row("\u22db", "GreaterEqualLess", Affix.INFIX, 290, Grouping.FLAT),
      row("\u22e0", "NotPrecedesSlantEqual", Affix.INFIX, 290, Grouping.FLAT),
      row("\u22e1", "NotSucceedsSlantEqual", Affix.INFIX, 290, Grouping.FLAT),
      row("\u22e2", "NotSquareSubsetEqual", Affix.INFIX, 250, Grouping.FLAT),
      row("\u22e3", "NotSquareSupersetEqual", Affix.INFIX, 250, Grouping.FLAT),
      row("\u22e8", "NotPrecedesTilde", Affix.INFIX, 290, Grouping.FLAT),
      row("\u22e9", "NotSucceedsTilde", Affix.INFIX, 290, Grouping.FLAT),
      row("\u22ea", "NotLeftTriangle", Affix.INFIX, 290, Grouping.FLAT),
      row("\u22eb", "NotRightTriangle", Affix.INFIX, 290, Grouping.FLAT),
      row("\u22ec", "NotLeftTriangleEqual", Affix.INFIX, 290, Grouping.FLAT),
      row("\u22ed", "NotRightTriangleEqual", Affix.INFIX, 290, Grouping.FLAT),
      row("\u2322", "Cap", Affix.INFIX, 350, Grouping.FLAT),
      row("\u2323", "Cup", Affix.INFIX, 340, Grouping.FLAT),
      row("\u25ab", "Square", Affix.PREFIX, 540, Grouping.FLAT, "\uf520"),
      row("\u2758", "VerticalBar", Affix.INFIX, 280, Grouping.FLAT, "\uf3d0"),
      row("\u27c2", "Perpendicular", Affix.INFIX, 190, Grouping.FLAT),
      row("\u27f5", "LongLeftArrow", Affix.INFIX, 580, Grouping.FLAT),
      row("\u27f6", "LongRightArrow", Affix.INFIX, 580, Grouping.FLAT),
      row("\u27f7", "LongLeftRightArrow", Affix.INFIX, 580, Grouping.FLAT),
      row("\u27f8", "DoubleLongLeftArrow", Affix.INFIX, 580, Grouping.FLAT),
      row("\u27f9", "DoubleLongRightArrow", Affix.INFIX, 580, Grouping.FLAT),
      row("\u27fa", "DoubleLongLeftRightArrow", Affix.INFIX, 580, Grouping.FLAT),
      row("\u2912", "UpArrowBar", Affix.INFIX, 580, Grouping.FLAT),
      row("\u2913", "DownArrowBar", Affix.INFIX, 580, Grouping.FLAT),
      row("\u294e", "LeftRightVector", Affix.INFIX, 270, Grouping.FLAT),
      row("\u294f", "RightUpDownVector", Affix.INFIX, 580, Grouping.FLAT),
      row("\u2950", "DownLeftRightVector", Affix.INFIX, 270, Grouping.FLAT),
      row("\u2951", "LeftUpDownVector", Affix.INFIX, 580, Grouping.FLAT),
      row("\u2952", "LeftVectorBar", Affix.INFIX, 270, Grouping.FLAT),
      row("\u2953", "RightVectorBar", Affix.INFIX, 270, Grouping.FLAT),
      row("\u2954", "RightUpVectorBar", Affix.INFIX, 580, Grouping.FLAT),
      row("\u2955", "RightDownVectorBar", Affix.INFIX, 580, Grouping.FLAT),
      row("\u2956", "DownLeftVectorBar", Affix.INFIX, 270, Grouping.FLAT),
      row("\u2957", "DownRightVectorBar", Affix.INFIX, 270, Grouping.FLAT),
      row("\u2958", "LeftUpVectorBar", Affix.INFIX, 580, Grouping.FLAT),
      row("\u2959", "LeftDownVectorBar", Affix.INFIX, 580, Grouping.FLAT),
      row("\u295a", "LeftTeeVector", Affix.INFIX, 270, Grouping.FLAT),
      row("\u295b", "RightTeeVector", Affix.INFIX, 270, Grouping.FLAT),
      row("\u295c", "RightUpTeeVector", Affix.INFIX, 580, Grouping.FLAT),
      row("\u295d", "RightDownTeeVector", Affix.INFIX, 580, Grouping.FLAT),
      row("\u295e", "DownLeftTeeVector", Affix.INFIX, 270, Grouping.FLAT),
      row("\u295f", "DownRightTeeVector", Affix.INFIX, 270, Grouping.FLAT),
      row("\u2960", "LeftUpTeeVector", Affix.INFIX, 580, Grouping.FLAT),
      row("\u2961", "LeftDownTeeVector", Affix.INFIX, 580, Grouping.FLAT),
      row("\u296e", "UpEquilibrium", Affix.INFIX, 580, Grouping.FLAT),
      row("\u296f", "ReverseUpEquilibrium", Affix.INFIX, 580, Grouping.FLAT),
      row("\u2970", "RoundImplies", Affix.INFIX, 193, Grouping.RIGHT),
      row("\u29cf", "LeftTriangleBar", Affix.INFIX, 290, Grouping.FLAT),
      row("\u29d0", "RightTriangleBar", Affix.INFIX, 290, Grouping.FLAT),
      row("\u29e6", "Equivalent", Affix.INFIX, 205, Grouping.FLAT),
      row("\u2a2f", "Cross", Affix.INFIX, 500, Grouping.FLAT, "\uf4a0"),
      row("\u2a7d", "LessSlantEqual", Affix.INFIX, 290, Grouping.FLAT),
      row("\u2a7e", "GreaterSlantEqual", Affix.INFIX, 290, Grouping.FLAT),
      row("\u2aa1", "NestedLessLess", Affix.INFIX, 290, Grouping.FLAT),
      row("\u2aa2", "NestedGreaterGreater", Affix.INFIX, 290, Grouping.FLAT),
      row("\u2aaf", "PrecedesEqual", Affix.INFIX, 290, Grouping.FLAT),
      row("\u2ab0", "SucceedsEqual", Affix.INFIX, 290, Grouping.FLAT),
      row("\u2ae4", "DoubleLeftTee", Affix.INFIX, 190, Grouping.LEFT),
      row("\uf120", "TwoWayRule", Affix.INFIX, 125, Grouping.RIGHT),
      inputOnlyRow("\uf361", "Piecewise", Affix.PREFIX, 535, Grouping.FLAT),
      row("\uf3c7", "Transpose", Affix.POSTFIX, 670, Grouping.FLAT),
      inputOnlyRow("\uf3c8", "Conjugate", Affix.POSTFIX, 670, Grouping.FLAT),
      inputOnlyRow("\uf3c9", "ConjugateTranspose", Affix.POSTFIX, 670, Grouping.FLAT),
      inputOnlyRow("\uf3d1", "NotVerticalBar", Affix.INFIX, 280, Grouping.FLAT),
      row("\uf3d2", "Distributed", Affix.INFIX, 250, Grouping.FLAT),
      row("\uf3d3", "Conditioned", Affix.INFIX, 195, Grouping.FLAT),
      row("\uf3d4", "UndirectedEdge", Affix.INFIX, 295, Grouping.RIGHT),
      row("\uf3d5", "DirectedEdge", Affix.INFIX, 295, Grouping.RIGHT),
      row("\uf3da", "TensorProduct", Affix.INFIX, 487, Grouping.FLAT),
      inputOnlyRow("\uf3db", "TensorWedge", Affix.INFIX, 488, Grouping.FLAT),
      inputOnlyRow("\uf3de", "PermutationProduct", Affix.INFIX, 486, Grouping.FLAT),
      inputOnlyRow("\uf400", "NotEqualTilde", Affix.INFIX, 290, Grouping.FLAT),
      inputOnlyRow("\uf401", "NotHumpEqual", Affix.INFIX, 290, Grouping.FLAT),
      inputOnlyRow("\uf402", "NotHumpDownHump", Affix.INFIX, 290, Grouping.FLAT),
      inputOnlyRow("\uf412", "NotLeftTriangleBar", Affix.INFIX, 290, Grouping.FLAT),
      inputOnlyRow("\uf413", "NotRightTriangleBar", Affix.INFIX, 290, Grouping.FLAT),
      inputOnlyRow("\uf422", "NotLessLess", Affix.INFIX, 290, Grouping.FLAT),
      inputOnlyRow("\uf423", "NotNestedLessLess", Affix.INFIX, 290, Grouping.FLAT),
      inputOnlyRow("\uf424", "NotLessSlantEqual", Affix.INFIX, 290, Grouping.FLAT),
      inputOnlyRow("\uf427", "NotGreaterGreater", Affix.INFIX, 290, Grouping.FLAT),
      inputOnlyRow("\uf428", "NotNestedGreaterGreater", Affix.INFIX, 290, Grouping.FLAT),
      inputOnlyRow("\uf429", "NotGreaterSlantEqual", Affix.INFIX, 290, Grouping.FLAT),
      inputOnlyRow("\uf42b", "NotPrecedesEqual", Affix.INFIX, 290, Grouping.FLAT),
      inputOnlyRow("\uf42d", "NotSucceedsEqual", Affix.INFIX, 290, Grouping.FLAT),
      inputOnlyRow("\uf42e", "NotSquareSubset", Affix.INFIX, 250, Grouping.FLAT),
      inputOnlyRow("\uf42f", "NotSquareSuperset", Affix.INFIX, 250, Grouping.FLAT),
      inputOnlyRow("\uf432", "VerticalSeparator", Affix.INFIX, 60, Grouping.FLAT),
      inputOnlyRow("\uf439", "MaxLimit", Affix.PREFIX, 320, Grouping.FLAT),
      inputOnlyRow("\uf43a", "MinLimit", Affix.PREFIX, 320, Grouping.FLAT),
      inputOnlyRow("\uf4a2", "Xnor", Affix.INFIX, 220, Grouping.FLAT),
      inputOnlyRow("\uf4a3", "DiscreteShift", Affix.PREFIX, 550, Grouping.FLAT),
      inputOnlyRow("\uf4a4", "DiscreteRatio", Affix.PREFIX, 550, Grouping.FLAT),
      row("\uf523", "Implies", Affix.INFIX, 200, Grouping.RIGHT),
      inputOnlyRow("\uf525", "ShortRightArrow", Affix.INFIX, 270, Grouping.FLAT),
      inputOnlyRow("\uf526", "ShortLeftArrow", Affix.INFIX, 270, Grouping.FLAT),
      inputOnlyRow("\uf52b", "ShortDownArrow", Affix.INFIX, 580, Grouping.FLAT),
      inputOnlyRow("\uf74b", "CapitalDifferentialD", Affix.PREFIX, 550, Grouping.FLAT),
      inputOnlyRow("\uf74c", "DifferentialD", Affix.PREFIX, 550, Grouping.FLAT),
      row("^", "Power", Affix.INFIX, 590, Grouping.RIGHT),
      row("^:=", "UpSetDelayed", Affix.INFIX, 40, Grouping.RIGHT),
      row("^=", "UpSet", Affix.INFIX, 40, Grouping.RIGHT),
      row("|", "Alternatives", Affix.INFIX, 160, Grouping.FLAT),
      row("|->", "Function", Affix.INFIX, 90, Grouping.RIGHT, "\uf4a1"),
      row("||", "Or", Affix.INFIX, 215, Grouping.FLAT, "\u2228"),
      row("~", "\u00a7TILDE\u00a7", Affix.INFIX, 630, Grouping.FLAT),
      row("~~", "StringExpression", Affix.INFIX, 135, Grouping.FLAT)));
}
