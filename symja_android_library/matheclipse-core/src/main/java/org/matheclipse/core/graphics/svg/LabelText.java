package org.matheclipse.core.graphics.svg;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
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
    if (expr == S.Null) {
      // nothing is drawn: a blank line as the entry of a Column
      return "";
    }
    if (expr.isAST(S.Style) && expr.argSize() >= 1) {
      return of(expr.first());
    }
    if (expr.isAST(S.HoldForm, 2) || expr.isAST(S.TraditionalForm, 2)
        || expr.isAST(S.StandardForm, 2)) {
      return of(expr.first());
    }
    if (expr.isAST(S.RawBoxes, 2) || expr.isAST(S.DisplayForm, 2)) {
      return boxes(expr.first());
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
    if ((expr.isPower() || expr.isAST(S.Superscript, 3) || expr.isAST(S.Subscript, 3))) {
      boolean sub = expr.isAST(S.Subscript);
      String script = script(of(expr.second()), sub ? SUB_FROM : SUPER_FROM,
          sub ? SUB_TO : SUPER_TO);
      if (script != null) {
        String base = of(expr.first());
        if (expr.first().isPlus() || expr.first().isTimes()) {
          base = "(" + base + ")";
        }
        return base + script;
      }
    }
    // a derivative anywhere inside is written with the same prime marks as one which stands alone
    return PrimitiveCollector
        .unquote((expr.isFree(S.Derivative, true) ? expr : primed(expr)).toString());
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

  /** The text of a box tree: the content of <code>RawBoxes</code> or <code>DisplayForm</code>. */
  private static String boxes(IExpr box) {
    if (box.isString()) {
      return box.toString();
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
    if (box.isAST(S.SubsuperscriptBox, 4)) {
      String sub = script(boxes(box.second()), SUB_FROM, SUB_TO);
      String sup = script(boxes(((IAST) box).arg3()), SUPER_FROM, SUPER_TO);
      return boxes(box.first()) + (sub != null ? sub : "_" + boxes(box.second()))
          + (sup != null ? sup : "^" + boxes(((IAST) box).arg3()));
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
