package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * The legend a chart draws for <code>ChartLegends -&gt; {labels}</code>: a
 * <code>SwatchLegend</code> in the colours the chart filled its elements with.
 */
public class ChartLegendTest extends ExprEvaluatorTestCase {

  /**
   * The swatch colours of the legend of <code>chart</code>, and whether each of them fills an
   * element of the picture.
   */
  private void checkSwatches(String chart, String expectedSwatches) {
    check("lg = " + chart + "; sw = Cases(lg, _SwatchLegend, Infinity); "
        + "{Length(sw), sw[[1, 1]], Complement(sw[[1, 1]], Cases(lg[[1]], _RGBColor, Infinity))}", //
        "{1," + expectedSwatches + ",{}}");
  }

  @Test
  public void testBarChart() {
    // a single dataset is drawn in one colour, which each swatch repeats
    check("Union(Cases(BarChart({4, 7, 2}, ChartLegends -> {\"a1\", \"b1\", \"c1\"}), "
        + "_RGBColor, Infinity))", //
        "{RGBColor(1.0,0.835,0.25)}");
    checkSwatches("BarChart({4, 7, 2}, ChartLegends -> {\"a1\", \"b1\", \"c1\"})", //
        "{RGBColor(1.0,0.835,0.25),RGBColor(1.0,0.835,0.25),RGBColor(1.0,0.835,0.25)}");
    // several datasets colour by position, which the legend names
    checkSwatches("BarChart({{1, 3}, {2, 5}}, ChartLegends -> {\"a1\", \"b1\"})", //
        "{RGBColor(1.0,0.835,0.25),RGBColor(0.0,0.665,0.95)}");
    checkSwatches(
        "BarChart({{1, 3}, {2, 5}}, ChartStyle -> {Red, Blue}, ChartLegends -> {\"a1\", \"b1\"})", //
        "{RGBColor(1,0,0),RGBColor(0,0,1)}");
  }

  @Test
  public void testPieAndSectorCharts() {
    checkSwatches("PieChart({2, 3, 5}, ChartLegends -> {\"a2\", \"b2\", \"c2\"})", //
        "{RGBColor(1.0,0.835,0.25),RGBColor(0.0,0.665,0.95),RGBColor(0.375,0.75,0.0)}");
    // Automatic names the sectors by their labels
    check("Cases(PieChart({2, 3}, ChartLabels -> {\"a2\", \"b2\"}, ChartLegends -> Automatic), "
        + "_SwatchLegend, Infinity)", //
        "{SwatchLegend({RGBColor(1.0,0.835,0.25),RGBColor(0.0,0.665,0.95)},{a2,b2})}");
    checkSwatches("SectorChart({{1, 2}, {2, 1}}, ChartLegends -> {\"a2\", \"b2\"})", //
        "{RGBColor(1.0,0.835,0.25),RGBColor(0.0,0.665,0.95)}");
  }

  @Test
  public void testDatasetCharts() {
    checkSwatches("Histogram({{1, 2, 2, 3}, {3, 4, 4, 5}}, ChartLegends -> {\"a3\", \"b3\"})", //
        "{RGBColor(1.0,0.835,0.25),RGBColor(0.0,0.665,0.95)}");
    checkSwatches(
        "BubbleChart({{{1, 2, 3}, {2, 3, 1}}, {{3, 1, 2}}}, ChartLegends -> {\"a3\", \"b3\"})", //
        "{RGBColor(1.0,0.835,0.25),RGBColor(0.0,0.665,0.95)}");
    // the rectangles of one dataset share a colour
    checkSwatches("RectangleChart({{2, 1}, {1, 2}}, ChartLegends -> {\"a3\", \"b3\"})", //
        "{RGBColor(1.0,0.835,0.25),RGBColor(1.0,0.835,0.25)}");
    // several datasets colour by position within the dataset, which the legend names
    checkSwatches("RectangleChart({{{2, 1}, {1, 1}}, {{1, 2}, {2, 2}}}, "
        + "ChartLegends -> {\"a3\", \"b3\"})", //
        "{RGBColor(1.0,0.835,0.25),RGBColor(0.0,0.665,0.95)}");
  }

  @Test
  public void testDistributionChart() {
    // the violins take the chart colours from the second one on
    checkSwatches("DistributionChart({{1, 2, 4}, {2, 3, 6}}, ChartLegends -> {\"a4\", \"b4\"})", //
        "{RGBColor(0.0,0.665,0.95),RGBColor(0.375,0.75,0.0)}");
    // quantile columns share the first colour, even when a legend names them
    checkSwatches("DistributionChart({{1, 2, 4}, {2, 3, 6}}, "
        + "ChartElementFunction -> \"Quantile\", ChartLegends -> {\"a4\", \"b4\"})", //
        "{RGBColor(1.0,0.835,0.25),RGBColor(1.0,0.835,0.25)}");
  }

  @Test
  public void testPlacedLabels() {
    // a placement is kept around the swatches
    check("Cases(BoxWhiskerChart({{2, 4, 5}, {1, 6, 9}}, "
        + "ChartLegends -> Placed({\"a5\", \"b5\"}, Below)), _Placed, Infinity)", //
        "{Placed(SwatchLegend({RGBColor(1.0,0.835,0.25),RGBColor(1.0,0.835,0.25)},{a5,b5}),"
            + "Below)}");
    // a legend given in full is handed on as it is
    check("Cases(BarChart({1, 2}, ChartLegends -> LineLegend({Red, Blue}, {\"a5\", \"b5\"})), "
        + "_LineLegend, Infinity)", //
        "{LineLegend({RGBColor(1,0,0),RGBColor(0,0,1)},{a5,b5})}");
  }

  @Test
  public void testRingsColourByPosition() {
    // every ring starts again at the first chart colour, and the swatches name the positions
    check("Union(Cases(SectorChart({{{2, 1}, {1, 2}, {3, 1}}, {{1, 1}, {2, 2}}}), _RGBColor, "
        + "Infinity))", //
        "{RGBColor(0.0,0.665,0.95),RGBColor(0.375,0.75,0.0),RGBColor(1.0,0.835,0.25)}");
    checkSwatches("SectorChart({{{2, 1}, {1, 2}}, {{1, 1}, {2, 2}}}, "
        + "ChartLegends -> {\"a9\", \"b9\"})", //
        "{RGBColor(1.0,0.835,0.25),RGBColor(0.0,0.665,0.95)}");
  }

  @Test
  public void testStackedBarsColourByPosition() {
    // a stacked bar colours its segments by position, and the swatches name them
    checkSwatches(
        "BarChart({{3, 1, 2}}, ChartLayout -> \"Stacked\", ChartLegends -> {\"a6\", \"b6\", \"c6\"})", //
        "{RGBColor(1.0,0.835,0.25),RGBColor(0.0,0.665,0.95),RGBColor(0.375,0.75,0.0)}");
  }

  @Test
  public void testSwatchesShowTheDrawnFill() {
    // a Style wrapper colours its sector, and the swatch shows it
    checkSwatches("PieChart({Style(2, Red), 3}, ChartLegends -> {\"a7\", \"b7\"})", //
        "{RGBColor(1,0,0),RGBColor(0.0,0.665,0.95)}");
    // a single dataset of bubbles is drawn in one colour, which each swatch repeats
    checkSwatches("BubbleChart({{1, 2, 3}, {2, 3, 1}}, ChartLegends -> {\"a7\", \"b7\"})", //
        "{RGBColor(1.0,0.835,0.25),RGBColor(1.0,0.835,0.25)}");
    checkSwatches("Histogram({1, 2, 2, 3}, ChartLegends -> {\"a7\", \"b7\"})", //
        "{RGBColor(1.0,0.835,0.25),RGBColor(1.0,0.835,0.25)}");
    // a dataset without a box keeps its style colour, and the others keep theirs
    check("Cases(BoxWhiskerChart({{}, {1, 2, 3}, {4, 5, 6}}, ChartStyle -> {Red, Green, Blue}, "
        + "ChartLegends -> {\"a7\", \"b7\", \"c7\"}), _SwatchLegend, Infinity)", //
        "{SwatchLegend({RGBColor(1,0,0),RGBColor(0,1,0),RGBColor(0,0,1)},{a7,b7,c7})}");
  }

  @Test
  public void testDirectiveSwatchIsDrawnInItsColour() {
    // the swatch of a Directive chart style is drawn in the colour the directive carries
    check("StringContainsQ(ExportString(BarChart({{1, 2}, {3, 4}}, "
        + "ChartStyle -> {Directive(EdgeForm(None), Red), Blue}, "
        + "ChartLegends -> {\"a8\", \"b8\"}), \"SVG\"), "
        + "\"height=\\\"11\\\" fill=\\\"rgb(255,0,0)\\\"\")", //
        "True");
  }
}
