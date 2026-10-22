package org.matheclipse.image.builtin;

import java.util.function.DoubleBinaryOperator;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
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

  /** A number, an <code>RGBColor</code> or a list of channel values, spread over the channels. */
  private static double[] constantOperand(IExpr operand, int channels) {
    IAST values = null;
    if (operand.isAST(S.RGBColor) || operand.isList()) {
      values = (IAST) operand;
    }
    double[] result = new double[channels];
    if (values != null) {
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
    double scalar = operand.evalfNaN();
    if (Double.isNaN(scalar)) {
      return null;
    }
    java.util.Arrays.fill(result, scalar);
    return result;
  }

  public static void initialize() {
    Initializer.init();
  }

  private ImageArithmeticFunctions() {}
}
