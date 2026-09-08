package org.matheclipse.image.expression.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The options an <code>Image</code> carries, and the storage <code>ImageExpr</code> gives them.
 *
 * <p>
 * None of this is reachable from an evaluation yet - <code>Image</code> still reads only
 * <code>ColorSpace</code> and discards the rest - so it is exercised here.
 */
public class ImageOptionsTest {

  static {
    try {
      F.await();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException(e);
    }
  }

  @Test
  public void theDefaultsAreWhatAnImageWithoutOptionsCarries() {
    ImageOptions options = ImageOptions.DEFAULT;
    assertEquals(S.Automatic, options.colorSpace());
    assertTrue(options.interleaved(), "data is interleaved unless it is said not to be");
    assertEquals(S.Automatic, options.imageSize());
    assertEquals(S.Automatic, options.imageResolution());
    assertEquals(S.Automatic, options.magnification());
    assertEquals(F.assoc(), options.metaInformation());
    assertEquals(S.Center, options.alignmentPoint());
    assertEquals(S.Automatic, options.baselinePosition());
  }

  /** <code>Automatic</code> means "take the space from the data", which Pixels spells null. */
  @Test
  public void automaticIsNotAColourSpaceName() {
    assertNull(ImageOptions.DEFAULT.colorSpaceName());
    assertNull(ImageOptions.DEFAULT.withColorSpace(F.stringx("Automatic")).colorSpaceName());
    assertNull(ImageOptions.DEFAULT.withColorSpace(S.Automatic).colorSpaceName());
    assertEquals("HSB", ImageOptions.DEFAULT.withColorSpace(F.stringx("HSB")).colorSpaceName());
    // a colour space that is not written as a string names nothing
    assertNull(ImageOptions.DEFAULT.withColorSpace(F.C1).colorSpaceName());
  }

  @Test
  public void theRulesAreReportedInAlphabeticalOrder() {
    IAST rules = ImageOptions.DEFAULT.toRules();
    assertEquals(8, rules.argSize());
    assertEquals(F.Rule(S.AlignmentPoint, S.Center), rules.arg1());
    assertEquals(F.Rule(S.BaselinePosition, S.Automatic), rules.arg2());
    assertEquals(F.Rule(S.ColorSpace, S.Automatic), rules.arg3());
    assertEquals(F.Rule(S.ImageResolution, S.Automatic), rules.get(4));
    assertEquals(F.Rule(S.ImageSize, S.Automatic), rules.get(5));
    assertEquals(F.Rule(S.Interleaving, S.True), rules.get(6));
    assertEquals(F.Rule(S.Magnification, S.Automatic), rules.get(7));
    assertEquals(F.Rule(S.MetaInformation, F.assoc()), rules.get(8));
  }

  /** The layout is reported as the boolean it is, not as the <code>Automatic</code> written for it. */
  @Test
  public void interleavingIsReportedAsTrueOrFalse() {
    assertEquals(S.False, ImageOptions.DEFAULT.withInterleaved(false).toRules().get(6).second());
  }

  @Test
  public void copiesChangeOneFieldAndKeepTheRest() {
    ImageOptions changed = ImageOptions.DEFAULT.withImageSize(F.ZZ(200));
    assertEquals(F.ZZ(200), changed.imageSize());
    assertEquals(S.Center, changed.alignmentPoint());
    assertTrue(changed.interleaved());
    assertEquals(ImageOptions.DEFAULT, ImageOptions.DEFAULT.withImageSize(S.Automatic));
  }

  // -------------------------------------------------------------- ImageExpr storage

  private static ImageExpr redPixel() {
    return ImageExpr.toImageExpr(F.list(F.list(F.list(F.C1, F.C0, F.C0))));
  }

  @Test
  public void anImageBuiltWithoutOptionsCarriesTheDefaults() {
    assertEquals(ImageOptions.DEFAULT, redPixel().getOptions());
  }

  /**
   * Changing the options does not touch the pixels, so the encoded bytes are shared rather than the
   * image being drawn and encoded a second time.
   */
  @Test
  public void withOptionsSharesTheEncodedImage() {
    ImageExpr image = redPixel();
    ImageExpr resized = image.withOptions(ImageOptions.DEFAULT.withImageSize(F.ZZ(200)));
    assertNotSame(image, resized);
    assertEquals(F.ZZ(200), resized.getOptions().imageSize());
    assertEquals(image.toBase64EncodedString(), resized.toBase64EncodedString());
    assertEquals(image.getMatrix(), resized.getMatrix());
    // and asking for the options it already has is not a copy at all
    assertSame(image, image.withOptions(ImageOptions.DEFAULT));
  }

  /** Display options are not part of what makes two images the same picture. */
  @Test
  public void optionsDoNotSplitIdentity() {
    ImageExpr image = redPixel();
    assertEquals(image, image.withOptions(ImageOptions.DEFAULT.withImageSize(F.ZZ(200))));
  }

  // -------------------------------------------------------------- display size

  /** A 4 by 2 image, so that the shape of the picture is visible in the sizes it is shown at. */
  private static double[] sizeOf(ImageOptions options) {
    return options.displaySize(4, 2);
  }

  /** With nothing asked for, there is no size to write and the image is shown pixel for pixel. */
  @Test
  public void theDefaultsAskForNoParticularSize() {
    assertNull(sizeOf(ImageOptions.DEFAULT));
  }

  @Test
  public void imageSizeIsAWidthAndTheShapeGivesTheHeight() {
    assertArrayEquals(new double[] {200.0, 100.0},
        sizeOf(ImageOptions.DEFAULT.withImageSize(F.ZZ(200))));
    // a pair gives both, and either half may be left to the shape of the image
    assertArrayEquals(new double[] {200.0, 50.0},
        sizeOf(ImageOptions.DEFAULT.withImageSize(F.list(F.ZZ(200), F.ZZ(50)))));
    assertArrayEquals(new double[] {200.0, 100.0},
        sizeOf(ImageOptions.DEFAULT.withImageSize(F.list(F.ZZ(200), S.Automatic))));
    assertArrayEquals(new double[] {200.0, 100.0},
        sizeOf(ImageOptions.DEFAULT.withImageSize(F.list(S.Automatic, F.ZZ(100)))));
  }

  /** <code>All</code> is the image's own size, which is what no options at all also comes to. */
  @Test
  public void allIsTheImagesOwnSize() {
    assertArrayEquals(new double[] {4.0, 2.0},
        sizeOf(ImageOptions.DEFAULT.withImageSize(S.All)));
  }

  @Test
  public void theNamedSizesAreWidths() {
    assertArrayEquals(new double[] {40.0, 20.0},
        sizeOf(ImageOptions.DEFAULT.withImageSize(S.Tiny)));
    assertArrayEquals(new double[] {180.0, 90.0},
        sizeOf(ImageOptions.DEFAULT.withImageSize(S.Small)));
    assertArrayEquals(new double[] {360.0, 180.0},
        sizeOf(ImageOptions.DEFAULT.withImageSize(S.Medium)));
    assertArrayEquals(new double[] {560.0, 280.0},
        sizeOf(ImageOptions.DEFAULT.withImageSize(S.Large)));
  }

  /**
   * <code>ImageResolution</code> says how many pixels are to occupy an inch, and a printer's point
   * is a 72nd of one - so 72 is pixel for pixel and 144 is half that.
   */
  @Test
  public void imageResolutionIsPixelsPerInch() {
    assertArrayEquals(new double[] {4.0, 2.0}, resolution(72));
    assertArrayEquals(new double[] {2.0, 1.0}, resolution(144));
    assertArrayEquals(new double[] {8.0, 4.0}, resolution(36));
  }

  private static double[] resolution(int pixelsPerInch) {
    return sizeOf(new ImageOptions(S.Automatic, true, S.Automatic, F.ZZ(pixelsPerInch),
        S.Automatic, F.assoc(), S.Center, S.Automatic));
  }

  /** An outright size is an answer; a resolution is only consulted when there is no size. */
  @Test
  public void imageSizeWinsOverImageResolution() {
    ImageOptions options = new ImageOptions(S.Automatic, true, F.ZZ(200), F.ZZ(144), S.Automatic,
        F.assoc(), S.Center, S.Automatic);
    assertArrayEquals(new double[] {200.0, 100.0}, sizeOf(options));
  }

  /** Magnification scales whatever the other two came to, and on its own the image's own size. */
  @Test
  public void magnificationScalesWhateverTheSizeIs() {
    assertArrayEquals(new double[] {12.0, 6.0}, magnified(S.Automatic, 3));
    assertArrayEquals(new double[] {400.0, 200.0}, magnified(F.ZZ(200), 2));
  }

  private static double[] magnified(IExpr imageSize, int magnification) {
    return sizeOf(new ImageOptions(S.Automatic, true, imageSize, S.Automatic,
        F.ZZ(magnification), F.assoc(), S.Center, S.Automatic));
  }

  /** A size that says nothing is not a size, and the image falls back to its own. */
  @Test
  public void valuesThatSayNothingFallBack() {
    assertNull(sizeOf(ImageOptions.DEFAULT.withImageSize(F.ZZ(-5))));
    assertNull(sizeOf(ImageOptions.DEFAULT.withImageSize(F.C0)));
    assertNull(sizeOf(ImageOptions.DEFAULT.withImageSize(F.Dummy("x"))));
  }

  // -------------------------------------------------------------- the rendered tag

  private static String imgTag(ImageExpr image) {
    for (String line : image.toHTML().split("\n")) {
      if (line.contains("<img")) {
        return line.trim().replaceAll("base64, [^\"]*", "base64, ...");
      }
    }
    return "<no img>";
  }

  /** With no options the tag is what it always was, with no style attribute at all. */
  @Test
  public void anImageWithoutOptionsIsRenderedAtItsOwnSize() {
    assertEquals("<img src=\"data:image/png;base64, ...\"/>", imgTag(redPixel()));
  }

  @Test
  public void theSizeIsWrittenAsACssStyle() {
    assertEquals("<img src=\"data:image/png;base64, ...\" style=\"width:200px; height:200px\"/>",
        imgTag(redPixel().withOptions(ImageOptions.DEFAULT.withImageSize(F.ZZ(200)))));
  }

  /** A size that rounds away to nothing is still a pixel, because a zero sized image is invisible. */
  @Test
  public void aSizeIsNeverRoundedAwayToNothing() {
    assertEquals("<img src=\"data:image/png;base64, ...\" style=\"width:1px; height:1px\"/>",
        imgTag(redPixel().withOptions(ImageOptions.DEFAULT.withImageSize(F.num(0.1)))));
  }

  @Test
  public void copyCarriesTheOptionsOver() {
    ImageExpr image = redPixel().withOptions(ImageOptions.DEFAULT.withImageSize(F.ZZ(200)));
    assertEquals(F.ZZ(200), ((ImageExpr) image.copy()).getOptions().imageSize());
  }

  /**
   * The matrix is kept only where it still describes the pixels. Three colour channels weighed into
   * one intensity is the case that used to get this wrong.
   */
  @Test
  public void theSourceMatrixIsKeptOnlyWhenItStillDescribesThePixels() {
    IAST colour = F.list(F.list(F.list(F.C1, F.C0, F.C0)));
    assertEquals(colour, ImageExpr.toImageExpr(colour, "RGB").getMatrix());
    assertEquals(colour, ImageExpr.toImageExpr(colour, "Automatic").getMatrix());
    assertNull(ImageExpr.toImageExpr(colour, "Grayscale").getMatrix(), "three channels into one");
    assertNull(ImageExpr.toImageExpr(colour, "HSB").getMatrix());
    assertNull(ImageExpr.toImageExpr(colour, "CMYK").getMatrix());

    // one intensity is left alone by "Grayscale", so that matrix survives
    IAST grey = F.list(F.list(F.C0, F.C1));
    assertEquals(grey, ImageExpr.toImageExpr(grey, "Grayscale").getMatrix());
  }
}
