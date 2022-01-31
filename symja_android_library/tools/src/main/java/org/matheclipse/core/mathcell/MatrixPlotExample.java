package org.matheclipse.core.mathcell;

public class MatrixPlotExample extends BasePlotExample {

  @Override
  public String exampleFunction() {
    return "MatrixPlot(SparseArray({{4, 2, 1}, {3, 0, -2}, {0, 0, -1}}))";
    // return "MatrixPlot({{4, 2, 1}, {3, 0, -2}, {0, 0, -1}})";
  }

  public static void main(String[] args) {
    MatrixPlotExample p = new MatrixPlotExample();
    p.generateHTML();
  }
}
