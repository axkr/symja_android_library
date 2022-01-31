package org.matheclipse.core.mathcell;

public class BoxWhiskerChartExample extends BasePlotExample {

  @Override
  public String exampleFunction() {
    return "BoxWhiskerChart( Table(RandomVariate(NormalDistribution(m, 1), 100), {m, {0, 5, 3, 7}}) )";
    // return "BoxWhiskerChart(RandomVariate(NormalDistribution(0, 1), 100))";
  }

  public static void main(String[] args) {
    BoxWhiskerChartExample p = new BoxWhiskerChartExample();
    p.generateHTML();
  }
}
