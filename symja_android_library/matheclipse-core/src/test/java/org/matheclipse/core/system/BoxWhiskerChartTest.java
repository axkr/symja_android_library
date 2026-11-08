package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** The structure of the framed <code>Graphics</code> a box and whisker chart returns. */
public class BoxWhiskerChartTest extends ExprEvaluatorTestCase {

  @Test
  public void testBoxesArePolygonsInAFrame() {
    check("g = BoxWhiskerChart({{3,5,7,9},{2,4,4,8,10}}); "
        + "{Length(Cases(g,_Polygon,Infinity)), Frame/.Options(g), Axes/.Options(g)}", //
        "{2,True,{False,False}}");
    // an association charts its values, its keys label the boxes
    check("(FrameTicks /. Options(BoxWhiskerChart(<|\"p1\" -> {1,2,3}, \"q1\" -> {4,5,7}|>)))"
        + "[[2, 1, All, 2]]", //
        "{p1,q1}");
    check("(FrameTicks /. Options(BoxWhiskerChart({{1,2,3},{4,5,6}}, "
        + "ChartLabels -> {\"u1\", \"v1\"})))[[2, 1, All, 2]]", //
        "{u1,v1}");
  }

  @Test
  public void testQuartilesWhiskersAndMedian() {
    // the quartiles interpolate at positions (n+2)/4, (n+1)/2 and (3n+2)/4
    check("g = BoxWhiskerChart({2,4,6,8,10,12}); "
        + "{MinMax(Cases(g, Polygon(q_) :> q[[All, 2]], Infinity)), "
        + "MinMax(Cases(g, {GrayLevel(0), CapForm(\"Butt\"), _, Line(l_)} :> l[[All, All, 2]], "
        + "Infinity)), Cases(g, {GrayLevel(0), GrayLevel(1), Line(l_)} :> l[[1, 2]], Infinity)}", //
        "{{4,10},{2,12},{7}}");
    // "Basic" has no fences: the median and the whiskers are the only lines
    check("Length(Cases(BoxWhiskerChart({1,2,3,4}, \"Basic\"), Line(_), Infinity))", //
        "2");
  }

  @Test
  public void testMarks() {
    // whiskers stop at the last value within 1.5 IQR; values beyond 3 IQR are gray
    check("g = BoxWhiskerChart({1,2,3,4,5,6,7,30,60}, \"Outliers\"); "
        + "{Cases(g, Inset(Style(_, 10, GrayLevel(l_)), {_, y_}) :> {l, y}, Infinity), "
        + "MinMax(Cases(g, {GrayLevel(0), CapForm(\"Butt\"), _, Line(l_)} :> l[[All, All, 2]], "
        + "Infinity))}", //
        "{{{0,30},{0.5,60}},{1,51/4}}");
    check("Cases(BoxWhiskerChart({1,2,6}, \"Mean\"), "
        + "{GrayLevel(0), GrayLevel(0), Line({{_, y_}, {_, y_}})} :> y, Infinity)", //
        "{3}");
    check("Length /@ Cases(BoxWhiskerChart({1,3,4,7,9,10,12}, \"Notched\"), "
        + "Polygon(q_) :> q, Infinity)", //
        "{10}");
  }

  @Test
  public void testBarOrigin() {
    // growing from the right negates the values
    check("MinMax(Cases(BoxWhiskerChart({1,3,4,7,9}, BarOrigin -> Right), "
        + "Polygon(q_) :> q[[All, 1]], Infinity))", //
        "{-15/2,-5/2}");
    check("Cases((FrameTicks /. Options(BoxWhiskerChart({1,3,4,7,9}, BarOrigin -> Top)))[[1, 1]], "
        + "{-4.0, l_, _} :> l)", //
        "{4}");
  }

  @Test
  public void testDatasetsShareTheFirstColour() {
    check("Union(Cases(BoxWhiskerChart({{3, 5, 8}, {1, 9, 12}, {2, 4, 6}}), _RGBColor, Infinity))", //
        "{RGBColor(1.0,0.835,0.25)}");
  }

  @Test
  public void testLegendSwatchesAreTheBoxColours() {
    // a legend leaves the boxes in their shared colour, and each swatch repeats it
    check("Cases(BoxWhiskerChart({{2, 4, 5}, {1, 6, 9}}, ChartLegends -> {\"p\", \"q\"}), "
        + "_SwatchLegend, Infinity)", //
        "{SwatchLegend({RGBColor(1.0,0.835,0.25),RGBColor(1.0,0.835,0.25)},{p,q})}");
  }

  @Test
  public void testExactDataKeepsExactGeometry() {
    // the quartiles of exact data are exact, interpolated at the positions 9/4 and 23/4
    check("Cases(BoxWhiskerChart({5/2, 1/4, 8, 1/2, 7, 2, 11/3}), Polygon(q_) :> q[[All, 2]], Infinity)", //
        "{{37/6,37/6,7/8,7/8}}");
    check("g = BoxWhiskerChart({5/2, 1/4, 8, 1/2, 7, 2, 11/3}, \"Mean\"); "
        + "{Cases(g, {GrayLevel(0), GrayLevel(1), Line(l_)} :> l[[1, 2]], Infinity), "
        + "Cases(g, {GrayLevel(0), GrayLevel(0), Line(l_)} :> l[[1, 2]], Infinity), "
        + "Cases(g, {GrayLevel(0), CapForm(\"Butt\"), _, Line(l_)} :> l[[All, 2, 2]], Infinity)}", //
        "{{5/2},{41/12},{{1/4,8}}}");
    // a box growing from the top negates the exact values
    check("Cases(BoxWhiskerChart({5/2, 1/4, 8, 1/2, 7, 2, 11/3}, BarOrigin -> Top), "
        + "Polygon(q_) :> q[[All, 2]], Infinity)", //
        "{{-37/6,-37/6,-7/8,-7/8}}");
    // machine data, or exact data mixed with machine numbers, gives machine numbers
    check("Cases(BoxWhiskerChart({5/2, 1/4, 8, 0.5, 7, 2, 11/3}), Polygon(q_) :> q[[All, 2]], Infinity)", //
        "{{6.16667,6.16667,0.875,0.875}}");
  }

  @Test
  public void testOutliersAreInsetMarkers() {
    // values beyond 1.5 IQR are black markers, beyond 3 IQR gray ones, each at its exact value
    check("g = BoxWhiskerChart({2, 3, 3, 4, 4, 4, 5, 5, 6, 11, 22, 1/2}, \"Outliers\"); "
        + "{Cases(g, Inset(Style(_, 10, GrayLevel(l_)), {_, y_}) :> {l, y}, Infinity), "
        + "Cases(g, {GrayLevel(0), CapForm(\"Butt\"), _, Line(l_)} :> l[[All, 2, 2]], Infinity)}", //
        "{{{0,11},{0.5,22}},{{1/2,6}}}");
  }
}
