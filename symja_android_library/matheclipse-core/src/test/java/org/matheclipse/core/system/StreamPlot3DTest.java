package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** StreamPlot3D draws streamlines of a field in space. */
public class StreamPlot3DTest extends ExprEvaluatorTestCase {

  @Test
  public void testStreamPlot3DLines() {
    // one seed in the middle of the box, the line runs across it
    check(
        "g = StreamPlot3D({1, 0, 0}, {x, -1, 1}, {y, -1, 1}, {z, -1, 1}, StreamPoints -> 1); "
            + "{Head(g), Cases(g, Line(p_, ___) :> Round({First(p), Last(p)}, 0.01), Infinity)}", //
        "{Graphics3D,{{{-0.99,0.0,0.0},{0.99,0.0,0.0}}}}");
    check(
        "Length(Cases(StreamPlot3D({y, -x, 1}, {x, -1, 1}, {y, -1, 1}, {z, 0, 2}, "
            + "StreamPoints -> 27), _Line, Infinity))", //
        "27");
    check(
        "FreeQ(StreamPlot3D({y, -x, 1}, {x, -1, 1}, {y, -1, 1}, {z, 0, 2}, StreamPoints -> 3, "
            + "StreamColorFunction -> None), VertexColors)", //
        "True");
  }

  @Test
  public void testStreamPlot3DSeedsAndColors() {
    // the corners of the 2 x 2 x 2 seed lattice lie on their lines
    check(
        "g = StreamPlot3D({z, 1, -x}, {x, 0, 1}, {y, 0, 1}, {z, 0, 1}, StreamPoints -> 8); "
            + "Min(Norm(# - {0.9, 0.1, 0.9}) & /@ Flatten(Cases(g, Line(p_, ___) :> p, Infinity), 1)) "
            + "< 10^-9", //
        "True");
    // the weakest point is the dark blue end of the gradient, the strongest the orange one
    check(
        "c = Flatten(Cases(StreamPlot3D({x, y, 1}, {x, -1, 1}, {y, -1, 1}, {z, 0, 1}, "
            + "StreamPoints -> 8), Line(_, VertexColors -> v_) :> v, Infinity)); "
            + "{MemberQ(c, RGBColor(r_, g_, b_) /; b > 0.6 && r < 0.1), "
            + "MemberQ(c, RGBColor(r_, g_, b_) /; r > 0.95 && b < 0.1)}", //
        "{True,True}");
  }
}
