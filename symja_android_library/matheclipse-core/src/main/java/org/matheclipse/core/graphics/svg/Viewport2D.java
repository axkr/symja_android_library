package org.matheclipse.core.graphics.svg;

import java.util.function.DoubleUnaryOperator;
import org.matheclipse.core.graphics.GraphicsOptions;

/**
 * The mapping from data coordinates to pixels.
 *
 * <p>
 * The drawing area is the image minus its padding. When the requested aspect ratio does not fill
 * that area, the drawing is centred in it rather than pinned to a corner.
 */
public final class Viewport2D {

  static final double LOG_MIN_CLAMP = 1e-10;

  private final GraphicsOptions2D options;

  /** Plot range in scaled (post log) coordinates. */
  public double minX;
  public double maxX;
  public double minY;
  public double maxY;

  /** Plot range in raw data coordinates, used for logarithmic tick placement. */
  public double rawMinX;
  public double rawMaxX;
  public double rawMinY;
  public double rawMaxY;

  public double scaleX;
  public double scaleY;

  public double paddingLeft;
  public double paddingRight;
  public double paddingTop;
  public double paddingBottom;

  /** Pixel bounds of the drawing area. */
  public double plotX1;
  public double plotX2;
  public double plotY1;
  public double plotY2;

  private final DoubleUnaryOperator scaleFnX;
  private final DoubleUnaryOperator scaleFnY;

  public Viewport2D(GraphicsOptions2D options) {
    this.options = options;
    this.scaleFnX = GraphicsOptions.getScalingFunction(options.scalingX);
    this.scaleFnY = GraphicsOptions.getScalingFunction(options.scalingY);
  }

  public boolean isLogX() {
    return isLog(options.scalingX);
  }

  public boolean isLogY() {
    return isLog(options.scalingY);
  }

  static boolean isLog(String scale) {
    return scale != null && (scale.equalsIgnoreCase("Log") || scale.equalsIgnoreCase("Log10"));
  }

  /**
   * Establish the plot range from the data bounds and the options, then compute the pixel scale.
   *
   * @param data the bounding box of the collected primitives
   * @param padding pixel padding, in the order left, right, bottom, top
   */
  public void configure(Bounds2D data, double[] padding) {
    paddingLeft = padding[0];
    paddingRight = padding[1];
    paddingBottom = padding[2];
    paddingTop = padding[3];

    double dMinX = data.isEmpty() ? (isLogX() ? 0.1 : 0.0) : data.xMin;
    double dMaxX = data.isEmpty() ? (isLogX() ? 10.0 : 1.0) : data.xMax;
    double dMinY = data.isEmpty() ? (isLogY() ? 0.1 : 0.0) : data.yMin;
    double dMaxY = data.isEmpty() ? (isLogY() ? 10.0 : 1.0) : data.yMax;

    if (options.plotRange != null) {
      if (!Double.isNaN(options.plotRange[0][0])) {
        dMinX = options.plotRange[0][0];
      }
      if (!Double.isNaN(options.plotRange[0][1])) {
        dMaxX = options.plotRange[0][1];
      }
      if (!Double.isNaN(options.plotRange[1][0])) {
        dMinY = options.plotRange[1][0];
      }
      if (!Double.isNaN(options.plotRange[1][1])) {
        dMaxY = options.plotRange[1][1];
      }
    }

    // A logarithmic axis starts at the smallest positive value in the data. Falling back to a fixed
    // floor instead put the bottom of the axis ten
    // decades below the data, so a plot whose values ran from 1 to 20 was drawn in the top
    // sliver of an eleven decade axis and its ticks came out as 0.01 and 1.
    if (isLogX() && dMinX <= 0) {
      dMinX = data.hasPositiveX() ? data.xMinPositive
          : (dMaxX > 0 ? Math.max(LOG_MIN_CLAMP, dMaxX / 100.0) : LOG_MIN_CLAMP);
    }
    if (isLogY() && dMinY <= 0) {
      dMinY = data.hasPositiveY() ? data.yMinPositive
          : (dMaxY > 0 ? Math.max(LOG_MIN_CLAMP, dMaxY / 100.0) : LOG_MIN_CLAMP);
    }

    // a zero width range would divide by zero; open it up around its value
    if (Math.abs(dMaxX - dMinX) < 1e-15) {
      if (isLogX()) {
        dMinX = dMinX / 2.0;
        dMaxX = dMaxX * 2.0;
      } else {
        double centre = dMinX;
        dMinX = centre - 0.5;
        dMaxX = centre + 0.5;
      }
    }
    if (Math.abs(dMaxY - dMinY) < 1e-15) {
      if (isLogY()) {
        dMinY = dMinY / 2.0;
        dMaxY = dMaxY * 2.0;
      } else {
        double centre = dMinY;
        dMinY = centre - 0.5;
        dMaxY = centre + 0.5;
      }
    }

    rawMinX = dMinX;
    rawMaxX = dMaxX;
    rawMinY = dMinY;
    rawMaxY = dMaxY;

    minX = scaleFnX.applyAsDouble(dMinX);
    maxX = scaleFnX.applyAsDouble(dMaxX);
    minY = scaleFnY.applyAsDouble(dMinY);
    maxY = scaleFnY.applyAsDouble(dMaxY);

    double padX = options.plotRangePaddingX.resolve(maxX - minX);
    double padY = options.plotRangePaddingY.resolve(maxY - minY);
    minX -= padX;
    maxX += padX;
    minY -= padY;
    maxY += padY;

    double rangeX = maxX - minX;
    double rangeY = maxY - minY;
    if (rangeX <= 0) {
      rangeX = 1;
      maxX = minX + 1;
    }
    if (rangeY <= 0) {
      rangeY = 1;
      maxY = minY + 1;
    }

    double drawWidth = Math.max(1.0, options.imageSize[0] - paddingLeft - paddingRight);
    double drawHeight = Math.max(1.0, options.imageSize[1] - paddingTop - paddingBottom);

    double targetRatio;
    if (options.aspectRatioAutomatic || Double.isNaN(options.aspectRatio)) {
      // Automatic keeps one data unit the same length on both axes
      targetRatio = rangeY / rangeX;
    } else {
      targetRatio = options.aspectRatio;
    }

    double effectiveW = drawWidth;
    double effectiveH = drawHeight;
    double screenRatio = drawHeight / drawWidth;
    if (targetRatio > screenRatio) {
      effectiveH = drawHeight;
      effectiveW = effectiveH / targetRatio;
    } else {
      effectiveW = drawWidth;
      effectiveH = effectiveW * targetRatio;
    }
    if (!Double.isFinite(effectiveW) || effectiveW <= 0) {
      effectiveW = drawWidth;
    }
    if (!Double.isFinite(effectiveH) || effectiveH <= 0) {
      effectiveH = drawHeight;
    }

    scaleX = effectiveW / rangeX;
    scaleY = effectiveH / rangeY;

    // centre the drawing inside the available area
    double slackX = (drawWidth - effectiveW) / 2.0;
    double slackY = (drawHeight - effectiveH) / 2.0;
    plotX1 = paddingLeft + slackX;
    plotX2 = plotX1 + effectiveW;
    plotY2 = options.imageSize[1] - paddingBottom - slackY;
    plotY1 = plotY2 - effectiveH;
  }

  /** Pixel x of a data coordinate. */
  public double mapX(double x) {
    double v = x;
    if (isLogX() && v <= 0) {
      v = LOG_MIN_CLAMP;
    }
    return plotX1 + (scaleFnX.applyAsDouble(v) - minX) * scaleX;
  }

  /** Pixel y of a data coordinate; the axis points up, so the mapping is inverted. */
  public double mapY(double y) {
    double v = y;
    if (isLogY() && v <= 0) {
      v = LOG_MIN_CLAMP;
    }
    return plotY2 - (scaleFnY.applyAsDouble(v) - minY) * scaleY;
  }

  /** Pixel x of an already scaled coordinate. */
  public double mapScaledX(double value) {
    return plotX1 + (value - minX) * scaleX;
  }

  /** Pixel y of an already scaled coordinate. */
  public double mapScaledY(double value) {
    return plotY2 - (value - minY) * scaleY;
  }

  /** Length in pixels of a horizontal data span, for shapes that are not log scaled. */
  public double lengthX(double dx) {
    return dx * scaleX;
  }

  public double lengthY(double dy) {
    return dy * scaleY;
  }
}
