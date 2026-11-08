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
   * groups, as a number in <code>0.0 ... 1.0</code>; <code>FindThreshold(data)</code> takes the
   * numbers of an array like the samples of an image.
   *
   * <p>
   * <code>Method -&gt; "Cluster"</code> (the default) is Otsu's method; <code>"Entropy"</code>,
   * <code>"Mean"</code> and <code>"Median"</code> are the other three.
   */
  private static class FindThreshold extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      Raster raster;
      String type = Pixels.REAL32;
      if (ast.arg1() instanceof ImageExpr) {
        raster = ((ImageExpr) ast.arg1()).raster();
        type = ((ImageExpr) ast.arg1()).sampleType();
      } else {
        raster = ofNumbers(ast.arg1());
      }
      if (raster == null) {
        return F.NIL;
      }
      String method = methodOption(ast, 2, engine, "Cluster");
      double threshold = threshold(raster, type, method, false);
      if (Double.isNaN(threshold)) {
        return F.NIL;
      }
      return Pixels.BIT.equals(type) && threshold == 0.5 ? F.C1D2 : F.num(threshold);
    }

    /**
     * The numbers of an array as a row of samples.
     *
     * @return <code>null</code> if <code>data</code> is no list of real numbers
     */
    private static Raster ofNumbers(IExpr data) {
      if (!data.isList()) {
        return null;
      }
      java.util.ArrayList<IExpr> leaves = new java.util.ArrayList<>();
      for (IExpr leaf : (IAST) F.Flatten(data).eval()) {
        leaves.add(leaf);
      }
      if (leaves.isEmpty()) {
        return null;
      }
      Raster raster = new Raster(leaves.size(), 1, 1);
      for (int i = 0; i < leaves.size(); i++) {
        double value = leaves.get(i).evalfNaN();
        if (Double.isNaN(value)) {
          return null;
        }
        raster.set(i, 0, 0, value);
      }
      return raster;
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
          low = range.arg1().evalfNaN();
          high = range.arg2().evalfNaN();
          if (Double.isNaN(low) || Double.isNaN(high)) {
            return F.NIL;
          }
        } else {
          low = ast.arg2().evalfNaN();
          if (Double.isNaN(low)) {
            return F.NIL;
          }
        }
      } else {
        low = threshold(raster, ((ImageExpr) ast.arg1()).sampleType(),
            methodOption(ast, 2, engine, "Cluster"), true);
        if (Double.isNaN(low)) {
          return F.NIL;
        }
      }

      final double lowBound = low;
      final double highBound = high;
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
   * <code>x -&gt; ((x - 1/2) 10^c + 1/2 + b)^(1/g)</code>. A third argument
   * <code>{imin, imax}</code> first rescales the samples from that range to <code>0 ... 1</code>,
   * and a fourth <code>{omin, omax}</code> rescales the adjusted samples from <code>0 ... 1</code>
   * to that range.
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
        // the range of the samples of each colour channel as they are, also beyond 0 ... 1, onto
        // 0 ... 1; a channel of a single value becomes 0
        Raster result = raster.copy();
        final int colorChannels = raster.colorChannels();
        for (int c = 0; c < colorChannels; c++) {
          double minimum = Double.POSITIVE_INFINITY;
          double maximum = Double.NEGATIVE_INFINITY;
          for (int y = 0; y < raster.height; y++) {
            for (int x = 0; x < raster.width; x++) {
              minimum = Math.min(minimum, raster.get(x, y, c));
              maximum = Math.max(maximum, raster.get(x, y, c));
            }
          }
          double width = maximum - minimum;
          for (int y = 0; y < raster.height; y++) {
            for (int x = 0; x < raster.width; x++) {
              result.set(x, y, c, width > 0.0 ? (raster.get(x, y, c) - minimum) / width : 0.0);
            }
          }
        }
        return imageOf(result, image);
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
      double[] inputRange = {0.0, 1.0};
      double[] outputRange = {0.0, 1.0};
      if (ast.argSize() >= 3 && !range(ast.arg3(), inputRange)) {
        return F.NIL;
      }
      if (ast.argSize() >= 4 && !range(ast.arg4(), outputRange)) {
        return F.NIL;
      }
      if (inputRange[1] == inputRange[0]) {
        return F.NIL;
      }
      final double slope = Math.pow(10.0, contrast);
      final double offset = brightness;
      final double exponent = 1.0 / gamma;
      final double inputMin = inputRange[0];
      final double inputWidth = inputRange[1] - inputRange[0];
      final double outputMin = outputRange[0];
      final double outputWidth = outputRange[1] - outputRange[0];
      return imageOf(raster.mapColor(value -> {
        double scaled = (value - inputMin) / inputWidth;
        double adjusted = (scaled - 0.5) * slope + 0.5 + offset;
        if (adjusted < 0.0) {
          adjusted = 0.0;
        }
        return outputMin + outputWidth * Math.pow(adjusted, exponent);
      }), image);
    }

    /**
     * Reads <code>{min, max}</code> into <code>range</code>.
     *
     * @return <code>false</code> if <code>spec</code> is no pair of real numbers
     */
    private static boolean range(IExpr spec, double[] range) {
      if (!spec.isList2()) {
        return false;
      }
      double min = spec.first().evalfNaN();
      double max = spec.second().evalfNaN();
      if (Double.isNaN(min) || Double.isNaN(max)) {
        return false;
      }
      range[0] = min;
      range[1] = max;
      return true;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_4;
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
  private static double threshold(Raster raster, String type, String method, boolean cut) {
    double[] levels = levels(raster, type);
    switch (method) {
      case "Cluster":
      case "Otsu":
      case "Automatic":
        return otsu(levels, type, false, cut);
      case "Entropy":
      case "MinimumError": {
        // on the 0 ... 255 scale
        float[] values = new float[levels.length];
        float low = 0.0f;
        float high = Boof.MAX_VALUE;
        for (int i = 0; i < levels.length; i++) {
          values[i] = (float) (255.0 * levels[i]);
          low = Math.min(low, values[i]);
          high = Math.max(high, values[i]);
        }
        return Enhance.entropy(values, raster.width, raster.height, low, high) / 255.0;
      }
      case "Mean": {
        double total = 0.0;
        for (double level : levels) {
          total += level;
        }
        return total / levels.length;
      }
      case "Median": {
        double[] sorted = levels.clone();
        java.util.Arrays.sort(sorted);
        return sorted[(sorted.length - 1) / 2];
      }
      default:
        return Double.NaN;
    }
  }

  /**
   * The intensities row by row from the top, those of a <code>"Real32"</code> image in single
   * precision.
   */
  static double[] levels(Raster raster, String type) {
    boolean single = Pixels.REAL32.equals(type);
    double[] levels = new double[raster.width * raster.height];
    for (int y = 0; y < raster.height; y++) {
      for (int x = 0; x < raster.width; x++) {
        double level = intensity(raster, x, y);
        levels[y * raster.width + x] = single ? (float) level : level;
      }
    }
    return levels;
  }

  /**
   * Otsu's threshold of the levels: the split of a histogram which separates the two groups best,
   * the first of several equally good ones. The histogram of an integer image has one bin per
   * integer, that of reals 256 bins over their own range, or over <code>0 ... 1</code> for
   * <code>unitRange</code>. The threshold is the start of the bin that ends the darker group, or
   * the middle between two bins if only they are in use; a constant image has the threshold 1.
   *
   * @param cut <code>true</code> for the value to split the samples at, which is the end of the bin
   *        of real levels the threshold names, because that bin still belongs to the darker group;
   *        all levels of a constant image are at it
   */
  static double otsu(double[] levels, String type, boolean unitRange, boolean cut) {
    double low = Double.POSITIVE_INFINITY;
    double high = Double.NEGATIVE_INFINITY;
    for (double level : levels) {
      low = Math.min(low, level);
      high = Math.max(high, level);
    }
    if (!(high > low)) {
      return cut ? low : 1.0;
    }
    boolean integer = Raster.isIntegerType(type);
    double max = Raster.maxOf(type);
    double origin = integer || unitRange ? 0.0 : low;
    int bins = integer ? (int) max + 1 : 256;
    double width = integer ? 1.0 / max : ((unitRange ? 1.0 : high) - origin) / bins;
    double[] histogram = new double[bins];
    for (double level : levels) {
      int bin = integer ? Raster.level(level, max)
          : Math.min(bins - 1, (int) Math.max(0.0, (level - origin) / width));
      histogram[bin]++;
    }
    int used = 0;
    int firstBin = 0;
    int lastBin = 0;
    double moment = 0.0;
    for (int k = 0; k < bins; k++) {
      if (histogram[k] > 0.0) {
        firstBin = used++ == 0 ? k : firstBin;
        lastBin = k;
        moment += k * histogram[k];
      }
    }
    if (used == 2) {
      return origin + (firstBin + lastBin) / 2.0 * width;
    }
    double weightBelow = 0.0;
    double momentBelow = 0.0;
    double best = -1.0;
    int bestBin = 0;
    for (int k = 0; k < bins; k++) {
      weightBelow += histogram[k];
      momentBelow += k * histogram[k];
      double weightAbove = levels.length - weightBelow;
      if (weightBelow == 0.0 || weightAbove == 0.0) {
        continue;
      }
      double difference = momentBelow / weightBelow - (moment - momentBelow) / weightAbove;
      double between = weightBelow * weightAbove * difference * difference;
      if (between > best) {
        best = between;
        bestBin = k;
      }
    }
    return origin + (cut && !integer ? bestBin + 1 : bestBin) * width;
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
  static double intensity(Raster raster, int x, int y) {
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
