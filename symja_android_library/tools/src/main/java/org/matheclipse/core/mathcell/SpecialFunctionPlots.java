package org.matheclipse.core.mathcell;

public class SpecialFunctionPlots extends BasePlotExample {

  @Override
  public String exampleFunction() {
    return "Plot(Gamma(x), {x, -10, 10}, JSForm->True)";
  }

  public static void main(String[] args) {
    SpecialFunctionPlots p = new SpecialFunctionPlots();
    p.generateHTML();
  }
}
