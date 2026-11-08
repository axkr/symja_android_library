package org.matheclipse.image.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.awt.image.BufferedImage;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.image.expression.data.ImageExpr;

/**
 * Images of four or more channels without a colour space hold spectral bands: they are in no colour
 * space, are shown as the means of three groups of bands and convert to RGB the same way. A stated
 * <code>"RGB"</code> space makes the fourth channel an alpha channel.
 */
public class SpectralImageTest extends AbstractTestCase {

  @Test
  public void testSpectralBandsHaveNoColorSpace() {
    check("ImageColorSpace(Image({{{0.1,0.2,0.3,0.4,0.5}}}))", //
        "Automatic");
    check("ImageColorSpace(Image({{{0.1,0.2,0.3,0.4}}}, ColorSpace -> \"CMYK\"))", //
        "CMYK");
  }

  @Test
  public void testColorConvertAveragesGroupsOfBands() {
    // one band red, two green, one blue
    check("ImageData(ColorConvert(Image({{{0.2,0.4,0.8,0.6}}}), \"RGB\"))", //
        "{{{0.2,0.6,0.6}}}");
    check("ImageData(ColorConvert(Image({{{0.2,0.4,0.6,0.8,1.0,0.0}},"
        + "{{0.,0.,1.,1.,0.5,0.5}}}), \"RGB\"))", //
        "{{{0.3,0.7,0.5}},{{0.0,1.0,0.5}}}");
    check("ImageData(ColorConvert(Image({{{10,20,30,40}}},\"Byte\"), \"RGB\"),\"Byte\")", //
        "{{{10,25,40}}}");
    check("ImageData(ColorConvert(Image({{{0.3,0.6,0.9,0.6}}}), \"Grayscale\"))", //
        "{{0.59835}}");
  }

  @Test
  public void testStatedRgbKeepsTheAlphaChannel() {
    check("ImageData(ColorConvert(Image({{{0.25,0.75,0.5,0.0}}}, ColorSpace -> \"RGB\"), "
        + "\"RGB\"))", //
        "{{{0.25,0.75,0.5,0.0}}}");
  }

  @Test
  public void testShownAsItsRgbConversion() {
    check("With({im=Image({{{0.25,0.5,1.0,0.75,0.0}}})}, "
        + "ExportString(im,\"SVG\")===ExportString(ColorConvert(im,\"RGB\"),\"SVG\"))", //
        "True");
    check("With({im=Image({{{0.25,0.5,1.0,0.75}}})}, "
        + "ExportString(im,\"SVG\")===ExportString(ColorConvert(im,\"RGB\"),\"SVG\"))", //
        "True");
  }

  @Test
  public void testTransparentBitmapIsRgbWithAlpha() {
    // a decoded picture with an alpha channel and no sample matrix: red, green, blue and alpha
    BufferedImage bitmap = new BufferedImage(3, 2, BufferedImage.TYPE_INT_ARGB);
    bitmap.setRGB(1, 1, 0x80336699);
    ImageExpr image = new ImageExpr(bitmap, null);
    assertTrue(F.eval(F.unaryAST1(S.ImageColorSpace, image)).isString("RGB"));
    assertEquals("4",
        F.eval(F.unaryAST1(S.ImageChannels, F.binaryAST2(S.ColorConvert, image, F.stringx("RGB"))))
            .toString());
  }
}
