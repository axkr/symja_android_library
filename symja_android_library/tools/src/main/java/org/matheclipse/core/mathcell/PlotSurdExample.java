package org.matheclipse.core.mathcell;

public class PlotSurdExample extends BasePlotExample {

  @Override
  public String exampleFunction() {

    return "Plot((1 + x^3)*CubeRoot(x), {x, -1, 1})";
  }

  public static void main(String[] args) {
    PlotSurdExample p = new PlotSurdExample();
    p.generateHTML();
  }
}
