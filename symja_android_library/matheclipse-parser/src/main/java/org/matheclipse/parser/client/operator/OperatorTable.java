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

    Row(String token, String head, Affix affix, int precedence, Grouping grouping,
        String... aliases) {
      this.token = token;
      this.head = head;
      this.affix = affix;
      this.precedence = precedence;
      this.grouping = grouping;
      this.aliases = Collections.unmodifiableList(Arrays.asList(aliases));
    }
  }

  private static Row row(String token, String head, Affix affix, int precedence, Grouping grouping,
      String... aliases) {
    return new Row(token, head, affix, precedence, grouping, aliases);
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
      row("\u2200", "ForAll", Affix.PREFIX, 240, Grouping.FLAT),
      row("\u2203", "Exists", Affix.PREFIX, 240, Grouping.FLAT),
      row("\u2206", "DifferenceDelta", Affix.PREFIX, 550, Grouping.FLAT),
      row("\u2208", "Element", Affix.INFIX, 250, Grouping.FLAT),
      row("\u2209", "NotElement", Affix.INFIX, 250, Grouping.FLAT),
      row("\u220d", "SuchThat", Affix.INFIX, 180, Grouping.RIGHT),
      row("\u220f", "Product", Affix.PREFIX, 380, Grouping.FLAT),
      row("\u2211", "Sum", Affix.PREFIX, 325, Grouping.FLAT),
      row("\u221a", "Sqrt", Affix.PREFIX, 570, Grouping.FLAT),
      row("\u2234", "Therefore", Affix.INFIX, 50, Grouping.RIGHT),
      row("\u2260", "Unequal", Affix.INFIX, 290, Grouping.FLAT),
      row("\u2297", "CircleTimes", Affix.INFIX, 420, Grouping.FLAT),
      row("\u2299", "CircleDot", Affix.INFIX, 520, Grouping.FLAT),
      row("\u22a2", "RightTee", Affix.INFIX, 190, Grouping.RIGHT),
      row("\u22a5", "UpTee", Affix.INFIX, 197, Grouping.LEFT),
      row("\u22c0", "Wedge", Affix.INFIX, 440, Grouping.FLAT),
      row("\u22c2", "Intersection", Affix.INFIX, 305, Grouping.FLAT),
      row("\u22c6", "Star", Affix.INFIX, 390, Grouping.FLAT),
      row("\u25ab", "Square", Affix.PREFIX, 540, Grouping.FLAT, "\uf520"),
      row("\u2970", "RoundImplies", Affix.INFIX, 193, Grouping.RIGHT),
      row("\u29e6", "Equivalent", Affix.INFIX, 205, Grouping.FLAT),
      row("\uf120", "TwoWayRule", Affix.INFIX, 125, Grouping.RIGHT),
      row("\uf3c7", "Transpose", Affix.POSTFIX, 670, Grouping.FLAT),
      row("\uf3d2", "Distributed", Affix.INFIX, 250, Grouping.FLAT),
      row("\uf3d3", "Conditioned", Affix.INFIX, 195, Grouping.FLAT),
      row("\uf3d4", "UndirectedEdge", Affix.INFIX, 295, Grouping.RIGHT),
      row("\uf3d5", "DirectedEdge", Affix.INFIX, 295, Grouping.RIGHT),
      row("\uf3da", "TensorProduct", Affix.INFIX, 487, Grouping.FLAT),
      row("\uf523", "Implies", Affix.INFIX, 200, Grouping.RIGHT),
      row("^", "Power", Affix.INFIX, 590, Grouping.RIGHT),
      row("^:=", "UpSetDelayed", Affix.INFIX, 40, Grouping.RIGHT),
      row("^=", "UpSet", Affix.INFIX, 40, Grouping.RIGHT),
      row("|", "Alternatives", Affix.INFIX, 160, Grouping.FLAT),
      row("|->", "Function", Affix.INFIX, 90, Grouping.RIGHT, "\uf4a1"),
      row("||", "Or", Affix.INFIX, 215, Grouping.FLAT, "\u2228"),
      row("~", "\u00a7TILDE\u00a7", Affix.INFIX, 630, Grouping.FLAT),
      row("~~", "StringExpression", Affix.INFIX, 135, Grouping.FLAT)));
}
