package org.matheclipse.image.algo;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.svg.ColorUtil;
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
 * measurement - {@link #distance} - and as the spaces a single colour directive is converted from
 * or into ({@link #toXyzaDouble}), not as colour spaces an image can be converted into.
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
      if (head == S.XYZColor || head == S.LABColor) {
        double[] rgba = toRgbaDouble(ast);
        return rgba == null ? null
            : new float[] {(float) rgba[0], (float) rgba[1], (float) rgba[2], (float) rgba[3]};
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

  /**
   * The colour <code>expr</code> denotes, in machine doubles: an <code>RGBColor</code> or a
   * <code>GrayLevel</code> keeps the digits it was written with, where {@link #toRgba(IExpr)}
   * rounds them to floats. The other directives are converted through {@link #toRgba(IExpr)}.
   *
   * @return <code>{r, g, b, a}</code>, or <code>null</code> if <code>expr</code> is not a colour
   */
  public static double[] toRgbaDouble(IExpr expr) {
    if (expr.isAST(S.RGBColor) || expr.isAST(S.GrayLevel)) {
      IAST ast = (IAST) expr;
      boolean gray = ast.isAST(S.GrayLevel);
      int count = gray ? 1 : 3;
      if (ast.argSize() < count) {
        return null;
      }
      double[] result = new double[] {0.0, 0.0, 0.0, 1.0};
      for (int i = 0; i < count; i++) {
        result[i] = ast.get(i + 1).evalfNaN();
        if (Double.isNaN(result[i])) {
          return null;
        }
      }
      if (gray) {
        result[1] = result[0];
        result[2] = result[0];
      }
      if (ast.argSize() > count) {
        double alpha = ast.get(count + 1).evalfNaN();
        if (!Double.isNaN(alpha)) {
          result[3] = alpha;
        }
      }
      return result;
    }
    if (expr.isAST(S.XYZColor) || expr.isAST(S.LABColor)) {
      double[] xyza = toXyzaDouble(expr);
      if (xyza == null) {
        return null;
      }
      double[] rgb = ColorUtil.xyzToRgb(xyza[0], xyza[1], xyza[2]);
      return new double[] {rgb[0], rgb[1], rgb[2], xyza[3]};
    }
    float[] rgba = toRgba(expr);
    if (rgba == null) {
      return null;
    }
    return new double[] {rgba[0], rgba[1], rgba[2], rgba[3]};
  }

  /**
   * The grey level of an RGB colour, weighting the channels by how bright they look:
   * <code>0.299 r + 0.587 g + 0.114 b</code>.
   */
  public static double grayLevel(double r, double g, double b) {
    return 0.299 * r + 0.587 * g + 0.114 * b;
  }

  /**
   * sRGB to CIE XYZ, relative to the D50 white point, see
   * {@link ColorUtil#rgbToXyz(double, double, double)}.
   */
  public static double[] rgbToXyz(double r, double g, double b) {
    return ColorUtil.rgbToXyz(r, g, b);
  }

  /**
   * The CIE XYZ coordinates, relative to D50, of a colour directive: an <code>XYZColor</code> or a
   * <code>LABColor</code> is converted without going through the clipped RGB channels.
   *
   * @return <code>{x, y, z, alpha}</code>, or <code>null</code> if <code>expr</code> is not a
   *         colour
   */
  public static double[] toXyzaDouble(IExpr expr) {
    if (expr.isAST(S.XYZColor) || expr.isAST(S.LABColor)) {
      double[] channels = channelsWithAlpha((IAST) expr);
      if (channels == null) {
        return null;
      }
      double[] xyz = expr.isAST(S.LABColor) //
          ? ColorUtil.labToXyz(channels[0], channels[1], channels[2])
          : channels;
      return new double[] {xyz[0], xyz[1], xyz[2], channels[3]};
    }
    double[] rgba = toRgbaDouble(expr);
    if (rgba == null) {
      return null;
    }
    double[] xyz = ColorUtil.rgbToXyz(rgba[0], rgba[1], rgba[2]);
    return new double[] {xyz[0], xyz[1], xyz[2], rgba[3]};
  }

  /**
   * The three channels and the alpha of <code>LABColor(c1, c2, c3)</code> or
   * <code>XYZColor(c1, c2, c3, alpha)</code>, or of the same with the channels in one list.
   *
   * @return <code>null</code> if a channel is no real number
   */
  private static double[] channelsWithAlpha(IAST ast) {
    if (ast.argSize() == 1 && ast.arg1().isList()) {
      ast = ((IAST) ast.arg1()).apply(ast.head());
    }
    if (ast.argSize() < 3 || ast.argSize() > 4) {
      return null;
    }
    double[] result = new double[] {0.0, 0.0, 0.0, 1.0};
    for (int i = 1; i <= ast.argSize(); i++) {
      result[i - 1] = ast.get(i).evalfNaN();
      if (Double.isNaN(result[i - 1])) {
        return null;
      }
    }
    return result;
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
    // the channels as they are, not rounded to bytes first: Hue(0.75) is RGBColor(0.5, 0, 1)
    double h6 = (hsb[0] - Math.floor(hsb[0])) * 6.0;
    int sector = (int) Math.floor(h6);
    double f = h6 - sector;
    double bri = hsb[2];
    double p = bri * (1.0 - hsb[1]);
    double q = bri * (1.0 - hsb[1] * f);
    double t = bri * (1.0 - hsb[1] * (1.0 - f));
    switch (sector % 6) {
      case 0:
        return new float[] {(float) bri, (float) t, (float) p};
      case 1:
        return new float[] {(float) q, (float) bri, (float) p};
      case 2:
        return new float[] {(float) p, (float) bri, (float) t};
      case 3:
        return new float[] {(float) p, (float) q, (float) bri};
      case 4:
        return new float[] {(float) t, (float) p, (float) bri};
      default:
        return new float[] {(float) bri, (float) p, (float) q};
    }
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
