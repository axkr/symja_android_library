package org.matheclipse.io.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.File;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.io.Extension;
import org.matheclipse.core.io.ImageFormatIO;
import org.matheclipse.io.IOInit;

/**
 * <code>Import</code> and <code>Export</code> of images, once <code>matheclipse-image</code> has
 * installed its {@link ImageFormatIO}.
 *
 * <p>
 * The behaviour this pins down is a change: <code>Import["photo.png"]</code> used to evaluate to a
 * nested matrix of pixel values and now evaluates to an <code>Image</code> object.
 * <code>ImageData</code> recovers the old value.
 */
public class ImageImportExportTest {

  @BeforeAll
  public static void setUp() throws InterruptedException {
    IOInit.init();
    F.await();
  }

  private static IExpr eval(String filename, String... arguments) {
    boolean fileSystemEnabled = Config.FILESYSTEM_ENABLED;
    try {
      Config.FILESYSTEM_ENABLED = true;
      IExpr[] args = new IExpr[arguments.length + 1];
      args[0] = F.stringx(filename);
      for (int i = 0; i < arguments.length; i++) {
        args[i + 1] = F.stringx(arguments[i]);
      }
      return S.Import.of(EvalEngine.get(), args);
    } finally {
      Config.FILESYSTEM_ENABLED = fileSystemEnabled;
    }
  }

  private static String resource(String name) {
    return new File(ImageImportExportTest.class.getResource(name).getFile()).getAbsolutePath();
  }

  @Test
  public void theReaderIsInstalled() {
    ImageFormatIO imageFormatIO = ImageFormatIO.get();
    assertNotNull(imageFormatIO, "ImageInit should have installed a reader");
    assertTrue(imageFormatIO.canImport(Extension.PNG));
    assertTrue(imageFormatIO.canExport(Extension.PNG));
    assertTrue(imageFormatIO.canImport(Extension.JPEG));
  }

  /** WebP and TIFF come from the TwelveMonkeys plugins rather than the JDK. */
  @Test
  public void theTwelveMonkeysPluginsAreDiscovered() {
    ImageFormatIO imageFormatIO = ImageFormatIO.get();
    assertTrue(imageFormatIO.canImport(Extension.TIFF), "TIFF reader");
    assertTrue(imageFormatIO.canImport(Extension.WEBP), "WebP reader");
    assertTrue(imageFormatIO.canImport(Extension.PNM), "PNM reader");
  }

  @Test
  public void importingAPngGivesAnImage() {
    IExpr image = eval(resource("/io/rgba15x33.png"));
    assertEquals("Image(Dimensions: 15,33 Transparency: 3)", image.toString());
    assertEquals("{15,33}", S.ImageDimensions.of(EvalEngine.get(), image).toString());
  }

  @Test
  public void importingAGrayscalePngGivesAnImage() {
    IExpr image = eval(resource("/io/gray15x9.png"));
    assertEquals("{15,9}", S.ImageDimensions.of(EvalEngine.get(), image).toString());
  }

  @Test
  public void importingAJpegGivesAnImage() {
    IExpr image = eval(resource("/io/rgb15x33.jpg"));
    assertEquals("{15,33}", S.ImageDimensions.of(EvalEngine.get(), image).toString());
  }

  /** The format may be named explicitly, and "TIF" is the same format as "TIFF". */
  @Test
  public void theFormatMayBeGivenAsTheSecondArgument() {
    IExpr image = eval(resource("/io/rgba15x33.png"), "PNG");
    assertEquals("{15,33}", S.ImageDimensions.of(EvalEngine.get(), image).toString());
    assertEquals(Extension.TIFF, Extension.importExtension("TIF"));
    assertEquals(Extension.TIFF, Extension.importFilename("scan.tif"));
    assertEquals(Extension.TIFF, Extension.exportFilename("scan.tif"));
  }

  @Test
  public void anImageRoundTripsThroughEveryWritableFormat(@TempDir Path directory) {
    IExpr image = eval(resource("/io/rgba15x33.png"));

    for (String extension : new String[] {"png", "gif", "tiff", "jpg", "bmp"}) {
      String written = directory.resolve("round-trip." + extension).toString();
      boolean fileSystemEnabled = Config.FILESYSTEM_ENABLED;
      IExpr exported;
      try {
        Config.FILESYSTEM_ENABLED = true;
        exported = S.Export.of(EvalEngine.get(), F.stringx(written), image);
      } finally {
        Config.FILESYSTEM_ENABLED = fileSystemEnabled;
      }
      assertEquals(written, exported.toString(),
          "Export should return the file name for " + extension);
      assertTrue(new File(written).length() > 0, extension + " should not be empty");

      assertEquals("{15,33}", S.ImageDimensions.of(EvalEngine.get(), eval(written)).toString(),
          "the size should survive a round trip through " + extension);
    }
  }

  /** Exporting to a format with nowhere to put the alpha channel composes onto white. */
  @Test
  public void alphaIsFlattenedRatherThanRejected(@TempDir Path directory) {
    IExpr image = eval(resource("/io/rgba15x33.png"));
    String written = directory.resolve("flattened.jpg").toString();
    boolean fileSystemEnabled = Config.FILESYSTEM_ENABLED;
    try {
      Config.FILESYSTEM_ENABLED = true;
      S.Export.of(EvalEngine.get(), F.stringx(written), image);
    } finally {
      Config.FILESYSTEM_ENABLED = fileSystemEnabled;
    }
    IExpr reread = eval(written);
    assertEquals("Image(Dimensions: 15,33 Transparency: 1)", reread.toString());
  }
}
