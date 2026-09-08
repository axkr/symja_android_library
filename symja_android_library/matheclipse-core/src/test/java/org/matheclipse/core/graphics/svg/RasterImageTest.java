package org.matheclipse.core.graphics.svg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.graphics.SVGGraphics;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * A raster that would need more shapes than the renderer allows is drawn as one embedded bitmap.
 *
 * <p>
 * The bitmap is written by {@link PngEncoder} rather than by {@code javax.imageio}, so these tests
 * decode it with {@code javax.imageio} to check the two agree: nothing else in the build would
 * notice a PNG that is subtly malformed, because the browser and the SVG rasterizer both fail
 * quietly by drawing nothing.
 */
public class RasterImageTest {

  private static ExprEvaluator evaluator;

  private static final Pattern DATA_URI =
      Pattern.compile("href=\"data:image/png;base64,([A-Za-z0-9+/=]+)\"");

  @BeforeAll
  public static void setUpEngine() {
    Locale.setDefault(Locale.US);
    Config.SERVER_MODE = false;
    // a raster of one cell per data point is a large expression by construction
    Config.MAX_AST_SIZE = Integer.MAX_VALUE;
    Config.MAX_MATRIX_DIMENSION_SIZE = Integer.MAX_VALUE;
    try {
      F.await();
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException(ie);
    }
    EvalEngine engine = new EvalEngine(true);
    EvalEngine.set(engine);
    engine.init();
    evaluator = new ExprEvaluator(engine, false, (short) 100);
    evaluator.eval("ClearAll(a,b,c,i,j,k,n,r,s,t,u,v,w,x,y,z)");
  }

  // ------------------------------------------------------------------ encoder

  /** Every channel of every pixel has to survive the encoding, transparency included. */
  @Test
  public void testEncodedPixelsRoundTrip() {
    int width = 7;
    int height = 5;
    int[] argb = new int[width * height];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int alpha = (x + y) % 4 == 0 ? 0 : 40 * (x + y) % 256;
        argb[y * width + x] =
            (alpha << 24) | ((x * 31) % 256 << 16) | ((y * 51) % 256 << 8) | ((x * y * 7) % 256);
      }
    }

    BufferedImage decoded = decode(PngEncoder.dataUri(argb, width, height));
    assertEquals(width, decoded.getWidth());
    assertEquals(height, decoded.getHeight());
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int expected = argb[y * width + x];
        int actual = decoded.getRGB(x, y);
        if ((expected >>> 24) == 0) {
          // a fully transparent pixel keeps only its alpha, the colour under it is not defined
          assertEquals(0, actual >>> 24, "pixel " + x + "," + y + " should be transparent");
        } else {
          assertEquals(expected, actual, "pixel " + x + "," + y);
        }
      }
    }
  }

  /** A single row and a single column are both legal images. */
  @Test
  public void testEncodesDegenerateShapes() {
    BufferedImage row = decode(PngEncoder.dataUri(new int[] {0xff102030, 0xff405060}, 2, 1));
    assertEquals(2, row.getWidth());
    assertEquals(1, row.getHeight());
    assertEquals(0xff102030, row.getRGB(0, 0));

    BufferedImage column = decode(PngEncoder.dataUri(new int[] {0xff102030, 0xff405060}, 1, 2));
    assertEquals(1, column.getWidth());
    assertEquals(2, column.getHeight());
    assertEquals(0xff405060, column.getRGB(0, 1));
  }

  // ----------------------------------------------------------------- renderer

  /**
   * A picture of a few flat regions stays vector: the rectangles are crisp at any zoom and merging
   * equal neighbours keeps them few, however many cells the data has.
   */
  @Test
  public void testFlatRasterStaysVector() {
    String small = render("ArrayPlot[Table[Mod[i + j, 2], {i, 10}, {j, 10}]]");
    assertTrue(small.contains("<rect"), "a small array should be drawn as rectangles");
    assertFalse(small.contains("<image"), "a small array should not need a bitmap");

    // 8000 cells, but blocks of ten leave only about twenty runs in each of the forty rows
    String blocks =
        render("ArrayPlot[Table[Mod[Floor[i/10] + Floor[j/10], 2], {i, 40}, {j, 200}]]");
    assertFalse(blocks.contains("<image"),
        "the cell count does not decide: flat blocks merge into few rectangles and need no bitmap");
  }

  /** A picture whose every cell differs is drawn as a bitmap instead of as thousands of shapes. */
  @Test
  public void testGradientRasterBecomesABitmap() {
    String svg = render("ListDensityPlot[Table[Sin[i]*Cos[j], {i, 10}, {j, 10}]]");
    assertTrue(svg.contains("<image"), "a gradient should be drawn as a bitmap");
    assertTrue(svg.length() < 200_000,
        "the bitmap should keep the document small, was " + svg.length() + " characters");
    assertNotNull(decode(dataUri(svg)), "the embedded bitmap has to be a readable PNG");
  }

  /** The bitmap holds one pixel per cell: switching to it must not cost resolution. */
  @Test
  public void testBitmapKeepsEveryCell() {
    BufferedImage image =
        decode(dataUri(render("ArrayPlot[Table[Mod[i*j, 7], {i, 199}, {j, 199}]]")));
    assertEquals(199, image.getWidth());
    assertEquals(199, image.getHeight());
  }

  /**
   * The cells are given bottom row first and a bitmap is written top row first, so the two have to
   * be turned over on the way. {@code ArrayPlot} draws the first row of its argument at the top.
   */
  @Test
  public void testBitmapRowsRunTopFirst() {
    // one wide black band on top of a white one, at a size that forces the bitmap
    BufferedImage image = decode(dataUri(render(
        "ArrayPlot[Table[If[i < 100, 1, 0] + 0.001*j, {i, 199}, {j, 199}]]")));
    int top = brightness(image.getRGB(0, 0));
    int bottom = brightness(image.getRGB(0, image.getHeight() - 1));
    assertTrue(top < bottom,
        "the first row holds the larger value, which ArrayPlot draws dark and at the top: got top="
            + top + " bottom=" + bottom);
  }

  /** Cells without a value stay transparent in the bitmap, as they do as rectangles. */
  @Test
  public void testBitmapKeepsEmptyCellsTransparent() {
    BufferedImage image = decode(dataUri(render(
        "ArrayPlot[Table[If[i == 1 && j == 1, Missing[], i*j + 0.5*i], {i, 60}, {j, 60}]]")));
    // the missing entry is in the first row, which is drawn at the top
    assertEquals(0, image.getRGB(0, 0) >>> 24, "a cell with no value must not be painted");
    assertEquals(255, image.getRGB(30, 30) >>> 24, "a cell with a value must be opaque");
  }

  // ------------------------------------------------------------------ helpers

  private static String render(String input) {
    IExpr result = evaluator.eval(input);
    assertTrue(result instanceof IAST && result.isGraphicsObject(),
        input + " should evaluate to a graphic, got " + result);
    return new SVGGraphics(600, 400).toSVG((IAST) result, true);
  }

  private static String dataUri(String svg) {
    Matcher matcher = DATA_URI.matcher(svg);
    assertTrue(matcher.find(), "the SVG should hold an embedded bitmap");
    return matcher.group(1);
  }

  private static BufferedImage decode(String base64OrUri) {
    String base64 = base64OrUri.startsWith("data:")
        ? base64OrUri.substring(base64OrUri.indexOf(',') + 1)
        : base64OrUri;
    try {
      BufferedImage image =
          ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(base64)));
      assertNotNull(image, "the encoded bytes should decode as an image");
      return image;
    } catch (java.io.IOException ioe) {
      throw new IllegalStateException("the encoded bytes are not a readable PNG", ioe);
    }
  }

  private static int brightness(int argb) {
    return ((argb >> 16) & 0xff) + ((argb >> 8) & 0xff) + (argb & 0xff);
  }
}
