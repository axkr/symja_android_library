package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** The range <code>PlotRange(graphics)</code> reports for function plots and their combinations. */
public class PlotRangeTest extends ExprEvaluatorTestCase {

  @Test
  public void testDefaultXRangeIsTheIteratorRange() {
    // the curve has no values left of 0, the picture still spans the whole iterator range
    check("PlotRange(Plot(Log(x), {x, -2, 3}))[[1]]", //
        "{-2.0,3.0}");
    // an explicit Automatic spans the sampled points, reaching to 0 when close to it
    check("PlotRange(Plot(Log(x), {x, -2, 3}, PlotRange -> Automatic))[[1]]", //
        "{0.0,3.0}");
    // a range given for y only keeps the iterator range as machine numbers
    check("PlotRange(Plot(x, {x, 3, 7}, PlotRange -> {-1, 20}))", //
        "{{3.0,7.0},{-1.0,20.0}}");
    check("PlotRange(Plot(x, {x, 0.25, 4}, PlotRange -> {-1, 20}))[[1, 1]]", //
        "0.25");
  }

  @Test
  public void testAllSpansTheSampledPoints() {
    check("PlotRange(Plot(x^2, {x, 1, 3}, PlotRange -> All))", //
        "{{1.0,3.0},{1.0,9.0}}");
    check("Map(# > 1&, Flatten(PlotRange(Plot(x^2, {x, 1, 3}, PlotRange -> All))[[All, 1]]))", //
        "{True,True}");
  }

  @Test
  public void testYRangeReachesToZero() {
    check("PlotRange(Plot(0.05 + 2*x, {x, 0, 2}))[[2, 1]]", //
        "0.0");
    check("PlotRange(Plot(-0.05 - 2*x, {x, 0, 2}))[[2, 2]]", //
        "0.0");
    check("PlotRange(Plot(0.5 + x, {x, 0, 2}))[[2, 1]] > 0.5", //
        "True");
  }

  @Test
  public void testConstantCurve() {
    check("PlotRange(Plot(2.5, {x, -1, 1}))[[2]]", //
        "{0.0,5.0}");
    check("PlotRange(Plot(-3, {x, -1, 1}))[[2]]", //
        "{-6.0,0.0}");
    check("PlotRange(Plot(0.0, {x, -1, 1}))[[2]]", //
        "{-1.0,1.0}");
  }

  @Test
  public void testEndSamplesInsideTheRange() {
    // the last sample lies just before the jump at the end of the range
    check("PlotRange(Plot(Floor(2*x), {x, 0, 2}, Exclusions -> None))", //
        "{{0.0,2.0},{0.0,3.0}}");
    check("Reap(Plot(x^2, {x, 1, 2}, PlotPoints -> 5, EvaluationMonitor :> Sow(x)))[[2, 1]]", //
        "{1.0,1.25,1.5,1.75,2.0}");
    check("Reap(Plot(x^2, {x, 1, 2}, PlotPoints -> 5, EvaluationMonitor :> Sow(x)))[[2, 1, {1, 5}]]"
        + " - {1, 2}", //
        "{2.5*10^-7,-2.5*10^-7}");
  }

  @Test
  public void testShowKeepsTheRangeOfTheFirstPlot() {
    check("PlotRange(Show(Plot(Cos(x), {x, 0, 2}), Plot(5*Cos(x), {x, 0, 8})))", //
        "{{0.0,2.0},{-0.416147,1.0}}");
    // unless the first plot asked for All
    check("Round(PlotRange(Show(Plot(Cos(x), {x, 0, 2}, PlotRange -> All), "
        + "Plot(5*Cos(x), {x, 0, 8}))))", //
        "{{0,8},{-5,5}}");
    // a plain graphic first spans everything shown
    check("Round(PlotRange(Show(Graphics(Point({{0, 0}, {1, 1}})), Plot(5*Cos(x), {x, 0, 8}))))", //
        "{{0,8},{-5,5}}");
  }

  @Test
  public void testPlotWritesItsRange() {
    // the iterator bounds as they were given, and the automatic y range
    check("PlotRange /. Rest(List @@ Plot(Cos(x), {x, 0, 2}))", //
        "{{0,2},{-0.416147,1.0}}");
    check("PlotRange /. Rest(List @@ Plot(x, {x, 3, 7}, PlotRange -> {-1, 20}))", //
        "{{3.0,7.0},{-1.0,20.0}}");
    // All is kept, and spans whatever a combined picture shows
    check("PlotRange /. Rest(List @@ Plot(Cos(x), {x, 0, 2}, PlotRange -> All))", //
        "{All,All}");
  }

  @Test
  public void testListPlotRange() {
    // values start at x = 0; y goes out by an 18th of the span when it stays far from 0
    check("PlotRange(ListPlot({3, 4, 5}))", //
        "{{0.0,3.0},{2.88889,5.0}}");
    check("PlotRange(ListLinePlot({7, 8}))", //
        "{{0.0,2.0},{6.94444,8.0}}");
    // points: x goes out by a 48th of the span, y reaches 0 within half of the far end
    check("PlotRange(ListPlot({{2, 1}, {4, 8}}))", //
        "{{1.95833,4.0},{0.0,8.0}}");
    check("PlotRange /. Rest(List @@ ListPlot({{2, 1}, {4, 8}}))", //
        "{{1.95833,4.0},{0,8.0}}");
    // All moves x out without reaching 0; negative values far from 0 keep their range
    check("PlotRange(ListPlot({-6, -5, -4}, PlotRange -> All))", //
        "{{0.958333,3.0},{-6.0,-4.0}}");
    // DataRange -> All reads each list as values
    check("PlotRange(ListPlot({{1, 5}, {2, 7}}, DataRange -> All))", //
        "{{0.0,2.0},{0.0,7.0}}");
  }
}
