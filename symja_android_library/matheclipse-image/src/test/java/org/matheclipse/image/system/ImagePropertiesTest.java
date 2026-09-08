package org.matheclipse.image.system;

import org.junit.jupiter.api.Test;

/** <code>ImageQ</code> and the functions that describe an image rather than change it. */
public class ImagePropertiesTest extends AbstractTestCase {

  @Test
  public void imageQ() {
    check("ImageQ(Image({{0.0,1.0}}))", "True");
    check("ImageQ(17)", "False");
    check("ImageQ({{0.0,1.0}})", "False");
  }

  @Test
  public void channelsAreCountedFromTheData() {
    check("ImageChannels(Image({{0.0,1.0}}))", "1");
    check("ImageChannels(Image({{{1.0,0.0,0.0}}}))", "3");
    check("ImageChannels(Image({{{1.0,0.0,0.0,0.5}}}))", "4");
  }

  @Test
  public void colorSpaceFollowsTheChannels() {
    check("ImageColorSpace(Image({{0.0,1.0}}))", "Grayscale");
    check("ImageColorSpace(Image({{{1.0,0.0,0.0}}}))", "RGB");
  }

  /** Real data is 0...1, integer data is 0...255, and integer data of 0s and 1s is bilevel. */
  @Test
  public void theTypeIsInferredFromTheSamples() {
    check("ImageType(Image({{0.0,1.0}}))", "Real32");
    check("ImageType(Image({{0,1}}))", "Bit");
    check("ImageType(Image({{0,128,255}}))", "Byte");
  }

  @Test
  public void aspectRatioIsExact() {
    check("ImageAspectRatio(Image({{0.0,1.0,0.5},{0.0,0.0,0.0}}))", "2/3");
    check("ImageAspectRatio(Image({{0.0,1.0},{0.0,0.0}}))", "1");
  }

  /**
   * Image coordinates count y upwards from the bottom left corner, unlike the rows of
   * <code>ImageData</code>.
   */
  @Test
  public void imageValueReadsFromTheBottomLeft() {
    check("ImageValue(Image({{0.0,1.0},{0.25,0.5}}),{0,0},\"Byte\")", "64");
    check("ImageValue(Image({{0.0,1.0},{0.25,0.5}}),{1,1},\"Byte\")", "255");
    check("ImageValue(Image({{{1.0,0.0,0.0}}}),{0,0},\"Byte\")", "{255,0,0}");
  }

  @Test
  public void imageValuePositionsGivesPixelCentres() {
    check("ImageValuePositions(Image({{0.0,1.0},{1.0,0.0}}),1)", //
        "{{1.5,1.5},{0.5,0.5}}");
    check("ImageValuePositions(Image({{0.0,1.0}}),0)", //
        "{{0.5,0.5}}");
  }

  @Test
  public void measurementsAreOnTheZeroToOneScale() {
    check("ImageMeasurements(Image({{0.0,1.0}}),\"Mean\")", "0.5");
    check("ImageMeasurements(Image({{0.0,1.0}}),\"Total\")", "1.0");
    check("ImageMeasurements(Image({{0.0,1.0}}),\"MinMax\")", "{0.0,1.0}");
    check("ImageMeasurements(Image({{0.0,1.0}}),\"Range\")", "1.0");
    check("ImageMeasurements(Image({{0.0,0.5,1.0}}),\"Median\")", "0.501961");
  }

  @Test
  public void measurementsThatDoNotLookAtTheSamples() {
    check("ImageMeasurements(Image({{0.0,1.0,0.5}}),\"Dimensions\")", "{3,1}");
    check("ImageMeasurements(Image({{0.0,1.0,0.5}}),\"Area\")", "3");
    check("ImageMeasurements(Image({{0.0,1.0,0.5}}),\"Count\")", "3");
  }

  @Test
  public void aColorImageIsMeasuredChannelByChannel() {
    check("ImageMeasurements(Image({{{1.0,0.0,0.5}}}),\"Mean\")", //
        "{1.0,0.0,0.501961}");
  }

  @Test
  public void severalPropertiesAtOnce() {
    check("ImageMeasurements(Image({{0.0,1.0}}),{\"Mean\",\"Area\"})", "{0.5,2}");
  }

  /** An unknown property is left unevaluated rather than guessed at. */
  @Test
  public void anUnknownPropertyDoesNotEvaluate() {
    check("ImageMeasurements(Image({{0.0,1.0}}),\"NoSuchProperty\")", //
        "ImageMeasurements(Image(Dimensions: 2,1 Transparency: 1),NoSuchProperty)");
  }
}
