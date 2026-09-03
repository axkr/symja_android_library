package org.matheclipse.image.builtin;

import java.awt.image.BufferedImage;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.LinearAlgebraUtil;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.PlotColorFunction;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.external.fastutil.ints.IntArrayList;
import org.matheclipse.image.algo.Colors;
import org.matheclipse.image.algo.Pixels;
import org.matheclipse.image.expression.data.ImageExpr;
import org.matheclipse.image.expression.data.ImageOptions;

/**
 * <code>ReliefImage(array)</code> - a relief image of an array of height values, lit from above
 * left as though it were a landscape seen from directly overhead.
 *
 * <p>
 * The array is a rectangular matrix of real heights with <code>n</code> rows and <code>m</code>
 * columns, and the result is an <code>m</code> by <code>n</code> image - one pixel per height,
 * always 8 bit RGB.
 *
 * <p>
 * <b>The heights are read as a surface over the unit square.</b> Shading needs a slope, and a slope
 * needs the heights and the distances between them to be on the same scale, which a bare array does
 * not say anything about. The heights are scaled to <code>0.0 ... 1.0</code> over the plot range,
 * and the array is treated as covering one unit in each direction whatever its size, so the same
 * surface sampled more finely comes out looking the same rather than flatter.
 *
 * <p>
 * <b>Colour and shading are separate.</b> <code>ColorFunction</code> gives each height a colour and
 * the lighting then darkens it, so <code>ColorFunction -&gt; Automatic</code> - which is white
 * everywhere - leaves the shading on its own, in grey.
 */
public class ReliefImage extends AbstractFunctionOptionEvaluator {

  /** The default light: 135 degrees, over the top left corner, half way up the sky. */
  private static final double DEFAULT_AZIMUTH = 3.0 * Math.PI / 4.0;
  private static final double DEFAULT_ALTITUDE = Math.PI / 4.0;

  private static final String DIFFUSE_REFLECTION = "DiffuseReflection";
  private static final String ASPECT_BASED_SHADING = "AspectBasedShading";

  public ReliefImage() {}

  @Override
  public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
      IAST originalAST) {
    IExpr arg1 = ast.arg1();
    if (!arg1.isList()) {
      return notAMatrix(arg1, engine);
    }
    IAST array = (IAST) arg1;
    IntArrayList dimensions = LinearAlgebraUtil.dimensions(array);
    if (dimensions == null || dimensions.size() != 2) {
      return notAMatrix(arg1, engine);
    }
    int height = dimensions.getInt(0);
    int width = dimensions.getInt(1);
    if (height <= 0 || width <= 0) {
      return notAMatrix(arg1, engine);
    }

    double[][] heights = heights(array, width, height);
    if (heights == null) {
      return notAMatrix(arg1, engine);
    }

    double[] range = plotRange(options[OPTION_PLOT_RANGE], heights, engine);
    if (range == null) {
      return F.NIL;
    }
    double lo = range[0];
    double span = range[1] - lo;

    double[] light = lightingAngle(options[OPTION_LIGHTING_ANGLE], engine);
    boolean aspectBased = ASPECT_BASED_SHADING.equals(methodName(options[OPTION_METHOD]));
    boolean scaleColors = !options[OPTION_COLOR_FUNCTION_SCALING].isFalse();
    // the heights are already mapped onto 0..1 below, so the slot is handed over as it stands
    PlotColorFunction colorFunction = PlotColorFunction
        .of(PlotColorFunction.Family.ARRAY, options[OPTION_COLOR_FUNCTION],
            options[OPTION_COLOR_FUNCTION_SCALING], S.ReliefImage, engine)
        .sink(PlotColorFunction.Sink.FLAT).fallback(t -> F.GrayLevel(F.C1)).build();
    // the default is written as {Black, White}, and a bare colour symbol is not yet a colour - it
    // has to be evaluated into the RGBColor it stands for before it can be read
    float[][] clipping = clippingStyle(engine.evaluate(options[OPTION_CLIPPING_STYLE]), engine);

    // the heights scaled onto 0.0 ... 1.0, which is what both the shading and the colour function
    // work from
    double[][] scaled = new double[height][width];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        scaled[y][x] = span == 0.0 ? 0.0 : clampUnit((heights[y][x] - lo) / span);
      }
    }

    BufferedImage image = Pixels.fromPixels(width, height, 3, (x, y) -> {
      float[] rgb = clippingColor(clipping, heights[y][x], range);
      if (rgb == null) {
        double argument = scaleColors ? scaled[y][x] : heights[y][x];
        float[] rgba = colorFunction == null ? null
            : Colors.toRgba(colorFunction.color(argument));
        rgb = rgba == null ? new float[] {1.0f, 1.0f, 1.0f} : rgba;
      }
      double shade = shade(scaled, x, y, width, height, light, aspectBased);
      return new float[] {(float) (255.0 * rgb[0] * shade), (float) (255.0 * rgb[1] * shade),
          (float) (255.0 * rgb[2] * shade)};
    });
    return new ImageExpr(image, null,
        ImageOptions.DEFAULT.withImageSize(options[OPTION_IMAGE_SIZE]));
  }

  // ------------------------------------------------------------------- shading

  /**
   * How much of the light reaches the surface at one point, in <code>0.0 ... 1.0</code>.
   *
   * <p>
   * The default method is diffuse reflection, where the surface normal is measured against the
   * direction of the light, so a slope facing the light is bright and one facing away is dark and
   * how steep it is matters. <code>"AspectBasedShading"</code> asks only which way the slope faces
   * and ignores how steep it is, which keeps gentle features visible next to sharp ones.
   */
  private static double shade(double[][] scaled, int x, int y, int width, int height,
      double[] light, boolean aspectBased) {
    double azimuth = light[0];
    double altitude = light[1];
    if (Double.isNaN(azimuth)) {
      // LightingAngle -> None, so the colours are handed back unshaded
      return 1.0;
    }
    // the slope of the surface over the unit square, so that an array of any size describes the
    // same landscape
    double dzdx = gradientX(scaled, x, y, width) * (width > 1 ? width - 1 : 1);
    double dzdy = gradientY(scaled, x, y, height) * (height > 1 ? height - 1 : 1);

    if (aspectBased) {
      double slope = Math.hypot(dzdx, dzdy);
      if (slope == 0.0) {
        return 0.5;
      }
      // the compass direction the slope faces, measured the same way as the light
      double aspect = Math.atan2(-dzdy, -dzdx);
      return 0.5 * (1.0 + Math.cos(azimuth - aspect));
    }

    double lx = Math.cos(altitude) * Math.cos(azimuth);
    double ly = Math.cos(altitude) * Math.sin(azimuth);
    double lz = Math.sin(altitude);
    // the upward normal of the surface z = f(x, y) is (-dz/dx, -dz/dy, 1), normalized
    double norm = Math.sqrt(dzdx * dzdx + dzdy * dzdy + 1.0);
    double cosine = (-dzdx * lx - dzdy * ly + lz) / norm;
    return cosine <= 0.0 ? 0.0 : cosine;
  }

  /** The change in height per pixel across the row, one sided at the two ends. */
  private static double gradientX(double[][] scaled, int x, int y, int width) {
    if (width < 2) {
      return 0.0;
    }
    if (x == 0) {
      return scaled[y][1] - scaled[y][0];
    }
    if (x == width - 1) {
      return scaled[y][width - 1] - scaled[y][width - 2];
    }
    return (scaled[y][x + 1] - scaled[y][x - 1]) / 2.0;
  }

  /**
   * The change in height per pixel up the column. Rows run down the image and the light is measured
   * against a y axis that runs up, so the difference is taken the other way round.
   */
  private static double gradientY(double[][] scaled, int x, int y, int height) {
    if (height < 2) {
      return 0.0;
    }
    if (y == 0) {
      return scaled[0][x] - scaled[1][x];
    }
    if (y == height - 1) {
      return scaled[height - 2][x] - scaled[height - 1][x];
    }
    return (scaled[y - 1][x] - scaled[y + 1][x]) / 2.0;
  }

  // ------------------------------------------------------------------- options

  /**
   * The direction the light comes from as <code>{azimuth, altitude}</code> in radians, or
   * <code>{NaN, NaN}</code> for <code>None</code>.
   *
   * <p>
   * Azimuth is measured the way the reference does: 0 is the right hand side and 180 degrees is the
   * left. A single angle is an azimuth and leaves the light half way up the sky.
   */
  private static double[] lightingAngle(IExpr value, EvalEngine engine) {
    if (value.isNone()) {
      return new double[] {Double.NaN, Double.NaN};
    }
    if (value == S.Automatic) {
      return new double[] {DEFAULT_AZIMUTH, DEFAULT_ALTITUDE};
    }
    if (value.isList2()) {
      double azimuth = value.first().evalfNaN();
      double altitude = value.second().evalfNaN();
      if (Double.isNaN(azimuth) || Double.isNaN(altitude)) {
        return new double[] {DEFAULT_AZIMUTH, DEFAULT_ALTITUDE};
      }
      return new double[] {azimuth, altitude};
    }
    double azimuth = value.evalfNaN();
    return Double.isNaN(azimuth) //
        ? new double[] {DEFAULT_AZIMUTH, DEFAULT_ALTITUDE}
        : new double[] {azimuth, DEFAULT_ALTITUDE};
  }

  /**
   * The range of heights the image covers as <code>{low, high}</code>.
   *
   * @return <code>null</code> if the option is a range that says nothing
   */
  private static double[] plotRange(IExpr value, double[][] heights, EvalEngine engine) {
    if (value.isList2()) {
      double lo = value.first().evalfNaN();
      double hi = value.second().evalfNaN();
      if (Double.isNaN(lo) || Double.isNaN(hi) || hi < lo) {
        return null;
      }
      return new double[] {lo, hi};
    }
    // All, and Automatic too: every height is inside the picture. The reference lets Automatic
    // clip the extremes; nothing here does, so the two are the same.
    double lo = Double.POSITIVE_INFINITY;
    double hi = Double.NEGATIVE_INFINITY;
    for (double[] row : heights) {
      for (double z : row) {
        lo = Math.min(lo, z);
        hi = Math.max(hi, z);
      }
    }
    return new double[] {lo, hi};
  }

  /**
   * The two colours that mark heights outside the plot range, as <code>{low, high}</code> rgb
   * triples, either of which may be <code>null</code> for "no marking".
   */
  private static float[][] clippingStyle(IExpr value, EvalEngine engine) {
    if (value == S.Automatic) {
      return new float[][] {{0.0f, 0.0f, 0.0f}, {1.0f, 1.0f, 1.0f}};
    }
    if (value.isNone()) {
      return new float[][] {null, null};
    }
    if (value.isList2()) {
      return new float[][] {rgbOrNull(value.first(), engine), rgbOrNull(value.second(), engine)};
    }
    float[] both = rgbOrNull(value, engine);
    return new float[][] {both, both};
  }

  private static float[] rgbOrNull(IExpr color, EvalEngine engine) {
    if (color.isNone()) {
      return null;
    }
    float[] rgba = Colors.toRgba(engine.evaluate(color));
    return rgba == null ? null : new float[] {rgba[0], rgba[1], rgba[2]};
  }

  /** The clipping colour for a height, or <code>null</code> where it is inside the range. */
  private static float[] clippingColor(float[][] clipping, double z, double[] range) {
    if (z < range[0]) {
      return clipping[0];
    }
    if (z > range[1]) {
      return clipping[1];
    }
    return null;
  }

  private static String methodName(IExpr value) {
    if (value.isString()) {
      return value.toString();
    }
    if (value.isRule() && value.first().isString()) {
      return value.first().toString();
    }
    return DIFFUSE_REFLECTION;
  }

  /**
   * Report an argument that is not a height matrix and leave the expression alone.
   *
   * @param arg1 the rejected argument
   * @param engine the evaluation engine
   * @return {@link F#NIL}, always
   */
  private static IExpr notAMatrix(IExpr arg1, EvalEngine engine) {
    // Argument `1` at position `2` is not a 2x2 or larger numerica matrix of real values.
    // The spelling of "numerica" is Mathematica's own and is kept so the text matches.
    return Errors.printMessage(S.ReliefImage, "input", F.List(arg1, F.C1), engine);
  }

  /** The array as a rectangle of doubles, or <code>null</code> if a height is not a real number. */
  private static double[][] heights(IAST array, int width, int height) {
    double[][] values = new double[height][width];
    for (int y = 0; y < height; y++) {
      IExpr rowExpr = array.get(y + 1);
      if (!rowExpr.isList() || ((IAST) rowExpr).argSize() < width) {
        // dimensions() answers a pair for arguments that are not matrices at all, such as
        // {"a string", {b,1}}, so a row here is not necessarily a list of the expected length
        return null;
      }
      IAST row = (IAST) rowExpr;
      for (int x = 0; x < width; x++) {
        double z = row.get(x + 1).evalfNaN();
        if (Double.isNaN(z) || Double.isInfinite(z)) {
          return null;
        }
        values[y][x] = z;
      }
    }
    return values;
  }

  private static double clampUnit(double value) {
    if (value < 0.0) {
      return 0.0;
    }
    return value > 1.0 ? 1.0 : value;
  }

  // ------------------------------------------------------------------- plumbing

  private static final int OPTION_CLIPPING_STYLE = 0;
  private static final int OPTION_COLOR_FUNCTION = 1;
  private static final int OPTION_COLOR_FUNCTION_SCALING = 2;
  private static final int OPTION_IMAGE_SIZE = 3;
  private static final int OPTION_LIGHTING_ANGLE = 4;
  private static final int OPTION_METHOD = 5;
  private static final int OPTION_PLOT_RANGE = 6;

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol, //
        new IBuiltInSymbol[] {S.ClippingStyle, S.ColorFunction, S.ColorFunctionScaling,
            S.ImageSize, S.LightingAngle, S.Method, S.PlotRange}, //
        new IExpr[] {F.list(S.Black, S.White), S.Automatic, S.True, S.Automatic, S.Automatic,
            S.Automatic, S.All});
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }
}
