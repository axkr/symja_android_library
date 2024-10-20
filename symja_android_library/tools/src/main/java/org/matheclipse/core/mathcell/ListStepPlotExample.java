package org.matheclipse.core.mathcell;

public class ListStepPlotExample extends BasePlotExample {

  @Override
  public String exampleFunction() {
    return null;
  }

  @Override
  public String[] exampleFunctions() {
    return new String[] {//
        "ListStepPlot({{1, 2, 3, 5, 8, 13, 21}, {1, 2, 4, 8, 16, 32, 64}}, " //
            + "PlotLabel ->\"Test\", PlotLegends ->Automatic, AxesLabel->{x,y})", //
        "ListStepPlot(Table({Prime(n), EulerPhi(n)}, {n, 50})," //
            + "Axes->{False, True}, AxesLabel->{x,y})", //
        "ListStepPlot({1, 1, 2, 3, 5, 8, 13, 21}, PlotLegends ->\"test\")", //
        "ListStepPlot({Fibonacci(Range(10)), Prime(Range(10))}, PlotLabel ->\"Number theory\",PlotLegends -> {\"fibonacci\", \"primes\"})"};
  }

  public static void main(String[] args) {
    ListStepPlotExample p = new ListStepPlotExample();
    p.generateHTML();
  }
}
