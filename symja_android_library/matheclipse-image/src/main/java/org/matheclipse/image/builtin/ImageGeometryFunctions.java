package org.matheclipse.image.builtin;

import java.awt.image.BufferedImage;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.image.algo.Boof;
import org.matheclipse.image.algo.Colors;
import org.matheclipse.image.algo.Geometry;
import org.matheclipse.image.algo.Pixels;
import org.matheclipse.image.expression.data.ImageExpr;

/**
 * Where the pixels are rather than what colour they are: <code>ImageResize</code>,
 * <code>ImageRotate</code>, <code>ImageReflect</code>, <code>ImagePad</code>,
 * <code>ImageTrim</code>, <code>ImageTake</code>, <code>ImageCompose</code>,
 * <code>Thumbnail</code>, <code>Rasterize</code> and the three transformation functions.
 *
 * <p>
 * <b>Two coordinate systems appear here</b>. <code>ImageTake</code> counts rows and columns of the
 * pixel matrix, from the top, one based, the way <code>Take</code> does. Everything else -
 * <code>ImageTrim</code>, <code>ImageCompose</code> and the transformations - uses image
 * coordinates, where <code>{0, 0}</code> is the bottom left corner and y grows upwards.
 */
public class ImageGeometryFunctions {

  /** The largest dimension of <code>Thumbnail(image)</code> without an explicit size. */
  private static final int DEFAULT_THUMBNAIL_SIZE = 48;

  private static class Initializer {

    private static void init() {
      S.ImageCompose.setEvaluator(new ImageCompose());
      S.ImageForwardTransformation.setEvaluator(new ImageForwardTransformation());
      S.ImagePad.setEvaluator(new ImagePad());
      S.ImagePerspectiveTransformation.setEvaluator(new ImagePerspectiveTransformation());
      S.ImageReflect.setEvaluator(new ImageReflect());
      S.ImageResize.setEvaluator(new ImageResize());
      S.ImageRotate.setEvaluator(new ImageRotate());
      S.ImageTake.setEvaluator(new ImageTake());
      S.ImageTransformation.setEvaluator(new ImageTransformation());
      S.ImageTrim.setEvaluator(new ImageTrim());
      S.Rasterize.setEvaluator(new Rasterize());
      S.Thumbnail.setEvaluator(new Thumbnail());
    }
  }

  /**
   * <code>ImageResize(image, w)</code> - the image scaled to width <code>w</code>, keeping its
   * aspect ratio. <code>{w, h}</code> gives both, with <code>Automatic</code> for the one that
   * should follow from the other, and <code>Scaled(s)</code> scales by a factor.
   */
  private static class ImageResize extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      BufferedImage image = ImagePropertyFunctions.bufferedImage(ast.arg1());
      if (image == null) {
        return F.NIL;
      }
      int[] size = targetSize(ast.arg2(), image.getWidth(), image.getHeight());
      if (size == null) {
        return F.NIL;
      }
      boolean smooth = !"Nearest".equals(resamplingOption(ast, 3, engine));
      return new ImageExpr(Geometry.resize(image, size[0], size[1], smooth), null);
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
   * <code>ImageRotate(image)</code> - the image turned a quarter turn counterclockwise.
   * <code>ImageRotate(image, theta)</code> turns it by <code>theta</code> radians, growing the
   * canvas so that nothing is cut off, and a third argument fixes the output size instead.
   */
  private static class ImageRotate extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      BufferedImage image = ImagePropertyFunctions.bufferedImage(ast.arg1());
      if (image == null) {
        return F.NIL;
      }
      if (ast.argSize() == 1) {
        return new ImageExpr(Geometry.rotateQuarters(image, 1), null);
      }
      double radians = ast.arg2().evalfNaN();
      if (Double.isNaN(radians)) {
        return F.NIL;
      }
      // an exact quarter turn is a transposition, and doing it by resampling would blur it
      double quarters = radians / (Math.PI / 2.0);
      if (ast.argSize() == 2 && Math.abs(quarters - Math.rint(quarters)) < 1e-12) {
        return new ImageExpr(Geometry.rotateQuarters(image, (int) Math.rint(quarters)), null);
      }
      int width = -1;
      int height = -1;
      if (ast.argSize() >= 3) {
        int[] size = targetSize(ast.arg3(), image.getWidth(), image.getHeight());
        if (size == null) {
          return F.NIL;
        }
        width = size[0];
        height = size[1];
      }
      return new ImageExpr(
          Geometry.rotate(image, radians, width, height, Geometry.transparentOrWhite()), null);
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
   * <code>ImageReflect(image)</code> - the image mirrored top to bottom.
   * <code>ImageReflect(image, side)</code> mirrors it onto the given side, and
   * <code>ImageReflect(image, side1 -&gt; side2)</code> reflects about the diagonal between them.
   */
  private static class ImageReflect extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      BufferedImage image = ImagePropertyFunctions.bufferedImage(ast.arg1());
      if (image == null) {
        return F.NIL;
      }
      if (ast.argSize() == 1) {
        return new ImageExpr(Geometry.flip(image, false, true), null);
      }
      IExpr side = ast.arg2();
      if (side.isRuleAST()) {
        IAST rule = (IAST) side;
        IExpr from = rule.arg1();
        IExpr to = rule.arg2();
        if (isVertical(from) && isVertical(to)) {
          return new ImageExpr(Geometry.flip(image, false, from != to), null);
        }
        if (isHorizontal(from) && isHorizontal(to)) {
          return new ImageExpr(Geometry.flip(image, from != to, false), null);
        }
        // Top -> Left is the main diagonal, Top -> Right the other one
        boolean antiDiagonal =
            (from == S.Top && to == S.Right) || (from == S.Bottom && to == S.Left)
                || (from == S.Left && to == S.Bottom) || (from == S.Right && to == S.Top);
        return new ImageExpr(Geometry.transpose(image, antiDiagonal), null);
      }
      if (isVertical(side)) {
        return new ImageExpr(Geometry.flip(image, false, true), null);
      }
      if (isHorizontal(side)) {
        return new ImageExpr(Geometry.flip(image, true, false), null);
      }
      return F.NIL;
    }

    private static boolean isVertical(IExpr side) {
      return side == S.Top || side == S.Bottom;
    }

    private static boolean isHorizontal(IExpr side) {
      return side == S.Left || side == S.Right;
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
   * <code>ImagePad(image, m)</code> - the image with <code>m</code> pixels added on every side.
   * <code>{left, right}</code> pads horizontally, <code>{{left, right}, {bottom, top}}</code> gives
   * all four, and a negative amount trims instead. A second argument is the colour to pad with.
   */
  private static class ImagePad extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      BufferedImage image = ImagePropertyFunctions.bufferedImage(ast.arg1());
      if (image == null) {
        return F.NIL;
      }
      int[] margins = margins(ast.arg2());
      if (margins == null) {
        return F.NIL;
      }
      float[] padding = null;
      if (ast.argSize() >= 3) {
        padding = Colors.toRgba(ast.arg3());
        if (padding == null) {
          return F.NIL;
        }
      }
      int left = margins[0];
      int right = margins[1];
      int bottom = margins[2];
      int top = margins[3];
      int width = image.getWidth() + left + right;
      int height = image.getHeight() + top + bottom;
      if (width <= 0 || height <= 0) {
        return F.NIL;
      }
      int channels = Boof.channels(image);
      // without an explicit colour the new pixels are black, or transparent where the image has an
      // alpha channel to be transparent in
      Geometry.Background background = padding == null //
          ? Geometry.transparentOrBlack()
          : Geometry.color(padding);
      return new ImageExpr(Pixels.fromPixels(width, height, channels, (x, y) -> {
        int sourceX = x - left;
        int sourceY = y - top;
        if (sourceX < 0 || sourceY < 0 || sourceX >= image.getWidth()
            || sourceY >= image.getHeight()) {
          return background.outside(channels);
        }
        return Pixels.pixel(image, sourceX, sourceY, channels);
      }), null);
    }

    /** <code>{left, right, bottom, top}</code> from the several shapes the argument may have. */
    private static int[] margins(IExpr spec) {
      if (spec.isList()) {
        IAST list = (IAST) spec;
        if (list.argSize() == 2 && list.arg1().isList() && list.arg2().isList()) {
          IAST horizontal = (IAST) list.arg1();
          IAST vertical = (IAST) list.arg2();
          if (horizontal.argSize() != 2 || vertical.argSize() != 2) {
            return null;
          }
          return integers(horizontal.arg1(), horizontal.arg2(), vertical.arg1(), vertical.arg2());
        }
        if (list.argSize() == 2) {
          return integers(list.arg1(), list.arg2(), list.arg1(), list.arg2());
        }
        return null;
      }
      return integers(spec, spec, spec, spec);
    }

    private static int[] integers(IExpr... values) {
      int[] result = new int[values.length];
      for (int i = 0; i < values.length; i++) {
        result[i] = values[i].toIntDefault();
        if (result[i] == Config.INVALID_INT) {
          return null;
        }
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
   * <code>ImageTrim(image, {{x1, y1}, {x2, y2}})</code> - the rectangle of the image between two
   * corners, given in image coordinates.
   */
  private static class ImageTrim extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      BufferedImage image = ImagePropertyFunctions.bufferedImage(ast.arg1());
      if (image == null || !ast.arg2().isList()) {
        return F.NIL;
      }
      IAST corners = (IAST) ast.arg2();
      if (corners.argSize() != 2 || !corners.arg1().isList() || !corners.arg2().isList()) {
        return F.NIL;
      }
      IAST first = (IAST) corners.arg1();
      IAST second = (IAST) corners.arg2();
      if (first.argSize() != 2 || second.argSize() != 2) {
        return F.NIL;
      }
      double x1 = first.arg1().evalfNaN();
      double y1 = first.arg2().evalfNaN();
      double x2 = second.arg1().evalfNaN();
      double y2 = second.arg2().evalfNaN();
      if (Double.isNaN(x1) || Double.isNaN(y1) || Double.isNaN(x2) || Double.isNaN(y2)) {
        return F.NIL;
      }
      int height = image.getHeight();
      int left = clamp((int) Math.floor(Math.min(x1, x2)), image.getWidth());
      int right = clamp((int) Math.ceil(Math.max(x1, x2)) - 1, image.getWidth());
      // image coordinates count y from the bottom
      int top = clamp(height - (int) Math.ceil(Math.max(y1, y2)), height);
      int bottom = clamp(height - (int) Math.floor(Math.min(y1, y2)) - 1, height);
      if (right < left || bottom < top) {
        return F.NIL;
      }
      final int originX = left;
      final int originY = top;
      int channels = Boof.channels(image);
      return new ImageExpr(Pixels.fromPixels(right - left + 1, bottom - top + 1, channels,
          (x, y) -> Pixels.pixel(image, originX + x, originY + y, channels)), null);
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
   * <code>ImageTake(image, rows)</code> - the given rows of the pixel matrix, counted from the top
   * the way <code>Take</code> counts them. <code>ImageTake(image, rows, columns)</code> takes both.
   */
  private static class ImageTake extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      BufferedImage image = ImagePropertyFunctions.bufferedImage(ast.arg1());
      if (image == null) {
        return F.NIL;
      }
      int[] rows = range(ast.arg2(), image.getHeight());
      if (rows == null) {
        return F.NIL;
      }
      int[] columns = ast.argSize() >= 3 //
          ? range(ast.arg3(), image.getWidth())
          : new int[] {0, image.getWidth() - 1};
      if (columns == null) {
        return F.NIL;
      }
      final int originX = columns[0];
      final int originY = rows[0];
      int channels = Boof.channels(image);
      return new ImageExpr(Pixels.fromPixels(columns[1] - columns[0] + 1, rows[1] - rows[0] + 1,
          channels, (x, y) -> Pixels.pixel(image, originX + x, originY + y, channels)), null);
    }

    /** A <code>Take</code> style specification as zero based inclusive bounds. */
    private static int[] range(IExpr spec, int size) {
      int from;
      int to;
      if (spec.isList()) {
        IAST list = (IAST) spec;
        if (list.argSize() != 2) {
          return null;
        }
        from = list.arg1().toIntDefault();
        to = list.arg2().toIntDefault();
      } else {
        int count = spec.toIntDefault();
        if (count == Config.INVALID_INT) {
          return null;
        }
        from = count >= 0 ? 1 : count;
        to = count >= 0 ? count : -1;
      }
      if (from == Config.INVALID_INT || to == Config.INVALID_INT) {
        return null;
      }
      if (from < 0) {
        from = size + from + 1;
      }
      if (to < 0) {
        to = size + to + 1;
      }
      if (from < 1 || to > size || to < from) {
        return null;
      }
      return new int[] {from - 1, to - 1};
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
   * <code>ImageCompose(background, overlay)</code> - the overlay drawn on the background, centred.
   * A third argument places the centre of the overlay at a position in image coordinates, and
   * <code>ImageCompose(background, {overlay, alpha})</code> makes the overlay partly transparent.
   */
  private static class ImageCompose extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      BufferedImage background = ImagePropertyFunctions.bufferedImage(ast.arg1());
      if (background == null) {
        return F.NIL;
      }
      IExpr overlayArgument = ast.arg2();
      double opacity = 1.0;
      if (overlayArgument.isList() && ((IAST) overlayArgument).argSize() == 2) {
        IAST pair = (IAST) overlayArgument;
        opacity = pair.arg2().evalfNaN();
        overlayArgument = pair.arg1();
        if (Double.isNaN(opacity)) {
          return F.NIL;
        }
      }
      BufferedImage overlay = ImagePropertyFunctions.bufferedImage(overlayArgument);
      if (overlay == null) {
        return F.NIL;
      }

      int backgroundHeight = background.getHeight();
      // the centre of the overlay lands here, in image coordinates
      double centreX = background.getWidth() / 2.0;
      double centreY = backgroundHeight / 2.0;
      if (ast.argSize() >= 3) {
        if (!ast.arg3().isList() || ((IAST) ast.arg3()).argSize() != 2) {
          return F.NIL;
        }
        IAST position = (IAST) ast.arg3();
        centreX = position.arg1().evalfNaN();
        centreY = position.arg2().evalfNaN();
        if (Double.isNaN(centreX) || Double.isNaN(centreY)) {
          return F.NIL;
        }
      }
      // to raster coordinates: the top left corner of the overlay
      final int offsetX = (int) Math.round(centreX - overlay.getWidth() / 2.0);
      final int offsetY = (int) Math.round(backgroundHeight - centreY - overlay.getHeight() / 2.0);

      int backgroundChannels = Boof.channels(background);
      int overlayChannels = Boof.channels(overlay);
      int channels = Math.max(backgroundChannels, Math.min(overlayChannels, 3));
      final double alpha = opacity;
      return new ImageExpr(
          Pixels.fromPixels(background.getWidth(), backgroundHeight, channels, (x, y) -> {
            float[] under = spread(Pixels.pixel(background, x, y, backgroundChannels),
                backgroundChannels, channels);
            int overlayX = x - offsetX;
            int overlayY = y - offsetY;
            if (overlayX < 0 || overlayY < 0 || overlayX >= overlay.getWidth()
                || overlayY >= overlay.getHeight()) {
              return under;
            }
            float[] over = Pixels.pixel(overlay, overlayX, overlayY, overlayChannels);
            double weight = alpha * (overlayChannels == 4 ? over[3] / 255.0 : 1.0);
            float[] result = under.clone();
            float[] spreadOver = spread(over, overlayChannels, channels);
            for (int c = 0; c < Math.min(3, channels); c++) {
              result[c] = (float) (spreadOver[c] * weight + under[c] * (1.0 - weight));
            }
            return result;
          }), null);
    }

    /** Widen a greyscale pixel to the channel count of the composed image. */
    private static float[] spread(float[] values, int from, int to) {
      if (from == to) {
        return values.clone();
      }
      float[] result = new float[to];
      for (int c = 0; c < Math.min(3, to); c++) {
        result[c] = from == 1 ? values[0] : values[Math.min(c, from - 1)];
      }
      if (to == 4) {
        result[3] = from == 4 ? values[3] : 255.0f;
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
   * <code>Thumbnail(image)</code> - a small copy of the image whose largest dimension is
   * {@value #DEFAULT_THUMBNAIL_SIZE} pixels, or the size given as a second argument.
   */
  private static class Thumbnail extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      BufferedImage image = ImagePropertyFunctions.bufferedImage(ast.arg1());
      if (image == null) {
        return F.NIL;
      }
      int size = DEFAULT_THUMBNAIL_SIZE;
      if (ast.argSize() >= 2) {
        size = ast.arg2().toIntDefault();
        if (size < 1) {
          return F.NIL;
        }
      }
      int width = image.getWidth();
      int height = image.getHeight();
      double scale = (double) size / Math.max(width, height);
      int targetWidth = Math.max(1, (int) Math.round(width * scale));
      int targetHeight = Math.max(1, (int) Math.round(height * scale));
      return new ImageExpr(Geometry.resize(image, targetWidth, targetHeight, true), null);
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
   * <code>Rasterize(graphics)</code> - a graphics object drawn into an image. An image rasterizes
   * to itself.
   */
  private static class Rasterize extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1 instanceof ImageExpr) {
        return arg1;
      }
      if (arg1.isAST() && (arg1.isGraphicsObject() || arg1.isAST(S.Graphics3D))) {
        ImageExpr image = ImageExpr.toImageExpr((IAST) arg1);
        if (image != null) {
          return image;
        }
      }
      return F.NIL;
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
   * <code>ImageTransformation(image, f)</code> - the image in which the pixel at <code>p</code> is
   * taken from the position <code>f(p)</code> of the original, both in image coordinates.
   *
   * <p>
   * <code>f</code> may be a function or a <code>TransformationFunction</code>.
   */
  private static class ImageTransformation extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return transform(ast, engine, false);
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
   * <code>ImageForwardTransformation(image, m)</code> - the image with the transformation applied
   * forwards, so that the pixel at <code>p</code> moves to <code>m(p)</code>.
   *
   * <p>
   * Only a matrix or a <code>TransformationFunction</code> is accepted, because running a general
   * function forwards would leave holes wherever no input pixel lands. Use
   * <code>ImageTransformation</code> with the inverse function for those.
   */
  private static class ImageForwardTransformation extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return transform(ast, engine, true);
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
   * <code>ImagePerspectiveTransformation(image, m)</code> - the image with the homogeneous 3x3
   * matrix <code>m</code> applied forwards, which is the transformation a change of viewpoint
   * makes. A 2x2 matrix is a linear map and a 2x3 one an affine map.
   */
  private static class ImagePerspectiveTransformation extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return transform(ast, engine, true);
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

  // -------------------------------------------------------------------- internals

  /**
   * The shared body of the three transformation functions.
   *
   * @param forward whether the argument maps input positions to output positions, which means the
   *        image is resampled through its inverse
   */
  private static IExpr transform(IAST ast, EvalEngine engine, boolean forward) {
    BufferedImage image = ImagePropertyFunctions.bufferedImage(ast.arg1());
    if (image == null) {
      return F.NIL;
    }
    int width = image.getWidth();
    int height = image.getHeight();
    if (ast.argSize() >= 3) {
      int[] size = targetSize(ast.arg3(), width, height);
      if (size == null) {
        return F.NIL;
      }
      width = size[0];
      height = size[1];
    }

    double[][] matrix = homogeneous(ast.arg2());
    IExpr function = matrix == null ? ast.arg2() : null;
    if (matrix != null && forward) {
      matrix = Geometry.invert(matrix);
      if (matrix == null) {
        return F.NIL;
      }
    } else if (matrix == null && forward) {
      // a general function cannot be run forwards without leaving holes
      return F.NIL;
    }

    int channels = Boof.channels(image);
    int sourceHeight = image.getHeight();
    Geometry.Background background = Geometry.transparentOrWhite();
    final double[][] backward = matrix;
    final int outputHeight = height;
    return new ImageExpr(Pixels.fromPixels(width, height, channels, (x, y) -> {
      // image coordinates: y counts from the bottom
      double imageX = x + 0.5;
      double imageY = outputHeight - y - 0.5;
      double[] source;
      if (backward != null) {
        source = Geometry.apply(backward, imageX, imageY);
      } else {
        IExpr position =
            engine.evaluate(F.unaryAST1(function, F.List(F.num(imageX), F.num(imageY))));
        if (!position.isList() || ((IAST) position).argSize() != 2) {
          return background.outside(channels);
        }
        double sourceX = ((IAST) position).arg1().evalfNaN();
        double sourceY = ((IAST) position).arg2().evalfNaN();
        source = Double.isNaN(sourceX) || Double.isNaN(sourceY) //
            ? null
            : new double[] {sourceX, sourceY};
      }
      if (source == null) {
        return background.outside(channels);
      }
      return Geometry.sample(image, channels, source[0] - 0.5, sourceHeight - source[1] - 0.5, true,
          background);
    }), null);
  }

  /**
   * A 2x2, 2x3 or 3x3 matrix, or a <code>TransformationFunction</code> of one, as a homogeneous 3x3
   * matrix.
   *
   * @return <code>null</code> if <code>expr</code> is not one of those
   */
  private static double[][] homogeneous(IExpr expr) {
    IExpr candidate = expr.isAST(S.TransformationFunction, 2) ? ((IAST) expr).arg1() : expr;
    int[] dimensions = candidate.isMatrix();
    if (dimensions == null || dimensions[0] < 2 || dimensions[0] > 3 || dimensions[1] < 2
        || dimensions[1] > 3) {
      return null;
    }
    IAST rows = (IAST) candidate;
    double[][] matrix = new double[][] {{1.0, 0.0, 0.0}, {0.0, 1.0, 0.0}, {0.0, 0.0, 1.0}};
    for (int i = 0; i < dimensions[0]; i++) {
      IAST row = (IAST) rows.get(i + 1);
      for (int j = 0; j < dimensions[1]; j++) {
        double value = row.get(j + 1).evalfNaN();
        if (Double.isNaN(value)) {
          return null;
        }
        matrix[i][j] = value;
      }
    }
    return matrix;
  }

  /**
   * A size given as <code>w</code>, <code>{w, h}</code> with Automatic, or <code>Scaled(s)</code>.
   */
  private static int[] targetSize(IExpr spec, int width, int height) {
    if (spec.isAST(S.Scaled, 2)) {
      double factor = ((IAST) spec).arg1().evalfNaN();
      if (Double.isNaN(factor) || factor <= 0.0) {
        return null;
      }
      return new int[] {Math.max(1, (int) Math.round(width * factor)),
          Math.max(1, (int) Math.round(height * factor))};
    }
    if (spec.isList()) {
      IAST list = (IAST) spec;
      if (list.argSize() != 2) {
        return null;
      }
      int targetWidth = dimension(list.arg1());
      int targetHeight = dimension(list.arg2());
      if (targetWidth == Config.INVALID_INT || targetHeight == Config.INVALID_INT) {
        return null;
      }
      if (targetWidth < 0 && targetHeight < 0) {
        return null;
      }
      if (targetWidth < 0) {
        targetWidth = Math.max(1, (int) Math.round(width * (double) targetHeight / height));
      }
      if (targetHeight < 0) {
        targetHeight = Math.max(1, (int) Math.round(height * (double) targetWidth / width));
      }
      return new int[] {targetWidth, targetHeight};
    }
    int targetWidth = spec.toIntDefault();
    if (targetWidth < 1) {
      return null;
    }
    return new int[] {targetWidth,
        Math.max(1, (int) Math.round(height * (double) targetWidth / width))};
  }

  /**
   * One entry of a size specification; <code>-1</code> for <code>Automatic</code> or
   * <code>All</code>.
   */
  private static int dimension(IExpr expr) {
    if (expr == S.Automatic || expr == S.All) {
      return -1;
    }
    int value = expr.toIntDefault();
    return value < 1 ? Config.INVALID_INT : value;
  }

  /** The <code>Resampling</code> or <code>Interpolation</code> option, as a method name. */
  private static String resamplingOption(IAST ast, int startIndex, EvalEngine engine) {
    if (ast.size() <= startIndex) {
      return "Automatic";
    }
    OptionArgs options = new OptionArgs(ast.topHead(), ast, startIndex, engine);
    IExpr resampling = options.getOption(S.Resampling);
    if (!resampling.isPresent()) {
      resampling = options.getOption(S.Interpolation);
    }
    return resampling.isString() ? resampling.toString() : "Automatic";
  }

  private static int clamp(int value, int size) {
    if (value < 0) {
      return 0;
    }
    return value >= size ? size - 1 : value;
  }

  public static void initialize() {
    Initializer.init();
  }

  private ImageGeometryFunctions() {}
}
