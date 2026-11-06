package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** The column elements of a distribution chart. */
public class DistributionChartTest extends ExprEvaluatorTestCase {

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
    // one rectangle under a complex of bands whose sides fade towards white and dark gray where
    // the density is lowest, and meet in the fill where it peaks
    check("g = DistributionChart({{2,4,4,5,5,5,6,12}}, ChartElementFunction -> \"Density\", "
        + "ChartStyle -> RGBColor(0.2, 0.4, 0.8)); vc = Cases(g, (VertexColors -> c_) :> c, "
        + "Infinity)[[1]]; {Length(Cases(g, _GraphicsComplex, Infinity)), "
        + "Length(Cases(g, Polygon(f_) :> f, Infinity)[[1]]), Length(vc), "
        + "MinMax(vc[[All, 1]]), Union(Cases(g, _Rectangle, Infinity))}", //
        "{1,120,242,{0.1,1.0},{Rectangle({0.803951,2.0},{1.19605,12.0})}}");
  }
}
