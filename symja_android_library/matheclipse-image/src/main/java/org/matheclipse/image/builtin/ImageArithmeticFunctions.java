package org.matheclipse.image.builtin;

import java.util.function.DoubleBinaryOperator;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.image.algo.Colors;
import org.matheclipse.image.algo.Pixels;
import org.matheclipse.image.algo.Raster;
import org.matheclipse.image.expression.data.ImageExpr;

/**
 * Sample by sample arithmetic: <code>ImageAdd</code>, <code>ImageSubtract</code>,
 * <code>ImageMultiply</code>, <code>ImageDivide</code>, <code>ImageDifference</code> and
 * <code>ImageClip</code>.
 *
 * <p>
 * Everything works on the intensity scale, where <code>1.0</code> is full intensity, so
 * <code>ImageAdd(image, 0.2)</code> brightens by a fifth of it whatever the storage of the image
 * is. The result has the type of the first image: an image of reals keeps samples outside of
 * <code>0.0 ... 1.0</code>, an integer type clips them, and a <code>"Bit"</code> image becomes a
 * <code>"Real32"</code> one.
 *
 * <p>
 * The alpha channel is carried through untouched. Arithmetic on transparency is what
 * <code>SetAlphaChannel</code> is for, and silently making a picture more transparent because
 * something was added to it would be a surprise.
 */
public class ImageArithmeticFunctions {

  private static class Initializer {

    private static void init() {
      S.ImageAdd.setEvaluator(new ImageAdd());
      S.ImageClip.setEvaluator(new ImageClip());
      S.ImageDifference.setEvaluator(new ImageDifference());
      S.ImageDivide.setEvaluator(new ImageDivide());
      S.ImageMultiply.setEvaluator(new ImageMultiply());
      S.ImageSubtract.setEvaluator(new ImageSubtract());
    }
  }

  /** The shared shape of the five binary functions. */
  private abstract static class BinaryImageFunction extends AbstractEvaluator {

    /** Combine two samples, both on the intensity scale. */
    abstract double apply(double left, double right);

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!(ast.arg1() instanceof ImageExpr)) {
        return F.NIL;
      }
      ImageExpr image = (ImageExpr) ast.arg1();
      Raster result = image.raster();
      if (result == null) {
        return F.NIL;
      }
      // ImageAdd(image, a, b, ...) folds left, the way Plus does
      for (int i = 2; i < ast.size(); i++) {
        result = combine(result, ast.get(i), this::apply);
        if (result == null) {
          return F.NIL;
        }
      }
      // the type of the first image: an image of reals keeps the samples outside of 0 ... 1,
      // an integer type clips them; a black and white image becomes one of reals
      String type = Pixels.BIT.equals(image.sampleType()) ? Pixels.REAL32 : image.sampleType();
      ImageExpr combined = ImageExpr.fromRaster(result, image.getOptions(), type);
      return combined == null ? F.NIL : combined;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_INFINITY;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  /** <code>ImageAdd(image, x)</code> - brighten by <code>x</code>, or add another image. */
  private static class ImageAdd extends BinaryImageFunction {
    @Override
    double apply(double left, double right) {
      return left + right;
    }
  }

  /** <code>ImageSubtract(image, x)</code> - darken by <code>x</code>, or subtract another image. */
  private static class ImageSubtract extends BinaryImageFunction {
    @Override
    double apply(double left, double right) {
      return left - right;
    }
  }

  /** <code>ImageMultiply(image, x)</code> - scale the samples, or multiply two images. */
  private static class ImageMultiply extends BinaryImageFunction {
    @Override
    double apply(double left, double right) {
      return left * right;
    }
  }

  /** <code>ImageDivide(image, x)</code> - divide the samples; dividing by zero saturates. */
  private static class ImageDivide extends BinaryImageFunction {
    @Override
    double apply(double left, double right) {
      if (right == 0.0) {
        return left == 0.0 ? 0.0 : Double.POSITIVE_INFINITY;
      }
      return left / right;
    }
  }

  /** <code>ImageDifference(image1, image2)</code> - the absolute difference of the samples. */
  private static class ImageDifference extends BinaryImageFunction {
    @Override
    double apply(double left, double right) {
      return Math.abs(left - right);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   * <code>ImageClip(image)</code> - clip the samples into <code>0.0 ... 1.0</code>, or into
   * <code>{min, max}</code> when a range is given.
   *
   * <p>
   * An image of reals may hold samples outside of <code>0.0 ... 1.0</code>, which the one
   * argument form brings back into it.
   */
  private static class ImageClip extends AbstractEvaluator {

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
      double low = 0.0;
      double high = 1.0;
      if (ast.argSize() >= 2) {
        if (!ast.arg2().isList() || ((IAST) ast.arg2()).argSize() != 2) {
          return F.NIL;
        }
        IAST range = (IAST) ast.arg2();
        low = range.arg1().evalfNaN();
        high = range.arg2().evalfNaN();
        if (Double.isNaN(low) || Double.isNaN(high) || low > high) {
          return F.NIL;
        }
      }
      final double lowBound = low;
      final double highBound = high;
      ImageExpr clipped = ImageExpr.fromRaster(
          raster.mapColor(sample -> Math.min(highBound, Math.max(lowBound, sample))),
          image.getOptions(), image.sampleType());
      return clipped == null ? F.NIL : clipped;
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

  /**
   * <code>Plus</code>, <code>Times</code> and <code>Power</code> of images: a sum or product of
   * images of one size and real numbers, and an image raised to a real power, are computed sample
   * by sample, the alpha channel carried through. An image of one channel combines with every
   * channel of a colour image.
   *
   * <p>
   * The result is an image of reals whose samples are not clipped: <code>"Real32"</code> for a sum
   * or a product unless one of the images is <code>"Real64"</code>, and <code>"Real64"</code> for a
   * power.
   *
   * @param ast <code>Plus(...)</code>, <code>Times(...)</code> or <code>Power(image, x)</code>
   * @return {@link F#NIL} if an operand is neither an image nor a real number, or if two images
   *         differ in size
   */
  public static IExpr arithmetic(IAST ast) {
    DoubleBinaryOperator operator;
    if (ast.isPlus()) {
      operator = (a, b) -> a + b;
    } else if (ast.isTimes()) {
      operator = (a, b) -> a * b;
    } else if (ast.isPower()) {
      if (!(ast.base() instanceof ImageExpr) || !ast.exponent().isReal()) {
        return F.NIL;
      }
      Raster base = ((ImageExpr) ast.base()).raster();
      if (base == null) {
        return F.NIL;
      }
      final double exponent = ast.exponent().evalfNaN();
      ImageExpr result = ImageExpr.fromRaster(
          base.mapColor(sample -> Math.pow(sample, exponent)),
          ((ImageExpr) ast.base()).getOptions(), Pixels.REAL64);
      return result == null ? F.NIL : result;
    } else {
      return F.NIL;
    }
    // the image with the most channels decides the shape of the result; the other operands are
    // spread over its channels
    ImageExpr first = null;
    int firstIndex = 0;
    Raster firstRaster = null;
    boolean real64 = false;
    for (int i = 1; i < ast.size(); i++) {
      IExpr arg = ast.get(i);
      if (arg instanceof ImageExpr) {
        ImageExpr image = (ImageExpr) arg;
        Raster raster = image.raster();
        if (raster == null) {
          return F.NIL;
        }
        if (firstRaster != null
            && (raster.width != firstRaster.width || raster.height != firstRaster.height)) {
          return F.NIL;
        }
        if (firstRaster == null || raster.channels > firstRaster.channels) {
          first = image;
          firstIndex = i;
          firstRaster = raster;
        }
        real64 |= Pixels.REAL64.equals(image.sampleType());
      } else if (!arg.isReal()) {
        return F.NIL;
      }
    }
    if (first == null) {
      return F.NIL;
    }
    Raster result = firstRaster;
    for (int i = 1; i < ast.size(); i++) {
      if (i == firstIndex) {
        continue;
      }
      result = combine(result, ast.get(i), operator);
      if (result == null) {
        return F.NIL;
      }
    }
    ImageExpr combined = ImageExpr.fromRaster(result, first.getOptions(),
        real64 ? Pixels.REAL64 : Pixels.REAL32);
    return combined == null ? F.NIL : combined;
  }

  // -------------------------------------------------------------------- internals

  /**
   * Combine the samples of an image with a number, a colour, a list of channel values or the
   * samples of another image. The alpha channel of the left image is carried through.
   *
   * @return <code>null</code> when <code>operand</code> is none of those, or when two images differ
   *         in size
   */
  private static Raster combine(Raster left, IExpr operand, DoubleBinaryOperator operator) {
    int colorChannels = left.colorChannels();
    Raster result = left.copy();
    if (operand instanceof ImageExpr) {
      Raster right = ((ImageExpr) operand).raster();
      if (right == null || right.width != left.width || right.height != left.height) {
        return null;
      }
      int rightColors = right.colorChannels();
      for (int y = 0; y < left.height; y++) {
        for (int x = 0; x < left.width; x++) {
          for (int c = 0; c < colorChannels; c++) {
            double rightSample = right.get(x, y, rightColors == 1 ? 0 : Math.min(c, rightColors - 1));
            result.set(x, y, c, operator.applyAsDouble(left.get(x, y, c), rightSample));
          }
        }
      }
      return result;
    }
    if (colorChannels == 1 && isColor(operand)) {
      // a colour on a greyscale image makes it a colour image, whatever the colour's own level
      left = toColor(left);
      result = left.copy();
      colorChannels = 3;
    }
    double[] operands = constantOperand(operand, colorChannels);
    if (operands == null) {
      return null;
    }
    for (int y = 0; y < left.height; y++) {
      for (int x = 0; x < left.width; x++) {
        for (int c = 0; c < colorChannels; c++) {
          result.set(x, y, c, operator.applyAsDouble(left.get(x, y, c), operands[c]));
        }
      }
    }
    return result;
  }

  /**
   * Whether <code>operand</code> is a colour directive other than a grey: the operand which turns
   * a greyscale image into a colour image.
   */
  private static boolean isColor(IExpr operand) {
    return operand.isAST() && !operand.isList() && !operand.isAST(S.GrayLevel)
        && Colors.toRgbaDouble(operand) != null;
  }

  /**
   * A greyscale raster as an RGB one, every colour channel the intensity; an alpha channel stays.
   */
  private static Raster toColor(Raster grey) {
    boolean alpha = grey.channels == 2;
    Raster result = new Raster(grey.width, grey.height, alpha ? 4 : 3);
    for (int y = 0; y < grey.height; y++) {
      for (int x = 0; x < grey.width; x++) {
        double intensity = grey.get(x, y, 0);
        result.set(x, y, 0, intensity);
        result.set(x, y, 1, intensity);
        result.set(x, y, 2, intensity);
        if (alpha) {
          result.set(x, y, 3, grey.get(x, y, 1));
        }
      }
    }
    return result;
  }

  /**
   * A number, a colour or a list of channel values, spread over the colour channels: a number is
   * the value of every channel, a list gives each of them, and a colour is converted to the
   * channels of the image. On a greyscale image that is a <code>GrayLevel</code>, as any other
   * colour has turned the image into a colour image first.
   *
   * @return <code>null</code> if <code>operand</code> is none of these, or a list with fewer
   *         entries than there are channels
   */
  private static double[] constantOperand(IExpr operand, int channels) {
    double[] result = new double[channels];
    if (operand.isList()) {
      IAST values = (IAST) operand;
      if (values.argSize() < channels) {
        return null;
      }
      for (int c = 0; c < channels; c++) {
        result[c] = values.get(c + 1).evalfNaN();
        if (Double.isNaN(result[c])) {
          return null;
        }
      }
      return result;
    }
    // a number before the colours: it is exact, where a colour is read through floats
    double scalar = operand.evalfNaN();
    if (!Double.isNaN(scalar)) {
      java.util.Arrays.fill(result, scalar);
      return result;
    }
    double[] rgba = Colors.toRgbaDouble(operand);
    if (rgba == null || channels > 3) {
      return null;
    }
    if (channels == 1) {
      // a grey is its level as written; the weighted sum is only for a colour reaching here
      boolean grey = rgba[0] == rgba[1] && rgba[1] == rgba[2];
      result[0] = grey ? rgba[0] : Colors.grayLevel(rgba[0], rgba[1], rgba[2]);
      return result;
    }
    for (int c = 0; c < channels; c++) {
      result[c] = rgba[c];
    }
    return result;
  }

  public static void initialize() {
    Initializer.init();
  }

  private ImageArithmeticFunctions() {}
}
