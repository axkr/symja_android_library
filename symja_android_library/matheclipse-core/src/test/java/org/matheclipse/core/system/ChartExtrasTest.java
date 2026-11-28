package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Tests for CandlestickChart, PieChart3D, BubbleHistogram, Dendrogram and ChartElementData.
 */
public class ChartExtrasTest extends ExprEvaluatorTestCase {

  @Test
  public void testCandlestickChart() {
    // the bodies span open to close, at 1, 2, 3
    check("Cases(CandlestickChart({{5, 7, 4, 6}, {6, 8, 5, 7}, {7, 7.5, 3, 4}}), Rectangle(a_, b_) :> {a, b}, Infinity)", //
        "{{{0.904,5.0},{1.096,6.0}},{{1.904,6.0},{2.096,7.0}},{{2.904,4.0},{3.096,7.0}}}");
    // the wicks run from the body to the low and to the high
    check("First(Cases(CandlestickChart({{5, 7, 4, 6}}), Line(p_) /; Length(p) == 2 && VectorQ(p[[1]]) == False :> p, Infinity))", //
        "{{{1.0,4.0},{1.0,5.0}},{{1.0,7.0},{1.0,6.0}}}");
    // rising candles green, falling ones red
    check("Cases(CandlestickChart({{5, 7, 4, 6}, {7, 7.5, 3, 4}}), {c_RGBColor, _EdgeForm, ___} :> c, Infinity)", //
        "{RGBColor(0.307,0.704,0.058),RGBColor(0.834,0.075,0.053)}");
    check("Cases(CandlestickChart({{5, 7, 4, 6}, {7, 7.5, 3, 4}}, ChartStyle -> {Blue, Orange}), {c_RGBColor, _EdgeForm, ___} :> c, Infinity)", //
        "{RGBColor(0,0,1),RGBColor(1.0,0.5,0.0)}");
    // a list of one colour colours both kinds of candles
    check("Cases(CandlestickChart({{5, 7, 4, 6}}, ChartStyle -> {Red}), _EdgeForm, Infinity)", //
        "{EdgeForm(RGBColor(1,0,0)),EdgeForm(RGBColor(1,0,0))}");
    check("Options(CandlestickChart({{5, 7, 4, 6}}), {Axes, PlotRange})", //
        "{Axes->{False,True},PlotRange->{{0.5,1.5},{3.6895,All}}}");
    // the days of one month label the candles
    check("Cases(CandlestickChart({{{2023, 5, 1}, {5, 7, 4, 6}}, {{2023, 5, 2}, {6, 8, 5, 7}}}), Text(s_, ___) :> s, Infinity)", //
        "{1,2}");
    check("Cases(CandlestickChart({{{2023, 5, 1}, {5, 7, 4, 6}}, {{2023, 7, 2}, {6, 8, 5, 7}}}), Text(s_, ___) :> s, Infinity)", //
        "{May 1,Jul 2}");
    check("CandlestickChart({{5, 7, 4}})", //
        "CandlestickChart({{5,7,4}})");
  }

  @Test
  public void testPieChart3D() {
    check("Options(PieChart3D({2, 3, 5}), {PlotRange, BoxRatios})", //
        "{PlotRange->{{-1.2,1.2},{-1.2,1.2},{0,1.02}},BoxRatios->{1,1,0.1}}");
    check("Options(PieChart3D({2, 3, 5}), {Boxed, ViewPoint})", //
        "{Boxed->False,ViewPoint->{0,-2.4,2}}");
    // the first slice runs clockwise from the left
    check("Round(Min(Cases(PieChart3D({1, 3}), {_, _, 0.0}, Infinity)[[All, 1]]), 0.01)", //
        "-1.0");
    // a second dataset is a ring from 1.5 to 2.5, coloured from the first colour on
    check("Union(Round(Norm /@ Cases(PieChart3D({{2, 3}, {1, 4}})[[1, 3]], {x_, y_, 0.0} :> {x, y}, Infinity), 0.01))", //
        "{1.5,2.5}");
    check("With({c = Cases(PieChart3D({{2, 3}, {1, 4}}), _RGBColor, Infinity)}, c[[1]] == c[[3]])", //
        "True");
    check("PieChart3D({2, x})", //
        "PieChart3D({2,x})");
  }

  @Test
  public void testBubbleHistogram() {
    // cells of width 1 centred on the integers; the radius grows from 0.1 to 0.5 of a cell
    check("Cases(BubbleHistogram({{1, 2}, {1, 2}, {3, 4}, {3, 4}, {3, 4}, {5, 1}}, {1}), Disk(c_, r_) :> {c, r}, Infinity)", //
        "{{{1.0,2.0},{0.366667,0.366667}},{{3.0,4.0},{0.5,0.5}},{{5.0,1.0},{0.233333,0.233333}}}");
    check("Options(BubbleHistogram({{1, 1}, {2, 2}, {2, 2}, {3, 3}}, {1}), PlotRange)", //
        "{PlotRange->{{0.5,3.5},{0.5,3.5}}}");
    // blue for the fewest points, yellow for the most
    check("Cases(BubbleHistogram({{1, 1}, {2, 2}, {2, 2}, {3, 3}, {3, 3}, {3, 3}}, {1}), _RGBColor, Infinity)", //
        "{RGBColor(0.0,0.08,0.697),RGBColor(0.83,0.313,0.372),RGBColor(1.0,0.745,0.044)}");
    // otherwise the cells start at the multiples of the width, a bubble at the mean of its points
    check("{Length(Cases(#, _Disk, Infinity)), Options(#, PlotRange)}&(BubbleHistogram(Table({Sin(k), Cos(2*k)}, {k, 1, 50})))", //
        "{8,{PlotRange->{{-1.0,1.0},{-1.0,1.0}}}}");
    check("Cases(BubbleHistogram({{0.1, 0.1}, {0.3, 0.2}}, {1}), Disk(c_, _) :> c, Infinity)", //
        "{{0.2,0.15}}");
    // ChartStyle gives the colours; it is no option of the picture
    check("Cases(BubbleHistogram({{1, 2}, {3, 4}}, ChartStyle -> {Red, Blue}), _RGBColor, Infinity)", //
        "{RGBColor(1,0,0),RGBColor(0,0,1)}");
    check("Options(BubbleHistogram({{1, 2}, {3, 4}}, ChartStyle -> Red), ChartStyle)", //
        "{}");
    // the options of the picture stay
    check("Options(BubbleHistogram({{1, 2}, {3, 4}}, PlotLabel -> \"t\", ImageSize -> 200), {PlotLabel, ImageSize})", //
        "{PlotLabel->t,ImageSize->200}");
    // cells too small to be numbered
    check("Head(BubbleHistogram({{1, 2}, {3, 4}}, {10^-300}))", //
        "BubbleHistogram");
    check("BubbleHistogram({1, 2, 3})", //
        "BubbleHistogram({1,2,3})");
  }

  @Test
  public void testDendrogram() {
    check("Options(Dendrogram({3, 4, 9}), AspectRatio)", //
        "{AspectRatio->1/Sqrt(2)}");
    // n leaves are joined by n - 1 links
    check("Length(Cases(Dendrogram({3, 4, 9, 10, 20}), _Line, Infinity))", //
        "4");
    // the leaves keep the order of the data where the tree allows it
    check("Cases(Dendrogram({3, 4, 9, 10, 20}), Text(s_, ___) :> s, Infinity)", //
        "{3,4,9,10,20}");
    // the top merge of single linkage is at the largest gap
    check("Max(Flatten(Cases(Dendrogram({3, 4, 9, 10, 20}), Line(p_) :> p[[All, 2]], Infinity)))", //
        "10.0");
    // complete linkage merges at the farthest members
    check("Max(Flatten(Cases(Dendrogram({3, 4, 9, 10, 20}, ClusterDissimilarityFunction -> \"Complete\"), Line(p_) :> p[[All, 2]], Infinity)))", //
        "17.0");
    // vectors with labels
    check("Cases(Dendrogram({{0, 0}, {0, 1}, {8, 8}} -> {\"p\", \"q\", \"r\"}), Text(s_, ___) :> s, Infinity)", //
        "{p,q,r}");
    check("Dendrogram({1, \"a\"})", //
        "Dendrogram({1,a})");
  }

  @Test
  public void testChartElementData() {
    check("ChartElementData(\"DistributionChart\")", //
        "{Density,HistogramDensity,Quantile}");
    check("ChartElementData(BubbleChart)", //
        "{Bubble,NoiseBubble}");
    // the charts
    check("ChartElementData()", //
        "{BarChart,BubbleChart,BubbleHistogram,DistributionChart,Histogram,PieChart3D,SectorChart3D}");
    check("ChartElementData(\"NoChart\")", //
        "ChartElementData(NoChart)");
  }
}
