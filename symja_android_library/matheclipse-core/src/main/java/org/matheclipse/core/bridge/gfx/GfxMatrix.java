// code adapted from https://github.com/datahaki/bridge
package org.matheclipse.core.bridge.gfx;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Hint: this class is identical to Se2Matrix in the library ch.alpine.sophus
 */
public class GfxMatrix {

  /**
   * maps a vector from the group SE2 to a matrix in SE2
   * 
   * @param g = {px, py, angle}
   * @return matrix with dimensions 3x3
   * 
   *         <pre>
   * [+Ca -Sa px]
   * [+Sa +Ca py]
   * [0 0 1]
   *         </pre>
   * 
   * @throws Exception if parameter g is not a vector of length 3
   */
  public static IAST of(IAST xya) {
    IExpr angle = xya.arg3();
    IExpr cos = S.Cos.of(angle);
    IExpr sin = S.Sin.of(angle);
    return F.List(F.List(cos, sin.negate(), xya.arg1()), //
        F.List(sin, cos, xya.arg2()), //
        F.List(F.C0, F.C0, F.C1) //
    );

  }

  /**
   * maps a matrix from the group SE2 to a vector in the group SE2
   * 
   * @param matrix
   * @return {px, py, angle}
   */
  public static IAST toVector(IAST matrix) {
    // SquareMatrixQ.require(matrix);
    return F.List(matrix.getPart(1, 3), matrix.getPart(2, 3), //
        S.ArcTan.of(matrix.getPart(1, 1), matrix.getPart(2, 1))); // arc tan is numerically stable
  }

  /**
   * @param vector of the form {px, py, ...}
   * @return
   * 
   *         <pre>
   * [1 0 px]
   * [0 1 py]
   * [0 0 1]
   *         </pre>
   */
  public static IAST translation(IAST xy) {
    return F.List(//
        F.List(F.C1, F.C0, xy.arg1()), //
        F.List(F.C0, F.C1, xy.arg2()), //
        F.List(F.C0, F.C0, F.C1) //
    );
  }

  public static IAST translation(Number x, Number y) {
    return translation(F.List(x.doubleValue(), y.doubleValue()));
  }

  /**
   * Hint: function is useful to construct a pixel2model matrix for an image of given height.
   * 
   * @param height
   * @return matrix with determinant -1
   * 
   *         <pre>
   * [1 0 0]
   * [0 -1 height]
   * [0 0 1]
   *         </pre>
   */
  public static IAST flipY(int height) {
    return F.List(//
        F.List(F.C1, F.C0, F.C0), //
        F.List(F.C0, F.CN1, F.ZZ(height)), //
        F.List(F.C0, F.C0, F.C1) //
    );
  }
}
