package org.matheclipse.image.builtin;

import java.awt.image.BufferedImage;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
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
import org.matheclipse.image.algo.Raster;
import org.matheclipse.image.expression.data.ImageExpr;
import org.matheclipse.image.expression.data.ImageOptions;

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
      IExpr rasterized = ImagePropertyFunctions.withRasterizedGraphics(ast, 1, engine);
      if (rasterized.isPresent()) {
        return rasterized;
      }
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
   * <code>ImageRotate(image, theta)</code> turns it by <code>theta</code> radians onto the
   * whole-pixel bounding box, each pixel taking the source pixel under its centre and black
   * outside, and a third argument fixes the output size instead.
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
      if (ast.argSize() == 2 && ast.arg1() instanceof ImageExpr) {
        ImageExpr source = (ImageExpr) ast.arg1();
        Raster raster = source.raster();
        if (raster != null) {
          ImageExpr rotated = ImageExpr.fromRaster(Geometry.rotateOntoBoundingBox(raster, radians),
              source.getOptions(), source.sampleType());
          return rotated == null ? F.NIL : rotated;
        }
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
      final boolean odd = (turns & 1) == 1;
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
   * <code>"Fixed"</code> to repeat the pixels of the border, <code>"Periodic"</code> to repeat
   * the image, <code>"Reflected"</code> to mirror it at the border pixels and
   * <code>"Reversed"</code> to mirror it at its edges.
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
      // how the image is continued beyond its border, or null for a colour
      String continuation = null;
      if (ast.argSize() >= 3) {
        if (ast.arg3().isString()) {
          continuation = ast.arg3().toString();
          if (!CONTINUATIONS.contains(continuation)) {
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
      if (padding != null && ast.arg1() instanceof ImageExpr) {
        IExpr promoted =
            padPromoted((ImageExpr) ast.arg1(), ast.arg3(), left, top, width, height);
        if (promoted.isPresent()) {
          return promoted;
        }
      }
      int channels = Boof.channels(image);
      final int sourceWidth = image.getWidth();
      final int sourceHeight = image.getHeight();
      final String continued = continuation;
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
        final IExpr outside = continued != null ? null
            : PixelMatrix.constant(colour, PixelMatrix.channels(matrix), source.sampleType());
        if (outside != null || continued != null) {
          IExpr result = PixelMatrix.image(source, width, height, (x, y) -> {
            int sourceX = x - left;
            int sourceY = y - top;
            if (continued != null) {
              sourceX = continued(sourceX, sourceWidth, continued);
              sourceY = continued(sourceY, sourceHeight, continued);
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
        if (continued != null) {
          sourceX = continued(sourceX, sourceWidth, continued);
          sourceY = continued(sourceY, sourceHeight, continued);
        } else if (sourceX < 0 || sourceY < 0 || sourceX >= sourceWidth
            || sourceY >= sourceHeight) {
          return background.outside(channels);
        }
        return Pixels.pixel(image, sourceX, sourceY, channels);
      }), null);
    }

    /**
     * The padding of a greyscale image with a colour other than a grey, which makes it a colour
     * image, or of an image without an alpha channel with a translucent colour, which adds an
     * alpha channel.
     *
     * @return {@link F#NIL} if the padding keeps the channels of the image
     */
    private static IExpr padPromoted(ImageExpr source, IExpr colorExpr, int left, int top,
        int width, int height) {
      Raster raster = source.raster();
      double[] rgba = Colors.toRgbaDouble(colorExpr);
      if (raster == null || rgba == null) {
        return F.NIL;
      }
      final int sourceColors = raster.colorChannels();
      final boolean sourceAlpha = raster.channels != sourceColors;
      final boolean color = sourceColors == 1 && colorExpr.isAST() && !colorExpr.isList()
          && !colorExpr.isAST(S.GrayLevel);
      final boolean alpha = !sourceAlpha && rgba[3] < 1.0;
      if (!color && !alpha) {
        return F.NIL;
      }
      // a greyscale image with an alpha channel has no representation here, so a translucent
      // padding of a greyscale image makes it an RGBA image, as SetAlphaChannel does
      final int colors = color || alpha ? Math.max(3, sourceColors) : sourceColors;
      final int channels = colors + (sourceAlpha || alpha ? 1 : 0);
      double[] outside = new double[channels];
      for (int c = 0; c < colors; c++) {
        outside[c] = colors == 1
            ? (rgba[0] == rgba[1] && rgba[1] == rgba[2] ? rgba[0]
                : Colors.grayLevel(rgba[0], rgba[1], rgba[2]))
            : rgba[c];
      }
      if (channels > colors) {
        outside[colors] = rgba[3];
      }
      Raster result = new Raster(width, height, channels);
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          int sourceX = x - left;
          int sourceY = y - top;
          boolean inside = sourceX >= 0 && sourceY >= 0 && sourceX < raster.width
              && sourceY < raster.height;
          for (int c = 0; c < channels; c++) {
            double value;
            if (!inside) {
              value = outside[c];
            } else if (c < colors) {
              value = raster.get(sourceX, sourceY, Math.min(c, sourceColors - 1));
            } else {
              value = sourceAlpha ? raster.get(sourceX, sourceY, sourceColors) : 1.0;
            }
            result.set(x, y, c, value);
          }
        }
      }
      String type = Pixels.BIT.equals(source.sampleType()) ? Pixels.REAL32 : source.sampleType();
      ImageExpr padded = ImageExpr.fromRaster(result, source.getOptions(), type);
      return padded == null ? F.NIL : padded;
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
          // a real width counts the whole pixels of it
          double value = values[i].isReal() ? values[i].evalfNaN() : Double.NaN;
          if (Double.isNaN(value) || Math.abs(value) >= Integer.MAX_VALUE) {
            return null;
          }
          result[i] = (int) value;
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
        if (list.argSize() == 1) {
          // {n}: the n-th one alone
          from = list.arg1().toIntDefault();
          to = from;
        } else if (list.argSize() == 2) {
          from = list.arg1().toIntDefault();
          to = list.arg2().toIntDefault();
        } else {
          return null;
        }
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
   * A third argument places the centre of the overlay at a position in image coordinates, a fourth
   * names the point of the overlay which goes there, and
   * <code>ImageCompose(background, {overlay, alpha})</code> makes the overlay partly transparent.
   *
   * <p>
   * <code>ImageCompose(background, overlay, pos, opos, {a, b, c})</code> composes by the weights of
   * the parts where only the overlay, only the background, and both of them are opaque:
   * <code>{1, 1, 1}</code> draws the overlay over the background. The alpha channels of both images
   * take part, an alpha channel of either gives the result one, and the result is
   * <code>"Real32"</code>.
   */
  private static class ImageCompose extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr rasterized = ImagePropertyFunctions.withRasterizedGraphics(ast, 1, engine);
      if (rasterized.isPresent()) {
        return rasterized;
      }
      if (ast.argSize() >= 2 && ast.arg2().isList2() && ast.arg2().first().isAST(S.Graphics)) {
        // {graphic, alpha}: the overlay is the image the graphic is drawn to
        IExpr image = engine.evaluate(F.unaryAST1(S.Rasterize, ast.arg2().first()));
        if (!(image instanceof ImageExpr)) {
          return F.NIL;
        }
        return engine.evaluate(ast.setAtCopy(2, ((IAST) ast.arg2()).setAtCopy(1, image)));
      }
      rasterized = ImagePropertyFunctions.withRasterizedGraphics(ast, 2, engine);
      if (rasterized.isPresent()) {
        return rasterized;
      }
      if (!(ast.arg1() instanceof ImageExpr)) {
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
      if (!(overlayArgument instanceof ImageExpr)) {
        return F.NIL;
      }
      double[] weights = {1.0, 1.0, 1.0};
      if (ast.argSize() >= 5) {
        weights = ast.arg5().isList3() ? ast.arg5().toDoubleVector() : null;
        if (weights == null) {
          return F.NIL;
        }
      }
      ImageExpr backgroundImage = (ImageExpr) ast.arg1();
      ImageExpr overlayImage = (ImageExpr) overlayArgument;
      Raster background = ColorFunctions.rgbOrGray(backgroundImage);
      Raster overlay = ColorFunctions.rgbOrGray(overlayImage);
      if (background == null || overlay == null) {
        return F.NIL;
      }
      // the top left corner of the overlay in raster coordinates
      int offsetX = background.width / 2 - overlay.width / 2;
      int offsetY = (background.height + 1) / 2 - (overlay.height + 1) / 2;
      if (ast.argSize() >= 3) {
        // the position in the background, in image coordinates, on which the overlay's own
        // position lands; the centre unless it is given
        double[] position = anchor(ast.arg3(), background.width, background.height);
        double[] overlayPosition = ast.argSize() >= 4 //
            ? anchor(ast.arg4(), overlay.width, overlay.height)
            : new double[] {overlay.width / 2.0, overlay.height / 2.0};
        if (position == null || overlayPosition == null) {
          return F.NIL;
        }
        offsetX = roundHalfAway(position[0] - overlayPosition[0]);
        offsetY = roundHalfAway(background.height - (position[1] - overlayPosition[1])
            - overlay.height);
      }
      return compose(background, ColorFunctions.hasAlpha(backgroundImage), overlay,
          ColorFunctions.hasAlpha(overlayImage), offsetX, offsetY, opacity, weights);
    }

    private static int roundHalfAway(double value) {
      return (int) (Math.signum(value) * Math.floor(Math.abs(value) + 0.5));
    }

    /**
     * Compose on the samples in single precision. With the opacities <code>ai</code> of the image
     * and <code>ao</code> of the overlay, the opacity of a pixel is
     * <code>a*ao*(1-ai) + b*ai*(1-ao) + c*ai*ao</code> and its colour is weighted the same way, the
     * part of both showing the overlay. Without an alpha channel the weighted colour is the pixel.
     * A greyscale image goes with a colour one by repeating its grey level.
     */
    private static IExpr compose(Raster background, boolean backgroundAlpha, Raster overlay,
        boolean overlayAlpha, int offsetX, int offsetY, double opacity, double[] weights) {
      int backgroundColors = backgroundAlpha ? background.channels - 1 : background.channels;
      int overlayColors = overlayAlpha ? overlay.channels - 1 : overlay.channels;
      int colors = Math.max(backgroundColors, overlayColors);
      boolean alpha = backgroundAlpha || overlayAlpha;
      Raster result = new Raster(background.width, background.height, colors + (alpha ? 1 : 0));
      for (int y = 0; y < background.height; y++) {
        for (int x = 0; x < background.width; x++) {
          int overX = x - offsetX;
          int overY = y - offsetY;
          boolean inside =
              overX >= 0 && overY >= 0 && overX < overlay.width && overY < overlay.height;
          double ai = backgroundAlpha ? (float) background.get(x, y, backgroundColors) : 1.0;
          double ao = !inside ? 0.0
              : opacity * (overlayAlpha ? (float) overlay.get(overX, overY, overlayColors) : 1.0);
          double onlyOverlay = weights[0] * ao * (1.0 - ai);
          double onlyBackground = weights[1] * ai * (1.0 - ao);
          double both = weights[2] * ai * ao;
          double opaque = onlyOverlay + onlyBackground + both;
          for (int c = 0; c < colors; c++) {
            double under = (float) background.get(x, y, Math.min(c, backgroundColors - 1));
            double over =
                inside ? (float) overlay.get(overX, overY, Math.min(c, overlayColors - 1)) : 0.0;
            double value = over * (onlyOverlay + both) + under * onlyBackground;
            if (alpha) {
              value = opaque > 0.0 ? value / opaque : 0.0;
            }
            result.set(x, y, c, value);
          }
          if (alpha) {
            result.set(x, y, colors, opaque);
          }
        }
      }
      ImageOptions options = alpha //
          ? ImageOptions.DEFAULT.withColorSpace(ColorFunctions.alphaSpace(colors))
          : ImageOptions.DEFAULT;
      return ColorFunctions.realImage(result, options);
    }

    /**
     * A position in an image of the given size: <code>{x, y}</code>, where each of the two may be
     * the name of a side or <code>Center</code>, or one such name alone, or
     * <code>Scaled({sx, sy})</code>, fractions of the width and the height.
     *
     * @return <code>null</code> if it is no position
     */
    private static double[] anchor(IExpr spec, int width, int height) {
      if (spec.isAST(S.Scaled, 2) && spec.first().isList2()) {
        double x = spec.first().first().evalfNaN();
        double y = spec.first().second().evalfNaN();
        return Double.isNaN(x) || Double.isNaN(y) ? null : new double[] {x * width, y * height};
      }
      if (spec.isList2()) {
        double x = anchorCoordinate(spec.first(), width, true);
        double y = anchorCoordinate(spec.second(), height, false);
        return Double.isNaN(x) || Double.isNaN(y) ? null : new double[] {x, y};
      }
      if (spec == S.Center) {
        return new double[] {width / 2.0, height / 2.0};
      }
      if (spec == S.Left || spec == S.Right) {
        return new double[] {anchorCoordinate(spec, width, true), height / 2.0};
      }
      if (spec == S.Bottom || spec == S.Top) {
        return new double[] {width / 2.0, anchorCoordinate(spec, height, false)};
      }
      return null;
    }

    private static double anchorCoordinate(IExpr spec, int extent, boolean horizontal) {
      if (spec == S.Center) {
        return extent / 2.0;
      }
      if (spec == (horizontal ? S.Left : S.Bottom)) {
        return 0.0;
      }
      if (spec == (horizontal ? S.Right : S.Top)) {
        return extent;
      }
      return spec.isSymbol() ? Double.NaN : spec.evalfNaN();
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_5;
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
   * <code>Rasterize(graphics)</code> - a graphics object drawn into an image, 360 pixels wide unless
   * its size is given. An image rasterizes to itself.
   *
   * <p>
   * <code>Rasterize(expr, element)</code> gives one element of that image: <code>"Image"</code>,
   * <code>"Data"</code>, the bytes of its colour channels, <code>"RasterSize"</code>, its width and
   * height, or <code>"BoundingBox"</code>, its width, its height and the distance of the baseline
   * from the top.
   */
  private static class Rasterize extends AbstractEvaluator {

    /** The width of a graphic which doesn't state its size. */
    private static final int GRAPHICS_WIDTH = 360;

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.argSize() >= 2 && ast.arg2().isString()) {
        return element(ast, ast.arg2().toString(), engine);
      }
      IExpr arg1 = ast.arg1();
      if (arg1 instanceof ImageExpr) {
        return arg1;
      }
      // any other expression is drawn as a text
      IAST graphics = arg1.isAST() && (arg1.isGraphicsObject() || arg1.isAST(S.Graphics3D))
          ? (IAST) arg1
          : F.Graphics(F.Text(arg1));
      ImageExpr image = ImageExpr.toImageExpr(graphics);
      if (image == null) {
        return F.NIL;
      }
      BufferedImage drawn = image.getBufferedImage();
      int[] size = rasterSize(ast, drawn.getWidth(), drawn.getHeight(), graphics == arg1);
      if (size == null) {
        return F.NIL;
      }
      // an opaque RGB image: the transparent background is white
      BufferedImage rgb = new BufferedImage(size[0], size[1], BufferedImage.TYPE_INT_RGB);
      java.awt.Graphics2D g = rgb.createGraphics();
      try {
        g.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
            java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setColor(java.awt.Color.WHITE);
        g.fillRect(0, 0, size[0], size[1]);
        g.drawImage(drawn, 0, 0, size[0], size[1], null);
      } finally {
        g.dispose();
      }
      return new ImageExpr(rgb, null);
    }

    /** One element of the image of the first argument, the options being passed on. */
    private static IExpr element(IAST ast, String element, EvalEngine engine) {
      IExpr image = engine.evaluate(ast.removeAtClone(2));
      if (!(image instanceof ImageExpr)) {
        return F.NIL;
      }
      switch (element) {
        case "Image":
          return image;
        case "Data":
          return engine.evaluate(F.binaryAST2(S.ImageData, F.unaryAST1(S.RemoveAlphaChannel, image),
              F.stringx("Byte")));
        case "RasterSize":
          return engine.evaluate(F.unaryAST1(S.ImageDimensions, image));
        case "BoundingBox":
          BufferedImage picture = ((ImageExpr) image).getBufferedImage();
          int height = picture.getHeight();
          // a graphic sits on its axis, a line of text on its ascent
          int baseline = ast.arg1().isAST(S.Graphics) || ast.arg1().isAST(S.Graphics3D)
              ? (height + 1) / 2
              : (int) Math.round(0.75 * height);
          return F.List(F.ZZ(picture.getWidth()), F.ZZ(height), F.ZZ(baseline));
        default:
          // "`1`" is not an available element. Possible elements include `2`.
          Errors.printMessage(S.Rasterize, "elmntavl", F.List(F.stringx(element),
              F.stringx("\"BoundingBox\", \"Data\", \"Graphics\", \"RasterSize\", "
                  + "\"Regions\", and \"Image\"")),
              engine);
          return S.$Failed;
      }
    }

    /**
     * The size of the image: <code>RasterSize -> w</code> scales the drawing to the width
     * <code>w</code>, <code>RasterSize -> {w, h}</code> to both.
     *
     * @param graphic whether the drawing is a graphic, which has a width of
     *        {@value #GRAPHICS_WIDTH} pixels unless it states its <code>ImageSize</code>
     * @return <code>null</code> for an invalid size
     */
    private static int[] rasterSize(IAST ast, int width, int height, boolean graphic) {
      boolean automatic = false;
      for (int i = 2; i < ast.size(); i++) {
        IExpr option = ast.get(i);
        if (option.isRuleAST() && option.first() == S.RasterSize) {
          IExpr value = option.second();
          if (value.isList2()) {
            int w = value.first().toIntDefault();
            int h = value.second().toIntDefault();
            return w > 0 && h > 0 ? new int[] {w, h} : null;
          }
          int w = value.toIntDefault();
          if (w <= 0) {
            if (value != S.Automatic) {
              return null;
            }
            // the drawing's own size, scaled by ImageResolution below
            automatic = true;
            break;
          }
          return new int[] {w, Math.max(1, (int) Math.round((double) w * height / width))};
        }
      }
      double w = width;
      double h = height;
      if (graphic && !automatic && !hasImageSize((IAST) ast.arg1())) {
        w = GRAPHICS_WIDTH;
        h = Math.max(1, Math.round((double) GRAPHICS_WIDTH * height / width));
      }
      // ImageResolution -> dpi: a size is given in printer points, 72 to the inch, so a point
      // covers dpi/72 pixels, the last partly covered pixel included
      double scale = imageResolution(ast) / 72.0;
      if (scale != 1.0) {
        return new int[] {Math.max(1, (int) Math.ceil(w * scale - 1e-9)),
            Math.max(1, (int) Math.ceil(h * scale - 1e-9))};
      }
      return new int[] {(int) w, (int) h};
    }

    /** The positive number of <code>ImageResolution -> dpi</code>, 72 if it is not given. */
    private static double imageResolution(IAST ast) {
      for (int i = 2; i < ast.size(); i++) {
        IExpr option = ast.get(i);
        if (option.isRuleAST() && option.first() == S.ImageResolution) {
          double dpi = option.second().evalfNaN();
          if (Double.isFinite(dpi) && dpi > 0 && dpi <= 7200) {
            return dpi;
          }
        }
      }
      return 72.0;
    }

    private static boolean hasImageSize(IAST graphics) {
      for (int i = 2; i < graphics.size(); i++) {
        if (graphics.get(i).isRuleAST() && graphics.get(i).first() == S.ImageSize) {
          return true;
        }
      }
      return false;
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
   * <code>ImageTransformation(image, f)</code> - the image in which the pixel at <code>p</code> is
   * taken from the position <code>f(p)</code> of the original, both in image coordinates.
   *
   * <p>
   * <code>f</code> may be a function or a <code>TransformationFunction</code>. The image rectangle
   * has the coordinates <code>{{0, 1}, {0, h/w}}</code>; the option
   * <code>DataRange -&gt; {{x0, x1}, {y0, y1}}</code> gives others, <code>DataRange -&gt; Full</code>
   * those in pixels. <code>Padding -&gt; value</code>, 0 by default, fills in what lies outside the
   * original; <code>"Fixed"</code>, <code>"Periodic"</code>, <code>"Reflected"</code> and
   * <code>"Reversed"</code> continue the image.
   */
  private static class ImageTransformation extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return transform(ast, engine, false);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_5;
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
      return ARGS_2_5;
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
      return ARGS_2_5;
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
    // the options DataRange and Padding come after the arguments
    IExpr dataRange = F.NIL;
    IExpr padding = F.NIL;
    int positional = ast.argSize();
    for (int i = ast.argSize(); i >= 3 && ast.get(i).isRuleAST(); i--) {
      IExpr name = ast.get(i).first();
      if (name == S.DataRange) {
        dataRange = ast.get(i).second();
      } else if (name == S.Padding) {
        padding = ast.get(i).second();
      } else {
        return F.NIL;
      }
      positional = i - 1;
    }
    if (positional > 3) {
      return F.NIL;
    }
    BufferedImage image = ImagePropertyFunctions.bufferedImage(ast.arg1());
    if (image == null) {
      return F.NIL;
    }
    int width = image.getWidth();
    int height = image.getHeight();
    if (positional >= 3) {
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

    return resampled((ImageExpr) ast.arg1(), width, height, matrix, function, dataRange, padding,
        engine);
  }

  /**
   * The samples of <code>source</code> read backwards through <code>backward</code> or
   * <code>function</code>, in the coordinates <code>dataRange</code> gives the image rectangle: x
   * grows to the right, y upwards, and each pixel is read at its centre. Between the centres the
   * samples are interpolated bilinearly; beyond the outermost centres the padding acts as a ring of
   * pixels around the image.
   *
   * @param backward the homogeneous matrix which maps output positions to input positions, or
   *        <code>null</code> if <code>function</code> does
   * @param dataRange <code>{{x0, x1}, {y0, y1}}</code> or <code>Full</code> for the rectangle of
   *        both images; <code>F.NIL</code> or <code>Automatic</code> for
   *        <code>{{0, 1}, {0, h/w}}</code> of the original
   * @param padding a value for every channel, a list of one per channel, a colour,
   *        <code>"Fixed"</code>, <code>"Periodic"</code>, <code>"Reflected"</code> or
   *        <code>"Reversed"</code>; <code>F.NIL</code> pads with 0. A list or a colour also makes
   *        the image itself opaque.
   * @return <code>F.NIL</code> if an option is not understood
   */
  private static IExpr resampled(ImageExpr source, int width, int height, double[][] backward,
      IExpr function, IExpr dataRange, IExpr padding, EvalEngine engine) {
    Raster raster = source.raster();
    if (raster == null) {
      return F.NIL;
    }
    final int sourceWidth = raster.width;
    final int sourceHeight = raster.height;
    final int channels = raster.channels;
    // {x0, x1, y0, y1} of the input rectangle, which the output covers too
    double[] in;
    if (dataRange == S.Full) {
      in = new double[] {0.0, sourceWidth, 0.0, sourceHeight};
    } else if (dataRange.isPresent() && dataRange != S.Automatic) {
      in = rectangle(dataRange);
      if (in == null) {
        return F.NIL;
      }
    } else {
      in = new double[] {0.0, 1.0, 0.0, (double) sourceHeight / sourceWidth};
    }
    final double[] out = in;

    // how a position outside the image is continued: by the padding samples or from the image
    String continuation = null;
    double[] outside = new double[channels];
    // the channel which is made opaque, or -1
    int opaqueChannel = -1;
    if (padding.isPresent()) {
      if (padding.isString()) {
        continuation = padding.toString();
        if (!CONTINUATIONS.contains(continuation)) {
          return F.NIL;
        }
      } else {
        outside = paddingSamples(padding, channels, source.storedColorSpace());
        if (outside == null) {
          return F.NIL;
        }
        if ((padding.isList() || !padding.isNumericFunction()) && source.storedColorSpace() == null
            && (channels == 2 || channels == 4)) {
          // a padding colour turns the image into an opaque one
          opaqueChannel = channels - 1;
        }
      }
    }

    // a single precision image keeps the samples it can hold, written as short as they print
    final boolean singlePrecision = Pixels.REAL32.equals(source.sampleType());
    Raster result = new Raster(width, height, channels);
    double[] values = new double[channels];
    for (int y = 0; y < height; y++) {
      double dataY = out[3] - (y + 0.5) * (out[3] - out[2]) / height;
      for (int x = 0; x < width; x++) {
        double dataX = out[0] + (x + 0.5) * (out[1] - out[0]) / width;
        double[] position = backward != null //
            ? Geometry.apply(backward, dataX, dataY)
            : position(function, dataX, dataY, engine);
        if (position == null || !Double.isFinite(position[0]) || !Double.isFinite(position[1])) {
          for (int c = 0; c < channels; c++) {
            result.set(x, y, c, outside[c]);
          }
          continue;
        }
        // column and row of the position, counted from the centre of the top left pixel
        double column = (position[0] - in[0]) / (in[1] - in[0]) * sourceWidth - 0.5;
        double row = (in[3] - position[1]) / (in[3] - in[2]) * sourceHeight - 0.5;
        if (continuation == null || "Fixed".equals(continuation)) {
          // further out than the ring of padding pixels nothing changes any more
          column = Math.max(-2.0, Math.min(sourceWidth + 1.0, column));
          row = Math.max(-2.0, Math.min(sourceHeight + 1.0, row));
        }
        int left = (int) Math.floor(column);
        int top = (int) Math.floor(row);
        double fractionX = column - left;
        double fractionY = row - top;
        java.util.Arrays.fill(values, 0.0);
        for (int dy = 0; dy <= 1; dy++) {
          for (int dx = 0; dx <= 1; dx++) {
            double weight = (dx == 0 ? 1.0 - fractionX : fractionX) //
                * (dy == 0 ? 1.0 - fractionY : fractionY);
            if (weight == 0.0) {
              continue;
            }
            int sourceX = left + dx;
            int sourceY = top + dy;
            if (continuation != null) {
              sourceX = continued(sourceX, sourceWidth, continuation);
              sourceY = continued(sourceY, sourceHeight, continuation);
            }
            boolean inside =
                sourceX >= 0 && sourceY >= 0 && sourceX < sourceWidth && sourceY < sourceHeight;
            for (int c = 0; c < channels; c++) {
              double sample = !inside ? outside[c]
                  : c == opaqueChannel ? 1.0 : raster.get(sourceX, sourceY, c);
              values[c] += weight * sample;
            }
          }
        }
        for (int c = 0; c < channels; c++) {
          result.set(x, y, c,
              singlePrecision ? Double.parseDouble(Float.toString((float) values[c])) : values[c]);
        }
      }
    }
    ImageExpr image = ImageExpr.fromRaster(result, source.getOptions(), source.sampleType());
    return image == null ? F.NIL : image;
  }

  /** The string values of <code>Padding</code> which continue the image. */
  static final java.util.Set<String> CONTINUATIONS =
      java.util.Set.of("Fixed", "Periodic", "Reflected", "Reversed");

  /**
   * The index inside <code>0 ... n-1</code> which continues the image at <code>index</code>:
   * <code>"Fixed"</code> repeats the border, <code>"Periodic"</code> the image,
   * <code>"Reflected"</code> mirrors it at the border pixel and <code>"Reversed"</code> at the edge.
   */
  static int continued(int index, int n, String continuation) {
    switch (continuation) {
      case "Fixed":
        return clamp(index, n);
      case "Periodic":
        return Math.floorMod(index, n);
      case "Reflected":
        if (n == 1) {
          return 0;
        }
        int period = 2 * (n - 1);
        int r = Math.floorMod(index, period);
        return r < n ? r : period - r;
      default:
        int r2 = Math.floorMod(index, 2 * n);
        return r2 < n ? r2 : 2 * n - 1 - r2;
    }
  }

  /**
   * <code>function({x, y})</code> as a pair of reals.
   *
   * @return <code>null</code> if the function does not give two reals
   */
  private static double[] position(IExpr function, double x, double y, EvalEngine engine) {
    IExpr mapped = engine.evaluate(F.unaryAST1(function, F.List(F.num(x), F.num(y))));
    if (!mapped.isList2()) {
      return null;
    }
    return new double[] {mapped.first().evalfNaN(), mapped.second().evalfNaN()};
  }

  /**
   * <code>{{x0, x1}, {y0, y1}}</code> as <code>{x0, x1, y0, y1}</code>.
   *
   * @return <code>null</code> unless these are four reals with <code>x0 != x1</code> and
   *         <code>y0 != y1</code>
   */
  private static double[] rectangle(IExpr spec) {
    if (!spec.isList2() || !spec.first().isList2() || !spec.second().isList2()) {
      return null;
    }
    double[] result = new double[] {spec.first().first().evalfNaN(),
        spec.first().second().evalfNaN(), spec.second().first().evalfNaN(),
        spec.second().second().evalfNaN()};
    for (double value : result) {
      if (!Double.isFinite(value)) {
        return null;
      }
    }
    return result[0] == result[1] || result[2] == result[3] ? null : result;
  }

  /**
   * The samples a padding value stands for: a number is the value of every channel, a list with one
   * entry per channel gives each of them, and a colour is converted to the channels of the image.
   *
   * @param colorSpace <code>"HSB"</code> or <code>"CMYK"</code> for an image stored in that space,
   *        <code>null</code> for grey levels or RGB
   * @return <code>null</code> if <code>padding</code> is none of these, or a colour for an image
   *         whose channels are no colour space
   */
  private static double[] paddingSamples(IExpr padding, int channels, String colorSpace) {
    double[] result = new double[channels];
    double number = padding.evalfNaN();
    if (!Double.isNaN(number)) {
      java.util.Arrays.fill(result, number);
      return result;
    }
    if (padding.isList() && padding.argSize() == channels) {
      for (int c = 0; c < channels; c++) {
        result[c] = padding.getAt(c + 1).evalfNaN();
        if (Double.isNaN(result[c])) {
          return null;
        }
      }
      return result;
    }
    float[] rgba = Colors.toRgba(padding);
    if (rgba == null) {
      return null;
    }
    float[] samples;
    if ("HSB".equals(colorSpace)) {
      samples = Colors.rgbToHsb(rgba);
    } else if ("CMYK".equals(colorSpace)) {
      samples = Colors.rgbToCmyk(rgba);
    } else if (channels <= 2) {
      double gray = Colors.grayLevel(rgba[0], rgba[1], rgba[2]);
      return channels == 1 ? new double[] {gray} : new double[] {gray, rgba[3]};
    } else {
      samples = rgba;
    }
    if (channels > samples.length) {
      return null;
    }
    for (int c = 0; c < channels; c++) {
      result[c] = samples[c];
    }
    return result;
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
    if (spec.isAST(S.Scaled, 2) && spec.first().isList2()) {
      // Scaled({sx, sy}) scales the two directions on their own
      double factorX = spec.first().first().evalfNaN();
      double factorY = spec.first().second().evalfNaN();
      if (!(factorX > 0.0) || !(factorY > 0.0)) {
        return null;
      }
      return new int[] {Math.max(1, (int) Math.round(width * factorX)),
          Math.max(1, (int) Math.round(height * factorY))};
    }
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
      if (list.arg1().isList1() && list.arg2().isList1()) {
        // {{wmax}, {hmax}} - as large as fits inside the maximum width and height, keeping the
        // aspect ratio
        double maxWidth = list.arg1().first().evalfNaN();
        double maxHeight = list.arg2().first().evalfNaN();
        if (!(maxWidth >= 1.0) || !(maxHeight >= 1.0) || Double.isInfinite(maxWidth)
            || Double.isInfinite(maxHeight)) {
          return null;
        }
        double factor = Math.min(maxWidth / width, maxHeight / height);
        return new int[] {
            Math.max(1, (int) Math.min(Math.floor(maxWidth), Math.round(width * factor))),
            Math.max(1, (int) Math.min(Math.floor(maxHeight), Math.round(height * factor)))};
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
