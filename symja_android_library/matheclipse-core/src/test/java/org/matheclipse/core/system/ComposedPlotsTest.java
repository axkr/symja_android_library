package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Tests for the 2D plots of tier 3.3: StreamDensityPlot, ListStreamPlot, ComplexRegionPlot,
 * ReliefPlot, JuliaSetPlot, DateListLogPlot, StackedListPlot, StackedDateListPlot, ListFitPlot,
 * TernaryListPlot, TimelinePlot and PlotGrid.
 */
public class ComposedPlotsTest extends ExprEvaluatorTestCase {

  @Test
  public void testStreamDensityPlot() {
    check("Attributes(StreamDensityPlot)", //
        "{HoldAll,Protected,ReadProtected}");
    // a raster of the speed under the arrows of the streamlines
    check(
        "Union(Head /@ Cases(StreamDensityPlot({y, -x}, {x, -1, 1}, {y, -1, 1}, PlotPoints -> 10), _Raster | _Arrow, Infinity))", //
        "{Arrow,Raster}");
    check(
        "Head(StreamDensityPlot({{x, y}, x - y}, {x, -1, 1}, {y, -1, 1}, PlotPoints -> 10, StreamPoints -> 5))", //
        "Graphics");
    // a density which cannot be drawn: the streamlines alone
    check(
        "Union(Head /@ Cases(StreamDensityPlot({{y, -x}, {1, 2}}, {x, -1, 1}, {y, -1, 1}), _Raster | _Arrow, Infinity))", //
        "{Arrow}");
    // a field which is no list: the empty frame
    check("Options(StreamDensityPlot(q, {x, -1, 1}, {y, -1, 1}), PlotRange)", //
        "{PlotRange->{{-1.0,1.0},{-1.0,1.0}}}");
    check("Attributes(StreamDensityPlot)", //
        "{HoldAll,Protected,ReadProtected}");
  }

  @Test
  public void testListStreamPlot() {
    check(
        "Head(ListStreamPlot(Flatten(Table({{a, b}, {b, -a}}, {a, -2, 2}, {b, -2, 2}), 1)))", //
        "Graphics");
    check(
        "Length(Cases(ListStreamPlot(Table({-j + 4, i - 4}, {j, 1, 7}, {i, 1, 7})), _Arrow, Infinity)) > 5", //
        "True");
    check(
        "Options(ListStreamPlot(Table({1, 0}, {j, 1, 4}, {i, 1, 5}), DataRange -> {{0, 8}, {0, 3}}), PlotRange)", //
        "{PlotRange->{{0.0,8.0},{0.0,3.0}}}");
    // a DataRange which is no pair of ranges is left out
    check(
        "Options(ListStreamPlot(Table({1, 0}, {j, 1, 4}, {i, 1, 5}), DataRange -> {{0, 8}, y}), PlotRange)", //
        "{PlotRange->{{1.0,5.0},{1.0,4.0}}}");
    // an array of vectors with two columns: its points as pairs lie on a line
    check("Options(ListStreamPlot(Table({1, j}, {j, 1, 6}, {i, 1, 2})), PlotRange)", //
        "{PlotRange->{{1.0,2.0},{1.0,6.0}}}");
    // rows of different lengths are no array
    check("ListStreamPlot({{{1, 0}, {1, 0}}, {{1, 0}, {1, 0}, {1, 0}}})", //
        "ListStreamPlot({{{1,0},{1,0}},{{1,0},{1,0},{1,0}}})");
  }

  @Test
  public void testComplexRegionPlot() {
    check("Head(ComplexRegionPlot(Abs(z - 1) <= 1/2, {z, 0, 2 + I}))", //
        "Graphics");
    // the whole rectangle is shown
    check("Options(ComplexRegionPlot(Abs(z) <= 1, {z, -2 - 2*I, 2 + 2*I}), PlotRange)", //
        "{PlotRange->{{-2.0,2.0},{-2.0,2.0}}}");
    check(
        "Head(ComplexRegionPlot({Re(z^3) > 0, Im(z) < 0}, {z, 3/2}))", //
        "Graphics");
    // the corners must span a rectangle
    check("ComplexRegionPlot(Abs(z) < 1, {z, 1, 2})", //
        "ComplexRegionPlot(Abs(z)<1,{z,1,2})");
  }

  @Test
  public void testReliefPlot() {
    // one RGB cell per entry, over the box of the array
    check("Dimensions(ReliefPlot({{1, 2, 4}, {2, 6, 3}})[[1, 1, 1]])", //
        "{2,3,3}");
    check("ReliefPlot({{1, 2, 4}, {2, 6, 3}})[[1, 1, 2]]", //
        "{{0,0},{3,2}}");
    // without shading the colours are those of the heights alone: equal heights, equal colours
    check(
        "Length(Union(Flatten(ReliefPlot({{1, 5, 1}, {1, 5, 1}}, LightingAngle -> None)[[1, 1, 1]], 1)))", //
        "2");
    check("ReliefPlot({1, 2, 3})", //
        "ReliefPlot({1,2,3})");
  }

  @Test
  public void testJuliaSetPlot() {
    check("Head(JuliaSetPlot(-1/2 + I/3, PlotPoints -> 30))", //
        "Graphics");
    // the picture spans the filled set with a margin: about the unit disk for z^2
    check("Round(JuliaSetPlot(w^2, w, PlotPoints -> 21)[[1, 1, 2]], 0.1)", //
        "{{-1.1,-1.1},{1.1,1.1}}");
    check("Options(JuliaSetPlot(0.25, PlotPoints -> 20), Frame)", //
        "{Frame->True}");
    // z -> z^2 has the unit disk as its filled Julia set: the centre is inside, a corner outside
    check("JuliaSetPlot(w^2, w, PlotPoints -> 21)[[1, 1, 1, 11, 11]]", //
        "{0,0,0}");
    check("NumberQ(JuliaSetPlot(w^2, w, PlotPoints -> 21)[[1, 1, 1, 1, 1]])", //
        "True");
    // only polynomials of degree 2 or more
    check("JuliaSetPlot(Sin(w), w)", //
        "JuliaSetPlot(Sin(w),w)");
    // a tall set: PlotPoints samples along its height, fewer across
    check("Dimensions(JuliaSetPlot(I*w^2 + 2*I, w, PlotPoints -> 40)[[1, 1, 1]])", //
        "{40,5}");
    // a polynomial of degree 1 is drawn too, over the square around its escape radius
    check("JuliaSetPlot(3*w + 1, w, PlotPoints -> 20)[[1, 1, 2]]", //
        "{{-3.0,-3.0},{3.0,3.0}}");
  }

  @Test
  public void testDateListLogPlot() {
    check(
        "Options(DateListLogPlot({{{2001, 1, 1}, 2}, {{2003, 1, 1}, 200}}), Method)", //
        "{Method->{Scaling->{None,Log}}}");
  }

  @Test
  public void testStackedPlots() {
    check(
        "Cases(StackedListPlot({{1, 2, 1}, {3, 1, 2}}), Line(p_) :> p[[All, 2]], Infinity)", //
        "{{1.0,2.0,1.0},{4.0,3.0,3.0}}");
    // each dataset is filled down to the one below
    check("Length(Cases(StackedListPlot({{1, 2, 1}, {3, 1, 2}}), _Polygon, Infinity))", //
        "2");
    check(
        "Cases(StackedDateListPlot({{{{2024, 1, 1}, 1}, {{2024, 2, 1}, 3}}, {{{2024, 1, 1}, 2}, {{2024, 2, 1}, 2}}}), Line(p_) :> p[[All, 2]], Infinity)", //
        "{{1.0,3.0},{3.0,5.0}}");
    // pairs are added where their x values agree
    check(
        "Cases(StackedListPlot({{{1, 1}, {2, 1}, {3, 1}}, {{2, 5}, {3, 5}, {4, 5}}}), Line(p_) :> p[[All, 2]], Infinity)", //
        "{{1.0,1.0,1.0},{6.0,6.0,5.0}}");
    check(
        "Cases(ListLinePlot({{{1, 1}, {2, 3}}, {{2, 1}, {3, 1}}}, PlotLayout -> \"Percentile\"), Line(p_) :> p[[All, 2]], Infinity)", //
        "{{100.0,75.0},{100.0,100.0}}");
  }

  @Test
  public void testListFitPlot() {
    // the points and the fitted line
    check(
        "Union(Head /@ Cases(ListFitPlot({{1, 3}, {2, 5}, {3, 7}}), _Point | _Line, Infinity))", //
        "{Line,Point}");
    // the line runs over the range of the data
    check(
        "Round(MinMax(Flatten(Cases(ListFitPlot({{1, 3}, {2, 5}, {3, 7}}), Line(p_) :> p[[All, 1]], Infinity))), 0.01)", //
        "{1.0,3.0}");
    // the fitted line in the colour of the points
    check("Union(Cases(ListFitPlot({{1, 3}, {2, 5}, {3, 7}}), _RGBColor, Infinity))", //
        "{RGBColor(0.24,0.6,0.8)}");
    // only options follow the data
    check("ListFitPlot({1, 2, 3}, \"Quadratic\")", //
        "ListFitPlot({1,2,3},Quadratic)");
  }

  @Test
  public void testTernaryListPlot() {
    // the centre of the triangle and its corners: a bottom right, b at the top, c bottom left
    check("Cases(TernaryListPlot({{1, 1, 1}, {2, 0, 0}, {0, 2, 0}, {0, 0, 3}}), Point(p_) :> p, Infinity)", //
        "{{{0.5,0.288675},{1.0,0.0},{0.5,0.866025},{0.0,0.0}}}");
    check("Length(Cases(TernaryListPlot({{{1, 2, 3}}, {{3, 2, 1}}}), _Point, Infinity))", //
        "2");
    check("TernaryListPlot({{1, 2}})", //
        "TernaryListPlot({{1,2}})");
  }

  @Test
  public void testTimelinePlot() {
    check(
        "Length(Cases(TimelinePlot({{2001, 1, 1}, {2002, 5, 1}, {2004, 1, 1}}), Point(p_) :> p, Infinity)[[1]])", //
        "3");
    // labels and a span
    check(
        "Cases(TimelinePlot({Labeled({2001, 1, 1}, \"a\"), Labeled(Interval({{2002, 1, 1}, {2003, 1, 1}}), \"b\")}), Text(s_, ___) :> s, Infinity)", //
        "{a,b}");
    // nothing which is an event: the empty frame
    check("First(TimelinePlot(q))", //
        "{}");
    // one date given as a list is one event
    check("Length(Cases(TimelinePlot({2001, 1, 1}), Point(p_) :> p, Infinity)[[1]])", //
        "1");
  }

  @Test
  public void testPlotGrid() {
    check(
        "Head(PlotGrid({{Plot(x, {x, 0, 1}), Plot(x^2, {x, 0, 2})}, {Plot(-x, {x, 0, 1}), Plot(1, {x, 0, 2})}}))", //
        "GraphicsGrid");
    // the plots of a column share the horizontal range
    check(
        "Options(PlotGrid({{Plot(x, {x, 0, 1})}, {Plot(x, {x, -1, 3})}})[[1, 1, 1]], PlotRange)[[1, 2, 1]]", //
        "{-1.0,3.0}");
    // the inner edges carry ticks without labels
    check(
        "Union(Cases(Options(PlotGrid({{Plot(x, {x, 0, 1}), Plot(x, {x, 0, 1})}})[[1, 1, 2]], FrameTicks), {_, s_String} :> s, Infinity))", //
        "{}");
    check(
        "Options(PlotGrid({{Plot(x, {x, 0, 1}), Plot(x, {x, 0, 1})}})[[1, 1, 1]], FrameTicks)", //
        "{FrameTicks->{{Automatic,None},{Automatic,None}}}");    // a plot with a legend keeps it
    check("Head(PlotGrid({{Plot(x, {x, 0, 1}, PlotLegends -> {\"a\"})}})[[1, 1, 1]])", //
        "Legended");
    check(
        "Options(PlotGrid({{Plot(x, {x, 0, 1}, PlotLegends -> {\"a\"})}})[[1, 1, 1, 1]], Frame)", //
        "{Frame->True}");
  }
}
