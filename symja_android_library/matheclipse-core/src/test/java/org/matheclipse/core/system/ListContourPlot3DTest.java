package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** ListContourPlot3D draws level surfaces of a three dimensional array. */
public class ListContourPlot3DTest extends ExprEvaluatorTestCase {

  @Test
  public void testListContourPlot3DLevels() {
    // array[[i, j, k]] sits at {k - 1, j - 1, i - 1}: i + 2 j == 5 is z + 2 y == 2
    check(
        "g = ListContourPlot3D(Table(i + 2*j, {i, 3}, {j, 3}, {k, 2}), Contours -> {5}); "
            + "{Head(g), Union(Flatten(Cases(g, GraphicsComplex(p_, ___) :> "
            + "Round(p[[All, 3]] + 2*p[[All, 2]], 0.001), Infinity)))}", //
        "{Graphics3D,{2.0}}");
    // DataRange spreads the samples over the box
    check(
        "g = ListContourPlot3D(Table(k, {i, 2}, {j, 2}, {k, 5}), Contours -> {2, 4}, "
            + "DataRange -> {{0, 8}, {0, 1}, {0, 1}}); "
            + "Cases(g, GraphicsComplex(p_, ___) :> Union(Round(p[[All, 1]], 0.001)), Infinity)", //
        "{{2.0},{6.0}}");
    // no three dimensional array
    check("ListContourPlot3D({1, 2, 3})", //
        "ListContourPlot3D({1,2,3})");
  }
}
