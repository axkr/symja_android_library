package org.matheclipse.core.mathcell;

public class DensityPlotExample extends BasePlotExample {

  @Override
  public String exampleFunction() {
    return "DensityPlot(Cos(x) + Cos(y), {x, 0, 4*Pi}, {y, 0, 4*Pi})";
  }

  public static void main(String[] args) {
    DensityPlotExample p = new DensityPlotExample();
    p.generateHTML();
  }
}
