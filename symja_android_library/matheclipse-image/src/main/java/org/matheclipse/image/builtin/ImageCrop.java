package org.matheclipse.image.builtin;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.image.expression.data.ImageExpr;

/**
 * <p>
 * Crops an image to a specified size or removes uniform borders.
 * </p>
 * <p>
 * The returned {@link ImageExpr} passes null for the data matrix. Recalculating the high-precision
 * matrix (if it existed) for the cropped area is computationally expensive and complex (requires
 * slicing nested lists). If the user calls {@link S#ImageData} on the result, it will be
 * regenerated from the cropped BufferedImage.
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
      return autoCrop(src, imgExpr.getMatrix());
    }

    // Case: ImageCrop[image, {width, height}, {alignX, alignY} (optional)]
    int targetW = src.getWidth();
    int targetH = src.getHeight();

    // Parse Size {w, h}
    if (ast.argSize() >= 2) {
      IExpr arg2 = ast.arg2();
      if (arg2.isList() && arg2.isAST2()) {
        targetW = ((IAST) arg2).arg1().toIntDefault(targetW);
        targetH = ((IAST) arg2).arg2().toIntDefault(targetH);
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

    if (ast.argSize() >= 3) {
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

    return cropFixedSize(src, targetW, targetH, alignX, alignY);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_3;
  }

  /**
   * Auto-crops borders of uniform color.
   */
  private static IExpr autoCrop(BufferedImage src, IAST originalData) {
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

    BufferedImage subImg = src.getSubimage(minX, minY, newW, newH);
    // Note: We lose the originalData matrix correlation here unless we also slice the matrix.
    // For simplicity, we pass null as the matrix argument for the new crop.
    return new ImageExpr(subImg, null);
  }

  /**
   * Crops (or pads) to a fixed size with alignment. alignX: -1 (Left), 0 (Center), 1 (Right)
   * alignY: -1 (Bottom), 0 (Center), 1 (Top)
   */
  private static IExpr cropFixedSize(BufferedImage src, int targetW, int targetH, double alignX,
      double alignY) {
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
    try {
      return expr.evalf();
    } catch (RuntimeException rex) {
      return 0.0;
    }
  }

  @Override
  public void setUp(final ISymbol newSymbol) {}
}
