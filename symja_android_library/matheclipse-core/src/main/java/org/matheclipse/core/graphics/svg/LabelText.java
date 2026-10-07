package org.matheclipse.core.graphics.svg;

import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

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
      return expr.toString();
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
      for (int i = 1; i < rows.size(); i++) {
        if (i > 1) {
          text.append('\n');
        }
        text.append(rows.get(i).isList() ? joined((IAST) rows.get(i), " ") : of(rows.get(i)));
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
      return boxes(box.first()) + "/" + boxes(box.second());
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
