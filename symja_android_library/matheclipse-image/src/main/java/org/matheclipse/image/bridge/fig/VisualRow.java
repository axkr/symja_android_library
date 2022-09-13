// code adapted from https://github.com/datahaki/bridge
package org.matheclipse.image.bridge.fig;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.io.Serializable;
import java.util.Objects;
import org.matheclipse.core.interfaces.IAST;

public class VisualRow implements Serializable {

  private static final long serialVersionUID = -842200759217788659L;

  private static final Stroke STROKE_DEFAULT = new BasicStroke(1f);
  // ---
  private final IAST points;
  private final ComparableLabel comparableLabel;
  private Color color = Color.BLUE;
  private boolean autoSort = false;
  /** not serializable */
  private transient Stroke stroke;

  /**
   * 
   * @param points of the form {{x1, y1}, {x2, y2}, ..., {xn, yn}}. The special case when points ==
   *        {} is also allowed.
   * @param index
   */
  /* package */ VisualRow(IAST points, int index) {
    // ScalarQ.thenThrow(points);
    this.points = points;
    this.comparableLabel = new ComparableLabel(index);
  }

  /** @return points of the form {{x1, y1}, {x2, y2}, ..., {xn, yn}} */
  public IAST points() {
    return points;
  }

  // ---
  /** @param color */
  public void setColor(Color color) {
    this.color = Objects.requireNonNull(color);
  }

  /** @return */
  public Color getColor() {
    return color;
  }

  // ---
  /** @param stroke */
  public void setStroke(Stroke stroke) {
    this.stroke = Objects.requireNonNull(stroke);
  }

  /** @return */
  public Stroke getStroke() {
    return Objects.isNull(stroke) //
        ? STROKE_DEFAULT
        : stroke;
  }

  // ---
  /** @param string */
  public void setLabel(String string) {
    comparableLabel.setString(string);
  }

  /** @return */
  public String getLabelString() {
    return getLabel().toString();
  }

  // ---
  /** @param autoSort */
  public void setAutoSort(boolean autoSort) {
    this.autoSort = autoSort;
  }

  /** @return */
  public boolean getAutoSort() {
    return autoSort;
  }

  // ---
  /* package */ ComparableLabel getLabel() {
    return comparableLabel;
  }

}
