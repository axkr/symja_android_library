package org.matheclipse.core.mathcell;

public class Plot3DExample extends BasePlotExample {

  @Override
  public String exampleFunction() {
    return "Plot3D(Re(Log(x + I*y)), {x, -2, 2}, {y, -2, 2},PlotRange->{-2,2}) ";
    // return "Plot3D(Im(Log(x + I*y)), {x, -2, 2}, {y, -2, 2},PlotRange->{-2,2}) ";
    // return "Plot3D(ArcTan(x, y), {x, -7, 7}, {y, -10, 10})";
    // return "Plot3D(Sin(x*y), {x,0, 5}, {y, 0, 5}, JSForm->True, ColorFunction->\"Rainbow\")";
    // return "Manipulate(Plot3D(Sin(a*x*y), {x, -1.5, 1.5}, {y, -1.5, 1.5}), {a,1,5})";
    // return "Manipulate(Plot3D(Sin(a*x*y), {x,-1.5, 1.5}, {y, -1.5, 1.5},
    // ColorFunction->\"Rainbow\"), {a,1,5})";
  }

  public static void main(String[] args) {
    Plot3DExample p = new Plot3DExample();
    p.generateHTML();
  }
}
