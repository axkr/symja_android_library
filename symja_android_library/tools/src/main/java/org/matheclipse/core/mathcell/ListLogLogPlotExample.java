package org.matheclipse.core.mathcell;

public class ListLogLogPlotExample extends BasePlotExample {

  @Override
  public String exampleFunction() {
    return null;
  }

  @Override
  public String[] exampleFunctions() {
    return new String[] {//
        "ListLogLogPlot(Range(20)^3)", //
        "ListLogLogPlot({Range(20), Sqrt(Range(20)), Log(Range(20))}, Joined -> True, PlotLegends -> {\"eins\", \"zwei\", \"drei\"})"
    };
  }

  public static void main(String[] args) {
    ListLogLogPlotExample p = new ListLogLogPlotExample();
    p.generateHTML();
  }
}
