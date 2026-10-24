package org.matheclipse.image.system;

import org.junit.jupiter.api.Test;

/**
 * What the <code>Image</code> object already does, so that the image processing functions have a
 * baseline to be built on.
 */
public class ImageExprTest extends AbstractTestCase {

  @Test
  public void imageDataReturnsThePixelsItWasBuiltFrom() {
    // ImageExpr keeps the source matrix, so this round trip is lossless rather than going
    // through the PNG buffer
    check("ImageData(Image({{0.0,1.0},{1.0,0.0}}))=={{0.0,1.0},{1.0,0.0}}", //
        "True");
  }

  @Test
  public void imageDimensionsIsWidthByHeightUnlikeDimensions() {
    check("ImageDimensions(Image({{0.0,1.0,0.5}}))", //
        "{3,1}");
    check("Dimensions(ImageData(Image({{0.0,1.0,0.5}})))", //
        "{1,3}");
  }

  @Test
  public void greyscalePixelsSurviveThePngBuffer() {
    // "Byte" bypasses the stored matrix and reads the bitmap back, so this is the round trip
    // through the encoder; 0.5 lands on 128 because the sample is rounded rather than truncated
    check("ImageData(Image({{0.0,0.5},{1.0,0.25}},ColorSpace->\"Grayscale\"),\"Byte\")"
        + "=={{0,128},{255,64}}", //
        "True");
  }

  @Test
  public void anImageIsItsOwnHead() {
    check("Head(Image({{0.0,1.0}}))", //
        "Image");
    check("Image({{0.0,1.0}})===Image({{0.0,1.0}})", //
        "True");
  }

  /**
   * The symbols of the image processing guide are declared, but their evaluators are installed one
   * stage at a time. Until then they stay unevaluated rather than failing, which is what makes it
   * safe to declare them all at once.
   */
  @Test
  public void functionsWithoutAnEvaluatorStayUnevaluated() {
    check("EdgeDetect(Image({{0.0,1.0}}),1)", //
        "EdgeDetect(Image(Dimensions: 2,1 Transparency: 1),1)");
  }

  @Test
  public void testRasterizeAndGraphicsAsImages() {
    // any expression is drawn as a text; the image is RGB without transparency
    check("{Head(Rasterize(\"Symja\")), ImageChannels(Rasterize(Graphics(Circle())))}", //
        "{Image,3}");
    check("ImageDimensions(Rasterize(Graphics(Rectangle()), RasterSize -> 30))", //
        "{30,30}");
    check("ImageDimensions(Rasterize(Style(\"B\", 20), RasterSize -> {12, 18}))", //
        "{12,18}");
    // the image functions take a graphic for the image it is drawn to
    check("{Head(ColorNegate(Graphics(Disk()))), Head(ColorConvert(Graphics(Disk()), \"Grayscale\")), "
        + "ImageDimensions(ImageResize(Graphics(Disk()), 16))}", //
        "{Image,Image,{16,16}}");
  }
}
