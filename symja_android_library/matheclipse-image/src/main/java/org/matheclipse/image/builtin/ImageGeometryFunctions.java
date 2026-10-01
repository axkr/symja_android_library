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
import org.matheclipse.core.interfaces.IASTAppendable;
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
      String resampling = resamplingOption(ast, 3, engine);
      // an enlargement by whole factors replicates the pixels unless a resampling is asked
      // for - ImageResize(Image({{1,2,3},{4,5,6}}/10.), {6,4}) has every pixel four times
      boolean wholeFactors = "Automatic".equals(resampling) && size[0] >= image.getWidth()
          && size[1] >= image.getHeight() && size[0] % image.getWidth() == 0
          && size[1] % image.getHeight() == 0;
      if (wholeFactors || "Nearest".equals(resampling)) {
        return nearest(ast.arg1(), image, size[0], size[1]);
      }
      if (size[0] <= image.getWidth() && size[1] <= image.getHeight()) {
        IExpr averaged = averaged(ast.arg1(), size[0], size[1], engine);
        if (averaged.isPresent()) {
          return averaged;
        }
      }
      return new ImageExpr(Geometry.resize(image, size[0], size[1], true), null);
    }

    /**
     * A smaller image of real samples: every pixel is the mean of the block of pixels behind it,
     * like {@link Geometry#resize} does it for the bitmap, but of the samples themselves and
     * without rounding the result to <code>1/255</code>.
     *
     * @return <code>F.NIL</code> if the image has no real samples
     */
    private static IExpr averaged(IExpr arg, int width, int height, EvalEngine engine) {
      if (!(arg instanceof ImageExpr) || PixelMatrix.of(arg) == null) {
        return F.NIL;
      }
      ImageExpr source = (ImageExpr) arg;
      String type = source.sampleType();
      if (!Pixels.REAL32.equals(type) && !Pixels.REAL64.equals(type)) {
        return F.NIL;
      }
      double[][][] channels = ImageFilterFunctions.channels(source, engine);
      if (channels == null) {
        return F.NIL;
      }
      final int sourceHeight = channels[0].length;
      final int sourceWidth = channels[0][0].length;
      final double scaleX = (double) sourceWidth / width;
      final double scaleY = (double) sourceHeight / height;
      double[][][] result = new double[channels.length][height][width];
      for (int c = 0; c < channels.length; c++) {
        for (int y = 0; y < height; y++) {
          int fromY = (int) Math.floor(y * scaleY);
          int toY = Math.min(sourceHeight, Math.max(fromY + 1, (int) Math.ceil((y + 1) * scaleY)));
          for (int x = 0; x < width; x++) {
            int fromX = (int) Math.floor(x * scaleX);
            int toX = Math.min(sourceWidth, Math.max(fromX + 1, (int) Math.ceil((x + 1) * scaleX)));
            double sum = 0.0;
            for (int row = fromY; row < toY; row++) {
              for (int column = fromX; column < toX; column++) {
                sum += channels[c][row][column];
              }
            }
            result[c][y][x] = sum / ((toY - fromY) * (toX - fromX));
          }
        }
      }
      return ImageFilterFunctions.toImage(result, source.getOptions(), type);
    }

    /**
     * Point sampling. The matrix an image was built from is sampled with it, so its type and its
     * samples are kept rather than read back off the 8 bit bitmap.
     */
    private static IExpr nearest(IExpr arg, BufferedImage image, int width, int height) {
      BufferedImage resized = Geometry.resize(image, width, height, false);
      if (arg instanceof ImageExpr) {
        ImageExpr source = (ImageExpr) arg;
        IAST matrix = source.getMatrix();
        if (matrix != null && source.getOptions().interleaved() && matrix.argSize() > 0
            && matrix.arg1().isList()) {
          int rows = matrix.argSize();
          int columns = matrix.arg1().argSize();
          IASTAppendable sampled = F.ListAlloc(height);
          for (int y = 0; y < height; y++) {
            IExpr row = matrix.get(clamp((int) Math.floor((y + 0.5) * rows / height), rows) + 1);
            if (!row.isList() || row.argSize() != columns) {
              return new ImageExpr(resized, null);
            }
            IASTAppendable sampledRow = F.ListAlloc(width);
            for (int x = 0; x < width; x++) {
              sampledRow.append(((IAST) row)
                  .get(clamp((int) Math.floor((x + 0.5) * columns / width), columns) + 1));
            }
            sampled.append(sampledRow);
          }
          sampled.isMatrix(true);
          return new ImageExpr(resized, sampled, source.getOptions(), source.sampleType());
        }
      }
      return new ImageExpr(resized, null);
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
        return quarterTurns(ast.arg1(), image, 1);
      }
      if (ast.argSize() == 2) {
        // Side forms: ImageRotate(img, side) puts the top at side, side1 -> side2 moves
        // side1 to side2
        int turns = sideTurns(ast.arg2());
        if (turns >= 0) {
          return quarterTurns(ast.arg1(), image, turns);
        }
      }
      double radians = ast.arg2().evalfNaN();
      if (Double.isNaN(radians)) {
        return F.NIL;
      }
      // an exact quarter turn is a transposition, and doing it by resampling would blur it
      double quarters = radians / (Math.PI / 2.0);
      if (ast.argSize() == 2 && Math.abs(quarters - Math.rint(quarters)) < 1e-12) {
        return quarterTurns(ast.arg1(), image, (int) Math.rint(quarters));
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

    /**
     * The counterclockwise quarter turns of <code>side</code> or <code>side1 -> side2</code>, or
     * <code>-1</code>: <code>Top</code>, <code>Left</code>, <code>Bottom</code>, <code>Right</code>
     * lie a quarter turn apart counterclockwise, and a single side means <code>Top -> side</code>.
     */
    private static int sideTurns(IExpr spec) {
      if (spec.isRuleAST()) {
        int from = sidePosition(spec.first());
        int to = sidePosition(spec.second());
        return from < 0 || to < 0 ? -1 : Math.floorMod(to - from, 4);
      }
      return sidePosition(spec);
    }

    private static int sidePosition(IExpr side) {
      if (side == S.Top) {
        return 0;
      }
      if (side == S.Left) {
        return 1;
      }
      if (side == S.Bottom) {
        return 2;
      }
      if (side == S.Right) {
        return 3;
      }
      return -1;
    }

    /**
     * The image turned by <code>turns</code> counterclockwise quarter turns. The matrix an image
     * was built from turns with it, exactly, so its type and its samples are kept rather than read
     * back off the 8 bit bitmap.
     */
    private static IExpr quarterTurns(IExpr arg, BufferedImage image, int turns) {
      BufferedImage rotated = Geometry.rotateQuarters(image, turns);
      if (arg instanceof ImageExpr) {
        ImageExpr source = (ImageExpr) arg;
        IAST matrix = source.getMatrix();
        if (matrix != null && source.getOptions().interleaved()) {
          IAST turned = rotateMatrix(matrix, Math.floorMod(turns, 4));
          if (turned != null) {
            turned.isMatrix(true);
            return new ImageExpr(rotated, turned, source.getOptions(), source.sampleType());
          }
        }
      }
      return new ImageExpr(rotated, null);
    }

    /**
     * The rows of <code>matrix</code> (each pixel a sample or a list of channels) turned
     * counterclockwise by <code>turns</code> quarter turns, or <code>null</code> if it is ragged.
     */
    private static IAST rotateMatrix(IAST matrix, int turns) {
      int rows = matrix.argSize();
      if (rows == 0 || !matrix.arg1().isList()) {
        return null;
      }
      int cols = matrix.arg1().argSize();
      for (int i = 1; i <= rows; i++) {
        if (!matrix.get(i).isList() || matrix.get(i).argSize() != cols) {
          return null;
        }
      }
      if (turns == 0) {
        return matrix;
      }
      final boolean odd = turns % 2 == 1;
      int newRows = odd ? cols : rows;
      int newCols = odd ? rows : cols;
      org.matheclipse.core.interfaces.IASTAppendable result = F.ListAlloc(newRows);
      for (int i = 0; i < newRows; i++) {
        org.matheclipse.core.interfaces.IASTAppendable row = F.ListAlloc(newCols);
        for (int j = 0; j < newCols; j++) {
          int r;
          int c;
          if (turns == 1) {
            // counterclockwise: the last column becomes the first row
            r = j;
            c = cols - 1 - i;
          } else if (turns == 2) {
            r = rows - 1 - i;
            c = cols - 1 - j;
          } else {
            // clockwise: the last row becomes the first column
            r = rows - 1 - j;
            c = i;
          }
          row.append(((IAST) matrix.get(r + 1)).get(c + 1));
        }
        result.append(row);
      }
      return result;
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
        return flip(ast.arg1(), image, false, true);
      }
      IExpr side = ast.arg2();
      if (side.isRuleAST()) {
        IAST rule = (IAST) side;
        IExpr from = rule.arg1();
        IExpr to = rule.arg2();
        if (isVertical(from) && isVertical(to)) {
          return flip(ast.arg1(), image, false, from != to);
        }
        if (isHorizontal(from) && isHorizontal(to)) {
          return flip(ast.arg1(), image, from != to, false);
        }
        // Top -> Left is the main diagonal, Top -> Right the other one
        boolean antiDiagonal =
            (from == S.Top && to == S.Right) || (from == S.Bottom && to == S.Left)
                || (from == S.Left && to == S.Bottom) || (from == S.Right && to == S.Top);
        return transpose(ast.arg1(), image, antiDiagonal);
      }
      if (isVertical(side)) {
        return flip(ast.arg1(), image, false, true);
      }
      if (isHorizontal(side)) {
        return flip(ast.arg1(), image, true, false);
      }
      return F.NIL;
    }

    /** The mirrored image; the matrix an image was built from is mirrored with it. */
    private static IExpr flip(IExpr arg, BufferedImage image, boolean horizontal,
        boolean vertical) {
      IAST matrix = PixelMatrix.of(arg);
      if (matrix != null) {
        final int width = PixelMatrix.width(matrix);
        final int height = PixelMatrix.height(matrix);
        IExpr result = PixelMatrix.image(arg, width, height, (x, y) -> PixelMatrix.pixel(matrix,
            horizontal ? width - 1 - x : x, vertical ? height - 1 - y : y));
        if (result != null) {
          return result;
        }
      }
      return new ImageExpr(Geometry.flip(image, horizontal, vertical), null);
    }

    /** The image reflected about a diagonal, and its matrix with it. */
    private static IExpr transpose(IExpr arg, BufferedImage image, boolean antiDiagonal) {
      IAST matrix = PixelMatrix.of(arg);
      if (matrix != null) {
        final int width = PixelMatrix.width(matrix);
        final int height = PixelMatrix.height(matrix);
        IExpr result = PixelMatrix.image(arg, height, width, (x, y) -> antiDiagonal //
            ? PixelMatrix.pixel(matrix, width - 1 - y, height - 1 - x)
            : PixelMatrix.pixel(matrix, y, x));
        if (result != null) {
          return result;
        }
      }
      return new ImageExpr(Geometry.transpose(image, antiDiagonal), null);
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
   * all four, and a negative amount trims instead. A third argument is the colour to pad with, or
   * <code>"Fixed"</code> to repeat the pixels of the border and <code>"Periodic"</code> to repeat
   * the image.
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
      boolean fixed = false;
      boolean periodic = false;
      if (ast.argSize() >= 3) {
        if (ast.arg3().isString()) {
          fixed = "Fixed".equals(ast.arg3().toString());
          periodic = "Periodic".equals(ast.arg3().toString());
          if (!fixed && !periodic) {
            return F.NIL;
          }
        } else {
          padding = Colors.toRgba(ast.arg3());
          if (padding == null) {
            return F.NIL;
          }
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
      final int sourceWidth = image.getWidth();
      final int sourceHeight = image.getHeight();
      final boolean repeatBorder = fixed;
      final boolean repeatImage = periodic;
      // the matrix an image was built from is padded with it
      IAST matrix = PixelMatrix.of(ast.arg1());
      if (matrix != null && PixelMatrix.width(matrix) == sourceWidth
          && PixelMatrix.height(matrix) == sourceHeight) {
        ImageExpr source = (ImageExpr) ast.arg1();
        double[] colour = new double[4];
        if (padding != null) {
          double gray = ast.arg3().isList() ? Double.NaN : ast.arg3().evalfNaN();
          for (int c = 0; c < 4; c++) {
            // a number is a grey level, and is kept as it was given
            colour[c] = c < 3 && !Double.isNaN(gray) ? gray : padding[c];
          }
        }
        final IExpr outside = repeatBorder || repeatImage ? null
            : PixelMatrix.constant(colour, PixelMatrix.channels(matrix), source.sampleType());
        if (outside != null || repeatBorder || repeatImage) {
          IExpr result = PixelMatrix.image(source, width, height, (x, y) -> {
            int sourceX = x - left;
            int sourceY = y - top;
            if (repeatBorder) {
              sourceX = clamp(sourceX, sourceWidth);
              sourceY = clamp(sourceY, sourceHeight);
            } else if (repeatImage) {
              sourceX = Math.floorMod(sourceX, sourceWidth);
              sourceY = Math.floorMod(sourceY, sourceHeight);
            } else if (sourceX < 0 || sourceY < 0 || sourceX >= sourceWidth
                || sourceY >= sourceHeight) {
              return outside;
            }
            return PixelMatrix.pixel(matrix, sourceX, sourceY);
          });
          if (result != null) {
            return result;
          }
        }
      }
      // without an explicit colour the new pixels are black, or transparent where the image has an
      // alpha channel to be transparent in
      Geometry.Background background = padding == null //
          ? Geometry.transparentOrBlack()
          : Geometry.color(padding);
      return new ImageExpr(Pixels.fromPixels(width, height, channels, (x, y) -> {
        int sourceX = x - left;
        int sourceY = y - top;
        if (repeatBorder) {
          sourceX = clamp(sourceX, sourceWidth);
          sourceY = clamp(sourceY, sourceHeight);
        } else if (repeatImage) {
          sourceX = Math.floorMod(sourceX, sourceWidth);
          sourceY = Math.floorMod(sourceY, sourceHeight);
        } else if (sourceX < 0 || sourceY < 0 || sourceX >= sourceWidth
            || sourceY >= sourceHeight) {
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
      return part(ast.arg1(), image, left, top, right - left + 1, bottom - top + 1);
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
      return part(ast.arg1(), image, columns[0], rows[0], columns[1] - columns[0] + 1,
          rows[1] - rows[0] + 1);
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

  /**
   * The rectangle of the image which starts at column <code>left</code> and row <code>top</code>.
   * The matrix an image was built from is cut with it, so its type and its samples are kept rather
   * than read back off the 8 bit bitmap.
   */
  static IExpr part(IExpr arg, BufferedImage image, int left, int top, int width, int height) {
    IAST matrix = PixelMatrix.of(arg);
    if (matrix != null && PixelMatrix.width(matrix) == image.getWidth()
        && PixelMatrix.height(matrix) == image.getHeight()) {
      IExpr result = PixelMatrix.image(arg, width, height,
          (x, y) -> PixelMatrix.pixel(matrix, left + x, top + y));
      if (result != null) {
        return result;
      }
    }
    int channels = Boof.channels(image);
    return new ImageExpr(Pixels.fromPixels(width, height, channels,
        (x, y) -> Pixels.pixel(image, left + x, top + y, channels)), null);
  }

  public static void initialize() {
    Initializer.init();
  }

  private ImageGeometryFunctions() {}
}
