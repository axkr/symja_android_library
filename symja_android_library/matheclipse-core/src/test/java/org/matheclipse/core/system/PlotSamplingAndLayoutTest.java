package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

/**
 * How often and where the plots sample their functions, and how a few options shape the finished
 * picture: contour surfaces of functions which only take numbers, the sample grid of a contour plot
 * without refinement, the refinement of a parametric curve in a stretched frame, the value range of
 * an array plot, automatic frame labels, the rim of a contour surface and the layout of an array of
 * graphics.
 */
public class PlotSamplingAndLayoutTest extends ExprEvaluatorTestCase {

  @Test
  @Tag(TestTags.SLOW)
  public void testContourPlot3DSamplesOnlyNumbers() {
    // a compiled iteration of a vector argument, which would never settle on symbols, is only
    // evaluated at the sample points
    check("cr = Compile({{v, _Real, 1}}, FixedPoint((2*# + Total(v^2)/#^2)/3 &, 1.0, "
        + "SameTest -> (Abs(#1 - #2) < 10^-8 &))); "
        + "Head(ContourPlot3D(cr({x, y, z}), {x, -1, 1}, {y, -1, 1}, {z, -1, 1}, "
        + "Contours -> {0.7}, PlotPoints -> 6))", //
        "Graphics3D");
    // the same for the plots of a function of two variables
    check("cq = Compile({{v, _Real, 1}}, FixedPoint((# + Total(v^2)/#)/2 &, 1.0, "
        + "SameTest -> (Abs(#1 - #2) < 10^-8 &))); "
        + "{Head(ContourPlot(cq({x, y}), {x, -1, 1}, {y, -1, 1}, PlotPoints -> 6)), "
        + "Head(DensityPlot(cq({x, y}), {x, -1, 1}, {y, -1, 1}, PlotPoints -> 6)), "
        + "Head(Plot3D(cq({x, y}), {x, -1, 1}, {y, -1, 1}, PlotPoints -> 6))}", //
        "{Graphics,Graphics,Graphics3D}");
  }

  @Test
  public void testPlotFunctionEvaluatedOnceSymbolically() {
    // a derivative is taken once with symbolic coordinates, then sampled
    check("Length(Cases(ContourPlot(D(x^3*y, x), {x, 0, 1}, {y, 0, 1}, Contours -> {1}, "
        + "ContourShading -> None), _Line, Infinity)) > 0", //
        "True");
    check("Head(Plot3D(D(x^2*y^2, y), {x, 0, 1}, {y, 0, 1}, PlotPoints -> 5))", //
        "Graphics3D");
    // an expansion is done once: the samples see its value
    check("k = 0; e(n_) := (k++; Expand((x + y)^n)); "
        + "DensityPlot(e(3), {x, 0, 1}, {y, 0, 1}, PlotPoints -> 4); k", //
        "1");
  }

  @Test
  public void testCompiledFunctionOfSymbols() {
    // without machine numbers the body is evaluated as it is, with a message
    check("cn = Compile({{v, _Real, 1}}, Total(v^3)); Check(cn({a, b}), failed, "
        + "CompiledFunction::cfta)", //
        "failed");
    check("cn = Compile({{v, _Real, 1}}, Total(v^3)); Quiet(cn({a, b}))", //
        "a^3+b^3");
    check("cs = Compile({t}, t^2 - 1); Check(cs(a), failed, CompiledFunction::cfsa)", //
        "failed");
    check("cs = Compile({t}, t^2 - 1); cs(3)", //
        "8.0");
    // numbers of the wrong rank are no machine numbers of the declared rank either
    check("cm = Compile({{v, _Real, 1}}, Total(v)); Check(cm({{1, 2}, {5, 7}}), failed, "
        + "CompiledFunction::cfta)", //
        "failed");
    check("cm = Compile({{v, _Real, 1}}, Total(v)); Quiet(cm({{1, 2}, {5, 7}}))", //
        "{6,9}");
  }

  @Test
  public void testContourPlotWithoutRefinementSamplesTheGrid() {
    // MaxRecursion -> 0 samples the PlotPoints grid and nothing else, 15 points per axis by default
    check("m = 0; q(a_?NumericQ, b_?NumericQ) := (m++; a^2 - b); "
        + "{m = 0; ContourPlot(q(a, b), {a, 0, 2}, {b, -1, 3}, MaxRecursion -> 0, "
        + "PlotPoints -> 7); m, "
        + "m = 0; ContourPlot(q(a, b), {a, 0, 2}, {b, -1, 3}, MaxRecursion -> 0, "
        + "PlotPoints -> {3, 4}); m, "
        + "m = 0; ContourPlot(q(a, b), {a, 0, 2}, {b, -1, 3}, MaxRecursion -> 0); m}", //
        "{49,12,225}");
    // a function with two values is still two functions
    check("w(a_?NumericQ, b_?NumericQ) := {a - b, a + b}; "
        + "Length(Cases(ContourPlot(w(a, b), {a, -1, 1}, {b, -1, 1}, Contours -> {0}, "
        + "ContourShading -> None, MaxRecursion -> 0), _Line, Infinity)) > 1", //
        "True");
  }

  @Test
  public void testContourPlotAutomaticFrameLabel() {
    // the label of each edge, {{left, right}, {bottom, top}}
    check("FrameLabel /. Options(ContourPlot(s*Sin(r), {r, 0, 3}, {s, -1, 1}, "
        + "FrameLabel -> Automatic))", //
        "{{s,None},{r,None}}");
    check("FrameLabel /. Options(DensityPlot(s*Sin(r), {r, 0, 3}, {s, -1, 1}, "
        + "FrameLabel -> Automatic))", //
        "{{s,None},{r,None}}");
    // as the default of the function
    check("SetOptions(DensityPlot, FrameLabel -> Automatic); "
        + "l = FrameLabel /. Options(DensityPlot(s*r, {r, 0, 3}, {s, -1, 1})); "
        + "SetOptions(DensityPlot, FrameLabel -> None); l", //
        "{{s,None},{r,None}}");
    check("svg = ExportString(ContourPlot(s*Sin(r), {r, 0, 3}, {s, -1, 1}, "
        + "FrameLabel -> Automatic), \"SVG\"); "
        + "{StringCount(svg, \">r</text>\"), StringCount(svg, \">s</text>\"), "
        + "StringCount(svg, \"None\")}", //
        "{1,1,0}");
  }

  @Test
  public void testParametricPlotRefinesAsDrawn() {
    // an ellipse 40 times narrower than high, drawn in a square frame: the turns between the
    // displayed segments stay small, and the samples are not spent on the rest of the curve
    check("pts = First(Cases(ParametricPlot({Cos(3*t)/40, Sin(2*t)}, {t, 0, 300}, "
        + "AspectRatio -> 1), Line(l_) :> l, Infinity)); "
        + "dv = Differences({40*#[[1]], #[[2]]} & /@ pts); "
        + "{Max(MapThread(VectorAngle, {Most(dv), Rest(dv)})) < 0.25, Length(pts) < 20000}", //
        "{True,True}");
  }

  @Test
  public void testArrayPlotValueRange() {
    // opaque grey, white at the minimum and black at the maximum; transparent outside
    check("Rest(First(ArrayPlot({{2, 4}, {6, 10}}, PlotRange -> {2, 6})))", //
        "Raster({{0,0},{2,2}},{0,1})");
    check("First(First(ArrayPlot({{2, 4}, {6, 10}}, PlotRange -> {2, 6})))", //
        "{{{0.0,0.0,0.0,1.0},{1.0,1.0,1.0,0.0}},{{1.0,1.0,1.0,1.0},{0.5,0.5,0.5,1.0}}}");
    // a single number is the top of a range from 0
    check("First(First(ArrayPlot({{1, 3, 5}}, PlotRange -> 4)))", //
        "{{{0.75,0.75,0.75,1.0},{0.25,0.25,0.25,1.0},{1.0,1.0,1.0,0.0}}}");
    // ClippingStyle colours the cells below and above the range
    check("First(First(ArrayPlot({{-3, 1, 7}}, PlotRange -> {0, 2}, "
        + "ClippingStyle -> {Green, Yellow})))", //
        "{{{0.0,1.0,0.0,1.0},{0.5,0.5,0.5,1.0},{1.0,1.0,0.0,1.0}}}");
    // the range of values is no range of coordinates: the picture has no PlotRange
    check("PlotRange /. Options(ArrayPlot({{1, 3, 5}}, PlotRange -> {0, 4}))", //
        "PlotRange");
  }

  @Test
  public void testContourPlot3DRim() {
    // a given BoundaryStyle outlines the surface where it meets the box: one closed curve of
    // vertex numbers in the GraphicsComplex
    check("g = ContourPlot3D(x + y == 0, {x, -1, 1}, {y, -1, 1}, {z, -1, 1}, Mesh -> None, "
        + "BoundaryStyle -> Directive(Green, Thick)); "
        + "gc = First(Cases(g, _GraphicsComplex, Infinity)); "
        + "ls = Cases(gc, {___, Directive(Green, Thick), Line(l_)} :> l, Infinity); "
        + "e = First(gc)[[First(ls)]]; "
        + "{Length(ls), VectorQ(First(ls), IntegerQ), First(First(ls)) == Last(First(ls)), "
        + "AllTrue(e, Abs(#[[1]] + #[[2]]) < 10^-6 &), AllTrue(e, Max(Abs(#)) > 1 - 10^-6 &)}", //
        "{1,True,True,True,True}");
    // a region cuts the surface and its rim at the edge of the region
    check("g = ContourPlot3D(y == 0, {x, -1, 1}, {y, -1, 1}, {z, -1, 1}, Mesh -> None, "
        + "BoundaryStyle -> Orange, RegionFunction -> Function({x, y, z}, x^2 + z^2 < 0.36)); "
        + "gc = First(Cases(g, _GraphicsComplex, Infinity)); "
        + "ix = Flatten(Cases(gc, {___, Orange, Line(l_)} :> l, Infinity)); e = First(gc)[[ix]]; "
        + "{Length(e) > 0, AllTrue(e, Abs(Norm({#[[1]], #[[3]]}) - 0.6) < 10^-4 &), "
        + "FreeQ(Differences(ix), 0)}", //
        "{True,True,True}");
    // BoundaryStyle -> None and no mesh draw no line
    check("Count(ContourPlot3D(x + y == 0, {x, -1, 1}, {y, -1, 1}, {z, -1, 1}, Mesh -> None, "
        + "BoundaryStyle -> None), _Line, Infinity)", //
        "0");
  }

  @Test
  public void testGraphicsArray() {
    // an obsolete name, with a message
    check("Check(GraphicsArray({Graphics(Point({0, 0}))}), failed, GraphicsArray::obs)", //
        "failed");
    // a list of graphics is one row of a grid, a matrix of them the grid itself
    check("GraphicsArray({Graphics(Point({0, 0})), Graphics(Line({{0, 0}, {1, 1}}))}) // InputForm", //
        "GraphicsGrid({{Graphics(Point({0,0})),Graphics(Line({{0,0},{1,1}}))}})");
    check("GraphicsArray({{Graphics(Point({0, 0}))}, {Graphics(Point({1, 1}))}}, Frame -> All) "
        + "// InputForm", //
        "GraphicsGrid({{Graphics(Point({0,0}))},{Graphics(Point({1,1}))}},Frame->All)");
    // a mixture of rows and single graphics has no layout
    check("Head(GraphicsArray({{Graphics(Point({0, 0}))}, Graphics(Point({1, 1}))}))", //
        "GraphicsArray");
  }

  @Test
  public void testPlotSamplesTheHeldFunctionWhereItsSymbolicValueDiffers() {
    // w(x) solves for the plot variable itself when x is a symbol; at numbers it is a root in a
    check("w(a_) := Part(FindRoot(Exp(-x) == a*x, {x, 0.5}), 1, -1); "
        + "Round(Cases(Plot(w(x), {x, 1, 3}), Line(l_) :> {First(l), Last(l)}, Infinity), "
        + "0.001)", //
        "{{{1.0,0.567},{3.0,0.258}}}");
    // a function which is only a number for a number: the curve is drawn all the same
    check("v(b_) := Part(FindRoot(r^3 + b*r == 1, {r, 1.0}), 1, -1); "
        + "l = First(Cases(ParametricPlot({b, v(b)}, {b, 0, 2}), Line(l_) :> l, Infinity)); "
        + "Round({First(l), Last(l)}, 0.001)", //
        "{{0.0,1.0},{2.0,0.453}}");
    // the polar radius of the same kind
    check("q(b_) := Part(FindRoot(r^3 + b*r == 1, {r, 1.0}), 1, -1); "
        + "l = First(Cases(PolarPlot(q(t), {t, 0, 1}), Line(l_) :> l, Infinity)); "
        + "Round(Norm /@ {First(l), Last(l)}, 0.001)", //
        "{1.0,0.682}");
    // a symbolic value which agrees with the function is sampled, and evaluated once
    check("k = 0; e(n_) := (k++; Expand((1 + x)^n)); Plot(e(4), {x, 0, 1}); k", //
        "1");
    check("Round(Cases(Plot(D(x^3, x, x, x), {x, 0, 1}), Line(l_) :> {First(l), Last(l)}, "
        + "Infinity), 0.001)", //
        "{{{0.0,6.0},{1.0,6.0}}}");
  }

  @Test
  public void testRegionPlotOfSeveralRegions() {
    // every region in its own colour, the fills before the boundary lines
    check("g = RegionPlot({x^2 + y^2 < 1, x > 1/2}, {x, -1, 1}, {y, -1, 1})[[1]]; "
        + "{Map(Head, Cases(g, _Polygon | _Line, Infinity)), "
        + "Length(Union(Cases(g, Directive(c_RGBColor, _, Opacity(_)) :> c, Infinity)))}", //
        "{{Polygon,Polygon,Line,Line},2}");
    // a boundary style is added to the default one
    check("Cases(RegionPlot(x + y < 1/2, {x, -1, 1}, {y, -1, 1}, BoundaryStyle -> Red)[[1]], "
        + "{s_, __Line} :> s, Infinity)", //
        "{Directive(RGBColor(0.24,0.6,0.8),AbsoluteThickness(2.0),RGBColor(1,0,0))}");
    check("Cases(RegionPlot({x < 0, y < 0}, {x, -1, 1}, {y, -1, 1}, BoundaryStyle -> None), "
        + "_Line, Infinity)", //
        "{}");
  }

  @Test
  public void testRegionPlotStyles() {
    // a given fill style is opaque unless it says otherwise, and is not nested in a directive
    check("Cases(First(RegionPlot(x + y > 0, {x, -1, 1}, {y, -1, 1}, "
        + "PlotStyle -> Directive(Red, Opacity(0.5)))), _Directive, Infinity)", //
        "{Directive(AbsoluteThickness(2.0),RGBColor(1,0,0),Opacity(0.5)),"
            + "Directive(RGBColor(0.24,0.6,0.8),AbsoluteThickness(2.0))}");
    check("Cases(First(RegionPlot(x + y > 0, {x, -1, 1}, {y, -1, 1}, PlotStyle -> Orange, "
        + "BoundaryStyle -> Thick)), _Directive, Infinity)", //
        "{Directive(AbsoluteThickness(2.0),Opacity(1),RGBColor(1.0,0.5,0.0)),"
            + "Directive(RGBColor(0.24,0.6,0.8),AbsoluteThickness(2.0),Thickness(Large))}");
    // a boundary of one piece is one list of point indices, of several pieces a list of them
    check("Cases(First(RegionPlot(x^2 + 2*y^2 < 1, {x, -2, 2}, {y, -2, 2})), "
        + "Line(p_) :> {VectorQ(p, IntegerQ), First(p) == Last(p)}, Infinity)", //
        "{{True,True}}");
    check("Cases(First(RegionPlot(x^2 > 1, {x, -2, 2}, {y, -2, 2})), "
        + "Line(p_) :> {Length(p), VectorQ(First(p), IntegerQ)}, Infinity)", //
        "{{2,True}}");
    // the surface of a region in space is in a list of primitives
    check("Head(First(RegionPlot3D(x^2 + y^2 + z^2 < 0.5, {x, -1, 1}, {y, -1, 1}, {z, -1, 1}, "
        + "PlotPoints -> 6)))", //
        "List");
  }

  @Test
  public void testCurvesClippedAtExplicitVerticalRange() {
    check("Round(MinMax(Flatten(Cases(First(Plot(1/x^2, {x, 0, 3}, PlotRange -> {0, 4})), "
        + "Line(p_) :> p[[All, 2]], Infinity))), 0.01)", //
        "{0.11,4.0}");
    // the pieces inside the range end on its boundary
    check("Cases(First(Plot(3*Sin(x), {x, 0, 2*Pi}, PlotRange -> {-2, 2})), "
        + "Line(p_) :> Round({First(p)[[1]], Last(p)}, 0.01), Infinity)", //
        "{{0.0,{0.73,2.0}},{2.41,{3.87,-2.0}},{5.55,{6.28,0.0}}}");
    check("Count(First(Plot(3*Sin(x), {x, 0, 2*Pi}, PlotRange -> {{0, 4}, {-2, 2}})), "
        + "_Line, Infinity)", //
        "3");
    // a ClippingStyle draws the stretches outside along the boundary
    check("Count(First(Plot(3*Sin(x), {x, 0, 2*Pi}, PlotRange -> {-2, 2}, "
        + "ClippingStyle -> Green)), _Line, Infinity)", //
        "5");
    check("Count(First(Plot(3*Sin(x), {x, 0, 2*Pi}, PlotRange -> {-2, 2}, "
        + "ClippingStyle -> None)), _Line, Infinity)", //
        "3");
    // only the ends which are numbers cut the curve
    check("Count(First(Plot(3*Sin(x), {x, 0, 2*Pi}, PlotRange -> {Full, {-2, Automatic}})), "
        + "_Line, Infinity)", //
        "2");
    check("Count(First(Plot(3*Sin(x), {x, 0, 2*Pi}, PlotRange -> All)), _Line, Infinity)", //
        "1");
    check("Cases(First(ListLinePlot({0, 3, 0, -3, 0}, PlotRange -> {-1, 1})), "
        + "Line(p_) :> Round(p, 0.01), Infinity)", //
        "{{{1.0,0.0},{1.33,1.0}},{{2.67,1.0},{3.0,0.0},{3.33,-1.0}},{{4.67,-1.0},{5.0,0.0}}}");
    // a style for the bottom and one for the top
    check("Cases(First(ListLinePlot({0, 3, 0, -3, 0}, PlotRange -> {-1, 1}, "
        + "ClippingStyle -> {Blue, Red})), {_RGBColor, __Line}, Infinity)", //
        "{{RGBColor(0,0,1),Line({{3.33333,-1.0},{4.66667,-1.0}})},"
            + "{RGBColor(1,0,0),Line({{1.33333,1.0},{2.66667,1.0}})}}");
  }

  @Test
  public void testFillingBetweenConsecutiveCurves() {
    check("Count(Plot({x, 2*x, 3*x, 4*x}, {x, 0, 1}, Filling -> True), _Polygon, Infinity)", //
        "3");
    check("Count(Plot(Sin(x), {x, 0, 1}, Filling -> True), _Polygon, Infinity)", //
        "0");
    // the area between two curves which cross is one polygon on each side of the crossing; the
    // fillings come before the curves
    check("Map(Head, Cases(First(Plot({x, 1 - x}, {x, 0, 1}, Filling -> True)), "
        + "_Polygon | _Line, Infinity))", //
        "{Polygon,Polygon,Line,Line}");
  }

  @Test
  public void testColorFunctionOfRegionAndContourSurfaces() {
    // one colour everywhere is the style of the surface, opacity and all
    check("g = RegionPlot3D(x^2 + y^2 + z^2 < 0.8, {x, -1, 1}, {y, -1, 1}, {z, -1, 1}, "
        + "ColorFunction -> (Directive(Blue, Opacity(0.5)) &), Mesh -> None, PlotPoints -> 8); "
        + "{Union(Cases(First(g), _RGBColor | _Opacity, Infinity)), FreeQ(g, VertexColors)}", //
        "{{Opacity(0.5),RGBColor(0,0,1)},True}");
    // colours which vary are the colours of the vertices; the coordinates are scaled to 0..1
    check("g = RegionPlot3D(x^2 + y^2 + z^2 < 0.8, {x, -1, 1}, {y, -1, 1}, {z, -1, 1}, "
        + "ColorFunction -> Function({x, y, z}, If(z > 0.5, Red, Blue)), Mesh -> None, "
        + "PlotPoints -> 8); Union(Flatten(Cases(g, (VertexColors -> c_) :> c, Infinity)))", //
        "{RGBColor(0,0,1),RGBColor(1,0,0)}");
    check("g = ContourPlot3D(z == 0, {x, -1, 1}, {y, -1, 1}, {z, -1, 1}, "
        + "ColorFunction -> Function({x, y, z, f}, If(x < 0.5, Red, Blue)), Mesh -> None, "
        + "PlotPoints -> 6); {Union(Flatten(Cases(g, (VertexColors -> c_) :> c, Infinity))), "
        + "FreeQ(Rest(List @@ g), ColorFunction)}", //
        "{{RGBColor(0,0,1),RGBColor(1,0,0)},True}");
  }

  @Test
  public void testColorFunctionCurveIsOneLine() {
    // a curve coloured along its length is one line with a colour at each point, and keeps its
    // style
    check("g = ParametricPlot({Sin(2*t), Cos(t)}, {t, 0, Pi}, PlotStyle -> Dotted, "
        + "ColorFunction -> Function({x, y, u}, GrayLevel(u))); "
        + "l = First(Cases(g, _Line, Infinity)); "
        + "{Count(g, _Line, Infinity), Length(l[[1]]) == Length(VertexColors /. Rest(List @@ l)), "
        + "Count(g, _Dashing, Infinity)}", //
        "{1,True,1}");
    check("l = First(Cases(Plot(x^2, {x, 0, 1}, ColorFunction -> (GrayLevel(#2) &), "
        + "ColorFunctionScaling -> False), _Line, Infinity)); "
        + "c = VertexColors /. Rest(List @@ l); Round({First(c)[[1]], Last(c)[[1]]}, 0.01)", //
        "{0.0,1.0}");
  }

  @Test
  public void testPlotRangeWithOpenEnds() {
    // a bare pair with a number is the vertical range; its All or Automatic end is the data's
    check("PlotRange(Graphics(Line({{1, -2}, {4, 6}}), PlotRange -> {0.5, All}))", //
        "{{1.0,4.0},{0.5,6.0}}");
    check("PlotRange(Graphics(Line({{1, -2}, {4, 6}}), PlotRange -> {Automatic, 3}))", //
        "{{1.0,4.0},{-2.0,3.0}}");
    // each end of {xspec, yspec} on its own
    check("PlotRange(Graphics(Line({{1, -2}, {4, 6}}), PlotRange -> {{2, All}, {Automatic, 3}}))", //
        "{{2.0,4.0},{-2.0,3.0}}");
    // a plot keeps its iterator range in x and fills the open end of y from what it drew
    check("Round(PlotRange(Plot(2*x - 3, {x, 0, 3}, PlotRange -> {-1, All})), 0.01)", //
        "{{0.0,3.0},{-1.0,3.0}}");
    check("Round(PlotRange(Plot(2*x - 3, {x, 0, 3}, PlotRange -> {Automatic, 1})), 0.01)", //
        "{{0.0,3.0},{-3.0,1.0}}");
    // the curve is cut at the numeric end only
    check("MinMax(Cases(Plot(2*x - 3, {x, 0, 3}, PlotRange -> {-1, All}), "
        + "Line(p_) :> p[[All, 2]], Infinity))", //
        "{-1.0,3.0}");
    // the open end gets the padding of an automatic range, the given end none
    check("PlotRangePadding /. Options(Plot(2*x - 3, {x, 0, 3}, PlotRange -> {-1, All}))", //
        "{Scaled(0.02),{Automatic,Scaled(0.02)}}");
    check("PlotRange /. Options(ListPlot({3, 1, 5, 2}, PlotRange -> {2, All}))", //
        "{{0.0,4.0},{2.0,5.0}}");
    // a combined picture keeps the partial range of its first graphic
    check("Round(PlotRange(Show(Graphics(Line({{0, 2}, {3, 5}}), PlotRange -> {All, 4}), "
        + "Graphics(Line({{1, -1}, {6, 3}})))), 0.01)", //
        "{{0.0,6.0},{-1.0,4.0}}");
  }

  @Test
  public void testPlot3DMeshLevels() {
    // Mesh -> {xspec, yspec}: lines at the given x values, None for y; one line per level
    check("g = Plot3D(x + y^2, {x, -1, 2}, {y, 0, 3}, Mesh -> {{0.25, 1.5}, None}, "
        + "BoundaryStyle -> None, MeshStyle -> Red); pts = First(Cases(g, "
        + "GraphicsComplex(p_, ___) :> p, Infinity)); "
        + "{Length(Cases(g, _Line, Infinity)), Union(Round(Flatten(Cases(g, {Red, l__Line} "
        + ":> pts[[Flatten({l}[[All, 1]]), 1]], Infinity)), 0.001))}", //
        "{2,{0.25,1.5}}");
    // a count in a slot spaces that many lines evenly, None in both draws no mesh at all
    check("g = Plot3D(x + y^2, {x, -1, 2}, {y, 0, 3}, Mesh -> {None, 2}, BoundaryStyle -> None); "
        + "pts = First(Cases(g, GraphicsComplex(p_, ___) :> p, Infinity)); "
        + "Union(Round(Flatten(Cases(g, Line(s_, ___) :> pts[[Flatten(s), 2]], "
        + "Infinity)), 0.001))", //
        "{1.0,2.0}");
    check("Cases(Plot3D(x + y^2, {x, -1, 2}, {y, 0, 3}, Mesh -> {None, None}, "
        + "BoundaryStyle -> None), _Line, Infinity)", //
        "{}");
  }

  @Test
  public void testRevolutionPlot3DOptionsAndProfiles() {
    // a single number is the box it stands for, with the number as it was given
    check("PlotRange /. Options(RevolutionPlot3D(t^2, {t, 0, 2}, PlotRange -> 3))", //
        "{{-3,3},{-3,3},{-3,3}}");
    // a list of one element profiles is one surface each, each in its own colour
    check("g = RevolutionPlot3D({{t^2}, {1 - t}, {3*t}, {Sqrt(t)}}, {t, 0, 1}); "
        + "{Length(Cases(g, _GraphicsComplex, Infinity)), "
        + "Length(Union(Cases(g, _RGBColor, Infinity)))}", //
        "{4,4}");
  }

  @Test
  public void testSurfacePlotRangeForAllAxes() {
    check("PlotRange /. Options(RevolutionPlot3D(1 + t, {t, 0, 1}, PlotRange -> 7/2))", //
        "{{-7/2,7/2},{-7/2,7/2},{-7/2,7/2}}");
    // a bare pair is the range of each of the three axes
    check("PlotRange /. Options(RevolutionPlot3D(1 + t, {t, 0, 1}, PlotRange -> {-3, 4}))", //
        "{{-3,4},{-3,4},{-3,4}}");
    check("PlotRange /. Options(ParametricPlot3D({u, v, u*v}, {u, 0, 1}, {v, 0, 1}, "
        + "PlotRange -> {0, 1.5}))", //
        "{{0,1.5},{0,1.5},{0,1.5}}");
    check("PlotRange /. Options(SphericalPlot3D(2, {t, 0, Pi}, {p, 0, 2*Pi}, PlotRange -> 1.25))", //
        "{{-1.25,1.25},{-1.25,1.25},{-1.25,1.25}}");
  }

  @Test
  public void testPlotScalarPlotRangeIsVertical() {
    // a single number is the vertical range; the horizontal one stays the iterator range
    check("Round(PlotRange(Plot(3*x - 2, {x, 0, 4}, PlotRange -> 5)), 0.01)", //
        "{{0.0,4.0},{-5.0,5.0}}");
    check("PlotRange /. Options(Plot(Cos(x), {x, 1, 2}, PlotRange -> 1/4))", //
        "{{1.0,2.0},{-0.25,0.25}}");
    // the curve is cut at that range
    check("Max(Abs(Cases(Plot(3*x - 2, {x, 0, 4}, PlotRange -> 5), Line(p_) :> p[[All, 2]], "
        + "Infinity))) <= 5.0", //
        "True");
    // a graphic keeps a single number for both axes
    check("PlotRange(Graphics(Line({{0, 0}, {7, 1}}), PlotRange -> 3))", //
        "{{-3.0,3.0},{-3.0,3.0}}");
  }

  @Test
  public void testShowTakesThePlotRangeOfTheFirstGraphic() {
    // a later graphic's range is ignored
    check("Round(PlotRange(Show(Graphics(Line({{-2, 1}, {3, 6}})), "
        + "Graphics(Line({{1, -3}, {5, 4}}), PlotRange -> {{0, 1}, {0, 1}}))), 0.01)", //
        "{{-2.0,5.0},{-3.0,6.0}}");
    // the first graphic's own range applies
    check("Round(PlotRange(Show(Graphics(Line({{-2, 1}, {3, 6}}), PlotRange -> {2, All}), "
        + "Graphics(Line({{1, -3}, {5, 4}})))), 0.01)", //
        "{{-2.0,5.0},{2.0,6.0}}");
    // a range given to Show wins over both
    check("Round(PlotRange(Show(Graphics(Line({{-2, 1}, {3, 6}}), PlotRange -> {2, All}), "
        + "Graphics(Line({{1, -3}, {5, 4}})), PlotRange -> {{0, 4}, {-1, 3}})), 0.01)", //
        "{{0.0,4.0},{-1.0,3.0}}");
    // other options of a later graphic still apply where the first has none
    check("Options(Show(Graphics(Line({{0, 0}, {1, 1}})), "
        + "Graphics(Point({0, 1}), Axes -> True, PlotRange -> 9)))", //
        "{Axes->True}");
  }

  @Test
  public void testMeshCurvesAreOneLineEach() {
    // Mesh -> n is n lines in each direction inside the range, each a Line of its own, and the rim
    check("Length(Cases(Plot3D(x*y, {x, 1, 4}, {y, -1, 1}, Mesh -> 3), _Line, Infinity))", //
        "7");
    check("g = Plot3D(x - y, {x, 0, 3}, {y, 0, 3}, Mesh -> 1, BoundaryStyle -> None); "
        + "pts = First(Cases(g, GraphicsComplex(p_, ___) :> p, Infinity)); "
        + "Map(Round(({Min(#), Max(#)} &) /@ Transpose(pts[[First(#), {1, 2}]]), 0.001) &, "
        + "Cases(g, _Line, Infinity))", //
        "{{{1.5,1.5},{0.0,3.0}},{{0.0,3.0},{1.5,1.5}}}");
    check("Length(Cases(Plot3D(x*y, {x, 1, 4}, {y, -1, 1}, Mesh -> {{2, 3}, {0}}), _Line, "
        + "Infinity))", //
        "4");
    // a level of a mesh function which crosses the surface twice is two curves
    check("Length(Cases(Plot3D(x^2 + y^2, {x, -2, 2}, {y, 0, 1}, MeshFunctions -> {#1 &}, "
        + "Mesh -> {{-1, 1}}, BoundaryStyle -> None), _Line, Infinity))", //
        "2");
    // scattered points: levels of x and y across the triangles, and the rim
    check("Length(Cases(ListPlot3D({{0, 0, 1}, {3, 0, 2}, {0, 3, 0}, {3, 3, 1}, {1, 2, 2}}, "
        + "Mesh -> 1), _Line, Infinity))", //
        "3");
    check("Length(Cases(ListPlot3D({{0, 0, 1}, {3, 0, 2}, {0, 3, 0}, {3, 3, 1}, {1, 2, 2}}, "
        + "Mesh -> {{1.5}}, MeshFunctions -> {#1 + #2 &}, BoundaryStyle -> None), _Line, "
        + "Infinity))", //
        "1");
    check("Length(Cases(ListPlot3D({{0, 0, 1}, {3, 0, 2}, {0, 3, 0}, {3, 3, 1}}, "
        + "Mesh -> None), _Line, Infinity))", //
        "1");
    // an array of heights: the same levels of x and y, not the lines of its grid
    check("g = ListPlot3D({{1, 2, 3, 2}, {2, 3, 1, 0}, {0, 1, 2, 3}}, Mesh -> 2, "
        + "BoundaryStyle -> None); pts = First(Cases(g, GraphicsComplex(p_, ___) :> p, "
        + "Infinity)); Map(Round(({Min(#), Max(#)} &) /@ Transpose(pts[[First(#), {1, 2}]]), "
        + "0.001) &, Cases(g, _Line, Infinity))", //
        "{{{2.0,2.0},{1.0,3.0}},{{3.0,3.0},{1.0,3.0}},{{1.0,4.0},{1.667,1.667}},"
            + "{{1.0,4.0},{2.333,2.333}}}");
  }

  @Test
  public void testClickPaneIsItsPicture() {
    // a ClickPane outside an interactive output shows its picture, inert
    check("m=MathMLForm(ClickPane(Graphics(Disk(),ImageSize->40),Print));"
        + "{StringContainsQ(m,\"ClickPane\"),StringContainsQ(m,\"<svg\"),StringContainsQ(m,\"symjaclick\")}", //
        "{False,True,False}");
    // with the position of a kept function it is marked for the clicks
    check("m=MathMLForm(ClickPane(Graphics(Disk(),ImageSize->40),3));"
        + "{StringContainsQ(m,\"data-action=\\\"3\\\"\"),StringContainsQ(m,\"data-frame\")}", //
        "{True,True}");
  }

  @Test
  public void testEventHandlerIsItsContent() {
    // outside an interactive output an EventHandler is its content
    check("StringContainsQ(MathMLForm(EventHandler(\"tap\", {\"MouseClicked\" :> Print(1)})),"
        + "{\"EventHandler\", \"symjaclick\"})", //
        "False");
    // with the positions of kept actions it is marked for the events
    check("m=MathMLForm(EventHandler(Graphics(Disk(),ImageSize->40),{2,-1,5}));"
        + "{StringContainsQ(m,\"data-action=\\\"2\\\"\"),StringContainsQ(m,\"data-drag=\\\"5\\\"\"),"
        + "StringContainsQ(m,\"data-down\")}", //
        "{True,True,False}");
    check("StringContainsQ(MathMLForm(EventHandler(\"tap\",{0,-1,-1})),\"<mrow class=\")", //
        "True");
  }

  @Test
  public void testClickableWrappedPictureAndStackedLegends() {
    // a picture with a legend is clickable too
    check("StringContainsQ(MathMLForm(ClickPane(Legended(Graphics(Disk(),ImageSize->40),"
        + "LineLegend({Red},{\"a\"})),0)),\"symjaclick\")", //
        "True");
    // legends of different kinds are drawn one below the other
    check("s=ExportString(Legended(Graphics(Line({{0,0},{1,1}})),{LineLegend({Red},{\"ln\"}),"
        + "PointLegend({Blue},{\"pt\"})}),\"SVG\");"
        + "{StringContainsQ(s,\">ln<\"),StringContainsQ(s,\">pt<\"),StringCount(s,\"<circle\")}", //
        "{True,True,1}");
  }

  @Test
  public void testPictureCarriesItsDataFrame() {
    // a page puts the points of a Locator on the picture with the frame of its coordinates:
    // x0 + (x - xmin) sx, y0 - (y - ymin) sy
    check("f=ToExpression(StringSplit(First(StringCases(ExportString(Graphics(Line({{-1,-2},{3,2}}),"
        + "PlotRange->{{-1,3},{-2,2}}),\"SVG\"),RegularExpression(\"data-frame=.([^\\\"]+)\"):>\"$1\"))));"
        + "{Length(f), f[[5]], f[[6]], f[[3]]>0, f[[4]]>0}", //
        "{6,-1.0,-2.0,True,True}");
    // a scaled axis has none
    check("StringContainsQ(ExportString(LogPlot(Exp(u),{u,0,2}),\"SVG\"),\"data-frame\")", //
        "False");
  }

  @Test
  public void testWrappedLabelsAreDrawnNotPrinted() {
    // a turned label is written along its direction
    check("s=ExportString(Graphics({Text(Rotate(\"up\",Pi/2),{0,0}),Point({2,1})}),\"SVG\");"
        + "{StringContainsQ(s,\"Rotate\"),StringContainsQ(s,\"rotate(-90\"),StringContainsQ(s,\">up<\")}", //
        "{False,True,True}");
    check("s=ExportString(Plot(u,{u,0,2},Epilog->Inset(Rotate(Style(\"mark\",Blue),Pi/2),{1,1})),"
        + "\"SVG\");{StringContainsQ(s,\"Rotate\"),StringContainsQ(s,\">mark<\")}", //
        "{False,True}");
    // a panel is a box around its text
    check("s=ExportString(Graphics({Inset(Panel(\"box\"),{0,0}),Point({1,1})}),\"SVG\");"
        + "{StringContainsQ(s,\"Panel\"),StringContainsQ(s,\">box<\"),StringCount(s,\"<rect\")>1}", //
        "{False,True,True}");
    // Framed and TextCell plot labels show their text
    check("s=ExportString(Plot(u,{u,0,2},PlotLabel->Framed(\"head\"),AxesLabel->{TextCell(\"tc\"),None}),"
        + "\"SVG\");{StringContainsQ(s,\"Framed\"),StringContainsQ(s,\"TextCell\"),"
        + "StringContainsQ(s,\">head<\"),StringContainsQ(s,\">tc<\")}", //
        "{False,False,True,True}");
    // scripts without script characters, subsuperscripts and square roots
    check("s=ExportString(Graphics({Text(Superscript(q,-w^3),{0,0}),Text(Subsuperscript(z,0,3),{1,0}),"
        + "Text(Sqrt(5),{0,1}),Text(Subscript(m,j+k),{1,1})}),\"SVG\");"
        + "StringCases(s,RegularExpression(\">[^<>]+</text>\"))", //
        "{>q^(-w³)</text>,>z₀³</text>,>√5</text>,>m_(j + k)</text>}");
    // an image inset is no text
    check("s=ExportString(Graphics({Inset(Image({{1,0},{0,1}}),{0,0}),Point({1,1})}),\"SVG\");"
        + "StringContainsQ(s,\"Image(\")", //
        "False");
    // Magnify scales a picture, a formula and a label
    check("m=MathMLForm(Magnify(Graphics(Line({{0,0},{1,2}}),ImageSize->150),3));"
        + "{StringContainsQ(m,\"Magnify\"),StringContainsQ(m,\"width=\\\"450\")}", //
        "{False,True}");
    check("StringContainsQ(MathMLForm(Magnify(a+b^2,2)),\"mathsize=\\\"200%\")", //
        "True");
    check("s=ExportString(Graphics({Text(Magnify(\"wide\",3),{0,0}),Point({1,1})}),\"SVG\");"
        + "{StringContainsQ(s,\"Magnify\"),StringContainsQ(s,\"font-size=\\\"36\")}", //
        "{False,True}");
    // a Null slot of a row shows nothing
    check("StringContainsQ(MathMLForm(Row({\"p\",Null,\"q\"})),\"Null\")", //
        "False");
  }

  @Test
  public void testExactTicksAndTraditionalLabels() {
    // an exact tick position is labelled as it is written
    check("s=ExportString(Plot(Cos(u),{u,-2*Pi,2*Pi},Ticks->{{-2*Pi,-Pi/3,3*Pi/2},{-1,-1/4}}),"
        + "\"SVG\");StringCases(s,RegularExpression(\">[^<>]{1,8}</text>\"))", //
        "{>-2 π</text>,>-π/3</text>,>3 π/2</text>,>-1</text>,>-1/4</text>}");
    // machine numbers keep their decimal labels
    check("s=ExportString(Plot(u,{u,0,1},Ticks->{{0.25,0.5},None}),\"SVG\");"
        + "StringCases(s,RegularExpression(\">0[.][0-9]+</text>\"))", //
        "{>0.25</text>,>0.5</text>}");
    // labels are set in traditional notation
    check("s=ExportString(Graphics({Text(Cosh(w)^3,{0,0}),Text(Sqrt(1+w)/2,{1,0}),"
        + "Text(ArcSin(v)>=g(v),{1,1})},PlotLabel->Gamma(1+k)/Log(k)),\"SVG\");"
        + "StringCases(s,RegularExpression(\">[^<>]+</text>\"))", //
        "{>cosh³(w)</text>,>√(1 + w)/2</text>,>sin⁻¹(v) ≥ g(v)</text>,>Γ(1 + k)/log(k)</text>}");
  }

  @Test
  public void testLegendsOfCombinedAndAutomaticPlots() {
    // Show draws the legends of all the legended plots it combines as one
    check("s=ExportString(Show(Plot(t,{t,0,2},PlotLegends->{\"up\"}),"
        + "Plot(2-t,{t,0,2},PlotStyle->Green,PlotLegends->{\"down\",\"none\"}),"
        + "Plot({t/2,t/3},{t,0,2},PlotLegends->{\"half\",\"third\"})),\"SVG\");"
        + "StringCases(s,RegularExpression(\">(up|down|half|third)<\"))", //
        "{>up<,>down<,>half<,>third<}");
    // Automatic and "Expressions" name every curve by the expression it plots
    check("l=Plot({Cos(t),t^2},{t,0,1},PlotLegends->Automatic)[[2]];{Head(l[[1]]),l[[1,2]],l[[2]]}", //
        "{LineLegend,{Cos(t),t^2},After}");
    check("Plot(Exp(-t),{t,0,1},PlotLegends->Placed(\"Expressions\",Below))[[2]]", //
        "Placed(LineLegend({RGBColor(0.24,0.6,0.8)},{E^(-t)}),Below)");
    check("h={t,1-t};Plot(h,{t,0,1},PlotLegends->Automatic)[[2,1,2]]", //
        "{t,1-t}");
    // a shaded contour plot has a bar of its colours over the range of the values
    check("c=ContourPlot(u-v,{u,0,3},{v,0,1},PlotLegends->Automatic);"
        + "{Head(c), c[[2,1,1,2]]}", //
        "{Legended,{-1.0,3.0}}");
    check("Head(ContourPlot(u^2+v^2==1,{u,-1,1},{v,-1,1},PlotLegends->Automatic))", //
        "Graphics");
  }

  @Test
  public void testCurvePlotPointsAndLabelBlanks() {
    // a space curve takes many more PlotPoints than a surface grid
    check("Length(First(Cases(ParametricPlot3D({s,s^2,1-s},{s,0,1},PlotPoints->600), "
        + "Line(p_):>p, Infinity)))", //
        "600");
    // blanks at the ends of a label or in a row are kept
    check("StringContainsQ(ExportString(Graphics(Text(\" ab\",{0,0})),\"SVG\"),"
        + "\"xml:space=\\\"preserve\")", //
        "True");
    check("StringContainsQ(ExportString(Graphics(Text(\"a b\",{0,0})),\"SVG\"),\"xml:space\")", //
        "False");
    check("StringContainsQ(ExportString(Graphics3D({Text(\"p  q\",{0,0,0})}),\"SVG\"),"
        + "\"xml:space=\\\"preserve\")", //
        "True");
  }

  @Test
  public void testAxesStyleFontSize() {
    // a font size of AxesStyle or FrameStyle sizes the tick labels and the axes or frame labels
    check("Union(StringCases(ExportString(Plot(x, {x, 0, 1}, AxesStyle -> Directive(Blue, 17), "
        + "AxesLabel -> {\"x\", \"y\"}), \"SVG\"), RegularExpression(\"font-size[=:]\\\"?[0-9.]+\")))", //
        "{font-size=\"17,font-size:17}");
    check("Union(StringCases(ExportString(Plot(x, {x, 0, 1}, Frame -> True, FrameStyle -> 15, "
        + "FrameLabel -> {\"p\", \"q\"}), \"SVG\"), RegularExpression(\"font-size[=:]\\\"?[0-9.]+\")))", //
        "{font-size=\"15,font-size:15}");
    check("Union(StringCases(ExportString(Plot(x, {x, 0, 1}, AxesStyle -> Directive(Blue, "
        + "FontSize -> 13), AxesLabel -> {\"x\", \"y\"}), \"SVG\"), "
        + "RegularExpression(\"font-size[=:]\\\"?[0-9.]+\")))", //
        "{font-size=\"13,font-size:13}");
    // LabelStyle names the size of every label
    check("Union(StringCases(ExportString(Plot(x, {x, 0, 1}, AxesStyle -> 17, LabelStyle -> 9, "
        + "AxesLabel -> {\"x\", \"y\"}), \"SVG\"), RegularExpression(\"font-size[=:]\\\"?[0-9.]+\")))", //
        "{font-size=\"9,font-size:9}");
  }
}
