package org.matheclipse.core.mathcell;

public class PlotExample extends BasePlotExample {

	@Override
	public String exampleFunction() {
		return "Manipulate(Plot(Sin(x)*Cos(1 + a*x), {x, 0, 2*Pi}, PlotRange->{-1,2}), {a,0,10})";
	}

	public static void main(String[] args) {
		PlotExample p = new PlotExample();
		p.generateHTML();
	}
}
