package org.matheclipse.image.system;

import org.junit.jupiter.api.Test;

/**
 * What the options of <code>Image</code> do, now that all of them are read rather than only
 * <code>ColorSpace</code>.
 *
 * <p>
 * Two of them describe the pixels: <code>Interleaving</code> says which way round the data is
 * written and <code>ColorSpace</code> says what the samples mean. The rest are carried along and
 * change nothing about the picture, which is what most of the tests at the bottom check.
 */
public class ImageOptionSemanticsTest extends AbstractTestCase {

  /** The picture used throughout: two by two, red and green on the diagonals. */
  private static final String INTERLEAVED =
      "{{{1.0,0.0,0.0},{0.0,1.0,0.0}},{{0.0,1.0,0.0},{1.0,0.0,0.0}}}";

  /** The same picture as three planes - red, green, blue - of a two by two image. */
  private static final String PLANAR =
      "{{{1.0,0.0},{0.0,1.0}},{{0.0,1.0},{1.0,0.0}},{{0.0,0.0},{0.0,0.0}}}";

  @Test
  public void everyDocumentedOptionIsDeclared() {
    check("Options(Image)", //
        "{AlignmentPoint->Center,BaselinePosition->Automatic,ColorSpace->Automatic,"
            + "ImageResolution->Automatic,ImageSize->Automatic,Interleaving->Automatic,"
            + "Magnification->Automatic,MetaInformation-><||>}");
  }

  // -------------------------------------------------------------------- Interleaving

  /**
   * Interleaved data is <code>{row, column, channel}</code> and planar data is
   * <code>{channel, row, column}</code>, so the same picture is written with its axes in a
   * different order and comes out the same image.
   */
  @Test
  public void planarAndInterleavedDataDescribeTheSamePicture() {
    check("Image(" + PLANAR + ",Interleaving->False)===Image(" + INTERLEAVED + ")", //
        "True");
    check("ImageDimensions(Image(" + PLANAR + ",Interleaving->False))", //
        "{2,2}");
    check("ImageChannels(Image(" + PLANAR + ",Interleaving->False))", //
        "3");
  }

  /**
   * <code>ImageData</code> reports interleaved whatever the image stores, so the planar data goes
   * in and the interleaved reading of it comes back.
   */
  @Test
  public void imageDataReportsInterleavedWhateverWentIn() {
    check("ImageData(Image(" + PLANAR + ",Interleaving->False),\"Byte\")"
        + "=={{{255,0,0},{0,255,0}},{{0,255,0},{255,0,0}}}", //
        "True");
    // in particular the planar matrix is not handed straight back, even on its own scale
    check("ImageData(Image(" + PLANAR + ",Interleaving->False))=={{{1.0,0.0,0.0},{0.0,1.0,0.0}},"
        + "{{0.0,1.0,0.0},{1.0,0.0,0.0}}}", //
        "True");
  }

  /** <code>True</code> and <code>Automatic</code> both read the data the usual way round. */
  @Test
  public void trueAndAutomaticAreBothInterleaved() {
    check("Image(" + INTERLEAVED + ",Interleaving->True)===Image(" + INTERLEAVED + ")", //
        "True");
    check("Image(" + INTERLEAVED + ",Interleaving->Automatic)===Image(" + INTERLEAVED + ")", //
        "True");
  }

  /** A matrix of scalars is one greyscale plane whichever way it is read. */
  @Test
  public void aMatrixOfScalarsIsGreyscaleInEitherLayout() {
    check("Image({{0.0,1.0},{1.0,0.0}},Interleaving->False)===Image({{0.0,1.0},{1.0,0.0}})", //
        "True");
  }

  /** One plane is a greyscale image, and four are RGB with alpha. */
  @Test
  public void planarDataMayHaveOneOrFourPlanesToo() {
    check("ImageChannels(Image({{{0.0,1.0},{1.0,0.0}}},Interleaving->False))", //
        "1");
    check("ImageChannels(Image({{{1.0}},{{0.0}},{{0.0}},{{0.5}}},Interleaving->False))", //
        "4");
    check("ImageData(Image({{{1.0}},{{0.0}},{{0.0}},{{0.5}}},Interleaving->False),\"Byte\")"
        + "=={{{255,0,0,128}}}", //
        "True");
  }

  @Test
  public void interleavingMustBeTrueFalseOrAutomatic() {
    check("Image({{0.0,1.0}},Interleaving->7)", //
        "Image({{0.0,1.0}},Interleaving->7)");
  }

  // -------------------------------------------------------------------- sample type

  /**
   * <code>Image(data, type)</code> writes the data on the scale <code>type</code> names, which is
   * what <code>ImageType</code> then reports.
   */
  @Test
  public void theSampleTypeArgumentIsHonoured() {
    check("ImageType(Image({{0.0,0.5}},\"Byte\"))", //
        "Byte");
    check("ImageData(Image({{0.0,0.5}},\"Byte\"),\"Byte\")=={{0,128}}", //
        "True");
    check("ImageType(Image({{0,255}},\"Real32\"))", //
        "Real32");
    check("ImageData(Image({{0,255}},\"Real32\"))=={{0.0,1.0}}", //
        "True");
    check("ImageType(Image({{0.0,1.0}},\"Bit\"))", //
        "Bit");
  }

  /**
   * Data already on the scale it is asked for is left exactly as written, so a real matrix asked
   * for as <code>"Real32"</code> is the lossless thing it would have been without the argument.
   */
  @Test
  public void dataAlreadyOnTheRequestedScaleIsNotConverted() {
    check("ImageData(Image({{0.0,0.3}},\"Real32\"))=={{0.0,0.3}}", //
        "True");
    check("ImageData(Image({{0.0,0.3}},\"Real64\"))=={{0.0,0.3}}", //
        "True");
    check("ImageType(Image({{0.0,0.3}},\"Real32\"))", //
        "Real32");
  }

  @Test
  public void anUnknownSampleTypeIsReported() {
    check("Image({{0.0,0.5}},\"Bogus\")", //
        "Image({{0.0,0.5}},Bogus)");
    // and something that is neither a type nor an option is left alone
    check("Image({{0.0,0.5}},7)", //
        "Image({{0.0,0.5}},7)");
  }

  // -------------------------------------------------------------------- ColorSpace

  /** A colour space that cannot be read is reported rather than quietly read as RGB. */
  @Test
  public void anUnknownColorSpaceIsReported() {
    check("Image({{{1.0,0.0,0.0}}},ColorSpace->\"XYZ\")", //
        "Image({{{1.0,0.0,0.0}}},ColorSpace->XYZ)");
    check("Image({{{1.0,0.0,0.0}}},ColorSpace->\"Bogus\")", //
        "Image({{{1.0,0.0,0.0}}},ColorSpace->Bogus)");
  }

  /**
   * A colour space needs the channels it is named for. Reading three samples as
   * <code>"CMYK"</code> would have to invent one and reading one as <code>"HSB"</code> would have
   * to invent two.
   */
  @Test
  public void aColorSpaceNeedsTheChannelsItIsNamedFor() {
    check("Image({{{1.0,0.0,0.0}}},ColorSpace->\"CMYK\")", //
        "Image({{{1.0,0.0,0.0}}},ColorSpace->CMYK)");
    check("Image({{0.0,1.0}},ColorSpace->\"HSB\")", //
        "Image({{0.0,1.0}},ColorSpace->HSB)");
    // greyscale weighs whatever channels there are into one intensity, so it takes any of them
    check("ImageData(Image({{{1.0,0.0,0.0}}},ColorSpace->\"Grayscale\"),\"Byte\")=={{76}}", //
        "True");
    check("ImageData(Image({{0.0,0.5}},ColorSpace->\"Grayscale\"),\"Byte\")=={{0,128}}", //
        "True");
    // RGB reads three channels or four
    check("ImageChannels(Image({{{1.0,0.0,0.0,1.0}}},ColorSpace->\"RGB\"))", //
        "4");
  }

  /** The spaces that do work are unchanged: the samples are interpreted, and RGB is stored. */
  @Test
  public void aWorkingColorSpaceStillOnlyInterpretsTheSamples() {
    check("ImageData(Image({{{0.0,1.0,1.0}}},ColorSpace->\"HSB\"),\"Byte\")=={{{255,0,0}}}", //
        "True");
    check("ImageColorSpace(Image({{{0.0,1.0,1.0}}},ColorSpace->\"HSB\"))", //
        "RGB");
    check("ImageData(Image({{{0.0,0.0,0.0,0.0}}},ColorSpace->\"CMYK\"),\"Byte\")"
        + "=={{{255,255,255}}}", //
        "True");
  }

  // -------------------------------------------------------------------- the rest

  /**
   * The display options are accepted, and none of them changes a pixel. They are written here one
   * at a time so that a failure names the option that broke.
   */
  @Test
  public void everyDisplayOptionIsAcceptedAndChangesNothing() {
    check("ImageData(Image({{0.0,1.0}},ImageSize->200))=={{0.0,1.0}}", //
        "True");
    check("ImageData(Image({{0.0,1.0}},Magnification->3))=={{0.0,1.0}}", //
        "True");
    check("ImageData(Image({{0.0,1.0}},ImageResolution->300))=={{0.0,1.0}}", //
        "True");
    check("ImageData(Image({{0.0,1.0}},MetaInformation-><|\"Owner\"->\"John\"|>))=={{0.0,1.0}}", //
        "True");
    check("ImageData(Image({{0.0,1.0}},MetaInformation->{\"Owner\"->\"John\"}))=={{0.0,1.0}}", //
        "True");
    check("ImageData(Image({{0.0,1.0}},AlignmentPoint->Center))=={{0.0,1.0}}", //
        "True");
    check("ImageData(Image({{0.0,1.0}},BaselinePosition->Automatic))=={{0.0,1.0}}", //
        "True");
  }

  /** All of them at once, with the sample type argument in front. */
  @Test
  public void theOptionsMayBeGivenTogetherAndAfterTheSampleType() {
    check("ImageType(Image({{0.0,0.5}},\"Byte\",ImageSize->200,Magnification->3,"
        + "ImageResolution->300,ColorSpace->\"Grayscale\",Interleaving->Automatic,"
        + "MetaInformation-><|\"Owner\"->\"John\"|>,AlignmentPoint->Center,"
        + "BaselinePosition->Automatic))", //
        "Byte");
  }

  /**
   * An option name <code>Image</code> does not know is reported, and the image is still built - the
   * same thing a misspelled option does everywhere else.
   */
  @Test
  public void anUnknownOptionIsReportedAndTheImageIsStillBuilt() {
    check("ImageDimensions(Image({{0.0,1.0}},Bogus->1))", //
        "{2,1}");
    check("ImageDimensions(Image({{0.0,1.0}},\"Byte\",Bogus->1))", //
        "{2,1}");
  }

  /** Options may be written as a list of rules as well as as trailing rules. */
  @Test
  public void optionsMayBeWrittenAsAList() {
    check("Image({{{1.0,0.0,0.0}}},{ColorSpace->\"Grayscale\"})"
        + "===Image({{{1.0,0.0,0.0}}},ColorSpace->\"Grayscale\")", //
        "True");
  }

  /** A graphics object is rasterized, and the options ride along with the bitmap. */
  @Test
  public void aGraphicsObjectStillRasterizes() {
    check("ImageDimensions(Image(Graphics(Disk()),ImageSize->200))", //
        "{600,600}");
  }
}
