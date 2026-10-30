package org.matheclipse.image.system;

import org.junit.jupiter.api.Test;

/**
 * Sample by sample arithmetic. Every expectation is written in <code>"Byte"</code> so that the 8 bit
 * storage of the result is visible rather than hidden behind a rounded real.
 */
public class ImageArithmeticTest extends AbstractTestCase {

  @Test
  public void addingANumberBrightens() {
    // 0.5 + 0.25 is added on the real samples: 0.75 is the byte 191, not 128 + 64
    check("ImageData(ImageAdd(Image({{0.0,0.5}}),0.25),\"Byte\")", "{{64,191}}");
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
        "{{64,64}}");
  }

  @Test
  public void aColorImageTakesOneOperandPerChannel() {
    check("ImageData(ImageAdd(Image({{{0.0,0.0,0.0}}}),{0.25,0.5,1.0}),\"Byte\")", //
        "{{{64,128,255}}}");
    check("ImageData(ImageAdd(Image({{{0.0,0.0,0.0}}}),RGBColor(1.0,0.0,0.0)),\"Byte\")", //
        "{{{255,0,0}}}");
  }

  /** The named greys White and Gray are grey levels, applied to every channel. */
  @Test
  public void aGrayLevelOperandAppliesToEveryChannel() {
    check("ImageData(ImageMultiply(Image({{0.2,0.6}}),White),\"Byte\")", "{{51,153}}");
    check("ImageData(ImageAdd(Image({{{0.25,0.0,0.5}}}),Gray),\"Byte\")", "{{{191,128,255}}}");
  }

  /**
   * A colour on a greyscale image makes it a colour image: the intensity is spread over three
   * channels and the colour applied to each; an image of reals is not clipped.
   */
  @Test
  public void aColourTurnsAGreyscaleImageIntoAColourImage() {
    check("ImageData(ImageMultiply(Image({{1.0}}),RGBColor(0,1,0)))", "{{{0.0,1.0,0.0}}}");
    check("ImageChannels(ImageMultiply(Image({{1.0}}),RGBColor(0,1,0)))", "3");
    check("ImageData(ImageAdd(Image({{0.5}}),RGBColor(0,0,1)))", "{{{0.5,0.5,1.5}}}");
    check("ImageData(ImageMultiply(Image({{1.0}}),RGBColor(1,0,0)))", "{{{1.0,0.0,0.0}}}");
    // a grey keeps the image greyscale, and its level is applied as written
    check("ImageChannels(ImageMultiply(Image({{1.0}}),GrayLevel(0.3)))", "1");
    check("ImageData(ImageMultiply(Image({{1.0}}),GrayLevel(0.3)))", "{{0.3}}");
    check("ImageData(ImageMultiply(Image({{{0.5,1.0,0.25}}}),Hue(0)),\"Byte\")", "{{{128,0,0}}}");
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

  /** An image of reals keeps its type and its exact samples through arithmetic with a constant. */
  @Test
  public void anImageOfRealsStaysAnImageOfReals() {
    check("ImageType(ImageMultiply(Image({{{1, 0.5, 0.25}}}), {1., 0.5, 2.}))", //
        "Real32");
    check("ImageData(ImageMultiply(Image({{{1, 0.5, 0.25}}}), {1., 0.5, 2.}))", //
        "{{{1.0,0.25,0.5}}}");
    check("ImageData(ImageAdd(Image({{{0.5, 0.5, 0.25}}}), {0.25, 0., 0.25}))", //
        "{{{0.75,0.5,0.5}}}");
    check("ImageData(ImageMultiply(Image({{0.2, 0.4}}), 0.5))", //
        "{{0.1,0.2}}");
    // the alpha channel is not an operand
    check("ImageData(ImageMultiply(Image({{{0.5, 0.5, 0.5, 0.25}}}), 0.5))", //
        "{{{0.25,0.25,0.25,0.25}}}");
    check("ImageType(ImageMultiply(Image({{10, 200}}, \"Byte\"), 0.5))", //
        "Byte");
  }

  /** Plus, Times and Power of images and numbers work sample by sample, without clipping. */
  @Test
  public void plusTimesAndPowerActOnTheSamples() {
    check("ImageData(Image({{0.25, 0.75}}) + Image({{0.5, 0.5}}))", //
        "{{0.75,1.25}}");
    check("ImageData(Image({{0.25, 0.75}}) + 0.5)", //
        "{{0.75,1.25}}");
    check("ImageData(4*Image({{0.25, 0.75}}))", //
        "{{1.0,3.0}}");
    check("ImageData(Image({{0.25, 0.75}})*Image({{2, 0}}))", //
        "{{0.5,0.0}}");
    check("ImageData(Image({{0.25, 0.75}}) - Image({{0.25, 0.25}}))", //
        "{{0.0,0.5}}");
    check("ImageData(Image({{0.5, 0.25}})^2)", //
        "{{0.25,0.0625}}");
    // a grey image combines with every channel of a colour image
    check("ImageData(Image({{{0.25, 0.5, 0.75}}}) + Image({{0.25}}))", //
        "{{{0.5,0.75,1.0}}}");
  }

  /** A sum or product of images is Real32, a power Real64. */
  @Test
  public void plusTimesAndPowerGiveImagesOfReals() {
    check("{ImageType(Image({{0.5}}) + 2), ImageType(3*Image({{0.5}})), ImageType(Image({{0.5}})^3)}", //
        "{Real32,Real32,Real64}");
    check("ImageType(Image({{0.5}}, \"Real64\") + Image({{0.25}}))", //
        "Real64");
    check("ImageType(Image({{1, 0}}, \"Bit\") + 0.25)", //
        "Real32");
  }

  /** Images of different sizes, or a symbolic operand, leave the sum as it is. */
  @Test
  public void plusOfMismatchedOperandsStaysUnevaluated() {
    check("Head(Image({{0.5}}) + Image({{0.5, 0.25}}))", //
        "Plus");
    check("Head(Image({{0.5}})*y)", //
        "Times");
    check("Head(Image({{0.5}})^n)", //
        "Power");
  }

  /**
   * An image of another size is centred on the first one, half a pixel to the top left when the
   * sizes differ by an odd number; the samples it doesn't cover keep their values.
   */
  @Test
  public void imagesOfDifferentSizesAreCentred() {
    check("ImageData(ImageAdd(Image({{0.25, 0.5, 0.75, 1.0, 0.0}}), Image({{0.125, 0.25, 0.375}})))", //
        "{{0.25,0.625,1.0,1.375,0.0}}");
    check("ImageData(ImageSubtract(Image({{0.5, 0.5}}), Image({{0.25, 0.125, 0.0625, 0.5}})))", //
        "{{0.375,0.4375}}");
    check("ImageData(ImageAdd(Image({{0.5}}),Image({{0.5,0.5}})))", //
        "{{1.0}}");
    check("ImageData(ImageAdd(Image(ConstantArray(0.25, {2, 4})), Image({{0.5}})))", //
        "{{0.25,0.75,0.25,0.25},\n" //
            + " {0.25,0.25,0.25,0.25}}");
    // a colour image makes a greyscale one a colour image
    check("ImageData(ImageMultiply(Image({{0.5, 0.25}}), Image({{{0.5, 1.0, 0.25}}})))", //
        "{{{0.25,0.5,0.125},{0.25,0.25,0.25}}}");
    // the difference needs images of one size
    check("Head(ImageDifference(Image({{0.5, 0.25}}), Image({{0.5}})))", //
        "ImageDifference");
  }
}
