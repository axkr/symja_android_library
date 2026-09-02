package org.matheclipse.image.builtin;

import java.awt.image.BufferedImage;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.image.algo.Boof;
import org.matheclipse.image.algo.Kernels;
import org.matheclipse.image.algo.Pixels;
import org.matheclipse.image.expression.data.ImageExpr;

/**
 * Rewriting an image through a Symja function, and cutting it up: <code>ImageApply</code>,
 * <code>ImageFilter</code>, <code>ImageScan</code>, <code>ImagePartition</code> and
 * <code>ImageAssemble</code>.
 *
 * <p>
 * The function is handed samples on the <code>0.0 ... 1.0</code> scale - one number for a greyscale
 * image, a list of channel values for a colour one - and is expected to return the same shape. As
 * with the arithmetic functions the alpha channel is carried through rather than passed in.
 */
public class ImageStructureFunctions {

  private static class Initializer {

    private static void init() {
      S.ImageApply.setEvaluator(new ImageApply());
      S.ImageAssemble.setEvaluator(new ImageAssemble());
      S.ImageFilter.setEvaluator(new ImageFilter());
      S.ImagePartition.setEvaluator(new ImagePartition());
      S.ImageScan.setEvaluator(new ImageScan());
    }
  }

  /** <code>ImageApply(f, image)</code> - replace every sample by <code>f</code> of it. */
  private static class ImageApply extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      BufferedImage image = ImagePropertyFunctions.bufferedImage(ast.arg2());
      if (image == null) {
        return F.NIL;
      }
      IExpr function = ast.arg1();
      int channels = Boof.channels(image);
      int colorChannels = channels == 4 ? 3 : channels;
      int width = image.getWidth();
      int height = image.getHeight();

      float[][] result = new float[width * height][];
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          float[] values = Pixels.pixel(image, x, y, channels);
          IExpr applied =
              engine.evaluate(F.unaryAST1(function, samplesOf(values, colorChannels)));
          float[] replaced = valuesOf(applied, values, colorChannels);
          if (replaced == null) {
            return F.NIL;
          }
          result[y * width + x] = replaced;
        }
      }
      return new ImageExpr(
          Pixels.fromPixels(width, height, channels, (x, y) -> result[y * width + x]), null);
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
   * <code>ImageFilter(f, image, r)</code> - replace every sample by <code>f</code> of the
   * <code>(2r+1) x (2r+1)</code> block of samples around it.
   *
   * <p>
   * The block is extended past the edge of the image by repeating the edge sample, which is what
   * <code>Padding -&gt; "Fixed"</code> means.
   */
  private static class ImageFilter extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      BufferedImage image = ImagePropertyFunctions.bufferedImage(ast.arg2());
      if (image == null) {
        return F.NIL;
      }
      int[] radii = Kernels.toRadii(ast.arg3());
      if (radii == null) {
        return F.NIL;
      }
      IExpr function = ast.arg1();
      int channels = Boof.channels(image);
      int colorChannels = channels == 4 ? 3 : channels;
      int width = image.getWidth();
      int height = image.getHeight();

      float[][] result = new float[width * height][];
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          IExpr block = neighbourhood(image, x, y, radii[0], radii[1], channels, colorChannels);
          IExpr applied = engine.evaluate(F.unaryAST1(function, block));
          float[] replaced =
              valuesOf(applied, Pixels.pixel(image, x, y, channels), colorChannels);
          if (replaced == null) {
            return F.NIL;
          }
          result[y * width + x] = replaced;
        }
      }
      return new ImageExpr(
          Pixels.fromPixels(width, height, channels, (x, y) -> result[y * width + x]), null);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  /**
   * <code>ImageScan(f, image)</code> - evaluate <code>f</code> for every sample and return
   * <code>Null</code>. Only the side effects of <code>f</code> are of interest.
   */
  private static class ImageScan extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      BufferedImage image = ImagePropertyFunctions.bufferedImage(ast.arg2());
      if (image == null) {
        return F.NIL;
      }
      IExpr function = ast.arg1();
      int channels = Boof.channels(image);
      int colorChannels = channels == 4 ? 3 : channels;
      for (int y = 0; y < image.getHeight(); y++) {
        for (int x = 0; x < image.getWidth(); x++) {
          float[] values = Pixels.pixel(image, x, y, channels);
          engine.evaluate(F.unaryAST1(function, samplesOf(values, colorChannels)));
        }
      }
      return S.Null;
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
   * <code>ImagePartition(image, {w, h})</code> - a matrix of the <code>w x h</code> blocks of
   * <code>image</code>, left to right and top to bottom. A partial block at the right or bottom edge
   * is dropped, and <code>ImagePartition(image, {w, h}, {dx, dy})</code> gives the offsets between
   * blocks.
   */
  private static class ImagePartition extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      BufferedImage image = ImagePropertyFunctions.bufferedImage(ast.arg1());
      if (image == null) {
        return F.NIL;
      }
      int[] size = sizeOf(ast.arg2());
      if (size == null || size[0] <= 0 || size[1] <= 0) {
        return F.NIL;
      }
      int[] offset = ast.argSize() >= 3 ? sizeOf(ast.arg3()) : size;
      if (offset == null || offset[0] <= 0 || offset[1] <= 0) {
        return F.NIL;
      }

      IASTAppendable rows = F.ListAlloc(8);
      for (int y = 0; y + size[1] <= image.getHeight(); y += offset[1]) {
        IASTAppendable row = F.ListAlloc(8);
        for (int x = 0; x + size[0] <= image.getWidth(); x += offset[0]) {
          BufferedImage block = image.getSubimage(x, y, size[0], size[1]);
          row.append(new ImageExpr(block, null));
        }
        if (row.argSize() > 0) {
          rows.append(row);
        }
      }
      return rows;
    }

    /** A size given as <code>s</code> or as <code>{w, h}</code>. */
    private static int[] sizeOf(IExpr expr) {
      if (expr.isList()) {
        IAST list = (IAST) expr;
        if (list.argSize() != 2) {
          return null;
        }
        int width = list.arg1().toIntDefault();
        int height = list.arg2().toIntDefault();
        return width == Config.INVALID_INT || height == Config.INVALID_INT //
            ? null
            : new int[] {width, height};
      }
      int side = expr.toIntDefault();
      return side == Config.INVALID_INT ? null : new int[] {side, side};
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
   * <code>ImageAssemble({{image11, image12}, ...})</code> - one image made of a matrix of images.
   *
   * <p>
   * Every image in a row has to have the same height and every image in a column the same width,
   * which is what makes the result rectangular.
   */
  private static class ImageAssemble extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (!arg1.isList()) {
        return F.NIL;
      }
      IAST rows = (IAST) arg1;
      // a flat list of images is assembled into a single row
      boolean singleRow = rows.argSize() > 0 && rows.arg1() instanceof ImageExpr;
      if (singleRow) {
        rows = F.List(rows);
      }

      BufferedImage[][] blocks = new BufferedImage[rows.argSize()][];
      int[] rowHeights = new int[rows.argSize()];
      int[] columnWidths = null;
      for (int i = 0; i < rows.argSize(); i++) {
        IExpr rowExpr = rows.get(i + 1);
        if (!rowExpr.isList()) {
          return F.NIL;
        }
        IAST row = (IAST) rowExpr;
        blocks[i] = new BufferedImage[row.argSize()];
        if (columnWidths == null) {
          columnWidths = new int[row.argSize()];
        } else if (columnWidths.length != row.argSize()) {
          return F.NIL;
        }
        for (int j = 0; j < row.argSize(); j++) {
          BufferedImage block = ImagePropertyFunctions.bufferedImage(row.get(j + 1));
          if (block == null) {
            return F.NIL;
          }
          blocks[i][j] = block;
          if (j == 0) {
            rowHeights[i] = block.getHeight();
          } else if (rowHeights[i] != block.getHeight()) {
            return F.NIL;
          }
          if (i == 0) {
            columnWidths[j] = block.getWidth();
          } else if (columnWidths[j] != block.getWidth()) {
            return F.NIL;
          }
        }
      }
      if (columnWidths == null || columnWidths.length == 0) {
        return F.NIL;
      }

      int totalWidth = 0;
      for (int width : columnWidths) {
        totalWidth += width;
      }
      int totalHeight = 0;
      for (int height : rowHeights) {
        totalHeight += height;
      }

      boolean anyColor = false;
      boolean anyAlpha = false;
      for (BufferedImage[] row : blocks) {
        for (BufferedImage block : row) {
          anyColor |= Boof.isColor(block);
          anyAlpha |= Boof.hasAlpha(block);
        }
      }
      int channels = anyColor ? (anyAlpha ? 4 : 3) : 1;

      final BufferedImage[][] grid = blocks;
      final int[] widths = columnWidths;
      final int[] heights = rowHeights;
      return new ImageExpr(Pixels.fromPixels(totalWidth, totalHeight, channels, (x, y) -> {
        int row = 0;
        int localY = y;
        while (localY >= heights[row]) {
          localY -= heights[row++];
        }
        int column = 0;
        int localX = x;
        while (localX >= widths[column]) {
          localX -= widths[column++];
        }
        BufferedImage block = grid[row][column];
        float[] values = Pixels.pixel(block, localX, localY, Boof.channels(block));
        return spread(values, channels);
      }), null);
    }

    /** Widen a greyscale pixel to the channel count of the assembled image. */
    private static float[] spread(float[] values, int channels) {
      if (values.length == channels) {
        return values;
      }
      float[] result = new float[channels];
      for (int c = 0; c < channels; c++) {
        result[c] = c < values.length ? values[c] : values[values.length - 1];
      }
      if (values.length == 1) {
        java.util.Arrays.fill(result, 0, Math.min(3, channels), values[0]);
        if (channels == 4) {
          result[3] = 255.0f;
        }
      }
      return result;
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

  // -------------------------------------------------------------------- internals

  /** The samples of one pixel as the argument of the user function. */
  private static IExpr samplesOf(float[] values, int colorChannels) {
    if (colorChannels == 1) {
      return F.num(values[0] / 255.0);
    }
    IASTAppendable list = F.ListAlloc(colorChannels);
    for (int c = 0; c < colorChannels; c++) {
      list.append(F.num(values[c] / 255.0));
    }
    return list;
  }

  /**
   * The value the user function returned, back on the 0 ... 255 scale, with the alpha channel of
   * <code>original</code> carried through.
   *
   * @return <code>null</code> if the function did not return a number or a list of the right length
   */
  private static float[] valuesOf(IExpr applied, float[] original, int colorChannels) {
    float[] result = original.clone();
    if (colorChannels == 1) {
      double value = applied.evalfNaN();
      if (Double.isNaN(value)) {
        return null;
      }
      result[0] = (float) (255.0 * value);
      return result;
    }
    if (!applied.isList() || ((IAST) applied).argSize() != colorChannels) {
      return null;
    }
    IAST list = (IAST) applied;
    for (int c = 0; c < colorChannels; c++) {
      double value = list.get(c + 1).evalfNaN();
      if (Double.isNaN(value)) {
        return null;
      }
      result[c] = (float) (255.0 * value);
    }
    return result;
  }

  /** The block of samples around one pixel, with the edge sample repeated past the border. */
  private static IExpr neighbourhood(BufferedImage image, int x, int y, int radiusX, int radiusY,
      int channels, int colorChannels) {
    IASTAppendable rows = F.ListAlloc(2 * radiusY + 1);
    for (int dy = -radiusY; dy <= radiusY; dy++) {
      IASTAppendable row = F.ListAlloc(2 * radiusX + 1);
      int sampleY = clamp(y + dy, image.getHeight());
      for (int dx = -radiusX; dx <= radiusX; dx++) {
        int sampleX = clamp(x + dx, image.getWidth());
        row.append(samplesOf(Pixels.pixel(image, sampleX, sampleY, channels), colorChannels));
      }
      rows.append(row);
    }
    return rows;
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

  private ImageStructureFunctions() {}
}
