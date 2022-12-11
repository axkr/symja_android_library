package org.matheclipse.core.graphics;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import com.fasterxml.jackson.databind.node.ArrayNode;

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
   * @param json the JSON array node
   * @param ast the graphics primitive (Circle, Disk, Line, Point,...)
   * @param listOfCoords collect all coordinates for calculating a bounding box
   * @param options TODO
   * @return
   */
  default boolean graphics2D(ArrayNode json, IAST ast, IASTAppendable listOfCoords,
      GraphicsOptions options) {
    return false;
  }

}
