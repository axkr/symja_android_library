package org.matheclipse.core.mathcell;

public class ComplexPlot3DExample extends BasePlotExample {
  @Override
  public String exampleFunction() {
    // return "ComplexPlot3D(InverseHaversine(z), {z, -1/3-I, 1/3+I}) ";
    // return "ComplexPlot3D(Arg(z), {z, -2 - 2*I, 2 + 2*I} ) ";
    // return "ComplexPlot3D(Haversine(z), {z, -2*Pi - 2*I, 2*Pi + 2*I} ) ";
    // return "ComplexPlot3D(StruveH(2, z), {z, -5-5*I,5+5*I}, PlotRange->{-5.0,10.0}) ";
    return "ComplexPlot3D((z^2 + 1)/(z^2 - 1), {z, -2 - 2*I, 2 + 2*I}, PlotRange->{0,3}) ";
    // return "ComplexPlot3D((z^4-1)/(z^2), {z, -2 - 2*I, 2 + 2*I}, PlotRange->{0,10}) "; //
  }

  public static void main(String[] args) {
    ComplexPlot3DExample p = new ComplexPlot3DExample();
    p.generateHTML();
  }
}
