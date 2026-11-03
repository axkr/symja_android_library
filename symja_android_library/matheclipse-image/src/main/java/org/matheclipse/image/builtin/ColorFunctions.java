package org.matheclipse.image.builtin;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.svg.ColorUtil;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.image.algo.Boof;
import org.matheclipse.image.algo.Colors;
import org.matheclipse.image.algo.Pixels;
import org.matheclipse.image.algo.Raster;
import org.matheclipse.image.expression.data.ImageExpr;
import org.matheclipse.image.expression.data.ImageOptions;

/**
 * Colour: <code>ColorConvert</code>, <code>ColorNegate</code>, <code>ColorSeparate</code>,
 * <code>ColorCombine</code>, <code>ColorQuantize</code>, <code>ColorReplace</code>,
 * <code>ColorDistance</code>, and the three alpha channel functions.
 *
 * <p>
 * An image is in one of the spaces <code>"Grayscale"</code>, <code>"RGB"</code>, <code>"HSB"</code>
 * and <code>"CMYK"</code>. An HSB or CMYK image keeps its samples in that space and is shown from
 * the RGB picture they denote, so <code>ColorConvert</code> of an image goes between these four
 * spaces, and back to RGB decodes the samples again. A colour <em>directive</em> converts to
 * <code>"XYZ"</code> as well, and a list of directives converts directive by directive.
 * <code>ColorSeparate(image, space)</code> gives the channels of other spaces as greyscale planes
 * and <code>ColorCombine({h, s, b}, "HSB")</code> puts them back together. Their samples are read
 * from {@link ImageExpr#raster()}, so nothing is rounded to 8 bits.
 */
public class ColorFunctions {

  private static class Initializer {

    private static void init() {
      S.AlphaChannel.setEvaluator(new AlphaChannel());
      S.ColorCombine.setEvaluator(new ColorCombine());
      S.Colorize.setEvaluator(new Colorize());
      S.ColorConvert.setEvaluator(new ColorConvert());
      S.ColorDistance.setEvaluator(new ColorDistance());
      S.ColorNegate.setEvaluator(new ColorNegate());
      S.ColorQuantize.setEvaluator(new ColorQuantize());
      S.ColorReplace.setEvaluator(new ColorReplace());
      S.ColorSeparate.setEvaluator(new ColorSeparate());
      S.RemoveAlphaChannel.setEvaluator(new RemoveAlphaChannel());
      S.SetAlphaChannel.setEvaluator(new SetAlphaChannel());
    }
  }

  /**
   * <code>Colorize(m)</code> or <code>Colorize(m, ColorFunction -&gt; f)</code> - an image of the
   * matrix <code>m</code> of non-negative integer labels with one colour for each label. The label
   * <code>0</code> is black; of the labels <code>1, ..., n</code> label <code>k</code> gets the
   * colour of <code>f((2*k-1)/(2*n))</code>, or a colour of the plot palette if no function is
   * given. A name <code>f</code> stands for the gradient <code>ColorData(f)</code>.
   *
   * <p>
   * <code>Colorize(image, ColorFunction -&gt; f)</code> - the RGB byte image in which a pixel takes
   * its colour from a lookup table of up to 1024 entries, see {@link #colorizeImage}.
   */
  private static class Colorize extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr colorFunction = S.Automatic;
      boolean scaling = true;
      for (int i = 2; i < ast.size(); i++) {
        IExpr option = ast.get(i);
        if (option.isRuleAST() && option.first() == S.ColorFunction) {
          colorFunction = option.second();
          if (colorFunction.isString()) {
            colorFunction = engine.evaluate(F.unaryAST1(S.ColorData, colorFunction));
          }
        } else if (option.isRuleAST() && option.first() == S.ColorFunctionScaling
            && ast.arg1() instanceof ImageExpr) {
          scaling = !option.second().isFalse();
        } else {
          return F.NIL;
        }
      }
      if (ast.arg1() instanceof ImageExpr) {
        return colorizeImage((ImageExpr) ast.arg1(), colorFunction, scaling, engine);
      }
      int[] dimensions = ast.arg1().isMatrix(false);
      if (dimensions == null || dimensions[0] == 0 || dimensions[1] == 0) {
        return F.NIL;
      }
      final int height = dimensions[0];
      final int width = dimensions[1];
      int[][] labels = new int[height][width];
      int n = 0;
      for (int y = 0; y < height; y++) {
        IAST row = (IAST) ast.arg1().getAt(y + 1);
        for (int x = 0; x < width; x++) {
          int label = row.get(x + 1).toIntDefault(-1);
          if (label < 0) {
            return F.NIL;
          }
          labels[y][x] = label;
          n = Math.max(n, label);
        }
      }
      // the byte values of the colour of each label which occurs
      java.util.Map<Integer, int[]> palette = new java.util.HashMap<Integer, int[]>();
      palette.put(0, new int[] {0, 0, 0});
      for (int[] row : labels) {
        for (int k : row) {
          if (palette.containsKey(k)) {
            continue;
          }
          if (colorFunction.isAutomatic()) {
            org.matheclipse.core.convert.RGBColor rgb =
                org.matheclipse.core.graphics.GraphicsOptions.plotStyleColor(k - 1, F.NIL);
            palette.put(k, new int[] {rgb.getRed(), rgb.getGreen(), rgb.getBlue()});
          } else {
            IExpr value = F.QQ(2 * k - 1, 2 * n);
            float[] rgba = Colors
                .toRgba(engine.evaluate(F.unaryAST1(colorFunction, F.num(value.evalf()))));
            if (rgba == null) {
              return F.NIL;
            }
            palette.put(k, new int[] {toByte(rgba[0]), toByte(rgba[1]), toByte(rgba[2])});
          }
        }
      }
      BufferedImage image = Pixels.fromPixels(width, height, 3, (x, y) -> {
        int[] rgb = palette.get(labels[y][x]);
        return new float[] {rgb[0], rgb[1], rgb[2]};
      });
      return new ImageExpr(image, null, ImageOptions.DEFAULT);
    }

    /**
     * The RGB byte image of an image coloured through a lookup table. A colour image goes to grey
     * levels first and samples are clipped to <code>0 ... 1</code>. A pixel <code>v</code> takes
     * entry <code>Min(Floor(1024*v), 1023)</code>, in an image of an integer type its integer. The
     * table runs up to the largest entry in use, <code>m</code>, and is filled in full, after the
     * function has been tried on <code>0, 1/2, 1</code> twice: entry <code>i</code> is
     * <code>f(i/m)</code>, with <code>ColorFunctionScaling -&gt; False</code> <code>f(i/1023)</code>.
     */
    private static IExpr colorizeImage(ImageExpr imageExpr, IExpr colorFunction, boolean scaling,
        EvalEngine engine) {
      if (colorFunction.isAutomatic()) {
        return F.NIL;
      }
      Raster raster = ColorConvert.samplesIn(imageExpr, ColorConvert.GRAYSCALE);
      if (raster == null) {
        return F.NIL;
      }
      final String type = imageExpr.sampleType();
      final int[][] index = new int[raster.height][raster.width];
      int last = 0;
      for (int y = 0; y < raster.height; y++) {
        for (int x = 0; x < raster.width; x++) {
          index[y][x] = tableIndex(raster.get(x, y, 0), type);
          last = Math.max(last, index[y][x]);
        }
      }
      for (int i = 0; i < 6; i++) {
        engine.evaluate(F.unaryAST1(colorFunction, F.num((i % 3) / 2.0)));
      }
      final double step = scaling ? (last == 0 ? 0.0 : 1.0 / last) : 1.0 / 1023.0;
      final int[][] table = new int[last + 1][];
      for (int i = 0; i <= last; i++) {
        engine.checkTimeLimit();
        double[] rgba =
            Colors.toRgbaDouble(engine.evaluate(F.unaryAST1(colorFunction, F.num(i * step))));
        if (rgba == null) {
          return F.NIL;
        }
        table[i] = new int[] {tableByte(rgba[0]), tableByte(rgba[1]), tableByte(rgba[2])};
      }
      BufferedImage image = Pixels.fromPixels(raster.width, raster.height, 3, (x, y) -> {
        int[] rgb = table[index[y][x]];
        return new float[] {rgb[0], rgb[1], rgb[2]};
      });
      return new ImageExpr(image, null, ImageOptions.DEFAULT);
    }

    /** The table entry of a sample of an image of <code>type</code>. */
    private static int tableIndex(double value, String type) {
      if (Raster.isIntegerType(type)) {
        return Raster.level(value, Raster.maxOf(type));
      }
      return (int) Math.min(1023.0, Math.floor(Math.max(0.0, Math.min(1.0, value)) * 1024.0));
    }

    /** A channel of a table entry as a byte: <code>Ceiling(256*c) - 1</code>. */
    private static int tableByte(double value) {
      return value <= 0.0 ? 0 : (int) Math.max(0.0, Math.min(255.0, Math.ceil(value * 256.0) - 1.0));
    }

    /** A channel value of <code>0.0 ... 1.0</code> as a byte: 1/2 is 127 and 5/6 is 213. */
    private static int toByte(float value) {
      return Math.max(0, Math.min(255, (int) Math.floor(value * 255.999)));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }
  }

  /**
   * <code>ColorConvert(image, space)</code> or <code>ColorConvert(color, space)</code> - the same
   * colour in another colour space.
   */
  private static class ColorConvert extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr rasterized = ImagePropertyFunctions.withRasterizedGraphics(ast, 1, engine);
      if (rasterized.isPresent()) {
        return rasterized;
      }
      if (!ast.arg2().isString()) {
        return F.NIL;
      }
      String space = canonicalSpace(ast.arg2().toString());
      if (ast.arg1() instanceof ImageExpr) {
        IExpr converted = convertColorSpace((ImageExpr) ast.arg1(), space);
        if (converted.isPresent()) {
          return converted;
        }
      }
      if (ast.arg1() instanceof ImageExpr && GRAYSCALE.equals(space)) {
        IExpr gray = realGrayscale((ImageExpr) ast.arg1(), engine);
        if (gray.isPresent()) {
          return gray;
        }
      }
      BufferedImage image = ImagePropertyFunctions.bufferedImage(ast.arg1());
      if (image != null) {
        return convertImage(image, space);
      }
      if (ast.arg1().isList() && ast.arg1().argSize() > 0
          && ((IAST) ast.arg1()).forAll(ColorConvert::isColorDirective)) {
        // a list of colours converts colour by colour
        IASTAppendable result = F.ListAlloc(ast.arg1().argSize());
        for (IExpr color : (IAST) ast.arg1()) {
          IExpr converted = convertColor(color, space);
          if (converted.isNIL()) {
            return F.NIL;
          }
          result.append(converted);
        }
        return result;
      }
      return convertColor(ast.arg1(), space);
    }

    static final String GRAYSCALE = "Grayscale";
    private static final String RGB = "RGB";
    private static final String HSB = "HSB";
    private static final String CMYK = "CMYK";
    private static final String XYZ = "XYZ";
    private static final String LAB = "LAB";

    /**
     * The colour space a name stands for: a space may be named as the directive whose colours are
     * in it, <code>"GrayLevel"</code> for <code>"Grayscale"</code> or <code>"RGBColor"</code> for
     * <code>"RGB"</code>. A name which is no space handled here comes back as it is.
     */
    static String canonicalSpace(String space) {
      if ("Grayscale".equalsIgnoreCase(space) || "Gray".equalsIgnoreCase(space)
          || "GrayLevel".equalsIgnoreCase(space)) {
        return GRAYSCALE;
      }
      if ("RGB".equalsIgnoreCase(space) || "RGBColor".equalsIgnoreCase(space)) {
        return RGB;
      }
      if ("HSB".equalsIgnoreCase(space) || "HSV".equalsIgnoreCase(space)
          || "Hue".equalsIgnoreCase(space)) {
        return HSB;
      }
      if ("CMYK".equalsIgnoreCase(space) || "CMYKColor".equalsIgnoreCase(space)) {
        return CMYK;
      }
      if ("XYZ".equalsIgnoreCase(space) || "XYZColor".equalsIgnoreCase(space)) {
        return XYZ;
      }
      if ("LAB".equalsIgnoreCase(space) || "LABColor".equalsIgnoreCase(space)) {
        return LAB;
      }
      return space;
    }

    /** A colour directive, as opposed to a list of channel values which is a colour as well. */
    private static boolean isColorDirective(IExpr expr) {
      return expr.isAST(S.RGBColor) || expr.isAST(S.GrayLevel) || expr.isAST(S.Hue)
          || expr.isAST(S.CMYKColor) || expr.isAST(S.XYZColor) || expr.isAST(S.LABColor);
    }

    /**
     * The grey level <code>0.299*r + 0.587*g + 0.114*b</code> of an image of real samples, from the
     * samples themselves: it gives <code>0.299</code> for a red pixel, where the 8 bit bitmap can
     * only say <code>76/255</code>.
     *
     * @return {@link F#NIL} for an image which isn't of a real type or has no three channels
     */
    private static IExpr realGrayscale(ImageExpr image, EvalEngine engine) {
      String type = image.sampleType();
      if (!"Real32".equals(type) && !"Real64".equals(type)) {
        return F.NIL;
      }
      double[][][] channels = ImageFilterFunctions.channels(image, engine);
      if (channels == null || channels.length < 3) {
        return F.NIL;
      }
      int height = channels[0].length;
      int width = channels[0][0].length;
      double[][] gray = new double[height][width];
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          gray[y][x] =
              0.299 * channels[0][y][x] + 0.587 * channels[1][y][x] + 0.114 * channels[2][y][x];
        }
      }
      return ImageFilterFunctions.toImage(new double[][][] {gray}, ImageOptions.DEFAULT);
    }

    /**
     * An image converted from or into the HSB or CMYK space, computed from its samples in machine
     * doubles. The result keeps the sample type of the image, except that a bilevel image gives
     * reals.
     *
     * @return {@link F#NIL} if neither the image nor <code>space</code> is HSB or CMYK, if
     *         <code>space</code> is no image colour space, or if an image with an alpha channel is
     *         to become HSB or CMYK
     */
    private static IExpr convertColorSpace(ImageExpr source, String space) {
      String from = source.storedColorSpace();
      boolean toStored = HSB.equals(space) || CMYK.equals(space);
      if (from == null && !toStored) {
        return F.NIL;
      }
      if (space.equals(from)) {
        return source;
      }
      if (!toStored && !RGB.equals(space) && !GRAYSCALE.equals(space)) {
        return F.NIL;
      }
      Raster raster = source.raster();
      if (raster == null || (from == null && raster.channels != 1 && raster.channels != 3)) {
        return F.NIL;
      }
      Raster result = converted(raster, from, space);
      ImageOptions options =
          source.getOptions().withColorSpace(toStored ? F.stringx(space) : S.Automatic);
      String type = Pixels.BIT.equals(source.sampleType()) ? Pixels.REAL32 : source.sampleType();
      ImageExpr image = ImageExpr.fromRaster(result, options, type);
      return image == null ? F.NIL : image;
    }

    /**
     * The samples of an image in <code>space</code>, which is one of the four image colour spaces.
     *
     * @return <code>null</code> for an image without samples or another space
     */
    static Raster samplesIn(ImageExpr image, String space) {
      Raster raster = image.raster();
      boolean known = GRAYSCALE.equals(space) || RGB.equals(space) || HSB.equals(space)
          || CMYK.equals(space);
      if (raster == null || !known) {
        return null;
      }
      String from = image.storedColorSpace();
      return space.equals(from) || (GRAYSCALE.equals(space) && raster.channels == 1) ? raster
          : converted(raster, from, space);
    }

    /** The samples of a raster in the space <code>from</code> in the space <code>space</code>. */
    private static Raster converted(Raster raster, String from, String space) {
      int outChannels = GRAYSCALE.equals(space) ? 1 : CMYK.equals(space) ? 4 : 3;
      Raster result = new Raster(raster.width, raster.height, outChannels);
      double[] values = new double[raster.channels];
      for (int y = 0; y < raster.height; y++) {
        for (int x = 0; x < raster.width; x++) {
          for (int c = 0; c < raster.channels; c++) {
            values[c] = raster.get(x, y, c);
          }
          double[] pixel = fromRgb(toRgb(values, from), space);
          for (int c = 0; c < outChannels; c++) {
            result.set(x, y, c, pixel[c]);
          }
        }
      }
      return result;
    }

    /** The red, green and blue of the samples of one pixel in the space <code>from</code>. */
    static double[] toRgb(double[] values, String from) {
      if (HSB.equals(from)) {
        return hsbToRgb(values[0], values[1], values[2]);
      }
      if (CMYK.equals(from)) {
        double scale = 1.0 - values[3];
        return new double[] {(1.0 - values[0]) * scale, (1.0 - values[1]) * scale,
            (1.0 - values[2]) * scale};
      }
      if (values.length < 3) {
        return new double[] {values[0], values[0], values[0]};
      }
      return new double[] {values[0], values[1], values[2]};
    }

    /** The samples of red, green and blue in <code>space</code>. */
    private static double[] fromRgb(double[] rgb, String space) {
      if (GRAYSCALE.equals(space)) {
        return new double[] {Colors.grayLevel(rgb[0], rgb[1], rgb[2])};
      }
      if (HSB.equals(space)) {
        return rgbToHsb(rgb[0], rgb[1], rgb[2]);
      }
      if (CMYK.equals(space)) {
        double black = 1.0 - Math.max(rgb[0], Math.max(rgb[1], rgb[2]));
        if (black >= 1.0) {
          return new double[] {0.0, 0.0, 0.0, 1.0};
        }
        double scale = 1.0 - black;
        return new double[] {(1.0 - rgb[0] - black) / scale, (1.0 - rgb[1] - black) / scale,
            (1.0 - rgb[2] - black) / scale, black};
      }
      return rgb;
    }

    /** Hue, saturation and brightness of a colour, each in <code>0.0 ... 1.0</code>. */
    private static double[] rgbToHsb(double r, double g, double b) {
      double max = Math.max(r, Math.max(g, b));
      double min = Math.min(r, Math.min(g, b));
      double range = max - min;
      double saturation = max > 0.0 ? range / max : 0.0;
      double hue = 0.0;
      if (range > 0.0) {
        if (r == max) {
          hue = (g - b) / range;
        } else if (g == max) {
          hue = 2.0 + (b - r) / range;
        } else {
          hue = 4.0 + (r - g) / range;
        }
        hue /= 6.0;
        if (hue < 0.0) {
          hue += 1.0;
        }
      }
      return new double[] {hue, saturation, max};
    }

    /** The red, green and blue of a hue, saturation and brightness. */
    private static double[] hsbToRgb(double hue, double saturation, double brightness) {
      double h6 = (hue - Math.floor(hue)) * 6.0;
      int sector = (int) Math.floor(h6);
      double f = h6 - sector;
      double p = brightness * (1.0 - saturation);
      double q = brightness * (1.0 - saturation * f);
      double t = brightness * (1.0 - saturation * (1.0 - f));
      switch (sector % 6) {
        case 0:
          return new double[] {brightness, t, p};
        case 1:
          return new double[] {q, brightness, p};
        case 2:
          return new double[] {p, brightness, t};
        case 3:
          return new double[] {p, q, brightness};
        case 4:
          return new double[] {t, p, brightness};
        default:
          return new double[] {brightness, p, q};
      }
    }

    private static IExpr convertImage(BufferedImage image, String space) {
      int channels = Boof.channels(image);
      int width = image.getWidth();
      int height = image.getHeight();
      if (GRAYSCALE.equals(space)) {
        return new ImageExpr(Pixels.fromPixels(width, height, 1,
            (x, y) -> new float[] {Boof.intensity(image, x, y)}), null);
      }
      if (RGB.equals(space)) {
        return new ImageExpr(Pixels.fromPixels(width, height, 3, (x, y) -> {
          float[] values = Pixels.pixel(image, x, y, channels);
          return channels == 1 //
              ? new float[] {values[0], values[0], values[0]}
              : new float[] {values[0], values[1], values[2]};
        }), null);
      }
      // LAB and the rest are not colour spaces an ImageExpr can be in - see the class comment.
      // ColorSeparate(image, space) is the way to reach their channels.
      return F.NIL;
    }

    /**
     * One colour directive in another space. The grey level, the RGB channels and the XYZ and LAB
     * coordinates are computed from the channels in machine doubles, so
     * <code>RGBColor(1, 0, 0)</code> has the grey level <code>0.299</code> exactly.
     *
     * @return {@link F#NIL} if <code>color</code> is no colour or <code>space</code> is no space
     *         handled here
     */
    private static IExpr convertColor(IExpr color, String space) {
      double[] rgbaDouble = Colors.toRgbaDouble(color);
      if (rgbaDouble == null) {
        return F.NIL;
      }
      double alpha = rgbaDouble[3];
      if (GRAYSCALE.equals(space)) {
        IExpr gray = F.num(Colors.grayLevel(rgbaDouble[0], rgbaDouble[1], rgbaDouble[2]));
        return alpha < 1.0 ? F.binaryAST2(S.GrayLevel, gray, F.num(alpha)) : F.GrayLevel(gray);
      }
      if (RGB.equals(space)) {
        IExpr r = F.num(rgbaDouble[0]);
        IExpr g = F.num(rgbaDouble[1]);
        IExpr b = F.num(rgbaDouble[2]);
        return alpha < 1.0 ? F.RGBColor(r, g, b, F.num(alpha)) : F.RGBColor(r, g, b);
      }
      if (XYZ.equals(space) || LAB.equals(space)) {
        // from the XYZ coordinates themselves, which an RGB colour would have clipped
        double[] xyz = Colors.toXyzaDouble(color);
        if (LAB.equals(space)) {
          xyz = ColorUtil.xyzToLab(xyz[0], xyz[1], xyz[2]);
        }
        IASTAppendable result = F.ast(LAB.equals(space) ? S.LABColor : S.XYZColor, 4);
        for (int i = 0; i < 3; i++) {
          result.append(F.num(xyz[i]));
        }
        if (alpha < 1.0) {
          result.append(F.num(alpha));
        }
        return result;
      }
      float[] rgba = Colors.toRgba(color);
      if (rgba == null) {
        return F.NIL;
      }
      if (HSB.equals(space)) {
        float[] hsb = Colors.rgbToHsb(rgba);
        return F.Hue(F.num(hsb[0]), F.num(hsb[1]), F.num(hsb[2]));
      }
      if (CMYK.equals(space)) {
        float[] cmyk = Colors.rgbToCmyk(rgba);
        return F.quaternary(S.CMYKColor, F.num(cmyk[0]), F.num(cmyk[1]), F.num(cmyk[2]),
            F.num(cmyk[3]));
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  /**
   * <code>ColorNegate(image)</code> or <code>ColorNegate(color)</code> - the complementary colour.
   */
  private static class ColorNegate extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr rasterized = ImagePropertyFunctions.withRasterizedGraphics(ast, 1, engine);
      if (rasterized.isPresent()) {
        return rasterized;
      }
      if (ast.arg1() instanceof ImageExpr) {
        // 1 - v of every colour sample, also of one outside of 0 ... 1; the type stays
        ImageExpr image = (ImageExpr) ast.arg1();
        org.matheclipse.image.algo.Raster raster = image.raster();
        if (raster == null) {
          return F.NIL;
        }
        ImageExpr negated = ImageExpr.fromRaster(raster.mapColor(v -> 1.0 - v),
            image.getOptions(), image.sampleType());
        return negated == null ? F.NIL : negated;
      }
      float[] rgba = Colors.toRgba(ast.arg1());
      if (rgba == null) {
        return F.NIL;
      }
      return Colors
          .toRGBColor(new float[] {1.0f - rgba[0], 1.0f - rgba[1], 1.0f - rgba[2], rgba[3]});
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public int status() {
      return ImplementationStatus.FULL_SUPPORT;
    }
  }

  /**
   * <code>ColorSeparate(image)</code> - the channels as separate greyscale images.
   * <code>ColorSeparate(image, "R")</code> gives one of them; a colour space name converts first.
   */
  private static class ColorSeparate extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!(ast.arg1() instanceof ImageExpr)) {
        return F.NIL;
      }
      ImageExpr source = (ImageExpr) ast.arg1();
      if (ast.argSize() >= 2) {
        if (!ast.arg2().isString()) {
          return F.NIL;
        }
        String name = ast.arg2().toString();
        int channel = channelIndex(name);
        if (channel < 0) {
          // a colour space name: one greyscale plane per channel of that space
          Raster samples = ColorConvert.samplesIn(source, ColorConvert.canonicalSpace(name));
          String type = Pixels.BIT.equals(source.sampleType()) ? Pixels.REAL32 : source.sampleType();
          return samples == null ? F.NIL : separate(samples, type);
        }
        Raster samples = source.raster();
        if (samples == null || samples.channels <= channel) {
          return F.NIL;
        }
        return plane(samples, channel, source.sampleType());
      }
      Raster samples = source.raster();
      return samples == null ? F.NIL : separate(samples, source.sampleType());
    }

    /** R, G, B and A as channel positions; anything else is a colour space name. */
    private static int channelIndex(String name) {
      switch (name) {
        case "R":
        case "Red":
          return 0;
        case "G":
        case "Green":
          return 1;
        case "B":
        case "Blue":
          return 2;
        case "A":
        case "Alpha":
          return 3;
        default:
          return -1;
      }
    }

    private static IExpr separate(Raster samples, String type) {
      IASTAppendable result = F.ListAlloc(samples.channels);
      for (int c = 0; c < samples.channels; c++) {
        IExpr plane = plane(samples, c, type);
        if (plane.isNIL()) {
          return F.NIL;
        }
        result.append(plane);
      }
      return result;
    }

    /** A channel as a greyscale image of <code>type</code>. */
    private static IExpr plane(Raster samples, int channel, String type) {
      ImageExpr image =
          ImageExpr.fromRaster(samples.channel(channel).storedAs(type), ImageOptions.DEFAULT, type);
      return image == null ? F.NIL : image;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  /**
   * <code>ColorCombine({image1, image2, ...})</code> - one image out of the channels of the images,
   * which have the same size; it has the largest of their types. With a colour space name there are
   * as many channels as that space needs, or one more which is the alpha channel. HSB and CMYK
   * samples are decoded to RGB.
   */
  private static class ColorCombine extends AbstractEvaluator {

    /** The image types by rank: the result has the last one which occurs. */
    private static final List<String> TYPES =
        List.of(Pixels.BIT, Pixels.BYTE, Pixels.BIT16, Pixels.REAL32, Pixels.REAL64);

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.arg1().isList() || ast.arg1().argSize() == 0) {
        return F.NIL;
      }
      IAST list = (IAST) ast.arg1();
      String space = null;
      if (ast.argSize() >= 2) {
        if (!ast.arg2().isString()) {
          return F.NIL;
        }
        space = ColorConvert.canonicalSpace(ast.arg2().toString());
      }
      List<double[]> channels = new ArrayList<>();
      int width = -1;
      int height = -1;
      int rank = 0;
      for (int i = 1; i < list.size(); i++) {
        if (!(list.get(i) instanceof ImageExpr)) {
          return F.NIL;
        }
        ImageExpr image = (ImageExpr) list.get(i);
        Raster raster = image.raster();
        if (raster == null || (i > 1 && (raster.width != width || raster.height != height))) {
          return F.NIL;
        }
        width = raster.width;
        height = raster.height;
        rank = Math.max(rank, TYPES.indexOf(image.sampleType()));
        boolean single = Pixels.REAL32.equals(image.sampleType());
        for (int c = 0; c < raster.channels; c++) {
          double[] samples = new double[width * height];
          for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
              double sample = raster.get(x, y, c);
              samples[y * width + x] = single ? (float) sample : sample;
            }
          }
          channels.add(samples);
        }
      }
      int total = channels.size();
      int needed = neededChannels(space, total);
      if (needed < 0 || (total != needed && total != needed + 1)) {
        return F.NIL;
      }
      boolean hsb = "HSB".equals(space);
      boolean cmyk = "CMYK".equals(space);
      int colors = hsb || cmyk ? 3 : needed;
      int outChannels = colors + total - needed;
      Raster result = new Raster(width, height, outChannels);
      double[] pixel = new double[total];
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          for (int c = 0; c < total; c++) {
            pixel[c] = channels.get(c)[y * width + x];
          }
          double[] decoded = hsb || cmyk ? ColorConvert.toRgb(pixel, space) : pixel;
          for (int c = 0; c < colors; c++) {
            result.set(x, y, c, decoded[c]);
          }
          if (outChannels > colors) {
            result.set(x, y, colors, pixel[total - 1]);
          }
        }
      }
      ImageOptions options = space == null || hsb || cmyk ? ImageOptions.DEFAULT
          : ImageOptions.DEFAULT.withColorSpace(F.stringx(space));
      ImageExpr image = ImageExpr.fromRaster(result, options, TYPES.get(rank));
      return image == null ? F.NIL : image;
    }

    /**
     * The colour channels of a space; <code>total</code> without a space.
     *
     * @return <code>-1</code> if there is no such image colour space
     */
    private static int neededChannels(String space, int total) {
      if (space == null) {
        return total;
      }
      switch (space) {
        case ColorConvert.GRAYSCALE:
          return 1;
        case "RGB":
        case "HSB":
          return 3;
        case "CMYK":
          return 4;
        default:
          return -1;
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  /**
   * <code>ColorQuantize(image, n)</code> - the image with at most <code>n</code> distinct colours,
   * chosen by median cut.
   *
   * <p>
   * Median cut splits the box of colours present in the image along its longest channel, at the
   * median, until there are <code>n</code> boxes; each box then contributes the mean of its
   * colours. It is deterministic, which a k-means palette would not be.
   */
  private static class ColorQuantize extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      BufferedImage image = ImagePropertyFunctions.bufferedImage(ast.arg1());
      if (image == null) {
        return F.NIL;
      }
      int colorCount = ast.arg2().toIntDefault();
      if (colorCount < 1) {
        return F.NIL;
      }
      int channels = Boof.channels(image);
      int width = image.getWidth();
      int height = image.getHeight();

      List<float[]> colors = new ArrayList<>(width * height);
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          colors.add(Pixels.pixel(image, x, y, channels));
        }
      }
      List<float[]> palette = medianCut(colors, colorCount, Math.min(channels, 3));
      if (palette.isEmpty()) {
        return F.NIL;
      }
      final int colorChannels = Math.min(channels, 3);
      return new ImageExpr(Pixels.fromPixels(width, height, channels, (x, y) -> {
        float[] values = Pixels.pixel(image, x, y, channels);
        float[] nearest = nearest(palette, values, colorChannels);
        float[] result = values.clone();
        System.arraycopy(nearest, 0, result, 0, colorChannels);
        return result;
      }), null);
    }

    private static List<float[]> medianCut(List<float[]> colors, int colorCount, int channels) {
      List<List<float[]>> boxes = new ArrayList<>();
      boxes.add(colors);
      while (boxes.size() < colorCount) {
        int widest = -1;
        int widestChannel = 0;
        float widestRange = 0.0f;
        for (int i = 0; i < boxes.size(); i++) {
          List<float[]> box = boxes.get(i);
          if (box.size() < 2) {
            continue;
          }
          for (int c = 0; c < channels; c++) {
            float low = Float.MAX_VALUE;
            float high = -Float.MAX_VALUE;
            for (float[] color : box) {
              low = Math.min(low, color[c]);
              high = Math.max(high, color[c]);
            }
            if (high - low > widestRange) {
              widestRange = high - low;
              widest = i;
              widestChannel = c;
            }
          }
        }
        if (widest < 0 || widestRange == 0.0f) {
          break;
        }
        List<float[]> box = boxes.remove(widest);
        final int channel = widestChannel;
        box.sort((left, right) -> Float.compare(left[channel], right[channel]));
        int middle = box.size() / 2;
        boxes.add(new ArrayList<>(box.subList(0, middle)));
        boxes.add(new ArrayList<>(box.subList(middle, box.size())));
      }

      List<float[]> palette = new ArrayList<>(boxes.size());
      for (List<float[]> box : boxes) {
        if (box.isEmpty()) {
          continue;
        }
        float[] mean = new float[channels];
        for (float[] color : box) {
          for (int c = 0; c < channels; c++) {
            mean[c] += color[c];
          }
        }
        for (int c = 0; c < channels; c++) {
          mean[c] /= box.size();
        }
        palette.add(mean);
      }
      return palette;
    }

    private static float[] nearest(List<float[]> palette, float[] color, int channels) {
      float[] best = palette.get(0);
      float bestDistance = Float.MAX_VALUE;
      for (float[] candidate : palette) {
        float distance = 0.0f;
        for (int c = 0; c < channels; c++) {
          float difference = candidate[c] - color[c];
          distance += difference * difference;
        }
        if (distance < bestDistance) {
          bestDistance = distance;
          best = candidate;
        }
      }
      return best;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  /**
   * <code>ColorReplace(image, old -&gt; new)</code> - replace every pixel close to
   * <code>old</code>. A list of rules replaces several colours at once, and a third argument widens
   * the tolerance, measured the way {@link ColorDistance} measures it.
   */
  private static class ColorReplace extends AbstractEvaluator {

    /** Half of one step of an 8 bit channel, in CIE76 units - close enough to "the same colour". */
    private static final double DEFAULT_TOLERANCE = 0.1;

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      BufferedImage image = ImagePropertyFunctions.bufferedImage(ast.arg1());
      if (image == null) {
        if (!(ast.arg1() instanceof ImageExpr) && !ast.arg1().isAST(S.Graphics)
            && !ast.arg1().isAST(S.Graphics3D)) {
          // Expecting an image or graphics instead of `1`.
          return Errors.printMessage(S.ColorReplace, "imginv", F.list(ast.arg1()), engine);
        }
        return F.NIL;
      }
      IAST rules = ast.arg2().isListOfRules(false) //
          ? (IAST) ast.arg2()
          : (ast.arg2().isRuleAST() ? F.List(ast.arg2()) : null);
      if (rules == null) {
        return F.NIL;
      }
      float[][] from = new float[rules.argSize()][];
      float[][] to = new float[rules.argSize()][];
      for (int i = 0; i < rules.argSize(); i++) {
        IAST rule = (IAST) rules.get(i + 1);
        from[i] = Colors.toRgba(rule.arg1());
        to[i] = Colors.toRgba(rule.arg2());
        if (from[i] == null || to[i] == null) {
          return F.NIL;
        }
      }
      double tolerance = DEFAULT_TOLERANCE;
      if (ast.argSize() >= 3) {
        tolerance = ast.arg3().evalfNaN();
        if (Double.isNaN(tolerance)) {
          return F.NIL;
        }
        // the third argument is a fraction of the colour space, CIE76 counts in about 100 units
        tolerance *= 100.0;
      }
      final double limit = tolerance;

      int channels = Boof.channels(image);
      int colorChannels = channels == 4 ? 3 : channels;
      return new ImageExpr(
          Pixels.fromPixels(image.getWidth(), image.getHeight(), channels, (x, y) -> {
            float[] values = Pixels.pixel(image, x, y, channels);
            float[] rgb = unit(values, channels);
            for (int i = 0; i < from.length; i++) {
              if (Colors.distance(rgb, from[i]) <= limit) {
                float[] result = values.clone();
                for (int c = 0; c < colorChannels; c++) {
                  result[c] = to[i][Math.min(c, 2)] * 255.0f;
                }
                return result;
              }
            }
            return values;
          }), null);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  /**
   * <code>ColorDistance(color1, color2)</code> - the CIE 1976 difference between two colours.
   * <code>ColorDistance(image, color)</code> gives a greyscale image of the distance of every
   * pixel.
   */
  private static class ColorDistance extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      float[] second = Colors.toRgba(ast.arg2());
      if (second == null) {
        return F.NIL;
      }
      BufferedImage image = ImagePropertyFunctions.bufferedImage(ast.arg1());
      if (image != null) {
        int channels = Boof.channels(image);
        return new ImageExpr(Pixels.fromPixels(image.getWidth(), image.getHeight(), 1, (x, y) -> {
          float[] rgb = unit(Pixels.pixel(image, x, y, channels), channels);
          // CIE76 runs to about 100, and the result has to fit in a greyscale image
          return new float[] {(float) (255.0 * Colors.distance(rgb, second) / 100.0)};
        }), null);
      }
      float[] first = Colors.toRgba(ast.arg1());
      return first == null ? F.NIL : F.num(Colors.distance(first, second));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  /**
   * <code>AlphaChannel(image)</code> - the transparency as a greyscale image. An image without an
   * alpha channel is fully opaque, so the result is all white.
   */
  private static class AlphaChannel extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!(ast.arg1() instanceof ImageExpr)) {
        return F.NIL;
      }
      ImageExpr image = (ImageExpr) ast.arg1();
      Raster raster = image.raster();
      if (raster == null) {
        return F.NIL;
      }
      Raster alpha;
      if (hasAlpha(image)) {
        alpha = raster.channel(raster.channels - 1);
      } else {
        alpha = new Raster(raster.width, raster.height, 1).mapColor(v -> 1.0);
      }
      return realImage(alpha, ImageOptions.DEFAULT);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public int status() {
      return ImplementationStatus.FULL_SUPPORT;
    }
  }

  /**
   * <code>SetAlphaChannel(image, alpha)</code> - the image with the given transparency, taken from
   * a number or from an image, which sits in the centre of <code>image</code> if the sizes differ
   * and is <code>0</code> around it. Without a second argument the image becomes fully opaque, and
   * an alpha channel it has is replaced. A graphic is drawn at the size of the alpha image.
   */
  private static class SetAlphaChannel extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isAST(S.Graphics) && ast.argSize() >= 2 && ast.arg2() instanceof ImageExpr) {
        Raster size = ((ImageExpr) ast.arg2()).raster();
        if (size == null) {
          return F.NIL;
        }
        IExpr image = engine.evaluate(F.binaryAST2(S.Rasterize, ast.arg1(),
            F.Rule(S.RasterSize, F.List(F.ZZ(size.width), F.ZZ(size.height)))));
        return image instanceof ImageExpr ? engine.evaluate(ast.setAtCopy(1, image)) : F.NIL;
      }
      IExpr rasterized = ImagePropertyFunctions.withRasterizedGraphics(ast, 1, engine);
      if (rasterized.isPresent()) {
        return rasterized;
      }
      if (!(ast.arg1() instanceof ImageExpr)) {
        return F.NIL;
      }
      ImageExpr source = (ImageExpr) ast.arg1();
      Raster raster = rgbOrGray(source);
      if (raster == null) {
        return F.NIL;
      }
      int colors = hasAlpha(source) ? raster.channels - 1 : raster.channels;
      Raster alphaPlane = null;
      double constant = 1.0;
      if (ast.argSize() >= 2) {
        if (ast.arg2() instanceof ImageExpr) {
          alphaPlane = ((ImageExpr) ast.arg2()).raster();
          if (alphaPlane == null) {
            return F.NIL;
          }
        } else {
          constant = ast.arg2().evalfNaN();
          if (Double.isNaN(constant)) {
            return F.NIL;
          }
        }
      }
      Raster result = new Raster(raster.width, raster.height, colors + 1);
      for (int y = 0; y < raster.height; y++) {
        for (int x = 0; x < raster.width; x++) {
          for (int c = 0; c < colors; c++) {
            result.set(x, y, c, raster.get(x, y, c));
          }
          result.set(x, y, colors, alphaPlane == null ? constant : alphaAt(alphaPlane, raster, x, y));
        }
      }
      return realImage(result, ImageOptions.DEFAULT.withColorSpace(alphaSpace(colors)));
    }

    /**
     * The sample of the alpha image over a pixel of <code>image</code>: an image of another size
     * sits in the centre, its offset from the bottom left corner rounded half to even.
     */
    private static double alphaAt(Raster alpha, Raster image, int x, int y) {
      int bottom = (int) Math.rint((image.height - alpha.height) / 2.0);
      int top = image.height - alpha.height - bottom;
      int left = (int) Math.rint((image.width - alpha.width) / 2.0);
      int row = y - top;
      int column = x - left;
      return row >= 0 && row < alpha.height && column >= 0 && column < alpha.width
          ? alpha.get(column, row, 0)
          : 0.0;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  /**
   * <code>RemoveAlphaChannel(image)</code> - the image without its alpha channel, the colour samples
   * as they are. <code>RemoveAlphaChannel(image, color)</code> composes it onto the colour first.
   */
  private static class RemoveAlphaChannel extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!(ast.arg1() instanceof ImageExpr)) {
        return F.NIL;
      }
      double[] background = null;
      if (ast.argSize() >= 2) {
        // a number is no colour here - RemoveAlphaChannel(image, 0.) is ::invcolor
        background = ast.arg2().isNumber() ? null : Colors.toRgbaDouble(ast.arg2());
        if (background == null) {
          return Errors.printMessage(S.RemoveAlphaChannel, "invcolor", F.List(ast.arg2()), engine);
        }
      }
      ImageExpr source = (ImageExpr) ast.arg1();
      Raster raster = source.raster();
      if (raster == null || !hasAlpha(source)) {
        return raster == null ? F.NIL : ast.arg1();
      }
      int colors = raster.channels - 1;
      Raster result = new Raster(raster.width, raster.height, colors);
      for (int y = 0; y < raster.height; y++) {
        for (int x = 0; x < raster.width; x++) {
          double alpha = raster.get(x, y, colors);
          for (int c = 0; c < colors; c++) {
            double value = raster.get(x, y, c);
            if (background != null) {
              double under = colors == 1
                  ? Colors.grayLevel(background[0], background[1], background[2])
                  : background[c];
              value = value * alpha + under * (1.0 - alpha);
            }
            result.set(x, y, c, value);
          }
        }
      }
      return realImage(result, source.getOptions());
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.FULL_SUPPORT;
    }
  }

  // -------------------------------------------------------------------- internals

  /**
   * Whether the last channel of an image is an alpha channel: two channels are one only in a stated
   * colour space, and the four of a CMYK image are no alpha channel.
   */
  static boolean hasAlpha(ImageExpr image) {
    int channels = image.channels();
    if (channels == 2) {
      return image.getOptions().colorSpaceName() != null;
    }
    return channels == 4 && image.storedColorSpace() == null
        && image.getOptions().colorSpaceName() != null;
  }

  /** The samples of an image in RGB or as grey levels: those of the picture of HSB or CMYK. */
  static Raster rgbOrGray(ImageExpr image) {
    return image.storedColorSpace() == null ? image.raster()
        : Raster.fromBitmap(image.getBufferedImage());
  }

  /** The <code>ColorSpace</code> of an image of <code>colors</code> and an alpha channel. */
  static IExpr alphaSpace(int colors) {
    return colors == 1 ? F.stringx(ColorConvert.GRAYSCALE)
        : colors == 3 ? F.stringx("RGB") : S.Automatic;
  }

  /** The <code>"Real32"</code> image of the samples. */
  static IExpr realImage(Raster raster, ImageOptions options) {
    ImageExpr image =
        ImageExpr.fromRaster(raster.toSinglePrecision(), options, Pixels.REAL32);
    return image == null ? F.NIL : image;
  }

  /** Channel values on the 0 ... 255 scale as an RGB triple on the 0.0 ... 1.0 scale. */
  static float[] unit(float[] values, int channels) {
    if (channels == 1) {
      float gray = values[0] / 255.0f;
      return new float[] {gray, gray, gray};
    }
    return new float[] {values[0] / 255.0f, values[1] / 255.0f, values[2] / 255.0f};
  }

  public static void initialize() {
    Initializer.init();
  }

  private ColorFunctions() {}
}
