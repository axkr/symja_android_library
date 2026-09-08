package org.matheclipse.image.system;

import org.junit.jupiter.api.Test;

/**
 * Sample by sample arithmetic. Every expectation is written in <code>"Byte"</code> so that the 8 bit
 * storage of the result is visible rather than hidden behind a rounded real.
 */
public class ImageArithmeticTest extends AbstractTestCase {

  @Test
  public void addingANumberBrightens() {
    check("ImageData(ImageAdd(Image({{0.0,0.5}}),0.25),\"Byte\")", "{{64,192}}");
  }

  @Test
  public void resultsAreClippedIntoRange() {
    check("ImageData(ImageAdd(Image({{0.5,1.0}}),0.75),\"Byte\")", "{{255,255}}");
    check("ImageData(ImageSubtract(Image({{0.5,0.0}}),0.75),\"Byte\")", "{{0,0}}");
  }

  @Test
  public void multiplyingScalesTheSamples() {
    check("ImageData(ImageMultiply(Image({{0.0,0.8}}),0.5),\"Byte\")", "{{0,102}}");
    check("ImageData(ImageDivide(Image({{0.0,0.4}}),0.5),\"Byte\")", "{{0,204}}");
  }

  @Test
  public void twoImagesCombineSampleBySample() {
    check("ImageData(ImageSubtract(Image({{0.5,0.5}}),Image({{0.25,0.75}})),\"Byte\")", //
        "{{64,0}}");
    check("ImageData(ImageDifference(Image({{0.5,0.5}}),Image({{0.25,0.75}})),\"Byte\")", //
        "{{64,63}}");
  }

  /** Two images of different sizes have no sample by sample meaning. */
  @Test
  public void imagesOfDifferentSizesDoNotCombine() {
    check("ImageAdd(Image({{0.5}}),Image({{0.5,0.5}}))", //
        "ImageAdd(Image(Dimensions: 1,1 Transparency: 1),Image(Dimensions: 2,1 Transparency: 1))");
  }

  @Test
  public void aColorImageTakesOneOperandPerChannel() {
    check("ImageData(ImageAdd(Image({{{0.0,0.0,0.0}}}),{0.25,0.5,1.0}),\"Byte\")", //
        "{{{64,128,255}}}");
    check("ImageData(ImageAdd(Image({{{0.0,0.0,0.0}}}),RGBColor(1.0,0.0,0.0)),\"Byte\")", //
        "{{{255,0,0}}}");
  }

  /** A greyscale operand is broadcast over the channels of a colour image. */
  @Test
  public void aScalarAppliesToEveryChannel() {
    check("ImageData(ImageMultiply(Image({{{1.0,0.5,0.0}}}),0.5),\"Byte\")", //
        "{{{128,64,0}}}");
  }

  @Test
  public void clipNarrowsTheRange() {
    check("ImageData(ImageClip(Image({{0.0,0.5,1.0}}),{0.25,0.75}),\"Byte\")", //
        "{{64,128,191}}");
    check("ImageData(ImageClip(Image({{0.0,0.5,1.0}})),\"Byte\")", //
        "{{0,128,255}}");
  }

  /** Arithmetic changes the colours, never the transparency. */
  @Test
  public void theAlphaChannelIsCarriedThrough() {
    check("ImageData(ImageMultiply(Image({{{1.0,1.0,1.0,0.5}}}),0.5),\"Byte\")", //
        "{{{128,128,128,128}}}");
  }
}
