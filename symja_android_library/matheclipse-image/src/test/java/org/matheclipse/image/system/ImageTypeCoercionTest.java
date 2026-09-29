package org.matheclipse.image.system;

import org.junit.jupiter.api.Test;

/** WMA's <code>Image(data, type)</code> and the quarter turns of <code>ImageRotate</code>. */
public class ImageTypeCoercionTest extends AbstractTestCase {

  @Test
  public void testStatedTypeCoercesTheSamplesWMA() {
    check("{ImageData(Image({{0, 300}}, \"Byte\"), \"Byte\")=={{0,255}}, "
        + "ImageData(Image({{0.5, 1.7}}, \"Byte\"), \"Byte\")=={{0,2}}, "
        + "ImageType(Image({{1, 2}}, \"Bit16\")), "
        + "ImageData(Image({{1, 70000}}, \"Bit16\"), \"Bit16\")=={{1,65535}}}", //
        "{True,True,Bit16,True}");
    // a stated type is kept although the samples look bilevel
    check("{ImageType(Image({{0, 1}}, \"Byte\")), "
        + "ImageData(Image({{0, 1}}, \"Byte\"), \"Byte\")=={{0,1}}}", //
        "{Byte,True}");
  }

  @Test
  public void testImageRotateSidesWMA() {
    // WMA: Right -> {{3.,1.},{4.,2.}}, Top -> Left and 90 Degree -> {{2.,4.},{1.,3.}}
    check("ImageData(ImageRotate(Image({{0.1, 0.2}, {0.3, 0.4}}), Right))==" //
        + "{{0.3, 0.1}, {0.4, 0.2}}", //
        "True");
    check("ImageData(ImageRotate(Image({{0.1, 0.2}, {0.3, 0.4}}), Top -> Left))==" //
        + "{{0.2, 0.4}, {0.1, 0.3}}", //
        "True");
    check("ImageData(ImageRotate(Image({{0.1, 0.2}, {0.3, 0.4}}), 90 Degree))==" //
        + "{{0.2, 0.4}, {0.1, 0.3}}", //
        "True");
    check("{ImageData(ImageRotate(Image({{0.1, 0.2}, {0.3, 0.4}}), Bottom))=={{0.4, 0.3}, {0.2, 0.1}}, "
        + "ImageDimensions(ImageRotate(Image({{0.1, 0.2, 0.3}, {0.4, 0.5, 0.6}}), Right)), "
        + "ImageType(ImageRotate(Image({{1, 70000}}, \"Bit16\"), Right))}", //
        "{True,{2,3},Bit16}");
  }
}
