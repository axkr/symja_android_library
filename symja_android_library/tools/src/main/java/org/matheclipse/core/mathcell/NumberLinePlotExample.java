package org.matheclipse.core.mathcell;

public class NumberLinePlotExample extends BasePlotExample {

  @Override
  public String exampleFunction() {
    return "NumberLinePlot({Prime(Range(20)),Prime(Range(40)),Prime(Range(80))})";
  }

  public static void main(String[] args) {
    NumberLinePlotExample p = new NumberLinePlotExample();
    p.generateHTML();
  }
}
