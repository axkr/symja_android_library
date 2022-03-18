package org.matheclipse.core.builtin;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.convert.RGBColor;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.ImageExpr;
import org.matheclipse.core.img.ColorFormat;
import org.matheclipse.core.img.ImageFormat;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.io.Extension;
import org.matheclipse.core.reflection.system.Export;
import it.unimi.dsi.fastutil.ints.IntArrayList;

public class ImageFunctions {
  private static final Logger LOGGER = LogManager.getLogger();

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.Image.setEvaluator(new Image());
      S.MaxFilter.setEvaluator(new MaxFilter());
      S.MeanFilter.setEvaluator(new MeanFilter());
      S.MedianFilter.setEvaluator(new MedianFilter());
      S.MinFilter.setEvaluator(new MinFilter());
    }
  }

  private static class Image extends AbstractEvaluator {

    public Image() {}

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isAST()) {
        try {
          IAST image = (IAST) ast.arg1();
          IntArrayList dimensions = LinearAlgebra.dimensions(image);
          if (dimensions != null) {
            if (dimensions.size() == 2) {
              BufferedImage buffer = ImageFormat.toIntARGB(image);
              if (buffer != null) {
                final IAST byteMatrix = ImageFormat.from(buffer);
                IAST resultMatrix = F.matrix(
                    (i, j) -> F.num((byteMatrix.getPart(i + 1, j + 1).evalDouble()) / 255.0),
                    dimensions.getInt(0), dimensions.getInt(1));
                Extension format = Extension.PNG;
                try (OutputStream outputStream = new ByteArrayOutputStream()) {
                  Export.exportImage(outputStream, resultMatrix, format);
                  return new ImageExpr(buffer, resultMatrix, format);
                } catch (IOException e) {
                  // e.printStackTrace();
                }
                return F.NIL;
              }
            } else if (dimensions.size() == 3) {
              BufferedImage buffer = ImageFormat.toIntFormat(image, BufferedImage.TYPE_INT_ARGB);
              if (buffer != null) {
                IAST byteMatrix = ImageFormat.from(buffer);
                IAST resultMatrix = F.matrix((i, j) -> {
                  IAST part = (IAST) byteMatrix.getPart(i + 1, j + 1);
                  RGBColor color = ColorFormat.toColor(part);
                  float[] floatValues = color.getColorComponents(null);
                  return F.List(F.num(floatValues[0]), F.num(floatValues[1]),
                      F.num(floatValues[2]));
                }, dimensions.getInt(0), dimensions.getInt(1));
                Extension format = Extension.PNG;
                try (OutputStream outputStream = new ByteArrayOutputStream()) {
                  Export.exportImage(outputStream, resultMatrix, format);
                  return new ImageExpr(buffer, resultMatrix, format);
                } catch (IOException e) {
                  // e.printStackTrace();
                }
                return F.NIL;
              }
            }
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

  private static class MinFilter extends AbstractEvaluator {

    protected IExpr filterHead() {
      return S.Min;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        if (ast.arg1().isList()) {
          IAST list = (IAST) ast.arg1();
          final int radius = ast.arg2().toIntDefault();
          if (radius >= 0) {
            return filterHead(list, radius, filterHead(), engine);
          }
        }
      } catch (RuntimeException rex) {
        LOGGER.debug("MinFilter.evaluate() failed", rex);
      }
      return F.NIL;
    }

    private static IExpr filterHead(IAST list, final int radius, IExpr filterHead,
        EvalEngine engine) {
      final IASTMutable result = list.copy();
      final int size = list.size();
      list.forEach((x, i) -> result.set(i, engine.evaluate( //
          F.unaryAST1( //
              filterHead, //
              list.slice(Math.max(1, i - radius), Math.min(size, i + radius + 1)) //
          ))));
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  private static class MaxFilter extends MinFilter {
    @Override
    protected IExpr filterHead() {
      return S.Max;
    }
  }

  private static class MeanFilter extends MinFilter {
    @Override
    protected IExpr filterHead() {
      return S.Mean;
    }
  }

  private static class MedianFilter extends MinFilter {
    @Override
    protected IExpr filterHead() {
      return S.Median;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private ImageFunctions() {}
}
