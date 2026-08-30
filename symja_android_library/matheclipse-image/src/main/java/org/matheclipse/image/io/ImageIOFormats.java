package org.matheclipse.image.io;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import javax.imageio.ImageIO;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.io.Extension;
import org.matheclipse.core.io.ImageFormatIO;
import org.matheclipse.image.expression.data.ImageExpr;

/**
 * The <code>Import</code> and <code>Export</code> image formats, backed by
 * <code>javax.imageio</code>.
 *
 * <p>
 * Which formats actually work is asked of {@link ImageIO} rather than hard coded, because the answer
 * depends on what is on the classpath: the JDK brings PNG, JPEG, GIF, BMP and WBMP readers and
 * writers plus a TIFF pair, and the TwelveMonkeys plugins this module depends on add WebP, PNM, PSD,
 * ICO, TGA and a JPEG reader that handles CMYK and progressive files the JDK one rejects. They
 * announce themselves through the <code>ServiceLoader</code>, so nothing here names them.
 *
 * <p>
 * Installed by <code>ImageInit</code>; see {@link ImageFormatIO} for why the dependency runs this
 * way round.
 */
public class ImageIOFormats implements ImageFormatIO {

  /** The <code>javax.imageio</code> format name of every {@link Extension} that names an image. */
  private static final Map<Extension, String> FORMAT_NAMES = new EnumMap<>(Extension.class);

  static {
    FORMAT_NAMES.put(Extension.BMP, "bmp");
    FORMAT_NAMES.put(Extension.GIF, "gif");
    FORMAT_NAMES.put(Extension.ICO, "ico");
    FORMAT_NAMES.put(Extension.JPEG, "jpeg");
    FORMAT_NAMES.put(Extension.PNG, "png");
    FORMAT_NAMES.put(Extension.PNM, "pnm");
    FORMAT_NAMES.put(Extension.PSD, "psd");
    FORMAT_NAMES.put(Extension.TGA, "tga");
    FORMAT_NAMES.put(Extension.TIFF, "tiff");
    FORMAT_NAMES.put(Extension.WEBP, "webp");
  }

  /** The formats that cannot carry an alpha channel, so it has to be flattened before writing. */
  private static boolean discardsAlpha(Extension format) {
    return format == Extension.JPEG || format == Extension.BMP;
  }

  @Override
  public boolean canImport(Extension format) {
    String name = FORMAT_NAMES.get(format);
    return name != null && ImageIO.getImageReadersByFormatName(name).hasNext();
  }

  @Override
  public boolean canExport(Extension format) {
    String name = FORMAT_NAMES.get(format);
    return name != null && ImageIO.getImageWritersByFormatName(name).hasNext();
  }

  @Override
  public IExpr importImage(InputStream inputStream, Extension format) {
    try {
      BufferedImage bufferedImage = ImageIO.read(inputStream);
      if (bufferedImage != null) {
        return new ImageExpr(bufferedImage, null);
      }
    } catch (IOException ioe) {
      // fall through - the caller reports "no image of that format"
    }
    return F.NIL;
  }

  @Override
  public boolean exportImage(OutputStream outputStream, IExpr expr, Extension format) {
    String name = FORMAT_NAMES.get(format);
    if (name == null) {
      return false;
    }
    BufferedImage bufferedImage = toBufferedImage(expr);
    if (bufferedImage == null) {
      return false;
    }
    if (discardsAlpha(format) && bufferedImage.getColorModel().hasAlpha()) {
      bufferedImage = withoutAlpha(bufferedImage);
    }
    try {
      return ImageIO.write(bufferedImage, name, outputStream);
    } catch (IOException ioe) {
      return false;
    }
  }

  /**
   * An image, a graphics object or a matrix of pixel values as a {@link BufferedImage}.
   *
   * @return <code>null</code> if <code>expr</code> is none of those
   */
  private static BufferedImage toBufferedImage(IExpr expr) {
    if (expr instanceof ImageExpr) {
      return ((ImageExpr) expr).getBufferedImage();
    }
    if (expr.isAST()) {
      // a Graphics/Graphics3D object gets rasterized, a matrix is read as pixel values
      ImageExpr imageExpr = ImageExpr.toImageExpr((IAST) expr);
      if (imageExpr != null) {
        return imageExpr.getBufferedImage();
      }
    }
    return null;
  }

  /**
   * Compose <code>bufferedImage</code> onto white, for the formats that have nowhere to put the
   * alpha channel. Writing an image with alpha as JPEG otherwise produces either a failure or
   * inverted colours, depending on the writer.
   */
  private static BufferedImage withoutAlpha(BufferedImage bufferedImage) {
    BufferedImage opaque = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(),
        BufferedImage.TYPE_INT_RGB);
    java.awt.Graphics2D graphics = opaque.createGraphics();
    try {
      graphics.setColor(java.awt.Color.WHITE);
      graphics.fillRect(0, 0, opaque.getWidth(), opaque.getHeight());
      graphics.drawImage(bufferedImage, 0, 0, null);
    } finally {
      graphics.dispose();
    }
    return opaque;
  }

  /** The <code>javax.imageio</code> format name, for diagnostics and tests. */
  public static String formatName(Extension format) {
    return FORMAT_NAMES.get(format);
  }

  /** The image {@link Extension} for a <code>javax.imageio</code> format name, or <code>null</code>. */
  public static Extension extensionOf(String formatName) {
    String lowerCase = formatName.toLowerCase(Locale.US);
    for (Map.Entry<Extension, String> entry : FORMAT_NAMES.entrySet()) {
      if (entry.getValue().equals(lowerCase)) {
        return entry.getKey();
      }
    }
    return null;
  }
}
