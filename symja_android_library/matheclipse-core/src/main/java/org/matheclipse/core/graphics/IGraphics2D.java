package org.matheclipse.core.graphics;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface IGraphics2D {

  default boolean graphics2DDimension(IAST ast, Dimensions2D dim) {
    return false;
  }

  default boolean graphics2DSVG(StringBuilder buf, IAST ast, Dimensions2D dim, IAST color,
      IExpr opacity) {
    return false;
  }

  /**
   * 
   * @param json the JSON object tree
   * @param ast the graphics primitive (Circle, Disk, Line, Point,...)
   * @param color the RGB color which should be used
   * @param opacity the opacity (converted to a double number internally)
   * @param pointSize TODO
   * @param listOfCoords collect all coordinates for calculating a bounding box
   * @return
   */
  default boolean graphics2D(ObjectNode json, IAST ast, IAST color, IExpr opacity,
      double pointSize, IASTAppendable listOfCoords) {
    return false;
  }

}
