package org.matheclipse.image.system;

import org.junit.jupiter.api.Test;

/**
 * The filters of an image, the enlargement by whole factors and the grey level of real samples.
 */
public class ImageFilterTest extends AbstractTestCase {

  @Test
  public void testGaussianFilter() {
    // {{0,0,0,0,0},{0,0.00987648,0.0796275,0.00987648,0},{0,0.0796275,0.641984,0.0796275,0},...}
    check(
        "d = Image(ReplacePart(Table(0., {5}, {5}), {3, 3} -> 1.)); "
            + "Max(Abs(ImageData(GaussianFilter(d, 1)) - {{0,0,0,0,0}, "
            + "{0,0.00987648032605648,0.07962752133607864,0.00987648032605648,0}, "
            + "{0,0.07962752133607864,0.6419840455055237,0.07962752133607864,0}, "
            + "{0,0.00987648032605648,0.07962752133607864,0.00987648032605648,0}, "
            + "{0,0,0,0,0}})) < 10^-7", //
        "True");
    check(
        "Max(Abs(ImageData(GaussianFilter(d, 1))[[2 ;; 4, 2 ;; 4]] - GaussianMatrix(1))) < 10^-15", //
        "True");
    check("{ImageType(GaussianFilter(d, 1)), ImageDimensions(GaussianFilter(d, 1))}", //
        "{Real32,{5,5}}");
    // every channel of a colour image is filtered
    check(
        "ImageChannels(GaussianFilter(Image({{{1., 0., 0.}, {0., 1., 0.}}, "
            + "{{0., 0., 1.}, {1., 1., 1.}}}), 1))", //
        "3");
  }

  @Test
  public void testConvolveAndCorrelate1() {
    // the convolution reflects the kernel, the correlation doesn't
    check("delta = Image({{0., 1., 0.}}); {ImageData(ImageConvolve(delta, {{1, 2, 3}})), "
        + "ImageData(ImageCorrelate(delta, {{1, 2, 3}}))} == " + "{{{1., 2., 3.}}, {{3., 2., 1.}}}", //
        "True");
    // the border continues with its own pixels, so a constant image stays constant
    check(
        "Max(Abs(ImageData(ImageConvolve(Image({{0.3, 0.3, 0.3}, {0.3, 0.3, 0.3}}), "
            + "{{0.25, 0.5, 0.25}})) - 0.3)) < 10^-15", //
        "True");
  }

  @Test
  public void testConvolveAndCorrelate2() {
    // {{0.4, 0.7, 1.}} - the border is continued with its own pixels
    check(
        "Max(Abs(ImageData(ImageConvolve(Image({{0.1, 0.2, 0.4}}), {{1, 1, 1}})) "
            + "- {{0.4, 0.7, 1.}})) < 10^-7", //
        "True");
    // a kernel of even length: centres the convolution on its first element of the two in the
    // middle and the correlation on the second one
    // {{0.3, 0.4, 0.8}}
    check(
        "Max(Abs(ImageData(ImageConvolve(Image({{0.1, 0.2, 0.4}}), {{1, 2}})) "
            + "- {{0.3, 0.4, 0.8}})) < 10^-7", //
        "True");
    // {{0.3, 0.5, 1.}}
    check(
        "Max(Abs(ImageData(ImageCorrelate(Image({{0.1, 0.2, 0.4}}), {{1, 2}})) "
            + "- {{0.3, 0.5, 1.}})) < 10^-7", //
        "True");
  }

  @Test
  public void testFilterTypes() {
    // {Real32,Byte,Byte,Byte,Real32,Real32} - the linear filters return reals, the filters
    // of a block keep the type
    check(
        "b = Image({{0, 128, 255}, {64, 192, 32}}, \"Byte\"); "
            + "ImageType /@ {GaussianFilter(b, 1), MeanFilter(b, 1), MedianFilter(b, 1), "
            + "MinFilter(b, 1), ImageConvolve(b, {{1}}), ImageCorrelate(b, {{1}})}", //
        "{Real32,Byte,Byte,Byte,Real32,Real32}");
    // {{19,131,223},{70,160,67}}
    check("ImageData(GaussianFilter(b, 1), \"Byte\")", //
        "{{19,131,223},{70,160,67}}");
  }

  @Test
  public void testRankAndMeanFilter1() {
    // the median removes an outlier
    check(
        "spike = Image({{0.2, 0.2, 0.2}, {0.2, 1., 0.2}, {0.2, 0.2, 0.2}}); "
            + "Union(Flatten(ImageData(MedianFilter(spike, 1))))", //
        "{0.2}");
    check(
        "{Union(Flatten(ImageData(MaxFilter(Image({{0., 1., 0.}, {0., 0., 0.}}), 1)))), "
            + "Union(Flatten(ImageData(MinFilter(Image({{1., 0., 1.}, {1., 1., 1.}}), 1))))}", //
        "{{1.0},{0.0}}");
    // a list is still filtered by the list filter
    check("MeanFilter({0., 0., 1., 0., 0.}, 1)", //
        "{0.0,0.333333,0.333333,0.333333,0.0}");
  }

  @Test
  public void testRankAndMeanFilter2() {
    // at the border the block is cut off, the image isn't continued:
    // {{0.375, 0.375}, {0.375, 0.375}}
    check(
        "ImageData(MeanFilter(Image({{0.1, 0.2}, {0.4, 0.8}}), 1)) == "
            + "{{0.375, 0.375}, {0.375, 0.375}}", //
        "True");
    // the median of an even number of samples is the upper one of the two in the middle:
    // {{0.4, 0.4}, {0.4, 0.4}}
    check(
        "ImageData(MedianFilter(Image({{0.1, 0.2}, {0.4, 0.8}}), 1)) == "
            + "{{0.4, 0.4}, {0.4, 0.4}}", //
        "True");
  }

  @Test
  public void testImageResizeWholeFactors() {
    // every pixel four times, exactly
    check(
        "ImageData(ImageResize(Image({{1, 2, 3}, {4, 5, 6}}/10.), {6, 4})) == "
            + "{{0.1,0.1,0.2,0.2,0.3,0.3}, {0.1,0.1,0.2,0.2,0.3,0.3}, "
            + "{0.4,0.4,0.5,0.5,0.6,0.6}, {0.4,0.4,0.5,0.5,0.6,0.6}}", //
        "True");
    check(
        "ImageData(ImageResize(Image({{1, 2, 3}, {4, 5, 6}}/10.), {6, 4}, "
            + "Resampling -> \"Nearest\")) == "
            + "{{0.1,0.1,0.2,0.2,0.3,0.3}, {0.1,0.1,0.2,0.2,0.3,0.3}, "
            + "{0.4,0.4,0.5,0.5,0.6,0.6}, {0.4,0.4,0.5,0.5,0.6,0.6}}", //
        "True");
    check("ImageDimensions(ImageResize(Image({{1, 2, 3}, {4, 5, 6}}/10.), 12))", //
        "{12,8}");
  }

  @Test
  public void testGrayscaleOfRealSamples() {
    // {{0.299, 0.587}, {0.114, 1.}}
    check("rgb = Image({{{1., 0., 0.}, {0., 1., 0.}}, {{0., 0., 1.}, {1., 1., 1.}}}); "
        + "Max(Abs(ImageData(ColorConvert(rgb, \"Grayscale\")) - {{0.299, 0.587}, {0.114, 1.}})) "
        + "< 10^-15", //
        "True");
    check("ImageChannels(ColorConvert(rgb, \"Grayscale\"))", //
        "1");
    // a Byte image stays on its 8 bit scale
    check("ImageData(ColorConvert(Image({{{255, 0, 0}}}, \"Byte\"), \"Grayscale\"), \"Byte\")", //
        "{{76}}");
  }
}
