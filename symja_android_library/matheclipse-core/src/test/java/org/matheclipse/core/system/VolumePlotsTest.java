package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Tests for DensityPlot3D, SliceDensityPlot3D, SliceContourPlot3D and ListSurfacePlot3D.
 */
public class VolumePlotsTest extends ExprEvaluatorTestCase {

  @Test
  public void testDensityPlot3D() {
    check("Attributes(DensityPlot3D)", //
        "{HoldAll,Protected,ReadProtected}");
    check("Head(DensityPlot3D(x*y - z, {x, -1, 1}, {y, -1, 1}, {z, -1, 1}))", //
        "Graphics3D");
    // a Raster3D of n x n x n values
    check(
        "Dimensions(Cases(DensityPlot3D(x - y + z, {x, 0, 2}, {y, 0, 2}, {z, 0, 2}, PlotPoints -> 3), Raster3D(a_, ___) :> a, Infinity)[[1]])", //
        "{3,3,3}");
    // sampled on the faces and in the middle, scaled into 0 ... 1
    check(
        "Cases(DensityPlot3D(z^2, {x, 0, 1}, {y, 0, 1}, {z, 0, 2}, PlotPoints -> 3), Raster3D(a_, ___) :> a[[All, 1, 1]], Infinity)", //
        "{{0.0,0.25,1.0}}");
    check(
        "Cases(DensityPlot3D(y, {x, 0, 1}, {y, 0, 1}, {z, 0, 1}, PlotPoints -> 4), Raster3D(_, b_, ___) :> b, Infinity)", //
        "{{{0.0,0.0,0.0},{1.0,1.0,1.0}}}");
    // the colour function gives each value its colour and opacity: a number for every cell
    check(
        "Cases(DensityPlot3D(z, {x, 0, 1}, {y, 0, 1}, {z, 0, 1}, PlotPoints -> 2, OpacityFunction -> 0.3), (ColorFunction -> f_) :> f(0.5)[[1]], Infinity)", //
        "{0.3}");
    // by default the most frequent values are nearly clear and the rest half opaque: here 4 of
    // the 5 layers hold one value, 80% of the cells
    check(
        "Cases(DensityPlot3D(If(z < 0.95, 0, z), {x, 0, 1}, {y, 0, 1}, {z, 0, 1}, PlotPoints -> 5), (ColorFunction -> f_) :> {f(0)[[1]], f(1)[[1]]}, Infinity)", //
        "{{0.01,0.5}}");
    // the variable is local to the plot
    check("x = 4; Head(DensityPlot3D(x^2 - y, {x, 0, 1}, {y, 0, 1}, {z, 0, 1}, PlotPoints -> 2))", //
        "Graphics3D");
    check("Clear(x); DensityPlot3D(x, {x, 0, 1}, {y, 0, 1})", //
        "DensityPlot3D(x,{x,0,1},{y,0,1})");
  }

  @Test
  public void testSliceDensityPlot3D() {
    check(
        "Head(SliceDensityPlot3D(x - 2*y*z, \"CenterPlanes\", {x, -1, 1}, {y, -1, 1}, {z, -1, 1}))", //
        "Graphics3D");
    // one plane of 3 x 3 samples
    check(
        "Length(SliceDensityPlot3D(x + 2*y, z == 0.25, {x, 0, 1}, {y, 0, 1}, {z, 0, 1}, PlotPoints -> 3)[[1, 1]])", //
        "9");
    check(
        "Union(SliceDensityPlot3D(x + 2*y, z == 0.25, {x, 0, 1}, {y, 0, 1}, {z, 0, 1}, PlotPoints -> 3)[[1, 1, All, 3]])", //
        "{0.25}");
    // three stacked planes
    check(
        "Union(SliceDensityPlot3D(x, {\"ZStackedPlanes\", 3}, {x, 0, 1}, {y, 0, 1}, {z, 0, 4}, PlotPoints -> 2)[[1, 1, All, 3]])", //
        "{1.0,2.0,3.0}");
    check(
        "Union(SliceDensityPlot3D(x, {\"YStackedPlanes\", {0.5, 1.5}}, {x, 0, 1}, {y, 0, 2}, {z, 0, 1}, PlotPoints -> 2)[[1, 1, All, 2]])", //
        "{0.5,1.5}");
    // the back planes are the faces at xmin, ymax and zmin
    check(
        "Length(SliceDensityPlot3D(z, \"BackPlanes\", {x, 0, 1}, {y, 0, 1}, {z, 0, 1}, PlotPoints -> 2)[[1, 1]])", //
        "7");
    // curved surfaces through marching cubes
    check(
        "Max(Abs(Norm /@ SliceDensityPlot3D(x, Sphere({0, 0, 0}, 1/2), {x, -1, 1}, {y, -1, 1}, {z, -1, 1}, PlotPoints -> 12)[[1, 1]] - 0.5)) < 0.05", //
        "True");
    check(
        "Head(SliceDensityPlot3D(z, x^2 + y^2 == 1/4, {x, -1, 1}, {y, -1, 1}, {z, -1, 1}, PlotPoints -> 8))", //
        "Graphics3D");
    // the vertices carry the colours
    check(
        "Cases(SliceDensityPlot3D(x, z == 0, {x, 0, 1}, {y, 0, 1}, {z, -1, 1}, PlotPoints -> 4), (VertexColors -> c_) :> Length(c), Infinity)", //
        "{16}");
    check(
        "SliceDensityPlot3D(x, \"Diagonal\", {x, -1, 1}, {y, -1, 1}, {z, -1, 1})", //
        "SliceDensityPlot3D(x,Diagonal,{x,-1,1},{y,-1,1},{z,-1,1})");
  }

  @Test
  public void testSliceContourPlot3D() {
    check(
        "Head(SliceContourPlot3D(x*y + z, \"CenterPlanes\", {x, -1, 1}, {y, -1, 1}, {z, -1, 1}, Contours -> 4))", //
        "Graphics3D");
    // three levels make four bands
    check(
        "Length(Cases(SliceContourPlot3D(x, z == 0, {x, 0, 1}, {y, 0, 1}, {z, -1, 1}, Contours -> {0.25, 0.5, 0.75}), _Polygon, Infinity))", //
        "4");
    // one contour line per level, on the level, and the rim of the slice
    check(
        "Union(Round(Flatten(Cases(SliceContourPlot3D(x, z == 0, {x, 0, 1}, {y, 0, 1}, {z, -1, 1}, Contours -> {0.25, 0.75}, PlotPoints -> 5), Line(s_) /; Depth(s) == 4 :> s[[All, All, 1]], Infinity)), 0.001))", //
        "{0.25,0.75}");
    check(
        "Length(Cases(SliceContourPlot3D(x, z == 0, {x, 0, 1}, {y, 0, 1}, {z, -1, 1}, Contours -> {0.25, 0.5, 0.75}), _Line, Infinity))", //
        "4");
    check(
        "Length(Cases(SliceContourPlot3D(x, z == 0, {x, 0, 1}, {y, 0, 1}, {z, -1, 1}, Contours -> 3, ContourStyle -> None), _Line, Infinity))", //
        "1");
  }

  @Test
  public void testListSurfacePlot3D() {
    // an array of points is joined cell by cell
    check(
        "Head(ListSurfacePlot3D(Table({i, j, i*j}, {i, 0, 3}, {j, 0, 2})))", //
        "Graphics3D");
    check(
        "Length(First(ListSurfacePlot3D(Table({i, j, i - j}, {i, 0, 3}, {j, 0, 2})))[[1]])", //
        "12");
    // scattered points are wrapped in their convex hull: a tetrahedron has 4 corners
    check(
        "Length(First(ListSurfacePlot3D({{0, 0, 0}, {2, 0, 0}, {0, 3, 0}, {0, 0, 1}, {0.1, 0.1, 0.1}}))[[1]])", //
        "4");
    // a point which is no finite number is left out of the hull
    check("Length(Cases(ListSurfacePlot3D({{0,0,0},{1,0,0},{0,1,0},{0,0,1},{1,1,Indeterminate}}), Polygon(p_) :> p, Infinity)[[1]])", //
        "4");
    check("ListSurfacePlot3D({{1, 2, 3}})", //
        "ListSurfacePlot3D({{1,2,3}})");
    // points on a line enclose nothing
    check("First(ListSurfacePlot3D({{1, 2, 3}, {2, 3, 4}, {3, 4, 5}}))", //
        "{}");
  }
}
