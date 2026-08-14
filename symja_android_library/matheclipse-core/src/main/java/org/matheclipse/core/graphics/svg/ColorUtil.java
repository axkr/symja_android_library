package org.matheclipse.core.graphics.svg;

import java.awt.Color;
import java.util.Locale;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;

/**
 * The single authority for turning a colour expression into a {@link Color} and a {@link Color}
 * into SVG attribute values.
 *
 * <p>
 * Symja normally evaluates the named colours to {@code RGBColor(...)} before a graphic reaches the
 * converter, but several ({@code Transparent}, {@code DarkGray}) stay symbolic, and held or
 * hand-built expressions can carry any of them, so the named table is still consulted.
 *
 * <p>
 * Every method returns {@code null} rather than throwing when the expression is not a colour, so a
 * malformed directive degrades to "no change" instead of losing the whole picture.
 */
public final class ColorUtil {

  private ColorUtil() {}

  /** Fully transparent, used for {@code Transparent} and {@code FaceForm[None]}. */
  public static final Color TRANSPARENT = new Color(0, 0, 0, 0);

  /**
   * Named colours, with the component values used. Kept here rather than duplicated across the
   * collector and the option parser.
   */
  public static Color named(IBuiltInSymbol symbol) {
    switch (symbol.ordinal()) {
      case ID.Red:
        return rgb(1, 0, 0);
      case ID.Green:
        return rgb(0, 1, 0);
      case ID.Blue:
        return rgb(0, 0, 1);
      case ID.Black:
        return rgb(0, 0, 0);
      case ID.White:
        return rgb(1, 1, 1);
      case ID.Gray:
        return rgb(0.5, 0.5, 0.5);
      case ID.Yellow:
        return rgb(1, 1, 0);
      case ID.Cyan:
        return rgb(0, 1, 1);
      case ID.Magenta:
        return rgb(1, 0, 1);
      case ID.Orange:
        return rgb(1, 0.5, 0);
      case ID.Pink:
        return rgb(1, 0.5, 0.5);
      case ID.Purple:
        return rgb(0.5, 0, 0.5);
      case ID.Brown:
        return rgb(0.6, 0.4, 0.2);
      case ID.LightRed:
        return rgb(1, 0.85, 0.85);
      case ID.LightGreen:
        return rgb(0.88, 1, 0.88);
      case ID.LightBlue:
        return rgb(0.87, 0.94, 1);
      case ID.LightYellow:
        return rgb(1, 1, 0.85);
      case ID.LightCyan:
        return rgb(0.9, 1, 1);
      case ID.LightMagenta:
        return rgb(1, 0.9, 1);
      case ID.LightOrange:
        return rgb(1, 0.9, 0.8);
      case ID.LightPink:
        return rgb(1, 0.925, 0.925);
      case ID.LightPurple:
        return rgb(0.94, 0.88, 0.94);
      case ID.LightBrown:
        return rgb(0.94, 0.91, 0.88);
      case ID.LightGray:
        return rgb(0.85, 0.85, 0.85);
      case ID.DarkGray:
        return rgb(0.25, 0.25, 0.25);
      case ID.Transparent:
        return TRANSPARENT;
      default:
        return null;
    }
  }

  /**
   * Parse any colour expression: a named symbol, one of the colour-space heads, or one of the
   * derived forms ({@code Lighter}, {@code Darker}, {@code Blend}, {@code Opacity[o, colour]}).
   *
   * @return the colour, or {@code null} when the expression does not denote one
   */
  public static Color parse(IExpr expr) {
    if (expr == null) {
      return null;
    }
    if (expr.isBuiltInSymbol()) {
      return named((IBuiltInSymbol) expr);
    }
    if (!expr.isAST()) {
      return null;
    }
    IAST ast = (IAST) expr;
    IExpr head = ast.head();
    if (!head.isBuiltInSymbol()) {
      return null;
    }
    switch (((IBuiltInSymbol) head).ordinal()) {
      case ID.RGBColor:
        return parseRGB(ast);
      case ID.Hue:
        return parseHue(ast);
      case ID.GrayLevel:
        return parseGrayLevel(ast);
      case ID.CMYKColor:
        return parseCMYK(ast);
      case ID.Lighter:
        return lighterDarker(ast, true);
      case ID.Darker:
        return lighterDarker(ast, false);
      case ID.Blend:
        return blend(ast);
      case ID.Opacity:
        // Opacity[o, colour] denotes a colour; plain Opacity[o] is a directive, not a colour
        if (ast.argSize() >= 2) {
          Color base = parse(ast.arg2());
          if (base != null) {
            return withAlpha(base, clamp01(dbl(ast.arg1(), 1.0)));
          }
        }
        return null;
      default:
        return null;
    }
  }

  /**
   * {@code RGBColor[r, g, b]}, {@code RGBColor[r, g, b, a]}, {@code RGBColor[{r, g, b}]} and the
   * hex string form {@code RGBColor["#ff0000"]}.
   */
  private static Color parseRGB(IAST ast) {
    if (ast.argSize() == 1) {
      IExpr arg = ast.arg1();
      if (arg.isList()) {
        return parseRGB((IAST) arg.makeList().apply(ast.head()));
      }
      if (arg.isString()) {
        return parseHex(arg.toString());
      }
      return null;
    }
    if (ast.argSize() < 3) {
      return null;
    }
    double a = ast.argSize() >= 4 ? clamp01(dbl(ast.arg4(), 1.0)) : 1.0;
    return new Color(fclamp(dbl(ast.arg1(), 0)), fclamp(dbl(ast.arg2(), 0)),
        fclamp(dbl(ast.arg3(), 0)), (float) a);
  }

  private static Color parseHue(IAST ast) {
    if (ast.argSize() < 1) {
      return null;
    }
    float h = (float) dbl(ast.arg1(), 0);
    float s = ast.argSize() >= 2 ? fclamp(dbl(ast.arg2(), 1)) : 1.0f;
    float b = ast.argSize() >= 3 ? fclamp(dbl(ast.arg3(), 1)) : 1.0f;
    double a = ast.argSize() >= 4 ? clamp01(dbl(ast.arg4(), 1.0)) : 1.0;
    Color c = Color.getHSBColor(h, s, b);
    return new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) Math.round(a * 255));
  }

  private static Color parseGrayLevel(IAST ast) {
    if (ast.argSize() < 1) {
      return null;
    }
    float g = fclamp(dbl(ast.arg1(), 0));
    float a = ast.argSize() >= 2 ? fclamp(dbl(ast.arg2(), 1)) : 1.0f;
    return new Color(g, g, g, a);
  }

  private static Color parseCMYK(IAST ast) {
    if (ast.argSize() < 4) {
      return null;
    }
    double c = dbl(ast.arg1(), 0);
    double m = dbl(ast.arg2(), 0);
    double y = dbl(ast.arg3(), 0);
    double k = dbl(ast.arg4(), 0);
    float a = ast.argSize() >= 5 ? fclamp(dbl(ast.get(5), 1)) : 1.0f;
    return new Color(fclamp((1 - c) * (1 - k)), fclamp((1 - m) * (1 - k)),
        fclamp((1 - y) * (1 - k)), a);
  }

  private static Color parseHex(String raw) {
    String s = raw.replace("\"", "").trim();
    if (s.startsWith("#")) {
      s = s.substring(1);
    }
    try {
      if (s.length() == 3) {
        int r = Integer.parseInt(s.substring(0, 1).repeat(2), 16);
        int g = Integer.parseInt(s.substring(1, 2).repeat(2), 16);
        int b = Integer.parseInt(s.substring(2, 3).repeat(2), 16);
        return new Color(r, g, b);
      }
      if (s.length() == 6) {
        return new Color(Integer.parseInt(s.substring(0, 2), 16),
            Integer.parseInt(s.substring(2, 4), 16), Integer.parseInt(s.substring(4, 6), 16));
      }
      if (s.length() == 8) {
        return new Color(Integer.parseInt(s.substring(0, 2), 16),
            Integer.parseInt(s.substring(2, 4), 16), Integer.parseInt(s.substring(4, 6), 16),
            Integer.parseInt(s.substring(6, 8), 16));
      }
    } catch (NumberFormatException nfe) {
      return null;
    }
    return null;
  }

  private static Color lighterDarker(IAST ast, boolean lighter) {
    if (ast.argSize() < 1) {
      return null;
    }
    Color base = parse(ast.arg1());
    if (base == null) {
      return null;
    }
    double fraction = ast.argSize() >= 2 ? clamp01(dbl(ast.arg2(), 1.0 / 3.0)) : 1.0 / 3.0;
    float r = base.getRed() / 255.0f;
    float g = base.getGreen() / 255.0f;
    float b = base.getBlue() / 255.0f;
    float a = base.getAlpha() / 255.0f;
    if (lighter) {
      return new Color(fclamp(r * (1 - fraction) + fraction), fclamp(g * (1 - fraction) + fraction),
          fclamp(b * (1 - fraction) + fraction), a);
    }
    return new Color(fclamp(r * (1 - fraction)), fclamp(g * (1 - fraction)),
        fclamp(b * (1 - fraction)), a);
  }

  /** {@code Blend[{c1, c2, ...}]} and {@code Blend[{c1, c2, ...}, x]}. */
  private static Color blend(IAST ast) {
    if (ast.argSize() < 1 || !ast.arg1().isList()) {
      return null;
    }
    IAST list = (IAST) ast.arg1();
    int n = list.argSize();
    if (n == 0) {
      return null;
    }
    if (n == 1) {
      return parse(list.arg1());
    }
    double x = ast.argSize() >= 2 ? clamp01(dbl(ast.arg2(), 0.5)) : 0.5;
    // x runs across the whole list: 0 is the first colour, 1 the last
    double scaled = x * (n - 1);
    int lo = (int) Math.floor(scaled);
    if (lo >= n - 1) {
      lo = n - 2;
    }
    double t = scaled - lo;
    Color a = parse(list.get(lo + 1));
    Color b = parse(list.get(lo + 2));
    if (a == null || b == null) {
      return a != null ? a : b;
    }
    return new Color(fclamp(mix(a.getRed(), b.getRed(), t) / 255.0),
        fclamp(mix(a.getGreen(), b.getGreen(), t) / 255.0),
        fclamp(mix(a.getBlue(), b.getBlue(), t) / 255.0),
        fclamp(mix(a.getAlpha(), b.getAlpha(), t) / 255.0));
  }

  private static double mix(int a, int b, double t) {
    return a * (1 - t) + b * t;
  }

  /** A copy of {@code c} whose alpha is multiplied by {@code factor}. */
  public static Color withAlpha(Color c, double factor) {
    int alpha = (int) Math.round(clamp01(c.getAlpha() / 255.0 * factor) * 255);
    return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
  }

  /**
   * The SVG paint value for a colour: {@code "none"} when it is fully transparent, otherwise an
   * {@code rgb(...)} triple. The alpha channel is emitted separately by {@link #alphaOf}, because
   * SVG carries opacity in its own attribute.
   */
  public static String css(Color c) {
    if (c == null || c.getAlpha() == 0) {
      return "none";
    }
    return String.format(Locale.US, "rgb(%d,%d,%d)", c.getRed(), c.getGreen(), c.getBlue());
  }

  /** The opacity of a colour combined with an {@code Opacity} directive, in 0..1. */
  public static double alphaOf(Color c, double directiveOpacity) {
    if (c == null) {
      return 0.0;
    }
    return clamp01(c.getAlpha() / 255.0 * directiveOpacity);
  }

  private static Color rgb(double r, double g, double b) {
    return new Color(fclamp(r), fclamp(g), fclamp(b), 1.0f);
  }

  private static float fclamp(double v) {
    return (float) Math.max(0.0, Math.min(1.0, v));
  }

  private static double clamp01(double v) {
    if (Double.isNaN(v)) {
      return 1.0;
    }
    return Math.max(0.0, Math.min(1.0, v));
  }

  /** Numeric value of an expression, or {@code def} when it is not numeric. */
  public static double dbl(IExpr expr, double def) {
    if (expr == null) {
      return def;
    }
    if (expr instanceof INumber) {
      return ((INumber) expr).reDoubleValue();
    }
    double d = expr.evalfNaN();
    return Double.isNaN(d) ? def : d;
  }
}
