package org.matheclipse.image.algo;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.SVGGraphics;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.image.expression.data.ImageExpr;
import org.matheclipse.image.expression.data.SVG2BufferedImage;

/**
 * The pixels a <code>Masking</code> option lets a function change.
 *
 * <p>
 * A mask is read as the image it would be: a pixel is included where the mask is positive. An image
 * or a matrix smaller than the target is centred on it, which is where the reference places a region
 * of interest, and a <code>Graphics</code> is drawn at the target's size, the pixels it draws on
 * being the included ones.
 */
public final class Masks {

  /** The answer for a mask that cannot be read, as distinct from none at all. */
  public static final boolean[][] UNREADABLE = new boolean[0][0];

  /** How dark a drawn pixel of a rasterized mask is at least, on <code>0 ... 255</code>. */
  private static final int DRAWN = 128;

  /**
   * The mask, row by row from the top, or <code>null</code> for <code>All</code>,
   * <code>None</code> and <code>Automatic</code>, which include every pixel.
   */
  public static boolean[][] of(IExpr spec, int width, int height) {
    if (spec == S.All || spec.isNone() || spec == S.Automatic) {
      return null;
    }
    if (spec instanceof ImageExpr) {
      BufferedImage image = ((ImageExpr) spec).getBufferedImage();
      return centred(positive(image, 0), width, height);
    }
    if (spec.isAST() && (spec.isGraphicsObject() || spec.isAST(S.Graphics))) {
      BufferedImage image = rasterize((IAST) spec, width, height);
      return image == null ? UNREADABLE : centred(positive(image, DRAWN), width, height);
    }
    if (spec.isListOfLists()) {
      IAST rows = (IAST) spec;
      int cols = ((IAST) rows.arg1()).argSize();
      boolean[][] mask = new boolean[rows.argSize()][cols];
      for (int r = 0; r < rows.argSize(); r++) {
        IAST row = (IAST) rows.get(r + 1);
        if (row.argSize() != cols) {
          return UNREADABLE;
        }
        for (int c = 0; c < cols; c++) {
          double v = row.get(c + 1).evalfNaN();
          if (Double.isNaN(v)) {
            return UNREADABLE;
          }
          mask[r][c] = v > 0;
        }
      }
      return centred(mask, width, height);
    }
    return UNREADABLE;
  }

  /**
   * Where an image is positive: any sample above zero for a bitmap mask, and darker than
   * <code>threshold</code> for a drawing on a white ground.
   */
  private static boolean[][] positive(BufferedImage image, int threshold) {
    int width = image.getWidth();
    int height = image.getHeight();
    int channels = Boof.channels(image);
    boolean[][] mask = new boolean[height][width];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        float[] values = Pixels.pixel(image, x, y, channels);
        int colors = channels == 4 ? 3 : channels;
        double sum = 0;
        for (int c = 0; c < colors; c++) {
          sum += values[c];
        }
        double level = sum / colors;
        mask[y][x] = threshold == 0 ? level > 0 : level < threshold;
      }
    }
    return mask;
  }

  /** A mask of any size placed with its centre on the target's; outside it nothing is included. */
  private static boolean[][] centred(boolean[][] mask, int width, int height) {
    int rows = mask.length;
    int cols = rows > 0 ? mask[0].length : 0;
    if (rows == height && cols == width) {
      return mask;
    }
    boolean[][] result = new boolean[height][width];
    int dy = (height - rows) / 2;
    int dx = (width - cols) / 2;
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int r = y - dy;
        int c = x - dx;
        result[y][x] = r >= 0 && r < rows && c >= 0 && c < cols && mask[r][c];
      }
    }
    return result;
  }

  /** The graphic drawn to fill exactly <code>width</code> by <code>height</code> pixels. */
  private static BufferedImage rasterize(IAST graphics, int width, int height) {
    IASTAppendable sized = graphics.copyAppendable();
    sized.append(F.Rule(S.ImageSize, F.list(F.ZZ(width), F.ZZ(height))));
    sized.append(F.Rule(S.AspectRatio, S.Full));
    sized.append(F.Rule(S.PlotRangePadding, S.None));
    sized.append(F.Rule(S.ImagePadding, S.None));
    sized.append(F.Rule(S.Background, S.White));
    String svg = new SVGGraphics(width, height).toSVG(sized);
    if (svg == null) {
      return null;
    }
    BufferedImage drawn = SVG2BufferedImage.createBufferedImage(svg);
    if (drawn == null) {
      return null;
    }
    if (drawn.getWidth() == width && drawn.getHeight() == height) {
      return drawn;
    }
    BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = scaled.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    g.drawImage(drawn, 0, 0, width, height, null);
    g.dispose();
    return scaled;
  }

  private Masks() {}
}
