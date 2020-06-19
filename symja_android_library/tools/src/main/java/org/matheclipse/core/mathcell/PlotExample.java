package org.matheclipse.core.mathcell;

public class PlotExample extends BasePlotExample {

	@Override
	public String exampleFunction() {
 		return "Plot(Tan(x), {x, -10, 10},PlotRange->{-20,20})";
//		return "Plot(SinIntegral(x), {x, -20, 20})";
		// return "Plot({x, Surd(x, 3), Surd(x, 5), Surd(x, 7)}, {x, -1, 1}, PlotLegends -> \"Expressions\")";
		// return "Plot(Clip(x, {-3, 2}), {x, -10, 10})";
		// return "Manipulate(Plot(Sinc(x), {x, -10, 10} ))";
		// return "Plot(Sin(x), {x, 0, 4*Pi}, PlotRange->{{0, 4*Pi}, {0, 1.5}})";
		// return "Manipulate(Plot(Sin(x), {x, 0, 6*Pi} ))";
		// return "Plot(Evaluate(Table(BesselJ(n, x), {n, 4})), {x, 0, 10})";
		
	}

	public static void main(String[] args) {
		PlotExample p = new PlotExample();
		p.generateHTML();
	}
}
