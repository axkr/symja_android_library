// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.opt.qh3;

import org.matheclipse.core.interfaces.IExpr;

/**
 * Represents vertices of the hull, as well as the points from which it is formed.
 *
 * @author John E. Lloyd, Fall 2004
 */
class Vertex {
  /** Spatial point associated with this vertex. */
  Vector3d pnt;
  /** Back index into an array. */
  int index;
  /** List forward link. */
  Vertex prev;
  /** List backward link. */
  Vertex next;
  /** Current face that this vertex is outside of. */
  Face face;

  /** Constructs a vertex and sets its coordinates to 0. */
  public Vertex() {
    pnt = new Vector3d();
  }

  /**
   * Constructs a vertex with the specified coordinates and index.
   */
  public Vertex(IExpr x, IExpr y, IExpr z, int idx) {
    pnt = new Vector3d(x, y, z);
    index = idx;
  }
}
