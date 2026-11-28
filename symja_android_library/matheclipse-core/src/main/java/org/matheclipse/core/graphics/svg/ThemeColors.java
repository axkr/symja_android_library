package org.matheclipse.core.graphics.svg;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * The colours which depend on the appearance a picture is shown in: <code>LightDarkSwitched</code>,
 * <code>ThemeColor</code> and <code>SystemColor</code>.
 *
 * <p>
 * Symja's renderers write static pictures, so each of them resolves to its <b>light</b> variant: the
 * one shown on a white background. The dark variants are kept in the tables so that a renderer which
 * knows its background can ask for them with {@link #resolve(IAST, boolean)}.
 */
public final class ThemeColors {

  private ThemeColors() {}

  /** The light and dark value of each <code>ThemeColor</code> name. */
  private static final Map<String, int[]> THEME = new HashMap<>();

  /** The light and dark value of each <code>SystemColor</code> name. */
  private static final Map<String, int[]> SYSTEM = new HashMap<>();

  static {
    theme("Foreground", 0x333333, 0xe0e0e0);
    theme("Background", 0xffffff, 0x1e1e1e);
    // the accents follow the default curve colours, a little lighter on a dark background
    theme("Accent1", 0x3d99cc, 0x66b3dd);
    theme("Accent2", 0xe0912e, 0xebaa55);
    theme("Accent3", 0x8fb032, 0xa8c75a);
    theme("Accent4", 0xeb6235, 0xf08560);
    theme("Accent5", 0x8778b3, 0xa497c7);
    theme("Accent6", 0xc56e1a, 0xd98d45);
    theme("Accent7", 0x5d9ec7, 0x80b5d6);
    theme("Accent8", 0xffbf00, 0xffcf40);
    theme("Accent9", 0xa5609d, 0xbb82b4);
    theme("Syntax1", 0x2e5f9e, 0x7aa6d9);
    theme("Syntax2", 0x3c7d3c, 0x7dbb7d);
    theme("Syntax3", 0x2e8b8b, 0x66c2c2);
    theme("Syntax4", 0x666666, 0x999999);
    theme("Syntax5", 0x8250a8, 0xb48ad6);
    theme("Syntax6", 0xa8642a, 0xcc9257);
    theme("Syntax7", 0x3a3a3a, 0xd0d0d0);
    theme("Syntax8", 0x888888, 0x808080);
    theme("SyntaxError1", 0xcc0000, 0xff6666);
    theme("SyntaxError2", 0xd94f00, 0xff8c4d);
    theme("SyntaxError3", 0xb8860b, 0xe0b040);
    theme("SyntaxError4", 0xcc3366, 0xe07a9e);
    theme("SyntaxError5", 0x993399, 0xc273c2);
    theme("SyntaxError6", 0x8b4513, 0xc47a4d);

    system(0x3875d7, 0x4d8de0, "Accent", "Highlight");
    system(0xffffff, 0xffffff, "HighlightText");
    system(0x0066cc, 0x66aaff, "Hotlight");
    system(0xc0c0c0, 0x4a4a4a, "InactiveHighlight");
    system(0x333333, 0xcccccc, "InactiveHighlightText");
    system(0xf5f5f5, 0x1e1e1e, "Window", "ModalDialog", "ModelessDialog");
    system(0xffffff, 0x2a2a2a, "Menu");
    system(0xececec, 0x2d2d2d, "Toolbar", "Status");
    system(0xf0f0f0, 0x262626, "Palette", "PanelBackground");
    system(0xe8e8e8, 0x3a3a3a, "DialogButton", "PaletteButton");
    system(0xcccccc, 0x3a3a3a, "StatusFrame");
    system(0xffffe1, 0x3a3a2e, "Tooltip");
    system(0xc9c98f, 0x55553a, "TooltipFrame");
    system(0x333333, 0xe0e0e0, "WindowText", "MenuText", "DialogText", "ButtonText",
        "StatusText", "TooltipText", "TabButtonText", "DefaultButtonText", "CancelButtonText");
    system(0x000000, 0xffffff, "PressedButtonText", "PressedCancelButtonText",
        "PressedDefaultButtonText", "TabButtonTextPressed");
    system(0x111111, 0xf5f5f5, "TabButtonTextHover");
    system(0x999999, 0x6e6e6e, "DialogTextDisabled", "TabButtonTextDisabled");
  }

  private static void theme(String name, int light, int dark) {
    THEME.put(name, new int[] {light, dark});
  }

  private static void system(int light, int dark, String... names) {
    for (String name : names) {
      SYSTEM.put(name, new int[] {light, dark});
    }
  }

  /** The names <code>ThemeColor</code> knows. */
  public static boolean isThemeName(String name) {
    return THEME.containsKey(name);
  }

  /** The names <code>SystemColor</code> knows. */
  public static boolean isSystemName(String name) {
    return SYSTEM.containsKey(name);
  }

  /**
   * The colour of <code>LightDarkSwitched(...)</code>, <code>ThemeColor(name)</code> or
   * <code>SystemColor(name)</code> on a light background.
   *
   * @return the colour, or {@code null} when the expression is not one of them or names no colour
   */
  public static Color resolve(IAST ast) {
    return resolve(ast, false);
  }

  /**
   * The colour of <code>LightDarkSwitched(...)</code>, <code>ThemeColor(name)</code> or
   * <code>SystemColor(name)</code>.
   *
   * @param dark whether the colour is shown on a dark background
   * @return the colour, or {@code null} when the expression is not one of them or names no colour
   */
  public static Color resolve(IAST ast, boolean dark) {
    if (ast.isAST(S.ThemeColor, 2)) {
      return fromTable(THEME, ast.arg1(), dark);
    }
    if (ast.isAST(S.SystemColor, 2)) {
      return fromTable(SYSTEM, ast.arg1(), dark);
    }
    if (ast.isAST(S.LightDarkSwitched) && ast.argSize() >= 1 && ast.argSize() <= 2) {
      IExpr light = ast.arg1();
      IExpr darkSpec = ast.argSize() == 2 ? ast.arg2() : S.Automatic;
      if (dark) {
        if (!darkSpec.isAutomatic()) {
          return ColorUtil.parse(darkSpec);
        }
        Color base = light.isAutomatic() ? null : ColorUtil.parse(light);
        return base == null ? null : flipLightness(base);
      }
      if (!light.isAutomatic()) {
        return ColorUtil.parse(light);
      }
      Color base = darkSpec.isAutomatic() ? null : ColorUtil.parse(darkSpec);
      return base == null ? null : flipLightness(base);
    }
    return null;
  }

  /** A name given as a string, or as a symbol such as <code>Foreground</code>. */
  private static Color fromTable(Map<String, int[]> table, IExpr name, boolean dark) {
    String key;
    if (name.isString()) {
      key = name.toString();
    } else if (name.isSymbol()) {
      key = ((ISymbol) name).getSymbolName();
    } else {
      return null;
    }
    int[] pair = table.get(key);
    if (pair == null) {
      return null;
    }
    return new Color(dark ? pair[1] : pair[0]);
  }

  /**
   * The automatic counterpart of a colour for the other background: hue and saturation are kept and
   * the HSL lightness <code>l</code> becomes <code>1 - l</code>, so a dark navy turns into a light
   * blue and black into white.
   */
  public static Color flipLightness(Color c) {
    double r = c.getRed() / 255.0;
    double g = c.getGreen() / 255.0;
    double b = c.getBlue() / 255.0;
    double max = Math.max(r, Math.max(g, b));
    double min = Math.min(r, Math.min(g, b));
    double l = (max + min) / 2.0;
    double flipped = 1.0 - l;
    double d = max - min;
    if (d < 1e-12) {
      return withAlpha(flipped, flipped, flipped, c);
    }
    double s = l > 0.5 ? d / (2.0 - max - min) : d / (max + min);
    double h;
    if (max == r) {
      h = ((g - b) / d + (g < b ? 6.0 : 0.0)) / 6.0;
    } else if (max == g) {
      h = ((b - r) / d + 2.0) / 6.0;
    } else {
      h = ((r - g) / d + 4.0) / 6.0;
    }
    double q = flipped < 0.5 ? flipped * (1.0 + s) : flipped + s - flipped * s;
    double p = 2.0 * flipped - q;
    return withAlpha(hueChannel(p, q, h + 1.0 / 3.0), hueChannel(p, q, h),
        hueChannel(p, q, h - 1.0 / 3.0), c);
  }

  private static double hueChannel(double p, double q, double t) {
    t = ((t % 1.0) + 1.0) % 1.0;
    if (t < 1.0 / 6.0) {
      return p + (q - p) * 6.0 * t;
    }
    if (t < 0.5) {
      return q;
    }
    if (t < 2.0 / 3.0) {
      return p + (q - p) * (2.0 / 3.0 - t) * 6.0;
    }
    return p;
  }

  private static Color withAlpha(double r, double g, double b, Color alphaOf) {
    return new Color(clamp(r), clamp(g), clamp(b), alphaOf.getAlpha() / 255.0f);
  }

  private static float clamp(double v) {
    return (float) Math.max(0.0, Math.min(1.0, v));
  }
}
