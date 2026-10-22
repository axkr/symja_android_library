package org.matheclipse.image.builtin;

import java.awt.image.BufferedImage;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.image.algo.Boof;
import org.matheclipse.image.algo.Pixels;
import org.matheclipse.image.expression.data.ImageExpr;
import org.matheclipse.image.expression.data.Image3DExpr;

/**
 * What an image is, rather than what it looks like: <code>ImageQ</code>,
 * <code>ImageChannels</code>, <code>ImageColorSpace</code>, <code>ImageType</code>,
 * <code>ImageAspectRatio</code>, <code>ImageValue</code>, <code>ImageValuePositions</code> and
 * <code>ImageMeasurements</code>.
 */
public class ImagePropertyFunctions {

  private static class Initializer {

    private static void init() {
      S.ImageAspectRatio.setEvaluator(new ImageAspectRatio());
      S.ImageChannels.setEvaluator(new ImageChannels());
      S.ImageColorSpace.setEvaluator(new ImageColorSpace());
      S.ImageMeasurements.setEvaluator(new ImageMeasurements());
      S.ImageQ.setEvaluator(new ImageQ());
      S.ImageType.setEvaluator(new ImageType());
      S.ImageValue.setEvaluator(new ImageValue());
      S.ImageValuePositions.setEvaluator(new ImageValuePositions());
    }
  }

  /** <code>ImageQ(expr)</code> - whether <code>expr</code> is an image. */
  private static class ImageQ extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.booleSymbol(ast.arg1() instanceof ImageExpr || ast.arg1() instanceof Image3DExpr);
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

  /** <code>ImageChannels(image)</code> - 1 for greyscale, 3 for RGB, 4 for RGB with alpha. */
  private static class ImageChannels extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1() instanceof Image3DExpr) {
        return F.ZZ(((Image3DExpr) ast.arg1()).channels());
      }
      BufferedImage image = bufferedImage(ast.arg1());
      return image == null ? F.NIL : F.ZZ(Boof.channels(image));
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

  /** <code>ImageColorSpace(image)</code> - <code>"Grayscale"</code> or <code>"RGB"</code>. */
  private static class ImageColorSpace extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1() instanceof Image3DExpr) {
        Image3DExpr volume = (Image3DExpr) ast.arg1();
        String stated = volume.getOptions().colorSpaceName();
        return F.stringx(stated != null ? stated : volume.channels() == 1 ? "Grayscale" : "RGB");
      }
      BufferedImage image = bufferedImage(ast.arg1());
      return image == null ? F.NIL : F.stringx(Pixels.colorSpaceOf(image));
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

  /**
   * <code>ImageType(image)</code> - the type of the samples.
   *
   * <p>
   * An image built from a matrix reports the type of that matrix. Everything else is stored as an 8
   * bit bitmap and reports <code>"Byte"</code>.
   */
  private static class ImageType extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1 instanceof Image3DExpr) {
        return F.stringx(((Image3DExpr) arg1).sampleType());
      }
      if (!(arg1 instanceof ImageExpr)) {
        return F.NIL;
      }
      return F.stringx(((ImageExpr) arg1).sampleType());
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

  /** <code>ImageAspectRatio(image)</code> - height over width, exactly. */
  private static class ImageAspectRatio extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      BufferedImage image = bufferedImage(ast.arg1());
      return image == null ? F.NIL : F.QQ(image.getHeight(), image.getWidth());
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
   * <code>ImageValue(image, {x, y})</code> - the sample at a position in image coordinates, where
   * <code>{0, 0}</code> is the bottom left corner and y grows upwards.
   */
  private static class ImageValue extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      BufferedImage image = bufferedImage(ast.arg1());
      if (image == null || !ast.arg2().isList()) {
        return F.NIL;
      }
      IAST position = (IAST) ast.arg2();
      if (position.argSize() != 2) {
        return F.NIL;
      }
      double x = position.arg1().evalfNaN();
      double y = position.arg2().evalfNaN();
      if (Double.isNaN(x) || Double.isNaN(y)) {
        return F.NIL;
      }
      String type = ast.argSize() >= 3 && ast.arg3().isString() //
          ? ast.arg3().toString()
          : Pixels.REAL32;

      int column = clamp((int) Math.floor(x), image.getWidth());
      // image coordinates count y from the bottom, the raster counts rows from the top
      int row = clamp(image.getHeight() - 1 - (int) Math.floor(y), image.getHeight());

      org.matheclipse.image.algo.Raster raster =
          ast.arg1() instanceof ImageExpr ? ((ImageExpr) ast.arg1()).raster() : null;
      if (raster != null) {
        // the sample as it is stored, which an image of reals keeps outside of 0 ... 1
        org.matheclipse.image.algo.Raster one = new org.matheclipse.image.algo.Raster(1, 1,
            raster.channels);
        for (int c = 0; c < raster.channels; c++) {
          one.set(0, 0, c, raster.get(column, row, c));
        }
        IExpr pixel = ((IAST) one.toMatrix(Pixels.imageType(type)).arg1()).arg1();
        return pixel;
      }
      int channels = Boof.channels(image);
      float[] values = Pixels.pixel(image, column, row, channels);
      if (channels == 1) {
        return Pixels.sample(Math.round(values[0]), type);
      }
      IASTAppendable result = F.ListAlloc(channels);
      for (float value : values) {
        result.append(Pixels.sample(Math.round(value), type));
      }
      return result;
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
   * <code>ImageValuePositions(image, value)</code> - the positions whose sample equals
   * <code>value</code>, in the same image coordinates {@link ImageValue} uses, at the centre of
   * each pixel.
   */
  private static class ImageValuePositions extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!(ast.arg1() instanceof ImageExpr)) {
        return F.NIL;
      }
      ImageExpr imageExpr = (ImageExpr) ast.arg1();
      org.matheclipse.image.algo.Raster image = imageExpr.raster();
      if (image == null) {
        return F.NIL;
      }
      int channels = image.channels;
      double[] wanted = wantedValue(ast.arg2(), channels);
      if (wanted == null) {
        return F.NIL;
      }
      double tolerance = ast.argSize() >= 3 ? ast.arg3().evalfNaN() : 0.0;
      if (Double.isNaN(tolerance)) {
        return F.NIL;
      }
      // the samples of a picture without its own matrix are levels of 8 bits, which are compared
      // as such: a value matches the levels within half a level of it
      boolean levels = imageExpr.getMatrix() == null;
      double slack = levels ? tolerance * 255.0 + 0.5 : tolerance + 1.0e-12;

      int height = image.height;
      IASTAppendable result = F.ListAlloc(16);
      for (int row = 0; row < height; row++) {
        for (int column = 0; column < image.width; column++) {
          if (matches(image, column, row, wanted, slack, levels)) {
            result.append(F.List(F.num(column + 0.5), F.num(height - row - 0.5)));
          }
        }
      }
      return result;
    }

    /** The sample value looked for in each channel, on the intensity scale. */
    private static double[] wantedValue(IExpr value, int channels) {
      if (value.isList()) {
        IAST list = (IAST) value;
        if (list.argSize() != channels) {
          return null;
        }
        double[] wanted = new double[channels];
        for (int i = 0; i < channels; i++) {
          wanted[i] = list.get(i + 1).evalfNaN();
          if (Double.isNaN(wanted[i])) {
            return null;
          }
        }
        return wanted;
      }
      double scalar = value.evalfNaN();
      if (Double.isNaN(scalar)) {
        return null;
      }
      double[] wanted = new double[channels];
      java.util.Arrays.fill(wanted, scalar);
      return wanted;
    }

    private static boolean matches(org.matheclipse.image.algo.Raster image, int x, int y,
        double[] wanted, double slack, boolean levels) {
      for (int c = 0; c < wanted.length; c++) {
        double difference = levels //
            ? Math.abs(Math.round(255.0 * image.get(x, y, c)) - 255.0 * wanted[c])
            : Math.abs(image.get(x, y, c) - wanted[c]);
        if (difference > slack) {
          return false;
        }
      }
      return true;
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
   * <code>ImageMeasurements(image, property)</code> - a statistic of the samples, on the
   * <code>0.0 ... 1.0</code> scale.
   *
   * <p>
   * A greyscale image gives one number per property, a colour image one per channel. An unknown
   * property name leaves the expression unevaluated rather than guessing.
   */
  private static class ImageMeasurements extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      BufferedImage image = bufferedImage(ast.arg1());
      if (image == null) {
        return F.NIL;
      }
      // the samples as they are, which an image of reals keeps beyond 0 ... 1
      org.matheclipse.image.algo.Raster raster = ast.arg1() instanceof ImageExpr
          ? ((ImageExpr) ast.arg1()).raster()
          : org.matheclipse.image.algo.Raster.fromBitmap(image);
      if (raster == null) {
        return F.NIL;
      }
      IExpr property = ast.arg2();
      if (property.isList()) {
        IAST properties = (IAST) property;
        IASTAppendable result = F.ListAlloc(properties.argSize());
        for (int i = 1; i < properties.size(); i++) {
          IExpr measurement = measure(raster, properties.get(i));
          if (!measurement.isPresent()) {
            return F.NIL;
          }
          result.append(measurement);
        }
        return result;
      }
      return measure(raster, property);
    }

    private static IExpr measure(org.matheclipse.image.algo.Raster raster, IExpr property) {
      if (!property.isString()) {
        return F.NIL;
      }
      String name = property.toString();
      int width = raster.width;
      int height = raster.height;
      if ("Dimensions".equals(name)) {
        return F.List(F.ZZ(width), F.ZZ(height));
      }
      if ("Area".equals(name)) {
        return F.ZZ((long) width * height);
      }

      int channels = raster.channels;
      double[][] samples = new double[channels][width * height];
      int index = 0;
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          for (int c = 0; c < channels; c++) {
            samples[c][index] = raster.get(x, y, c);
          }
          index++;
        }
      }

      IExpr[] perChannel = new IExpr[channels];
      for (int c = 0; c < channels; c++) {
        IExpr value = statistic(name, samples[c]);
        if (!value.isPresent()) {
          return F.NIL;
        }
        perChannel[c] = value;
      }
      return channels == 1 ? perChannel[0] : F.List(perChannel);
    }

    private static IExpr statistic(String name, double[] values) {
      switch (name) {
        case "Total":
          return F.num(total(values));
        case "Mean":
          return F.num(total(values) / values.length);
        case "Min":
          return F.num(min(values));
        case "Max":
          return F.num(max(values));
        case "MinMax":
          return F.List(F.num(min(values)), F.num(max(values)));
        case "Range":
          return F.num(max(values) - min(values));
        case "Median":
          return F.num(median(values));
        case "Variance":
          return F.num(variance(values));
        case "StandardDeviation":
          return F.num(Math.sqrt(variance(values)));
        case "Entropy":
          return F.num(entropy(values));
        case "Count":
          return F.ZZ(values.length);
        default:
          return F.NIL;
      }
    }

    private static double total(double[] values) {
      double sum = 0.0;
      for (double value : values) {
        sum += value;
      }
      return sum;
    }

    private static double min(double[] values) {
      double result = Double.POSITIVE_INFINITY;
      for (double value : values) {
        result = Math.min(result, value);
      }
      return result;
    }

    private static double max(double[] values) {
      double result = Double.NEGATIVE_INFINITY;
      for (double value : values) {
        result = Math.max(result, value);
      }
      return result;
    }

    private static double median(double[] values) {
      double[] sorted = values.clone();
      java.util.Arrays.sort(sorted);
      int middle = sorted.length / 2;
      return sorted.length % 2 == 1 //
          ? sorted[middle]
          : (sorted[middle - 1] + sorted[middle]) / 2.0;
    }

    private static double variance(double[] values) {
      if (values.length < 2) {
        return 0.0;
      }
      double mean = total(values) / values.length;
      double sum = 0.0;
      for (double value : values) {
        double difference = value - mean;
        sum += difference * difference;
      }
      // the unbiased estimator, the way Variance reports it
      return sum / (values.length - 1);
    }

    /**
     * Shannon entropy in nats over the 256 sample levels, the way <code>Entropy</code> reports it.
     */
    private static double entropy(double[] values) {
      int[] histogram = new int[256];
      for (double value : values) {
        int level = (int) Math.round(value * 255.0);
        histogram[level < 0 ? 0 : (level > 255 ? 255 : level)]++;
      }
      double result = 0.0;
      for (int count : histogram) {
        if (count > 0) {
          double probability = (double) count / values.length;
          result -= probability * Math.log(probability);
        }
      }
      return result;
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

  private static int clamp(int value, int size) {
    if (value < 0) {
      return 0;
    }
    return value >= size ? size - 1 : value;
  }

  /** The bitmap of <code>expr</code>, or <code>null</code> when it is not an image. */
  static BufferedImage bufferedImage(IExpr expr) {
    return expr instanceof ImageExpr ? ((ImageExpr) expr).getBufferedImage() : null;
  }

  public static void initialize() {
    Initializer.init();
  }

  private ImagePropertyFunctions() {}
}
