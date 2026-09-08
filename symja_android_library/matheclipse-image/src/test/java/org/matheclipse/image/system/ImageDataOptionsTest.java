package org.matheclipse.image.system;

import org.junit.jupiter.api.Test;

/**
 * The options of <code>ImageData</code>.
 *
 * <p>
 * <code>Interleaving</code> here is a different question from the one <code>Image</code> asks. There
 * it says how the data being read was written; here it says how the answer is to be written, and
 * the default is interleaved whatever the image itself stores. Only
 * <code>Interleaving -&gt; Automatic</code> asks the image.
 */
public class ImageDataOptionsTest extends AbstractTestCase {

  /** A one row RGB image, red then green. */
  private static final String RGB = "Image({{{1.0,0.0,0.0},{0.0,1.0,0.0}}})";

  /** The same picture built from planar data, so its own form is planar. */
  private static final String RGB_PLANAR =
      "Image({{{1.0,0.0}},{{0.0,1.0}},{{0.0,0.0}}},Interleaving->False)";

  @Test
  public void bothOptionsAreDeclared() {
    check("Options(ImageData)", //
        "{DataReversed->False,Interleaving->True}");
  }

  // -------------------------------------------------------------------- Interleaving

  /**
   * Interleaved is <code>{row, column, channel}</code> and planar is
   * <code>{channel, row, column}</code>, so the same two pixels are reported with the axes the
   * other way round.
   */
  @Test
  public void interleavingDecidesTheOrderOfTheAxes() {
    check("Dimensions(ImageData(" + RGB + ",\"Byte\"))", //
        "{1,2,3}");
    check("Dimensions(ImageData(" + RGB + ",\"Byte\",Interleaving->False))", //
        "{3,1,2}");
    check("ImageData(" + RGB + ",\"Byte\",Interleaving->False)"
        + "=={{{255,0}},{{0,255}},{{0,0}}}", //
        "True");
  }

  @Test
  public void trueIsTheDefault() {
    check("ImageData(" + RGB + ",\"Byte\",Interleaving->True)==ImageData(" + RGB + ",\"Byte\")", //
        "True");
  }

  /**
   * The default does not ask the image what it stores: an image built from planar data still
   * reports interleaved.
   */
  @Test
  public void theDefaultIgnoresWhatTheImageStores() {
    check("ImageData(" + RGB_PLANAR + ",\"Byte\")=={{{255,0,0},{0,255,0}}}", //
        "True");
    check("ImageData(" + RGB_PLANAR + ",\"Byte\")==ImageData(" + RGB + ",\"Byte\")", //
        "True");
  }

  /** <code>Automatic</code> is the one setting that hands back the form the image holds. */
  @Test
  public void automaticReportsTheFormTheImageStores() {
    check("ImageData(" + RGB_PLANAR + ",\"Byte\",Interleaving->Automatic)"
        + "=={{{255,0}},{{0,255}},{{0,0}}}", //
        "True");
    check("ImageData(" + RGB + ",\"Byte\",Interleaving->Automatic)=={{{255,0,0},{0,255,0}}}", //
        "True");
    // an image with no data of its own - a rasterized graphic, or anything an image operation
    // returned - counts as interleaved
    check("Dimensions(ImageData(ImageResize(" + RGB + ",{2,1}),Interleaving->Automatic))", //
        "{1,2,3}");
  }

  /**
   * A greyscale image is one plane, so planar gives a rank three answer where interleaved gives the
   * plain matrix.
   */
  @Test
  public void aGreyscaleImageIsOnePlane() {
    check("ImageData(Image({{0.0,1.0}}),\"Byte\")=={{0,255}}", //
        "True");
    check("ImageData(Image({{0.0,1.0}}),\"Byte\",Interleaving->False)=={{{0,255}}}", //
        "True");
  }

  /** Data written one way and read back the same way is the picture it started as. */
  @Test
  public void planarDataRoundTrips() {
    check("Image(ImageData(" + RGB + ",Interleaving->False),Interleaving->False)===" + RGB, //
        "True");
    check("Image(ImageData(" + RGB + "))===" + RGB, //
        "True");
  }

  // -------------------------------------------------------------------- DataReversed

  @Test
  public void dataReversedGivesTheRowsBottomToTop() {
    check("ImageData(Image({{0,0},{255,255}}),\"Byte\")=={{0,0},{255,255}}", //
        "True");
    check("ImageData(Image({{0,0},{255,255}}),\"Byte\",DataReversed->True)"
        + "=={{255,255},{0,0}}", //
        "True");
  }

  /** Each plane is turned over, not the list of planes. */
  @Test
  public void dataReversedAppliesInsideEachPlane() {
    check("ImageData(Image({{{1.0,0.0,0.0}},{{0.0,1.0,0.0}}}),\"Byte\",Interleaving->False,"
        + "DataReversed->True)=={{{0},{255}},{{255},{0}},{{0},{0}}}", //
        "True");
  }

  // ----------------------------------------------------- the stored matrix shortcut

  /**
   * An image remembers the matrix it was built from and hands it straight back where that is the
   * answer to what was asked. It must not do so when the answer would be the wrong way round or
   * upside down - which is visible here, because the remembered matrix carries the matrix layout of
   * <code>OutputForm</code> and breaks one row per line where a matrix read off the bitmap does
   * not.
   */
  @Test
  public void theRememberedMatrixIsOnlyHandedBackWhenItIsTheAnswer() {
    check("ImageData(Image({{0.0,1.0},{1.0,0.0}}))", //
        "{{0.0,1.0},\n {1.0,0.0}}");
    check("ImageData(Image({{0.0,1.0},{1.0,0.0}}),DataReversed->True)", //
        "{{1.0,0.0},{0.0,1.0}}");
    check("ImageData(Image({{0.0,1.0},{1.0,0.0}}),Interleaving->False)", //
        "{{{0.0,1.0},{1.0,0.0}}}");
  }

  // -------------------------------------------------------------------- refusals

  @Test
  public void badOptionValuesAreReported() {
    check("ImageData(Image({{0.0,1.0}}),Interleaving->7)", //
        "ImageData(Image(Dimensions: 2,1 Transparency: 1),Interleaving->7)");
    check("ImageData(Image({{0.0,1.0}}),DataReversed->7)", //
        "ImageData(Image(Dimensions: 2,1 Transparency: 1),DataReversed->7)");
  }

  /** An unknown option is reported, and the pixels are still handed back. */
  @Test
  public void anUnknownOptionIsReportedAndTheDataStillComesBack() {
    check("ImageData(Image({{0.0,1.0}}),Bogus->1)=={{0.0,1.0}}", //
        "True");
    check("ImageData(Image({{0.0,1.0}}),\"Byte\",Bogus->1)=={{0,255}}", //
        "True");
  }

  @Test
  public void whatIsNotAnImageOrATypeIsLeftAlone() {
    check("ImageData(7)", //
        "ImageData(7)");
    check("ImageData(Image({{0.0,1.0}}),7)", //
        "ImageData(Image(Dimensions: 2,1 Transparency: 1),7)");
  }
}
