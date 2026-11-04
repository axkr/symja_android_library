package org.matheclipse.image.builtin;

import java.util.ArrayDeque;
import java.util.Arrays;
import org.matheclipse.core.builtin.FilterFunctions;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.LinearAlgebraUtil;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.external.fastutil.ints.IntArrayList;
import org.matheclipse.image.algo.Colors;
import org.matheclipse.image.algo.Pixels;
import org.matheclipse.image.algo.Raster;
import org.matheclipse.image.algo.Skeleton;
import org.matheclipse.image.algo.Volumes;
import org.matheclipse.image.algo.Watershed;
import org.matheclipse.image.expression.data.Image3DExpr;
import org.matheclipse.image.expression.data.ImageExpr;
import org.matheclipse.image.expression.data.ImageOptions;

/**
 * The filters of an image:
 * <ul>
 * <li>linear: <code>GaussianFilter</code>, <code>ImageConvolve</code>, <code>ImageCorrelate</code>,
 * <code>DerivativeFilter</code>, <code>GradientFilter</code>,
 * <li>of the block around a pixel: <code>MeanFilter</code>, <code>GeometricMeanFilter</code>,
 * <code>MedianFilter</code>,
 * <code>MinFilter</code>, <code>MaxFilter</code>,
 * <li>morphological: <code>Dilation</code>, <code>Erosion</code>, <code>Opening</code>,
 * <code>Closing</code>, <code>MorphologicalComponents</code>, <code>MorphologicalGraph</code>,
 * <code>WatershedComponents</code>,
 * <code>DistanceTransform</code>.
 * </ul>
 *
 * <p>
 * Every filter works on the real samples of <code>ImageData(image)</code>, channel by channel, and
 * keeps the filtered samples exactly; nothing is rounded to the 8 bit bitmap an image is shown
 * with. The linear filters return a <code>"Real32"</code> image and continue an image beyond its
 * border with the pixels of the border (<code>"Fixed"</code> padding), while the block and the
 * morphological filters return an image of the type they were given and use the part of the block
 * which lies inside of the image.
 *
 * <p>
 * The filters which core defines for lists keep doing that: the evaluators installed here take an
 * image and hand everything else on to the evaluator of core.
 */
public class ImageFilterFunctions {

  private static class Initializer {

    private static void init() {
      install(S.GaussianFilter, new GaussianFilter());
      install(S.LaplacianFilter, new LaplacianFilter());
      install(S.MeanFilter, new MeanFilter());
      install(S.GeometricMeanFilter, new GeometricMeanFilter());
      install(S.MedianFilter, new RankFilter(RankFilter.MEDIAN));
      install(S.MinFilter, new RankFilter(RankFilter.MIN));
      install(S.MaxFilter, new RankFilter(RankFilter.MAX));
      install(S.CommonestFilter, new CommonestFilter());
      install(S.TotalVariationFilter, new TotalVariationFilter());
      S.ImageConvolve.setEvaluator(new ImageConvolve(true));
      S.ImageCorrelate.setEvaluator(new ImageConvolve(false));
      S.DerivativeFilter.setEvaluator(new DerivativeFilter());
      S.GradientFilter.setEvaluator(new GradientFilter());
      S.Dilation.setEvaluator(new Morphology(true, false));
      S.Erosion.setEvaluator(new Morphology(false, false));
      S.Opening.setEvaluator(new Morphology(false, true));
      S.Closing.setEvaluator(new Morphology(true, true));
      S.CrossingDetect.setEvaluator(new CrossingDetect());
      S.MorphologicalComponents.setEvaluator(new MorphologicalComponents());
      S.MorphologicalEulerNumber.setEvaluator(new MorphologicalEulerNumber());
      S.Threshold.setEvaluator(new Threshold());
      S.ImagePeriodogram.setEvaluator(new ImagePeriodogram());
      S.WatershedComponents.setEvaluator(new WatershedComponents());
      S.MorphologicalGraph.setEvaluator(new MorphologicalGraph());
      S.ComponentMeasurements.setEvaluator(new ComponentMeasurements());
      S.DeleteSmallComponents.setEvaluator(new DeleteSmallComponents());
      S.MorphologicalBinarize.setEvaluator(new MorphologicalBinarize());
      S.DistanceTransform.setEvaluator(new DistanceTransform());
      S.Inpaint.setEvaluator(new Inpaint());
      S.ImageEffect.setEvaluator(new ImageEffect());
    }

    private static void install(IBuiltInSymbol symbol, ImageFilter filter) {
      filter.listEvaluator = symbol.getEvaluator();
      symbol.setEvaluator(filter);
    }
  }

  /**
   * The samples of an image as <code>[channel][row][column]</code>, on the intensity scale and not
   * clipped; see {@link ImageExpr#raster()}.
   *
   * @return <code>null</code> if the image has no samples
   */
  public static double[][][] channels(ImageExpr image, EvalEngine engine) {
    Raster raster = image.raster();
    return raster == null ? null : raster.toChannels();
  }

  /**
   * The samples of a matrix of numbers, or of pixels which are lists of numbers, as
   * <code>[channel][row][column]</code>.
   *
   * @return <code>null</code> if <code>data</code> is no array of real numbers
   */
  static double[][][] channels(IExpr data) {
    if (!data.isList() || data.argSize() == 0 || !data.first().isList()) {
      return null;
    }
    IAST rows = (IAST) data;
    final int height = rows.argSize();
    final int width = rows.arg1().argSize();
    if (width == 0) {
      return null;
    }
    final int channels = rows.arg1().first().isList() ? rows.arg1().first().argSize() : 1;
    double[][][] result = new double[channels][height][width];
    for (int y = 0; y < height; y++) {
      if (!rows.get(y + 1).isList() || rows.get(y + 1).argSize() != width) {
        return null;
      }
      IAST row = (IAST) rows.get(y + 1);
      for (int x = 0; x < width; x++) {
        IExpr pixel = row.get(x + 1);
        if (pixel.isList()) {
          if (pixel.argSize() != channels) {
            return null;
          }
          for (int c = 0; c < channels; c++) {
            result[c][y][x] = ((IAST) pixel).get(c + 1).evalfNaN();
          }
        } else {
          if (channels != 1) {
            return null;
          }
          result[0][y][x] = pixel.evalfNaN();
        }
      }
    }
    for (double[][] channel : result) {
      for (double[] row : channel) {
        for (double sample : row) {
          if (Double.isNaN(sample)) {
            return null;
          }
        }
      }
    }
    return result;
  }

  /** A <code>"Real32"</code> image of the samples <code>[channel][row][column]</code>. */
  public static IExpr toImage(double[][][] channels, ImageOptions options) {
    return toImage(channels, options, "Real32");
  }

  /**
   * An image of the samples <code>[channel][row][column]</code>, which are on the intensity scale
   * of <code>ImageData</code>. A <code>"Byte"</code>, <code>"Bit16"</code> or <code>"Bit"</code>
   * image stores them as the integers of its type, clipped to its range; every other one is a
   * <code>"Real32"</code> image, which keeps them as they are, unless <code>"Real64"</code> is asked
   * for.
   */
  static IExpr toImage(double[][][] channels, ImageOptions options, String type) {
    Raster raster = Raster.fromChannels(channels);
    if (raster == null) {
      return F.NIL;
    }
    String stored = Raster.isIntegerType(type) || Pixels.REAL64.equals(type) ? type : Pixels.REAL32;
    ImageExpr image = ImageExpr.fromRaster(raster, options, stored);
    return image == null ? F.NIL : image;
  }

  /** A filter which takes an image, and leaves a list to the evaluator of core. */
  private abstract static class ImageFilter extends AbstractEvaluator {
    IFunctionEvaluator listEvaluator;

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.argSize() >= 1 && ast.arg1() instanceof ImageExpr) {
        ImageExpr image = (ImageExpr) ast.arg1();
        double[][][] channels = channels(image, engine);
        if (channels == null) {
          return F.NIL;
        }
        for (int c = 0; c < channels.length; c++) {
          channels[c] = filter(channels[c], ast, engine);
          if (channels[c] == null) {
            return F.NIL;
          }
        }
        return toImage(channels, image.getOptions(), keepsType() ? image.sampleType() : "Real32");
      }
      if (ast.argSize() >= 1 && ast.arg1() instanceof Image3DExpr) {
        // a 3D image: the same filter with one more direction
        Image3DExpr volume = (Image3DExpr) ast.arg1();
        double[][][][] samples = volume.samples();
        for (int c = 0; c < samples.length; c++) {
          samples[c] = filter3D(samples[c], ast, engine);
          if (samples[c] == null) {
            return F.NIL;
          }
        }
        return Image3DExpr.of(samples, keepsType() ? volume.sampleType() : "Real32",
            volume.getOptions());
      }
      return listEvaluator == null ? F.NIL : listEvaluator.evaluate(ast, engine);
    }

    /**
     * Filter one channel <code>[slice][row][column]</code> of a 3D image.
     *
     * @return <code>null</code> if the filter has no 3D form, or its arguments aren't valid
     */
    double[][][] filter3D(double[][][] volume, IAST ast, EvalEngine engine) {
      return null;
    }

    /**
     * Filter one channel.
     *
     * @return <code>null</code> if the arguments behind the image aren't valid
     */
    abstract double[][] filter(double[][] channel, IAST ast, EvalEngine engine);

    /** Whether the result has the type of the image; a linear filter returns "Real32". */
    boolean keepsType() {
      return false;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   * <code>GaussianFilter(image, r)</code> and <code>GaussianFilter(image, {r, sigma})</code>: the
   * discrete Gaussian kernel of {@link FilterFunctions#gaussianKernel}, with the standard deviation
   * <code>r/2</code> if none is given.
   */
  private static class GaussianFilter extends ImageFilter {

    @Override
    double[][] filter(double[][] channel, IAST ast, EvalEngine engine) {
      IExpr spec = ast.arg2();
      int radius = spec.isList2() ? spec.first().toIntDefault() : spec.toIntDefault();
      double sigma = spec.isList2() ? spec.second().evalfNaN() : radius / 2.0;
      if (radius < 0 || Double.isNaN(sigma) || sigma < 0.0) {
        return null;
      }
      return FilterFunctions.separableFilter(channel,
          FilterFunctions.gaussianKernel(radius, sigma, engine));
    }

    @Override
    double[][][] filter3D(double[][][] volume, IAST ast, EvalEngine engine) {
      IExpr spec = ast.arg2();
      int radius = spec.isList2() ? spec.first().toIntDefault() : spec.toIntDefault();
      double sigma = spec.isList2() ? spec.second().evalfNaN() : radius / 2.0;
      if (radius < 0 || Double.isNaN(sigma) || sigma < 0.0) {
        return null;
      }
      return Volumes.separable(volume, FilterFunctions.gaussianKernel(radius, sigma, engine));
    }
  }

  /**
   * <code>LaplacianFilter(image, r)</code>: the Laplacian kernel of
   * {@link FilterFunctions#laplacianFilter}, applied to each channel.
   */
  private static class LaplacianFilter extends ImageFilter {

    @Override
    double[][] filter(double[][] channel, IAST ast, EvalEngine engine) {
      int radius = FilterFunctions.laplacianRadius(ast.arg2());
      if (radius < 0) {
        return null;
      }
      return FilterFunctions.laplacianFilter(channel, radius, false);
    }
  }

  /**
   * <code>ImageEffect(image, effect)</code> and <code>ImageEffect(image, {effect, params...})</code>
   * - a <code>"Real32"</code> image of the effect:
   * <ul>
   * <li><code>{"Solarization", t}</code>: the pixels whose mean over the channels is above
   * <code>t</code> (<code>0.5</code> if none is given) are inverted, every channel <code>v</code>
   * becomes <code>1 - v</code>,
   * <li><code>{"OilPainting", r}</code>: each pixel takes the most frequent intensity of the
   * <code>(2r+1) x (2r+1)</code> block around it (on 256 levels, of the part of the block inside
   * the image), as the mean of the pixels of that intensity; a tie keeps the pixel's own intensity
   * when it is one of the most frequent. Without a radius it is <code>Floor(Min(width,
   * height)/64)</code>.
   * <li><code>{"MotionBlur", r, a}</code>: each pixel is the mean of the <code>2r+1</code> samples
   * one pixel apart on the line through it in the direction of <code>a</code> degrees from the
   * horizontal (counterclockwise); the samples are interpolated bilinearly and positions outside of
   * the image take the nearest border pixel. The defaults are <code>r = 5</code> and <code>a =
   * 0</code>.
   * </ul>
   */
  private static class ImageEffect extends AbstractEvaluator {

    /** The other effects of the reference implementation, which stay unevaluated without a message. */
    private static final java.util.Set<String> OTHER_EFFECTS = new java.util.HashSet<String>(
        Arrays.asList("Charcoal", "Comics", "Embossing", "Noise", "Posterization"));

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!(ast.arg1() instanceof ImageExpr)) {
        return F.NIL;
      }
      IExpr spec = ast.arg2();
      IExpr name = spec.isList() && spec.argSize() >= 1 ? spec.first() : spec;
      if (!name.isString()) {
        return F.NIL;
      }
      ImageExpr image = (ImageExpr) ast.arg1();
      double[][][] channels = channels(image, engine);
      if (channels == null) {
        return F.NIL;
      }
      double parameter = spec.isList() && spec.argSize() >= 2 ? spec.second().evalfNaN() : Double.NaN;
      switch (name.toString()) {
        case "Solarization":
          solarize(channels, Double.isNaN(parameter) ? 0.5 : parameter);
          break;
        case "OilPainting": {
          int radius = Double.isNaN(parameter)
              ? Math.min(channels[0].length, channels[0][0].length) / 64
              : (int) parameter;
          if (radius < 0) {
            return F.NIL;
          }
          channels = oilPainting(channels, radius);
          break;
        }
        case "MotionBlur": {
          double angle = spec.isList() && spec.argSize() >= 3 ? spec.getAt(3).evalfNaN() : 0.0;
          double radius = Double.isNaN(parameter) ? 5.0 : Math.rint(parameter);
          if (!(radius >= 0.0) || Double.isNaN(angle) || Double.isInfinite(angle)
              || radius > Integer.MAX_VALUE / 4) {
            return F.NIL;
          }
          channels = motionBlur(channels, (int) radius, Math.toRadians(angle));
          break;
        }
        default:
          if (OTHER_EFFECTS.contains(name.toString())) {
            // an effect of the reference implementation which is not drawn here
            return F.NIL;
          }
          // `1` is not a known image effect.
          return Errors.printMessage(S.ImageEffect, "effect", F.list(name), engine);
      }
      return toImage(channels, image.getOptions());
    }

    private static void solarize(double[][][] channels, double threshold) {
      final int height = channels[0].length;
      final int width = channels[0][0].length;
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          double mean = 0.0;
          for (double[][] channel : channels) {
            mean += channel[y][x];
          }
          if (mean / channels.length > threshold) {
            for (double[][] channel : channels) {
              channel[y][x] = 1.0 - channel[y][x];
            }
          }
        }
      }
    }

    private static double[][][] motionBlur(double[][][] channels, int radius, double angle) {
      final int height = channels[0].length;
      final int width = channels[0][0].length;
      // a step along the line; the rows count downwards, so a positive angle steps up
      // (rounded, so that a multiple of 90 degrees stays on the pixel grid)
      final double stepX = Math.rint(Math.cos(angle) * 1.0e12) / 1.0e12;
      final double stepY = -Math.rint(Math.sin(angle) * 1.0e12) / 1.0e12;
      final int taps = 2 * radius + 1;
      double[][][] result = new double[channels.length][height][width];
      for (int c = 0; c < channels.length; c++) {
        final double[][] channel = channels[c];
        for (int y = 0; y < height; y++) {
          for (int x = 0; x < width; x++) {
            double sum = 0.0;
            for (int t = -radius; t <= radius; t++) {
              sum += bilinear(channel, x + t * stepX, y + t * stepY, width, height);
            }
            result[c][y][x] = sum / taps;
          }
        }
      }
      return result;
    }

    /** The bilinear interpolation at a position, clamped to the pixels of the image. */
    private static double bilinear(double[][] channel, double x, double y, int width,
        int height) {
      x = Math.max(0.0, Math.min(width - 1, x));
      y = Math.max(0.0, Math.min(height - 1, y));
      final int x0 = (int) Math.floor(x);
      final int y0 = (int) Math.floor(y);
      final int x1 = Math.min(x0 + 1, width - 1);
      final int y1 = Math.min(y0 + 1, height - 1);
      final double tx = x - x0;
      final double ty = y - y0;
      double top = channel[y0][x0] * (1.0 - tx) + channel[y0][x1] * tx;
      double bottom = channel[y1][x0] * (1.0 - tx) + channel[y1][x1] * tx;
      return top * (1.0 - ty) + bottom * ty;
    }

    private static double[][][] oilPainting(double[][][] channels, int radius) {
      final int count = channels.length;
      final int height = channels[0].length;
      final int width = channels[0][0].length;
      int[][] level = new int[height][width];
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          double mean = 0.0;
          for (double[][] channel : channels) {
            mean += channel[y][x];
          }
          level[y][x] = (int) Math.round(Math.max(0.0, Math.min(1.0, mean / count)) * 255.0);
        }
      }
      double[][][] result = new double[count][height][width];
      int[] frequency = new int[256];
      double[][] sums = new double[256][count];
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          Arrays.fill(frequency, 0);
          for (double[] sum : sums) {
            Arrays.fill(sum, 0.0);
          }
          for (int yy = Math.max(0, y - radius); yy <= Math.min(height - 1, y + radius); yy++) {
            for (int xx = Math.max(0, x - radius); xx <= Math.min(width - 1, x + radius); xx++) {
              int l = level[yy][xx];
              frequency[l]++;
              for (int c = 0; c < count; c++) {
                sums[l][c] += channels[c][yy][xx];
              }
            }
          }
          int best = level[y][x];
          for (int l = 0; l < 256; l++) {
            if (frequency[l] > frequency[best]) {
              best = l;
            }
          }
          for (int c = 0; c < count; c++) {
            result[c][y][x] = sums[best][c] / frequency[best];
          }
        }
      }
      return result;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   * <code>MeanFilter(image, r)</code>: the mean of the <code>(2r+1) x (2r+1)</code> block, of the
   * part of it which lies inside of the image.
   */
  private static class MeanFilter extends ImageFilter {

    @Override
    double[][] filter(double[][] channel, IAST ast, EvalEngine engine) {
      int radius = ast.arg2().toIntDefault();
      if (radius < 0) {
        return null;
      }
      final int height = channel.length;
      final int width = channel[0].length;
      double[][] result = new double[height][width];
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          double sum = 0.0;
          int count = 0;
          for (int row = Math.max(0, y - radius); row <= Math.min(height - 1, y + radius); row++) {
            for (int column = Math.max(0, x - radius); column <= Math.min(width - 1,
                x + radius); column++) {
              sum += channel[row][column];
              count++;
            }
          }
          result[y][x] = sum / count;
        }
      }
      return result;
    }

    @Override
    double[][][] filter3D(double[][][] volume, IAST ast, EvalEngine engine) {
      int radius = ast.arg2().toIntDefault();
      return radius < 0 ? null : Volumes.block(volume, radius, Volumes.MEAN);
    }

    @Override
    boolean keepsType() {
      return true;
    }
  }

  /**
   * <code>GeometricMeanFilter(image, r)</code>: the geometric mean of the
   * <code>(2r+1) x (2r+1)</code> block, of the part of it which lies inside of the image. A block
   * with a sample of 0 has the mean 0.
   */
  private static class GeometricMeanFilter extends ImageFilter {

    @Override
    double[][] filter(double[][] channel, IAST ast, EvalEngine engine) {
      int radius = ast.arg2().toIntDefault();
      if (radius < 0) {
        return null;
      }
      final int height = channel.length;
      final int width = channel[0].length;
      double[][] result = new double[height][width];
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          double logSum = 0.0;
          int count = 0;
          for (int row = Math.max(0, y - radius); row <= Math.min(height - 1, y + radius); row++) {
            for (int column = Math.max(0, x - radius); column <= Math.min(width - 1,
                x + radius); column++) {
              logSum += Math.log(channel[row][column]);
              count++;
            }
          }
          // a sample of 0 makes the sum -Infinity and the mean 0
          result[y][x] = Math.exp(logSum / count);
        }
      }
      return result;
    }

    @Override
    boolean keepsType() {
      return true;
    }
  }

  /**
   * <code>MedianFilter(image, r)</code>, <code>MinFilter(image, r)</code> and
   * <code>MaxFilter(image, r)</code>: the median, the smallest or the largest sample of the part of
   * the <code>(2r+1) x (2r+1)</code> block which lies inside of the image. The median of an even
   * number of samples is the upper one of the two in the middle - a sample of the image, not a mean
   * of two.
   */
  private static class RankFilter extends ImageFilter {
    static final int MIN = 0;
    static final int MEDIAN = 1;
    static final int MAX = 2;

    private final int rank;

    RankFilter(int rank) {
      this.rank = rank;
    }

    @Override
    double[][] filter(double[][] channel, IAST ast, EvalEngine engine) {
      int radius = ast.arg2().toIntDefault();
      if (radius < 0) {
        return null;
      }
      final int height = channel.length;
      final int width = channel[0].length;
      final int side = 2 * radius + 1;
      double[] block = new double[side * side];
      double[][] result = new double[height][width];
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          int count = 0;
          for (int row = Math.max(0, y - radius); row <= Math.min(height - 1, y + radius); row++) {
            for (int column = Math.max(0, x - radius); column <= Math.min(width - 1,
                x + radius); column++) {
              block[count++] = channel[row][column];
            }
          }
          Arrays.sort(block, 0, count);
          result[y][x] = rank == MIN ? block[0] : rank == MAX ? block[count - 1] : block[count / 2];
        }
      }
      return result;
    }

    @Override
    double[][][] filter3D(double[][][] volume, IAST ast, EvalEngine engine) {
      int radius = ast.arg2().toIntDefault();
      return radius < 0 ? null
          : Volumes.block(volume, radius,
              rank == MIN ? Volumes.MIN : rank == MAX ? Volumes.MAX : Volumes.MEDIAN);
    }

    @Override
    boolean keepsType() {
      return true;
    }
  }

  /**
   * <code>CommonestFilter(image, r)</code>: the commonest sample of the part of the
   * <code>(2r+1) x (2r+1)</code> block which lies inside of the image, channel by channel. Of
   * several equally common samples the one of the pixel itself is taken if it is among them, else
   * the first one row by row. The range rounds up and its sign doesn't matter.
   */
  private static class CommonestFilter extends ImageFilter {

    @Override
    double[][] filter(double[][] channel, IAST ast, EvalEngine engine) {
      double range = ast.arg2().evalfNaN();
      if (Double.isNaN(range)) {
        return null;
      }
      final int radius = (int) Math.ceil(Math.abs(range));
      final boolean single = Pixels.REAL32.equals(((ImageExpr) ast.arg1()).sampleType());
      final int height = channel.length;
      final int width = channel[0].length;
      double[][] result = new double[height][width];
      for (int y = 0; y < height; y++) {
        engine.checkTimeLimit();
        for (int x = 0; x < width; x++) {
          double commonest = commonest(channel, x, y, radius);
          result[y][x] = single ? (float) commonest : commonest;
        }
      }
      return result;
    }

    private static double commonest(double[][] channel, int x, int y, int radius) {
      int lastRow = Math.min(channel.length - 1, y + radius);
      int lastColumn = Math.min(channel[0].length - 1, x + radius);
      java.util.Map<Double, int[]> counts = new java.util.LinkedHashMap<Double, int[]>();
      int most = 0;
      for (int row = Math.max(0, y - radius); row <= lastRow; row++) {
        for (int column = Math.max(0, x - radius); column <= lastColumn; column++) {
          int[] count = counts.computeIfAbsent(channel[row][column], key -> new int[1]);
          most = Math.max(most, ++count[0]);
        }
      }
      if (counts.get(channel[y][x])[0] == most) {
        return channel[y][x];
      }
      for (java.util.Map.Entry<Double, int[]> entry : counts.entrySet()) {
        if (entry.getValue()[0] == most) {
          return entry.getKey();
        }
      }
      return channel[y][x];
    }

    @Override
    boolean keepsType() {
      return true;
    }
  }

  /**
   * <code>ImageConvolve(image, kernel)</code> and <code>ImageCorrelate(image, kernel)</code>. The
   * convolution reflects the kernel, the correlation doesn't. The correlation centres a kernel of
   * <code>k</code> elements on its element <code>Floor(k/2)</code> (counted from 0), and the
   * convolution, being the correlation with the reflected kernel, on the element
   * <code>Floor((k-1)/2)</code>.
   */
  private static class ImageConvolve extends ImageFilter {
    private final boolean convolve;

    ImageConvolve(boolean convolve) {
      this.convolve = convolve;
    }

    @Override
    double[][] filter(double[][] channel, IAST ast, EvalEngine engine) {
      IExpr arg2 = ast.arg2();
      if (!arg2.isList() || arg2.argSize() == 0) {
        return null;
      }
      // a vector is a kernel of one row
      double[][] kernel =
          arg2.first().isList() ? arg2.toDoubleMatrix() : new double[][] {arg2.toDoubleVector()};
      if (kernel == null || kernel.length == 0 || kernel[0] == null || kernel[0].length == 0) {
        return null;
      }
      // Padding -> "Fixed" (the default), "Periodic", "Reflected", "Reversed" or a constant
      String continuation = "Fixed";
      double constant = Double.NaN;
      if (ast.argSize() == 3) {
        if (!ast.arg3().isRuleAST() || ast.arg3().first() != S.Padding) {
          return null;
        }
        IExpr padding = ast.arg3().second();
        if (padding.isString()) {
          continuation = padding.toString();
          if (!ImageGeometryFunctions.CONTINUATIONS.contains(continuation)) {
            return null;
          }
        } else if (padding != S.Automatic) {
          constant = padding.evalfNaN();
          if (Double.isNaN(constant)) {
            return null;
          }
        }
      }
      final int kernelRows = kernel.length;
      final int kernelColumns = kernel[0].length;
      final int centerRow = convolve ? (kernelRows - 1) / 2 : kernelRows / 2;
      final int centerColumn = convolve ? (kernelColumns - 1) / 2 : kernelColumns / 2;
      final int height = channel.length;
      final int width = channel[0].length;
      double[][] result = new double[height][width];
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          double sum = 0.0;
          for (int a = 0; a < kernelRows; a++) {
            int row = convolve ? y + centerRow - a : y + a - centerRow;
            boolean rowInside = row >= 0 && row < height;
            row = ImageGeometryFunctions.continued(row, height, continuation);
            for (int b = 0; b < kernelColumns; b++) {
              int column = convolve ? x + centerColumn - b : x + b - centerColumn;
              double sample;
              if (!Double.isNaN(constant) && (!rowInside || column < 0 || column >= width)) {
                sample = constant;
              } else {
                int inside = ImageGeometryFunctions.continued(column, width, continuation);
                sample = channel[row][inside];
              }
              sum += kernel[a][b] * sample;
            }
          }
          result[y][x] = sum;
        }
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }
  }

  /**
   * <code>Dilation(image, r)</code>, <code>Erosion(image, r)</code>, <code>Opening(image, r)</code>
   * and <code>Closing(image, r)</code> with the <code>(2r+1) x (2r+1)</code> box, or with a matrix
   * whose non-zero elements are the structuring element.
   *
   * <p>
   * The dilation is the largest and the erosion the smallest sample under the element, where the
   * element <code>ker[a][b]</code> lies on the pixel <code>(y+a-cy, x+b-cx)</code>:
   * <code>Dilation({{0,0,1,0,0}}, {{1,1,0}})</code> is <code>{{0,0,1,1,0}}</code>. The opening is
   * the dilation of the erosion and the closing the erosion of the dilation.
   */
  private static class Morphology extends ImageFilter {
    private final boolean dilateFirst;
    private final boolean both;

    /**
     * @param dilateFirst the first (or only) operation is a dilation
     * @param both the other operation follows
     */
    Morphology(boolean dilateFirst, boolean both) {
      this.dilateFirst = dilateFirst;
      this.both = both;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.arg1().isList()) {
        return ast.isAST2() ? super.evaluate(ast, engine) : F.NIL;
      }
      double padding = Double.NaN;
      if (ast.isAST3()) {
        final OptionArgs options = new OptionArgs(ast.topHead(), ast, 3, engine);
        if (options.isInvalidPosition(2)) {
          return options.printNonopt(ast, 2, engine);
        }
        padding = options.getOption(S.Padding).evalfNaN();
        if (Double.isNaN(padding)) {
          return F.NIL;
        }
      }
      return evaluateArray(ast, padding);
    }

    /** The filter of a vector or a matrix of real numbers, the integers stay integers. */
    private IExpr evaluateArray(IAST ast, double padding) {
      IExpr array = ast.arg1();
      boolean vector = array.isVector() >= 0;
      double[][] channel =
          vector ? new double[][] {array.toDoubleVector()} : array.toDoubleMatrix();
      boolean[][] element = element(ast.arg2());
      if (!ast.arg2().isList() && vector) {
        // the radius of a vector has one row
        element = element == null ? null : new boolean[][] {element[0]};
      }
      if (channel == null || channel.length == 0 || channel[0] == null || channel[0].length == 0
          || element == null) {
        return F.NIL;
      }
      double[][] result = apply(channel, element, dilateFirst, padding);
      if (both) {
        result = apply(result, element, !dilateFirst, padding);
      }
      boolean exact = array.isList() && array.isFree(x -> x.isNumber() && !x.isInteger(), true)
          && (Double.isNaN(padding) || padding == Math.rint(padding));
      final double[][] values = result;
      IAST rows = F.mapRange(0, values.length, i -> F.mapRange(0, values[i].length,
          j -> exact ? F.ZZ(Math.round(values[i][j])) : F.num(values[i][j])));
      return vector ? rows.arg1() : rows;
    }

    @Override
    double[][] filter(double[][] channel, IAST ast, EvalEngine engine) {
      boolean[][] element = element(ast.arg2());
      if (element == null) {
        return null;
      }
      double[][] result = apply(channel, element, dilateFirst, Double.NaN);
      return both ? apply(result, element, !dilateFirst, Double.NaN) : result;
    }

    @Override
    double[][][] filter3D(double[][][] volume, IAST ast, EvalEngine engine) {
      IExpr spec = ast.arg2();
      if (!spec.isList()) {
        // the cube of 2r+1 voxels
        int radius = spec.toIntDefault();
        if (radius < 0) {
          return null;
        }
        double[][][] result =
            Volumes.block(volume, radius, dilateFirst ? Volumes.MAX : Volumes.MIN);
        return both ? Volumes.block(result, radius, dilateFirst ? Volumes.MIN : Volumes.MAX)
            : result;
      }
      boolean[][][] element = element3D((IAST) spec);
      if (element == null) {
        return null;
      }
      double[][][] result = Volumes.extreme(volume, element, dilateFirst);
      return both ? Volumes.extreme(result, element, !dilateFirst) : result;
    }

    /**
     * The structuring element of an array of rank 3; a matrix is an element of one slice and a
     * vector one of one row.
     */
    private static boolean[][][] element3D(IAST spec) {
      IntArrayList dimensions = LinearAlgebraUtil.dimensions(spec);
      if (dimensions == null || dimensions.size() < 1 || dimensions.size() > 3) {
        return null;
      }
      IExpr array = spec;
      for (int rank = dimensions.size(); rank < 3; rank++) {
        array = F.List(array);
      }
      IAST slices = (IAST) array;
      final int depth = slices.argSize();
      final int height = slices.arg1().argSize();
      final int width = slices.arg1().first().argSize();
      if (depth == 0 || height == 0 || width == 0) {
        return null;
      }
      boolean[][][] element = new boolean[depth][height][width];
      for (int a = 0; a < depth; a++) {
        for (int b = 0; b < height; b++) {
          for (int c = 0; c < width; c++) {
            double value = ((IAST) ((IAST) slices.get(a + 1)).get(b + 1)).get(c + 1).evalfNaN();
            if (Double.isNaN(value)) {
              return null;
            }
            element[a][b][c] = value != 0.0;
          }
        }
      }
      return element;
    }

    private static boolean[][] element(IExpr spec) {
      if (spec.isList()) {
        double[][] matrix = spec.argSize() > 0 && spec.first().isList() ? spec.toDoubleMatrix()
            : new double[][] {spec.toDoubleVector()};
        if (matrix == null || matrix.length == 0 || matrix[0] == null || matrix[0].length == 0) {
          return null;
        }
        boolean[][] element = new boolean[matrix.length][matrix[0].length];
        for (int a = 0; a < matrix.length; a++) {
          for (int b = 0; b < matrix[0].length; b++) {
            element[a][b] = matrix[a][b] != 0.0;
          }
        }
        return element;
      }
      int radius = spec.toIntDefault();
      if (radius < 0) {
        return null;
      }
      boolean[][] element = new boolean[2 * radius + 1][2 * radius + 1];
      for (boolean[] row : element) {
        Arrays.fill(row, true);
      }
      return element;
    }

    /**
     * @param padding the value of the elements outside of the channel, <code>NaN</code> if the
     *        structuring element is cut off there
     */
    private static double[][] apply(double[][] channel, boolean[][] element, boolean dilate,
        double padding) {
      final int height = channel.length;
      final int width = channel[0].length;
      final int centerRow = element.length / 2;
      final int centerColumn = element[0].length / 2;
      double[][] result = new double[height][width];
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          double value = dilate ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
          for (int a = 0; a < element.length; a++) {
            int row = y + a - centerRow;
            for (int b = 0; b < element[0].length; b++) {
              int column = x + b - centerColumn;
              if (element[a][b]) {
                boolean inside = row >= 0 && row < height && column >= 0 && column < width;
                double sample = inside ? channel[row][column] : padding;
                if (!Double.isNaN(sample)) {
                  value = dilate ? Math.max(value, sample) : Math.min(value, sample);
                }
              }
            }
          }
          // no element of the structuring element lies on the image
          result[y][x] = Double.isInfinite(value) ? channel[y][x] : value;
        }
      }
      return result;
    }

    @Override
    boolean keepsType() {
      return true;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }

    @Override
    public void setUp(final org.matheclipse.core.interfaces.ISymbol newSymbol) {
      setOptions(newSymbol, F.list(F.Rule(S.Padding, S.Automatic)));
    }
  }

  /**
   * <code>CrossingDetect(array)</code> and <code>CrossingDetect(image)</code> mark the positive
   * elements with a negative neighbor, as a <code>SparseArray</code> or a <code>"Bit"</code> image.
   * <code>CrossingDetect(data, delta)</code> counts the magnitudes below <code>delta</code> as
   * zero, and <code>CornerNeighbors-&gt;False</code> or <code>None</code> leaves out the diagonal
   * neighbors.
   */
  private static class CrossingDetect extends AbstractEvaluator {

    private static boolean[][] crossings(double[][] grid, double delta, boolean corners) {
      final int height = grid.length;
      final int width = grid[0].length;
      boolean[][] result = new boolean[height][width];
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          if (grid[y][x] > 0.0 && grid[y][x] >= delta) {
            result[y][x] = hasNegativeNeighbor(grid, x, y, delta, corners);
          }
        }
      }
      return result;
    }

    private static boolean hasNegativeNeighbor(double[][] grid, int x, int y, double delta,
        boolean corners) {
      for (int row = Math.max(0, y - 1); row <= Math.min(grid.length - 1, y + 1); row++) {
        for (int column = Math.max(0, x - 1); column <= Math.min(grid[0].length - 1, x + 1);
            column++) {
          boolean diagonal = row != y && column != x;
          if ((corners || !diagonal) && grid[row][column] < 0.0 && -grid[row][column] >= delta) {
            return true;
          }
        }
      }
      return false;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int argSize = ast.argSize();
      boolean corners = true;
      if (ast.last().isRuleAST()) {
        IExpr rule = ast.last();
        if (rule.first() != S.CornerNeighbors) {
          return F.NIL;
        }
        corners = rule.second().isTrue() || rule.second() == S.Automatic;
        if (!corners && !rule.second().isFalse() && rule.second() != S.None) {
          return F.NIL;
        }
        argSize--;
      }
      double delta = argSize == 2 ? ast.arg2().evalfNaN() : 0.0;
      if (argSize < 1 || argSize > 2 || !(delta >= 0.0)) {
        return F.NIL;
      }
      IExpr data = ast.arg1();
      if (data instanceof ImageExpr) {
        Raster raster = ((ImageExpr) data).raster();
        if (raster == null || raster.channels != 1) {
          return F.NIL;
        }
        boolean[][] marks = crossings(raster.toChannels()[0], delta, corners);
        Raster bits = new Raster(raster.width, raster.height, 1);
        for (int y = 0; y < raster.height; y++) {
          for (int x = 0; x < raster.width; x++) {
            bits.set(x, y, 0, marks[y][x] ? 1.0 : 0.0);
          }
        }
        ImageExpr image = ImageExpr.fromRaster(bits, ImageOptions.DEFAULT, Pixels.BIT);
        return image == null ? F.NIL : image;
      }
      IExpr array = data.isList() ? data : data.normal(false);
      boolean vector = array.isVector() >= 0;
      double[][] grid = !array.isList() ? null
          : vector ? new double[][] {array.toDoubleVector()} : array.toDoubleMatrix();
      if (grid == null || grid.length == 0 || grid[0] == null || grid[0].length == 0) {
        return F.NIL;
      }
      boolean[][] marks = crossings(grid, delta, corners);
      IAST rows = F.mapRange(0, marks.length, i -> F.mapRange(0, marks[i].length,
          j -> marks[i][j] ? F.C1 : F.C0));
      return F.unaryAST1(S.SparseArray, vector ? rows.arg1() : rows);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  /**
   * <code>DerivativeFilter(image, {n1, n2})</code> and
   * <code>DerivativeFilter(image, {n1, n2}, sigma)</code>: the derivative of order <code>n1</code>
   * along the rows' direction (down) and <code>n2</code> along the columns' direction (right).
   *
   * <p>
   * The derivative is the one of the cubic spline which interpolates the samples - of the image
   * continued with the pixels of its border - and <code>sigma</code> convolves that spline with a
   * Gaussian of the standard deviation <code>sigma</code>. With the B-spline coefficients
   * <code>c(j)</code> of the samples the value at the pixel <code>x</code> is
   * <code>Sum(c(j) * h(x-j))</code>, where <code>h</code> is the <code>n</code>-th derivative of
   * the cubic B-spline convolved with the Gaussian. Without <code>sigma</code> the first derivative
   * is <code>(c(x+1)-c(x-1))/2</code>.
   */
  private static class DerivativeFilter extends ImageFilter {

    @Override
    double[][] filter(double[][] channel, IAST ast, EvalEngine engine) {
      if (!ast.arg2().isList2()) {
        return null;
      }
      int rowOrder = ast.arg2().first().toIntDefault();
      int columnOrder = ast.arg2().second().toIntDefault();
      double sigma = ast.isAST3() ? ast.arg3().evalfNaN() : 0.0;
      if (rowOrder < 0 || rowOrder > 2 || columnOrder < 0 || columnOrder > 2 || Double.isNaN(sigma)
          || sigma < 0.0) {
        return null;
      }
      final int height = channel.length;
      final int width = channel[0].length;
      double[][] result = new double[height][width];
      // along the columns' direction: every row is a line
      double[] rowKernel = splineKernel(columnOrder, sigma);
      for (int y = 0; y < height; y++) {
        result[y] = rowKernel == null ? channel[y].clone() : splineFilter(channel[y], rowKernel);
      }
      // along the rows' direction: every column is a line
      double[] columnKernel = splineKernel(rowOrder, sigma);
      if (columnKernel != null) {
        double[] line = new double[height];
        for (int x = 0; x < width; x++) {
          for (int y = 0; y < height; y++) {
            line[y] = result[y][x];
          }
          double[] filtered = splineFilter(line, columnKernel);
          for (int y = 0; y < height; y++) {
            result[y][x] = filtered[y];
          }
        }
      }
      return result;
    }

    /** The <code>order</code>-th derivative of the cubic B-spline. */
    private static double bSpline(int order, double u) {
      double a = Math.abs(u);
      if (a >= 2.0) {
        return 0.0;
      }
      double sign = u < 0 ? -1.0 : 1.0;
      switch (order) {
        case 0:
          return a < 1.0 ? 2.0 / 3.0 - a * a + a * a * a / 2.0
              : (2.0 - a) * (2.0 - a) * (2.0 - a) / 6.0;
        case 1:
          return a < 1.0 ? sign * (1.5 * a * a - 2.0 * a) : -sign * 0.5 * (2.0 - a) * (2.0 - a);
        default:
          return a < 1.0 ? 3.0 * a - 2.0 : 2.0 - a;
      }
    }

    /**
     * The values <code>h(-K), ..., h(K)</code> of the <code>order</code>-th derivative of the cubic
     * B-spline convolved with the Gaussian.
     *
     * @return <code>null</code> for the interpolation itself (order 0 without a Gaussian), which
     *         gives the samples back
     */
    private static double[] splineKernel(int order, double sigma) {
      // a Gaussian this narrow changes the kernel by less than the precision of an image
      if (sigma < 1.0e-3) {
        if (order == 0) {
          return null;
        }
        return new double[] {bSpline(order, -1), bSpline(order, 0), bSpline(order, 1)};
      }
      final int radius = (int) Math.ceil(6.0 * sigma) + 2;
      double[] kernel = new double[2 * radius + 1];
      // Simpson's rule on each of the four pieces of the spline
      final int steps = 2 * Math.max(100, (int) Math.ceil(40.0 / sigma));
      final double norm = 1.0 / (sigma * Math.sqrt(2.0 * Math.PI));
      for (int k = -radius; k <= radius; k++) {
        double total = 0.0;
        for (int piece = -2; piece < 2; piece++) {
          final double h = 1.0 / steps;
          double sum = 0.0;
          for (int i = 0; i <= steps; i++) {
            // inside of the piece, so that the one sided values at its ends are used
            double v = piece + i * h;
            double inside = piece + Math.min(Math.max(i * h, 1e-12), 1.0 - 1e-12);
            double gauss = Math.exp(-(k - v) * (k - v) / (2.0 * sigma * sigma)) * norm;
            double weight = i == 0 || i == steps ? 1.0 : (i & 1) == 1 ? 4.0 : 2.0;
            sum += weight * bSpline(order, inside) * gauss;
          }
          total += sum * h / 3.0;
        }
        kernel[k + radius] = total;
      }
      return kernel;
    }

    /**
     * The B-spline coefficients of the line, continued with its end values, summed with the
     * <code>kernel</code>.
     */
    private static double[] splineFilter(double[] line, double[] kernel) {
      final int radius = kernel.length / 2;
      // the coefficients decay like (Sqrt(3)-2)^n from an end, 40 pixels are below the precision
      final int pad = radius + 40;
      final int n = line.length + 2 * pad;
      double[] samples = new double[n];
      for (int i = 0; i < n; i++) {
        samples[i] = line[Math.min(line.length - 1, Math.max(0, i - pad))];
      }
      // (c(i-1) + 4*c(i) + c(i+1))/6 == samples(i), the ends continued with their own value
      double[] diagonal = new double[n];
      double[] coefficients = new double[n];
      diagonal[0] = 5.0 / 6.0;
      coefficients[0] = samples[0];
      for (int i = 1; i < n; i++) {
        double factor = (1.0 / 6.0) / diagonal[i - 1];
        diagonal[i] = (i == n - 1 ? 5.0 / 6.0 : 4.0 / 6.0) - factor / 6.0;
        coefficients[i] = samples[i] - factor * coefficients[i - 1];
      }
      coefficients[n - 1] /= diagonal[n - 1];
      for (int i = n - 2; i >= 0; i--) {
        coefficients[i] = (coefficients[i] - coefficients[i + 1] / 6.0) / diagonal[i];
      }
      double[] result = new double[line.length];
      for (int x = 0; x < line.length; x++) {
        double sum = 0.0;
        for (int k = -radius; k <= radius; k++) {
          sum += coefficients[x + pad - k] * kernel[k + radius];
        }
        result[x] = sum;
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }
  }

  /**
   * <code>GradientFilter(image, r)</code>: the magnitude of the gradient, from the discrete
   * derivative of the Gaussian kernel of radius <code>r</code> along one direction and that kernel
   * along the other. For <code>r == 1</code> the derivative is the central difference
   * <code>(f(x+1)-f(x-1))/2</code>.
   */
  private static class GradientFilter extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!(ast.arg1() instanceof ImageExpr)) {
        return F.NIL;
      }
      int radius = ast.arg2().toIntDefault();
      if (radius < 1) {
        return F.NIL;
      }
      ImageExpr image = (ImageExpr) ast.arg1();
      double[][][] channels = channels(image, engine);
      if (channels == null) {
        return F.NIL;
      }
      // the discrete Gaussian of radius r+1, whose central difference is the derivative kernel
      double[] wide = FilterFunctions.gaussianKernel(radius + 1, radius / 2.0, engine);
      double[] derivative = new double[2 * radius + 1];
      double scale = 0.0;
      for (int k = 1; k <= radius; k++) {
        double weight = (wide[radius + 1 + k - 1] - wide[radius + 1 + k + 1]) / 2.0;
        derivative[radius + k] = weight;
        derivative[radius - k] = -weight;
        scale += 2.0 * k * weight;
      }
      for (int i = 0; i < derivative.length; i++) {
        // a ramp of slope 1 has the derivative 1
        derivative[i] /= scale;
      }
      double[] smooth = FilterFunctions.gaussianKernel(radius, radius / 2.0, engine);
      final int height = channels[0].length;
      final int width = channels[0][0].length;
      double[][] magnitude = new double[height][width];
      for (double[][] channel : channels) {
        double[][] dx = correlate(correlate(channel, derivative, false), smooth, true);
        double[][] dy = correlate(correlate(channel, derivative, true), smooth, false);
        for (int y = 0; y < height; y++) {
          for (int x = 0; x < width; x++) {
            magnitude[y][x] += dx[y][x] * dx[y][x] + dy[y][x] * dy[y][x];
          }
        }
      }
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          magnitude[y][x] = Math.sqrt(magnitude[y][x]);
        }
      }
      return toImage(new double[][][] {magnitude}, ImageOptions.DEFAULT);
    }

    /** Correlate the rows, or the columns, with the kernel; "Fixed" padding. */
    private static double[][] correlate(double[][] data, double[] kernel, boolean columns) {
      final int height = data.length;
      final int width = data[0].length;
      final int radius = kernel.length / 2;
      double[][] result = new double[height][width];
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          double sum = 0.0;
          for (int k = -radius; k <= radius; k++) {
            sum += kernel[k + radius] * (columns //
                ? data[Math.min(height - 1, Math.max(0, y + k))][x]
                : data[y][Math.min(width - 1, Math.max(0, x + k))]);
          }
          result[y][x] = sum;
        }
      }
      return result;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   * The foreground of an image or a matrix as <code>[row][column]</code>: the pixels with a sample
   * greater than the threshold. A pixel of several channels counts with its largest sample.
   *
   * @return <code>null</code> if <code>arg</code> is neither an image nor a matrix of numbers
   */
  private static boolean[][] foreground(IExpr arg, double threshold, EvalEngine engine) {
    double[][][] channels =
        arg instanceof ImageExpr ? channels((ImageExpr) arg, engine) : channels(arg);
    if (channels == null) {
      return null;
    }
    final int height = channels[0].length;
    final int width = channels[0][0].length;
    boolean[][] result = new boolean[height][width];
    for (double[][] channel : channels) {
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          result[y][x] |= channel[y][x] > threshold;
        }
      }
    }
    return result;
  }

  /**
   * <code>MorphologicalComponents(image)</code>, <code>MorphologicalComponents(image, t)</code>:
   * the matrix in which every pixel greater than <code>t</code> (<code>0</code> if not given) has
   * the number of its connected component and every other pixel has <code>0</code>. The components
   * are numbered in the order their first pixel is met row by row, and pixels which only touch at a
   * corner are connected unless <code>CornerNeighbors -> False</code> is given.
   */
  private static class MorphologicalComponents extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      double threshold = 0.0;
      boolean cornerNeighbors = true;
      for (int i = 2; i < ast.size(); i++) {
        IExpr arg = ast.get(i);
        if (arg.isRuleAST()) {
          if (arg.first() != S.CornerNeighbors) {
            return F.NIL;
          }
          cornerNeighbors = !arg.second().isFalse();
        } else if (i == 2) {
          threshold = arg.evalfNaN();
          if (Double.isNaN(threshold)) {
            return F.NIL;
          }
        } else {
          return F.NIL;
        }
      }
      boolean[][][] volume = foreground3D(ast.arg1(), threshold);
      if (volume != null) {
        return toList(labels(volume, cornerNeighbors));
      }
      boolean[][] foreground = foreground(ast.arg1(), threshold, engine);
      if (foreground == null) {
        return F.NIL;
      }
      final int[][] labels = labels(new boolean[][][] {foreground}, cornerNeighbors)[0];
      return F.matrix((i, j) -> F.ZZ(labels[i][j]), foreground.length, foreground[0].length);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }
  }

  /**
   * <code>TotalVariationFilter(image, lambda)</code>: the total variation denoising of every
   * channel, see {@link FilterFunctions#totalVariation}. The image keeps its type.
   */
  private static class TotalVariationFilter extends ImageFilter {

    @Override
    double[][] filter(double[][] channel, IAST ast, EvalEngine engine) {
      double[] parameters = FilterFunctions.totalVariationParameters(ast);
      if (parameters == null) {
        return null;
      }
      return FilterFunctions.totalVariation(channel, parameters[0], (int) parameters[1], engine);
    }

    @Override
    boolean keepsType() {
      return true;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_4;
    }
  }

  /**
   * <code>MorphologicalEulerNumber(image)</code>, <code>MorphologicalEulerNumber(image, t)</code>:
   * the number of connected components of the pixels whose intensity is greater than
   * <code>t</code> (<code>0</code> if not given) minus the number of holes in them. Pixels which
   * only touch at a corner are connected unless <code>CornerNeighbors -> False</code> is given; the
   * background is connected the other way. <code>Padding -> v</code> continues the image beyond its
   * border with the value <code>v</code>, <code>0</code> by default.
   */
  private static class MorphologicalEulerNumber extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      double threshold = 0.0;
      double padding = 0.0;
      boolean cornerNeighbors = true;
      for (int i = 2; i < ast.size(); i++) {
        IExpr arg = ast.get(i);
        if (arg.isRuleAST() && arg.first() == S.CornerNeighbors) {
          cornerNeighbors = !arg.second().isFalse();
        } else if (arg.isRuleAST() && arg.first() == S.Padding) {
          padding = arg.second().evalfNaN();
        } else if (i == 2 && !arg.isRuleAST()) {
          threshold = arg.evalfNaN();
        } else {
          return F.NIL;
        }
      }
      if (Double.isNaN(threshold) || Double.isNaN(padding)
          || !(ast.arg1() instanceof ImageExpr)) {
        return F.NIL;
      }
      double[][][] channels = channels((ImageExpr) ast.arg1(), engine);
      if (channels == null) {
        return F.NIL;
      }
      final int height = channels[0].length;
      final int width = channels[0][0].length;
      // the image with a ring of padding pixels around it
      boolean ring = padding > threshold;
      boolean[][] foreground = new boolean[height + 2][width + 2];
      boolean[][] background = new boolean[height + 2][width + 2];
      for (int y = 0; y < height + 2; y++) {
        for (int x = 0; x < width + 2; x++) {
          boolean inside = y > 0 && x > 0 && y <= height && x <= width;
          boolean value = inside ? intensity(channels, y - 1, x - 1) > threshold : ring;
          foreground[y][x] = value;
          background[y][x] = !value;
        }
      }
      int components = count(labels(new boolean[][][] {foreground}, cornerNeighbors));
      int holes = count(labels(new boolean[][][] {background}, !cornerNeighbors));
      if (!ring) {
        // the background around the image is no hole
        holes--;
      }
      return F.ZZ(components - holes);
    }

    /** The grey level of a pixel, the luminance for a colour pixel. */
    private static double intensity(double[][][] channels, int y, int x) {
      if (channels.length >= 3) {
        return Colors.grayLevel(channels[0][y][x], channels[1][y][x], channels[2][y][x]);
      }
      return channels[0][y][x];
    }

    private static int count(int[][][] labels) {
      int max = 0;
      for (int[] row : labels[0]) {
        for (int label : row) {
          max = Math.max(max, label);
        }
      }
      return max;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_4;
    }
  }

  /**
   * <code>Threshold(image, d)</code> and <code>Threshold(data, d)</code>: every sample whose
   * absolute value is below <code>d</code> becomes <code>0</code>, the others are kept;
   * <code>Threshold(x, {"Hard", d})</code> is the same and <code>Threshold(x, {"Soft", d})</code>
   * also moves the kept samples by <code>d</code> towards <code>0</code>. A list keeps its exact
   * numbers.
   */
  private static class Threshold extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr spec = ast.arg2();
      boolean soft = false;
      if (spec.isList2() && spec.first().isString()) {
        String type = spec.first().toString();
        if (type.equals("Soft")) {
          soft = true;
        } else if (!type.equals("Hard")) {
          return F.NIL;
        }
        spec = spec.second();
      }
      final double delta = spec.evalfNaN();
      if (Double.isNaN(delta) || delta < 0.0) {
        return F.NIL;
      }
      final boolean softThreshold = soft;
      if (ast.arg1() instanceof ImageExpr) {
        ImageExpr image = (ImageExpr) ast.arg1();
        double[][][] channels = channels(image, engine);
        if (channels == null) {
          return F.NIL;
        }
        for (double[][] channel : channels) {
          for (double[] row : channel) {
            for (int x = 0; x < row.length; x++) {
              row[x] = threshold(row[x], delta, softThreshold);
            }
          }
        }
        return toImage(channels, image.getOptions(), image.sampleType());
      }
      if (ast.arg1().isList()) {
        return thresholdList(ast.arg1(), spec, delta, softThreshold, engine);
      }
      return F.NIL;
    }

    /** The thresholded nested list of real numbers, or <code>F.NIL</code>. */
    private static IExpr thresholdList(IExpr data, IExpr d, double delta, boolean soft,
        EvalEngine engine) {
      if (data.isList()) {
        IAST list = (IAST) data;
        IASTAppendable result = F.ListAlloc(list.argSize());
        for (IExpr element : list) {
          IExpr value = thresholdList(element, d, delta, soft, engine);
          if (value.isNIL()) {
            return F.NIL;
          }
          result.append(value);
        }
        return result;
      }
      if (!data.isReal()) {
        return F.NIL;
      }
      if (Math.abs(data.evalf()) < delta) {
        return data.isExactNumber() ? F.C0 : F.CD0;
      }
      return soft ? engine.evaluate(F.Subtract(data, F.Times(F.Sign(data), d))) : data;
    }

    private static double threshold(double value, double delta, boolean soft) {
      if (Math.abs(value) < delta) {
        return 0.0;
      }
      return soft ? value - Math.signum(value) * delta : value;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   * <code>ImagePeriodogram(image)</code>: the squared magnitude of the discrete Fourier transform
   * of every channel, on a logarithmic scale stretched to <code>0 ... 1</code>, as a
   * <code>"Byte"</code> image. The zero frequency lies in the centre, at the pixel
   * <code>Floor((n-1)/2)</code> counted from the top left, or with <code>Alignment -> Top</code>
   * in the top left corner.
   */
  private static class ImagePeriodogram extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!(ast.arg1() instanceof ImageExpr)) {
        return F.NIL;
      }
      boolean center = true;
      if (ast.argSize() == 2) {
        IExpr option = ast.arg2();
        if (!option.isRuleAST() || option.first() != S.Alignment) {
          return F.NIL;
        }
        if (option.second() == S.Top || option.second().equals(F.list(S.Left, S.Top))) {
          center = false;
        } else if (option.second() != S.Center) {
          return F.NIL;
        }
      }
      ImageExpr image = (ImageExpr) ast.arg1();
      double[][][] channels = channels(image, engine);
      if (channels == null) {
        return F.NIL;
      }
      final int height = channels[0].length;
      final int width = channels[0][0].length;
      if ((long) height * width * (height + width) > 50_000_000L) {
        return F.NIL;
      }
      final int shiftY = center ? (height - 1) / 2 : 0;
      final int shiftX = center ? (width - 1) / 2 : 0;
      int colorChannels = channels.length == 2 || channels.length == 4 ? channels.length - 1
          : channels.length;
      double[][][] result = new double[colorChannels][][];
      for (int c = 0; c < colorChannels; c++) {
        double[][] power = power(channels[c], engine);
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        for (double[] row : power) {
          for (int x = 0; x < width; x++) {
            row[x] = row[x] > 0.0 ? Math.log(row[x]) : Double.NEGATIVE_INFINITY;
            if (row[x] > Double.NEGATIVE_INFINITY) {
              min = Math.min(min, row[x]);
              max = Math.max(max, row[x]);
            }
          }
        }
        double[][] shifted = new double[height][width];
        for (int y = 0; y < height; y++) {
          for (int x = 0; x < width; x++) {
            double value = power[y][x];
            shifted[(y + shiftY) % height][(x + shiftX) % width] =
                max > min && value > Double.NEGATIVE_INFINITY ? (value - min) / (max - min) : 0.0;
          }
        }
        result[c] = shifted;
      }
      return toImage(result, image.getOptions(), Pixels.BYTE);
    }

    /** The squared magnitudes of the two dimensional discrete Fourier transform. */
    private static double[][] power(double[][] data, EvalEngine engine) {
      final int height = data.length;
      final int width = data[0].length;
      // the rows first, then the columns
      double[][] re = new double[height][width];
      double[][] im = new double[height][width];
      for (int y = 0; y < height; y++) {
        for (int v = 0; v < width; v++) {
          double sumRe = 0.0;
          double sumIm = 0.0;
          for (int x = 0; x < width; x++) {
            double angle = -2.0 * Math.PI * ((long) v * x % width) / width;
            sumRe += data[y][x] * Math.cos(angle);
            sumIm += data[y][x] * Math.sin(angle);
          }
          re[y][v] = sumRe;
          im[y][v] = sumIm;
        }
      }
      double[][] power = new double[height][width];
      for (int v = 0; v < width; v++) {
        engine.checkTimeLimit();
        for (int u = 0; u < height; u++) {
          double sumRe = 0.0;
          double sumIm = 0.0;
          for (int y = 0; y < height; y++) {
            double angle = -2.0 * Math.PI * ((long) u * y % height) / height;
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            sumRe += re[y][v] * cos - im[y][v] * sin;
            sumIm += re[y][v] * sin + im[y][v] * cos;
          }
          power[u][v] = sumRe * sumRe + sumIm * sumIm;
        }
      }
      return power;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   * <code>WatershedComponents(image)</code>, <code>WatershedComponents(image, markers)</code>: the
   * matrix of the basins the grey levels of <code>image</code> form, numbered from <code>1</code>,
   * with <code>0</code> for the pixels of the watershed between two basins. Without markers every
   * regional minimum grows into a basin, numbered in the order its first pixel is met row by row.
   * The markers are an image of the same size, each connected component of whose non-zero pixels
   * grows into a basin, or a list of positions <code>{x, y}</code> in image coordinates, each of
   * which grows into the basin of its number in the list. Pixels which only touch at a corner are
   * neighbours unless <code>CornerNeighbors -> False</code> is given.
   */
  private static class WatershedComponents extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!(ast.arg1() instanceof ImageExpr)) {
        return F.NIL;
      }
      boolean cornerNeighbors = true;
      IExpr markers = F.NIL;
      for (int i = 2; i < ast.size(); i++) {
        IExpr arg = ast.get(i);
        if (arg.isRuleAST()) {
          if (arg.first() != S.CornerNeighbors) {
            return F.NIL;
          }
          cornerNeighbors = !arg.second().isFalse();
        } else if (i == 2) {
          markers = arg;
        } else {
          return F.NIL;
        }
      }
      double[][] relief = grayLevels((ImageExpr) ast.arg1(), engine);
      if (relief == null) {
        return F.NIL;
      }
      final int height = relief.length;
      final int width = relief[0].length;
      final int[][] basins;
      if (markers.isPresent()) {
        int[][] seeds = seeds(markers, width, height, cornerNeighbors, engine);
        if (seeds == null) {
          // an image of another size, or a matrix, is no marker specification
          return Errors.printMessage(S.WatershedComponents, "mkinv",
              F.list(F.list(F.ZZ(width), F.ZZ(height)), markers), engine);
        }
        basins = Watershed.flood(relief, seeds, cornerNeighbors);
      } else {
        basins = Watershed.fromMinima(relief, cornerNeighbors);
      }
      return F.matrix((i, j) -> F.ZZ(basins[i][j]), height, width);
    }

    /**
     * The grey levels of an image as <code>[row][column]</code>: the sample of a one channel image,
     * and <code>0.299 r + 0.587 g + 0.114 b</code> of a colour one.
     */
    private static double[][] grayLevels(ImageExpr image, EvalEngine engine) {
      double[][][] channels = channels(image, engine);
      if (channels == null) {
        return null;
      }
      if (channels.length < 3) {
        return channels[0];
      }
      final int height = channels[0].length;
      final int width = channels[0][0].length;
      double[][] gray = new double[height][width];
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          gray[y][x] = Colors.grayLevel(channels[0][y][x], channels[1][y][x], channels[2][y][x]);
        }
      }
      return gray;
    }

    /**
     * The seeds the markers give, numbered from <code>1</code>, in an image of
     * <code>width x height</code> pixels.
     *
     * @return <code>null</code> if <code>markers</code> is no image of that size and no list of
     *         positions inside the image
     */
    private static int[][] seeds(IExpr markers, int width, int height, boolean cornerNeighbors,
        EvalEngine engine) {
      if (markers instanceof ImageExpr) {
        boolean[][] foreground = foreground(markers, 0.0, engine);
        if (foreground == null || foreground.length != height || foreground[0].length != width) {
          return null;
        }
        return labels(new boolean[][][] {foreground}, cornerNeighbors)[0];
      }
      if (!markers.isList() || markers.argSize() == 0) {
        return null;
      }
      int[] dimensions = markers.isMatrix(false);
      if (dimensions != null && dimensions[0] == height && dimensions[1] == width) {
        // a matrix of the size of the image is a label matrix, not a list of positions
        return null;
      }
      int[][] seeds = new int[height][width];
      IAST positions = (IAST) markers;
      for (int k = 1; k <= positions.argSize(); k++) {
        IExpr position = positions.get(k);
        if (!position.isList2()) {
          return null;
        }
        double x = position.first().evalfNaN();
        double y = position.second().evalfNaN();
        if (Double.isNaN(x) || Double.isNaN(y) || x < 0 || x > width || y < 0 || y > height) {
          return null;
        }
        // image coordinates count y from the bottom, the rows are counted from the top
        int column = Math.min(width - 1, (int) Math.floor(x));
        int row = height - 1 - Math.min(height - 1, (int) Math.floor(y));
        seeds[row][column] = k;
      }
      return seeds;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }
  }

  /**
   * <code>MorphologicalGraph(image, options)</code>: the graph of the skeleton of the pixels of an
   * image greater than <code>1/2</code>, with a vertex at every end and branch point of the skeleton
   * and an edge along every branch, see {@link Skeleton#graph(boolean[][])}. The options are given
   * to the <code>Graph</code>.
   */
  private static class MorphologicalGraph extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!(ast.arg1() instanceof ImageExpr)) {
        return F.NIL;
      }
      for (int i = 2; i < ast.size(); i++) {
        if (!ast.get(i).isRuleAST()) {
          return F.NIL;
        }
      }
      boolean[][] foreground = foreground(ast.arg1(), 0.5, engine);
      if (foreground == null || foreground.length == 0 || foreground[0].length == 0) {
        return F.NIL;
      }
      IASTAppendable graph = Skeleton.graph(foreground);
      graph.appendAll(ast, 2, ast.size());
      return graph;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }
  }

  /**
   * The connected components of a foreground <code>[slice][row][column]</code>, numbered in the
   * order their first voxel is met slice by slice and row by row. Voxels are connected through
   * their faces, and also through their edges and corners if <code>cornerNeighbors</code> is set:
   * 26 neighbours in three and 8 in two dimensions, otherwise 6 and 4.
   */
  private static int[][][] labels(boolean[][][] foreground, boolean cornerNeighbors) {
    final int depth = foreground.length;
    final int height = foreground[0].length;
    final int width = foreground[0][0].length;
    int[][][] label = new int[depth][height][width];
    int components = 0;
    ArrayDeque<int[]> queue = new ArrayDeque<int[]>();
    for (int z = 0; z < depth; z++) {
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          if (!foreground[z][y][x] || label[z][y][x] != 0) {
            continue;
          }
          label[z][y][x] = ++components;
          queue.add(new int[] {z, y, x});
          while (!queue.isEmpty()) {
            int[] voxel = queue.poll();
            for (int dz = -1; dz <= 1; dz++) {
              for (int dy = -1; dy <= 1; dy++) {
                for (int dx = -1; dx <= 1; dx++) {
                  int offsets = Math.abs(dz) + Math.abs(dy) + Math.abs(dx);
                  if (offsets == 0 || (!cornerNeighbors && offsets > 1)) {
                    continue;
                  }
                  int slice = voxel[0] + dz;
                  int row = voxel[1] + dy;
                  int column = voxel[2] + dx;
                  if (slice >= 0 && slice < depth && row >= 0 && row < height && column >= 0
                      && column < width && foreground[slice][row][column]
                      && label[slice][row][column] == 0) {
                    label[slice][row][column] = components;
                    queue.add(new int[] {slice, row, column});
                  }
                }
              }
            }
          }
        }
      }
    }
    return label;
  }

  /**
   * The foreground of a 3D image or of an array of rank 3 of real numbers as
   * <code>[slice][row][column]</code>: the voxels greater than the threshold. A voxel of several
   * channels counts with its largest sample.
   *
   * @return <code>null</code> if <code>arg</code> is neither
   */
  private static boolean[][][] foreground3D(IExpr arg, double threshold) {
    if (arg instanceof Image3DExpr) {
      double[][][][] samples = ((Image3DExpr) arg).samples();
      if (samples == null || samples.length == 0 || samples[0].length == 0
          || samples[0][0].length == 0 || samples[0][0][0].length == 0) {
        return null;
      }
      final int depth = samples[0].length;
      final int height = samples[0][0].length;
      final int width = samples[0][0][0].length;
      boolean[][][] result = new boolean[depth][height][width];
      for (double[][][] channel : samples) {
        for (int z = 0; z < depth; z++) {
          for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
              result[z][y][x] |= channel[z][y][x] > threshold;
            }
          }
        }
      }
      return result;
    }
    double[][][] volume = realArray3D(arg);
    if (volume == null) {
      return null;
    }
    boolean[][][] result = new boolean[volume.length][volume[0].length][volume[0][0].length];
    for (int z = 0; z < volume.length; z++) {
      for (int y = 0; y < volume[0].length; y++) {
        for (int x = 0; x < volume[0][0].length; x++) {
          result[z][y][x] = volume[z][y][x] > threshold;
        }
      }
    }
    return result;
  }

  /**
   * A rectangular array of rank 3 of real numbers as <code>[slice][row][column]</code>.
   *
   * @return <code>null</code> if <code>arg</code> is no such array
   */
  private static double[][][] realArray3D(IExpr arg) {
    IntArrayList dims = arg.isList() ? LinearAlgebraUtil.dimensions((IAST) arg) : null;
    if (dims == null || dims.size() != 3 || dims.getInt(0) == 0 || dims.getInt(1) == 0
        || dims.getInt(2) == 0) {
      return null;
    }
    final int depth = dims.getInt(0);
    final int height = dims.getInt(1);
    final int width = dims.getInt(2);
    double[][][] result = new double[depth][height][width];
    for (int z = 0; z < depth; z++) {
      IExpr slice = arg.getAt(z + 1);
      for (int y = 0; y < height; y++) {
        IExpr row = slice.getAt(y + 1);
        for (int x = 0; x < width; x++) {
          IExpr value = row.getAt(x + 1);
          if (!value.isReal()) {
            return null;
          }
          result[z][y][x] = value.evalf();
        }
      }
    }
    return result;
  }

  private static IAST toList(int[][][] labels) {
    IASTAppendable result = F.ListAlloc(labels.length);
    for (int[][] slice : labels) {
      final int[][] matrix = slice;
      result.append(F.matrix((i, j) -> F.ZZ(matrix[i][j]), slice.length, slice[0].length));
    }
    return result;
  }

  /**
   * <code>DeleteSmallComponents(image)</code> - the image with its small components set to black:
   * those smaller than the mean size of its components, or
   * <code>DeleteSmallComponents(image, n)</code> those of at most <code>n</code> pixels.
   * <code>DeleteSmallComponents(labels, n)</code> sets the small components of a matrix of integer
   * labels to <code>0</code>; without <code>n</code> it stays as it is.
   */
  private static class DeleteSmallComponents extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr rasterized = ImagePropertyFunctions.withRasterizedGraphics(ast, 1, engine);
      if (rasterized.isPresent()) {
        return rasterized;
      }
      int limit = -1;
      if (ast.argSize() >= 2) {
        limit = ast.arg2().toIntDefault();
        if (limit < 0) {
          return F.NIL;
        }
      }
      if (!(ast.arg1() instanceof ImageExpr)) {
        return deleteLabels(ast.arg1(), Math.max(limit, 0));
      }
      ImageExpr image = (ImageExpr) ast.arg1();
      Raster raster = image.raster();
      boolean[][] foreground = foreground(image, 0.0, engine);
      if (raster == null || foreground == null) {
        return F.NIL;
      }
      int[][] labels = labels(new boolean[][][] {foreground}, true)[0];
      java.util.Map<Integer, int[]> counts = labelCounts(new int[][][] {labels});
      if (counts.isEmpty()) {
        return image;
      }
      int pixels = 0;
      for (int[] count : counts.values()) {
        pixels += count[0];
      }
      double mean = (double) pixels / counts.size();
      Raster result = raster.copy();
      for (int y = 0; y < raster.height; y++) {
        for (int x = 0; x < raster.width; x++) {
          if (labels[y][x] != 0) {
            int size = counts.get(labels[y][x])[0];
            if (limit >= 0 ? size <= limit : size < mean) {
              for (int c = 0; c < raster.channels; c++) {
                result.set(x, y, c, 0.0);
              }
            }
          }
        }
      }
      ImageExpr deleted = ImageExpr.fromRaster(result, image.getOptions(), image.sampleType());
      return deleted == null ? F.NIL : deleted;
    }

    private static IExpr deleteLabels(IExpr matrix, int limit) {
      int[][][] labels = ComponentMeasurements.labelArray(matrix);
      if (labels == null || labels.length != 1 || matrix.isMatrix() == null) {
        return F.NIL;
      }
      final int[][] rows = labels[0];
      final java.util.Map<Integer, int[]> counts = labelCounts(labels);
      return F.matrix((i, j) -> {
        int label = rows[i][j];
        return F.ZZ(label != 0 && counts.get(label)[0] > limit ? label : 0);
      }, rows.length, rows[0].length);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /** The number of elements of each label above 0, ordered by the label. */
  private static java.util.TreeMap<Integer, int[]> labelCounts(int[][][] labels) {
    java.util.TreeMap<Integer, int[]> counts = new java.util.TreeMap<Integer, int[]>();
    for (int[][] slice : labels) {
      for (int[] row : slice) {
        for (int label : row) {
          if (label > 0) {
            counts.computeIfAbsent(label, k -> new int[1])[0]++;
          }
        }
      }
    }
    return counts;
  }

  /**
   * <code>MorphologicalBinarize(image, {t1, t2})</code> - the black and white image in which the
   * pixels above <code>t2</code> are white and so are those above <code>t1</code> connected to them,
   * corners included. A single <code>t</code> stands for <code>{0.8*t, t}</code>,
   * <code>{t}</code> for <code>{t, t}</code>. Without thresholds <code>t</code> is Otsu's threshold
   * of the image with its levels binned over <code>0 ... 1</code>, not over their own range as for
   * <code>FindThreshold</code>.
   */
  private static class MorphologicalBinarize extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr rasterized = ImagePropertyFunctions.withRasterizedGraphics(ast, 1, engine);
      if (rasterized.isPresent()) {
        return rasterized;
      }
      if (!(ast.arg1() instanceof ImageExpr)) {
        // Expecting an image or graphics instead of `1`.
        return Errors.printMessage(S.MorphologicalBinarize, "imginv", F.list(ast.arg1()), engine);
      }
      ImageExpr image = (ImageExpr) ast.arg1();
      Raster raster = image.raster();
      if (raster == null) {
        return F.NIL;
      }
      final double[] levels = ImageAdjustFunctions.levels(raster, image.sampleType());
      double low;
      double high;
      if (ast.argSize() < 2) {
        high = ImageAdjustFunctions.otsu(levels, image.sampleType(), true, false);
        low = 0.8 * high;
      } else if (ast.arg2().isList() && ast.arg2().argSize() >= 1 && ast.arg2().argSize() <= 2) {
        low = ast.arg2().first().evalfNaN();
        high = ((IAST) ast.arg2()).last().evalfNaN();
      } else {
        high = ast.arg2().evalfNaN();
        low = 0.8 * high;
      }
      if (Double.isNaN(low) || Double.isNaN(high)) {
        return F.NIL;
      }
      final int width = raster.width;
      final int height = raster.height;
      Raster binary = new Raster(width, height, 1);
      ArrayDeque<int[]> pending = new ArrayDeque<int[]>();
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          if (levels[y * width + x] > high) {
            binary.set(x, y, 0, 1.0);
            pending.add(new int[] {x, y});
          }
        }
      }
      while (!pending.isEmpty()) {
        int[] pixel = pending.poll();
        for (int row = Math.max(0, pixel[1] - 1); row <= Math.min(height - 1, pixel[1] + 1); row++) {
          for (int column = Math.max(0, pixel[0] - 1); column <= Math.min(width - 1,
              pixel[0] + 1); column++) {
            if (binary.get(column, row, 0) == 0.0 && levels[row * width + column] > low) {
              binary.set(column, row, 0, 1.0);
              pending.add(new int[] {column, row});
            }
          }
        }
      }
      ImageExpr result = ImageExpr.fromRaster(binary,
          image.getOptions().withColorSpace(S.Automatic), Pixels.BIT);
      return result == null ? F.NIL : result;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   * <code>ComponentMeasurements(labels, "Count")</code> - the rules <code>k -> n</code> of the
   * labels <code>k > 0</code> of a label matrix or a label array of rank 3 and the number
   * <code>n</code> of their elements, ordered by the label; <code>"Area"</code> is that number as a
   * real and <code>"EquivalentDiskRadius"</code> the radius of the disk of that area. An image (or a
   * 3D image) is labelled by its components first. A list of properties gives the list of their
   * values in each rule.
   */
  private static class ComponentMeasurements extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      int[][][] labels = null;
      if (arg1 instanceof ImageExpr) {
        boolean[][] foreground = foreground(arg1, 0.0, engine);
        if (foreground != null) {
          labels = labels(new boolean[][][] {foreground}, true);
        }
      } else if (arg1 instanceof Image3DExpr) {
        boolean[][][] foreground = foreground3D(arg1, 0.0);
        if (foreground != null) {
          labels = labels(foreground, true);
        }
      } else {
        labels = labelArray(arg1);
      }
      if (labels == null) {
        return F.NIL;
      }
      IExpr properties = ast.arg2();
      IAST propertyList = properties.isList() ? (IAST) properties : F.list(properties);
      for (IExpr property : propertyList) {
        if (measurement(property, 1).isNIL()) {
          return F.NIL;
        }
      }
      java.util.TreeMap<Integer, int[]> counts = labelCounts(labels);
      IASTAppendable result = F.ListAlloc(counts.size());
      for (java.util.Map.Entry<Integer, int[]> entry : counts.entrySet()) {
        int count = entry.getValue()[0];
        IASTAppendable values = F.ListAlloc(propertyList.argSize());
        for (IExpr property : propertyList) {
          values.append(measurement(property, count));
        }
        result.append(F.Rule(F.ZZ(entry.getKey()), properties.isList() ? values : values.arg1()));
      }
      return result;
    }

    /** The value of a property for a component of <code>count</code> elements. */
    private static IExpr measurement(IExpr property, int count) {
      if (property.isString("Count")) {
        return F.ZZ(count);
      }
      if (property.isString("Area")) {
        return F.num(count);
      }
      if (property.isString("EquivalentDiskRadius")) {
        return F.num(Math.sqrt(count / Math.PI));
      }
      return F.NIL;
    }

    /**
     * A label matrix or a label array of rank 3 of integers as
     * <code>[slice][row][column]</code>.
     */
    static int[][][] labelArray(IExpr arg) {
      if (!arg.isList()) {
        return null;
      }
      IntArrayList dims = LinearAlgebraUtil.dimensions((IAST) arg);
      if (dims.size() == 2) {
        arg = F.list(arg);
      } else if (dims.size() != 3) {
        return null;
      }
      IAST array = (IAST) arg;
      int[][][] result = new int[array.argSize()][][];
      for (int z = 0; z < array.argSize(); z++) {
        IAST slice = (IAST) array.get(z + 1);
        result[z] = new int[slice.argSize()][];
        for (int y = 0; y < slice.argSize(); y++) {
          IAST row = (IAST) slice.get(y + 1);
          result[z][y] = new int[row.argSize()];
          for (int x = 0; x < row.argSize(); x++) {
            int label = row.get(x + 1).toIntDefault();
            if (label < 0) {
              return null;
            }
            result[z][y][x] = label;
          }
        }
      }
      return result;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   * <code>Inpaint(image, mask, Method -> "Diffusion")</code> - the pixels where the mask (an image
   * or a matrix of the same size) is not 0 filled in from the rest of the image.
   *
   * <p>
   * Every filled pixel is the mean of its eight neighbours inside the image, the filled ones among
   * them included: the discrete harmonic interpolation of the known pixels, solved by Gauss-Seidel
   * sweeps until the samples no longer change. Every channel is filled on its own, and the image
   * keeps its type. Other methods stay unevaluated.
   */
  private static class Inpaint extends AbstractEvaluator {

    /** The change of a sample in a sweep below which the samples have settled. */
    private static final double TOLERANCE = 1.0e-12;

    /** The most sweeps over the filled pixels. */
    private static final int MAX_SWEEPS = 100000;

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.argSize() < 3 || !(ast.arg1() instanceof ImageExpr)) {
        return F.NIL;
      }
      OptionArgs options = new OptionArgs(ast.topHead(), ast, 3, engine);
      IExpr method = options.getOption(S.Method);
      if (!method.isString("Diffusion")) {
        return F.NIL;
      }
      ImageExpr image = (ImageExpr) ast.arg1();
      double[][][] channels = channels(image, engine);
      double[][][] maskChannels = ast.arg2() instanceof ImageExpr
          ? channels((ImageExpr) ast.arg2(), engine)
          : channels(ast.arg2());
      if (channels == null || maskChannels == null) {
        return F.NIL;
      }
      final int height = channels[0].length;
      final int width = channels[0][0].length;
      if (maskChannels[0].length != height || maskChannels[0][0].length != width) {
        return F.NIL;
      }
      IntArrayList holes = new IntArrayList();
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          for (double[][] maskChannel : maskChannels) {
            if (maskChannel[y][x] != 0.0) {
              holes.add(y * width + x);
              break;
            }
          }
        }
      }
      if (holes.isEmpty() || holes.size() == width * height) {
        // nothing to fill, or nothing to fill it from
        return ast.arg1();
      }
      for (double[][] channel : channels) {
        fill(channel, holes, width, height);
      }
      return toImage(channels, image.getOptions(), image.sampleType());
    }

    private static void fill(double[][] channel, IntArrayList holes, int width, int height) {
      // the filled pixels start at the mean of the known ones
      boolean[] hole = new boolean[width * height];
      for (int i = 0; i < holes.size(); i++) {
        hole[holes.getInt(i)] = true;
      }
      double known = 0.0;
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          if (!hole[y * width + x]) {
            known += channel[y][x];
          }
        }
      }
      known /= width * height - holes.size();
      for (int i = 0; i < holes.size(); i++) {
        int index = holes.getInt(i);
        channel[index / width][index % width] = known;
      }
      for (int sweep = 0; sweep < MAX_SWEEPS; sweep++) {
        double change = 0.0;
        for (int i = 0; i < holes.size(); i++) {
          int index = holes.getInt(i);
          int y = index / width;
          int x = index % width;
          double sum = 0.0;
          int count = 0;
          for (int row = Math.max(0, y - 1); row <= Math.min(height - 1, y + 1); row++) {
            for (int column = Math.max(0, x - 1); column <= Math.min(width - 1, x + 1); column++) {
              if (row != y || column != x) {
                sum += channel[row][column];
                count++;
              }
            }
          }
          double value = sum / count;
          change = Math.max(change, Math.abs(value - channel[y][x]));
          channel[y][x] = value;
        }
        if (change <= TOLERANCE) {
          return;
        }
      }
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }
  }

  /**
   * <code>DistanceTransform(image)</code>, <code>DistanceTransform(image, t)</code>: a
   * <code>"Real32"</code> image in which every pixel greater than <code>t</code> (<code>0</code> if
   * not given) has its Euclidean distance to the nearest pixel which isn't, and every other pixel
   * has <code>0</code>. The distances are in pixels and not scaled to <code>0...1</code>. An image
   * without such a pixel gets the distance to the outside of the image.
   */
  private static class DistanceTransform extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isAST(S.Graphics)) {
        // the graphics as the image they are drawn to
        IExpr image = engine.evaluate(F.unaryAST1(S.Rasterize, ast.arg1()));
        return image instanceof ImageExpr ? engine.evaluate(ast.setAtCopy(1, image)) : F.NIL;
      }
      if (!(ast.arg1() instanceof ImageExpr)) {
        return F.NIL;
      }
      double threshold = 0.0;
      if (ast.isAST2()) {
        threshold = ast.arg2().evalfNaN();
        if (Double.isNaN(threshold)) {
          return F.NIL;
        }
      }
      boolean[][] foreground = foreground(ast.arg1(), threshold, engine);
      if (foreground == null) {
        return F.NIL;
      }
      final int height = foreground.length;
      final int width = foreground[0].length;
      boolean background = false;
      for (int y = 0; y < height && !background; y++) {
        for (int x = 0; x < width && !background; x++) {
          background = !foreground[y][x];
        }
      }
      double[][] distance = new double[height][width];
      if (!background) {
        for (int y = 0; y < height; y++) {
          for (int x = 0; x < width; x++) {
            distance[y][x] = Math.min(Math.min(y + 1, height - y), Math.min(x + 1, width - x));
          }
        }
        return toImage(new double[][][] {distance}, ImageOptions.DEFAULT);
      }
      // the exact squared distances in two passes: along the columns to the nearest background
      // pixel of the column, then along every row the lower envelope of the parabolas
      final double far = (double) (height + width) * (height + width);
      double[][] squared = new double[height][width];
      for (int x = 0; x < width; x++) {
        double[] column = new double[height];
        for (int y = 0; y < height; y++) {
          column[y] = foreground[y][x] ? far : 0.0;
        }
        double[] d = lowerEnvelope(column);
        for (int y = 0; y < height; y++) {
          squared[y][x] = d[y];
        }
      }
      for (int y = 0; y < height; y++) {
        double[] d = lowerEnvelope(squared[y]);
        for (int x = 0; x < width; x++) {
          distance[y][x] = Math.sqrt(d[x]);
        }
      }
      return toImage(new double[][][] {distance}, ImageOptions.DEFAULT);
    }

    /**
     * <code>d(p) == Min((p-q)^2 + f(q))</code> over <code>q</code>, the lower envelope of the
     * parabolas rooted at the samples (P. Felzenszwalb, D. Huttenlocher: Distance transforms of
     * sampled functions).
     */
    private static double[] lowerEnvelope(double[] f) {
      final int n = f.length;
      double[] d = new double[n];
      int[] vertex = new int[n];
      double[] boundary = new double[n + 1];
      int k = 0;
      vertex[0] = 0;
      boundary[0] = Double.NEGATIVE_INFINITY;
      boundary[1] = Double.POSITIVE_INFINITY;
      for (int q = 1; q < n; q++) {
        double s;
        while (true) {
          int v = vertex[k];
          s = ((f[q] + (double) q * q) - (f[v] + (double) v * v)) / (2.0 * q - 2.0 * v);
          if (s <= boundary[k] && k > 0) {
            k--;
          } else {
            break;
          }
        }
        k++;
        vertex[k] = q;
        boundary[k] = s;
        boundary[k + 1] = Double.POSITIVE_INFINITY;
      }
      k = 0;
      for (int q = 0; q < n; q++) {
        while (boundary[k + 1] < q) {
          k++;
        }
        int v = vertex[k];
        d[q] = (double) (q - v) * (q - v) + f[v];
      }
      return d;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private ImageFilterFunctions() {}
}
