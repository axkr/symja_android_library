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
        "{240,{1.0,8.0}}");
  }
}
