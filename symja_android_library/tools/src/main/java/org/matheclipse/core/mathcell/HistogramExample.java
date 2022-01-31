package org.matheclipse.core.mathcell;

public class HistogramExample extends BasePlotExample {

  @Override
  public String exampleFunction() {
    return "Histogram({1, 2, 3, None, 3, 5, f(), 2, 1, 5,4,3,2,foo, 2, 3})";
    // return "Histogram(RandomVariate(NormalDistribution(0, 1), 200))";
  }

  public static void main(String[] args) {
    HistogramExample p = new HistogramExample();
    p.generateHTML();
  }
}
