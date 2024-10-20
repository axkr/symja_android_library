package org.matheclipse.core.mathcell;

public class ListLogPlotExample extends BasePlotExample {

  @Override
  public String exampleFunction() {
    return null;
  }

  @Override
  public String[] exampleFunctions() {
    return new String[] {//
        "ListLogPlot({Range(40), Table(PartitionsQ(n), {n, 50})})", //
        "ListLogPlot(Table({n, n!}, {n, 1, 20, .1}), Joined -> True, PlotLabel->\"Table values\")", //
        "ListLogPlot(Table(PartitionsQ(n), {n, 50}))"};
  }

  public static void main(String[] args) {
    ListLogPlotExample p = new ListLogPlotExample();
    p.generateHTML();
  }
}
