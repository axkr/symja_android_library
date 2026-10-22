package org.matheclipse.image.builtin;

import java.awt.image.BufferedImage;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.image.algo.Boof;
import org.matheclipse.image.algo.Enhance;
import org.matheclipse.image.algo.Pixels;
import org.matheclipse.image.algo.Raster;
import org.matheclipse.image.expression.data.ImageExpr;

/**
 * Turning an image into black and white, and moving its samples around inside
 * <code>0.0 ... 1.0</code>: <code>FindThreshold</code>, <code>Binarize</code>,
 * <code>LocalAdaptiveBinarize</code>, <code>ImageAdjust</code> and <code>HistogramTransform</code>.
 *
 * <p>
 * Thresholding always looks at the intensity, so a colour image is reduced to greyscale first and
 * the result of <code>Binarize</code> is a one channel image whose samples are 0 or 1.
 */
public class ImageAdjustFunctions {

  private static class Initializer {

    private static void init() {
      S.Binarize.setEvaluator(new Binarize());
      S.FindThreshold.setEvaluator(new FindThreshold());
      S.HistogramTransform.setEvaluator(new HistogramTransform());
      S.ImageAdjust.setEvaluator(new ImageAdjust());
      S.LocalAdaptiveBinarize.setEvaluator(new LocalAdaptiveBinarize());
    }
  }

  /**
   * <code>FindThreshold(image)</code> - the intensity that best separates the image into two
   * groups, as a number in <code>0.0 ... 1.0</code>.
   *
   * <p>
   * <code>Method -&gt; "Cluster"</code> (the default) is Otsu's method; <code>"Entropy"</code>,
   * <code>"Mean"</code> and <code>"Median"</code> are the other three.
   */
  private static class FindThreshold extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!(ast.arg1() instanceof ImageExpr)) {
        return F.NIL;
      }
      Raster raster = ((ImageExpr) ast.arg1()).raster();
      if (raster == null) {
        return F.NIL;
      }
      String method = methodOption(ast, 2, engine, "Cluster");
      double threshold = threshold(raster, method, false);
      return Double.isNaN(threshold) ? F.NIL : F.num(threshold / 255.0);
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
   * <code>Binarize(image, f)</code>: white where <code>f</code> gives <code>True</code> for the
   * pixel, its value for a gray image and the list of its channels otherwise.
   */
  private static IExpr binarizeByFunction(ImageExpr image, IExpr f, EvalEngine engine) {
    double[][][] channels = ImageFilterFunctions.channels(image, engine);
    if (channels == null) {
      return F.NIL;
    }
    final int height = channels[0].length;
    final int width = channels[0][0].length;
    final boolean[][] white = new boolean[height][width];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        IExpr pixel;
        if (channels.length == 1) {
          pixel = F.num(channels[0][y][x]);
        } else {
          IASTAppendable values = F.ListAlloc(channels.length);
          for (double[][] channel : channels) {
            values.append(F.num(channel[y][x]));
          }
          pixel = values;
        }
        IExpr test = engine.evaluate(F.unaryAST1(f, pixel));
        if (!test.isTrue() && !test.isFalse()) {
          // no decision, as for a symbol without a definition: the call stays as it is
          return F.NIL;
        }
        white[y][x] = test.isTrue();
      }
    }
    Raster binary = new Raster(width, height, 1);
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        binary.set(x, y, 0, white[y][x] ? 1.0 : 0.0);
      }
    }
    return bitImage(binary, image);
  }

  /**
   * <code>Binarize(image)</code> - a black and white image, split at
   * <code>FindThreshold(image)</code>. <code>Binarize(image, t)</code> splits at <code>t</code> and
   * <code>Binarize(image, {t1, t2})</code> keeps the samples inside the range.
   */
  private static class Binarize extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.argSize() >= 2 && ast.arg1() instanceof ImageExpr
          && (ast.arg2().isFunction() || (ast.arg2().isSymbol() && !ast.arg2().isNumericFunction()))) {
        return binarizeByFunction((ImageExpr) ast.arg1(), ast.arg2(), engine);
      }
      if (!(ast.arg1() instanceof ImageExpr)) {
        return F.NIL;
      }
      Raster raster = ((ImageExpr) ast.arg1()).raster();
      if (raster == null) {
        return F.NIL;
      }
      double low;
      double high = Double.POSITIVE_INFINITY;
      if (ast.argSize() >= 2 && !ast.arg2().isRuleAST()) {
        if (ast.arg2().isList()) {
          IAST range = (IAST) ast.arg2();
          if (range.argSize() != 2) {
            return F.NIL;
          }
          low = range.arg1().evalfNaN() * 255.0;
          high = range.arg2().evalfNaN() * 255.0;
          if (Double.isNaN(low) || Double.isNaN(high)) {
            return F.NIL;
          }
        } else {
          low = ast.arg2().evalfNaN() * 255.0;
          if (Double.isNaN(low)) {
            return F.NIL;
          }
        }
      } else {
        low = threshold(raster, methodOption(ast, 2, engine, "Cluster"), true);
        if (Double.isNaN(low)) {
          return F.NIL;
        }
      }

      // the thresholds are on the 0 ... 255 scale, the intensities of the samples as they are
      final double lowBound = low / 255.0;
      final double highBound = high / 255.0;
      Raster binary = new Raster(raster.width, raster.height, 1);
      for (int y = 0; y < raster.height; y++) {
        for (int x = 0; x < raster.width; x++) {
          double intensity = intensity(raster, x, y);
          binary.set(x, y, 0, intensity > lowBound && intensity <= highBound ? 1.0 : 0.0);
        }
      }
      return bitImage(binary, (ImageExpr) ast.arg1());
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
   * <code>LocalAdaptiveBinarize(image, r)</code> - a black and white image where the threshold is
   * computed from the <code>(2r+1) x (2r+1)</code> neighbourhood of each pixel rather than from the
   * whole image, which is what makes it survive uneven lighting.
   *
   * <p>
   * <code>Method -&gt; "Mean" | "Otsu" | "Niblack" | "Sauvola"</code>.
   */
  private static class LocalAdaptiveBinarize extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      BufferedImage image = ImagePropertyFunctions.bufferedImage(ast.arg1());
      if (image == null) {
        return F.NIL;
      }
      int radius = ast.argSize() >= 2 && !ast.arg2().isRuleAST() //
          ? ast.arg2().toIntDefault()
          : 2;
      if (radius < 1) {
        return F.NIL;
      }
      String method = methodOption(ast, 3, engine, "Mean");

      boolean[][] mask = Enhance.localBinary(image, radius, method);
      if (mask == null) {
        return F.NIL;
      }
      return new ImageExpr(Pixels.fromPixels(image.getWidth(), image.getHeight(), 1,
          (x, y) -> new float[] {mask[y][x] ? 255.0f : 0.0f}), null);
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
   * <code>ImageAdjust(image)</code> - the samples stretched so that the darkest becomes 0 and the
   * brightest 1.
   *
   * <p>
   * <code>ImageAdjust(image, {c, b})</code> and <code>ImageAdjust(image, {c, b, g})</code> apply
   * contrast <code>c</code>, brightness <code>b</code> and gamma <code>g</code> instead, as
   * <code>x -&gt; ((x - 1/2) 10^c + 1/2 + b)^(1/g)</code>.
   */
  private static class ImageAdjust extends AbstractEvaluator {

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

      if (ast.argSize() == 1) {
        // the range of the samples as they are, also beyond 0 ... 1, onto 0 ... 1
        double[] range = raster.colorRange();
        if (range[1] <= range[0]) {
          return ast.arg1();
        }
        final double minimum = range[0];
        final double width = range[1] - range[0];
        return imageOf(raster.mapColor(value -> (value - minimum) / width), image);
      }

      double contrast = 0.0;
      double brightness = 0.0;
      double gamma = 1.0;
      if (ast.arg2().isList()) {
        IAST parameters = (IAST) ast.arg2();
        if (parameters.argSize() < 2 || parameters.argSize() > 3) {
          return F.NIL;
        }
        contrast = parameters.arg1().evalfNaN();
        brightness = parameters.arg2().evalfNaN();
        if (parameters.argSize() == 3) {
          gamma = parameters.arg3().evalfNaN();
        }
      } else {
        contrast = ast.arg2().evalfNaN();
      }
      if (Double.isNaN(contrast) || Double.isNaN(brightness) || Double.isNaN(gamma)
          || gamma == 0.0) {
        return F.NIL;
      }
      final double slope = Math.pow(10.0, contrast);
      final double offset = brightness;
      final double exponent = 1.0 / gamma;
      return imageOf(raster.mapColor(value -> {
        double adjusted = (value - 0.5) * slope + 0.5 + offset;
        if (adjusted < 0.0) {
          adjusted = 0.0;
        }
        return Math.pow(adjusted, exponent);
      }), image);
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
   * <code>HistogramTransform(image)</code> - the samples redistributed so that every intensity is
   * about equally common, which is what evens out a picture with too little contrast.
   *
   * <p>
   * A sample goes to the middle of the share of the samples which are not larger than it, on the
   * scale of 256 levels: <code>(256 m - 1/2)/255</code>. A colour image is equalized channel by
   * channel, and the type stays.
   */
  private static class HistogramTransform extends AbstractEvaluator {

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
      int count = raster.width * raster.height;
      Raster result = raster.copy();
      for (int c = 0; c < raster.colorChannels(); c++) {
        double[] sorted = new double[count];
        for (int y = 0; y < raster.height; y++) {
          for (int x = 0; x < raster.width; x++) {
            sorted[y * raster.width + x] = raster.get(x, y, c);
          }
        }
        java.util.Arrays.sort(sorted);
        for (int y = 0; y < raster.height; y++) {
          for (int x = 0; x < raster.width; x++) {
            double value = raster.get(x, y, c);
            // the share of the samples below the value and up to it
            int below = lowerBound(sorted, value);
            int upTo = upperBound(sorted, value);
            double middle = (below + upTo) / (2.0 * count);
            result.set(x, y, c, (256.0 * middle - 0.5) / 255.0);
          }
        }
      }
      ImageExpr equalized = ImageExpr.fromRaster(result, image.getOptions(), image.sampleType());
      return equalized == null ? F.NIL : equalized;
    }

    /** The number of entries of <code>sorted</code> below <code>value</code>. */
    private static int lowerBound(double[] sorted, double value) {
      int low = 0;
      int high = sorted.length;
      while (low < high) {
        int middle = (low + high) >>> 1;
        if (sorted[middle] < value) {
          low = middle + 1;
        } else {
          high = middle;
        }
      }
      return low;
    }

    /** The number of entries of <code>sorted</code> up to and including <code>value</code>. */
    private static int upperBound(double[] sorted, double value) {
      int low = 0;
      int high = sorted.length;
      while (low < high) {
        int middle = (low + high) >>> 1;
        if (sorted[middle] <= value) {
          low = middle + 1;
        } else {
          high = middle;
        }
      }
      return low;
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

  // -------------------------------------------------------------------- internals

  /**
   * The whole image threshold of the given method, on the 0 ... 255 scale, of the intensities as
   * they are: an image of reals with samples beyond 1 is thresholded over its whole range.
   *
   * @param cut <code>true</code> for the value <code>Binarize</code> splits the samples at,
   *        <code>false</code> for the one <code>FindThreshold</code> reports
   */
  private static double threshold(Raster raster, String method, boolean cut) {
    // the intensities row by row from the top, on the 0 ... 255 scale
    float[] values = new float[raster.width * raster.height];
    float low = 0.0f;
    float high = Boof.MAX_VALUE;
    for (int y = 0; y < raster.height; y++) {
      for (int x = 0; x < raster.width; x++) {
        float value = (float) (255.0 * intensity(raster, x, y));
        values[y * raster.width + x] = value;
        low = Math.min(low, value);
        high = Math.max(high, value);
      }
    }
    switch (method) {
      case "Cluster":
      case "Otsu":
      case "Automatic":
        return otsu(values, cut);
      case "Entropy":
      case "MinimumError":
        return Enhance.entropy(values, raster.width, raster.height, low, high);
      case "Mean": {
        double total = 0.0;
        for (float value : values) {
          total += value;
        }
        return total / values.length;
      }
      case "Median": {
        float[] sorted = values.clone();
        java.util.Arrays.sort(sorted);
        return sorted[(sorted.length - 1) / 2];
      }
      default:
        return Double.NaN;
    }
  }

  /**
   * Otsu's threshold of the values: the split of a 256 bin histogram over their own range which
   * separates the two groups best. Where several splits are equally good the first of them is
   * taken, unless the darker group is the darkest bin alone - two values only, say - where the
   * threshold goes to the middle of the gap above it.
   *
   * @param cut <code>true</code> for the value to split the samples at, which is the end of the bin
   *        the threshold names: that bin still belongs to the darker group
   */
  private static double otsu(float[] values, boolean cut) {
    double low = Double.POSITIVE_INFINITY;
    double high = Double.NEGATIVE_INFINITY;
    for (float value : values) {
      low = Math.min(low, value);
      high = Math.max(high, value);
    }
    if (!(high > low)) {
      return low;
    }
    final int bins = 256;
    double width = (high - low) / bins;
    long[] histogram = new long[bins];
    double total = 0.0;
    for (float value : values) {
      histogram[Math.min(bins - 1, (int) ((value - low) / width))]++;
      total += value;
    }
    double count = values.length;
    double weightBelow = 0.0;
    double sumBelow = 0.0;
    double best = -1.0;
    int first = 0;
    int last = 0;
    for (int t = 0; t < bins - 1; t++) {
      weightBelow += histogram[t];
      sumBelow += histogram[t] * (low + (t + 0.5) * width);
      double weightAbove = count - weightBelow;
      if (weightBelow == 0.0 || weightAbove == 0.0) {
        continue;
      }
      double meanBelow = sumBelow / weightBelow;
      double meanAbove = (total - sumBelow) / weightAbove;
      double between = weightBelow * weightAbove * (meanBelow - meanAbove) * (meanBelow - meanAbove);
      if (between > best * (1.0 + 1.0e-12)) {
        best = between;
        first = t;
        last = t;
      } else if (between >= best * (1.0 - 1.0e-12)) {
        last = t;
      }
    }
    if (first == 0) {
      return low + width * (first + last + 1) / 2.0;
    }
    return low + width * (cut ? first + 1 : first);
  }

  /** The <code>Method</code> option, looked for from <code>startIndex</code> on. */
  private static String methodOption(IAST ast, int startIndex, EvalEngine engine,
      String defaultMethod) {
    if (ast.size() <= startIndex) {
      return defaultMethod;
    }
    OptionArgs options = new OptionArgs(ast.topHead(), ast, startIndex, engine);
    IExpr method = options.getOption(S.Method);
    return method.isString() ? method.toString() : defaultMethod;
  }

  /**
   * The intensity of a pixel: its sample, or the weighted sum of its colour samples, as they are.
   */
  private static double intensity(Raster raster, int x, int y) {
    if (raster.colorChannels() < 3) {
      return raster.get(x, y, 0);
    }
    double r = raster.get(x, y, 0);
    double g = raster.get(x, y, 1);
    double b = raster.get(x, y, 2);
    if (r == g && g == b) {
      return r;
    }
    return 0.299 * r + 0.587 * g + 0.114 * b;
  }

  /** The image of new samples in the type and with the options of <code>source</code>. */
  private static IExpr imageOf(Raster raster, ImageExpr source) {
    ImageExpr image = ImageExpr.fromRaster(raster, source.getOptions(), source.sampleType());
    return image == null ? F.NIL : image;
  }

  /** The black and white image of samples 0 and 1: a <code>"Bit"</code> image. */
  private static IExpr bitImage(Raster raster, ImageExpr source) {
    ImageExpr image = ImageExpr.fromRaster(raster,
        source.getOptions().withColorSpace(S.Automatic), Pixels.BIT);
    return image == null ? F.NIL : image;
  }

  public static void initialize() {
    Initializer.init();
  }

  private ImageAdjustFunctions() {}
}
