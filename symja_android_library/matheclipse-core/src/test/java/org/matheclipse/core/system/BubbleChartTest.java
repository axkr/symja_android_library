package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** The geometry, order and colours of the bubbles of <code>BubbleChart</code>. */
public class BubbleChartTest extends ExprEvaluatorTestCase {

  @Test
  public void testRadiiFollowTheSpan() {
    // the largest bubble gets a tenth of the span along each axis, the area grows with z
    check("Cases(BubbleChart({{0, 0, 4}, {2, 1, 1}}), Disk(c_, r_) :> {c, r}, Infinity)", //
        "{{{0.0,0.0},{0.2,0.1}},{{2.0,1.0},{0.1,0.05}}}");
    // the plot range just encloses the bubbles, padded by a twentieth of the picture
    check("Cases(BubbleChart({{0, 0, 4}, {2, 1, 1}}), (PlotRange -> r_) :> r, Infinity)", //
        "{{{-0.2,2.1},{-0.1,1.05}}}");
    check("Cases(BubbleChart({{0, 0, 4}, {2, 1, 1}}), (PlotRangePadding -> r_) :> r, Infinity)", //
        "{{{Scaled(0.05),Scaled(0.05)},{Scaled(0.05),Scaled(0.05)}}}");
    // given settings are kept
    check("Cases(BubbleChart({{0, 0, 4}, {2, 1, 1}}, PlotRange -> {{-1, 3}, {-1, 2}}, "
        + "PlotRangePadding -> None), ((PlotRange | PlotRangePadding) -> r_) :> r, Infinity)", //
        "{{{-1,3},{-1,2}},None}");
  }

  @Test
  public void testBubbleSizes() {
    // the diameters run from smin to smax of the span, linearly in Sqrt(z)
    check("Cases(BubbleChart({{0, 0, 1}, {1, 2, 9}, {2, 1, 4}}, BubbleSizes -> {0.1, 0.3}), "
        + "Disk(_, r_) :> r, Infinity)", //
        "{{0.3,0.3},{0.2,0.2},{0.1,0.1}}");
    // equal sizes all get the smallest diameter
    check("Cases(BubbleChart({{1, 1, 5}, {3, 2, 5}}, BubbleSizes -> {0.2, 0.4}), "
        + "Disk(_, r_) :> r, Infinity)", //
        "{{0.2,0.1},{0.2,0.1}}");
    check("Options(BubbleChart, BubbleSizes)", //
        "{BubbleSizes->Automatic}");
  }

  @Test
  public void testZeroSpanAndNonPositiveSizes() {
    // all x coincide: the span is twice their size; z <= 0 is not drawn but counts for the span
    check("Cases(BubbleChart({{2, 0, 1}, {2, 3, -1}}), Disk(c_, r_) :> {c, r}, Infinity)", //
        "{{{2.0,0.0},{0.4,0.3}}}");
    // a single point at the origin measures its bubble in 2
    check("Cases(BubbleChart({{0, 0, 5}}), Disk(c_, r_) :> {c, r}, Infinity)", //
        "{{{0.0,0.0},{0.2,0.2}}}");
    check("Cases(BubbleChart({{1, 2, 0}}), _Disk, Infinity)", //
        "{}");
    // without a bubble the plot range encloses the points
    check("Cases(BubbleChart({{-2, 1, 0}, {4, 5, -3}}), (PlotRange -> r_) :> r, Infinity)", //
        "{{{-2.0,4.0},{1.0,5.0}}}");
    check("Cases(BubbleChart({{3, -1, 0}}), (PlotRange -> r_) :> r, Infinity)", //
        "{{{0.0,6.0},{-2.0,0.0}}}");
  }

  @Test
  public void testColoursAndOrder() {
    // largest first; a list of styles runs over the points in the order they were given
    check("Cases(BubbleChart({{1, 1, 1}, {2, 2, 2}, {3, 3, 3}}, ChartStyle -> {Green, Orange}), "
        + "{Directive(_, c_), Disk(p_, _)} :> {First(p), c === Green}, Infinity)", //
        "{{3.0,True},{2.0,False},{1.0,True}}");
    // several datasets each take a colour of their own
    check("Cases(BubbleChart({{{0, 0, 1}, {1, 1, 2}}, {{2, 2, 3}}}), "
        + "{Directive(_, c_), Disk(p_, _)} :> {p, c}, Infinity)", //
        "{{{2.0,2.0},RGBColor(0.0,0.665,0.95)},{{1.0,1.0},RGBColor(1.0,0.835,0.25)},{{0.0,0.0},RGBColor(1.0,0.835,0.25)}}");
    // the edge, the colour and the base style form one flat directive
    check("Union(Cases(BubbleChart({{1, 1, 2}, {2, 3, 1}}, ChartStyle -> Directive(Blue, Opacity(0.4))), "
        + "Directive(_EdgeForm, s__) :> {s}, Infinity))", //
        "{{RGBColor(0,0,1),Opacity(0.4)}}");
    check("Union(Cases(BubbleChart({{1, 1, 2}, {2, 3, 1}}, ChartBaseStyle -> Opacity(0.6)), "
        + "Directive(_EdgeForm, s__) :> {s}, Infinity))", //
        "{{RGBColor(1.0,0.835,0.25),Opacity(0.6)}}");
    // the bubbles are grouped under a directive in the first colour
    check("BubbleChart({{1, 1, 2}}, ChartStyle -> Purple)[[1]] /. Directive(_EdgeForm, c_) :> c", //
        "{{RGBColor(0.5,0.0,0.5),{{RGBColor(0.5,0.0,0.5),Disk({1.0,1.0},{0.2,0.2})}}}}");
    // a point that isn't drawn still takes its colour from the cycle
    check("Cases(BubbleChart({{3, 1, 5}, {4, 2, 0}, {5, 3, 2}}, ChartStyle -> {Green, Orange}), "
        + "{Directive(_, c_), Disk(p_, _)} :> {First(p), c === Green}, Infinity)", //
        "{{3.0,True},{5.0,True}}");
    // ChartStyle -> None keeps the default colours
    check("Union(Cases(BubbleChart({{1, 1, 2}, {2, 3, 1}}, ChartStyle -> None), "
        + "Directive(_EdgeForm, s__) :> {s}, Infinity))", //
        "{{RGBColor(1.0,0.835,0.25)}}");
    // the plot range encloses the wobbly rims of noise bubbles
    check("With({g = BubbleChart({{0, 0, 1}, {1, 1, 1}}, ChartElementFunction -> \"NoiseBubble\")}, "
        + "Max(Cases(g, Polygon(p_) :> p[[All, 1]], Infinity)) <= "
        + "First(Cases(g, (PlotRange -> r_) :> r[[1, 2]], Infinity)))", //
        "True");
    // a function of the caller's draws anywhere, so the range takes in all of it
    check("Cases(BubbleChart({{0, 0, 1}, {1, 1, 2}}, ChartElementFunction -> (Rectangle(#1[[All, 1]] - 1, "
        + "#1[[All, 2]] + 1) &)), (PlotRange -> r_) :> r, Infinity)", //
        "{All}");
    check("StringContainsQ(ExportString(BubbleChart({{0, 0, 4}, {2, 1, 1}}), \"SVG\"), \"<svg\")", //
        "True");
  }
}
