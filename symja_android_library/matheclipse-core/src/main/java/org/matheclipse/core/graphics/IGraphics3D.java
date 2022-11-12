package org.matheclipse.core.graphics;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Interface for 3D graphics primitives like Cubiod, Sphere, Cylinder,... which create a threejs
 * JSON String which is used in calling the
 * <a href="https://github.com/Mathics3/mathics-threejs-backend/wiki">mathics-threejs-backend
 * library</a>.
 */
public interface IGraphics3D {

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
   * @return
   */
  default boolean graphics2D(ObjectNode json, IAST ast, IAST color, IExpr opacity) {
    return false;
  }

  /**
   * Interface for 3D graphics primitives like Cuboid, Sphere, Cylinder,... which create a threejs
   * JSON String which is used in calling the
   * <a href="https://github.com/Mathics3/mathics-threejs-backend/wiki">mathics-threejs-backend
   * library</a>.
   *
   * @param json the JSON object tree
   * @param ast the graphics primitive (Cuboid, Sphere, Cylinder,...)
   * @param color the RGB color which should be used
   * @param opacity the opacity (converted to a double number internally)
   * @return
   */
  default boolean graphics3D(ObjectNode json, IAST ast, IAST color, IExpr opacity) {
    return false;
  }
}
