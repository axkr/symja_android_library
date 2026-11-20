package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** The column elements of a distribution chart. */
public class DistributionChartTest extends ExprEvaluatorTestCase {

  @Test
  public void testHistogramDensity() {
    // sideways bars over the bins, as wide as the density of the bin; the densest bin of all the
    // datasets fills the slot
    check("g = DistributionChart({{10, 20, 20, 30, 30, 30, 40, 90}, {50, 60, 60, 70}}, "
        + "ChartElementFunction -> \"HistogramDensity\"); "
        + "{Length(Cases(g, _Polygon, Infinity)), "
        + "Cases(g, Rectangle({a_, b_}, {c_, d_}) :> {(a + c)/2, c - a, b, d}, Infinity)}", //
        "{0,{{1.0,0.2,10.0,20.0},{1.0,0.4,20.0,30.0},{1.0,0.6,30.0,40.0},{1.0,0.2,40.0,50.0},"
            + "{1.0,0.0,50.0,60.0},{1.0,0.0,60.0,70.0},{1.0,0.0,70.0,80.0},{1.0,0.0,80.0,90.0},"
            + "{1.0,0.2,90.0,100.0},{2.0,0.4,50.0,60.0},{2.0,0.8,60.0,70.0},"
            + "{2.0,0.4,70.0,80.0}}}");
    // an empty bin between two full ones is a rectangle of no width
    check("Cases(DistributionChart({{11, 12, 12, 13, 17}}, "
        + "ChartElementFunction -> \"HistogramDensity\"), "
        + "Rectangle({a_, b_}, {c_, d_}) :> {c - a, b}, Infinity)", //
        "{{0.266667,10.0},{0.8,12.0},{0.0,14.0},{0.266667,16.0}}");
    // every dataset in its own colour
    check("Length(Union(Cases(DistributionChart({{1, 2, 2, 3}, {4, 5, 5, 6}, {2, 4, 6, 8}}, "
        + "ChartElementFunction -> \"HistogramDensity\"), _RGBColor, Infinity)))", //
        "3");
  }

  @Test
  public void testQuantileColumns() {
    // a column from the smallest to the largest value, crossed at the ceil(n p)-th values
    check("g = DistributionChart({{1,2,3,4,5,6,7,8}}, ChartElementFunction -> \"Quantile\"); "
        + "{Cases(g, Rectangle({_, a_}, {_, b_}) :> {a, b}, Infinity), "
        + "Cases(g, {RGBColor(r_, _, _), Line({{_, y_}, _})} :> {r, y}, Infinity)}", //
        "{{{1.0,8.0}},{{0.7,2.0},{0.7,4.0},{0.7,6.0}}}");
    check("Union(Cases(DistributionChart({{1,2,3,4,5,6,7,8}}, ChartElementFunction -> "
        + "\"Quantile\", ChartStyle -> Red), {c_RGBColor, _Line} :> c, Infinity))", //
        "{RGBColor(0.7,0.0,0.0)}");
  }

  @Test
  public void testDensityColumns() {
    check("g = DistributionChart({{1,2,3,4,5,6,7,8}, {2,2,3}}, "
        + "ChartElementFunction -> \"Density\"); {Length(Cases(g, _Rectangle, Infinity)), "
        + "MinMax(Cases(g, Rectangle({_, a_}, {_, b_}) :> {a, b}, Infinity))}", //
        "{2,{1.0,8.0}}");
    // one rectangle under a complex of bands which fade from the fill where the density peaks
    // towards white where it is lowest
    check("g = DistributionChart({{2,4,4,5,5,5,6,12}}, ChartElementFunction -> \"Density\", "
        + "ChartStyle -> RGBColor(0.2, 0.4, 0.8)); vc = Cases(g, (VertexColors -> c_) :> c, "
        + "Infinity)[[1]]; {Length(Cases(g, _GraphicsComplex, Infinity)), "
        + "Length(Cases(g, Polygon(f_) :> f, Infinity)[[1]]), Length(vc), "
        + "MinMax(vc[[All, 1]]), Union(Cases(g, _Rectangle, Infinity))}", //
        "{1,120,242,{0.2,1.0},{Rectangle({0.803951,2.0},{1.19605,12.0})}}");
  }

  @Test
  public void testDensityColumnSidesShareTheirColour() {
    // both corners of a sample have one colour; the default fill is the darkest, white the lightest
    check("g = DistributionChart({{3,7,7,8,8,8,8,10,11,15}}, ChartElementFunction -> \"Density\"); "
        + "vc = Cases(g, (VertexColors -> c_) :> c, Infinity)[[1]]; "
        + "{Union(Map(Apply(SameQ, #) &, Partition(vc, 2))), "
        + "MinMax(vc[[All, 3]]), MinMax(vc[[All, 2]])}", //
        "{{True},{0.25,1.0},{0.835,1.0}}");
    // the fill of a chart style
    check("g = DistributionChart({{-4,-1,0,0,1,6}}, ChartElementFunction -> \"Density\", "
        + "ChartStyle -> RGBColor(0.6, 0.3, 0.1)); "
        + "MinMax(Cases(g, (VertexColors -> c_) :> c, Infinity)[[1, All, 3]])", //
        "{0.1,1.0}");
  }
}
