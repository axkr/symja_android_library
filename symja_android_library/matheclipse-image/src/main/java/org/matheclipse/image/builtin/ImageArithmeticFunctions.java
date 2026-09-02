package org.matheclipse.image.builtin;

import java.awt.image.BufferedImage;
import java.util.function.DoubleBinaryOperator;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.image.algo.Boof;
import org.matheclipse.image.algo.Pixels;
import org.matheclipse.image.expression.data.ImageExpr;

/**
 * Sample by sample arithmetic: <code>ImageAdd</code>, <code>ImageSubtract</code>,
 * <code>ImageMultiply</code>, <code>ImageDivide</code>, <code>ImageDifference</code> and
 * <code>ImageClip</code>.
 *
 * <p>
 * Everything works on the <code>0.0 ... 1.0</code> scale, so <code>ImageAdd(image, 0.2)</code>
 * brightens by a fifth of full intensity whatever the storage of the image is, and every result is
 * clipped back into that range.
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

    /** Combine two samples, both on the <code>0.0 ... 1.0</code> scale. */
    abstract double apply(double left, double right);

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      BufferedImage image = ImagePropertyFunctions.bufferedImage(ast.arg1());
      if (image == null) {
        return F.NIL;
      }
      IExpr result = ast.arg1();
      // ImageAdd(image, a, b, ...) folds left, the way Plus does
      for (int i = 2; i < ast.size(); i++) {
        image = ImagePropertyFunctions.bufferedImage(result);
        IExpr combined = combine(image, ast.get(i), this::apply);
        if (!combined.isPresent()) {
          return F.NIL;
        }
        result = combined;
      }
      return result;
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
   * The samples of a stored image are already inside <code>0.0 ... 1.0</code>, so the one argument
   * form is the identity and only the two argument form does anything.
   */
  private static class ImageClip extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      BufferedImage image = ImagePropertyFunctions.bufferedImage(ast.arg1());
      if (image == null) {
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
      return map(image, sample -> Math.min(highBound, Math.max(lowBound, sample)));
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
   * Combine <code>image</code> with a number, a colour, a list of channel values or another image.
   *
   * @return {@link F#NIL} when <code>operand</code> is none of those, or when two images differ in
   *         size
   */
  private static IExpr combine(BufferedImage image, IExpr operand, DoubleBinaryOperator operator) {
    int width = image.getWidth();
    int height = image.getHeight();
    int channels = Boof.channels(image);
    // the alpha channel of the left image is carried through, so it is not an operand
    int colorChannels = channels == 4 ? 3 : channels;

    BufferedImage other = ImagePropertyFunctions.bufferedImage(operand);
    if (other != null) {
      if (other.getWidth() != width || other.getHeight() != height) {
        return F.NIL;
      }
      int otherChannels = Boof.channels(other);
      final BufferedImage right = other;
      return imageOf(Pixels.fromPixels(width, height, channels, (x, y) -> {
        float[] left = Pixels.pixel(image, x, y, channels);
        float[] rightPixel = Pixels.pixel(right, x, y, otherChannels);
        float[] result = left.clone();
        for (int c = 0; c < colorChannels; c++) {
          double rightSample = rightPixel[otherChannels == 1 ? 0 : Math.min(c, 2)] / 255.0;
          result[c] = (float) (255.0 * operator.applyAsDouble(left[c] / 255.0, rightSample));
        }
        return result;
      }));
    }

    double[] operands = constantOperand(operand, colorChannels);
    if (operands == null) {
      return F.NIL;
    }
    return imageOf(Pixels.fromPixels(width, height, channels, (x, y) -> {
      float[] left = Pixels.pixel(image, x, y, channels);
      float[] result = left.clone();
      for (int c = 0; c < colorChannels; c++) {
        result[c] = (float) (255.0 * operator.applyAsDouble(left[c] / 255.0, operands[c]));
      }
      return result;
    }));
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

  /** Apply <code>operator</code> to every colour sample, leaving alpha alone. */
  private static IExpr map(BufferedImage image, java.util.function.DoubleUnaryOperator operator) {
    int channels = Boof.channels(image);
    int colorChannels = channels == 4 ? 3 : channels;
    return imageOf(
        Pixels.fromPixels(image.getWidth(), image.getHeight(), channels, (x, y) -> {
          float[] values = Pixels.pixel(image, x, y, channels);
          float[] result = values.clone();
          for (int c = 0; c < colorChannels; c++) {
            result[c] = (float) (255.0 * operator.applyAsDouble(values[c] / 255.0));
          }
          return result;
        }));
  }

  private static IExpr imageOf(BufferedImage image) {
    // no source matrix: see Boof#toImageExpr
    return new ImageExpr(image, null);
  }

  public static void initialize() {
    Initializer.init();
  }

  private ImageArithmeticFunctions() {}
}
