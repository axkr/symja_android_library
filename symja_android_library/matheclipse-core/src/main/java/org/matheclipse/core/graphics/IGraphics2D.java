package org.matheclipse.core.graphics;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface IGraphics2D {

  default boolean graphics2DDimension(IAST ast, Dimensions2D dim) {
    return false;
  }

  /**
   * 
   * @param json the JSON array node
   * @param ast the graphics primitive (Circle, Disk, Line, Point,...)
   * @param options TODO
   * @return
   */
  default boolean graphics2D(ArrayNode json, IAST ast, GraphicsOptions options) {
    return false;
  }

  default boolean graphicsComplex2D(ArrayNode json, IAST ast, IAST listOfIntPositions,
      GraphicsOptions options) {
    return false;
  }

  default boolean graphicsComplex2DPositions(ArrayNode arrayNode, ObjectNode g, IExpr expr,
      IAST listOfIntPositions, GraphicsOptions options) {
    IAST list = expr.makeList();
    if (graphicsComplex2DPositions(g, list, listOfIntPositions, options)) {
      arrayNode.add(g);
      return true;
    }
    return false;
  }

  default boolean graphicsComplex2DPositions(ObjectNode json, IAST ast, IAST listOfIntPositions,
      GraphicsOptions options) {
    ArrayNode array = json.arrayNode();
    for (int i = 1; i < ast.size(); i++) {
      IExpr arg = ast.get(i);
      if (arg.isList()) {
        ObjectNode g = json.objectNode();
        if (!graphicsComplex2DPositions(g, (IAST) arg, listOfIntPositions, options)) {
          return false;
        }
        array.add(g);
      } else if (arg.isInteger()) {
        int iValue = arg.toIntDefault();
        if (iValue <= 0) {
          return false;
        }
        array.add(iValue);
      }
    }
    json.set("positions", array);
    return true;
  }

}
