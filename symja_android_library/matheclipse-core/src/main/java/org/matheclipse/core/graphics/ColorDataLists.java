package org.matheclipse.core.graphics;

import org.matheclipse.core.convert.RGBColor;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.graphics.svg.ColorUtil;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

/**
 * The indexed color schemes of <code>ColorData</code>.
 *
 * <p>
 * A scheme maps a 1-based index to a color; a real index is taken down to the integer below it and
 * a negative index has no color. Some schemes list a few colors and repeat them. The others never
 * repeat: they compute every color from the golden ratio, either all of them or the ones after a
 * listed start.
 *
 * @see ColorDataGradients
 */
public enum ColorDataLists {
  /** black */
  _000(new CyclicList(F.GrayLevel(F.C0))),
  /** hues spaced by twice the reciprocal golden ratio, at saturation and brightness 0.6 */
  _001(new GoldenHues()),
  /** 10 colors */
  _003(new CyclicList( //
      rgb(0.0, 0.0, 0.0), //
      rgb(0.996078431372549, 0.3607843137254902, 0.027450980392156862), //
      rgb(0.996078431372549, 0.9882352941176471, 0.03529411764705882), //
      rgb(0.5411764705882353, 0.7137254901960784, 0.027450980392156862), //
      rgb(0.1450980392156863, 0.43529411764705883, 0.3843137254901961), //
      rgb(0.00784313725490196, 0.5098039215686274, 0.9294117647058824), //
      rgb(0.15294117647058825, 0.11372549019607843, 0.49019607843137253), //
      rgb(0.47058823529411764, 0.2627450980392157, 0.5843137254901961), //
      rgb(0.8901960784313725, 0.011764705882352941, 0.49019607843137253), //
      rgb(0.9058823529411765, 0.027450980392156862, 0.12941176470588237))),
  /** 14 colors */
  _058(new CyclicList( //
      rgb(0.742077, 0.0624857, 0.00605783), rgb(0, 0.501961, 0), //
      rgb(0.891279, 0.721172, 0.777905), rgb(0.0620432, 0.521904, 0.704387), //
      rgb(0.823529, 0.663981, 0.00828565), rgb(1, 0.0175937, 0.505959), //
      rgb(0.975067, 1, 0.906294), rgb(0.162524, 0.167422, 0.153964), //
      rgb(0.904982, 0.676844, 0.566705), rgb(0.475792, 0.675181, 0.189776), //
      rgb(0.760174, 0.00823987, 0.412406), rgb(0, 0.390173, 0.773754), //
      rgb(1, 0.904479, 0.279423), rgb(0.0184939, 0.438911, 0.147189))),
  /** 9 colors */
  _061(new CyclicList( //
      rgb(0.70135, 0.093019, 0.00140383), rgb(0.289647, 0.222614, 0.484169), //
      rgb(0.98677, 0.98793, 0.458686), rgb(0.42948, 0.524086, 0.719463), //
      rgb(0.30808, 0.443137, 0.124895), rgb(0.895125, 0.71606, 0.0964523), //
      rgb(0.138415, 0.184848, 0.429862), rgb(0.882353, 0.0253147, 0.11165), //
      rgb(0.895125, 0.870893, 0.632624))),
  /** hues spaced by the golden ratio, blended with a yellow */
  _063(new BlendedGoldenHues()),
  /** 10 colors given by hue, saturation and brightness */
  _074(new CyclicList( //
      hue(0.33, 0.5, 0.6), hue(0.11, 0.75, 0.9), hue(0.5, 0.5, 0.5), hue(0.83, 0.33, 0.75),
      hue(0.17, 0.5, 0.5), hue(0.67, 0.33, 0.75), hue(0.83, 0.5, 0.5), hue(0.17, 0.67, 0.65),
      hue(0.92, 0.67, 0.6), hue(0.08, 0.67, 0.75))),
  /** L*a*b* colors of lightness 0.5 around a circle, spaced by the golden ratio */
  _094(new GoldenLabCircle(0.5)),
  /** L*a*b* colors of lightness 0.7 around a circle, spaced by the golden ratio */
  _096(new GoldenLabCircle(0.7)),
  /** the default plot colors */
  _097(new ListedThenBlended(-10, //
      new IAST[] {rgb(0.368417, 0.506779, 0.709798), rgb(0.880722, 0.611041, 0.142051),
          rgb(0.560181, 0.691569, 0.194885), rgb(0.922526, 0.385626, 0.209179),
          rgb(0.528488, 0.470624, 0.701351), rgb(0.772079, 0.431554, 0.102387),
          rgb(0.363898, 0.618501, 0.782349), rgb(1, 0.75, 0), rgb(0.647624, 0.37816, 0.614037),
          rgb(0.571589, 0.586483, 0.0)},
      new double[][] {{0.915, 0.3325, 0.2125}, {0.83, 0.46, 0.0}, {0.9575, 0.545, 0.11475},
          {1.0, 0.7575, 0.0}, {0.6175, 0.715, 0.0}, {0.15, 0.715, 0.595}, {0.3625, 0.545, 0.85},
          {0.575, 0.4175, 0.85}, {0.677, 0.358, 0.595}, {0.7875, 0.358, 0.425},
          {0.915, 0.3325, 0.2125}})),
  _098(new ListedThenBlended(4, //
      new IAST[] {rgb(0.29, 0.588, 0.612), rgb(0.886243, 0.527215, 0.0910023),
          rgb(0.613966, 0.37652, 0.585084), rgb(0.521981, 0.66, 0.0942065),
          rgb(0.820916, 0.341417, 0.22514), rgb(0.436075, 0.482355, 0.72),
          rgb(0.915458, 0.67329, 0.0122632), rgb(0.687223, 0.3576, 0.441196),
          rgb(0.217839, 0.655694, 0.494605), rgb(0.868187, 0.436936, 0.13357)},
      new double[][] {{0.828, 0.3204, 0.18}, {0.864, 0.5004, 0.0972}, {0.9, 0.6804, 0.0},
          {0.576, 0.6444, 0.0}, {0.18, 0.6444, 0.504}, {0.36, 0.5004, 0.72}, {0.54, 0.3924, 0.72},
          {0.6264, 0.342, 0.504}, {0.72, 0.342, 0.36}, {0.828, 0.3204, 0.18}})),
  _099(new ListedThenBlended(-5, //
      new IAST[] {rgb(0.65, 0.0, 0.0), rgb(0.0504678, 0.526626, 0.627561),
          rgb(0.752461, 0.362306, 0.125339), rgb(0.435888, 0.259065, 0.71028),
          rgb(0.461492, 0.563303, 0.0104797), rgb(0.659814, 0.212037, 0.300311),
          rgb(0.212151, 0.39271, 0.8), rgb(0.784922, 0.524612, 0.0407096),
          rgb(0.515278, 0.224, 0.530342), rgb(0.111025, 0.56, 0.418696)},
      new double[][] {{0.72, 0.2, 0.2}, {0.64, 0.32, 0.0}, {0.76, 0.4, 0.108}, {0.8, 0.6, 0.0},
          {0.44, 0.56, 0.0}, {0.0, 0.56, 0.56}, {0.2, 0.4, 0.8}, {0.4, 0.28, 0.8},
          {0.496, 0.224, 0.56}, {0.6, 0.224, 0.4}, {0.72, 0.2, 0.2}})),
  _100(new ListedThenBlended(9, //
      new IAST[] {rgb(0.0684356, 0.645252, 0.782123), rgb(0.98993, 0.699651, 0.0271887),
          rgb(0.450866, 0.379481, 1.0), rgb(0.369422, 0.7, 0.229826),
          rgb(0.942659, 0.463296, 0.151884), rgb(0.214511, 0.528391, 0.957413),
          rgb(0.827693, 0.730855, 0.0), rgb(0.546532, 0.322857, 0.883671),
          rgb(0.0575287, 0.697632, 0.634101), rgb(0.971874, 0.609371, 0.0759396)},
      new double[][] {{0.9315, 0.4075, 0.17755}, {0.9639, 0.5695, 0.09747},
          {0.9893, 0.7273, 0.01188}, {0.7417, 0.7213, 0.0}, {0.4279, 0.7, 0.1554},
          {0.0715, 0.7, 0.609}, {0.1295, 0.5964, 0.8554}, {0.2915, 0.4751, 1.0},
          {0.4535, 0.3779, 1.0}, {0.55544, 0.31766, 0.8614}})),
  _103(new ListedThenBlended(7, //
      new IAST[] {rgb(0.9, 0.378, 0.0), rgb(0.50284, 0.348442, 0.978155),
          rgb(0.974953, 0.624767, 0.0676258), rgb(0.717359, 0.28, 0.550217),
          rgb(0.440683, 0.7, 0.13913), rgb(0.7, 0.294, 0.0), rgb(0.0, 0.56, 0.8),
          rgb(1.0, 0.37, 0.1), rgb(0.704, 0.32, 0.8), rgb(0.641118, 0.710124, 0.0)},
      new double[][] {{0.35, 0.44, 1.0}, {0.5144, 0.3416, 0.964}, {0.6008, 0.2912, 0.748},
          {0.6928, 0.28, 0.588}, {0.792, 0.2716, 0.43}, {0.9, 0.25, 0.25}, {0.936, 0.43, 0.1672},
          {0.972, 0.61, 0.0756}, {0.928, 0.742, 0.0}, {0.604, 0.706, 0.0}})),
  _104(new ListedThenBlended(6, //
      new IAST[] {rgb(0.028, 0.5376, 0.5936), hueAsRGB(0.07, 1.0, 0.75),
          rgb(0.531753, 0.331477, 0.920616), rgb(0.627887, 0.708654, 0.0),
          rgb(0.237882, 0.510711, 0.979357), rgb(0.975692, 0.628459, 0.0656322),
          rgb(0.629898, 0.28, 0.684772), rgb(0.198854, 0.7, 0.446913),
          rgb(0.910038, 0.300188, 0.226913), rgb(0.440765, 0.385541, 1.0)},
      new double[][] {{0.035, 0.672, 0.742}, {0.239545, 0.509455, 0.980909},
          {0.444091, 0.383545, 1.0}, {0.571345, 0.308382, 0.821636}, {0.673655, 0.28, 0.617455},
          {0.784636, 0.273073, 0.442273}, {0.902455, 0.262273, 0.244355},
          {0.943364, 0.466818, 0.150264}, {0.984273, 0.671364, 0.0424636},
          {0.773364, 0.724818, 0.0}, {0.373, 0.7, 0.225273}})),
  _106(new ListedThenBlended(9, //
      new IAST[] {rgb(0.34398, 0.49112, 0.89936), rgb(0.97, 0.606, 0.081),
          rgb(0.91, 0.318, 0.243), rgb(0.448, 0.69232, 0.1538), rgb(0.62168, 0.2798, 0.6914),
          rgb(0.09096, 0.6296, 0.85532), rgb(0.46056, 0.40064, 0.81392),
          rgb(0.94, 0.462, 0.162), rgb(0.0, 0.7, 0.7), rgb(0.827051, 0.418034, 0.0243459)},
      new double[][] {{0.9, 0.25, 0.25}, {0.8, 0.4, 0.0}, {0.95, 0.5, 0.135}, {1.0, 0.75, 0.0},
          {0.55, 0.7, 0.0}, {0.0, 0.7, 0.7}, {0.25, 0.5, 1.0}, {0.5, 0.35, 1.0}, {0.62, 0.28, 0.7},
          {0.75, 0.28, 0.5}, {0.9, 0.25, 0.25}})),
  _108(new ListedThenBlended(13, //
      new IAST[] {rgb(0.9, 0.36, 0.054), rgb(0.365248, 0.427802, 0.758297),
          rgb(0.945109, 0.593901, 0.0), rgb(0.645957, 0.253192, 0.685109),
          rgb(0.285821, 0.56, 0.450773), rgb(0.7, 0.336, 0.0), rgb(0.491486, 0.345109, 0.8),
          rgb(0.71788, 0.568653, 0.0), rgb(0.70743, 0.224, 0.542415),
          rgb(0.287228, 0.490217, 0.664674)},
      new double[][] {{0.9, 0.36, 0.054}, {0.9696, 0.448, 0.08208}, {0.9832, 0.5864, 0.00648},
          {0.7408, 0.5712, 0.0}, {0.4376, 0.56, 0.2576}, {0.24, 0.528, 0.608},
          {0.388, 0.4104, 0.7808}, {0.536, 0.3184, 0.8}, {0.64032, 0.25648, 0.6992},
          {0.71264, 0.224, 0.5344}})),
  _109(new ListedThenBlended(9, //
      new IAST[] {rgb(1.0, 0.4, 0.0), rgb(0.655728, 0.8, 0.0), rgb(0.0, 0.742291, 0.873126),
          rgb(1.0, 0.656408, 0.0), rgb(0.893126, 0.4, 0.767184), rgb(0.295048, 0.8, 0.286932),
          rgb(0.238758, 0.610466, 1.0), rgb(1.0, 0.325204, 0.406504),
          rgb(0.0, 0.786874, 0.739379), rgb(1.0, 0.520437, 0.0)},
      new double[][] {{1.0, 0.0, 0.0}, {1.0, 0.5, 0.0}, {1.0, 0.8, 0.0}, {0.5, 0.8, 0.0},
          {0.0, 0.8, 0.7}, {0.0, 0.7, 1.0}, {0.8, 0.4, 1.0}, {1.0, 0.4, 0.5}, {1.0, 0.0, 0.0}})),
  _110(new ListedThenBlended(4, //
      new IAST[] {rgb(0.3, 0.68, 0.88), rgb(0.962492, 0.612461, 0.301114),
          rgb(0.659963, 0.445022, 0.850093), rgb(0.557756, 0.76, 0.304674),
          rgb(0.889907, 0.406019, 0.450155), rgb(0.462306, 0.562616, 1.0),
          rgb(0.994953, 0.774767, 0.213626), rgb(0.741359, 0.424, 0.690217)},
      new double[][] {{0.92, 0.4, 0.4}, {0.96, 0.6, 0.308}, {1.0, 0.8, 0.2}, {0.64, 0.76, 0.2},
          {0.2, 0.76, 0.76}, {0.4, 0.6, 1.0}, {0.6, 0.48, 1.0}, {0.696, 0.424, 0.76},
          {0.8, 0.424, 0.6}, {0.92, 0.4, 0.4}})),
  _112(new ListedThenBlended(4, //
      new IAST[] {rgb(0.790588, 0.201176, 0.0), rgb(0.192157, 0.388235, 0.807843),
          rgb(1.0, 0.607843, 0.0), rgb(0.0, 0.596078, 0.109804),
          rgb(0.567426, 0.32317, 0.729831), rgb(0.0, 0.588235, 0.705882),
          rgb(0.8505, 0.4275, 0.13185), rgb(0.499929, 0.285875, 0.775177)},
      new double[][] {{0.81, 0.225, 0.225}, {0.855, 0.45, 0.1215}, {0.9, 0.675, 0.0},
          {0.495, 0.63, 0.0}, {0.0, 0.63, 0.63}, {0.225, 0.45, 0.9}, {0.45, 0.315, 0.9},
          {0.558, 0.252, 0.63}, {0.675, 0.252, 0.45}, {0.81, 0.225, 0.225}})),
  /** hue palette with 13 colors normalized according to brightness */
  _250(new Table("250")),
  /** hue palette with 13 colors */
  _251(new Table("251"));

  /** How a scheme maps an index to a color. */
  private interface Scheme {
    /** The color at the 1-based index <code>n &gt;= 0</code>. */
    IExpr color(IInteger n);

    /** <code>{1, length, 1}</code> or <code>{1, Infinity, 1}</code>. */
    IAST range();

    /** The number of colors <code>ColorData(scheme, "ColorList")</code> lists. */
    int colorListLength();
  }

  /** The number of colors a scheme without end lists. */
  private static final int ENDLESS_COLOR_LIST_LENGTH = 15;

  private static final double PHI = (1.0 + Math.sqrt(5.0)) / 2.0;

  private final Scheme scheme;

  ColorDataLists(Scheme scheme) {
    this.scheme = scheme;
  }

  /** The range of the indexes, as <code>ColorDataFunction</code> shows it. */
  public IAST range() {
    return scheme.range();
  }

  /** The number of colors <code>ColorData(scheme, "ColorList")</code> lists. */
  public int colorListLength() {
    return scheme.colorListLength();
  }

  /**
   * The color of the scheme at the 1-based <code>index</code>. A real index is taken down to the
   * integer below it, so <code>2.5</code> is color 2.
   *
   * @param index a real number
   * @param engine the engine that takes the index down to an integer
   * @return {@link F#NIL} if <code>index</code> is no real number or is negative
   */
  public IExpr color(IExpr index, EvalEngine engine) {
    if (!index.isReal() || index.isNegative()) {
      return F.NIL;
    }
    IExpr floor = index.isInteger() ? index : engine.evaluate(F.Floor(index));
    if (!floor.isInteger()) {
      return F.NIL;
    }
    return scheme.color((IInteger) floor);
  }

  /**
   * A color whose components are machine numbers, except the ones written as <code>int</code>,
   * which stay exact as in <code>RGBColor(1, 0.75, 0)</code>.
   */
  private static IAST rgb(Number r, Number g, Number b) {
    return F.RGBColor(component(r), component(g), component(b));
  }

  private static IExpr component(Number value) {
    return value instanceof Integer ? F.ZZ(value.intValue()) : F.num(value.doubleValue());
  }

  private static IAST hue(double h, double s, double b) {
    return F.Hue(F.num(h), F.num(s), F.num(b));
  }

  private static IAST hueAsRGB(double h, double s, double b) {
    double[] rgb = hsbToRGB(h, s, b);
    return rgb(rgb[0], rgb[1], rgb[2]);
  }

  /** Hue, saturation and brightness to red, green and blue, the hue taken modulo 1. */
  private static double[] hsbToRGB(double h, double s, double b) {
    h = h - Math.floor(h);
    double h6 = h * 6;
    int sector = (int) h6;
    double f = h6 - sector;
    double p = b * (1 - s);
    double q = b * (1 - s * f);
    double t = b * (1 - s * (1 - f));
    switch (sector % 6) {
      case 0:
        return new double[] {b, t, p};
      case 1:
        return new double[] {q, b, p};
      case 2:
        return new double[] {p, b, t};
      case 3:
        return new double[] {p, q, b};
      case 4:
        return new double[] {t, p, b};
      default:
        return new double[] {b, p, q};
    }
  }

  /** The fractional part, which keeps the sign of <code>x</code>. */
  private static double fractionalPart(double x) {
    return x - (x < 0 ? Math.ceil(x) : Math.floor(x));
  }

  /** <code>a + (b - a) u</code> per component. */
  private static double[] lerp(double[] a, double[] b, double u) {
    return new double[] {a[0] + (b[0] - a[0]) * u, a[1] + (b[1] - a[1]) * u,
        a[2] + (b[2] - a[2]) * u};
  }

  /** The anchors spread evenly over <code>[0, 1]</code> and blended at <code>position</code>. */
  private static double[] blend(double[][] anchors, double position) {
    int last = anchors.length - 1;
    if (!(position > 0.0)) {
      return anchors[0];
    }
    double scaled = position * last;
    int segment = (int) scaled;
    if (segment >= last) {
      return anchors[last];
    }
    return lerp(anchors[segment], anchors[segment + 1], scaled - segment);
  }

  /** A list of colors repeated without end: index <code>n</code> is color <code>Mod(n, len, 1)</code>. */
  private static final class CyclicList implements Scheme {
    private final IAST[] colors;

    CyclicList(IAST... colors) {
      this.colors = colors;
    }

    @Override
    public IExpr color(IInteger n) {
      int position = n.subtract(F.C1).mod(colors.length).toInt();
      return colors[position];
    }

    @Override
    public IAST range() {
      return F.List(F.C1, F.ZZ(colors.length), F.C1);
    }

    @Override
    public int colorListLength() {
      return colors.length;
    }
  }

  /** A scheme with a color for every index. */
  private abstract static class Endless implements Scheme {
    @Override
    public IAST range() {
      return F.List(F.C1, F.CInfinity, F.C1);
    }

    @Override
    public int colorListLength() {
      return ENDLESS_COLOR_LIST_LENGTH;
    }

    static IAST toRGBColor(double[] rgb) {
      return F.RGBColor(F.num(rgb[0]), F.num(rgb[1]), F.num(rgb[2]));
    }
  }

  /** Hue <code>FractionalPart(0.67 + 2 (n - 1) / GoldenRatio)</code> at saturation and brightness 0.6. */
  private static final class GoldenHues extends Endless {
    @Override
    public IExpr color(IInteger n) {
      double h = 0.67 + (2 / PHI) * (n.evalf() - 1);
      return toRGBColor(hsbToRGB(h - Math.floor(h), 0.6, 0.6));
    }
  }

  /**
   * <code>Hue(0.15)</code> blended at 0.9 with <code>Hue(GoldenRatio (n - 1), 0.7, 0.7)</code>; the
   * blend runs over hue, saturation and brightness.
   */
  private static final class BlendedGoldenHues extends Endless {
    @Override
    public IExpr color(IInteger n) {
      double golden = PHI * (n.evalf() - 1);
      double[] hsb = lerp(new double[] {0.15, 1.0, 1.0},
          new double[] {golden - Math.floor(golden), 0.7, 0.7}, 0.9);
      return toRGBColor(hsbToRGB(hsb[0], hsb[1], hsb[2]));
    }
  }

  /** <code>LABColor(l, l Sin(angle), l Cos(angle))</code>, the angle advancing by the golden ratio. */
  private static final class GoldenLabCircle extends Endless {
    private final double lightness;

    GoldenLabCircle(double lightness) {
      this.lightness = lightness;
    }

    @Override
    public IExpr color(IInteger n) {
      double angle = ((n.evalf() - 1) * PHI - 0.5) * 2 * Math.PI;
      double[] xyz = ColorUtil.labToXyz(lightness, lightness * Math.sin(angle),
          lightness * Math.cos(angle));
      return toRGBColor(ColorUtil.xyzToRgb(xyz[0], xyz[1], xyz[2]));
    }
  }

  /**
   * Listed colors first; every other index <code>n &gt;= 0</code> blends the anchors at
   * <code>FractionalPart((offset + n - 1) / GoldenRatio)</code>, a negative position blending to the
   * first anchor. The arithmetic is in machine numbers.
   */
  private static final class ListedThenBlended extends Endless {
    private final int offset;
    private final IAST[] listed;
    private final double[][] anchors;

    ListedThenBlended(int offset, IAST[] listed, double[][] anchors) {
      this.offset = offset;
      this.listed = listed;
      this.anchors = anchors;
    }

    @Override
    public IExpr color(IInteger n) {
      int index = n.toIntDefault();
      if (1 <= index && index <= listed.length) {
        return listed[index - 1];
      }
      double position = fractionalPart((offset + n.evalf() - 1) / PHI);
      return toRGBColor(blend(anchors, position));
    }
  }

  /** A list of colors read from <code>/img/colorlist/&lt;name&gt;.csv</code> and repeated. */
  private static final class Table implements Scheme {
    private final String name;

    /** The colors, read on first use. */
    private volatile RGBColor[] colors;

    Table(String name) {
      this.name = name;
    }

    private RGBColor[] colors() {
      RGBColor[] result = colors;
      if (result == null) {
        synchronized (this) {
          result = colors;
          if (result == null) {
            IAST table = ColorDataGradients.readRGBATable("/img/colorlist/" + name + ".csv");
            result = new RGBColor[table.argSize()];
            for (int i = 0; i < result.length; i++) {
              result[i] = RGBColor.fromRGBAList(table.get(i + 1));
            }
            colors = result;
          }
        }
      }
      return result;
    }

    @Override
    public IExpr color(IInteger n) {
      RGBColor[] all = colors();
      IAST rgba = all[n.subtract(F.C1).mod(all.length).toInt()].toRGBAList();
      return F.RGBColor(F.Divide(rgba.arg1(), F.num(255)), F.Divide(rgba.arg2(), F.num(255)),
          F.Divide(rgba.arg3(), F.num(255)), F.Divide(rgba.arg4(), F.num(255)));
    }

    @Override
    public IAST range() {
      return F.List(F.C1, F.ZZ(colors().length), F.C1);
    }

    @Override
    public int colorListLength() {
      return colors().length;
    }
  }
}
