package org.matheclipse.core.graphics.svg;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.NumberFormatter;
import org.matheclipse.core.form.NumberFormatter.FormattedNumber;
import org.matheclipse.core.form.output.NumberFormPrinter;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.parser.BoxNotation;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.parser.client.Scanner;

/**
 * The plain text a label expression reads as in a picture: a string without its quotes, a
 * <code>Row</code> joined up, a <code>Style</code> unwrapped, and a power, a
 * <code>Superscript</code> or a <code>Subscript</code> written with the Unicode superscript and
 * subscript characters - <code>x^2</code> as <code>x²</code> - when every character of the script
 * has one. A script which has none keeps its written form.
 */
public final class LabelText {

  private static final String SUPER_FROM = "0123456789+-=()abcdefghijklmnoprstuvwxyz";
  private static final String SUPER_TO = "⁰¹²³⁴⁵⁶⁷⁸⁹⁺⁻⁼⁽⁾ᵃᵇᶜᵈᵉᶠᵍʰⁱʲᵏˡᵐⁿᵒᵖʳˢᵗᵘᵛʷˣʸᶻ";
  private static final String SUB_FROM = "0123456789+-=()aehijklmnoprstuvx";
  private static final String SUB_TO = "₀₁₂₃₄₅₆₇₈₉₊₋₌₍₎ₐₑₕᵢⱼₖₗₘₙₒₚᵣₛₜᵤᵥₓ";

  private LabelText() {}

  /** The text of a label expression. */
  public static String of(IExpr expr) {
    if (expr.isString()) {
      String text = expr.toString();
      return text.indexOf(Scanner.BOX_BANG) < 0 ? text : typeset(text);
    }
    if (expr.isAST(S.Invisible, 2)) {
      // takes its room in a notebook, but shows nothing
      return "";
    }
    if (expr == S.Null) {
      // nothing is drawn: a blank line as the entry of a Column
      return "";
    }
    if ((expr.isAST(S.Style) || expr.isAST(S.Text)) && expr.argSize() >= 1) {
      // Text(expr) as a label is the label expr
      return of(expr.first());
    }
    if ((expr.isAST(S.Framed) || expr.isAST(S.Panel) || expr.isAST(S.Pane)
        || expr.isAST(S.TextCell) || expr.isAST(S.Rotate) || expr.isAST(S.Tooltip)
        || expr.isAST(S.Magnify)) && expr.argSize() >= 1) {
      // a box, a turn, a size or a hover around a label: as text, what it holds
      return of(expr.first());
    }
    if (expr.isAST(S.HoldForm, 2) || expr.isAST(S.TraditionalForm, 2)
        || expr.isAST(S.StandardForm, 2)) {
      return of(expr.first());
    }
    if (expr.isAST(S.RawBoxes, 2) || expr.isAST(S.DisplayForm, 2)) {
      return boxes(expr.first());
    }
    if (expr.isAST() && NumberFormatter.formKind(expr.head()) != null) {
      String number = formattedNumber((IAST) expr);
      if (number != null) {
        return number;
      }
      if (expr.argSize() >= 1 && !expr.first().isNumber()) {
        // a number form around an expression formats the numbers in it
        final IAST form = (IAST) expr;
        IExpr formatted = form.arg1().replaceAll(x -> {
          // exact numbers stay as they are written, as in the number form itself
          if (!x.isReal() || !x.isInexactNumber()) {
            return org.matheclipse.core.expression.F.NIL;
          }
          String text = formattedNumber(form.setAtCopy(1, x));
          return text == null ? org.matheclipse.core.expression.F.NIL
              : org.matheclipse.core.expression.F.stringx(text);
        });
        return of(formatted.orElse(form.arg1()));
      }
    }
    String derivative = derivative(expr);
    if (derivative != null) {
      return derivative;
    }
    if (expr.isAST(S.Column) && expr.argSize() >= 1 && expr.first().isList()) {
      // one line per entry
      return joined((IAST) expr.first(), "\n");
    }
    if (expr.isAST(S.Grid) && expr.argSize() >= 1 && expr.first().isList()) {
      // one line per row
      StringBuilder text = new StringBuilder();
      IAST rows = (IAST) expr.first();
      int width = itemWidth((IAST) expr);
      for (int i = 1; i < rows.size(); i++) {
        if (i > 1) {
          text.append('\n');
        }
        IExpr row = rows.get(i);
        if (width > 0 && row.isList() && row.argSize() == 1) {
          // a lone cell is broken into lines of the item width
          text.append(wrapped(of(row.first()), width));
        } else {
          text.append(row.isList() ? joined((IAST) row, " ") : of(row));
        }
      }
      return text.toString();
    }
    if ((expr.isAST(S.BracketingBar) || expr.isAST(S.DoubleBracketingBar))
        && expr.argSize() >= 1) {
      // |x| and ||x||, the arguments between the bars
      String bar = expr.isAST(S.BracketingBar) ? "|" : "\u2016";
      return bar + joined((IAST) expr, ",") + bar;
    }
    if (expr.isAST(S.Overscript, 3)) {
      String accent = combiningAccent(of(expr.second()));
      if (accent != null) {
        return of(expr.first()) + accent;
      }
    }
    if ((expr.isAST(S.LineLegend) || expr.isAST(S.SwatchLegend) || expr.isAST(S.PointLegend))
        && expr.argSize() >= 1) {
      // a legend inside a label: its entries on one line, each behind the legend's own marker
      IExpr labels = expr.argSize() >= 2 && expr.second().isList() ? expr.second() : expr.first();
      if (labels.isList()) {
        String marker = expr.isAST(S.LineLegend) ? "— " : expr.isAST(S.SwatchLegend) ? "■ " : "● ";
        StringBuilder text = new StringBuilder();
        IAST list = (IAST) labels;
        for (int i = 1; i < list.size(); i++) {
          if (i > 1) {
            text.append("  ");
          }
          text.append(marker).append(of(list.get(i)));
        }
        return text.toString();
      }
    }
    if (expr.isAST(S.Row) && expr.argSize() >= 1 && expr.first().isList()) {
      String separator = expr.argSize() >= 2 ? of(expr.second()) : "";
      if (RELATION_SEPARATORS.contains(separator)) {
        // x = 2: a relation between the parts is set with space around it
        separator = " " + separator + " ";
      }
      StringBuilder text = new StringBuilder();
      IAST parts = (IAST) expr.first();
      for (int i = 1; i < parts.size(); i++) {
        if (i > 1) {
          text.append(separator);
        }
        text.append(of(parts.get(i)));
      }
      return text.toString();
    }
    if (expr.isNegativeNumberPower()) {
      // 1/x
      return product(org.matheclipse.core.expression.F.Times(expr));
    }
    if (expr.isPower() && expr.exponent().isNumEqualRational(
        org.matheclipse.core.expression.F.C1D2)) {
      // the square root, its radicand in parentheses unless it is a single symbol or number
      IExpr radicand = expr.base();
      String text = of(radicand);
      return "\u221A" + (radicand.isAtom() ? text : "(" + text + ")");
    }
    if ((expr.isPower() || expr.isAST(S.Superscript, 3) || expr.isAST(S.Subscript, 3))) {
      boolean sub = expr.isAST(S.Subscript);
      String script = script(of(expr.second()), sub ? SUB_FROM : SUPER_FROM,
          sub ? SUB_TO : SUPER_TO);
      if (script != null && expr.isPower() && expr.exponent().isInteger()
          && expr.exponent().isPositive() && isNamedFunctionCall(expr.base())) {
        // sin²(x): the power goes on the name of the function
        IAST call = (IAST) expr.base();
        return functionName((ISymbol) call.head()) + script + "(" + joined(call, ", ") + ")";
      }
      if (script != null) {
        String base = of(expr.first());
        if (expr.first().isPlus() || expr.first().isTimes()) {
          base = "(" + base + ")";
        }
        return base + script;
      }
      if (expr.isPower()) {
        // an exponent without superscript characters, after a caret
        String base = of(expr.first());
        if (!expr.first().isAtom()) {
          base = "(" + base + ")";
        }
        return base + "^" + scriptText(expr.second());
      }
    }
    if (expr.isAST(S.Superscript, 3) || expr.isAST(S.Subscript, 3)) {
      // a script without superscript or subscript characters, after a caret or an underscore
      return scriptBase(expr.first()) + (expr.isAST(S.Subscript) ? "_" : "^")
          + scriptText(expr.second());
    }
    if (expr.isAST(S.Subsuperscript, 4) || expr.isAST(S.Underoverscript, 4)) {
      // x with the subscript first, then the superscript; on one line the limits below and
      // above a base are written as scripts, like those of an inline sum
      IExpr over = ((IAST) expr).arg3();
      return scriptBase(expr.first()) + subAndSuperscript(of(expr.second()), of(over),
          scriptText(expr.second()), scriptText(over));
    }
    if (expr.isAST(S.SuperStar, 2) || expr.isAST(S.SuperDagger, 2)) {
      // x* and x\u2020: the mark follows the base
      return of(expr.first()) + (expr.isAST(S.SuperStar) ? "*" : "\u2020");
    }
    if (expr == S.Pi) {
      return "\u03C0";
    }
    if (expr == S.E) {
      return "e";
    }
    if (expr.isInfinity()) {
      return "\u221E";
    }
    if (expr.isPlus()) {
      return sum((IAST) expr);
    }
    if (expr.isTimes()) {
      return product((IAST) expr);
    }
    String traditional = traditional(expr);
    if (traditional != null) {
      return traditional;
    }
    // a derivative anywhere inside is written with the same prime marks as one which stands alone
    return PrimitiveCollector
        .unquote((expr.isFree(S.Derivative, true) ? expr : primed(expr)).toString());
  }

  /**
   * The relations, lists, rules and function calls of a label as they are set in traditional
   * notation: <code>x == 1</code> as <code>x = 1</code>, <code>Sin(x)</code> as
   * <code>sin(x)</code>, <code>f(t)</code> with parentheses, <code>Abs(x)</code> as
   * <code>|x|</code>.
   *
   * @return <code>null</code> for anything else, which keeps its written form
   */
  private static String traditional(IExpr expr) {
    if (!expr.isAST() || expr.isAST0()) {
      return null;
    }
    IAST ast = (IAST) expr;
    if (ast.isList()) {
      return "{" + joined(ast, ", ") + "}";
    }
    String relation = relation(ast.head());
    if (relation != null && ast.argSize() >= 2) {
      return joined(ast, relation);
    }
    if (ast.isAST(S.Abs, 2)) {
      return "|" + of(ast.arg1()) + "|";
    }
    if (ast.isAST(S.Floor, 2)) {
      return "\u230A" + of(ast.arg1()) + "\u230B";
    }
    if (ast.isAST(S.Ceiling, 2)) {
      return "\u2308" + of(ast.arg1()) + "\u2309";
    }
    if (ast.isAST(S.Factorial, 2)) {
      return factorText(ast.arg1()) + "!";
    }
    IExpr head = ast.head();
    String name;
    if (head.isBuiltInSymbol()) {
      if (!((ISymbol) head).isNumericFunctionAttribute()) {
        // a builtin which is no mathematical function keeps its written form
        return null;
      }
      name = functionName((ISymbol) head);
    } else if (head.isSymbol()) {
      name = of(head);
    } else if (isDerivative(head)) {
      // f'(t)
      name = derivative(head);
    } else if (head.isAST(S.Style) || head.isAST(S.Subscript) || head.isAST(S.Superscript)) {
      // Style("E", Italic)(t) is E(t)
      name = of(head);
    } else {
      return null;
    }
    return name + "(" + joined(ast, ", ") + ")";
  }

  /**
   * <code>true</code> for a call of a function whose name is set in lower case, like
   * <code>Sin(x)</code>, whose powers are written on the name.
   */
  private static boolean isNamedFunctionCall(IExpr expr) {
    if (!expr.isAST() || expr.isAST0() || !expr.head().isBuiltInSymbol()) {
      return false;
    }
    String set = setName((ISymbol) expr.head());
    return set != null && Character.isLowerCase(set.charAt(0)) && !set.endsWith(INVERSE)
        && expr.head() != S.Sign;
  }

  /** The base of a script, in parentheses if it is a sum or a product. */
  private static String scriptBase(IExpr base) {
    String text = of(base);
    return base.isPlus() || base.isTimes() ? "(" + text + ")" : text;
  }

  /**
   * A script written after a caret or an underscore, in parentheses unless it is a symbol or a
   * natural number: <code>x^(1/3)</code>, <code>x^(-2)</code>.
   */
  /**
   * A subscript and a superscript in script characters; if one of them has no such characters,
   * both are written plain, after an underscore and a caret, so that the two read alike.
   */
  private static String subAndSuperscript(String sub, String sup, String plainSub,
      String plainSup) {
    String subGlyphs = script(sub, SUB_FROM, SUB_TO);
    String supGlyphs = script(sup, SUPER_FROM, SUPER_TO);
    if (subGlyphs != null && supGlyphs != null) {
      return subGlyphs + supGlyphs;
    }
    return "_" + plainSub + "^" + plainSup;
  }

  private static String scriptText(IExpr script) {
    String text = of(script);
    return script.isSymbol() || (script.isInteger() && !script.isNegative()) ? text
        : "(" + text + ")";
  }

  /** The separator of a relation written between its sides, or <code>null</code>. */
  private static String relation(IExpr head) {
    if (head == S.Equal) {
      return " = ";
    }
    if (head == S.Unequal) {
      return " \u2260 ";
    }
    if (head == S.Less) {
      return " < ";
    }
    if (head == S.LessEqual) {
      return " \u2264 ";
    }
    if (head == S.Greater) {
      return " > ";
    }
    if (head == S.GreaterEqual) {
      return " \u2265 ";
    }
    if (head == S.Rule) {
      return " \u2192 ";
    }
    if (head == S.Set) {
      // z = sin(2 x), the way a held definition is written in a label
      return " = ";
    }
    if (head == S.SetDelayed) {
      return " := ";
    }
    return null;
  }

  /**
   * The name a mathematical function is set with: the trigonometric and hyperbolic functions and
   * the logarithm in lower case, their inverses as <code>sin\u207B\u00B9</code>, the gamma and
   * zeta functions by their Greek letters.
   */
  private static String functionName(ISymbol function) {
    String set = setName(function);
    return set != null ? set : of(function);
  }

  /**
   * The name a function is set with in traditional notation, or <code>null</code> for one which
   * keeps its own name.
   */
  private static String setName(ISymbol function) {
    if (!function.isBuiltInSymbol()) {
      return null;
    }
    switch (((org.matheclipse.core.interfaces.IBuiltInSymbol) function).ordinal()) {
      case ID.Sin:
        return "sin";
      case ID.Cos:
        return "cos";
      case ID.Tan:
        return "tan";
      case ID.Cot:
        return "cot";
      case ID.Sec:
        return "sec";
      case ID.Csc:
        return "csc";
      case ID.Sinh:
        return "sinh";
      case ID.Cosh:
        return "cosh";
      case ID.Tanh:
        return "tanh";
      case ID.Coth:
        return "coth";
      case ID.Sech:
        return "sech";
      case ID.Csch:
        return "csch";
      case ID.Log:
        return "log";
      case ID.Erf:
        return "erf";
      case ID.Erfc:
        return "erfc";
      case ID.ArcSin:
        return "sin" + INVERSE;
      case ID.ArcCos:
        return "cos" + INVERSE;
      case ID.ArcTan:
        return "tan" + INVERSE;
      case ID.ArcCot:
        return "cot" + INVERSE;
      case ID.ArcSec:
        return "sec" + INVERSE;
      case ID.ArcCsc:
        return "csc" + INVERSE;
      case ID.ArcSinh:
        return "sinh" + INVERSE;
      case ID.ArcCosh:
        return "cosh" + INVERSE;
      case ID.ArcTanh:
        return "tanh" + INVERSE;
      case ID.ArcCoth:
        return "coth" + INVERSE;
      case ID.ArcSech:
        return "sech" + INVERSE;
      case ID.ArcCsch:
        return "csch" + INVERSE;
      case ID.Sign:
        return "sgn";
      case ID.Gamma:
        return "\u0393";
      case ID.Zeta:
        return "\u03B6";
      default:
        return null;
    }
  }

  /** The separators of a Row which are relations, set with space around them. */
  private static final java.util.Set<String> RELATION_SEPARATORS = java.util.Set.of("=", "==",
      "\u2260", "<", ">", "\u2264", "\u2265", "<=", ">=", "\u2192", "->", ":=");

  /** The superscript -1 of an inverse function. */
  private static final String INVERSE = "\u207B\u00B9";

  /**
   * The number of a number form wrapper as it is written in a picture, with the exponent of the
   * scientific notation in superscript digits on the same line: <code>7.08 × 10⁷</code>.
   *
   * @return <code>null</code> if the wrapper does not format an integer or a real number
   */
  private static String formattedNumber(IAST formAST) {
    FormattedNumber formatted = NumberFormPrinter.formatArgument(formAST, EvalEngine.get());
    if (formatted == null) {
      return null;
    }
    if (!formatted.scientific) {
      return formatted.mantissa;
    }
    String exponent = Integer.toString(formatted.exponent);
    String script = script(exponent, SUPER_FROM, SUPER_TO);
    return formatted.mantissa + " " + formatted.multiplier + " 10"
        + (script != null ? script : "^" + exponent);
  }

  /** A sum as it is written by hand: <code>2 a - b</code>. */
  private static String sum(IAST plus) {
    StringBuilder text = new StringBuilder();
    for (int i = 1; i <= plus.argSize(); i++) {
      String term = of(plus.get(i));
      if (i == 1) {
        text.append(term);
      } else if (term.startsWith("-")) {
        text.append(" - ").append(term, 1, term.length());
      } else {
        text.append(" + ").append(term);
      }
    }
    return text.toString();
  }

  /**
   * A product as it is written by hand: the factors side by side, <code>2 a</code>, the factors
   * with a negative power behind a slash, <code>a/c²</code>, and a sum among them in parentheses.
   */
  private static String product(IAST times) {
    StringBuilder numerator = new StringBuilder();
    StringBuilder denominator = new StringBuilder();
    int denominators = 0;
    boolean negative = false;
    for (int i = 1; i <= times.argSize(); i++) {
      IExpr factor = times.get(i);
      if (i == 1 && factor.isMinusOne()) {
        negative = true;
        continue;
      }
      if (factor.isFraction()) {
        // 3 π/4 instead of 3/4 π
        org.matheclipse.core.interfaces.IFraction fraction =
            (org.matheclipse.core.interfaces.IFraction) factor;
        negative ^= factor.isNegative();
        if (!fraction.numerator().abs().isOne()) {
          append(numerator, of(fraction.numerator().abs()));
        }
        append(denominator, of(fraction.denominator()));
        denominators++;
        continue;
      }
      if (factor.isNegativeNumberPower()) {
        IExpr power = factor.exponent().negate();
        IExpr below = power.isOne() ? factor.base()
            : org.matheclipse.core.expression.F.Power(factor.base(), power);
        append(denominator, factorText(below));
        denominators++;
        continue;
      }
      append(numerator, factorText(factor));
    }
    if (numerator.length() == 0) {
      numerator.append('1');
    }
    String text = (negative ? "-" : "") + numerator;
    if (denominators == 0) {
      return text;
    }
    return text + "/" + (denominators > 1 ? "(" + denominator + ")" : denominator.toString());
  }

  private static String factorText(IExpr factor) {
    String text = of(factor);
    return factor.isPlus() ? "(" + text + ")" : text;
  }

  private static void append(StringBuilder text, String factor) {
    if (text.length() > 0) {
      text.append(' ');
    }
    text.append(factor);
  }

  /** Whether <code>expr</code> is <code>Derivative(n)[f]</code> with an order from 1 on. */
  private static boolean isDerivative(IExpr expr) {
    return expr.isAST1() && expr.head().isAST(S.Derivative, 2) && expr.head().first().isInteger()
        && expr.head().first().toIntDefault(-1) >= 1;
  }

  /**
   * <code>Derivative(n)[f]</code> as <code>f</code> with prime marks, <code>f&#x2032;</code> to
   * <code>f&#x2034;</code>, and with the order as a superscript from the fourth derivative on.
   *
   * @return <code>null</code> if <code>expr</code> is no such derivative
   */
  private static String derivative(IExpr expr) {
    if (!isDerivative(expr)) {
      return null;
    }
    int order = expr.head().first().toIntDefault(-1);
    String function = of(expr.first());
    return order <= 3 ? function + "\u2032\u2033\u2034".charAt(order - 1)
        : function + "\u207D" + script(Integer.toString(order), SUPER_FROM, SUPER_TO) + "\u207E";
  }

  /** <code>expr</code> with every <code>Derivative(n)[f]</code> in it named by its label. */
  private static IExpr primed(IExpr expr) {
    if (!expr.isAST()) {
      return expr;
    }
    String derivative = derivative(expr);
    if (derivative != null) {
      return org.matheclipse.core.expression.F.Dummy(derivative);
    }
    IAST ast = (IAST) expr;
    org.matheclipse.core.interfaces.IASTMutable copy = ast.copy();
    for (int i = 0; i < ast.size(); i++) {
      copy.set(i, primed(ast.get(i)));
    }
    return copy;
  }

  /**
   * The number of characters a line of a <code>Grid</code> cell holds with
   * <code>ItemSize -> w</code> or <code>ItemSize -> {w, h}</code>: a width of <code>w</code> ems,
   * with half an em for a character.
   *
   * @return <code>0</code> if the width is not set
   */
  private static int itemWidth(IAST grid) {
    for (int i = 2; i < grid.size(); i++) {
      if (grid.get(i).isRuleAST() && grid.get(i).first() == S.ItemSize) {
        IExpr size = grid.get(i).second();
        while (size.isList() && size.argSize() >= 1) {
          size = size.first();
        }
        double ems = size.isReal() ? size.evalf() : 0.0;
        return ems > 0.0 && ems < 10000.0 ? (int) Math.round(2.0 * ems) : 0;
      }
    }
    return 0;
  }

  /** <code>text</code> broken at its spaces into lines of at most <code>width</code> characters. */
  private static String wrapped(String text, int width) {
    if (text.length() <= width || text.indexOf('\n') >= 0) {
      return text;
    }
    StringBuilder result = new StringBuilder(text.length() + 8);
    int lineLength = 0;
    for (String word : text.split(" ")) {
      if (lineLength > 0 && lineLength + 1 + word.length() > width) {
        result.append('\n');
        lineLength = 0;
      } else if (lineLength > 0) {
        result.append(' ');
        lineLength++;
      }
      result.append(word);
      lineLength += word.length();
    }
    return result.toString();
  }

  /**
   * The text of a string with box escapes in it, <code>"area: \!\(\*FractionBox[...]\)"</code>:
   * each escape is read as boxes, the text around it stays. An escape which has no reading stays
   * as it is written.
   */
  private static String typeset(String text) {
    StringBuilder result = new StringBuilder(text.length());
    int i = 0;
    while (i < text.length()) {
      int start = text.indexOf(Scanner.BOX_BANG, i);
      if (start < 0 || start + 1 >= text.length() || text.charAt(start + 1) != Scanner.BOX_OPEN) {
        break;
      }
      int depth = 0;
      int end = -1;
      for (int j = start + 1; j < text.length(); j++) {
        char ch = text.charAt(j);
        if (ch == Scanner.BOX_OPEN) {
          depth++;
        } else if (ch == Scanner.BOX_CLOSE && --depth == 0) {
          end = j;
          break;
        }
      }
      if (end < 0) {
        break;
      }
      result.append(text, i, start);
      String escape = text.substring(start, end + 1);
      IExpr boxes = null;
      try {
        // \( ... \) without the leading \! is the box tree itself
        boxes = new ExprParser(EvalEngine.get(), false).parse(escape.substring(1));
      } catch (RuntimeException rex) {
        // no reading
      }
      result.append(boxes == null || boxes.isAST(S.HoldComplete) ? BoxNotation.writeEscapes(escape)
          : boxes(boxes));
      i = end + 1;
    }
    return result.append(BoxNotation.writeEscapes(text.substring(i))).toString();
  }

  /** A numerator or denominator: in parentheses if it is more than one item. */
  private static String fractionPart(IExpr box) {
    IExpr items = box;
    while ((items.isAST(S.RowBox, 2) || (items.isList() && items.argSize() == 1))) {
      items = items.first();
    }
    String text = boxes(box);
    return items.isList() && items.argSize() > 1 && !text.startsWith("(") ? "(" + text + ")"
        : text;
  }

  /**
   * The characters of the inside of a string box: <code>a\"b</code> is <code>a"b</code>. The
   * escapes are the ones a string box is written with: <code>\"</code>, <code>\\</code>,
   * <code>\n</code> and <code>\t</code>.
   */
  private static String unescape(String text) {
    if (text.indexOf('\\') < 0) {
      return text;
    }
    StringBuilder buf = new StringBuilder(text.length());
    for (int i = 0; i < text.length(); i++) {
      char ch = text.charAt(i);
      if (ch == '\\' && i + 1 < text.length()) {
        char next = text.charAt(++i);
        buf.append(next == 'n' ? '\n' : next == 't' ? '\t' : next);
      } else {
        buf.append(ch);
      }
    }
    return buf.toString();
  }

  /** The text of a box tree: the content of <code>RawBoxes</code> or <code>DisplayForm</code>. */
  private static String boxes(IExpr box) {
    if (box.isString()) {
      String text = box.toString();
      // a string box shows its characters without the quotation marks and escapes
      return text.length() >= 2 && text.charAt(0) == '"' && text.charAt(text.length() - 1) == '"'
          ? unescape(text.substring(1, text.length() - 1))
          : text;
    }
    if (box.isList()) {
      StringBuilder text = new StringBuilder();
      for (IExpr part : (IAST) box) {
        text.append(boxes(part));
      }
      return text.toString();
    }
    if (box.isAST(S.RowBox, 2)) {
      return boxes(box.first());
    }
    if (box.isAST(S.TemplateBox) && box.argSize() >= 2 && box.first().argSize() == 1
        && box.first().isList() && box.second().isString()) {
      // the postfix marks m^T, m^H and z^*
      String tag = box.second().toString();
      String mark = tag.equals("Transpose") ? "ᵀ"
          : tag.equals("ConjugateTranspose") ? "ᴴ" : tag.equals("Conjugate") ? "*" : null;
      if (mark != null) {
        return boxes(box.first().first()) + mark;
      }
    }
    if (box.isAST(S.TemplateBox) && box.argSize() >= 2 && box.first().isList()
        && box.second().isString() && (box.second().toString().equals("RowWithSeparators")
            || box.second().toString().equals("RowWithSeparator"))) {
      // the items joined by the separator; the separator comes first, a string one as drawn and
      // as written, any other one once
      IAST parts = (IAST) box.first();
      int first = box.second().toString().equals("RowWithSeparators") ? 3 : 2;
      StringBuilder text = new StringBuilder();
      for (int i = first; i < parts.size(); i++) {
        if (i > first) {
          text.append(boxes(parts.arg1()));
        }
        text.append(boxes(parts.get(i)));
      }
      return text.toString();
    }
    if (box.isAST(S.SqrtBox) && box.argSize() >= 1) {
      String radicand = boxes(box.first());
      return radicand.length() > 1 ? "\u221A(" + radicand + ")" : "\u221A" + radicand;
    }
    if (box.isAST(S.FractionBox, 3)) {
      return fractionPart(box.first()) + "/" + fractionPart(box.second());
    }
    if (box.isAST(S.OverscriptBox, 3)) {
      String accent = combiningAccent(boxes(box.second()));
      if (accent != null) {
        return boxes(box.first()) + accent;
      }
    }
    if (box.isAST(S.SuperscriptBox, 3) || box.isAST(S.SubscriptBox, 3)) {
      boolean sub = box.isAST(S.SubscriptBox);
      String index = boxes(box.second());
      String script = script(index, sub ? SUB_FROM : SUPER_FROM, sub ? SUB_TO : SUPER_TO);
      return boxes(box.first()) + (script != null ? script : (sub ? "_" : "^") + index);
    }
    if (box.isAST(S.RadicalBox, 3)) {
      String index = script(boxes(box.second()), SUPER_FROM, SUPER_TO);
      String radicand = boxes(box.first());
      return (index != null ? index : "") + "\u221A"
          + (radicand.length() > 1 ? "(" + radicand + ")" : radicand);
    }
    if (box.isAST(S.SubsuperscriptBox, 4) || box.isAST(S.UnderoverscriptBox, 4)) {
      String under = boxes(box.second());
      String over = boxes(((IAST) box).arg3());
      return boxes(box.first()) + subAndSuperscript(under, over, under, over);
    }
    if (box.isAST(S.GridBox) && box.argSize() >= 1 && box.first().isList()) {
      // one line per row
      StringBuilder text = new StringBuilder();
      IAST rows = (IAST) box.first();
      for (int i = 1; i < rows.size(); i++) {
        if (i > 1) {
          text.append('\n');
        }
        if (rows.get(i).isList()) {
          IAST row = (IAST) rows.get(i);
          for (int j = 1; j < row.size(); j++) {
            text.append(j > 1 ? " " : "").append(boxes(row.get(j)));
          }
        } else {
          text.append(boxes(rows.get(i)));
        }
      }
      return text.toString();
    }
    if (box.isAST() && box.argSize() >= 1 && box.head().isSymbol()
        && box.head().toString().toLowerCase(java.util.Locale.ROOT).endsWith("box")) {
      // StyleBox, TagBox, an under- or overscript, ...: the box they dress up
      return boxes(box.first());
    }
    return of(box);
  }

  private static String joined(IAST parts, String separator) {
    StringBuilder text = new StringBuilder();
    for (int i = 1; i < parts.size(); i++) {
      if (i > 1) {
        text.append(separator);
      }
      text.append(of(parts.get(i)));
    }
    return text.toString();
  }

  /** The combining character an <code>Overscript</code> mark is written with, or <code>null</code>. */
  private static String combiningAccent(String mark) {
    switch (mark) {
      case ".":
        return "̇";
      case "..":
        return "̈";
      case "-":
      case "_":
      case "¯":
        return "̄";
      case "^":
        return "̂";
      case "~":
        return "̃";
      case "→":
        return "⃗";
      default:
        return null;
    }
  }

  /** <code>text</code> in script characters, or <code>null</code> if one of them has none. */
  private static String script(String text, String from, String to) {
    if (text.isEmpty()) {
      return null;
    }
    StringBuilder result = new StringBuilder(text.length());
    for (int i = 0; i < text.length(); i++) {
      int index = from.indexOf(text.charAt(i));
      if (index < 0) {
        return null;
      }
      result.appendCodePoint(to.codePointAt(to.offsetByCodePoints(0, index)));
    }
    return result.toString();
  }
}
