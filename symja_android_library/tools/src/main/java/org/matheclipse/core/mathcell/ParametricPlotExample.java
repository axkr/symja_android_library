package org.matheclipse.core.mathcell;

public class ParametricPlotExample extends BasePlotExample {

  @Override
  public String exampleFunction() {
    // TODO improve for multiple variables:
    //    return "ParametricPlot({{2*r*Cos(t), r*Sin(t)}, {r*Cos(t), 2*r*Sin(t)}}, {t, 0, 2 Pi}, {r,
    // 0, 1})";

    return "model = NDSolve({x'(t) == -y(t) - x(t)^2, y'(t) == 2*x(t) - y(t)^3, x(0) == 1, y(0) == 1}, {x, y}, {t, 20});" //
        + "ParametricPlot(Evaluate({x(t),y(t)} /.model), {t, 0, 20})";
    // return "ParametricPlot({{2*Cos(t), 2*Sin(t)}, {2*Cos(t), Sin(t)}, {Cos(t), 2*Sin(t)},
    // {Cos(t), Sin(t)}}, {t,
    // 0, 2 Pi}, PlotLegends -> \"Expressions\")";
    // return "ParametricPlot({Sin(t), Cos(t^2)}, {t, 0, 2*Pi})";
  }

  public static void main(String[] args) {
    ParametricPlotExample p = new ParametricPlotExample();
    p.generateHTML();
  }
}
