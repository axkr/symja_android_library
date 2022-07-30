package org.mathclipse.core.tensor.opt.qh3;

import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.tensor.opt.qh3.ConvexHull3D;
import org.matheclipse.core.tensor.opt.qh3.Vector3d;

/**
 * Simple example usage of ConvexHull3D.
 * 
 */
class SimpleExampleTest {
  /** Run for a simple demonstration of ConvexHull3D. */
  @Test
  void test() {
    // x y z coordinates of 6 points
    IAST points = F.List( //
        F.List(0.0, 0.0, 0.0), //
        F.List(1.0, 0.5, 0.0), //
        F.List(2.0, 0.0, 0.0), //
        F.List(0.5, 0.5, 0.5), //
        F.List(0.0, 0.0, 2.0), //
        F.List(0.1, 0.2, 0.3), //
        F.List(0.0, 2.0, 0.0));
    try {
      ConvexHull3D hull = new ConvexHull3D();
      hull.build(points);
      System.out.println("Vertices:");
      Vector3d[] vertices = hull.getVertices();
      for (int i = 0; i < vertices.length; i++) {
        Vector3d pnt = vertices[i];
        System.out.println(pnt);
      }
      System.out.println("Faces:");
      int[][] faceIndices = hull.getFaces();
      for (int i = 0; i < vertices.length; i++) {
        for (int k = 0; k < faceIndices[i].length; k++) {
          System.out.print(faceIndices[i][k] + " ");
        }
        System.out.println("");
      }
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }
}
