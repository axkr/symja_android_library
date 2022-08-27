// code adapted from https://github.com/datahaki/bridge
package org.matheclipse.core.bridge.fig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.tensor.img.ColorDataIndexed;
import org.matheclipse.core.tensor.img.ColorDataLists;
import org.matheclipse.core.tensor.qty.QuantityUnit;

public class VisualSet extends VisualBase {
  private final List<VisualRow> visualRows = new ArrayList<>();
  private final ColorDataIndexed colorDataIndexed;

  public VisualSet(ColorDataIndexed colorDataIndexed) {
    this.colorDataIndexed = Objects.requireNonNull(colorDataIndexed);
  }

  public VisualSet() {
    this(ColorDataLists._097.cyclic());
  }

  /**
   * @param points of the form {{x1, y1}, {x2, y2}, ..., {xn, yn}}. The special case when points ==
   *        {} is also allowed.
   * @return instance of the visual row, that was added to this visual set
   * @throws Exception if not all entries in points are vectors of length 2
   */
  public VisualRow add(IAST points) {
    if (!points.isEmpty()) {
      if (!getAxisX().hasUnit()) {
        getAxisX().setUnit(QuantityUnit.of(points.getPart(1, 1)));
      }
      if (!getAxisY().hasUnit()) {
        getAxisY().setUnit(QuantityUnit.of(points.getPart(1, 2)));
      }
    }
    final int index = visualRows.size();
    // points.stream().forEach(row -> VectorQ.requireLength(row, 2));
    VisualRow visualRow = new VisualRow(points, index);
    visualRow.setColor(colorDataIndexed.getColor(index));
    visualRows.add(visualRow);
    return visualRow;
  }

  /**
   * @param domain {x1, x2, ..., xn}
   * @param values {y1, y2, ..., yn}
   * @return
   */
  public VisualRow add(IAST domain, IAST values) {
    return add((IAST) S.Transpose.of(F.list(domain, values)));
  }

  public List<VisualRow> visualRows() {
    return Collections.unmodifiableList(visualRows);
  }

  public VisualRow getVisualRow(int index) {
    return visualRows.get(index);
  }

  public boolean hasLegend() {
    return visualRows.stream() //
        .map(VisualRow::getLabelString) //
        .anyMatch(string -> !string.isEmpty());
  }
}
