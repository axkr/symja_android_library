package org.matheclipse.image.expression.data;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.util.Base64;
import javax.imageio.ImageIO;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.LinearAlgebraUtil;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.output.HtmlTemplates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.external.fastutil.ints.IntArrayList;
import org.matheclipse.image.algo.Pixels;
import org.matheclipse.image.algo.Volumes;

/**
 * A 3D image: a volume of voxels.
 *
 * <p>
 * The data is the array <code>{slice, row, column}</code> of the samples, or
 * <code>{slice, row, column, channel}</code> where a voxel has several channels, written on the
 * scale of the type of the image - <code>0 ... 255</code> for a <code>"Byte"</code> image,
 * <code>0.0 ... 1.0</code> for a <code>"Real32"</code> one. Unlike a 2D image a volume is no
 * bitmap; the picture it is shown as is drawn from the data when it is asked for.
 *
 * <p>
 * The first slice is the top of the volume and its first row the back, so the dimensions
 * <code>{width, depth, height}</code> of the image are the dimensions of the data in reverse.
 */
public final class Image3DExpr extends DataExpr<IAST> {

  private static final long serialVersionUID = 5417392860015362911L;

  private final String sampleType;

  private final ImageOptions options;

  private final int slices;
  private final int rows;
  private final int columns;
  private final int channels;

  private transient SoftReference<BufferedImage> picture;

  private Image3DExpr(IAST data, String sampleType, ImageOptions options, int slices, int rows,
      int columns, int channels) {
    super(S.Image3D, data);
    this.sampleType = sampleType;
    this.options = options;
    this.slices = slices;
    this.rows = rows;
    this.columns = columns;
    this.channels = channels;
  }

  /**
   * A 3D image of the data.
   *
   * @param data the array <code>{slice, row, column}</code> or
   *        <code>{slice, row, column, channel}</code> of the samples
   * @param sampleType one of the five image types, or <code>null</code> to read it off the data
   * @return <code>null</code> if the data is no such array of real numbers, or has 2 or more
   *         than 4 channels
   */
  public static Image3DExpr of(IAST data, String sampleType, ImageOptions options) {
    IntArrayList dimensions = LinearAlgebraUtil.dimensions(data);
    if (dimensions == null || dimensions.size() < 3 || dimensions.size() > 4) {
      return null;
    }
    final int channels = dimensions.size() == 4 ? dimensions.getInt(3) : 1;
    if (dimensions.getInt(0) <= 0 || dimensions.getInt(1) <= 0 || dimensions.getInt(2) <= 0
        || (channels != 1 && channels != 3 && channels != 4)) {
      return null;
    }
    String type = sampleType != null ? sampleType : typeOf(data, new boolean[] {true});
    if (type == null) {
      return null;
    }
    IAST samples = sampleType != null ? Pixels.coerceToType(data, type) : data;
    if (samples == null) {
      return null;
    }
    if (Pixels.REAL32.equals(type) || Pixels.REAL64.equals(type)) {
      samples = Pixels.realSamples(samples);
    }
    return new Image3DExpr(samples, type, options, dimensions.getInt(0), dimensions.getInt(1),
        dimensions.getInt(2), channels);
  }

  /**
   * <code>"Bit"</code> when every sample is 0 or 1, <code>"Byte"</code> for any other integer
   * data, <code>"Real32"</code> as soon as one sample is no integer, <code>null</code> if one is
   * no real number.
   */
  private static String typeOf(IAST data, boolean[] bilevel) {
    String type = Pixels.BIT;
    for (int i = 1; i < data.size(); i++) {
      IExpr element = data.get(i);
      if (element.isList()) {
        String inner = typeOf((IAST) element, bilevel);
        if (inner == null) {
          return null;
        }
        if (Pixels.REAL32.equals(inner)) {
          type = inner;
        }
      } else if (!element.isReal()) {
        return null;
      } else if (!element.isInteger()) {
        type = Pixels.REAL32;
      } else if (!element.isZero() && !element.isOne()) {
        bilevel[0] = false;
      }
    }
    if (Pixels.REAL32.equals(type)) {
      return type;
    }
    return bilevel[0] ? Pixels.BIT : Pixels.BYTE;
  }

  /**
   * A 3D image of the samples <code>[channel][slice][row][column]</code>, which are on the scale
   * <code>0.0 ... 1.0</code>.
   */
  public static Image3DExpr of(double[][][][] samples, String sampleType, ImageOptions options) {
    final int channels = samples.length;
    final int slices = samples[0].length;
    final int rows = samples[0][0].length;
    final int columns = samples[0][0][0].length;
    final double scale = Pixels.BYTE.equals(sampleType) ? 255.0
        : Pixels.BIT16.equals(sampleType) ? 65535.0 : Pixels.BIT.equals(sampleType) ? 1.0 : 0.0;
    IASTAppendable data = F.ListAlloc(slices);
    for (int z = 0; z < slices; z++) {
      IASTAppendable slice = F.ListAlloc(rows);
      for (int y = 0; y < rows; y++) {
        IASTAppendable row = F.ListAlloc(columns);
        for (int x = 0; x < columns; x++) {
          if (channels == 1) {
            row.append(sample(samples[0][z][y][x], scale));
          } else {
            IASTAppendable voxel = F.ListAlloc(channels);
            for (int c = 0; c < channels; c++) {
              voxel.append(sample(samples[c][z][y][x], scale));
            }
            row.append(voxel);
          }
        }
        slice.append(row);
      }
      data.append(slice);
    }
    return new Image3DExpr(data, sampleType, options, slices, rows, columns, channels);
  }

  private static IExpr sample(double value, double scale) {
    return scale == 0.0 ? F.num(value)
        : F.ZZ(Math.max(0L, Math.min((long) scale, Math.round(value * scale))));
  }

  /** The type of the samples: <code>"Bit"</code>, <code>"Byte"</code>, ... */
  public String sampleType() {
    return sampleType;
  }

  /** The options this image carries; never <code>null</code>. */
  public ImageOptions getOptions() {
    return options;
  }

  /** <code>{width, depth, height}</code>: the columns, the rows and the slices of the data. */
  public int[] dimensions() {
    return new int[] {columns, rows, slices};
  }

  public int channels() {
    return channels;
  }

  /** The samples <code>[channel][slice][row][column]</code> on the scale <code>0.0 ... 1.0</code>. */
  public double[][][][] samples() {
    final double scale = Pixels.scaleOf(sampleType) / 255.0;
    double[][][][] result = new double[channels][slices][rows][columns];
    for (int z = 0; z < slices; z++) {
      IAST slice = (IAST) fData.get(z + 1);
      for (int y = 0; y < rows; y++) {
        IAST row = (IAST) slice.get(y + 1);
        for (int x = 0; x < columns; x++) {
          IExpr voxel = row.get(x + 1);
          if (channels == 1) {
            result[0][z][y][x] = voxel.evalf() * scale;
          } else {
            for (int c = 0; c < channels; c++) {
              result[c][z][y][x] = ((IAST) voxel).get(c + 1).evalf() * scale;
            }
          }
        }
      }
    }
    return result;
  }

  /**
   * The data on the scale of the given type: what <code>ImageData(image, type)</code> gives.
   */
  public IAST data(String type) {
    if (Pixels.sameScale(sampleType, type)) {
      return fData;
    }
    return of(samples(), type, options).fData;
  }

  /** The picture the volume is shown as. */
  public BufferedImage getPicture() {
    BufferedImage image = picture == null ? null : picture.get();
    if (image == null) {
      image = Volumes.picture(samples());
      picture = new SoftReference<BufferedImage>(image);
    }
    return image;
  }

  /** The picture as a PNG in Base64. */
  public String toBase64EncodedString() {
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final OutputStream b64 = Base64.getEncoder().wrap(outputStream)) {
      ImageIO.write(getPicture(), "png", b64);
      b64.close();
      return outputStream.toString();
    } catch (IOException ioex) {
      return "";
    }
  }

  @Override
  public String toHTML() {
    String[] argsToRender = new String[3];
    argsToRender[0] = toBase64EncodedString();
    argsToRender[1] = "";
    argsToRender[2] = "";
    return Errors.templateRender(HtmlTemplates.IMAGE_TEMPLATE, argsToRender);
  }

  /**
   * <code>Image3D[data, "type", options]</code>; evaluating it builds the same image again.
   */
  @Override
  public IAST normal(boolean nilIfUnevaluated) {
    IASTAppendable result = F.ast(S.Image3D);
    result.append(fData);
    result.append(F.stringx(sampleType));
    if (!options.colorSpace().isAutomatic()) {
      result.append(F.Rule(S.ColorSpace, options.colorSpace()));
    }
    return result;
  }

  @Override
  public IExpr copy() {
    return new Image3DExpr(fData, sampleType, options, slices, rows, columns, channels);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof Image3DExpr) {
      Image3DExpr other = (Image3DExpr) obj;
      return sampleType.equals(other.sampleType) && fData.equals(other.fData);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return 3769 + fData.hashCode();
  }

  @Override
  public String fullFormString() {
    return normal(false).fullFormString();
  }

  @Override
  public String toString() {
    return "Image3D(Dimensions: " + columns + "," + rows + "," + slices + " Channels: "
        + channels + ")";
  }
}
