package org.matheclipse.image.system;

import org.junit.jupiter.api.Test;

/**
 * What <code>Image</code>, <code>ImageData</code> and the property functions do <em>today</em>,
 * pinned before the option work starts.
 *
 * <p>
 * <p>
 * <b>The interleaved path has to survive.</b> <code>Image[data]</code> reads a matrix of scalars,
 * triples or quadruples, and that reading is about to be rewritten to go through a layout
 * abstraction so that <code>Interleaving -&gt; False</code> can read planar data through the same
 * code. Nothing below is allowed to move while that happens.
 *
 * <p>
 * <b>The gaps are pinned too.</b> Options that are silently ignored, a second argument that is
 * silently ignored, an unknown option that raises nothing - those are recorded here as they are, so
 * that the tests which start to fail are exactly the ones the option work is supposed to change.
 * They are marked as such and are expected to be rewritten, not preserved.
 */
public class ImageCharacterizationTest extends AbstractTestCase {

  // ----------------------------------------------------------------- sample scales

  /**
   * The scale a sample is on is read off the data: reals run 0.0 ... 1.0, integers run 0 ... 255,
   * and integer data whose samples are all 0 or 1 is bilevel.
   */
  @Test
  public void theSampleScaleIsReadOffTheData() {
    check("ImageType(Image({{0.0,1.0}}))", //
        "Real32");
    check("ImageType(Image({{0,255}}))", //
        "Byte");
    check("ImageType(Image({{0,1}}))", //
        "Bit");
    // the same picture written on two scales
    check("ImageData(Image({{0.0,1.0}}),\"Byte\")==ImageData(Image({{0,255}}),\"Byte\")", //
        "True");
  }

  /** A rasterized graphics object has no source matrix, so it reports the type of its bitmap. */
  @Test
  public void aRasterizedGraphicIsAByteImage() {
    check("ImageType(Image(Graphics(Disk())))", //
        "Byte");
    check("ImageDimensions(Image(Graphics(Disk())))", //
        "{600,600}");
  }

  // ------------------------------------------------------- ImageData, every sample type

  @Test
  public void imageDataReportsTheRequestedSampleType() {
    check("ImageData(Image({{0.0,1.0}}))=={{0.0,1.0}}", //
        "True");
    check("ImageData(Image({{0.0,1.0}}),\"Real64\")=={{0.0,1.0}}", //
        "True");
    check("ImageData(Image({{0.0,1.0}}),\"Byte\")=={{0,255}}", //
        "True");
    check("ImageData(Image({{0.0,1.0}}),\"Bit\")=={{0,1}}", //
        "True");
    check("ImageData(Image({{0.0,1.0}}),\"Bit16\")=={{0,65535}}", //
        "True");
    // 0.5 lands on 128 because the sample is rounded rather than truncated
    check(
        "ImageData(Image({{0.0,0.5},{1.0,0.25}},ColorSpace->\"Grayscale\"),\"Byte\")"
            + "=={{0,128},{255,64}}", //
        "True");
  }

  /** An unrecognized type name falls through to the real samples rather than raising. */
  @Test
  public void anUnknownSampleTypeIsReadAsReal() {
    check("ImageData(Image({{0.0,1.0}}),\"Bogus\")=={{0.0,1.0}}", //
        "True");
    // a non-string second argument is a different matter and leaves the call alone
    check("ImageData(Image({{0.0,1.0}}),7)", //
        "ImageData(Image(Dimensions: 2,1 Transparency: 1),7)");
  }

  /**
   * <code>ImageData</code> hands the source matrix straight back when the samples it holds are
   * already on the requested scale, and reads the bitmap otherwise. The two paths are visible in
   * the output: the stored matrix carries the matrix layout flag and breaks one row per line.
   */
  @Test
  public void theStoredMatrixIsHandedBackOnlyOnItsOwnScale() {
    // stored matrix, Byte in and Byte out
    check("ImageData(Image({{0,255},{128,64}}),\"Byte\")", //
        "{{0,255},\n {128,64}}");
    // bitmap, because Byte samples are not what a Real32 request asks for
    check("ImageData(Image({{0,255},{128,64}}))", //
        "{{0.0,1.0},{0.501961,0.25098}}");
    // stored matrix again, Real32 in and Real32 out
    check("ImageData(Image({{0.0,1.0},{1.0,0.0}}))", //
        "{{0.0,1.0},\n {1.0,0.0}}");
  }

  // ------------------------------------------------------------ channels and layout

  /** The number of channels is counted from the data: scalar, triple or quadruple. */
  @Test
  public void channelsAreCountedFromTheData() {
    check("ImageChannels(Image({{0.0,1.0}}))", //
        "1");
    check("ImageChannels(Image({{{1.0,0.0,0.0}}}))", //
        "3");
    check("ImageChannels(Image({{{1.0,0.0,0.0,1.0}}}))", //
        "4");
  }

  @Test
  public void colourSamplesSurviveTheRoundTrip() {
    check(
        "ImageData(Image({{{1.0,0.0,0.0},{0.0,1.0,0.0}}}),\"Byte\")" + "=={{{255,0,0},{0,255,0}}}", //
        "True");
    check(
        "ImageData(Image({{{255,0,0,255},{0,255,0,128}}}),\"Byte\")"
            + "=={{{255,0,0,255},{0,255,0,128}}}", //
        "True");
    // the alpha channel survives the png buffer
    check("ImageData(Image({{{1.0,0.0,0.0,0.5}}}),\"Byte\")=={{{255,0,0,128}}}", //
        "True");
  }

  /**
   * <code>ImageDimensions</code> is <code>{width, height}</code> while the data matrix is indexed
   * <code>{height, width, channels}</code>. This is the ordering that
   * <code>Interleaving -&gt; False</code> will invert, so it is worth having written down.
   */
  @Test
  public void dimensionsAreWidthByHeightAndTheDataIsHeightByWidthByChannels() {
    check("ImageDimensions(Image({{0.0,1.0,0.5}}))", //
        "{3,1}");
    check("Dimensions(ImageData(Image({{0.0,1.0,0.5}})))", //
        "{1,3}");
    check("ImageDimensions(Image({{{1.0,0.0,0.0},{0.0,1.0,0.0}}}))", //
        "{2,1}");
    check("Dimensions(ImageData(Image({{{1.0,0.0,0.0},{0.0,1.0,0.0}}})))", //
        "{1,2,3}");
    check("ImageAspectRatio(Image({{0.0,1.0,0.5}}))", //
        "1/3");
  }

  /** Data a <code>BufferedImage</code> has no type for is refused rather than coerced. */
  @Test
  public void onlyOneThreeOrFourChannelsAreAccepted() {
    // two channels - greyscale with alpha has no BufferedImage type and no colour space name
    check("Image({{{0.0,1.0}}})", //
        "Image({{{0.0,1.0}}})");
    // read as height 3, width 2, channels 2 - which is the planar layout Interleaving->False
    // is going to have to make sense of
    check("Image({{{1.0,1.0},{1.0,1.0}},{{0.0,0.0},{0.0,0.0}},{{0.0,0.0},{0.0,0.0}}})", //
        "Image({{{1.0,1.0},{1.0,1.0}},{{0.0,0.0},{0.0,0.0}},{{0.0,0.0},{0.0,0.0}}})");
  }

  @Test
  public void whatIsNotAMatrixIsLeftAlone() {
    check("Image(1)", //
        "Image(1)");
    check("Image({})", //
        "Image({})");
    check("Image({{1,2},{3}})", //
        "Image({{1,2},{3}})");
    check("ImageData(7)", //
        "ImageData(7)");
    check("ImageDimensions(7)", //
        "ImageDimensions(7)");
  }

  // ------------------------------------------------------------------- ColorSpace

  /**
   * <code>ColorSpace</code> says how the samples are to be <em>read</em>; what is stored is always
   * an sRGB or greyscale bitmap, so <code>ImageColorSpace</code> answers from the bitmap and never
   * repeats the option back.
   */
  @Test
  public void colorSpaceInterpretsTheSamplesAndIsNotRecorded() {
    check("ImageData(Image({{{0.0,1.0,1.0}}},ColorSpace->\"HSB\"),\"Byte\")=={{{255,0,0}}}", //
        "True");
    check("ImageColorSpace(Image({{{0.0,1.0,1.0}}},ColorSpace->\"HSB\"))", //
        "RGB");
    check("ImageColorSpace(Image({{0.0,1.0}}))", //
        "Grayscale");
    check("ImageColorSpace(Image({{{1.0,0.0,0.0}}}))", //
        "RGB");
  }

  /** <code>"CMYK"</code> reads four samples and stores the three RGB channels they denote. */
  @Test
  public void cmykCollapsesFourChannelsToThree() {
    check(
        "ImageData(Image({{{0.0,0.0,0.0,0.0}}},ColorSpace->\"CMYK\"),\"Byte\")"
            + "=={{{255,255,255}}}", //
        "True");
    check("ImageChannels(Image({{{0.0,0.0,0.0,0.0}}},ColorSpace->\"CMYK\"))", //
        "3");
  }

  /** <code>"Grayscale"</code> on colour data weighs the channels into one intensity. */
  @Test
  public void grayscaleOnColourDataIsALuminance() {
    check("ImageData(Image({{{1.0,0.0,0.0}}},ColorSpace->\"Grayscale\"),\"Byte\")=={{76}}", //
        "True");
    check("ImageChannels(Image({{{1.0,0.0,0.0}}},ColorSpace->\"Grayscale\"))", //
        "1");
  }

  /** Options may be written as a list of rules as well as as trailing rules. */
  @Test
  public void optionsMayBeWrittenAsAList() {
    check("ImageData(Image({{{1.0,0.0,0.0}}},{ColorSpace->\"Grayscale\"}),\"Byte\")=={{76}}", //
        "True");
  }

  /** <code>Automatic</code>, as a symbol or as a string, takes the colour space from the data. */
  @Test
  public void automaticColorSpaceTakesTheSpaceFromTheData() {
    check("ImageData(Image({{{1.0,0.0,0.0}}},ColorSpace->Automatic),\"Byte\")=={{{255,0,0}}}", //
        "True");
    check(
        "ImageData(Image({{{1.0,0.0,0.0}}},ColorSpace->\"Automatic\"),\"Byte\")"
            + "=={{{255,0,0}}}", //
        "True");
  }

  // ------------------------------------------------------------------ still pending
  //
  // What the option work has not reached yet. These are expected to be rewritten rather than kept
  // green; everything the work has already changed has moved to ImageOptionSemanticsTest.

  /**
   * PENDING: a rasterized graphics object has no source matrix for the sample type to be written
   * on, so it reports the type of its bitmap whatever was asked for.
   */
  @Test
  public void pendingTheSampleTypeOfARasterizedGraphicIsAlwaysByte() {
    check("ImageType(Image(Graphics(Disk()),\"Real32\"))", //
        "Byte");
  }

  /** Display options are not part of what makes two images the same picture. */
  @Test
  public void displayOptionsDoNotSplitIdentity() {
    check("Image({{0.0,1.0}},ImageSize->200)===Image({{0.0,1.0}})", //
        "True");
  }

  /**
   * <code>ColorSpace -&gt; "Grayscale"</code> on colour data weighs three channels into one, so the
   * three channel source matrix is not what the image holds and must not be kept as its data.
   *
   * <p>
   * This used to be a defect: <code>ImageChannels</code> said 1 and the <code>"Byte"</code> request
   * - which reads the bitmap - said <code>{{76}}</code>, while the <code>Real32</code> request
   * handed back the untouched <code>{{{1.0, 0.0, 0.0}}}</code>. The colour space alone no longer
   * decides whether the matrix survives; how many channels the data had decides too.
   */
  @Test
  public void grayscaleOnColourDataDoesNotKeepTheColourMatrix() {
    check("Dimensions(ImageData(Image({{{1.0,0.0,0.0}}},ColorSpace->\"Grayscale\")))", //
        "{1,1}");
    check(
        "ImageData(Image({{{1.0,0.0,0.0}}},ColorSpace->\"Grayscale\"))"
            + "==ImageData(Image({{{1.0,0.0,0.0}}},ColorSpace->\"Grayscale\"),\"Real32\")", //
        "True");
    // a single intensity is untouched by "Grayscale", so that matrix is still kept
    check("ImageData(Image({{0.0,0.5},{1.0,0.25}},ColorSpace->\"Grayscale\"))", //
        "{{0.0,0.5},\n {1.0,0.25}}");
  }
}
