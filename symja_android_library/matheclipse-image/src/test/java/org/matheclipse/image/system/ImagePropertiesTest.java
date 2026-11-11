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

  /** Data without a type is an image of reals, whatever its samples are. */
  @Test
  public void dataWithoutATypeIsReal32() {
    check("ImageType(Image({{0.0,1.0}}))", "Real32");
    check("ImageType(Image({{1,0},{0,1}}))", "Real32");
    check("ImageType(Image({{3,77,250}}))", "Real32");
    check("ImageType(Image({{3,77,250}},\"Byte\"))", "Byte");
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
  public void imageValuePositionsOfColours() {
    // a colour directive in the colour space of the image
    check("ImageValuePositions(Image({{{0.2,0.4,0.6},{0,0,1}},{{0,0,1},{0.2,0.4,0.6}}}),Blue)", //
        "{{1.5,1.5},{0.5,0.5}}");
    check("ImageValuePositions(Image({{{0.2,0.4,0.6},{1,1,1}}}),GrayLevel(1))", //
        "{{1.5,0.5}}");
    check("ImageValuePositions(Image({{1,0.5}}),RGBColor(1,1,1))", //
        "{{0.5,0.5}}");
    check("ImageValuePositions(Image({{0.3,0.6},{0.6,0.2}}),GrayLevel(0.6))", //
        "{{1.5,1.5},{0.5,0.5}}");
    // a channel list, within a distance in every channel
    check("ImageValuePositions(Image({{{0.2,0.4,0.6},{0.25,0.35,0.6},{0.2,0.4,0.9}}}),{0.2,0.4,0.6},0.06)", //
        "{{0.5,0.5},{1.5,0.5}}");
  }

  /**
   * An image stored in another colour space compares a list with its samples, but a colour and
   * brightness with the RGB form of its pixels.
   */
  @Test
  public void imageValuePositionsInOtherColourSpaces() {
    // green and a dark blue
    check("hsb=Image({{{1/3,1,1},{2/3,1,0.5}}},ColorSpace->\"HSB\");" //
        + "ImageValuePositions(hsb,{2/3,1,0.5})", //
        "{{1.5,0.5}}");
    check("{ImageValuePositions(hsb,Green),ImageValuePositions(hsb,RGBColor(0,0,0.5))}", //
        "{{{0.5,0.5}},{{1.5,0.5}}}");
    check("{ImageValuePositions(hsb,\"Max\"),ImageValuePositions(hsb,\"Min\")}", //
        "{{{0.5,0.5}},{{1.5,0.5}}}");
    // red and black
    check("cmyk=Image({{{0,1,1,0},{0,0,0,1}}},ColorSpace->\"CMYK\");" //
        + "{ImageValuePositions(cmyk,Red),ImageValuePositions(cmyk,Black),"
        + "ImageValuePositions(cmyk,\"Min\")}", //
        "{{{0.5,0.5}},{{1.5,0.5}},{{1.5,0.5}}}");
  }

  @Test
  public void imageValuePositionsOfExtremes() {
    check("ImageValuePositions(Image({{0.3,0.6},{0.6,0.2}}),\"Min\")", //
        "{{1.5,0.5}}");
    check("ImageValuePositions(Image({{0.3,0.6},{0.6,0.2}}),\"Max\",0.3)", //
        "{{0.5,1.5},{1.5,1.5},{0.5,0.5}}");
    // the pixels of an RGB image ordered by their grey level: blue is darker than green
    check("ImageValuePositions(Image({{{0,0,1},{0,1,0}},{{0.5,0.5,0.5},{0,0,1}}}),\"Min\")", //
        "{{0.5,1.5},{1.5,0.5}}");
    check("ImageValuePositions(Image({{{0,0,1},{0,1,0}},{{0.5,0.5,0.5},{0,0,1}}}),\"Max\")", //
        "{{1.5,1.5}}");
  }

  @Test
  public void imageValuePositionsOfRealsBeyondOne() {
    // an image of reals keeps its samples, and the value, beyond the intensity range
    check("ImageValuePositions(Image({{0.25,3.,1.}}),3)", //
        "{{1.5,0.5}}");
    check("ImageValuePositions(Image({{0.25,3.,1.}}),7)", //
        "{}");
  }

  @Test
  public void imageValuePositionsOfBytes() {
    // a value beyond the intensity range is clipped to it
    check("ImageValuePositions(Image({{10,255},{100,200}},\"Byte\"),200)", //
        "{{1.5,1.5}}");
    check("ImageValuePositions(Image({{10,255},{0,200}},\"Byte\"),-3)", //
        "{{0.5,0.5}}");
    check("ImageValuePositions(Image({{10,200},{100,200}},\"Byte\"),0.4,0.01)", //
        "{{0.5,0.5}}");
  }

  @Test
  public void measurementsAreOnTheZeroToOneScale() {
    check("ImageMeasurements(Image({{0.0,1.0}}),\"Mean\")", "0.5");
    check("ImageMeasurements(Image({{0.0,1.0}}),\"Total\")", "1.0");
    check("ImageMeasurements(Image({{0.0,1.0}}),\"MinMax\")", "{0.0,1.0}");
    check("ImageMeasurements(Image({{0.0,1.0}}),\"Range\")", "1.0");
    check("ImageMeasurements(Image({{0.0,0.5,1.0}}),\"Median\")", "0.5");
    // the samples as they are, beyond full intensity too
    check("ImageMeasurements(Image({{0, 3, 9}}),{\"Mean\",\"Max\"})", "{4.0,9.0}");
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
        "{1.0,0.0,0.5}");
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
