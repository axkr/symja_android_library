package org.matheclipse.image.builtin;

import java.awt.image.BufferedImage;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.image.algo.Boof;
import org.matheclipse.image.algo.Enhance;
import org.matheclipse.image.algo.Pixels;
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
      BufferedImage image = ImagePropertyFunctions.bufferedImage(ast.arg1());
      if (image == null) {
        return F.NIL;
      }
      String method = methodOption(ast, 2, engine, "Cluster");
      double threshold = threshold(image, method);
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
   * <code>Binarize(image)</code> - a black and white image, split at
   * <code>FindThreshold(image)</code>. <code>Binarize(image, t)</code> splits at <code>t</code> and
   * <code>Binarize(image, {t1, t2})</code> keeps the samples inside the range.
   */
  private static class Binarize extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      BufferedImage image = ImagePropertyFunctions.bufferedImage(ast.arg1());
      if (image == null) {
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
        low = threshold(image, methodOption(ast, 2, engine, "Cluster"));
        if (Double.isNaN(low)) {
          return F.NIL;
        }
      }

      final double lowBound = low;
      final double highBound = high;
      return new ImageExpr(Pixels.fromPixels(image.getWidth(), image.getHeight(), 1, (x, y) -> {
        float intensity = Boof.intensity(image, x, y);
        boolean inside = intensity > lowBound && intensity <= highBound;
        return new float[] {inside ? 255.0f : 0.0f};
      }), null);
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
      BufferedImage image = ImagePropertyFunctions.bufferedImage(ast.arg1());
      if (image == null) {
        return F.NIL;
      }
      int channels = Boof.channels(image);
      int colorChannels = channels == 4 ? 3 : channels;

      if (ast.argSize() == 1) {
        float low = Float.MAX_VALUE;
        float high = -Float.MAX_VALUE;
        for (int y = 0; y < image.getHeight(); y++) {
          for (int x = 0; x < image.getWidth(); x++) {
            float[] values = Pixels.pixel(image, x, y, channels);
            for (int c = 0; c < colorChannels; c++) {
              low = Math.min(low, values[c]);
              high = Math.max(high, values[c]);
            }
          }
        }
        if (high <= low) {
          return ast.arg1();
        }
        final float minimum = low;
        final float scale = 255.0f / (high - low);
        return map(image, channels, colorChannels, value -> (value - minimum) * scale);
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
      return map(image, channels, colorChannels, value -> {
        double unit = value / 255.0;
        double adjusted = (unit - 0.5) * slope + 0.5 + offset;
        if (adjusted < 0.0) {
          adjusted = 0.0;
        }
        return (float) (255.0 * Math.pow(adjusted, exponent));
      });
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
   * A colour image is equalized channel by channel.
   */
  private static class HistogramTransform extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      BufferedImage image = ImagePropertyFunctions.bufferedImage(ast.arg1());
      if (image == null) {
        return F.NIL;
      }
      int channels = Boof.channels(image);
      int colorChannels = channels == 4 ? 3 : channels;
      int width = image.getWidth();
      int height = image.getHeight();

      int[][] transform = new int[colorChannels][];
      for (int c = 0; c < colorChannels; c++) {
        int[] histogram = new int[256];
        for (int y = 0; y < height; y++) {
          for (int x = 0; x < width; x++) {
            histogram[clamp(Pixels.pixel(image, x, y, channels)[c])]++;
          }
        }
        transform[c] = Enhance.equalizationTable(histogram);
      }

      return new ImageExpr(Pixels.fromPixels(width, height, channels, (x, y) -> {
        float[] values = Pixels.pixel(image, x, y, channels);
        float[] result = values.clone();
        for (int c = 0; c < colorChannels; c++) {
          result[c] = transform[c][clamp(values[c])];
        }
        return result;
      }), null);
    }

    private static int clamp(float value) {
      int rounded = Math.round(value);
      if (rounded < 0) {
        return 0;
      }
      return rounded > 255 ? 255 : rounded;
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

  /** The whole image threshold of the given method, on the 0 ... 255 scale. */
  private static double threshold(BufferedImage image, String method) {
    switch (method) {
      case "Cluster":
      case "Otsu":
      case "Automatic":
        return Enhance.otsu(image);
      case "Entropy":
      case "MinimumError":
        return Enhance.entropy(image);
      case "Mean": {
        double total = 0.0;
        for (int y = 0; y < image.getHeight(); y++) {
          for (int x = 0; x < image.getWidth(); x++) {
            total += Boof.intensity(image, x, y);
          }
        }
        return total / ((long) image.getWidth() * image.getHeight());
      }
      case "Median": {
        int[] histogram = new int[256];
        int count = image.getWidth() * image.getHeight();
        for (int y = 0; y < image.getHeight(); y++) {
          for (int x = 0; x < image.getWidth(); x++) {
            histogram[Math.round(Boof.intensity(image, x, y))]++;
          }
        }
        int seen = 0;
        for (int level = 0; level < histogram.length; level++) {
          seen += histogram[level];
          if (seen * 2 >= count) {
            return level;
          }
        }
        return 127.0;
      }
      default:
        return Double.NaN;
    }
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

  private static IExpr map(BufferedImage image, int channels, int colorChannels,
      java.util.function.DoubleUnaryOperator operator) {
    return new ImageExpr(
        Pixels.fromPixels(image.getWidth(), image.getHeight(), channels, (x, y) -> {
          float[] values = Pixels.pixel(image, x, y, channels);
          float[] result = values.clone();
          for (int c = 0; c < colorChannels; c++) {
            result[c] = (float) operator.applyAsDouble(values[c]);
          }
          return result;
        }), null);
  }

  public static void initialize() {
    Initializer.init();
  }

  private ImageAdjustFunctions() {}
}
