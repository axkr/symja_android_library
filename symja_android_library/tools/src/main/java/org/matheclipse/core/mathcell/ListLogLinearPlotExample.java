package org.matheclipse.core.mathcell;

public class ListLogLinearPlotExample extends BasePlotExample {

  @Override
  public String exampleFunction() {
    return null;
  }

  @Override
  public String[] exampleFunctions() {
    return new String[] {//
        "ListLogLinearPlot(Table({n, Sqrt[n]}, {n, 100}))" //
    };
  }

  public static void main(String[] args) {
    ListLogLinearPlotExample p = new ListLogLinearPlotExample();
    p.generateHTML();
  }
}
