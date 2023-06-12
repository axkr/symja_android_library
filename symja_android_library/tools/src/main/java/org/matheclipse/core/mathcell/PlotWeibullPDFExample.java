package org.matheclipse.core.mathcell;

public class PlotWeibullPDFExample extends BasePlotExample {

  @Override
  public String exampleFunction() {
    return "Plot(Table(PDF(WeibullDistribution(a, 2), x), {a, {0.5, 1.0, 3.0}}) // Evaluate, {x, 0, 4}, JSForm->True, PlotRange->{-0.1,1.0})";
  }

  public static void main(String[] args) {
    PlotWeibullPDFExample p = new PlotWeibullPDFExample();
    p.generateHTML();
  }
}
