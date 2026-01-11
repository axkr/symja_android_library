package org.matheclipse.image.builtin;

import java.awt.image.BufferedImage;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.image.expression.data.ImageExpr;

public class ImageFunctions {

  private static class Initializer {

    private static void init() {
      S.Image.setEvaluator(new Image());
      S.ImageData.setEvaluator(new ImageData());
      S.ImageDimensions.setEvaluator(new ImageDimensions());
    }
  }

  private static class Image extends AbstractEvaluator {

    public Image() {}

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // Argument checking: Image[data, opts...] or Image[data, "type", opts...]
      if (ast.argSize() >= 1 && ast.arg1().isAST()) {
        try {
          IAST data = (IAST) ast.arg1();
          String colorSpace = "Automatic"; // Default

          // Simple option parsing (looks for Rule(ColorSpace, "value"))
          // Iterate starting from arg2
          for (int i = 2; i < ast.size(); i++) {
            IExpr arg = ast.get(i);
            if (arg.isRuleAST()) {
              IExpr key = ((IAST) arg).arg1();
              if (key == S.ColorSpace) {
                colorSpace = ((IAST) arg).arg2().toString();
              }
            }
          }

          ImageExpr imageExpr = ImageExpr.toImageExpr(data, colorSpace);
          if (imageExpr != null) {
            return imageExpr;
          }
        } catch (RuntimeException rex) {
          rex.printStackTrace();
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return IFunctionEvaluator.ARGS_1_2;
    }
  }

  /**
   * <p>
   * Get the array of pixel values of an image.
   * </p>
   */
  private static class ImageData extends AbstractEvaluator {

    public ImageData() {}

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (!(arg1 instanceof ImageExpr)) {
        return F.NIL;
      }
      ImageExpr imgExpr = (ImageExpr) arg1;

      // 1. Determine target type
      String type = "Real32"; // Default
      if (ast.argSize() >= 2) {
        IExpr arg2 = ast.arg2();
        if (arg2.isString()) {
          type = arg2.toString();
        } else {
          return F.NIL;
        }
      }

      // 2. OPTIMIZATION: If we have the original matrix and the user wants Reals,
      // return the original high-precision data directly.
      IAST originalData = imgExpr.getMatrix();
      if (originalData != null && (type.equals("Real64") || type.equals("Real32"))) {
        // We assume the original data is in the correct format (0-1 Reals).
        // You might want to add a check here to ensure it's converted to Double if it was Integer
        // input.
        // But generally, returning the stored AST is the correct "Lossless" behavior.
        return originalData;
      }

      // 3. Fallback: Convert from the Bitmap (Lossy for float inputs)
      BufferedImage image = imgExpr.getBufferedImage();
      if (image == null) {
        return F.NIL;
      }
      return convertToData(image, type);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    // ... [The convertToData method remains the same as provided in previous step] ...
    private IAST convertToData(BufferedImage image, String type) {
      int width = image.getWidth();
      int height = image.getHeight();
      boolean hasAlpha = image.getColorModel().hasAlpha();
      int bands = image.getRaster().getNumBands();

      boolean isColor = (bands >= 3);
      int channelCount = isColor ? (hasAlpha ? 4 : 3) : 1;

      IASTAppendable result = F.ListAlloc(height);
      final double scaleBit16 = 65535.0 / 255.0;

      for (int y = 0; y < height; y++) {
        IASTAppendable rowList = F.ListAlloc(width);
        for (int x = 0; x < width; x++) {
          int pixel = image.getRGB(x, y);

          int alpha = (pixel >> 24) & 0xff;
          int red = (pixel >> 16) & 0xff;
          int green = (pixel >> 8) & 0xff;
          int blue = (pixel) & 0xff;

          IASTAppendable pixelExpr = isColor ? F.ListAlloc(channelCount) : null;
          int loopMax = isColor ? (hasAlpha ? 4 : 3) : 1;

          for (int c = 0; c < loopMax; c++) {
            int val = 0;
            if (c == 0)
              val = red;
            else if (c == 1)
              val = green;
            else if (c == 2)
              val = blue;
            else if (c == 3)
              val = alpha;

            IExpr componentValue;
            switch (type) {
              case "Bit":
                componentValue = (val > 127) ? F.C1 : F.C0;
                break;
              case "Byte":
                componentValue = F.ZZ(val);
                break;
              case "Bit16":
                componentValue = F.ZZ((int) (val * scaleBit16));
                break;
              case "Real32":
              case "Real64":
              default:
                componentValue = F.num(val / 255.0);
                break;
            }

            if (isColor)
              pixelExpr.append(componentValue);
            else
              rowList.append(componentValue);
          }
          if (isColor)
            rowList.append(pixelExpr);
        }
        result.append(rowList);
      }
      return result;
    }
  }


  /**
   * <p>
   * Get the dimensions <code>{width, height}</code> of an image.
   * </p>
   * 
   */
  private static class ImageDimensions extends AbstractEvaluator {

    public ImageDimensions() {}

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1 instanceof ImageExpr) {
        BufferedImage bufferedImage = ((ImageExpr) arg1).getBufferedImage();
        if (bufferedImage != null) {
          // ImageDimensions returns {width, height} (Cartesian coordinates)
          // unlike Dimensions[ImageData[...]] which returns {height, width} (Matrix coordinates)
          return F.List(F.ZZ(bufferedImage.getWidth()), F.ZZ(bufferedImage.getHeight()));
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private ImageFunctions() {}
}
