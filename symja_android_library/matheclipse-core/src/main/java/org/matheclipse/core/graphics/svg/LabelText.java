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
    if (expr.isAST(S.Style) && expr.argSize() >= 1) {
      return of(expr.first());
    }
    if (expr.isAST(S.HoldForm, 2)) {
      return of(expr.first());
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
    return PrimitiveCollector.unquote(expr.toString());
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
