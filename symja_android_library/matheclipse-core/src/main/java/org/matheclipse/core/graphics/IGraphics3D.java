package org.matheclipse.core.graphics;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Interface for 3D graphics primitives like Cuboid, Sphere, Cylinder,... which create a threejs
 * JSON String which is used in calling the
 * <a href="https://github.com/Mathics3/mathics-threejs-backend/wiki">mathics-threejs-backend
 * library</a>.
 */
public interface IGraphics3D {

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
