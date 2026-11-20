package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * The successive differences of a list of points, the refinement of a parametric curve where it
 * turns sharply, the mesh lines of a contour surface, the shading of density plot cells, the
 * frame ticks {@code True} asks for, the fill rule of a crossing polygon and the height of a small
 * graphic.
 */
public class CurveSamplingAndMeshTest extends ExprEvaluatorTestCase {

  @Test
  public void testDifferencesOfPoints() {
    // a list of points gives the list of the vectors between them
    check("Differences({{2, 5}, {3, 1}, {7, 2}, {7, 7}})", //
        "{{1,-4},{4,1},{0,5}}");
    check("Differences({{2, 5}, {3, 1}, {7, 2}, {7, 7}}, 2)", //
        "{{3,5},{-4,4}}");
    check("Differences({{2.5, 1}, {3, -1.5}})", //
        "{{0.5,-2.5}}");
    // a list of elements of different shapes subtracts element by element
    check("Differences({u, {1, 2}, v})", //
        "{{1-u,2-u},{-1+v,-2+v}}");
    check("Differences({{1, 2, 3}, {4, 6, 8}, {9, 9, 9}}, 0)", //
        "{{1,2,3},{4,6,8},{9,9,9}}");
  }

  @Test
  public void testParametricPlotRefinesSharpTurns() {
    // a curve that winds many times is sampled more finely at its tight turns: no two consecutive
    // segments of the drawn polyline turn by much more than a few degrees
    check("pts = First(Cases(ParametricPlot({Cos(9*t), Sin(4*t)}, {t, 0, 60}), "
        + "Line(l_) :> l, Infinity)); dv = Differences(pts); "
        + "{Length(pts) > 1201, Max(MapThread(VectorAngle, {Most(dv), Rest(dv)})) < 0.21}", //
        "{True,True}");
    // a smooth curve that turns slowly keeps the even sweep
    check("Length(First(Cases(ParametricPlot({2*Cos(t), Sin(t)}, {t, 0, 3}), "
        + "Line(l_) :> l, Infinity)))", //
        "1201");
    // MaxRecursion -> 0 asks for no refinement
    check("Length(First(Cases(ParametricPlot({Cos(9*t), Sin(4*t)}, {t, 0, 60}, "
        + "MaxRecursion -> 0), Line(l_) :> l, Infinity)))", //
        "151");
  }

  @Test
  public void testContourPlot3DMeshLines() {
    // Mesh -> n cuts the surface at n evenly spaced planes across each axis; the curves of one
    // axis form one Line after the style of that axis
    check("gc = First(ContourPlot3D(x^2 + 2*y^2 + z^2 == 2, {x, -2, 2}, {y, -2, 2}, {z, -2, 2}, "
        + "Mesh -> 1, PlotPoints -> 9)); ls = Cases(gc, Line(l_) :> l, Infinity); "
        + "{Length(ls), Union(Round(Flatten(ls[[1]], 1)[[All, 1]], 0.001)), "
        + "Union(Round(Flatten(ls[[2]], 1)[[All, 2]], 0.001)), "
        + "Union(Round(Flatten(ls[[3]], 1)[[All, 3]], 0.001))}", //
        "{3,{0.0},{0.0},{0.0}}");
    // the points of a mesh line lie on the surface, up to the sampling error
    check("gc = First(ContourPlot3D(x^2 + y^2 + z^2 == 4, {x, -3, 3}, {y, -3, 3}, {z, -3, 3}, "
        + "Mesh -> {{1}, None, None}, PlotPoints -> 15)); "
        + "p = Flatten(Cases(gc, Line(l_) :> l, Infinity), 2); "
        + "{Union(Round(p[[All, 1]], 0.001)), Max(Abs(Norm /@ p - 2)) < 0.1}", //
        "{{1.0},True}");
    // a MeshStyle list is taken in turn for the x, y and z lines
    check("Cases(First(ContourPlot3D(x^2 + y^2 + z^2 == 4, {x, -3, 3}, {y, -3, 3}, {z, -3, 3}, "
        + "Mesh -> 1, MeshStyle -> {Green, Red}, PlotPoints -> 8)), "
        + "{s_, Line(_)} :> s, Infinity)", //
        "{RGBColor(0,1,0),RGBColor(1,0,0),RGBColor(0,1,0)}");
    // MeshStyle -> None and Mesh -> None draw no mesh lines
    check("Count(ContourPlot3D(x^2 + y^2 + z^2 == 4, {x, -3, 3}, {y, -3, 3}, {z, -3, 3}, "
        + "Mesh -> 3, MeshStyle -> None, PlotPoints -> 8), _Line, Infinity)", //
        "0");
    check("Count(ContourPlot3D(x*y*z == 1/2, {x, -2, 2}, {y, -2, 2}, {z, -2, 2}, "
        + "Mesh -> None, BoundaryStyle -> None, PlotPoints -> 8), _Line, Infinity)", //
        "0");
  }
  @Test
  public void testListDensityPlotShadesTriangles() {
    // a cell is split along the diagonal from its lower left to its upper right node and shaded
    // linearly in each half: the shared value of the two ends runs straight along that diagonal,
    // while the corners off it keep their own value
    check("r = First(Cases(ListDensityPlot({{3, 0}, {0, 3}}, ColorFunction -> GrayLevel), "
        + "Raster(m_, ___) :> m, Infinity)); n = Length(r); "
        + "{Round(Min(Table(r[[k, k, 1]], {k, n})), 0.01), Round(r[[2, n - 1, 1]], 0.01), "
        + "Round(r[[n - 1, 2, 1]], 0.01)}", //
        "{1.0,0.19,0.19}");
  }

  @Test
  public void testFrameTicksTrue() {
    // True is the automatic ticks on the bottom and left edge and none on the top and right edge
    check("ExportString(Plot(x^3, {x, -1, 2}, Frame -> True, FrameTicks -> True), \"SVG\") == "
        + "ExportString(Plot(x^3, {x, -1, 2}, Frame -> True, "
        + "FrameTicks -> {{Automatic, None}, {Automatic, None}}), \"SVG\")", //
        "True");
    check("ExportString(Plot(x^3, {x, -1, 2}, Frame -> True, FrameTicks -> {True, False}), "
        + "\"SVG\") == ExportString(Plot(x^3, {x, -1, 2}, Frame -> True, "
        + "FrameTicks -> {{None, None}, {Automatic, None}}), \"SVG\")", //
        "True");
    check("ExportString(Plot(x^3, {x, -1, 2}, Frame -> True, "
        + "FrameTicks -> {True, True, False, False}), \"SVG\") == "
        + "ExportString(Plot(x^3, {x, -1, 2}, Frame -> True, FrameTicks -> True), \"SVG\")", //
        "True");
  }

  @Test
  public void testContourPlot3DMeshDefaults() {
    check("Options(ContourPlot3D, {Mesh, MeshStyle, MeshFunctions})", //
        "{Mesh->Automatic,MeshFunctions->{#1&,#2&,#3&},MeshStyle->Automatic}");
  }

  @Test
  public void testCrossingPolygonFillsEvenOdd() {
    // an outline that crosses itself leaves the regions it winds around twice open
    check("StringContainsQ(ExportString(Graphics(Polygon({{0, 0}, {4, 3}, {4, 0}, {0, 3}})), "
        + "\"SVG\"), \"evenodd\")", //
        "True");
    check("StringContainsQ(ExportString(Graphics(Polygon(Table({Cos(2*Pi*k*3/7), "
        + "Sin(2*Pi*k*3/7)}, {k, 0, 6}))), \"SVG\"), \"evenodd\")", //
        "True");
    // a simple outline needs no fill rule
    check("StringContainsQ(ExportString(Graphics(Polygon({{0, 0}, {4, 0}, {3, 2}, {1, 3}})), "
        + "\"SVG\"), \"evenodd\")", //
        "False");
  }

  @Test
  public void testSmallGraphicKeepsItsShape() {
    // a narrow picture is not stretched to a minimum height
    check("StringCases(ExportString(Graphics(Rectangle(), ImageSize -> 24), \"SVG\"), "
        + "RegularExpression(\"<svg[^>]*>\"))", //
        "{<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"24\" height=\"24\" viewBox=\"0 0 24 24\">}");
  }

  @Test
  public void testLargeCrossingOutlineIsFilledEvenOdd() {
    // a star of 601 corners, each joined to the one two steps on, crosses itself everywhere
    check("StringContainsQ(ExportString(Graphics(Polygon(Table({Cos(4*Pi*k/601), Sin(4*Pi*k/601)}, "
        + "{k, 0, 600}))), \"SVG\"), \"evenodd\")", //
        "True");
  }

  @Test
  public void testParametricPlotBreaksAtPoles() {
    // a coordinate which runs off to infinity breaks the curve, unless Exclusions -> None
    check("Cases(ParametricPlot({2*Sec(s), Tan(s)}, {s, 0, 2*Pi}), Line(p_) :> Length(p) > 10, Infinity)", //
        "{True,True,True}");
    check("Cases(ParametricPlot({s, Cot(s)}, {s, 0.5, 6}), Line(p_) :> Length(p) > 10, Infinity)", //
        "{True,True}");
    check("Cases(ParametricPlot({s, Cot(s)}, {s, 0.5, 6}, Exclusions -> None), Line(p_) :> Length(p) > 10, Infinity)", //
        "{True}");
    // the zeros of a coordinate don't break it
    check("Length(Cases(ParametricPlot({Sin(2*s), Cos(3*s)}, {s, 0, 2*Pi}), _Line, Infinity))", //
        "1");
  }

  @Test
  public void testAutomaticRangeLeavesOutPoles() {
    // the automatic range is the body of the curve over its variable, not the samples crowded
    // next to a pole
    check("y = Last(PlotRange /. AbsoluteOptions(Plot(1/(x + 1/4)^2, {x, -1, 1}), PlotRange)); "
        + "{First(y), 30 < Last(y) < 100}", //
        "{0.0,True}");
    check("y = Last(PlotRange /. AbsoluteOptions(Plot(2/(x - 1), {x, -2, 3}), PlotRange)); "
        + "-20 < First(y) < -5 && 5 < Last(y) < 20", //
        "True");
    check("r = PlotRange /. AbsoluteOptions(ParametricPlot({s, Cot(s)}, {s, 0.5, 6}), PlotRange); "
        + "{First(r), -10 < r[[2, 1]] < -3 && 3 < r[[2, 2]] < 10}", //
        "{{0.5,6.0},True}");
    // the steep end of a bounded curve over a small part of the variable is cut as well
    check("y = Last(PlotRange /. AbsoluteOptions(Plot(Exp(x/2), {x, 0, 16}), PlotRange)); "
        + "{First(y), 1000 < Last(y) < 2500}", //
        "{0.0,True}");
    check("y = Last(PlotRange /. AbsoluteOptions(Plot(1/(x^2 + 1/100), {x, -1, 1}), PlotRange)); "
        + "{First(y), 40 < Last(y) < 80}", //
        "{0.0,True}");
    // a curve spread evenly over its range keeps all of it
    check("Max(Abs(Flatten(PlotRange /. AbsoluteOptions(ParametricPlot({Cosh(s), Sinh(s)}, "
        + "{s, -3, 3}), PlotRange)) - N({1, Cosh(3), -Sinh(3), Sinh(3)}))) < 10^-6", //
        "True");
    // mesh points on the curve count like the curve points they sit on
    check("y = Last(PlotRange /. AbsoluteOptions(Plot(1/(x + 1/4)^2, {x, -1, 1}, Mesh -> All), "
        + "PlotRange)); {First(y), 30 < Last(y) < 100}", //
        "{0.0,True}");
    // on a logarithmic axis an exponential is a straight line and keeps its range, a pole is cut
    check("Last(PlotRange /. AbsoluteOptions(LogPlot(Exp(x/2), {x, 0, 16}), PlotRange))[[2]] "
        + "> 0.99*E^8", //
        "True");
    check("Last(PlotRange /. AbsoluteOptions(LogPlot(1/(x - 2/5)^2, {x, 0, 1}), PlotRange))[[2]] "
        + "< 10^6", //
        "True");
    check("Last(PlotRange /. AbsoluteOptions(ParametricPlot({s, Exp(s/2)}, {s, 0, 16}, "
        + "ScalingFunctions -> {None, \"Log\"}), PlotRange))[[2]] > 0.99*E^8", //
        "True");
    // a range given in the call stays as it is
    check("Last(PlotRange /. AbsoluteOptions(ParametricPlot({s, Cot(s)}, {s, 0.5, 6}, "
        + "PlotRange -> All), PlotRange))[[2]] > 1000", //
        "True");
  }
}
