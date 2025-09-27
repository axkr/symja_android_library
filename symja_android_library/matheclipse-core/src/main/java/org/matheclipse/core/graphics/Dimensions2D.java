package org.matheclipse.core.graphics;

import org.matheclipse.core.convert.RGBColor;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IReal;

public class Dimensions2D {

  private RGBColor color;
  private int width;
  private int height;

  private double xMin;
  private double xMax;

  private double yMin;

  private double yMax;

  private boolean plotRange;

  private boolean axes;
  public Dimensions2D() {
    this(600, 400);
  }
  public Dimensions2D(int width, int height) {
    this.color = RGBColor.BLACK;
    this.width = width;
    this.height = height;
    this.xMin = Double.MAX_VALUE;
    this.xMax = -Double.MAX_VALUE;
    this.yMin = Double.MAX_VALUE;
    this.yMax = -Double.MAX_VALUE;
    this.plotRange = false;
    this.axes = false;
  }

  public void getColorRGB(StringBuilder buf) {
    float[] rgb = color.getRGBColorComponents(null);
    buf.append(Float.toString(rgb[0] * 100));
    buf.append("%, ");
    buf.append(Float.toString(rgb[1] * 100));
    buf.append("%, ");
    buf.append(Float.toString(rgb[2] * 100));
    buf.append("%");
  }

  public double getXMax() {
    return xMax;
  }

  public double getXMin() {
    return xMin;
  }

  public double getXScale() {
    double diff = xMax - xMin;
    if (F.isZero(diff)) {
      return 0.0;
    }
    return width / diff;
  }

  public double getYMax() {
    return yMax;
  }

  public double getYMin() {
    return yMin;
  }

  public double getYScale() {
    double diff = yMax - yMin;
    if (F.isZero(diff)) {
      return 0.0;
    }
    return height / (yMax - yMin);
  }

  public boolean isAxes() {
    return axes;
  }

  public boolean isValidRange() {
    return xMin != Double.MAX_VALUE && xMax != -Double.MAX_VALUE && yMin != Double.MAX_VALUE
        && yMax != -Double.MAX_VALUE;
  }

  public void minMax(double xmin, double xmax, double ymin, double ymax) {
    if (!plotRange) {
      if (xmin < xMin) {
        xMin = xmin;
      }
      if (xmax > xMax) {
        xMax = xmax;
      }
      if (ymin < yMin) {
        yMin = ymin;
      }
      if (ymax > yMax) {
        yMax = ymax;
      }
    }
  }

  public void setAxes(boolean axes) {
    this.axes = axes;
  }

  public void setColorRGB(IAST rgbColor) {
    if (rgbColor.size() == 4 || rgbColor.size() == 5) {
      float r = (float) rgbColor.arg1().evalf();
      float g = (float) rgbColor.arg2().evalf();
      float b = (float) rgbColor.arg3().evalf();
      color = new RGBColor(r, g, b);
    }
  }

  public void setPlotRange(IAST p1, IAST p2) {
    double x1 = ((IReal) p1.arg1()).doubleValue();
    double y1 = ((IReal) p1.arg2()).doubleValue();
    double x2 = ((IReal) p2.arg1()).doubleValue();
    double y2 = ((IReal) p2.arg2()).doubleValue();
    minMax(x1, y1, x2, y2);
    plotRange = true;
  }
}
