// code adapted from https://github.com/datahaki/bridge
package org.matheclipse.core.bridge.gfx;

import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayDeque;
import java.util.Deque;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.ISignedNumber;

/**
 * GeometricLayer transforms from model to pixel coordinates
 * 
 * see RenderInterface
 */
public class GeometricLayer {
  private final Deque<AffineFrame2D> deque = new ArrayDeque<>();

  /**
   * @param model2pixel matrix of dimension 3x3 that becomes first element on matrix stack
   * @param mouseSe2CState typically a vector of length 3
   */
  public GeometricLayer(IAST model2pixel) {
    deque.push(new AffineFrame2D(model2pixel.copy()));
  }

  /**
   * only the first 2 entries of x are taken into account
   * 
   * @param vector of the form {px, py, ...}
   * @return
   */
  public Point2D toPoint2D(IAST vector) {
    return deque.peek().toPoint2D( //
        vector.arg1().evalDouble(), //
        vector.arg2().evalDouble());
  }

  /**
   * @param px
   * @param py
   * @return
   */
  public Point2D toPoint2D(double px, double py) {
    return deque.peek().toPoint2D(px, py);
  }

  /**
   * @param vector of the form {px, py, ...}
   * @return vector of length 2
   */
  public IAST toVector(IAST vector) {
    return deque.peek().toVector( //
        vector.arg1().evalDouble(), //
        vector.arg2().evalDouble());
  }

  /**
   * @param px
   * @param py
   * @return vector of length 2
   */
  public IAST toVector(double px, double py) {
    return deque.peek().toVector(px, py);
  }

  /**
   * inspired by opengl
   * 
   * @param matrix 3x3
   */
  public void pushMatrix(IAST matrix) {
    deque.push(deque.peek().dot(matrix));
  }

  /**
   * inspired by opengl
   * 
   * @throws Exception without a corresponding call to {@link #pushMatrix(IAST)}
   */
  public void popMatrix() {
    if (deque.size() == 1)
      throw new IllegalStateException();
    deque.pop();
  }

  /** @return current model2pixel matrix */
  public IAST getMatrix() {
    return deque.peek().matrix_copy();
  }

  /**
   * @param p
   * @param q
   * @return line that connects p and q
   */
  public Line2D toLine2D(IAST p, IAST q) {
    return new Line2D.Double( //
        toPoint2D(p), //
        toPoint2D(q));
  }

  /**
   * @param p
   * @return line that connects the origin with p
   */
  public Line2D toLine2D(IAST p) {
    return new Line2D.Double( //
        deque.peek().toPoint2D(), //
        toPoint2D(p));
  }

  /**
   * @param polygon
   * @return path that is not closed
   */
  public Path2D toPath2D(IAST polygon) {
    Path2D path2d = new Path2D.Double();
    if (!polygon.isEmpty()) {
      Point2D point2d = toPoint2D((IAST) polygon.arg1());
      path2d.moveTo(point2d.getX(), point2d.getY());
    }
    polygon.stream() //
        .skip(1) // first coordinate already used in moveTo
        .map(x -> toPoint2D((IAST) x)) //
        .forEach(point2d -> path2d.lineTo(point2d.getX(), point2d.getY()));
    return path2d;
  }

  /**
   * @param polygon
   * @param close
   * @return path that is closed if given parameter close is true
   */
  public Path2D toPath2D(IAST polygon, boolean close) {
    Path2D path2d = toPath2D(polygon);
    if (close)
      path2d.closePath();
    return path2d;
  }

  /**
   * function allows to render lines with width defined in model coordinates
   * 
   * <pre>
   * new BasicStroke(geometricLayer.model2pixelWidth(0.1))
   * </pre>
   * 
   * @param modelWidth
   * @return non-negative value
   */
  public float model2pixelWidth(ISignedNumber modelWidth) {
    return (float) (Math.sqrt(Math.abs(deque.peek().det())) * modelWidth.evalDouble());
  }

  public double pixel2modelWidth(double pixelWidth) {
    return pixelWidth / Math.sqrt(Math.abs(deque.peek().det()));
  }
}
