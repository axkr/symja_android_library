package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** Charts, gauges, filters and plots. */
public class ChartGaugeFilterTest extends ExprEvaluatorTestCase {

  @Test
  public void testBulletGauge() {
    check("Head(BulletGauge(4, 6, {0, 20}))", //
        "Graphics");
    // the bands between the fractions of the inner ranges
    check("Cases(BulletGauge(2, 3, {0, 5, 8, 10}), {Directive(_), "
        + "Rectangle({a_, 0}, {b_, 0.1}, ___)} :> {a, b}, Infinity)", //
        "{{0,1/2},{1/2,4/5},{4/5,1}}");
    // the bars, longest first, as exact fractions of the range; a value beyond the range is
    // clipped to it
    check("Cases(BulletGauge({2, 6, 25}, 4, {0, 20}), "
        + "Tooltip({_, Rectangle(_, {_, h_}, ___)}, v_) :> {h, v}, Infinity)", //
        "{{1.0,20.0},{3/10,6},{1/10,2}}");
    // the marker of a reference sits at its fraction of the range
    check("Cases(BulletGauge(4, {5, 15}, {0, 20}), "
        + "GeometricTransformation(_, {_, t_}) :> t, Infinity)", //
        "{{0.25,0.05},{0.75,0.05}}");
    // ticks at the multiples of a step of 1, 2 or 5 times a power of ten
    check("Cases(BulletGauge(4, 6, {3, 17}), Text(t_, ___) :> t, Infinity)", //
        "{5,10,15}");
    check("Cases(BulletGauge(1, 2, {0, 3}, ScaleDivisions -> 3), Text(t_, ___) :> t, Infinity)", //
        "{0,1,2,3}");
    check("BulletGauge(a, 2, {0, 3})", //
        "BulletGauge(a,2,{0,3})");
  }

  @Test
  public void testDistributionChart() {
    check("g = DistributionChart({{2, 3, 3, 4, 7}, {1, 1, 2}, {5, 6, 6, 6}}); "
        + "{Head(g), Length(Cases(g, _Polygon, Infinity))}", //
        "{Graphics,3}");
    check("Head(DistributionChart({{1, 2, 4}, {2, 2, 5}}, ChartLegends -> {\"x\", \"y\"}))", //
        "Legended");
  }

  @Test
  public void testRootLocusPlot() {
    // 1 + k/(s (s + 2)) == 0 has the roots -1 +- I*Sqrt(k - 1) for k > 1
    check("g = RootLocusPlot(k/(s*(s + 2)), {k, 0, 5}); "
        + "{Head(g), Sort(Round(Last /@ Cases(g, Line(p_) :> p, Infinity), 0.001))}", //
        "{Graphics,{{-1.0,-2.0},{-1.0,2.0}}}");
    check("Round(Sort(First /@ Cases(RootLocusPlot(k/(s*(s + 2)), {k, 0, 5}), "
        + "Line(p_) :> p, Infinity)), 0.001)", //
        "{{-2.0,0.0},{0.0,0.0}}");
  }

  @Test
  public void testComplexListPlot() {
    check("Cases(ComplexListPlot({2*I, 3, 1 - I}), Point(p_) :> p, Infinity)", //
        "{{{0.0,2.0},{3.0,0.0},{1.0,-1.0}}}");
    check("Cases(ComplexListPlot(<|\"p\" -> 2 + I, \"q\" -> -1|>), Point(p_) :> p, Infinity)", //
        "{{{2.0,1.0},{-1.0,0.0}}}");
    check("Head(ComplexListPlot(Tooltip({I, 2})))", //
        "Graphics");
  }

  @Test
  public void testRectangleChartData() {
    check("RectangleChart(<|\"u\" -> {2, 3}, \"v\" -> {1, 1}|>) "
        + "=== RectangleChart({{2, 3}, {1, 1}})", //
        "True");
    check("RectangleChart({{Quantity(2, \"Meters\"), Quantity(5, \"Seconds\")}}) "
        + "=== RectangleChart({{2, 5}})", //
        "True");
    check("Head(RectangleChart3D(<|\"u\" -> {1, 2, 2}|>))", //
        "Graphics3D");
  }

  @Test
  public void testDiscretePlot3DMarkers() {
    // the markers of one function are a single point primitive
    check("Count(DiscretePlot3D({i*j, i + j, i - j}, {i, 3}, {j, 2}), _Point, Infinity)", //
        "3");
    check("Length(First(Cases(DiscretePlot3D(i + j, {i, 4}, {j, 3}), "
        + "Point(p_) :> p, Infinity)))", //
        "12");
    check("Count(DiscretePlot3D(i*j, {i, 3}, {j, 3}, Filling -> None), _Line, Infinity)", //
        "0");
    check("Cases(DiscretePlot3D(j, {i, 1, 1}, {j, 1, 3}, ExtentSize -> Left), "
        + "_Cuboid, Infinity)[[1]]", //
        "Cuboid({0.0,0.0,1.0},{1.0,1.0,0.0})");
  }

  @Test
  public void testDiscretePlot3DBarsFillingLevel() {
    // a zero value on the level zero has no bar: i*j is zero in five of nine samples
    check("Count(DiscretePlot3D(i*j, {i, 0, 2}, {j, 0, 2}, ExtentSize -> Full), _Cuboid, Infinity)", //
        "4");
    // Bottom and Top are the lowest and highest value; a value on a nonzero level keeps its flat bar
    check("Table(Count(DiscretePlot3D(i*j, {i, -1, 1}, {j, -1, 1}, ExtentSize -> Full, "
        + "Filling -> f), _Cuboid, Infinity), {f, {Automatic, Axis, Bottom, Top, 2, 0, None, False}})", //
        "{4,4,9,9,9,4,0,0}");
    // the lowest value is zero, so Bottom is the level zero
    check("Count(DiscretePlot3D(i*j, {i, 0, 2}, {j, 0, 2}, ExtentSize -> Full, Filling -> Bottom), "
        + "_Cuboid, Infinity)", //
        "4");
    // the first corner is at the value, the second on the filling level
    check("Cases(DiscretePlot3D(3, {i, 1, 1}, {j, 1, 1}, ExtentSize -> 1, Filling -> 1), "
        + "_Cuboid, Infinity)", //
        "{Cuboid({0.5,0.5,3.0},{1.5,1.5,1.0})}");
    check("Cases(DiscretePlot3D(3, {i, 1, 1}, {j, 1, 1}, ExtentSize -> 1, Filling -> Top), "
        + "_Cuboid, Infinity)", //
        "{Cuboid({0.5,0.5,3.0},{1.5,1.5,3.0})}");
    // the bars of several functions share the level of all their values
    check("Cases(DiscretePlot3D({i, i + 4}, {i, 1, 1}, {j, 1, 1}, ExtentSize -> 1, "
        + "Filling -> Top), _Cuboid, Infinity)", //
        "{Cuboid({0.5,0.5,1.0},{1.5,1.5,5.0}),Cuboid({0.5,0.5,5.0},{1.5,1.5,5.0})}");
    // a stem reaches from its value down to the filling level; the stems which reach up to the
    // level and those above it are two lines
    check("Cases(DiscretePlot3D(i + 1, {i, 2, 2}, {j, 0, 0}, Filling -> 1), _Line, Infinity)", //
        "{Line({},VertexColors->None),Line({{{2.0,0.0,3.0},{2.0,0.0,1.0}}},VertexColors->None)}");
    check("Cases(DiscretePlot3D(2*i, {i, -1, 1}, {j, 0, 0}, Filling -> Bottom), _Line, Infinity)", //
        "{Line({{{-1.0,0.0,-2.0},{-1.0,0.0,-2.0}}},VertexColors->None),"
            + "Line({{{0.0,0.0,0.0},{0.0,0.0,-2.0}},{{1.0,0.0,2.0},{1.0,0.0,-2.0}}},VertexColors->None)}");
  }

  @Test
  public void testGaborMatrix() {
    check("Dimensions /@ {GaborMatrix(0.6, {1, 0}), GaborMatrix(3.5, {0, 1}), "
        + "GaborMatrix({2, 1}, {1})}", //
        "{{3,3},{9,9},{5}}");
    // the Gaussian part sums to 1, so a vanishing wave vector gives a kernel of sum 1
    check("Chop(Total(Flatten(GaborMatrix(2, {0, 0}))) - 1)", //
        "0");
    // the phase Pi/2 makes the kernel odd along the wave vector
    check("Chop(Total(GaborMatrix(1.5, {2}, Pi/2)))", //
        "0");
  }

  @Test
  public void testTotalVariationFilter() {
    // the result scales with the data
    check("v = {0.2, 0.0, 0.9, 1.0, 0.1, 0.8}; "
        + "Max(Abs(TotalVariationFilter(7*v, 0.3)/7 - TotalVariationFilter(v, 0.3))) < 10^-12", //
        "True");
    // no regularization gives the data back
    check("TotalVariationFilter({3.0, 1.0, 2.0}, 0)", //
        "{3.0,1.0,2.0}");
    // a constant signal stays constant
    check("Max(Abs(TotalVariationFilter({{2., 2.}, {2., 2.}}, 0.5) - 2.)) < 10^-12", //
        "True");
    // the smoothing pulls an outlier towards its neighbours but keeps the mean
    check("w = TotalVariationFilter({0., 0., 1., 0., 0.}, 0.2); "
        + "{w[[3]] < 1, Abs(Total(w) - 1) < 10^-9}", //
        "{True,True}");
  }

  @Test
  public void testAudioLength() {
    check("AudioLength(Audio({0.1, -0.2, 0.3, 0.0, 0.5}))", //
        "5");
    check("AudioLength(Audio({{0.1, 0.2, 0.3}, {0.0, 0.0, 0.1}}))", //
        "3");
  }

  @Test
  public void testColorDataName() {
    check("ColorData(\"SunsetColors\", \"Name\")", //
        "sunset colors");
  }
}
