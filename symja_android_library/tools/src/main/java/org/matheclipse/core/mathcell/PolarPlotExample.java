package org.matheclipse.core.mathcell;

public class PolarPlotExample extends BasePlotExample {

  @Override
  public String exampleFunction() {
    return "PolarPlot({t,Sin(2*t),Cos(t)}, {t, 0, 4*Pi}, JSForm->True )";
    // return "PolarPlot(1 + 1/10*Sin(10*t), {t, 0, 2*Pi})";
    // return "PolarPlot(1-Cos(t), {t, 0, 2*Pi})";
  }

  public static void main(String[] args) {
    PolarPlotExample p = new PolarPlotExample();
    p.generateHTML();
  }
}
