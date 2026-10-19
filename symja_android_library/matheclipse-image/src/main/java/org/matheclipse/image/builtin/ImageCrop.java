package org.matheclipse.image.builtin;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.image.expression.data.ImageExpr;

/**
 * <p>
 * Crops an image to a specified size or removes uniform borders.
 * </p>
 * <p>
 * A crop which lies inside of the image cuts the matrix the image was built from along with the
 * pixels, so {@link S#ImageData} of the result has the samples and the type of the image. A crop
 * to a larger size pads the image and is read back off the bitmap.
 * </p>
 */
public class ImageCrop extends AbstractEvaluator {

  public ImageCrop() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    // Validate First Argument (Image)
    IExpr arg1 = ast.arg1();
    if (!(arg1 instanceof ImageExpr)) {
      return F.NIL;
    }
    ImageExpr imgExpr = (ImageExpr) arg1;
    BufferedImage src = imgExpr.getBufferedImage();
    if (src == null) {
      return F.NIL;
    }

    // Case: ImageCrop(image) -> Auto-crop uniform borders
    if (ast.argSize() == 1) {
      return autoCrop(imgExpr, src);
    }

    // the option Padding -> p, which may stand in place of the alignment
    IExpr padding = F.NIL;
    int positional = ast.argSize();
    for (int i = ast.argSize(); i >= 2 && ast.get(i).isRuleAST(); i--) {
      if (ast.get(i).first() == S.Padding) {
        padding = ast.get(i).second();
      }
      positional = i - 1;
    }

    // Case: ImageCrop[image, {width, height}, {alignX, alignY} (optional)]
    int targetW = src.getWidth();
    int targetH = src.getHeight();

    // Parse Size {w, h}
    if (positional >= 2) {
      IExpr arg2 = ast.arg2();
      if (arg2.isList() && arg2.isAST2()) {
        // a size which isn't a whole number of pixels is rounded
        targetW = roundedSize(arg2.first(), targetW);
        targetH = roundedSize(arg2.second(), targetH);
      } else if (arg2.isInteger()) {
        // Support square crop if single integer provided
        int size = arg2.toIntDefault();
        if (size > 0) {
          targetW = size;
          targetH = size;
        }
      }
    }

    // Parse Alignment {x, y} (Default: Center {0, 0})
    // Range: -1 (Left/Bottom) to +1 (Right/Top)
    double alignX = 0.0;
    double alignY = 0.0;

    if (positional >= 3) {
      IExpr arg3 = ast.arg3();
      if (arg3.isList() && arg3.isAST2()) {
        alignX = evalDouble(arg3.first());
        alignY = evalDouble(arg3.second());
      } else if (arg3.isBuiltInSymbol()) {
        // Handle symbolic alignment shortcuts if necessary (Left, Right, etc.)
        if (arg3 == S.Left)
          alignX = -1.0;
        else if (arg3 == S.Right)
          alignX = 1.0;
        else if (arg3 == S.Top)
          alignY = 1.0;
        else if (arg3 == S.Bottom)
          alignY = -1.0;
        else if (arg3 == S.Center) {
          alignX = 0.0;
          alignY = 0.0;
        }
      }
    }

    if (padding.isPresent() && (targetW > src.getWidth() || targetH > src.getHeight())) {
      IExpr padded = padded(imgExpr, targetW, targetH, alignX, alignY, padding, engine);
      if (padded.isPresent()) {
        return padded;
      }
    }
    return cropFixedSize(imgExpr, src, targetW, targetH, alignX, alignY);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_4;
  }

  /** A size in pixels, rounded to a whole number; <code>def</code> if it isn't a positive one. */
  private static int roundedSize(IExpr size, int def) {
    double value = size.evalfNaN();
    if (!(value >= 0.5)) {
      return def;
    }
    return (int) Math.round(value);
  }

  /**
   * The image cropped or padded to <code>targetW</code> x <code>targetH</code>, the space it doesn't
   * cover filled with the value of <code>padding</code> in each channel. The alignment
   * <code>-1</code>, <code>0</code>, <code>1</code> puts the image at the left/bottom, the centre
   * or the right/top; an odd remainder in the centre goes to the top and the right.
   *
   * @return {@link F#NIL} if the padding is no colour or number
   */
  private static IExpr padded(ImageExpr image, int targetW, int targetH, double alignX,
      double alignY, IExpr padding, EvalEngine engine) {
    // the samples on the scale of the image's own type, which the result keeps
    final String type = image.sampleType();
    IExpr data = engine.evaluate(F.binaryAST2(S.ImageData, image, F.stringx(type)));
    int[] dims = data.isMatrix(false);
    if (dims == null) {
      return F.NIL;
    }
    int srcH = dims[0];
    int srcW = dims[1];
    IExpr first = ((IAST) data).getPart(1, 1);
    int channels = first.isList() ? first.argSize() : 1;
    double[] fill = paddingChannels(padding, channels, engine);
    if (fill == null) {
      return F.NIL;
    }
    // one padding pixel, shared by every position it fills
    IExpr padPixel;
    if (channels == 1) {
      padPixel = sample(fill[0], type);
    } else {
      IASTAppendable pixel = F.ListAlloc(channels);
      for (int c = 0; c < channels; c++) {
        pixel.append(sample(fill[c], type));
      }
      padPixel = pixel;
    }
    // the position of the image in the result; in a direction in which the image is larger, it
    // is cut as a crop without padding cuts it
    final double factorX = (alignX + 1.0) / 2.0;
    final double factorY = (alignY + 1.0) / 2.0;
    int left = targetW >= srcW ? (int) Math.floor((targetW - srcW) * factorX)
        : -(int) Math.round((srcW - targetW) * factorX);
    // the space below the image is measured from the bottom, as the image coordinates are
    int top = targetH >= srcH
        ? targetH - srcH - (int) Math.floor((targetH - srcH) * factorY)
        : -(int) Math.round((srcH - targetH) * (1.0 - factorY));
    IASTAppendable rows = F.ListAlloc(targetH);
    for (int y = 0; y < targetH; y++) {
      IASTAppendable row = F.ListAlloc(targetW);
      for (int x = 0; x < targetW; x++) {
        int sy = y - top;
        int sx = x - left;
        if (sy >= 0 && sy < srcH && sx >= 0 && sx < srcW) {
          row.append(((IAST) data).getPart(sy + 1, sx + 1));
        } else {
          row.append(padPixel);
        }
      }
      rows.append(row);
    }
    ImageExpr result = ImageExpr.toImageExpr(rows, image.getOptions(), type);
    return result == null ? F.NIL : result;
  }

  /** A channel value of <code>0 ... 1</code> on the scale of the image type. */
  private static IExpr sample(double value, String type) {
    // the integer types have no samples outside of 0 ... 1
    final double clamped = Math.max(0.0, Math.min(1.0, value));
    switch (type) {
      case "Bit":
        return clamped >= 0.5 ? F.C1 : F.C0;
      case "Byte":
        return F.ZZ((int) Math.round(255.0 * clamped));
      case "Bit16":
        return F.ZZ((int) Math.round(65535.0 * clamped));
      default:
        return F.num(value);
    }
  }

  /** The channel values of a padding colour or gray level, as many as the image has. */
  private static double[] paddingChannels(IExpr padding, int channels, EvalEngine engine) {
    double[] fill = new double[channels];
    double value = padding.evalfNaN();
    if (!Double.isNaN(value)) {
      java.util.Arrays.fill(fill, value);
      return fill;
    }
    float[] rgba = org.matheclipse.image.algo.Colors.toRgba(engine.evaluate(padding));
    if (rgba == null) {
      return null;
    }
    if (channels <= 2) {
      // the gray level of the colour, and its opacity for an image with an alpha channel
      fill[0] = 0.299 * rgba[0] + 0.587 * rgba[1] + 0.114 * rgba[2];
      if (channels == 2) {
        fill[1] = rgba[3];
      }
      return fill;
    }
    for (int c = 0; c < channels; c++) {
      fill[c] = c < 3 ? rgba[c] : rgba[3];
    }
    return fill;
  }

  /**
   * Auto-crops borders of uniform color.
   */
  private static IExpr autoCrop(ImageExpr image, BufferedImage src) {
    int w = src.getWidth();
    int h = src.getHeight();

    // Determine background color from top-left pixel
    // (A more robust implementation might check all 4 corners)
    int bgColor = src.getRGB(0, 0);

    int minX = 0;
    int maxX = w - 1;
    int minY = 0;
    int maxY = h - 1;

    // Scan Top-Down
    boolean found = false;
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        if (src.getRGB(x, y) != bgColor) {
          minY = y;
          found = true;
          break;
        }
      }
      if (found)
        break;
    }
    // If image is completely uniform
    if (!found)
      return new ImageExpr(new BufferedImage(1, 1, src.getType()), null);

    // Scan Bottom-Up
    found = false;
    for (int y = h - 1; y >= 0; y--) {
      for (int x = 0; x < w; x++) {
        if (src.getRGB(x, y) != bgColor) {
          maxY = y;
          found = true;
          break;
        }
      }
      if (found)
        break;
    }

    // Scan Left-Right (within Y bounds)
    found = false;
    for (int x = 0; x < w; x++) {
      for (int y = minY; y <= maxY; y++) {
        if (src.getRGB(x, y) != bgColor) {
          minX = x;
          found = true;
          break;
        }
      }
      if (found)
        break;
    }

    // Scan Right-Left (within Y bounds)
    found = false;
    for (int x = w - 1; x >= 0; x--) {
      for (int y = minY; y <= maxY; y++) {
        if (src.getRGB(x, y) != bgColor) {
          maxX = x;
          found = true;
          break;
        }
      }
      if (found)
        break;
    }

    int newW = maxX - minX + 1;
    int newH = maxY - minY + 1;

    if (newW <= 0 || newH <= 0) {
      return new ImageExpr(new BufferedImage(1, 1, src.getType()), null);
    }

    // the matrix the image was built from is cut with it
    return ImageGeometryFunctions.part(image, src, minX, minY, newW, newH);
  }

  /**
   * Crops (or pads) to a fixed size with alignment. alignX: -1 (Left), 0 (Center), 1 (Right)
   * alignY: -1 (Bottom), 0 (Center), 1 (Top)
   */
  private static IExpr cropFixedSize(ImageExpr image, BufferedImage src, int targetW,
      int targetH, double alignX, double alignY) {
    int srcW = src.getWidth();
    int srcH = src.getHeight();

    // Calculate offsets
    // Formula X: Start = (srcW - targetW) * factor.
    // Map -1..1 to 0..1 for linear interpolation factor
    // Left (-1) -> factor 0.0 -> Start 0 (Keep Left) -> Crop Right
    // Center (0) -> factor 0.5 -> Start Center
    // Right (1) -> factor 1.0 -> Start (srcW-targetW) (Keep Right) -> Crop Left
    double factorX = (alignX + 1.0) / 2.0;

    // Formula Y (Java Coordinates: 0 is Top, H is Bottom)
    // -1 is Bottom, 1 is Top.
    // Map +1 (Top) -> factor 0.0 (Y=0)
    // Map -1 (Bottom) -> factor 1.0 (Y=srcH-targetH)
    double factorY = 1.0 - ((alignY + 1.0) / 2.0);

    int x = (int) Math.round((srcW - targetW) * factorX);
    int y = (int) Math.round((srcH - targetH) * factorY);

    if (targetW > 0 && targetH > 0 && x >= 0 && y >= 0 && x + targetW <= srcW
        && y + targetH <= srcH) {
      // a crop inside of the image keeps its channels, and the matrix it was built from
      return ImageGeometryFunctions.part(image, src, x, y, targetW, targetH);
    }

    // Create new image
    // Use TYPE_INT_ARGB to support transparency if padding is needed
    BufferedImage result = new BufferedImage(targetW, targetH, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = result.createGraphics();

    // If source is smaller, we might need to fill background or leave transparent.
    // Defaulting to transparent (0,0,0,0) which is standard for ARGB new images.

    // Draw source image at calculated offset
    // -x and -y because if x>0 (Crop), we draw the image shifted left.
    // If x<0 (Padding), we draw the image shifted right (positive draw coordinate).
    g2d.drawImage(src, -x, -y, null);
    g2d.dispose();

    return new ImageExpr(result, null);
  }

  private static double evalDouble(IExpr expr) {
    double d = expr.evalfNaN();
    return Double.isNaN(d) ? 0.0 : d;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {}
}
