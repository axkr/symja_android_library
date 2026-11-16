package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * How often and where the plots sample their functions, and how a few options shape the finished
 * picture: contour surfaces of functions which only take numbers, the sample grid of a contour plot
 * without refinement, the refinement of a parametric curve in a stretched frame, the value range of
 * an array plot, automatic frame labels, the rim of a contour surface and the layout of an array of
 * graphics.
 */
public class PlotSamplingAndLayoutTest extends ExprEvaluatorTestCase {

  @Test
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
}
