package org.matheclipse.image.algo;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import boofcv.alg.color.ColorLab;

/**
 * Colours as the image functions need them: four floats <code>{r, g, b, a}</code> in
 * <code>0.0 ... 1.0</code>, whatever directive they were written as.
 *
 * <p>
 * The colour space conversions here all round trip through RGB, because that is the only thing an
 * <code>ImageExpr</code> can store. <code>"LAB"</code>, <code>"XYZ"</code> and <code>"LUV"</code>
 * have channels that do not fit in <code>0.0 ... 1.0</code>, so they appear here only as a
 * measurement - {@link #distance} - and not as a colour space an image can be converted into.
 */
public final class Colors {

  private Colors() {}

  /**
   * The colour <code>expr</code> denotes.
   *
   * @return <code>{r, g, b, a}</code> in <code>0.0 ... 1.0</code>, or <code>null</code> if
   *         <code>expr</code> is not a colour
   */
  public static float[] toRgba(IExpr expr) {
    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      IExpr head = ast.head();
      if (head == S.RGBColor) {
        return withAlpha(components(ast, 3), ast, 4);
      }
      if (head == S.GrayLevel) {
        float[] gray = components(ast, 1);
        if (gray == null) {
          return null;
        }
        return withAlpha(new float[] {gray[0], gray[0], gray[0]}, ast, 2);
      }
      if (head == S.Hue) {
        float[] hsb = ast.argSize() == 1 //
            ? new float[] {(float) ast.arg1().evalfNaN(), 1.0f, 1.0f}
            : components(ast, 3);
        if (hsb == null || Float.isNaN(hsb[0])) {
          return null;
        }
        return withAlpha(hsbToRgb(hsb), ast, 4);
      }
      if (head == S.CMYKColor) {
        float[] cmyk = components(ast, 4);
        if (cmyk == null) {
          return null;
        }
        return withAlpha(cmykToRgb(cmyk), ast, 5);
      }
      if (ast.isList()) {
        if (ast.argSize() == 1) {
          float[] gray = components(ast, 1);
          return gray == null ? null : new float[] {gray[0], gray[0], gray[0], 1.0f};
        }
        if (ast.argSize() == 3) {
          return withAlpha(components(ast, 3), ast, 4);
        }
        if (ast.argSize() == 4) {
          float[] rgb = components(ast, 3);
          return rgb == null ? null : withAlpha(rgb, ast, 4);
        }
        return null;
      }
      return null;
    }
    double gray = expr.evalfNaN();
    if (Double.isNaN(gray)) {
      return null;
    }
    return new float[] {(float) gray, (float) gray, (float) gray, 1.0f};
  }

  /** An <code>RGBColor</code> expression, with the alpha argument only when it is not opaque. */
  public static IAST toRGBColor(float[] rgba) {
    if (rgba.length >= 4 && rgba[3] < 1.0f) {
      return F.RGBColor(F.num(rgba[0]), F.num(rgba[1]), F.num(rgba[2]), F.num(rgba[3]));
    }
    return F.RGBColor(F.num(rgba[0]), F.num(rgba[1]), F.num(rgba[2]));
  }

  // ---------------------------------------------------------------- colour spaces

  /** RGB to hue, saturation, brightness, all in <code>0.0 ... 1.0</code>. */
  public static float[] rgbToHsb(float[] rgb) {
    float[] hsb = new float[3];
    java.awt.Color.RGBtoHSB(to255(rgb[0]), to255(rgb[1]), to255(rgb[2]), hsb);
    return hsb;
  }

  /** Hue, saturation, brightness to RGB. */
  public static float[] hsbToRgb(float[] hsb) {
    int rgb = java.awt.Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
    return new float[] {((rgb >> 16) & 0xFF) / 255.0f, ((rgb >> 8) & 0xFF) / 255.0f,
        (rgb & 0xFF) / 255.0f};
  }

  /** RGB to cyan, magenta, yellow, black. */
  public static float[] rgbToCmyk(float[] rgb) {
    float black = 1.0f - Math.max(rgb[0], Math.max(rgb[1], rgb[2]));
    if (black >= 1.0f) {
      return new float[] {0.0f, 0.0f, 0.0f, 1.0f};
    }
    float scale = 1.0f - black;
    return new float[] {(1.0f - rgb[0] - black) / scale, (1.0f - rgb[1] - black) / scale,
        (1.0f - rgb[2] - black) / scale, black};
  }

  /** Cyan, magenta, yellow, black to RGB. */
  public static float[] cmykToRgb(float[] cmyk) {
    float scale = 1.0f - cmyk[3];
    return new float[] {(1.0f - cmyk[0]) * scale, (1.0f - cmyk[1]) * scale,
        (1.0f - cmyk[2]) * scale};
  }

  /**
   * The CIE 1976 colour difference: the Euclidean distance in CIE L*a*b*, which is what
   * <code>ColorDistance</code> measures by default.
   */
  public static double distance(float[] rgb1, float[] rgb2) {
    double[] lab1 = new double[3];
    double[] lab2 = new double[3];
    ColorLab.rgbToLab(to255(rgb1[0]), to255(rgb1[1]), to255(rgb1[2]), lab1);
    ColorLab.rgbToLab(to255(rgb2[0]), to255(rgb2[1]), to255(rgb2[2]), lab2);
    double sum = 0.0;
    for (int i = 0; i < 3; i++) {
      double difference = lab1[i] - lab2[i];
      sum += difference * difference;
    }
    return Math.sqrt(sum);
  }

  // ------------------------------------------------------------------- internals

  private static float[] components(IAST ast, int count) {
    if (ast.argSize() < count) {
      return null;
    }
    float[] values = new float[count];
    for (int i = 0; i < count; i++) {
      double value = ast.get(i + 1).evalfNaN();
      if (Double.isNaN(value)) {
        return null;
      }
      values[i] = (float) value;
    }
    return values;
  }

  private static float[] withAlpha(float[] rgb, IAST ast, int alphaPosition) {
    if (rgb == null) {
      return null;
    }
    float alpha = 1.0f;
    if (ast.size() > alphaPosition) {
      double value = ast.get(alphaPosition).evalfNaN();
      if (!Double.isNaN(value)) {
        alpha = (float) value;
      }
    }
    return new float[] {rgb[0], rgb[1], rgb[2], alpha};
  }

  private static int to255(float value) {
    int scaled = Math.round(value * 255.0f);
    if (scaled < 0) {
      return 0;
    }
    return scaled > 255 ? 255 : scaled;
  }
}
