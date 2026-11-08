package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** Plots and colours. */
public class VectorPlotTest extends ExprEvaluatorTestCase {

  @Test
  public void testPieChartSectorOriginAngle() {
    // a bare angle runs the sectors counterclockwise from it
    check("Cases(PieChart({2, 2}, SectorOrigin -> 0), Disk(_, _, a_) :> Round(a, 0.01), Infinity)", //
        "{{0.0,3.14},{3.14,6.28}}");
    // a named direction and the default both run clockwise from the left
    check(
        "Cases(PieChart({1, 3}, SectorOrigin -> {Pi, \"Clockwise\"}), "
            + "Disk(_, _, a_) :> Round(a, 0.01), Infinity)", //
        "{{1.57,3.14},{-3.14,1.57}}");
    check("Cases(PieChart({1, 3}), Disk(_, _, a_) :> Round(a, 0.01), Infinity)", //
        "{{1.57,3.14},{-3.14,1.57}}");
  }

  @Test
  public void testVectorPlotLattice() {
    // 19 rows of 16 and 15 points; no point lies on a zero of the field
    check("Length(Cases(VectorPlot({y, x}, {x, 0, 3}, {y, 0, 3}), _Arrow, Infinity))", //
        "295");
    // 5 rows of 4 and 3 points
    check(
        "Length(Cases(VectorPlot({1, 2}, {x, 0, 1}, {y, 0, 1}, VectorPoints -> 4), _Arrow, "
            + "Infinity))", //
        "18");
    check(
        "Length(Cases(VectorPlot({1, 1}, {x, 0, 6}, {y, -3, 3}, VectorPoints -> Fine), _Arrow, "
            + "Infinity))", //
        "711");
  }

  @Test
  public void testVectorPlotArrowLengths() {
    // every arrow is 0.9/16 of the side
    check("Union(Round(Norm(#[[2]] - #[[1]]) & /@ "
        + "Cases(VectorPlot({y, x}, {x, 0, 3}, {y, 0, 3}), Arrow(p_) :> p, Infinity), 0.00001))", //
        "{0.16875}");
    // VectorScaling -> Automatic: from a third of that length to all of it
    check(
        "MinMax(Round(Norm(#[[2]] - #[[1]]) & /@ Cases(VectorPlot({y, 0}, {x, 0, 2}, {y, 0, 2}, "
            + "VectorScaling -> Automatic), Arrow(p_) :> p, Infinity), 0.0001))", //
        "{0.0375,0.1125}");
  }

  @Test
  public void testVectorPlotColors() {
    // the gradient from the weakest to the strongest field, blended in LAB
    check(
        "Union(Cases(VectorPlot({1 + y, 0}, {x, 0, 1}, {y, 0, 1}, VectorPoints -> 3), "
            + "{c_, _Arrow} :> c, Infinity))", //
        "{LABColor(0.188668,0.522596,-0.857211),LABColor(0.402329,0.522215,-0.346571),"
            + "LABColor(0.644456,0.537992,0.732112),LABColor(0.816797,0.145001,0.828253)}");
    // halfway between the second and the third stop
    check(
        "Cases(VectorPlot({1 + y, 0}, {x, 0, 1}, {y, 0, 1}, VectorPoints -> {1, 3}), "
            + "{c_, _Arrow} :> c, Infinity)[[2]]", //
        "LABColor(0.523393,0.530103,0.19277)");
  }

  @Test
  public void testListVectorPlotPointsOnALine() {
    // no area to interpolate the field over
    check(
        "Length(Cases(ListVectorPlot({{{0, 0}, {1, 1}}, {{2, 2}, {1, 0}}, {{3, 3}, {0, 1}}}), "
            + "_Arrow, Infinity))", //
        "0");
  }

  @Test
  public void testListVectorPlotResamplesPairs() {
    // the field interpolated linearly on the lattice of VectorPlot over the points' bounding box
    check(
        "g = ListVectorPlot({{{0, 0}, {0, 1}}, {{3, 0}, {1, 1}}, {{0, 3}, {1, 0}}}); "
            + "{Length(Cases(g, _Arrow, Infinity)), PlotRange /. Rest(List @@ g)}", //
        "{295,{{0.0,3.0},{0.0,3.0}}}");
    // a field which is linear in the plane is reproduced: here {1, 2} everywhere
    check("Union(Round(Cases(ListVectorPlot({{{0, 0}, {1, 2}}, {{1, 0}, {1, 2}}, {{0, 1}, {1, 2}}, "
        + "{{1, 1}, {1, 2}}}, VectorPoints -> 3), Arrow({a_, b_}) :> (b - a)/Norm(b - a), Infinity), "
        + "0.001))", //
        "{{0.447,0.894}}");
  }

  @Test
  public void testDiscretePlotExtentSize() {
    check("Cases(DiscretePlot(k^2, {k, 0, 4, 2}, ExtentSize -> Left), _Rectangle, Infinity)", //
        "{Rectangle({-2.0,0},{0.0,0.0}),Rectangle({0.0,0},{2.0,4.0}),Rectangle({2.0,0},{4.0,16.0})}");
    check("Cases(DiscretePlot(k, {k, 1, 2}, ExtentSize -> Right), _Rectangle, Infinity)", //
        "{Rectangle({1.0,0},{2.0,1.0}),Rectangle({2.0,0},{3.0,2.0})}");
    check("Cases(DiscretePlot(k, {k, 1, 2}, ExtentSize -> 0.5), _Rectangle, Infinity)", //
        "{Rectangle({0.75,0},{1.25,1.0}),Rectangle({1.75,0},{2.25,2.0})}");
  }

  @Test
  public void testComplexFunctionPlots() {
    check(
        "Head /@ {AbsArgPlot(Exp(I*x), {x, 0, 2}), ReImPlot(Sqrt(x), {x, -1, 1}), "
            + "ComplexContourPlot(Re(z^3), {z, -1 - I, 1 + I})}", //
        "{Graphics,Graphics,Graphics}");
    // a negative function is cyan
    check("Union(Cases(AbsArgPlot(x - 2, {x, 0, 1}), _Hue, Infinity))", //
        "{Hue(0.5,0.75,0.9)}");
    // the real and the imaginary part of each function
    check("Length(Cases(ReImPlot({x, I*x}, {x, 0, 1}), _Line, Infinity))", //
        "4");
  }

  @Test
  public void testLineIntegralConvolutionPlot() {
    check(
        "Dimensions(First(Cases(LineIntegralConvolutionPlot({1, y}, {x, 0, 1}, {y, 0, 1}, "
            + "RasterSize -> {6, 4}), Raster(d_, ___) :> d, Infinity)))", //
        "{4,6,3}");
    check(
        "Dimensions(First(Cases(LineIntegralConvolutionPlot({{1, x}, {\"noise\", 12, 30}}, "
            + "{x, 0, 1}, {y, 0, 2}), Raster(d_, ___) :> d, Infinity)))", //
        "{12,12,3}");
    check(
        "d = First(Cases(LineIntegralConvolutionPlot({1, y}, {x, 0, 1}, {y, 0, 1}, "
            + "RasterSize -> 10), Raster(d_, ___) :> d, Infinity)); {Min(d) >= 0, Max(d) <= 1}", //
        "{True,True}");
    check("LineIntegralConvolutionPlot({1, y}, {x, 0, 0}, {y, 0, 1})", //
        "LineIntegralConvolutionPlot({1,y},{x,0,0},{y,0,1})");
    // a field held by a symbol
    check(
        "w = {x - y, 2}; Head(LineIntegralConvolutionPlot(w, {x, 0, 1}, {y, 0, 1}, "
            + "RasterSize -> 5))", //
        "Graphics");
  }

  @Test
  public void testVectorPlotStyleAndWeakField() {
    // the Arrowheads of VectorStyle are the only ones
    check(
        "Union(Cases(VectorPlot({2*y, x}, {x, 0, 1}, {y, 0, 1}, VectorPoints -> 4, "
            + "VectorStyle -> {Red, Arrowheads(0.07)}), _Arrowheads, Infinity))", //
        "{Arrowheads(0.07)}");
    // the arrows of a weak field are as long as those of a strong one
    check(
        "{Length(Cases(VectorPlot({10^-20*x, 10^-20}, {x, 0, 1}, {y, 0, 1}, VectorPoints -> 4), "
            + "_Arrow, Infinity)), "
            + "Length(Cases(VectorPlot({x, 1}, {x, 0, 1}, {y, 0, 1}, VectorPoints -> 4), "
            + "_Arrow, Infinity))}", //
        "{18,18}");
  }

  @Test
  public void testVectorPlot3DColors() {
    // a ColorFunction colours every arrow
    check(
        "Union(Cases(VectorPlot3D({y, 1, 0}, {x, 0, 1}, {y, 0, 1}, {z, 0, 1}, VectorPoints -> 2, "
            + "ColorFunction -> Function(Red)), _RGBColor, Infinity))", //
        "{RGBColor(1.0,0.0,0.0)}");
    // several fields are drawn together, each in a colour of its own
    check(
        "g = VectorPlot3D({{1, 0, 0}, {0, 1, 0}, {0, 0, 1}}, {x, 0, 1}, {y, 0, 1}, {z, 0, 1}, "
            + "VectorPoints -> 2); "
            + "{Length(Cases(g, Arrow(_Tube), Infinity)), Length(Union(Cases(g, _RGBColor, Infinity)))}", //
        "{24,3}");
  }
}
