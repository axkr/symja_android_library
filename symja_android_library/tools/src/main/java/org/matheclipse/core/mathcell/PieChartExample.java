package org.matheclipse.core.mathcell;

public class PieChartExample extends BasePlotExample {

  @Override
  public String exampleFunction() {
    return "PieChart({25, 33, 33, 10})";
  }

  public static void main(String[] args) {
    PieChartExample p = new PieChartExample();
    p.generateHTML();
  }
}
