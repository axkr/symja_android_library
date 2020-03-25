package org.matheclipse.core.mathcell;

public class SpecialFunctionPlots extends BasePlotExample {

	@Override
	public String exampleFunction() {
		return "Plot(StruveH(x), {x, 0, 100})";
	}

	public static void main(String[] args) {
		SpecialFunctionPlots p = new SpecialFunctionPlots();
		p.generateHTML();
	}
}
