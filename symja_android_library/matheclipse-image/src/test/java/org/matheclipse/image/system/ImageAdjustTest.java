package org.matheclipse.image.system;

import org.junit.jupiter.api.Test;

/** Thresholding, binarization and the two brightness transforms. */
public class ImageAdjustTest extends AbstractTestCase {

  @Test
  public void findThresholdSplitsTheHistogram() {
    check("FindThreshold(Image({{0.0,0.1,0.9,1.0}}))", "0.101961");
    check("FindThreshold(Image({{0.0,0.1,0.9,1.0}}),Method->\"Mean\")", "0.50098");
    check("FindThreshold(Image({{0.0,0.1,0.9,1.0}}),Method->\"Median\")", "0.101961");
  }

  /** An unknown method is left unevaluated rather than falling back to the default. */
  @Test
  public void anUnknownMethodDoesNotEvaluate() {
    check("FindThreshold(Image({{0.0,0.1,0.9,1.0}}),Method->\"Nonsense\")", //
        "FindThreshold(Image(Dimensions: 4,1 Transparency: 1),Method->Nonsense)");
  }

  @Test
  public void binarizeSplitsAtTheFoundThreshold() {
    check("ImageData(Binarize(Image({{0.0,0.1,0.9,1.0}})),\"Byte\")", "{{0,0,255,255}}");
  }

  @Test
  public void binarizeTakesAThresholdOrARange() {
    check("ImageData(Binarize(Image({{0.0,0.4,0.6,1.0}}),0.5),\"Byte\")", "{{0,0,255,255}}");
    check("ImageData(Binarize(Image({{0.0,0.4,0.6,1.0}}),{0.3,0.7}),\"Byte\")", //
        "{{0,255,255,0}}");
  }

  /** The result of Binarize is a one channel image, whatever went in. */
  @Test
  public void binarizeAlwaysGivesOneChannel() {
    check("ImageChannels(Binarize(Image({{{1.0,0.0,0.0},{0.0,0.0,0.0}}})))", "1");
  }

  @Test
  public void localAdaptiveBinarizeKeepsTheSize() {
    check("ImageDimensions(LocalAdaptiveBinarize(Image(ConstantArray(0.5,{6,6})),1))", "{6,6}");
    check("ImageChannels(LocalAdaptiveBinarize(Image(ConstantArray(0.5,{6,6})),1))", "1");
    check("ImageDimensions(LocalAdaptiveBinarize(Image(ConstantArray(0.5,{6,6})),1,"
        + "Method->\"Sauvola\"))", //
        "{6,6}");
  }

  /** Without arguments ImageAdjust stretches the samples to fill the whole range. */
  @Test
  public void imageAdjustStretchesTheRange() {
    check("ImageData(ImageAdjust(Image({{0.25,0.5,0.75}})),\"Byte\")", "{{0,129,255}}");
  }

  @Test
  public void imageAdjustTakesContrastAndBrightness() {
    check("ImageData(ImageAdjust(Image({{0.25,0.5,0.75}}),{0,0.1}),\"Byte\")", //
        "{{90,154,217}}");
  }

  /** An image that is already flat has nothing to stretch. */
  @Test
  public void imageAdjustOfAFlatImageIsTheIdentity() {
    check("ImageData(ImageAdjust(Image({{0.5,0.5}})),\"Byte\")", "{{128,128}}");
  }

  @Test
  public void histogramTransformEvensOutTheSamples() {
    check("ImageData(HistogramTransform(Image({{0.0,0.25,0.5,1.0}})),\"Byte\")", //
        "{{63,127,191,255}}");
  }
}
