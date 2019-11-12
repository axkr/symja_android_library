package org.matheclipse.core.mathcell;

public class PlotExample extends BasePlotExample {

	@Override
	public String exampleFunction() {
		return "Plot(Sin(x), {x, 0, 4 Pi}, PlotRange->{{0, 4 Pi}, {0, 1.5}})";
		// return "Manipulate(Plot(Sin(x), {x, 0, 6*Pi} ))";
		// return "Plot(Evaluate(Table(BesselJ(n, x), {n, 4})), {x, 0, 10})";
		// return "Manipulate(Plot(Sin(x), {x, -2*Pi, 2*Pi} ))";
		// return "Manipulate(Plot(Log(1- q), {q,0,1} ))";
		// return "Manipulate(Plot(ConditionalExpression(Log(1- q), 0 <=q<=1), {q,0.1,0.9} ))";
		// return "Manipulate(Plot(Sin(x)*Cos(1 + a*x), {x, 0, 2*Pi}, PlotRange->{-1,2}), {a,0,10})";
	}

	public static void main(String[] args) {
		PlotExample p = new PlotExample();
		p.generateHTML();
	}
}
