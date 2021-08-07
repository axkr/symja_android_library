package org.matheclipse.core.graphics;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Interface for 3D graphics primitives like Cubiod, Sphere, Cylinder,... which create a threejs
 * JSON String which is used in calling the <a
 * href="https://github.com/Mathics3/mathics-threejs-backend/wiki">mathics-threejs-backend
 * library</a>.
 */
public interface IGraphics3D {

  /**
   * Interface for 3D graphics primitives like Cubiod, Sphere, Cylinder,... which create a threejs
   * JSON String which is used in calling the <a
   * href="https://github.com/Mathics3/mathics-threejs-backend/wiki">mathics-threejs-backend
   * library</a>.
   *
   * @param buf
   * @param ast the graphics primitive (Cubiod, Sphere, Cylinder,...)
   * @param color the RGB color which should be used
   * @param opacity the opecity (converted to a double number internally)
   * @return
   */
  boolean graphics3D(StringBuilder buf, IAST ast, IAST color, IExpr opacity);
}
