package org.matheclipse.image.expression.data;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The options an <code>Image</code> object carries.
 *
 * <p>
 * Two of them say how the pixels were read and so belong to the picture itself: {@link
 * #colorSpace()} is the space the samples were <em>interpreted</em> in, and {@link #interleaved()}
 * is the layout the data was written in, which is the image's native form. The rest -
 * <code>ImageSize</code>, <code>ImageResolution</code>, <code>Magnification</code>,
 * <code>MetaInformation</code>, <code>AlignmentPoint</code>, <code>BaselinePosition</code> - say
 * nothing about the pixels and only affect how the image is displayed or what is carried alongside
 * it.
 *
 * <p>
 * Instances are immutable and shared; {@link #DEFAULT} is what an image built without options has.
 */
public final class ImageOptions {

  /** What an <code>Image</code> written without options carries. */
  public static final ImageOptions DEFAULT = new ImageOptions(S.Automatic, true, S.Automatic,
      S.Automatic, S.Automatic, F.assoc(), S.Center, S.Automatic);

  private final IExpr colorSpace;
  private final boolean interleaved;
  private final IExpr imageSize;
  private final IExpr imageResolution;
  private final IExpr magnification;
  private final IExpr metaInformation;
  private final IExpr alignmentPoint;
  private final IExpr baselinePosition;

  /**
   * @param colorSpace the <code>ColorSpace</code> the samples were read in, or
   *        <code>Automatic</code>
   * @param interleaved whether the data was written as <code>{row, column, channel}</code>
   */
  public ImageOptions(IExpr colorSpace, boolean interleaved, IExpr imageSize,
      IExpr imageResolution, IExpr magnification, IExpr metaInformation, IExpr alignmentPoint,
      IExpr baselinePosition) {
    this.colorSpace = colorSpace;
    this.interleaved = interleaved;
    this.imageSize = imageSize;
    this.imageResolution = imageResolution;
    this.magnification = magnification;
    this.metaInformation = metaInformation;
    this.alignmentPoint = alignmentPoint;
    this.baselinePosition = baselinePosition;
  }

  public IExpr colorSpace() {
    return colorSpace;
  }

  /**
   * The <code>ColorSpace</code> as {@link org.matheclipse.image.algo.Pixels} wants it: the name
   * written in the option, or <code>null</code> where the space is to be taken from the shape of
   * the data.
   */
  public String colorSpaceName() {
    if (colorSpace == null || colorSpace == S.Automatic) {
      return null;
    }
    String name = colorSpace.isString() ? colorSpace.toString() : null;
    return "Automatic".equalsIgnoreCase(name) ? null : name;
  }

  /**
   * Whether the image's own data is interleaved. This is what
   * <code>ImageData[image, Interleaving -&gt; Automatic]</code> reports, and it is <em>not</em>
   * what <code>ImageData[image]</code> gives back - that one is interleaved whatever the image
   * stores.
   */
  public boolean interleaved() {
    return interleaved;
  }

  public IExpr imageSize() {
    return imageSize;
  }

  public IExpr imageResolution() {
    return imageResolution;
  }

  public IExpr magnification() {
    return magnification;
  }

  public IExpr metaInformation() {
    return metaInformation;
  }

  public IExpr alignmentPoint() {
    return alignmentPoint;
  }

  public IExpr baselinePosition() {
    return baselinePosition;
  }

  public ImageOptions withColorSpace(IExpr newColorSpace) {
    return new ImageOptions(newColorSpace, interleaved, imageSize, imageResolution, magnification,
        metaInformation, alignmentPoint, baselinePosition);
  }

  public ImageOptions withInterleaved(boolean newInterleaved) {
    return new ImageOptions(colorSpace, newInterleaved, imageSize, imageResolution, magnification,
        metaInformation, alignmentPoint, baselinePosition);
  }

  public ImageOptions withImageSize(IExpr newImageSize) {
    return new ImageOptions(colorSpace, interleaved, newImageSize, imageResolution, magnification,
        metaInformation, alignmentPoint, baselinePosition);
  }

  // ------------------------------------------------------------------ display size

  /** The named <code>ImageSize</code> values, as widths in printer's points. */
  private static final double TINY = 40.0;
  private static final double SMALL = 180.0;
  private static final double MEDIUM = 360.0;
  private static final double LARGE = 560.0;

  /** A printer's point is a 72nd of an inch, which is the resolution nothing is scaled at. */
  private static final double POINTS_PER_INCH = 72.0;

  /**
   * The size the image is to be displayed at, as <code>{width, height}</code> in css pixels.
   *
   * <p>
   * The three options say different things about it. <code>ImageSize</code> gives the size
   * outright. <code>ImageResolution</code> gives it indirectly, as how many pixels are to occupy an
   * inch: an image at 72 is shown pixel for pixel and one at 144 is shown at half that.
   * <code>Magnification</code> scales whatever the other two came to, and on its own scales the
   * image's own size.
   *
   * @return <code>null</code> where none of the three asks for anything and the image is to be
   *         shown pixel for pixel
   */
  public double[] displaySize(int pixelWidth, int pixelHeight) {
    if (pixelWidth <= 0 || pixelHeight <= 0) {
      return null;
    }
    double scale = positive(magnification, 1.0);
    double[] size = requestedSize(pixelWidth, pixelHeight);
    if (size == null) {
      if (scale == 1.0) {
        return null;
      }
      size = new double[] {pixelWidth, pixelHeight};
    }
    return new double[] {size[0] * scale, size[1] * scale};
  }

  /** What <code>ImageSize</code> asks for, or what <code>ImageResolution</code> asks for instead. */
  private double[] requestedSize(int pixelWidth, int pixelHeight) {
    double aspect = pixelHeight / (double) pixelWidth;
    if (imageSize == S.All || imageSize == S.Full) {
      return new double[] {pixelWidth, pixelHeight};
    }
    if (imageSize.isList2()) {
      double width = positive(imageSize.first(), Double.NaN);
      double height = positive(imageSize.second(), Double.NaN);
      if (Double.isNaN(width) && Double.isNaN(height)) {
        return fromResolution(pixelWidth, pixelHeight);
      }
      // one of the two may be Automatic, and then the other one and the shape of the image decide
      if (Double.isNaN(width)) {
        width = height / aspect;
      } else if (Double.isNaN(height)) {
        height = width * aspect;
      }
      return new double[] {width, height};
    }
    double width = namedWidth(imageSize);
    if (Double.isNaN(width)) {
      width = positive(imageSize, Double.NaN);
    }
    if (Double.isNaN(width)) {
      return fromResolution(pixelWidth, pixelHeight);
    }
    return new double[] {width, width * aspect};
  }

  private double[] fromResolution(int pixelWidth, int pixelHeight) {
    double resolution = positive(imageResolution, Double.NaN);
    if (Double.isNaN(resolution)) {
      return null;
    }
    double factor = POINTS_PER_INCH / resolution;
    return new double[] {pixelWidth * factor, pixelHeight * factor};
  }

  private static double namedWidth(IExpr value) {
    if (value == S.Tiny) {
      return TINY;
    }
    if (value == S.Small) {
      return SMALL;
    }
    if (value == S.Medium) {
      return MEDIUM;
    }
    return value == S.Large ? LARGE : Double.NaN;
  }

  /**
   * The positive number <code>value</code> denotes. <code>Automatic</code> and anything else that
   * is not a positive number - a negative size says nothing - falls back.
   */
  private static double positive(IExpr value, double fallback) {
    if (value == null || value == S.Automatic) {
      return fallback;
    }
    double number = value.evalfNaN();
    return Double.isNaN(number) || number <= 0.0 ? fallback : number;
  }

  /**
   * The options as a list of rules, in the order <code>Options</code> reports them, which is
   * alphabetical by option name.
   */
  public IAST toRules() {
    return F.List(//
        F.Rule(S.AlignmentPoint, alignmentPoint), //
        F.Rule(S.BaselinePosition, baselinePosition), //
        F.Rule(S.ColorSpace, colorSpace), //
        F.Rule(S.ImageResolution, imageResolution), //
        F.Rule(S.ImageSize, imageSize), //
        F.Rule(S.Interleaving, F.booleSymbol(interleaved)), //
        F.Rule(S.Magnification, magnification), //
        F.Rule(S.MetaInformation, metaInformation));
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof ImageOptions)) {
      return false;
    }
    ImageOptions other = (ImageOptions) obj;
    return interleaved == other.interleaved //
        && colorSpace.equals(other.colorSpace) //
        && imageSize.equals(other.imageSize) //
        && imageResolution.equals(other.imageResolution) //
        && magnification.equals(other.magnification) //
        && metaInformation.equals(other.metaInformation) //
        && alignmentPoint.equals(other.alignmentPoint) //
        && baselinePosition.equals(other.baselinePosition);
  }

  @Override
  public int hashCode() {
    return toRules().hashCode();
  }

  @Override
  public String toString() {
    return toRules().toString();
  }
}
