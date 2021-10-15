package org.matheclipse.core.mathcell;

public class ManipulateExample extends BasePlotExample {

  @Override
  public String exampleFunction() {
    return "Manipulate(Plot({Re(CoshIntegral(a*x)),Im(CoshIntegral(a*x))},{x,-2.0`,2.0`},PlotRange->{-5.0`,5.0`}),{a,1,10})";
    // return "Manipulate({Plot(Sin(x), {x, -2*Pi, 2*Pi} ),PolarPlot({t,Sin(2*t),Cos(t)}, {t, 0,
    // 4*Pi} )})";
    // return
    // "Manipulate(Plot({Re(CosIntegral(a*x)),Im(CosIntegral(a*x))},{x,-10.0,10.0},PlotRange->{-5.0,5.0}),{a,1,10})";
    // return "Manipulate(Plot(Sin(x), {x, -2*Pi, 2*Pi} ))";
    // return "Manipulate(Plot(Log(1- q), {q,0,1} ))";
    // return "Manipulate(Plot(ConditionalExpression(Log(1- q), 0 <=q<=1), {q,0.1,0.9} ))";
    //    return "Manipulate(Plot(Sin(x)*Cos(1 + a*x), {x, 0, 2*Pi}, PlotRange->{-1,2}), {a,0,10})";
  }

  public static void main(String[] args) {
    ManipulateExample p = new ManipulateExample();
    p.generateHTML();
  }
}
