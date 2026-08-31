package org.matheclipse.core.io;

import java.io.InputStream;
import java.io.OutputStream;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The image readers and writers used by <code>Import</code> and <code>Export</code>.
 *
 * <p>
 * <code>Export</code> lives in <code>matheclipse-core</code>, which must not depend on
 * <code>matheclipse-image</code>, so the dependency is inverted: <code>ImageInit.init()</code>
 * installs an implementation here and core calls it through this interface. The same pattern
 * <code>matheclipse-bio</code> uses for {@link BioSequenceFormat} and
 * <code>matheclipse-compile</code> for <code>IExprCompiler</code>.
 *
 * <p>
 * While no implementation is installed, <code>Import</code> and <code>Export</code> fall back to
 * the pixel matrix handling they had before, so core on its own keeps working. With one installed
 * they read and write an <code>Image</code> object instead, and gain every format
 * <code>javax.imageio</code> has a plugin for.
 */
public interface ImageFormatIO {

  /**
   * The installed reader/writer, or <code>null</code> when <code>matheclipse-image</code> is not
   * present.
   */
  ImageFormatIO[] INSTANCE = new ImageFormatIO[1];

  /** Whether {@link #importImage(InputStream, Extension)} can read <code>format</code>. */
  boolean canImport(Extension format);

  /** Whether {@link #exportImage(OutputStream, IExpr, Extension)} can write <code>format</code>. */
  boolean canExport(Extension format);

  /**
   * Read an image.
   *
   * @return the image, or {@link org.matheclipse.core.expression.F#NIL} if the stream holds no
   *         image of that format
   */
  IExpr importImage(InputStream inputStream, Extension format);

  /**
   * Write <code>expr</code> - an image, a graphics object, or a matrix of pixel values.
   *
   * @return <code>false</code> if <code>expr</code> is not an image, or writing failed
   */
  boolean exportImage(OutputStream outputStream, IExpr expr, Extension format);

  /** Install the reader/writer. Called from <code>org.matheclipse.image.ImageInit</code>. */
  static void install(ImageFormatIO imageFormatIO) {
    INSTANCE[0] = imageFormatIO;
  }

  /** @return the installed reader/writer, or <code>null</code> */
  static ImageFormatIO get() {
    return INSTANCE[0];
  }
}
