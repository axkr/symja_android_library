package org.matheclipse.image.system;

import org.junit.jupiter.api.Test;

/** Thresholding, binarization and the two brightness transforms. */
public class ImageAdjustTest extends AbstractTestCase {

  @Test
  public void findThresholdSplitsTheHistogram() {
    // every split between the two groups is as good: the first of them, or the middle of the gap
    // where the darker group is the darkest value alone
    check("FindThreshold(Image({{0.0,0.1,0.9,1.0}}))", "0.0976562");
    check("FindThreshold(Image({{0.0,0.1,0.9,1.0}}),Method->\"Mean\")", "0.5");
    check("FindThreshold(Image({{0.0,0.1,0.9,1.0}}),Method->\"Median\")", "0.1");
    // the samples as they are, beyond full intensity too
    check("FindThreshold(Image({{0, 6}}))", "2.98828");
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
    check("ImageData(ImageAdjust(Image({{0.25,0.5,0.75}})),\"Byte\")", "{{0,128,255}}");
    // the range of the samples as they are, also beyond full intensity
    check("ImageData(ImageAdjust(Image({{1,5,9}})))", "{{0.0,0.5,1.0}}");
  }

  @Test
  public void imageAdjustTakesContrastAndBrightness() {
    check("ImageData(ImageAdjust(Image({{0.25,0.5,0.75}}),{0,0.1}),\"Byte\")", //
        "{{89,153,217}}");
  }

  /** An image that is already flat has nothing to stretch. */
  @Test
  public void imageAdjustOfAFlatImageIsTheIdentity() {
    check("ImageData(ImageAdjust(Image({{0.5,0.5}})),\"Byte\")", "{{128,128}}");
  }

  @Test
  public void histogramTransformEvensOutTheSamples() {
    // a sample goes to the middle of the share of the samples up to it, on 256 levels
    check("ImageData(HistogramTransform(Image({{0.0,0.25,0.5,1.0}})),\"Byte\")", //
        "{{32,96,160,224}}");
    check("ImageData(HistogramTransform(Image({{4, 9, 9, 9, 50}})))", //
        "{{0.0984314,0.5,0.5,0.5,0.901569}}");
    // the type stays
    check("h = HistogramTransform(Image({{3, 7, 7, 200}}, \"Byte\")); "
        + "{ImageType(h), Flatten(ImageData(h, \"Byte\"))}", //
        "{Byte,{32,128,128,224}}");
  }

  @Test
  public void imageAdjustInputAndOutputRanges() {
    // the samples of {imin, imax} are rescaled to 0 ... 1 before the adjustment
    check("Max(Abs(ImageData(ImageAdjust(Image({{0.1, 0.3}, {0.5, 0.7}}), {0, 0}, {0.1, 0.5})) "
        + "- {{0., 0.5}, {1., 1.5}})) < 10^-7", //
        "True");
    // and from 0 ... 1 to {omin, omax} after it
    check("Max(Abs(ImageData(ImageAdjust(Image({{0.1, 0.3}, {0.5, 0.7}}), {0, 0}, {0.1, 0.5}, "
        + "{0.2, 0.6})) - {{0.2, 0.4}, {0.6, 0.8}})) < 10^-7", //
        "True");
  }
}
