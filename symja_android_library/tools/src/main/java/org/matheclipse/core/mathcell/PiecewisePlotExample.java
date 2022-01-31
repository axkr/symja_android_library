package org.matheclipse.core.mathcell;

public class PiecewisePlotExample extends BasePlotExample {

  @Override
  public String exampleFunction() {
    return "Plot(x^2+Piecewise({{x, 0 < x < 1}, {x^3, 1 < x < 2}}), {x, -5, 5})";
    // return "Plot( Piecewise({{x^2, x < 0}, {x, x >= 0&&x<1},{Cos(x-1), x >= 1}}) , {x, -2, 12})";
  }

  public static void main(String[] args) {
    PiecewisePlotExample p = new PiecewisePlotExample();
    p.generateHTML();
  }
}
