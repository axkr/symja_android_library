package org.matheclipse.core.mathcell;

public class TraceFormExample extends BasePlotExample {

	@Override
	public String exampleFunction() {

		return "TraceForm(7^3 + 5^2 + Sin(Pi/2) + 1)";
		// return "TraceForm(u = 2; Do(u = u*u, {3}); u)";

		// return "TraceForm(f(f(f(1 + 1))))";
		// return "TraceForm(f(g(1 + 1), 2 + 3))";
		// return "TraceForm(Integrate(E^(-x^2),x))";
	}

	public static void main(String[] args) {
		TraceFormExample p = new TraceFormExample();
		p.generateHTML();
	}
}