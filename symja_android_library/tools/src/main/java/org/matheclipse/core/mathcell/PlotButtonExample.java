package org.matheclipse.core.mathcell;

public class PlotButtonExample extends BasePlotExample {

  @Override
  public String exampleFunction() {
    return "Manipulate(Plot(f(x), {x, 0, 2*Pi}), {f, {Sin, Cos, Tan, Cot}})";
  }

  public static void main(String[] args) {
    PlotButtonExample p = new PlotButtonExample();
    p.generateHTML();
  }
}
