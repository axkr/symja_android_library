package org.matheclipse.core.mathcell;

public class BarChartExample extends BasePlotExample {

  @Override
  public String exampleFunction() {
    return "BarChart({1, -2, 3})";
  }

  public static void main(String[] args) {
    BarChartExample p = new BarChartExample();
    p.generateHTML();
  }
}
