package org.matheclipse.core.mathcell;

public class ContourPlotExample extends BasePlotExample {

  @Override
  public String exampleFunction() {
    // not supported
    return "ContourPlot(Cos(x) + Cos(y), {x, 0, 4*Pi}, {y, 0, 4*Pi})";
  }

  public static void main(String[] args) {
    ContourPlotExample p = new ContourPlotExample();
    p.generateHTML();
  }
}
