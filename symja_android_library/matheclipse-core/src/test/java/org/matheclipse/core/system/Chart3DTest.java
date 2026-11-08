package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** The three dimensional charts BarChart3D and BubbleChart3D. */
public class Chart3DTest extends ExprEvaluatorTestCase {

  @Test
  public void testBarChart3DBlocks() {
    // one GraphicsComplex of six shaded faces for every value
    check(
        "g = BarChart3D({2, 5, 1}); {Length(Cases(g, _GraphicsComplex, Infinity)), "
            + "Length(Cases(g, Polygon(_, VertexColors -> _), Infinity))}", //
        "{3,18}");
    // a flat list is drawn in the first chart colour, several datasets by position
    check(
        "Cases(BarChart3D({{1, 2}, {3, 4}}), Polygon({4, 3, 2, 1}, VertexColors -> {c_, ___}) :> c, "
            + "Infinity)", //
        "{RGBColor(0.95,0.7125,0.0),RGBColor(0.0,0.595,0.85),RGBColor(0.95,0.7125,0.0),"
            + "RGBColor(0.0,0.595,0.85)}");
    check(
        "Cases(BarChart3D({1, 2}, ChartStyle -> {Red, Blue}), "
            + "Polygon({4, 3, 2, 1}, VertexColors -> {c_, ___}) :> c, Infinity)", //
        "{RGBColor(1.0,0.0,0.0),RGBColor(0.0,0.0,1.0)}");
    // a Labeled value is named at its bar
    check("Cases(BarChart3D({7, Labeled(3, \"b\")}), Text(t_, ___) :> t, Infinity)", //
        "{b}");
  }

  @Test
  public void testBarChart3DStacked() {
    // positive values pile up, negative ones down; the second column starts after a group gap
    check(
        "Cases(BarChart3D({{2, -1, 3}, {4}}, ChartLayout -> \"Stacked\"), "
            + "GraphicsComplex(p_, ___) :> {Min(p[[All, 1]]), Min(p[[All, 3]]), Max(p[[All, 3]])}, "
            + "Infinity)", //
        "{{0.0,0.0,2.0},{0.0,-1.0,0.0},{0.0,2.0,5.0},{1.2,0.0,4.0}}");
  }

  @Test
  public void testBubbleChart3D() {
    // the largest sphere is a fifth of the smallest extent, the volume follows the size
    check("Cases(BubbleChart3D({{0, 0, 0, 27}, {2, 4, 3, 1}}), Sphere(_, r_) :> r, Infinity)", //
        "{0.4,0.133333}");
    check("Head(BubbleChart3D({{0, 0, 0, 27}, {2, 4, 3, 1}}))", //
        "Graphics3D");
    // every dataset in a colour of its own
    check(
        "Cases(BubbleChart3D({{{0, 0, 0, 1}, {1, 1, 1, 1}}, {{2, 2, 2, 1}}}), _RGBColor, Infinity)", //
        "{RGBColor(0.95,0.7125,0.0),RGBColor(0.0,0.595,0.85)}");
  }
}
